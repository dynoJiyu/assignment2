package JiyunAssignment2;

import com.alibaba.fastjson.JSON;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.PriorityBlockingQueue;
import java.net.ServerSocket;
import java.net.Socket;

public class AggregationServer {
    private static Map<String, Database> locationDatabaseMap = new HashMap<>();
    private static final String DATABASE_ROOT_DIRECTORY = "./databases";


    private static List<Socket> existSocket=new ArrayList<>();
    private static PriorityBlockingQueue<Requestion> pq = new PriorityBlockingQueue<>();
    public  static LamportClock lamportClock = new LamportClock();
    public static boolean databaseIsExist(String location){
        return locationDatabaseMap.containsKey(location);
    }
    public static void addToExistSocket(Socket socket){
        existSocket.add(socket);
    }
    public static boolean isExist(Socket socket){
        return existSocket.contains(socket);
    }
    public static void insertToPq(Requestion requestion) {
        pq.add(requestion);
    }

    public static boolean pqIsEmpty() {
        return pq.isEmpty();
    }

    public static Requestion pollfromPq() {
        return pq.poll();
    }

    public static void main(String[] args) {

        //creat
        int port = 4567;
        for (String arg : args) {
            port = Integer.valueOf(arg);
        }
        new RequestionHandler().start();
        try (ServerSocket server = new ServerSocket(port)) {
            System.out.println("Server is listening on port " + port);
            while (true) {
                Socket socket = server.accept();
                System.out.println("Client connected");
                new ClientHandler(socket).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static Database getDatabaseFileLocation(String stationID) {
        try {
            checkAndCreateDirectory();
            if (locationDatabaseMap.containsKey(stationID)) {
                return locationDatabaseMap.get(stationID);
            } else {
                String newDatabaseFileLocation = DATABASE_ROOT_DIRECTORY +"/"+stationID + ".txt";
                Database database= new Database(newDatabaseFileLocation);
                locationDatabaseMap.put(stationID, database);
                return database;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    private static void checkAndCreateDirectory() {
        Path path = Paths.get(DATABASE_ROOT_DIRECTORY);

        // 检查目录是否存在
        if (!Files.exists(path)) {
            try {
                Files.createDirectories(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void createNewDatabase(String newDatabaseFileLocation) {
        try {

            Path path = Paths.get(newDatabaseFileLocation);
            // 如果文件不存在，则创建空的txt文件
            if (!Files.exists(path)) {
                Files.createFile(path);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class Requestion implements Comparable<Requestion> {
    private int lamportClock;
    private String function;
    private String resource;
    private String messageBody;
    private Socket socket;

    public void setSocket(Socket socket) {
        this.socket = socket;
    }
    public Socket getSocket() {
        return socket;
    }
    public void setFunction(String function) {
        this.function = function;
    }

    public void setLamportClock(int lamportClock) {
        this.lamportClock = lamportClock;
    }

    public void setMessageBody(String messageBody) {
        this.messageBody = messageBody;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public int getLamportClock() {
        return lamportClock;
    }

    public String getFunction() {
        return function;
    }

    public String getMessageBody() {
        return messageBody;
    }

    public String getResource() {
        return resource;
    }

    @Override
    public int compareTo(Requestion other) {
        return Integer.compare(this.lamportClock, other.lamportClock);  // 否则根据 lamportClock 排序
    }
}

class RequestionHandler extends Thread {
    public void run() {
        while (true) {

            Database allstationDatabase=AggregationServer.getDatabaseFileLocation("allStation");
            if (AggregationServer.pqIsEmpty()) {
                try {
                    sleep(500);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            } else {
                Requestion requestion = AggregationServer.pollfromPq();
                int localLamport=AggregationServer.lamportClock.tick();
                Socket socket = requestion.getSocket();
                String function = requestion.getFunction();
                int lamport = requestion.getLamportClock();
//                System.out.println(lamport);
                ResponseMessage responseMessage=new ResponseMessage();
                int statue;
                Map<String,String> headers=new HashMap<>();
                headers.put("Lamport-Clock",String.valueOf(AggregationServer.lamportClock.getTime()+1));

                switch (function){
                    case "Heartbeat":
                        responseMessage.setStatusLine(1.1,400);
                        responseMessage.setHeaders(headers);
                        headers.put("Content-Type", "Heartbeat");
                        break;
                    case "PUT":
                        String messageBody=requestion.getMessageBody();
                        if (messageBody.isEmpty()||(!WeatherContent.isValidWeatherContent(messageBody)))
                        {
                            statue=500;
                        }else {
                            if(AggregationServer.isExist(socket)){
                                statue=200;
                            }
                            else{
                                statue=201;
                                AggregationServer.addToExistSocket(socket);
                            }
                            WeatherContent weatherContent= JSON.parseObject(messageBody, WeatherContent.class);
                            String stationID=weatherContent.getId();
                            Database database=AggregationServer.getDatabaseFileLocation(stationID);
                            try {
                                database.insert(JSON.parseObject(messageBody), lamport);
                                allstationDatabase.insert(JSON.parseObject(messageBody), lamport);
                            }catch (IOException e){
                                e.printStackTrace();
                                statue=500;
                            }
                        }
                        responseMessage.setStatusLine(1.1,statue);
                        responseMessage.setHeaders(headers);
                        break;
                    case "GET":
                        statue=200;
                        String stationID=requestion.getResource();
                        if (!AggregationServer.databaseIsExist(stationID)){
                            statue=500;
                            responseMessage.setStatusLine(1.1,statue);
                            responseMessage.setHeaders(headers);
                        }
                        else{
                            Database database=AggregationServer.getDatabaseFileLocation(stationID);
                            try {
                                String json=JSON.toJSONString(database.readRecords());
                                responseMessage.setStatusLine(1.1,statue);
                                headers.put("Length",String.valueOf(json.length()));
                                headers.put("Content-Type", "Json");
                                responseMessage.setHeaders(headers);
                                responseMessage.setResponseBody(json);
                            } catch (IOException e) {
                                statue=500;
                                e.printStackTrace();
                            }
                        }
                        break;
                    default:
                        statue=400;
                        responseMessage.setStatusLine(1.1,statue);
                        responseMessage.setHeaders(headers);
                }
                try {
                    OutputStream out = socket.getOutputStream();
                    out.write((JSON.toJSONString(responseMessage)+"*").getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

class ClientHandler extends Thread {
    private Socket socket;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    //    public void clinetHandleFunc;
//    public void ContentServerHandleFunc;
    public void run() {
        while (true) {
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

                RequestMessage requestMessage = JSON.parseObject(fullResponse, RequestMessage.class);
                String function=requestMessage.getRequestLine().split(" /")[0];
                int lamportTime=Integer.valueOf(requestMessage.getHeaders().get("Lamport-Clock"));
                Requestion requestion = new Requestion();
                requestion.setFunction(function);
                requestion.setLamportClock(lamportTime);
                AggregationServer.lamportClock.receiveMessage(lamportTime);
                requestion.setSocket(socket);
                if (!requestion.getFunction().equals("Heartbeat")) {
                    requestion.setResource(requestMessage.getRequestLine().split(" /")[1].split(" ")[0]);
                    requestion.setMessageBody(requestMessage.getRequestBody());
                }
                AggregationServer.insertToPq(requestion);
            } catch (IOException e) {
                System.out.println("Client disconnected");
            }
        }
    }
}
