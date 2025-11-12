package br.com.ui.view;

import br.com.api.dto.BombaDTO;
import br.com.api.dto.ProdutoDTO;
import br.com.common.service.ApiServiceException;
import br.com.pessoa.dto.PessoaResponse;
import br.com.service.BombaService;
import br.com.service.PdfService;
import br.com.ui.util.ColorPalette;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class AbastecimentoScreen extends JFrame {

    private String loggedInUsername;
    private JPanel pumpsPanel;
    private BombaService bombaService;
    private PdfService pdfService;

    public AbastecimentoScreen(String username) {
        this.loggedInUsername = username;
        this.bombaService = new BombaService();
        this.pdfService = new PdfService();
        setTitle("Central de Abastecimento - PDV Posto de Combustível");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        Container contentPane = getContentPane();
        contentPane.setBackground(ColorPalette.BACKGROUND);
        contentPane.setLayout(new BorderLayout(0, 0));

        // Header
        JPanel headerPanel = createHeader();
        contentPane.add(headerPanel, BorderLayout.NORTH);

        // Painel de Bombas
        pumpsPanel = new JPanel(new GridLayout(0, 3, 20, 20));
        pumpsPanel.setOpaque(false);
        pumpsPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        JScrollPane scrollPane = new JScrollPane(pumpsPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setBackground(ColorPalette.BACKGROUND);
        contentPane.add(scrollPane, BorderLayout.CENTER);

        carregarBombas();
    }

    private JPanel createHeader() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(ColorPalette.PANEL_BACKGROUND);
        headerPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, ColorPalette.BORDER_COLOR));
        headerPanel.setPreferredSize(new Dimension(getWidth(), 60));

        JLabel titleLabel = new JLabel("Central de Abastecimento");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(ColorPalette.TEXT);
        titleLabel.setBorder(new EmptyBorder(0, 20, 0, 0));
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        userPanel.setOpaque(false);
        JLabel userLabel = new JLabel("Operador: " + loggedInUsername);
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

    private void carregarBombas() {
        SwingWorker<List<BombaDTO>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<BombaDTO> doInBackground() throws Exception {
                return bombaService.buscarTodas();
            }

            @Override
            protected void done() {
                try {
                    pumpsPanel.removeAll();
                    List<BombaDTO> bombas = get();
                    if (bombas != null && !bombas.isEmpty()) {
                        for (BombaDTO bomba : bombas) {
                            pumpsPanel.add(createPumpCard(bomba));
                        }
                    } else {
                        JLabel noPumpsLabel = new JLabel("Nenhuma bomba encontrada.");
                        noPumpsLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
                        noPumpsLabel.setHorizontalAlignment(SwingConstants.CENTER);
                        pumpsPanel.setLayout(new BorderLayout());
                        pumpsPanel.add(noPumpsLabel, BorderLayout.CENTER);
                    }
                    pumpsPanel.revalidate();
                    pumpsPanel.repaint();
                } catch (InterruptedException | ExecutionException e) {
                    handleError(e, "Erro ao carregar bombas");
                }
            }
        };
        worker.execute();
    }

    private JPanel createPumpCard(BombaDTO bomba) {
        JPanel cardPanel = new JPanel(new BorderLayout(10, 10));
        cardPanel.setBackground(ColorPalette.PANEL_BACKGROUND);
        cardPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 1, 1, 1, ColorPalette.BORDER_COLOR),
                new EmptyBorder(20, 20, 20, 20)
        ));

        // Título da Bomba
        JLabel titleLabel = new JLabel(bomba.getNome(), SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(ColorPalette.TEXT);
        cardPanel.add(titleLabel, BorderLayout.NORTH);

        // Status da Bomba
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        statusPanel.setOpaque(false);
        JLabel statusLabel = new JLabel("Status:");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        JLabel statusValue = new JLabel(bomba.getStatus());
        statusValue.setFont(new Font("Segoe UI", Font.BOLD, 16));
        
        Color statusColor;
        switch (bomba.getStatus()) {
            case "ATIVA":
                statusColor = ColorPalette.ACCENT_WARNING;
                break;
            case "CONCLUIDA":
                statusColor = ColorPalette.ACCENT_SUCCESS;
                break;
            default:
                statusColor = ColorPalette.ACCENT_DANGER;
                break;
        }
        statusValue.setForeground(statusColor);
        
        statusPanel.add(statusLabel);
        statusPanel.add(statusValue);
        cardPanel.add(statusPanel, BorderLayout.CENTER);

        // Ação
        if ("INATIVA".equals(bomba.getStatus())) {
            cardPanel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            cardPanel.addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent evt) {
                    cardPanel.setBackground(ColorPalette.PRIMARY_LIGHT);
                }

                public void mouseExited(MouseEvent evt) {
                    cardPanel.setBackground(ColorPalette.PANEL_BACKGROUND);
                }
                
                public void mouseClicked(MouseEvent e) {
                    abrirDialogoAbastecimento(bomba);
                }
            });
        } else if ("CONCLUIDA".equals(bomba.getStatus())) {
            Timer timer = new Timer(3000, evt -> resetBombaStatus(bomba));
            timer.setRepeats(false);
            timer.start();
        }

        return cardPanel;
    }

    private void resetBombaStatus(BombaDTO bomba) {
        try {
            bombaService.atualizarStatus(bomba.getId(), "INATIVA");
            carregarBombas();
        } catch (IOException | ApiServiceException ex) {
            handleError(ex, "Erro ao resetar a bomba");
        }
    }

    private void abrirDialogoAbastecimento(BombaDTO bomba) {
        AbastecimentoDialog dialog = new AbastecimentoDialog(this, bomba);
        dialog.setVisible(true);

        if (dialog.isConfirmado()) {
            try {
                bombaService.atualizarStatus(bomba.getId(), "ATIVA");
                carregarBombas();

                ProdutoDTO produto = dialog.getProdutoSelecionado();
                double litros = dialog.getLitrosAbastecidos();
                double reais = dialog.getReaisAbastecidos();
                String formaPagamento = dialog.getFormaPagamento();

                new AnimacaoAbastecimentoDialog(this, bomba, produto, litros, reais).setVisible(true);
                carregarBombas();

                promptForNfce(bomba, produto, litros, reais, formaPagamento);

            } catch (IOException | ApiServiceException e) {
                handleError(e, "Erro ao iniciar abastecimento");
            }
        }
    }

    private void promptForNfce(BombaDTO bomba, ProdutoDTO produto, double litros, double reais, String formaPagamento) {
        int resposta = JOptionPane.showConfirmDialog(this,
                "Deseja imprimir o cupom fiscal (NFC-e)?",
                "Impressão de Cupom",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (resposta == JOptionPane.YES_OPTION) {
            SelecaoClienteScreen clienteScreen = new SelecaoClienteScreen(this);
            clienteScreen.setVisible(true);

            PessoaResponse cliente = clienteScreen.getClienteSelecionado();
            if (cliente != null || clienteScreen.isConsumidorNaoIdentificado()) {
                try {
                    pdfService.gerarDanfeNfce(loggedInUsername, bomba, produto, litros, reais, cliente, formaPagamento);
                    JOptionPane.showMessageDialog(this, "PDF da NFC-e gerado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                } catch (IOException ex) {
                    handleError(ex, "Erro ao gerar PDF");
                }
            }
        }
    }

    private void handleError(Exception e, String title) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Ocorreu um erro: " + e.getMessage(), title, JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(new FlatLightLaf());
            } catch (UnsupportedLookAndFeelException e) {
                e.printStackTrace();
            }
            new AbastecimentoScreen("OperadorX").setVisible(true);
        });
    }
}
