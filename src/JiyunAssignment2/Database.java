package JiyunAssignment2;

import com.alibaba.fastjson.*;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import java.io.*;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Database {
    private final String baseDirectory;
    private final int MAX_RECORDS_PER_LOCATION = 20;
    private Lock lock = new ReentrantLock();

    public Database(String baseDirectory) {
        this.baseDirectory = baseDirectory;
        createNewDatabase(baseDirectory);
    }
    private static void createNewDatabase(String newDatabaseFileLocation) {
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

    public void insert( JSONObject json, int lamport) throws IOException {
        lock.lock();
        try {
            String filePath = baseDirectory;
            Map<Integer, String> records = readRecords();
            // Add the new record
            int minlamport = 999999999;
            if (records.size() >= MAX_RECORDS_PER_LOCATION) {
                for (Map.Entry<Integer, String> entryset : records.entrySet()) {
                    int elementLamport = entryset.getKey();
                    if (elementLamport < minlamport) {
                        minlamport = elementLamport;
                    }
                }
                if (minlamport < lamport) {
                    records.remove(minlamport);
                    records.put(lamport, json.toJSONString());
                }
            } else {
                records.put(lamport, json.toJSONString());
            }
            // Write records back to the file
            writeRecords(filePath, records);
        } finally {
            lock.unlock();
        }
    }

    public Map<Integer, String> readRecords() throws IOException {
        Map<Integer, String> records = new TreeMap<>();
        String filePath=this.baseDirectory;
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            Integer lamportClock = null;
            StringBuilder jsonString = new StringBuilder();

            while ((line = reader.readLine()) != null) {
                if (line.startsWith("Lamport_Clock:")) {
                    lamportClock = Integer.parseInt(line.split(":")[1]);
                    jsonString.setLength(0); // Clear the string builder
                } else if (line.startsWith("*")) {
                    if (lamportClock != null) {
                        records.put(lamportClock, jsonString.toString());
                        jsonString.setLength(0); // Clear the string builder
                    }
                } else {
                    jsonString.append(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return records;
}

    private void writeRecords(String filePath, Map<Integer, String> records) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (Map.Entry<Integer, String> entryset : records.entrySet()) {
                writer.write("Lamport_Clock:" + entryset.getKey().toString() + "\n");
                writer.write(entryset.getValue() + "\n");
                writer.write("*\n");
            }

        }

    }
}
