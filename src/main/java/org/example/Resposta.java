package org.example;

import java.util.Date;
import java.util.Objects;

public class Resposta {
    private Jogador jogador;
    private Pergunta pergunta;
    private int alternativaEscolhida;
    private long tempoResposta; // em milissegundos
    private boolean usouDica;
    private boolean pulou;
    private Date timestamp;
    private boolean correta;
    private int pontosObtidos;

    public Resposta(Jogador jogador, Pergunta pergunta, int alternativaEscolhida,
                    long tempoResposta, boolean usouDica, boolean pulou) {
        this.jogador = jogador;
        this.pergunta = pergunta;
        this.alternativaEscolhida = alternativaEscolhida;
        this.tempoResposta = tempoResposta;
        this.usouDica = usouDica;
        this.pulou = pulou;
        this.timestamp = new Date();
        this.correta = !pulou && pergunta.verificarResposta(alternativaEscolhida);
        this.pontosObtidos = calcularPontos();
    }

    public int calcularPontos() {
        if (pulou) return 0;
        if (!correta) return 0;

        return pergunta.calcularPontuacao(tempoResposta, usouDica, pulou);
    }

    // Getters FALTANTES
    public Jogador getJogador() { return jogador; }
    public Pergunta getPergunta() { return pergunta; }
    public int getAlternativaEscolhida() { return alternativaEscolhida; }
    public long getTempoResposta() { return tempoResposta; }
    public boolean isUsouDica() { return usouDica; }
    public boolean isPulou() { return pulou; }
    public Date getTimestamp() { return new Date(timestamp.getTime()); }
    public boolean isCorreta() { return correta; }
    public int getPontosObtidos() { return pontosObtidos; }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Resposta resposta = (Resposta) obj;
        return Objects.equals(jogador, resposta.jogador) &&
                Objects.equals(pergunta, resposta.pergunta) &&
                Objects.equals(timestamp, resposta.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(jogador, pergunta, timestamp);
    }

    @Override
    public String toString() {
        return String.format("Resposta{jogador=%s, correta=%s, pontos=%d}",
                jogador.getNome(), correta, pontosObtidos);
    }
}

