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

import controller.HospedeController;
import exception.NegocioException;
import model.Hospede;
import model.Reserva;
import model.ServicoConsumido;
import util.Conversor;

public class TelaHospedes extends JFrame {

    private HospedeController hospedeController;
    private List<Hospede> hospedes = new ArrayList<>();
    private int idSelecionado = 0;

    private JTextField txtNome;
    private JTextField txtCpf;
    private JTextField txtNascimento;
    private JTextField txtTelefone;
    private JTextField txtEmail;
    private JTextField txtPesquisa;

    private JTable tabela;
    private DefaultTableModel modelo;

    public TelaHospedes(HospedeController hospedeController) {
        this.hospedeController = hospedeController;
        configurarJanela();
        criarComponentes();
        mostrarNaTabela(listarComTratamento());
    }

    private void configurarJanela() {
        setTitle("Hóspedes");
        setSize(850, 550);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    private void criarComponentes() {
        JPanel painelFormulario = new JPanel(new GridLayout(5, 2, 10, 10));
        txtNome = new JTextField();
        txtCpf = new JTextField();
        txtNascimento = new JTextField();
        txtTelefone = new JTextField();
        txtEmail = new JTextField();

        painelFormulario.add(new JLabel("Nome:"));
        painelFormulario.add(txtNome);
        painelFormulario.add(new JLabel("CPF (somente números):"));
        painelFormulario.add(txtCpf);
        painelFormulario.add(new JLabel("Data de nascimento (dd/mm/aaaa):"));
        painelFormulario.add(txtNascimento);
        painelFormulario.add(new JLabel("Telefone:"));
        painelFormulario.add(txtTelefone);
        painelFormulario.add(new JLabel("E-mail:"));
        painelFormulario.add(txtEmail);

        JButton btnCadastrar = new JButton("Cadastrar");
        JButton btnAtualizar = new JButton("Atualizar");
        JButton btnExcluir = new JButton("Excluir");
        JButton btnLimpar = new JButton("Limpar");
        JButton btnHistorico = new JButton("Histórico de hospedagens");
        btnCadastrar.addActionListener(e -> cadastrar());
        btnAtualizar.addActionListener(e -> atualizar());
        btnExcluir.addActionListener(e -> excluir());
        btnLimpar.addActionListener(e -> limparCampos());
        btnHistorico.addActionListener(e -> verHistorico());

        JPanel painelBotoes = new JPanel(new FlowLayout());
        painelBotoes.add(btnCadastrar);
        painelBotoes.add(btnAtualizar);
        painelBotoes.add(btnExcluir);
        painelBotoes.add(btnLimpar);
        painelBotoes.add(btnHistorico);

        JPanel painelTopo = new JPanel(new BorderLayout());
        painelTopo.add(painelFormulario, BorderLayout.CENTER);
        painelTopo.add(painelBotoes, BorderLayout.SOUTH);

        // pesquisa por nome ou CPF
        txtPesquisa = new JTextField(20);
        JButton btnPesquisar = new JButton("Pesquisar");
        JButton btnTodos = new JButton("Mostrar todos");
        btnPesquisar.addActionListener(e -> pesquisar());
        btnTodos.addActionListener(e -> {
            txtPesquisa.setText("");
            mostrarNaTabela(listarComTratamento());
        });
        JPanel painelPesquisa = new JPanel(new FlowLayout());
        painelPesquisa.add(new JLabel("Nome ou CPF:"));
        painelPesquisa.add(txtPesquisa);
        painelPesquisa.add(btnPesquisar);
        painelPesquisa.add(btnTodos);

        String[] colunas = {"ID", "Nome", "CPF", "Nascimento", "Telefone", "E-mail"};
        modelo = new DefaultTableModel(colunas, 0);
        tabela = new JTable(modelo);
        tabela.getSelectionModel().addListSelectionListener(e -> preencherCampos());

        JPanel painelCentro = new JPanel(new BorderLayout());
        painelCentro.add(painelPesquisa, BorderLayout.NORTH);
        painelCentro.add(new JScrollPane(tabela), BorderLayout.CENTER);

        add(painelTopo, BorderLayout.NORTH);
        add(painelCentro, BorderLayout.CENTER);
    }

    private List<Hospede> listarComTratamento() {
        try {
            return hospedeController.listar();
        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            return new ArrayList<>();
        }
    }

    private void mostrarNaTabela(List<Hospede> lista) {
        hospedes = lista;
        modelo.setRowCount(0);
        for (Hospede h : hospedes) {
            modelo.addRow(new Object[] {h.getId(), h.getNome(), h.getCpf(), Conversor.formatarData(h.getDataNascimento()),
                    h.getTelefone(), h.getEmail()});
        }
    }

    private void pesquisar() {
        try {
            mostrarNaTabela(hospedeController.pesquisar(txtPesquisa.getText()));
        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void preencherCampos() {
        int linha = tabela.getSelectedRow();
        if (linha < 0) {
            return;
        }
        Hospede h = hospedes.get(linha);
        idSelecionado = h.getId();
        txtNome.setText(h.getNome());
        txtCpf.setText(h.getCpf());
        txtNascimento.setText(Conversor.formatarData(h.getDataNascimento()));
        txtTelefone.setText(h.getTelefone());
        txtEmail.setText(h.getEmail());
    }

    private void cadastrar() {
        try {
            hospedeController.cadastrar(txtNome.getText(), txtCpf.getText(), txtNascimento.getText(), txtTelefone.getText(), txtEmail.getText());
            JOptionPane.showMessageDialog(this, "Hóspede cadastrado com sucesso!");
            limparCampos();
            mostrarNaTabela(listarComTratamento());
        } catch (NegocioException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Aviso", JOptionPane.WARNING_MESSAGE);
        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void atualizar() {
        if (idSelecionado == 0) {
            JOptionPane.showMessageDialog(this, "Selecione um hóspede na tabela.");
            return;
        }
        try {
            hospedeController.atualizar(idSelecionado, txtNome.getText(), txtCpf.getText(), txtNascimento.getText(),
                    txtTelefone.getText(), txtEmail.getText());
            JOptionPane.showMessageDialog(this, "Hóspede atualizado com sucesso!");
            limparCampos();
            mostrarNaTabela(listarComTratamento());
        } catch (NegocioException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Aviso", JOptionPane.WARNING_MESSAGE);
        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void excluir() {
        if (idSelecionado == 0) {
            JOptionPane.showMessageDialog(this, "Selecione um hóspede na tabela.");
            return;
        }
        int resposta = JOptionPane.showConfirmDialog(this, "Deseja excluir este hóspede?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (resposta != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            hospedeController.excluir(idSelecionado);
            JOptionPane.showMessageDialog(this, "Hóspede excluído com sucesso!");
            limparCampos();
            mostrarNaTabela(listarComTratamento());
        } catch (NegocioException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Aviso", JOptionPane.WARNING_MESSAGE);
        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    // mostra reservas anteriores, períodos, quartos e serviços consumidos
    private void verHistorico() {
        if (idSelecionado == 0) {
            JOptionPane.showMessageDialog(this, "Selecione um hóspede na tabela.");
            return;
        }
        try {
            List<Object[]> linhas = new ArrayList<>();
            for (Reserva r : hospedeController.historico(idSelecionado)) {
                String servicos = "";
                for (ServicoConsumido sc : r.getServicos()) {
                    servicos += sc.getServico().getNome() + " (" + sc.getQuantidade() + ") ";
                }
                linhas.add(new Object[] {r.getId(), r.getQuarto().getHotel().getNome(), r.getQuarto().toString(),
                        Conversor.formatarData(r.getDataEntrada()) + " a " + Conversor.formatarData(r.getDataSaida()),
                        r.getStatus(), servicos, String.format("R$ %.2f", r.getValorTotal())});
            }
            String[] colunas = {"Reserva", "Hotel", "Quarto", "Período", "Status", "Serviços consumidos", "Valor"};
            new TelaListagem("Histórico de " + txtNome.getText(), colunas, linhas).setVisible(true);
        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limparCampos() {
        idSelecionado = 0;
        txtNome.setText("");
        txtCpf.setText("");
        txtNascimento.setText("");
        txtTelefone.setText("");
        txtEmail.setText("");
        tabela.clearSelection();
    }
}
