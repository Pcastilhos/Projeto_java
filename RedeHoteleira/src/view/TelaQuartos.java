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

import controller.HotelController;
import controller.QuartoController;
import exception.NegocioException;
import model.Hotel;
import model.Quarto;

public class TelaQuartos extends JFrame {

    private QuartoController quartoController;
    private HotelController hotelController;
    private List<Quarto> quartos = new ArrayList<>();
    private int idSelecionado = 0;

    private JComboBox<Hotel> cbHotel;
    private JTextField txtNumero;
    private JComboBox<String> cbTipo;
    private JTextField txtCapacidade;
    private JTextField txtValorDiaria;
    private JComboBox<String> cbSituacao;

    private JTable tabela;
    private DefaultTableModel modelo;

    public TelaQuartos(QuartoController quartoController, HotelController hotelController) {
        this.quartoController = quartoController;
        this.hotelController = hotelController;
        configurarJanela();
        criarComponentes();
        carregarHoteis();
        carregarTabela();
    }

    private void configurarJanela() {
        setTitle("Quartos");
        setSize(850, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    private void criarComponentes() {
        JPanel painelFormulario = new JPanel(new GridLayout(6, 2, 10, 10));
        cbHotel = new JComboBox<>();
        txtNumero = new JTextField();
        cbTipo = new JComboBox<>();
        cbTipo.addItem("STANDARD");
        cbTipo.addItem("LUXO");
        cbTipo.addItem("SUITE");
        txtCapacidade = new JTextField();
        txtValorDiaria = new JTextField();
        cbSituacao = new JComboBox<>();
        cbSituacao.addItem(Quarto.DISPONIVEL);
        cbSituacao.addItem(Quarto.OCUPADO);
        cbSituacao.addItem(Quarto.MANUTENCAO);

        painelFormulario.add(new JLabel("Hotel:"));
        painelFormulario.add(cbHotel);
        painelFormulario.add(new JLabel("Número:"));
        painelFormulario.add(txtNumero);
        painelFormulario.add(new JLabel("Tipo:"));
        painelFormulario.add(cbTipo);
        painelFormulario.add(new JLabel("Capacidade:"));
        painelFormulario.add(txtCapacidade);
        painelFormulario.add(new JLabel("Valor da diária (base):"));
        painelFormulario.add(txtValorDiaria);
        painelFormulario.add(new JLabel("Situação:"));
        painelFormulario.add(cbSituacao);

        JButton btnCadastrar = new JButton("Cadastrar");
        JButton btnAtualizar = new JButton("Atualizar");
        JButton btnExcluir = new JButton("Excluir");
        JButton btnLimpar = new JButton("Limpar");
        btnCadastrar.addActionListener(e -> cadastrar());
        btnAtualizar.addActionListener(e -> atualizar());
        btnExcluir.addActionListener(e -> excluir());
        btnLimpar.addActionListener(e -> limparCampos());

        JPanel painelBotoes = new JPanel(new FlowLayout());
        painelBotoes.add(btnCadastrar);
        painelBotoes.add(btnAtualizar);
        painelBotoes.add(btnExcluir);
        painelBotoes.add(btnLimpar);

        JPanel painelTopo = new JPanel(new BorderLayout());
        painelTopo.add(painelFormulario, BorderLayout.CENTER);
        painelTopo.add(painelBotoes, BorderLayout.SOUTH);

        // a coluna "Diária final" mostra o polimorfismo: cada tipo calcula diferente
        String[] colunas = {"ID", "Hotel", "Número", "Tipo", "Capacidade", "Diária base", "Diária final", "Situação"};
        modelo = new DefaultTableModel(colunas, 0);
        tabela = new JTable(modelo);
        tabela.getSelectionModel().addListSelectionListener(e -> preencherCampos());

        add(painelTopo, BorderLayout.NORTH);
        add(new JScrollPane(tabela), BorderLayout.CENTER);
    }

    private void carregarHoteis() {
        try {
            for (Hotel h : hotelController.listar()) {
                cbHotel.addItem(h);
            }
        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void carregarTabela() {
        try {
            quartos = quartoController.listar();
            modelo.setRowCount(0);
            for (Quarto q : quartos) {
                modelo.addRow(new Object[] {q.getId(), q.getHotel().getNome(), q.getNumero(), q.getTipo(), q.getCapacidade(),
                        String.format("R$ %.2f", q.getValorDiaria()), String.format("R$ %.2f", q.calcularDiaria()), q.getSituacao()});
            }
        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void preencherCampos() {
        int linha = tabela.getSelectedRow();
        if (linha < 0) {
            return;
        }
        Quarto q = quartos.get(linha);
        idSelecionado = q.getId();
        // seleciona no combo o hotel do quarto
        for (int i = 0; i < cbHotel.getItemCount(); i++) {
            if (cbHotel.getItemAt(i).getId() == q.getHotel().getId()) {
                cbHotel.setSelectedIndex(i);
            }
        }
        txtNumero.setText(String.valueOf(q.getNumero()));
        cbTipo.setSelectedItem(q.getTipo());
        txtCapacidade.setText(String.valueOf(q.getCapacidade()));
        txtValorDiaria.setText(String.valueOf(q.getValorDiaria()));
        cbSituacao.setSelectedItem(q.getSituacao());
    }

    private void cadastrar() {
        try {
            quartoController.cadastrar((Hotel) cbHotel.getSelectedItem(), txtNumero.getText(), (String) cbTipo.getSelectedItem(),
                    txtCapacidade.getText(), txtValorDiaria.getText(), (String) cbSituacao.getSelectedItem());
            JOptionPane.showMessageDialog(this, "Quarto cadastrado com sucesso!");
            limparCampos();
            carregarTabela();
        } catch (NegocioException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Aviso", JOptionPane.WARNING_MESSAGE);
        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void atualizar() {
        if (idSelecionado == 0) {
            JOptionPane.showMessageDialog(this, "Selecione um quarto na tabela.");
            return;
        }
        try {
            quartoController.atualizar(idSelecionado, (Hotel) cbHotel.getSelectedItem(), txtNumero.getText(), (String) cbTipo.getSelectedItem(),
                    txtCapacidade.getText(), txtValorDiaria.getText(), (String) cbSituacao.getSelectedItem());
            JOptionPane.showMessageDialog(this, "Quarto atualizado com sucesso!");
            limparCampos();
            carregarTabela();
        } catch (NegocioException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Aviso", JOptionPane.WARNING_MESSAGE);
        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void excluir() {
        if (idSelecionado == 0) {
            JOptionPane.showMessageDialog(this, "Selecione um quarto na tabela.");
            return;
        }
        int resposta = JOptionPane.showConfirmDialog(this, "Deseja excluir este quarto?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (resposta != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            quartoController.excluir(idSelecionado);
            JOptionPane.showMessageDialog(this, "Quarto excluído com sucesso!");
            limparCampos();
            carregarTabela();
        } catch (NegocioException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Aviso", JOptionPane.WARNING_MESSAGE);
        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limparCampos() {
        idSelecionado = 0;
        txtNumero.setText("");
        txtCapacidade.setText("");
        txtValorDiaria.setText("");
        cbTipo.setSelectedIndex(0);
        cbSituacao.setSelectedIndex(0);
        tabela.clearSelection();
    }
}
