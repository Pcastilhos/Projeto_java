package hotelapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import hotelapi.model.Hospede;

public interface HospedeRepository extends JpaRepository<Hospede, Long> {
}
