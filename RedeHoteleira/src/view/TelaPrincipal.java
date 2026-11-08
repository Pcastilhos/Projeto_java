package view;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import controller.FuncionarioController;
import controller.HospedeController;
import controller.HotelController;
import controller.QuartoController;
import controller.ReservaController;
import controller.ServicoController;

public class TelaPrincipal extends JFrame {

    private HotelController hotelController;
    private QuartoController quartoController;
    private HospedeController hospedeController;
    private FuncionarioController funcionarioController;
    private ServicoController servicoController;
    private ReservaController reservaController;

    public TelaPrincipal(HotelController hotelController, QuartoController quartoController, HospedeController hospedeController,
            FuncionarioController funcionarioController, ServicoController servicoController, ReservaController reservaController) {
        this.hotelController = hotelController;
        this.quartoController = quartoController;
        this.hospedeController = hospedeController;
        this.funcionarioController = funcionarioController;
        this.servicoController = servicoController;
        this.reservaController = reservaController;

        configurarJanela();
        criarComponentes();
    }

    private void configurarJanela() {
        setTitle("Rede Hoteleira");
        setSize(500, 350);
        // fechar a tela principal encerra a aplicação
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    private void criarComponentes() {
        JLabel titulo = new JLabel("Sistema de Gerenciamento da Rede Hoteleira", SwingConstants.CENTER);

        JButton btnHoteis = new JButton("Hotéis");
        JButton btnQuartos = new JButton("Quartos");
        JButton btnHospedes = new JButton("Hóspedes");
        JButton btnFuncionarios = new JButton("Funcionários");
        JButton btnServicos = new JButton("Serviços");
        JButton btnReservas = new JButton("Reservas");

        // grid de 3 linhas e 2 colunas com os botões do menu
        JPanel painel = new JPanel(new GridLayout(3, 2, 10, 10));
        painel.add(btnHoteis);
        painel.add(btnQuartos);
        painel.add(btnHospedes);
        painel.add(btnFuncionarios);
        painel.add(btnServicos);
        painel.add(btnReservas);

        btnHoteis.addActionListener(e -> new TelaHoteis(hotelController).setVisible(true));
        btnQuartos.addActionListener(e -> new TelaQuartos(quartoController, hotelController).setVisible(true));
        btnHospedes.addActionListener(e -> new TelaHospedes(hospedeController).setVisible(true));
        btnFuncionarios.addActionListener(e -> new TelaFuncionarios(funcionarioController, hotelController).setVisible(true));
        btnServicos.addActionListener(e -> new TelaServicos(servicoController).setVisible(true));
        btnReservas.addActionListener(e -> new TelaReservas(reservaController, hospedeController, hotelController, servicoController).setVisible(true));

        add(titulo, BorderLayout.NORTH);
        add(painel, BorderLayout.CENTER);
    }
}
