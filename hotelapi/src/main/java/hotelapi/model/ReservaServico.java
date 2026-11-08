package hotelapi.model;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

// serviço usado pelo hóspede durante uma reserva
@Entity
@Table(name = "reserva_servico")
public class ReservaServico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private Long reservaId;
    @Column
    private Long servicoId;
    @Column
    private int quantidade;
    @Column
    private double valorUnitario;
    @Column
    private LocalDate data;

    public ReservaServico() {
    }

    public double calcularSubtotal() {
        return quantidade * valorUnitario;
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Long getReservaId() {
        return reservaId;
    }
    public void setReservaId(Long reservaId) {
        this.reservaId = reservaId;
    }
    public Long getServicoId() {
        return servicoId;
    }
    public void setServicoId(Long servicoId) {
        this.servicoId = servicoId;
    }
    public int getQuantidade() {
        return quantidade;
    }
    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }
    public double getValorUnitario() {
        return valorUnitario;
    }
    public void setValorUnitario(double valorUnitario) {
        this.valorUnitario = valorUnitario;
    }
    public LocalDate getData() {
        return data;
    }
    public void setData(LocalDate data) {
        this.data = data;
    }
}
