package br.com.ui.view;

import br.com.api.dto.AcessoRequest;
import br.com.api.dto.AcessoResponse;
import br.com.common.service.ApiServiceException;
import br.com.service.AcessoService;
import br.com.ui.util.ColorPalette;
import br.com.acesso.enums.TipoAcesso;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

public class RegisterScreen extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JComboBox<TipoAcesso> tipoAcessoComboBox;
    private JButton registerButton;
    private JButton backButton; // Botão para voltar
    private final AcessoService acessoService;

    public RegisterScreen() {
        this.acessoService = new AcessoService();

        setTitle("Criar Novo Acesso");
        setSize(450, 550);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Fecha apenas esta janela
        setLocationRelativeTo(null);
        setResizable(false);

        Container contentPane = getContentPane();
        contentPane.setBackground(ColorPalette.BACKGROUND);
        contentPane.setLayout(new BorderLayout());

        // Header com o título
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        headerPanel.setBackground(ColorPalette.BACKGROUND);
        headerPanel.setBorder(new EmptyBorder(40, 20, 20, 20));
        JLabel titleLabel = new JLabel("Criar Novo Acesso");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(ColorPalette.TEXT);
        headerPanel.add(titleLabel);
        contentPane.add(headerPanel, BorderLayout.NORTH);

        // Painel do formulário
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(ColorPalette.BACKGROUND);
        formPanel.setBorder(new EmptyBorder(20, 40, 20, 40));

        formPanel.add(createLabel("Usuário"));
        usernameField = createTextField();
        formPanel.add(usernameField);
        formPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        formPanel.add(createLabel("Senha"));
        passwordField = createPasswordField();
        formPanel.add(passwordField);
        formPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        formPanel.add(createLabel("Tipo de Acesso"));
        tipoAcessoComboBox = createTipoAcessoComboBox();
        formPanel.add(tipoAcessoComboBox);
        formPanel.add(Box.createRigidArea(new Dimension(0, 30)));

        registerButton = createButton("Registrar");
        formPanel.add(registerButton);

        formPanel.add(Box.createRigidArea(new Dimension(0, 10))); // Espaçamento
        backButton = createSecondaryButton("Voltar");
        formPanel.add(backButton);

        contentPane.add(formPanel, BorderLayout.CENTER);

        // Rodapé (opcional, mantendo o padrão)
        JPanel footerPanel = new JPanel();
        footerPanel.setBackground(ColorPalette.BACKGROUND);
        footerPanel.setBorder(new EmptyBorder(0, 0, 20, 0));
        JLabel footerLabel = new JLabel("© 2024 PDV Posto de Combustível");
        footerLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        footerLabel.setForeground(ColorPalette.TEXT_MUTED);
        footerPanel.add(footerLabel);
        contentPane.add(footerPanel, BorderLayout.SOUTH);

        registerButton.addActionListener(e -> registerUser());
        backButton.addActionListener(e -> dispose()); // Fecha a tela de registro
    }

    private void registerUser() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        TipoAcesso tipoAcesso = (TipoAcesso) tipoAcessoComboBox.getSelectedItem();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Usuário e senha não podem ser vazios.", "Erro de Registro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        AcessoRequest request = new AcessoRequest(username, password, tipoAcesso);

        try {
            AcessoResponse response = acessoService.createAcesso(request);
            JOptionPane.showMessageDialog(this, "Acesso criado com sucesso para: " + response.usuario(), "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            this.dispose(); // Fecha a tela de registro após o sucesso
        } catch (ApiServiceException e) {
            JOptionPane.showMessageDialog(this, "Falha ao criar acesso: " + e.getMessage(), "Erro de Registro", JOptionPane.ERROR_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Erro de comunicação com o servidor: " + e.getMessage(), "Erro de Rede", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Ocorreu um erro inesperado: " + e.getMessage(), "Erro Crítico", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setForeground(ColorPalette.TEXT);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private JTextField createTextField() {
        JTextField textField = new JTextField();
        textField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        textField.setBackground(ColorPalette.PANEL_BACKGROUND);
        textField.setForeground(ColorPalette.TEXT);
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 1, 1, 1, ColorPalette.BORDER_COLOR),
                new EmptyBorder(10, 10, 10, 10)
        ));
        textField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        textField.setAlignmentX(Component.LEFT_ALIGNMENT);
        return textField;
    }

    private JPasswordField createPasswordField() {
        JPasswordField passwordField = new JPasswordField();
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        passwordField.setBackground(ColorPalette.PANEL_BACKGROUND);
        passwordField.setForeground(ColorPalette.TEXT);
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 1, 1, 1, ColorPalette.BORDER_COLOR),
                new EmptyBorder(10, 10, 10, 10)
        ));
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        passwordField.setAlignmentX(Component.LEFT_ALIGNMENT);
        return passwordField;
    }

    private JComboBox<TipoAcesso> createTipoAcessoComboBox() {
        JComboBox<TipoAcesso> comboBox = new JComboBox<>(TipoAcesso.values());
        comboBox.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        comboBox.setBackground(ColorPalette.PANEL_BACKGROUND);
        comboBox.setForeground(ColorPalette.TEXT);
        comboBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 1, 1, 1, ColorPalette.BORDER_COLOR),
                new EmptyBorder(10, 10, 10, 10)
        ));
        comboBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        comboBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        return comboBox;
    }

    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 16));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setFocusPainted(false);
        button.setBackground(ColorPalette.PRIMARY);
        button.setForeground(ColorPalette.WHITE_TEXT);
        button.setBorder(new EmptyBorder(12, 20, 12, 20));
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        button.setAlignmentX(Component.LEFT_ALIGNMENT);

        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                button.setBackground(ColorPalette.PRIMARY_DARK);
            }

            public void mouseExited(MouseEvent evt) {
                button.setBackground(ColorPalette.PRIMARY);
            }
        });

        return button;
    }

    private JButton createSecondaryButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 16));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setFocusPainted(false);
        button.setBackground(ColorPalette.BACKGROUND); // Cor de fundo diferente
        button.setForeground(ColorPalette.PRIMARY); // Cor do texto principal
        button.setBorder(BorderFactory.createLineBorder(ColorPalette.PRIMARY, 1)); // Borda primária
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        button.setAlignmentX(Component.LEFT_ALIGNMENT);

        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                button.setBackground(ColorPalette.PRIMARY_LIGHT); // Efeito hover
                button.setForeground(ColorPalette.WHITE_TEXT);
            }

            public void mouseExited(MouseEvent evt) {
                button.setBackground(ColorPalette.BACKGROUND);
                button.setForeground(ColorPalette.PRIMARY);
            }
        });

        return button;
    }
}
