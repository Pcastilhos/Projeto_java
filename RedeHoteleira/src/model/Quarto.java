package model;

// classe abstrata: cada tipo de quarto calcula sua diária de um jeito
public abstract class Quarto {

    public static final String DISPONIVEL = "DISPONIVEL";
    public static final String OCUPADO = "OCUPADO";
    public static final String MANUTENCAO = "MANUTENCAO";

    protected int id;
    protected Hotel hotel;
    protected int numero;
    protected int capacidade;
    protected double valorDiaria;
    protected String situacao;

    public Quarto(int id, Hotel hotel, int numero, int capacidade, double valorDiaria, String situacao) {
        this.id = id;
        this.hotel = hotel;
        this.numero = numero;
        this.capacidade = capacidade;
        this.valorDiaria = valorDiaria;
        this.situacao = situacao;
    }

    public abstract String getTipo();

    public abstract double calcularDiaria();

    // valor da hospedagem = diária do tipo de quarto x quantidade de dias
    public double calcularValorHospedagem(int dias) {
        return calcularDiaria() * dias;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public Hotel getHotel() {
        return hotel;
    }
    public void setHotel(Hotel hotel) {
        this.hotel = hotel;
    }
    public int getNumero() {
        return numero;
    }
    public void setNumero(int numero) {
        this.numero = numero;
    }
    public int getCapacidade() {
        return capacidade;
    }
    public void setCapacidade(int capacidade) {
        this.capacidade = capacidade;
    }
    public double getValorDiaria() {
        return valorDiaria;
    }
    public void setValorDiaria(double valorDiaria) {
        this.valorDiaria = valorDiaria;
    }
    public String getSituacao() {
        return situacao;
    }
    public void setSituacao(String situacao) {
        this.situacao = situacao;
    }

    @Override
    public String toString() {
        return "Quarto " + numero + " - " + getTipo();
    }
}
