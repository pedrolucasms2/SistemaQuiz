package org.example;

import java.util.Date;

public class Pergunta {
    // Enumeração para dificuldade
    public enum Dificuldade {
        FACIL(10), MEDIO(20), DIFICIL(30), EXPERT(50);

        private final int pontuacaoBase;
        Dificuldade(int pontuacao) { this.pontuacaoBase = pontuacao; }
        public int getPontuacaoBase() { return pontuacaoBase; }
    }

    // Atributos privados
    private int id;
    private String enunciado;
    private String[] alternativas; // 4 alternativas
    private int respostaCorreta; // índice da resposta correta (0-3)
    private Categoria categoria;
    private Dificuldade dificuldade;
    private String dica;
    private boolean ativa;
    private int totalUsos;
    private int totalAcertos;
    private Date dataCriacao;
    private Administrador criador;

    // Construtor
    public Pergunta(String enunciado, String[] alternativas, int respostaCorreta,
                    Categoria categoria, Dificuldade dificuldade, String dica,
                    Administrador criador) {
        this.enunciado = enunciado;
        this.alternativas = alternativas.clone();
        this.respostaCorreta = respostaCorreta;
        this.categoria = categoria;
        this.dificuldade = dificuldade;
        this.dica = dica;
        this.criador = criador;
        this.ativa = true;
        this.totalUsos = 0;
        this.totalAcertos = 0;
        this.dataCriacao = new Date();
    }

    // Métodos públicos
    public boolean verificarResposta(int alternativaEscolhida) {
        boolean correto = alternativaEscolhida == respostaCorreta;
        totalUsos++;
        if (correto) totalAcertos++;
        return correto;
    }

    public int calcularPontuacao(long tempoResposta, boolean usouDica, boolean pulou) {
        if (pulou) return 0;

        int pontos = dificuldade.getPontuacaoBase();

        // Penalidade por dica
        if (usouDica) {
            pontos = (int) (pontos * 0.7); // 30% de penalidade
        }

        // Bonus por velocidade (se respondeu em menos de 10 segundos)
        if (tempoResposta < 10000) { // 10 segundos em milissegundos
            pontos = (int) (pontos * 1.2); // 20% de bonus
        }

        return pontos;
    }

    public double getTaxaAcerto() {
        return totalUsos > 0 ? (double) totalAcertos / totalUsos : 0.0;
    }

    // Getters/Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getEnunciado() { return enunciado; }
    public void setEnunciado(String enunciado) { this.enunciado = enunciado; }
    public String[] getAlternativas() { return alternativas.clone(); }
    public void setAlternativas(String[] alternativas) { this.alternativas = alternativas.clone(); }
    public int getRespostaCorreta() { return respostaCorreta; }
    public Categoria getCategoria() { return categoria; }
    public Dificuldade getDificuldade() { return dificuldade; }
    public String getDica() { return dica; }
    public void setDica(String dica) { this.dica = dica; }
    public boolean isAtiva() { return ativa; }
    public void setAtiva(boolean ativa) { this.ativa = ativa; }
    public int getTotalUsos() { return totalUsos; }
    public int getTotalAcertos() { return totalAcertos; }
}

