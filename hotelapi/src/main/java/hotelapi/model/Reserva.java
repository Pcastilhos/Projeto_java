package hotelapi.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "reserva")
public class Reserva {

    public static final String PENDENTE = "PENDENTE";
    public static final String CONFIRMADA = "CONFIRMADA";
    public static final String EM_ANDAMENTO = "EM_ANDAMENTO";
    public static final String FINALIZADA = "FINALIZADA";
    public static final String CANCELADA = "CANCELADA";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private Long hospedeId;
    @Column
    private Long quartoId;
    @Column
    private LocalDate dataEntrada;
    @Column
    private LocalDate dataSaida;
    @Column
    private int quantidadeHospedes;
    @Column
    private String status;
    @Column
    private double valorTotal;

    public Reserva() {
    }

    public int calcularDiarias() {
        return (int) ChronoUnit.DAYS.between(dataEntrada, dataSaida);
    }

    // mesma regra da versão Swing: períodos conflitam se um começa antes do outro terminar
    public boolean conflitaCom(LocalDate entrada, LocalDate saida) {
        return entrada.isBefore(dataSaida) && saida.isAfter(dataEntrada);
    }

    public boolean estaAtiva() {
        return !status.equals(CANCELADA) && !status.equals(FINALIZADA);
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Long getHospedeId() {
        return hospedeId;
    }
    public void setHospedeId(Long hospedeId) {
        this.hospedeId = hospedeId;
    }
    public Long getQuartoId() {
        return quartoId;
    }
    public void setQuartoId(Long quartoId) {
        this.quartoId = quartoId;
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
}
