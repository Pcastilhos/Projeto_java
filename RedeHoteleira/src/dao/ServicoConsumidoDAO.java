package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import model.Servico;
import model.ServicoConsumido;

// grava os serviços usados em cada reserva (tabela reserva_servico)
public class ServicoConsumidoDAO {

    private ServicoDAO servicoDAO = new ServicoDAO();

    public void salvar(ServicoConsumido sc) {
        String sql = "INSERT INTO reserva_servico (reserva_id, servico_id, quantidade, valor_unitario, data) VALUES (?, ?, ?, ?, ?)";
        try (
                Connection con = ConnectionFactory.getConnection();
                PreparedStatement stmt = con.prepareStatement(sql)
        ) {
            stmt.setInt(1, sc.getReservaId());
            stmt.setInt(2, sc.getServico().getId());
            stmt.setInt(3, sc.getQuantidade());
            stmt.setDouble(4, sc.getValorUnitario());
            stmt.setString(5, sc.getData().toString());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<ServicoConsumido> listarPorReserva(int reservaId) {
        String sql = "SELECT * FROM reserva_servico WHERE reserva_id = ?";
        List<ServicoConsumido> lista = new ArrayList<>();
        try (
                Connection con = ConnectionFactory.getConnection();
                PreparedStatement stmt = con.prepareStatement(sql)
        ) {
            stmt.setInt(1, reservaId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Servico servico = servicoDAO.buscarPorId(rs.getInt("servico_id"));
                lista.add(new ServicoConsumido(rs.getInt("id"), rs.getInt("reserva_id"), servico,
                        rs.getInt("quantidade"), rs.getDouble("valor_unitario"), LocalDate.parse(rs.getString("data"))));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return lista;
    }

    // verifica se o serviço já foi usado em alguma reserva
    public boolean servicoFoiUsado(int servicoId) {
        String sql = "SELECT id FROM reserva_servico WHERE servico_id = ?";
        try (
                Connection con = ConnectionFactory.getConnection();
                PreparedStatement stmt = con.prepareStatement(sql)
        ) {
            stmt.setInt(1, servicoId);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void excluirPorReserva(int reservaId) {
        String sql = "DELETE FROM reserva_servico WHERE reserva_id = ?";
        try (
                Connection con = ConnectionFactory.getConnection();
                PreparedStatement stmt = con.prepareStatement(sql)
        ) {
            stmt.setInt(1, reservaId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
