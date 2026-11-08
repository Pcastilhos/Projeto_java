package hotelapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import hotelapi.model.Funcionario;

public interface FuncionarioRepository extends JpaRepository<Funcionario, Long> {
}
