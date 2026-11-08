package hotelapi.service;

import java.util.List;

import org.springframework.stereotype.Service;

import hotelapi.exception.NegocioException;
import hotelapi.model.ReservaServico;
import hotelapi.model.Servico;
import hotelapi.repository.ReservaServicoRepository;
import hotelapi.repository.ServicoRepository;
import hotelapi.util.Validacao;

@Service
public class ServicoService {

    private final ServicoRepository servicoRepository;
    private final ReservaServicoRepository reservaServicoRepository;

    public ServicoService(ServicoRepository servicoRepository, ReservaServicoRepository reservaServicoRepository) {
        this.servicoRepository = servicoRepository;
        this.reservaServicoRepository = reservaServicoRepository;
    }

    public List<Servico> findAll() {
        return servicoRepository.findAll();
    }

    public Servico findById(Long id) {
        Servico servico = servicoRepository.findById(id).orElse(null);
        if (servico == null) {
            throw new NegocioException("Serviço não encontrado.");
        }
        return servico;
    }

    public Servico save(Servico servico) {
        validar(servico);
        return servicoRepository.save(servico);
    }

    public Servico update(Long id, Servico servico) {
        findById(id);
        validar(servico);
        servico.setId(id);
        return servicoRepository.save(servico);
    }

    public void delete(Long id) {
        findById(id);
        for (ReservaServico rs : reservaServicoRepository.findAll()) {
            if (rs.getServicoId().equals(id)) {
                throw new NegocioException("Não é possível excluir: o serviço já foi usado em reservas.");
            }
        }
        servicoRepository.deleteById(id);
    }

    private void validar(Servico servico) {
        Validacao.obrigatorio(servico.getNome(), "nome");
        if (servico.getValor() <= 0) {
            throw new NegocioException("O valor do serviço deve ser maior que zero.");
        }
    }
}
