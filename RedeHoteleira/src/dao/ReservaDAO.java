package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import model.Hospede;
import model.Quarto;
import model.Reserva;

public class ReservaDAO implements Dao<Reserva> {

    private HospedeDAO hospedeDAO = new HospedeDAO();
    private QuartoDAO quartoDAO = new QuartoDAO();
    private ServicoConsumidoDAO servicoConsumidoDAO = new ServicoConsumidoDAO();

    @Override
    public void salvar(Reserva r) {
        String sql = "INSERT INTO reserva (hospede_id, quarto_id, data_entrada, data_saida, quantidade_hospedes, status, valor_total) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (
                Connection con = ConnectionFactory.getConnection();
                PreparedStatement stmt = con.prepareStatement(sql)
        ) {
            stmt.setInt(1, r.getHospede().getId());
            stmt.setInt(2, r.getQuarto().getId());
            stmt.setString(3, r.getDataEntrada().toString());
            stmt.setString(4, r.getDataSaida().toString());
            stmt.setInt(5, r.getQuantidadeHospedes());
            stmt.setString(6, r.getStatus());
            stmt.setDouble(7, r.getValorTotal());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Reserva> listarTodos() {
        return listar("SELECT * FROM reserva ORDER BY data_entrada DESC", 0);
    }

    public List<Reserva> listarPorQuarto(int quartoId) {
        return listar("SELECT * FROM reserva WHERE quarto_id = ?", quartoId);
    }

    public List<Reserva> listarPorHospede(int hospedeId) {
        return listar("SELECT * FROM reserva WHERE hospede_id = ? ORDER BY data_entrada DESC", hospedeId);
    }

    @Override
    public Reserva buscarPorId(int id) {
        List<Reserva> lista = listar("SELECT * FROM reserva WHERE id = ?", id);
        if (lista.isEmpty()) {
            return null;
        }
        return lista.get(0);
    }

    @Override
    public void atualizar(Reserva r) {
        String sql = "UPDATE reserva SET hospede_id = ?, quarto_id = ?, data_entrada = ?, data_saida = ?, "
                + "quantidade_hospedes = ?, status = ?, valor_total = ? WHERE id = ?";
        try (
                Connection con = ConnectionFactory.getConnection();
                PreparedStatement stmt = con.prepareStatement(sql)
        ) {
            stmt.setInt(1, r.getHospede().getId());
            stmt.setInt(2, r.getQuarto().getId());
            stmt.setString(3, r.getDataEntrada().toString());
            stmt.setString(4, r.getDataSaida().toString());
            stmt.setInt(5, r.getQuantidadeHospedes());
            stmt.setString(6, r.getStatus());
            stmt.setDouble(7, r.getValorTotal());
            stmt.setInt(8, r.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void excluir(int id) {
        servicoConsumidoDAO.excluirPorReserva(id);
        String sql = "DELETE FROM reserva WHERE id = ?";
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

    private List<Reserva> listar(String sql, int parametro) {
        List<Reserva> lista = new ArrayList<>();
        try (
                Connection con = ConnectionFactory.getConnection();
                PreparedStatement stmt = con.prepareStatement(sql)
        ) {
            if (parametro > 0) {
                stmt.setInt(1, parametro);
            }
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Hospede hospede = hospedeDAO.buscarPorId(rs.getInt("hospede_id"));
                Quarto quarto = quartoDAO.buscarPorId(rs.getInt("quarto_id"));
                Reserva reserva = new Reserva(rs.getInt("id"), hospede, quarto,
                        LocalDate.parse(rs.getString("data_entrada")), LocalDate.parse(rs.getString("data_saida")),
                        rs.getInt("quantidade_hospedes"), rs.getString("status"), rs.getDouble("valor_total"));
                // carrega também os serviços usados nessa reserva
                reserva.setServicos(servicoConsumidoDAO.listarPorReserva(reserva.getId()));
                lista.add(reserva);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return lista;
    }
}
