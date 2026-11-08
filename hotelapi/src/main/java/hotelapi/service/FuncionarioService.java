package hotelapi.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import hotelapi.exception.NegocioException;
import hotelapi.model.Funcionario;
import hotelapi.repository.FuncionarioRepository;
import hotelapi.repository.HotelRepository;
import hotelapi.util.Validacao;

@Service
public class FuncionarioService {

    private final FuncionarioRepository funcionarioRepository;
    private final HotelRepository hotelRepository;

    public FuncionarioService(FuncionarioRepository funcionarioRepository, HotelRepository hotelRepository) {
        this.funcionarioRepository = funcionarioRepository;
        this.hotelRepository = hotelRepository;
    }

    public List<Funcionario> findAll() {
        return funcionarioRepository.findAll();
    }

    public Funcionario findById(Long id) {
        Funcionario f = funcionarioRepository.findById(id).orElse(null);
        if (f == null) {
            throw new NegocioException("Funcionário não encontrado.");
        }
        return f;
    }

    public Funcionario save(Funcionario f) {
        validar(f, null);
        return funcionarioRepository.save(f);
    }

    public Funcionario update(Long id, Funcionario f) {
        findById(id);
        validar(f, id);
        f.setId(id);
        return funcionarioRepository.save(f);
    }

    public void delete(Long id) {
        findById(id);
        funcionarioRepository.deleteById(id);
    }

    private void validar(Funcionario f, Long id) {
        if (f.getHotelId() == null || !hotelRepository.existsById(f.getHotelId())) {
            throw new NegocioException("Hotel não encontrado.");
        }
        if (!"RECEPCIONISTA".equals(f.getTipo()) && !"GERENTE".equals(f.getTipo()) && !"CAMAREIRA".equals(f.getTipo())) {
            throw new NegocioException("O tipo deve ser RECEPCIONISTA, GERENTE ou CAMAREIRA.");
        }
        Validacao.obrigatorio(f.getNome(), "nome");
        f.setCpf(Validacao.validarCpf(f.getCpf()));
        Validacao.obrigatorio(f.getDataContratacao(), "dataContratacao");
        Validacao.obrigatorio(f.getHorarioEntrada(), "horarioEntrada");
        Validacao.obrigatorio(f.getHorarioSaida(), "horarioSaida");

        if (f.getSalario() <= 0) {
            throw new NegocioException("O salário deve ser maior que zero.");
        }
        if (f.getDataContratacao().isAfter(LocalDate.now())) {
            throw new NegocioException("A data de contratação não pode ser no futuro.");
        }
        // férias: as duas datas juntas, o fim depois do início e depois da contratação
        if ((f.getFeriasInicio() == null) != (f.getFeriasFim() == null)) {
            throw new NegocioException("Informe o início e o fim das férias, ou deixe os dois em branco.");
        }
        if (f.getFeriasInicio() != null) {
            if (f.getFeriasFim().isBefore(f.getFeriasInicio())) {
                throw new NegocioException("O fim das férias deve ser depois do início.");
            }
            if (f.getFeriasInicio().isBefore(f.getDataContratacao())) {
                throw new NegocioException("As férias não podem começar antes da contratação.");
            }
        }
        for (Funcionario outro : funcionarioRepository.findAll()) {
            if (outro.getCpf().equals(f.getCpf()) && !outro.getId().equals(id)) {
                throw new NegocioException("Já existe um funcionário com este CPF.");
            }
        }
    }
}
