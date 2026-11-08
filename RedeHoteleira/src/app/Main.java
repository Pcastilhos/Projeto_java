package app;

import controller.FuncionarioController;
import controller.HospedeController;
import controller.HotelController;
import controller.QuartoController;
import controller.ReservaController;
import controller.ServicoController;
import view.TelaPrincipal;

public class Main {

    public static void main(String[] args) {

        HotelController hotelController = new HotelController();
        QuartoController quartoController = new QuartoController();
        HospedeController hospedeController = new HospedeController();
        FuncionarioController funcionarioController = new FuncionarioController();
        ServicoController servicoController = new ServicoController();
        ReservaController reservaController = new ReservaController();

        TelaPrincipal telaPrincipal = new TelaPrincipal(hotelController, quartoController, hospedeController,
                funcionarioController, servicoController, reservaController);
        telaPrincipal.setVisible(true);
    }
}
