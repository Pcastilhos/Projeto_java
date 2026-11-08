package hotelapi.pagina;

import java.io.IOException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import hotelapi.exception.NegocioException;
import hotelapi.model.Funcionario;
import hotelapi.model.Hotel;
import hotelapi.service.FuncionarioService;
import hotelapi.service.HotelService;

@RestController
public class FuncionarioPagina {

    private final FuncionarioService service;
    private final HotelService hotelService;

    public FuncionarioPagina(FuncionarioService service, HotelService hotelService) {
        this.service = service;
        this.hotelService = hotelService;
    }

    @GetMapping(value = "/tela/funcionarios", produces = "text/html;charset=UTF-8")
    public String pagina(@RequestParam(defaultValue = "") String msg, @RequestParam(defaultValue = "") String erro,
            @RequestParam(defaultValue = "0") Long editar) throws IOException {

        String html = Pagina.ler("funcionarios.html");

        Funcionario f = new Funcionario();
        String titulo = "Novo funcionário";
        if (editar > 0) {
            f = service.findById(editar);
            titulo = "Editando funcionário " + f.getId();
        }

        String hoteis = "";
        for (Hotel h : hotelService.findAll()) {
            hoteis += Pagina.opcao(h.getId(), h.getNome(), f.getHotelId());
        }
        String cargos = Pagina.opcao("RECEPCIONISTA", "RECEPCIONISTA", f.getTipo())
                + Pagina.opcao("GERENTE", "GERENTE", f.getTipo())
                + Pagina.opcao("CAMAREIRA", "CAMAREIRA", f.getTipo());

        String lista = "<table><tr><th>ID</th><th>Nome</th><th>Cargo</th><th>Hotel</th><th>Salário base</th>"
                + "<th>Salário final</th><th>Horário</th><th>Férias</th><th>Situação hoje</th><th>Ações</th></tr>";
        for (Funcionario func : service.findAll()) {
            String ferias = "-";
            if (func.getFeriasInicio() != null) {
                ferias = Pagina.data(func.getFeriasInicio()) + " a " + Pagina.data(func.getFeriasFim());
            }
            String situacao = "Trabalhando";
            if (func.isDeFerias()) {
                situacao = "De férias";
            }
            lista += "<tr>";
            lista += "<td>" + func.getId() + "</td>";
            lista += "<td>" + Pagina.texto(func.getNome()) + "</td>";
            lista += "<td>" + func.getTipo() + "</td>";
            lista += "<td>" + Pagina.texto(hotelService.findById(func.getHotelId()).getNome()) + "</td>";
            lista += "<td>" + Pagina.dinheiro(func.getSalario()) + "</td>";
            lista += "<td>" + Pagina.dinheiro(func.getSalarioFinal()) + "</td>";
            lista += "<td>" + Pagina.texto(func.getHorarioEntrada()) + " às " + Pagina.texto(func.getHorarioSaida()) + "</td>";
            lista += "<td>" + ferias + "</td>";
            lista += "<td>" + situacao + "</td>";
            lista += "<td><a href='/tela/funcionarios?editar=" + func.getId() + "'>Editar</a>";
            lista += "<form class='inline' method='post' action='/tela/funcionarios/" + func.getId() + "/excluir'>"
                    + "<button class='perigo'>Excluir</button></form></td>";
            lista += "</tr>";
        }
        lista += "</table>";

        html = html.replace("{{MENSAGEM}}", Pagina.mensagem(msg, erro));
        html = html.replace("{{TITULO_FORM}}", titulo);
        html = html.replace("{{ID}}", Pagina.texto(f.getId()));
        html = html.replace("{{OPCOES_HOTEIS}}", hoteis);
        html = html.replace("{{OPCOES_CARGO}}", cargos);
        html = html.replace("{{NOME}}", Pagina.texto(f.getNome()));
        html = html.replace("{{CPF}}", Pagina.texto(f.getCpf()));
        html = html.replace("{{TELEFONE}}", Pagina.texto(f.getTelefone()));
        html = html.replace("{{EMAIL}}", Pagina.texto(f.getEmail()));
        html = html.replace("{{SALARIO}}", editar > 0 ? String.valueOf(f.getSalario()) : "");
        html = html.replace("{{CONTRATACAO}}", Pagina.texto(f.getDataContratacao()));
        html = html.replace("{{ENTRADA}}", Pagina.texto(f.getHorarioEntrada()));
        html = html.replace("{{SAIDA}}", Pagina.texto(f.getHorarioSaida()));
        html = html.replace("{{FERIAS_INICIO}}", Pagina.texto(f.getFeriasInicio()));
        html = html.replace("{{FERIAS_FIM}}", Pagina.texto(f.getFeriasFim()));
        html = html.replace("{{LISTA}}", lista);
        return html;
    }

    @PostMapping("/tela/funcionarios")
    public ResponseEntity<Void> salvar(@RequestParam String id, @RequestParam(defaultValue = "") String hotelId,
            @RequestParam String tipo, @RequestParam String nome, @RequestParam String cpf, @RequestParam String telefone,
            @RequestParam String email, @RequestParam String salario, @RequestParam String dataContratacao,
            @RequestParam String horarioEntrada, @RequestParam String horarioSaida,
            @RequestParam String feriasInicio, @RequestParam String feriasFim) {
        try {
            Funcionario f = new Funcionario();
            f.setHotelId(Pagina.paraId(hotelId, "Hotel"));
            f.setTipo(tipo);
            f.setNome(nome);
            f.setCpf(cpf);
            f.setTelefone(telefone);
            f.setEmail(email);
            f.setSalario(Pagina.paraDouble(salario, "Salário"));
            f.setDataContratacao(Pagina.paraData(dataContratacao, "Data de contratação"));
            f.setHorarioEntrada(horarioEntrada);
            f.setHorarioSaida(horarioSaida);
            f.setFeriasInicio(Pagina.paraData(feriasInicio, "Início das férias"));
            f.setFeriasFim(Pagina.paraData(feriasFim, "Fim das férias"));

            if (id.isEmpty()) {
                service.save(f);
                return Pagina.voltar("/tela/funcionarios", "Funcionário cadastrado com sucesso!");
            }
            service.update(Long.valueOf(id), f);
            return Pagina.voltar("/tela/funcionarios", "Funcionário atualizado com sucesso!");
        } catch (NegocioException e) {
            return Pagina.voltarComErro("/tela/funcionarios", e.getMessage());
        }
    }

    @PostMapping("/tela/funcionarios/{id}/excluir")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        try {
            service.delete(id);
            return Pagina.voltar("/tela/funcionarios", "Funcionário excluído com sucesso!");
        } catch (NegocioException e) {
            return Pagina.voltarComErro("/tela/funcionarios", e.getMessage());
        }
    }
}
