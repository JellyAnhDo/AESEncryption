/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package aesencryption;

/**
 *
 * @author Anhho
 */
import java.nio.charset.StandardCharsets;

public class AES {

    private static final int BlockSize = 16;

    public static byte[] Encrypt(String plainText, String key, int keyRound) {
        byte[] plainBytes = plainText.getBytes(StandardCharsets.UTF_8);
        byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);

        int numBlocks = (int) Math.ceil((double) plainBytes.length / BlockSize);
        byte[] cipherBytes = new byte[numBlocks * BlockSize];  // Pre-allocate space for all blocks

        for (int blockIndex = 0; blockIndex < numBlocks; blockIndex++) {
            byte[][] state = new byte[4][4];
            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 4; j++) {
                    int index = i * 4 + j;
                    if (blockIndex * BlockSize + index < plainBytes.length) {
                        state[j][i] = plainBytes[blockIndex * BlockSize + index];
                    } else {
                        state[j][i] = 0; // Padding byte
                    }
                }
            }

            byte[][] w = KeyExpansion(keyBytes); // Placeholder for actual implementation

            AddRoundKey(state, w, 0); // Placeholder for actual implementation

            for (int round = 1; round < keyRound; round++) {
                SubBytes(state); // Placeholder for actual implementation
                ShiftRows(state); // Placeholder for actual implementation
                MixColumns(state); // Placeholder for actual implementation
                AddRoundKey(state, w, round);
            }

            SubBytes(state);
            ShiftRows(state);
            AddRoundKey(state, w, keyRound);

            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 4; j++) {
                    cipherBytes[blockIndex * BlockSize + i * 4 + j] = state[j][i];
                }
            }
        }
        return cipherBytes;
    }

    public static String Decrypt(byte[] cipherText, String key, int keyRound) {
        byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);

        int numBlocks = cipherText.length / BlockSize;
        byte[] decryptedBytes = new byte[numBlocks * BlockSize];

        for (int blockIndex = 0; blockIndex < numBlocks; blockIndex++) {
            byte[][] state = new byte[4][4];

            for (int i = 0; i < BlockSize; i++) {
                state[i % 4][i / 4] = cipherText[blockIndex * BlockSize + i];
            }

            byte[][] w = KeyExpansion(keyBytes); // You need to implement this method

            AddRoundKey(state, w, keyRound); // And this one

            for (int round = keyRound - 1; round > 0; round--) {
                InvShiftRows(state); // And all these
                InvSubBytes(state);
                AddRoundKey(state, w, round);
                InvMixColumns(state);
            }

            InvShiftRows(state);
            InvSubBytes(state);
            AddRoundKey(state, w, 0);

            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 4; j++) {
                    decryptedBytes[blockIndex * BlockSize + i * 4 + j] = state[j][i];
                }
            }
        }

        // Remove padding
        int paddingLength = decryptedBytes.length;
        while (paddingLength > 0 && decryptedBytes[paddingLength - 1] == 0) {
            paddingLength--;
        }

        byte[] finalDecryptedBytes = new byte[paddingLength];
        System.arraycopy(decryptedBytes, 0, finalDecryptedBytes, 0, paddingLength);

        return new String(finalDecryptedBytes, StandardCharsets.UTF_8);
    }

    private static void SubBytes(byte[][] state) {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                state[i][j] = SBox[state[i][j] & 0xFF];
            }
        }
    }

    private static void InvSubBytes(byte[][] state) {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                state[i][j] = InvSBox[state[i][j] & 0xFF];
            }
        }
    }

    private static void ShiftRows(byte[][] state) {
        for (int i = 1; i < 4; i++) {
            for (int j = 0; j < i; j++) {
                byte temp = state[i][0];
                System.arraycopy(state[i], 1, state[i], 0, 3);
                state[i][3] = temp;
            }
        }
    }

    private static void InvShiftRows(byte[][] state) {
        for (int i = 1; i < 4; i++) {
            for (int j = 0; j < i; j++) {
                byte temp = state[i][3];
                System.arraycopy(state[i], 0, state[i], 1, 3);
                state[i][0] = temp;
            }
        }
    }

    private static void MixColumns(byte[][] state) {
        byte[][] temp = new byte[4][4];

        for (int i = 0; i < 4; i++) {
            temp[0][i] = (byte) (Multiply((byte) 0x02, state[0][i]) ^ Multiply((byte) 0x03, state[1][i]) ^ state[2][i] ^ state[3][i]);
            temp[1][i] = (byte) (state[0][i] ^ Multiply((byte) 0x02, state[1][i]) ^ Multiply((byte) 0x03, state[2][i]) ^ state[3][i]);
            temp[2][i] = (byte) (state[0][i] ^ state[1][i] ^ Multiply((byte) 0x02, state[2][i]) ^ Multiply((byte) 0x03, state[3][i]));
            temp[3][i] = (byte) (Multiply((byte) 0x03, state[0][i]) ^ state[1][i] ^ state[2][i] ^ Multiply((byte) 0x02, state[3][i]));
        }

        for (int i = 0; i < 4; i++) {
            System.arraycopy(temp[i], 0, state[i], 0, 4);
        }
    }

    private static void InvMixColumns(byte[][] state) {
        byte[][] temp = new byte[4][4];

        for (int i = 0; i < 4; i++) {
            temp[0][i] = (byte) (Multiply((byte) 0x0E, state[0][i]) ^ Multiply((byte) 0x0B, state[1][i]) ^ Multiply((byte) 0x0D, state[2][i]) ^ Multiply((byte) 0x09, state[3][i]));
            temp[1][i] = (byte) (Multiply((byte) 0x09, state[0][i]) ^ Multiply((byte) 0x0E, state[1][i]) ^ Multiply((byte) 0x0B, state[2][i]) ^ Multiply((byte) 0x0D, state[3][i]));
            temp[2][i] = (byte) (Multiply((byte) 0x0D, state[0][i]) ^ Multiply((byte) 0x09, state[1][i]) ^ Multiply((byte) 0x0E, state[2][i]) ^ Multiply((byte) 0x0B, state[3][i]));
            temp[3][i] = (byte) (Multiply((byte) 0x0B, state[0][i]) ^ Multiply((byte) 0x0D, state[1][i]) ^ Multiply((byte) 0x09, state[2][i]) ^ Multiply((byte) 0x0E, state[3][i]));
        }

        for (int i = 0; i < 4; i++) {
            System.arraycopy(temp[i], 0, state[i], 0, 4);
        }
    }

    private static void AddRoundKey(byte[][] state, byte[][] w, int round) {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                state[j][i] ^= w[round * 4 + i][j];
            }
        }
    }

    private static byte Multiply(byte a, byte b) {
        byte result = 0;
        byte highBit = 0;
        for (int i = 0; i < 8; i++) {
            if ((b & 1) == 1) {
                result ^= a;
            }
            highBit = (byte) (a & 0x80);
            a <<= 1;
            if (highBit == 0x80) {
                a ^= 0x1B;
            }
            b >>= 1;
        }
        return result;
    }

    private static byte[][] KeyExpansion(byte[] key) {
        int Nk = key.length / 4;
        int Nr = Nk + 6;
        int Nb = 4;
        byte[][] w = new byte[Nb * (Nr + 1)][];

        for (int i = 0; i < Nk; i++) {
            byte[] temp = new byte[]{key[4 * i], key[4 * i + 1], key[4 * i + 2], key[4 * i + 3]};
            w[i] = temp;
        }

        for (int i = Nk; i < Nb * (Nr + 1); i++) {
            byte[] temp = new byte[4];
            System.arraycopy(w[i - 1], 0, temp, 0, 4);
            if (i % Nk == 0) {
                byte[] rotated = RotWord(temp);
                byte[] subbed = SubWord(rotated);
                byte rcon = Rcon[i / Nk];
                subbed[0] ^= rcon;
                temp = subbed;
            } else if (Nk > 6 && i % Nk == 4) {
                temp = SubWord(temp);
            }
            for (int j = 0; j < 4; j++) {
                temp[j] ^= w[i - Nk][j];
            }
            w[i] = temp;
        }

        return w;
    }

    private static byte[] RotWord(byte[] word) {
        byte temp = word[0];
        System.arraycopy(word, 1, word, 0, 3);
        word[3] = temp;
        return word;
    }

    private static byte[] SubWord(byte[] word) {
        byte[] result = new byte[4];
        for (int i = 0; i < 4; i++) {
            result[i] = SBox[word[i] & 0xFF];
        }
        return result;
    }

    private static final byte[] Rcon = {
        0x00, 0x01, 0x02, 0x04, 0x08, 0x10, 0x20, 0x40, (byte) 0x80, 0x1b, 0x36
    };

    // S-Box
    private static final byte[] SBox = {
        0x63, 0x7C, 0x77, 0x7B, (byte) 0xF2, 0x6B, 0x6F, (byte) 0xC5, 0x30, 0x01, 0x67, 0x2B, (byte) 0xFE, (byte) 0xD7, (byte) 0xAB, 0x76,
        (byte) 0xCA, (byte) 0x82, (byte) 0xC9, 0x7D, (byte) 0xFA, 0x59, 0x47, (byte) 0xF0, (byte) 0xAD, (byte) 0xD4, (byte) 0xA2, (byte) 0xAF, (byte) 0x9C, (byte) 0xA4, 0x72, (byte) 0xC0,
        (byte) 0xB7, (byte) 0xFD, (byte) 0x93, 0x26, 0x36, 0x3F, (byte) 0xF7, (byte) 0xCC, 0x34, (byte) 0xA5, (byte) 0xE5, (byte) 0xF1, 0x71, (byte) 0xD8, 0x31, 0x15,
        0x04, (byte) 0xC7, 0x23, (byte) 0xC3, 0x18, (byte) 0x96, 0x05, (byte) 0x9A, 0x07, 0x12, (byte) 0x80, (byte) 0xE2, (byte) 0xEB, 0x27, (byte) 0xB2, 0x75,
        0x09, (byte) 0x83, 0x2C, 0x1A, 0x1B, 0x6E, 0x5A, (byte) 0xA0, 0x52, 0x3B, (byte) 0xD6, (byte) 0xB3, 0x29, (byte) 0xE3, 0x2F, (byte) 0x84,
        0x53, (byte) 0xD1, 0x00, (byte) 0xED, 0x20, (byte) 0xFC, (byte) 0xB1, 0x5B, 0x6A, (byte) 0xCB, (byte) 0xBE, 0x39, 0x4A, 0x4C, 0x58, (byte) 0xCF,
        (byte) 0xD0, (byte) 0xEF, (byte) 0xAA, (byte) 0xFB, 0x43, 0x4D, 0x33, (byte) 0x85, 0x45, (byte) 0xF9, 0x02, 0x7F, 0x50, 0x3C, (byte) 0x9F, (byte) 0xA8,
        0x51, (byte) 0xA3, 0x40, (byte) 0x8F, (byte) 0x92, (byte) 0x9D, 0x38, (byte) 0xF5, (byte) 0xBC, (byte) 0xB6, (byte) 0xDA, 0x21, 0x10, (byte) 0xFF, (byte) 0xF3, (byte) 0xD2,
        (byte) 0xCD, 0x0C, 0x13, (byte) 0xEC, 0x5F, (byte) 0x97, 0x44, 0x17, (byte) 0xC4, (byte) 0xA7, 0x7E, 0x3D, 0x64, 0x5D, 0x19, 0x73,
        0x60, (byte) 0x81, 0x4F, (byte) 0xDC, 0x22, 0x2A, (byte) 0x90, (byte) 0x88, 0x46, (byte) 0xEE, (byte) 0xB8, 0x14, (byte) 0xDE, 0x5E, 0x0B, (byte) 0xDB,
        (byte) 0xE0, 0x32, 0x3A, 0x0A, 0x49, 0x06, 0x24, 0x5C, (byte) 0xC2, (byte) 0xD3, (byte) 0xAC, 0x62, (byte) 0x91, (byte) 0x95, (byte) 0xE4, 0x79,
        (byte) 0xE7, (byte) 0xC8, 0x37, 0x6D, (byte) 0x8D, (byte) 0xD5, 0x4E, (byte) 0xA9, 0x6C, 0x56, (byte) 0xF4, (byte) 0xEA, 0x65, 0x7A, (byte) 0xAE, 0x08,
        (byte) 0xBA, 0x78, 0x25, 0x2E, 0x1C, (byte) 0xA6, (byte) 0xB4, (byte) 0xC6, (byte) 0xE8, (byte) 0xDD, 0x74, 0x1F, 0x4B, (byte) 0xBD, (byte) 0x8B, (byte) 0x8A,
        0x70, 0x3E, (byte) 0xB5, 0x66, 0x48, 0x03, (byte) 0xF6, 0x0E, 0x61, 0x35, 0x57, (byte) 0xB9, (byte) 0x86, (byte) 0xC1, 0x1D, (byte) 0x9E,
        (byte) 0xE1, (byte) 0xF8, (byte) 0x98, 0x11, 0x69, (byte) 0xD9, (byte) 0x8E, (byte) 0x94, (byte) 0x9B, 0x1E, (byte) 0x87, (byte) 0xE9, (byte) 0xCE, 0x55, 0x28, (byte) 0xDF,
        (byte) 0x8C, (byte) 0xA1, (byte) 0x89, 0x0D, (byte) 0xBF, (byte) 0xE6, 0x42, 0x68, 0x41, (byte) 0x99, 0x2D, 0x0F, (byte) 0xB0, 0x54, (byte) 0xBB, 0x16
    };

    // Inverse S-Box
    private static final byte[] InvSBox = {
        (byte) 0x52, (byte) 0x09, (byte) 0x6A, (byte) 0xD5, (byte) 0x30, (byte) 0x36, (byte) 0xA5, (byte) 0x38, (byte) 0xBF, (byte) 0x40, (byte) 0xA3, (byte) 0x9E, (byte) 0x81, (byte) 0xF3, (byte) 0xD7, (byte) 0xFB,
        (byte) 0x7C, (byte) 0xE3, (byte) 0x39, (byte) 0x82, (byte) 0x9B, (byte) 0x2F, (byte) 0xFF, (byte) 0x87, (byte) 0x34, (byte) 0x8E, (byte) 0x43, (byte) 0x44, (byte) 0xC4, (byte) 0xDE, (byte) 0xE9, (byte) 0xCB,
        (byte) 0x54, (byte) 0x7B, (byte) 0x94, (byte) 0x32, (byte) 0xA6, (byte) 0xC2, (byte) 0x23, (byte) 0x3D, (byte) 0xEE, (byte) 0x4C, (byte) 0x95, (byte) 0x0B, (byte) 0x42, (byte) 0xFA, (byte) 0xC3, (byte) 0x4E,
        (byte) 0x08, (byte) 0x2E, (byte) 0xA1, (byte) 0x66, (byte) 0x28, (byte) 0xD9, (byte) 0x24, (byte) 0xB2, (byte) 0x76, (byte) 0x5B, (byte) 0xA2, (byte) 0x49, (byte) 0x6D, (byte) 0x8B, (byte) 0xD1, (byte) 0x25,
        (byte) 0x72, (byte) 0xF8, (byte) 0xF6, (byte) 0x64, (byte) 0x86, (byte) 0x68, (byte) 0x98, (byte) 0x16, (byte) 0xD4, (byte) 0xA4, (byte) 0x5C, (byte) 0xCC, (byte) 0x5D, (byte) 0x65, (byte) 0xB6, (byte) 0x92,
        (byte) 0x6C, (byte) 0x70, (byte) 0x48, (byte) 0x50, (byte) 0xFD, (byte) 0xED, (byte) 0xB9, (byte) 0xDA, (byte) 0x5E, (byte) 0x15, (byte) 0x46, (byte) 0x57, (byte) 0xA7, (byte) 0x8D, (byte) 0x9D, (byte) 0x84,
        (byte) 0x90, (byte) 0xD8, (byte) 0xAB, (byte) 0x00, (byte) 0x8C, (byte) 0xBC, (byte) 0xD3, (byte) 0x0A, (byte) 0xF7, (byte) 0xE4, (byte) 0x58, (byte) 0x05, (byte) 0xB8, (byte) 0xB3, (byte) 0x45, (byte) 0x06,
        (byte) 0xD0, (byte) 0x2C, (byte) 0x1E, (byte) 0x8F, (byte) 0xCA, (byte) 0x3F, (byte) 0x0F, (byte) 0x02, (byte) 0xC1, (byte) 0xAF, (byte) 0xBD, (byte) 0x03, (byte) 0x01, (byte) 0x13, (byte) 0x8A, (byte) 0x6B,
        (byte) 0x3A, (byte) 0x91, (byte) 0x11, (byte) 0x41, (byte) 0x4F, (byte) 0x67, (byte) 0xDC, (byte) 0xEA, (byte) 0x97, (byte) 0xF2, (byte) 0xCF, (byte) 0xCE, (byte) 0xF0, (byte) 0xB4, (byte) 0xE6, (byte) 0x73,
        (byte) 0x96, (byte) 0xAC, (byte) 0x74, (byte) 0x22, (byte) 0xE7, (byte) 0xAD, (byte) 0x35, (byte) 0x85, (byte) 0xE2, (byte) 0xF9, (byte) 0x37, (byte) 0xE8, (byte) 0x1C, (byte) 0x75, (byte) 0xDF, (byte) 0x6E,
        (byte) 0x47, (byte) 0xF1, (byte) 0x1A, (byte) 0x71, (byte) 0x1D, (byte) 0x29, (byte) 0xC5, (byte) 0x89, (byte) 0x6F, (byte) 0xB7, (byte) 0x62, (byte) 0x0E, (byte) 0xAA, (byte) 0x18, (byte) 0xBE, (byte) 0x1B,
        (byte) 0xFC, (byte) 0x56, (byte) 0x3E, (byte) 0x4B, (byte) 0xC6, (byte) 0xD2, (byte) 0x79, (byte) 0x20, (byte) 0x9A, (byte) 0xDB, (byte) 0xC0, (byte) 0xFE, (byte) 0x78, (byte) 0xCD, (byte) 0x5A, (byte) 0xF4,
        (byte) 0x1F, (byte) 0xDD, (byte) 0xA8, (byte) 0x33, (byte) 0x88, (byte) 0x07, (byte) 0xC7, (byte) 0x31, (byte) 0xB1, (byte) 0x12, (byte) 0x10, (byte) 0x59, (byte) 0x27, (byte) 0x80, (byte) 0xEC, (byte) 0x5F,
        (byte) 0x60, (byte) 0x51, (byte) 0x7F, (byte) 0xA9, (byte) 0x19, (byte) 0xB5, (byte) 0x4A, (byte) 0x0D, (byte) 0x2D, (byte) 0xE5, (byte) 0x7A, (byte) 0x9F, (byte) 0x93, (byte) 0xC9, (byte) 0x9C, (byte) 0xEF,
        (byte) 0xA0, (byte) 0xE0, (byte) 0x3B, (byte) 0x4D, (byte) 0xAE, (byte) 0x2A, (byte) 0xF5, (byte) 0xB0, (byte) 0xC8, (byte) 0xEB, (byte) 0xBB, (byte) 0x3C, (byte) 0x83, (byte) 0x53, (byte) 0x99, (byte) 0x61,
        (byte) 0x17, (byte) 0x2B, (byte) 0x04, (byte) 0x7E, (byte) 0xBA, (byte) 0x77, (byte) 0xD6, (byte) 0x26, (byte) 0xE1, (byte) 0x69, (byte) 0x14, (byte) 0x63, (byte) 0x55, (byte) 0x21, (byte) 0x0C, (byte) 0x7D
    };
}
