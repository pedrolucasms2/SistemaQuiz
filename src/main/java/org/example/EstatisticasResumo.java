package org.example;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class EstatisticasResumo {
    private final String nomeJogador;
    private final int totalJogos;
    private final int totalVitorias;
    private final double taxaVitoria;
    private final int pontuacaoTotal;
    private final int posicaoRanking;
    private final List<String> conquistasRecentes;
    private final Map<String, Integer> pontuacaoPorCategoria;

    public EstatisticasResumo(Jogador jogador) {
        this.nomeJogador = jogador.getNome();
        this.totalJogos = jogador.getJogosParticipados();
        this.totalVitorias = jogador.getNumeroVitorias();
        this.taxaVitoria = jogador.calcularTaxaVitoria();
        this.pontuacaoTotal = jogador.getPontuacaoTotal();

        // Buscar posição no ranking
        Ranking rankingGeral = SistemaQuiz.getInstance().getGerenciadorRanking().getRankingGeral();
        RankingPosicao posicao = rankingGeral.getPosicaoJogador(jogador);
        this.posicaoRanking = posicao != null ? posicao.getPosicao() : -1;

        // Conquistas recentes (últimas 5)
        this.conquistasRecentes = jogador.getConquistasObtidas().stream()
                .map(Conquista::getNome)
                .limit(5)
                .collect(Collectors.toList());

        // Pontuação por categoria
        this.pontuacaoPorCategoria = new HashMap<>();
        for (Categoria categoria : SistemaQuiz.getInstance().getCategorias()) {
            this.pontuacaoPorCategoria.put(
                    categoria.getNome(),
                    jogador.getPontuacaoCategoria(categoria)
            );
        }
    }

    // Getters formatados
    public String getNomeJogador() { return nomeJogador; }
    public int getTotalJogos() { return totalJogos; }
    public int getTotalVitorias() { return totalVitorias; }
    public String getTaxaVitoriaFormatada() {
        return String.format("%.1f%%", taxaVitoria * 100);
    }
    public int getPontuacaoTotal() { return pontuacaoTotal; }
    public String getPosicaoRankingFormatada() {
        return posicaoRanking > 0 ? posicaoRanking + "º lugar" : "Não rankeado";
    }
    public List<String> getConquistasRecentes() { return conquistasRecentes; }
    public Map<String, Integer> getPontuacaoPorCategoria() { return pontuacaoPorCategoria; }

    public String getCategoriaFavorita() {
        return pontuacaoPorCategoria.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("Nenhuma");
    }
}