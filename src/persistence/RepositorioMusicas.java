package persistence;

import model.Musica;
import model.MusicaCanonica;
import model.MusicaCustomizada;
import model.NotaMusical;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RepositorioMusicas {
    private final Path caminhoArquivo;

    public RepositorioMusicas(String nomeArquivo) {
        this.caminhoArquivo = Paths.get(nomeArquivo);
    }

    public List<Musica> carregarMusicas() {
        List<Musica> musicas = new ArrayList<>();

        if (!Files.exists(caminhoArquivo)) {
            musicas.addAll(criarMusicasCanonicasPadrao());
            salvarMusicas(musicas);
            return musicas;
        }

        try (BufferedReader reader = Files.newBufferedReader(caminhoArquivo, StandardCharsets.UTF_8)) {
            String linha;
            while ((linha = reader.readLine()) != null) {
                if (linha.isBlank()) {
                    continue;
                }
                String[] partes = linha.split(";", 8);
                if (partes.length < 4) {
                    continue;
                }

                String tipo = partes[0].trim();
                String titulo = partes[1].trim();
                List<NotaMusical> notas = converterNotas(partes[2].trim());
                String extra = decodificarTextoArquivo(partes[3].trim());
                List<Integer> continuacao = partes.length >= 5 ? converterListaInteiros(partes[4].trim()) : List.of();
                boolean usarHarpa = partes.length >= 6 && Boolean.parseBoolean(partes[5].trim());
                List<Integer> duracoesNotas = partes.length >= 7 ? converterListaInteiros(partes[6].trim()) : List.of();
                List<Integer> duracoesContinuacao = partes.length == 8 ? converterListaInteiros(partes[7].trim()) : List.of();

                if (notas.isEmpty()) {
                    continue;
                }

                if ("CANÔNICA".equalsIgnoreCase(tipo) || "CANONICA".equalsIgnoreCase(tipo)) {
                    musicas.add(new MusicaCanonica(titulo, notas, extra, continuacao, usarHarpa, duracoesNotas, duracoesContinuacao));
                } else if ("AUTORAL".equalsIgnoreCase(tipo)) {
                    musicas.add(new MusicaCustomizada(titulo, notas, extra));
                }
            }
        } catch (IOException e) {
            System.out.println("Erro ao ler o arquivo de músicas: " + e.getMessage());
        }

        return musicas;
    }

    public void salvarMusicas(List<Musica> musicas) {
        try (BufferedWriter writer = Files.newBufferedWriter(caminhoArquivo, StandardCharsets.UTF_8)) {
            for (Musica musica : musicas) {
                String extra = "";
                String continuacao = "NONE";
                String usarHarpa = "false";
                String duracoesNotas = "NONE";
                String duracoesContinuacao = "NONE";

                if (musica instanceof MusicaCanonica canonica) {
                    extra = codificarTextoArquivo(canonica.getEfeitoMagico());
                    continuacao = canonica.getContinuacaoParaArquivo();
                    usarHarpa = String.valueOf(canonica.isUsarHarpa());
                    duracoesNotas = canonica.getDuracoesNotasParaArquivo();
                    duracoesContinuacao = canonica.getDuracoesContinuacaoParaArquivo();
                } else if (musica instanceof MusicaCustomizada customizada) {
                    extra = codificarTextoArquivo(customizada.getCompositor());
                }

                String linha = String.join(";",
                        musica.getTipo(),
                        musica.getTitulo().replace(";", ","),
                        musica.getSequenciaParaArquivo(),
                        extra,
                        continuacao,
                        usarHarpa,
                        duracoesNotas,
                        duracoesContinuacao
                );
                writer.write(linha);
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Erro ao salvar o arquivo de músicas: " + e.getMessage());
        }
    }

    public void registrarCancaoDoEspantalho(List<Musica> musicas, List<NotaMusical> notasEspantalho) {
        musicas.removeIf(m -> m.getTitulo().equalsIgnoreCase("Canção do Espantalho"));
        MusicaCanonica espantalho = new MusicaCanonica(
                "Canção do Espantalho",
                notasEspantalho,
                "\u001B[38;2;175;78;54mEi, fala sério! Ei! Eu ouvi de longe o seu ritmo hipnotizante.\n" +
                        "Esse som de estremecer mexe comigo! E onde estamos dessa vez?\n" +
                        "Caramba um projeto de Programação Orientada a Objetos?\n" +
                        "Espero que vá bem meu amigo!\u001B[0m",
                List.of(),
                false,
                List.of(),
                List.of()
        );
        musicas.add(espantalho);
        salvarMusicas(musicas);
    }

    private String codificarTextoArquivo(String texto) {
        return texto.replace("\n", "\\n")
                .replace("\u001B[38;2;220;233;85m", "<COR_SARIA>")
                .replace("\u001B[38;2;175;78;54m", "<COR_PIERRE>")
                .replace("\u001B[0m", "<COR_RESET>")
                .replace(";", ",");
    }

    private String decodificarTextoArquivo(String texto) {
        return texto.replace("\\n", "\n")
                .replace("<COR_SARIA>", "\u001B[38;2;220;233;85m")
                .replace("<COR_PIERRE>", "\u001B[38;2;175;78;54m")
                .replace("<COR_RESET>", "\u001B[0m");
    }

    private List<NotaMusical> converterNotas(String sequenciaTexto) {
        List<NotaMusical> notas = new ArrayList<>();
        for (String token : sequenciaTexto.split(",")) {
            NotaMusical nota = NotaMusical.fromEntrada(token);
            if (nota != null) {
                notas.add(nota);
            }
        }
        return notas;
    }

    private List<Integer> converterListaInteiros(String texto) {
        List<Integer> lista = new ArrayList<>();
        if (texto == null || texto.isBlank() || "NONE".equalsIgnoreCase(texto)) {
            return lista;
        }
        for (String token : texto.split(",")) {
            try {
                lista.add(Integer.parseInt(token.trim()));
            } catch (NumberFormatException ignored) {
            }
        }
        return lista;
    }

    private List<Musica> criarMusicasCanonicasPadrao() {
        return List.of(
                new MusicaCanonica(
                        "Acalanto da Zelda",
                        Arrays.asList(NotaMusical.Y, NotaMusical.X, NotaMusical.A, NotaMusical.Y, NotaMusical.X, NotaMusical.A),
                        "O brasão da Família Real respondeu à melodia.",
                        List.of(71, 74, 81, 79, 74),
                        false,
                        List.of(620, 320, 880, 620, 320, 950),
                        List.of(620, 320, 620, 320, 950)
                ),
                new MusicaCanonica(
                        "Canção do Tempo",
                        Arrays.asList(NotaMusical.A, NotaMusical.L, NotaMusical.R, NotaMusical.A, NotaMusical.L, NotaMusical.R),
                        "A Porta do Tempo ressoou diante de você.",
                        List.of(69, 72, 71, 67, 65, 67, 69, 62, 60, 64, 62),
                        false,
                        List.of(380, 740, 380, 380, 740, 420),
                        List.of(220, 220, 400, 400, 220, 220, 420, 420, 220, 220, 820)
                ),
                new MusicaCanonica(
                        "Canção do Sol",
                        Arrays.asList(NotaMusical.A, NotaMusical.R, NotaMusical.X, NotaMusical.A, NotaMusical.R, NotaMusical.X),
                        "O sol atravessou o céu e o tempo avançou entre dia e noite.",
                        List.of(76, 77, 79),
                        false,
                        List.of(210, 210, 640, 210, 210, 640),
                        List.of(190, 190, 750)
                ),
                new MusicaCanonica(
                        "Trova da Tempestade",
                        Arrays.asList(NotaMusical.L, NotaMusical.R, NotaMusical.X, NotaMusical.L, NotaMusical.R, NotaMusical.X),
                        "Nuvens escuras se formaram e uma tempestade começou a cair.",
                        List.of(76, 77, 76, 77, 76, 72, 69),
                        false,
                        List.of(190, 190, 620, 190, 190, 620),
                        List.of(460, 180, 180, 180, 180, 180, 760)
                ),
                new MusicaCanonica(
                        "Canção da Epona",
                        Arrays.asList(NotaMusical.X, NotaMusical.Y, NotaMusical.A, NotaMusical.X, NotaMusical.Y, NotaMusical.A),
                        "Epona ouviu seu chamado ao longe e galopou até você.",
                        List.of(74, 71, 69, 71, 69),
                        false,
                        List.of(340, 340, 720, 340, 340, 720),
                        List.of(340, 340, 680, 360, 850)
                ),
                new MusicaCanonica(
                        "Canção da Saria",
                        Arrays.asList(NotaMusical.R, NotaMusical.A, NotaMusical.Y, NotaMusical.R, NotaMusical.A, NotaMusical.Y),
                        "\u001B[38;2;220;233;85mLink? É a Saria... Tá me ouvindo? Ei, onde você está agora?\n" +
                                "Ah sim, então você está em um projeto de Programação\n" +
                                "Orientada a Objetos? Que legal! Espero que tire uma nota boa! :)\u001B[0m",
                        List.of(65, 69, 71, 76, 74, 71, 72, 71, 67, 64),
                        false,
                        List.of(200, 200, 420, 200, 200, 420),
                        List.of(200, 200, 200, 200, 400, 200, 200, 200, 200, 680)
                ),
                new MusicaCanonica(
                        "Minueto da Floresta",
                        Arrays.asList(NotaMusical.L, NotaMusical.X, NotaMusical.Y, NotaMusical.A, NotaMusical.Y, NotaMusical.A),
                        "Quer se teletransportar à Clareira Sagrada?",
                        List.of(),
                        true,
                        List.of(300, 300, 860, 300, 300, 1050),
                        List.of()
                ),
                new MusicaCanonica(
                        "Bolero do Fogo",
                        Arrays.asList(NotaMusical.R, NotaMusical.L, NotaMusical.R, NotaMusical.L, NotaMusical.A, NotaMusical.R, NotaMusical.A, NotaMusical.R),
                        "Quer se teletransportar à cratera da Montanha da Morte",
                        List.of(),
                        true,
                        List.of(),
                        List.of()
                ),
                new MusicaCanonica(
                        "Serenata das Águas",
                        Arrays.asList(NotaMusical.L, NotaMusical.R, NotaMusical.A, NotaMusical.A, NotaMusical.Y),
                        "Quer se teletransportar ao Lago Hylia?",
                        List.of(),
                        true,
                        List.of(380, 380, 380, 380, 880),
                        List.of()
                ),
                new MusicaCanonica(
                        "Réquiem dos Espíritos",
                        Arrays.asList(NotaMusical.L, NotaMusical.R, NotaMusical.L, NotaMusical.A, NotaMusical.R, NotaMusical.L),
                        "Quer se teletransportar ao Colosso do Deserto?",
                        List.of(),
                        true,
                        List.of(640, 340, 340, 640, 340, 920),
                        List.of()
                ),
                new MusicaCanonica(
                        "Noturno das Sombras",
                        Arrays.asList(NotaMusical.Y, NotaMusical.A, NotaMusical.A, NotaMusical.L, NotaMusical.Y, NotaMusical.A, NotaMusical.R),
                        "Quer se teletransportar ao cemitério?",
                        List.of(),
                        true,
                        List.of(580, 560, 280, 280, 280, 280, 880),
                        List.of()
                ),
                new MusicaCanonica(
                        "Prelúdio da Luz",
                        Arrays.asList(NotaMusical.X, NotaMusical.A, NotaMusical.X, NotaMusical.A, NotaMusical.Y, NotaMusical.X),
                        "Quer se teletransportar ao Templo do Tempo?",
                        List.of(),
                        true,
                        List.of(320, 620, 320, 320, 320, 860),
                        List.of()
                )
        );
    }
}