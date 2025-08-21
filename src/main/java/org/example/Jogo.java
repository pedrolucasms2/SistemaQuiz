package org.example;

import java.util.*;
import java.util.stream.Collectors;

public class Jogo extends QuizObservable {
    // Enumeração para status do jogo
    public enum StatusJogo {
        AGUARDANDO("Aguardando Jogadores"),
        EM_ANDAMENTO("Em Andamento"),
        FINALIZADO("Finalizado"),
        CANCELADO("Cancelado"),
        PAUSADO("Pausado");

        private final String descricao;

        StatusJogo(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() { return descricao; }
    }

    // Atributos privados
    private int id;
    private String nome;
    private List<Categoria> categorias;
    private IModalidadeJogo modalidade;
    private int numeroRodadas;
    private int tempoLimitePergunta;
    private List<Jogador> participantes;
    private List<Equipe> equipesParticipantes;
    private List<Pergunta> perguntasJogo;
    private StatusJogo status;
    private Date dataInicio;
    private Date dataFim;
    private Date dataCriacao;
    private Administrador criador;
    private int rodadaAtual;
    private int perguntaAtual;
    private Map<Jogador, Integer> pontuacoes;
    private Map<Jogador, List<Resposta>> respostasJogadores;
    private static int proximoId = 1;

    // Construtor
    public Jogo(String nome, List<Categoria> categorias, IModalidadeJogo modalidade,
                int numeroRodadas, int tempoLimitePergunta, Administrador criador) {
        this.id = proximoId++;
        this.nome = nome;
        this.categorias = new ArrayList<>(categorias);
        this.modalidade = modalidade;
        this.numeroRodadas = numeroRodadas;
        this.tempoLimitePergunta = tempoLimitePergunta;
        this.criador = criador;
        this.participantes = new ArrayList<>();
        this.equipesParticipantes = new ArrayList<>();
        this.perguntasJogo = new ArrayList<>();
        this.pontuacoes = new HashMap<>();
        this.respostasJogadores = new HashMap<>();
        this.status = StatusJogo.AGUARDANDO;
        this.dataCriacao = new Date();
        this.rodadaAtual = 0;
        this.perguntaAtual = 0;
    }

    // Métodos públicos
    public boolean adicionarParticipante(Jogador jogador) {
        if (jogador == null) {
            throw new IllegalArgumentException("Jogador não pode ser nulo");
        }

        if (status != StatusJogo.AGUARDANDO) {
            return false;
        }

        if (participantes.contains(jogador)) {
            return false;
        }

        if (!modalidade.podeAdicionarParticipante(jogador)) {
            return false;
        }

        if (participantes.size() >= modalidade.getMaximoParticipantes()) {
            return false;
        }

        participantes.add(jogador);
        pontuacoes.put(jogador, 0);
        respostasJogadores.put(jogador, new ArrayList<>());

        setChanged();
        notifyObservers(new EventoSistema(EventoSistema.TipoEvento.JOGADOR_INSCRITO, jogador));

        return true;
    }


    public boolean removerParticipante(Jogador jogador) {
        if (status != StatusJogo.AGUARDANDO) {
            return false;
        }

        boolean removido = participantes.remove(jogador);
        if (removido) {
            pontuacoes.remove(jogador);
            respostasJogadores.remove(jogador);

            setChanged();
            notifyObservers(new EventoSistema(EventoSistema.TipoEvento.JOGADOR_REMOVIDO, jogador));
        }



        return removido;
    }

    public void iniciar() {
        if (participantes.isEmpty()) {
            throw new IllegalStateException("Não é possível iniciar jogo sem participantes");
        }

        if (status != StatusJogo.AGUARDANDO) {
            throw new IllegalStateException("Jogo não está aguardando para iniciar");
        }

        status = StatusJogo.EM_ANDAMENTO;
        dataInicio = new Date();
        selecionarPerguntas();
        modalidade.iniciarJogo();

        // Notificar observadores
        setChanged();
        notifyObservers(new EventoSistema(EventoSistema.TipoEvento.JOGO_INICIADO, this));
    }

    public void pausar() {
        if (status == StatusJogo.EM_ANDAMENTO) {
            status = StatusJogo.PAUSADO;

            setChanged();
            notifyObservers(new EventoSistema(EventoSistema.TipoEvento.JOGO_PAUSADO, this));
        }
    }

    public void retomar() {
        if (status == StatusJogo.PAUSADO) {
            status = StatusJogo.EM_ANDAMENTO;

            setChanged();
            notifyObservers(new EventoSistema(EventoSistema.TipoEvento.JOGO_RETOMADO, this));
        }
    }

    public void processarResposta(Jogador jogador, Resposta resposta) {
        if (status != StatusJogo.EM_ANDAMENTO) {
            throw new IllegalStateException("Jogo não está em andamento");
        }

        if (!participantes.contains(jogador)) {
            throw new IllegalArgumentException("Jogador não participa deste jogo");
        }

        modalidade.processarResposta(jogador, resposta);
        atualizarPontuacao(jogador, resposta);

        // Adicionar resposta ao histórico
        respostasJogadores.get(jogador).add(resposta);

        // Notificar observadores
        setChanged();
        notifyObservers(new EventoSistema(EventoSistema.TipoEvento.RESPOSTA_PROCESSADA, resposta));
    }

    public void proximaPergunta() {
        if (perguntaAtual < perguntasJogo.size() - 1) {
            perguntaAtual++;

            setChanged();
            notifyObservers(new EventoSistema(EventoSistema.TipoEvento.PROXIMA_PERGUNTA, getPerguntaAtual()));
        }
    }

    public void proximaRodada() {
        if (rodadaAtual < numeroRodadas - 1) {
            rodadaAtual++;
            perguntaAtual = 0; // Reset para primeira pergunta da rodada

            setChanged();
            notifyObservers(new EventoSistema(EventoSistema.TipoEvento.PROXIMA_RODADA, rodadaAtual));
        }
    }

    public void finalizar() {
        if (status != StatusJogo.EM_ANDAMENTO && status != StatusJogo.PAUSADO) {
            throw new IllegalStateException("Jogo não pode ser finalizado no estado atual");
        }

        status = StatusJogo.FINALIZADO;
        dataFim = new Date();
        modalidade.finalizarJogo();

        // Processar resultados finais
        List<ResultadoJogo> resultados = calcularResultadosFinais();

        // Atualizar estatísticas dos jogadores
        for (Jogador jogador : participantes) {
            jogador.incrementarJogosParticipados();
        }

        // Determinar vencedores
        List<Jogador> vencedores = determinarVencedores();
        for (Jogador vencedor : vencedores) {
            vencedor.registrarVitoria();
        }

        // Notificar observadores
        setChanged();
        notifyObservers(new EventoSistema(EventoSistema.TipoEvento.JOGO_FINALIZADO, resultados));
    }

    public void cancelar() {
        if (status == StatusJogo.FINALIZADO) {
            throw new IllegalStateException("Não é possível cancelar jogo já finalizado");
        }

        status = StatusJogo.CANCELADO;
        dataFim = new Date();

        // Remover jogadores das listas de inscritos
        for (Jogador jogador : participantes) {
            jogador.getJogosInscritos().remove(this);
        }

        setChanged();
        notifyObservers(new EventoSistema(EventoSistema.TipoEvento.JOGO_CANCELADO, this));
    }

    // Métodos de consulta
    public Pergunta getPerguntaAtual() {
        if (perguntasJogo.isEmpty() || perguntaAtual >= perguntasJogo.size()) {
            return null;
        }
        return perguntasJogo.get(perguntaAtual);
    }

    public boolean temProximaPergunta() {
        return perguntaAtual < perguntasJogo.size() - 1;
    }

    public boolean temProximaRodada() {
        return rodadaAtual < numeroRodadas - 1;
    }

    public int getProgressoPercentual() {
        if (perguntasJogo.isEmpty()) return 0;
        return (perguntaAtual * 100) / perguntasJogo.size();
    }

    public boolean podeIniciar() {
        return status == StatusJogo.AGUARDANDO && !participantes.isEmpty();
    }

    public boolean podeAdicionarParticipante(Jogador jogador) {
        return status == StatusJogo.AGUARDANDO &&
                !participantes.contains(jogador) &&
                participantes.size() < modalidade.getMaximoParticipantes() &&
                modalidade.podeAdicionarParticipante(jogador);
    }

    public String getStatusFormatado() {
        return status.getDescricao();
    }

    public List<String> getInformacoesDisplay() {
        List<String> infos = new ArrayList<>();
        infos.add("Jogo: " + nome);
        infos.add("Status: " + getStatusFormatado());
        infos.add("Participantes: " + participantes.size() + "/" + modalidade.getMaximoParticipantes());
        infos.add("Modalidade: " + modalidade.getDescricaoModalidade());
        infos.add("Categorias: " + categorias.stream().map(Categoria::getNome).collect(Collectors.joining(", ")));

        if (dataInicio != null) {
            infos.add("Iniciado: " + FormatadorTexto.formatarData(dataInicio));
        }

        if (status == StatusJogo.EM_ANDAMENTO) {
            infos.add("Rodada: " + (rodadaAtual + 1) + "/" + numeroRodadas);
            infos.add("Pergunta: " + (perguntaAtual + 1) + "/" + perguntasJogo.size());
        }

        return infos;
    }

    public long getDuracaoJogo() {
        if (dataInicio == null) return 0;

        Date fim = dataFim != null ? dataFim : new Date();
        return fim.getTime() - dataInicio.getTime();
    }

    public String getDuracaoFormatada() {
        return FormatadorTexto.formatarTempo(getDuracaoJogo());
    }

    // Métodos privados
    private void selecionarPerguntas() {
        perguntasJogo.clear();

        int perguntasPorRodada = 10; // Configurável
        int totalPerguntas = numeroRodadas * perguntasPorRodada;

        List<Pergunta> todasPerguntas = new ArrayList<>();
        for (Categoria categoria : categorias) {
            todasPerguntas.addAll(categoria.getPerguntas().stream()
                    .filter(Pergunta::isAtiva)
                    .collect(Collectors.toList()));
        }

        if (todasPerguntas.size() < totalPerguntas) {
            throw new IllegalStateException("Não há perguntas suficientes nas categorias selecionadas");
        }

        // Selecionar perguntas aleatoriamente
        Collections.shuffle(todasPerguntas);
        perguntasJogo.addAll(todasPerguntas.subList(0, Math.min(totalPerguntas, todasPerguntas.size())));

        // Balancear dificuldades se possível
        balancearDificuldades();
    }

    private void balancearDificuldades() {
        // Implementação para balancear dificuldades das perguntas
        Map<Pergunta.Dificuldade, List<Pergunta>> perguntasPorDificuldade =
                perguntasJogo.stream().collect(Collectors.groupingBy(Pergunta::getDificuldade));

        // Reordenar para intercalar dificuldades
        List<Pergunta> perguntasBalanceadas = new ArrayList<>();
        int maxPerDificuldade = perguntasJogo.size() / 4; // 4 níveis de dificuldade

        for (int i = 0; i < maxPerDificuldade; i++) {
            for (Pergunta.Dificuldade diff : Pergunta.Dificuldade.values()) {
                List<Pergunta> perguntasDiff = perguntasPorDificuldade.get(diff);
                if (perguntasDiff != null && i < perguntasDiff.size()) {
                    perguntasBalanceadas.add(perguntasDiff.get(i));
                }
            }
        }

        if (!perguntasBalanceadas.isEmpty()) {
            perguntasJogo = perguntasBalanceadas;
        }
    }

    private void atualizarPontuacao(Jogador jogador, Resposta resposta) {
        int pontosGanhos = resposta.calcularPontos();
        int pontuacaoAtual = pontuacoes.getOrDefault(jogador, 0);
        pontuacoes.put(jogador, pontuacaoAtual + pontosGanhos);

        // Atualizar pontuação do jogador
        jogador.adicionarPontos(pontosGanhos, resposta.getPergunta().getCategoria());
    }

    private List<ResultadoJogo> calcularResultadosFinais() {
        List<ResultadoJogo> resultados = new ArrayList<>();

        for (Jogador jogador : participantes) {
            List<Resposta> respostasJogador = respostasJogadores.get(jogador);
            int pontuacaoFinal = pontuacoes.get(jogador);

            ResultadoJogo resultado = new ResultadoJogo(
                    jogador, this, pontuacaoFinal, respostasJogador, getDuracaoJogo());

            resultados.add(resultado);
        }

        // Ordenar por pontuação (maior primeiro)
        resultados.sort((r1, r2) -> Integer.compare(r2.getPontuacaoFinal(), r1.getPontuacaoFinal()));

        return resultados;
    }

    private List<Jogador> determinarVencedores() {
        if (pontuacoes.isEmpty()) {
            return new ArrayList<>();
        }

        int maiorPontuacao = pontuacoes.values().stream()
                .mapToInt(Integer::intValue)
                .max()
                .orElse(0);

        return pontuacoes.entrySet().stream()
                .filter(entry -> entry.getValue() == maiorPontuacao)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    // Getters/Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public List<Categoria> getCategorias() { return new ArrayList<>(categorias); }
    public void setCategorias(List<Categoria> categorias) {
        this.categorias = new ArrayList<>(categorias);
    }

    public IModalidadeJogo getModalidade() { return modalidade; }
    public void setModalidade(IModalidadeJogo modalidade) { this.modalidade = modalidade; }

    public int getNumeroRodadas() { return numeroRodadas; }
    public void setNumeroRodadas(int numeroRodadas) { this.numeroRodadas = numeroRodadas; }

    public int getTempoLimitePergunta() { return tempoLimitePergunta; }
    public void setTempoLimitePergunta(int tempoLimitePergunta) {
        this.tempoLimitePergunta = tempoLimitePergunta;
    }

    public List<Jogador> getParticipantes() { return new ArrayList<>(participantes); }

    public List<Equipe> getEquipesParticipantes() { return new ArrayList<>(equipesParticipantes); }

    public List<Pergunta> getPerguntasJogo() { return new ArrayList<>(perguntasJogo); }

    public StatusJogo getStatus() { return status; }

    public Date getDataInicio() { return dataInicio != null ? new Date(dataInicio.getTime()) : null; }

    public Date getDataFim() { return dataFim != null ? new Date(dataFim.getTime()) : null; }

    public Date getDataCriacao() { return new Date(dataCriacao.getTime()); }

    public Administrador getCriador() { return criador; }

    public int getRodadaAtual() { return rodadaAtual; }

    public int getIndicePerguntaAtual() { return perguntaAtual; }

    public int getNumeroParticipantes() { return participantes.size(); }

    public Map<Jogador, Integer> getPontuacoes() { return new HashMap<>(pontuacoes); }

    public Map<Jogador, List<Resposta>> getRespostasJogadores() {
        Map<Jogador, List<Resposta>> copia = new HashMap<>();
        for (Map.Entry<Jogador, List<Resposta>> entry : respostasJogadores.entrySet()) {
            copia.put(entry.getKey(), new ArrayList<>(entry.getValue()));
        }
        return copia;
    }

    public int getNumeroPerguntas() { return perguntasJogo.size(); }

    public Pergunta getPergunta(int indice) {
        if (indice >= 0 && indice < perguntasJogo.size()) {
            return perguntasJogo.get(indice);
        }
        return null;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Jogo jogo = (Jogo) obj;
        return id == jogo.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("Jogo{id=%d, nome='%s', status=%s, participantes=%d}",
                id, nome, status, participantes.size());
    }
}

