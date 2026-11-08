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
import hotelapi.model.Hospede;
import hotelapi.service.HospedeService;

@RestController
@RequestMapping("/hospedes")
public class HospedeController {

    private final HospedeService service;

    public HospedeController(HospedeService service) {
        this.service = service;
    }

    @GetMapping
    public List<Hospede> listar() {
        return service.findAll();
    }

    // exemplo: GET /hospedes/pesquisa?texto=ana
    @GetMapping("/pesquisa")
    public List<Hospede> pesquisar(@RequestParam String texto) {
        return service.pesquisar(texto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> buscar(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(service.findById(id));
        } catch (NegocioException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/{id}/historico")
    public ResponseEntity<Object> historico(@PathVariable Long id) {
        return ResponseEntity.ok(service.historico(id));
    }

    @PostMapping
    public ResponseEntity<Object> salvar(@RequestBody Hospede hospede) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(service.save(hospede));
        } catch (NegocioException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> atualizar(@PathVariable Long id, @RequestBody Hospede hospede) {
        try {
            return ResponseEntity.ok(service.update(id, hospede));
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
