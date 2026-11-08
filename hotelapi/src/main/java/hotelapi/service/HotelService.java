package hotelapi.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import hotelapi.exception.NegocioException;
import hotelapi.model.Funcionario;
import hotelapi.model.Hotel;
import hotelapi.model.Quarto;
import hotelapi.repository.FuncionarioRepository;
import hotelapi.repository.HotelRepository;
import hotelapi.repository.QuartoRepository;
import hotelapi.util.Validacao;

@Service
public class HotelService {

    private final HotelRepository hotelRepository;
    private final QuartoRepository quartoRepository;
    private final FuncionarioRepository funcionarioRepository;

    // injeção de dependência: o Spring passa os repositories pelo construtor
    public HotelService(HotelRepository hotelRepository, QuartoRepository quartoRepository, FuncionarioRepository funcionarioRepository) {
        this.hotelRepository = hotelRepository;
        this.quartoRepository = quartoRepository;
        this.funcionarioRepository = funcionarioRepository;
    }

    public List<Hotel> findAll() {
        return hotelRepository.findAll();
    }

    public Hotel findById(Long id) {
        Hotel hotel = hotelRepository.findById(id).orElse(null);
        if (hotel == null) {
            throw new NegocioException("Hotel não encontrado.");
        }
        return hotel;
    }

    public Hotel save(Hotel hotel) {
        validar(hotel);
        return hotelRepository.save(hotel);
    }

    public Hotel update(Long id, Hotel hotel) {
        findById(id);
        validar(hotel);
        hotel.setId(id);
        return hotelRepository.save(hotel);
    }

    public void delete(Long id) {
        findById(id);
        if (!listarQuartos(id).isEmpty()) {
            throw new NegocioException("Não é possível excluir: o hotel possui quartos cadastrados.");
        }
        if (!listarFuncionarios(id).isEmpty()) {
            throw new NegocioException("Não é possível excluir: o hotel possui funcionários cadastrados.");
        }
        hotelRepository.deleteById(id);
    }

    public List<Quarto> listarQuartos(Long hotelId) {
        List<Quarto> lista = new ArrayList<>();
        for (Quarto q : quartoRepository.findAll()) {
            if (q.getHotelId().equals(hotelId)) {
                lista.add(q);
            }
        }
        return lista;
    }

    public List<Funcionario> listarFuncionarios(Long hotelId) {
        List<Funcionario> lista = new ArrayList<>();
        for (Funcionario f : funcionarioRepository.findAll()) {
            if (f.getHotelId().equals(hotelId)) {
                lista.add(f);
            }
        }
        return lista;
    }

    private void validar(Hotel hotel) {
        Validacao.obrigatorio(hotel.getNome(), "nome");
        Validacao.obrigatorio(hotel.getEndereco(), "endereco");
        Validacao.obrigatorio(hotel.getCidade(), "cidade");
        if (hotel.getEstrelas() < 1 || hotel.getEstrelas() > 5) {
            throw new NegocioException("A classificação deve ser de 1 a 5 estrelas.");
        }
    }
}
