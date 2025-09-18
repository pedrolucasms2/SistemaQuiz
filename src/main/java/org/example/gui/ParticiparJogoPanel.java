package org.example.gui;

import org.example.*;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ParticiparJogoPanel extends JPanel {
    private QuizGameFrame framePrincipal;
    private JList<Jogo> listaJogos;
    private DefaultListModel<Jogo> modeloLista;
    private JLabel labelStatus;
    private JButton botaoAbrirJogo;
    private JButton botaoAtualizar;

    // --- PAINÉIS PARA NAVEGAÇÃO ENTRE TELAS ---
    private JPanel painelPrincipal; // Painel que contém a lista de jogos
    private JPanel painelPerguntas; // Painel que mostrará as perguntas

    private int indicePerguntaAtual;
    private List<Pergunta> perguntasJogo;
    private JLabel labelPergunta;
    private JButton botaoProxima;
    private JButton botaoAnterior;

    // --- NOVOS COMPONENTES ADICIONADOS PARA AS RESPOSTAS ---
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

        // --- PAINEL PRINCIPAL (COM A LISTA DE JOGOS) ---
        painelPrincipal = new JPanel(new BorderLayout());
        painelPrincipal.setBackground(getBackground());

        // Título - CORRIGIDO PARA "MEUS JOGOS"
        JLabel titulo = new JLabel("Meus Jogos");
        titulo.setFont(new Font("Arial", Font.BOLD, 24));
        titulo.setHorizontalAlignment(SwingConstants.CENTER);
        titulo.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        painelPrincipal.add(titulo, BorderLayout.NORTH);

        JPanel painelDaLista = new JPanel(new BorderLayout());
        painelDaLista.setBackground(getBackground());
        painelDaLista.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        // Instrução - CORRIGIDO
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

        // --- PAINEL DE BOTÕES (INFERIOR) ---
        JPanel painelBotoes = new JPanel(new FlowLayout());
        painelBotoes.setBackground(getBackground());

        JButton botaoVoltar = new JButton("Voltar");
        botaoVoltar.setPreferredSize(new Dimension(100, 35));
        botaoVoltar.setBackground(Color.GRAY);
        botaoVoltar.setForeground(Color.WHITE);
        botaoVoltar.setOpaque(true);
        botaoVoltar.setBorderPainted(false);
        botaoVoltar.addActionListener(e -> framePrincipal.mostrarMenu());

        botaoAtualizar = new JButton("Atualizar Lista");
        botaoAtualizar.setPreferredSize(new Dimension(120, 35));
        botaoAtualizar.setBackground(GerenciadorRecursos.carregarCor("azul"));
        botaoAtualizar.setForeground(Color.WHITE);
        botaoAtualizar.setOpaque(true);
        botaoAtualizar.setBorderPainted(false);
        botaoAtualizar.addActionListener(e -> atualizarListaJogos());

        // Botão principal - CORRIGIDO
        botaoAbrirJogo = new JButton("Abrir Jogo");
        botaoAbrirJogo.setPreferredSize(new Dimension(120, 35));
        botaoAbrirJogo.setBackground(GerenciadorRecursos.carregarCor("verde"));
        botaoAbrirJogo.setForeground(Color.WHITE);
        botaoAbrirJogo.setFont(new Font("Arial", Font.BOLD, 14));
        botaoAbrirJogo.setOpaque(true);
        botaoAbrirJogo.setBorderPainted(false);
        botaoAbrirJogo.addActionListener(e -> abrirJogoSelecionado());
        botaoAbrirJogo.setEnabled(false);

        painelBotoes.add(botaoVoltar);
        painelBotoes.add(botaoAtualizar);
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
        boolean temSelecao = listaJogos.getSelectedValue() != null;
        botaoAbrirJogo.setEnabled(temSelecao);
    }

    /**
     * --- MÉTODO CORRIGIDO ---
     * Prepara o layout para exibir a pergunta e as respostas.
     */
    private void exibirPerguntas(Jogo jogoSelecionado) {
        perguntasJogo = jogoSelecionado.getPerguntas();
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
        painelPerguntas.add(painelRespostas, BorderLayout.CENTER);

        JPanel painelNavegacao = new JPanel(new FlowLayout());
        botaoAnterior = new JButton("Anterior");
        botaoAnterior.addActionListener(e -> mostrarPergunta(indicePerguntaAtual - 1));
        botaoProxima = new JButton("Próxima");
        botaoProxima.addActionListener(e -> mostrarPergunta(indicePerguntaAtual + 1));
        painelNavegacao.add(botaoAnterior);
        painelNavegacao.add(botaoProxima);
        painelPerguntas.add(painelNavegacao, BorderLayout.SOUTH);

        add(painelPerguntas, BorderLayout.CENTER);

        revalidate();
        repaint();

        mostrarPergunta(indicePerguntaAtual);
    }

    /**
     * --- MÉTODO CORRIGIDO ---
     * Exibe a pergunta e cria dinamicamente os botões de resposta.
     */
    private void mostrarPergunta(int indice) {
        if (perguntasJogo == null || indice < 0 || indice >= perguntasJogo.size()) return;

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

        painelRespostas.revalidate();
        painelRespostas.repaint();

        botaoAnterior.setEnabled(indice > 0);
        botaoProxima.setEnabled(indice < perguntasJogo.size() - 1);
    }

    private void abrirJogoSelecionado() {
        Jogo jogoSelecionado = listaJogos.getSelectedValue();
        if (jogoSelecionado == null) return;

        exibirSucesso("Abrindo o jogo '" + jogoSelecionado.getNome() + "'...");
        exibirPerguntas(jogoSelecionado);
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
                String texto = String.format("<html><b>%s</b><br>Status: %s | Participantes: %d<br>Modalidade: %s</html>",
                        jogo.getNome(),
                        jogo.getStatus(),
                        jogo.getParticipantes().size(),
                        modalidadeStr);
                setText(texto);
            }
            return this;
        }
    }
}