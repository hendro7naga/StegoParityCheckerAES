package kelas;

/**
 * Created by hendro.sinaga on 27-Jun-16.
 */
public class ParityChecker {
    /**
     * GenerateParity, mengembalikan 1 jika Odd, 0 jika Even
     * @param r merupakan channel R dari RGB
     * @param g merupakan channel G dari RGB
     * @param b merupakan channel B dari RGB
     * @return nilai hasil parity, 1 jika Odd, 0 jika Even
     */
    public static int generateParity(int r, int g, int b) {
        int parity = 0, counter = 0;

        String rbin = Integer.toBinaryString(r);
        String gbin = Integer.toBinaryString(g);
        String bbin = Integer.toBinaryString(b);

        if (Integer.parseInt(rbin.charAt(rbin.length() - 1) + "", 10) == 1)
            counter += 1;
        if (Integer.parseInt(gbin.charAt(gbin.length() - 1) + "", 10) == 1)
            counter += 1;
        if (Integer.parseInt(bbin.charAt(bbin.length() - 1) + "", 10) == 1)
            counter += 1;

        if (counter > 0) {
            if (counter % 2 != 0)
                parity = 1;
        }
        return parity;
    }

    public static String reverseLSB(String source) {
        String str = source;
        char[] tmp = str.toCharArray();
        if (tmp[tmp.length - 1] == '1') {
            tmp[tmp.length - 1] = '0';
        } else {
            tmp[tmp.length - 1] = '1';
        }
        str = String.valueOf(tmp);
        return str;
    }

    /**
     * Berikan sebuah channel R, G, atau B untuk proses reverse LSB channel
     * @param channel Channel yang akan diproses reverse LSB
     * @return nilai baru channel setelah dilakukan reverse LSB
     */
    public static int takeChannelRGBtoReverse(int channel) {
        int newChannelValue = channel;
        String biner = Integer.toBinaryString(channel);
        biner = reverseLSB(biner);
        newChannelValue = Integer.parseInt(biner, 2);

        return newChannelValue;
    }
}
