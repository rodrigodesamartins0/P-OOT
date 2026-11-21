package model;

public enum NotaMusical {
    L("L", "[L]", 62), // Ré grave (D4)
    R("R", "[R]", 65), // Fá (F4)
    Y("Y", "[Y]", 69), // Lá (A4)
    X("X", "[X]", 71), // Si (B4)
    A("A", "[A]", 74); // Ré agudo (D5)

    private final String tecla;
    private final String simbolo;
    private final int notaMidi;

    NotaMusical(String tecla, String simbolo, int notaMidi) {
        this.tecla = tecla;
        this.simbolo = simbolo;
        this.notaMidi = notaMidi;
    }

    public String getTecla() {
        return tecla;
    }

    public String getSimbolo() {
        return simbolo;
    }

    public int getNotaMidi() {
        return notaMidi;
    }

    public static NotaMusical fromEntrada(String entrada) {
        if (entrada == null) {
            return null;
        }
        return switch (entrada.trim().toUpperCase()) {
            case "L" -> L;
            case "R" -> R;
            case "Y" -> Y;
            case "X" -> X;
            case "A" -> A;
            default -> null;
        };
    }

    @Override
    public String toString() {
        return simbolo;
    }
}