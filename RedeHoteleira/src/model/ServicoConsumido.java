package model;

import java.time.LocalDate;

// serviço que o hóspede usou durante uma reserva
public class ServicoConsumido {

    private int id;
    private int reservaId;
    private Servico servico;
    private int quantidade;
    private double valorUnitario;
    private LocalDate data;

    public ServicoConsumido(int id, int reservaId, Servico servico, int quantidade, double valorUnitario, LocalDate data) {
        this.id = id;
        this.reservaId = reservaId;
        this.servico = servico;
        this.quantidade = quantidade;
        this.valorUnitario = valorUnitario;
        this.data = data;
    }

    public double calcularSubtotal() {
        return quantidade * valorUnitario;
    }

    public int getId() {
        return id;
    }
    public int getReservaId() {
        return reservaId;
    }
    public Servico getServico() {
        return servico;
    }
    public int getQuantidade() {
        return quantidade;
    }
    public double getValorUnitario() {
        return valorUnitario;
    }
    public LocalDate getData() {
        return data;
    }
}
