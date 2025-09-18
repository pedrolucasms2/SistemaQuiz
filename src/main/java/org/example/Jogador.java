package org.example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class Jogador extends Usuario {
    // Atributos específicos
    private int pontuacaoTotal;
    private int numeroVitorias;
    private int jogosParticipados;
    private Date ultimoLogin;
    private List<Conquista> conquistasObtidas;
    private Map<Categoria, Integer> pontuacaoPorCategoria;
    private Equipe equipeAtual;
    private List<Jogo> jogosInscritos;
    private Map<String, Object> estatisticas;
    private List<Jogo> jogosDesignados;

    // Construtor
    public Jogador(String nome, String email, String senha) {
        super(nome, email, senha);
        this.pontuacaoTotal = 0;
        this.numeroVitorias = 0;
        this.jogosParticipados = 0;
        this.ultimoLogin = new Date();
        this.conquistasObtidas = new ArrayList<>();
        this.pontuacaoPorCategoria = new HashMap<>();
        this.jogosInscritos = new ArrayList<>();
        this.estatisticas = new HashMap<>();
        this.jogosDesignados = new ArrayList<>();
        inicializarEstatisticas();
    }

    // Implementação de métodos abstratos
    @Override
    public String getTipoUsuario() {
        return "JOGADOR";
    }

    @Override
    public void exibirMenu() {
        System.out.println("=== MENU JOGADOR ===");
        System.out.println("1. Participar de Jogo");
        System.out.println("2. Ver Ranking");
        System.out.println("3. Minhas Estatísticas");
        System.out.println("4. Minhas Conquistas");
        System.out.println("5. Formar Equipe");
        System.out.println("0. Sair");
    }

    // Métodos específicos do jogador
    public boolean participarJogo(Jogo jogo) {
        if (jogo == null) {
            return false;
        }

        if (jogosInscritos.contains(jogo)) {
            throw new IllegalStateException("Já está inscrito neste jogo");
        }

        boolean sucesso = jogo.adicionarParticipante(this);
        if (sucesso) {
            jogosInscritos.add(jogo);
            atualizarUltimoLogin();
        }

        return sucesso;
    }

    public boolean cancelarInscricao(Jogo jogo) {
        if (jogo == null || !jogosInscritos.contains(jogo)) {
            return false;
        }

        if (jogo.getStatus() != Jogo.StatusJogo.EM_ANDAMENTO) {
            throw new IllegalStateException("Não é possível cancelar inscrição - jogo já iniciado ou finalizado");
        }

        boolean sucesso = jogo.removerParticipante(this);
        if (sucesso) {
            jogosInscritos.remove(jogo);
        }

        return sucesso;
    }

    public Resposta responderPergunta(Pergunta pergunta, int alternativaEscolhida, long tempoResposta) {
        if (pergunta == null) {
            throw new IllegalArgumentException("Pergunta não pode ser nula");
        }

        if (alternativaEscolhida < 0 || alternativaEscolhida > 3) {
            throw new IllegalArgumentException("Alternativa deve estar entre 0 e 3");
        }

        Resposta resposta = new Resposta(this, pergunta, alternativaEscolhida, tempoResposta, false, false);

        // Atualizar estatísticas pessoais
        atualizarEstatisticasResposta(resposta);

        return resposta;
    }

    public boolean usarDica(Pergunta pergunta) {
        if (pergunta == null || pergunta.getDica() == null || pergunta.getDica().isEmpty()) {
            return false;
        }

        // Marcar uso da dica nas estatísticas
        Integer dicasUsadas = (Integer) estatisticas.getOrDefault("dicasUsadas", 0);
        estatisticas.put("dicasUsadas", dicasUsadas + 1);

        return true;
    }

    public void pularPergunta(Pergunta pergunta) {
        if (pergunta != null) {
            Integer perguntasPuladas = (Integer) estatisticas.getOrDefault("perguntasPuladas", 0);
            estatisticas.put("perguntasPuladas", perguntasPuladas + 1);
        }
    }

    public void adicionarConquista(Conquista conquista) {
        if (conquista != null && !conquistasObtidas.contains(conquista)) {
            conquistasObtidas.add(conquista);
        }
    }

    public List<EstatisticaJogador> visualizarEstatisticas() {
        List<EstatisticaJogador> stats = new ArrayList<>();

        stats.add(new EstatisticaJogador("Jogos Participados", String.valueOf(jogosParticipados)));
        stats.add(new EstatisticaJogador("Vitórias", String.valueOf(numeroVitorias)));
        stats.add(new EstatisticaJogador("Taxa de Vitória", FormatadorTexto.formatarPercentual(calcularTaxaVitoria())));
        stats.add(new EstatisticaJogador("Pontuação Total", FormatadorTexto.formatarPontuacao(pontuacaoTotal)));
        stats.add(new EstatisticaJogador("Conquistas", String.valueOf(conquistasObtidas.size())));

        return stats;
    }

    public RankingPosicao consultarRanking(String tipoRanking) {
        Ranking ranking;
        GerenciadorRanking gerenciador = SistemaQuiz.getInstance().getGerenciadorRanking();

        switch (tipoRanking.toUpperCase()) {
            case "GERAL":
                ranking = gerenciador.getRankingGeral();
                break;
            case "MENSAL":
                ranking = gerenciador.getRankingMensal();
                break;
            case "ANUAL":
                ranking = gerenciador.getRankingAnual();
                break;
            default:
                throw new IllegalArgumentException("Tipo de ranking inválido: " + tipoRanking);
        }

        return ranking.getPosicaoJogador(this);
    }

    // Métodos para gerenciamento de pontuação
    public void adicionarPontos(int pontos, Categoria categoria) {
        if (pontos > 0) {
            this.pontuacaoTotal += pontos;

            if (categoria != null) {
                int pontosCategoria = pontuacaoPorCategoria.getOrDefault(categoria, 0);
                pontuacaoPorCategoria.put(categoria, pontosCategoria + pontos);
            }

            // Atualizar estatísticas
            Integer pontosGanhos = (Integer) estatisticas.getOrDefault("pontosGanhosTotal", 0);
            estatisticas.put("pontosGanhosTotal", pontosGanhos + pontos);
        }
    }

    public void registrarVitoria() {
        this.numeroVitorias++;

        // Atualizar estatísticas
        Date ultimaVitoria = new Date();
        estatisticas.put("ultimaVitoria", ultimaVitoria);
    }

    public void incrementarJogosParticipados() {
        this.jogosParticipados++;
        atualizarUltimoLogin();
    }

    // Métodos utilitários
    public double calcularTaxaVitoria() {
        return jogosParticipados > 0 ? (double) numeroVitorias / jogosParticipados : 0.0;
    }

    public int getPontuacaoCategoria(Categoria categoria) {
        return pontuacaoPorCategoria.getOrDefault(categoria, 0);
    }

    public Categoria getCategoriaFavorita() {
        return pontuacaoPorCategoria.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);
    }

    public boolean temConquista(String nomeConquista) {
        return conquistasObtidas.stream()
                .anyMatch(c -> c.getNome().equals(nomeConquista));
    }

    public int getTotalAcertos() {
        return (Integer) estatisticas.getOrDefault("totalAcertos", 0);
    }

    public int getTotalErros() {
        return (Integer) estatisticas.getOrDefault("totalErros", 0);
    }

    public double getPorcentagemAcertos() {
        int acertos = getTotalAcertos();
        int erros = getTotalErros();
        int total = acertos + erros;

        return total > 0 ? (double) acertos / total : 0.0;
    }

    // Métodos privados
    private void inicializarEstatisticas() {
        estatisticas.put("totalAcertos", 0);
        estatisticas.put("totalErros", 0);
        estatisticas.put("dicasUsadas", 0);
        estatisticas.put("perguntasPuladas", 0);
        estatisticas.put("pontosGanhosTotal", 0);
        estatisticas.put("tempoMedioResposta", 0L);
    }

    private void atualizarEstatisticasResposta(Resposta resposta) {
        if (resposta.isCorreta()) {
            Integer acertos = (Integer) estatisticas.get("totalAcertos");
            estatisticas.put("totalAcertos", acertos + 1);
        } else if (!resposta.isPulou()) {
            Integer erros = (Integer) estatisticas.get("totalErros");
            estatisticas.put("totalErros", erros + 1);
        }

        // Atualizar tempo médio
        Long tempoMedio = (Long) estatisticas.get("tempoMedioResposta");
        int totalRespostas = getTotalAcertos() + getTotalErros();
        long novoTempoMedio = (tempoMedio * (totalRespostas - 1) + resposta.getTempoResposta()) / totalRespostas;
        estatisticas.put("tempoMedioResposta", novoTempoMedio);
    }

    public void adicionarJogoDesignado(Jogo jogo) {
        if (!jogosDesignados.contains(jogo)) {
            jogosDesignados.add(jogo);
        }
    }

    public boolean temJogoDesignado(Jogo jogo) {
        return jogosDesignados.contains(jogo);
    }

    private void atualizarUltimoLogin() {
        this.ultimoLogin = new Date();
    }

    // Getters e Setters necessários para persistência
    public int getPontuacaoTotal() {
        return pontuacaoTotal;
    }

    public void setPontuacaoTotal(int pontuacaoTotal) {
        this.pontuacaoTotal = pontuacaoTotal;
    }

    public int getNumeroVitorias() {
        return numeroVitorias;
    }

    public void setNumeroVitorias(int numeroVitorias) {
        this.numeroVitorias = numeroVitorias;
    }

    public int getJogosParticipados() {
        return jogosParticipados;
    }

    public void setJogosParticipados(int jogosParticipados) {
        this.jogosParticipados = jogosParticipados;
    }

    public Date getUltimoLogin() {
        return ultimoLogin;
    }

    public void setUltimoLogin(Date ultimoLogin) {
        this.ultimoLogin = ultimoLogin;
    }

    public List<Conquista> getConquistasObtidas() {
        return new ArrayList<>(conquistasObtidas);
    }

    public Map<Categoria, Integer> getPontuacaoPorCategoria() {
        return new HashMap<>(pontuacaoPorCategoria);
    }

    public Equipe getEquipeAtual() {
        return equipeAtual;
    }

    public void setEquipeAtual(Equipe equipeAtual) {
        this.equipeAtual = equipeAtual;
    }

    public List<Jogo> getJogosInscritos() {
        return new ArrayList<>(jogosInscritos);
    }

    public Map<String, Object> getEstatisticas() {
        return new HashMap<>(estatisticas);
    }

    public List<Jogo> getJogosDesignados() {
        return new ArrayList<>(jogosDesignados);
    }
}
