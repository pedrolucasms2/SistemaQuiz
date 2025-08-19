package org.example;

import java.util.Date;

public class EventoSistema {
    public enum TipoEvento {
        LOGIN_REALIZADO,
        LOGOUT_REALIZADO,
        JOGADOR_CADASTRADO,
        ADMIN_CADASTRADO,
        JOGO_CRIADO,
        RESPOSTA_PROCESSADA,
        RANKING_ATUALIZADO,
        NOVA_CONQUISTA,
        JOGO_INICIADO,
        JOGO_FINALIZADO,
        JOGADOR_INSCRITO,
        JOGADOR_REMOVIDO,
        JOGO_PAUSADO,
        JOGO_RETOMADO,
        PROXIMA_PERGUNTA,
        PROXIMA_RODADA,
        JOGO_CANCELADO,
        JOGOS_ATUALIZADOS,
        RANKING_GERAL_ATUALIZADO
    }

    private TipoEvento tipo;
    private Object dados;
    private Date timestamp;
    private String descricao;

    public EventoSistema(String tipoString, Object dados) {
        this.tipo = TipoEvento.valueOf(tipoString);
        this.dados = dados;
        this.timestamp = new Date();
        this.descricao = gerarDescricao();
    }

    public EventoSistema(TipoEvento tipo, Object dados) {
        this.tipo = tipo;
        this.dados = dados;
        this.timestamp = new Date();
        this.descricao = gerarDescricao();
    }

    private String gerarDescricao() {
        switch (tipo) {
            case LOGIN_REALIZADO:
                if (dados instanceof Usuario) {
                    return "Usuário " + ((Usuario) dados).getNome() + " fez login";
                }
                break;
            case NOVA_CONQUISTA:
                if (dados instanceof Conquista) {
                    return "Nova conquista obtida: " + ((Conquista) dados).getNome();
                }
                break;
            case JOGO_INICIADO:
                if (dados instanceof Jogo) {
                    return "Jogo " + ((Jogo) dados).getNome() + " foi iniciado";
                }
                break;
            case JOGADOR_INSCRITO:
                if (dados instanceof Usuario) {
                    return "Usuário " + ((Usuario) dados).getNome() + "se inscreveu no jogo";
                }
            case JOGADOR_REMOVIDO:
                if (dados instanceof Usuario) {
                    return "Usuário " + ((Usuario) dados).getNome() + "foi removido do jogo";
                }
            default:
                return tipo.toString();
        }
        return tipo.toString();
    }

    // Getters
    public TipoEvento getTipo() { return tipo; }
    public String getTipoString() { return tipo.toString(); }
    public Object getDados() { return dados; }
    public Date getTimestamp() { return timestamp; }
    public String getDescricao() { return descricao; }
}