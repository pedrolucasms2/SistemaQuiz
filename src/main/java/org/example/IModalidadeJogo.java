package org.example;

import java.util.ArrayList;
import java.util.List;

public interface IModalidadeJogo {
    void iniciarJogo();
    void processarResposta(Jogador jogador, Resposta resposta);
    void finalizarJogo();
    List<ResultadoJogo> calcularResultados();
    boolean podeAdicionarParticipante(Jogador jogador);
    int getMaximoParticipantes();
    String getDescricaoModalidade();
}
