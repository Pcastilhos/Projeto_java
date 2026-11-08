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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import hotelapi.exception.NegocioException;
import hotelapi.model.Reserva;
import hotelapi.model.ReservaServico;
import hotelapi.service.ReservaService;

@RestController
@RequestMapping("/reservas")
public class ReservaController {

    private final ReservaService service;

    public ReservaController(ReservaService service) {
        this.service = service;
    }

    @GetMapping
    public List<Reserva> listar() {
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

    // exemplo: GET /reservas/disponiveis?hotelId=1&entrada=2026-12-10&saida=2026-12-15&quantidade=2
    @GetMapping("/disponiveis")
    public ResponseEntity<Object> disponiveis(@RequestParam Long hotelId, @RequestParam String entrada,
            @RequestParam String saida, @RequestParam int quantidade) {
        try {
            return ResponseEntity.ok(service.buscarQuartosDisponiveis(hotelId, entrada, saida, quantidade));
        } catch (NegocioException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<Object> reservar(@RequestBody Reserva reserva) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(service.save(reserva));
        } catch (NegocioException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // altera as datas da reserva
    @PutMapping("/{id}")
    public ResponseEntity<Object> alterarDatas(@PathVariable Long id, @RequestBody Reserva reserva) {
        try {
            return ResponseEntity.ok(service.update(id, reserva));
        } catch (NegocioException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping("/{id}/confirmar")
    public ResponseEntity<Object> confirmar(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(service.confirmar(id));
        } catch (NegocioException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping("/{id}/checkin")
    public ResponseEntity<Object> checkIn(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(service.checkIn(id));
        } catch (NegocioException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // devolve o resumo com os serviços usados e o total
    @PutMapping("/{id}/checkout")
    public ResponseEntity<Object> checkOut(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(service.checkOut(id));
        } catch (NegocioException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // resumo para conferir os serviços antes do check-out (não finaliza nada)
    @GetMapping("/{id}/resumo")
    public ResponseEntity<Object> resumo(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(service.resumoCheckOut(id));
        } catch (NegocioException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping("/{id}/cancelar")
    public ResponseEntity<Object> cancelar(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(service.cancelar(id));
        } catch (NegocioException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/{id}/servicos")
    public ResponseEntity<Object> servicos(@PathVariable Long id) {
        return ResponseEntity.ok(service.listarServicos(id));
    }

    // corpo: { "servicoId": 1, "quantidade": 2 }
    @PostMapping("/{id}/servicos")
    public ResponseEntity<Object> adicionarServico(@PathVariable Long id, @RequestBody ReservaServico dados) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(service.adicionarServico(id, dados));
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
