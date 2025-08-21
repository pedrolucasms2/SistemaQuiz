package org.example;

import java.util.List;

public interface MenuView {
    void exibirEstatisticas(EstatisticasResumo estatisticas);
    void atualizarListaJogos(List<JogoResumo> jogos);
    void exibirRanking(List<RankingPosicao> ranking);
    void exibirSucesso(String mensagem);
    void exibirErro(String mensagem);
}

