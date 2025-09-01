package org.example;

import javax.swing.ImageIcon;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class GerenciadorRecursos {
    private static final String CAMINHO_ICONES = "/icones/";
    private static final String CAMINHO_FONTES = "/fontes/";
    private static final String CAMINHO_SONS = "/sons/";

    private static Map<String, ImageIcon> cacheIcones = new HashMap<>();
    private static Map<String, Font> cacheFontes = new HashMap<>();

    public static ImageIcon carregarIcone(String nome) {
        if (cacheIcones.containsKey(nome)) {
            return cacheIcones.get(nome);
        }

        try {
            URL url = GerenciadorRecursos.class.getResource(CAMINHO_ICONES + nome);
            if (url != null) {
                ImageIcon icone = new ImageIcon(url);
                cacheIcones.put(nome, icone);
                return icone;
            }
        } catch (Exception e) {
            System.err.println("Erro ao carregar ícone: " + nome);
        }

        // Retornar ícone padrão se não encontrar
        return criarIconePadrao();
    }

    public static Font carregarFonte(String nome, int estilo, int tamanho) {
        String chave = nome + "_" + estilo + "_" + tamanho;

        if (cacheFontes.containsKey(chave)) {
            return cacheFontes.get(chave);
        }

        try {
            URL url = GerenciadorRecursos.class.getResource(CAMINHO_FONTES + nome);
            if (url != null) {
                Font fonte = Font.createFont(Font.TRUETYPE_FONT, url.openStream());
                fonte = fonte.deriveFont(estilo, (float) tamanho);
                cacheFontes.put(chave, fonte);
                return fonte;
            }
        } catch (Exception e) {
            System.err.println("Erro ao carregar fonte: " + nome);
        }

        // Retornar fonte padrão se não encontrar
        Font fontePadrao = new Font("SansSerif", estilo, tamanho);
        cacheFontes.put(chave, fontePadrao);
        return fontePadrao;
    }

    private static ImageIcon criarIconePadrao() {
        BufferedImage img = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();
        g2d.setColor(Color.GRAY);
        g2d.fillRect(0, 0, 16, 16);
        g2d.dispose();
        return new ImageIcon(img);
    }

    public static void limparCaches() {
        cacheIcones.clear();
        cacheFontes.clear();
    }

    // Método para carregar cores do sistema
    public static Color carregarCor(String nomeCor) {
        switch (nomeCor.toLowerCase()) {
            case "azul":
                return new Color(52, 152, 219);
            case "verde":
                return new Color(46, 204, 113);
            case "vermelho":
                return new Color(231, 76, 60);
            case "laranja":
                return new Color(230, 126, 34);
            case "roxo":
                return new Color(155, 89, 182);
            case "claro":
                return new Color(236, 240, 241);
            case "escuro":
                return new Color(52, 73, 94);
            default:
                return Color.GRAY;
        }
    }
}
