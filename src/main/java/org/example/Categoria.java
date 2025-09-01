package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Categoria {
    // Atributos privados
    private int id;
    private String nome;
    private String descricao;
    private Categoria categoriaPai; // Para subcategorias
    private List<Categoria> subcategorias;
    private List<Pergunta> perguntas;
    private boolean ativa;
    private String cor; // Para interface visual

    // Construtor
    public Categoria(String nome, String descricao, Categoria categoriaPai) {
        this.nome = nome;
        this.descricao = descricao;
        this.categoriaPai = categoriaPai;
        this.subcategorias = new ArrayList<>();
        this.perguntas = new ArrayList<>();
        this.ativa = true;
    }

    // Métodos públicos
    public void adicionarSubcategoria(Categoria subcategoria) {
        if (subcategoria != null && !subcategorias.contains(subcategoria)) {
            subcategorias.add(subcategoria);
            subcategoria.categoriaPai = this;
        }
    }

    public void removerSubcategoria(Categoria subcategoria) {
        if (subcategoria != null) {
            subcategorias.remove(subcategoria);
            subcategoria.categoriaPai = null;
        }
    }

    public void adicionarPergunta(Pergunta pergunta) {
        if (pergunta != null && !perguntas.contains(pergunta)) {
            perguntas.add(pergunta);
            System.out.println("✅ Pergunta adicionada à categoria " + nome + ": " + pergunta.getEnunciado());
        }
    }

    public List<Pergunta> getPerguntasPorDificuldade(String dificuldade) {
        if (dificuldade == null || dificuldade.trim().isEmpty()) {
            return new ArrayList<>(perguntas);
        }

        try {
            Pergunta.Dificuldade diff = Pergunta.Dificuldade.valueOf(dificuldade.toUpperCase());
            return perguntas.stream()
                    .filter(p -> p.getDificuldade() == diff)
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            return new ArrayList<>();
        }
    }

    public boolean isSubcategoria() { return categoriaPai != null; }
    public int getTotalPerguntas() { return perguntas.size(); }

    // Getters/Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public Categoria getCategoriaPai() { return categoriaPai; }
    public List<Categoria> getSubcategorias() { return new ArrayList<>(subcategorias); }
    public List<Pergunta> getPerguntas() { return new ArrayList<>(perguntas); }
    public boolean isAtiva() { return ativa; }
    public void setAtiva(boolean ativa) { this.ativa = ativa; }

    public String getCor() { return cor; }
    public void setCor(String cor) { this.cor = cor; }

    // Método toString para exibição correta em dropdowns
    @Override
    public String toString() {
        return nome;
    }

    // Métodos equals e hashCode para comparações corretas
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Categoria categoria = (Categoria) obj;
        return nome.equals(categoria.nome);
    }

    @Override
    public int hashCode() {
        return nome.hashCode();
    }
}
