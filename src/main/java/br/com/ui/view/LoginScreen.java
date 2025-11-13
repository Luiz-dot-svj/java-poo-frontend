package br.com.ui.view;

import br.com.auth.dto.LoginResponse;
import br.com.auth.service.AuthService;
import br.com.common.service.ApiServiceException;
import br.com.ui.util.ColorPalette;
import br.com.acesso.enums.TipoAcesso;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class LoginScreen extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton createAccountButton;
    private final AuthService authService;

    public LoginScreen() {
        this.authService = new AuthService();

        setTitle("Login - PDV Posto de Combustível");
        setSize(450, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        Container contentPane = getContentPane();
        contentPane.setBackground(ColorPalette.BACKGROUND);
        contentPane.setLayout(new BorderLayout());

        // Painel superior com logo, slogan e título
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setBackground(ColorPalette.BACKGROUND);
        topPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Logo
        JLabel logoLabel = new JLabel("PDV");
        logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        logoLabel.setForeground(ColorPalette.PRIMARY);
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        topPanel.add(logoLabel);

        // Slogan
        JLabel sloganLabel = new JLabel("Qualidade no tanque, sorriso no rosto");
        sloganLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        sloganLabel.setForeground(ColorPalette.TEXT_MUTED);
        sloganLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        topPanel.add(sloganLabel);

        // Espaçamento
        topPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Título
        JLabel titleLabel = new JLabel("Bem-vindo!");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(ColorPalette.TEXT);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        topPanel.add(titleLabel);

        contentPane.add(topPanel, BorderLayout.NORTH);

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
        formPanel.add(Box.createRigidArea(new Dimension(0, 30)));

        // Painel para os botões
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setBackground(ColorPalette.BACKGROUND);
        buttonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        loginButton = createButton("Entrar");
        buttonPanel.add(loginButton);

        createAccountButton = createButton("Criar Acesso");
        buttonPanel.add(createAccountButton);

        formPanel.add(buttonPanel);

        contentPane.add(formPanel, BorderLayout.CENTER);

        // Rodapé
        JPanel footerPanel = new JPanel();
        footerPanel.setBackground(ColorPalette.BACKGROUND);
        footerPanel.setBorder(new EmptyBorder(0, 0, 20, 0));
        JLabel footerLabel = new JLabel("© 2025 PDV Posto de Combustível");
        footerLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        footerLabel.setForeground(ColorPalette.TEXT_MUTED);
        footerPanel.add(footerLabel);
        contentPane.add(footerPanel, BorderLayout.SOUTH);

        loginButton.addActionListener(e -> authenticateUser());

        createAccountButton.addActionListener(e -> {
            new RegisterScreen().setVisible(true);
        });
    }

    private void authenticateUser() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        try {
            LoginResponse loginResponse = authService.login(username, password);
            this.dispose();

            if (loginResponse.tipoAcesso() == TipoAcesso.ADMINISTRADOR || loginResponse.tipoAcesso() == TipoAcesso.GERENCIA) {
                new MainScreen(username).setVisible(true);
            } else if (loginResponse.tipoAcesso() == TipoAcesso.FUNCIONARIO) {
                new AbastecimentoScreen(username).setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Tipo de acesso não reconhecido.", "Erro de Acesso", JOptionPane.ERROR_MESSAGE);
            }

        } catch (ApiServiceException e) {
            JOptionPane.showMessageDialog(this, "Falha na autenticação: " + e.getMessage(), "Erro de Login", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Ocorreu um erro inesperado: " + e.getMessage(), "Erro Crítico", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setForeground(ColorPalette.TEXT);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        return label;
    }

    private JTextField createTextField() {
        JTextField textField = new JTextField();
        textField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        textField.setBackground(ColorPalette.PANEL_BACKGROUND);
        textField.setForeground(ColorPalette.TEXT);
        textField.setHorizontalAlignment(JTextField.CENTER);
        textField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 1, 1, 1, ColorPalette.BORDER_COLOR),
            new EmptyBorder(10, 10, 10, 10)
        ));
        textField.setMaximumSize(new Dimension(300, 45));
        textField.setAlignmentX(Component.CENTER_ALIGNMENT);
        return textField;
    }

    private JPasswordField createPasswordField() {
        JPasswordField passwordField = new JPasswordField();
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        passwordField.setBackground(ColorPalette.PANEL_BACKGROUND);
        passwordField.setForeground(ColorPalette.TEXT);
        passwordField.setHorizontalAlignment(JPasswordField.CENTER);
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 1, 1, 1, ColorPalette.BORDER_COLOR),
            new EmptyBorder(10, 10, 10, 10)
        ));
        passwordField.setMaximumSize(new Dimension(300, 45));
        passwordField.setAlignmentX(Component.CENTER_ALIGNMENT);
        return passwordField;
    }

    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 16));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setFocusPainted(false);
        button.setBackground(ColorPalette.PRIMARY);
        button.setForeground(ColorPalette.WHITE_TEXT);
        button.setBorder(new EmptyBorder(12, 20, 12, 20));

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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(new FlatLightLaf());
            } catch (UnsupportedLookAndFeelException e) {
                e.printStackTrace();
            }
            new LoginScreen().setVisible(true);
        });
    }
}
