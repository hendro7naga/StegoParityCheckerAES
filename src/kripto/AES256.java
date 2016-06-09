package kripto;

/**
 * Created by hendro.sinaga on 10-Jun-16.
 */
public class AES256 {
    private static AES256 ourInstance = new AES256();

    public static AES256 getInstance() {
        return ourInstance;
    }

    private AES256() {
    }
}
