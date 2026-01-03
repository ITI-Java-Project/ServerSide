package data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.apache.derby.jdbc.ClientDriver;

public class DbManager {

    private static DbManager dbManager;
    private Connection conn;

    private DbManager() {
        try {
            DriverManager.registerDriver(new ClientDriver());
            conn = DriverManager.getConnection("jdbc:derby://localhost:1527/tic_tac_toe_database", "root", "root");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static synchronized DbManager init() {
        if (dbManager == null) {
            dbManager = new DbManager();
        }
        return dbManager;
    }

    public ResultSet getQuery(String query) {
        try {
            Statement stmt = conn.createStatement();
            return stmt.executeQuery(query);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean insertQuery(String query) {
        return executeUpdate(query);
    }

    public boolean updateQuery(String query) {
        return executeUpdate(query);
    }

    public boolean deleteQuery(String query) {
        return executeUpdate(query);
    }

    private boolean executeUpdate(String query) {
        try (Statement stmt = conn.createStatement()) {
            return stmt.executeUpdate(query) > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public ResultSet getQueryPrepared(String query, Object... params) {
        try {
            PreparedStatement ps = prepare(query, params);
            return ps.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean insertQueryPrepared(String query, Object... params) {
        return executePrepared(query, params);
    }

    public boolean updateQueryPrepared(String query, Object... params) {
        return executePrepared(query, params);
    }

    public boolean deleteQueryPrepared(String query, Object... params) {
        return executePrepared(query, params);
    }

    private boolean executePrepared(String query, Object... params) {
        try (PreparedStatement ps = prepare(query, params)) {
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private PreparedStatement prepare(String query, Object... params) throws SQLException {
        PreparedStatement ps = conn.prepareStatement(query);
        for (int i = 0; i < params.length; i++) {
            ps.setObject(i + 1, params[i]);
        }
        return ps;
    }
}
