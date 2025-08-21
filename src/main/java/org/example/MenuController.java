package org.example;

import java.util.List;

public class MenuController implements QuizObserver {
    private SistemaQuiz sistema;
    private MenuView view;

    public MenuController(MenuView view) {
        this.view = view;
        this.sistema = SistemaQuiz.getInstance();
        sistema.addObserver(this);
    }

    public void carregarDadosUsuario() {
        Usuario usuario = sistema.getUsuarioLogado();
        if (usuario instanceof Jogador) {
            Jogador jogador = (Jogador) usuario;
            EstatisticasResumo stats = new EstatisticasResumo(jogador);
            view.exibirEstatisticas(stats);
        }
    }

    public void carregarJogosDisponiveis() {
        List<JogoResumo> jogos = sistema.listarJogosDisponiveisResumo();
        view.atualizarListaJogos(jogos);
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