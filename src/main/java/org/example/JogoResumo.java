package org.example;

import java.util.Date;
import java.util.stream.Collectors;

public class JogoResumo {
    private final int id;
    private final String nome;
    private final String categorias;
    private final int participantes;
    private final int maxParticipantes;
    private final String modalidade;
    private final String status;
    private final Date dataInicio;
    private final boolean podeParticipar;

    public JogoResumo(Jogo jogo) {
        this.id = jogo.getId();
        this.nome = jogo.getNome();
        this.categorias = jogo.getCategorias().stream()
                .map(Categoria::getNome)
                .collect(Collectors.joining(", "));
        this.participantes = jogo.getNumeroParticipantes();
        this.maxParticipantes = jogo.getModalidade().getMaximoParticipantes();
        this.modalidade = jogo.getModalidade().getDescricaoModalidade();
        this.status = jogo.getStatus().toString();
        this.dataInicio = jogo.getDataInicio();
        this.podeParticipar = jogo.getStatus() == Jogo.StatusJogo.EM_ANDAMENTO;
    }

    // Getters formatados para exibição
    public int getId() { return id; }
    public String getNome() { return nome; }
    public String getCategorias() { return categorias; }
    public String getParticipantesFormatado() {
        return participantes + "/" + maxParticipantes;
    }
    public String getModalidade() { return modalidade; }
    public String getStatusFormatado() {
        switch (status) {
            case "EM_ANDAMENTO": return "Em andamento";
            case "FINALIZADO": return "Finalizado";
            default: return status;
        }
    }
    public boolean isPodeParticipar() { return podeParticipar; }
    public boolean isLotado() { return participantes >= maxParticipantes; }
}
