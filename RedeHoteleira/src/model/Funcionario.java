package model;

import java.time.LocalDate;

// todo funcionário é uma Pessoa e é Remuneravel (interface)
public abstract class Funcionario extends Pessoa implements Remuneravel {

    protected Hotel hotel;
    protected double salario;
    protected LocalDate dataContratacao;
    protected String horarioEntrada;
    protected String horarioSaida;
    protected LocalDate feriasInicio;
    protected LocalDate feriasFim;

    public Funcionario(int id, String nome, String cpf, String telefone, String email, Hotel hotel, double salario,
            LocalDate dataContratacao, String horarioEntrada, String horarioSaida, LocalDate feriasInicio, LocalDate feriasFim) {
        super(id, nome, cpf, telefone, email);
        this.hotel = hotel;
        this.salario = salario;
        this.dataContratacao = dataContratacao;
        this.horarioEntrada = horarioEntrada;
        this.horarioSaida = horarioSaida;
        this.feriasInicio = feriasInicio;
        this.feriasFim = feriasFim;
    }

    // cada tipo de funcionário informa seu cargo
    public abstract String getCargo();

    // verifica se a data está dentro do período de férias
    public boolean estaDeFerias(LocalDate data) {
        if (feriasInicio == null || feriasFim == null) {
            return false;
        }
        return !data.isBefore(feriasInicio) && !data.isAfter(feriasFim);
    }

    public Hotel getHotel() {
        return hotel;
    }
    public void setHotel(Hotel hotel) {
        this.hotel = hotel;
    }
    public double getSalario() {
        return salario;
    }
    public void setSalario(double salario) {
        this.salario = salario;
    }
    public LocalDate getDataContratacao() {
        return dataContratacao;
    }
    public void setDataContratacao(LocalDate dataContratacao) {
        this.dataContratacao = dataContratacao;
    }
    public String getHorarioEntrada() {
        return horarioEntrada;
    }
    public void setHorarioEntrada(String horarioEntrada) {
        this.horarioEntrada = horarioEntrada;
    }
    public String getHorarioSaida() {
        return horarioSaida;
    }
    public void setHorarioSaida(String horarioSaida) {
        this.horarioSaida = horarioSaida;
    }
    public LocalDate getFeriasInicio() {
        return feriasInicio;
    }
    public void setFeriasInicio(LocalDate feriasInicio) {
        this.feriasInicio = feriasInicio;
    }
    public LocalDate getFeriasFim() {
        return feriasFim;
    }
    public void setFeriasFim(LocalDate feriasFim) {
        this.feriasFim = feriasFim;
    }
}
