package kripto;

import java.util.Arrays;

/**
 * Created by hendro.sinaga on 12-Jun-16.
 */
public class HAES256 {
    private static HAES256 ourInstance = new HAES256();

    private static  final int[][] SBOX =
            new int[][]{
                    {0x63, 0x7c, 0x77, 0x7b, 0xf2, 0x6b, 0x6f, 0xc5, 0x30, 0x01, 0x67, 0x2b, 0xfe, 0xd7, 0xab, 0x76},
                    {0xca, 0x82, 0xc9, 0x7d, 0xfa, 0x59, 0x47, 0xf0, 0xad, 0xd4, 0xa2, 0xaf, 0x9c, 0xa4, 0x72, 0xc0},
                    {0xb7, 0xfd, 0x93, 0x26, 0x36, 0x3f, 0xf7, 0xcc, 0x34, 0xa5, 0xe5, 0xf1, 0x71, 0xd8, 0x31, 0x15},
                    {0x04, 0xc7, 0x23, 0xc3, 0x18, 0x96, 0x05, 0x9a, 0x07, 0x12, 0x80, 0xe2, 0xeb, 0x27, 0xb2, 0x75},
                    {0x09, 0x83, 0x2c, 0x1a, 0x1b, 0x6e, 0x5a, 0xa0, 0x52, 0x3b, 0xd6, 0xb3, 0x29, 0xe3, 0x2f, 0x84},
                    {0x53, 0xd1, 0x00, 0xed, 0x20, 0xfc, 0xb1, 0x5b, 0x6a, 0xcb, 0xbe, 0x39, 0x4a, 0x4c, 0x58, 0xcf},
                    {0xd0, 0xef, 0xaa, 0xfb, 0x43, 0x4d, 0x33, 0x85, 0x45, 0xf9, 0x02, 0x7f, 0x50, 0x3c, 0x9f, 0xa8},
                    {0x51, 0xa3, 0x40, 0x8f, 0x92, 0x9d, 0x38, 0xf5, 0xbc, 0xb6, 0xda, 0x21, 0x10, 0xff, 0xf3, 0xd2},
                    {0xcd, 0x0c, 0x13, 0xec, 0x5f, 0x97, 0x44, 0x17, 0xc4, 0xa7, 0x7e, 0x3d, 0x64, 0x5d, 0x19, 0x73},
                    {0x60, 0x81, 0x4f, 0xdc, 0x22, 0x2a, 0x90, 0x88, 0x46, 0xee, 0xb8, 0x14, 0xde, 0x5e, 0x0b, 0xdb},
                    {0xe0, 0x32, 0x3a, 0x0a, 0x49, 0x06, 0x24, 0x5c, 0xc2, 0xd3, 0xac, 0x62, 0x91, 0x95, 0xe4, 0x79},
                    {0xe7, 0xc8, 0x37, 0x6d, 0x8d, 0xd5, 0x4e, 0xa9, 0x6c, 0x56, 0xf4, 0xea, 0x65, 0x7a, 0xae, 0x08},
                    {0xba, 0x78, 0x25, 0x2e, 0x1c, 0xa6, 0xb4, 0xc6, 0xe8, 0xdd, 0x74, 0x1f, 0x4b, 0xbd, 0x8b, 0x8a},
                    {0x70, 0x3e, 0xb5, 0x66, 0x48, 0x03, 0xf6, 0x0e, 0x61, 0x35, 0x57, 0xb9, 0x86, 0xc1, 0x1d, 0x9e},
                    {0xe1, 0xf8, 0x98, 0x11, 0x69, 0xd9, 0x8e, 0x94, 0x9b, 0x1e, 0x87, 0xe9, 0xce, 0x55, 0x28, 0xdf},
                    {0x8c, 0xa1, 0x89, 0x0d, 0xbf, 0xe6, 0x42, 0x68, 0x41, 0x99, 0x2d, 0x0f, 0xb0, 0x54, 0xbb, 0x16}
            };
    private static final int[][] INVERSE_SBOX =
            new int[][] {
                    {0x52, 0x09, 0x6a, 0xd5, 0x30, 0x36, 0xa5, 0x38, 0xbf, 0x40, 0xa3, 0x9e, 0x81, 0xf3, 0xd7, 0xfb},
                    {0x7c, 0xe3, 0x39, 0x82, 0x9b, 0x2f, 0xff, 0x87, 0x34, 0x8e, 0x43, 0x44, 0xc4, 0xde, 0xe9, 0xcb},
                    {0x54, 0x7b, 0x94, 0x32, 0xa6, 0xc2, 0x23, 0x3d, 0xee, 0x4c, 0x95, 0x0b, 0x42, 0xfa, 0xc3, 0x4e},
                    {0x08, 0x2e, 0xa1, 0x66, 0x28, 0xd9, 0x24, 0xb2, 0x76, 0x5b, 0xa2, 0x49, 0x6d, 0x8b, 0xd1, 0x25},
                    {0x72, 0xf8, 0xf6, 0x64, 0x86, 0x68, 0x98, 0x16, 0xd4, 0xa4, 0x5c, 0xcc, 0x5d, 0x65, 0xb6, 0x92},
                    {0x6C, 0x70, 0x48, 0x50, 0xfd, 0xed, 0xb9, 0xda, 0x5e, 0x15, 0x46, 0x57, 0xa7, 0x8d, 0x9d, 0x84},
                    {0x90, 0xd8, 0xab, 0x00, 0x8c, 0xbc, 0xd3, 0x0a, 0xf7, 0xe4, 0x58, 0x05, 0xb8, 0xb3, 0x45, 0x06},
                    {0xd0, 0x2c, 0x1e, 0x8f, 0xca, 0x3f, 0x0f, 0x02, 0xc1, 0xaf, 0xbd, 0x03, 0x01, 0x13, 0x8a, 0x6b},
                    {0x3a, 0x91, 0x11, 0x41, 0x4f, 0x67, 0xdc, 0xea, 0x97, 0xf2, 0xcf, 0xce, 0xf0, 0xb4, 0xe6, 0x73},
                    {0x96, 0xac, 0x74, 0x22, 0xe7, 0xad, 0x35, 0x85, 0xe2, 0xf9, 0x37, 0xe8, 0x1c, 0x75, 0xdf, 0x6e},
                    {0x47, 0xf1, 0x1a, 0x71, 0x1d, 0x29, 0xc5, 0x89, 0x6f, 0xb7, 0x62, 0x0e, 0xaa, 0x18, 0xbe, 0x1b},
                    {0xfc, 0x56, 0x3e, 0x4b, 0xc6, 0xd2, 0x79, 0x20, 0x9a, 0xdb, 0xc0, 0xfe, 0x78, 0xcd, 0x5a, 0xf4},
                    {0x1f, 0xdd, 0xA8, 0x33, 0x88, 0x07, 0xc7, 0x31, 0xb1, 0x12, 0x10, 0x59, 0x27, 0x80, 0xec, 0x5f},
                    {0x60, 0x51, 0x7f, 0xa9, 0x19, 0xb5, 0x4a, 0x0d, 0x2d, 0xe5, 0x7a, 0x9f, 0x93, 0xc9, 0x9c, 0xef},
                    {0xa0, 0xe0, 0x3b, 0x4d, 0xae, 0x2a, 0xf5, 0xb0, 0xc8, 0xeb, 0xbb, 0x3c, 0x83, 0x53, 0x99, 0x61},
                    {0x17, 0x2b, 0x04, 0x7e, 0xba, 0x77, 0xd6, 0x26, 0xe1, 0x69, 0x14, 0x63, 0x55, 0x21, 0x0c, 0x7d}
            };
    private static final int[] RCON =
            new int[] {0x00, 0x01, 0x02, 0x04, 0x08, 0x10, 0x20, 0x40, 0x80, 0x1B, 0x36};
    private static final int N_OF_COL = 4;
    private static final int N_OF_KEY = 8;
    private static final int N_OF_ROUND = 14;
    private static final int N_OF_WORD = 60;

    static int iterator;
    static int indexOfWord;
    static boolean statusProses;
    static boolean keyHasCreated = false;

    static int[][] arrKunci;
    static int[][] arrPesan;
    static int[][] arrChiper;

    public static HAES256 getInstance() {
        return ourInstance;
    }

    private HAES256() {
        AESH.indexOfWord = 0;
        keyHasCreated = false;
        arrKunci = new int[N_OF_COL][(N_OF_ROUND + 1) * N_OF_COL];
        arrPesan = new int[N_OF_COL][N_OF_COL];
        arrChiper = new int[N_OF_COL][N_OF_COL];
    }

    public static void ReInitProps() {
        AESH.indexOfWord = 0;
        keyHasCreated = false;
        arrKunci = new int[N_OF_COL][(N_OF_ROUND + 1) * N_OF_COL];
        arrPesan = new int[N_OF_COL][N_OF_COL];
        arrChiper = new int[N_OF_COL][N_OF_COL];
    }

    public static boolean isKeyHasCreated() {
        return keyHasCreated;
    }

    public static int[][] getArrKunci() {
        return arrKunci;
    }

    public static int[][] getArrPesan() {
        return arrPesan;
    }
    public static int[][] getArrChiper() { return arrChiper; }

    public static int[] getEncryptResult1D() {
        int[] tmp = new int[16];
        int indeks = 0;
        for (int i = 0; i < N_OF_COL; i += 1) {
            for (int j = 0; j < N_OF_COL; j += 1) {
                tmp[indeks] = arrPesan[j][i];
            }
        }
        return tmp;
    }

    public static int[] getDecryptResult1D() {
        int[] tmp = new int[16];
        int indeks = 0;
        for (int i = 0; i < N_OF_COL; i += 1) {
            for (int j = 0; j < N_OF_COL; j += 1) {
                tmp[indeks] = arrChiper[j][i];
            }
        }
        return tmp;
    }

    public static boolean encrypt(String pesan, String kunci) {
        statusProses = true;
        if (!keyHasCreated) {
            boolean result = keyExpansion(kunci);
            if (!result) {
                statusProses = false;
                throw new ExceptionInInitializerError("Error: Kesalahan pada Key Expansion");
            }
        }
        AESH.indexOfWord = 0;
        if (statusProses) {
            boolean resKonversiPesan = toArray2D(arrPesan, pesan);
            if (resKonversiPesan) {
                addRoundKeys(arrPesan, arrKunci, true);
                for (short ronde = 1; ronde < N_OF_ROUND; ronde += 1) {
                    subBytePesan(arrPesan, SBOX);
                    shiftRow(arrPesan);
                    mixColumns(arrPesan);
                    addRoundKeys(arrPesan, arrKunci, true);
                }
                subBytePesan(arrPesan, SBOX);
                shiftRow(arrPesan);
                addRoundKeys(arrPesan, arrKunci, true);

            } else {
                statusProses = false;
            }
        }
        return  statusProses;
    }

    public static boolean encryptDataInteger(int[] pesan, int[] kunci) {
        statusProses = true;
        if (!keyHasCreated) {
            //boolean result = keyExpansion(kunci);
            if (!keyExpansion(kunci)) {
                statusProses = false;
                throw new ExceptionInInitializerError("Error: Kesalahan pada Key Expansion");
            }
        }
        AESH.indexOfWord = 0;
        if (statusProses) {
            boolean resKonversiPesan = toArray2D(arrPesan, pesan);
            if (resKonversiPesan) {
                addRoundKeys(arrPesan, arrKunci, true);
                for (short ronde = 1; ronde < N_OF_ROUND; ronde += 1) {
                    subBytePesan(arrPesan, SBOX);
                    shiftRow(arrPesan);
                    mixColumns(arrPesan);
                    addRoundKeys(arrPesan, arrKunci, true);
                }
                subBytePesan(arrPesan, SBOX);
                shiftRow(arrPesan);
                addRoundKeys(arrPesan, arrKunci, true);

            } else {
                statusProses = false;
            }
        }
        return statusProses;
    }

    public static boolean decrypt(String chiper, String kunci) {
        statusProses = true;
        AESH.indexOfWord = 0;
        if (!keyHasCreated) {
            boolean result = keyExpansion(kunci);
            if (!result) {
                statusProses = false;
                throw new ExceptionInInitializerError("Error: Kesalahan pada Key Expansion Decrypt");
            }
        }

        if (statusProses) {
            AESH.indexOfWord = 59;
            boolean resKonversiPesan = toArray2D(arrChiper, chiper);
            if (resKonversiPesan) {
                addRoundKeys(arrChiper, arrKunci, false);

                for (short ronde = 1; ronde < N_OF_ROUND; ronde += 1) {
                    invShiftRow(arrChiper);
                    subBytePesan(arrChiper, INVERSE_SBOX);
                    addRoundKeys(arrChiper, arrKunci, false);
                    invMixColumns(arrChiper);
                }
                invShiftRow(arrChiper);
                subBytePesan(arrChiper, INVERSE_SBOX);
                addRoundKeys(arrChiper, arrKunci, false);
            } else {
                statusProses = false;
            }
        }
        return statusProses;
    }

    public static boolean decryptDataInteger(int[] chiper, int[] kunci) {
        statusProses = true;
        AESH.indexOfWord = 0;
        if (!keyHasCreated) {
            //boolean result = keyExpansion(kunci);
            if (!keyExpansion(kunci)) {
                statusProses = false;
                throw new ExceptionInInitializerError("Error: Kesalahan pada Key Expansion Decrypt");
            }
        }

        if (statusProses) {
            AESH.indexOfWord = 59;
            boolean resKonversiPesan = toArray2D(arrChiper, chiper);
            if (resKonversiPesan) {
                addRoundKeys(arrChiper, arrKunci, false);

                for (short ronde = 1; ronde < N_OF_ROUND; ronde += 1) {
                    invShiftRow(arrChiper);
                    subBytePesan(arrChiper, INVERSE_SBOX);
                    addRoundKeys(arrChiper, arrKunci, false);
                    invMixColumns(arrChiper);
                }
                invShiftRow(arrChiper);
                subBytePesan(arrChiper, INVERSE_SBOX);
                addRoundKeys(arrChiper, arrKunci, false);
            } else {
                statusProses = false;
            }
        }
        return statusProses;
    }

    private static boolean keyExpansion(String kunci) {
        boolean proses = true;
        if (toArray2D(arrKunci, kunci)) {
            //proses mendapatkan w8 - w59
            iterator = N_OF_KEY; //untuk iterasi w8 - w59
            while (iterator < N_OF_COL * (N_OF_ROUND + 1)) {
                int[] temp = getWord(arrKunci, iterator - 1);
                if (iterator % N_OF_KEY == 0) //jika habis dibagi 8
                {
                    temp = substitudeWord(rotateWord(temp), SBOX);
                    temp = XOR(temp, new int[] {RCON[iterator / N_OF_KEY], 0x00, 0x00, 0x00});
                }
                else if (iterator % N_OF_KEY == 4) //jika habis dibagi 4
                {
                    temp = substitudeWord(temp, SBOX);
                }
                temp = XOR(getWord(arrKunci, iterator - N_OF_KEY), temp);
                for (short i = 0; i < 4; i += 1)
                    arrKunci[i][iterator] = temp[i];
                iterator += 1;
            } //end while
            keyHasCreated = true;
        } else {
            proses = false;
        }
        return  proses;
    }

    private static boolean keyExpansion(int[] kunci) {
        boolean proses = true;
        if (toArray2D(arrKunci, kunci)) {
            //proses mendapatkan w8 - w59
            iterator = N_OF_KEY; //untuk iterasi w8 - w59
            while (iterator < N_OF_COL * (N_OF_ROUND + 1)) {
                int[] temp = getWord(arrKunci, iterator - 1);
                if (iterator % N_OF_KEY == 0) //jika habis dibagi 8
                {
                    temp = substitudeWord(rotateWord(temp), SBOX);
                    temp = XOR(temp, new int[] {RCON[iterator / N_OF_KEY], 0x00, 0x00, 0x00});
                }
                else if (iterator % N_OF_KEY == 4) //jika habis dibagi 4
                {
                    temp = substitudeWord(temp, SBOX);
                }
                temp = XOR(getWord(arrKunci, iterator - N_OF_KEY), temp);
                for (short i = 0; i < 4; i += 1)
                    arrKunci[i][iterator] = temp[i];
                iterator += 1;
            } //end while
            keyHasCreated = true;
        } else {
            proses = false;
        }
        return  proses;
    }

    private static void addRoundKeys(int[][] state, int[][] keys, boolean encryptProcess) // proses round enkrip dekrip
    {
        int[] tmpState;
        int[] tmpKeys;
        int wc = AESH.indexOfWord - 3;
        for (short i = 0; i < state.length; i += 1)
        {
            tmpState = getWord(state, i);
            tmpKeys  = encryptProcess ? getWord(keys, AESH.indexOfWord) : getWord(keys, wc); //ubah
            tmpState = XOR(tmpState, tmpKeys);

            for (short j = 0; j < state[0].length; j += 1)
                state[j][i] = tmpState[j];
            if (encryptProcess)
                AESH.indexOfWord += 1;
            else
            {
                AESH.indexOfWord -= 1;
                wc += 1;
            }
        }
    }

    /**
     * Fungsi untuk proses SubByte pesan
     * @param state merupakan array dua dimensi dari pesan
     * @param sb merupakan array dua dimensi dari SBOX
     */
    private static void subBytePesan(int[][] state, int[][] sb)
    {
        int[] tmp;
        for (int i = 0; i < state[0].length; i += 1) //ketentuan panjang kolom
        {
            tmp = substitudeWord(getWord(state, i), sb);
            for (int j = 0; j < state.length; j += 1) //ketentuan panjang baris
            {
                state[j][i] = tmp[j];
            }
        }
    }

    private static void shiftRow(int[][] state) //fungsi untuk shift row
    {
        short shift = 1;
        int[] tmp;
        while (shift < 4)
        {
            tmp = new int[shift];
            for (short i = 0; i < shift; i += 1)
                tmp[i] = state[shift][i];

            for (short i = 0; i < shift; i += 1) //proses pergeseran hingga i proses
            {
                for (short col = 0; col < 3; col += 1)
                    state[shift][col] = state[shift][col + 1];
            }
            for (short i = shift, indeks = 3; i > 0; i -= 1) {
                state[shift][indeks] = tmp[i - 1];
                indeks -= 1;
            }
            shift += 1;
        }
    }

    private static void invShiftRow(int[][] state)
    {
        short shift = 1;
        int[] tmp;
        while (shift < 4)
        {
            tmp = new int[shift];
            for (short i = 0, indeks = 3; i < shift; i += 1)
            {
                tmp[i] = state[shift][indeks];
                indeks -= 1;
            }
            for (short i = 0; i < shift; i += 1)
            {
                for (short col = 3; col > 0; col -= 1)
                    state[shift][col] = state[shift][col - 1];
            }
            for (short i = 0, col = shift; i < shift; i += 1)
            {
                state[shift][i] = tmp[col - 1];
                col -= 1;
            }
            shift += 1;
        }
    }

    /*private static void mixColumns(int[][] s) {
        // 's' is the main State matrix, 'ss' is a temp matrix of the same dimensions as 's'.
        int[][] ss = new int[4][4];
       //Array.Clear(ss, 0, ss.Length);

        for (int c = 0; c < 4; c++) {
            ss[0][c] = (GMul(0x02, s[0][c]) ^ GMul(0x03, s[1][c]) ^ s[2][c] ^ s[3][c]);
            ss[1][c] = (s[0][c] ^ GMul(0x02, s[1][c]) ^ GMul(0x03, s[2][c]) ^ s[3][c]);
            ss[2][c] = (s[0][c] ^ s[1][c] ^ GMul(0x02, s[2][c]) ^ GMul(0x03, s[3][c]));
            ss[3][c] = (GMul(0x03, s[0][c]) ^ s[1][c] ^ s[2][c] ^ GMul(0x02, s[3][c]));
        }
        //Array.ConstrainedCopy(ss, 0, s, 0, ss.Length);
        System.arraycopy(ss, 0, s, 0, ss.length);
    }*/

    private static void mixColumns(int[][] arr)
    {
        int[] temp = new int[4];
        int[][] mc = { { 2, 3, 1, 1 }, { 1, 2, 3, 1 }, { 1, 1, 2, 3 }, { 3, 1, 1, 2 } };
        for (int col = 0; col < 4; col++)
        {
            int res;
            for (int i = 0; i < 4; i++)
            {
                res = 0;
                for (int j = 0; j < 4; j++)
                {
                    res ^= GF_8(mc[i][j], arr[j][col]);
                }
                temp[i] = res;
            }
            for (int i = 0; i < 4; i++)
            {
                arr[i][col] = temp[i];
            }
        }
    }

    private static void invMixColumns(int[][] arr)
    {
        int[] temp = new int[4];
        int[][] mc = { { 14, 11, 13, 9 }, { 9, 14, 11, 13 }, { 13, 9, 14, 11 }, { 11, 13, 9, 14 } };
        for (int col = 0; col < 4; col++)
        {
            int res;
            for (int i = 0; i < 4; i++)
            {
                res = 0;
                for (int j = 0; j < 4; j++)
                {
                    res ^= GF_8(mc[i][j], arr[j][col]);
                }
                temp[i] = res;
            }
            for (int i = 0; i < 4; i++)
            {
                arr[i][col] = temp[i];
            }
        }
    }

    public static int GF_8(int number1, int number2)
    {
        int res = 0;
        while (number2 >= 1)
        {
            //cek
            if ((number2 & 1) == 1)
            {
                res ^= number1;
            }
            number1 <<= 1;
            if ((number1 & 1 << 8) == 1 << 8)
                number1 ^= 0x00011B;
            number2 >>= 1;
        }
        return res;
    }

    private static Integer GMul(int a, int b) { // Galois Field (256) Multiplication of two Bytes
        Integer p = 0;
        Integer counter;
        Integer hi_bit_set;
        for (counter = 0; counter < 8; counter++) {
            if ((b & 1) != 0) {
                p ^= a;
            }
            hi_bit_set = (a & 0x80);
            a <<= 1;
            if (hi_bit_set != 0) {
                a ^= 0x1b; /* x^8 + x^4 + x^3 + x + 1 */
            }
            b >>= 1;
        }
        return p;
    }

    private static int[] getWord(int[][] w, int indeks)
    {
        int[] tmp = new int[4];
        for (short i = 0; i < 4; i += 1)
        {
            tmp[i] = w[i][indeks];
        }
        return tmp;
    }

    private static int[] substitudeWord(int[] words, int[][] sb)
    {
        int[] tmp = new int[N_OF_COL];

        for (short i = 0; i < N_OF_COL; i += 1) {
            String hexa = Integer.toHexString(words[i]);
            if (hexa.length() == 1)
                hexa = "0" + hexa;
            tmp[i] = sb[Integer.decode("0x" + hexa.charAt(0))][Integer.decode("0x" + hexa.charAt(1))];
        }
        return tmp;
    }

    private static int[] rotateWord(int[] w)
    {
        int[] tmp = w;
        int first = w[0];
        for (int i = 0; i < 3; i += 1) {
            tmp[i] = tmp[i+1];
        }
        tmp[3] = first;
        return tmp;
    }

    private static int[] XOR(int[] words, int[] anothers)
    {
        int[] tmp = new int[N_OF_COL];
        for (int i = 0; i < N_OF_COL; i += 1)
        {
            tmp[i] = words[i] ^ anothers[i];
        }
        return tmp;
    }

    private static boolean toArray2D(int[][] target, String source) //fungsi untuk mengubah array 1 dimensi ke 2 dimensi
    {
        char[] arrSource = source.toCharArray();
        int indeks = 0;
        boolean status = true;
        for (short i = 0; i < target[0].length; i += 1)
        {
            for (short j = 0; j < target.length; j += 1)
            {
                try {
                    if (indeks < arrSource.length) {
                        target[j][i] = (int)arrSource[indeks];
                        indeks += 1;
                    }
                    else
                    {
                        target[j][i] = 0;
                        //target[j, i] = 0x00;
                    }
                } catch (ArrayIndexOutOfBoundsException aie) {
                    status = false;
                }
            }
        }
        return status;
    }

    private static boolean toArray2D(int[][] target, int[] source) {
        int[] arrSource = source;
        int indeks = 0;
        boolean status = true;
        for (short i = 0; i < target[0].length; i += 1)
        {
            for (short j = 0; j < target.length; j += 1)
            {
                try {
                    if (indeks < arrSource.length) {
                        target[j][i] = arrSource[indeks];
                        indeks += 1;
                    }
                    else
                    {
                        target[j][i] = 0;
                    }
                } catch (ArrayIndexOutOfBoundsException aie) {
                    status = false;
                }
            }
        }
        return status;
    }
}
