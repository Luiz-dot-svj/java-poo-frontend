package br.com.pdvfrontend.view;

import br.com.pdvfrontend.model.Pessoa;
import br.com.pdvfrontend.service.PessoaService;

import javax.swing.*;
import java.awt.*;

public class PessoaForm extends JDialog {

    private JTextField txtNome, txtCpf, txtEmail;
    private PessoaService pessoaService = new PessoaService();
    private Pessoa pessoa;
    private PessoaList parent;

    public PessoaForm(Pessoa pessoa, PessoaList parent) {
        this.pessoa = pessoa;
        this.parent = parent;

        setTitle(pessoa == null ? "Nova Pessoa" : "Editar Pessoa");
        setSize(400, 250);
        setLayout(new GridLayout(4, 2));
        setLocationRelativeTo(parent);

        add(new JLabel("Nome:"));
        txtNome = new JTextField();
        add(txtNome);

        add(new JLabel("CPF:"));
        txtCpf = new JTextField();
        add(txtCpf);

        add(new JLabel("Email:"));
        txtEmail = new JTextField();
        add(txtEmail);

        JButton btnSalvar = new JButton("Salvar");
        btnSalvar.addActionListener(e -> salvar());
        add(btnSalvar);

        if (pessoa != null) {
            txtNome.setText(pessoa.getNome());
            txtCpf.setText(pessoa.getCpf());
            txtEmail.setText(pessoa.getEmail());
        }

        setVisible(true);
    }

    private void salvar() {
        if (pessoa == null) pessoa = new Pessoa();

        pessoa.setNome(txtNome.getText());
        pessoa.setCpf(txtCpf.getText());
        pessoa.setEmail(txtEmail.getText());

        if (pessoa.getId() == null)
            pessoaService.salvar(pessoa);
        else
            pessoaService.atualizar(pessoa);

        parent.atualizarTabela();
        dispose();
    }
}