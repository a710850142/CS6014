public class RC4 {
    // Initialize the state array and indices.
    private int[] s = new int[256];
    private int i = 0, j = 0;

    // Constructor to initialize and scramble the state array with the given key.
    public RC4(byte[] key) {
        int keyLength = key.length;
        // Initialize the state array with values from 0 to 255.
        for (i = 0; i < 256; i++) {
            s[i] = i;
        }

        int j = 0;
        // Scramble the state array based on the key.
        for (i = 0; i < 256; i++) {
            j = (j + s[i] + key[i % keyLength]) & 255;
            // Swap s[i] and s[j].
            int temp = s[i];
            s[i] = s[j];
            s[j] = temp;
        }

        // Reset i and j for the actual encryption/decryption process.
        i = 0;
        j = 0;
    }

    // Generates and returns the next pseudo-random byte.
    public byte getNextByte() {
        i = (i + 1) & 255;
        j = (j + s[i]) & 255;
        // Swap s[i] and s[j] again.
        int temp = s[i];
        s[i] = s[j];
        s[j] = temp;
        // Generate the next pseudo-random byte.
        return (byte) (s[(s[i] + s[j]) & 255]);
    }

    // Encrypts or decrypts the given input array.
    public byte[] encryptOrDecrypt(byte[] input) {
        byte[] output = new byte[input.length];
        // XOR each byte of the input with a pseudo-random byte.
        for (int k = 0; k < input.length; k++) {
            output[k] = (byte) (input[k] ^ getNextByte());
        }
        return output;
    }
}
