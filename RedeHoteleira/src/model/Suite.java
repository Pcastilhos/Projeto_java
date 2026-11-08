package model;

public class Suite extends Quarto {

    public Suite(int id, Hotel hotel, int numero, int capacidade, double valorDiaria, String situacao) {
        super(id, hotel, numero, capacidade, valorDiaria, situacao);
    }

    @Override
    public String getTipo() {
        return "SUITE";
    }

    // suíte cobra 20% de taxa sobre a diária
    @Override
    public double calcularDiaria() {
        return valorDiaria * 1.20;
    }

    // suíte dá 10% de desconto para estadias de 7 dias ou mais
    @Override
    public double calcularValorHospedagem(int dias) {
        double valor = super.calcularValorHospedagem(dias);
        if (dias >= 7) {
            valor = valor * 0.90;
        }
        return valor;
    }
}
