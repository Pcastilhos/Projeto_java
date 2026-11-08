package model;

import java.time.LocalDate;

public class Recepcionista extends Funcionario {

    public Recepcionista(int id, String nome, String cpf, String telefone, String email, Hotel hotel, double salario,
            LocalDate dataContratacao, String horarioEntrada, String horarioSaida, LocalDate feriasInicio, LocalDate feriasFim) {
        super(id, nome, cpf, telefone, email, hotel, salario, dataContratacao, horarioEntrada, horarioSaida, feriasInicio, feriasFim);
    }

    @Override
    public String getCargo() {
        return "RECEPCIONISTA";
    }

    // recepcionista recebe o salário base
    @Override
    public double calcularSalario() {
        return salario;
    }
}
