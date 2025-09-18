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
    private JButton botaoAbrirJogo; // Renomeado para clareza
    private JButton botaoAtualizar;
    private JPanel painelRespostas;
    private ButtonGroup grupoRespostas;


    // --- PAINÉIS PARA NAVEGAÇÃO ENTRE TELAS ---
    private JPanel painelPrincipal; // Painel que contém a lista de jogos
    private JPanel painelPerguntas; // Painel que mostrará as perguntas

    private int indicePerguntaAtual;
    private List<Pergunta> perguntasJogo;
    private JLabel labelPergunta;
    private JButton botaoProxima;
    private JButton botaoAnterior;

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

        // Título - CORRIGIDO
        JLabel titulo = new JLabel("Meus Jogos");
        titulo.setFont(new Font("Arial", Font.BOLD, 24));
        titulo.setHorizontalAlignment(SwingConstants.CENTER);
        titulo.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        painelPrincipal.add(titulo, BorderLayout.NORTH);

        // Sub-painel para a lista
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

        // Status
        labelStatus = new JLabel(" ");
        labelStatus.setHorizontalAlignment(SwingConstants.CENTER);
        painelDaLista.add(labelStatus, BorderLayout.SOUTH);

        painelPrincipal.add(painelDaLista, BorderLayout.CENTER);

        // Adiciona o painel principal à tela
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

    /**
     * --- LÓGICA TOTALMENTE CORRIGIDA ---
     * Este método agora filtra e mostra apenas os jogos dos quais o usuário
     * já participa e que ainda estão em andamento.
     */
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
            // REGRA CORRETA: Mostrar o jogo se o jogador JÁ for um participante
            // e o jogo não estiver finalizado ou cancelado.
            if (jogo.getParticipantes().contains(jogador) &&
                    (jogo.getStatus() == Jogo.StatusJogo.EM_ANDAMENTO || jogo.getStatus() == Jogo.StatusJogo.PAUSADO)) {
                meusJogos.add(jogo);
            }
        }

        if (meusJogos.isEmpty()) {
            exibirInfo("Você não está participando de nenhum jogo no momento.");
        } else {
            for (Jogo jogo : meusJogos) {
                modeloLista.addElement(jogo);
            }
            exibirSucesso(meusJogos.size() + " jogo(s) encontrado(s).");
        }

        atualizarBotoes();
    }

    private void atualizarBotoes() {
        boolean temSelecao = listaJogos.getSelectedValue() != null;
        botaoAbrirJogo.setEnabled(temSelecao);
    }

    private void exibirPerguntas(Jogo jogoSelecionado) {
        perguntasJogo = jogoSelecionado.getPerguntas();
        if (perguntasJogo == null || perguntasJogo.isEmpty()) {
            exibirErro("Este jogo não possui perguntas disponíveis.");
            return;
        }

        // Esconde o painel da lista e mostra o painel das perguntas
        remove(painelPrincipal);

        indicePerguntaAtual = 0;
        painelPerguntas = new JPanel(new BorderLayout());
        labelPergunta = new JLabel();
        labelPergunta.setFont(new Font("Arial", Font.BOLD, 16));
        labelPergunta.setHorizontalAlignment(SwingConstants.CENTER);
        painelPerguntas.add(labelPergunta, BorderLayout.CENTER);

        JPanel painelNavegacao = new JPanel(new FlowLayout());
        botaoAnterior = new JButton("Anterior");
        botaoAnterior.addActionListener(e -> mostrarPergunta(indicePerguntaAtual - 1));
        botaoProxima = new JButton("Próxima");
        botaoProxima.addActionListener(e -> mostrarPergunta(indicePerguntaAtual + 1));
        painelNavegacao.add(botaoAnterior);
        painelNavegacao.add(botaoProxima);
        painelPerguntas.add(painelNavegacao, BorderLayout.SOUTH);

        add(painelPerguntas, BorderLayout.CENTER);

        // Atualiza a interface gráfica para mostrar a mudança de painel
        revalidate();
        repaint();

        mostrarPergunta(indicePerguntaAtual);
    }

    private void mostrarPergunta(int indice) {
        if (perguntasJogo == null) return;
        if (indice < 0 || indice >= perguntasJogo.size()) return;

        indicePerguntaAtual = indice;
        Pergunta pergunta = perguntasJogo.get(indice);
        labelPergunta.setText("<html><div style='text-align: center;'>" + "Pergunta " + (indice + 1) + ":<br>" + pergunta.getTexto() + "</div></html>");

        botaoAnterior.setEnabled(indice > 0);
        botaoProxima.setEnabled(indice < perguntasJogo.size() - 1);
    }

    /**
     * --- MÉTODO DE AÇÃO CORRIGIDO ---
     * Agora, este método apenas abre o jogo selecionado para visualização,
     * sem tentar adicionar o jogador novamente.
     */
    private void abrirJogoSelecionado() {
        Jogo jogoSelecionado = listaJogos.getSelectedValue();
        if (jogoSelecionado == null) return;

        exibirSucesso("Abrindo o jogo '" + jogoSelecionado.getNome() + "'...");

        // Ação principal: mostrar a tela de perguntas do jogo.
        exibirPerguntas(jogoSelecionado);
    }

    // --- MÉTODOS AUXILIARES (Sem alteração) ---
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