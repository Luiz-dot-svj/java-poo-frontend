package br.com.ui.view;

import br.com.api.dto.BombaDTO;
import br.com.api.dto.ProdutoDTO;
import br.com.common.service.ApiServiceException;
import br.com.service.ProdutoService;
import br.com.ui.util.ColorPalette;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class AbastecimentoDialog extends JDialog {

    private JComboBox<String> combustivelComboBox;
    private JTextField litrosTextField;
    private JTextField reaisTextField;
    private JComboBox<String> pagamentoComboBox;
    private final ProdutoService produtoService;
    private List<ProdutoDTO> produtos;

    private ProdutoDTO produtoSelecionado;
    private double litrosAbastecidos;
    private double reaisAbastecidos;
    private String formaPagamento;
    private boolean confirmado = false;

    public AbastecimentoDialog(Frame owner, BombaDTO bomba) {
        super(owner, "Iniciar Abastecimento na Bomba " + bomba.getNome(), true);
        this.produtoService = new ProdutoService();

        setSize(450, 400);
        setLocationRelativeTo(owner);
        setResizable(false);
        
        Container contentPane = getContentPane();
        contentPane.setBackground(ColorPalette.PANEL_BACKGROUND);
        contentPane.setLayout(new BorderLayout());
        ((JPanel) contentPane).setBorder(new EmptyBorder(20, 20, 20, 20));

        // Título
        JLabel titleLabel = new JLabel("Configurar Abastecimento", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(ColorPalette.TEXT);
        titleLabel.setBorder(new EmptyBorder(0, 0, 20, 0));
        contentPane.add(titleLabel, BorderLayout.NORTH);

        // Formulário
        JPanel formPanel = new JPanel();
        formPanel.setOpaque(false);
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        
        formPanel.add(createLabel("Combustível:"));
        combustivelComboBox = createComboBox();
        formPanel.add(combustivelComboBox);
        formPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        formPanel.add(createLabel("Valor a abastecer (R$) ou Litros:"));
        JPanel inputPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        inputPanel.setOpaque(false);
        reaisTextField = createTextField("R$");
        litrosTextField = createTextField("Litros");
        inputPanel.add(reaisTextField);
        inputPanel.add(litrosTextField);
        formPanel.add(inputPanel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        formPanel.add(createLabel("Forma de Pagamento:"));
        pagamentoComboBox = createComboBox(new String[]{"Dinheiro", "Pix", "Cartão de Crédito", "Cartão de Débito"});
        formPanel.add(pagamentoComboBox);
        
        contentPane.add(formPanel, BorderLayout.CENTER);

        // Botões
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(new EmptyBorder(20, 0, 0, 0));
        JButton cancelButton = new JButton("Cancelar");
        cancelButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        cancelButton.addActionListener(e -> dispose());
        JButton okButton = new JButton("Confirmar");
        okButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        okButton.setBackground(ColorPalette.PRIMARY);
        okButton.setForeground(ColorPalette.WHITE_TEXT);
        buttonPanel.add(cancelButton);
        buttonPanel.add(okButton);
        contentPane.add(buttonPanel, BorderLayout.SOUTH);

        carregarCombustiveis();
        setupInputListeners();

        okButton.addActionListener(e -> onConfirm());
    }

    private void carregarCombustiveis() {
        SwingWorker<List<ProdutoDTO>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<ProdutoDTO> doInBackground() throws Exception {
                return produtoService.buscarTodos();
            }

            @Override
            protected void done() {
                try {
                    produtos = get();
                    NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
                    List<String> items = produtos.stream()
                            .filter(p -> "COMBUSTIVEL".equals(p.getTipoProduto()) && p.getPrecos() != null && !p.getPrecos().isEmpty())
                            .map(p -> String.format("%s - %s/L", p.getNome(), currencyFormat.format(p.getPrecos().get(0).getValor())))
                            .collect(Collectors.toList());
                    
                    for (String item : items) {
                        combustivelComboBox.addItem(item);
                    }
                } catch (InterruptedException | ExecutionException e) {
                    showError("Erro ao carregar combustíveis: " + e.getCause().getMessage());
                }
            }
        };
        worker.execute();
    }

    private void setupInputListeners() {
        KeyAdapter listener = new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                JTextField source = (JTextField) e.getSource();
                String text = source.getText().replace(",", ".");
                
                Optional<ProdutoDTO> produtoOpt = getSelectedProduto();
                if (produtoOpt.isEmpty() || text.isBlank()) {
                    if (source == reaisTextField) litrosTextField.setText("");
                    else reaisTextField.setText("");
                    return;
                }

                try {
                    double value = Double.parseDouble(text);
                    double preco = produtoOpt.get().getPrecos().get(0).getValor().doubleValue();
                    
                    if (source == reaisTextField) {
                        litrosTextField.setText(String.format(Locale.US, "%.2f", value / preco));
                    } else { // source == litrosTextField
                        reaisTextField.setText(String.format(Locale.US, "%.2f", value * preco));
                    }
                } catch (NumberFormatException ex) {
                    // Ignora entrada inválida
                }
            }
        };
        reaisTextField.addKeyListener(listener);
        litrosTextField.addKeyListener(listener);
    }

    private void onConfirm() {
        Optional<ProdutoDTO> produtoOpt = getSelectedProduto();
        if (produtoOpt.isEmpty()) {
            showError("Selecione um combustível válido.");
            return;
        }
        this.produtoSelecionado = produtoOpt.get();

        try {
            if (!litrosTextField.getText().isBlank()) {
                litrosAbastecidos = Double.parseDouble(litrosTextField.getText().replace(",", "."));
                reaisAbastecidos = litrosAbastecidos * produtoSelecionado.getPrecos().get(0).getValor().doubleValue();
            } else if (!reaisTextField.getText().isBlank()) {
                reaisAbastecidos = Double.parseDouble(reaisTextField.getText().replace(",", "."));
                litrosAbastecidos = reaisAbastecidos / produtoSelecionado.getPrecos().get(0).getValor().doubleValue();
            } else {
                showError("Preencha o valor em Reais (R$) ou em Litros.");
                return;
            }

            if (litrosAbastecidos <= 0) {
                showError("O valor a ser abastecido deve ser positivo.");
                return;
            }

            this.formaPagamento = (String) pagamentoComboBox.getSelectedItem();
            this.confirmado = true;
            dispose();

        } catch (NumberFormatException ex) {
            showError("Valor inválido. Use apenas números e vírgula/ponto como separador decimal.");
        }
    }

    private Optional<ProdutoDTO> getSelectedProduto() {
        String itemSelecionado = (String) combustivelComboBox.getSelectedItem();
        if (itemSelecionado == null || produtos == null) return Optional.empty();
        String nomeCombustivel = itemSelecionado.split(" - ")[0];
        return produtos.stream().filter(p -> p.getNome().equals(nomeCombustivel)).findFirst();
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Erro de Validação", JOptionPane.ERROR_MESSAGE);
    }

    // Component Factory Methods
    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setForeground(ColorPalette.TEXT);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private JTextField createTextField(String placeholder) {
        JTextField textField = new JTextField();
        textField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        // Placeholder text can be simulated with a FocusListener if needed
        textField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 1, 1, 1, ColorPalette.BORDER_COLOR),
            new EmptyBorder(8, 8, 8, 8)
        ));
        textField.setAlignmentX(Component.LEFT_ALIGNMENT);
        textField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        return textField;
    }

    private JComboBox<String> createComboBox(String... items) {
        JComboBox<String> comboBox = new JComboBox<>(items);
        comboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        comboBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        comboBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        return comboBox;
    }

    // Getters
    public boolean isConfirmado() { return confirmado; }
    public ProdutoDTO getProdutoSelecionado() { return produtoSelecionado; }
    public double getLitrosAbastecidos() { return litrosAbastecidos; }
    public double getReaisAbastecidos() { return reaisAbastecidos; }
    public String getFormaPagamento() { return formaPagamento; }
}
