package model;

import java.util.List;

public class MusicaCustomizada extends Musica {
    private String compositor;

    public MusicaCustomizada(String titulo, List<NotaMusical> notas, String compositor) {
        super(titulo, notas);
        this.compositor = compositor;
    }

    public String getCompositor() {
        return compositor;
    }

    public void setCompositor(String compositor) {
        this.compositor = compositor;
    }

    @Override
    public void executarEfeito() {
        System.out.println("Melodia autoral composta por " + compositor + ".");
    }

    @Override
    public String getTipo() {
        return "AUTORAL";
    }

    @Override
    public String toString() {
        return super.toString() + " (por " + compositor + ")";
    }
}