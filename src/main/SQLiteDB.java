package main;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by hendro.sinaga on 15-Jul-16.
 */
public class SQLiteDB {
    final String DRIVER_NAME = "jdbc:sqlite:";
    final String SPESIAL_DIR = "user.home";
    final String DB_DIR = "steganoparity";
    private boolean alreadyInitialize;
    private String pathSeparator, dbName;

    private Connection connection;
    private Statement statement;

    private static SQLiteDB sqLiteDB = new SQLiteDB();
    public static SQLiteDB getInstance() {
        return sqLiteDB;
    }

    private SQLiteDB() {
        this.connection = null;
        this.statement = null;
        this.alreadyInitialize = false;
    }

    public Connection createConnection () throws Exception {
        Connection tempConnection = null;
        boolean koneksiOke = false;
        if (this.getInitializeStatus()) {
            try {
                Class.forName("org.sqlite.JDBC");
                this.connection = DriverManager.getConnection(
                        this.DRIVER_NAME + System.getProperty(this.SPESIAL_DIR)
                                + this.pathSeparator + this.DB_DIR + this.pathSeparator + this.dbName
                );
                koneksiOke = true;
            }
            catch (ClassNotFoundException classNotFoundException) {
                throw new Exception("Class org.slite.JDBC Tidak ditemukan..");
            }
            catch (SQLException sqlException) {
                throw new Exception("Kesalahan SQL Sintaks:\n" + sqlException.getMessage());
            }
        } else {
            throw new Exception("Inisialisasi belum dideklarasi...");
        }
        if (koneksiOke) {
            return this.connection;
        }
        else {
            return tempConnection;
        }
    }

    public ResultSet SelectQuery(String sql) throws Exception{
        if (this.connection.isClosed()) {
            this.statement = this.createConnection().createStatement();
        } else {
            this.statement = this.connection.createStatement();
        }
        ResultSet resultSet = this.statement.executeQuery(sql);
        this.statement.closeOnCompletion();
        return resultSet;
    }

    public int InsertUpdateDeleteQuery(String sql) throws Exception {
        int temp = -1;
        if (this.connection.isClosed()) {
            this.statement = this.createConnection().createStatement();
        } else {
            this.statement = this.connection.createStatement();
        }
        temp = this.statement.executeUpdate(sql);
        this.statement.closeOnCompletion();
        this.closeConnection();
        return temp;
    }

    private void checkPathSeparator() {
        String osVendor = System.getenv("OS").toString();
        if (osVendor.contains("Windows")) {
            this.pathSeparator = "\\";
        } else {
            this.pathSeparator = "/";
        }
    }

    public void closeConnection () throws SQLException {
        if (!(this.connection.isClosed())) {
            this.connection.close();
        }
    }

    public void deleteTableData(String tableName) throws Exception {
        if (this.connection.isClosed()) {
            createConnection();
        }
        this.statement = this.connection.createStatement();
        this.statement.executeUpdate("delete from " + tableName);
        this.statement.executeUpdate("VACUUM " + tableName);
        this.statement.closeOnCompletion();
    }


    public boolean getInitializeStatus () {
        return this.alreadyInitialize;
    }

    public int initialize(String dbName) {
        int status;
        try {
            this.checkPathSeparator();
            this.dbName = dbName;
            this.alreadyInitialize = true;
            status = 1;
        } catch (Exception ex) {
            status = 0;
        }
        return status;
    }

    public int getJumlahBaris(String tableName) throws Exception {
        int temp = 0;
        if (this.connection.isClosed()) {
            this.statement = this.createConnection().createStatement();
        }
        /*ResultSet rs = this.statement.executeQuery("select * from " + tableName);

        while (rs.next()) {
            temp += 1;
        }
        rs.close();*/
        temp = this.statement.executeQuery("SELECT COUNT(*) AS jumlahBaris FROM " + tableName).getInt("jumlahBaris");
        this.statement.close();
        this.connection.close();
        return temp;
    }

}
