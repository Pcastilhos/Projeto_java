package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import model.Hospede;

public class HospedeDAO implements Dao<Hospede> {

    @Override
    public void salvar(Hospede hospede) {
        String sql = "INSERT INTO hospede (nome, cpf, data_nascimento, telefone, email) VALUES (?, ?, ?, ?, ?)";
        try (
                Connection con = ConnectionFactory.getConnection();
                PreparedStatement stmt = con.prepareStatement(sql)
        ) {
            stmt.setString(1, hospede.getNome());
            stmt.setString(2, hospede.getCpf());
            stmt.setString(3, dataParaTexto(hospede.getDataNascimento()));
            stmt.setString(4, hospede.getTelefone());
            stmt.setString(5, hospede.getEmail());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Hospede> listarTodos() {
        String sql = "SELECT * FROM hospede ORDER BY nome";
        List<Hospede> hospedes = new ArrayList<>();
        try (
                Connection con = ConnectionFactory.getConnection();
                PreparedStatement stmt = con.prepareStatement(sql)
        ) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                hospedes.add(montarHospede(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return hospedes;
    }

    // pesquisa por parte do nome ou do CPF
    public List<Hospede> pesquisar(String texto) {
        String sql = "SELECT * FROM hospede WHERE nome LIKE ? OR cpf LIKE ? ORDER BY nome";
        List<Hospede> hospedes = new ArrayList<>();
        try (
                Connection con = ConnectionFactory.getConnection();
                PreparedStatement stmt = con.prepareStatement(sql)
        ) {
            stmt.setString(1, "%" + texto + "%");
            stmt.setString(2, "%" + texto + "%");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                hospedes.add(montarHospede(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return hospedes;
    }

    @Override
    public Hospede buscarPorId(int id) {
        String sql = "SELECT * FROM hospede WHERE id = ?";
        try (
                Connection con = ConnectionFactory.getConnection();
                PreparedStatement stmt = con.prepareStatement(sql)
        ) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return montarHospede(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public Hospede buscarPorCpf(String cpf) {
        String sql = "SELECT * FROM hospede WHERE cpf = ?";
        try (
                Connection con = ConnectionFactory.getConnection();
                PreparedStatement stmt = con.prepareStatement(sql)
        ) {
            stmt.setString(1, cpf);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return montarHospede(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public void atualizar(Hospede hospede) {
        String sql = "UPDATE hospede SET nome = ?, cpf = ?, data_nascimento = ?, telefone = ?, email = ? WHERE id = ?";
        try (
                Connection con = ConnectionFactory.getConnection();
                PreparedStatement stmt = con.prepareStatement(sql)
        ) {
            stmt.setString(1, hospede.getNome());
            stmt.setString(2, hospede.getCpf());
            stmt.setString(3, dataParaTexto(hospede.getDataNascimento()));
            stmt.setString(4, hospede.getTelefone());
            stmt.setString(5, hospede.getEmail());
            stmt.setInt(6, hospede.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void excluir(int id) {
        String sql = "DELETE FROM hospede WHERE id = ?";
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

    private Hospede montarHospede(ResultSet rs) throws SQLException {
        LocalDate nascimento = null;
        if (rs.getString("data_nascimento") != null) {
            nascimento = LocalDate.parse(rs.getString("data_nascimento"));
        }
        return new Hospede(rs.getInt("id"), rs.getString("nome"), rs.getString("cpf"), nascimento,
                rs.getString("telefone"), rs.getString("email"));
    }

    // o MySQL aceita datas no formato aaaa-mm-dd
    private String dataParaTexto(LocalDate data) {
        if (data == null) {
            return null;
        }
        return data.toString();
    }
}
