package hotelapi.model;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "funcionario")
public class Funcionario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private Long hotelId;
    @Column
    private String tipo;
    @Column
    private String nome;
    @Column
    private String cpf;
    @Column
    private String telefone;
    @Column
    private String email;
    @Column
    private double salario;
    @Column
    private LocalDate dataContratacao;
    @Column
    private String horarioEntrada;
    @Column
    private String horarioSaida;
    @Column
    private LocalDate feriasInicio;
    @Column
    private LocalDate feriasFim;

    public Funcionario() {
    }

    // mesma regra da versão Swing: gerente +20%, camareira +10%
    public double getSalarioFinal() {
        if ("GERENTE".equals(tipo)) {
            return salario * 1.20;
        }
        if ("CAMAREIRA".equals(tipo)) {
            return salario * 1.10;
        }
        return salario;
    }

    public boolean isDeFerias() {
        LocalDate hoje = LocalDate.now();
        if (feriasInicio == null || feriasFim == null) {
            return false;
        }
        return !hoje.isBefore(feriasInicio) && !hoje.isAfter(feriasFim);
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
    public String getTipo() {
        return tipo;
    }
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
    public String getNome() {
        return nome;
    }
    public void setNome(String nome) {
        this.nome = nome;
    }
    public String getCpf() {
        return cpf;
    }
    public void setCpf(String cpf) {
        this.cpf = cpf;
    }
    public String getTelefone() {
        return telefone;
    }
    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
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
