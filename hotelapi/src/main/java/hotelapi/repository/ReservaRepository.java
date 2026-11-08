package hotelapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import hotelapi.model.Reserva;

public interface ReservaRepository extends JpaRepository<Reserva, Long> {
}
