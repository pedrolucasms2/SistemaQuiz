package org.example;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;

public class ResultadoJogo {
    private final Jogador jogador;
    private final Jogo jogo;
    private final int pontuacaoFinal;
    private final List<Resposta> respostas;
    private final long tempoTotalJogo;
    private final int acertos;
    private final int erros;
    private final int puladas;
    private final boolean vitoria;
    private final int posicaoFinal;

    public ResultadoJogo(Jogador jogador, Jogo jogo, int pontuacaoFinal,
                         List<Resposta> respostas, long tempoTotalJogo) {
        this.jogador = jogador;
        this.jogo = jogo;
        this.pontuacaoFinal = pontuacaoFinal;
        this.respostas = new ArrayList<>(respostas);
        this.tempoTotalJogo = tempoTotalJogo;

        // Calcular estatísticas
        this.acertos = (int) respostas.stream().filter(Resposta::isCorreta).count();
        this.erros = (int) respostas.stream()
                .filter(r -> !r.isCorreta() && !r.isPulou()).count();
        this.puladas = (int) respostas.stream().filter(Resposta::isPulou).count();

        // Determinar se foi vitória (precisa verificar com outros jogadores)
        this.vitoria = determinarVitoria();
        this.posicaoFinal = calcularPosicaoFinal();
    }

    private boolean determinarVitoria() {
        Map<Jogador, Integer> pontuacoes = jogo.getPontuacoes();
        return pontuacoes.values().stream()
                .mapToInt(Integer::intValue)
                .max()
                .orElse(0) == pontuacaoFinal;
    }

    private int calcularPosicaoFinal() {
        Map<Jogador, Integer> pontuacoes = jogo.getPontuacoes();
        return (int) pontuacoes.values().stream()
                .filter(p -> p > pontuacaoFinal)
                .count() + 1;
    }

    // Getters formatados para exibição
    public String getNomeJogador() { return jogador.getNome(); }
    public String getNomeJogo() { return jogo.getNome(); }
    public int getPontuacaoFinal() { return pontuacaoFinal; }
    public int getAcertos() { return acertos; }
    public int getErros() { return erros; }
    public int getPuladas() { return puladas; }
    public int getTotalPerguntas() { return respostas.size(); }

    public String getTempoTotalFormatado() {
        long segundos = tempoTotalJogo / 1000;
        long minutos = segundos / 60;
        segundos = segundos % 60;
        return String.format("%02d:%02d", minutos, segundos);
    }

    public String getPercentualAcerto() {
        if (respostas.isEmpty()) return "0%";
        return String.format("%.1f%%", (acertos * 100.0) / respostas.size());
    }

    public boolean isVitoria() { return vitoria; }
    public int getPosicaoFinal() { return posicaoFinal; }
    public String getPosicaoFormatada() { return posicaoFinal + "º lugar"; }

    public String getClassificacao() {
        if (vitoria) return "Vencedor!";
        if (posicaoFinal <= 3) return "Ótimo resultado!";
        if (posicaoFinal <= 5) return "Bom resultado!";
        return "Continue praticando!";
    }
}
