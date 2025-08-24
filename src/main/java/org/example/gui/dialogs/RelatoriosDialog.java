package org.example.gui.dialogs;

import org.example.*;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class RelatoriosDialog extends JDialog {
    private SistemaQuiz sistema;
    private JTextArea areaRelatorio;
    private JComboBox<String> comboTipoRelatorio;

    public RelatoriosDialog(Frame parent, SistemaQuiz sistema) {
        super(parent, "Relatórios Administrativos", true);
        this.sistema = sistema;
        inicializarInterface();
    }

    private void inicializarInterface() {
        setLayout(new BorderLayout());
        setSize(800, 600);
        setLocationRelativeTo(getParent());

        // Header
        JPanel header = new JPanel();
        header.setBackground(GerenciadorRecursos.carregarCor("azul"));
        header.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titulo = new JLabel("📊 Relatórios do Sistema");
        titulo.setFont(new Font("Arial", Font.BOLD, 18));
        titulo.setForeground(Color.WHITE);
        titulo.setHorizontalAlignment(SwingConstants.CENTER);
        header.add(titulo);

        add(header, BorderLayout.NORTH);

        // Painel de controle
        JPanel painelControle = new JPanel(new FlowLayout());
        painelControle.setBackground(GerenciadorRecursos.carregarCor("claro"));
        painelControle.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel labelTipo = new JLabel("Tipo de Relatório:");
        labelTipo.setFont(new Font("Arial", Font.BOLD, 14));

        comboTipoRelatorio = new JComboBox<>(new String[]{
            "Estatísticas Gerais do Sistema",
            "Ranking Global de Jogadores",
            "Relatório de Jogos",
            "Relatório de Categorias e Perguntas",
            "Atividade dos Usuários"
        });
        comboTipoRelatorio.setPreferredSize(new Dimension(250, 30));

        JButton botaoGerar = new JButton("Gerar Relatório");
        botaoGerar.setBackground(GerenciadorRecursos.carregarCor("verde"));
        botaoGerar.setForeground(Color.WHITE);
        botaoGerar.setFont(new Font("Arial", Font.BOLD, 14));
        botaoGerar.addActionListener(e -> gerarRelatorio());

        painelControle.add(labelTipo);
        painelControle.add(comboTipoRelatorio);
        painelControle.add(botaoGerar);

        add(painelControle, BorderLayout.NORTH);

        // Área do relatório
        areaRelatorio = new JTextArea();
        areaRelatorio.setFont(new Font("Courier New", Font.PLAIN, 12));
        areaRelatorio.setEditable(false);
        areaRelatorio.setBackground(Color.WHITE);
        areaRelatorio.setText("Selecione um tipo de relatório e clique em 'Gerar Relatório'");

        JScrollPane scrollPane = new JScrollPane(areaRelatorio);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        add(scrollPane, BorderLayout.CENTER);

        // Footer
        JPanel footer = new JPanel(new FlowLayout());
        footer.setBackground(GerenciadorRecursos.carregarCor("claro"));

        JButton botaoFechar = new JButton("Fechar");
        botaoFechar.setPreferredSize(new Dimension(100, 35));
        botaoFechar.setBackground(Color.GRAY);
        botaoFechar.setForeground(Color.WHITE);
        botaoFechar.addActionListener(e -> dispose());

        footer.add(botaoFechar);
        add(footer, BorderLayout.SOUTH);
    }

    private void gerarRelatorio() {
        String tipoSelecionado = (String) comboTipoRelatorio.getSelectedItem();
        StringBuilder relatorio = new StringBuilder();

        switch (tipoSelecionado) {
            case "Estatísticas Gerais do Sistema":
                relatorio.append(gerarEstatisticasGerais());
                break;
            case "Ranking Global de Jogadores":
                relatorio.append(gerarRankingGlobal());
                break;
            case "Relatório de Jogos":
                relatorio.append(gerarRelatorioJogos());
                break;
            case "Relatório de Categorias e Perguntas":
                relatorio.append(gerarRelatorioCategorias());
                break;
            case "Atividade dos Usuários":
                relatorio.append(gerarRelatorioAtividade());
                break;
        }

        areaRelatorio.setText(relatorio.toString());
        areaRelatorio.setCaretPosition(0);
    }

    private String gerarEstatisticasGerais() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== ESTATÍSTICAS GERAIS DO SISTEMA ===\n\n");

        sb.append("📊 RESUMO GERAL\n");
        sb.append("Usuários cadastrados: ").append(sistema.getUsuarios().size()).append("\n");
        sb.append("Jogos criados: ").append(sistema.getJogos().size()).append("\n");
        sb.append("Categorias disponíveis: ").append(sistema.getCategorias().size()).append("\n");
        sb.append("Conquistas disponíveis: ").append(sistema.getConquistasDisponiveis().size()).append("\n\n");

        // Contar jogadores vs administradores
        long jogadores = sistema.getUsuarios().stream().filter(u -> u instanceof Jogador).count();
        long admins = sistema.getUsuarios().stream().filter(u -> u instanceof Administrador).count();

        sb.append("👥 TIPOS DE USUÁRIO\n");
        sb.append("Jogadores: ").append(jogadores).append("\n");
        sb.append("Administradores: ").append(admins).append("\n\n");

        // Estatísticas de perguntas
        int totalPerguntas = 0;
        for (Categoria categoria : sistema.getCategorias()) {
            totalPerguntas += categoria.getPerguntas().size();
        }
        sb.append("❓ PERGUNTAS\n");
        sb.append("Total de perguntas: ").append(totalPerguntas).append("\n");
        sb.append("Média por categoria: ").append(
            sistema.getCategorias().isEmpty() ? 0 : totalPerguntas / sistema.getCategorias().size()
        ).append("\n\n");

        return sb.toString();
    }

    private String gerarRankingGlobal() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== RANKING GLOBAL DE JOGADORES ===\n\n");

        List<Jogador> jogadores = sistema.getUsuarios().stream()
            .filter(u -> u instanceof Jogador)
            .map(u -> (Jogador) u)
            .sorted((j1, j2) -> Integer.compare(
                j2.getPontuacaoTotal(),
                j1.getPontuacaoTotal()
            ))
            .limit(20)
            .toList();

        if (jogadores.isEmpty()) {
            sb.append("Nenhum jogador encontrado.\n");
        } else {
            sb.append(String.format("%-4s %-25s %-8s %-8s %-10s\n",
                "Pos.", "Nome", "Jogos", "Vitórias", "Pontos"));
            sb.append("-".repeat(60)).append("\n");

            for (int i = 0; i < jogadores.size(); i++) {
                Jogador jogador = jogadores.get(i);
                sb.append(String.format("%-4d %-25s %-8d %-8d %-10d\n",
                    i + 1,
                    jogador.getNome().length() > 25 ? jogador.getNome().substring(0, 22) + "..." : jogador.getNome(),
                    jogador.getJogosParticipados(),
                    jogador.getNumeroVitorias(),
                    jogador.getPontuacaoTotal()
                ));
            }
        }

        return sb.toString();
    }

    private String gerarRelatorioJogos() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== RELATÓRIO DE JOGOS ===\n\n");

        List<Jogo> jogos = sistema.getJogos();
        if (jogos.isEmpty()) {
            sb.append("Nenhum jogo criado ainda.\n");
        } else {
            sb.append(String.format("%-3s %-25s %-12s %-15s %-12s\n",
                "ID", "Nome", "Status", "Modalidade", "Participantes"));
            sb.append("-".repeat(70)).append("\n");

            for (int i = 0; i < jogos.size(); i++) {
                Jogo jogo = jogos.get(i);
                sb.append(String.format("%-3d %-25s %-12s %-15s %-12d\n",
                    i + 1,
                    jogo.getNome().length() > 25 ? jogo.getNome().substring(0, 22) + "..." : jogo.getNome(),
                    jogo.getStatus(),
                    jogo.getModalidade().getClass().getSimpleName(),
                    jogo.getParticipantes().size()
                ));
            }

            sb.append("\n📈 ESTATÍSTICAS DOS JOGOS\n");
            long jogosAtivos = jogos.stream().filter(j -> j.getStatus() == Jogo.StatusJogo.EM_ANDAMENTO).count();
            long jogosConcluidos = jogos.stream().filter(j -> j.getStatus() == Jogo.StatusJogo.FINALIZADO).count();

            sb.append("Jogos ativos: ").append(jogosAtivos).append("\n");
            sb.append("Jogos concluídos: ").append(jogosConcluidos).append("\n");
        }

        return sb.toString();
    }

    private String gerarRelatorioCategorias() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== RELATÓRIO DE CATEGORIAS E PERGUNTAS ===\n\n");

        List<Categoria> categorias = sistema.getCategorias();
        if (categorias.isEmpty()) {
            sb.append("Nenhuma categoria criada ainda.\n");
        } else {
            sb.append(String.format("%-25s %-12s %-30s\n",
                "Categoria", "Perguntas", "Descrição"));
            sb.append("-".repeat(70)).append("\n");

            for (Categoria categoria : categorias) {
                sb.append(String.format("%-25s %-12d %-30s\n",
                    categoria.getNome().length() > 25 ? categoria.getNome().substring(0, 22) + "..." : categoria.getNome(),
                    categoria.getPerguntas().size(),
                    categoria.getDescricao().length() > 30 ? categoria.getDescricao().substring(0, 27) + "..." : categoria.getDescricao()
                ));
            }

            sb.append("\n📊 ESTATÍSTICAS POR DIFICULDADE\n");
            int facil = 0, medio = 0, dificil = 0;
            for (Categoria categoria : categorias) {
                for (Pergunta pergunta : categoria.getPerguntas()) {
                    switch (pergunta.getDificuldade()) {
                        case FACIL -> facil++;
                        case MEDIO -> medio++;
                        case DIFICIL -> dificil++;
                    }
                }
            }

            sb.append("Fácil: ").append(facil).append("\n");
            sb.append("Médio: ").append(medio).append("\n");
            sb.append("Difícil: ").append(dificil).append("\n");
        }

        return sb.toString();
    }

    private String gerarRelatorioAtividade() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== RELATÓRIO DE ATIVIDADE DOS USUÁRIOS ===\n\n");

        List<Usuario> usuarios = sistema.getUsuarios();
        if (usuarios.isEmpty()) {
            sb.append("Nenhum usuário cadastrado.\n");
        } else {
            sb.append("👥 USUÁRIOS MAIS ATIVOS\n");
            sb.append(String.format("%-25s %-10s %-12s %-15s\n",
                "Nome", "Tipo", "Jogos", "Última Atividade"));
            sb.append("-".repeat(65)).append("\n");

            for (Usuario usuario : usuarios) {
                String tipo = usuario instanceof Administrador ? "Admin" : "Jogador";
                int jogos = 0;

                if (usuario instanceof Jogador) {
                    jogos = ((Jogador) usuario).getJogosParticipados();
                }

                sb.append(String.format("%-25s %-10s %-12d %-15s\n",
                    usuario.getNome().length() > 25 ? usuario.getNome().substring(0, 22) + "..." : usuario.getNome(),
                    tipo,
                    jogos,
                    "Recente" // Simplificado - em uma implementação real, teria timestamp
                ));
            }

            sb.append("\n🏆 CONQUISTAS MAIS OBTIDAS\n");
            sb.append("(Estatística simplificada - implementação completa requereria tracking detalhado)\n");
        }

        return sb.toString();
    }
}
