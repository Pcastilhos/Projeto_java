package controller;

import java.time.LocalDate;
import java.util.List;

import dao.HospedeDAO;
import dao.ReservaDAO;
import exception.NegocioException;
import model.Hospede;
import model.Reserva;
import util.Conversor;

public class HospedeController {

    private HospedeDAO hospedeDAO = new HospedeDAO();
    private ReservaDAO reservaDAO = new ReservaDAO();

    public void cadastrar(String nome, String cpf, String nascimento, String telefone, String email) throws NegocioException {
        Hospede hospede = montarHospede(0, nome, cpf, nascimento, telefone, email);
        hospedeDAO.salvar(hospede);
    }

    public void atualizar(int id, String nome, String cpf, String nascimento, String telefone, String email) throws NegocioException {
        Hospede hospede = montarHospede(id, nome, cpf, nascimento, telefone, email);
        hospedeDAO.atualizar(hospede);
    }

    public void excluir(int id) throws NegocioException {
        if (!reservaDAO.listarPorHospede(id).isEmpty()) {
            throw new NegocioException("Não é possível excluir: o hóspede possui reservas no histórico.");
        }
        hospedeDAO.excluir(id);
    }

    public List<Hospede> listar() {
        return hospedeDAO.listarTodos();
    }

    public List<Hospede> pesquisar(String texto) {
        return hospedeDAO.pesquisar(texto.trim());
    }

    // histórico de hospedagens do hóspede (reservas, quartos e serviços)
    public List<Reserva> historico(int hospedeId) {
        return reservaDAO.listarPorHospede(hospedeId);
    }

    private Hospede montarHospede(int id, String nome, String cpfTexto, String nascimentoTexto, String telefone, String email) throws NegocioException {
        nome = Conversor.obrigatorio(nome, "Nome");
        String cpf = Conversor.validarCpf(cpfTexto);
        LocalDate nascimento = Conversor.paraDataOpcional(nascimentoTexto, "Data de nascimento");

        if (nascimento != null && nascimento.isAfter(LocalDate.now())) {
            throw new NegocioException("A data de nascimento não pode ser no futuro.");
        }

        // CPF não pode se repetir
        Hospede existente = hospedeDAO.buscarPorCpf(cpf);
        if (existente != null && existente.getId() != id) {
            throw new NegocioException("Já existe um hóspede com este CPF.");
        }
        return new Hospede(id, nome, cpf, nascimento, telefone, email);
    }
}
