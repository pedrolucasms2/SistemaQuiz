package org.example;

import java.util.ArrayList;
import java.util.List;

public class TestGameUI {
    public static void main(String[] args) {
        System.out.println("=== TESTE DA UI DE JOGOS ===");

        // Criar instância do sistema (dados já são carregados automaticamente)
        SistemaQuiz sistema = SistemaQuiz.getInstance();

        // Buscar o jogador teste
        Usuario jogadorTeste = sistema.buscarUsuarioPorEmail("teste@email.com");

        if (jogadorTeste instanceof Jogador) {
            Jogador jogador = (Jogador) jogadorTeste;

            System.out.println("=== USUÁRIO ENCONTRADO ===");
            System.out.println("Nome: " + jogador.getNome());
            System.out.println("Email: " + jogador.getEmail());

            // Testar o método do MenuController
            System.out.println("\n=== TESTANDO MENUCONTROLLER ===");

            // Simular o que o MenuController faz
            List<Jogo> jogosParaExibir = new ArrayList<>(jogador.getJogosInscritos());
            jogosParaExibir.addAll(jogador.getJogosDesignados());

            System.out.println("Jogos inscritos: " + jogador.getJogosInscritos().size());
            System.out.println("Jogos designados: " + jogador.getJogosDesignados().size());
            System.out.println("Total de jogos para exibir: " + jogosParaExibir.size());

            if (jogosParaExibir.isEmpty()) {
                System.out.println("❌ PROBLEMA: Nenhum jogo para exibir!");
            } else {
                System.out.println("✅ SUCESSO: Jogos encontrados para exibir!");

                System.out.println("\n=== LISTA DE JOGOS ===");
                for (Jogo jogo : jogosParaExibir) {
                    System.out.println("- " + jogo.getNome() + " (ID: " + jogo.getId() + ")");
                }

                // Converter para JogoResumo como faz o MenuController
                List<JogoResumo> resumoJogos = jogosParaExibir.stream()
                        .map(JogoResumo::new)
                        .toList();

                System.out.println("\n=== JOGOS RESUMO (PARA UI) ===");
                for (JogoResumo resumo : resumoJogos) {
                    System.out.println("- " + resumo.getNome());
                }
            }
        } else {
            System.out.println("❌ Erro: Usuário não é um jogador ou não foi encontrado");
        }
    }
}
