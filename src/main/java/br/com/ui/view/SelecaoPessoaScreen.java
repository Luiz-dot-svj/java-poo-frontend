package br.com.ui.view;

import br.com.pessoa.dto.PessoaResponse;
import br.com.pessoa.service.PessoaService;
import br.com.ui.util.ColorPalette;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.List;

public class SelecaoPessoaScreen extends JDialog {

    private JTable tabelaPessoas;
    private DefaultTableModel tableModel;
    private PessoaResponse pessoaSelecionada;
    private List<PessoaResponse> listaPessoas;
    private JTextField searchField;

    private final PessoaService pessoaService;

    public SelecaoPessoaScreen(Frame owner) {
        super(owner, "Selecionar Pessoa", true);
        this.pessoaService = new PessoaService();

        setSize(800, 600);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(0, 0));
        getContentPane().setBackground(ColorPalette.BACKGROUND);

        // Header
        add(createHeader(), BorderLayout.NORTH);

        // Tabela
        add(createTablePanel(), BorderLayout.CENTER);

        // Botões
        add(createButtonsPanel(), BorderLayout.SOUTH);

        carregarPessoas();
    }

    private JPanel createHeader() {
        JPanel headerPanel = new JPanel(new BorderLayout(10, 10));
        headerPanel.setOpaque(false);
        headerPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("Selecione uma Pessoa");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        headerPanel.add(titleLabel, BorderLayout.WEST);

        searchField = new JTextField();
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                filterTable();
            }
        });
        headerPanel.add(searchField, BorderLayout.CENTER);

        return headerPanel;
    }

    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setOpaque(false);
        tablePanel.setBorder(new EmptyBorder(0, 10, 10, 10));

        String[] colunas = {"ID", "Nome", "CPF/CNPJ"};
        tableModel = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabelaPessoas = new JTable(tableModel);
        tabelaPessoas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabelaPessoas.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tabelaPessoas.setRowHeight(30);
        tabelaPessoas.setGridColor(ColorPalette.BORDER_COLOR);

        JTableHeader header = tabelaPessoas.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(ColorPalette.PANEL_BACKGROUND);
        header.setForeground(ColorPalette.TEXT);

        JScrollPane scrollPane = new JScrollPane(tabelaPessoas);
        scrollPane.setBorder(BorderFactory.createLineBorder(ColorPalette.BORDER_COLOR));
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        return tablePanel;
    }

    private JPanel createButtonsPanel() {
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonsPanel.setOpaque(false);

        JButton cancelarButton = new JButton("Cancelar");
        cancelarButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        cancelarButton.addActionListener(e -> onCancelar());
        buttonsPanel.add(cancelarButton);

        JButton selecionarButton = new JButton("Selecionar");
        selecionarButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        selecionarButton.setBackground(ColorPalette.PRIMARY);
        selecionarButton.setForeground(ColorPalette.WHITE_TEXT);
        selecionarButton.addActionListener(e -> onSelecionar());
        buttonsPanel.add(selecionarButton);

        return buttonsPanel;
    }

    private void carregarPessoas() {
        try {
            this.listaPessoas = pessoaService.findPessoas();
            updateTable(this.listaPessoas);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar pessoas: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateTable(List<PessoaResponse> pessoas) {
        tableModel.setRowCount(0);
        for (PessoaResponse pessoa : pessoas) {
            tableModel.addRow(new Object[]{
                    pessoa.id(),
                    pessoa.nomeCompleto(),
                    pessoa.cpfCnpj()
            });
        }
    }

    private void filterTable() {
        String searchText = searchField.getText().toLowerCase();
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        tabelaPessoas.setRowSorter(sorter);
        if (searchText.trim().length() == 0) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + searchText));
        }
    }

    private void onSelecionar() {
        int selectedRow = tabelaPessoas.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Por favor, selecione uma pessoa na tabela.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int modelRow = tabelaPessoas.convertRowIndexToModel(selectedRow);
        Long pessoaId = (Long) tableModel.getValueAt(modelRow, 0);
        this.pessoaSelecionada = listaPessoas.stream()
                .filter(p -> p.id().equals(pessoaId))
                .findFirst()
                .orElse(null);
        dispose();
    }

    private void onCancelar() {
        this.pessoaSelecionada = null;
        dispose();
    }

    public PessoaResponse getPessoaSelecionada() {
        return pessoaSelecionada;
    }
}
