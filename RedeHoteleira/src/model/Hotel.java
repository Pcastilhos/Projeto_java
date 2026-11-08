package model;

public class Hotel {

    private int id;
    private String nome;
    private String endereco;
    private String cidade;
    private String telefone;
    private int estrelas;

    public Hotel(int id, String nome, String endereco, String cidade, String telefone, int estrelas) {
        this.id = id;
        this.nome = nome;
        this.endereco = endereco;
        this.cidade = cidade;
        this.telefone = telefone;
        this.estrelas = estrelas;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getNome() {
        return nome;
    }
    public void setNome(String nome) {
        this.nome = nome;
    }
    public String getEndereco() {
        return endereco;
    }
    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }
    public String getCidade() {
        return cidade;
    }
    public void setCidade(String cidade) {
        this.cidade = cidade;
    }
    public String getTelefone() {
        return telefone;
    }
    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }
    public int getEstrelas() {
        return estrelas;
    }
    public void setEstrelas(int estrelas) {
        this.estrelas = estrelas;
    }

    @Override
    public String toString() {
        return nome + " - " + cidade;
    }
}
