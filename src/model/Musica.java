package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public abstract class Musica {
    private final String titulo;
    private List<NotaMusical> notas;

    public Musica(String titulo, List<NotaMusical> notas) {
        this.titulo = titulo;
        this.notas = new ArrayList<>(notas);
    }

    public String getTitulo() {
        return titulo;
    }

    public List<NotaMusical> getNotas() {
        return Collections.unmodifiableList(notas);
    }

    public void setNotas(List<NotaMusical> notas) {
        this.notas = new ArrayList<>(notas);
    }

    public int getDuracaoNota(int indice) {
        if (notas != null && !notas.isEmpty() && indice == notas.size() - 1) {
            return 470;
        }
        return 270;
    }

    public String getSequenciaParaArquivo() {
        return notas.stream()
                .map(Enum::name)
                .collect(Collectors.joining(","));
    }

    public abstract void executarEfeito();

    public abstract String getTipo();

    @Override
    public String toString() {
        String sequencia = notas.stream()
                .map(NotaMusical::getSimbolo)
                .collect(Collectors.joining(" "));
        return "[" + getTipo() + "] " + titulo + " -> " + sequencia;
    }
}