package Jogo;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

/**
 * Base aliada no meio do setor: entra pela direita depois da sentinela, estaciona e espera a nave atracar.
 * Atracada, oferece reparo, mercado, hangar (so naves desbloqueadas) e grava o ponto de retomada.
 */
final class EstacaoAliada {
    private static final BufferedImage ARTE = ler("base_aliada.png");
    private static final double PARADA = 640, ESPERA = 14;
    private double x = 1000, y = 230, tempo, parada;
    private boolean saindo;

    void atualizar(double dt) {
        tempo += dt;
        if (saindo) { x += 160 * dt; return; }
        if (x > PARADA) x = Math.max(PARADA, x - 120 * dt);
        else { parada += dt; if (parada >= ESPERA) saindo = true; }
    }
    void partir() { saindo = true; }
    boolean saiu() { return saindo && x > 1000; }
    boolean aguardando() { return !saindo && x <= PARADA; }
    double restante() { return Math.max(0, ESPERA - parada); }
    /** Doca: a boca do hangar no lado esquerdo da estacao. */
    Rectangle doca() { return new Rectangle((int) x - 10, (int) y + 52, 60, 46); }

    void desenhar(Graphics2D original) {
        Graphics2D g = (Graphics2D) original.create();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        int bx = (int) x, by = (int) y + (int) Math.round(Math.sin(tempo * 1.3) * 3);
        if (ARTE != null) g.drawImage(ARTE, bx - 20, by - 20, 300, 190, null);
        else casco(g, bx, by);
        // luzes da doca piscando enquanto espera a nave
        if (aguardando()) {
            boolean liga = (int) (tempo * 4) % 2 == 0;
            for (int i = 0; i < 3; i++) {
                g.setColor(liga ? new Color(90, 255, 140) : new Color(30, 90, 50));
                g.fillRect(bx - 4 - i * 14, by + 74, 8, 4);
            }
            g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 13));
            g.setColor(new Color(130, 255, 160));
            g.drawString("BASE ALIADA - ATRAQUE NA DOCA (" + (int) Math.ceil(restante()) + "s)", bx - 150, by - 14);
        }
        g.dispose();
    }

    /** Desenho provisorio em pixel art: modulo central, antenas, paineis solares e a doca. */
    private void casco(Graphics2D g, int x, int y) {
        Color metal = new Color(60, 66, 82), escuro = new Color(30, 34, 46), claro = new Color(150, 160, 180), verde = new Color(80, 230, 130);
        // paineis solares
        for (int s = -1; s <= 1; s += 2) {
            int py = y + 75 + s * 70;
            g.setColor(escuro); g.fillRect(x + 110, py - 2, 8, 4 + Math.abs(s) * 0);
            g.setColor(new Color(30, 60, 120)); g.fillRect(x + 60, py - 22, 110, 40);
            g.setColor(new Color(70, 120, 200));
            for (int c = 0; c < 5; c++) g.drawLine(x + 60 + c * 22, py - 22, x + 60 + c * 22, py + 18);
            g.drawLine(x + 60, py - 2, x + 170, py - 2);
            g.setColor(claro); g.drawRect(x + 60, py - 22, 110, 40);
        }
        // modulo central
        g.setColor(metal); g.fillRoundRect(x + 20, y + 40, 210, 70, 18, 18);
        g.setColor(escuro); g.fillRect(x + 20, y + 70, 210, 10);
        g.setColor(claro); g.drawRoundRect(x + 20, y + 40, 210, 70, 18, 18);
        for (int i = 0; i < 6; i++) { g.setColor(new Color(255, 210, 110)); g.fillRect(x + 70 + i * 22, y + 52, 8, 6); }
        // torre e antena
        g.setColor(metal); g.fillRect(x + 150, y + 10, 34, 32);
        g.setColor(claro); g.drawRect(x + 150, y + 10, 34, 32); g.drawLine(x + 167, y + 10, x + 167, y - 12);
        g.setColor((int) (tempo * 2) % 2 == 0 ? Color.RED : new Color(90, 20, 20)); g.fillRect(x + 165, y - 15, 5, 5);
        // doca (boca do hangar)
        g.setColor(escuro); g.fillRect(x - 4, y + 54, 40, 42);
        g.setColor(claro); g.drawRect(x - 4, y + 54, 40, 42);
        g.setColor(verde); g.fillRect(x + 30, y + 60, 3, 30);
        g.setColor(new Color(255, 190, 60)); for (int i = 0; i < 5; i++) g.fillRect(x - 4 + i * 8, y + 52, 4, 3);
    }

    private static BufferedImage ler(String nome) {
        java.net.URL u = EstacaoAliada.class.getResource("/ImagemIcon/" + nome);
        if (u == null) return null;
        try { return javax.imageio.ImageIO.read(u); } catch (java.io.IOException e) { return null; }
    }

    // ---- menu da base ----
    static final String[] OPCOES = {"REPARAR CASCO", "MERCADO", "HANGAR", "PARTIR"};
    static final int CUSTO_REPARO = 30;

    static void desenharMenu(Graphics2D g, int cursor, Nave nave, ProgressaoNave inv, int modelo, String aviso, double tempo) {
        g.setColor(new Color(0, 0, 0, 190)); g.fillRect(0, 0, 960, 600);
        HudMetal.painel(g, 200, 120, 560, 360);
        g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 24)); g.setColor(new Color(130, 255, 160));
        g.drawString("BASE ALIADA", 236, 170);
        g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 13)); g.setColor(new Color(170, 190, 210));
        g.drawString("PONTO DE RETOMADA SALVO", 236, 192);
        g.setColor(new Color(110, 222, 245)); g.drawString("CR " + inv.saldo(), 640, 170);
        int falta = nave.getVidaMaxima() - nave.getVida();
        String[] detalhe = {
            falta == 0 ? "CASCO INTEIRO" : "CR " + falta * CUSTO_REPARO + " (" + falta + " PONTOS)",
            "PECAS PERMANENTES",
            Pausa099.NAVES[modelo] + (ProgressoJogo.modeloDisponivel(modelo) ? "" : "  [BLOQUEADA]"),
            "VOLTAR AO COMBATE"};
        for (int i = 0; i < OPCOES.length; i++) {
            int oy = 232 + i * 52; boolean sel = i == cursor;
            g.setColor(sel ? new Color(40, 70, 60) : new Color(14, 22, 34)); g.fillRect(236, oy - 26, 488, 42);
            g.setColor(sel ? new Color(255, 206, 98) : new Color(48, 76, 101)); g.drawRect(236, oy - 26, 488, 42);
            g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 18)); g.setColor(sel ? new Color(255, 206, 98) : Color.WHITE);
            g.drawString((sel ? "> " : "  ") + OPCOES[i], 250, oy);
            g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 13)); g.setColor(i == 0 && falta * CUSTO_REPARO > inv.saldo() && falta > 0 ? new Color(255, 90, 90) : new Color(170, 190, 210));
            g.drawString(detalhe[i], 470, oy);
        }
        { // previa da nave do hangar, dentro da propria linha, com setas para trocar
            int oy = 232 + 2 * 52;
            Graphics2D a = (Graphics2D) g.create(); a.clipRect(236, oy - 26, 488, 42);
            if (!ProgressoJogo.modeloDisponivel(modelo)) a.setComposite(java.awt.AlphaComposite.SrcOver.derive(.3f));
            ArteNaves.desenharJogador(a, nave.getNivel(), modelo, 650, oy - 25, tempo);
            a.dispose();
            if (cursor == 2) { g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 16)); g.setColor(new Color(255, 206, 98)); g.drawString("<", 600, oy); g.drawString(">", 712, oy); }
        }
        g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 12)); g.setColor(new Color(255, 206, 98));
        if (aviso != null && !aviso.isEmpty()) g.drawString(aviso, 236, 452);
        g.setColor(new Color(150, 160, 180)); g.drawString("SETAS: ESCOLHER   ENTER: CONFIRMAR   ESC: PARTIR", 236, 470);
    }
}
