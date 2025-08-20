package org.example;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class Jogador extends Usuario {
    private int pontuacaoTotal;
    private int jogosParticipados;
    private Date ultimoLogin;
    private List<Jogo> jogosInscritos;
    private Map<String, Object> estatisticas;

    public Jogador(String nome, String email, String senha) {
        super(nome, email, senha);
        this.pontuacaoTotal = 0;
        this.jogosParticipados = 0;
        this.ultimoLogin = new Date();
        this.jogosInscritos = new ArrayList<>();
        this.estatisticas = new HashMap<>();
        inicializarEstatisticas();
    }

    @Override
    public String getTipoUsuario() {
        return "JOGADOR";
    }

    @Override
    public void exibirMenu() {
        System.out.println("=== MENU JOGADOR ===");
        System.out.println("1. Participar de Jogo");
        System.out.println("2. Ver Minhas Estatísticas");
        System.out.println("0. Sair");
    }

    public boolean participarJogo(Jogo jogo) {
        if (jogo == null) {
            return false;
        }
        if (jogo.getStatus() != Jogo.StatusJogo.AGUARDANDO) {
            throw new IllegalStateException("Jogo não está aguardando participantes");
        }
        if (jogosInscritos.contains(jogo)) {
            throw new IllegalStateException("Já está inscrito neste jogo");
        }
        boolean sucesso = jogo.adicionarParticipante(this);
        if (sucesso) {
            jogosInscritos.add(jogo);
            atualizarUltimoLogin();
        }
        return sucesso;
    }

    public void adicionarPontos(int pontos) {
        if (pontos > 0) {
            this.pontuacaoTotal += pontos;
            Integer pontosGanhos = (Integer) estatisticas.getOrDefault("pontosGanhosTotal", 0);
            estatisticas.put("pontosGanhosTotal", pontosGanhos + pontos);
        }
    }

    public void incrementarJogosParticipados() {
        this.jogosParticipados++;
        atualizarUltimoLogin();
    }

    private void inicializarEstatisticas() {
        estatisticas.put("pontosGanhosTotal", 0);
    }

    private void atualizarUltimoLogin() {
        this.ultimoLogin = new Date();
    }

    public int getPontuacaoTotal() { return pontuacaoTotal; }
    public int getJogosParticipados() { return jogosParticipados; }
    public Date getUltimoLogin() { return new Date(ultimoLogin.getTime()); }
    public List<Jogo> getJogosInscritos() { return new ArrayList<>(jogosInscritos); }
}