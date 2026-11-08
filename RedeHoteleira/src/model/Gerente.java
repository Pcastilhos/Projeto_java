package model;

import java.time.LocalDate;

public class Gerente extends Funcionario {

    public Gerente(int id, String nome, String cpf, String telefone, String email, Hotel hotel, double salario,
            LocalDate dataContratacao, String horarioEntrada, String horarioSaida, LocalDate feriasInicio, LocalDate feriasFim) {
        super(id, nome, cpf, telefone, email, hotel, salario, dataContratacao, horarioEntrada, horarioSaida, feriasInicio, feriasFim);
    }

    @Override
    public String getCargo() {
        return "GERENTE";
    }

    // gerente ganha 20% de gratificação
    @Override
    public double calcularSalario() {
        return salario * 1.20;
    }
}
