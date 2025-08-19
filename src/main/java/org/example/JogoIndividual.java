package org.example;


import java.util.ArrayList;
import java.util.List;

public class JogoIndividual implements IModalidadeJogo {
    private static final int MAXIMO_PARTICIPANTES = 50;
    private String nome = "Individual";
    private String descricao = "Cada jogador compete individualmente";

    @Override
    public void iniciarJogo() {
        // Implementação específica para jogo individual
        System.out.println("Iniciando jogo individual...");
        // Cada jogador responde às perguntas independentemente
    }

    @Override
    public void processarResposta(Jogador jogador, Resposta resposta) {
        // Em jogo individual, cada resposta é processada independentemente
        // Não há interação entre jogadores

        if (resposta.isCorreta()) {
            System.out.println(jogador.getNome() + " acertou! +" + resposta.getPontosObtidos() + " pontos");
        } else if (resposta.isPulou()) {
            System.out.println(jogador.getNome() + " pulou a pergunta");
        } else {
            System.out.println(jogador.getNome() + " errou a pergunta");
        }
    }

    @Override
    public void finalizarJogo() {
        System.out.println("Finalizando jogo individual...");
        // Calcular ranking final baseado na pontuação individual
    }

    @Override
    public List<ResultadoJogo> calcularResultados() {
        // Retorna lista vazia - resultados são calculados pelo Jogo principal
        return new ArrayList<>();
    }

    @Override
    public boolean podeAdicionarParticipante(Jogador jogador) {
        // Em jogo individual, qualquer jogador pode participar
        return jogador != null && jogador.isAtivo();
    }

    @Override
    public int getMaximoParticipantes() {
        return MAXIMO_PARTICIPANTES;
    }

    @Override
    public String getDescricaoModalidade() {
        return descricao;
    }

    public String getNome() { return nome; }

    @Override
    public String toString() {
        return nome + " - " + descricao;
    }
}


