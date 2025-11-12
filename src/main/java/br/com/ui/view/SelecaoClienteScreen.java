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

public class SelecaoClienteScreen extends JDialog {

    private JTable tabelaClientes;
    private DefaultTableModel tableModel;
    private PessoaResponse clienteSelecionado;
    private boolean consumidorNaoIdentificado = false;
    private List<PessoaResponse> listaClientes;
    private JTextField searchField;

    private final PessoaService pessoaService;

    public SelecaoClienteScreen(Frame owner) {
        super(owner, "Selecionar Cliente", true);
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

        carregarClientes();
    }

    private JPanel createHeader() {
        JPanel headerPanel = new JPanel(new BorderLayout(10, 10));
        headerPanel.setOpaque(false);
        headerPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("Selecione um Cliente");
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
        tabelaClientes = new JTable(tableModel);
        tabelaClientes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabelaClientes.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tabelaClientes.setRowHeight(30);
        tabelaClientes.setGridColor(ColorPalette.BORDER_COLOR);

        JTableHeader header = tabelaClientes.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(ColorPalette.PANEL_BACKGROUND);
        header.setForeground(ColorPalette.TEXT);

        JScrollPane scrollPane = new JScrollPane(tabelaClientes);
        scrollPane.setBorder(BorderFactory.createLineBorder(ColorPalette.BORDER_COLOR));
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        return tablePanel;
    }

    private JPanel createButtonsPanel() {
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonsPanel.setOpaque(false);

        JButton consumidorNaoIdentificadoButton = new JButton("Consumidor Não Identificado");
        consumidorNaoIdentificadoButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        consumidorNaoIdentificadoButton.addActionListener(e -> onConsumidorNaoIdentificado());
        buttonsPanel.add(consumidorNaoIdentificadoButton);

        JButton cancelarButton = new JButton("Cancelar");
        cancelarButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        cancelarButton.addActionListener(e -> onCancelar());
        buttonsPanel.add(cancelarButton);

        JButton selecionarButton = new JButton("Selecionar Cliente");
        selecionarButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        selecionarButton.setBackground(ColorPalette.PRIMARY);
        selecionarButton.setForeground(ColorPalette.WHITE_TEXT);
        selecionarButton.addActionListener(e -> onSelecionar());
        buttonsPanel.add(selecionarButton);

        return buttonsPanel;
    }

    private void carregarClientes() {
        try {
            this.listaClientes = pessoaService.findPessoas();
            updateTable(this.listaClientes);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar clientes: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateTable(List<PessoaResponse> clientes) {
        tableModel.setRowCount(0);
        for (PessoaResponse cliente : clientes) {
            tableModel.addRow(new Object[]{
                    cliente.id(),
                    cliente.nomeCompleto(),
                    cliente.cpfCnpj()
            });
        }
    }

    private void filterTable() {
        String searchText = searchField.getText().toLowerCase();
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        tabelaClientes.setRowSorter(sorter);
        if (searchText.trim().length() == 0) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + searchText));
        }
    }

    private void onSelecionar() {
        int selectedRow = tabelaClientes.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um cliente na tabela.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int modelRow = tabelaClientes.convertRowIndexToModel(selectedRow);
        Long clienteId = (Long) tableModel.getValueAt(modelRow, 0);
        this.clienteSelecionado = listaClientes.stream()
                .filter(p -> p.id().equals(clienteId))
                .findFirst()
                .orElse(null);
        this.consumidorNaoIdentificado = false;
        dispose();
    }

    private void onConsumidorNaoIdentificado() {
        this.clienteSelecionado = null;
        this.consumidorNaoIdentificado = true;
        dispose();
    }

    private void onCancelar() {
        this.clienteSelecionado = null;
        this.consumidorNaoIdentificado = false;
        dispose();
    }

    public PessoaResponse getClienteSelecionado() {
        return clienteSelecionado;
    }

    public boolean isConsumidorNaoIdentificado() {
        return consumidorNaoIdentificado;
    }
}
