public class TestRC4 {
    public static void main(String[] args) {
        String message = "Hello, World!";
        byte[] key = "secret".getBytes();
        byte[] wrongKey = "wrongkey".getBytes();

        RC4 rc4Encrypt = new RC4(key);
        byte[] cipherText = rc4Encrypt.encryptOrDecrypt(message.getBytes());

        RC4 rc4DecryptWithWrongKey = new RC4(wrongKey);
        byte[] decryptedTextWithWrongKey = rc4DecryptWithWrongKey.encryptOrDecrypt(cipherText);

        System.out.println("Decrypted Text with Wrong Key: " + new String(decryptedTextWithWrongKey));
    }
}
