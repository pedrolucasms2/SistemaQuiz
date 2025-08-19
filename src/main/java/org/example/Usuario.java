package org.example;

import java.util.Date;
import java.util.Objects;

public abstract class Usuario {
    // Atributos privados
    private int id;
    private String nome;
    private String email;
    private String senha;
    private Date dataCadastro;
    private boolean ativo;
    private static int proximoId = 1;

    // Construtor
    public Usuario(String nome, String email, String senha) {
        this.id = proximoId++;
        this.nome = nome;
        this.email = email;
        this.senha = criptografarSenha(senha);
        this.dataCadastro = new Date();
        this.ativo = true;
    }

    // Métodos públicos (getters/setters)
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) {
        if (nome != null && !nome.trim().isEmpty()) {
            this.nome = nome.trim();
        }
    }

    public String getEmail() { return email; }
    public void setEmail(String email) {
        if (ValidadorInterface.validarEmail(email)) {
            this.email = email.toLowerCase().trim();
        }
    }

    public Date getDataCadastro() { return new Date(dataCadastro.getTime()); }

    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }

    // Métodos protegidos para subclasses
    protected void setDataCadastro(Date data) {
        this.dataCadastro = new Date(data.getTime());
    }

    protected boolean validarSenha(String senha) {
        return this.senha.equals(criptografarSenha(senha));
    }

    protected void setSenha(String novaSenha) {
        this.senha = criptografarSenha(novaSenha);
    }

    // Métodos abstratos
    public abstract String getTipoUsuario();
    public abstract void exibirMenu();

    // Métodos concretos
    public boolean autenticar(String email, String senha) {
        return this.email.equals(email) && validarSenha(senha);
    }

    public void alterarSenha(String novaSenha) {
        if (ValidadorInterface.validarSenha(novaSenha)) {
            setSenha(novaSenha);
        } else {
            throw new IllegalArgumentException("Senha inválida");
        }
    }

    public String getResumoEstatisticas() {
        return "Usuário: " + nome + " - Cadastrado em: " +
                FormatadorTexto.formatarData(dataCadastro);
    }

    public boolean podeAcessarFuncionalidade(String funcionalidade) {
        return ativo;
    }

    // Métodos utilitários privados
    private String criptografarSenha(String senha) {
        // Implementação simples de hash (em produção usar BCrypt ou similar)
        return String.valueOf(senha.hashCode());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Usuario usuario = (Usuario) obj;
        return id == usuario.id && Objects.equals(email, usuario.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email);
    }

    @Override
    public String toString() {
        return getTipoUsuario() + ": " + nome + " (" + email + ")";
    }
}


