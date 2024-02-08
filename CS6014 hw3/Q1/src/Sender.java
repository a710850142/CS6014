import java.util.HashSet;
import java.util.Set;

public class Sender {
    private int sequenceNumber = 0;
    private Set<Integer> acksReceived;

    public Sender() {
        this.acksReceived = new HashSet<>();
    }

    public void sendData(String data) {
        Packet packet = new Packet(sequenceNumber, data);
        acksReceived.clear();
        udtSendToAll(packet);
        waitForAcks();
    }

    private void udtSendToAll(Packet packet) {
        System.out.println("Broadcasting packet with sequence number: " + packet.getSequenceNumber() + " and data: " + packet.getData());

    }

    private void waitForAcks() {
        while (acksReceived.size() < 2) { // 假设有两个接收方
            // 检查是否收到ACK
            // 根据接收到的ACK更新acksReceived
            // 如果超时，使用udtSendToAll重新发送数据包
        }
        sequenceNumber = 1 - sequenceNumber; // 切换序列号
    }

    public void ackReceived(int fromReceiver, int sequenceNumber) {
        if (sequenceNumber == this.sequenceNumber) {
            acksReceived.add(fromReceiver);
        }
    }
}
