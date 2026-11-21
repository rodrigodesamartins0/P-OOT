package model;

import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Synthesizer;
import java.util.List;

public class SintetizadorOcarina {
    private static final int INSTRUMENTO_OCARINA = 79;
    private Synthesizer sintetizador;
    private MidiChannel canal;
    private boolean audioDisponivel;

    public SintetizadorOcarina() {
        try {
            sintetizador = MidiSystem.getSynthesizer();
            sintetizador.open();
            canal = sintetizador.getChannels()[0];
            canal.programChange(INSTRUMENTO_OCARINA);
            audioDisponivel = true;
        } catch (Exception e) {
            audioDisponivel = false;
        }
    }

    public void tocarNota(NotaMusical nota, int duracaoMs) {
        if (!audioDisponivel || nota == null) {
            return;
        }
        try {
            canal.noteOn(nota.getNotaMidi(), 100);
            Thread.sleep(duracaoMs);
            canal.noteOff(nota.getNotaMidi());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void tocarMelodiaCompleta(List<NotaMusical> notas) {
        for (NotaMusical nota : notas) {
            System.out.print(nota.getSimbolo() + " ");
            tocarNota(nota, 320);
        }
        System.out.println();
    }

    public void encerrar() {
        if (sintetizador != null && sintetizador.isOpen()) {
            sintetizador.close();
        }
    }
}