package JiyunAssignment2;

import com.alibaba.fastjson.JSON;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class GETClient {
    private LamportClock lamportClock = new LamportClock();
    private int id;
    private static int ID = 0;

    public static void main(String[] args) throws IOException {
        GETClient getClient = new GETClient();
        Random random = new Random();
        getClient.id = random.nextInt(100000);
        int id = getClient.id;
        int port;
        String stationID;
        String urlString;
        String host;
        host = "127.0.0.1";
        port = 4567;
        if (args.length != 0) {
            urlString = args[0];
            URL url = new URL(urlString);
            host = url.getHost();
            port = url.getPort();
        }
        if (args.length >= 2) {
            stationID = args[1];
        } else {
            stationID = "allStation";
        }
        Socket socket = new Socket(host, port);
        RequestMessage requestMessage = new RequestMessage();
        requestMessage.setRequestLine("GET", stationID, "1.1");
        Map<String, String> headers = new HashMap<>();
        OutputStream out = socket.getOutputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        int time = ContentServer.lamportClock.tick();
        Map<String, String> headersForHeartbeat = new HashMap<>();
        headersForHeartbeat.put("Lamport-Clock", String.valueOf(time));
        headersForHeartbeat.put("User-Agent", new String("client" + id));
        headersForHeartbeat.put("Content-Type", "HeartbeatMessage");
        RequestMessage heartbeat = new RequestMessage();
        heartbeat.setRequestLine("Heartbeat", "1", "1.1");
        heartbeat.setHeaders(headersForHeartbeat);
        out.write((JSON.toJSONString(heartbeat) + "*").getBytes());
        out.flush();

        StringBuilder receivedData = new StringBuilder();
        char[] buffer = new char[4096];
        int charsRead;
        while ((charsRead = reader.read(buffer)) != -1) {
            receivedData.append(buffer, 0, charsRead);
            if (receivedData.toString().endsWith("*")) {
                receivedData.setLength(receivedData.length() - 1);
                break;
            }
        }
        String fullResponse = receivedData.toString();
//        System.out.println(fullResponse);
        ResponseMessage rm = ResponseMessage.StringToRM(fullResponse);
        int AggregationTime = Integer.valueOf(rm.getHeaders().get("Lamport-Clock"));
        getClient.lamportClock.receiveMessage(AggregationTime);

        RequestMessage requestMessageForGET = new RequestMessage();
        requestMessageForGET.setRequestLine("GET", stationID, "1.1");
        headers.put("User-Agent", new String("client" + id));
        headers.put("Lamport-Clock", String.valueOf(getClient.lamportClock.getTime()));
        requestMessageForGET.setHeaders(headers);
        out.write((JSON.toJSONString(requestMessageForGET) + "*").getBytes());
        out.flush();
        receivedData.delete(0, receivedData.length());
        buffer = new char[1000];
        charsRead = 0;
        while ((charsRead = reader.read(buffer)) != -1) {
            receivedData.append(buffer, 0, charsRead);
            if (receivedData.toString().endsWith("*")) {
                receivedData.setLength(receivedData.length() - 1);
                break;
            }
        }
        fullResponse = receivedData.toString();
//        System.out.println(fullResponse);
        rm = ResponseMessage.StringToRM(fullResponse);
        AggregationTime = Integer.valueOf(rm.getHeaders().get("Lamport-Clock"));
        String json = rm.getResponseBody();
        Map<Integer, String> records = JSON.parseObject(json, Map.class);
        records.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> {
                    WeatherContent weatherContent = JSON.parseObject(entry.getValue(), WeatherContent.class);
                    System.out.println(weatherContent.toString());
                    System.out.println("_________________________________________________________________");
                });
        getClient.lamportClock.receiveMessage(AggregationTime);
        socket.close();
    }
}

