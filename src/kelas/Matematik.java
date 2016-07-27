package kelas;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hendro.sinaga on 27-Jun-16.
 */
public class Matematik {
    private static boolean subGemod(int g, int pangkat, int mods) {
        boolean gemodStatus = false;
        //int loopState = pangkat - 1;
        //int proses = 0;
        BigInteger gBig = new BigInteger(g + "", 10);
        BigInteger primeNumBig = new BigInteger(pangkat + "", 10);
        BigInteger modBig = new BigInteger(mods + "", 10);
        //BigInteger tmpResult;

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

    private static List<Integer> primeFactors(int numbers) {
        int n = numbers;
        List<Integer> factors = new ArrayList<>();
        for (int i = 2; i <= n / i; i++) {
            while (n % i == 0) {
                if (!factors.contains(i)) {
                    factors.add(i);
                }
                n /= i;
            }
        }
        if (n > 1) {
            factors.add(n);
        }
        return factors;
    }

    private static int findGemodNumber(int numberToTest, int yourPrimesNumbers) {
        //BigInteger primesNum = new BigInteger(yourPrimesNumbers + "", 10);
        BigInteger modNums = new BigInteger(yourPrimesNumbers + "", 10);
        int result = yourPrimesNumbers;
        int numbers = numberToTest;
        BigInteger test = new BigInteger(numbers + "", 10);
        List<Integer> faktorPrima = primeFactors(yourPrimesNumbers - 1);

        for (int i = 0; i < faktorPrima.size(); i += 1) {
            BigInteger pangkat = new BigInteger((yourPrimesNumbers -1) / faktorPrima.get(i) + "", 10);
            BigInteger hasilMod = test.modPow(pangkat, modNums);
            if (hasilMod.intValue() == 1) {
                result = yourPrimesNumbers;
                break;
            } else {
                result = hasilMod.intValue();
            }
        }

        return result;
    }

    public static int gemodFinder(int primesNumber) {
        int bilPrima = primesNumber;
        int bilTest = bilPrima - 1;
        int hasilBilangan = bilPrima;
        for (int bilangan = bilTest; bilangan > 1; bilangan -= 1) {
            int hasilnya = findGemodNumber(bilangan, bilPrima);
            if (hasilnya != bilPrima) {
                hasilBilangan = bilangan;
                break;
            }
        }
        return hasilBilangan;
    }

    public static double roundUpDoubleWithTwoDecimalPlace (double d) {
        DecimalFormat decimalFormat = new DecimalFormat("000.000");
        decimalFormat.setRoundingMode(RoundingMode.CEILING);
        return Double.parseDouble(decimalFormat.format(d));
        //return Double.valueOf(decimalFormat.format(d));
        //return bigDecimal.doubleValue();
    }


}
