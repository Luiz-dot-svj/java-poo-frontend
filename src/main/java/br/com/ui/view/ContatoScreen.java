package br.com.ui.view;

import br.com.common.service.ApiServiceException;
import br.com.contato.dto.ContatoRequest;
import br.com.contato.dto.ContatoResponse;
import br.com.contato.enums.TipoContato;
import br.com.contato.service.ContatoService;
import br.com.pessoa.dto.PessoaResponse;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContatoScreen extends JFrame {

    private JTextField telefoneField, emailField, enderecoField, pessoaField;
    private JRadioButton clienteRadioButton, fornecedorRadioButton, funcionarioRadioButton;
    private ButtonGroup tipoContatoGroup;
    private JButton selecionarPessoaButton;
    private PessoaResponse pessoaSelecionada;
    private JTable tabelaContatos;
    private DefaultTableModel tableModel;
    private Long contatoIdEmEdicao;

    private final ContatoService contatoService;
    private final PessoaService pessoaService;
    private final Map<Long, PessoaResponse> pessoasMap;

    public ContatoScreen() {
        this.contatoService = new ContatoService();
        this.pessoaService = new PessoaService();
        this.pessoasMap = new HashMap<>();

        setTitle("Gerenciamento de Contatos");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        Container contentPane = getContentPane();
        contentPane.setBackground(ColorPalette.BACKGROUND);
        contentPane.setLayout(new BorderLayout(0, 0));

        contentPane.add(createHeader("Gerenciamento de Contatos"), BorderLayout.NORTH);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, createFormPanel(), createTablePanel());
        splitPane.setDividerLocation(350);
        splitPane.setBorder(BorderFactory.createEmptyBorder());
        contentPane.add(splitPane, BorderLayout.CENTER);

        carregarMapaPessoas();
        carregarContatos();
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

        formPanel.add(createLabel("Pessoa:"));
        formPanel.add(createPessoaSelectionPanel());
        formPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        formPanel.add(createLabel("Telefone:"));
        telefoneField = createTextField();
        formPanel.add(telefoneField);
        formPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        formPanel.add(createLabel("Email:"));
        emailField = createTextField();
        formPanel.add(emailField);
        formPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        formPanel.add(createLabel("Endereço:"));
        enderecoField = createTextField();
        formPanel.add(enderecoField);
        formPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        formPanel.add(createLabel("Tipo de Contato:"));
        
        clienteRadioButton = new JRadioButton("Cliente");
        clienteRadioButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        clienteRadioButton.setBackground(ColorPalette.PANEL_BACKGROUND);
        clienteRadioButton.setForeground(ColorPalette.TEXT);

        fornecedorRadioButton = new JRadioButton("Fornecedor");
        fornecedorRadioButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        fornecedorRadioButton.setBackground(ColorPalette.PANEL_BACKGROUND);
        fornecedorRadioButton.setForeground(ColorPalette.TEXT);

        funcionarioRadioButton = new JRadioButton("Funcionário");
        funcionarioRadioButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        funcionarioRadioButton.setBackground(ColorPalette.PANEL_BACKGROUND);
        funcionarioRadioButton.setForeground(ColorPalette.TEXT);

        tipoContatoGroup = new ButtonGroup();
        tipoContatoGroup.add(clienteRadioButton);
        tipoContatoGroup.add(fornecedorRadioButton);
        tipoContatoGroup.add(funcionarioRadioButton);

        JPanel radioPanel = new JPanel();
        radioPanel.setLayout(new BoxLayout(radioPanel, BoxLayout.Y_AXIS));
        radioPanel.setBackground(ColorPalette.PANEL_BACKGROUND);
        radioPanel.add(clienteRadioButton);
        radioPanel.add(fornecedorRadioButton);
        radioPanel.add(funcionarioRadioButton);
        radioPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        formPanel.add(radioPanel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        formPanel.add(createButtonsPanel());
        formPanel.add(Box.createVerticalGlue());

        return formPanel;
    }

    private JPanel createPessoaSelectionPanel() {
        JPanel pessoaPanel = new JPanel(new BorderLayout(5, 0));
        pessoaPanel.setOpaque(false);
        pessoaPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        pessoaPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        pessoaField = createTextField();
        pessoaField.setEditable(false);
        pessoaPanel.add(pessoaField, BorderLayout.CENTER);

        selecionarPessoaButton = new JButton("...");
        selecionarPessoaButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        selecionarPessoaButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        selecionarPessoaButton.addActionListener(e -> abrirSelecaoPessoa());
        pessoaPanel.add(selecionarPessoaButton, BorderLayout.EAST);

        return pessoaPanel;
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
        salvarButton.addActionListener(e -> salvarContato());
        buttonsPanel.add(salvarButton);

        JButton editarButton = createButton("Editar", ColorPalette.ACCENT_INFO, ColorPalette.WHITE_TEXT);
        editarButton.addActionListener(e -> editarContato());
        buttonsPanel.add(editarButton);

        JButton excluirButton = createButton("Excluir", ColorPalette.ACCENT_INFO, ColorPalette.WHITE_TEXT);
        excluirButton.addActionListener(e -> excluirContato());
        buttonsPanel.add(excluirButton);

        return buttonsPanel;
    }

    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(ColorPalette.BACKGROUND);
        tablePanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        String[] colunas = {"ID", "Pessoa", "Telefone", "Email", "Endereço", "Tipo"};
        tableModel = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabelaContatos = new JTable(tableModel);
        tabelaContatos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabelaContatos.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tabelaContatos.setRowHeight(30);
        tabelaContatos.setGridColor(ColorPalette.BORDER_COLOR);

        JTableHeader header = tabelaContatos.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(ColorPalette.PANEL_BACKGROUND);
        header.setForeground(ColorPalette.TEXT);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, ColorPalette.BORDER_COLOR));

        JScrollPane scrollPane = new JScrollPane(tabelaContatos);
        scrollPane.setBorder(BorderFactory.createLineBorder(ColorPalette.BORDER_COLOR));
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        return tablePanel;
    }

    private void abrirSelecaoPessoa() {
        SelecaoPessoaScreen selecaoPessoaScreen = new SelecaoPessoaScreen(this);
        selecaoPessoaScreen.setVisible(true);
        PessoaResponse pessoa = selecaoPessoaScreen.getPessoaSelecionada();
        if (pessoa != null) {
            this.pessoaSelecionada = pessoa;
            pessoaField.setText(pessoa.nomeCompleto());
        }
    }

    private void carregarMapaPessoas() {
        try {
            List<PessoaResponse> pessoas = pessoaService.findPessoas();
            pessoasMap.clear();
            for (PessoaResponse pessoa : pessoas) {
                pessoasMap.put(pessoa.id(), pessoa);
            }
        } catch (ApiServiceException | IOException e) {
            showErrorDialog("Erro ao Carregar Pessoas", "Não foi possível carregar os dados das pessoas: " + e.getMessage());
        }
    }

    private void carregarContatos() {
        tableModel.setRowCount(0);
        try {
            List<ContatoResponse> contatos = contatoService.findContatos();
            for (ContatoResponse contato : contatos) {
                PessoaResponse pessoa = pessoasMap.get(contato.pessoaId());
                String nomePessoa = (pessoa != null) ? pessoa.nomeCompleto() : "ID: " + contato.pessoaId();
                tableModel.addRow(new Object[]{
                        contato.id(),
                        nomePessoa,
                        contato.telefone(),
                        contato.email(),
                        contato.endereco(),
                        contato.tipoContato()
                });
            }
        } catch (ApiServiceException | IOException e) {
            showErrorDialog("Erro ao Carregar Contatos", "Não foi possível carregar os contatos: " + e.getMessage());
        }
    }

    private void salvarContato() {
        try {
            if (pessoaSelecionada == null) {
                showErrorDialog("Validação", "É necessário selecionar uma pessoa.");
                return;
            }

            TipoContato tipoContato = null;
            if (clienteRadioButton.isSelected()) {
                tipoContato = TipoContato.CLIENTE;
            } else if (fornecedorRadioButton.isSelected()) {
                tipoContato = TipoContato.FORNECEDOR;
            } else if (funcionarioRadioButton.isSelected()) {
                tipoContato = TipoContato.FUNCIONARIO;
            }

            ContatoRequest request = new ContatoRequest(
                    telefoneField.getText(),
                    emailField.getText(),
                    enderecoField.getText(),
                    tipoContato,
                    pessoaSelecionada.id()
            );

            if (contatoIdEmEdicao == null) {
                contatoService.createContato(request);
                JOptionPane.showMessageDialog(this, "Contato salvo com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            } else {
                contatoService.updateContato(contatoIdEmEdicao, request);
                JOptionPane.showMessageDialog(this, "Contato atualizado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            }
            carregarContatos();
            limparCampos();

        } catch (ApiServiceException | IOException e) {
            showErrorDialog("Erro de Salvamento", "Não foi possível salvar o contato: " + e.getMessage());
        }
    }

    private void editarContato() {
        int selectedRow = tabelaContatos.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um contato na tabela para editar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        contatoIdEmEdicao = (Long) tableModel.getValueAt(selectedRow, 0);
        try {
            ContatoResponse contato = contatoService.findContatoById(contatoIdEmEdicao);
            telefoneField.setText(contato.telefone());
            emailField.setText(contato.email());
            enderecoField.setText(contato.endereco());
            
            TipoContato tipoContato = contato.tipoContato();
            if (tipoContato == TipoContato.CLIENTE) {
                clienteRadioButton.setSelected(true);
            } else if (tipoContato == TipoContato.FORNECEDOR) {
                fornecedorRadioButton.setSelected(true);
            } else if (tipoContato == TipoContato.FUNCIONARIO) {
                funcionarioRadioButton.setSelected(true);
            }

            pessoaSelecionada = pessoasMap.get(contato.pessoaId());
            pessoaField.setText(pessoaSelecionada != null ? pessoaSelecionada.nomeCompleto() : "");
        } catch (ApiServiceException | IOException e) {
            showErrorDialog("Erro ao Editar", "Não foi possível carregar os dados do contato: " + e.getMessage());
        }
    }

    private void excluirContato() {
        int selectedRow = tabelaContatos.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um contato na tabela para excluir.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Long id = (Long) tableModel.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Tem certeza que deseja excluir este contato?", "Confirmar Exclusão", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                contatoService.deleteContato(id);
                carregarContatos();
                limparCampos();
            } catch (ApiServiceException | IOException e) {
                showErrorDialog("Erro ao Excluir", "Não foi possível excluir o contato: " + e.getMessage());
            }
        }
    }

    private void limparCampos() {
        telefoneField.setText("");
        emailField.setText("");
        enderecoField.setText("");
        tipoContatoGroup.clearSelection();
        pessoaField.setText("");
        pessoaSelecionada = null;
        tabelaContatos.clearSelection();
        contatoIdEmEdicao = null;
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
