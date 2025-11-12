package br.com.api.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class PrecoDTO {

    private Long id;
    private LocalDate dataAlteracao;
    private LocalDate horaAlteracao;
    private BigDecimal valor;
    private Long produtoId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDataAlteracao() {
        return dataAlteracao;
    }

    public void setDataAlteracao(LocalDate dataAlteracao) {
        this.dataAlteracao = dataAlteracao;
    }

    public LocalDate getHoraAlteracao() {
        return horaAlteracao;
    }

    public void setHoraAlteracao(LocalDate horaAlteracao) {
        this.horaAlteracao = horaAlteracao;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public Long getProdutoId() {
        return produtoId;
    }

    public void setProdutoId(Long produtoId) {
        this.produtoId = produtoId;
    }
}
