package model;

import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Synthesizer;
import java.util.List;

public class SintetizadorOcarina {
    private static final int INSTRUMENTO_OCARINA = 79;
    private static final int INSTRUMENTO_HARPA = 46;
    private Synthesizer sintetizador;
    private MidiChannel canalOcarina;
    private MidiChannel canalHarpa;
    private boolean audioDisponivel;

    public SintetizadorOcarina() {
        try {
            sintetizador = MidiSystem.getSynthesizer();
            sintetizador.open();
            canalOcarina = sintetizador.getChannels()[0];
            canalOcarina.programChange(INSTRUMENTO_OCARINA);
            canalHarpa = sintetizador.getChannels()[1];
            canalHarpa.programChange(INSTRUMENTO_HARPA);
            audioDisponivel = true;
        } catch (Exception e) {
            audioDisponivel = false;
        }
    }

    public void tocarNota(NotaMusical nota, int duracaoMs) {
        if (nota == null) {
            return;
        }
        tocarFrequenciaNoCanal(canalOcarina, nota.getNotaMidi(), duracaoMs);
    }

    public void tocarNotaHarpa(NotaMusical nota, int duracaoMs) {
        if (nota == null) {
            return;
        }
        tocarFrequenciaNoCanal(canalHarpa, nota.getNotaMidi(), duracaoMs);
    }

    public void tocarFrequenciaMidi(int notaMidi, int duracaoMs) {
        tocarFrequenciaNoCanal(canalOcarina, notaMidi, duracaoMs);
    }

    private void tocarFrequenciaNoCanal(MidiChannel canal, int notaMidi, int duracaoMs) {
        if (!audioDisponivel || canal == null) {
            return;
        }
        try {
            canal.noteOn(notaMidi, 105);
            Thread.sleep(duracaoMs);
            canal.noteOff(notaMidi);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void tocarMelodiaCompleta(Musica musica) {
        List<NotaMusical> notas = musica.getNotas();
        for (int i = 0; i < notas.size(); i++) {
            NotaMusical nota = notas.get(i);
            System.out.print(nota.getSimbolo() + " ");
            tocarNota(nota, musica.getDuracaoNota(i));
        }
        System.out.println();
    }

    public void tocarMelodiaComHarpa(Musica musica) {
        List<NotaMusical> notas = musica.getNotas();
        for (int i = 0; i < notas.size(); i++) {
            NotaMusical nota = notas.get(i);
            System.out.print(nota.getSimbolo() + " ");
            tocarNotaHarpa(nota, musica.getDuracaoNota(i));
        }
        System.out.println();
    }

    public void tocarSequenciaMidi(MusicaCanonica canonica) {
        List<Integer> notasMidi = canonica.getContinuacaoMidi();
        for (int i = 0; i < notasMidi.size(); i++) {
            System.out.print("~ ");
            tocarFrequenciaMidi(notasMidi.get(i), canonica.getDuracaoContinuacao(i));
        }
        System.out.println();
    }

    public void encerrar() {
        if (sintetizador != null && sintetizador.isOpen()) {
            sintetizador.close();
        }
    }
}