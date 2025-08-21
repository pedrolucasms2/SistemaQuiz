package org.example;

import java.util.List;
import java.util.Date;
import java.util.ArrayList;

public class Equipe {
    private int id;
    private String nome;
    private List<Jogador> membros;
    private Jogador capitao;
    private int pontuacaoTotal;
    private Date dataCriacao;

    public Equipe(String nome, Jogador capitao) {
        this.nome = nome;
        this.capitao = capitao;
        this.membros = new ArrayList<>();
        this.membros.add(capitao);
        this.pontuacaoTotal = 0;
        this.dataCriacao = new Date();
    }

    public boolean adicionarMembro(Jogador jogador) {
        if (membros.size() >= 4) return false;
        if (membros.contains(jogador)) return false;

        membros.add(jogador);
        jogador.setEquipeAtual(this);
        return true;
    }

    public void removerMembro(Jogador jogador) {
        membros.remove(jogador);
        jogador.setEquipeAtual(null);

        // Se o capitão saiu, escolher novo capitão
        if (capitao.equals(jogador) && !membros.isEmpty()) {
            capitao = membros.get(0);
        }
    }

    // Getters/Setters
    public List<Jogador> getMembros() {
        return new ArrayList<>(membros);
    }

    public int getTamanho() {
        return membros.size();
    }

    public boolean isCompleta() {
        return membros.size() == 4;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public int getPontuacaoTotal() {
        return pontuacaoTotal;
    }

    public void setPontuacaoTotal(int pontuacaoTotal) {
        this.pontuacaoTotal = pontuacaoTotal;
    }
}

