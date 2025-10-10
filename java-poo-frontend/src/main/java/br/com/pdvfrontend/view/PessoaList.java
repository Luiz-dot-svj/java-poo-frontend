package br.com.pdvfrontend.view;

import br.com.pdvfrontend.model.Pessoa;
import br.com.pdvfrontend.service.PessoaService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PessoaList extends JFrame {

    private PessoaService pessoaService = new PessoaService();
    private JTable table;

    public PessoaList() {
        setTitle("Cadastro de Pessoas");
        setSize(800, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JButton btnNovo = new JButton("Novo");
        JButton btnEditar = new JButton("Editar");
        JButton btnExcluir = new JButton("Excluir");

        btnNovo.addActionListener(e -> new PessoaForm(null, this));
        btnEditar.addActionListener(e -> editarPessoa());
        btnExcluir.addActionListener(e -> excluirPessoa());

        JPanel botoes = new JPanel();
        botoes.add(btnNovo);
        botoes.add(btnEditar);
        botoes.add(btnExcluir);

        table = new JTable();
        JScrollPane scroll = new JScrollPane(table);

        add(scroll, BorderLayout.CENTER);
        add(botoes, BorderLayout.SOUTH);

        atualizarTabela();
        setVisible(true);
    }

    public void atualizarTabela() {
        List<Pessoa> pessoas = pessoaService.listar();
        DefaultTableModel model = new DefaultTableModel(new Object[]{"ID", "Nome", "CPF", "Email"}, 0);
        for (Pessoa p : pessoas) {
            model.addRow(new Object[]{p.getId(), p.getNome(), p.getCpf(), p.getEmail()});
        }
        table.setModel(model);
    }

    private void editarPessoa() {
        int linha = table.getSelectedRow();
        if (linha != -1) {
            Long id = (Long) table.getValueAt(linha, 0);
            Pessoa pessoa = pessoaService.buscarPorId(id);
            new PessoaForm(pessoa, this);
        } else {
            JOptionPane.showMessageDialog(this, "Selecione uma pessoa para editar.");
        }
    }

    private void excluirPessoa() {
        int linha = table.getSelectedRow();
        if (linha != -1) {
            Long id = (Long) table.getValueAt(linha, 0);
            pessoaService.deletar(id);
            atualizarTabela();
        } else {
            JOptionPane.showMessageDialog(this, "Selecione uma pessoa para excluir.");
        }
    }
}