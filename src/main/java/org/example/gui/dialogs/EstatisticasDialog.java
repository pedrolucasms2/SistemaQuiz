package org.example.gui.dialogs;

import org.example.*;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class EstatisticasDialog extends JDialog {
    private SistemaQuiz sistema;
    private Jogador jogador;

    public EstatisticasDialog(Frame parent, SistemaQuiz sistema, Jogador jogador) {
        super(parent, "Estatísticas - " + jogador.getNome(), true);
        this.sistema = sistema;
        this.jogador = jogador;
        inicializarInterface();
    }

    private void inicializarInterface() {
        setLayout(new BorderLayout());
        setSize(600, 500);
        setLocationRelativeTo(getParent());

        // Header
        JPanel header = new JPanel();
        header.setBackground(GerenciadorRecursos.carregarCor("azul"));
        header.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titulo = new JLabel("📊 Estatísticas de " + jogador.getNome());
        titulo.setFont(new Font("Arial", Font.BOLD, 18));
        titulo.setForeground(Color.WHITE);
        titulo.setHorizontalAlignment(SwingConstants.CENTER);
        header.add(titulo);

        add(header, BorderLayout.NORTH);

        // Conteúdo principal
        JPanel conteudo = new JPanel();
        conteudo.setLayout(new BoxLayout(conteudo, BoxLayout.Y_AXIS));
        conteudo.setBackground(GerenciadorRecursos.carregarCor("claro"));
        conteudo.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        // Estatísticas básicas usando métodos disponíveis
        adicionarSecao(conteudo, "📈 Estatísticas Básicas", criarPainelEstatisticasBasicas());

        // Conquistas
        adicionarSecao(conteudo, "🏆 Conquistas", criarPainelConquistas());

        JScrollPane scrollPane = new JScrollPane(conteudo);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        add(scrollPane, BorderLayout.CENTER);

        // Botões
        JPanel painelBotoes = new JPanel(new FlowLayout());
        painelBotoes.setBackground(GerenciadorRecursos.carregarCor("claro"));

        JButton botaoFechar = new JButton("Fechar");
        botaoFechar.setPreferredSize(new Dimension(100, 35));
        botaoFechar.setBackground(Color.GRAY);
        botaoFechar.setForeground(Color.WHITE);
        botaoFechar.setOpaque(true);
        botaoFechar.setBorderPainted(false);
        botaoFechar.setFocusPainted(false);
        botaoFechar.addActionListener(e -> dispose());

        painelBotoes.add(botaoFechar);
        add(painelBotoes, BorderLayout.SOUTH);
    }

    private void adicionarSecao(JPanel container, String titulo, JPanel conteudo) {
        JLabel labelTitulo = new JLabel(titulo);
        labelTitulo.setFont(new Font("Arial", Font.BOLD, 16));
        labelTitulo.setForeground(GerenciadorRecursos.carregarCor("azul"));
        labelTitulo.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        container.add(labelTitulo);
        container.add(conteudo);
        container.add(Box.createVerticalStrut(15));
    }

    private JPanel criarPainelEstatisticasBasicas() {
        JPanel painel = new JPanel(new GridLayout(0, 2, 10, 5));
        painel.setBackground(GerenciadorRecursos.carregarCor("claro"));
        painel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(GerenciadorRecursos.carregarCor("azul"), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        // Usar métodos disponíveis do Jogador
        adicionarEstatistica(painel, "Pontuação Total:", String.valueOf(jogador.getPontuacaoTotal()));
        adicionarEstatistica(painel, "Número de Vitórias:", String.valueOf(jogador.getNumeroVitorias()));
        adicionarEstatistica(painel, "Jogos Participados:", String.valueOf(jogador.getJogosParticipados()));

        // Calcular taxa de vitória
        double taxaVitoria = jogador.getJogosParticipados() > 0 ?
            (double) jogador.getNumeroVitorias() / jogador.getJogosParticipados() * 100 : 0;
        adicionarEstatistica(painel, "Taxa de Vitória:", String.format("%.1f%%", taxaVitoria));

        // Média de pontos por jogo
        double mediaPontos = jogador.getJogosParticipados() > 0 ?
            (double) jogador.getPontuacaoTotal() / jogador.getJogosParticipados() : 0;
        adicionarEstatistica(painel, "Média por Jogo:", String.format("%.1f pontos", mediaPontos));

        return painel;
    }

    private void adicionarEstatistica(JPanel painel, String label, String valor) {
        JLabel labelTexto = new JLabel(label);
        labelTexto.setFont(new Font("Arial", Font.PLAIN, 14));

        JLabel labelValor = new JLabel(valor);
        labelValor.setFont(new Font("Arial", Font.BOLD, 14));
        labelValor.setForeground(GerenciadorRecursos.carregarCor("azul"));

        painel.add(labelTexto);
        painel.add(labelValor);
    }

    private JPanel criarPainelConquistas() {
        JPanel painel = new JPanel();
        painel.setLayout(new BoxLayout(painel, BoxLayout.Y_AXIS));
        painel.setBackground(GerenciadorRecursos.carregarCor("claro"));
        painel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(GerenciadorRecursos.carregarCor("verde"), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        // Usar método disponível
        List<Conquista> conquistasJogador = jogador.getConquistasObtidas();

        if (conquistasJogador.isEmpty()) {
            JLabel labelVazio = new JLabel("Nenhuma conquista obtida ainda");
            labelVazio.setFont(new Font("Arial", Font.ITALIC, 14));
            labelVazio.setForeground(Color.GRAY);
            painel.add(labelVazio);
        } else {
            for (Conquista conquista : conquistasJogador) {
                JPanel itemConquista = new JPanel(new BorderLayout());
                itemConquista.setBackground(painel.getBackground());
                itemConquista.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));

                JLabel icone = new JLabel("🏆");
                icone.setFont(new Font("Arial", Font.PLAIN, 16));

                JPanel info = new JPanel();
                info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
                info.setBackground(painel.getBackground());

                JLabel nome = new JLabel(conquista.getNome());
                nome.setFont(new Font("Arial", Font.BOLD, 14));
                nome.setForeground(GerenciadorRecursos.carregarCor("verde"));

                JLabel descricao = new JLabel(conquista.getDescricao());
                descricao.setFont(new Font("Arial", Font.PLAIN, 12));
                descricao.setForeground(Color.GRAY);

                info.add(nome);
                info.add(descricao);

                itemConquista.add(icone, BorderLayout.WEST);
                itemConquista.add(info, BorderLayout.CENTER);

                painel.add(itemConquista);
            }
        }

        return painel;
    }
}
