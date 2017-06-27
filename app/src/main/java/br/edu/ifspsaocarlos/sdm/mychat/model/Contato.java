package br.edu.ifspsaocarlos.sdm.mychat.model;

/**
 * Created by Andrey Brugnera on 17/06/2017.
 */
public class Contato {
    private Integer id;
    private String nome;
    private String apelido;
    private boolean selecionado;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getApelido() {
        return apelido;
    }

    public void setApelido(String apelido) {
        this.apelido = apelido;
    }

    public boolean isSelecionado() {
        return selecionado;
    }

    public void setSelecionado(boolean selecionado) {
        this.selecionado = selecionado;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Contato contato = (Contato) o;

        return id.equals(contato.id);

    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}

