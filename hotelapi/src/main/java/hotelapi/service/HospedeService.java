package hotelapi.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import hotelapi.exception.NegocioException;
import hotelapi.model.Hospede;
import hotelapi.model.Reserva;
import hotelapi.repository.HospedeRepository;
import hotelapi.repository.ReservaRepository;
import hotelapi.util.Validacao;

@Service
public class HospedeService {

    private final HospedeRepository hospedeRepository;
    private final ReservaRepository reservaRepository;

    public HospedeService(HospedeRepository hospedeRepository, ReservaRepository reservaRepository) {
        this.hospedeRepository = hospedeRepository;
        this.reservaRepository = reservaRepository;
    }

    public List<Hospede> findAll() {
        return hospedeRepository.findAll();
    }

    public Hospede findById(Long id) {
        Hospede hospede = hospedeRepository.findById(id).orElse(null);
        if (hospede == null) {
            throw new NegocioException("Hóspede não encontrado.");
        }
        return hospede;
    }

    // pesquisa por parte do nome ou do CPF
    public List<Hospede> pesquisar(String texto) {
        List<Hospede> lista = new ArrayList<>();
        for (Hospede h : hospedeRepository.findAll()) {
            if (h.getNome().toLowerCase().contains(texto.toLowerCase()) || h.getCpf().contains(texto)) {
                lista.add(h);
            }
        }
        return lista;
    }

    public Hospede save(Hospede hospede) {
        validar(hospede, null);
        return hospedeRepository.save(hospede);
    }

    public Hospede update(Long id, Hospede hospede) {
        findById(id);
        validar(hospede, id);
        hospede.setId(id);
        return hospedeRepository.save(hospede);
    }

    public void delete(Long id) {
        findById(id);
        if (!historico(id).isEmpty()) {
            throw new NegocioException("Não é possível excluir: o hóspede possui reservas no histórico.");
        }
        hospedeRepository.deleteById(id);
    }

    // histórico de hospedagens do hóspede
    public List<Reserva> historico(Long hospedeId) {
        List<Reserva> lista = new ArrayList<>();
        for (Reserva r : reservaRepository.findAll()) {
            if (r.getHospedeId().equals(hospedeId)) {
                lista.add(r);
            }
        }
        return lista;
    }

    private void validar(Hospede hospede, Long id) {
        Validacao.obrigatorio(hospede.getNome(), "nome");
        hospede.setCpf(Validacao.validarCpf(hospede.getCpf()));
        if (hospede.getDataNascimento() != null && hospede.getDataNascimento().isAfter(LocalDate.now())) {
            throw new NegocioException("A data de nascimento não pode ser no futuro.");
        }
        // CPF não pode se repetir
        for (Hospede h : hospedeRepository.findAll()) {
            if (h.getCpf().equals(hospede.getCpf()) && !h.getId().equals(id)) {
                throw new NegocioException("Já existe um hóspede com este CPF.");
            }
        }
    }
}
