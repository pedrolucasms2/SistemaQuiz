package org.example;

import java.util.*;
import java.util.stream.Collectors;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import org.example.gui.QuizGameFrame;


public class SistemaQuiz extends QuizObservable {
    private static SistemaQuiz instance;

    private List<Usuario> usuarios;
    private List<Jogo> jogos;
    private List<Categoria> categorias;
    private List<Conquista> conquistasDisponiveis;
    private GerenciadorRanking gerenciadorRanking;
    private GerenciadorConquistas gerenciadorConquistas;
    private GerenciadorDados gerenciadorDados;
    private Usuario usuarioLogado;

    private SistemaQuiz() {
        usuarios = new ArrayList<>();
        jogos = new ArrayList<>();
        categorias = new ArrayList<>();
        conquistasDisponiveis = new ArrayList<>();
        gerenciadorRanking = new GerenciadorRanking();
        gerenciadorConquistas = new GerenciadorConquistas();
        gerenciadorDados = new GerenciadorDados(this);
        inicializarSistema();
    }

    public static SistemaQuiz getInstance() {
        if (instance == null) {
            instance = new SistemaQuiz();
        }
        return instance;
    }

    public static void main(String[] args) {
        System.out.println("=== INICIANDO SISTEMA QUIZMASTER ===");
        try {
            // Inicializar o sistema
            SistemaQuiz sistema = SistemaQuiz.getInstance();
            System.out.println("Sistema inicializado com sucesso!");
            System.out.println("Categorias criadas: " + sistema.getCategorias().size());
            System.out.println("Conquistas disponíveis: " + sistema.getConquistasDisponiveis().size());
            System.out.println("Usuários cadastrados: " + sistema.getUsuarios().size());

            // Exemplo de teste (opcional)
            testarSistema(sistema);

            // NOVA ADIÇÃO: Inicializar a interface gráfica
            SwingUtilities.invokeLater(() -> {
                try {
                    // Configurar Look and Feel do sistema - MÉTODO CORRETO
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (Exception e) {
                    System.err.println("Não foi possível definir Look and Feel: " + e.getMessage());
                }

                // Criar e exibir a janela principal
                QuizGameFrame frame = new QuizGameFrame();
                frame.setVisible(true);

                System.out.println("Interface gráfica inicializada com sucesso!");
            });

        } catch (Exception e) {
            System.err.println("❌ Erro ao inicializar sistema: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("=== SISTEMA QUIZMASTER PRONTO PARA USO ===");
    }


    private static void testarSistema(SistemaQuiz sistema) {
        try {
            // Teste de cadastro de jogador
            sistema.cadastrarJogador("Jogador Teste", "teste@email.com", "senha123");
            System.out.println("Teste de cadastro: OK");

            // Teste de login
            boolean loginOk = sistema.autenticar("admin@quizmaster.com", "admin123");
            System.out.println("Teste de login admin: " + (loginOk ? "OK" : "FALHOU"));

            if (loginOk) {
                sistema.logout();
                System.out.println("Teste de logout: OK");
            }

        } catch (EmailJaExisteException e) {
            System.out.println("Email já existe (normal em re-execuções)");
        } catch (Exception e) {
            System.err.println("Erro no teste: " + e.getMessage());
        }
    }

    public synchronized boolean autenticar(String email, String senha) {
        Usuario usuario = buscarUsuarioPorEmail(email);
        if (usuario != null && usuario.autenticar(email, senha)) {
            usuarioLogado = usuario;
            // Notificar observadores
            setChanged();
            notifyObservers(new EventoSistema(EventoSistema.TipoEvento.LOGIN_REALIZADO, usuario));

            return true;
        }
        return false;
    }

    public synchronized void logout() {
        Usuario usuarioAnterior = usuarioLogado;
        usuarioLogado = null;
        // Notificar observadores
        setChanged();
        notifyObservers(new EventoSistema(EventoSistema.TipoEvento.LOGOUT_REALIZADO, usuarioAnterior));
    }

    public synchronized boolean cadastrarJogador(String nome, String email, String senha) throws EmailJaExisteException {
        if (buscarUsuarioPorEmail(email) != null) {
            throw new EmailJaExisteException("Email já cadastrado no sistema");
        }

        Jogador novoJogador = new Jogador(nome, email, senha);
        usuarios.add(novoJogador);
        setChanged();
        notifyObservers(new EventoSistema(EventoSistema.TipoEvento.JOGADOR_CADASTRADO, novoJogador));
        return true;
    }

    public synchronized boolean cadastrarAdministrador(String nome, String email, String senha, String nivel) throws EmailJaExisteException {
        if (buscarUsuarioPorEmail(email) != null) {
            throw new EmailJaExisteException("Email já cadastrado no sistema");
        }

        Administrador novoAdmin = new Administrador(nome, email, senha, nivel);
        usuarios.add(novoAdmin);
        setChanged();
        notifyObservers(new EventoSistema(EventoSistema.TipoEvento.ADMIN_CADASTRADO, novoAdmin));
        return true;
    }

    public Usuario buscarUsuarioPorEmail(String email) {
        return usuarios.stream()
                .filter(u -> u.getEmail().equals(email))
                .findFirst()
                .orElse(null);
    }

    public Jogo buscarJogoPorId(int id) {
        return jogos.stream()
                .filter(j -> j.getId() == id)
                .findFirst()
                .orElse(null);
    }

    public List<JogoResumo> listarJogosDisponiveisResumo() {
        return jogos.stream()
                .filter(j -> j.getStatus() == Jogo.StatusJogo.AGUARDANDO)
                .map(j -> new JogoResumo(j))
                .collect(Collectors.toList());
    }

    public void processarResposta(Jogo jogo, Jogador jogador, Resposta resposta) {

        jogo.processarResposta(jogador, resposta);

        // Atualizar sistemas
        gerenciadorRanking.atualizarRankings();
        List<Conquista> novasConquistas = gerenciadorConquistas.verificarNovasConquistas(jogador);

        // Notificar observadores
        setChanged();
        notifyObservers(new EventoSistema(EventoSistema.TipoEvento.RESPOSTA_PROCESSADA, resposta));

        // Notificar sobre novas conquistas
        for (Conquista conquista : novasConquistas) {
            setChanged();
            notifyObservers(new EventoSistema(EventoSistema.TipoEvento.NOVA_CONQUISTA, conquista));
        }
    }

    public synchronized Jogo criarJogo(String nome, List<Categoria> categorias,
                                       IModalidadeJogo modalidade, int numeroRodadas,
                                       int tempoLimitePergunta) {
        if (!(usuarioLogado instanceof Administrador)) {
            throw new IllegalStateException("Apenas administradores podem criar jogos");
        }

        Administrador admin = (Administrador) usuarioLogado;
        Jogo novoJogo = new Jogo(nome, categorias, modalidade, numeroRodadas, tempoLimitePergunta, admin);

        jogos.add(novoJogo);

        // Notificar observadores
        setChanged();
        notifyObservers(new EventoSistema(EventoSistema.TipoEvento.JOGO_CRIADO, novoJogo));

        return novoJogo;
    }

    // Método para inicializar dados padrão do sistema
    private void inicializarSistema() {
        // Primeiro carregar dados dos arquivos
        gerenciadorDados.carregarDadosIniciais();

        // Só criar dados padrão se não foram carregados dos arquivos
        if (categorias.isEmpty()) {
            System.out.println("📁 Criando categorias padrão...");
            criarCategoriasIniciais();
        } else {
            System.out.println("📁 Categorias carregadas dos arquivos: " + categorias.size());
        }

        if (conquistasDisponiveis.isEmpty()) {
            System.out.println("🏆 Criando conquistas padrão...");
            criarConquistasIniciais();
        } else {
            System.out.println("🏆 Conquistas carregadas dos arquivos: " + conquistasDisponiveis.size());
        }

        // Verificar se admin padrão existe (só criar se não existir)
        if (buscarUsuarioPorEmail("admin@quizmaster.com") == null) {
            try {
                System.out.println("👑 Criando administrador padrão...");
                cadastrarAdministrador("Admin Sistema", "admin@quizmaster.com", "admin123", "MASTER");
            } catch (EmailJaExisteException e) {
                System.out.println("⚠️  Administrador padrão já existe");
            }
        } else {
            System.out.println("👑 Administrador padrão encontrado nos dados carregados");
        }

        // Adicionar hook para salvar dados ao encerrar o sistema
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("💾 Salvando estado do sistema...");
            gerenciadorDados.salvarEstadoFinal();
        }));
    }

    private void criarCategoriasIniciais() {
        categorias.add(new Categoria("História", "Perguntas sobre eventos históricos", null));
        categorias.add(new Categoria("Ciências", "Perguntas sobre biologia, química, física", null));
        categorias.add(new Categoria("Esportes", "Perguntas sobre modalidades esportivas", null));
        categorias.add(new Categoria("Geografia", "Perguntas sobre países, capitais, rios", null));

        // Adicionar perguntas padrão para teste
        criarPerguntasPadrao();
    }

    private void criarPerguntasPadrao() {
        try {
            // Criar admin temporário para as perguntas padrão
            Administrador adminPadrao = new Administrador("Sistema", "sistema@quiz.com", "123", "MASTER");

            // Encontrar categorias
            Categoria historia = categorias.stream().filter(c -> c.getNome().equals("História")).findFirst().orElse(null);
            Categoria ciencias = categorias.stream().filter(c -> c.getNome().equals("Ciências")).findFirst().orElse(null);
            Categoria geografia = categorias.stream().filter(c -> c.getNome().equals("Geografia")).findFirst().orElse(null);
            Categoria esportes = categorias.stream().filter(c -> c.getNome().equals("Esportes")).findFirst().orElse(null);

            if (historia != null) {
                // Perguntas de História
                String[] alt1 = {"Augusto", "Júlio César", "Nero", "Trajano"};
                Pergunta p1 = new Pergunta("Quem foi o primeiro imperador romano?", alt1, 0, historia, Pergunta.Dificuldade.FACIL, "", adminPadrao);
                historia.adicionarPergunta(p1);

                String[] alt2 = {"1939", "1940", "1938", "1941"};
                Pergunta p2 = new Pergunta("Em que ano começou a Segunda Guerra Mundial?", alt2, 0, historia, Pergunta.Dificuldade.MEDIO, "", adminPadrao);
                historia.adicionarPergunta(p2);

                String[] alt3 = {"Incas", "Maias", "Astecas", "Olmecas"};
                Pergunta p3 = new Pergunta("Qual civilização construiu Machu Picchu?", alt3, 0, historia, Pergunta.Dificuldade.MEDIO, "", adminPadrao);
                historia.adicionarPergunta(p3);
            }

            if (ciencias != null) {
                // Perguntas de Ciências
                String[] alt4 = {"Au", "Ag", "Fe", "Cu"};
                Pergunta p4 = new Pergunta("Qual é o símbolo químico do ouro?", alt4, 0, ciencias, Pergunta.Dificuldade.FACIL, "", adminPadrao);
                ciencias.adicionarPergunta(p4);

                String[] alt5 = {"206", "208", "204", "210"};
                Pergunta p5 = new Pergunta("Quantos ossos tem o corpo humano adulto?", alt5, 0, ciencias, Pergunta.Dificuldade.MEDIO, "", adminPadrao);
                ciencias.adicionarPergunta(p5);
            }

            if (geografia != null) {
                // Perguntas de Geografia
                String[] alt6 = {"Canberra", "Sydney", "Melbourne", "Perth"};
                Pergunta p6 = new Pergunta("Qual é a capital da Austrália?", alt6, 0, geografia, Pergunta.Dificuldade.MEDIO, "", adminPadrao);
                geografia.adicionarPergunta(p6);

                String[] alt7 = {"Pacífico", "Atlântico", "Índico", "Ártico"};
                Pergunta p7 = new Pergunta("Qual é o maior oceano do mundo?", alt7, 0, geografia, Pergunta.Dificuldade.FACIL, "", adminPadrao);
                geografia.adicionarPergunta(p7);
            }

            if (esportes != null) {
                // Perguntas de Esportes
                String[] alt8 = {"Brasil", "Alemanha", "Argentina", "França"};
                Pergunta p8 = new Pergunta("Qual país ganhou a Copa do Mundo de 2002?", alt8, 0, esportes, Pergunta.Dificuldade.FACIL, "", adminPadrao);
                esportes.adicionarPergunta(p8);

                String[] alt9 = {"5", "6", "7", "4"};
                Pergunta p9 = new Pergunta("Quantos jogadores tem uma equipe de basquete em quadra?", alt9, 0, esportes, Pergunta.Dificuldade.FACIL, "", adminPadrao);
                esportes.adicionarPergunta(p9);
            }

            System.out.println("✅ Perguntas padrão criadas com sucesso!");

        } catch (Exception e) {
            System.err.println("❌ Erro ao criar perguntas padrão: " + e.getMessage());
        }
    }

    private void criarConquistasIniciais() {
        conquistasDisponiveis.add(new Conquista("Primeira Vitória", "Ganhe seu primeiro jogo",
                Conquista.TipoConquista.VITORIA, 1, false));
        conquistasDisponiveis.add(new Conquista("Veterano", "Participe de 100 jogos",
                Conquista.TipoConquista.PERFORMANCE, 100, true));
    }

    public Usuario getUsuarioLogado() {
        return usuarioLogado;
    }

    public List<Usuario> getUsuarios() {
        return new ArrayList<>(usuarios);
    }

    public List<Categoria> getCategorias() {
        return new ArrayList<>(categorias);
    }

    public List<Jogo> getJogos() {
        return new ArrayList<>(jogos);
    }

    public List<Conquista> getConquistasDisponiveis() {
        return new ArrayList<>(conquistasDisponiveis);
    }

    public GerenciadorRanking getGerenciadorRanking() {
        return gerenciadorRanking;
    }

    public GerenciadorConquistas getGerenciadorConquistas() {
        return gerenciadorConquistas;
    }
}
