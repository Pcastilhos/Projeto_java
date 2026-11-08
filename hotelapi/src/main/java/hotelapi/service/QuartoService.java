package hotelapi.service;

import java.util.List;

import org.springframework.stereotype.Service;

import hotelapi.exception.NegocioException;
import hotelapi.model.Quarto;
import hotelapi.model.Reserva;
import hotelapi.repository.HotelRepository;
import hotelapi.repository.QuartoRepository;
import hotelapi.repository.ReservaRepository;

@Service
public class QuartoService {

    private final QuartoRepository quartoRepository;
    private final HotelRepository hotelRepository;
    private final ReservaRepository reservaRepository;

    public QuartoService(QuartoRepository quartoRepository, HotelRepository hotelRepository, ReservaRepository reservaRepository) {
        this.quartoRepository = quartoRepository;
        this.hotelRepository = hotelRepository;
        this.reservaRepository = reservaRepository;
    }

    public List<Quarto> findAll() {
        return quartoRepository.findAll();
    }

    public Quarto findById(Long id) {
        Quarto quarto = quartoRepository.findById(id).orElse(null);
        if (quarto == null) {
            throw new NegocioException("Quarto não encontrado.");
        }
        return quarto;
    }

    public Quarto save(Quarto quarto) {
        validar(quarto, null);
        return quartoRepository.save(quarto);
    }

    public Quarto update(Long id, Quarto quarto) {
        findById(id);
        validar(quarto, id);
        // não deixa mudar a situação de um quarto com hóspede dentro
        for (Reserva r : reservaRepository.findAll()) {
            if (r.getQuartoId().equals(id) && r.getStatus().equals(Reserva.EM_ANDAMENTO) && !quarto.getSituacao().equals("OCUPADO")) {
                throw new NegocioException("O quarto está com hóspede. A situação só muda no check-out.");
            }
        }
        quarto.setId(id);
        return quartoRepository.save(quarto);
    }

    public void delete(Long id) {
        findById(id);
        for (Reserva r : reservaRepository.findAll()) {
            if (r.getQuartoId().equals(id)) {
                throw new NegocioException("Não é possível excluir: o quarto possui reservas.");
            }
        }
        quartoRepository.deleteById(id);
    }

    private void validar(Quarto quarto, Long id) {
        if (quarto.getSituacao() == null) {
            quarto.setSituacao("DISPONIVEL");
        }
        if (quarto.getHotelId() == null || !hotelRepository.existsById(quarto.getHotelId())) {
            throw new NegocioException("Hotel não encontrado.");
        }
        if (!"STANDARD".equals(quarto.getTipo()) && !"LUXO".equals(quarto.getTipo()) && !"SUITE".equals(quarto.getTipo())) {
            throw new NegocioException("O tipo deve ser STANDARD, LUXO ou SUITE.");
        }
        if (!quarto.getSituacao().equals("DISPONIVEL") && !quarto.getSituacao().equals("OCUPADO") && !quarto.getSituacao().equals("MANUTENCAO")) {
            throw new NegocioException("A situação deve ser DISPONIVEL, OCUPADO ou MANUTENCAO.");
        }
        if (quarto.getNumero() <= 0 || quarto.getCapacidade() <= 0 || quarto.getValorDiaria() <= 0) {
            throw new NegocioException("Número, capacidade e valor da diária devem ser maiores que zero.");
        }
        // não pode existir dois quartos com o mesmo número no mesmo hotel
        for (Quarto q : quartoRepository.findAll()) {
            if (q.getHotelId().equals(quarto.getHotelId()) && q.getNumero() == quarto.getNumero() && !q.getId().equals(id)) {
                throw new NegocioException("Já existe o quarto " + quarto.getNumero() + " neste hotel.");
            }
        }
    }
}
