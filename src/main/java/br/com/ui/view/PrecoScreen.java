package br.com.ui.view;

import br.com.common.service.ApiServiceException;
import br.com.preco.dto.PrecoRequest;
import br.com.preco.dto.PrecoResponse;
import br.com.preco.service.PrecoService;
import br.com.produto.dto.ProdutoResponse;
import br.com.produto.service.ProdutoService;
import br.com.ui.util.ColorPalette;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PrecoScreen extends JFrame {

    private JTextField valorField, dataAlteracaoField, produtoField;
    private JButton selecionarProdutoButton;
    private ProdutoResponse produtoSelecionado;
    private JTable tabelaPrecos;
    private DefaultTableModel tableModel;
    private Long precoIdEmEdicao;

    private final PrecoService precoService;
    private final ProdutoService produtoService;
    private final Map<Long, ProdutoResponse> produtosMap;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public PrecoScreen() {
        this.precoService = new PrecoService();
        this.produtoService = new ProdutoService();
        this.produtosMap = new HashMap<>();

        setTitle("Gerenciamento de Preços");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        Container contentPane = getContentPane();
        contentPane.setBackground(ColorPalette.BACKGROUND);
        contentPane.setLayout(new BorderLayout(0, 0));

        contentPane.add(createHeader("Gerenciamento de Preços"), BorderLayout.NORTH);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, createFormPanel(), createTablePanel());
        splitPane.setDividerLocation(350);
        splitPane.setBorder(BorderFactory.createEmptyBorder());
        contentPane.add(splitPane, BorderLayout.CENTER);

        carregarMapaProdutos();
        carregarPrecos();
    }

    private JPanel createHeader(String title) {
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headerPanel.setBackground(ColorPalette.PANEL_BACKGROUND);
        headerPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, ColorPalette.BORDER_COLOR));
        headerPanel.setPreferredSize(new Dimension(getWidth(), 60));
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(ColorPalette.TEXT);
        titleLabel.setBorder(new EmptyBorder(0, 10, 0, 0));
        headerPanel.add(titleLabel);
        return headerPanel;
    }

    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(ColorPalette.PANEL_BACKGROUND);
        formPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        formPanel.add(createLabel("Produto:"));
        formPanel.add(createProdutoSelectionPanel());
        formPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        formPanel.add(createLabel("Valor (R$):"));
        valorField = createTextField();
        formPanel.add(valorField);
        formPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        formPanel.add(createLabel("Data Alteração (dd/MM/yyyy):"));
        dataAlteracaoField = createTextField();
        formPanel.add(dataAlteracaoField);
        formPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        formPanel.add(createButtonsPanel());
        formPanel.add(Box.createVerticalGlue());

        return formPanel;
    }

    private JPanel createProdutoSelectionPanel() {
        JPanel produtoPanel = new JPanel(new BorderLayout(5, 0));
        produtoPanel.setOpaque(false);
        produtoPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        produtoPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        produtoField = createTextField();
        produtoField.setEditable(false);
        produtoPanel.add(produtoField, BorderLayout.CENTER);

        selecionarProdutoButton = new JButton("...");
        selecionarProdutoButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        selecionarProdutoButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        selecionarProdutoButton.addActionListener(e -> abrirSelecaoProduto());
        produtoPanel.add(selecionarProdutoButton, BorderLayout.EAST);

        return produtoPanel;
    }

    private JPanel createButtonsPanel() {
        JPanel buttonsPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        buttonsPanel.setOpaque(false);
        buttonsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        buttonsPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

        JButton novoButton = createButton("Novo", ColorPalette.ACCENT_INFO, ColorPalette.WHITE_TEXT);
        novoButton.addActionListener(e -> limparCampos());
        buttonsPanel.add(novoButton);

        JButton salvarButton = createButton("Salvar", ColorPalette.ACCENT_SUCCESS, ColorPalette.WHITE_TEXT);
        salvarButton.addActionListener(e -> salvarPreco());
        buttonsPanel.add(salvarButton);

        JButton editarButton = createButton("Editar", ColorPalette.ACCENT_WARNING, ColorPalette.WHITE_TEXT);
        editarButton.addActionListener(e -> editarPreco());
        buttonsPanel.add(editarButton);

        JButton excluirButton = createButton("Excluir", ColorPalette.ACCENT_DANGER, ColorPalette.WHITE_TEXT);
        excluirButton.addActionListener(e -> excluirPreco());
        buttonsPanel.add(excluirButton);

        return buttonsPanel;
    }

    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(ColorPalette.BACKGROUND);
        tablePanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        String[] colunas = {"ID", "Produto", "Valor (R$)", "Data de Alteração"};
        tableModel = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabelaPrecos = new JTable(tableModel);
        tabelaPrecos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabelaPrecos.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tabelaPrecos.setRowHeight(30);
        tabelaPrecos.setGridColor(ColorPalette.BORDER_COLOR);

        JTableHeader header = tabelaPrecos.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(ColorPalette.PANEL_BACKGROUND);
        header.setForeground(ColorPalette.TEXT);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, ColorPalette.BORDER_COLOR));

        JScrollPane scrollPane = new JScrollPane(tabelaPrecos);
        scrollPane.setBorder(BorderFactory.createLineBorder(ColorPalette.BORDER_COLOR));
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        return tablePanel;
    }

    private void abrirSelecaoProduto() {
        SelecaoProdutoScreen selecaoProdutoScreen = new SelecaoProdutoScreen(this);
        selecaoProdutoScreen.setVisible(true);
        ProdutoResponse produto = selecaoProdutoScreen.getProdutoSelecionado();
        if (produto != null) {
            this.produtoSelecionado = produto;
            produtoField.setText(produto.nome());
        }
    }

    private void carregarMapaProdutos() {
        try {
            List<ProdutoResponse> produtos = produtoService.findProducts();
            produtosMap.clear();
            for (ProdutoResponse produto : produtos) {
                produtosMap.put(produto.id(), produto);
            }
        } catch (ApiServiceException | IOException e) {
            showErrorDialog("Erro ao Carregar Produtos", "Não foi possível carregar os dados dos produtos: " + e.getMessage());
        }
    }

    private void carregarPrecos() {
        tableModel.setRowCount(0);
        try {
            List<PrecoResponse> precos = precoService.findPrecos();
            for (PrecoResponse preco : precos) {
                ProdutoResponse produto = produtosMap.get(preco.produtoId());
                String nomeProduto = (produto != null) ? produto.nome() : "ID: " + preco.produtoId();
                tableModel.addRow(new Object[]{
                        preco.id(),
                        nomeProduto,
                        String.format("%.2f", preco.valor()),
                        preco.dataAlteracao().format(dateFormatter)
                });
            }
        } catch (ApiServiceException | IOException e) {
            showErrorDialog("Erro ao Carregar Preços", "Não foi possível carregar os preços: " + e.getMessage());
        }
    }

    private void salvarPreco() {
        try {
            if (produtoSelecionado == null) {
                showErrorDialog("Validação", "É necessário selecionar um produto.");
                return;
            }
            BigDecimal valor = new BigDecimal(valorField.getText().replace(",", "."));
            LocalDate dataAlteracao = LocalDate.parse(dataAlteracaoField.getText(), dateFormatter);
            PrecoRequest request = new PrecoRequest(valor, dataAlteracao, LocalDate.now(), produtoSelecionado.id());

            if (precoIdEmEdicao == null) {
                precoService.createPreco(request);
                JOptionPane.showMessageDialog(this, "Preço salvo com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            } else {
                precoService.updatePreco(precoIdEmEdicao, request);
                JOptionPane.showMessageDialog(this, "Preço atualizado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            }
            carregarPrecos();
            limparCampos();

        } catch (DateTimeParseException ex) {
            showErrorDialog("Erro de Formato", "Data inválida. Use o formato dd/MM/yyyy.");
        } catch (NumberFormatException ex) {
            showErrorDialog("Erro de Formato", "Valor inválido. Use ponto ou vírgula como separador decimal.");
        } catch (ApiServiceException | IOException e) {
            showErrorDialog("Erro de Salvamento", "Não foi possível salvar o preço: " + e.getMessage());
        }
    }

    private void editarPreco() {
        int selectedRow = tabelaPrecos.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um preço na tabela para editar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        precoIdEmEdicao = (Long) tableModel.getValueAt(selectedRow, 0);
        try {
            PrecoResponse preco = precoService.findPrecoById(precoIdEmEdicao);
            valorField.setText(String.valueOf(preco.valor()).replace('.', ','));
            dataAlteracaoField.setText(preco.dataAlteracao().format(dateFormatter));
            produtoSelecionado = produtosMap.get(preco.produtoId());
            produtoField.setText(produtoSelecionado != null ? produtoSelecionado.nome() : "");
        } catch (ApiServiceException | IOException e) {
            showErrorDialog("Erro ao Editar", "Não foi possível carregar os dados do preço: " + e.getMessage());
        }
    }

    private void excluirPreco() {
        int selectedRow = tabelaPrecos.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um preço para excluir.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Long id = (Long) tableModel.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Tem certeza que deseja excluir este preço?", "Confirmar Exclusão", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            // A exclusão está desabilitada na versão original, mantendo o comportamento.
            JOptionPane.showMessageDialog(this, "Funcionalidade de exclusão de preço ainda não disponível.", "Não Implementado", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void limparCampos() {
        valorField.setText("");
        dataAlteracaoField.setText("");
        produtoField.setText("");
        produtoSelecionado = null;
        tabelaPrecos.clearSelection();
        precoIdEmEdicao = null;
    }

    private void showErrorDialog(String title, String message) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.ERROR_MESSAGE);
    }

    // Component Creation Methods
    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setForeground(ColorPalette.TEXT);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private JTextField createTextField() {
        JTextField textField = new JTextField();
        textField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        textField.setBackground(ColorPalette.PANEL_BACKGROUND);
        textField.setForeground(ColorPalette.TEXT);
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 1, 1, 1, ColorPalette.BORDER_COLOR),
                new EmptyBorder(8, 8, 8, 8)
        ));
        textField.setAlignmentX(Component.LEFT_ALIGNMENT);
        textField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        return textField;
    }

    private JButton createButton(String text, Color background, Color foreground) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setFocusPainted(false);
        button.setBackground(background);
        button.setForeground(foreground);
        button.setBorder(new EmptyBorder(10, 20, 10, 20));

        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                button.setBackground(background.darker());
            }

            public void mouseExited(MouseEvent evt) {
                button.setBackground(background);
            }
        });

        return button;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(new FlatLightLaf());
            } catch (UnsupportedLookAndFeelException e) {
                e.printStackTrace();
            }
            new PrecoScreen().setVisible(true);
        });
    }
}
