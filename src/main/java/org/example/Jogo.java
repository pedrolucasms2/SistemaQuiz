package org.example;

import java.util.*;
import java.util.stream.Collectors;

public class Jogo {
    public enum StatusJogo {
        AGUARDANDO("Aguardando Jogadores"),
        EM_ANDAMENTO("Em Andamento"),
        FINALIZADO("Finalizado"),
        CANCELADO("Cancelado");
        private final String descricao;

        StatusJogo(String descricao) {
            this.descricao = descricao;
        }
        public String getDescricao() { return descricao; }
    }

    private int id;
    private String nome;
    private int numeroRodadas;
    private int tempoLimitePergunta;
    private List<Jogador> participantes;
    private List<Pergunta> perguntasJogo;
    private StatusJogo status;
    private Date dataCriacao;
    private Date dataInicio;
    private Administrador criador;
    private Map<Jogador, Integer> pontuacoes;
    private static int proximoId = 1;

    public Jogo(String nome, int numeroRodadas, int tempoLimitePergunta, Administrador criador) {
        this.id = proximoId++;
        this.nome = nome;
        this.numeroRodadas = numeroRodadas;
        this.tempoLimitePergunta = tempoLimitePergunta;
        this.criador = criador;
        this.participantes = new ArrayList<>();
        this.perguntasJogo = new ArrayList<>();
        this.pontuacoes = new HashMap<>();
        this.status = StatusJogo.AGUARDANDO;
        this.dataCriacao = new Date();
    }

    public boolean adicionarParticipante(Jogador jogador) {
        if (jogador == null) {
            throw new IllegalArgumentException("Jogador não pode ser nulo");
        }
        if (status != StatusJogo.AGUARDANDO) {
            return false;
        }
        if (participantes.contains(jogador)) {
            return false;
        }
        participantes.add(jogador);
        pontuacoes.put(jogador, 0);
        return true;
    }

    public void iniciar() {
        if (participantes.isEmpty()) {
            throw new IllegalStateException("Não é possível iniciar jogo sem participantes");
        }
        if (status != StatusJogo.AGUARDANDO) {
            throw new IllegalStateException("Jogo não está aguardando para iniciar");
        }
        status = StatusJogo.EM_ANDAMENTO;
        dataInicio = new Date();
        selecionarPerguntas();
    }

    public void processarResposta(Jogador jogador, Resposta resposta) {
        if (status != StatusJogo.EM_ANDAMENTO) {
            throw new IllegalStateException("Jogo não está em andamento");
        }
        if (!participantes.contains(jogador)) {
            throw new IllegalArgumentException("Jogador não participa deste jogo");
        }
        int pontosGanhos = resposta.calcularPontos();
        int pontuacaoAtual = pontuacoes.getOrDefault(jogador, 0);
        pontuacoes.put(jogador, pontuacaoAtual + pontosGanhos);
        jogador.adicionarPontos(pontosGanhos);
    }

    public void finalizar() {
        if (status != StatusJogo.EM_ANDAMENTO) {
            throw new IllegalStateException("Jogo não pode ser finalizado no estado atual");
        }
        status = StatusJogo.FINALIZADO;
        for (Jogador jogador : participantes) {
            jogador.incrementarJogosParticipados();
        }
    }

    private void selecionarPerguntas() {
        perguntasJogo.clear();
        int perguntasPorRodada = 10;
        int totalPerguntas = numeroRodadas * perguntasPorRodada;
        List<Pergunta> todasPerguntas = SistemaQuiz.getInstance().getPerguntas();

        if (todasPerguntas.size() < totalPerguntas) {
            throw new IllegalStateException("Não há perguntas suficientes cadastradas");
        }
        Collections.shuffle(todasPerguntas);
        perguntasJogo.addAll(todasPerguntas.subList(0, Math.min(totalPerguntas, todasPerguntas.size())));
    }

    // Getters
    public int getId() { return id; }
    public String getNome() { return nome; }
    public List<Jogador> getParticipantes() { return new ArrayList<>(participantes); }
    public List<Pergunta> getPerguntasJogo() { return new ArrayList<>(perguntasJogo); }
    public StatusJogo getStatus() { return status; }
    public int getNumeroParticipantes() { return participantes.size(); }
    public Map<Jogador, Integer> getPontuacoes() { return new HashMap<>(pontuacoes); }
    public int getTempoLimitePergunta() { return tempoLimitePergunta; }
    public Date getDataInicio() { return dataInicio; }
}