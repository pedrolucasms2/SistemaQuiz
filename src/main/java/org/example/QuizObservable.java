package org.example;

import java.util.List;
import java.util.ArrayList;

public class QuizObservable {
    private List<QuizObserver> observers;
    private boolean changed;

    public QuizObservable() {
        this.observers = new ArrayList<>();
        this.changed = false;
    }

    public synchronized void addObserver(QuizObserver observer) {
        if (observer != null && !observers.contains(observer)) {
            observers.add(observer);
        }
    }

    public synchronized void removeObserver(QuizObserver observer) {
        observers.remove(observer);
    }

    public synchronized void removeAllObservers() {
        observers.clear();
    }

    protected synchronized void setChanged() {
        changed = true;
    }

    protected synchronized void clearChanged() {
        changed = false;
    }

    public synchronized boolean hasChanged() {
        return changed;
    }

    public void notifyObservers(EventoSistema evento) {
        List<QuizObserver> observersToNotify = null;

        synchronized(this) {
            if (!hasChanged()) return;

            observersToNotify = new ArrayList<>(observers);
            clearChanged();
        }

        // Notificar fora do bloco sincronizado para evitar deadlocks
        for (QuizObserver observer : observersToNotify) {
            try {
                observer.onEventoSistema(evento);
            } catch (Exception e) {
                System.err.println("Erro ao notificar observer: " + e.getMessage());
            }
        }
    }

    public synchronized int countObservers() {
        return observers.size();
    }
}

