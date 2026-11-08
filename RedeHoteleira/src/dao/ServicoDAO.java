package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.Servico;

public class ServicoDAO implements Dao<Servico> {

    @Override
    public void salvar(Servico servico) {
        String sql = "INSERT INTO servico (nome, descricao, valor) VALUES (?, ?, ?)";
        try (
                Connection con = ConnectionFactory.getConnection();
                PreparedStatement stmt = con.prepareStatement(sql)
        ) {
            stmt.setString(1, servico.getNome());
            stmt.setString(2, servico.getDescricao());
            stmt.setDouble(3, servico.getValor());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Servico> listarTodos() {
        String sql = "SELECT * FROM servico ORDER BY nome";
        List<Servico> servicos = new ArrayList<>();
        try (
                Connection con = ConnectionFactory.getConnection();
                PreparedStatement stmt = con.prepareStatement(sql)
        ) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                servicos.add(new Servico(rs.getInt("id"), rs.getString("nome"), rs.getString("descricao"), rs.getDouble("valor")));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return servicos;
    }

    @Override
    public Servico buscarPorId(int id) {
        String sql = "SELECT * FROM servico WHERE id = ?";
        try (
                Connection con = ConnectionFactory.getConnection();
                PreparedStatement stmt = con.prepareStatement(sql)
        ) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Servico(rs.getInt("id"), rs.getString("nome"), rs.getString("descricao"), rs.getDouble("valor"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public void atualizar(Servico servico) {
        String sql = "UPDATE servico SET nome = ?, descricao = ?, valor = ? WHERE id = ?";
        try (
                Connection con = ConnectionFactory.getConnection();
                PreparedStatement stmt = con.prepareStatement(sql)
        ) {
            stmt.setString(1, servico.getNome());
            stmt.setString(2, servico.getDescricao());
            stmt.setDouble(3, servico.getValor());
            stmt.setInt(4, servico.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void excluir(int id) {
        String sql = "DELETE FROM servico WHERE id = ?";
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
}
