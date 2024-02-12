#include <iostream> // For input and output.
#include <array> // For using array containers.
#include <algorithm> // For std::shuffle.
#include <random> // For random number generation.
#include <string> // For using string objects.

// Define a type alias for a block of 8 bytes.
using Block = std::array<uint8_t, 8>;

// Generates a key from a password.
Block generateKey(const std::string& password) {
    Block key = {0};
    // XOR each character of the password with the key, cycling through key positions.
    for (size_t i = 0; i < password.length(); ++i) {
        key[i % 8] ^= password[i];
    }
    return key;
}

// Generates substitution tables for encryption and decryption.
std::array<std::array<uint8_t, 256>, 8> generateSubstitutionTables() {
    std::array<std::array<uint8_t, 256>, 8> tables;
    // Initialize tables with sequential values and then shuffle each.
    for (int table = 0; table < 8; ++table) {
        for (int i = 0; i < 256; ++i) {
            tables[table][i] = i;
        }
        std::random_device rd;
        std::mt19937 g(rd());
        std::shuffle(tables[table].begin(), tables[table].end(), g);
    }
    return tables;
}

// Rotates a block left by 1 bit.
Block rotateLeft(Block block) {
    uint64_t temp = 0;
    // Convert block to a 64-bit integer.
    for (int i = 0; i < 8; ++i) {
        temp |= static_cast<uint64_t>(block[i]) << (56 - i * 8);
    }
    // Rotate left.
    temp = (temp << 1) | (temp >> (63));
    // Convert back to block.
    for (int i = 0; i < 8; ++i) {
        block[i] = (temp >> (56 - i * 8)) & 0xFF;
    }
    return block;
}

// Rotates a block right by 1 bit.
Block rotateRight(Block block) {
    uint64_t temp = 0;
    // Convert block to a 64-bit integer.
    for (int i = 0; i < 8; ++i) {
        temp |= static_cast<uint64_t>(block[i]) << (56 - i * 8);
    }
    // Rotate right.
    temp = (temp >> 1) | (temp << (63));
    // Convert back to block.
    for (int i = 0; i < 8; ++i) {
        block[i] = (temp >> (56 - i * 8)) & 0xFF;
    }
    return block;
}

// Encrypts a message block using a key and substitution tables.
Block encrypt(const Block& message, const Block& key, const std::array<std::array<uint8_t, 256>, 8>& tables) {
    Block state = message;
    // Apply XOR, substitute, and rotate for 16 rounds.
    for (int round = 0; round < 16; ++round) {
        for (int i = 0; i < 8; ++i) {
            state[i] ^= key[i];
        }
        for (int i = 0; i < 8; ++i) {
            state[i] = tables[i][state[i]];
        }
        state = rotateLeft(state);
    }
    return state;
}

// Decrypts a cipher block using a key and substitution tables.
Block decrypt(const Block& cipher, const Block& key, const std::array<std::array<uint8_t, 256>, 8>& tables) {
    Block state = cipher;
    // Prepare inverse substitution tables for decryption.
    std::array<std::array<uint8_t, 256>, 8> inverseTables;
    for (int table = 0; table < 8; ++table) {
        for (int i = 0; i < 256; ++i) {
            inverseTables[table][tables[table][i]] = i;
        }
    }
    // Apply inverse operations for 16 rounds.
    for (int round = 0; round < 16; ++round) {
        state = rotateRight(state);
        for (int i = 0; i < 8; ++i) {
            state[i] = inverseTables[i][state[i]];
        }
        for (int i = 0; i < 8; ++i) {
            state[i] ^= key[i];
        }
    }
    return state;
}

// Main function demonstrating encryption and decryption.
int main() {
    std::string password = "password123"; // Example password.
    Block key = generateKey(password); // Generate key from password.
    auto tables = generateSubstitutionTables(); // Generate substitution tables.

    Block message = { 'H', 'e', 'l', 'l', 'o', '!', '!', '!' }; // Example message.
    std::cout << "Original message: ";
    for (char c : message) std::cout << c;
    std::cout << std::endl;

    Block encrypted = encrypt(message, key, tables); // Encrypt message.
    std::cout << "Encrypted message: ";
    for (uint8_t c : encrypted) std::cout << std::hex << +c << " ";
    std::cout << std::endl;

    Block decrypted = decrypt(encrypted, key, tables); // Decrypt message.
    std::cout << "Decrypted message: ";
    for (char c : decrypted) std::cout << c;
    std::cout << std::endl;

    return 0;
}
