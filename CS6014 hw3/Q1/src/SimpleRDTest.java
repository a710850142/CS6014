public class SimpleRDTest {

    public static void main(String[] args) {
        // 创建发送方和接收方对象
        Sender sender = new Sender();
        Receiver receiver1 = new Receiver();
        Receiver receiver2 = new Receiver();

        // 模拟从发送方到接收方的数据传输
        // 假设有一个方法可以模拟这个过程
        simulateSendAndReceive(sender, receiver1, receiver2, "Hello World!");

        // 模拟接收ACK
    }

    private static void simulateSendAndReceive(Sender sender, Receiver receiver1, Receiver receiver2, String message) {
        // 发送方发送数据
        sender.sendData(message);

        // 构造数据包
        Packet packet = new Packet(0, message); // 假设序列号为0

        // 接收方接收数据
        receiver1.receiveData(packet);
        receiver2.receiveData(packet);

        // 模拟接收方发送ACK
        sender.ackReceived(1, 0); // 假设接收方1的ID为1
        sender.ackReceived(2, 0); // 假设接收方2的ID为2
    }
}
