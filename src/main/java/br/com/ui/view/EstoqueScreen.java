package br.com.ui.view;

import br.com.common.service.ApiServiceException;
import br.com.estoque.dto.EstoqueRequest;
import br.com.estoque.dto.EstoqueResponse;
import br.com.estoque.enums.TipoEstoque;
import br.com.estoque.service.EstoqueService;
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

public class EstoqueScreen extends JFrame {

    private JTextField quantidadeField, localTanqueField, localEnderecoField, loteFabricacaoField, dataValidadeField, produtoField;
    private JRadioButton consumoRadioButton, vendasRadioButton;
    private ButtonGroup tipoEstoqueGroup;
    private JButton selecionarProdutoButton;
    private ProdutoResponse produtoSelecionado;
    private JTable tabelaEstoque;
    private DefaultTableModel tableModel;
    private Long estoqueIdEmEdicao;

    private final EstoqueService estoqueService;
    private final ProdutoService produtoService;
    private final Map<Long, ProdutoResponse> produtosMap;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public EstoqueScreen() {
        this.estoqueService = new EstoqueService();
        this.produtoService = new ProdutoService();
        this.produtosMap = new HashMap<>();

        setTitle("Gerenciamento de Estoque");
        setSize(1200, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        Container contentPane = getContentPane();
        contentPane.setBackground(ColorPalette.BACKGROUND);
        contentPane.setLayout(new BorderLayout(0, 0));

        contentPane.add(createHeader("Gerenciamento de Estoque"), BorderLayout.NORTH);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, createFormPanel(), createTablePanel());
        splitPane.setDividerLocation(380);
        splitPane.setBorder(BorderFactory.createEmptyBorder());
        contentPane.add(splitPane, BorderLayout.CENTER);

        carregarMapaProdutos();
        carregarEstoques();
    }

    private JPanel createHeader(String title) {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(ColorPalette.PANEL_BACKGROUND);
        headerPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, ColorPalette.BORDER_COLOR));
        headerPanel.setPreferredSize(new Dimension(getWidth(), 80));

        // Painel para logo e slogan
        JPanel logoSloganPanel = new JPanel();
        logoSloganPanel.setLayout(new BoxLayout(logoSloganPanel, BoxLayout.Y_AXIS));
        logoSloganPanel.setOpaque(false);
        logoSloganPanel.setBorder(new EmptyBorder(10, 20, 10, 20));

        JLabel logoLabel = new JLabel("PDV");
        logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        logoLabel.setForeground(ColorPalette.PRIMARY);
        logoSloganPanel.add(logoLabel);

        JLabel sloganLabel = new JLabel("Qualidade no tanque, sorriso no rosto");
        sloganLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        sloganLabel.setForeground(ColorPalette.TEXT_MUTED);
        logoSloganPanel.add(sloganLabel);

        headerPanel.add(logoSloganPanel, BorderLayout.WEST);

        // Título centralizado
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(ColorPalette.TEXT);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        headerPanel.add(titleLabel, BorderLayout.CENTER);

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

        formPanel.add(createLabel("Quantidade:"));
        quantidadeField = createTextField();
        formPanel.add(quantidadeField);
        formPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        formPanel.add(createLabel("Local do Tanque:"));
        localTanqueField = createTextField();
        formPanel.add(localTanqueField);
        formPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        formPanel.add(createLabel("Endereço do Local:"));
        localEnderecoField = createTextField();
        formPanel.add(localEnderecoField);
        formPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        formPanel.add(createLabel("Lote de Fabricação:"));
        loteFabricacaoField = createTextField();
        formPanel.add(loteFabricacaoField);
        formPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        formPanel.add(createLabel("Data de Validade (dd/MM/yyyy):"));
        dataValidadeField = createTextField();
        formPanel.add(dataValidadeField);
        formPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        formPanel.add(createLabel("Tipo de Estoque:"));
        
        consumoRadioButton = new JRadioButton("Consumo");
        consumoRadioButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        consumoRadioButton.setBackground(ColorPalette.PANEL_BACKGROUND);
        consumoRadioButton.setForeground(ColorPalette.TEXT);

        vendasRadioButton = new JRadioButton("Vendas");
        vendasRadioButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        vendasRadioButton.setBackground(ColorPalette.PANEL_BACKGROUND);
        vendasRadioButton.setForeground(ColorPalette.TEXT);

        tipoEstoqueGroup = new ButtonGroup();
        tipoEstoqueGroup.add(consumoRadioButton);
        tipoEstoqueGroup.add(vendasRadioButton);

        JPanel radioPanel = new JPanel();
        radioPanel.setLayout(new BoxLayout(radioPanel, BoxLayout.Y_AXIS));
        radioPanel.setBackground(ColorPalette.PANEL_BACKGROUND);
        radioPanel.add(consumoRadioButton);
        radioPanel.add(vendasRadioButton);
        radioPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        formPanel.add(radioPanel);
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
        JPanel buttonsPanel = new JPanel(new GridLayout(4, 1, 0, 10));
        buttonsPanel.setOpaque(false);
        buttonsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        buttonsPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));

        JButton novoButton = createButton("Novo", ColorPalette.ACCENT_INFO, ColorPalette.WHITE_TEXT);
        novoButton.addActionListener(e -> limparCampos());
        buttonsPanel.add(novoButton);

        JButton salvarButton = createButton("Salvar", ColorPalette.ACCENT_INFO, ColorPalette.WHITE_TEXT);
        salvarButton.addActionListener(e -> salvarEstoque());
        buttonsPanel.add(salvarButton);

        JButton editarButton = createButton("Editar", ColorPalette.ACCENT_INFO, ColorPalette.WHITE_TEXT);
        editarButton.addActionListener(e -> editarEstoque());
        buttonsPanel.add(editarButton);

        JButton excluirButton = createButton("Excluir", ColorPalette.ACCENT_INFO, ColorPalette.WHITE_TEXT);
        excluirButton.addActionListener(e -> excluirEstoque());
        buttonsPanel.add(excluirButton);

        return buttonsPanel;
    }

    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(ColorPalette.BACKGROUND);
        tablePanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        String[] colunas = {"ID", "Produto", "Qtd.", "Tanque", "Endereço", "Lote", "Validade", "Tipo"};
        tableModel = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabelaEstoque = new JTable(tableModel);
        tabelaEstoque.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabelaEstoque.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tabelaEstoque.setRowHeight(30);
        tabelaEstoque.setGridColor(ColorPalette.BORDER_COLOR);

        JTableHeader header = tabelaEstoque.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(ColorPalette.PANEL_BACKGROUND);
        header.setForeground(ColorPalette.TEXT);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, ColorPalette.BORDER_COLOR));

        JScrollPane scrollPane = new JScrollPane(tabelaEstoque);
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

    private void carregarEstoques() {
        tableModel.setRowCount(0);
        try {
            List<EstoqueResponse> estoques = estoqueService.findEstoques();
            for (EstoqueResponse estoque : estoques) {
                ProdutoResponse produto = produtosMap.get(estoque.produtoId());
                String nomeProduto = (produto != null) ? produto.nome() : "ID: " + estoque.produtoId();
                tableModel.addRow(new Object[]{
                        estoque.id(),
                        nomeProduto,
                        String.format("%.2f", estoque.quantidade()),
                        estoque.localTanque(),
                        estoque.localEndereco(),
                        estoque.loteFabricacao(),
                        estoque.dataValidade().format(dateFormatter),
                        estoque.tipoEstoque()
                });
            }
        } catch (ApiServiceException | IOException e) {
            showErrorDialog("Erro ao Carregar Estoque", "Não foi possível carregar o estoque: " + e.getMessage());
        }
    }

    private void salvarEstoque() {
        try {
            if (produtoSelecionado == null) {
                showErrorDialog("Validação", "É necessário selecionar um produto.");
                return;
            }
            BigDecimal quantidade = new BigDecimal(quantidadeField.getText().replace(",", "."));
            LocalDate dataValidade = LocalDate.parse(dataValidadeField.getText(), dateFormatter);

            TipoEstoque tipoEstoque = null;
            if (consumoRadioButton.isSelected()) {
                tipoEstoque = TipoEstoque.CONSUMO;
            } else if (vendasRadioButton.isSelected()) {
                tipoEstoque = TipoEstoque.VENDAS;
            }

            EstoqueRequest request = new EstoqueRequest(
                    quantidade,
                    localTanqueField.getText(),
                    localEnderecoField.getText(),
                    loteFabricacaoField.getText(),
                    dataValidade,
                    tipoEstoque,
                    produtoSelecionado.id()
            );

            if (estoqueIdEmEdicao == null) {
                estoqueService.createEstoque(request);
                JOptionPane.showMessageDialog(this, "Estoque salvo com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            } else {
                estoqueService.updateEstoque(estoqueIdEmEdicao, request);
                JOptionPane.showMessageDialog(this, "Estoque atualizado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            }
            carregarEstoques();
            limparCampos();

        } catch (DateTimeParseException ex) {
            showErrorDialog("Erro de Formato", "Data inválida. Use o formato dd/MM/yyyy.");
        } catch (NumberFormatException ex) {
            showErrorDialog("Erro de Formato", "Quantidade inválida. Use ponto ou vírgula como separador decimal.");
        } catch (ApiServiceException | IOException e) {
            showErrorDialog("Erro de Salvamento", "Não foi possível salvar o estoque: " + e.getMessage());
        }
    }

    private void editarEstoque() {
        int selectedRow = tabelaEstoque.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um item do estoque na tabela para editar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        estoqueIdEmEdicao = (Long) tableModel.getValueAt(selectedRow, 0);
        try {
            EstoqueResponse estoque = estoqueService.findEstoqueById(estoqueIdEmEdicao);
            quantidadeField.setText(String.valueOf(estoque.quantidade()).replace('.', ','));
            localTanqueField.setText(estoque.localTanque());
            localEnderecoField.setText(estoque.localEndereco());
            loteFabricacaoField.setText(estoque.loteFabricacao());
            dataValidadeField.setText(estoque.dataValidade().format(dateFormatter));
            
            TipoEstoque tipoEstoque = estoque.tipoEstoque();
            if (tipoEstoque == TipoEstoque.CONSUMO) {
                consumoRadioButton.setSelected(true);
            } else if (tipoEstoque == TipoEstoque.VENDAS) {
                vendasRadioButton.setSelected(true);
            }

            produtoSelecionado = produtosMap.get(estoque.produtoId());
            produtoField.setText(produtoSelecionado != null ? produtoSelecionado.nome() : "");
        } catch (ApiServiceException | IOException e) {
            showErrorDialog("Erro ao Editar", "Não foi possível carregar os dados do estoque: " + e.getMessage());
        }
    }

    private void excluirEstoque() {
        int selectedRow = tabelaEstoque.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um item do estoque para excluir.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Long id = (Long) tableModel.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Tem certeza que deseja excluir este item do estoque?", "Confirmar Exclusão", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                estoqueService.deleteEstoque(id);
                carregarEstoques();
                limparCampos();
            } catch (ApiServiceException | IOException e) {
                showErrorDialog("Erro ao Excluir", "Não foi possível excluir o item do estoque: " + e.getMessage());
            }
        }
    }

    private void limparCampos() {
        quantidadeField.setText("");
        localTanqueField.setText("");
        localEnderecoField.setText("");
        loteFabricacaoField.setText("");
        dataValidadeField.setText("");
        tipoEstoqueGroup.clearSelection();
        produtoField.setText("");
        produtoSelecionado = null;
        tabelaEstoque.clearSelection();
        estoqueIdEmEdicao = null;
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
        textField.setBackground(ColorPalette.ACCENT_INFO);
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
}
