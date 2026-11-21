package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public abstract class Musica {
    private String titulo;
    private List<NotaMusical> notas;

    public Musica(String titulo, List<NotaMusical> notas) {
        this.titulo = titulo;
        this.notas = new ArrayList<>(notas);
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public List<NotaMusical> getNotas() {
        return Collections.unmodifiableList(notas);
    }

    public void setNotas(List<NotaMusical> notas) {
        this.notas = new ArrayList<>(notas);
    }

    public String getSequenciaVisual() {
        return notas.stream()
                .map(NotaMusical::getSimbolo)
                .collect(Collectors.joining(" "));
    }

    public String getSequenciaParaArquivo() {
        return notas.stream()
                .map(NotaMusical::getTecla)
                .collect(Collectors.joining(","));
    }

    public abstract void executarEfeito();

    public abstract String getTipo();

    @Override
    public String toString() {
        return String.format("[%s] %s -> %s", getTipo(), titulo, getSequenciaVisual());
    }
}