package JiyunAssignment2;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.net.Socket;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import com.alibaba.fastjson.JSON;

public class ContentServer {
    private int id;
    public static LamportClock lamportClock=new LamportClock();
    public int getId() {
        return id;
    }

    public static void main(String[] args) throws IOException {
        ContentServer cs = new ContentServer();
        Random random = new Random();
        cs.id = random.nextInt(100000);
        int id = cs.getId();
        String urlString;
        String filePath;
        String host;
        int port;
        host = "127.0.0.1";
        port = 4567;
        filePath = "src/main/java/org/example/JiyunAssignment2.test.json";
        if (args.length != 0) {
            urlString = args[0];
            URL url = new URL(urlString);
            host = url.getHost();
            port = url.getPort();
            filePath = args[1];
        }

        Socket socket = new Socket(host, port);

        try {
            // 从文件中读取内容
            new Thread(new ContentSender(socket, filePath, id)).start();
            new Thread(new HeartbeatSender(socket, id)).start();
            new Thread(new ResponseReceiver(socket,  id)).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class ContentSender implements Runnable {
    private final Socket socket;
    private final String filePath;
    private final int serverid;

    public ContentSender(Socket socket, String filePath, int serverid) {
        this.socket = socket;
        this.filePath = filePath;
        this.serverid = serverid;
    }
    public static String convertFileToJSON(String filePath) throws IOException {
        BufferedReader reader = null;
        JSONObject jsonObject = new JSONObject();
        try {
            reader = new BufferedReader(new FileReader(filePath));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":",2);
                if (parts.length == 2) {
                    String key = parts[0].trim();
                    String value = parts[1].trim();
                    jsonObject.put(key, value);
                }
            }
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
        return jsonObject.toString();
    }
    @Override
    public void run() {
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        while (true) {
            try {
                OutputStream out=socket.getOutputStream();
//                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                String weatherString = convertFileToJSON(filePath);
                int length = weatherString.length();
                RequestMessage rm = new RequestMessage();
                int time = ContentServer.lamportClock.tick();
                Map<String, String> headers = new HashMap<>();
                headers.put("Lamport-Clock", String.valueOf(time));
                headers.put("User-Agent", new String("JiyunAssignment2.ContentServer:" + serverid));
                headers.put("Content-Type", "Json");
                headers.put("Length", String.valueOf(length));
                rm.setRequestLine("PUT", "json", "1.1");
//                System.out.println(time);
                rm.setHeaders(headers);
                rm.setRequestBody(weatherString);
                out.write((JSON.toJSONString(rm)+"*").getBytes());
                out.flush();
                try {
                    Thread.sleep(30000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

class HeartbeatSender implements Runnable {
    private final Socket socket;

    private final int serverid;

    public HeartbeatSender(Socket socket, int serverid) {
        this.socket = socket;
        this.serverid = serverid;
    }

    @Override
    public void run() {
        try {
            OutputStream out=socket.getOutputStream();
//            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            while (true) {
                int time = ContentServer.lamportClock.tick();
                Map<String, String> headersForHeartbeat = new HashMap<>();
                headersForHeartbeat.put("Lamport-Clock", String.valueOf(time));
                headersForHeartbeat.put("User-Agent", new String("JiyunAssignment2.ContentServer:" + serverid));
                headersForHeartbeat.put("Content-Type", "HeartbeatMessage");
                RequestMessage heartbeat = new RequestMessage();
                heartbeat.setRequestLine("Heartbeat", "1", "1.1");
                heartbeat.setHeaders(headersForHeartbeat);
                out.write((JSON.toJSONString(heartbeat)+"*").getBytes());
//                System.out.println(time);
                out.flush();
                try {
                    Thread.sleep(15000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class ResponseReceiver implements Runnable {
    private final Socket socket;
    private final int serverid;

    public ResponseReceiver(Socket socket,int serverid) {
        this.socket = socket;
        this.serverid = serverid;
    }

    @Override
    public void run() {
        while(true) {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                StringBuilder receivedData = new StringBuilder();
                char[] buffer = new char[4096];
                int charsRead;

                // Keep reading until you encounter the '*' character

                while ((charsRead = reader.read(buffer)) != -1) {
                    receivedData.append(buffer, 0, charsRead);
                    if (receivedData.toString().endsWith("*")) {
                        // Remove the '*' from the end
                        receivedData.setLength(receivedData.length() - 1);
                        break;
                    }
                }
                String fullResponse = receivedData.toString();
                ResponseMessage rm = ResponseMessage.StringToRM(fullResponse);

                String statusLine = rm.getStatusLine();
                String[] statusComponents = statusLine.split(" ");
                if (statusComponents.length >= 3) {
                    String HTTPversion = statusComponents[0].substring(5);
                    String statusText = statusComponents[2];
                    for (int i = 3; i < statusComponents.length; i++) {
                        statusText += " " + statusComponents[i];
                    }
                    String statusCode = statusComponents[1];
                    Map<String, String> headers = rm.getHeaders();
                    int AggregationTime = Integer.valueOf(headers.get("Lamport-Clock"));
                    ContentServer.lamportClock.receiveMessage(AggregationTime);
                    System.out.println("Content Server "+String.valueOf(serverid)+" Received from server: " + fullResponse);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}