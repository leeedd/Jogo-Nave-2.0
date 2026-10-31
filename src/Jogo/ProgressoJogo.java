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
    // ---- patente do piloto e frota liberada por contrato ----
    private static final String XP = "metalspace_xp_piloto", FROTA = "metalspace_frota_liberada";
    static int xp() { try { return Math.max(0, dados().getInt(XP, 0)); } catch (SecurityException ex) { return 0; } }
    static int patente() { return Contrato.patente(xp()); }
    /** Soma XP; devolve true se o piloto subiu de patente. */
    static boolean ganharXP(int n) {
        if (n <= 0) return false; int antes = patente();
        try { dados().putInt(XP, xp() + n); } catch (SecurityException ex) { System.err.println("XP nao gravado"); }
        return patente() > antes;
    }
    /**
     * Naves liberadas. Na primeira vez que o sistema de contratos roda, grava as naves que o
     * jogador ja tinha pela regra antiga (so trofeus): ninguem perde nave que ja era sua.
     */
    private static int frota() {
        try {
            int f = dados().getInt(FROTA, -1);
            if (f < 0) { f = 1; int n = totalTrofeus(); if (n >= 1) f |= 2; if (n >= 3) f |= 4; if (n >= 5) f |= 8; if (lendariaDisponivel()) f |= 16; dados().putInt(FROTA, f); }
            return f;
        } catch (SecurityException ex) { return 1; }
    }
    static boolean naveLiberada(int m) { return m == 0 || (frota() & (1 << m)) != 0; }
    static void liberarNave(int m) {
        if (m < 0 || m > 4) return;
        try { dados().putInt(FROTA, frota() | (1 << m)); } catch (SecurityException ex) { System.err.println("Nave nao gravada"); }
        if (m == 4) desbloquearLendaria();
    }
    /** Trofeus e patente suficientes: o contrato especial da nave aparece no quadro. */
    static boolean podeTentarNave(int m) {
        return m >= 1 && m <= 4 && !naveLiberada(m) && totalTrofeus() >= Contrato.TROFEUS_NAVE[m] && patente() >= Contrato.PATENTE_NAVE[m];
    }
    /** O que falta para liberar a nave (texto curto), ou vazio se ja esta liberada. */
    static String requisito(int m) {
        if (naveLiberada(m)) return "";
        StringBuilder b = new StringBuilder();
        if (totalTrofeus() < Contrato.TROFEUS_NAVE[m]) b.append(Contrato.TROFEUS_NAVE[m]).append(" TROFEUS  ");
        if (patente() < Contrato.PATENTE_NAVE[m]) b.append("PATENTE ").append(Contrato.PATENTES[Contrato.PATENTE_NAVE[m]]).append("  ");
        b.append("CONTRATO ").append(Contrato.CONTRATO_NAVE[m]);
        return b.toString().trim();
    }
    public static boolean modeloDisponivel(int modelo) { return modelo >= 0 && modelo <= 4 && naveLiberada(modelo); }
    /** Regra antiga, so por trofeus (mantida para referencia e migracao). */
    static boolean modeloPorTrofeus(int modelo) {
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
