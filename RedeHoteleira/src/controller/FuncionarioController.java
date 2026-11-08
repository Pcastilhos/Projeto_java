package controller;

import java.time.LocalDate;
import java.util.List;

import dao.FuncionarioDAO;
import exception.NegocioException;
import model.Funcionario;
import model.Hotel;
import util.Conversor;

public class FuncionarioController {

    private FuncionarioDAO funcionarioDAO = new FuncionarioDAO();

    public void cadastrar(Hotel hotel, String cargo, String nome, String cpf, String telefone, String email, String salario,
            String contratacao, String entrada, String saida, String feriasInicio, String feriasFim) throws NegocioException {
        Funcionario f = montarFuncionario(0, hotel, cargo, nome, cpf, telefone, email, salario, contratacao, entrada, saida, feriasInicio, feriasFim);
        funcionarioDAO.salvar(f);
    }

    public void atualizar(int id, Hotel hotel, String cargo, String nome, String cpf, String telefone, String email, String salario,
            String contratacao, String entrada, String saida, String feriasInicio, String feriasFim) throws NegocioException {
        Funcionario f = montarFuncionario(id, hotel, cargo, nome, cpf, telefone, email, salario, contratacao, entrada, saida, feriasInicio, feriasFim);
        funcionarioDAO.atualizar(f);
    }

    public void excluir(int id) {
        funcionarioDAO.excluir(id);
    }

    public List<Funcionario> listar() {
        return funcionarioDAO.listarTodos();
    }

    private Funcionario montarFuncionario(int id, Hotel hotel, String cargo, String nome, String cpfTexto, String telefone,
            String email, String salarioTexto, String contratacaoTexto, String entrada, String saida,
            String feriasInicioTexto, String feriasFimTexto) throws NegocioException {
        if (hotel == null) {
            throw new NegocioException("Selecione o hotel.");
        }
        nome = Conversor.obrigatorio(nome, "Nome");
        String cpf = Conversor.validarCpf(cpfTexto);
        double salario = Conversor.paraDouble(salarioTexto, "Salário");
        LocalDate contratacao = Conversor.paraData(contratacaoTexto, "Data de contratação");
        entrada = Conversor.obrigatorio(entrada, "Horário de entrada");
        saida = Conversor.obrigatorio(saida, "Horário de saída");
        LocalDate feriasInicio = Conversor.paraDataOpcional(feriasInicioTexto, "Início das férias");
        LocalDate feriasFim = Conversor.paraDataOpcional(feriasFimTexto, "Fim das férias");

        if (salario <= 0) {
            throw new NegocioException("O salário deve ser maior que zero.");
        }
        if (contratacao.isAfter(LocalDate.now())) {
            throw new NegocioException("A data de contratação não pode ser no futuro.");
        }

        // férias: as duas datas juntas, o fim depois do início e depois da contratação
        if ((feriasInicio == null) != (feriasFim == null)) {
            throw new NegocioException("Informe o início e o fim das férias, ou deixe os dois em branco.");
        }
        if (feriasInicio != null) {
            if (feriasFim.isBefore(feriasInicio)) {
                throw new NegocioException("O fim das férias deve ser depois do início.");
            }
            if (feriasInicio.isBefore(contratacao)) {
                throw new NegocioException("As férias não podem começar antes da contratação.");
            }
        }

        Funcionario existente = funcionarioDAO.buscarPorCpf(cpf);
        if (existente != null && existente.getId() != id) {
            throw new NegocioException("Já existe um funcionário com este CPF.");
        }
        return FuncionarioDAO.criarFuncionario(cargo, id, nome, cpf, telefone, email, hotel, salario, contratacao,
                entrada, saida, feriasInicio, feriasFim);
    }
}
