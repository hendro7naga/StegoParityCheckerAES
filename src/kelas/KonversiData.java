package kelas;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Created by hendro.sinaga on 17-Jun-16.
 */
public class KonversiData {
    public static String paddingInLeftBinaryString(String source, int paddTo) {
        String tmp = source;
        if (tmp.length() < paddTo) {
            for (int i = tmp.length(); i < paddTo; i += 1) {
                tmp = "0" + tmp;
            }
        }
        return tmp;
    }

    public static String paddingInRightBinaryString(String source, int paddTo) {
        String temp = source;
        if (temp.length() < paddTo) {
            for (int i = temp.length(); i < paddTo; i += 1) {
                temp = temp + "0";
            }
        }
        return temp;
    }

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
                tmp[indeks] = source[y][x];
                indeks += 1;
            }
        }
        return tmp;
    }

    public static int[] arraylist1DToArr1D(ArrayList<Integer> source) {
        int[] tmp = new int[source.size()];
        for (int i = 0; i < source.size(); i += 1) {
            tmp[i] = source.get(i);
        }
        return tmp;
    }

    public static int[] getDistinctArray(int[] arr) {
        if (arr == null)
            return null;
        List<Integer> list = new ArrayList<>();
        list.add(arr[0]);

        for (int i = 1; i < arr.length; i += 1) {
            if (!list.contains(arr[i])) {
                list.add(arr[i]);
            }
        }
        int[] res = new int[list.size()];
        for (int i = 0; i < list.size(); i += 1) {
            res[i] = list.get(i);
        }
        return res;
    }
}
