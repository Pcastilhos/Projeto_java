package hotelapi.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "quarto")
public class Quarto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private Long hotelId;
    @Column
    private int numero;
    @Column
    private String tipo;
    @Column
    private int capacidade;
    @Column
    private double valorDiaria;
    @Column
    private String situacao;

    public Quarto() {
    }

    // mesma regra da versão Swing: cada tipo de quarto tem uma taxa diferente
    public double getDiariaFinal() {
        if ("LUXO".equals(tipo)) {
            return valorDiaria * 1.10;
        }
        if ("SUITE".equals(tipo)) {
            return valorDiaria * 1.20;
        }
        return valorDiaria;
    }

    // suíte tem 10% de desconto para 7 dias ou mais
    public double calcularValorHospedagem(int dias) {
        double valor = getDiariaFinal() * dias;
        if ("SUITE".equals(tipo) && dias >= 7) {
            valor = valor * 0.90;
        }
        return valor;
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Long getHotelId() {
        return hotelId;
    }
    public void setHotelId(Long hotelId) {
        this.hotelId = hotelId;
    }
    public int getNumero() {
        return numero;
    }
    public void setNumero(int numero) {
        this.numero = numero;
    }
    public String getTipo() {
        return tipo;
    }
    public void setTipo(String tipo) {
        this.tipo = tipo;
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
}
