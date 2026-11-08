package hotelapi.pagina;

import java.io.IOException;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import hotelapi.exception.NegocioException;
import hotelapi.model.Hospede;
import hotelapi.model.Quarto;
import hotelapi.model.Reserva;
import hotelapi.model.ReservaServico;
import hotelapi.service.HospedeService;
import hotelapi.service.HotelService;
import hotelapi.service.QuartoService;
import hotelapi.service.ReservaService;
import hotelapi.service.ServicoService;

@RestController
public class HospedePagina {

    private final HospedeService service;
    private final ReservaService reservaService;
    private final QuartoService quartoService;
    private final HotelService hotelService;
    private final ServicoService servicoService;

    public HospedePagina(HospedeService service, ReservaService reservaService, QuartoService quartoService,
            HotelService hotelService, ServicoService servicoService) {
        this.service = service;
        this.reservaService = reservaService;
        this.quartoService = quartoService;
        this.hotelService = hotelService;
        this.servicoService = servicoService;
    }

    @GetMapping(value = "/tela/hospedes", produces = "text/html;charset=UTF-8")
    public String pagina(@RequestParam(defaultValue = "") String msg, @RequestParam(defaultValue = "") String erro,
            @RequestParam(defaultValue = "0") Long editar, @RequestParam(defaultValue = "") String texto) throws IOException {

        String html = Pagina.ler("hospedes.html");

        Hospede hospede = new Hospede();
        String titulo = "Novo hóspede";
        if (editar > 0) {
            hospede = service.findById(editar);
            titulo = "Editando hóspede " + hospede.getId();
        }

        // com texto na pesquisa, mostra só quem combina
        List<Hospede> hospedes = service.findAll();
        if (!texto.isEmpty()) {
            hospedes = service.pesquisar(texto);
        }

        String lista = "<table><tr><th>ID</th><th>Nome</th><th>CPF</th><th>Nascimento</th><th>Telefone</th><th>E-mail</th><th>Ações</th></tr>";
        for (Hospede h : hospedes) {
            lista += "<tr>";
            lista += "<td>" + h.getId() + "</td>";
            lista += "<td>" + Pagina.texto(h.getNome()) + "</td>";
            lista += "<td>" + Pagina.texto(h.getCpf()) + "</td>";
            lista += "<td>" + Pagina.data(h.getDataNascimento()) + "</td>";
            lista += "<td>" + Pagina.texto(h.getTelefone()) + "</td>";
            lista += "<td>" + Pagina.texto(h.getEmail()) + "</td>";
            lista += "<td><a href='/tela/hospedes?editar=" + h.getId() + "'>Editar</a> · ";
            lista += "<a href='/tela/hospedes/" + h.getId() + "/historico'>Histórico</a>";
            lista += "<form class='inline' method='post' action='/tela/hospedes/" + h.getId() + "/excluir'>"
                    + "<button class='perigo'>Excluir</button></form></td>";
            lista += "</tr>";
        }
        lista += "</table>";

        html = html.replace("{{MENSAGEM}}", Pagina.mensagem(msg, erro));
        html = html.replace("{{TITULO_FORM}}", titulo);
        html = html.replace("{{ID}}", Pagina.texto(hospede.getId()));
        html = html.replace("{{NOME}}", Pagina.texto(hospede.getNome()));
        html = html.replace("{{CPF}}", Pagina.texto(hospede.getCpf()));
        html = html.replace("{{NASCIMENTO}}", Pagina.texto(hospede.getDataNascimento()));
        html = html.replace("{{TELEFONE}}", Pagina.texto(hospede.getTelefone()));
        html = html.replace("{{EMAIL}}", Pagina.texto(hospede.getEmail()));
        html = html.replace("{{TEXTO}}", Pagina.texto(texto));
        html = html.replace("{{LISTA}}", lista);
        return html;
    }

    @PostMapping("/tela/hospedes")
    public ResponseEntity<Void> salvar(@RequestParam String id, @RequestParam String nome, @RequestParam String cpf,
            @RequestParam String dataNascimento, @RequestParam String telefone, @RequestParam String email) {
        try {
            Hospede hospede = new Hospede();
            hospede.setNome(nome);
            hospede.setCpf(cpf);
            hospede.setDataNascimento(Pagina.paraData(dataNascimento, "Data de nascimento"));
            hospede.setTelefone(telefone);
            hospede.setEmail(email);

            if (id.isEmpty()) {
                service.save(hospede);
                return Pagina.voltar("/tela/hospedes", "Hóspede cadastrado com sucesso!");
            }
            service.update(Long.valueOf(id), hospede);
            return Pagina.voltar("/tela/hospedes", "Hóspede atualizado com sucesso!");
        } catch (NegocioException e) {
            return Pagina.voltarComErro("/tela/hospedes", e.getMessage());
        }
    }

    @PostMapping("/tela/hospedes/{id}/excluir")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        try {
            service.delete(id);
            return Pagina.voltar("/tela/hospedes", "Hóspede excluído com sucesso!");
        } catch (NegocioException e) {
            return Pagina.voltarComErro("/tela/hospedes", e.getMessage());
        }
    }

    // histórico: reservas anteriores, quartos, períodos e serviços consumidos
    @GetMapping(value = "/tela/hospedes/{id}/historico", produces = "text/html;charset=UTF-8")
    public String historico(@PathVariable Long id) throws IOException {
        Hospede hospede = service.findById(id);

        String lista = "<table><tr><th>Reserva</th><th>Hotel</th><th>Quarto</th><th>Período</th><th>Status</th>"
                + "<th>Serviços consumidos</th><th>Valor</th></tr>";
        for (Reserva r : service.historico(id)) {
            Quarto quarto = quartoService.findById(r.getQuartoId());
            String servicos = "";
            for (ReservaServico rs : reservaService.listarServicos(r.getId())) {
                servicos += Pagina.texto(servicoService.findById(rs.getServicoId()).getNome()) + " (" + rs.getQuantidade() + ") ";
            }
            lista += "<tr><td>" + r.getId() + "</td>";
            lista += "<td>" + Pagina.texto(hotelService.findById(quarto.getHotelId()).getNome()) + "</td>";
            lista += "<td>" + quarto.getNumero() + " - " + quarto.getTipo() + "</td>";
            lista += "<td>" + Pagina.data(r.getDataEntrada()) + " a " + Pagina.data(r.getDataSaida()) + "</td>";
            lista += "<td>" + r.getStatus() + "</td>";
            lista += "<td>" + servicos + "</td>";
            lista += "<td>" + Pagina.dinheiro(r.getValorTotal()) + "</td></tr>";
        }
        lista += "</table>";

        String html = Pagina.ler("listagem.html");
        html = html.replace("{{TITULO}}", "Histórico de " + Pagina.texto(hospede.getNome()));
        html = html.replace("{{LISTA}}", lista);
        html = html.replace("{{VOLTAR}}", "/tela/hospedes");
        return html;
    }
}
