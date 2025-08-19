package org.example;

import java.util.*;

public class GerenciadorConquistas {
    private List<Conquista> conquistasDisponiveis;
    private Map<Jogador, Set<Conquista>> conquistasPorJogador;

    public GerenciadorConquistas() {
        this.conquistasDisponiveis = new ArrayList<>();
        this.conquistasPorJogador = new HashMap<>();
        inicializarConquistas();
    }

    private void inicializarConquistas() {
        // Conquistas básicas
        conquistasDisponiveis.add(new Conquista("Primeira Vitória", "Ganhe seu primeiro jogo",
                Conquista.TipoConquista.VITORIA, 1, false));
        conquistasDisponiveis.add(new Conquista("Veterano", "Participe de 100 jogos",
                Conquista.TipoConquista.PERFORMANCE, 100, true));
        conquistasDisponiveis.add(new Conquista("Especialista", "Alcance 1000 pontos",
                Conquista.TipoConquista.PERFORMANCE, 1000, false));
    }

    public List<Conquista> verificarNovasConquistas(Jogador jogador) {
        List<Conquista> novasConquistas = new ArrayList<>();
        Set<Conquista> conquistasJogador = conquistasPorJogador.computeIfAbsent(jogador, k -> new HashSet<>());

        for (Conquista conquista : conquistasDisponiveis) {
            if (!conquistasJogador.contains(conquista) && conquista.verificarCriterio(jogador)) {
                conquistasJogador.add(conquista);
                jogador.adicionarConquista(conquista);
                novasConquistas.add(conquista);
            }
        }

        return novasConquistas;
    }

    public void adicionarConquista(Conquista conquista) {
        if (!conquistasDisponiveis.contains(conquista)) {
            conquistasDisponiveis.add(conquista);
        }
    }

    public List<Conquista> getConquistasDisponiveis() {
        return new ArrayList<>(conquistasDisponiveis);
    }

    public Set<Conquista> getConquistasJogador(Jogador jogador) {
        return new HashSet<>(conquistasPorJogador.getOrDefault(jogador, new HashSet<>()));
    }
}

