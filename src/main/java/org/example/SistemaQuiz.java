package org.example;

import java.util.*;
import java.util.stream.Collectors;

public class SistemaQuiz extends QuizObservable {
    private static SistemaQuiz instance;

    private List<Usuario> usuarios;
    private List<Jogo> jogos;
    private List<Pergunta> perguntas;
    private Usuario usuarioLogado;

    private SistemaQuiz() {
        usuarios = new ArrayList<>();
        jogos = new ArrayList<>();
        perguntas = new ArrayList<>();
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
            SistemaQuiz sistema = SistemaQuiz.getInstance();
            System.out.println("Sistema inicializado com sucesso!");
            System.out.println("Usuários cadastrados: " + sistema.getUsuarios().size());
            testarSistema(sistema);
        } catch (Exception e) {
            System.err.println("❌ Erro ao inicializar sistema: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println("=== SISTEMA QUIZMASTER PRONTO PARA USO ===");
    }

    private static void testarSistema(SistemaQuiz sistema) {
        try {
            sistema.cadastrarJogador("Jogador Teste", "teste@email.com", "senha123");
            System.out.println("Teste de cadastro: OK");
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
            setChanged();
            notifyObservers(new EventoSistema(EventoSistema.TipoEvento.LOGIN_REALIZADO, usuario));
            return true;
        }
        return false;
    }

    public synchronized void logout() {
        Usuario usuarioAnterior = usuarioLogado;
        usuarioLogado = null;
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
        setChanged();
        notifyObservers(new EventoSistema(EventoSistema.TipoEvento.RESPOSTA_PROCESSADA, resposta));
    }

    public synchronized Jogo criarJogo(String nome, int numeroRodadas,
                                       int tempoLimitePergunta) {
        if (!(usuarioLogado instanceof Administrador)) {
            throw new IllegalStateException("Apenas administradores podem criar jogos");
        }
        Administrador admin = (Administrador) usuarioLogado;
        Jogo novoJogo = new Jogo(nome, numeroRodadas, tempoLimitePergunta, admin);
        jogos.add(novoJogo);
        setChanged();
        notifyObservers(new EventoSistema(EventoSistema.TipoEvento.JOGO_CRIADO, novoJogo));
        return novoJogo;
    }

    private void inicializarSistema() {
        criarPerguntasIniciais();
        try {
            cadastrarAdministrador("Admin Sistema", "admin@quizmaster.com", "admin123", "MASTER");
        } catch (EmailJaExisteException e) {
            System.out.println("⚠️  Administrador padrão já existe");
        }
    }

    private void criarPerguntasIniciais() {
        perguntas.add(new Pergunta("Qual a capital da França?", new String[]{"Berlim", "Paris", "Madri", "Roma"}, 1, Pergunta.Dificuldade.FACIL, null, null));
        perguntas.add(new Pergunta("Qual o maior planeta do Sistema Solar?", new String[]{"Terra", "Marte", "Júpiter", "Vênus"}, 2, Pergunta.Dificuldade.MEDIO, null, null));
    }

    public Usuario getUsuarioLogado() {
        return usuarioLogado;
    }
    public List<Usuario> getUsuarios() {
        return new ArrayList<>(usuarios);
    }
    public List<Pergunta> getPerguntas() {
        return new ArrayList<>(perguntas);
    }
    public List<Jogo> getJogos() {
        return new ArrayList<>(jogos);
    }
}