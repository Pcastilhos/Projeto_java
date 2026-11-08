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
import exception.NegocioException;
import model.Funcionario;
import model.Hotel;
import model.Quarto;

public class TelaHoteis extends JFrame {

    private HotelController hotelController;
    private List<Hotel> hoteis = new ArrayList<>();
    // id do hotel selecionado na tabela (0 = nenhum)
    private int idSelecionado = 0;

    private JTextField txtNome;
    private JTextField txtEndereco;
    private JTextField txtCidade;
    private JTextField txtTelefone;
    private JComboBox<Integer> cbEstrelas;

    private JTable tabela;
    private DefaultTableModel modelo;

    public TelaHoteis(HotelController hotelController) {
        this.hotelController = hotelController;
        configurarJanela();
        criarComponentes();
        carregarTabela();
    }

    private void configurarJanela() {
        setTitle("Hotéis");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    private void criarComponentes() {
        JPanel painelFormulario = new JPanel(new GridLayout(5, 2, 10, 10));
        txtNome = new JTextField();
        txtEndereco = new JTextField();
        txtCidade = new JTextField();
        txtTelefone = new JTextField();
        cbEstrelas = new JComboBox<>();
        for (int i = 1; i <= 5; i++) {
            cbEstrelas.addItem(i);
        }

        painelFormulario.add(new JLabel("Nome:"));
        painelFormulario.add(txtNome);
        painelFormulario.add(new JLabel("Endereço:"));
        painelFormulario.add(txtEndereco);
        painelFormulario.add(new JLabel("Cidade:"));
        painelFormulario.add(txtCidade);
        painelFormulario.add(new JLabel("Telefone:"));
        painelFormulario.add(txtTelefone);
        painelFormulario.add(new JLabel("Estrelas:"));
        painelFormulario.add(cbEstrelas);

        JButton btnCadastrar = new JButton("Cadastrar");
        JButton btnAtualizar = new JButton("Atualizar");
        JButton btnExcluir = new JButton("Excluir");
        JButton btnLimpar = new JButton("Limpar");
        JButton btnQuartos = new JButton("Ver quartos");
        JButton btnFuncionarios = new JButton("Ver funcionários");

        btnCadastrar.addActionListener(e -> cadastrar());
        btnAtualizar.addActionListener(e -> atualizar());
        btnExcluir.addActionListener(e -> excluir());
        btnLimpar.addActionListener(e -> limparCampos());
        btnQuartos.addActionListener(e -> verQuartos());
        btnFuncionarios.addActionListener(e -> verFuncionarios());

        JPanel painelBotoes = new JPanel(new FlowLayout());
        painelBotoes.add(btnCadastrar);
        painelBotoes.add(btnAtualizar);
        painelBotoes.add(btnExcluir);
        painelBotoes.add(btnLimpar);
        painelBotoes.add(btnQuartos);
        painelBotoes.add(btnFuncionarios);

        JPanel painelTopo = new JPanel(new BorderLayout());
        painelTopo.add(painelFormulario, BorderLayout.CENTER);
        painelTopo.add(painelBotoes, BorderLayout.SOUTH);

        String[] colunas = {"ID", "Nome", "Endereço", "Cidade", "Telefone", "Estrelas"};
        modelo = new DefaultTableModel(colunas, 0);
        tabela = new JTable(modelo);
        // ao clicar em uma linha, os dados vão para o formulário
        tabela.getSelectionModel().addListSelectionListener(e -> preencherCampos());

        add(painelTopo, BorderLayout.NORTH);
        add(new JScrollPane(tabela), BorderLayout.CENTER);
    }

    private void carregarTabela() {
        try {
            hoteis = hotelController.listar();
            modelo.setRowCount(0);
            for (Hotel h : hoteis) {
                modelo.addRow(new Object[] {h.getId(), h.getNome(), h.getEndereco(), h.getCidade(), h.getTelefone(), h.getEstrelas()});
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
        Hotel h = hoteis.get(linha);
        idSelecionado = h.getId();
        txtNome.setText(h.getNome());
        txtEndereco.setText(h.getEndereco());
        txtCidade.setText(h.getCidade());
        txtTelefone.setText(h.getTelefone());
        cbEstrelas.setSelectedItem(h.getEstrelas());
    }

    private void cadastrar() {
        try {
            hotelController.cadastrar(txtNome.getText(), txtEndereco.getText(), txtCidade.getText(),
                    txtTelefone.getText(), (Integer) cbEstrelas.getSelectedItem());
            JOptionPane.showMessageDialog(this, "Hotel cadastrado com sucesso!");
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
            JOptionPane.showMessageDialog(this, "Selecione um hotel na tabela.");
            return;
        }
        try {
            hotelController.atualizar(idSelecionado, txtNome.getText(), txtEndereco.getText(), txtCidade.getText(),
                    txtTelefone.getText(), (Integer) cbEstrelas.getSelectedItem());
            JOptionPane.showMessageDialog(this, "Hotel atualizado com sucesso!");
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
            JOptionPane.showMessageDialog(this, "Selecione um hotel na tabela.");
            return;
        }
        int resposta = JOptionPane.showConfirmDialog(this, "Deseja excluir este hotel?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (resposta != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            hotelController.excluir(idSelecionado);
            JOptionPane.showMessageDialog(this, "Hotel excluído com sucesso!");
            limparCampos();
            carregarTabela();
        } catch (NegocioException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Aviso", JOptionPane.WARNING_MESSAGE);
        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void verQuartos() {
        if (idSelecionado == 0) {
            JOptionPane.showMessageDialog(this, "Selecione um hotel na tabela.");
            return;
        }
        try {
            List<Object[]> linhas = new ArrayList<>();
            for (Quarto q : hotelController.listarQuartos(idSelecionado)) {
                linhas.add(new Object[] {q.getNumero(), q.getTipo(), q.getCapacidade(),
                        String.format("R$ %.2f", q.calcularDiaria()), q.getSituacao()});
            }
            String[] colunas = {"Número", "Tipo", "Capacidade", "Diária", "Situação"};
            new TelaListagem("Quartos do hotel " + txtNome.getText(), colunas, linhas).setVisible(true);
        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void verFuncionarios() {
        if (idSelecionado == 0) {
            JOptionPane.showMessageDialog(this, "Selecione um hotel na tabela.");
            return;
        }
        try {
            List<Object[]> linhas = new ArrayList<>();
            for (Funcionario f : hotelController.listarFuncionarios(idSelecionado)) {
                linhas.add(new Object[] {f.getNome(), f.getCargo(), f.getHorarioEntrada() + " às " + f.getHorarioSaida(),
                        String.format("R$ %.2f", f.calcularSalario())});
            }
            String[] colunas = {"Nome", "Cargo", "Horário", "Salário"};
            new TelaListagem("Funcionários do hotel " + txtNome.getText(), colunas, linhas).setVisible(true);
        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limparCampos() {
        idSelecionado = 0;
        txtNome.setText("");
        txtEndereco.setText("");
        txtCidade.setText("");
        txtTelefone.setText("");
        cbEstrelas.setSelectedIndex(0);
        tabela.clearSelection();
        txtNome.requestFocus();
    }
}
