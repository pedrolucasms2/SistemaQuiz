package org.example;

import java.util.Date;
import java.util.Objects;

public class EstatisticaJogador {
    private String nome;
    private String valor;
    private String unidade;
    private String categoria;
    private Date dataAtualizacao;

    public EstatisticaJogador(String nome, String valor) {
        this.nome = nome;
        this.valor = valor;
        this.unidade = "";
        this.categoria = "Geral";
        this.dataAtualizacao = new Date();
    }

    public EstatisticaJogador(String nome, String valor, String unidade, String categoria) {
        this.nome = nome;
        this.valor = valor;
        this.unidade = unidade != null ? unidade : "";
        this.categoria = categoria != null ? categoria : "Geral";
        this.dataAtualizacao = new Date();
    }

    // Getters/Setters
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getValor() { return valor; }
    public void setValor(String valor) {
        this.valor = valor;
        this.dataAtualizacao = new Date();
    }

    public String getUnidade() { return unidade; }
    public void setUnidade(String unidade) { this.unidade = unidade; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public Date getDataAtualizacao() { return new Date(dataAtualizacao.getTime()); }

    public String getValorFormatado() {
        return valor + (unidade.isEmpty() ? "" : " " + unidade);
    }

    @Override
    public String toString() {
        return nome + ": " + getValorFormatado();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        EstatisticaJogador that = (EstatisticaJogador) obj;
        return Objects.equals(nome, that.nome) && Objects.equals(categoria, that.categoria);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nome, categoria);
    }
}
