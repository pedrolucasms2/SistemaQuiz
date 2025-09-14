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
    private JButton botaoParticipar;
    private JButton botaoAtualizar;

    private int indicePerguntaAtual;
    private List<Pergunta> perguntasJogo;
    private JPanel painelPerguntas;
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

        // Título
        JLabel titulo = new JLabel("Participar de Jogo");
        titulo.setFont(new Font("Arial", Font.BOLD, 24));
        titulo.setHorizontalAlignment(SwingConstants.CENTER);
        titulo.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        add(titulo, BorderLayout.NORTH);

        // Lista de jogos
        JPanel painelCentral = new JPanel(new BorderLayout());
        painelCentral.setBackground(getBackground());
        painelCentral.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        JLabel labelInstrucao = new JLabel("Selecione um jogo disponível:");
        labelInstrucao.setFont(new Font("Arial", Font.BOLD, 14));
        painelCentral.add(labelInstrucao, BorderLayout.NORTH);

        modeloLista = new DefaultListModel<>();
        listaJogos = new JList<>(modeloLista);
        listaJogos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listaJogos.setCellRenderer(new JogoListCellRenderer());
        listaJogos.addListSelectionListener(e -> atualizarBotoes());

        JScrollPane scrollPane = new JScrollPane(listaJogos);
        scrollPane.setPreferredSize(new Dimension(600, 300));
        painelCentral.add(scrollPane, BorderLayout.CENTER);

        // Status
        labelStatus = new JLabel(" ");
        labelStatus.setHorizontalAlignment(SwingConstants.CENTER);
        painelCentral.add(labelStatus, BorderLayout.SOUTH);

        add(painelCentral, BorderLayout.CENTER);

        // Botões
        JPanel painelBotoes = new JPanel(new FlowLayout());
        painelBotoes.setBackground(getBackground());

        JButton botaoVoltar = new JButton("Voltar");
        botaoVoltar.setPreferredSize(new Dimension(100, 35));
        botaoVoltar.setBackground(Color.GRAY);
        botaoVoltar.setForeground(Color.WHITE);
        botaoVoltar.setOpaque(true);
        botaoVoltar.setBorderPainted(false);
        botaoVoltar.setFocusPainted(false);
        botaoVoltar.addActionListener(e -> framePrincipal.mostrarMenu());

        botaoAtualizar = new JButton("Atualizar Lista");
        botaoAtualizar.setPreferredSize(new Dimension(120, 35));
        botaoAtualizar.setBackground(GerenciadorRecursos.carregarCor("azul"));
        botaoAtualizar.setForeground(Color.WHITE);
        botaoAtualizar.setOpaque(true);
        botaoAtualizar.setBorderPainted(false);
        botaoAtualizar.setFocusPainted(false);
        botaoAtualizar.addActionListener(e -> atualizarListaJogos());

        botaoParticipar = new JButton("Participar");
        botaoParticipar.setPreferredSize(new Dimension(120, 35));
        botaoParticipar.setBackground(GerenciadorRecursos.carregarCor("verde"));
        botaoParticipar.setForeground(Color.WHITE);
        botaoParticipar.setFont(new Font("Arial", Font.BOLD, 14));
        botaoParticipar.setOpaque(true);
        botaoParticipar.setBorderPainted(false);
        botaoParticipar.setFocusPainted(false);
        botaoParticipar.addActionListener(e -> participarJogo());
        botaoParticipar.setEnabled(false);

        painelBotoes.add(botaoVoltar);
        painelBotoes.add(botaoAtualizar);
        painelBotoes.add(botaoParticipar);
        add(painelBotoes, BorderLayout.SOUTH);
    }

    private void atualizarListaJogos() {
        modeloLista.clear();

        Usuario usuarioLogado = framePrincipal.getSistema().getUsuarioLogado();
        if (!(usuarioLogado instanceof Jogador)) {
            exibirErro("Apenas jogadores podem ver jogos disponíveis");
            return;
        }

        Jogador jogador = (Jogador) usuarioLogado;

        //  CORREÇÃO: Usar método filtrado do sistema
        List<Jogo> todosJogos = framePrincipal.getSistema().getJogos();
        List<Jogo> jogosDisponiveis = new ArrayList<>();

        for (Jogo jogo : todosJogos) {
            //  Filtros aplicados:
            if (jogo.getStatus() == Jogo.StatusJogo.EM_ANDAMENTO &&
                    !jogo.getParticipantes().contains(jogador) &&           // Não participa ainda
                    jogo.podeAdicionarParticipante(jogador)) {              // Pode participar
                jogosDisponiveis.add(jogo);
            }
        }

        if (jogosDisponiveis.isEmpty()) {
            exibirInfo("Nenhum jogo disponível para você no momento");
        } else {
            for (Jogo jogo : jogosDisponiveis) {
                modeloLista.addElement(jogo);
            }
            exibirSucesso(jogosDisponiveis.size() + " jogo(s) disponível(eis) para você");
        }

        atualizarBotoes();
    }


    private void atualizarBotoes() {
        boolean temSelecao = listaJogos.getSelectedValue() != null;
        botaoParticipar.setEnabled(temSelecao);
    }

    private void exibirPerguntas(Jogo jogoSelecionado) {
        perguntasJogo = jogoSelecionado.getPerguntas();
        if (perguntasJogo.isEmpty()) {
            exibirErro("Este jogo não possui perguntas disponíveis.");
            return;
        }

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
        mostrarPergunta(indicePerguntaAtual);
    }

    private void mostrarPergunta(int indice) {
        if (indice < 0 || indice >= perguntasJogo.size()) return;
        indicePerguntaAtual = indice;
        Pergunta pergunta = perguntasJogo.get(indice);
        labelPergunta.setText("Pergunta " + (indice + 1) + ": " + pergunta.getTexto());
        botaoAnterior.setEnabled(indice > 0);
        botaoProxima.setEnabled(indice < perguntasJogo.size() - 1);
    }

    private void participarJogo() {
        Jogo jogoSelecionado = listaJogos.getSelectedValue();
        if (jogoSelecionado == null) return;

        try {
            Usuario usuario = framePrincipal.getSistema().getUsuarioLogado();
            if (!(usuario instanceof Jogador)) {
                exibirErro("Apenas jogadores podem participar de jogos");
                return;
            }

            Jogador jogador = (Jogador) usuario;

            // Verificar se já está participando
            if (jogoSelecionado.getParticipantes().contains(jogador)) {
                exibirErro("Você já está participando deste jogo");
                return;
            }

            // Adicionar jogador ao jogo
            boolean sucesso = jogoSelecionado.adicionarParticipante(jogador);
            if (!sucesso) {
                exibirErro("Não foi possível se inscrever neste jogo");
                return;
            }

            //  CORREÇÃO: Salvar o jogo após adicionar participante
            jogoSelecionado.salvarJogo();

            //  CORREÇÃO: NÃO chamar iniciar() automaticamente
            // Deixar para o admin decidir quando iniciar o jogo

            exibirSucesso("Inscrito no jogo '" + jogoSelecionado.getNome() + "' com sucesso!");

            // Atualizar lista para remover o jogo (já que agora participa)
            atualizarListaJogos();

            //  OPCIONAL: Mostrar as perguntas do jogo
            exibirPerguntas(jogoSelecionado);

        } catch (Exception e) {
            exibirErro("Erro ao participar do jogo: " + e.getMessage());
        }
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

    // Renderer personalizado para a lista de jogos
    private class JogoListCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

            if (value instanceof Jogo) {
                Jogo jogo = (Jogo) value;
                String texto = String.format("<html><b>%s</b><br>Status: %s | Participantes: %d<br>Modalidade: %s</html>",
                        jogo.getNome(),
                        jogo.getStatus(),
                        jogo.getParticipantes().size(),
                        jogo.getModalidade().getClass().getSimpleName());
                setText(texto);
            }

            return this;
        }
    }
}
