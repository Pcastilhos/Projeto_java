package hotelapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import hotelapi.model.ReservaServico;

public interface ReservaServicoRepository extends JpaRepository<ReservaServico, Long> {
}
