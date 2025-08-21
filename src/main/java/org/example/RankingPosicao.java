package org.example;

import java.util.Date;

public class RankingPosicao {
    private int posicao;
    private Jogador jogador;
    private int pontuacao;
    private Date dataRanking;

    public RankingPosicao(int posicao, Jogador jogador) {
        this.posicao = posicao;
        this.jogador = jogador;
        this.pontuacao = jogador.getPontuacaoTotal();
        this.dataRanking = new Date();
    }

    // Getters
    public int getPosicao() { return posicao; }
    public Jogador getJogador() { return jogador; }
    public int getPontuacao() { return pontuacao; }
    public Date getDataRanking() { return dataRanking; }
}
