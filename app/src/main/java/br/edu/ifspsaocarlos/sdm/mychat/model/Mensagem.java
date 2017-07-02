package br.edu.ifspsaocarlos.sdm.mychat.model;

/**
 * Created by Andrey Brugnera on 17/06/2017.
 */
public class Mensagem {
    private Integer id;
    private Integer idOrigem;
    private Integer idDestino;
    private String assunto;
    private String corpo;
    private Contato origem;
    private Contato destino;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getIdOrigem() {
        return idOrigem;
    }

    public void setIdOrigem(Integer idOrigem) {
        this.idOrigem = idOrigem;
    }

    public Integer getIdDestino() {
        return idDestino;
    }

    public void setIdDestino(Integer idDestino) {
        this.idDestino = idDestino;
    }

    public String getAssunto() {
        return assunto;
    }

    public void setAssunto(String assunto) {
        this.assunto = assunto;
    }

    public String getCorpo() {
        return corpo;
    }

    public void setCorpo(String corpo) {
        this.corpo = corpo;
    }

    public Contato getOrigem() {
        return origem;
    }

    public void setOrigem(Contato origem) {
        this.origem = origem;
        this.idOrigem = origem.getId();
    }

    public Contato getDestino() {
        return destino;
    }

    public void setDestino(Contato destino) {
        this.destino = destino;
        this.idDestino = destino.getId();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Mensagem [origem: ").append(origem.getNome());
        sb.append(", destino: ").append(destino.getNome());
        sb.append(", corpo: ").append(corpo);
        sb.append(", assunto: ").append(assunto).append("]");
        return sb.toString();
    }

}

