package model;

import java.time.LocalDate;

public class Camareira extends Funcionario {

    public Camareira(int id, String nome, String cpf, String telefone, String email, Hotel hotel, double salario,
            LocalDate dataContratacao, String horarioEntrada, String horarioSaida, LocalDate feriasInicio, LocalDate feriasFim) {
        super(id, nome, cpf, telefone, email, hotel, salario, dataContratacao, horarioEntrada, horarioSaida, feriasInicio, feriasFim);
    }

    @Override
    public String getCargo() {
        return "CAMAREIRA";
    }

    // camareira ganha 10% de adicional de insalubridade
    @Override
    public double calcularSalario() {
        return salario * 1.10;
    }
}
