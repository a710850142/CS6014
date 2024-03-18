import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.cert.Certificate;

public class Server {

    public static void main(String[] args) {
        int port = 8080; // 定义服务器监听的端口
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("The server is listening on the port: " + port);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Accept from " + clientSocket.getRemoteSocketAddress() + " connection");

                // 插入加载CA证书、服务器证书和私钥、生成DH密钥对的代码
                Certificate caCertificate = KeyLoader.loadCertificate("/Users/xxy/Desktop/TLS/resources/CAcertificate.pem");
                Certificate serverCertificate = KeyLoader.loadCertificate("/Users/xxy/Desktop/TLS/resources/CASignedServerCertificate.pem");
                PrivateKey serverPrivateKey = KeyLoader.loadPrivateKey("/Users/xxy/Desktop/TLS/resources/serverPrivateKey.der");

                KeyPair serverDHKeyPair = DiffieHellmanUtil.generateKeyPair();

                // 执行TLS握手
                TlsHandshake tlsHandshake = new TlsHandshake(caCertificate, serverCertificate, serverPrivateKey, serverDHKeyPair);
                tlsHandshake.performServerHandshake(clientSocket);

                // 发送两条消息到客户端
                ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
                out.writeObject("xxy");
                out.writeObject("Hello world");
                out.flush();

                ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
                String clientResponse = (String) in.readObject();
                System.out.println("Client response: " + clientResponse);

                // 关闭连接
                clientSocket.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
