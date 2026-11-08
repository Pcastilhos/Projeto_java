package view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import controller.ServicoController;
import exception.NegocioException;
import model.Servico;

public class TelaServicos extends JFrame {

    private ServicoController servicoController;
    private List<Servico> servicos = new ArrayList<>();
    private int idSelecionado = 0;

    private JTextField txtNome;
    private JTextField txtDescricao;
    private JTextField txtValor;

    private JTable tabela;
    private DefaultTableModel modelo;

    public TelaServicos(ServicoController servicoController) {
        this.servicoController = servicoController;
        configurarJanela();
        criarComponentes();
        carregarTabela();
    }

    private void configurarJanela() {
        setTitle("Serviços");
        setSize(700, 450);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    private void criarComponentes() {
        JPanel painelFormulario = new JPanel(new GridLayout(3, 2, 10, 10));
        txtNome = new JTextField();
        txtDescricao = new JTextField();
        txtValor = new JTextField();

        painelFormulario.add(new JLabel("Nome:"));
        painelFormulario.add(txtNome);
        painelFormulario.add(new JLabel("Descrição:"));
        painelFormulario.add(txtDescricao);
        painelFormulario.add(new JLabel("Valor:"));
        painelFormulario.add(txtValor);

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

        String[] colunas = {"ID", "Nome", "Descrição", "Valor"};
        modelo = new DefaultTableModel(colunas, 0);
        tabela = new JTable(modelo);
        tabela.getSelectionModel().addListSelectionListener(e -> preencherCampos());

        add(painelTopo, BorderLayout.NORTH);
        add(new JScrollPane(tabela), BorderLayout.CENTER);
    }

    private void carregarTabela() {
        try {
            servicos = servicoController.listar();
            modelo.setRowCount(0);
            for (Servico s : servicos) {
                modelo.addRow(new Object[] {s.getId(), s.getNome(), s.getDescricao(), String.format("R$ %.2f", s.getValor())});
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
        Servico s = servicos.get(linha);
        idSelecionado = s.getId();
        txtNome.setText(s.getNome());
        txtDescricao.setText(s.getDescricao());
        txtValor.setText(String.valueOf(s.getValor()));
    }

    private void cadastrar() {
        try {
            servicoController.cadastrar(txtNome.getText(), txtDescricao.getText(), txtValor.getText());
            JOptionPane.showMessageDialog(this, "Serviço cadastrado com sucesso!");
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
            JOptionPane.showMessageDialog(this, "Selecione um serviço na tabela.");
            return;
        }
        try {
            servicoController.atualizar(idSelecionado, txtNome.getText(), txtDescricao.getText(), txtValor.getText());
            JOptionPane.showMessageDialog(this, "Serviço atualizado com sucesso!");
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
            JOptionPane.showMessageDialog(this, "Selecione um serviço na tabela.");
            return;
        }
        int resposta = JOptionPane.showConfirmDialog(this, "Deseja excluir este serviço?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (resposta != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            servicoController.excluir(idSelecionado);
            JOptionPane.showMessageDialog(this, "Serviço excluído com sucesso!");
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
        txtNome.setText("");
        txtDescricao.setText("");
        txtValor.setText("");
        tabela.clearSelection();
    }
}
