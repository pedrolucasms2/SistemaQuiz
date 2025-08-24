package org.example.gui;

import org.example.*;
import javax.swing.*;
import java.awt.*;
import java.util.List; // Import correto para List

public class CriarJogoPanel extends JPanel {

    private QuizGameFrame framePrincipal;
    private JTextField campoNome;
    private JSpinner spinnerRodadas;
    private JSpinner spinnerTempo;
    private JComboBox<String> comboModalidade; // NOVA ADIÇÃO
    private JLabel labelStatus;

    public CriarJogoPanel(QuizGameFrame frame) {
        this.framePrincipal = frame;
        inicializarInterface();
    }

    private void inicializarInterface() {
        setLayout(new BorderLayout());
        setBackground(GerenciadorRecursos.carregarCor("claro"));

        JLabel titulo = new JLabel("Criar Novo Jogo");
        titulo.setFont(new Font("Arial", Font.BOLD, 24));
        titulo.setHorizontalAlignment(SwingConstants.CENTER);
        titulo.setBorder(BorderFactory.createEmptyBorder(30, 0, 30, 0));
        add(titulo, BorderLayout.NORTH);

        JPanel formulario = new JPanel(new GridBagLayout());
        formulario.setBackground(getBackground());
        formulario.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.anchor = GridBagConstraints.WEST;

        // Nome
        gbc.gridx = 0; gbc.gridy = 0;
        formulario.add(new JLabel("Nome do Jogo:"), gbc);
        campoNome = new JTextField(25);
        campoNome.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formulario.add(campoNome, gbc);

        // Rodadas
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE;
        formulario.add(new JLabel("Número de Rodadas:"), gbc);
        spinnerRodadas = new JSpinner(new SpinnerNumberModel(3, 1, 10, 1));
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formulario.add(spinnerRodadas, gbc);

        // Tempo
        gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE;
        formulario.add(new JLabel("Tempo por Pergunta (segundos):"), gbc);
        spinnerTempo = new JSpinner(new SpinnerNumberModel(30, 10, 180, 5));
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formulario.add(spinnerTempo, gbc);

        // Modalidade (NOVA ADIÇÃO)
        gbc.gridx = 0; gbc.gridy = 3; gbc.fill = GridBagConstraints.NONE;
        formulario.add(new JLabel("Modalidade:"), gbc);
        comboModalidade = new JComboBox<>(new String[]{
                "Individual", "Equipe", "Eliminatória"
        });
        comboModalidade.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formulario.add(comboModalidade, gbc);

        // Status
        labelStatus = new JLabel(" ");
        labelStatus.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        formulario.add(labelStatus, gbc);

        add(formulario, BorderLayout.CENTER);

        // Botões
        JPanel painelBotoes = new JPanel(new FlowLayout());
        painelBotoes.setBackground(getBackground());

        JButton botaoVoltar = new JButton("Voltar");
        botaoVoltar.setPreferredSize(new Dimension(100, 35));
        botaoVoltar.setBackground(Color.GRAY);
        botaoVoltar.setForeground(Color.WHITE);
        botaoVoltar.addActionListener(e -> framePrincipal.mostrarMenu());

        JButton botaoCriar = new JButton("Criar Jogo");
        botaoCriar.setPreferredSize(new Dimension(120, 35));
        botaoCriar.setBackground(GerenciadorRecursos.carregarCor("verde"));
        botaoCriar.setForeground(Color.WHITE);
        botaoCriar.setFont(new Font("Arial", Font.BOLD, 14));
        botaoCriar.addActionListener(e -> criarJogo());

        painelBotoes.add(botaoVoltar);
        painelBotoes.add(botaoCriar);
        add(painelBotoes, BorderLayout.SOUTH);
    }

    private void criarJogo() {
        try {
            String nome = campoNome.getText().trim();
            if (nome.isEmpty()) {
                exibirErro("Nome do jogo é obrigatório");
                return;
            }

            int rodadas = (Integer) spinnerRodadas.getValue();
            int tempo = (Integer) spinnerTempo.getValue();

            // Buscar categorias do sistema
            List<Categoria> categorias = framePrincipal.getSistema().getCategorias();

            // Criar modalidade baseada na seleção
            IModalidadeJogo modalidade;
            String modalidadeSelecionada = (String) comboModalidade.getSelectedItem();

            switch (modalidadeSelecionada) {
                case "Individual":
                    modalidade = new JogoIndividual();
                    break;
                case "Equipe":
                    modalidade = new JogoEquipe();
                    break;
                case "Eliminatória":
                    modalidade = new JogoEliminatoria();
                    break;
                default:
                    modalidade = new JogoIndividual();
            }

            // Chamar o método do sistema
            Jogo jogo = framePrincipal.getSistema().criarJogo(
                    nome,
                    categorias,
                    modalidade,
                    rodadas,
                    tempo
            );

            exibirSucesso("Jogo '" + nome + "' criado com sucesso! (Modalidade: " +
                    modalidadeSelecionada + ")");
            limparFormulario();

            Timer timer = new Timer(2000, e -> framePrincipal.mostrarMenu());
            timer.setRepeats(false);
            timer.start();

        } catch (Exception e) {
            exibirErro("Erro ao criar jogo: " + e.getMessage());
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

    private void limparFormulario() {
        campoNome.setText("");
        spinnerRodadas.setValue(3);
        spinnerTempo.setValue(30);
        comboModalidade.setSelectedIndex(0); // NOVA ADIÇÃO
        labelStatus.setText(" ");
    }
}
