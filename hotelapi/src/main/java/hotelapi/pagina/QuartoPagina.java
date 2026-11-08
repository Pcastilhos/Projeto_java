package hotelapi.pagina;

import java.io.IOException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import hotelapi.exception.NegocioException;
import hotelapi.model.Hotel;
import hotelapi.model.Quarto;
import hotelapi.service.HotelService;
import hotelapi.service.QuartoService;

@RestController
public class QuartoPagina {

    private final QuartoService service;
    private final HotelService hotelService;

    public QuartoPagina(QuartoService service, HotelService hotelService) {
        this.service = service;
        this.hotelService = hotelService;
    }

    @GetMapping(value = "/tela/quartos", produces = "text/html;charset=UTF-8")
    public String pagina(@RequestParam(defaultValue = "") String msg, @RequestParam(defaultValue = "") String erro,
            @RequestParam(defaultValue = "0") Long editar) throws IOException {

        String html = Pagina.ler("quartos.html");

        Quarto quarto = new Quarto();
        String titulo = "Novo quarto";
        if (editar > 0) {
            quarto = service.findById(editar);
            titulo = "Editando quarto " + quarto.getNumero();
        }

        String hoteis = "";
        for (Hotel h : hotelService.findAll()) {
            hoteis += Pagina.opcao(h.getId(), h.getNome(), quarto.getHotelId());
        }
        String tipos = Pagina.opcao("STANDARD", "STANDARD", quarto.getTipo())
                + Pagina.opcao("LUXO", "LUXO", quarto.getTipo())
                + Pagina.opcao("SUITE", "SUITE", quarto.getTipo());
        String situacoes = Pagina.opcao("DISPONIVEL", "DISPONIVEL", quarto.getSituacao())
                + Pagina.opcao("OCUPADO", "OCUPADO", quarto.getSituacao())
                + Pagina.opcao("MANUTENCAO", "MANUTENCAO", quarto.getSituacao());

        String lista = "<table><tr><th>ID</th><th>Hotel</th><th>Número</th><th>Tipo</th><th>Capacidade</th>"
                + "<th>Diária base</th><th>Diária final</th><th>Situação</th><th>Ações</th></tr>";
        for (Quarto q : service.findAll()) {
            lista += "<tr>";
            lista += "<td>" + q.getId() + "</td>";
            lista += "<td>" + Pagina.texto(hotelService.findById(q.getHotelId()).getNome()) + "</td>";
            lista += "<td>" + q.getNumero() + "</td>";
            lista += "<td>" + q.getTipo() + "</td>";
            lista += "<td>" + q.getCapacidade() + "</td>";
            lista += "<td>" + Pagina.dinheiro(q.getValorDiaria()) + "</td>";
            lista += "<td>" + Pagina.dinheiro(q.getDiariaFinal()) + "</td>";
            lista += "<td>" + q.getSituacao() + "</td>";
            lista += "<td><a href='/tela/quartos?editar=" + q.getId() + "'>Editar</a>";
            lista += "<form class='inline' method='post' action='/tela/quartos/" + q.getId() + "/excluir'>"
                    + "<button class='perigo'>Excluir</button></form></td>";
            lista += "</tr>";
        }
        lista += "</table>";

        html = html.replace("{{MENSAGEM}}", Pagina.mensagem(msg, erro));
        html = html.replace("{{TITULO_FORM}}", titulo);
        html = html.replace("{{ID}}", Pagina.texto(quarto.getId()));
        html = html.replace("{{OPCOES_HOTEIS}}", hoteis);
        html = html.replace("{{NUMERO}}", editar > 0 ? String.valueOf(quarto.getNumero()) : "");
        html = html.replace("{{OPCOES_TIPO}}", tipos);
        html = html.replace("{{CAPACIDADE}}", editar > 0 ? String.valueOf(quarto.getCapacidade()) : "");
        html = html.replace("{{VALOR}}", editar > 0 ? String.valueOf(quarto.getValorDiaria()) : "");
        html = html.replace("{{OPCOES_SITUACAO}}", situacoes);
        html = html.replace("{{LISTA}}", lista);
        return html;
    }

    @PostMapping("/tela/quartos")
    public ResponseEntity<Void> salvar(@RequestParam String id, @RequestParam(defaultValue = "") String hotelId,
            @RequestParam String numero, @RequestParam String tipo, @RequestParam String capacidade,
            @RequestParam String valorDiaria, @RequestParam String situacao) {
        try {
            Quarto quarto = new Quarto();
            quarto.setHotelId(Pagina.paraId(hotelId, "Hotel"));
            quarto.setNumero(Pagina.paraInt(numero, "Número"));
            quarto.setTipo(tipo);
            quarto.setCapacidade(Pagina.paraInt(capacidade, "Capacidade"));
            quarto.setValorDiaria(Pagina.paraDouble(valorDiaria, "Valor da diária"));
            quarto.setSituacao(situacao);

            if (id.isEmpty()) {
                service.save(quarto);
                return Pagina.voltar("/tela/quartos", "Quarto cadastrado com sucesso!");
            }
            service.update(Long.valueOf(id), quarto);
            return Pagina.voltar("/tela/quartos", "Quarto atualizado com sucesso!");
        } catch (NegocioException e) {
            return Pagina.voltarComErro("/tela/quartos", e.getMessage());
        }
    }

    @PostMapping("/tela/quartos/{id}/excluir")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        try {
            service.delete(id);
            return Pagina.voltar("/tela/quartos", "Quarto excluído com sucesso!");
        } catch (NegocioException e) {
            return Pagina.voltarComErro("/tela/quartos", e.getMessage());
        }
    }
}
