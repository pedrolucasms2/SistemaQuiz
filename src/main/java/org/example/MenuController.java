package org.example;

import java.util.ArrayList;
import java.util.List;

public class MenuController implements QuizObserver {
    private final SistemaQuiz sistema;
    private final MenuView view;

    public MenuController(MenuView view) {
        this.view = view;
        this.sistema = SistemaQuiz.getInstance();
        sistema.addObserver(this);
    }

    public void carregarDadosUsuario() {
        Usuario usuario = sistema.getUsuarioLogado();
        if (usuario instanceof Jogador jogador) {
            EstatisticasResumo stats = new EstatisticasResumo(jogador);
            view.exibirEstatisticas(stats);
        }
    }

    public void carregarJogosDisponiveis() {
        Usuario usuarioLogado = sistema.getUsuarioLogado();
        if (usuarioLogado instanceof Jogador jogador) {
            // Usar jogos inscritos (que já funciona) + jogos designados
            List<Jogo> jogosParaExibir = new ArrayList<>(jogador.getJogosInscritos());

            // Adicionar jogos designados para o jogador
            jogosParaExibir.addAll(jogador.getJogosDesignados());

            // Converter para JogoResumo
            List<JogoResumo> resumoJogos = jogosParaExibir.stream()
                    .map(JogoResumo::new)
                    .toList();

            view.atualizarListaJogos(resumoJogos);
        } else {
            // Comportamento original para administradores
            List<JogoResumo> jogos = sistema.listarJogosDisponiveisResumo();
            view.atualizarListaJogos(jogos);
        }
    }

    public void carregarRanking() {
        List<RankingPosicao> ranking = sistema.getGerenciadorRanking()
                .getRankingGeral().getTop10();
        view.exibirRanking(ranking);
    }

    public void processarInscricaoJogo(int jogoId) {
        try {
            Jogo jogo = sistema.buscarJogoPorId(jogoId);
            Jogador jogador = (Jogador) sistema.getUsuarioLogado();

            if (jogo.adicionarParticipante(jogador)) {
                view.exibirSucesso("Inscrição realizada com sucesso!");
                carregarJogosDisponiveis(); // Atualizar lista
            } else {
                view.exibirErro("Não foi possível se inscrever neste jogo");
            }

        } catch (Exception e) {
            view.exibirErro("Erro ao se inscrever: " + e.getMessage());
        }
    }

    @Override
    public void onEventoSistema(EventoSistema evento) {
        if (evento.getTipo() != null) {
            switch (evento.getTipo()) {
                case JOGOS_ATUALIZADOS:
                    carregarJogosDisponiveis();
                    break;
                case RANKING_ATUALIZADO:
                    carregarRanking();
                    break;
                default:
                    break;
            }
        }
    }
}