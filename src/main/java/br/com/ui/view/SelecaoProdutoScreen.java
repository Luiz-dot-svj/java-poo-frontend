package br.com.ui.view;

import br.com.produto.dto.ProdutoResponse;
import br.com.produto.service.ProdutoService;
import br.com.ui.util.ColorPalette;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.List;

public class SelecaoProdutoScreen extends JDialog {

    private JTable tabelaProdutos;
    private DefaultTableModel tableModel;
    private ProdutoResponse produtoSelecionado;
    private List<ProdutoResponse> listaProdutos;
    private JTextField searchField;

    private final ProdutoService produtoService;

    public SelecaoProdutoScreen(Frame owner) {
        super(owner, "Selecionar Produto", true);
        this.produtoService = new ProdutoService();

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

        carregarProdutos();
    }

    private JPanel createHeader() {
        JPanel headerPanel = new JPanel(new BorderLayout(10, 10));
        headerPanel.setOpaque(false);
        headerPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("Selecione um Produto");
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

        String[] colunas = {"ID", "Nome", "Referência", "Marca"};
        tableModel = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabelaProdutos = new JTable(tableModel);
        tabelaProdutos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabelaProdutos.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tabelaProdutos.setRowHeight(30);
        tabelaProdutos.setGridColor(ColorPalette.BORDER_COLOR);

        JTableHeader header = tabelaProdutos.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(ColorPalette.PANEL_BACKGROUND);
        header.setForeground(ColorPalette.TEXT);

        JScrollPane scrollPane = new JScrollPane(tabelaProdutos);
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

    private void carregarProdutos() {
        try {
            this.listaProdutos = produtoService.findProducts();
            updateTable(this.listaProdutos);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar produtos: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateTable(List<ProdutoResponse> produtos) {
        tableModel.setRowCount(0);
        for (ProdutoResponse produto : produtos) {
            tableModel.addRow(new Object[]{
                    produto.id(),
                    produto.nome(),
                    produto.referencia(),
                    produto.marca()
            });
        }
    }

    private void filterTable() {
        String searchText = searchField.getText().toLowerCase();
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        tabelaProdutos.setRowSorter(sorter);
        if (searchText.trim().length() == 0) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + searchText));
        }
    }

    private void onSelecionar() {
        int selectedRow = tabelaProdutos.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Por favor, selecione um produto na tabela.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int modelRow = tabelaProdutos.convertRowIndexToModel(selectedRow);
        Long produtoId = (Long) tableModel.getValueAt(modelRow, 0);
        this.produtoSelecionado = listaProdutos.stream()
                .filter(p -> p.id().equals(produtoId))
                .findFirst()
                .orElse(null);
        dispose();
    }

    private void onCancelar() {
        this.produtoSelecionado = null;
        dispose();
    }

    public ProdutoResponse getProdutoSelecionado() {
        return produtoSelecionado;
    }
}
