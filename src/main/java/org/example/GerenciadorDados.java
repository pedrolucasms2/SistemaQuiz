package org.example;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Gerenciador de dados que carrega informações de arquivos texto apenas na inicialização.
 * Não realiza operações diretas nos arquivos durante a execução do sistema.
 */
public class GerenciadorDados {
    private static final String DIRETORIO_DADOS = "dados/";
    private static final String ARQUIVO_USUARIOS = "usuarios.txt";
    private static final String ARQUIVO_CATEGORIAS = "categorias.txt";
    private static final String ARQUIVO_PERGUNTAS = "perguntas.txt";
    private static final String ARQUIVO_CONQUISTAS = "conquistas.txt";
    private static final SimpleDateFormat formatoData = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private SistemaQuiz sistema;

    public GerenciadorDados(SistemaQuiz sistema) {
        this.sistema = sistema;
        criarDiretorioSeNecessario();
    }

    /**
     * Carrega todos os dados dos arquivos na inicialização do sistema
     */
    public void carregarDadosIniciais() {
        try {
            carregarCategorias();
            carregarConquistas();
            carregarUsuarios();
            carregarPerguntas();
            System.out.println("✅ Dados iniciais carregados com sucesso!");
        } catch (Exception e) {
            System.err.println("⚠️ Erro ao carregar dados iniciais: " + e.getMessage());
            System.out.println("Sistema continuará com dados padrão.");
        }
    }

    /**
     * Salva o estado atual do sistema em arquivos (apenas ao encerrar)
     */
    public void salvarEstadoFinal() {
        try {
            salvarUsuarios();
            salvarEstatisticas();
            System.out.println("✅ Estado do sistema salvo com sucesso!");
        } catch (Exception e) {
            System.err.println("❌ Erro ao salvar estado do sistema: " + e.getMessage());
        }
    }

    private void criarDiretorioSeNecessario() {
        File diretorio = new File(DIRETORIO_DADOS);
        if (!diretorio.exists()) {
            diretorio.mkdirs();
        }
    }

    private void carregarCategorias() throws IOException {
        File arquivo = new File(DIRETORIO_DADOS + ARQUIVO_CATEGORIAS);
        if (!arquivo.exists()) {
            criarArquivoCategoriasDefault();
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(arquivo))) {
            String linha;
            while ((linha = reader.readLine()) != null) {
                if (linha.trim().isEmpty() || linha.startsWith("#")) continue;

                String[] partes = linha.split("\\|");
                if (partes.length >= 2) {
                    String nome = partes[0].trim();
                    String descricao = partes[1].trim();
                    Categoria categoria = new Categoria(nome, descricao, null);
                    sistema.adicionarCategoriaInterna(categoria);
                    System.out.println("📁 Categoria carregada: " + nome);
                }
            }
        }
    }

    private void carregarConquistas() throws IOException {
        File arquivo = new File(DIRETORIO_DADOS + ARQUIVO_CONQUISTAS);
        if (!arquivo.exists()) {
            criarArquivoConquistasDefault();
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(arquivo))) {
            String linha;
            while ((linha = reader.readLine()) != null) {
                if (linha.trim().isEmpty() || linha.startsWith("#")) continue;

                String[] partes = linha.split("\\|");
                if (partes.length >= 5) {
                    String nome = partes[0].trim();
                    String descricao = partes[1].trim();
                    Conquista.TipoConquista tipo = Conquista.TipoConquista.valueOf(partes[2].trim());
                    int meta = Integer.parseInt(partes[3].trim());
                    boolean repetivel = Boolean.parseBoolean(partes[4].trim());

                    sistema.adicionarConquistaInterna(
                        new Conquista(nome, descricao, tipo, meta, repetivel)
                    );
                }
            }
        }
    }

    private void carregarUsuarios() throws IOException {
        File arquivo = new File(DIRETORIO_DADOS + ARQUIVO_USUARIOS);
        if (!arquivo.exists()) {
            System.out.println("📄 Arquivo de usuários não encontrado, sistema começará sem usuários salvos");
            return; // Sem usuários salvos, sistema começará vazio
        }

        System.out.println("📄 Carregando usuários do arquivo: " + arquivo.getAbsolutePath());
        int usuariosCarregados = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(arquivo))) {
            String linha;
            while ((linha = reader.readLine()) != null) {
                if (linha.trim().isEmpty() || linha.startsWith("#")) continue;

                String[] partes = linha.split("\\|");
                if (partes.length >= 4) {
                    String tipo = partes[0].trim();
                    String nome = partes[1].trim();
                    String email = partes[2].trim();
                    String senha = partes[3].trim();

                    try {
                        if ("JOGADOR".equals(tipo)) {
                            Jogador jogador = new Jogador(nome, email, senha);

                            // Carregar estatísticas se disponíveis
                            if (partes.length >= 7) {
                                jogador.setPontuacaoTotal(Integer.parseInt(partes[4].trim()));
                                jogador.setNumeroVitorias(Integer.parseInt(partes[5].trim()));
                                jogador.setJogosParticipados(Integer.parseInt(partes[6].trim()));
                            }

                            sistema.adicionarUsuarioInterno(jogador);
                            usuariosCarregados++;
                            System.out.println("👤 Jogador carregado: " + nome + " (" + email + ")");
                        } else if ("ADMINISTRADOR".equals(tipo)) {
                            String nivel = partes.length > 4 ? partes[4].trim() : "STANDARD";
                            Administrador admin = new Administrador(nome, email, senha, nivel);
                            sistema.adicionarUsuarioInterno(admin);
                            usuariosCarregados++;
                            System.out.println("👑 Administrador carregado: " + nome + " (" + email + ")");
                        }
                    } catch (Exception e) {
                        System.err.println("❌ Erro ao carregar usuário: " + email + " - " + e.getMessage());
                    }
                }
            }
        }

        System.out.println("✅ Total de usuários carregados: " + usuariosCarregados);
    }

    private void carregarPerguntas() throws IOException {
        File arquivo = new File(DIRETORIO_DADOS + ARQUIVO_PERGUNTAS);
        if (!arquivo.exists()) {
            criarArquivoPerguntasDefault();
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(arquivo))) {
            String linha;
            Categoria categoriaAtual = null;

            while ((linha = reader.readLine()) != null) {
                if (linha.trim().isEmpty() || linha.startsWith("#")) continue;

                if (linha.startsWith("[") && linha.endsWith("]")) {
                    // Nova categoria
                    String nomeCategoria = linha.substring(1, linha.length() - 1);
                    categoriaAtual = sistema.getCategorias().stream()
                        .filter(c -> c.getNome().equals(nomeCategoria))
                        .findFirst()
                        .orElse(null);
                } else if (categoriaAtual != null) {
                    // Pergunta da categoria atual
                    String[] partes = linha.split("\\|");
                    if (partes.length >= 6) {
                        String textoPergunta = partes[0].trim();
                        String dificuldade = partes[1].trim();
                        String respostaCorreta = partes[2].trim();
                        String opcao1 = partes[3].trim();
                        String opcao2 = partes[4].trim();
                        String opcao3 = partes[5].trim();

                        // Criar array de alternativas
                        String[] alternativas = {respostaCorreta, opcao1, opcao2, opcao3};

                        // Embaralhar as alternativas e encontrar nova posição da resposta correta
                        List<String> opcoesList = Arrays.asList(alternativas);
                        Collections.shuffle(opcoesList);
                        int novaRespostaCorreta = opcoesList.indexOf(respostaCorreta);

                        // Converter de volta para array
                        String[] opcoesArray = opcoesList.toArray(new String[0]);

                        Pergunta pergunta = new Pergunta(
                            textoPergunta,
                            opcoesArray,
                            novaRespostaCorreta,
                            categoriaAtual,
                            Pergunta.Dificuldade.valueOf(dificuldade),
                            "", // dica vazia por padrão
                            null // criador nulo para perguntas carregadas de arquivo
                        );

                        categoriaAtual.adicionarPergunta(pergunta);
                    }
                }
            }
        }
    }

    private void salvarUsuarios() throws IOException {
        File arquivo = new File(DIRETORIO_DADOS + ARQUIVO_USUARIOS);
        try (PrintWriter writer = new PrintWriter(new FileWriter(arquivo))) {
            writer.println("# Arquivo de usuários do Sistema Quiz");
            writer.println("# Formato: TIPO|NOME|EMAIL|SENHA|DADOS_EXTRAS");
            writer.println();

            for (Usuario usuario : sistema.getUsuarios()) {
                if (usuario instanceof Jogador) {
                    Jogador jogador = (Jogador) usuario;
                    writer.printf("JOGADOR|%s|%s|%s|%d|%d|%d%n",
                        jogador.getNome(),
                        jogador.getEmail(),
                        jogador.getSenha(),
                        jogador.getPontuacaoTotal(),
                        jogador.getNumeroVitorias(),
                        jogador.getJogosParticipados()
                    );
                } else if (usuario instanceof Administrador) {
                    Administrador admin = (Administrador) usuario;
                    writer.printf("ADMINISTRADOR|%s|%s|%s|%s%n",
                        admin.getNome(),
                        admin.getEmail(),
                        admin.getSenha(),
                        admin.getNivel()
                    );
                }
            }
        }
    }

    private void salvarEstatisticas() throws IOException {
        File arquivo = new File(DIRETORIO_DADOS + "estatisticas.txt");
        try (PrintWriter writer = new PrintWriter(new FileWriter(arquivo))) {
            writer.println("# Estatísticas do Sistema Quiz");
            writer.println("# Gerado em: " + formatoData.format(new Date()));
            writer.println();

            writer.println("TOTAL_USUARIOS=" + sistema.getUsuarios().size());
            writer.println("TOTAL_JOGOS=" + sistema.getJogos().size());
            writer.println("TOTAL_CATEGORIAS=" + sistema.getCategorias().size());

            // Estatísticas dos jogadores
            writer.println();
            writer.println("# Estatísticas dos Jogadores");
            for (Usuario usuario : sistema.getUsuarios()) {
                if (usuario instanceof Jogador) {
                    Jogador jogador = (Jogador) usuario;
                    writer.printf("JOGADOR_STATS|%s|%d|%d|%d%n",
                        jogador.getEmail(),
                        jogador.getPontuacaoTotal(),
                        jogador.getNumeroVitorias(),
                        jogador.getJogosParticipados()
                    );
                }
            }
        }
    }

    // Métodos para criar arquivos padrão
    private void criarArquivoCategoriasDefault() throws IOException {
        File arquivo = new File(DIRETORIO_DADOS + ARQUIVO_CATEGORIAS);
        try (PrintWriter writer = new PrintWriter(new FileWriter(arquivo))) {
            writer.println("# Categorias do Sistema Quiz");
            writer.println("# Formato: NOME|DESCRIÇÃO");
            writer.println();
            writer.println("História|Perguntas sobre eventos históricos mundiais");
            writer.println("Ciências|Perguntas sobre biologia, química, física e matemática");
            writer.println("Esportes|Perguntas sobre modalidades esportivas e competições");
            writer.println("Geografia|Perguntas sobre países, capitais, rios e montanhas");
            writer.println("Literatura|Perguntas sobre livros, autores e obras clássicas");
            writer.println("Tecnologia|Perguntas sobre informática, programação e inovações");
        }
    }

    private void criarArquivoConquistasDefault() throws IOException {
        File arquivo = new File(DIRETORIO_DADOS + ARQUIVO_CONQUISTAS);
        try (PrintWriter writer = new PrintWriter(new FileWriter(arquivo))) {
            writer.println("# Conquistas do Sistema Quiz");
            writer.println("# Formato: NOME|DESCRIÇÃO|TIPO|META|REPETÍVEL");
            writer.println();
            writer.println("Primeira Vitória|Ganhe seu primeiro jogo|VITORIA|1|false");
            writer.println("Veterano|Participe de 100 jogos|PERFORMANCE|100|true");
            writer.println("Especialista|Acerte 500 perguntas seguidas|PERFORMANCE|500|true");
            writer.println("Mestre das Categorias|Domine todas as categorias|CATEGORIA|6|false");
        }
    }

    private void criarArquivoPerguntasDefault() throws IOException {
        File arquivo = new File(DIRETORIO_DADOS + ARQUIVO_PERGUNTAS);
        try (PrintWriter writer = new PrintWriter(new FileWriter(arquivo))) {
            writer.println("# Perguntas do Sistema Quiz");
            writer.println("# Formato: [CATEGORIA] seguido de PERGUNTA|DIFICULDADE|RESPOSTA_CORRETA|OPCAO1|OPCAO2|OPCAO3");
            writer.println();

            // História
            writer.println("[História]");
            writer.println("Quem foi o primeiro imperador romano?|FACIL|Augusto|Júlio César|Nero|Trajano");
            writer.println("Em que ano começou a Segunda Guerra Mundial?|MEDIO|1939|1940|1938|1941");
            writer.println("Qual civilização construiu Machu Picchu?|MEDIO|Incas|Maias|Astecas|Olmecas");

            // Ciências
            writer.println();
            writer.println("[Ciências]");
            writer.println("Qual é o símbolo químico do ouro?|FACIL|Au|Ag|Fe|Cu");
            writer.println("Quantos ossos tem o corpo humano adulto?|MEDIO|206|208|204|210");
            writer.println("Qual é a velocidade da luz no vácuo?|DIFICIL|300.000 km/s|150.000 km/s|450.000 km/s|600.000 km/s");

            // Geografia
            writer.println();
            writer.println("[Geografia]");
            writer.println("Qual é a capital da Austrália?|MEDIO|Canberra|Sydney|Melbourne|Perth");
            writer.println("Qual é o maior oceano do mundo?|FACIL|Pacífico|Atlântico|Índico|Ártico");
            writer.println("Em que continente fica o deserto do Saara?|FACIL|África|Ásia|América|Oceania");
        }
    }
}
