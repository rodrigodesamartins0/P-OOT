package app;

import model.Musica;
import model.MusicaCustomizada;
import model.NotaMusical;
import model.Ocarina;
import model.SintetizadorOcarina;
import persistence.RepositorioMusicas;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Main {
    private static final String ARQUIVO_MUSICAS = "musicas.txt";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        SintetizadorOcarina sintetizador = new SintetizadorOcarina();
        Ocarina ocarina = new Ocarina(sintetizador);
        RepositorioMusicas repositorio = new RepositorioMusicas(ARQUIVO_MUSICAS);
        List<Musica> repertorio = repositorio.carregarMusicas();

        int opcao = -1;
        while (opcao != 0) {
            exibirMenuPrincipal();
            String entrada = scanner.nextLine().trim();

            try {
                opcao = Integer.parseInt(entrada);
            } catch (NumberFormatException e) {
                System.out.println("Opção inválida. Digite apenas o número desejado.");
                continue;
            }

            switch (opcao) {
                case 1 -> modoTocarOcarina(scanner, ocarina, repertorio);
                case 2 -> abrirGrimorio(scanner, ocarina, repertorio);
                case 3 -> comporMusicaAutoral(scanner, ocarina, repositorio, repertorio);
                case 4 -> criarCancaoDoEspantalho(scanner, ocarina, repositorio, repertorio);
                case 5 -> gerenciarMusicasAutorais(scanner, ocarina, repositorio, repertorio);
                case 0 -> System.out.println("Guardando a Ocarina do Tempo. Até logo!");
                default -> System.out.println("Opção inexistente.");
            }
        }

        sintetizador.encerrar();
        scanner.close();
    }

    private static void exibirMenuPrincipal() {
        System.out.println("\n==========================================");
        System.out.println("        OCARINA ORIENTADA A OBJETOS         ");
        System.out.println("==========================================");
        System.out.println("1. Tocar ocarina (Livre com reconhecimento)");
        System.out.println("2. Conferir partitura (Listar e tocar músicas)");
        System.out.println("3. Compor música autoral");
        System.out.println("4. Criar Canção do Espantalho (8 notas)");
        System.out.println("5. Editar ou excluir música autoral");
        System.out.println("0. Sair");
        System.out.print("Escolha uma opção: ");
    }

    private static void modoTocarOcarina(Scanner scanner, Ocarina ocarina, List<Musica> repertorio) {
        ocarina.limparHistorico();
        System.out.println("\n--- MODO OCARINA ---");
        System.out.println("Botões: [L] [R] [A] [Y] [X]");
        System.out.println("Digite uma nota ou sequência (ex: Y X A Y X A ou YXAYXA).");
        System.out.println("Comandos: 1 - Limpar notas | 0 - Voltar ao menu principal");

        while (true) {
            System.out.println("\nNotas na memória: " + formatarHistorico(ocarina.getHistoricoNotas()));
            System.out.print("Toque (ou 0 para voltar) > ");
            String entrada = scanner.nextLine().trim();

            if (entrada.equals("0") || entrada.equalsIgnoreCase("VOLTAR")) {
                break;
            }
            if (entrada.equals("1") || entrada.equalsIgnoreCase("LIMPAR")) {
                ocarina.limparHistorico();
                System.out.println("Histórico de notas limpo.");
                continue;
            }

            List<NotaMusical> notasDigitadas = parsearSequencia(entrada);
            if (notasDigitadas.isEmpty()) {
                System.out.println("Entrada inválida. Use L, R, A, Y, X, 1 para limpar ou 0 para voltar.");
                continue;
            }

            for (NotaMusical nota : notasDigitadas) {
                System.out.print(nota.getSimbolo() + " ");
                ocarina.tocarNota(nota);

                Musica reconhecida = ocarina.verificarReconhecimento(repertorio);
                if (reconhecida != null) {
                    System.out.println();
                    ocarina.reproduzirMusicaReconhecida(reconhecida);
                    break;
                }
            }
            System.out.println();
        }
    }

    private static void abrirGrimorio(Scanner scanner, Ocarina ocarina, List<Musica> repertorio) {
        if (repertorio.isEmpty()) {
            System.out.println("\nNenhuma música cadastrada no grimório.");
            return;
        }

        System.out.println("\n--- GRIMÓRIO DE MELODIAS ---");
        for (int i = 0; i < repertorio.size(); i++) {
            System.out.println((i + 1) + ". " + repertorio.get(i));
        }

        System.out.print("\nDigite o número da música para ouvi-la (ou 0 para voltar): ");
        try {
            int escolha = Integer.parseInt(scanner.nextLine().trim());
            if (escolha == 0) {
                return;
            }
            if (escolha >= 1 && escolha <= repertorio.size()) {
                ocarina.tocarMusicaDireto(repertorio.get(escolha - 1));
            } else {
                System.out.println("Número inválido.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Digite apenas números.");
        }
    }

    private static void comporMusicaAutoral(Scanner scanner, Ocarina ocarina, RepositorioMusicas repositorio, List<Musica> repertorio) {
        System.out.println("\n--- COMPOR MÚSICA AUTORAL (digite 0 para voltar) ---");
        System.out.print("Título da música: ");
        String titulo = scanner.nextLine().trim();
        if (titulo.equals("0")) {
            return;
        }
        if (titulo.isBlank()) {
            System.out.println("O título não pode ficar vazio.");
            return;
        }

        System.out.print("Nome do compositor: ");
        String compositor = scanner.nextLine().trim();
        if (compositor.equals("0")) {
            return;
        }
        if (compositor.isBlank()) {
            compositor = "Link";
        }

        System.out.print("Digite a sequência de notas (botões L, R, A, Y, X): ");
        String entradaNotas = scanner.nextLine().trim();
        if (entradaNotas.equals("0")) {
            return;
        }

        List<NotaMusical> notas = parsearSequencia(entradaNotas);
        if (notas.size() < 3) {
            System.out.println("A composição precisa ter pelo menos 3 notas válidas.");
            return;
        }

        if (ocarina.verificarConflitoMusicaAutoral(notas, repertorio, null)) {
            System.out.println("\n\u001B[38;2;95;225;249mOuve só! Ei Link! Essa música é parecida demais\ncom as que já possuímos!\u001B[0m");
            return;
        }

        MusicaCustomizada novaMusica = new MusicaCustomizada(titulo, notas, compositor);
        repertorio.add(novaMusica);
        repositorio.salvarMusicas(repertorio);
        System.out.println("Música '" + titulo + "' salva no grimório com sucesso!");
    }

    private static void criarCancaoDoEspantalho(Scanner scanner, Ocarina ocarina, RepositorioMusicas repositorio, List<Musica> repertorio) {
        System.out.println("\n--- CRIAR CANÇÃO DO ESPANTALHO (digite 0 para voltar) ---");
        System.out.println("Bonooru está ouvindo! Toque uma sequência de exatamente 8 notas (L, R, A, Y, X).");
        System.out.print("Sequência de 8 notas: ");
        String entrada = scanner.nextLine().trim();
        if (entrada.equals("0")) {
            return;
        }

        List<NotaMusical> notas = parsearSequencia(entrada);
        if (notas.size() != 8) {
            System.out.println("A Canção do Espantalho exige exatamente 8 notas válidas. Você informou " + notas.size() + ".");
            return;
        }

        if (ocarina.verificarConflitoEspantalho(notas, repertorio)) {
            System.out.println("\n\u001B[38;2;186;161;120mEi meu considerado! Eu já conheço essa aí!\nMe dá uma bem original!\u001B[0m");
            return;
        }

        repositorio.registrarCancaoDoEspantalho(repertorio, notas);
        System.out.println("Bonooru memorizou sua melodia! Quando você tocá-la, Pierre responderá ao seu chamado.");
    }

    private static void gerenciarMusicasAutorais(Scanner scanner, Ocarina ocarina, RepositorioMusicas repositorio, List<Musica> repertorio) {
        List<MusicaCustomizada> autorais = new ArrayList<>();
        for (Musica musica : repertorio) {
            if (musica instanceof MusicaCustomizada customizada) {
                autorais.add(customizada);
            }
        }

        if (autorais.isEmpty()) {
            System.out.println("\nVocê ainda não possui músicas autorais cadastradas.");
            return;
        }

        System.out.println("\n--- SUAS MÚSICAS AUTORAIS ---");
        for (int i = 0; i < autorais.size(); i++) {
            System.out.println((i + 1) + ". " + autorais.get(i));
        }

        System.out.print("\nEscolha o número da música (ou 0 para voltar): ");
        int indice;
        try {
            indice = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Entrada inválida.");
            return;
        }

        if (indice <= 0 || indice > autorais.size()) {
            return;
        }

        MusicaCustomizada selecionada = autorais.get(indice - 1);
        System.out.println("1. Editar notas da música");
        System.out.println("2. Excluir música");
        System.out.println("0. Voltar");
        System.out.print("O que deseja fazer? ");
        String acao = scanner.nextLine().trim();

        if ("1".equals(acao)) {
            System.out.print("Nova sequência de notas (L, R, A, Y, X): ");
            List<NotaMusical> novasNotas = parsearSequencia(scanner.nextLine());
            if (novasNotas.size() < 3) {
                System.out.println("Sequência inválida. A música precisa de pelo menos 3 notas.");
                return;
            }
            if (ocarina.verificarConflitoMusicaAutoral(novasNotas, repertorio, selecionada)) {
                System.out.println("\n\u001B[38;2;95;225;249mOuve só! Ei Link! Essa música é parecida demais\ncom as que já possuímos!\u001B[0m");
                return;
            }
            selecionada.setNotas(novasNotas);
            repositorio.salvarMusicas(repertorio);
            System.out.println("Notas atualizadas e salvas no arquivo!");
        } else if ("2".equals(acao)) {
            repertorio.remove(selecionada);
            repositorio.salvarMusicas(repertorio);
            System.out.println("Música removida com sucesso.");
        }
    }

    private static List<NotaMusical> parsearSequencia(String entrada) {
        List<NotaMusical> notas = new ArrayList<>();
        if (entrada == null || entrada.isBlank()) {
            return notas;
        }

        String texto = entrada.trim();
        if (texto.contains(" ")) {
            for (String token : texto.split("\\s+")) {
                NotaMusical nota = NotaMusical.fromEntrada(token);
                if (nota == null) {
                    return new ArrayList<>();
                }
                notas.add(nota);
            }
        } else {
            for (char c : texto.toCharArray()) {
                NotaMusical nota = NotaMusical.fromEntrada(String.valueOf(c));
                if (nota == null) {
                    return new ArrayList<>();
                }
                notas.add(nota);
            }
        }
        return notas;
    }

    private static String formatarHistorico(List<NotaMusical> historico) {
        if (historico.isEmpty()) {
            return "(nenhuma)";
        }
        return historico.stream()
                .map(NotaMusical::getSimbolo)
                .collect(Collectors.joining(" "));
    }
}