package model;

public class QuartoLuxo extends Quarto {

    public QuartoLuxo(int id, Hotel hotel, int numero, int capacidade, double valorDiaria, String situacao) {
        super(id, hotel, numero, capacidade, valorDiaria, situacao);
    }

    @Override
    public String getTipo() {
        return "LUXO";
    }

    // luxo cobra 10% de taxa de serviço sobre a diária
    @Override
    public double calcularDiaria() {
        return valorDiaria * 1.10;
    }
}
