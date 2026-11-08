package hotelapi.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import hotelapi.exception.NegocioException;
import hotelapi.model.Hotel;
import hotelapi.service.HotelService;

@RestController
@RequestMapping("/hoteis")
public class HotelController {

    private final HotelService service;

    public HotelController(HotelService service) {
        this.service = service;
    }

    @GetMapping
    public List<Hotel> listar() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> buscar(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(service.findById(id));
        } catch (NegocioException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // quartos e funcionários de uma unidade
    @GetMapping("/{id}/quartos")
    public ResponseEntity<Object> quartos(@PathVariable Long id) {
        return ResponseEntity.ok(service.listarQuartos(id));
    }

    @GetMapping("/{id}/funcionarios")
    public ResponseEntity<Object> funcionarios(@PathVariable Long id) {
        return ResponseEntity.ok(service.listarFuncionarios(id));
    }

    @PostMapping
    public ResponseEntity<Object> salvar(@RequestBody Hotel hotel) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(service.save(hotel));
        } catch (NegocioException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> atualizar(@PathVariable Long id, @RequestBody Hotel hotel) {
        try {
            return ResponseEntity.ok(service.update(id, hotel));
        } catch (NegocioException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> remover(@PathVariable Long id) {
        try {
            service.delete(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (NegocioException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
