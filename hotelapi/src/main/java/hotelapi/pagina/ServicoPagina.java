package hotelapi.pagina;

import java.io.IOException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import hotelapi.exception.NegocioException;
import hotelapi.model.Servico;
import hotelapi.service.ServicoService;

@RestController
public class ServicoPagina {

    private final ServicoService service;

    public ServicoPagina(ServicoService service) {
        this.service = service;
    }

    @GetMapping(value = "/tela/servicos", produces = "text/html;charset=UTF-8")
    public String pagina(@RequestParam(defaultValue = "") String msg, @RequestParam(defaultValue = "") String erro,
            @RequestParam(defaultValue = "0") Long editar) throws IOException {

        String html = Pagina.ler("servicos.html");

        Servico servico = new Servico();
        String titulo = "Novo serviço";
        if (editar > 0) {
            servico = service.findById(editar);
            titulo = "Editando serviço " + servico.getId();
        }

        String lista = "<table><tr><th>ID</th><th>Nome</th><th>Descrição</th><th>Valor</th><th>Ações</th></tr>";
        for (Servico s : service.findAll()) {
            lista += "<tr>";
            lista += "<td>" + s.getId() + "</td>";
            lista += "<td>" + Pagina.texto(s.getNome()) + "</td>";
            lista += "<td>" + Pagina.texto(s.getDescricao()) + "</td>";
            lista += "<td>" + Pagina.dinheiro(s.getValor()) + "</td>";
            lista += "<td><a href='/tela/servicos?editar=" + s.getId() + "'>Editar</a>";
            lista += "<form class='inline' method='post' action='/tela/servicos/" + s.getId() + "/excluir'>"
                    + "<button class='perigo'>Excluir</button></form></td>";
            lista += "</tr>";
        }
        lista += "</table>";

        html = html.replace("{{MENSAGEM}}", Pagina.mensagem(msg, erro));
        html = html.replace("{{TITULO_FORM}}", titulo);
        html = html.replace("{{ID}}", Pagina.texto(servico.getId()));
        html = html.replace("{{NOME}}", Pagina.texto(servico.getNome()));
        html = html.replace("{{DESCRICAO}}", Pagina.texto(servico.getDescricao()));
        html = html.replace("{{VALOR}}", editar > 0 ? String.valueOf(servico.getValor()) : "");
        html = html.replace("{{LISTA}}", lista);
        return html;
    }

    @PostMapping("/tela/servicos")
    public ResponseEntity<Void> salvar(@RequestParam String id, @RequestParam String nome,
            @RequestParam String descricao, @RequestParam String valor) {
        try {
            Servico servico = new Servico();
            servico.setNome(nome);
            servico.setDescricao(descricao);
            servico.setValor(Pagina.paraDouble(valor, "Valor"));

            if (id.isEmpty()) {
                service.save(servico);
                return Pagina.voltar("/tela/servicos", "Serviço cadastrado com sucesso!");
            }
            service.update(Long.valueOf(id), servico);
            return Pagina.voltar("/tela/servicos", "Serviço atualizado com sucesso!");
        } catch (NegocioException e) {
            return Pagina.voltarComErro("/tela/servicos", e.getMessage());
        }
    }

    @PostMapping("/tela/servicos/{id}/excluir")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        try {
            service.delete(id);
            return Pagina.voltar("/tela/servicos", "Serviço excluído com sucesso!");
        } catch (NegocioException e) {
            return Pagina.voltarComErro("/tela/servicos", e.getMessage());
        }
    }
}
