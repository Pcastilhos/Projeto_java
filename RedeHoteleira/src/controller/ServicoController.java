package controller;

import java.util.List;

import dao.ServicoConsumidoDAO;
import dao.ServicoDAO;
import exception.NegocioException;
import model.Servico;
import util.Conversor;

public class ServicoController {

    private ServicoDAO servicoDAO = new ServicoDAO();
    private ServicoConsumidoDAO servicoConsumidoDAO = new ServicoConsumidoDAO();

    public void cadastrar(String nome, String descricao, String valor) throws NegocioException {
        servicoDAO.salvar(montarServico(0, nome, descricao, valor));
    }

    public void atualizar(int id, String nome, String descricao, String valor) throws NegocioException {
        servicoDAO.atualizar(montarServico(id, nome, descricao, valor));
    }

    public void excluir(int id) throws NegocioException {
        if (servicoConsumidoDAO.servicoFoiUsado(id)) {
            throw new NegocioException("Não é possível excluir: o serviço já foi usado em reservas.");
        }
        servicoDAO.excluir(id);
    }

    public List<Servico> listar() {
        return servicoDAO.listarTodos();
    }

    private Servico montarServico(int id, String nome, String descricao, String valorTexto) throws NegocioException {
        nome = Conversor.obrigatorio(nome, "Nome");
        double valor = Conversor.paraDouble(valorTexto, "Valor");
        if (valor <= 0) {
            throw new NegocioException("O valor do serviço deve ser maior que zero.");
        }
        return new Servico(id, nome, descricao, valor);
    }
}
