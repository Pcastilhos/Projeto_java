package model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class Reserva {

    public static final String PENDENTE = "PENDENTE";
    public static final String CONFIRMADA = "CONFIRMADA";
    public static final String EM_ANDAMENTO = "EM_ANDAMENTO";
    public static final String FINALIZADA = "FINALIZADA";
    public static final String CANCELADA = "CANCELADA";

    private int id;
    private Hospede hospede;
    private Quarto quarto;
    private LocalDate dataEntrada;
    private LocalDate dataSaida;
    private int quantidadeHospedes;
    private String status;
    private double valorTotal;
    private List<ServicoConsumido> servicos = new ArrayList<>();

    public Reserva(int id, Hospede hospede, Quarto quarto, LocalDate dataEntrada, LocalDate dataSaida,
            int quantidadeHospedes, String status, double valorTotal) {
        this.id = id;
        this.hospede = hospede;
        this.quarto = quarto;
        this.dataEntrada = dataEntrada;
        this.dataSaida = dataSaida;
        this.quantidadeHospedes = quantidadeHospedes;
        this.status = status;
        this.valorTotal = valorTotal;
    }

    public int calcularDiarias() {
        return (int) ChronoUnit.DAYS.between(dataEntrada, dataSaida);
    }

    // polimorfismo: cada tipo de quarto calcula o valor do seu jeito
    public double calcularValorDiarias() {
        return quarto.calcularValorHospedagem(calcularDiarias());
    }

    public double calcularValorServicos() {
        double total = 0;
        for (ServicoConsumido servico : servicos) {
            total += servico.calcularSubtotal();
        }
        return total;
    }

    public double calcularValorTotal() {
        return calcularValorDiarias() + calcularValorServicos();
    }

    // dois períodos conflitam quando um começa antes do outro terminar
    public boolean conflitaCom(LocalDate entrada, LocalDate saida) {
        return entrada.isBefore(dataSaida) && saida.isAfter(dataEntrada);
    }

    // reservas canceladas ou finalizadas não ocupam mais o quarto
    public boolean estaAtiva() {
        return !status.equals(CANCELADA) && !status.equals(FINALIZADA);
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public Hospede getHospede() {
        return hospede;
    }
    public void setHospede(Hospede hospede) {
        this.hospede = hospede;
    }
    public Quarto getQuarto() {
        return quarto;
    }
    public void setQuarto(Quarto quarto) {
        this.quarto = quarto;
    }
    public LocalDate getDataEntrada() {
        return dataEntrada;
    }
    public void setDataEntrada(LocalDate dataEntrada) {
        this.dataEntrada = dataEntrada;
    }
    public LocalDate getDataSaida() {
        return dataSaida;
    }
    public void setDataSaida(LocalDate dataSaida) {
        this.dataSaida = dataSaida;
    }
    public int getQuantidadeHospedes() {
        return quantidadeHospedes;
    }
    public void setQuantidadeHospedes(int quantidadeHospedes) {
        this.quantidadeHospedes = quantidadeHospedes;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public double getValorTotal() {
        return valorTotal;
    }
    public void setValorTotal(double valorTotal) {
        this.valorTotal = valorTotal;
    }
    public List<ServicoConsumido> getServicos() {
        return servicos;
    }
    public void setServicos(List<ServicoConsumido> servicos) {
        this.servicos = servicos;
    }
}
