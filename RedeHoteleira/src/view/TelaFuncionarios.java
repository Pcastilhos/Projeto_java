package view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.time.LocalDate;
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

import controller.FuncionarioController;
import controller.HotelController;
import exception.NegocioException;
import model.Funcionario;
import model.Hotel;
import util.Conversor;

public class TelaFuncionarios extends JFrame {

    private FuncionarioController funcionarioController;
    private HotelController hotelController;
    private List<Funcionario> funcionarios = new ArrayList<>();
    private int idSelecionado = 0;

    private JComboBox<Hotel> cbHotel;
    private JComboBox<String> cbCargo;
    private JTextField txtNome;
    private JTextField txtCpf;
    private JTextField txtTelefone;
    private JTextField txtEmail;
    private JTextField txtSalario;
    private JTextField txtContratacao;
    private JTextField txtEntrada;
    private JTextField txtSaida;
    private JTextField txtFeriasInicio;
    private JTextField txtFeriasFim;

    private JTable tabela;
    private DefaultTableModel modelo;

    public TelaFuncionarios(FuncionarioController funcionarioController, HotelController hotelController) {
        this.funcionarioController = funcionarioController;
        this.hotelController = hotelController;
        configurarJanela();
        criarComponentes();
        carregarHoteis();
        carregarTabela();
    }

    private void configurarJanela() {
        setTitle("Funcionários");
        setSize(950, 650);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    private void criarComponentes() {
        // 6 linhas e 4 colunas para caber todos os campos
        JPanel painelFormulario = new JPanel(new GridLayout(6, 4, 10, 10));
        cbHotel = new JComboBox<>();
        cbCargo = new JComboBox<>();
        cbCargo.addItem("RECEPCIONISTA");
        cbCargo.addItem("GERENTE");
        cbCargo.addItem("CAMAREIRA");
        txtNome = new JTextField();
        txtCpf = new JTextField();
        txtTelefone = new JTextField();
        txtEmail = new JTextField();
        txtSalario = new JTextField();
        txtContratacao = new JTextField();
        txtEntrada = new JTextField();
        txtSaida = new JTextField();
        txtFeriasInicio = new JTextField();
        txtFeriasFim = new JTextField();

        painelFormulario.add(new JLabel("Hotel:"));
        painelFormulario.add(cbHotel);
        painelFormulario.add(new JLabel("Cargo:"));
        painelFormulario.add(cbCargo);
        painelFormulario.add(new JLabel("Nome:"));
        painelFormulario.add(txtNome);
        painelFormulario.add(new JLabel("CPF:"));
        painelFormulario.add(txtCpf);
        painelFormulario.add(new JLabel("Telefone:"));
        painelFormulario.add(txtTelefone);
        painelFormulario.add(new JLabel("E-mail:"));
        painelFormulario.add(txtEmail);
        painelFormulario.add(new JLabel("Salário base:"));
        painelFormulario.add(txtSalario);
        painelFormulario.add(new JLabel("Contratação (dd/mm/aaaa):"));
        painelFormulario.add(txtContratacao);
        painelFormulario.add(new JLabel("Entrada (hh:mm):"));
        painelFormulario.add(txtEntrada);
        painelFormulario.add(new JLabel("Saída (hh:mm):"));
        painelFormulario.add(txtSaida);
        painelFormulario.add(new JLabel("Férias início:"));
        painelFormulario.add(txtFeriasInicio);
        painelFormulario.add(new JLabel("Férias fim:"));
        painelFormulario.add(txtFeriasFim);

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

        // "Salário final" usa calcularSalario(), que muda conforme o cargo
        String[] colunas = {"ID", "Nome", "Cargo", "Hotel", "Salário base", "Salário final", "Horário", "Férias", "Situação hoje"};
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
            funcionarios = funcionarioController.listar();
            modelo.setRowCount(0);
            for (Funcionario f : funcionarios) {
                String ferias = "-";
                if (f.getFeriasInicio() != null) {
                    ferias = Conversor.formatarData(f.getFeriasInicio()) + " a " + Conversor.formatarData(f.getFeriasFim());
                }
                String situacao = "Trabalhando";
                if (f.estaDeFerias(LocalDate.now())) {
                    situacao = "De férias";
                }
                modelo.addRow(new Object[] {f.getId(), f.getNome(), f.getCargo(), f.getHotel().getNome(),
                        String.format("R$ %.2f", f.getSalario()), String.format("R$ %.2f", f.calcularSalario()),
                        f.getHorarioEntrada() + " às " + f.getHorarioSaida(), ferias, situacao});
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
        Funcionario f = funcionarios.get(linha);
        idSelecionado = f.getId();
        for (int i = 0; i < cbHotel.getItemCount(); i++) {
            if (cbHotel.getItemAt(i).getId() == f.getHotel().getId()) {
                cbHotel.setSelectedIndex(i);
            }
        }
        cbCargo.setSelectedItem(f.getCargo());
        txtNome.setText(f.getNome());
        txtCpf.setText(f.getCpf());
        txtTelefone.setText(f.getTelefone());
        txtEmail.setText(f.getEmail());
        txtSalario.setText(String.valueOf(f.getSalario()));
        txtContratacao.setText(Conversor.formatarData(f.getDataContratacao()));
        txtEntrada.setText(f.getHorarioEntrada());
        txtSaida.setText(f.getHorarioSaida());
        txtFeriasInicio.setText(Conversor.formatarData(f.getFeriasInicio()));
        txtFeriasFim.setText(Conversor.formatarData(f.getFeriasFim()));
    }

    private void cadastrar() {
        try {
            funcionarioController.cadastrar((Hotel) cbHotel.getSelectedItem(), (String) cbCargo.getSelectedItem(), txtNome.getText(),
                    txtCpf.getText(), txtTelefone.getText(), txtEmail.getText(), txtSalario.getText(), txtContratacao.getText(),
                    txtEntrada.getText(), txtSaida.getText(), txtFeriasInicio.getText(), txtFeriasFim.getText());
            JOptionPane.showMessageDialog(this, "Funcionário cadastrado com sucesso!");
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
            JOptionPane.showMessageDialog(this, "Selecione um funcionário na tabela.");
            return;
        }
        try {
            funcionarioController.atualizar(idSelecionado, (Hotel) cbHotel.getSelectedItem(), (String) cbCargo.getSelectedItem(),
                    txtNome.getText(), txtCpf.getText(), txtTelefone.getText(), txtEmail.getText(), txtSalario.getText(),
                    txtContratacao.getText(), txtEntrada.getText(), txtSaida.getText(), txtFeriasInicio.getText(), txtFeriasFim.getText());
            JOptionPane.showMessageDialog(this, "Funcionário atualizado com sucesso!");
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
            JOptionPane.showMessageDialog(this, "Selecione um funcionário na tabela.");
            return;
        }
        int resposta = JOptionPane.showConfirmDialog(this, "Deseja excluir este funcionário?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (resposta != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            funcionarioController.excluir(idSelecionado);
            JOptionPane.showMessageDialog(this, "Funcionário excluído com sucesso!");
            limparCampos();
            carregarTabela();
        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limparCampos() {
        idSelecionado = 0;
        txtNome.setText("");
        txtCpf.setText("");
        txtTelefone.setText("");
        txtEmail.setText("");
        txtSalario.setText("");
        txtContratacao.setText("");
        txtEntrada.setText("");
        txtSaida.setText("");
        txtFeriasInicio.setText("");
        txtFeriasFim.setText("");
        cbCargo.setSelectedIndex(0);
        tabela.clearSelection();
    }
}
