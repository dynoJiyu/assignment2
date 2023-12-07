# Project README

## How to Run

1. Execute the `make` command to run the `makefile`.
2. After that, I've set up the `run_tests.sh` script for you. This script performs the following actions:
    - It first runs the `AggregationServer`.
    - Then it runs two instances of `ContentServer`, which send the `weather content` from `test.json` and `test1.json` to the `AggregationServer`.
      (Note: Both files are based on sample inputs from the assignment. I've only modified the `StationID` value in `test1.json`.)
    - After running the above servers for 60 seconds, two `GETclient` are executed:
    - The first one fetches weather data for all locations.
    - The second one retrieves weather data for a specific `stationID`. 
    - To demonstrate the automatic overwrite functionality of my database, the aforementioned two `GETclient` will be rerun after 600 seconds.
3. You can also manually run the `AggregationServer`, `ContentServer`, and `GETclient` in the terminal. The `AggregationServer` accepts a series of numbers entered in the terminal as the port. The first command-line argument for the `ContentServer` accepts a server address and port in URL format, and the second one accepts the local storage address for a JSON string. The first command-line argument for the `GETclient` accepts a server address and port in URL format, and the second one is for the stationID. If you want to get the weather for all stations, you don't need to enter the second argument.
## Note
Please ensure you have all the necessary permissions and dependencies installed before executing these steps.
