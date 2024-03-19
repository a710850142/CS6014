import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.spec.X509EncodedKeySpec;

public class TlsHandshake {

    private Certificate caCertificate;
    private Certificate myCertificate;
    private PrivateKey myPrivateKey;
    private KeyPair myDHKeyPair;

    public TlsHandshake(Certificate caCertificate, Certificate myCertificate, PrivateKey myPrivateKey, KeyPair myDHKeyPair) {
        this.caCertificate = caCertificate;
        this.myCertificate = myCertificate;
        this.myPrivateKey = myPrivateKey;
        this.myDHKeyPair = myDHKeyPair;
    }

    public void performClientHandshake(Socket socket) throws Exception {
        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
        ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

        // 1. 发送客户端证书和DH公钥
        out.writeObject(myCertificate);
        out.writeObject(myDHKeyPair.getPublic().getEncoded()); // 发送DH公钥
        out.flush();

        // 2. 接收服务器证书和DH公钥，进行验证
        Certificate serverCertificate = (Certificate) in.readObject();
        serverCertificate.verify(caCertificate.getPublicKey());
        byte[] serverDHPubKeyBytes = (byte[]) in.readObject();
        PublicKey serverDHPublicKey = KeyFactory.getInstance("DH").generatePublic(new X509EncodedKeySpec(serverDHPubKeyBytes));

        // 3. 计算共享密钥
        byte[] sharedSecret = DiffieHellmanUtil.computeSharedSecret(myDHKeyPair, serverDHPublicKey);

    }

    public void performServerHandshake(Socket socket) throws Exception {
        ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());

        // 1. 接收客户端证书和DH公钥，进行验证
        Certificate clientCertificate = (Certificate) in.readObject();
        clientCertificate.verify(caCertificate.getPublicKey());
        byte[] clientDHPubKeyBytes = (byte[]) in.readObject();
        PublicKey clientDHPublicKey = KeyFactory.getInstance("DH").generatePublic(new X509EncodedKeySpec(clientDHPubKeyBytes));

        // 2. 发送服务器证书和DH公钥
        out.writeObject(myCertificate);
        out.writeObject(myDHKeyPair.getPublic().getEncoded()); // 发送DH公钥
        out.flush();

        // 3. 计算共享密钥
        byte[] sharedSecret = DiffieHellmanUtil.computeSharedSecret(myDHKeyPair, clientDHPublicKey);

    }
}
