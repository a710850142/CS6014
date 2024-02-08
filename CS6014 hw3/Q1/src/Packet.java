public class Packet {
    private int sequenceNumber;
    private String data;
    private boolean corrupt;

    public Packet(int sequenceNumber, String data) {
        this.sequenceNumber = sequenceNumber;
        this.data = data;
        this.corrupt = false; // 默认情况下，假设数据包不损坏
    }

    public int getSequenceNumber() {
        return sequenceNumber;
    }

    public String getData() {
        return data;
    }

    public boolean isCorrupt() {
        return corrupt;
    }


}
