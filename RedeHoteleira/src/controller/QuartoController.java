package controller;

import java.util.List;

import dao.QuartoDAO;
import dao.ReservaDAO;
import exception.NegocioException;
import model.Hotel;
import model.Quarto;
import model.Reserva;
import util.Conversor;

public class QuartoController {

    private QuartoDAO quartoDAO = new QuartoDAO();
    private ReservaDAO reservaDAO = new ReservaDAO();

    public void cadastrar(Hotel hotel, String numero, String tipo, String capacidade, String valorDiaria, String situacao) throws NegocioException {
        Quarto quarto = montarQuarto(0, hotel, numero, tipo, capacidade, valorDiaria, situacao);
        quartoDAO.salvar(quarto);
    }

    public void atualizar(int id, Hotel hotel, String numero, String tipo, String capacidade, String valorDiaria, String situacao) throws NegocioException {
        Quarto quarto = montarQuarto(id, hotel, numero, tipo, capacidade, valorDiaria, situacao);

        // não deixa mudar a situação de um quarto com hóspede dentro
        for (Reserva reserva : reservaDAO.listarPorQuarto(id)) {
            if (reserva.getStatus().equals(Reserva.EM_ANDAMENTO) && !situacao.equals(Quarto.OCUPADO)) {
                throw new NegocioException("O quarto está com hóspede. A situação só muda no check-out.");
            }
        }
        quartoDAO.atualizar(quarto);
    }

    public void excluir(int id) throws NegocioException {
        if (!reservaDAO.listarPorQuarto(id).isEmpty()) {
            throw new NegocioException("Não é possível excluir: o quarto possui reservas.");
        }
        quartoDAO.excluir(id);
    }

    public List<Quarto> listar() {
        return quartoDAO.listarTodos();
    }

    private Quarto montarQuarto(int id, Hotel hotel, String numeroTexto, String tipo, String capacidadeTexto,
            String valorTexto, String situacao) throws NegocioException {
        if (hotel == null) {
            throw new NegocioException("Selecione o hotel.");
        }
        int numero = Conversor.paraInteiro(numeroTexto, "Número");
        int capacidade = Conversor.paraInteiro(capacidadeTexto, "Capacidade");
        double valorDiaria = Conversor.paraDouble(valorTexto, "Valor da diária");

        if (numero <= 0 || capacidade <= 0 || valorDiaria <= 0) {
            throw new NegocioException("Número, capacidade e valor da diária devem ser maiores que zero.");
        }

        // não pode existir dois quartos com o mesmo número no mesmo hotel
        for (Quarto q : quartoDAO.listarPorHotel(hotel.getId())) {
            if (q.getNumero() == numero && q.getId() != id) {
                throw new NegocioException("Já existe o quarto " + numero + " neste hotel.");
            }
        }
        return QuartoDAO.criarQuarto(tipo, id, hotel, numero, capacidade, valorDiaria, situacao);
    }
}
