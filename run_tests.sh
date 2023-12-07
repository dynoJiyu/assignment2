#!/bin/bash

# Define paths and parameters
CLASSPATH="bin:src/JiyunAssignment2/fastjson-1.2.7.jar:src/JiyunAssignment2/commons-lang3-3.13.0.jar:src/JiyunAssignment2/commons-io-2.13.0.jar:src/JiyunAssignment2/hamcrest-core-1.1.jar:src/JiyunAssignment2/junit-4.12.jar"

# Start AggregationServer
java -cp $CLASSPATH JiyunAssignment2.AggregationServer &

# Pause to let the server start up
sleep 1

# Start two ContentServers with respective parameters
java -cp $CLASSPATH JiyunAssignment2.ContentServer "http://127.0.0.1:4567/" "jsonFiles/test.json" &
java -cp $CLASSPATH JiyunAssignment2.ContentServer "http://127.0.0.1:4567/" "jsonFiles/test1.json" &

# Wait for 60 seconds
sleep 60

# Start two GETClients with respective parameters
echo "GET_All_Station"
java -cp $CLASSPATH JiyunAssignment2.GETClient "http://127.0.0.1:4567/" &
sleep 10
echo "GET_IDS60902_Station"
java -cp $CLASSPATH JiyunAssignment2.GETClient "http://127.0.0.1:4567/" "IDS60902" &
sleep 600
echo "GET_All_Station"
java -cp $CLASSPATH JiyunAssignment2.GETClient "http://127.0.0.1:4567/" &
sleep 10
echo "GET_IDS60902_Station"
java -cp $CLASSPATH JiyunAssignment2.GETClient "http://127.0.0.1:4567/" "IDS60902" &
wait
