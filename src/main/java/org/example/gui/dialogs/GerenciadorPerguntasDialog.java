package org.example.gui.dialogs;

import org.example.*;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class GerenciadorPerguntasDialog extends JDialog {
    private final SistemaQuiz sistema;
    private JList<Pergunta> listaPerguntasDisponiveis;
    private DefaultListModel<Pergunta> modeloListaDisponiveis;
    private JTextArea campoTexto;
    private JComboBox<Categoria> comboCategoria;
    private JComboBox<Pergunta.Dificuldade> comboDificuldade;
    private List<JTextField> camposRespostas;
    private JComboBox<Integer> comboRespostaCorreta;
    private JLabel labelStatus;

    public GerenciadorPerguntasDialog(Frame parent, SistemaQuiz sistema) {
        super(parent, "Gerenciar Perguntas", true);
        this.sistema = sistema;
        inicializarInterface();
        atualizarListaPerguntas();
    }

    private void inicializarInterface() {
        setLayout(new BorderLayout());
        setSize(900, 700);
        setLocationRelativeTo(getParent());

        // Header
        JPanel header = new JPanel();
        header.setBackground(GerenciadorRecursos.carregarCor("azul"));
        header.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titulo = new JLabel("📝 Gerenciador de Perguntas");
        titulo.setFont(new Font("Arial", Font.BOLD, 18));
        titulo.setForeground(Color.WHITE);
        titulo.setHorizontalAlignment(SwingConstants.CENTER);
        header.add(titulo);

        add(header, BorderLayout.NORTH);

        // Painel principal dividido
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(400);

        // Lado esquerdo - Lista de perguntas
        JPanel painelLista = criarPainelLista();
        splitPane.setLeftComponent(painelLista);

        // Lado direito - Formulário de edição
        JPanel painelFormulario = criarPainelFormulario();
        splitPane.setRightComponent(painelFormulario);

        add(splitPane, BorderLayout.CENTER);

        // Footer
        JPanel footer = new JPanel(new FlowLayout());
        footer.setBackground(GerenciadorRecursos.carregarCor("claro"));

        JButton botaoFechar = new JButton("Fechar");
        botaoFechar.setPreferredSize(new Dimension(100, 35));
        botaoFechar.setBackground(Color.GRAY);
        botaoFechar.setForeground(Color.WHITE);
        botaoFechar.setOpaque(true);
        botaoFechar.setBorderPainted(false);
        botaoFechar.setFocusPainted(false);
        botaoFechar.addActionListener(e -> dispose());

        footer.add(botaoFechar);
        add(footer, BorderLayout.SOUTH);
    }

    private JPanel criarPainelLista() {
        JPanel painel = new JPanel(new BorderLayout());
        painel.setBackground(GerenciadorRecursos.carregarCor("claro"));
        painel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 10));

        JLabel titulo = new JLabel("Perguntas Existentes");
        titulo.setFont(new Font("Arial", Font.BOLD, 16));
        titulo.setForeground(GerenciadorRecursos.carregarCor("azul"));
        painel.add(titulo, BorderLayout.NORTH);

        modeloListaDisponiveis = new DefaultListModel<>();
        listaPerguntasDisponiveis = new JList<>(modeloListaDisponiveis);
        listaPerguntasDisponiveis.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listaPerguntasDisponiveis.setCellRenderer(new PerguntaListCellRenderer());
        listaPerguntasDisponiveis.addListSelectionListener(e -> carregarPerguntaSelecionada());

        JScrollPane scrollPane = new JScrollPane(listaPerguntasDisponiveis);
        scrollPane.setPreferredSize(new Dimension(350, 400));
        painel.add(scrollPane, BorderLayout.CENTER);

        // Botões de ação para a lista
        JPanel painelBotoesLista = new JPanel(new FlowLayout());
        painelBotoesLista.setBackground(painel.getBackground());

        JButton botaoNova = new JButton("Nova Pergunta");
        botaoNova.setBackground(GerenciadorRecursos.carregarCor("verde"));
        botaoNova.setForeground(Color.WHITE);
        botaoNova.setOpaque(true);
        botaoNova.setBorderPainted(false);
        botaoNova.setFocusPainted(false);
        botaoNova.addActionListener(e -> novaPergunta());

        JButton botaoExcluir = new JButton("Excluir");
        botaoExcluir.setBackground(GerenciadorRecursos.carregarCor("vermelho"));
        botaoExcluir.setForeground(Color.WHITE);
        botaoExcluir.setOpaque(true);
        botaoExcluir.setBorderPainted(false);
        botaoExcluir.setFocusPainted(false);
        botaoExcluir.addActionListener(e -> excluirPergunta());

        painelBotoesLista.add(botaoNova);
        painelBotoesLista.add(botaoExcluir);
        painel.add(painelBotoesLista, BorderLayout.SOUTH);

        return painel;
    }

    private JPanel criarPainelFormulario() {
        JPanel painel = new JPanel();
        painel.setLayout(new BoxLayout(painel, BoxLayout.Y_AXIS));
        painel.setBackground(GerenciadorRecursos.carregarCor("claro"));
        painel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 20));

        JLabel titulo = new JLabel("Editar Pergunta");
        titulo.setFont(new Font("Arial", Font.BOLD, 16));
        titulo.setForeground(GerenciadorRecursos.carregarCor("azul"));
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        painel.add(titulo);
        painel.add(Box.createVerticalStrut(20));

        // Formulário
        JPanel formulario = new JPanel(new GridBagLayout());
        formulario.setBackground(painel.getBackground());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        // Texto da pergunta
        gbc.gridx = 0; gbc.gridy = 0;
        formulario.add(new JLabel("Texto da Pergunta:"), gbc);

        campoTexto = new JTextArea(3, 25);
        campoTexto.setFont(new Font("Arial", Font.PLAIN, 14));
        campoTexto.setLineWrap(true);
        campoTexto.setWrapStyleWord(true);
        JScrollPane scrollTexto = new JScrollPane(campoTexto);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formulario.add(scrollTexto, gbc);

        // Categoria
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE;
        formulario.add(new JLabel("Categoria:"), gbc);

        comboCategoria = new JComboBox<>();
        for (Categoria categoria : sistema.getCategorias()) {
            comboCategoria.addItem(categoria);
        }
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formulario.add(comboCategoria, gbc);

        // Dificuldade
        gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE;
        formulario.add(new JLabel("Dificuldade:"), gbc);

        comboDificuldade = new JComboBox<>(Pergunta.Dificuldade.values());
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formulario.add(comboDificuldade, gbc);

        // Respostas
        gbc.gridx = 0; gbc.gridy = 3; gbc.fill = GridBagConstraints.NONE;
        formulario.add(new JLabel("Respostas:"), gbc);

        JPanel painelRespostas = new JPanel();
        painelRespostas.setLayout(new BoxLayout(painelRespostas, BoxLayout.Y_AXIS));
        painelRespostas.setBackground(formulario.getBackground());

        camposRespostas = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            JPanel linhaResposta = new JPanel(new BorderLayout());
            linhaResposta.setBackground(painelRespostas.getBackground());

            JLabel labelNum = new JLabel((i + 1) + ". ");
            labelNum.setPreferredSize(new Dimension(25, 25));

            JTextField campoResposta = new JTextField(20);
            campoResposta.setFont(new Font("Arial", Font.PLAIN, 14));
            camposRespostas.add(campoResposta);

            linhaResposta.add(labelNum, BorderLayout.WEST);
            linhaResposta.add(campoResposta, BorderLayout.CENTER);
            painelRespostas.add(linhaResposta);
            painelRespostas.add(Box.createVerticalStrut(5));
        }

        gbc.gridx = 1; gbc.gridy = 3; gbc.fill = GridBagConstraints.HORIZONTAL;
        formulario.add(painelRespostas, gbc);

        // Resposta correta
        gbc.gridx = 0; gbc.gridy = 4; gbc.fill = GridBagConstraints.NONE;
        formulario.add(new JLabel("Resposta Correta:"), gbc);

        comboRespostaCorreta = new JComboBox<>(new Integer[]{1, 2, 3, 4});
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formulario.add(comboRespostaCorreta, gbc);

        painel.add(formulario);

        // Status
        labelStatus = new JLabel(" ");
        labelStatus.setAlignmentX(Component.CENTER_ALIGNMENT);
        painel.add(Box.createVerticalStrut(10));
        painel.add(labelStatus);

        // Botões do formulário
        JPanel painelBotoes = new JPanel(new FlowLayout());
        painelBotoes.setBackground(painel.getBackground());

        JButton botaoSalvar = new JButton("Salvar");
        botaoSalvar.setPreferredSize(new Dimension(100, 35));
        botaoSalvar.setBackground(GerenciadorRecursos.carregarCor("verde"));
        botaoSalvar.setForeground(Color.WHITE);
        botaoSalvar.setOpaque(true);
        botaoSalvar.setBorderPainted(false);
        botaoSalvar.setFocusPainted(false);
        botaoSalvar.addActionListener(e -> salvarPergunta());

        JButton botaoLimpar = new JButton("Limpar");
        botaoLimpar.setPreferredSize(new Dimension(100, 35));
        botaoLimpar.setBackground(Color.GRAY);
        botaoLimpar.setForeground(Color.WHITE);
        botaoLimpar.setOpaque(true);
        botaoLimpar.setBorderPainted(false);
        botaoLimpar.setFocusPainted(false);
        botaoLimpar.addActionListener(e -> limparFormulario());

        painelBotoes.add(botaoSalvar);
        painelBotoes.add(botaoLimpar);
        painel.add(painelBotoes);

        return painel;
    }

    private void atualizarListaPerguntas() {
        modeloListaDisponiveis.clear();
        for (Categoria categoria : sistema.getCategorias()) {
            for (Pergunta pergunta : categoria.getPerguntas()) {
                modeloListaDisponiveis.addElement(pergunta);
            }
        }
    }

    private void carregarPerguntaSelecionada() {
        Pergunta selecionada = listaPerguntasDisponiveis.getSelectedValue();
        if (selecionada == null) return;

        campoTexto.setText(selecionada.getEnunciado());
        comboDificuldade.setSelectedItem(selecionada.getDificuldade());

        // Encontrar categoria
        for (int i = 0; i < comboCategoria.getItemCount(); i++) {
            Categoria categoria = comboCategoria.getItemAt(i);
            if (categoria.getPerguntas().contains(selecionada)) {
                comboCategoria.setSelectedItem(categoria);
                break;
            }
        }

        // Carregar alternativas
        String[] alternativas = selecionada.getAlternativas();
        for (int i = 0; i < Math.min(alternativas.length, camposRespostas.size()); i++) {
            camposRespostas.get(i).setText(alternativas[i]);
        }

        // Definir resposta correta
        comboRespostaCorreta.setSelectedItem(selecionada.getRespostaCorreta() + 1);
    }

    private void novaPergunta() {
        listaPerguntasDisponiveis.clearSelection();
        limparFormulario();
    }

    private void limparFormulario() {
        campoTexto.setText("");
        comboCategoria.setSelectedIndex(0);
        comboDificuldade.setSelectedIndex(0);
        for (JTextField campo : camposRespostas) {
            campo.setText("");
        }
        comboRespostaCorreta.setSelectedIndex(0);
        labelStatus.setText(" ");
    }

    private void salvarPergunta() {
        try {
            // Validar dados
            String enunciado = campoTexto.getText().trim();
            if (enunciado.isEmpty()) {
                exibirErro("Texto da pergunta é obrigatório");
                return;
            }

            Categoria categoria = (Categoria) comboCategoria.getSelectedItem();
            Pergunta.Dificuldade dificuldade = (Pergunta.Dificuldade) comboDificuldade.getSelectedItem();

            // Validar respostas
            String[] alternativas = new String[4];
            for (int i = 0; i < camposRespostas.size(); i++) {
                String textoResposta = camposRespostas.get(i).getText().trim();
                if (textoResposta.isEmpty()) {
                    exibirErro("Todas as respostas devem ser preenchidas");
                    return;
                }
                alternativas[i] = textoResposta;
            }

            Integer respostaCorretaObj = (Integer) comboRespostaCorreta.getSelectedItem();
            if (respostaCorretaObj == null) {
                exibirErro("Selecione a resposta correta");
                return;
            }
            int respostaCorretaIndex = respostaCorretaObj - 1;

            // Obter administrador atual
            Usuario usuarioLogado = sistema.getUsuarioLogado();
            if (!(usuarioLogado instanceof Administrador)) {
                exibirErro("Apenas administradores podem criar perguntas");
                return;
            }
            Administrador admin = (Administrador) usuarioLogado;

            // Criar pergunta
            Pergunta novaPergunta = new Pergunta(enunciado, alternativas, respostaCorretaIndex,
                    categoria, dificuldade, "", admin);

            // Verificar se é edição ou nova pergunta
            Pergunta perguntaSelecionada = listaPerguntasDisponiveis.getSelectedValue();
            if (perguntaSelecionada != null) {
                // Para edição, vamos simplesmente adicionar uma nova pergunta
                // (remover pergunta existente requereria métodos adicionais na categoria)
                categoria.adicionarPergunta(novaPergunta);
                exibirSucesso("Nova versão da pergunta criada com sucesso!");
            } else {
                // Nova pergunta
                categoria.adicionarPergunta(novaPergunta);
                exibirSucesso("Nova pergunta criada com sucesso!");
            }

            atualizarListaPerguntas();
            limparFormulario();

        } catch (Exception e) {
            exibirErro("Erro ao salvar pergunta: " + e.getMessage());
        }
    }

    private void excluirPergunta() {
        Pergunta selecionada = listaPerguntasDisponiveis.getSelectedValue();
        if (selecionada == null) {
            exibirErro("Selecione uma pergunta para excluir");
            return;
        }

        int opcao = JOptionPane.showConfirmDialog(
                this,
                "Deseja realmente excluir esta pergunta?",
                "Confirmar Exclusão",
                JOptionPane.YES_NO_OPTION
        );

        if (opcao == JOptionPane.YES_OPTION) {
            // Marcar pergunta como inativa ao invés de remover
            selecionada.setAtiva(false);
            atualizarListaPerguntas();
            limparFormulario();
            exibirSucesso("Pergunta desativada com sucesso!");
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

    // Renderer personalizado para a lista de perguntas
    private static class PerguntaListCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

            if (value instanceof Pergunta pergunta) {
                String texto = pergunta.getEnunciado();
                if (texto.length() > 50) {
                    texto = texto.substring(0, 47) + "...";
                }
                setText(String.format("<html><b>%s</b><br><small>%s - %s</small></html>",
                        texto,
                        pergunta.getDificuldade(),
                        pergunta.getCategoria().getNome()));
            }

            return this;
        }
    }
}
