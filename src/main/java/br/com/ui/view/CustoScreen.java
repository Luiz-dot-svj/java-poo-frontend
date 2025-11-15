package br.com.ui.view;

import br.com.common.service.ApiServiceException;
import br.com.custo.dto.CustoRequest;
import br.com.custo.dto.CustoResponse;
import br.com.custo.enums.TipoCusto;
import br.com.custo.service.CustoService;
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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class CustoScreen extends JFrame {

    private JTextField impostoField, custoVariavelField, custoFixoField, margemLucroField, dataProcessamentoField;
    private JRadioButton energiaRadioButton, freteRadioButton, aguaRadioButton;
    private ButtonGroup tipoCustoGroup;
    private JTable tabelaCustos;
    private DefaultTableModel tableModel;
    private Long custoIdEmEdicao;

    private final CustoService custoService;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public CustoScreen() {
        this.custoService = new CustoService();

        setTitle("Gerenciamento de Custos");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        Container contentPane = getContentPane();
        contentPane.setBackground(ColorPalette.BACKGROUND);
        contentPane.setLayout(new BorderLayout(0, 0));

        contentPane.add(createHeader("Gerenciamento de Custos"), BorderLayout.NORTH);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, createFormPanel(), createTablePanel());
        splitPane.setDividerLocation(350);
        splitPane.setBorder(BorderFactory.createEmptyBorder());
        contentPane.add(splitPane, BorderLayout.CENTER);

        carregarCustos();
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

        formPanel.add(createLabel("Imposto (%):"));
        impostoField = createTextField();
        formPanel.add(impostoField);
        formPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        formPanel.add(createLabel("Custo Variável (R$):"));
        custoVariavelField = createTextField();
        formPanel.add(custoVariavelField);
        formPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        formPanel.add(createLabel("Custo Fixo (R$):"));
        custoFixoField = createTextField();
        formPanel.add(custoFixoField);
        formPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        formPanel.add(createLabel("Margem de Lucro (%):"));
        margemLucroField = createTextField();
        formPanel.add(margemLucroField);
        formPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        formPanel.add(createLabel("Data Processamento (dd/MM/yyyy):"));
        dataProcessamentoField = createTextField();
        formPanel.add(dataProcessamentoField);
        formPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        formPanel.add(createLabel("Tipo de Custo:"));
        
        energiaRadioButton = new JRadioButton("Energia");
        energiaRadioButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        energiaRadioButton.setBackground(ColorPalette.PANEL_BACKGROUND);
        energiaRadioButton.setForeground(ColorPalette.TEXT);

        freteRadioButton = new JRadioButton("Frete");
        freteRadioButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        freteRadioButton.setBackground(ColorPalette.PANEL_BACKGROUND);
        freteRadioButton.setForeground(ColorPalette.TEXT);

        aguaRadioButton = new JRadioButton("Água");
        aguaRadioButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        aguaRadioButton.setBackground(ColorPalette.PANEL_BACKGROUND);
        aguaRadioButton.setForeground(ColorPalette.TEXT);

        tipoCustoGroup = new ButtonGroup();
        tipoCustoGroup.add(energiaRadioButton);
        tipoCustoGroup.add(freteRadioButton);
        tipoCustoGroup.add(aguaRadioButton);

        JPanel radioPanel = new JPanel();
        radioPanel.setLayout(new BoxLayout(radioPanel, BoxLayout.Y_AXIS));
        radioPanel.setBackground(ColorPalette.PANEL_BACKGROUND);
        radioPanel.add(energiaRadioButton);
        radioPanel.add(freteRadioButton);
        radioPanel.add(aguaRadioButton);
        radioPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        formPanel.add(radioPanel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        formPanel.add(createButtonsPanel());
        formPanel.add(Box.createVerticalGlue());

        return formPanel;
    }

    private JPanel createButtonsPanel() {
        JPanel buttonsPanel = new JPanel(new GridLayout(4, 1, 0, 5));
        buttonsPanel.setOpaque(false);
        buttonsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        buttonsPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 180));

        JButton novoButton = createButton("Novo", ColorPalette.ACCENT_INFO, ColorPalette.WHITE_TEXT);
        novoButton.addActionListener(e -> limparCampos());
        buttonsPanel.add(novoButton);

        JButton salvarButton = createButton("Salvar", ColorPalette.ACCENT_INFO, ColorPalette.WHITE_TEXT);
        salvarButton.addActionListener(e -> salvarCusto());
        buttonsPanel.add(salvarButton);

        JButton editarButton = createButton("Editar", ColorPalette.ACCENT_INFO, ColorPalette.WHITE_TEXT);
        editarButton.addActionListener(e -> editarCusto());
        buttonsPanel.add(editarButton);

        JButton excluirButton = createButton("Excluir", ColorPalette.ACCENT_INFO, ColorPalette.WHITE_TEXT);
        excluirButton.addActionListener(e -> excluirCusto());
        buttonsPanel.add(excluirButton);

        return buttonsPanel;
    }

    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(ColorPalette.BACKGROUND);
        tablePanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        String[] colunas = {"ID", "Imposto (%)", "C. Variável (R$)", "C. Fixo (R$)", "M. Lucro (%)", "Data", "Tipo"};
        tableModel = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabelaCustos = new JTable(tableModel);
        tabelaCustos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabelaCustos.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tabelaCustos.setRowHeight(30);
        tabelaCustos.setGridColor(ColorPalette.BORDER_COLOR);

        JTableHeader header = tabelaCustos.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(ColorPalette.PANEL_BACKGROUND);
        header.setForeground(ColorPalette.TEXT);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, ColorPalette.BORDER_COLOR));

        JScrollPane scrollPane = new JScrollPane(tabelaCustos);
        scrollPane.setBorder(BorderFactory.createLineBorder(ColorPalette.BORDER_COLOR));
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        return tablePanel;
    }

    private void carregarCustos() {
        tableModel.setRowCount(0);
        try {
            List<CustoResponse> custos = custoService.findCustos();
            for (CustoResponse custo : custos) {
                tableModel.addRow(new Object[]{
                        custo.id(),
                        String.format("%.2f", custo.imposto()),
                        String.format("%.2f", custo.custoVariavel()),
                        String.format("%.2f", custo.custoFixo()),
                        String.format("%.2f", custo.margemLucro()),
                        custo.dataProcessamento().format(dateFormatter),
                        custo.tipoCusto()
                });
            }
        } catch (ApiServiceException | IOException e) {
            showErrorDialog("Erro ao Carregar", "Não foi possível carregar os custos: " + e.getMessage());
        }
    }

    private void salvarCusto() {
        try {
            double imposto = Double.parseDouble(impostoField.getText().replace(",", "."));
            double custoVariavel = Double.parseDouble(custoVariavelField.getText().replace(",", "."));
            double custoFixo = Double.parseDouble(custoFixoField.getText().replace(",", "."));
            double margemLucro = Double.parseDouble(margemLucroField.getText().replace(",", "."));
            LocalDate dataProcessamento = LocalDate.parse(dataProcessamentoField.getText(), dateFormatter);

            TipoCusto tipoCusto = null;
            if (energiaRadioButton.isSelected()) {
                tipoCusto = TipoCusto.ENERGIA;
            } else if (freteRadioButton.isSelected()) {
                tipoCusto = TipoCusto.FRETE;
            } else if (aguaRadioButton.isSelected()) {
                tipoCusto = TipoCusto.AGUA;
            }

            CustoRequest request = new CustoRequest(imposto, custoVariavel, custoFixo, margemLucro, dataProcessamento, tipoCusto);

            if (custoIdEmEdicao == null) {
                custoService.createCusto(request);
                JOptionPane.showMessageDialog(this, "Custo salvo com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            } else {
                custoService.updateCusto(custoIdEmEdicao, request);
                JOptionPane.showMessageDialog(this, "Custo atualizado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            }
            carregarCustos();
            limparCampos();

        } catch (DateTimeParseException ex) {
            showErrorDialog("Erro de Formato", "Data inválida. Use o formato dd/MM/yyyy.");
        } catch (NumberFormatException ex) {
            showErrorDialog("Erro de Formato", "Número inválido. Use ponto ou vírgula como separador decimal.");
        } catch (ApiServiceException | IOException e) {
            showErrorDialog("Erro de Salvamento", "Não foi possível salvar o custo: " + e.getMessage());
        }
    }

    private void editarCusto() {
        int selectedRow = tabelaCustos.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um custo na tabela para editar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        custoIdEmEdicao = (Long) tableModel.getValueAt(selectedRow, 0);
        impostoField.setText(tableModel.getValueAt(selectedRow, 1).toString());
        custoVariavelField.setText(tableModel.getValueAt(selectedRow, 2).toString());
        custoFixoField.setText(tableModel.getValueAt(selectedRow, 3).toString());
        margemLucroField.setText(tableModel.getValueAt(selectedRow, 4).toString());
        dataProcessamentoField.setText(tableModel.getValueAt(selectedRow, 5).toString());
        
        TipoCusto tipoCusto = (TipoCusto) tableModel.getValueAt(selectedRow, 6);
        if (tipoCusto == TipoCusto.ENERGIA) {
            energiaRadioButton.setSelected(true);
        } else if (tipoCusto == TipoCusto.FRETE) {
            freteRadioButton.setSelected(true);
        } else if (tipoCusto == TipoCusto.AGUA) {
            aguaRadioButton.setSelected(true);
        }
    }

    private void excluirCusto() {
        int selectedRow = tabelaCustos.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um custo na tabela para excluir.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Long id = (Long) tableModel.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Tem certeza que deseja excluir este custo?", "Confirmar Exclusão", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                custoService.deleteCusto(id);
                carregarCustos();
                limparCampos();
            } catch (ApiServiceException | IOException e) {
                showErrorDialog("Erro ao Excluir", "Não foi possível excluir o custo: " + e.getMessage());
            }
        }
    }

    private void limparCampos() {
        impostoField.setText("");
        custoVariavelField.setText("");
        custoFixoField.setText("");
        margemLucroField.setText("");
        dataProcessamentoField.setText("");
        tipoCustoGroup.clearSelection();
        tabelaCustos.clearSelection();
        custoIdEmEdicao = null;
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
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setFocusPainted(false);
        button.setBackground(background);
        button.setForeground(foreground);
        button.setBorder(new EmptyBorder(8, 15, 8, 15));

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
