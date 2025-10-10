package br.com;

import javax.swing.*;

import br.com.pdvfrontend.view.PessoaList;
import com.formdev.flatlaf.FlatLightLaf;

public class Main {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(PessoaList::new);
    }
}
