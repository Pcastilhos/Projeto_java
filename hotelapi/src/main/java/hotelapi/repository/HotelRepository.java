package hotelapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import hotelapi.model.Hotel;

public interface HotelRepository extends JpaRepository<Hotel, Long> {
}
