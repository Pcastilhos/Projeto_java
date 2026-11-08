package view;

import java.awt.BorderLayout;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

// tela simples só para mostrar uma tabela (quartos do hotel, histórico, etc.)
public class TelaListagem extends JFrame {

    public TelaListagem(String titulo, String[] colunas, List<Object[]> linhas) {
        setTitle(titulo);
        setSize(800, 350);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        DefaultTableModel modelo = new DefaultTableModel(colunas, 0);
        for (Object[] linha : linhas) {
            modelo.addRow(linha);
        }
        JTable tabela = new JTable(modelo);
        add(new JScrollPane(tabela), BorderLayout.CENTER);
    }
}
