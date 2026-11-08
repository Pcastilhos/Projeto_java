package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.Hotel;
import model.Quarto;
import model.QuartoLuxo;
import model.QuartoStandard;
import model.Suite;

public class QuartoDAO implements Dao<Quarto> {

    private HotelDAO hotelDAO = new HotelDAO();

    @Override
    public void salvar(Quarto quarto) {
        String sql = "INSERT INTO quarto (hotel_id, numero, tipo, capacidade, valor_diaria, situacao) VALUES (?, ?, ?, ?, ?, ?)";
        try (
                Connection con = ConnectionFactory.getConnection();
                PreparedStatement stmt = con.prepareStatement(sql)
        ) {
            stmt.setInt(1, quarto.getHotel().getId());
            stmt.setInt(2, quarto.getNumero());
            stmt.setString(3, quarto.getTipo());
            stmt.setInt(4, quarto.getCapacidade());
            stmt.setDouble(5, quarto.getValorDiaria());
            stmt.setString(6, quarto.getSituacao());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Quarto> listarTodos() {
        return listar("SELECT * FROM quarto ORDER BY hotel_id, numero", 0);
    }

    public List<Quarto> listarPorHotel(int hotelId) {
        return listar("SELECT * FROM quarto WHERE hotel_id = ? ORDER BY numero", hotelId);
    }

    @Override
    public Quarto buscarPorId(int id) {
        List<Quarto> quartos = listar("SELECT * FROM quarto WHERE id = ?", id);
        if (quartos.isEmpty()) {
            return null;
        }
        return quartos.get(0);
    }

    @Override
    public void atualizar(Quarto quarto) {
        String sql = "UPDATE quarto SET hotel_id = ?, numero = ?, tipo = ?, capacidade = ?, valor_diaria = ?, situacao = ? WHERE id = ?";
        try (
                Connection con = ConnectionFactory.getConnection();
                PreparedStatement stmt = con.prepareStatement(sql)
        ) {
            stmt.setInt(1, quarto.getHotel().getId());
            stmt.setInt(2, quarto.getNumero());
            stmt.setString(3, quarto.getTipo());
            stmt.setInt(4, quarto.getCapacidade());
            stmt.setDouble(5, quarto.getValorDiaria());
            stmt.setString(6, quarto.getSituacao());
            stmt.setInt(7, quarto.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void excluir(int id) {
        String sql = "DELETE FROM quarto WHERE id = ?";
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

    // executa um SELECT com ou sem parâmetro e monta a lista de quartos
    private List<Quarto> listar(String sql, int parametro) {
        List<Quarto> quartos = new ArrayList<>();
        try (
                Connection con = ConnectionFactory.getConnection();
                PreparedStatement stmt = con.prepareStatement(sql)
        ) {
            if (parametro > 0) {
                stmt.setInt(1, parametro);
            }
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Hotel hotel = hotelDAO.buscarPorId(rs.getInt("hotel_id"));
                quartos.add(criarQuarto(rs.getString("tipo"), rs.getInt("id"), hotel, rs.getInt("numero"),
                        rs.getInt("capacidade"), rs.getDouble("valor_diaria"), rs.getString("situacao")));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return quartos;
    }

    // cria o objeto da subclasse certa de acordo com o tipo gravado no banco
    public static Quarto criarQuarto(String tipo, int id, Hotel hotel, int numero, int capacidade, double valorDiaria, String situacao) {
        if (tipo.equals("LUXO")) {
            return new QuartoLuxo(id, hotel, numero, capacidade, valorDiaria, situacao);
        }
        if (tipo.equals("SUITE")) {
            return new Suite(id, hotel, numero, capacidade, valorDiaria, situacao);
        }
        return new QuartoStandard(id, hotel, numero, capacidade, valorDiaria, situacao);
    }
}
