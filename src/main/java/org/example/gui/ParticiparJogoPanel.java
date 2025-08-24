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
        // Use existing getJogos() method and filter for available games
        List<Jogo> todosJogos = framePrincipal.getSistema().getJogos();
        List<Jogo> jogosDisponiveis = new ArrayList<>();

        for (Jogo jogo : todosJogos) {
            // Filter for games that are waiting for players or active
            if (jogo.getStatus() == Jogo.StatusJogo.AGUARDANDO ||
                jogo.getStatus() == Jogo.StatusJogo.EM_ANDAMENTO) {
                jogosDisponiveis.add(jogo);
            }
        }

        if (jogosDisponiveis.isEmpty()) {
            exibirInfo("Nenhum jogo disponível no momento");
        } else {
            for (Jogo jogo : jogosDisponiveis) {
                modeloLista.addElement(jogo);
            }
            exibirSucesso(jogosDisponiveis.size() + " jogo(s) disponível(eis)");
        }
        atualizarBotoes();
    }

    private void atualizarBotoes() {
        boolean temSelecao = listaJogos.getSelectedValue() != null;
        botaoParticipar.setEnabled(temSelecao);
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
            jogoSelecionado.adicionarParticipante(jogador);

            exibirSucesso("Inscrito no jogo '" + jogoSelecionado.getNome() + "' com sucesso!");

            // Atualizar lista
            atualizarListaJogos();

            // Voltar ao menu após 2 segundos
            Timer timer = new Timer(2000, e -> framePrincipal.mostrarMenu());
            timer.setRepeats(false);
            timer.start();

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
