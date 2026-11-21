package model;

public enum NotaMusical {
    L("L", "[L]", 62),
    R("R", "[R]", 65),
    A("A", "[A]", 69),
    Y("Y", "[Y]", 71),
    X("X", "[X]", 74);

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
            case "A" -> A;
            case "Y" -> Y;
            case "X" -> X;
            default -> null;
        };
    }

    @Override
    public String toString() {
        return simbolo;
    }
}