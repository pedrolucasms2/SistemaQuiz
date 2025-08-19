package org.example;

import java.util.List;
import java.util.ArrayList;

public class JogoEquipe implements IModalidadeJogo {
    private static final int MAXIMO_PARTICIPANTES = 40; // 10 equipes x 4 jogadores
    private static final int TAMANHO_EQUIPE = 4;
    private static final int MAXIMO_EQUIPES = 10;

    private String nome = "Equipe";
    private String descricao = "Jogadores competem em equipes de até " + TAMANHO_EQUIPE + " membros";
    private List<Equipe> equipesJogo;

    public JogoEquipe() {
        this.equipesJogo = new ArrayList<>();
    }

    @Override
    public void iniciarJogo() {
        System.out.println("Iniciando jogo em equipe...");

        // Organizar jogadores em equipes se necessário
        organizarEquipes();

        System.out.println("Equipes formadas: " + equipesJogo.size());
        for (Equipe equipe : equipesJogo) {
            System.out.println("- " + equipe.getNome() + " (" + equipe.getTamanho() + " membros)");
        }
    }

    @Override
    public void processarResposta(Jogador jogador, Resposta resposta) {
        // Em jogo de equipe, a resposta afeta toda a equipe
        Equipe equipe = jogador.getEquipeAtual();

        if (equipe != null) {
            // Adicionar pontos para todos os membros da equipe
            int pontos = resposta.getPontosObtidos();
            for (Jogador membro : equipe.getMembros()) {
                membro.adicionarPontos(pontos, resposta.getPergunta().getCategoria());
            }

            if (resposta.isCorreta()) {
                System.out.println("Equipe " + equipe.getNome() + " acertou! +" + pontos + " pontos para cada membro");
            } else if (resposta.isPulou()) {
                System.out.println("Equipe " + equipe.getNome() + " pulou a pergunta");
            } else {
                System.out.println("Equipe " + equipe.getNome() + " errou a pergunta");
            }
        }
    }

    @Override
    public void finalizarJogo() {
        System.out.println("Finalizando jogo em equipe...");

        // Calcular pontuação das equipes
        for (Equipe equipe : equipesJogo) {
            int pontuacaoEquipe = equipe.getMembros().stream()
                    .mapToInt(Jogador::getPontuacaoTotal)
                    .sum();
            equipe.setPontuacaoTotal(pontuacaoEquipe);
        }

        // Ordenar equipes por pontuação
        equipesJogo.sort((e1, e2) -> Integer.compare(e2.getPontuacaoTotal(), e1.getPontuacaoTotal()));

        System.out.println("Ranking das equipes:");
        for (int i = 0; i < equipesJogo.size(); i++) {
            Equipe equipe = equipesJogo.get(i);
            System.out.println((i + 1) + "º lugar: " + equipe.getNome() +
                    " - " + equipe.getPontuacaoTotal() + " pontos");
        }
    }

    @Override
    public List<ResultadoJogo> calcularResultados() {
        List<ResultadoJogo> resultados = new ArrayList<>();

        // Criar resultados baseados nas equipes
        for (Equipe equipe : equipesJogo) {
            for (Jogador jogador : equipe.getMembros()) {
                // Resultado individual dentro do contexto da equipe
                // Implementação específica para equipes
            }
        }

        return resultados;
    }

    @Override
    public boolean podeAdicionarParticipante(Jogador jogador) {
        if (jogador == null || !jogador.isAtivo()) {
            return false;
        }

        // Verificar se ainda há espaço em equipes
        int totalParticipantes = equipesJogo.stream()
                .mapToInt(Equipe::getTamanho)
                .sum();

        return totalParticipantes < MAXIMO_PARTICIPANTES;
    }

    @Override
    public int getMaximoParticipantes() {
        return MAXIMO_PARTICIPANTES;
    }

    @Override
    public String getDescricaoModalidade() {
        return descricao;
    }

    // Métodos específicos de equipe
    public boolean adicionarEquipe(Equipe equipe) {
        if (equipesJogo.size() < MAXIMO_EQUIPES && !equipesJogo.contains(equipe)) {
            equipesJogo.add(equipe);
            return true;
        }
        return false;
    }

    public boolean removerEquipe(Equipe equipe) {
        return equipesJogo.remove(equipe);
    }

    private void organizarEquipes() {
        // Implementação para organizar jogadores em equipes automaticamente
        // se alguns jogadores não estiverem em equipes
    }

    public List<Equipe> getEquipesJogo() {
        return new ArrayList<>(equipesJogo);
    }

    public int getNumeroEquipes() {
        return equipesJogo.size();
    }

    public String getNome() { return nome; }

    @Override
    public String toString() {
        return nome + " - " + descricao;
    }
}
