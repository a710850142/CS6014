import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class DNSServer {
    // 服务器监听端口
    private static final int PORT = 8053;
    // Google DNS服务器的IP地址
    private static final String GOOGLE_DNS = "8.8.8.8";
    // 标准DNS包大小
    private static final int BUFFER_SIZE = 512;
    // Google DNS服务器的端口
    private static final int GOOGLE_DNS_PORT = 53;

    public static void main(String[] args) {
        try (DatagramSocket serverSocket = new DatagramSocket(PORT)) {
            System.out.println("DNS Server listening on port " + PORT);
            while (true) {
                try {
                    // 处理客户端请求
                    handleClientRequest(serverSocket);
                } catch (IOException e) {
                    System.err.println("Error handling client request: " + e.getMessage());
                    // 即使处理某个请求失败，也继续服务其他请求
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to start DNS server: " + e.getMessage());
        }
    }

    private static void handleClientRequest(DatagramSocket serverSocket) throws IOException {
        // 接收缓冲区
        byte[] receiveBuffer = new byte[BUFFER_SIZE];
        DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, BUFFER_SIZE);
        // 接收客户端的DNS查询
        serverSocket.receive(receivePacket);

        // 将查询转换为字符串作为缓存的键
        String queryKey = new String(receivePacket.getData(), 0, receivePacket.getLength());
        // 尝试从缓存中获取响应
        byte[] cachedResponse = DNSCache.get(queryKey);
        if (cachedResponse != null) {
            // 如果缓存中有响应，直接返回给客户端
            sendResponse(serverSocket, receivePacket.getAddress(), receivePacket.getPort(), cachedResponse);
            return;
        }

        // 如果缓存中没有响应，转发查询到Google DNS，并缓存Google的响应
        byte[] responseFromGoogleDNS = forwardQueryToGoogleDNS(receivePacket.getData(), receivePacket.getLength());
        DNSCache.put(queryKey, responseFromGoogleDNS);
        // 将响应返回给客户端
        sendResponse(serverSocket, receivePacket.getAddress(), receivePacket.getPort(), responseFromGoogleDNS);
    }

    private static byte[] forwardQueryToGoogleDNS(byte[] queryData, int length) throws IOException {
        // 创建一个新的DatagramSocket来与Google DNS通信
        try (DatagramSocket googleSocket = new DatagramSocket()) {
            InetAddress googleAddress = InetAddress.getByName(GOOGLE_DNS);
            DatagramPacket queryPacket = new DatagramPacket(queryData, length, googleAddress, GOOGLE_DNS_PORT);
            // 向Google DNS发送查询
            googleSocket.send(queryPacket);

            // 接收Google DNS的响应
            byte[] responseBuffer = new byte[BUFFER_SIZE];
            DatagramPacket responsePacket = new DatagramPacket(responseBuffer, BUFFER_SIZE);
            googleSocket.receive(responsePacket);
            // 返回收到的响应
            return responsePacket.getData();
        }
    }

    private static void sendResponse(DatagramSocket serverSocket, InetAddress clientAddress, int clientPort, byte[] responseData) throws IOException {
        // 创建响应包并发送给客户端
        DatagramPacket responsePacket = new DatagramPacket(responseData, responseData.length, clientAddress, clientPort);
        serverSocket.send(responsePacket);
    }
}
