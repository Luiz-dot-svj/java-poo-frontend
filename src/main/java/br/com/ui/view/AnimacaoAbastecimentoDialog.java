package br.com.ui.view;

import br.com.api.dto.BombaDTO;
import br.com.api.dto.ProdutoDTO;
import br.com.common.service.ApiServiceException;
import br.com.service.BombaService;
import br.com.ui.util.ColorPalette;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.IOException;

public class AnimacaoAbastecimentoDialog extends JDialog {

    private final JProgressBar progressBar;
    private final JLabel litrosLabel;
    private final JLabel reaisLabel;
    private final BombaService bombaService;

    public AnimacaoAbastecimentoDialog(Frame owner, BombaDTO bomba, ProdutoDTO produto, double litros, double reais) {
        super(owner, "Abastecendo...", true);
        this.bombaService = new BombaService();

        setSize(450, 250);
        setLocationRelativeTo(owner);
        setResizable(false);
        
        Container contentPane = getContentPane();
        contentPane.setBackground(ColorPalette.PANEL_BACKGROUND);
        contentPane.setLayout(new BorderLayout(10, 10));
        ((JPanel) contentPane).setBorder(new EmptyBorder(20, 20, 20, 20));

        // Título
        JLabel titleLabel = new JLabel("Abastecendo Bomba " + bomba.getNome(), SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(ColorPalette.TEXT);
        contentPane.add(titleLabel, BorderLayout.NORTH);

        // Painel de informações
        JPanel infoPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        infoPanel.setOpaque(false);
        litrosLabel = new JLabel("Litros: 0.00");
        litrosLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        reaisLabel = new JLabel("Reais: R$ 0.00");
        reaisLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        infoPanel.add(litrosLabel);
        infoPanel.add(reaisLabel);
        contentPane.add(infoPanel, BorderLayout.CENTER);

        // Barra de progresso
        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        progressBar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        progressBar.setForeground(ColorPalette.PRIMARY);
        progressBar.setBackground(ColorPalette.BACKGROUND);
        contentPane.add(progressBar, BorderLayout.SOUTH);

        startAnimation(bomba, litros, reais);
    }

    private void startAnimation(BombaDTO bomba, double totalLitros, double totalReais) {
        Thread animationThread = new Thread(() -> {
            try {
                for (int i = 0; i <= 100; i++) {
                    Thread.sleep(50); // Simula o tempo de abastecimento
                    double litrosAtuais = (totalLitros * i) / 100;
                    double reaisAtuais = (totalReais * i) / 100;
                    final int progress = i;

                    SwingUtilities.invokeLater(() -> {
                        litrosLabel.setText(String.format("Litros: %.2f", litrosAtuais));
                        reaisLabel.setText(String.format("Reais: R$ %.2f", reaisAtuais));
                        progressBar.setValue(progress);
                        progressBar.setString(progress + "%");
                    });
                }

                bombaService.atualizarStatus(bomba.getId(), "CONCLUIDA");

                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(this, "Abastecimento concluído com sucesso!", "Concluído", JOptionPane.INFORMATION_MESSAGE);
                    dispose();
                });

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                showError("Animação interrompida.");
            } catch (IOException | ApiServiceException e) {
                showError("Erro ao finalizar abastecimento: " + e.getMessage());
            }
        });
        animationThread.start();
    }

    private void showError(String message) {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(this, message, "Erro", JOptionPane.ERROR_MESSAGE);
            dispose();
        });
    }
}
