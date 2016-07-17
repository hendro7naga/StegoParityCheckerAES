package main;

import java.io.File;
import java.sql.Statement;

/**
 * Created by hendro.sinaga on 15-Jul-16.
 */
public class AppControll {
    public static final String USER_DIR = "user.home";
    public static final String TABLE_STEGANO_NAME = "stegano";
    public static final String TABLE_STEGANO_NOISE_NAME = "steganonoise";
    public static final String DB_NAME = "main.db";
    private static String pathSeparator;
    private boolean isAlreadyInitialize = false;
    public SQLiteDB sqLiteDB;

    private static AppControll appControll = new AppControll();

    public static AppControll getInstance() {
        return appControll;
    }

    private AppControll() {
    }

    private void checkPathSeparator() {
        String osVendor = System.getenv("OS").toString();
        if (osVendor.contains("Windows")) {
            AppControll.pathSeparator = "\\";
        } else {
            AppControll.pathSeparator = "/";
        }
    }

    private void checkDirektori() throws Exception { //sudah oke
        boolean initDir = true;
        String dir = System.getProperty(AppControll.USER_DIR) + pathSeparator + "steganoparity";
        String db = dir + pathSeparator + AppControll.DB_NAME;
        File file = new File(dir);

        if (!file.exists()) {
            initDir = file.mkdir();
        }
        if (!initDir) {
            throw new Exception("Gagal membuat direktori aplikasi...");
        } else {
            file = new File(db);
            if (!file.exists()) {
                if (!file.createNewFile()) {
                    throw new Exception("Gagal membuat file database...");
                }
            }
        }
    }

    private void checkTable() throws Exception {
        try {
            Statement statement = this.sqLiteDB.createConnection().createStatement();
            String sql = "CREATE TABLE IF NOT EXISTS " + AppControll.TABLE_STEGANO_NAME
                    + " (id INTEGER PRIMARY KEY NOT NULL, "
                    + "oriImageName VARCHAR NOT NULL, "
                    + "stegoImageName VARCHAR NOT NULL, "
                    + "msgLength INTEGER NOT NULL, "
                    + "mseVal DOUBLE NOT NULL, "
                    + "psnrVal DOUBLE NOT NULL"
                    + ");";
            if (statement.executeUpdate(sql) > -1) {
                sql = "CREATE TABLE IF NOT EXISTS " + AppControll.TABLE_STEGANO_NOISE_NAME
                        + " (nid INTEGER PRIMARY KEY NOT NULL, "
                        + "sid INTEGER NOT NULL, "
                        + "noiseProb DOUBLE NOT NULL, "
                        + "percentage DOUBLE NOT NULL"
                        + ");";
                statement.executeUpdate(sql);
            }
            statement.close();
            this.sqLiteDB.closeConnection();
        } catch (Exception e) {
            throw e;
        }
    }

    public int init() throws Exception {
        int status = 0;
        try {
            checkPathSeparator();
            checkDirektori();
            this.sqLiteDB = SQLiteDB.getInstance();
            if (!this.sqLiteDB.getInitializeStatus()) {
                if (this.sqLiteDB.initialize(AppControll.DB_NAME) == 0) {
                    throw new Exception("Gagal inisialisasi koneksi pada database");
                }
            }
            this.checkTable();
            this.isAlreadyInitialize = true;
            status = 1;
        } catch (Exception ex) {
            throw ex;
        }
        return status;
    }

    public boolean getInitializeStatus () {
        return this.isAlreadyInitialize;
    }

    public String getPathSeparator () {
        return AppControll.pathSeparator;
    }
}
