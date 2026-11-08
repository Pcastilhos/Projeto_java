package hotelapi.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import hotelapi.exception.NegocioException;
import hotelapi.model.Quarto;
import hotelapi.model.Reserva;
import hotelapi.model.ReservaServico;
import hotelapi.model.Servico;
import hotelapi.repository.HospedeRepository;
import hotelapi.repository.QuartoRepository;
import hotelapi.repository.ReservaRepository;
import hotelapi.repository.ReservaServicoRepository;
import hotelapi.repository.ServicoRepository;
import hotelapi.util.Validacao;

@Service
public class ReservaService {

    private final ReservaRepository reservaRepository;
    private final QuartoRepository quartoRepository;
    private final HospedeRepository hospedeRepository;
    private final ServicoRepository servicoRepository;
    private final ReservaServicoRepository reservaServicoRepository;

    public ReservaService(ReservaRepository reservaRepository, QuartoRepository quartoRepository, HospedeRepository hospedeRepository,
            ServicoRepository servicoRepository, ReservaServicoRepository reservaServicoRepository) {
        this.reservaRepository = reservaRepository;
        this.quartoRepository = quartoRepository;
        this.hospedeRepository = hospedeRepository;
        this.servicoRepository = servicoRepository;
        this.reservaServicoRepository = reservaServicoRepository;
    }

    public List<Reserva> findAll() {
        return reservaRepository.findAll();
    }

    public Reserva findById(Long id) {
        Reserva reserva = reservaRepository.findById(id).orElse(null);
        if (reserva == null) {
            throw new NegocioException("Reserva não encontrada.");
        }
        return reserva;
    }

    // consulta de disponibilidade: quartos do hotel livres no período
    public List<Quarto> buscarQuartosDisponiveis(Long hotelId, String entradaTexto, String saidaTexto, int quantidade) {
        LocalDate entrada = Validacao.paraData(entradaTexto, "entrada");
        LocalDate saida = Validacao.paraData(saidaTexto, "saida");
        validarPeriodo(entrada, saida);

        List<Quarto> disponiveis = new ArrayList<>();
        for (Quarto q : quartoRepository.findAll()) {
            if (q.getHotelId().equals(hotelId)
                    && !q.getSituacao().equals("MANUTENCAO")
                    && q.getCapacidade() >= quantidade
                    && !temConflito(q.getId(), entrada, saida, null)) {
                disponiveis.add(q);
            }
        }
        return disponiveis;
    }

    public Reserva save(Reserva reserva) {
        if (reserva.getHospedeId() == null || !hospedeRepository.existsById(reserva.getHospedeId())) {
            throw new NegocioException("Hóspede não encontrado.");
        }
        Quarto quarto = buscarQuarto(reserva.getQuartoId());
        Validacao.obrigatorio(reserva.getDataEntrada(), "dataEntrada");
        Validacao.obrigatorio(reserva.getDataSaida(), "dataSaida");
        validarPeriodo(reserva.getDataEntrada(), reserva.getDataSaida());

        if (quarto.getSituacao().equals("MANUTENCAO")) {
            throw new NegocioException("O quarto está em manutenção.");
        }
        if (reserva.getQuantidadeHospedes() <= 0 || reserva.getQuantidadeHospedes() > quarto.getCapacidade()) {
            throw new NegocioException("O quarto comporta no máximo " + quarto.getCapacidade() + " hóspedes.");
        }
        if (temConflito(quarto.getId(), reserva.getDataEntrada(), reserva.getDataSaida(), null)) {
            throw new NegocioException("O quarto já está reservado em parte desse período.");
        }

        reserva.setStatus(Reserva.PENDENTE);
        reserva.setValorTotal(quarto.calcularValorHospedagem(reserva.calcularDiarias()));
        return reservaRepository.save(reserva);
    }

    // alteração da reserva: muda as datas e recalcula o valor
    public Reserva update(Long id, Reserva dados) {
        Reserva reserva = findById(id);
        if (!reserva.getStatus().equals(Reserva.PENDENTE) && !reserva.getStatus().equals(Reserva.CONFIRMADA)) {
            throw new NegocioException("Só é possível alterar reservas pendentes ou confirmadas.");
        }
        Validacao.obrigatorio(dados.getDataEntrada(), "dataEntrada");
        Validacao.obrigatorio(dados.getDataSaida(), "dataSaida");
        validarPeriodo(dados.getDataEntrada(), dados.getDataSaida());

        if (temConflito(reserva.getQuartoId(), dados.getDataEntrada(), dados.getDataSaida(), reserva.getId())) {
            throw new NegocioException("O quarto já está reservado em parte desse período.");
        }
        reserva.setDataEntrada(dados.getDataEntrada());
        reserva.setDataSaida(dados.getDataSaida());
        Quarto quarto = buscarQuarto(reserva.getQuartoId());
        reserva.setValorTotal(quarto.calcularValorHospedagem(reserva.calcularDiarias()));
        return reservaRepository.save(reserva);
    }

    public Reserva confirmar(Long id) {
        Reserva reserva = findById(id);
        if (!reserva.getStatus().equals(Reserva.PENDENTE)) {
            throw new NegocioException("Só é possível confirmar reservas pendentes.");
        }
        reserva.setStatus(Reserva.CONFIRMADA);
        return reservaRepository.save(reserva);
    }

    public Reserva cancelar(Long id) {
        Reserva reserva = findById(id);
        if (!reserva.getStatus().equals(Reserva.PENDENTE) && !reserva.getStatus().equals(Reserva.CONFIRMADA)) {
            throw new NegocioException("Só é possível cancelar reservas pendentes ou confirmadas.");
        }
        reserva.setStatus(Reserva.CANCELADA);
        return reservaRepository.save(reserva);
    }

    // check-in: reserva fica EM_ANDAMENTO e o quarto fica OCUPADO
    public Reserva checkIn(Long id) {
        Reserva reserva = findById(id);
        if (!reserva.getStatus().equals(Reserva.CONFIRMADA)) {
            throw new NegocioException("O check-in só pode ser feito em reservas confirmadas.");
        }
        if (LocalDate.now().isBefore(reserva.getDataEntrada())) {
            throw new NegocioException("O check-in só pode ser feito a partir da data de entrada.");
        }
        Quarto quarto = buscarQuarto(reserva.getQuartoId());
        if (!quarto.getSituacao().equals("DISPONIVEL")) {
            throw new NegocioException("O quarto não está disponível (situação: " + quarto.getSituacao() + ").");
        }
        reserva.setStatus(Reserva.EM_ANDAMENTO);
        quarto.setSituacao("OCUPADO");
        quartoRepository.save(quarto);
        return reservaRepository.save(reserva);
    }

    // serviço só pode ser lançado enquanto o hóspede está no hotel
    public ReservaServico adicionarServico(Long reservaId, ReservaServico dados) {
        Reserva reserva = findById(reservaId);
        if (!reserva.getStatus().equals(Reserva.EM_ANDAMENTO)) {
            throw new NegocioException("Serviços só podem ser lançados após o check-in.");
        }
        Servico servico = null;
        if (dados.getServicoId() != null) {
            servico = servicoRepository.findById(dados.getServicoId()).orElse(null);
        }
        if (servico == null) {
            throw new NegocioException("Serviço não encontrado.");
        }
        if (dados.getQuantidade() <= 0) {
            throw new NegocioException("A quantidade deve ser maior que zero.");
        }
        dados.setReservaId(reservaId);
        dados.setValorUnitario(servico.getValor());
        dados.setData(LocalDate.now());
        return reservaServicoRepository.save(dados);
    }

    public List<ReservaServico> listarServicos(Long reservaId) {
        List<ReservaServico> lista = new ArrayList<>();
        for (ReservaServico rs : reservaServicoRepository.findAll()) {
            if (rs.getReservaId().equals(reservaId)) {
                lista.add(rs);
            }
        }
        return lista;
    }

    // resumo para conferência antes do check-out: diárias, serviços usados e total
    public String resumoCheckOut(Long id) {
        Reserva reserva = findById(id);
        if (!reserva.getStatus().equals(Reserva.EM_ANDAMENTO)) {
            throw new NegocioException("O check-out só pode ser feito em reservas com check-in.");
        }
        Quarto quarto = buscarQuarto(reserva.getQuartoId());
        double valorDiarias = quarto.calcularValorHospedagem(reserva.calcularDiarias());

        String resumo = "Diárias: " + reserva.calcularDiarias() + " = R$ " + String.format("%.2f", valorDiarias) + "\n";
        resumo += "Serviços utilizados:\n";
        double valorServicos = 0;
        for (ReservaServico rs : listarServicos(id)) {
            Servico servico = servicoRepository.findById(rs.getServicoId()).orElse(null);
            String nome = "Serviço " + rs.getServicoId();
            if (servico != null) {
                nome = servico.getNome();
            }
            resumo += "- " + nome + " x" + rs.getQuantidade() + " = R$ " + String.format("%.2f", rs.calcularSubtotal()) + "\n";
            valorServicos += rs.calcularSubtotal();
        }
        if (valorServicos == 0) {
            resumo += "Nenhum serviço utilizado.\n";
        }
        double total = valorDiarias + valorServicos;
        resumo += "TOTAL: R$ " + String.format("%.2f", total);
        return resumo;
    }

    // check-out: confere os serviços usados, soma tudo, finaliza a reserva e libera o quarto
    public String checkOut(Long id) {
        String resumo = resumoCheckOut(id);
        Reserva reserva = findById(id);
        Quarto quarto = buscarQuarto(reserva.getQuartoId());

        double total = quarto.calcularValorHospedagem(reserva.calcularDiarias());
        for (ReservaServico rs : listarServicos(id)) {
            total += rs.calcularSubtotal();
        }
        reserva.setValorTotal(total);
        reserva.setStatus(Reserva.FINALIZADA);
        quarto.setSituacao("DISPONIVEL");
        quartoRepository.save(quarto);
        reservaRepository.save(reserva);
        return resumo;
    }

    public void delete(Long id) {
        Reserva reserva = findById(id);
        if (!reserva.getStatus().equals(Reserva.PENDENTE) && !reserva.getStatus().equals(Reserva.CANCELADA)) {
            throw new NegocioException("Só é possível excluir reservas pendentes ou canceladas.");
        }
        for (ReservaServico rs : listarServicos(id)) {
            reservaServicoRepository.deleteById(rs.getId());
        }
        reservaRepository.deleteById(id);
    }

    private Quarto buscarQuarto(Long quartoId) {
        Quarto quarto = null;
        if (quartoId != null) {
            quarto = quartoRepository.findById(quartoId).orElse(null);
        }
        if (quarto == null) {
            throw new NegocioException("Quarto não encontrado.");
        }
        return quarto;
    }

    private void validarPeriodo(LocalDate entrada, LocalDate saida) {
        if (entrada.isBefore(LocalDate.now())) {
            throw new NegocioException("A data de entrada não pode ser no passado.");
        }
        if (!saida.isAfter(entrada)) {
            throw new NegocioException("A data de saída deve ser depois da data de entrada.");
        }
    }

    // verifica se outra reserva ativa do mesmo quarto bate com o período
    private boolean temConflito(Long quartoId, LocalDate entrada, LocalDate saida, Long reservaIgnorada) {
        for (Reserva r : reservaRepository.findAll()) {
            if (r.getQuartoId().equals(quartoId) && !r.getId().equals(reservaIgnorada)
                    && r.estaAtiva() && r.conflitaCom(entrada, saida)) {
                return true;
            }
        }
        return false;
    }
}
