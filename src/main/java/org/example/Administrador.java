package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Date;

public class Administrador extends Usuario {
    private String nivel;
    private int totalJogosCriados;
    private Date ultimoAcesso;

    public Administrador(String nome, String email, String senha, String nivel) {
        super(nome, email, senha);
        this.nivel = nivel != null ? nivel.toUpperCase() : "MODERADOR";
        this.totalJogosCriados = 0;
        this.ultimoAcesso = new Date();
    }

    @Override
    public String getTipoUsuario() {
        return "ADMINISTRADOR";
    }

    @Override
    public void exibirMenu() {
        System.out.println("=== MENU ADMINISTRADOR ===");
        System.out.println("1. Criar Jogo");
        System.out.println("2. Cadastrar Pergunta");
        System.out.println("0. Sair");
    }

    public Jogo criarJogo(String nome, int numeroRodadas, int tempoLimitePergunta) {
        List<String> erros = ValidadorInterface.validarCriacaoJogo(
                nome, numeroRodadas, tempoLimitePergunta);
        if (!erros.isEmpty()) {
            throw new IllegalArgumentException("Dados inválidos: " + String.join(", ", erros));
        }
        Jogo novoJogo = new Jogo(nome, numeroRodadas, tempoLimitePergunta, this);
        totalJogosCriados++;
        atualizarUltimoAcesso();
        return novoJogo;
    }

    public Pergunta cadastrarPergunta(String enunciado, String[] alternativas,
                                      int respostaCorreta, String dificuldade, String dica) {
        Pergunta.Dificuldade diff;
        try {
            diff = Pergunta.Dificuldade.valueOf(dificuldade.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Dificuldade inválida: " + dificuldade);
        }
        Pergunta novaPergunta = new Pergunta(enunciado, alternativas, respostaCorreta,
                diff, dica, this);
        List<String> erros = ValidadorInterface.validarCadastroPergunta(novaPergunta);
        if (!erros.isEmpty()) {
            throw new IllegalArgumentException("Pergunta inválida: " + String.join(", ", erros));
        }
        SistemaQuiz.getInstance().getPerguntas().add(novaPergunta);
        return novaPergunta;
    }

    private void atualizarUltimoAcesso() {
        this.ultimoAcesso = new Date();
    }

    public String getNivel() { return nivel; }
    public boolean isMaster() { return "MASTER".equals(nivel); }
}