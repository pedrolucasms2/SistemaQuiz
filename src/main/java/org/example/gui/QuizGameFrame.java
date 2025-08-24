package org.example.gui;

import org.example.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class QuizGameFrame extends JFrame {
    private static final int LARGURA_JANELA = 1000;
    private static final int ALTURA_JANELA = 700;

    private CardLayout cardLayout;
    private JPanel painelPrincipal;
    private SistemaQuiz sistema;

    private LoginPanel loginPanel;
    private MenuPrincipalPanel menuPanel;
    private CriarJogoPanel criarJogoPanel;

    public QuizGameFrame() {
        this.sistema = SistemaQuiz.getInstance();
        inicializarInterface();
        configurarJanela();
        mostrarLogin();
    }

    private void inicializarInterface() {
        cardLayout = new CardLayout();
        painelPrincipal = new JPanel(cardLayout);

        loginPanel = new LoginPanel(this);
        menuPanel = new MenuPrincipalPanel(this);
        criarJogoPanel = new CriarJogoPanel(this);

        painelPrincipal.add(loginPanel, "LOGIN");
        painelPrincipal.add(menuPanel, "MENU");
        painelPrincipal.add(criarJogoPanel, "CRIAR_JOGO");

        add(painelPrincipal);
    }

    private void configurarJanela() {
        setTitle("Sistema Quiz");
        setSize(LARGURA_JANELA, ALTURA_JANELA);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int opcao = JOptionPane.showConfirmDialog(
                        QuizGameFrame.this,
                        "Deseja realmente sair?",
                        "Confirmar Saída",
                        JOptionPane.YES_NO_OPTION
                );

                if (opcao == JOptionPane.YES_OPTION) {
                    System.exit(0);
                }
            }
        });
    }

    public void mostrarLogin() {
        cardLayout.show(painelPrincipal, "LOGIN");
    }

    public void mostrarMenu() {
        menuPanel.atualizarInterface();
        cardLayout.show(painelPrincipal, "MENU");
    }

    public void mostrarCriarJogo() {
        cardLayout.show(painelPrincipal, "CRIAR_JOGO");
    }

    public SistemaQuiz getSistema() {
        return sistema;
    }
}
