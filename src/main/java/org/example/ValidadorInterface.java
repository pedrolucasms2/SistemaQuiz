package org.example;

import java.util.List;
import java.util.ArrayList;

public class ValidadorInterface {

    public static boolean validarEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        String regex = "^[\\w\\.-]+@([\\w\\-]+\\.)+[\\w\\-]{2,4}$";
        return email.matches(regex);
    }

    public static boolean validarSenha(String senha) {
        return senha != null && senha.length() >= 6;
    }

    public static List<String> validarCadastroPergunta(Pergunta pergunta) {
        List<String> erros = new ArrayList<>();
        if (pergunta.getEnunciado() == null || pergunta.getEnunciado().trim().isEmpty()) {
            erros.add("Enunciado é obrigatório");
        }
        if (pergunta.getEnunciado() != null && pergunta.getEnunciado().length() > 500) {
            erros.add("Enunciado deve ter no máximo 500 caracteres");
        }
        String[] alternativas = pergunta.getAlternativas();
        if (alternativas == null || alternativas.length != 4) {
            erros.add("Deve ter exatamente 4 alternativas");
        } else {
            for (int i = 0; i < alternativas.length; i++) {
                if (alternativas[i] == null || alternativas[i].trim().isEmpty()) {
                    erros.add("Alternativa " + (i + 1) + " é obrigatória");
                }
                if (alternativas[i] != null && alternativas[i].length() > 200) {
                    erros.add("Alternativa " + (i + 1) + " deve ter no máximo 200 caracteres");
                }
            }
        }
        if (pergunta.getRespostaCorreta() < 0 || pergunta.getRespostaCorreta() > 3) {
            erros.add("Resposta correta deve estar entre 1 e 4");
        }
        return erros;
    }

    public static List<String> validarCriacaoJogo(String nome,
                                                  int numeroRodadas, int tempoLimitePergunta) {
        List<String> erros = new ArrayList<>();
        if (nome == null || nome.trim().isEmpty()) {
            erros.add("Nome do jogo é obrigatório");
        }
        if (nome != null && nome.length() > 100) {
            erros.add("Nome do jogo deve ter no máximo 100 caracteres");
        }
        if (numeroRodadas < 1 || numeroRodadas > 10) {
            erros.add("Número de rodadas deve estar entre 1 e 10");
        }
        if (tempoLimitePergunta < 10 || tempoLimitePergunta > 300) {
            erros.add("Tempo limite deve estar entre 10 e 300 segundos");
        }
        return erros;
    }

    public static boolean validarNome(String nome) {
        return nome != null && nome.trim().length() >= 2 && nome.trim().length() <= 50;
    }

    public static boolean validarIdade(int idade) {
        return idade >= 13 && idade <= 120;
    }
}