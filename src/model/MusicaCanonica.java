package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class MusicaCanonica extends Musica {
    private final String efeitoMagico;
    private final List<Integer> continuacaoMidi;
    private final boolean usarHarpa;
    private final List<Integer> duracoesNotas;
    private final List<Integer> duracoesContinuacao;

    public MusicaCanonica(String titulo, List<NotaMusical> notas, String efeitoMagico, List<Integer> continuacaoMidi, boolean usarHarpa) {
        this(titulo, notas, efeitoMagico, continuacaoMidi, usarHarpa, List.of(), List.of());
    }

    public MusicaCanonica(
            String titulo,
            List<NotaMusical> notas,
            String efeitoMagico,
            List<Integer> continuacaoMidi,
            boolean usarHarpa,
            List<Integer> duracoesNotas,
            List<Integer> duracoesContinuacao
    ) {
        super(titulo, notas);
        this.efeitoMagico = efeitoMagico;
        this.continuacaoMidi = continuacaoMidi == null ? new ArrayList<>() : new ArrayList<>(continuacaoMidi);
        this.usarHarpa = usarHarpa;
        this.duracoesNotas = duracoesNotas == null ? new ArrayList<>() : new ArrayList<>(duracoesNotas);
        this.duracoesContinuacao = duracoesContinuacao == null ? new ArrayList<>() : new ArrayList<>(duracoesContinuacao);
    }

    public String getEfeitoMagico() {
        return efeitoMagico;
    }

    public List<Integer> getContinuacaoMidi() {
        return Collections.unmodifiableList(continuacaoMidi);
    }

    public boolean isUsarHarpa() {
        return usarHarpa;
    }

    public boolean temContinuacao() {
        return !continuacaoMidi.isEmpty();
    }

    @Override
    public int getDuracaoNota(int indice) {
        if (indice >= 0 && indice < duracoesNotas.size()) {
            return duracoesNotas.get(indice);
        }
        return super.getDuracaoNota(indice);
    }

    public int getDuracaoContinuacao(int indice) {
        if (indice >= 0 && indice < duracoesContinuacao.size()) {
            return duracoesContinuacao.get(indice);
        }
        if (!continuacaoMidi.isEmpty() && indice == continuacaoMidi.size() - 1) {
            return 550;
        }
        return 330;
    }

    public String getContinuacaoParaArquivo() {
        return formatarListaInteiros(continuacaoMidi);
    }

    public String getDuracoesNotasParaArquivo() {
        return formatarListaInteiros(duracoesNotas);
    }

    public String getDuracoesContinuacaoParaArquivo() {
        return formatarListaInteiros(duracoesContinuacao);
    }

    private String formatarListaInteiros(List<Integer> lista) {
        if (lista.isEmpty()) {
            return "NONE";
        }
        return lista.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
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