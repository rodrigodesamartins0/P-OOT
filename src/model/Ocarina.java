package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Ocarina {
    private static final int LIMITE_HISTORICO = 20;
    private final SintetizadorOcarina sintetizador;
    private final List<NotaMusical> historicoNotas;

    public Ocarina(SintetizadorOcarina sintetizador) {
        this.sintetizador = sintetizador;
        this.historicoNotas = new ArrayList<>();
    }

    public void tocarNota(NotaMusical nota) {
        if (nota == null) {
            return;
        }
        if (historicoNotas.size() >= LIMITE_HISTORICO) {
            historicoNotas.remove(0);
        }
        historicoNotas.add(nota);
        sintetizador.tocarNota(nota, 240);
    }

    public Musica verificarReconhecimento(List<Musica> repertorio) {
        for (Musica musica : repertorio) {
            List<NotaMusical> sequencia = musica.getNotas();
            int tamanhoMusica = sequencia.size();

            if (tamanhoMusica > 0 && historicoNotas.size() >= tamanhoMusica) {
                int inicio = historicoNotas.size() - tamanhoMusica;
                List<NotaMusical> trechoRecente = historicoNotas.subList(inicio, historicoNotas.size());

                if (trechoRecente.equals(sequencia)) {
                    return musica;
                }
            }
        }
        return null;
    }

    public boolean verificarConflitoEspantalho(List<NotaMusical> notasEspantalho, List<Musica> repertorio) {
        for (Musica musica : repertorio) {
            if (musica.getTitulo().equalsIgnoreCase("Canção do Espantalho")) {
                continue;
            }
            List<NotaMusical> sequencia = musica.getNotas();
            if (!sequencia.isEmpty() && sequencia.size() <= notasEspantalho.size()) {
                List<NotaMusical> inicioEspantalho = notasEspantalho.subList(0, sequencia.size());
                if (inicioEspantalho.equals(sequencia)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean verificarConflitoMusicaAutoral(List<NotaMusical> notasAutoral, List<Musica> repertorio, Musica musicaIgnorada) {
        for (Musica musica : repertorio) {
            if (musica == musicaIgnorada) {
                continue;
            }
            List<NotaMusical> sequencia = musica.getNotas();
            if (sequencia.isEmpty()) {
                continue;
            }

            if (musica instanceof MusicaCanonica) {
                int menorTamanho = Math.min(notasAutoral.size(), sequencia.size());
                List<NotaMusical> prefixoAutoral = notasAutoral.subList(0, menorTamanho);
                List<NotaMusical> prefixoCanonica = sequencia.subList(0, menorTamanho);
                if (prefixoAutoral.equals(prefixoCanonica)) {
                    return true;
                }
            } else if (musica instanceof MusicaCustomizada) {
                if (sequencia.equals(notasAutoral)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void reproduzirMusicaReconhecida(Musica musica) {
        System.out.println("\nVocê tocou: " + musica.getTitulo() + "!");
        if (musica instanceof MusicaCanonica canonica) {
            if (canonica.isUsarHarpa()) {
                pausar(350);
                sintetizador.tocarMelodiaComHarpa(canonica);
                pausar(300);
                sintetizador.tocarMelodiaCompleta(canonica);
            } else {
                pausar(350);
                sintetizador.tocarMelodiaCompleta(canonica);
                if (canonica.temContinuacao()) {
                    sintetizador.tocarSequenciaMidi(canonica);
                }
            }
            canonica.executarEfeito();
        } else {
            pausar(300);
            sintetizador.tocarMelodiaCompleta(musica);
            musica.executarEfeito();
        }
        limparHistorico();
    }

    public void tocarMusicaDireto(Musica musica) {
        System.out.println("\nTocando: " + musica.getTitulo());
        if (musica instanceof MusicaCanonica canonica && canonica.isUsarHarpa()) {
            sintetizador.tocarMelodiaCompleta(canonica);
            pausar(300);
            sintetizador.tocarMelodiaComHarpa(canonica);
            pausar(300);
            sintetizador.tocarMelodiaCompleta(canonica);
        } else {
            sintetizador.tocarMelodiaCompleta(musica);
            if (musica instanceof MusicaCanonica canonica && canonica.temContinuacao()) {
                sintetizador.tocarSequenciaMidi(canonica);
            }
        }
        musica.executarEfeito();
    }

    public List<NotaMusical> getHistoricoNotas() {
        return Collections.unmodifiableList(historicoNotas);
    }

    public void limparHistorico() {
        historicoNotas.clear();
    }

    private void pausar(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}