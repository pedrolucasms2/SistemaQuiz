package org.example.gui;

import org.example.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.ArrayList;

public class CriarJogoPanel extends JPanel {

    private QuizGameFrame framePrincipal;
    private JTextField campoNome;
    private JSpinner spinnerRodadas;
    private JSpinner spinnerTempo;
    private JComboBox<String> comboModalidade;
    private JComboBox<Categoria> comboFiltroCategorias;
    private JTable tabelaPerguntas;
    private PerguntaTableModel modeloTabelaPerguntas;
    private JTable tabelaUsuarios;
    private UsuarioTableModel modeloTabelaUsuarios;
    private JLabel labelStatus;
    private JLabel labelPerguntasSelecionadas;
    private JLabel labelUsuariosSelecionados;

    public CriarJogoPanel(QuizGameFrame frame) {
        this.framePrincipal = frame;
        inicializarInterface();
        carregarDados();
    }

    private void inicializarInterface() {
        setLayout(new BorderLayout());
        setBackground(GerenciadorRecursos.carregarCor("claro"));

        JLabel titulo = new JLabel("Criar Novo Jogo");
        titulo.setFont(new Font("Arial", Font.BOLD, 24));
        titulo.setHorizontalAlignment(SwingConstants.CENTER);
        titulo.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        add(titulo, BorderLayout.NORTH);

        // Painel principal com abas
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.BOLD, 14));

        // Aba 1: Configurações básicas
        JPanel abaConfiguracoes = criarAbaConfiguracoes();
        tabbedPane.addTab("Configurações", abaConfiguracoes);

        // Aba 2: Seleção de perguntas com checkboxes
        JPanel abaPerguntas = criarAbaPerguntasAprimorada();
        tabbedPane.addTab("Perguntas", abaPerguntas);

        // Aba 3: Seleção de usuários com checkboxes
        JPanel abaUsuarios = criarAbaUsuariosAprimorada();
        tabbedPane.addTab("Usuários", abaUsuarios);

        add(tabbedPane, BorderLayout.CENTER);

        // Painel de botões
        JPanel painelBotoes = criarPainelBotoes();
        add(painelBotoes, BorderLayout.SOUTH);
    }

    private JPanel criarAbaConfiguracoes() {
        JPanel painel = new JPanel(new GridBagLayout());
        painel.setBackground(getBackground());
        painel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.anchor = GridBagConstraints.WEST;

        // Nome
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel lblNome = new JLabel("Nome do Jogo:");
        lblNome.setFont(new Font("Arial", Font.BOLD, 14));
        painel.add(lblNome, gbc);

        campoNome = new JTextField(25);
        campoNome.setFont(new Font("Arial", Font.PLAIN, 14));
        campoNome.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(GerenciadorRecursos.carregarCor("azul"), 2),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        painel.add(campoNome, gbc);

        // Rodadas
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE;
        JLabel lblRodadas = new JLabel("Número de Rodadas:");
        lblRodadas.setFont(new Font("Arial", Font.BOLD, 14));
        painel.add(lblRodadas, gbc);

        spinnerRodadas = new JSpinner(new SpinnerNumberModel(3, 1, 10, 1));
        spinnerRodadas.setFont(new Font("Arial", Font.PLAIN, 14));
        ((JSpinner.DefaultEditor) spinnerRodadas.getEditor()).getTextField().setHorizontalAlignment(JTextField.CENTER);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        painel.add(spinnerRodadas, gbc);

        // Tempo
        gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE;
        JLabel lblTempo = new JLabel("Tempo por Pergunta (segundos):");
        lblTempo.setFont(new Font("Arial", Font.BOLD, 14));
        painel.add(lblTempo, gbc);

        spinnerTempo = new JSpinner(new SpinnerNumberModel(30, 10, 180, 5));
        spinnerTempo.setFont(new Font("Arial", Font.PLAIN, 14));
        ((JSpinner.DefaultEditor) spinnerTempo.getEditor()).getTextField().setHorizontalAlignment(JTextField.CENTER);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        painel.add(spinnerTempo, gbc);

        // Modalidade
        gbc.gridx = 0; gbc.gridy = 3; gbc.fill = GridBagConstraints.NONE;
        JLabel lblModalidade = new JLabel("Modalidade:");
        lblModalidade.setFont(new Font("Arial", Font.BOLD, 14));
        painel.add(lblModalidade, gbc);

        comboModalidade = new JComboBox<>(new String[]{"Individual", "Equipe", "Eliminatória"});
        comboModalidade.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        painel.add(comboModalidade, gbc);

        // Status
        labelStatus = new JLabel(" ");
        labelStatus.setHorizontalAlignment(SwingConstants.CENTER);
        labelStatus.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        painel.add(labelStatus, gbc);

        return painel;
    }

    private JPanel criarAbaPerguntasAprimorada() {
        JPanel painel = new JPanel(new BorderLayout());
        painel.setBackground(getBackground());
        painel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Painel superior com filtros
        JPanel painelFiltros = new JPanel(new FlowLayout(FlowLayout.LEFT));
        painelFiltros.setBackground(painel.getBackground());

        JLabel lblFiltro = new JLabel("Filtrar por categoria:");
        lblFiltro.setFont(new Font("Arial", Font.BOLD, 14));
        painelFiltros.add(lblFiltro);

        comboFiltroCategorias = new JComboBox<>();
        comboFiltroCategorias.addItem(null); // Opção "Todas"
        comboFiltroCategorias.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value == null) {
                    setText("Todas as Categorias");
                } else if (value instanceof Categoria) {
                    setText(((Categoria) value).getNome());
                }
                return this;
            }
        });
        comboFiltroCategorias.addActionListener(e -> filtrarPerguntas());
        painelFiltros.add(comboFiltroCategorias);

        // Botões de seleção rápida
        JButton btnSelecionarTodas = new JButton("Selecionar Todas");
        btnSelecionarTodas.setBackground(GerenciadorRecursos.carregarCor("verde"));
        btnSelecionarTodas.setForeground(Color.WHITE);
        btnSelecionarTodas.setOpaque(true);
        btnSelecionarTodas.setBorderPainted(false);
        btnSelecionarTodas.addActionListener(e -> selecionarTodasPerguntas(true));
        painelFiltros.add(btnSelecionarTodas);

        JButton btnDeselecionarTodas = new JButton("Desmarcar Todas");
        btnDeselecionarTodas.setBackground(GerenciadorRecursos.carregarCor("vermelho"));
        btnDeselecionarTodas.setForeground(Color.WHITE);
        btnDeselecionarTodas.setOpaque(true);
        btnDeselecionarTodas.setBorderPainted(false);
        btnDeselecionarTodas.addActionListener(e -> selecionarTodasPerguntas(false));
        painelFiltros.add(btnDeselecionarTodas);

        painel.add(painelFiltros, BorderLayout.NORTH);

        // Tabela de perguntas com checkboxes
        modeloTabelaPerguntas = new PerguntaTableModel();
        tabelaPerguntas = new JTable(modeloTabelaPerguntas);
        tabelaPerguntas.setFont(new Font("Arial", Font.PLAIN, 12));
        tabelaPerguntas.setRowHeight(25);
        tabelaPerguntas.getColumnModel().getColumn(0).setPreferredWidth(50);  // Checkbox
        tabelaPerguntas.getColumnModel().getColumn(1).setPreferredWidth(300); // Pergunta
        tabelaPerguntas.getColumnModel().getColumn(2).setPreferredWidth(100); // Categoria
        tabelaPerguntas.getColumnModel().getColumn(3).setPreferredWidth(80);  // Dificuldade

        JScrollPane scrollPerguntas = new JScrollPane(tabelaPerguntas);
        scrollPerguntas.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(GerenciadorRecursos.carregarCor("azul"), 2),
            "Selecione as Perguntas para o Jogo"
        ));
        painel.add(scrollPerguntas, BorderLayout.CENTER);

        // Painel inferior com contador
        labelPerguntasSelecionadas = new JLabel("Perguntas selecionadas: 0");
        labelPerguntasSelecionadas.setFont(new Font("Arial", Font.BOLD, 14));
        labelPerguntasSelecionadas.setForeground(GerenciadorRecursos.carregarCor("azul"));
        labelPerguntasSelecionadas.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        painel.add(labelPerguntasSelecionadas, BorderLayout.SOUTH);

        return painel;
    }

    private JPanel criarAbaUsuariosAprimorada() {
        JPanel painel = new JPanel(new BorderLayout());
        painel.setBackground(getBackground());
        painel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Painel superior com botões de seleção
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.LEFT));
        painelBotoes.setBackground(painel.getBackground());

        JButton btnSelecionarTodos = new JButton("Selecionar Todos");
        btnSelecionarTodos.setBackground(GerenciadorRecursos.carregarCor("verde"));
        btnSelecionarTodos.setForeground(Color.WHITE);
        btnSelecionarTodos.setOpaque(true);
        btnSelecionarTodos.setBorderPainted(false);
        btnSelecionarTodos.addActionListener(e -> selecionarTodosUsuarios(true));
        painelBotoes.add(btnSelecionarTodos);

        JButton btnDeselecionarTodos = new JButton("Desmarcar Todos");
        btnDeselecionarTodos.setBackground(GerenciadorRecursos.carregarCor("vermelho"));
        btnDeselecionarTodos.setForeground(Color.WHITE);
        btnDeselecionarTodos.setOpaque(true);
        btnDeselecionarTodos.setBorderPainted(false);
        btnDeselecionarTodos.addActionListener(e -> selecionarTodosUsuarios(false));
        painelBotoes.add(btnDeselecionarTodos);

        painel.add(painelBotoes, BorderLayout.NORTH);

        // Tabela de usuários com checkboxes
        modeloTabelaUsuarios = new UsuarioTableModel();
        tabelaUsuarios = new JTable(modeloTabelaUsuarios);
        tabelaUsuarios.setFont(new Font("Arial", Font.PLAIN, 12));
        tabelaUsuarios.setRowHeight(25);
        tabelaUsuarios.getColumnModel().getColumn(0).setPreferredWidth(50);  // Checkbox
        tabelaUsuarios.getColumnModel().getColumn(1).setPreferredWidth(200); // Nome
        tabelaUsuarios.getColumnModel().getColumn(2).setPreferredWidth(250); // Email
        tabelaUsuarios.getColumnModel().getColumn(3).setPreferredWidth(100); // Tipo

        JScrollPane scrollUsuarios = new JScrollPane(tabelaUsuarios);
        scrollUsuarios.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(GerenciadorRecursos.carregarCor("azul"), 2),
            "Selecione os Usuários para o Jogo"
        ));
        painel.add(scrollUsuarios, BorderLayout.CENTER);

        // Painel inferior com contador
        labelUsuariosSelecionados = new JLabel("Usuários selecionados: 0");
        labelUsuariosSelecionados.setFont(new Font("Arial", Font.BOLD, 14));
        labelUsuariosSelecionados.setForeground(GerenciadorRecursos.carregarCor("azul"));
        labelUsuariosSelecionados.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        painel.add(labelUsuariosSelecionados, BorderLayout.SOUTH);

        return painel;
    }

    private JPanel criarPainelBotoes() {
        JPanel painel = new JPanel(new FlowLayout());
        painel.setBackground(getBackground());
        painel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JButton botaoVoltar = new JButton("← Voltar");
        botaoVoltar.setPreferredSize(new Dimension(120, 45));
        botaoVoltar.setBackground(Color.GRAY);
        botaoVoltar.setForeground(Color.WHITE);
        botaoVoltar.setFont(new Font("Arial", Font.BOLD, 14));
        botaoVoltar.setOpaque(true);
        botaoVoltar.setBorderPainted(false);
        botaoVoltar.setFocusPainted(false);
        botaoVoltar.addActionListener(e -> framePrincipal.mostrarMenu());

        JButton botaoCriar = new JButton("Criar Jogo");
        botaoCriar.setPreferredSize(new Dimension(150, 45));
        botaoCriar.setBackground(GerenciadorRecursos.carregarCor("verde"));
        botaoCriar.setForeground(Color.WHITE);
        botaoCriar.setFont(new Font("Arial", Font.BOLD, 14));
        botaoCriar.setOpaque(true);
        botaoCriar.setBorderPainted(false);
        botaoCriar.setFocusPainted(false);
        botaoCriar.addActionListener(e -> criarJogo());

        painel.add(botaoVoltar);
        painel.add(botaoCriar);

        return painel;
    }

    // Métodos auxiliares
    private void filtrarPerguntas() {
        Categoria categoriaSelecionada = (Categoria) comboFiltroCategorias.getSelectedItem();
        modeloTabelaPerguntas.filtrarPorCategoria(categoriaSelecionada);
        atualizarContadorPerguntas();
    }

    private void selecionarTodasPerguntas(boolean selecionar) {
        modeloTabelaPerguntas.selecionarTodas(selecionar);
        atualizarContadorPerguntas();
    }

    private void selecionarTodosUsuarios(boolean selecionar) {
        modeloTabelaUsuarios.selecionarTodos(selecionar);
        atualizarContadorUsuarios();
    }

    private void atualizarContadorPerguntas() {
        int selecionadas = modeloTabelaPerguntas.getQuantidadeSelecionadas();
        labelPerguntasSelecionadas.setText("Perguntas selecionadas: " + selecionadas);
    }

    private void atualizarContadorUsuarios() {
        int selecionados = modeloTabelaUsuarios.getQuantidadeSelecionados();
        labelUsuariosSelecionados.setText("Usuários selecionados: " + selecionados);
    }

    public void carregarDados() {
        carregarCategorias();
        carregarPerguntas();
        carregarUsuarios();
    }

    private void carregarCategorias() {
        comboFiltroCategorias.removeAllItems();
        comboFiltroCategorias.addItem(null); // Todas as categorias
        for (Categoria categoria : framePrincipal.getSistema().getCategorias()) {
            comboFiltroCategorias.addItem(categoria);
        }
    }

    private void carregarPerguntas() {
        List<Pergunta> todasPerguntas = new ArrayList<>();
        for (Categoria categoria : framePrincipal.getSistema().getCategorias()) {
            for (Pergunta pergunta : categoria.getPerguntas()) {
                if (pergunta.isAtiva()) {
                    todasPerguntas.add(pergunta);
                }
            }
        }
        modeloTabelaPerguntas.setPerguntas(todasPerguntas);
        atualizarContadorPerguntas();
    }

    public void carregarUsuarios() {
        List<Usuario> jogadores = new ArrayList<>();
        for (Usuario usuario : framePrincipal.getSistema().getUsuarios()) {
            if (usuario instanceof Jogador) {
                jogadores.add(usuario);
            }
        }
        modeloTabelaUsuarios.setUsuarios(jogadores);
        atualizarContadorUsuarios();
    }

    private void criarJogo() {
        try {
            // Validações
            String nome = campoNome.getText().trim();
            if (nome.isEmpty()) {
                exibirErro("Nome do jogo é obrigatório");
                return;
            }

            List<Pergunta> perguntasSelecionadas = modeloTabelaPerguntas.getPerguntasSelecionadas();
            if (perguntasSelecionadas.isEmpty()) {
                exibirErro("Selecione pelo menos uma pergunta para o jogo");
                return;
            }

            int rodadas = (Integer) spinnerRodadas.getValue();
            int tempo = (Integer) spinnerTempo.getValue();

            // Buscar categorias das perguntas selecionadas
            List<Categoria> categorias = new ArrayList<>();
            for (Pergunta pergunta : perguntasSelecionadas) {
                if (!categorias.contains(pergunta.getCategoria())) {
                    categorias.add(pergunta.getCategoria());
                }
            }

            // Criar modalidade
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

            // Criar jogo
            Jogo jogo = framePrincipal.getSistema().criarJogo(
                    nome, categorias, modalidade, rodadas, tempo, perguntasSelecionadas);

            // Atribuir usuários selecionados
            List<Usuario> usuariosSelecionados = modeloTabelaUsuarios.getUsuariosSelecionados();
            for (Usuario usuario : usuariosSelecionados) {
                jogo.adicionarUsuario(usuario);
            }

            jogo.salvarJogo();

            if (!usuariosSelecionados.isEmpty()) {
                jogo.iniciar();
            }

            exibirSucesso(String.format("Jogo '%s' criado com sucesso!\n" +
                    "Modalidade: %s\n" +
                    "Perguntas: %d\n" +
                    "Usuários: %d",
                    nome, modalidadeSelecionada,
                    perguntasSelecionadas.size(),
                    usuariosSelecionados.size()));

            limparFormulario();

            Timer timer = new Timer(4000, e -> framePrincipal.mostrarMenu());
            timer.setRepeats(false);
            timer.start();

        } catch (Exception e) {
            exibirErro("Erro ao criar jogo: " + e.getMessage());
        }
    }

    private void exibirErro(String mensagem) {
        labelStatus.setText("<html><div style='color: red; text-align: center;'>" + mensagem + "</div></html>");
    }

    private void exibirSucesso(String mensagem) {
        labelStatus.setText("<html><div style='color: green; text-align: center;'>" +
            mensagem.replace("\n", "<br>") + "</div></html>");
    }

    private void limparFormulario() {
        campoNome.setText("");
        spinnerRodadas.setValue(3);
        spinnerTempo.setValue(30);
        comboModalidade.setSelectedIndex(0);
        modeloTabelaPerguntas.limparSelecoes();
        modeloTabelaUsuarios.limparSelecoes();
        atualizarContadorPerguntas();
        atualizarContadorUsuarios();
        labelStatus.setText(" ");
    }

    // Modelo de tabela para perguntas
    private class PerguntaTableModel extends javax.swing.table.AbstractTableModel {
        private List<Pergunta> perguntas = new ArrayList<>();
        private List<Pergunta> perguntasFiltradas = new ArrayList<>();
        private List<Boolean> selecoes = new ArrayList<>();
        private final String[] colunas = {"Selecionada", "Pergunta", "Categoria", "Dificuldade"};

        public void setPerguntas(List<Pergunta> perguntas) {
            this.perguntas = new ArrayList<>(perguntas);
            this.perguntasFiltradas = new ArrayList<>(perguntas);
            this.selecoes = new ArrayList<>();
            for (int i = 0; i < perguntas.size(); i++) {
                selecoes.add(false);
            }
            fireTableDataChanged();
        }

        public void filtrarPorCategoria(Categoria categoria) {
            perguntasFiltradas.clear();
            selecoes.clear();

            for (Pergunta pergunta : perguntas) {
                if (categoria == null || pergunta.getCategoria().equals(categoria)) {
                    perguntasFiltradas.add(pergunta);
                    selecoes.add(false);
                }
            }
            fireTableDataChanged();
        }

        public void selecionarTodas(boolean selecionar) {
            for (int i = 0; i < selecoes.size(); i++) {
                selecoes.set(i, selecionar);
            }
            fireTableDataChanged();
        }

        public void limparSelecoes() {
            for (int i = 0; i < selecoes.size(); i++) {
                selecoes.set(i, false);
            }
            fireTableDataChanged();
        }

        public List<Pergunta> getPerguntasSelecionadas() {
            List<Pergunta> selecionadas = new ArrayList<>();
            for (int i = 0; i < Math.min(perguntasFiltradas.size(), selecoes.size()); i++) {
                if (selecoes.get(i)) {
                    selecionadas.add(perguntasFiltradas.get(i));
                }
            }
            return selecionadas;
        }

        public int getQuantidadeSelecionadas() {
            return (int) selecoes.stream().mapToInt(b -> b ? 1 : 0).sum();
        }

        @Override
        public int getRowCount() { return perguntasFiltradas.size(); }

        @Override
        public int getColumnCount() { return colunas.length; }

        @Override
        public String getColumnName(int column) { return colunas[column]; }

        @Override
        public Class<?> getColumnClass(int column) {
            return column == 0 ? Boolean.class : String.class;
        }

        @Override
        public boolean isCellEditable(int row, int column) { return column == 0; }

        @Override
        public Object getValueAt(int row, int column) {
            if (row >= perguntasFiltradas.size()) return null;

            Pergunta pergunta = perguntasFiltradas.get(row);
            switch (column) {
                case 0: return selecoes.get(row);
                case 1:
                    String texto = pergunta.getEnunciado();
                    return texto.length() > 60 ? texto.substring(0, 57) + "..." : texto;
                case 2: return pergunta.getCategoria().getNome();
                case 3: return pergunta.getDificuldade().toString();
                default: return null;
            }
        }

        @Override
        public void setValueAt(Object value, int row, int column) {
            if (column == 0 && row < selecoes.size()) {
                selecoes.set(row, (Boolean) value);
                fireTableCellUpdated(row, column);
                SwingUtilities.invokeLater(() -> atualizarContadorPerguntas());
            }
        }
    }

    // Modelo de tabela para usuários
    private class UsuarioTableModel extends javax.swing.table.AbstractTableModel {
        private List<Usuario> usuarios = new ArrayList<>();
        private List<Boolean> selecoes = new ArrayList<>();
        private final String[] colunas = {"Selecionado", "Nome", "Email", "Tipo"};

        public void setUsuarios(List<Usuario> usuarios) {
            this.usuarios = new ArrayList<>(usuarios);
            this.selecoes = new ArrayList<>();
            for (int i = 0; i < usuarios.size(); i++) {
                selecoes.add(false);
            }
            fireTableDataChanged();
        }

        public void selecionarTodos(boolean selecionar) {
            for (int i = 0; i < selecoes.size(); i++) {
                selecoes.set(i, selecionar);
            }
            fireTableDataChanged();
        }

        public void limparSelecoes() {
            for (int i = 0; i < selecoes.size(); i++) {
                selecoes.set(i, false);
            }
            fireTableDataChanged();
        }

        public List<Usuario> getUsuariosSelecionados() {
            List<Usuario> selecionados = new ArrayList<>();
            for (int i = 0; i < Math.min(usuarios.size(), selecoes.size()); i++) {
                if (selecoes.get(i)) {
                    selecionados.add(usuarios.get(i));
                }
            }
            return selecionados;
        }

        public int getQuantidadeSelecionados() {
            return (int) selecoes.stream().mapToInt(b -> b ? 1 : 0).sum();
        }

        @Override
        public int getRowCount() { return usuarios.size(); }

        @Override
        public int getColumnCount() { return colunas.length; }

        @Override
        public String getColumnName(int column) { return colunas[column]; }

        @Override
        public Class<?> getColumnClass(int column) {
            return column == 0 ? Boolean.class : String.class;
        }

        @Override
        public boolean isCellEditable(int row, int column) { return column == 0; }

        @Override
        public Object getValueAt(int row, int column) {
            if (row >= usuarios.size()) return null;

            Usuario usuario = usuarios.get(row);
            switch (column) {
                case 0: return selecoes.get(row);
                case 1: return usuario.getNome();
                case 2: return usuario.getEmail();
                case 3: return usuario instanceof Administrador ? "Admin" : "Jogador";
                default: return null;
            }
        }

        @Override
        public void setValueAt(Object value, int row, int column) {
            if (column == 0 && row < selecoes.size()) {
                selecoes.set(row, (Boolean) value);
                fireTableCellUpdated(row, column);
                SwingUtilities.invokeLater(() -> atualizarContadorUsuarios());
            }
        }
    }
}
