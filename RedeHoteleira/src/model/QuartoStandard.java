package model;

public class QuartoStandard extends Quarto {

    public QuartoStandard(int id, Hotel hotel, int numero, int capacidade, double valorDiaria, String situacao) {
        super(id, hotel, numero, capacidade, valorDiaria, situacao);
    }

    @Override
    public String getTipo() {
        return "STANDARD";
    }

    // standard cobra só a diária base
    @Override
    public double calcularDiaria() {
        return valorDiaria;
    }
}
