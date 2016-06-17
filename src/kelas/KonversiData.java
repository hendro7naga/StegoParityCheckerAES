package kelas;

/**
 * Created by hendro.sinaga on 17-Jun-16.
 */
public class KonversiData {
    public static int[] stringToIntArr1D(String source) {
        char[] arr = source.toCharArray();
        int[] tmp = new int[arr.length];
        for (int i = 0; i < arr.length; i += 1) {
            tmp[i] = (int)arr[i];
        }
        return tmp;
    }

    public static int[] arr2DToIntArr1D(int[][] source) {
        int[] tmp = new int[source.length * source[0].length];
        int indeks = 0;
        for (int x = 0; x < source.length; x += 1) {
            for (int y = 0; y < source[x].length; y += 1) {
                //teks += Integer.toHexString(arrHasilEnkrip[x][y]) + "    ";
                tmp[indeks] = source[y][x];
                indeks += 1;
            }
            //teks += "\n";
        }
        return tmp;
    }
}
