package org.example.gui;

import org.example.*;
import org.example.gui.dialogs.*;

import javax.swing.*;
import java.awt.*;

public class MenuPrincipalPanel extends JPanel {
    private QuizGameFrame framePrincipal;
    private JLabel labelBoasVindas;
    private JPanel painelBotoes;

    public MenuPrincipalPanel(QuizGameFrame frame) {
        this.framePrincipal = frame;
        inicializarInterface();
    }

    private void inicializarInterface() {
        setLayout(new BorderLayout());
        setBackground(GerenciadorRecursos.carregarCor("claro"));

        // Header
        JPanel header = new JPanel();
        header.setBackground(GerenciadorRecursos.carregarCor("azul"));
        header.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        labelBoasVindas = new JLabel();
        labelBoasVindas.setFont(new Font("Arial", Font.BOLD, 18));
        labelBoasVindas.setForeground(Color.WHITE);
        labelBoasVindas.setHorizontalAlignment(SwingConstants.CENTER);
        header.add(labelBoasVindas);

        add(header, BorderLayout.NORTH);

        painelBotoes = new JPanel(new GridBagLayout());
        painelBotoes.setBackground(getBackground());
        add(painelBotoes, BorderLayout.CENTER);

        // Footer
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footer.setBackground(getBackground());

        JButton botaoSair = new JButton("Sair");
        botaoSair.setBackground(GerenciadorRecursos.carregarCor("vermelho"));
        botaoSair.setForeground(Color.WHITE);
        botaoSair.addActionListener(e -> framePrincipal.mostrarLogin());

        footer.add(botaoSair);
        add(footer, BorderLayout.SOUTH);
    }

    public void atualizarInterface() {
        // USA O MÉTODO QUE JÁ EXISTE NO SEU SISTEMAQUIZ
        Usuario usuario = framePrincipal.getSistema().getUsuarioLogado();
        if (usuario == null) return;

        labelBoasVindas.setText("Bem-vindo, " + usuario.getNome() + "!");

        painelBotoes.removeAll();

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;

        if (usuario instanceof Administrador) {
            gbc.gridy = 0;
            JButton botaoCriar = criarBotao("Criar Jogo", GerenciadorRecursos.carregarCor("verde"));
            botaoCriar.addActionListener(e -> framePrincipal.mostrarCriarJogo());
            painelBotoes.add(botaoCriar, gbc);

            gbc.gridy = 1;
            JButton botaoPerguntas = criarBotao("Gerenciar Perguntas", GerenciadorRecursos.carregarCor("azul"));
            botaoPerguntas.addActionListener(e -> {
                GerenciadorPerguntasDialog dialog = new GerenciadorPerguntasDialog(
                        (Frame) SwingUtilities.getWindowAncestor(this),
                        framePrincipal.getSistema()
                );
                dialog.setVisible(true);
            });
            painelBotoes.add(botaoPerguntas, gbc);

            gbc.gridy = 2;
            JButton botaoRelatorios = criarBotao("Relatórios", GerenciadorRecursos.carregarCor("laranja"));
            botaoRelatorios.addActionListener(e -> {
                RelatoriosDialog dialog = new RelatoriosDialog(
                        (Frame) SwingUtilities.getWindowAncestor(this),
                        framePrincipal.getSistema()
                );
                dialog.setVisible(true);
            });
            painelBotoes.add(botaoRelatorios, gbc);

        } else if (usuario instanceof Jogador) {
            Jogador jogador = (Jogador) usuario;

            gbc.gridy = 0;
            JButton botaoParticipar = criarBotao("Participar de Jogo", GerenciadorRecursos.carregarCor("verde"));
            botaoParticipar.addActionListener(e -> framePrincipal.mostrarParticiparJogo());
            painelBotoes.add(botaoParticipar, gbc);

            gbc.gridy = 1;
            JButton botaoEstatisticas = criarBotao("Minhas Estatísticas", GerenciadorRecursos.carregarCor("azul"));
            botaoEstatisticas.addActionListener(e -> {
                EstatisticasDialog dialog = new EstatisticasDialog(
                        (Frame) SwingUtilities.getWindowAncestor(this),
                        framePrincipal.getSistema(),
                        jogador
                );
                dialog.setVisible(true);
            });
            painelBotoes.add(botaoEstatisticas, gbc);

            gbc.gridy = 2;
            JButton botaoHistorico = criarBotao("Histórico de Jogos", GerenciadorRecursos.carregarCor("laranja"));
            botaoHistorico.addActionListener(e -> {
                HistoricoDialog dialog = new HistoricoDialog(
                        (Frame) SwingUtilities.getWindowAncestor(this),
                        framePrincipal.getSistema(),
                        jogador
                );
                dialog.setVisible(true);
            });
            painelBotoes.add(botaoHistorico, gbc);
        }

        revalidate();
        repaint();
    }

    private JButton criarBotao(String texto, Color cor) {
        JButton botao = new JButton(texto);
        botao.setPreferredSize(new Dimension(200, 50));
        botao.setFont(new Font("Arial", Font.BOLD, 14));
        botao.setBackground(cor);
        botao.setForeground(Color.WHITE);
        botao.setOpaque(true);
        botao.setBorderPainted(false);
        botao.setFocusPainted(false);
        return botao;
    }
}
