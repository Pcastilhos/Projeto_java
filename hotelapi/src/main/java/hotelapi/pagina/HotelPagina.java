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
import hotelapi.model.Funcionario;
import hotelapi.model.Hotel;
import hotelapi.model.Quarto;
import hotelapi.service.HotelService;

// páginas HTML de hotéis: usam o mesmo HotelService da API
@RestController
public class HotelPagina {

    private final HotelService service;

    public HotelPagina(HotelService service) {
        this.service = service;
    }

    @GetMapping(value = "/tela/hoteis", produces = "text/html;charset=UTF-8")
    public String pagina(@RequestParam(defaultValue = "") String msg, @RequestParam(defaultValue = "") String erro,
            @RequestParam(defaultValue = "0") Long editar) throws IOException {

        String html = Pagina.ler("hoteis.html");

        // se clicou em "Editar", o formulário vem preenchido
        Hotel hotel = new Hotel();
        hotel.setEstrelas(3);
        String titulo = "Novo hotel";
        if (editar > 0) {
            hotel = service.findById(editar);
            titulo = "Editando hotel " + hotel.getId();
        }

        String estrelas = "";
        for (int i = 1; i <= 5; i++) {
            estrelas += Pagina.opcao(i, i + " estrelas", hotel.getEstrelas());
        }

        String lista = "<table><tr><th>ID</th><th>Nome</th><th>Endereço</th><th>Cidade</th><th>Telefone</th><th>Estrelas</th><th>Ações</th></tr>";
        for (Hotel h : service.findAll()) {
            lista += "<tr>";
            lista += "<td>" + h.getId() + "</td>";
            lista += "<td>" + Pagina.texto(h.getNome()) + "</td>";
            lista += "<td>" + Pagina.texto(h.getEndereco()) + "</td>";
            lista += "<td>" + Pagina.texto(h.getCidade()) + "</td>";
            lista += "<td>" + Pagina.texto(h.getTelefone()) + "</td>";
            lista += "<td>" + h.getEstrelas() + "</td>";
            lista += "<td>";
            lista += "<a href='/tela/hoteis?editar=" + h.getId() + "'>Editar</a> · ";
            lista += "<a href='/tela/hoteis/" + h.getId() + "/quartos'>Quartos</a> · ";
            lista += "<a href='/tela/hoteis/" + h.getId() + "/funcionarios'>Funcionários</a>";
            lista += "<form class='inline' method='post' action='/tela/hoteis/" + h.getId() + "/excluir'>"
                    + "<button class='perigo'>Excluir</button></form>";
            lista += "</td></tr>";
        }
        lista += "</table>";

        html = html.replace("{{MENSAGEM}}", Pagina.mensagem(msg, erro));
        html = html.replace("{{TITULO_FORM}}", titulo);
        html = html.replace("{{ID}}", Pagina.texto(hotel.getId()));
        html = html.replace("{{NOME}}", Pagina.texto(hotel.getNome()));
        html = html.replace("{{ENDERECO}}", Pagina.texto(hotel.getEndereco()));
        html = html.replace("{{CIDADE}}", Pagina.texto(hotel.getCidade()));
        html = html.replace("{{TELEFONE}}", Pagina.texto(hotel.getTelefone()));
        html = html.replace("{{OPCOES_ESTRELAS}}", estrelas);
        html = html.replace("{{LISTA}}", lista);
        return html;
    }

    // salva um novo hotel ou atualiza, se o id vier preenchido
    @PostMapping("/tela/hoteis")
    public ResponseEntity<Void> salvar(@RequestParam String id, @RequestParam String nome, @RequestParam String endereco,
            @RequestParam String cidade, @RequestParam String telefone, @RequestParam String estrelas) {
        try {
            Hotel hotel = new Hotel();
            hotel.setNome(nome);
            hotel.setEndereco(endereco);
            hotel.setCidade(cidade);
            hotel.setTelefone(telefone);
            hotel.setEstrelas(Pagina.paraInt(estrelas, "Estrelas"));

            if (id.isEmpty()) {
                service.save(hotel);
                return Pagina.voltar("/tela/hoteis", "Hotel cadastrado com sucesso!");
            }
            service.update(Long.valueOf(id), hotel);
            return Pagina.voltar("/tela/hoteis", "Hotel atualizado com sucesso!");
        } catch (NegocioException e) {
            return Pagina.voltarComErro("/tela/hoteis", e.getMessage());
        }
    }

    @PostMapping("/tela/hoteis/{id}/excluir")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        try {
            service.delete(id);
            return Pagina.voltar("/tela/hoteis", "Hotel excluído com sucesso!");
        } catch (NegocioException e) {
            return Pagina.voltarComErro("/tela/hoteis", e.getMessage());
        }
    }

    @GetMapping(value = "/tela/hoteis/{id}/quartos", produces = "text/html;charset=UTF-8")
    public String quartos(@PathVariable Long id) throws IOException {
        Hotel hotel = service.findById(id);
        List<Quarto> quartos = service.listarQuartos(id);

        String lista = "<table><tr><th>Número</th><th>Tipo</th><th>Capacidade</th><th>Diária final</th><th>Situação</th></tr>";
        for (Quarto q : quartos) {
            lista += "<tr><td>" + q.getNumero() + "</td><td>" + q.getTipo() + "</td><td>" + q.getCapacidade()
                    + "</td><td>" + Pagina.dinheiro(q.getDiariaFinal()) + "</td><td>" + q.getSituacao() + "</td></tr>";
        }
        lista += "</table>";

        String html = Pagina.ler("listagem.html");
        html = html.replace("{{TITULO}}", "Quartos do " + Pagina.texto(hotel.getNome()));
        html = html.replace("{{LISTA}}", lista);
        html = html.replace("{{VOLTAR}}", "/tela/hoteis");
        return html;
    }

    @GetMapping(value = "/tela/hoteis/{id}/funcionarios", produces = "text/html;charset=UTF-8")
    public String funcionarios(@PathVariable Long id) throws IOException {
        Hotel hotel = service.findById(id);
        List<Funcionario> funcionarios = service.listarFuncionarios(id);

        String lista = "<table><tr><th>Nome</th><th>Cargo</th><th>Horário</th><th>Salário final</th></tr>";
        for (Funcionario f : funcionarios) {
            lista += "<tr><td>" + Pagina.texto(f.getNome()) + "</td><td>" + f.getTipo() + "</td><td>"
                    + Pagina.texto(f.getHorarioEntrada()) + " às " + Pagina.texto(f.getHorarioSaida()) + "</td><td>"
                    + Pagina.dinheiro(f.getSalarioFinal()) + "</td></tr>";
        }
        lista += "</table>";

        String html = Pagina.ler("listagem.html");
        html = html.replace("{{TITULO}}", "Funcionários do " + Pagina.texto(hotel.getNome()));
        html = html.replace("{{LISTA}}", lista);
        html = html.replace("{{VOLTAR}}", "/tela/hoteis");
        return html;
    }
}
