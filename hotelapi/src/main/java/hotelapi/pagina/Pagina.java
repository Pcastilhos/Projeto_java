package hotelapi.pagina;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;

import hotelapi.exception.NegocioException;

// métodos usados por todas as páginas HTML (igual à lojinha)
public class Pagina {

    // lê o arquivo da pasta templates
    public static String ler(String arquivo) throws IOException {
        ClassPathResource recurso = new ClassPathResource("templates/" + arquivo);
        return StreamUtils.copyToString(recurso.getInputStream(), StandardCharsets.UTF_8);
    }

    // volta para a página com uma mensagem de sucesso
    public static ResponseEntity<Void> voltar(String url, String mensagem) {
        String destino = url + "?msg=" + URLEncoder.encode(mensagem, StandardCharsets.UTF_8);
        return ResponseEntity.status(HttpStatus.FOUND).header("Location", destino).build();
    }

    // volta para a página com uma mensagem de erro
    public static ResponseEntity<Void> voltarComErro(String url, String erro) {
        String destino = url + "?erro=" + URLEncoder.encode(erro, StandardCharsets.UTF_8);
        return ResponseEntity.status(HttpStatus.FOUND).header("Location", destino).build();
    }

    public static String mensagem(String msg, String erro) {
        if (!erro.isEmpty()) {
            return "<p class='erro'>" + texto(erro) + "</p>";
        }
        if (!msg.isEmpty()) {
            return "<p class='ok'>" + texto(msg) + "</p>";
        }
        return "";
    }

    // evita que um texto digitado com < ou > quebre o HTML
    public static String texto(Object valor) {
        if (valor == null) {
            return "";
        }
        return valor.toString().replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("'", "&#39;");
    }

    public static String dinheiro(double valor) {
        return String.format("R$ %.2f", valor);
    }

    // monta um <option> do <select>, já marcado se for o valor escolhido
    public static String opcao(Object valor, String rotulo, Object escolhido) {
        String marcado = "";
        if (escolhido != null && valor.toString().equals(escolhido.toString())) {
            marcado = " selected";
        }
        return "<option value='" + texto(valor) + "'" + marcado + ">" + texto(rotulo) + "</option>";
    }

    public static int paraInt(String texto, String campo) {
        try {
            return Integer.parseInt(texto.trim());
        } catch (NumberFormatException e) {
            throw new NegocioException("O campo " + campo + " deve ser um número inteiro.");
        }
    }

    public static double paraDouble(String texto, String campo) {
        try {
            return Double.parseDouble(texto.trim().replace(",", "."));
        } catch (NumberFormatException e) {
            throw new NegocioException("O campo " + campo + " deve ser um valor numérico.");
        }
    }

    public static Long paraId(String texto, String campo) {
        if (texto == null || texto.isEmpty()) {
            throw new NegocioException("Selecione o campo " + campo + ".");
        }
        return Long.valueOf(paraInt(texto, campo));
    }

    // o <input type='date'> envia a data como aaaa-mm-dd; em branco vira null
    public static LocalDate paraData(String texto, String campo) {
        if (texto == null || texto.isEmpty()) {
            return null;
        }
        try {
            return LocalDate.parse(texto);
        } catch (DateTimeParseException e) {
            throw new NegocioException("O campo " + campo + " tem uma data inválida.");
        }
    }

    public static String data(LocalDate data) {
        if (data == null) {
            return "";
        }
        return String.format("%02d/%02d/%d", data.getDayOfMonth(), data.getMonthValue(), data.getYear());
    }
}
