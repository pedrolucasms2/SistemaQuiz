package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import java.util.stream.Collectors;

public class Administrador extends Usuario {
    // Atributos específicos
    private String nivel;
    private List<Categoria> categoriasGerenciadas;
    private int totalJogosCriados;
    private int totalPerguntasCriadas;
    private Date ultimoAcesso;

    // Construtor
    public Administrador(String nome, String email, String senha, String nivel) {
        super(nome, email, senha);
        this.nivel = nivel != null ? nivel.toUpperCase() : "MODERADOR";
        this.categoriasGerenciadas = new ArrayList<>();
        this.totalJogosCriados = 0;
        this.totalPerguntasCriadas = 0;
        this.ultimoAcesso = new Date();
    }

    // Construtor especial para carregamento de arquivo
    public Administrador(String nome, String email, String senhaJaHashada, String nivel, boolean carregandoDoArquivo) {
        super(nome, email, senhaJaHashada, carregandoDoArquivo);
        this.nivel = nivel != null ? nivel.toUpperCase() : "MODERADOR";
        this.categoriasGerenciadas = new ArrayList<>();
        this.totalJogosCriados = 0;
        this.totalPerguntasCriadas = 0;
        this.ultimoAcesso = new Date();
    }

    // Implementação de métodos abstratos
    @Override
    public String getTipoUsuario() {
        return "ADMINISTRADOR";
    }

    @Override
    public void exibirMenu() {
        System.out.println("=== MENU ADMINISTRADOR ===");
        System.out.println("1. Criar Jogo");
        System.out.println("2. Cadastrar Pergunta");
        System.out.println("3. Gerenciar Categorias");
        System.out.println("4. Relatórios");
        System.out.println("5. Gerenciar Usuários");
        System.out.println("0. Sair");
    }

    // Métodos específicos do administrador
    public Jogo criarJogo(String nome, List<Categoria> categorias, String modalidade,
                          int numeroRodadas, int tempoLimitePergunta) {
        // Validar parâmetros
        List<String> erros = ValidadorInterface.validarCriacaoJogo(
                nome, categorias, numeroRodadas, tempoLimitePergunta);

        if (!erros.isEmpty()) {
            throw new IllegalArgumentException("Dados inválidos: " + String.join(", ", erros));
        }

        // Criar modalidade
        IModalidadeJogo mod;
        switch (modalidade.toUpperCase()) {
            case "INDIVIDUAL":
                mod = new JogoIndividual();
                break;
            case "EQUIPE":
                mod = new JogoEquipe();
                break;
            case "ELIMINATORIA":
                mod = new JogoEliminatoria();
                break;
            default:
                throw new IllegalArgumentException("Modalidade inválida: " + modalidade);
        }

        Jogo novoJogo = new Jogo(nome, categorias, mod, numeroRodadas, tempoLimitePergunta, this);
        totalJogosCriados++;
        atualizarUltimoAcesso();

        return novoJogo;
    }

    public Pergunta cadastrarPergunta(String enunciado, String[] alternativas,
                                      int respostaCorreta, Categoria categoria,
                                      String dificuldade, String dica) {
        // Criar pergunta
        Pergunta.Dificuldade diff;
        try {
            diff = Pergunta.Dificuldade.valueOf(dificuldade.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Dificuldade inválida: " + dificuldade);
        }

        Pergunta novaPergunta = new Pergunta(enunciado, alternativas, respostaCorreta,
                categoria, diff, dica, this);

        // Validar pergunta
        List<String> erros = ValidadorInterface.validarCadastroPergunta(novaPergunta);
        if (!erros.isEmpty()) {
            throw new IllegalArgumentException("Pergunta inválida: " + String.join(", ", erros));
        }

        categoria.adicionarPergunta(novaPergunta);
        totalPerguntasCriadas++;
        atualizarUltimoAcesso();

        return novaPergunta;
    }

    public void editarPergunta(int idPergunta, Pergunta novaPergunta) {
        // Validar nova pergunta
        List<String> erros = ValidadorInterface.validarCadastroPergunta(novaPergunta);
        if (!erros.isEmpty()) {
            throw new IllegalArgumentException("Dados inválidos: " + String.join(", ", erros));
        }

        // Buscar pergunta original e atualizar
        // Implementação dependeria de como as perguntas são armazenadas
        atualizarUltimoAcesso();
    }

    public void desativarPergunta(int idPergunta) {
        // Buscar pergunta e desativar
        // Implementação dependeria do sistema de persistência
        atualizarUltimoAcesso();
    }

    public List<Pergunta> consultarPerguntas(Categoria categoria, String dificuldade) {
        if (categoria == null) {
            return new ArrayList<Pergunta>();
        }

        if (dificuldade == null || dificuldade.trim().isEmpty()) {
            return new ArrayList<Pergunta>(categoria.getPerguntas());
        }

        return categoria.getPerguntasPorDificuldade(dificuldade);
    }

    public Categoria criarCategoria(String nome, String descricao, Categoria categoriaPai) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome da categoria é obrigatório");
        }

        Categoria novaCategoria = new Categoria(nome, descricao, categoriaPai);
        categoriasGerenciadas.add(novaCategoria);
        atualizarUltimoAcesso();

        return novaCategoria;
    }

    public RelatorioAdmin gerarRelatorio(String tipoRelatorio, Date inicio, Date fim) {
        atualizarUltimoAcesso();

        switch (tipoRelatorio.toUpperCase()) {
            case "JOGOS":
                return gerarRelatorioJogos(inicio, fim);
            case "USUARIOS":
                return gerarRelatorioUsuarios(inicio, fim);
            case "PERGUNTAS":
                return gerarRelatorioPerguntas(inicio, fim);
            default:
                throw new IllegalArgumentException("Tipo de relatório inválido: " + tipoRelatorio);
        }
    }

    // Métodos privados de apoio
    private RelatorioAdmin gerarRelatorioJogos(Date inicio, Date fim) {
        // Implementação específica do relatório de jogos
        return new RelatorioAdmin("Relatório de Jogos", inicio, fim, this);
    }

    private RelatorioAdmin gerarRelatorioUsuarios(Date inicio, Date fim) {
        // Implementação específica do relatório de usuários
        return new RelatorioAdmin("Relatório de Usuários", inicio, fim, this);
    }

    private RelatorioAdmin gerarRelatorioPerguntas(Date inicio, Date fim) {
        // Implementação específica do relatório de perguntas
        return new RelatorioAdmin("Relatório de Perguntas", inicio, fim, this);
    }

    private void atualizarUltimoAcesso() {
        this.ultimoAcesso = new Date();
    }

    // Getters/Setters específicos
    public String getNivel() { return nivel; }
    public void setNivel(String nivel) {
        if (nivel != null && (nivel.equals("MASTER") || nivel.equals("MODERADOR"))) {
            this.nivel = nivel;
        }
    }

    public List<Categoria> getCategoriasGerenciadas() {
        return new ArrayList<>(categoriasGerenciadas);
    }

    public int getTotalJogosCriados() { return totalJogosCriados; }
    public int getTotalPerguntasCriadas() { return totalPerguntasCriadas; }

    public Date getUltimoAcesso() { return new Date(ultimoAcesso.getTime()); }

    public boolean isMaster() { return "MASTER".equals(nivel); }

    @Override
    public String getResumoEstatisticas() {
        return String.format("Admin %s (%s) - %d jogos, %d perguntas criadas",
                getNome(), nivel, totalJogosCriados, totalPerguntasCriadas);
    }

    @Override
    public boolean podeAcessarFuncionalidade(String funcionalidade) {
        if (!super.podeAcessarFuncionalidade(funcionalidade)) {
            return false;
        }

        // Verificações específicas de administrador
        switch (funcionalidade.toUpperCase()) {
            case "CRIAR_JOGO":
            case "CRIAR_PERGUNTA":
            case "CRIAR_CATEGORIA":
                return true;
            case "GERENCIAR_USUARIOS":
            case "RELATORIOS_AVANCADOS":
                return isMaster();
            default:
                return true;
        }
    }
}
