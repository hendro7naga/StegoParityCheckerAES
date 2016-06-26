package kelas;

import java.math.BigInteger;

/**
 * Created by hendro.sinaga on 27-Jun-16.
 */
public class Matematik {
    private static boolean subGemod(int g, int pangkat, int mods) {
        boolean gemodStatus = false;
        int loopState = pangkat - 1;
        int proses = 0;
        BigInteger gBig = new BigInteger(g + "", 10);
        BigInteger primeNumBig = new BigInteger(pangkat + "", 10);
        BigInteger modBig = new BigInteger(mods + "", 10);
        BigInteger tmpResult;

        gBig = gBig.modPow(primeNumBig, modBig);
        if (gBig.intValue() == 1) {
            gemodStatus = true;
        }
        return gemodStatus;
    }

    public static boolean isGemod(int g, int primeNum, int mods) {
        boolean gemodStatus = false;
        boolean clockwase = false;
        int proses = 0;
        int primeNumProses = primeNum - 1;

        while (primeNumProses > 1) {
            if (clockwase) {
                break;
            }
            else {
                boolean tmpGemod = subGemod(g, primeNumProses, mods);
                if (tmpGemod == true) {
                    if (proses >= 1) {
                        clockwase = true;
                        gemodStatus = false;
                    } else {
                        gemodStatus = true;
                    }
                }
                primeNumProses = primeNumProses / 2;
                proses += 1;
            }
        }

        return gemodStatus;
    }
}
