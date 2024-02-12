public class TestRC4Insecurity {
    public static void main(String[] args) {
        String message1 = "Attack at dawn";
        String message2 = "Attack at dusk";
        byte[] key = "secret".getBytes();

        RC4 rc4Encrypt = new RC4(key);
        byte[] cipherText1 = rc4Encrypt.encryptOrDecrypt(message1.getBytes());


        rc4Encrypt = new RC4(key);
        byte[] cipherText2 = rc4Encrypt.encryptOrDecrypt(message2.getBytes());

        byte[] xored = new byte[cipherText1.length];
        for (int i = 0; i < cipherText1.length; i++) {
            xored[i] = (byte) (cipherText1[i] ^ cipherText2[i]);
        }

        System.out.println("XOR of two encrypted messages: " + new String(xored));
    }
}
