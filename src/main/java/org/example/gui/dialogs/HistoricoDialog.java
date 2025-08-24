package org.example.gui.dialogs;

import org.example.*;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class HistoricoDialog extends JDialog {
    private SistemaQuiz sistema;
    private Jogador jogador;

    public HistoricoDialog(Frame parent, SistemaQuiz sistema, Jogador jogador) {
        super(parent, "Histórico de Jogos - " + jogador.getNome(), true);
        this.sistema = sistema;
        this.jogador = jogador;
        inicializarInterface();
    }

    private void inicializarInterface() {
        setLayout(new BorderLayout());
        setSize(600, 400);
        setLocationRelativeTo(getParent());

        // Header
        JPanel header = new JPanel();
        header.setBackground(GerenciadorRecursos.carregarCor("azul"));
        header.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titulo = new JLabel("📚 Histórico de " + jogador.getNome());
        titulo.setFont(new Font("Arial", Font.BOLD, 18));
        titulo.setForeground(Color.WHITE);
        titulo.setHorizontalAlignment(SwingConstants.CENTER);
        header.add(titulo);

        add(header, BorderLayout.NORTH);

        // Conteúdo simplificado
        JPanel conteudo = new JPanel();
        conteudo.setLayout(new BoxLayout(conteudo, BoxLayout.Y_AXIS));
        conteudo.setBackground(GerenciadorRecursos.carregarCor("claro"));
        conteudo.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        // Estatísticas resumidas
        JPanel estatisticas = new JPanel(new GridLayout(0, 2, 10, 10));
        estatisticas.setBackground(conteudo.getBackground());
        estatisticas.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("Resumo do Histórico"),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        adicionarEstatistica(estatisticas, "Jogos Participados:", String.valueOf(jogador.getJogosParticipados()));
        adicionarEstatistica(estatisticas, "Vitórias:", String.valueOf(jogador.getNumeroVitorias()));
        adicionarEstatistica(estatisticas, "Pontuação Total:", String.valueOf(jogador.getPontuacaoTotal()));

        double taxaVitoria = jogador.getJogosParticipados() > 0 ?
            (double) jogador.getNumeroVitorias() / jogador.getJogosParticipados() * 100 : 0;
        adicionarEstatistica(estatisticas, "Taxa de Vitória:", String.format("%.1f%%", taxaVitoria));

        conteudo.add(estatisticas);
        conteudo.add(Box.createVerticalStrut(20));

        // Informação sobre histórico detalhado
        JPanel infoPanel = new JPanel();
        infoPanel.setBackground(conteudo.getBackground());
        infoPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("Histórico Detalhado"),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        JTextArea infoText = new JTextArea(8, 40);
        infoText.setEditable(false);
        infoText.setBackground(Color.WHITE);
        infoText.setFont(new Font("Arial", Font.PLAIN, 12));
        infoText.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        StringBuilder info = new StringBuilder();
        info.append("📊 RESUMO DO DESEMPENHO\n\n");
        info.append("Este jogador participou de ").append(jogador.getJogosParticipados()).append(" jogo(s)\n");
        info.append("e conquistou ").append(jogador.getNumeroVitorias()).append(" vitória(s).\n\n");

        if (jogador.getJogosParticipados() > 0) {
            double mediaPontos = (double) jogador.getPontuacaoTotal() / jogador.getJogosParticipados();
            info.append("Média de pontos por jogo: ").append(String.format("%.1f", mediaPontos)).append("\n");
        }

        info.append("\nConquistas obtidas: ").append(jogador.getConquistasObtidas().size()).append("\n\n");
        info.append("💡 Dica: Continue jogando para melhorar suas estatísticas\n");
        info.append("e desbloquear novas conquistas!");

        infoText.setText(info.toString());

        JScrollPane scrollPane = new JScrollPane(infoText);
        infoPanel.add(scrollPane);

        conteudo.add(infoPanel);

        add(conteudo, BorderLayout.CENTER);

        // Footer
        JPanel footer = new JPanel(new FlowLayout());
        footer.setBackground(GerenciadorRecursos.carregarCor("claro"));

        JButton botaoFechar = new JButton("Fechar");
        botaoFechar.setPreferredSize(new Dimension(100, 35));
        botaoFechar.setBackground(Color.GRAY);
        botaoFechar.setForeground(Color.WHITE);
        botaoFechar.addActionListener(e -> dispose());

        footer.add(botaoFechar);
        add(footer, BorderLayout.SOUTH);
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
}
