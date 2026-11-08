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
import hotelapi.model.Hotel;
import hotelapi.model.Quarto;
import hotelapi.model.Reserva;
import hotelapi.model.ReservaServico;
import hotelapi.model.Servico;
import hotelapi.service.HospedeService;
import hotelapi.service.HotelService;
import hotelapi.service.QuartoService;
import hotelapi.service.ReservaService;
import hotelapi.service.ServicoService;

// páginas HTML de reservas: usam o mesmo ReservaService da API
@RestController
public class ReservaPagina {

    private final ReservaService service;
    private final HospedeService hospedeService;
    private final HotelService hotelService;
    private final QuartoService quartoService;
    private final ServicoService servicoService;

    public ReservaPagina(ReservaService service, HospedeService hospedeService, HotelService hotelService,
            QuartoService quartoService, ServicoService servicoService) {
        this.service = service;
        this.hospedeService = hospedeService;
        this.hotelService = hotelService;
        this.quartoService = quartoService;
        this.servicoService = servicoService;
    }

    @GetMapping(value = "/tela/reservas", produces = "text/html;charset=UTF-8")
    public String pagina(@RequestParam(defaultValue = "") String msg, @RequestParam(defaultValue = "") String erro,
            @RequestParam(defaultValue = "") String hospedeId, @RequestParam(defaultValue = "") String hotelId,
            @RequestParam(defaultValue = "") String entrada, @RequestParam(defaultValue = "") String saida,
            @RequestParam(defaultValue = "") String quantidade) throws IOException {

        String html = Pagina.ler("reservas.html");

        String hospedes = "";
        for (Hospede h : hospedeService.findAll()) {
            hospedes += Pagina.opcao(h.getId(), h.getNome() + " (" + h.getCpf() + ")", hospedeId);
        }
        String hoteis = "";
        for (Hotel h : hotelService.findAll()) {
            hoteis += Pagina.opcao(h.getId(), h.getNome(), hotelId);
        }

        // se o formulário de busca foi enviado, mostra os quartos livres com o botão de reservar
        String disponiveis = "";
        if (!hotelId.isEmpty()) {
            try {
                int qtd = Pagina.paraInt(quantidade, "Quantidade de hóspedes");
                List<Quarto> quartos = service.buscarQuartosDisponiveis(Long.valueOf(hotelId), entrada, saida, qtd);
                if (quartos.isEmpty()) {
                    disponiveis = "<p class='erro'>Nenhum quarto disponível para esse período.</p>";
                } else {
                    disponiveis = "<h3>Quartos disponíveis</h3><table><tr><th>Quarto</th><th>Tipo</th><th>Capacidade</th><th>Diária</th><th></th></tr>";
                    for (Quarto q : quartos) {
                        disponiveis += "<tr><td>" + q.getNumero() + "</td><td>" + q.getTipo() + "</td><td>" + q.getCapacidade()
                                + "</td><td>" + Pagina.dinheiro(q.getDiariaFinal()) + "</td><td>";
                        disponiveis += "<form method='post' action='/tela/reservas'>"
                                + "<input type='hidden' name='hospedeId' value='" + Pagina.texto(hospedeId) + "'>"
                                + "<input type='hidden' name='quartoId' value='" + q.getId() + "'>"
                                + "<input type='hidden' name='entrada' value='" + Pagina.texto(entrada) + "'>"
                                + "<input type='hidden' name='saida' value='" + Pagina.texto(saida) + "'>"
                                + "<input type='hidden' name='quantidade' value='" + Pagina.texto(quantidade) + "'>"
                                + "<button>Reservar este quarto</button></form>";
                        disponiveis += "</td></tr>";
                    }
                    disponiveis += "</table>";
                }
            } catch (NegocioException e) {
                disponiveis = "<p class='erro'>" + Pagina.texto(e.getMessage()) + "</p>";
            }
        }

        String opcoesServicos = "";
        for (Servico s : servicoService.findAll()) {
            opcoesServicos += Pagina.opcao(s.getId(), s.getNome() + " - " + Pagina.dinheiro(s.getValor()), null);
        }

        String lista = "<table><tr><th>ID</th><th>Hóspede</th><th>Hotel / Quarto</th><th>Período</th><th>Diárias</th>"
                + "<th>Status</th><th>Valor</th><th>Ações</th></tr>";
        for (Reserva r : service.findAll()) {
            Quarto quarto = quartoService.findById(r.getQuartoId());
            String acao = "/tela/reservas/" + r.getId();

            lista += "<tr>";
            lista += "<td>" + r.getId() + "</td>";
            lista += "<td>" + Pagina.texto(hospedeService.findById(r.getHospedeId()).getNome()) + "</td>";
            lista += "<td>" + Pagina.texto(hotelService.findById(quarto.getHotelId()).getNome()) + " / "
                    + quarto.getNumero() + " " + quarto.getTipo() + "</td>";
            lista += "<td>" + Pagina.data(r.getDataEntrada()) + " a " + Pagina.data(r.getDataSaida()) + "</td>";
            lista += "<td>" + r.calcularDiarias() + "</td>";
            lista += "<td>" + r.getStatus() + "</td>";
            lista += "<td>" + Pagina.dinheiro(r.getValorTotal()) + "</td>";
            lista += "<td>";

            // os botões que aparecem dependem do status da reserva
            if (r.getStatus().equals(Reserva.PENDENTE)) {
                lista += "<form class='inline' method='post' action='" + acao + "/confirmar'><button>Confirmar</button></form>";
            }
            if (r.getStatus().equals(Reserva.CONFIRMADA)) {
                lista += "<form class='inline' method='post' action='" + acao + "/checkin'><button>Check-in</button></form>";
            }
            if (r.getStatus().equals(Reserva.PENDENTE) || r.getStatus().equals(Reserva.CONFIRMADA)) {
                lista += "<form class='inline' method='post' action='" + acao + "/cancelar'><button class='perigo'>Cancelar</button></form>";
                lista += "<form method='post' action='" + acao + "/datas'>"
                        + "<input type='date' name='entrada' value='" + r.getDataEntrada() + "'>"
                        + "<input type='date' name='saida' value='" + r.getDataSaida() + "'>"
                        + "<button>Alterar datas</button></form>";
            }
            if (r.getStatus().equals(Reserva.EM_ANDAMENTO)) {
                lista += "<form method='post' action='" + acao + "/servicos'>"
                        + "<select name='servicoId'>" + opcoesServicos + "</select>"
                        + "<input type='number' name='quantidade' value='1' style='width:60px'>"
                        + "<button>Adicionar serviço</button></form>";
                lista += "<form class='inline' method='get' action='" + acao + "/checkout'><button>Check-out</button></form>";
            }
            if (r.getStatus().equals(Reserva.PENDENTE) || r.getStatus().equals(Reserva.CANCELADA)) {
                lista += "<form class='inline' method='post' action='" + acao + "/excluir'><button class='perigo'>Excluir</button></form>";
            }
            lista += " <a href='" + acao + "/servicos'>Ver serviços</a>";
            lista += "</td></tr>";
        }
        lista += "</table>";

        html = html.replace("{{MENSAGEM}}", Pagina.mensagem(msg, erro));
        html = html.replace("{{OPCOES_HOSPEDES}}", hospedes);
        html = html.replace("{{OPCOES_HOTEIS}}", hoteis);
        html = html.replace("{{ENTRADA}}", Pagina.texto(entrada));
        html = html.replace("{{SAIDA}}", Pagina.texto(saida));
        html = html.replace("{{QUANTIDADE}}", Pagina.texto(quantidade));
        html = html.replace("{{DISPONIVEIS}}", disponiveis);
        html = html.replace("{{LISTA}}", lista);
        return html;
    }

    @PostMapping("/tela/reservas")
    public ResponseEntity<Void> reservar(@RequestParam(defaultValue = "") String hospedeId, @RequestParam String quartoId,
            @RequestParam String entrada, @RequestParam String saida, @RequestParam String quantidade) {
        try {
            Reserva reserva = new Reserva();
            reserva.setHospedeId(Pagina.paraId(hospedeId, "Hóspede"));
            reserva.setQuartoId(Long.valueOf(quartoId));
            reserva.setDataEntrada(Pagina.paraData(entrada, "Data de entrada"));
            reserva.setDataSaida(Pagina.paraData(saida, "Data de saída"));
            reserva.setQuantidadeHospedes(Pagina.paraInt(quantidade, "Quantidade de hóspedes"));
            service.save(reserva);
            return Pagina.voltar("/tela/reservas", "Reserva criada com status PENDENTE!");
        } catch (NegocioException e) {
            return Pagina.voltarComErro("/tela/reservas", e.getMessage());
        }
    }

    @PostMapping("/tela/reservas/{id}/datas")
    public ResponseEntity<Void> alterarDatas(@PathVariable Long id, @RequestParam String entrada, @RequestParam String saida) {
        try {
            Reserva dados = new Reserva();
            dados.setDataEntrada(Pagina.paraData(entrada, "Data de entrada"));
            dados.setDataSaida(Pagina.paraData(saida, "Data de saída"));
            service.update(id, dados);
            return Pagina.voltar("/tela/reservas", "Datas alteradas e valor recalculado!");
        } catch (NegocioException e) {
            return Pagina.voltarComErro("/tela/reservas", e.getMessage());
        }
    }

    @PostMapping("/tela/reservas/{id}/confirmar")
    public ResponseEntity<Void> confirmar(@PathVariable Long id) {
        try {
            service.confirmar(id);
            return Pagina.voltar("/tela/reservas", "Reserva confirmada!");
        } catch (NegocioException e) {
            return Pagina.voltarComErro("/tela/reservas", e.getMessage());
        }
    }

    @PostMapping("/tela/reservas/{id}/checkin")
    public ResponseEntity<Void> checkIn(@PathVariable Long id) {
        try {
            service.checkIn(id);
            return Pagina.voltar("/tela/reservas", "Check-in realizado! O quarto agora está OCUPADO.");
        } catch (NegocioException e) {
            return Pagina.voltarComErro("/tela/reservas", e.getMessage());
        }
    }

    @PostMapping("/tela/reservas/{id}/servicos")
    public ResponseEntity<Void> adicionarServico(@PathVariable Long id, @RequestParam(defaultValue = "") String servicoId,
            @RequestParam String quantidade) {
        try {
            ReservaServico dados = new ReservaServico();
            dados.setServicoId(Pagina.paraId(servicoId, "Serviço"));
            dados.setQuantidade(Pagina.paraInt(quantidade, "Quantidade"));
            service.adicionarServico(id, dados);
            return Pagina.voltar("/tela/reservas", "Serviço lançado na reserva!");
        } catch (NegocioException e) {
            return Pagina.voltarComErro("/tela/reservas", e.getMessage());
        }
    }

    // antes de finalizar, mostra o resumo com os serviços usados e o total
    @GetMapping(value = "/tela/reservas/{id}/checkout", produces = "text/html;charset=UTF-8")
    public String resumoCheckOut(@PathVariable Long id) throws IOException {
        String html = Pagina.ler("checkout.html");
        try {
            Reserva reserva = service.findById(id);
            html = html.replace("{{HOSPEDE}}", Pagina.texto(hospedeService.findById(reserva.getHospedeId()).getNome()));
            html = html.replace("{{RESUMO}}", Pagina.texto(service.resumoCheckOut(id)));
        } catch (NegocioException e) {
            html = html.replace("{{HOSPEDE}}", "");
            html = html.replace("{{RESUMO}}", Pagina.texto(e.getMessage()));
        }
        html = html.replace("{{ID}}", String.valueOf(id));
        return html;
    }

    @PostMapping("/tela/reservas/{id}/checkout")
    public ResponseEntity<Void> checkOut(@PathVariable Long id) {
        try {
            service.checkOut(id);
            return Pagina.voltar("/tela/reservas", "Check-out realizado! Reserva finalizada e quarto liberado.");
        } catch (NegocioException e) {
            return Pagina.voltarComErro("/tela/reservas", e.getMessage());
        }
    }

    @PostMapping("/tela/reservas/{id}/cancelar")
    public ResponseEntity<Void> cancelar(@PathVariable Long id) {
        try {
            service.cancelar(id);
            return Pagina.voltar("/tela/reservas", "Reserva cancelada!");
        } catch (NegocioException e) {
            return Pagina.voltarComErro("/tela/reservas", e.getMessage());
        }
    }

    @PostMapping("/tela/reservas/{id}/excluir")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        try {
            service.delete(id);
            return Pagina.voltar("/tela/reservas", "Reserva excluída!");
        } catch (NegocioException e) {
            return Pagina.voltarComErro("/tela/reservas", e.getMessage());
        }
    }

    @GetMapping(value = "/tela/reservas/{id}/servicos", produces = "text/html;charset=UTF-8")
    public String servicos(@PathVariable Long id) throws IOException {
        String lista = "<table><tr><th>Serviço</th><th>Quantidade</th><th>Valor unitário</th><th>Subtotal</th><th>Data</th></tr>";
        for (ReservaServico rs : service.listarServicos(id)) {
            lista += "<tr><td>" + Pagina.texto(servicoService.findById(rs.getServicoId()).getNome()) + "</td>";
            lista += "<td>" + rs.getQuantidade() + "</td>";
            lista += "<td>" + Pagina.dinheiro(rs.getValorUnitario()) + "</td>";
            lista += "<td>" + Pagina.dinheiro(rs.calcularSubtotal()) + "</td>";
            lista += "<td>" + Pagina.data(rs.getData()) + "</td></tr>";
        }
        lista += "</table>";

        String html = Pagina.ler("listagem.html");
        html = html.replace("{{TITULO}}", "Serviços da reserva " + id);
        html = html.replace("{{LISTA}}", lista);
        html = html.replace("{{VOLTAR}}", "/tela/reservas");
        return html;
    }
}
