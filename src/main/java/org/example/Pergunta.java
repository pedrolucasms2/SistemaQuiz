package org.example;

import java.util.Date;

public class Pergunta {
    public enum Dificuldade {
        FACIL(10), MEDIO(20), DIFICIL(30), EXPERT(50);

        private final int pontuacaoBase;
        Dificuldade(int pontuacao) { this.pontuacaoBase = pontuacao; }
        public int getPontuacaoBase() { return pontuacaoBase; }
    }

    private int id;
    private String enunciado;
    private String[] alternativas;
    private int respostaCorreta;
    private Dificuldade dificuldade;
    private String dica;
    private boolean ativa;
    private int totalUsos;
    private int totalAcertos;
    private Date dataCriacao;
    private Administrador criador;

    public Pergunta(String enunciado, String[] alternativas, int respostaCorreta,
                    Dificuldade dificuldade, String dica, Administrador criador) {
        this.enunciado = enunciado;
        this.alternativas = alternativas.clone();
        this.respostaCorreta = respostaCorreta;
        this.dificuldade = dificuldade;
        this.dica = dica;
        this.criador = criador;
        this.ativa = true;
        this.totalUsos = 0;
        this.totalAcertos = 0;
        this.dataCriacao = new Date();
    }

    public boolean verificarResposta(int alternativaEscolhida) {
        boolean correto = alternativaEscolhida == respostaCorreta;
        totalUsos++;
        if (correto) totalAcertos++;
        return correto;
    }

    public int calcularPontuacao(long tempoResposta, boolean usouDica, boolean pulou) {
        if (pulou) return 0;
        int pontos = dificuldade.getPontuacaoBase();
        if (usouDica) {
            pontos = (int) (pontos * 0.7);
        }
        if (tempoResposta < 10000) {
            pontos = (int) (pontos * 1.2);
        }
        return pontos;
    }

    public double getTaxaAcerto() {
        return totalUsos > 0 ? (double) totalAcertos / totalUsos : 0.0;
    }

    public int getId() { return id; }
    public String getEnunciado() { return enunciado; }
    public String[] getAlternativas() { return alternativas.clone(); }
    public int getRespostaCorreta() { return respostaCorreta; }
    public Dificuldade getDificuldade() { return dificuldade; }
    public String getDica() { return dica; }
    public boolean isAtiva() { return ativa; }
}