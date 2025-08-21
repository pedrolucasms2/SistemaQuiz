package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import java.util.stream.Collectors;

public class Ranking {
    public enum TipoRanking {
        GERAL, POR_CATEGORIA, MENSAL, ANUAL
    }

    private TipoRanking tipo;
    private Categoria categoria; // null se for ranking geral
    private List<RankingPosicao> posicoes;
    private Date ultimaAtualizacao;

    public Ranking(TipoRanking tipo, Categoria categoria) {
        this.tipo = tipo;
        this.categoria = categoria;
        this.posicoes = new ArrayList<>();
        this.ultimaAtualizacao = new Date();
    }

    public void atualizar(List<Jogador> jogadores) {
        posicoes.clear();

        // Ordenar jogadores por pontuação
        jogadores.sort((j1, j2) -> {
            int pontos1 = categoria != null ? j1.getPontuacaoCategoria(categoria) : j1.getPontuacaoTotal();
            int pontos2 = categoria != null ? j2.getPontuacaoCategoria(categoria) : j2.getPontuacaoTotal();

            if (pontos1 != pontos2) {
                return Integer.compare(pontos2, pontos1); // Ordem decrescente
            }

            // Critério de desempate: menor número de jogos (mais eficiente)
            return Integer.compare(j1.getJogosParticipados(), j2.getJogosParticipados());
        });

        // Criar posições
        for (int i = 0; i < jogadores.size(); i++) {
            posicoes.add(new RankingPosicao(i + 1, jogadores.get(i)));
        }

        ultimaAtualizacao = new Date();
    }

    public RankingPosicao getPosicaoJogador(Jogador jogador) {
        return posicoes.stream()
                .filter(p -> p.getJogador().equals(jogador))
                .findFirst()
                .orElse(null);
    }

    public List<RankingPosicao> getTop10() {
        return posicoes.stream()
                .limit(10)
                .collect(Collectors.toList());
    }

    // Getters
    public TipoRanking getTipo() { return tipo; }
    public Categoria getCategoria() { return categoria; }
    public List<RankingPosicao> getPosicoes() { return new ArrayList<>(posicoes); }
}

