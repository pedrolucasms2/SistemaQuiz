package org.example;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

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
    private static final String ARQUIVO_JOGOS = "jogos.txt";
    private static final String ARQUIVO_DESIGNACOES = "designacoes.txt";
    private static final String DIRETORIO_JOGOS = "jogos/";
    List<String> emailsPermitidos = new ArrayList<>();

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
            carregarJogos();
            carregarDesignacoes();
            System.out.println("Dados iniciais carregados com sucesso!");
        } catch (Exception e) {
            System.err.println("Erro ao carregar dados iniciais: " + e.getMessage());
            System.out.println("Sistema continuará com dados padrão.");
        }
    }



    /**
     * Salva o estado atual do sistema em arquivos (apenas ao encerrar)
     */
    public void salvarEstadoFinal() {
        try {
            salvarUsuarios();
            salvarJogos();
            salvarDesignacoes();
            System.out.println("Estado do sistema salvo com sucesso!");
        } catch (Exception e) {
            System.err.println("Erro ao salvar estado do sistema: " + e.getMessage());
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
                    System.out.println(" Categoria carregada: " + nome);
                    System.out.println("Categoria carregada: " + nome);
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
            System.out.println(" Arquivo de usuários não encontrado, sistema começará sem usuários salvos");
            return; // Sem usuários salvos, sistema começará vazio
        }

        System.out.println(" Carregando usuários do arquivo: " + arquivo.getAbsolutePath());
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
                            System.out.println(" Jogador carregado: " + nome + " (" + email + ")");
                        } else if ("ADMINISTRADOR".equals(tipo)) {
                            String nivel = partes.length > 4 ? partes[4].trim() : "STANDARD";
                            Administrador admin = new Administrador(nome, email, senha, nivel);
                            sistema.adicionarUsuarioInterno(admin);
                            usuariosCarregados++;
                            System.out.println(" Administrador carregado: " + nome + " (" + email + ")");
                        }
                    } catch (Exception e) {
                        System.err.println(" Erro ao carregar usuário: " + email + " - " + e.getMessage());
                    }
                }
            }
        }

        System.out.println(" Total de usuários carregados: " + usuariosCarregados);
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

    private void carregarJogos() {
        try {
            File diretorioJogos = new File(DIRETORIO_DADOS + "jogos/");
            if (!diretorioJogos.exists()) {
                System.out.println(" Diretório de jogos não encontrado, nenhum jogo salvo para carregar");
                return;
            }

            File[] arquivosJogos = diretorioJogos.listFiles((dir, name) -> name.endsWith(".txt"));
            if (arquivosJogos == null || arquivosJogos.length == 0) {
                System.out.println(" Nenhum arquivo de jogo encontrado");
                return;
            }

            int jogosCarregados = 0;
            for (File arquivoJogo : arquivosJogos) {
                try {
                    Jogo jogo = carregarJogoDeArquivo(arquivoJogo);
                    if (jogo != null) {
                        sistema.adicionarJogoInterno(jogo);
                        jogosCarregados++;
                        System.out.println(" Jogo carregado: " + jogo.getNome());
                    }
                } catch (Exception e) {
                    System.err.println(" Erro ao carregar jogo " + arquivoJogo.getName() + ": " + e.getMessage());
                }
            }

            System.out.println(" Total de jogos carregados: " + jogosCarregados);
        } catch (Exception e) {
            System.err.println(" Erro ao carregar jogos: " + e.getMessage());
        }
    }

    private Jogo carregarJogoDeArquivo(File arquivo) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(arquivo))) {
            String linha;

            // Variáveis para reconstruir o jogo
            String nomeJogo = null;
            String nomeCriador = null;
            List<String> emailsParticipantes = new ArrayList<>();
            List<Pergunta> perguntasJogo = new ArrayList<>();

            // Ler arquivo linha por linha
            while ((linha = reader.readLine()) != null) {
                linha = linha.trim();

                if (linha.startsWith("# Jogo: ")) {
                    nomeJogo = linha.substring("# Jogo: ".length());
                }
                else if (linha.startsWith("# Criador: ")) {
                    nomeCriador = linha.substring("# Criador: ".length());
                }
                else if (linha.startsWith("- ") && linha.contains("@")) {
                    // Extrair email do participante
                    int inicioEmail = linha.indexOf('(');
                    int fimEmail = linha.indexOf(')');
                    if (inicioEmail != -1 && fimEmail != -1) {
                        String email = linha.substring(inicioEmail + 1, fimEmail);
                        emailsParticipantes.add(email);
                    }
                }
                else if (linha.startsWith("PERMITIDOS:")) { // << NOVA CONDIÇÃO
                    String emailsStr = linha.substring("PERMITIDOS:".length()).trim();
                    if (!emailsStr.isEmpty()) {
                        String[] emailsArray = emailsStr.split(",");
                        emailsPermitidos.addAll(Arrays.asList(emailsArray));
                    }
                }
                else if (linha.startsWith("Pergunta: ")) {
                    String textoPergunta = linha.substring("Pergunta: ".length());

                    // Ler próximas linhas para dificuldade e categoria
                    String linhaDificuldade = reader.readLine();
                    String linhaCategoria = reader.readLine();

                    if (linhaDificuldade != null && linhaCategoria != null) {
                        String dificuldade = linhaDificuldade.substring("Dificuldade: ".length());
                        String nomeCategoria = linhaCategoria.substring("Categoria: ".length());

                        // Encontrar a pergunta nas categorias carregadas
                        Pergunta perguntaEncontrada = encontrarPergunta(textoPergunta, nomeCategoria);
                        if (perguntaEncontrada != null) {
                            perguntasJogo.add(perguntaEncontrada);
                        }
                    }
                }
            }

            // Reconstruir o jogo se temos dados suficientes
            if (nomeJogo != null && nomeCriador != null) {
                Jogo jogoReconstruido = reconstruirJogo(nomeJogo, nomeCriador, emailsParticipantes, perguntasJogo);
                if (jogoReconstruido != null) {
                    // Define a lista de usuários permitidos no jogo reconstruído
                    jogoReconstruido.setUsuariosPermitidos(emailsPermitidos); // << DEFINIR A LISTA
                }
                return jogoReconstruido;
            }
            return null;
        }
    }

    private Pergunta encontrarPergunta(String textoPergunta, String nomeCategoria) {
        for (Categoria categoria : sistema.getCategorias()) {
            if (categoria.getNome().equals(nomeCategoria)) {
                for (Pergunta pergunta : categoria.getPerguntas()) {
                    if (pergunta.getTexto().equals(textoPergunta)) {
                        return pergunta;
                    }
                }
            }
        }
        return null;
    }

    private Jogo reconstruirJogo(String nomeJogo, String nomeCriador,
                                 List<String> emailsParticipantes, List<Pergunta> perguntasJogo) {
        // Encontrar o administrador criador
        Administrador criador = null;

        for (Usuario usuario : sistema.getUsuarios()) {
            if (usuario instanceof Administrador) {
                if (usuario.getNome().equals(nomeCriador)) {
                    criador = (Administrador) usuario;
                    break;
                }
            }
        }

        if (criador == null) {
            System.err.println("Criador do jogo não encontrado: " + nomeCriador);
            return null;
        }

        // Extrair categorias das perguntas
        List<Categoria> categorias = new ArrayList<>();
        for (Pergunta pergunta : perguntasJogo) {
            if (!categorias.contains(pergunta.getCategoria())) {
                categorias.add(pergunta.getCategoria());
            }
        }

        // Criar o jogo com modalidade padrão
        IModalidadeJogo modalidade = new JogoIndividual();
        Jogo jogo = new Jogo(nomeJogo, categorias, modalidade, 3, 30, criador, perguntasJogo);

        // Adicionar participantes
        for (String email : emailsParticipantes) {
            Usuario usuario = sistema.buscarUsuarioPorEmail(email);
            if (usuario instanceof Jogador) {
                jogo.adicionarParticipante((Jogador) usuario);
            }
        }

        return jogo;
    }

    private void salvarJogos() throws IOException {
        File arquivo = new File(DIRETORIO_DADOS + ARQUIVO_JOGOS);
        try (PrintWriter writer = new PrintWriter(new FileWriter(arquivo))) {
            writer.println("# Jogos salvos do sistema");
            writer.println("# Formato: ID|NOME|CRIADOR|STATUS|DATA|CATEGORIAS");

            for (Jogo jogo : sistema.getJogos()) {
                writer.printf("%d|%s|%s|%s|%s|%s%n",
                        jogo.getId(),
                        jogo.getNome(),
                        jogo.getCriador().getEmail(),
                        jogo.getStatus(),
                        formatoData.format(jogo.getDataCriacao()),
                        jogo.getCategorias().stream()
                                .map(Categoria::getNome)
                                .collect(Collectors.joining(","))
                );
            }
        }
    }

    private void carregarDesignacoes() throws IOException {
        File arquivo = new File(DIRETORIO_DADOS + ARQUIVO_DESIGNACOES);
        if (!arquivo.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(arquivo))) {
            String linha;
            while ((linha = reader.readLine()) != null) {
                if (linha.trim().isEmpty() || linha.startsWith("#")) continue;
                // Formato: JOGO_ID|JOGADOR_EMAIL
                String[] partes = linha.split("\\|");
                if (partes.length >= 2) {
                    int jogoId = Integer.parseInt(partes[0]);
                    String jogadorEmail = partes[1];
                    // Associar jogo ao jogador
                    sistema.designarJogoParaJogador(jogoId, jogadorEmail);
                }
            }
        }
    }

    private void salvarDesignacoes() throws IOException {
        File arquivo = new File(DIRETORIO_DADOS + ARQUIVO_DESIGNACOES);
        try (PrintWriter writer = new PrintWriter(new FileWriter(arquivo))) {
            writer.println("# Designações de jogos para jogadores");
            writer.println("# Formato: JOGO_ID|JOGADOR_EMAIL");

            for (Jogo jogo : sistema.getJogos()) {
                for (Jogador participante : jogo.getParticipantes()) {
                    writer.printf("%d|%s%n", jogo.getId(), participante.getEmail());
                }
            }
        }
    }


}
