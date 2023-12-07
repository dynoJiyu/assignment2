# Directories and files
SRC_DIR = src/JiyunAssignment2# Update the source directory path
BIN_DIR = bin
JARS = $(SRC_DIR)/fastjson-1.2.7.jar:$(SRC_DIR)/commons-lang3-3.13.0.jar:$(SRC_DIR)/commons-io-2.13.0.jar:$(SRC_DIR)/hamcrest-core-1.1.jar:$(SRC_DIR)/junit-4.12.jar

# Java compiler and flags
JAVAC = javac
JFLAGS = -d $(BIN_DIR) -cp $(JARS)

# For running the Java program
JAVA = java
CLASSPATH = $(BIN_DIR):$(JARS)

# Targets
all: compile

compile:
	@mkdir -p $(BIN_DIR)/$(SRC_DIR)  # Ensure the output directory exists
	$(JAVAC) $(JFLAGS) $(SRC_DIR)/*.java
