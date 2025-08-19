package org.example;

import java.util.*;
import java.util.stream.Collectors;

public class GerenciadorRanking {
    private Ranking rankingGeral;
    private Ranking rankingMensal;
    private Ranking rankingAnual;
    private Map<Categoria, Ranking> rankingsPorCategoria;
    private Date ultimaAtualizacao;

    public GerenciadorRanking() {
        this.rankingGeral = new Ranking(Ranking.TipoRanking.GERAL, null);
        this.rankingMensal = new Ranking(Ranking.TipoRanking.MENSAL, null);
        this.rankingAnual = new Ranking(Ranking.TipoRanking.ANUAL, null);
        this.rankingsPorCategoria = new HashMap<>();
        this.ultimaAtualizacao = new Date();
    }

    public void atualizarRankings() {
        List<Jogador> todosJogadores = obterTodosJogadores();

        // Atualizar ranking geral
        rankingGeral.atualizar(todosJogadores);

        // Atualizar ranking mensal
        List<Jogador> jogadoresMensais = filtrarJogadoresPorPeriodo(todosJogadores, 30);
        rankingMensal.atualizar(jogadoresMensais);

        // Atualizar ranking anual
        List<Jogador> jogadoresAnuais = filtrarJogadoresPorPeriodo(todosJogadores, 365);
        rankingAnual.atualizar(jogadoresAnuais);

        // Atualizar rankings por categoria
        atualizarRankingsPorCategoria(todosJogadores);

        ultimaAtualizacao = new Date();
    }

    private List<Jogador> obterTodosJogadores() {
        return SistemaQuiz.getInstance().getUsuarios().stream()
                .filter(u -> u instanceof Jogador)
                .map(u -> (Jogador) u)
                .collect(Collectors.toList());
    }

    private List<Jogador> filtrarJogadoresPorPeriodo(List<Jogador> jogadores, int dias) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -dias);
        Date dataLimite = cal.getTime();

        return jogadores.stream()
                .filter(j -> j.getUltimoLogin().after(dataLimite))
                .collect(Collectors.toList());
    }

    private void atualizarRankingsPorCategoria(List<Jogador> jogadores) {
        for (Categoria categoria : SistemaQuiz.getInstance().getCategorias()) {
            Ranking rankingCategoria = rankingsPorCategoria.computeIfAbsent(
                    categoria, k -> new Ranking(Ranking.TipoRanking.POR_CATEGORIA, categoria));

            rankingCategoria.atualizar(jogadores);
        }
    }

    // Getters
    public Ranking getRankingGeral() { return rankingGeral; }
    public Ranking getRankingMensal() { return rankingMensal; }
    public Ranking getRankingAnual() { return rankingAnual; }

    public Ranking getRankingPorCategoria(Categoria categoria) {
        return rankingsPorCategoria.get(categoria);
    }

    public Date getUltimaAtualizacao() { return new Date(ultimaAtualizacao.getTime()); }
}

