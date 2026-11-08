package util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import exception.NegocioException;

// converte e valida os textos digitados nas telas
public class Conversor {

    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public static String obrigatorio(String texto, String campo) throws NegocioException {
        if (texto == null || texto.trim().isEmpty()) {
            throw new NegocioException("O campo " + campo + " é obrigatório.");
        }
        return texto.trim();
    }

    public static int paraInteiro(String texto, String campo) throws NegocioException {
        try {
            return Integer.parseInt(obrigatorio(texto, campo));
        } catch (NumberFormatException e) {
            throw new NegocioException("O campo " + campo + " deve ser um número inteiro.");
        }
    }

    public static double paraDouble(String texto, String campo) throws NegocioException {
        try {
            return Double.parseDouble(obrigatorio(texto, campo).replace(",", "."));
        } catch (NumberFormatException e) {
            throw new NegocioException("O campo " + campo + " deve ser um valor numérico.");
        }
    }

    public static LocalDate paraData(String texto, String campo) throws NegocioException {
        try {
            String digitado = obrigatorio(texto, campo);
            LocalDate data = LocalDate.parse(digitado, FORMATO);
            // impede datas que não existem, como 31/02 (o Java ajustaria para 28/02)
            if (!data.format(FORMATO).equals(digitado)) {
                throw new NegocioException("O campo " + campo + " tem uma data que não existe.");
            }
            return data;
        } catch (DateTimeParseException e) {
            throw new NegocioException("O campo " + campo + " deve estar no formato dd/mm/aaaa.");
        }
    }

    // para datas que podem ficar em branco (ex.: férias)
    public static LocalDate paraDataOpcional(String texto, String campo) throws NegocioException {
        if (texto == null || texto.trim().isEmpty()) {
            return null;
        }
        return paraData(texto, campo);
    }

    // aceita CPF com ou sem pontos e traço, mas precisa ter 11 números
    public static String validarCpf(String texto) throws NegocioException {
        String cpf = obrigatorio(texto, "CPF").replace(".", "").replace("-", "");
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

    public static String formatarData(LocalDate data) {
        if (data == null) {
            return "";
        }
        return data.format(FORMATO);
    }
}
