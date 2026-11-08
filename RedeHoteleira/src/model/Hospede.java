package model;

import java.time.LocalDate;

public class Hospede extends Pessoa {

    private LocalDate dataNascimento;

    public Hospede(int id, String nome, String cpf, LocalDate dataNascimento, String telefone, String email) {
        super(id, nome, cpf, telefone, email);
        this.dataNascimento = dataNascimento;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }
    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
    }
}
