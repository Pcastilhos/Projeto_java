package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

// abre a conexão com o banco MySQL do XAMPP
public class ConnectionFactory {

    private static final String URL = "jdbc:mysql://localhost:3306/rede_hoteleira";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            throw new RuntimeException("Não foi possível conectar ao banco. Verifique se o MySQL do XAMPP está ligado.", e);
        }
    }
}
