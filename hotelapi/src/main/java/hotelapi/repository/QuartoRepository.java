package hotelapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import hotelapi.model.Quarto;

public interface QuartoRepository extends JpaRepository<Quarto, Long> {
}
