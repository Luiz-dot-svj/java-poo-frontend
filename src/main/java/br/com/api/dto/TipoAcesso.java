package br.com.api.dto;

public enum TipoAcesso {

    admin("Acesso Administrador"),
    func("Acesso Funcionário"),
    ger("Acesso Gerência");

    private  final String descricao;

    private TipoAcesso(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
