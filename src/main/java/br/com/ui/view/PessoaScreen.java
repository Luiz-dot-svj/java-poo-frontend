package br.com.ui.view;

import br.com.common.service.ApiServiceException;
import br.com.pessoa.dto.PessoaRequest;
import br.com.pessoa.dto.PessoaResponse;
import br.com.pessoa.enums.TipoPessoa;
import br.com.pessoa.service.PessoaService;
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

public class PessoaScreen extends JFrame {

    private JTextField nomeCompletoField, cpfCnpjField, numeroCtpsField, dataNascimentoField;
    private JRadioButton fisicaRadioButton, juridicaRadioButton;
    private ButtonGroup tipoPessoaGroup;
    private JTable tabelaPessoas;
    private DefaultTableModel tableModel;
    private Long pessoaIdEmEdicao;

    private final PessoaService pessoaService;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public PessoaScreen() {
        this.pessoaService = new PessoaService();

        setTitle("Gerenciamento de Pessoas");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        Container contentPane = getContentPane();
        contentPane.setBackground(ColorPalette.BACKGROUND);
        contentPane.setLayout(new BorderLayout(0, 0));

        contentPane.add(createHeader("Gerenciamento de Pessoas"), BorderLayout.NORTH);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, createFormPanel(), createTablePanel());
        splitPane.setDividerLocation(350);
        splitPane.setBorder(BorderFactory.createEmptyBorder());
        contentPane.add(splitPane, BorderLayout.CENTER);

        carregarPessoas();
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

        formPanel.add(createLabel("Nome Completo:"));
        nomeCompletoField = createTextField();
        formPanel.add(nomeCompletoField);
        formPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        formPanel.add(createLabel("CPF/CNPJ:"));
        cpfCnpjField = createTextField();
        formPanel.add(cpfCnpjField);
        formPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        formPanel.add(createLabel("Nº CTPS (opcional):"));
        numeroCtpsField = createTextField();
        formPanel.add(numeroCtpsField);
        formPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        formPanel.add(createLabel("Data Nascimento (dd/MM/yyyy):"));
        dataNascimentoField = createTextField();
        formPanel.add(dataNascimentoField);
        formPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        formPanel.add(createLabel("Tipo de Pessoa:"));
        
        fisicaRadioButton = new JRadioButton("Física");
        fisicaRadioButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        fisicaRadioButton.setBackground(ColorPalette.PANEL_BACKGROUND);
        fisicaRadioButton.setForeground(ColorPalette.TEXT);

        juridicaRadioButton = new JRadioButton("Jurídica");
        juridicaRadioButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        juridicaRadioButton.setBackground(ColorPalette.PANEL_BACKGROUND);
        juridicaRadioButton.setForeground(ColorPalette.TEXT);

        tipoPessoaGroup = new ButtonGroup();
        tipoPessoaGroup.add(fisicaRadioButton);
        tipoPessoaGroup.add(juridicaRadioButton);

        JPanel radioPanel = new JPanel();
        radioPanel.setLayout(new BoxLayout(radioPanel, BoxLayout.Y_AXIS));
        radioPanel.setBackground(ColorPalette.PANEL_BACKGROUND);
        radioPanel.add(fisicaRadioButton);
        radioPanel.add(juridicaRadioButton);
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
        salvarButton.addActionListener(e -> salvarPessoa());
        buttonsPanel.add(salvarButton);

        JButton editarButton = createButton("Editar", ColorPalette.ACCENT_INFO, ColorPalette.WHITE_TEXT);
        editarButton.addActionListener(e -> editarPessoa());
        buttonsPanel.add(editarButton);

        JButton excluirButton = createButton("Excluir", ColorPalette.ACCENT_INFO, ColorPalette.WHITE_TEXT);
        excluirButton.addActionListener(e -> excluirPessoa());
        buttonsPanel.add(excluirButton);

        return buttonsPanel;
    }

    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(ColorPalette.BACKGROUND);
        tablePanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        String[] colunas = {"ID", "Nome", "CPF/CNPJ", "Nº CTPS", "Nascimento", "Tipo"};
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
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, ColorPalette.BORDER_COLOR));

        JScrollPane scrollPane = new JScrollPane(tabelaPessoas);
        scrollPane.setBorder(BorderFactory.createLineBorder(ColorPalette.BORDER_COLOR));
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        return tablePanel;
    }

    private void carregarPessoas() {
        tableModel.setRowCount(0);
        try {
            List<PessoaResponse> pessoas = pessoaService.findPessoas();
            for (PessoaResponse pessoa : pessoas) {
                tableModel.addRow(new Object[]{
                        pessoa.id(),
                        pessoa.nomeCompleto(),
                        pessoa.cpfCnpj(),
                        pessoa.numeroCtps(),
                        pessoa.dataNascimento() != null ? pessoa.dataNascimento().format(dateFormatter) : "",
                        pessoa.tipoPessoa()
                });
            }
        } catch (ApiServiceException | IOException e) {
            showErrorDialog("Erro ao Carregar", "Não foi possível carregar as pessoas: " + e.getMessage());
        }
    }

    private void salvarPessoa() {
        try {
            LocalDate dataNascimento = dataNascimentoField.getText().isBlank() ? null : LocalDate.parse(dataNascimentoField.getText(), dateFormatter);
            Long numeroCtps = numeroCtpsField.getText().isBlank() ? null : Long.parseLong(numeroCtpsField.getText());

            TipoPessoa tipoPessoa = null;
            if (fisicaRadioButton.isSelected()) {
                tipoPessoa = TipoPessoa.FISICA;
            } else if (juridicaRadioButton.isSelected()) {
                tipoPessoa = TipoPessoa.JURIDICA;
            }

            PessoaRequest request = new PessoaRequest(
                    nomeCompletoField.getText(),
                    cpfCnpjField.getText(),
                    numeroCtps,
                    dataNascimento,
                    tipoPessoa
            );

            if (pessoaIdEmEdicao == null) {
                pessoaService.createPessoa(request);
                JOptionPane.showMessageDialog(this, "Pessoa salva com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            } else {
                pessoaService.updatePessoa(pessoaIdEmEdicao, request);
                JOptionPane.showMessageDialog(this, "Pessoa atualizada com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            }
            carregarPessoas();
            limparCampos();

        } catch (DateTimeParseException ex) {
            showErrorDialog("Erro de Formato", "Data inválida. Use o formato dd/MM/yyyy.");
        } catch (NumberFormatException ex) {
            showErrorDialog("Erro de Formato", "Número de CTPS inválido.");
        } catch (ApiServiceException | IOException e) {
            showErrorDialog("Erro de Salvamento", "Não foi possível salvar a pessoa: " + e.getMessage());
        }
    }

    private void editarPessoa() {
        int selectedRow = tabelaPessoas.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecione uma pessoa na tabela para editar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        pessoaIdEmEdicao = (Long) tableModel.getValueAt(selectedRow, 0);
        nomeCompletoField.setText(tableModel.getValueAt(selectedRow, 1).toString());
        cpfCnpjField.setText(tableModel.getValueAt(selectedRow, 2).toString());
        numeroCtpsField.setText(tableModel.getValueAt(selectedRow, 3) != null ? tableModel.getValueAt(selectedRow, 3).toString() : "");
        dataNascimentoField.setText(tableModel.getValueAt(selectedRow, 4) != null ? tableModel.getValueAt(selectedRow, 4).toString() : "");
        
        TipoPessoa tipoPessoa = (TipoPessoa) tableModel.getValueAt(selectedRow, 5);
        if (tipoPessoa == TipoPessoa.FISICA) {
            fisicaRadioButton.setSelected(true);
        } else if (tipoPessoa == TipoPessoa.JURIDICA) {
            juridicaRadioButton.setSelected(true);
        }
    }

    private void excluirPessoa() {
        int selectedRow = tabelaPessoas.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecione uma pessoa na tabela para excluir.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Long id = (Long) tableModel.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Tem certeza que deseja excluir esta pessoa?", "Confirmar Exclusão", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                pessoaService.deletePessoa(id);
                carregarPessoas();
                limparCampos();
            } catch (ApiServiceException | IOException e) {
                showErrorDialog("Erro ao Excluir", "Não foi possível excluir a pessoa: " + e.getMessage());
            }
        }
    }

    private void limparCampos() {
        nomeCompletoField.setText("");
        cpfCnpjField.setText("");
        numeroCtpsField.setText("");
        dataNascimentoField.setText("");
        tipoPessoaGroup.clearSelection();
        tabelaPessoas.clearSelection();
        pessoaIdEmEdicao = null;
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
