import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.cert.Certificate;

public class Client {

    public static void main(String[] args) {
        String host = "localhost";
        int port = 8080;

        try (Socket socket = new Socket(host, port)) {
            System.out.println("Connect to server " + host + " port " + port);

            // 插入加载CA证书、客户端证书和私钥、生成DH密钥对的代码
            Certificate caCertificate = KeyLoader.loadCertificate("/Users/xxy/Desktop/TLS/resources/CAcertificate.pem");
            Certificate clientCertificate = KeyLoader.loadCertificate("/Users/xxy/Desktop/TLS/resources/CASignedClientCertificate.pem");
            PrivateKey clientPrivateKey = KeyLoader.loadPrivateKey("/Users/xxy/Desktop/TLS/resources/clientPrivateKey.der");
            KeyPair clientDHKeyPair = DiffieHellmanUtil.generateKeyPair();

            // 执行TLS握手
            TlsHandshake tlsHandshake = new TlsHandshake(caCertificate, clientCertificate, clientPrivateKey, clientDHKeyPair);
            tlsHandshake.performClientHandshake(socket);

            // 接收服务器发送的两条消息
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            System.out.println("Server message: " + in.readObject());
            System.out.println("Server message: " + in.readObject());

            // 发送确认消息回服务器
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            out.writeObject("The client has received the message");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}