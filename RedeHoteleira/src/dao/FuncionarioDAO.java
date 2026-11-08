package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import model.Camareira;
import model.Funcionario;
import model.Gerente;
import model.Hotel;
import model.Recepcionista;

public class FuncionarioDAO implements Dao<Funcionario> {

    private HotelDAO hotelDAO = new HotelDAO();

    @Override
    public void salvar(Funcionario f) {
        String sql = "INSERT INTO funcionario (hotel_id, tipo, nome, cpf, telefone, email, salario, data_contratacao, "
                + "horario_entrada, horario_saida, ferias_inicio, ferias_fim) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (
                Connection con = ConnectionFactory.getConnection();
                PreparedStatement stmt = con.prepareStatement(sql)
        ) {
            preencherParametros(stmt, f);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Funcionario> listarTodos() {
        return listar("SELECT * FROM funcionario ORDER BY nome", 0);
    }

    public List<Funcionario> listarPorHotel(int hotelId) {
        return listar("SELECT * FROM funcionario WHERE hotel_id = ? ORDER BY nome", hotelId);
    }

    @Override
    public Funcionario buscarPorId(int id) {
        List<Funcionario> lista = listar("SELECT * FROM funcionario WHERE id = ?", id);
        if (lista.isEmpty()) {
            return null;
        }
        return lista.get(0);
    }

    public Funcionario buscarPorCpf(String cpf) {
        for (Funcionario f : listarTodos()) {
            if (f.getCpf().equals(cpf)) {
                return f;
            }
        }
        return null;
    }

    @Override
    public void atualizar(Funcionario f) {
        String sql = "UPDATE funcionario SET hotel_id = ?, tipo = ?, nome = ?, cpf = ?, telefone = ?, email = ?, salario = ?, "
                + "data_contratacao = ?, horario_entrada = ?, horario_saida = ?, ferias_inicio = ?, ferias_fim = ? WHERE id = ?";
        try (
                Connection con = ConnectionFactory.getConnection();
                PreparedStatement stmt = con.prepareStatement(sql)
        ) {
            preencherParametros(stmt, f);
            stmt.setInt(13, f.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void excluir(int id) {
        String sql = "DELETE FROM funcionario WHERE id = ?";
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

    // os parâmetros do INSERT e do UPDATE são os mesmos, por isso ficam num método só
    private void preencherParametros(PreparedStatement stmt, Funcionario f) throws SQLException {
        stmt.setInt(1, f.getHotel().getId());
        stmt.setString(2, f.getCargo());
        stmt.setString(3, f.getNome());
        stmt.setString(4, f.getCpf());
        stmt.setString(5, f.getTelefone());
        stmt.setString(6, f.getEmail());
        stmt.setDouble(7, f.getSalario());
        stmt.setString(8, dataParaTexto(f.getDataContratacao()));
        stmt.setString(9, f.getHorarioEntrada());
        stmt.setString(10, f.getHorarioSaida());
        stmt.setString(11, dataParaTexto(f.getFeriasInicio()));
        stmt.setString(12, dataParaTexto(f.getFeriasFim()));
    }

    private List<Funcionario> listar(String sql, int parametro) {
        List<Funcionario> lista = new ArrayList<>();
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
                lista.add(criarFuncionario(rs.getString("tipo"), rs.getInt("id"), rs.getString("nome"),
                        rs.getString("cpf"), rs.getString("telefone"), rs.getString("email"), hotel,
                        rs.getDouble("salario"), textoParaData(rs.getString("data_contratacao")),
                        rs.getString("horario_entrada"), rs.getString("horario_saida"),
                        textoParaData(rs.getString("ferias_inicio")), textoParaData(rs.getString("ferias_fim"))));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return lista;
    }

    // cria a subclasse certa de acordo com o cargo
    public static Funcionario criarFuncionario(String tipo, int id, String nome, String cpf, String telefone, String email,
            Hotel hotel, double salario, LocalDate contratacao, String entrada, String saida, LocalDate feriasInicio, LocalDate feriasFim) {
        if (tipo.equals("GERENTE")) {
            return new Gerente(id, nome, cpf, telefone, email, hotel, salario, contratacao, entrada, saida, feriasInicio, feriasFim);
        }
        if (tipo.equals("CAMAREIRA")) {
            return new Camareira(id, nome, cpf, telefone, email, hotel, salario, contratacao, entrada, saida, feriasInicio, feriasFim);
        }
        return new Recepcionista(id, nome, cpf, telefone, email, hotel, salario, contratacao, entrada, saida, feriasInicio, feriasFim);
    }

    private String dataParaTexto(LocalDate data) {
        if (data == null) {
            return null;
        }
        return data.toString();
    }

    private LocalDate textoParaData(String texto) {
        if (texto == null) {
            return null;
        }
        return LocalDate.parse(texto);
    }
}
