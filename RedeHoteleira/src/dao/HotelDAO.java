package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.Hotel;

public class HotelDAO implements Dao<Hotel> {

    @Override
    public void salvar(Hotel hotel) {
        String sql = "INSERT INTO hotel (nome, endereco, cidade, telefone, estrelas) VALUES (?, ?, ?, ?, ?)";
        try (
                Connection con = ConnectionFactory.getConnection();
                PreparedStatement stmt = con.prepareStatement(sql)
        ) {
            stmt.setString(1, hotel.getNome());
            stmt.setString(2, hotel.getEndereco());
            stmt.setString(3, hotel.getCidade());
            stmt.setString(4, hotel.getTelefone());
            stmt.setInt(5, hotel.getEstrelas());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Hotel> listarTodos() {
        String sql = "SELECT * FROM hotel ORDER BY nome";
        List<Hotel> hoteis = new ArrayList<>();
        try (
                Connection con = ConnectionFactory.getConnection();
                PreparedStatement stmt = con.prepareStatement(sql)
        ) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                hoteis.add(montarHotel(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return hoteis;
    }

    @Override
    public Hotel buscarPorId(int id) {
        String sql = "SELECT * FROM hotel WHERE id = ?";
        try (
                Connection con = ConnectionFactory.getConnection();
                PreparedStatement stmt = con.prepareStatement(sql)
        ) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return montarHotel(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public void atualizar(Hotel hotel) {
        String sql = "UPDATE hotel SET nome = ?, endereco = ?, cidade = ?, telefone = ?, estrelas = ? WHERE id = ?";
        try (
                Connection con = ConnectionFactory.getConnection();
                PreparedStatement stmt = con.prepareStatement(sql)
        ) {
            stmt.setString(1, hotel.getNome());
            stmt.setString(2, hotel.getEndereco());
            stmt.setString(3, hotel.getCidade());
            stmt.setString(4, hotel.getTelefone());
            stmt.setInt(5, hotel.getEstrelas());
            stmt.setInt(6, hotel.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void excluir(int id) {
        String sql = "DELETE FROM hotel WHERE id = ?";
        try (
                Connection con = ConnectionFactory.getConnection();
                PreparedStatement stmt = con.prepareStatement(sql)
        ) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // transforma a linha do banco em um objeto Hotel
    private Hotel montarHotel(ResultSet rs) throws SQLException {
        return new Hotel(rs.getInt("id"), rs.getString("nome"), rs.getString("endereco"),
                rs.getString("cidade"), rs.getString("telefone"), rs.getInt("estrelas"));
    }
}
