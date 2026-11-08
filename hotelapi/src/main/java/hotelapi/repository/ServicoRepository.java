package hotelapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import hotelapi.model.Servico;

public interface ServicoRepository extends JpaRepository<Servico, Long> {
}
