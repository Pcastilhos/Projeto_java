package hotelapi.util;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import hotelapi.exception.NegocioException;

// validações usadas por mais de um service
public class Validacao {

    public static void obrigatorio(String texto, String campo) {
        if (texto == null || texto.trim().isEmpty()) {
            throw new NegocioException("O campo " + campo + " é obrigatório.");
        }
    }

    public static void obrigatorio(Object valor, String campo) {
        if (valor == null) {
            throw new NegocioException("O campo " + campo + " é obrigatório.");
        }
    }

    // aceita CPF com ou sem pontos e traço, mas precisa ter 11 números
    public static String validarCpf(String texto) {
        obrigatorio(texto, "cpf");
        String cpf = texto.replace(".", "").replace("-", "");
        if (cpf.length() != 11) {
            throw new NegocioException("O CPF deve ter 11 números.");
        }
        for (int i = 0; i < cpf.length(); i++) {
            if (!Character.isDigit(cpf.charAt(i))) {
                throw new NegocioException("O CPF deve conter apenas números.");
            }
        }
        return cpf;
    }

    // datas recebidas pela URL vêm no formato aaaa-mm-dd
    public static LocalDate paraData(String texto, String campo) {
        obrigatorio(texto, campo);
        try {
            return LocalDate.parse(texto);
        } catch (DateTimeParseException e) {
            throw new NegocioException("O campo " + campo + " deve estar no formato aaaa-mm-dd.");
        }
    }
}
