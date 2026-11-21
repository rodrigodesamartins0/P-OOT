package model;

import java.util.List;

public class MusicaCanonica extends Musica {
    private final String efeitoMagico;

    public MusicaCanonica(String titulo, List<NotaMusical> notas, String efeitoMagico) {
        super(titulo, notas);
        this.efeitoMagico = efeitoMagico;
    }

    public String getEfeitoMagico() {
        return efeitoMagico;
    }

    @Override
    public void executarEfeito() {
        System.out.println(efeitoMagico);
    }

    @Override
    public String getTipo() {
        return "CANÔNICA";
    }
}