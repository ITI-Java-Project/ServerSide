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
            System.out.println(conn + "..................");
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

    public boolean executeUpdate(String query) {
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

    public int insertAndGetId(String query, Object... params) {
        PreparedStatement ps = null;
        Statement stmt = null;
        ResultSet rs = null;
        
        try {
            // 1. Prepare and execute insert with parameters
            ps = conn.prepareStatement(query);
            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }
            
            int rowsAffected = ps.executeUpdate();
            System.out.println("Insert affected rows: " + rowsAffected);
            
            if (rowsAffected == 0) {
                System.err.println("Insert failed - no rows affected");
                return -1;
            }

            // 2. Get last generated ID (Derby way)
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT IDENTITY_VAL_LOCAL() FROM SYSIBM.SYSDUMMY1");

            if (rs.next()) {
                int id = rs.getInt(1);
                System.out.println("Generated ID: " + id);
                return id;
            } else {
                System.err.println("Could not retrieve generated ID");
                return -1;
            }

        } catch (SQLException e) {
            System.err.println("Error in insertAndGetId: " + e.getMessage());
            e.printStackTrace();
            return -1;
        } finally {
            // Close resources in reverse order
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (ps != null) ps.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }
}