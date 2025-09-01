package org.example.gui.dialogs;

import org.example.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GerenciadorUsuariosDialog extends JDialog {
    private final SistemaQuiz sistema;
    private JTextField campoNome;
    private JTextField campoEmail;
    private JPasswordField campoSenha;
    private JPasswordField campoConfirmarSenha;
    private JComboBox<String> comboTipoUsuario;
    private JComboBox<String> comboNivelAdmin;
    private JLabel labelStatus;
    private JList<Usuario> listaUsuarios;
    private DefaultListModel<Usuario> modeloListaUsuarios;

    public GerenciadorUsuariosDialog(Frame parent, SistemaQuiz sistema) {
        super(parent, "Gerenciar Usuários", true);
        this.sistema = sistema;
        inicializarInterface();
        atualizarListaUsuarios();
    }

    private void inicializarInterface() {
        setLayout(new BorderLayout());
        setSize(800, 600);
        setLocationRelativeTo(getParent());

        // Header
        JPanel header = new JPanel();
        header.setBackground(GerenciadorRecursos.carregarCor("azul"));
        header.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titulo = new JLabel("👥 Gerenciador de Usuários");
        titulo.setFont(new Font("Arial", Font.BOLD, 18));
        titulo.setForeground(Color.WHITE);
        titulo.setHorizontalAlignment(SwingConstants.CENTER);
        header.add(titulo);

        add(header, BorderLayout.NORTH);

        // Painel principal dividido
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(350);

        // Lado esquerdo - Lista de usuários
        JPanel painelLista = criarPainelListaUsuarios();
        splitPane.setLeftComponent(painelLista);

        // Lado direito - Formulário de criação
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

    private JPanel criarPainelListaUsuarios() {
        JPanel painel = new JPanel(new BorderLayout());
        painel.setBackground(GerenciadorRecursos.carregarCor("claro"));
        painel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 10));

        JLabel titulo = new JLabel("Usuários Cadastrados");
        titulo.setFont(new Font("Arial", Font.BOLD, 16));
        titulo.setForeground(GerenciadorRecursos.carregarCor("azul"));
        painel.add(titulo, BorderLayout.NORTH);

        modeloListaUsuarios = new DefaultListModel<>();
        listaUsuarios = new JList<>(modeloListaUsuarios);
        listaUsuarios.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listaUsuarios.setCellRenderer(new UsuarioListCellRenderer());

        JScrollPane scrollPane = new JScrollPane(listaUsuarios);
        scrollPane.setPreferredSize(new Dimension(320, 400));
        painel.add(scrollPane, BorderLayout.CENTER);

        // Botões de ação
        JPanel painelBotoes = new JPanel(new FlowLayout());
        painelBotoes.setBackground(painel.getBackground());

        JButton botaoAtualizar = new JButton("Atualizar");
        botaoAtualizar.setBackground(GerenciadorRecursos.carregarCor("azul"));
        botaoAtualizar.setForeground(Color.WHITE);
        botaoAtualizar.setOpaque(true);
        botaoAtualizar.setBorderPainted(false);
        botaoAtualizar.setFocusPainted(false);
        botaoAtualizar.addActionListener(e -> atualizarListaUsuarios());

        painelBotoes.add(botaoAtualizar);
        painel.add(painelBotoes, BorderLayout.SOUTH);

        return painel;
    }

    private JPanel criarPainelFormulario() {
        JPanel painel = new JPanel();
        painel.setLayout(new BoxLayout(painel, BoxLayout.Y_AXIS));
        painel.setBackground(GerenciadorRecursos.carregarCor("claro"));
        painel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 20));

        JLabel titulo = new JLabel("Criar Novo Usuário");
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

        // Nome
        gbc.gridx = 0; gbc.gridy = 0;
        formulario.add(new JLabel("Nome:"), gbc);

        campoNome = new JTextField(20);
        campoNome.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formulario.add(campoNome, gbc);

        // Email
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE;
        formulario.add(new JLabel("Email:"), gbc);

        campoEmail = new JTextField(20);
        campoEmail.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formulario.add(campoEmail, gbc);

        // Senha
        gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE;
        formulario.add(new JLabel("Senha:"), gbc);

        campoSenha = new JPasswordField(20);
        campoSenha.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formulario.add(campoSenha, gbc);

        // Confirmar Senha
        gbc.gridx = 0; gbc.gridy = 3; gbc.fill = GridBagConstraints.NONE;
        formulario.add(new JLabel("Confirmar Senha:"), gbc);

        campoConfirmarSenha = new JPasswordField(20);
        campoConfirmarSenha.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formulario.add(campoConfirmarSenha, gbc);

        // Tipo de usuário
        gbc.gridx = 0; gbc.gridy = 4; gbc.fill = GridBagConstraints.NONE;
        formulario.add(new JLabel("Tipo de Usuário:"), gbc);

        comboTipoUsuario = new JComboBox<>(new String[]{"JOGADOR", "ADMINISTRADOR"});
        comboTipoUsuario.addActionListener(e -> atualizarCamposAdmin());
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formulario.add(comboTipoUsuario, gbc);

        // Nível do admin (só aparece se for administrador)
        gbc.gridx = 0; gbc.gridy = 5; gbc.fill = GridBagConstraints.NONE;
        JLabel labelNivel = new JLabel("Nível Admin:");
        formulario.add(labelNivel, gbc);

        comboNivelAdmin = new JComboBox<>(new String[]{"MODERADOR", "STANDARD", "MASTER"});
        comboNivelAdmin.setVisible(false);
        labelNivel.setVisible(false);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formulario.add(comboNivelAdmin, gbc);

        painel.add(formulario);

        // Status
        labelStatus = new JLabel(" ");
        labelStatus.setAlignmentX(Component.CENTER_ALIGNMENT);
        painel.add(Box.createVerticalStrut(10));
        painel.add(labelStatus);

        // Botões
        JPanel painelBotoes = new JPanel(new FlowLayout());
        painelBotoes.setBackground(painel.getBackground());

        JButton botaoSalvar = new JButton("Criar Usuário");
        botaoSalvar.setPreferredSize(new Dimension(120, 35));
        botaoSalvar.setBackground(GerenciadorRecursos.carregarCor("verde"));
        botaoSalvar.setForeground(Color.WHITE);
        botaoSalvar.setOpaque(true);
        botaoSalvar.setBorderPainted(false);
        botaoSalvar.setFocusPainted(false);
        botaoSalvar.addActionListener(e -> criarUsuario());

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

    private void atualizarCamposAdmin() {
        String tipoSelecionado = (String) comboTipoUsuario.getSelectedItem();
        boolean isAdmin = "ADMINISTRADOR".equals(tipoSelecionado);

        // Encontrar e mostrar/ocultar campos do admin
        Container parent = comboNivelAdmin.getParent();
        Component[] components = parent.getComponents();

        for (Component comp : components) {
            if (comp instanceof JLabel && ((JLabel) comp).getText().equals("Nível Admin:")) {
                comp.setVisible(isAdmin);
            }
        }
        comboNivelAdmin.setVisible(isAdmin);
        parent.revalidate();
        parent.repaint();
    }

    private void atualizarListaUsuarios() {
        modeloListaUsuarios.clear();
        for (Usuario usuario : sistema.getUsuarios()) {
            modeloListaUsuarios.addElement(usuario);
        }
    }

    private void criarUsuario() {
        try {
            // Validar dados
            String nome = campoNome.getText().trim();
            String email = campoEmail.getText().trim();
            String senha = new String(campoSenha.getPassword());
            String confirmarSenha = new String(campoConfirmarSenha.getPassword());
            String tipoUsuario = (String) comboTipoUsuario.getSelectedItem();

            if (nome.isEmpty() || email.isEmpty() || senha.isEmpty()) {
                exibirErro("Todos os campos são obrigatórios");
                return;
            }

            if (!senha.equals(confirmarSenha)) {
                exibirErro("As senhas não coincidem");
                return;
            }

            if (senha.length() < 6) {
                exibirErro("A senha deve ter pelo menos 6 caracteres");
                return;
            }

            // Criar usuário baseado no tipo
            if ("JOGADOR".equals(tipoUsuario)) {
                sistema.cadastrarJogador(nome, email, senha);
                exibirSucesso("Jogador criado com sucesso!");
            } else if ("ADMINISTRADOR".equals(tipoUsuario)) {
                String nivelAdmin = (String) comboNivelAdmin.getSelectedItem();
                sistema.cadastrarAdministrador(nome, email, senha, nivelAdmin);
                exibirSucesso("Administrador criado com sucesso!");
            }

            atualizarListaUsuarios();
            limparFormulario();

        } catch (EmailJaExisteException e) {
            exibirErro("Este email já está cadastrado no sistema");
        } catch (Exception e) {
            exibirErro("Erro ao criar usuário: " + e.getMessage());
        }
    }

    private void limparFormulario() {
        campoNome.setText("");
        campoEmail.setText("");
        campoSenha.setText("");
        campoConfirmarSenha.setText("");
        comboTipoUsuario.setSelectedIndex(0);
        comboNivelAdmin.setSelectedIndex(0);
        labelStatus.setText(" ");
        atualizarCamposAdmin();
    }

    private void exibirErro(String mensagem) {
        labelStatus.setText(mensagem);
        labelStatus.setForeground(GerenciadorRecursos.carregarCor("vermelho"));
    }

    private void exibirSucesso(String mensagem) {
        labelStatus.setText(mensagem);
        labelStatus.setForeground(GerenciadorRecursos.carregarCor("verde"));
    }

    // Renderer personalizado para a lista de usuários
    private static class UsuarioListCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

            if (value instanceof Usuario usuario) {
                String icone = usuario instanceof Administrador ? "👑" : "👤";
                setText(String.format("<html>%s <b>%s</b><br><small>%s - %s</small></html>",
                        icone,
                        usuario.getNome(),
                        usuario.getEmail(),
                        usuario.getTipoUsuario()));
            }

            return this;
        }
    }
}
