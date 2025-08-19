package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

public class SessaoJogo {
    private Jogo jogo;
    private Jogador jogador;
    private int perguntaAtual;
    private long inicioTempo;
    private long inicioJogo;
    private List<Resposta> respostasJogador;
    private int pontuacaoAtual;
    private boolean usouDicaNaPerguntaAtual;
    private Timer timerPergunta;

    public SessaoJogo(Jogo jogo, Jogador jogador) {
        this.jogo = jogo;
        this.jogador = jogador;
        this.perguntaAtual = 0;
        this.respostasJogador = new ArrayList<>();
        this.pontuacaoAtual = 0;
        this.inicioJogo = System.currentTimeMillis();
        this.usouDicaNaPerguntaAtual = false;
        iniciarPrimeiraPergunta();
    }

    private void iniciarPrimeiraPergunta() {
        inicioTempo = System.currentTimeMillis();
    }

    public void proximaPergunta() {
        if (temProximaPergunta()) {
            perguntaAtual++;
            inicioTempo = System.currentTimeMillis();
            usouDicaNaPerguntaAtual = false;
        }
    }

    public boolean temProximaPergunta() {
        return perguntaAtual < jogo.getPerguntasJogo().size() - 1;
    }

    public long getTempoRestante() {
        long tempoDecorrido = System.currentTimeMillis() - inicioTempo;
        long tempoLimite = jogo.getTempoLimitePergunta() * 1000L;
        return Math.max(0, tempoLimite - tempoDecorrido);
    }

    public void salvarResposta(int alternativaEscolhida) {
        Pergunta pergunta = getPerguntaAtual();
        long tempoResposta = System.currentTimeMillis() - inicioTempo;

        Resposta resposta = new Resposta(
                jogador,
                pergunta,
                alternativaEscolhida,
                tempoResposta,
                usouDicaNaPerguntaAtual,
                false
        );

        respostasJogador.add(resposta);
        pontuacaoAtual += resposta.getPontosObtidos();

        SistemaQuiz sistemaQuiz = SistemaQuiz.getInstance();
        sistemaQuiz.processarResposta(jogo, jogador, resposta);
    }

    public void pularPergunta() {
        Pergunta pergunta = getPerguntaAtual();

        Resposta resposta = new Resposta(
                jogador,
                pergunta,
                -1, // Nenhuma alternativa escolhida
                0,
                false,
                true // Marcado como pulado
        );

        respostasJogador.add(resposta);
        // Pular não adiciona pontos
    }

    public void marcarTempoEsgotado() {
        pularPergunta(); // Tratar como pergunta pulada
    }

    public void marcarUsoDidica() {
        usouDicaNaPerguntaAtual = true;
    }

    public ResultadoJogo finalizarJogo() {
        long tempoTotalJogo = System.currentTimeMillis() - inicioJogo;

        return new ResultadoJogo(
                jogador,
                jogo,
                pontuacaoAtual,
                respostasJogador,
                tempoTotalJogo
        );
    }

    // Getters para interface
    public Pergunta getPerguntaAtual() {
        return jogo.getPerguntasJogo().get(perguntaAtual);
    }

    public int getProgresso() {
        return (perguntaAtual * 100) / jogo.getPerguntasJogo().size();
    }

    public int getPontuacaoAtual() {
        return pontuacaoAtual;
    }

    public boolean isUsouDicaNaPerguntaAtual() {
        return usouDicaNaPerguntaAtual;
    }

    public int getTotalPerguntas() {
        return jogo.getPerguntasJogo().size();
    }

    public int getPerguntaNumero() {
        return perguntaAtual + 1;
    }
}

