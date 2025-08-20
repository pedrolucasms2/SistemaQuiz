package org.example;

import java.util.List;

public interface MenuView {
    void atualizarListaJogos(List<JogoResumo> jogos);
    void exibirSucesso(String mensagem);
    void exibirErro(String mensagem);
}