public class TestRC4BitFlipping {
    public static void main(String[] args) {
        String originalMessage = "Your salary is $1000";
        byte[] key = "secret".getBytes();
        RC4 rc4 = new RC4(key);

        byte[] cipherText = rc4.encryptOrDecrypt(originalMessage.getBytes());


        String targetPart = "$1000";
        String replacementPart = "$9999";
        int startIndex = originalMessage.indexOf(targetPart);

        if (startIndex != -1) { // 确保找到了字符串
            byte[] attackMessage = replacementPart.getBytes();
            for (int i = 0; i < attackMessage.length; i++) {
                cipherText[startIndex + i] ^= (originalMessage.getBytes()[startIndex + i] ^ attackMessage[i]);
            }
        }

        // 解密修改后的密文
        rc4 = new RC4(key); // 使用相同的密钥重新初始化RC4进行解密
        byte[] decryptedAttackMessage = rc4.encryptOrDecrypt(cipherText);

        System.out.println("Decrypted Message after Bit-Flipping Attack: " + new String(decryptedAttackMessage));
    }
}
