package org.example;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class RelatorioAdmin {
    private String titulo;
    private Date dataInicio;
    private Date dataFim;
    private Date dataGeracao;
    private Administrador gerador;
    private Map<String, Object> dados;
    private List<String> secoes;

    public RelatorioAdmin(String titulo, Date dataInicio, Date dataFim, Administrador gerador) {
        this.titulo = titulo;
        this.dataInicio = new Date(dataInicio.getTime());
        this.dataFim = new Date(dataFim.getTime());
        this.gerador = gerador;
        this.dataGeracao = new Date();
        this.dados = new HashMap<>();
        this.secoes = new ArrayList<>();
        gerarRelatorio();
    }

    private void gerarRelatorio() {
        secoes.add("Resumo Executivo");
        secoes.add("Dados Principais");
        secoes.add("Análise Detalhada");
        secoes.add("Conclusões");

        // Gerar dados baseado no tipo de relatório
        switch (titulo.toLowerCase()) {
            case "relatório de jogos":
                gerarDadosJogos();
                break;
            case "relatório de usuários":
                gerarDadosUsuarios();
                break;
            case "relatório de perguntas":
                gerarDadosPerguntas();
                break;
        }
    }

    private void gerarDadosJogos() {
        // Implementar coleta de dados de jogos
        dados.put("totalJogos", 0);
        dados.put("jogosFinalizados", 0);
        dados.put("jogosEmAndamento", 0);
        dados.put("mediaParticipantes", 0.0);
        dados.put("modalidadeMaisPopular", "Individual");
    }

    private void gerarDadosUsuarios() {
        // Implementar coleta de dados de usuários
        dados.put("totalUsuarios", 0);
        dados.put("usuariosAtivos", 0);
        dados.put("novosCadastros", 0);
        dados.put("mediaJogosPorUsuario", 0.0);
    }

    private void gerarDadosPerguntas() {
        // Implementar coleta de dados de perguntas
        dados.put("totalPerguntas", 0);
        dados.put("perguntasAtivas", 0);
        dados.put("categoriaMaisUsada", "História");
        dados.put("mediaDificuldade", "Médio");
    }

    // Getters/Setters
    public String getTitulo() { return titulo; }
    public Date getDataInicio() { return new Date(dataInicio.getTime()); }
    public Date getDataFim() { return new Date(dataFim.getTime()); }
    public Date getDataGeracao() { return new Date(dataGeracao.getTime()); }
    public Administrador getGerador() { return gerador; }
    public Map<String, Object> getDados() { return new HashMap<>(dados); }
    public List<String> getSecoes() { return new ArrayList<>(secoes); }

    public Object getDado(String chave) {
        return dados.get(chave);
    }

    public void adicionarDado(String chave, Object valor) {
        dados.put(chave, valor);
    }

    public String gerarTextoRelatorio() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== ").append(titulo.toUpperCase()).append(" ===\n");
        sb.append("Período: ").append(FormatadorTexto.formatarData(dataInicio))
                .append(" a ").append(FormatadorTexto.formatarData(dataFim)).append("\n");
        sb.append("Gerado por: ").append(gerador.getNome()).append("\n");
        sb.append("Data: ").append(FormatadorTexto.formatarData(dataGeracao)).append("\n\n");

        for (String secao : secoes) {
            sb.append("--- ").append(secao).append(" ---\n");
            // Adicionar dados da seção
            sb.append("\n");
        }

        return sb.toString();
    }

    @Override
    public String toString() {
        return titulo + " (" + FormatadorTexto.formatarData(dataGeracao) + ")";
    }
}

