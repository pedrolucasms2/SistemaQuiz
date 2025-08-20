package org.example;

import java.util.Date;
import java.util.stream.Collectors;

public class JogoResumo {
    private final int id;
    private final String nome;
    private final int participantes;
    private final int maxParticipantes = 50;
    private final String status;
    private final Date dataInicio;
    private final boolean podeParticipar;

    public JogoResumo(Jogo jogo) {
        this.id = jogo.getId();
        this.nome = jogo.getNome();
        this.participantes = jogo.getNumeroParticipantes();
        this.status = jogo.getStatus().toString();
        this.dataInicio = jogo.getDataInicio();
        this.podeParticipar = jogo.getStatus() == Jogo.StatusJogo.AGUARDANDO;
    }

    // Getters formatados para exibição
    public int getId() { return id; }
    public String getNome() { return nome; }
    public String getParticipantesFormatado() {
        return participantes + "/" + maxParticipantes;
    }
    public String getStatusFormatado() {
        switch (status) {
            case "AGUARDANDO": return "Aguardando jogadores";
            case "EM_ANDAMENTO": return "Em andamento";
            case "FINALIZADO": return "Finalizado";
            default: return status;
        }
    }
    public boolean isPodeParticipar() { return podeParticipar; }
    public boolean isLotado() { return participantes >= maxParticipantes; }
}