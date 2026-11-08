package view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import controller.HospedeController;
import controller.HotelController;
import controller.ReservaController;
import controller.ServicoController;
import exception.NegocioException;
import model.Hospede;
import model.Hotel;
import model.Quarto;
import model.Reserva;
import model.Servico;
import model.ServicoConsumido;
import util.Conversor;

public class TelaReservas extends JFrame {

    private ReservaController reservaController;
    private HospedeController hospedeController;
    private HotelController hotelController;
    private ServicoController servicoController;
    private List<Reserva> reservas = new ArrayList<>();

    private JComboBox<Hospede> cbHospede;
    private JComboBox<Hotel> cbHotel;
    private JTextField txtEntrada;
    private JTextField txtSaida;
    private JTextField txtQuantidade;
    private JComboBox<Quarto> cbQuarto;
    private JComboBox<Servico> cbServico;
    private JTextField txtQtdServico;

    private JTable tabela;
    private DefaultTableModel modelo;

    public TelaReservas(ReservaController reservaController, HospedeController hospedeController,
            HotelController hotelController, ServicoController servicoController) {
        this.reservaController = reservaController;
        this.hospedeController = hospedeController;
        this.hotelController = hotelController;
        this.servicoController = servicoController;
        configurarJanela();
        criarComponentes();
        carregarCombos();
        carregarTabela();
    }

    private void configurarJanela() {
        setTitle("Reservas");
        setSize(1000, 650);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    private void criarComponentes() {
        // formulário da nova reserva
        JPanel painelFormulario = new JPanel(new GridLayout(6, 2, 10, 10));
        cbHospede = new JComboBox<>();
        cbHotel = new JComboBox<>();
        txtEntrada = new JTextField();
        txtSaida = new JTextField();
        txtQuantidade = new JTextField();
        cbQuarto = new JComboBox<>();

        painelFormulario.add(new JLabel("Hóspede:"));
        painelFormulario.add(cbHospede);
        painelFormulario.add(new JLabel("Hotel:"));
        painelFormulario.add(cbHotel);
        painelFormulario.add(new JLabel("Data de entrada (dd/mm/aaaa):"));
        painelFormulario.add(txtEntrada);
        painelFormulario.add(new JLabel("Data de saída (dd/mm/aaaa):"));
        painelFormulario.add(txtSaida);
        painelFormulario.add(new JLabel("Quantidade de hóspedes:"));
        painelFormulario.add(txtQuantidade);
        painelFormulario.add(new JLabel("Quarto disponível:"));
        painelFormulario.add(cbQuarto);

        JButton btnBuscar = new JButton("Buscar quartos disponíveis");
        JButton btnReservar = new JButton("Reservar");
        JButton btnAlterar = new JButton("Alterar datas da selecionada");
        JButton btnLimpar = new JButton("Limpar");
        btnBuscar.addActionListener(e -> buscarQuartos());
        btnReservar.addActionListener(e -> reservar());
        btnAlterar.addActionListener(e -> alterarDatas());
        btnLimpar.addActionListener(e -> limparCampos());

        JPanel painelBotoes = new JPanel(new FlowLayout());
        painelBotoes.add(btnBuscar);
        painelBotoes.add(btnReservar);
        painelBotoes.add(btnAlterar);
        painelBotoes.add(btnLimpar);

        JPanel painelTopo = new JPanel(new BorderLayout());
        painelTopo.add(painelFormulario, BorderLayout.CENTER);
        painelTopo.add(painelBotoes, BorderLayout.SOUTH);

        String[] colunas = {"ID", "Hóspede", "Hotel", "Quarto", "Entrada", "Saída", "Diárias", "Status", "Valor"};
        modelo = new DefaultTableModel(colunas, 0);
        tabela = new JTable(modelo);
        tabela.getSelectionModel().addListSelectionListener(e -> preencherDatas());

        // ações sobre a reserva selecionada na tabela
        JButton btnConfirmar = new JButton("Confirmar");
        JButton btnCheckIn = new JButton("Check-in");
        JButton btnCheckOut = new JButton("Check-out");
        JButton btnCancelar = new JButton("Cancelar");
        JButton btnExcluir = new JButton("Excluir");
        JButton btnVerServicos = new JButton("Ver serviços");
        btnConfirmar.addActionListener(e -> confirmar());
        btnCheckIn.addActionListener(e -> checkIn());
        btnCheckOut.addActionListener(e -> checkOut());
        btnCancelar.addActionListener(e -> cancelar());
        btnExcluir.addActionListener(e -> excluir());
        btnVerServicos.addActionListener(e -> verServicos());

        JPanel painelAcoes = new JPanel(new FlowLayout());
        painelAcoes.add(btnConfirmar);
        painelAcoes.add(btnCheckIn);
        painelAcoes.add(btnCheckOut);
        painelAcoes.add(btnCancelar);
        painelAcoes.add(btnExcluir);
        painelAcoes.add(btnVerServicos);

        // lançamento de serviço na reserva selecionada
        cbServico = new JComboBox<>();
        txtQtdServico = new JTextField("1", 4);
        JButton btnAdicionarServico = new JButton("Adicionar serviço");
        btnAdicionarServico.addActionListener(e -> adicionarServico());

        JPanel painelServico = new JPanel(new FlowLayout());
        painelServico.add(new JLabel("Serviço:"));
        painelServico.add(cbServico);
        painelServico.add(new JLabel("Qtd:"));
        painelServico.add(txtQtdServico);
        painelServico.add(btnAdicionarServico);

        JPanel painelRodape = new JPanel(new GridLayout(2, 1));
        painelRodape.add(painelAcoes);
        painelRodape.add(painelServico);

        add(painelTopo, BorderLayout.NORTH);
        add(new JScrollPane(tabela), BorderLayout.CENTER);
        add(painelRodape, BorderLayout.SOUTH);
    }

    private void carregarCombos() {
        try {
            for (Hospede h : hospedeController.listar()) {
                cbHospede.addItem(h);
            }
            for (Hotel h : hotelController.listar()) {
                cbHotel.addItem(h);
            }
            for (Servico s : servicoController.listar()) {
                cbServico.addItem(s);
            }
        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void carregarTabela() {
        try {
            reservas = reservaController.listar();
            modelo.setRowCount(0);
            for (Reserva r : reservas) {
                modelo.addRow(new Object[] {r.getId(), r.getHospede().getNome(), r.getQuarto().getHotel().getNome(),
                        r.getQuarto().toString(), Conversor.formatarData(r.getDataEntrada()), Conversor.formatarData(r.getDataSaida()),
                        r.calcularDiarias(), r.getStatus(), String.format("R$ %.2f", r.getValorTotal())});
            }
        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ao selecionar uma reserva, as datas dela vão para os campos (para alterar)
    private void preencherDatas() {
        int linha = tabela.getSelectedRow();
        if (linha < 0) {
            return;
        }
        Reserva r = reservas.get(linha);
        txtEntrada.setText(Conversor.formatarData(r.getDataEntrada()));
        txtSaida.setText(Conversor.formatarData(r.getDataSaida()));
    }

    // retorna a reserva selecionada ou null se nenhuma estiver selecionada
    private Reserva reservaSelecionada() {
        int linha = tabela.getSelectedRow();
        if (linha < 0) {
            JOptionPane.showMessageDialog(this, "Selecione uma reserva na tabela.");
            return null;
        }
        return reservas.get(linha);
    }

    private void buscarQuartos() {
        try {
            List<Quarto> disponiveis = reservaController.buscarQuartosDisponiveis((Hotel) cbHotel.getSelectedItem(),
                    txtEntrada.getText(), txtSaida.getText(), txtQuantidade.getText());
            cbQuarto.removeAllItems();
            for (Quarto q : disponiveis) {
                cbQuarto.addItem(q);
            }
            if (disponiveis.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Nenhum quarto disponível para esse período.");
            }
        } catch (NegocioException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Aviso", JOptionPane.WARNING_MESSAGE);
        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void reservar() {
        try {
            reservaController.reservar((Hospede) cbHospede.getSelectedItem(), (Quarto) cbQuarto.getSelectedItem(),
                    txtEntrada.getText(), txtSaida.getText(), txtQuantidade.getText());
            JOptionPane.showMessageDialog(this, "Reserva criada com status PENDENTE!");
            limparCampos();
            carregarTabela();
        } catch (NegocioException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Aviso", JOptionPane.WARNING_MESSAGE);
        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void alterarDatas() {
        Reserva r = reservaSelecionada();
        if (r == null) {
            return;
        }
        try {
            reservaController.alterarDatas(r.getId(), txtEntrada.getText(), txtSaida.getText());
            JOptionPane.showMessageDialog(this, "Datas alteradas e valor recalculado!");
            carregarTabela();
        } catch (NegocioException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Aviso", JOptionPane.WARNING_MESSAGE);
        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void confirmar() {
        Reserva r = reservaSelecionada();
        if (r == null) {
            return;
        }
        try {
            reservaController.confirmar(r.getId());
            JOptionPane.showMessageDialog(this, "Reserva confirmada!");
            carregarTabela();
        } catch (NegocioException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Aviso", JOptionPane.WARNING_MESSAGE);
        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void checkIn() {
        Reserva r = reservaSelecionada();
        if (r == null) {
            return;
        }
        try {
            reservaController.checkIn(r.getId());
            JOptionPane.showMessageDialog(this, "Check-in realizado! O quarto agora está OCUPADO.");
            carregarTabela();
        } catch (NegocioException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Aviso", JOptionPane.WARNING_MESSAGE);
        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void adicionarServico() {
        Reserva r = reservaSelecionada();
        if (r == null) {
            return;
        }
        try {
            reservaController.adicionarServico(r.getId(), (Servico) cbServico.getSelectedItem(), txtQtdServico.getText());
            JOptionPane.showMessageDialog(this, "Serviço lançado na reserva!");
            carregarTabela();
        } catch (NegocioException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Aviso", JOptionPane.WARNING_MESSAGE);
        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    // antes do check-out mostra os serviços usados e o total para conferência
    private void checkOut() {
        Reserva r = reservaSelecionada();
        if (r == null) {
            return;
        }
        try {
            Reserva reserva = reservaController.buscar(r.getId());
            if (!reserva.getStatus().equals(Reserva.EM_ANDAMENTO)) {
                throw new NegocioException("O check-out só pode ser feito em reservas com check-in.");
            }
            String resumo = "Hóspede: " + reserva.getHospede().getNome() + "\n";
            resumo += "Diárias: " + reserva.calcularDiarias() + " = " + String.format("R$ %.2f", reserva.calcularValorDiarias()) + "\n\n";
            resumo += "Serviços utilizados:\n";
            if (reserva.getServicos().isEmpty()) {
                resumo += "Nenhum serviço utilizado.\n";
            }
            for (ServicoConsumido sc : reserva.getServicos()) {
                resumo += "- " + sc.getServico().getNome() + " x" + sc.getQuantidade() + " = "
                        + String.format("R$ %.2f", sc.calcularSubtotal()) + "\n";
            }
            resumo += "\nTOTAL: " + String.format("R$ %.2f", reserva.calcularValorTotal()) + "\n\nConfirmar check-out?";

            int resposta = JOptionPane.showConfirmDialog(this, resumo, "Check-out", JOptionPane.YES_NO_OPTION);
            if (resposta == JOptionPane.YES_OPTION) {
                reservaController.checkOut(reserva.getId());
                JOptionPane.showMessageDialog(this, "Check-out realizado! Reserva finalizada e quarto liberado.");
                carregarTabela();
            }
        } catch (NegocioException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Aviso", JOptionPane.WARNING_MESSAGE);
        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cancelar() {
        Reserva r = reservaSelecionada();
        if (r == null) {
            return;
        }
        try {
            reservaController.cancelar(r.getId());
            JOptionPane.showMessageDialog(this, "Reserva cancelada!");
            carregarTabela();
        } catch (NegocioException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Aviso", JOptionPane.WARNING_MESSAGE);
        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void excluir() {
        Reserva r = reservaSelecionada();
        if (r == null) {
            return;
        }
        int resposta = JOptionPane.showConfirmDialog(this, "Deseja excluir esta reserva?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (resposta != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            reservaController.excluir(r.getId());
            JOptionPane.showMessageDialog(this, "Reserva excluída!");
            carregarTabela();
        } catch (NegocioException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Aviso", JOptionPane.WARNING_MESSAGE);
        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void verServicos() {
        Reserva r = reservaSelecionada();
        if (r == null) {
            return;
        }
        List<Object[]> linhas = new ArrayList<>();
        for (ServicoConsumido sc : r.getServicos()) {
            linhas.add(new Object[] {sc.getServico().getNome(), sc.getQuantidade(), String.format("R$ %.2f", sc.getValorUnitario()),
                    String.format("R$ %.2f", sc.calcularSubtotal()), Conversor.formatarData(sc.getData())});
        }
        String[] colunas = {"Serviço", "Quantidade", "Valor unitário", "Subtotal", "Data"};
        new TelaListagem("Serviços da reserva " + r.getId(), colunas, linhas).setVisible(true);
    }

    private void limparCampos() {
        txtEntrada.setText("");
        txtSaida.setText("");
        txtQuantidade.setText("");
        cbQuarto.removeAllItems();
        tabela.clearSelection();
    }
}
