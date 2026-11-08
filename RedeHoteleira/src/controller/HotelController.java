package controller;

import java.util.List;

import dao.FuncionarioDAO;
import dao.HotelDAO;
import dao.QuartoDAO;
import exception.NegocioException;
import model.Funcionario;
import model.Hotel;
import model.Quarto;
import util.Conversor;

public class HotelController {

    private HotelDAO hotelDAO = new HotelDAO();
    private QuartoDAO quartoDAO = new QuartoDAO();
    private FuncionarioDAO funcionarioDAO = new FuncionarioDAO();

    public void cadastrar(String nome, String endereco, String cidade, String telefone, int estrelas) throws NegocioException {
        Hotel hotel = montarHotel(0, nome, endereco, cidade, telefone, estrelas);
        hotelDAO.salvar(hotel);
    }

    public void atualizar(int id, String nome, String endereco, String cidade, String telefone, int estrelas) throws NegocioException {
        Hotel hotel = montarHotel(id, nome, endereco, cidade, telefone, estrelas);
        hotelDAO.atualizar(hotel);
    }

    public void excluir(int id) throws NegocioException {
        if (!quartoDAO.listarPorHotel(id).isEmpty()) {
            throw new NegocioException("Não é possível excluir: o hotel possui quartos cadastrados.");
        }
        if (!funcionarioDAO.listarPorHotel(id).isEmpty()) {
            throw new NegocioException("Não é possível excluir: o hotel possui funcionários cadastrados.");
        }
        hotelDAO.excluir(id);
    }

    public List<Hotel> listar() {
        return hotelDAO.listarTodos();
    }

    public List<Quarto> listarQuartos(int hotelId) {
        return quartoDAO.listarPorHotel(hotelId);
    }

    public List<Funcionario> listarFuncionarios(int hotelId) {
        return funcionarioDAO.listarPorHotel(hotelId);
    }

    // valida os campos e monta o objeto
    private Hotel montarHotel(int id, String nome, String endereco, String cidade, String telefone, int estrelas) throws NegocioException {
        nome = Conversor.obrigatorio(nome, "Nome");
        endereco = Conversor.obrigatorio(endereco, "Endereço");
        cidade = Conversor.obrigatorio(cidade, "Cidade");
        if (estrelas < 1 || estrelas > 5) {
            throw new NegocioException("A classificação deve ser de 1 a 5 estrelas.");
        }
        return new Hotel(id, nome, endereco, cidade, telefone, estrelas);
    }
}
