package controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import dao.QuartoDAO;
import dao.ReservaDAO;
import dao.ServicoConsumidoDAO;
import exception.NegocioException;
import model.Hospede;
import model.Hotel;
import model.Quarto;
import model.Reserva;
import model.Servico;
import model.ServicoConsumido;
import util.Conversor;

public class ReservaController {

    private ReservaDAO reservaDAO = new ReservaDAO();
    private QuartoDAO quartoDAO = new QuartoDAO();
    private ServicoConsumidoDAO servicoConsumidoDAO = new ServicoConsumidoDAO();

    // consulta de disponibilidade: quartos do hotel livres no período
    public List<Quarto> buscarQuartosDisponiveis(Hotel hotel, String entradaTexto, String saidaTexto, String quantidadeTexto) throws NegocioException {
        if (hotel == null) {
            throw new NegocioException("Selecione o hotel.");
        }
        LocalDate entrada = Conversor.paraData(entradaTexto, "Data de entrada");
        LocalDate saida = Conversor.paraData(saidaTexto, "Data de saída");
        int quantidade = Conversor.paraInteiro(quantidadeTexto, "Quantidade de hóspedes");
        validarPeriodo(entrada, saida);

        List<Quarto> disponiveis = new ArrayList<>();
        for (Quarto quarto : quartoDAO.listarPorHotel(hotel.getId())) {
            if (!quarto.getSituacao().equals(Quarto.MANUTENCAO)
                    && quarto.getCapacidade() >= quantidade
                    && !temConflito(quarto.getId(), entrada, saida, 0)) {
                disponiveis.add(quarto);
            }
        }
        return disponiveis;
    }

    public void reservar(Hospede hospede, Quarto quarto, String entradaTexto, String saidaTexto, String quantidadeTexto) throws NegocioException {
        if (hospede == null) {
            throw new NegocioException("Selecione o hóspede.");
        }
        if (quarto == null) {
            throw new NegocioException("Busque e selecione um quarto disponível.");
        }
        LocalDate entrada = Conversor.paraData(entradaTexto, "Data de entrada");
        LocalDate saida = Conversor.paraData(saidaTexto, "Data de saída");
        int quantidade = Conversor.paraInteiro(quantidadeTexto, "Quantidade de hóspedes");
        validarPeriodo(entrada, saida);

        if (quarto.getSituacao().equals(Quarto.MANUTENCAO)) {
            throw new NegocioException("O quarto está em manutenção.");
        }
        if (quantidade <= 0 || quantidade > quarto.getCapacidade()) {
            throw new NegocioException("O quarto comporta no máximo " + quarto.getCapacidade() + " hóspedes.");
        }
        if (temConflito(quarto.getId(), entrada, saida, 0)) {
            throw new NegocioException("O quarto já está reservado em parte desse período.");
        }

        Reserva reserva = new Reserva(0, hospede, quarto, entrada, saida, quantidade, Reserva.PENDENTE, 0);
        reserva.setValorTotal(reserva.calcularValorDiarias());
        reservaDAO.salvar(reserva);
    }

    // alteração da reserva: muda as datas e recalcula o valor
    public void alterarDatas(int id, String entradaTexto, String saidaTexto) throws NegocioException {
        Reserva reserva = buscar(id);
        if (!reserva.getStatus().equals(Reserva.PENDENTE) && !reserva.getStatus().equals(Reserva.CONFIRMADA)) {
            throw new NegocioException("Só é possível alterar reservas pendentes ou confirmadas.");
        }
        LocalDate entrada = Conversor.paraData(entradaTexto, "Data de entrada");
        LocalDate saida = Conversor.paraData(saidaTexto, "Data de saída");
        validarPeriodo(entrada, saida);

        if (temConflito(reserva.getQuarto().getId(), entrada, saida, reserva.getId())) {
            throw new NegocioException("O quarto já está reservado em parte desse período.");
        }
        reserva.setDataEntrada(entrada);
        reserva.setDataSaida(saida);
        reserva.setValorTotal(reserva.calcularValorDiarias());
        reservaDAO.atualizar(reserva);
    }

    public void confirmar(int id) throws NegocioException {
        Reserva reserva = buscar(id);
        if (!reserva.getStatus().equals(Reserva.PENDENTE)) {
            throw new NegocioException("Só é possível confirmar reservas pendentes.");
        }
        reserva.setStatus(Reserva.CONFIRMADA);
        reservaDAO.atualizar(reserva);
    }

    public void cancelar(int id) throws NegocioException {
        Reserva reserva = buscar(id);
        if (!reserva.getStatus().equals(Reserva.PENDENTE) && !reserva.getStatus().equals(Reserva.CONFIRMADA)) {
            throw new NegocioException("Só é possível cancelar reservas pendentes ou confirmadas.");
        }
        reserva.setStatus(Reserva.CANCELADA);
        reservaDAO.atualizar(reserva);
    }

    // check-in: reserva fica EM_ANDAMENTO e o quarto fica OCUPADO
    public void checkIn(int id) throws NegocioException {
        Reserva reserva = buscar(id);
        if (!reserva.getStatus().equals(Reserva.CONFIRMADA)) {
            throw new NegocioException("O check-in só pode ser feito em reservas confirmadas.");
        }
        if (LocalDate.now().isBefore(reserva.getDataEntrada())) {
            throw new NegocioException("O check-in só pode ser feito a partir da data de entrada.");
        }
        Quarto quarto = reserva.getQuarto();
        if (!quarto.getSituacao().equals(Quarto.DISPONIVEL)) {
            throw new NegocioException("O quarto não está disponível (situação: " + quarto.getSituacao() + ").");
        }
        reserva.setStatus(Reserva.EM_ANDAMENTO);
        quarto.setSituacao(Quarto.OCUPADO);
        reservaDAO.atualizar(reserva);
        quartoDAO.atualizar(quarto);
    }

    // serviço só pode ser lançado enquanto o hóspede está no hotel
    public void adicionarServico(int reservaId, Servico servico, String quantidadeTexto) throws NegocioException {
        Reserva reserva = buscar(reservaId);
        if (!reserva.getStatus().equals(Reserva.EM_ANDAMENTO)) {
            throw new NegocioException("Serviços só podem ser lançados após o check-in.");
        }
        if (servico == null) {
            throw new NegocioException("Selecione o serviço.");
        }
        int quantidade = Conversor.paraInteiro(quantidadeTexto, "Quantidade");
        if (quantidade <= 0) {
            throw new NegocioException("A quantidade deve ser maior que zero.");
        }
        servicoConsumidoDAO.salvar(new ServicoConsumido(0, reservaId, servico, quantidade, servico.getValor(), LocalDate.now()));
    }

    // check-out: soma diárias + serviços, finaliza a reserva e libera o quarto
    public Reserva checkOut(int id) throws NegocioException {
        Reserva reserva = buscar(id);
        if (!reserva.getStatus().equals(Reserva.EM_ANDAMENTO)) {
            throw new NegocioException("O check-out só pode ser feito em reservas com check-in.");
        }
        reserva.setValorTotal(reserva.calcularValorTotal());
        reserva.setStatus(Reserva.FINALIZADA);
        Quarto quarto = reserva.getQuarto();
        quarto.setSituacao(Quarto.DISPONIVEL);
        reservaDAO.atualizar(reserva);
        quartoDAO.atualizar(quarto);
        return reserva;
    }

    public void excluir(int id) throws NegocioException {
        Reserva reserva = buscar(id);
        if (!reserva.getStatus().equals(Reserva.PENDENTE) && !reserva.getStatus().equals(Reserva.CANCELADA)) {
            throw new NegocioException("Só é possível excluir reservas pendentes ou canceladas.");
        }
        reservaDAO.excluir(id);
    }

    public List<Reserva> listar() {
        return reservaDAO.listarTodos();
    }

    public Reserva buscar(int id) throws NegocioException {
        Reserva reserva = reservaDAO.buscarPorId(id);
        if (reserva == null) {
            throw new NegocioException("Reserva não encontrada.");
        }
        return reserva;
    }

    private void validarPeriodo(LocalDate entrada, LocalDate saida) throws NegocioException {
        if (entrada.isBefore(LocalDate.now())) {
            throw new NegocioException("A data de entrada não pode ser no passado.");
        }
        if (!saida.isAfter(entrada)) {
            throw new NegocioException("A data de saída deve ser depois da data de entrada.");
        }
    }

    // verifica se outra reserva ativa do mesmo quarto bate com o período
    private boolean temConflito(int quartoId, LocalDate entrada, LocalDate saida, int reservaIgnorada) {
        for (Reserva r : reservaDAO.listarPorQuarto(quartoId)) {
            if (r.getId() != reservaIgnorada && r.estaAtiva() && r.conflitaCom(entrada, saida)) {
                return true;
            }
        }
        return false;
    }
}
