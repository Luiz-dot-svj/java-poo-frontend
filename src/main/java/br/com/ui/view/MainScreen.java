package br.com.ui.view;

import br.com.ui.util.ColorPalette;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MainScreen extends JFrame {

    private final String loggedInUsername;

    public MainScreen(String username) {
        this.loggedInUsername = username;
        setTitle("Painel Principal - PDV Posto de Combustível");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        Container contentPane = getContentPane();
        contentPane.setBackground(ColorPalette.BACKGROUND);
        contentPane.setLayout(new BorderLayout(0, 0));

        // Header
        JPanel headerPanel = createHeader();
        contentPane.add(headerPanel, BorderLayout.NORTH);

        // Navigation
        JPanel navPanel = createNavPanel();
        contentPane.add(navPanel, BorderLayout.CENTER);

        // Main Content
        JPanel mainContentPanel = createMainContentPanel();
        contentPane.add(mainContentPanel, BorderLayout.SOUTH);
    }

    private JPanel createHeader() {
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
        JLabel titleLabel = new JLabel("Painel Principal");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(ColorPalette.TEXT);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        userPanel.setOpaque(false);
        JLabel userLabel = new JLabel("Olá, " + loggedInUsername);
        userLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        userLabel.setForeground(ColorPalette.TEXT_MUTED);
        userPanel.add(userLabel);

        JButton logoutButton = new JButton("Sair");
        logoutButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        logoutButton.setForeground(ColorPalette.WHITE_TEXT);
        logoutButton.setBackground(ColorPalette.PRIMARY);
        logoutButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutButton.setFocusPainted(false);
        logoutButton.setBorder(new EmptyBorder(8, 15, 8, 15));
        logoutButton.addActionListener(e -> {
            this.dispose();
            new LoginScreen().setVisible(true);
        });
        userPanel.add(logoutButton);
        userPanel.setBorder(new EmptyBorder(0, 0, 0, 20));
        headerPanel.add(userPanel, BorderLayout.EAST);

        return headerPanel;
    }

    private JPanel createNavPanel() {
        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        navPanel.setBackground(ColorPalette.PANEL_BACKGROUND);
        navPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, ColorPalette.BORDER_COLOR));
        navPanel.setPreferredSize(new Dimension(getWidth(), 60));

        addNavButton(navPanel, "Produtos", () -> new ProdutoScreen().setVisible(true));
        addNavButton(navPanel, "Clientes", () -> new PessoaScreen().setVisible(true));
        addNavButton(navPanel, "Estoque", () -> new EstoqueScreen().setVisible(true));
        addNavButton(navPanel, "Contatos", () -> new ContatoScreen().setVisible(true));
        addNavButton(navPanel, "Custos", () -> new CustoScreen().setVisible(true));
        addNavButton(navPanel, "Preços", () -> new PrecoScreen().setVisible(true));
        addNavButton(navPanel, "Acesso", () -> new GerenciamentoAcessoScreen().setVisible(true));

        return navPanel;
    }

    private JPanel createMainContentPanel() {
        JPanel mainContentPanel = new JPanel(new BorderLayout());
        mainContentPanel.setBackground(ColorPalette.BACKGROUND);
        mainContentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        JLabel welcomeLabel = new JLabel(String.format("Bem-vindo, %s, escolha uma opção para prosseguir", loggedInUsername), SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 22));
        welcomeLabel.setForeground(ColorPalette.TEXT_MUTED);
        mainContentPanel.add(welcomeLabel, BorderLayout.CENTER);
        return mainContentPanel;
    }

    private void addNavButton(JPanel panel, String text, Runnable action) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setFocusPainted(false);
        button.setBackground(ColorPalette.PANEL_BACKGROUND);
        button.setForeground(ColorPalette.TEXT);
        button.setBorder(new EmptyBorder(10, 20, 10, 20));

        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                button.setBackground(ColorPalette.PRIMARY_LIGHT);
                button.setForeground(ColorPalette.WHITE_TEXT);
            }

            public void mouseExited(MouseEvent evt) {
                button.setBackground(ColorPalette.PANEL_BACKGROUND);
                button.setForeground(ColorPalette.TEXT);
            }
        });

        button.addActionListener(e -> action.run());
        panel.add(button);
    }
}
