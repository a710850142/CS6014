public class Receiver {
    private int expectedSequenceNumber = 0;

    public void receiveData(Packet packet) {
        if (packet.getSequenceNumber() == expectedSequenceNumber && !packet.isCorrupt()) {
            deliverData(packet.getData());
            sendAck(packet.getSequenceNumber());
            expectedSequenceNumber = 1 - expectedSequenceNumber; // 切换期望的序列号
        } else {
            // 如果收到的是损坏的包或者序列号不匹配，重新发送上一个ACK
            sendAck(1 - expectedSequenceNumber);
        }
    }

    private void deliverData(String data) {
        System.out.println("Data received: " + data);
        // 向上层应用交付数据
    }

    private void sendAck(int sequenceNumber) {
        System.out.println("Sending ACK for sequence number: " + sequenceNumber);
        // 发送ACK的逻辑
    }
}
