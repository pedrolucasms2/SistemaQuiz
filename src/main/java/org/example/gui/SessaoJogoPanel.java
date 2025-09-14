package org.example.gui;

import org.example.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SessaoJogoPanel extends JPanel implements SessaoListener {
    private QuizGameFrame framePrincipal;
    private Jogo jogoAtual;
    private Timer timerPergunta;
    private int tempoRestante;
    private int perguntaAtual = 0;
    private Pergunta perguntaExibida;

    // Componentes da interface
    private JLabel labelTitulo;
    private JLabel labelPergunta;
    private JLabel labelTempo;
    private JLabel labelRodada;
    private JLabel labelPontuacao;
    private ButtonGroup grupoRespostas;
    private JPanel painelRespostas;
    private JButton botaoConfirmar;
    private JButton botaoProxima;
    private JProgressBar barraProgresso;

    public SessaoJogoPanel(QuizGameFrame frame) {
        this.framePrincipal = frame;
        inicializarInterface();
    }

    private void inicializarInterface() {
        setLayout(new BorderLayout());
        setBackground(GerenciadorRecursos.carregarCor("claro"));

        // Header com informações do jogo
        JPanel header = criarHeader();
        add(header, BorderLayout.NORTH);

        // Área central com pergunta e respostas
        JPanel centro = criarAreaPergunta();
        add(centro, BorderLayout.CENTER);

        // Footer com botões e progresso
        JPanel footer = criarFooter();
        add(footer, BorderLayout.SOUTH);
    }

    private JPanel criarHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(GerenciadorRecursos.carregarCor("azul"));
        header.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        labelTitulo = new JLabel();
        labelTitulo.setFont(new Font("Arial", Font.BOLD, 20));
        labelTitulo.setForeground(Color.WHITE);
        header.add(labelTitulo, BorderLayout.WEST);

        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        infoPanel.setBackground(header.getBackground());

        labelRodada = new JLabel();
        labelRodada.setFont(new Font("Arial", Font.BOLD, 14));
        labelRodada.setForeground(Color.WHITE);

        labelTempo = new JLabel();
        labelTempo.setFont(new Font("Arial", Font.BOLD, 16));
        labelTempo.setForeground(Color.YELLOW);

        labelPontuacao = new JLabel();
        labelPontuacao.setFont(new Font("Arial", Font.BOLD, 14));
        labelPontuacao.setForeground(Color.WHITE);

        infoPanel.add(labelRodada);
        infoPanel.add(Box.createHorizontalStrut(20));
        infoPanel.add(labelTempo);
        infoPanel.add(Box.createHorizontalStrut(20));
        infoPanel.add(labelPontuacao);

        header.add(infoPanel, BorderLayout.EAST);
        return header;
    }

    private JPanel criarAreaPergunta() {
        JPanel centro = new JPanel(new BorderLayout());
        centro.setBackground(getBackground());
        centro.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));

        // Pergunta
        labelPergunta = new JLabel();
        labelPergunta.setFont(new Font("Arial", Font.PLAIN, 18));
        labelPergunta.setHorizontalAlignment(SwingConstants.CENTER);
        labelPergunta.setBorder(BorderFactory.createEmptyBorder(20, 20, 30, 20));
        centro.add(labelPergunta, BorderLayout.NORTH);

        // Respostas
        painelRespostas = new JPanel();
        painelRespostas.setLayout(new BoxLayout(painelRespostas, BoxLayout.Y_AXIS));
        painelRespostas.setBackground(getBackground());
        centro.add(painelRespostas, BorderLayout.CENTER);

        return centro;
    }

    private JPanel criarFooter() {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(getBackground());
        footer.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        // Barra de progresso
        barraProgresso = new JProgressBar();
        barraProgresso.setStringPainted(true);
        barraProgresso.setString("Aguardando início...");
        footer.add(barraProgresso, BorderLayout.NORTH);

        // Botões
        JPanel painelBotoes = new JPanel(new FlowLayout());
        painelBotoes.setBackground(getBackground());

        botaoConfirmar = new JButton("Confirmar Resposta");
        botaoConfirmar.setPreferredSize(new Dimension(150, 40));
        botaoConfirmar.setBackground(GerenciadorRecursos.carregarCor("verde"));
        botaoConfirmar.setForeground(Color.WHITE);
        botaoConfirmar.setFont(new Font("Arial", Font.BOLD, 14));
        botaoConfirmar.setOpaque(true);
        botaoConfirmar.setBorderPainted(false);
        botaoConfirmar.setFocusPainted(false);
        botaoConfirmar.addActionListener(e -> confirmarResposta());
        botaoConfirmar.setEnabled(false);

        botaoProxima = new JButton("Próxima Pergunta");
        botaoProxima.setPreferredSize(new Dimension(150, 40));
        botaoProxima.setBackground(GerenciadorRecursos.carregarCor("azul"));
        botaoProxima.setForeground(Color.WHITE);
        botaoProxima.setFont(new Font("Arial", Font.BOLD, 14));
        botaoProxima.setOpaque(true);
        botaoProxima.setBorderPainted(false);
        botaoProxima.setFocusPainted(false);
        botaoProxima.addActionListener(e -> proximaPergunta());
        botaoProxima.setVisible(false);

        JButton botaoSair = new JButton("Sair do Jogo");
        botaoSair.setPreferredSize(new Dimension(120, 40));
        botaoSair.setBackground(GerenciadorRecursos.carregarCor("vermelho"));
        botaoSair.setForeground(Color.WHITE);
        botaoSair.setOpaque(true);
        botaoSair.setBorderPainted(false);
        botaoSair.setFocusPainted(false);
        botaoSair.addActionListener(e -> sairDoJogo());

        painelBotoes.add(botaoConfirmar);
        painelBotoes.add(botaoProxima);
        painelBotoes.add(botaoSair);

        footer.add(painelBotoes, BorderLayout.SOUTH);
        return footer;
    }

    public void iniciarJogo(Jogo jogo) {
        this.jogoAtual = jogo;
        this.perguntaAtual = 0;

        labelTitulo.setText("Jogo: " + jogo.getNome());
        atualizarInterface();

        // Simular início do jogo
        iniciarProximaPergunta();
    }

    private void atualizarInterface() {
        if (jogoAtual == null) return;

        labelRodada.setText("Pergunta: " + (perguntaAtual + 1) + "/" + jogoAtual.getNumeroRodadas());

        // Simplificado - mostrar pontuação básica
        labelPontuacao.setText("Pontos: 0");

        barraProgresso.setMaximum(jogoAtual.getNumeroRodadas());
        barraProgresso.setValue(perguntaAtual);
        barraProgresso.setString("Progresso: " + perguntaAtual + "/" + jogoAtual.getNumeroRodadas());
    }

    private void iniciarProximaPergunta() {
        if (perguntaAtual >= jogoAtual.getNumeroRodadas()) {
            finalizarJogo();
            return;
        }

        // Buscar uma pergunta aleatória das categorias do jogo
        perguntaExibida = obterProximaPergunta();
        if (perguntaExibida != null) {
            exibirPergunta(perguntaExibida);
        } else {
            JOptionPane.showMessageDialog(this, "Não há perguntas disponíveis para este jogo.");
            framePrincipal.mostrarMenu();
        }
    }

    private Pergunta obterProximaPergunta() {
        // Simplified - get a random question from the game's categories
        for (Categoria categoria : jogoAtual.getCategorias()) {
            if (!categoria.getPerguntas().isEmpty()) {
                return categoria.getPerguntas().get(0); // Get first available question
            }
        }
        return null;
    }

    private void exibirPergunta(Pergunta pergunta) {
        labelPergunta.setText("<html><div style='text-align: center;'>" + pergunta.getEnunciado() + "</div></html>");

        painelRespostas.removeAll();
        grupoRespostas = new ButtonGroup();

        String[] alternativas = pergunta.getAlternativas();
        for (int i = 0; i < alternativas.length; i++) {
            JRadioButton radioButton = new JRadioButton(alternativas[i]);
            radioButton.setFont(new Font("Arial", Font.PLAIN, 16));
            radioButton.setBackground(getBackground());
            radioButton.setActionCommand(String.valueOf(i));
            radioButton.addActionListener(e -> botaoConfirmar.setEnabled(true));

            grupoRespostas.add(radioButton);

            JPanel painelResposta = new JPanel(new FlowLayout(FlowLayout.LEFT));
            painelResposta.setBackground(getBackground());
            painelResposta.add(radioButton);
            painelRespostas.add(painelResposta);
        }

        painelRespostas.revalidate();
        painelRespostas.repaint();

        // Iniciar timer
        iniciarTimer(jogoAtual.getTempoLimitePergunta());
        botaoConfirmar.setEnabled(false);
        botaoProxima.setVisible(false);
    }

    private void iniciarTimer(int segundos) {
        tempoRestante = segundos;

        if (timerPergunta != null) {
            timerPergunta.stop();
        }

        timerPergunta = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tempoRestante--;
                labelTempo.setText("Tempo: " + tempoRestante + "s");

                if (tempoRestante <= 0) {
                    timerPergunta.stop();
                    confirmarResposta(); // Auto-confirmar quando tempo acabar
                }
            }
        });

        timerPergunta.start();
        labelTempo.setText("Tempo: " + tempoRestante + "s");
    }

    private void confirmarResposta() {
        if (timerPergunta != null) {
            timerPergunta.stop();
        }

        int respostaSelecionada = -1;
        ButtonModel selecao = grupoRespostas.getSelection();
        if (selecao != null) {
            respostaSelecionada = Integer.parseInt(selecao.getActionCommand());
        }

        // Verificar resposta
        boolean acertou = false;
        if (perguntaExibida != null && respostaSelecionada != -1) {
            acertou = perguntaExibida.verificarResposta(respostaSelecionada);
        }

        // Mostrar feedback
        String feedback = acertou ? "Correto!" : "Incorreto!";
        JOptionPane.showMessageDialog(this, feedback);

        // Desabilitar interação
        for (AbstractButton botao : java.util.Collections.list(grupoRespostas.getElements())) {
            botao.setEnabled(false);
        }
        botaoConfirmar.setEnabled(false);
        botaoProxima.setVisible(true);

        perguntaAtual++;
        atualizarInterface();
    }

    private void proximaPergunta() {
        iniciarProximaPergunta();
    }

    private void finalizarJogo() {
        if (timerPergunta != null) {
            timerPergunta.stop();
        }

        JOptionPane.showMessageDialog(this,
            " Jogo Finalizado! \n\nObrigado por jogar!",
            "Jogo Concluído", JOptionPane.INFORMATION_MESSAGE);

        framePrincipal.mostrarMenu();
    }

    private void sairDoJogo() {
        int opcao = JOptionPane.showConfirmDialog(
                this,
                "Deseja realmente sair do jogo?",
                "Confirmar Saída",
                JOptionPane.YES_NO_OPTION
        );

        if (opcao == JOptionPane.YES_OPTION) {
            if (timerPergunta != null) {
                timerPergunta.stop();
            }
            framePrincipal.mostrarMenu();
        }
    }

    // Implementação da interface SessaoListener
    @Override
    public void onEventoSessao(String evento, Object dados) {
        SwingUtilities.invokeLater(() -> {
            switch (evento) {
                case "PERGUNTA_INICIADA":
                    if (dados instanceof Pergunta) {
                        exibirPergunta((Pergunta) dados);
                    }
                    break;
                case "JOGO_FINALIZADO":
                    finalizarJogo();
                    break;
                case "ERRO":
                    JOptionPane.showMessageDialog(this, "Erro: " + dados.toString());
                    break;
                default:
                    // Ignorar eventos não reconhecidos
                    break;
            }
        });
    }
}
