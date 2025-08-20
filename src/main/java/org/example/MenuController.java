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

    public void carregarJogosDisponiveis() {
        List<JogoResumo> jogos = sistema.listarJogosDisponiveisResumo();
        view.atualizarListaJogos(jogos);
    }

    public void processarInscricaoJogo(int jogoId) {
        try {
            Jogo jogo = sistema.buscarJogoPorId(jogoId);
            Jogador jogador = (Jogador) sistema.getUsuarioLogado();

            if (jogo.adicionarParticipante(jogador)) {
                view.exibirSucesso("Inscrição realizada com sucesso!");
                carregarJogosDisponiveis();
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
                case JOGO_CRIADO:
                case JOGADOR_INSCRITO:
                    carregarJogosDisponiveis();
                    break;
                default:
                    break;
            }
        }
    }
}