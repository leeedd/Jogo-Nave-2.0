package Jogo;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;

/**
 * Hangar clandestino: um contato do submundo, nos fundos da base. Aparece uma vez depois de um
 * contrato dourado cumprido, so para pilotos VETERANO. E aqui que o jogador descobre o que as
 * reliquias douradas fazem: cada uma vira o passe do show da banda no festival do fim da jornada.
 */
final class HangarClandestino {
    private HangarClandestino() { }

    /** Bandas que tem reliquia (1..7): a Lula nao tem, o show dela e sempre aberto. */
    static final int PRIMEIRA = 1, ULTIMA = 7;

    static void desenhar(Graphics2D g, int cursor, String aviso, double tempo) {
        g.setColor(new Color(6, 3, 8, 250)); g.fillRect(0, 0, 960, 600);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        // luz de lampada balancando sobre a mesa do contato
        int lx = 480 + (int) (Math.sin(tempo * 1.3) * 30);
        g.setColor(new Color(255, 190, 90, 26)); g.fillPolygon(new int[]{lx, lx - 330, lx + 330}, new int[]{0, 600, 600}, 3);
        texto(g, "HANGAR CLANDESTINO", 40, 52, 26, new Color(255, 196, 70));
        texto(g, "\"Reliquia dourada, ne? Sei bem o que isso abre, cacador.\"", 40, 82, 14, new Color(210, 190, 160));
        texto(g, "\"No fim da estrada tem um festival. Cada reliquia vale o passe de um palco.\"", 40, 102, 14, new Color(210, 190, 160));
        for (int b = PRIMEIRA; b <= ULTIMA; b++) {
            int i = b - PRIMEIRA, y = 130 + i * 56; boolean sel = b == cursor;
            int frag = ProgressoJogo.fragmentosBanda(b), n = Turne.integrantes(b);
            boolean reliquia = ProgressoJogo.temReliquiaBanda(b), passe = ProgressoJogo.temPasse(b);
            g.setColor(sel ? new Color(60, 40, 20, 220) : new Color(18, 12, 16, 200)); g.fillRect(40, y, 880, 48);
            g.setColor(sel ? new Color(255, 196, 70) : new Color(90, 70, 50)); g.drawRect(40, y, 880, 48);
            Image e = CamisaShow.emblema(b);
            if (e != null) { double s = Math.min(56.0 / e.getWidth(null), 42.0 / e.getHeight(null)); int ew = (int) (e.getWidth(null) * s), eh = (int) (e.getHeight(null) * s); g.drawImage(e, 74 - ew / 2, y + 24 - eh / 2, ew, eh, null); }
            texto(g, Fase.BANDAS[b], 120, y + 21, 16, Color.WHITE);
            // pedacos da reliquia: um por integrante
            for (int k = 0; k < n; k++) { g.setColor(k < frag ? new Color(255, 200, 60) : new Color(60, 50, 40)); g.fillRect(120 + k * 22, y + 30, 16, 10); }
            String st = passe ? "PASSE NA MAO" : reliquia ? "ENTER: TROCAR PELO PASSE" : frag > 0 ? "RELIQUIA INCOMPLETA " + frag + "/" + n : "SEM RELIQUIA";
            texto(g, st, 900 - largura(g, st, 14), y + 30, 14, passe ? new Color(120, 230, 140) : reliquia ? new Color(255, 196, 70) : new Color(150, 130, 120));
        }
        if (aviso != null && !aviso.isEmpty()) texto(g, aviso, 480 - largura(g, aviso, 14) / 2, 538, 14, new Color(255, 196, 70));
        texto(g, "SETAS: ESCOLHER     ENTER: TROCAR     ESC: SAIR (o contato some quando voce partir)", 40, 580, 12, new Color(160, 140, 120));
    }

    private static int largura(Graphics2D g, String s, int t) { g.setFont(new Font(Font.MONOSPACED, Font.BOLD, t)); return g.getFontMetrics().stringWidth(s); }
    private static void texto(Graphics2D g, String s, int x, int y, int t, Color c) { g.setFont(new Font(Font.MONOSPACED, Font.BOLD, t)); g.setColor(c); g.drawString(s, x, y); }
}
