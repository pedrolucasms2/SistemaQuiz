package org.example.gui;

import org.example.*;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ParticiparJogoPanel extends JPanel {
    private QuizGameFrame framePrincipal;
    private JList<Jogo> listaJogos;
    private DefaultListModel<Jogo> modeloLista;
    private JLabel labelStatus;
    private JButton botaoAbrirJogo;
    private JButton botaoAtualizar;

    // --- PAINÉIS PARA NAVEGAÇÃO ENTRE TELAS ---
    private JPanel painelPrincipal;
    private JPanel painelPerguntas;

    // --- ATRIBUTOS PARA O CONTROLE DO JOGO ---
    private Jogo jogoAtual;
    private Jogador jogadorAtual;
    private int indicePerguntaAtual;
    private List<Pergunta> perguntasJogo;
    private long tempoInicioPergunta;

    // --- COMPONENTES DA TELA DE PERGUNTAS ---
    private JLabel labelPergunta;
    private JButton botaoProxima;
    private JButton botaoAnterior;
    // O botão "botaoConfirmar" foi removido.
    private JPanel painelRespostas;
    private ButtonGroup grupoRespostas;

    public ParticiparJogoPanel(QuizGameFrame frame) {
        this.framePrincipal = frame;
        inicializarInterface();
        atualizarListaJogos();
    }

    private void inicializarInterface() {
        setLayout(new BorderLayout());
        setBackground(GerenciadorRecursos.carregarCor("claro"));

        painelPrincipal = new JPanel(new BorderLayout());
        painelPrincipal.setBackground(getBackground());

        JLabel titulo = new JLabel("Meus Jogos");
        titulo.setFont(new Font("Arial", Font.BOLD, 24));
        titulo.setHorizontalAlignment(SwingConstants.CENTER);
        titulo.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        painelPrincipal.add(titulo, BorderLayout.NORTH);

        JPanel painelDaLista = new JPanel(new BorderLayout());
        painelDaLista.setBackground(getBackground());
        painelDaLista.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        JLabel labelInstrucao = new JLabel("Selecione um jogo para continuar:");
        labelInstrucao.setFont(new Font("Arial", Font.BOLD, 14));
        painelDaLista.add(labelInstrucao, BorderLayout.NORTH);

        modeloLista = new DefaultListModel<>();
        listaJogos = new JList<>(modeloLista);
        listaJogos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listaJogos.setCellRenderer(new JogoListCellRenderer());
        listaJogos.addListSelectionListener(e -> atualizarBotoes());

        JScrollPane scrollPane = new JScrollPane(listaJogos);
        scrollPane.setPreferredSize(new Dimension(600, 300));
        painelDaLista.add(scrollPane, BorderLayout.CENTER);

        labelStatus = new JLabel(" ");
        labelStatus.setHorizontalAlignment(SwingConstants.CENTER);
        painelDaLista.add(labelStatus, BorderLayout.SOUTH);
        painelPrincipal.add(painelDaLista, BorderLayout.CENTER);
        add(painelPrincipal, BorderLayout.CENTER);

        JPanel painelBotoes = new JPanel(new FlowLayout());
        painelBotoes.setBackground(getBackground());

        JButton botaoVoltar = new JButton("Voltar");
        botaoVoltar.addActionListener(e -> framePrincipal.mostrarMenu());
        painelBotoes.add(botaoVoltar);

        botaoAtualizar = new JButton("Atualizar Lista");
        botaoAtualizar.addActionListener(e -> atualizarListaJogos());
        painelBotoes.add(botaoAtualizar);

        botaoAbrirJogo = new JButton("Abrir Jogo");
        botaoAbrirJogo.addActionListener(e -> abrirJogoSelecionado());
        botaoAbrirJogo.setEnabled(false);
        painelBotoes.add(botaoAbrirJogo);
        add(painelBotoes, BorderLayout.SOUTH);
    }

    private void atualizarListaJogos() {
        modeloLista.clear();
        Usuario usuarioLogado = framePrincipal.getSistema().getUsuarioLogado();
        if (!(usuarioLogado instanceof Jogador)) {
            exibirErro("Apenas jogadores podem ter jogos.");
            return;
        }
        Jogador jogador = (Jogador) usuarioLogado;
        List<Jogo> todosJogos = framePrincipal.getSistema().getJogos();
        List<Jogo> meusJogos = new ArrayList<>();
        for (Jogo jogo : todosJogos) {
            if (jogo.getParticipantes().contains(jogador) &&
                    (jogo.getStatus() == Jogo.StatusJogo.EM_ANDAMENTO || jogo.getStatus() == Jogo.StatusJogo.PAUSADO)) {
                meusJogos.add(jogo);
            }
        }
        if (meusJogos.isEmpty()) {
            exibirInfo("Você não está participando de nenhum jogo no momento.");
        } else {
            meusJogos.forEach(modeloLista::addElement);
            exibirSucesso(meusJogos.size() + " jogo(s) encontrado(s).");
        }
        atualizarBotoes();
    }

    private void atualizarBotoes() {
        botaoAbrirJogo.setEnabled(listaJogos.getSelectedValue() != null);
    }

    private void exibirPerguntas() {
        perguntasJogo = jogoAtual.getPerguntas();
        if (perguntasJogo == null || perguntasJogo.isEmpty()) {
            exibirErro("Este jogo não possui perguntas disponíveis.");
            return;
        }
        remove(painelPrincipal);

        indicePerguntaAtual = 0;
        painelPerguntas = new JPanel(new BorderLayout(10, 10));
        painelPerguntas.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        labelPergunta = new JLabel();
        labelPergunta.setFont(new Font("Arial", Font.BOLD, 16));
        labelPergunta.setHorizontalAlignment(SwingConstants.CENTER);
        painelPerguntas.add(labelPergunta, BorderLayout.NORTH);

        painelRespostas = new JPanel();
        painelRespostas.setLayout(new BoxLayout(painelRespostas, BoxLayout.Y_AXIS));
        grupoRespostas = new ButtonGroup();
        painelPerguntas.add(new JScrollPane(painelRespostas), BorderLayout.CENTER);

        // --- PAINEL DE NAVEGAÇÃO SEM O BOTÃO CONFIRMAR ---
        JPanel painelNavegacao = new JPanel(new FlowLayout());
        botaoAnterior = new JButton("Anterior");
        botaoAnterior.addActionListener(e -> mostrarPergunta(indicePerguntaAtual - 1));

        botaoProxima = new JButton("Próxima");
        // --- MUDANÇA IMPORTANTE: ActionListener do botão "Próxima" ---
        botaoProxima.addActionListener(e -> processarEAvancar());

        painelNavegacao.add(botaoAnterior);
        painelNavegacao.add(botaoProxima);
        painelPerguntas.add(painelNavegacao, BorderLayout.SOUTH);

        add(painelPerguntas, BorderLayout.CENTER);
        revalidate();
        repaint();
        mostrarPergunta(indicePerguntaAtual);
    }

    private void mostrarPergunta(int indice) {
        if (perguntasJogo == null || indice < 0 || indice >= perguntasJogo.size()) {
            // Lógica para finalizar o jogo
            finalizarJogo();
            return;
        }

        indicePerguntaAtual = indice;
        Pergunta pergunta = perguntasJogo.get(indice);

        labelPergunta.setText("<html><div style='text-align: center;'>" + "Pergunta " + (indice + 1) + ":<br>" + pergunta.getTexto() + "</div></html>");
        painelRespostas.removeAll();
        grupoRespostas = new ButtonGroup();

        String[] opcoes = pergunta.getAlternativas();
        if (opcoes != null) {
            for (String opcao : opcoes) {
                JRadioButton botaoResposta = new JRadioButton(opcao);
                botaoResposta.setFont(new Font("Arial", Font.PLAIN, 14));
                grupoRespostas.add(botaoResposta);
                painelRespostas.add(botaoResposta);
            }
        }

        // --- LÓGICA DE BOTÕES SIMPLIFICADA ---
        botaoAnterior.setEnabled(indice > 0);
        // O botão "Próxima" agora controla o fim do jogo
        if (indice == perguntasJogo.size() - 1) {
            botaoProxima.setText("Finalizar Jogo");
        } else {
            botaoProxima.setText("Próxima");
        }

        painelRespostas.revalidate();
        painelRespostas.repaint();

        tempoInicioPergunta = System.currentTimeMillis();
    }

    private void finalizarJogo() {
        // Exibe a pontuação final
        int pontuacaoFinal = jogoAtual.getPontuacoes().get(jogadorAtual);
        String mensagem = String.format("Fim de Jogo!\nSua pontuação final foi: %d pontos.", pontuacaoFinal);
        JOptionPane.showMessageDialog(this, mensagem, "Jogo Finalizado", JOptionPane.INFORMATION_MESSAGE);

        // Retorna ao menu principal
        framePrincipal.mostrarMenu();
    }

    private void abrirJogoSelecionado() {
        this.jogoAtual = listaJogos.getSelectedValue();
        if (this.jogoAtual == null) return;

        this.jogadorAtual = (Jogador) framePrincipal.getSistema().getUsuarioLogado();

        // --- LÓGICA DE CONTINUIDADE DO JOGO ---
        // 1. Pega o mapa de respostas do jogo atual.
        Map<Jogador, List<Resposta>> respostasDoJogo = jogoAtual.getRespostasJogadores();

        // 2. Verifica quantas respostas o jogador ATUAL já deu.
        List<Resposta> respostasAnteriores = respostasDoJogo.get(jogadorAtual);
        int proximaPergunta = 0;
        if (respostasAnteriores != null) {
            proximaPergunta = respostasAnteriores.size();
        }

        // 3. Inicia a exibição das perguntas...
        exibirPerguntas();

        // 4. ...e imediatamente pula para a pergunta correta.
        mostrarPergunta(proximaPergunta);
    }

    private void processarEAvancar() {
        int indiceSelecionado = getIndiceRespostaSelecionada();
        long tempoResposta = System.currentTimeMillis() - tempoInicioPergunta;
        Pergunta perguntaAtual = perguntasJogo.get(indicePerguntaAtual);
        Resposta resposta;

        if (indiceSelecionado == -1) {
            resposta = new Resposta(jogadorAtual, perguntaAtual, -1, tempoResposta, false, true); // Pulo
        } else {
            resposta = new Resposta(jogadorAtual, perguntaAtual, indiceSelecionado, tempoResposta, false, false); // Resposta normal
        }

        framePrincipal.getSistema().processarResposta(jogoAtual, jogadorAtual, resposta);

        String feedbackMsg;
        if (resposta.isPulou()) {
            feedbackMsg = "Você pulou a pergunta. 0 pontos.";
        } else {
            feedbackMsg = resposta.isCorreta() ? "Você acertou!" : "Você errou!";
            feedbackMsg += " Você ganhou " + resposta.getPontosObtidos() + " pontos.";
        }
        JOptionPane.showMessageDialog(this, feedbackMsg, "Resultado da Pergunta " + (indicePerguntaAtual + 1), JOptionPane.INFORMATION_MESSAGE);

        // --- LÓGICA DE DECISÃO ---
        // Verifica se a pergunta atual era a última da lista
        if (indicePerguntaAtual >= perguntasJogo.size() - 1) {
            encerrarQuiz(); // Se for a última, encerra o quiz.
        } else {
            mostrarPergunta(indicePerguntaAtual + 1); // Se não, avança para a próxima.
        }
    }

    private void encerrarQuiz() {

        framePrincipal.getSistema().finalizarJogo(jogoAtual);

        // 3. Exibe a pontuação final
        int pontuacaoFinal = jogoAtual.getPontuacoes().get(jogadorAtual);
        String mensagem = String.format("Fim de Jogo!\nSua pontuação final foi: %d pontos.", pontuacaoFinal);
        JOptionPane.showMessageDialog(this, mensagem, "Jogo Finalizado", JOptionPane.INFORMATION_MESSAGE);

        // 4. Retorna ao menu principal
        framePrincipal.mostrarMenu();

        resetarPainel();
    }

    private int getIndiceRespostaSelecionada() {
        int i = 0;
        for (Component comp : painelRespostas.getComponents()) {
            if (comp instanceof JRadioButton) {
                if (((JRadioButton) comp).isSelected()) {
                    return i;
                }
                i++;
            }
        }
        return -1; // Retorna -1 se nenhuma resposta for selecionada
    }

    private void exibirErro(String mensagem) {
        labelStatus.setText(mensagem);
        labelStatus.setForeground(GerenciadorRecursos.carregarCor("vermelho"));
    }

    private void exibirSucesso(String mensagem) {
        labelStatus.setText(mensagem);
        labelStatus.setForeground(GerenciadorRecursos.carregarCor("verde"));
    }

    private void exibirInfo(String mensagem) {
        labelStatus.setText(mensagem);
        labelStatus.setForeground(GerenciadorRecursos.carregarCor("azul"));
    }

    private class JogoListCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                      boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof Jogo) {
                Jogo jogo = (Jogo) value;
                String modalidadeStr = jogo.getModalidade() != null ? jogo.getModalidade().getClass().getSimpleName() : "N/A";
                setText(String.format("<html><b>%s</b><br>Status: %s | Participantes: %d<br>Modalidade: %s</html>",
                        jogo.getNome(), jogo.getStatus(), jogo.getParticipantes().size(), modalidadeStr));
            }
            return this;
        }
    }

    public void resetarPainel() {
        // 1. Limpa as variáveis de estado do jogo atual
        this.jogoAtual = null;
        this.jogadorAtual = null;
        this.perguntasJogo = null;
        this.indicePerguntaAtual = 0;

        // 2. Garante que o painel de perguntas seja removido e o painel da lista seja mostrado
        // Isso é crucial para não ficar preso na "tela fantasma"
        if (painelPerguntas != null) {
            remove(painelPerguntas);
        }
        add(painelPrincipal, BorderLayout.CENTER);

        // 3. Atualiza a lista de jogos. Como o jogo anterior foi finalizado,
        // ele não aparecerá mais aqui, que é o comportamento desejado.
        atualizarListaJogos();

        // 4. Redesenha a interface para garantir que as mudanças apareçam
        revalidate();
        repaint();
    }
}