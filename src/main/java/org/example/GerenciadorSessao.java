package org.example;

import javax.swing.Timer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class GerenciadorSessao {
    private SessaoJogo sessaoAtual;
    private Timer timerGlobal;
    private List<SessaoListener> listeners;

    public GerenciadorSessao() {
        this.listeners = new ArrayList<>();
    }

    public void iniciarSessao(SessaoJogo sessao) {
        if (sessaoAtual != null) {
            finalizarSessao();
        }

        sessaoAtual = sessao;
        iniciarTimerGlobal();
        notificarListeners("SESSAO_INICIADA", sessao);
    }

    public void pausarSessao() {
        if (timerGlobal != null && timerGlobal.isRunning()) {
            timerGlobal.stop();
        }
        notificarListeners("SESSAO_PAUSADA", sessaoAtual);
    }

    public void retomarSessao() {
        if (sessaoAtual != null) {
            iniciarTimerGlobal();
            notificarListeners("SESSAO_RETOMADA", sessaoAtual);
        }
    }

    public void finalizarSessao() {
        if (timerGlobal != null && timerGlobal.isRunning()) {
            timerGlobal.stop();
            timerGlobal = null;
        }

        if (sessaoAtual != null) {
            notificarListeners("SESSAO_FINALIZADA", sessaoAtual);
            sessaoAtual = null;
        }
    }

    private void iniciarTimerGlobal() {
        timerGlobal = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (sessaoAtual != null) {
                    notificarListeners("TEMPO_ATUALIZADO", sessaoAtual.getTempoRestante());

                    if (sessaoAtual.getTempoRestante() <= 0) {
                        notificarListeners("TEMPO_ESGOTADO", sessaoAtual);
                    }
                }
            }
        });
        timerGlobal.start(); // CORRIGIDO
    }

    public void adicionarListener(SessaoListener listener) {
        listeners.add(listener);
    }

    public void removerListener(SessaoListener listener) {
        listeners.remove(listener);
    }

    private void notificarListeners(String evento, Object dados) {
        for (SessaoListener listener : listeners) {
            listener.onEventoSessao(evento, dados);
        }
    }

    public SessaoJogo getSessaoAtual() {
        return sessaoAtual;
    }

    public boolean temSessaoAtiva() {
        return sessaoAtual != null;
    }
}


