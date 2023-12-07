package JiyunAssignment2;

public class LamportClock {
    private int clock;

    public LamportClock() {
        this.clock = 0;
    }

    // Increment the clock
    public synchronized int tick() {
        this.clock=this.clock+1;
        return getTime();
    }

    // Update the clock when a message is received
    public synchronized void receiveMessage(int otherClock) {
        this.clock = Math.max(this.clock, otherClock) + 1;
    }

    // Get the current clock value
    public synchronized int getTime() {
        return this.clock;
    }
}
