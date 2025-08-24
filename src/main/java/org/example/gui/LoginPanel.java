package org.example.gui;

import org.example.*;
import javax.swing.*;
import java.awt.*;

public class LoginPanel extends JPanel implements LoginView {
    private QuizGameFrame framePrincipal;
    private LoginControler controller;

    private JTextField campoEmail;
    private JPasswordField campoSenha;
    private JButton botaoLogin;
    private JLabel labelStatus;

    public LoginPanel(QuizGameFrame frame) {
        this.framePrincipal = frame;
        this.controller = new LoginControler(this);
        inicializarInterface();
    }

    private void inicializarInterface() {
        setLayout(new BorderLayout());
        setBackground(GerenciadorRecursos.carregarCor("claro"));

        JPanel painelFormulario = new JPanel(new GridBagLayout());
        painelFormulario.setBackground(getBackground());
        painelFormulario.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // Título
        JLabel titulo = new JLabel("Sistema Quiz");
        titulo.setFont(new Font("Arial", Font.BOLD, 28));
        titulo.setForeground(GerenciadorRecursos.carregarCor("azul"));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        painelFormulario.add(titulo, gbc);

        gbc.gridy = 1;
        painelFormulario.add(Box.createVerticalStrut(30), gbc);

        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;

        // Email
        gbc.gridx = 0; gbc.gridy = 2;
        painelFormulario.add(new JLabel("Email:"), gbc);

        campoEmail = new JTextField(20);
        campoEmail.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 1;
        painelFormulario.add(campoEmail, gbc);

        // Senha
        gbc.gridx = 0; gbc.gridy = 3;
        painelFormulario.add(new JLabel("Senha:"), gbc);

        campoSenha = new JPasswordField(20);
        campoSenha.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 1;
        painelFormulario.add(campoSenha, gbc);

        // Botão
        botaoLogin = new JButton("Entrar");
        botaoLogin.setPreferredSize(new Dimension(100, 35));
        botaoLogin.setBackground(GerenciadorRecursos.carregarCor("azul"));
        botaoLogin.setForeground(Color.WHITE);
        botaoLogin.setOpaque(true);
        botaoLogin.setBorderPainted(false);
        botaoLogin.setFocusPainted(false);
        botaoLogin.addActionListener(e -> processarLogin());

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        painelFormulario.add(botaoLogin, gbc);

        // Status
        labelStatus = new JLabel(" ");
        labelStatus.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 5;
        painelFormulario.add(labelStatus, gbc);

        add(painelFormulario, BorderLayout.CENTER);

        campoEmail.addActionListener(e -> processarLogin());
        campoSenha.addActionListener(e -> processarLogin());
    }

    private void processarLogin() {
        String email = campoEmail.getText().trim();
        String senha = new String(campoSenha.getPassword());

        if (email.isEmpty() || senha.isEmpty()) {
            exibirErro("Preencha todos os campos");
            return;
        }

        controller.processarLogin(email, senha);
    }

    @Override
    public void exibirErro(String mensagem) {
        labelStatus.setText(mensagem);
        labelStatus.setForeground(GerenciadorRecursos.carregarCor("vermelho"));
    }

    @Override
    public void exibirErros(java.util.List<String> erros) {
        String mensagem = String.join(", ", erros);
        exibirErro(mensagem);
    }

    @Override
    public void exibirSucesso(String mensagem) {
        labelStatus.setText(mensagem);
        labelStatus.setForeground(GerenciadorRecursos.carregarCor("verde"));
    }

    @Override
    public void limparFormulario() {
        campoEmail.setText("");
        campoSenha.setText("");
        labelStatus.setText(" ");
    }

    @Override
    public void redirecionarParaMenuPrincipal() {
        framePrincipal.mostrarMenu();
    }
}
