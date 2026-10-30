package Jogo;

import java.util.prefs.Preferences;

/** Progresso real e persistido: setores, insignias e modelos da frota. */
public final class ProgressoJogo {
    private static final String CHAVE = "metalspace_nave_lendaria";
    private static final String MEDALHAS = "metalspace_medalhas_8";
    private ProgressoJogo() { }
    private static Preferences dados() { return Preferences.userNodeForPackage(ProgressoJogo.class); }
    public static boolean lendariaDisponivel() {
        try { return dados().getBoolean(CHAVE, false) || totalTrofeus() == 8; }
        catch (SecurityException ex) { return false; }
    }
    public static void desbloquearLendaria() {
        try { dados().putBoolean(CHAVE, true); }
        catch (SecurityException ex) { System.err.println("Conquista nao gravada: " + ex.getMessage()); }
    }
    public static void registrarTrofeu(int id) {
        if (id < 1 || id > 8) throw new IllegalArgumentException("trofeu fora da faixa");
        try { dados().putInt(MEDALHAS, dados().getInt(MEDALHAS, 0) | (1 << (id - 1))); }
        catch (SecurityException ex) { System.err.println("Trofeu nao gravado: " + ex.getMessage()); }
    }
    public static boolean temTrofeu(int id) {
        if (id < 1 || id > 8) return false;
        try { return (dados().getInt(MEDALHAS, 0) & (1 << (id - 1))) != 0; }
        catch (SecurityException ex) { return false; }
    }
    public static int totalTrofeus() {
        try { return Integer.bitCount(dados().getInt(MEDALHAS, 0) & 255); }
        catch (SecurityException ex) { return 0; }
    }
    public static boolean modeloDisponivel(int modelo) {
        int n = totalTrofeus();
        switch (modelo) {
            case 0: return true;
            case 1: return n >= 1;
            case 2: return n >= 3;
            case 3: return n >= 5;
            case 4: return lendariaDisponivel();
            default: return false;
        }
    }
}
