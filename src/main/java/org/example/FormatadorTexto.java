package org.example;

import java.util.Date;
import java.text.SimpleDateFormat;

public class FormatadorTexto {

    public static String formatarTempo(long milissegundos) {
        long segundos = milissegundos / 1000;
        long minutos = segundos / 60;
        segundos = segundos % 60;

        if (minutos > 0) {
            return String.format("%d:%02d", minutos, segundos);
        } else {
            return String.format("%ds", segundos);
        }
    }

    public static String formatarPontuacao(int pontos) {
        if (pontos >= 1000000) {
            return String.format("%.1fM", pontos / 1000000.0);
        } else if (pontos >= 1000) {
            return String.format("%.1fK", pontos / 1000.0);
        } else {
            return String.valueOf(pontos);
        }
    }

    public static String formatarRanking(int posicao, int total) {
        return String.format("%dº de %d", posicao, total);
    }

    public static String formatarPercentual(double valor) {
        return String.format("%.1f%%", valor * 100);
    }

    public static String formatarData(Date data) {
        SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        return formato.format(data);
    }

    public static String formatarDataRelativa(Date data) {
        long diferenca = System.currentTimeMillis() - data.getTime();
        long segundos = diferenca / 1000;
        long minutos = segundos / 60;
        long horas = minutos / 60;
        long dias = horas / 24;

        if (dias > 0) {
            return dias == 1 ? "1 dia atrás" : dias + " dias atrás";
        } else if (horas > 0) {
            return horas == 1 ? "1 hora atrás" : horas + " horas atrás";
        } else if (minutos > 0) {
            return minutos == 1 ? "1 minuto atrás" : minutos + " minutos atrás";
        } else {
            return "Agora mesmo";
        }
    }

    public static String limitarTexto(String texto, int limite) {
        if (texto == null) return "";
        if (texto.length() <= limite) return texto;
        return texto.substring(0, limite - 3) + "...";
    }

    public static String capitalizar(String texto) {
        if (texto == null || texto.isEmpty()) return texto;
        return texto.substring(0, 1).toUpperCase() + texto.substring(1).toLowerCase();
    }
}
