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
    private static final BufferedImage ARTE = ler("base_aliada.png"), INTERIOR = ler("base_interior.png"), LUZES = ler("base_doca_luzes.png");
    private static final double PARADA = 560, ESPERA = 14;
    static BufferedImage arte() { return ARTE; }
    /** Estacao vista de cima, animada (estacao_aliada_anim.png, 4 quadros): luzes do anel, doca e antena. */
    private static final BufferedImage ANIM = ler("estacao_aliada_anim.png");
    private static final BufferedImage[] QUADROS = new BufferedImage[4];
    static { if (ANIM != null) { int fw = ANIM.getWidth() / 4; for (int k = 0; k < 4; k++) QUADROS[k] = ANIM.getSubimage(k * fw, 0, fw, ANIM.getHeight()); } }
    /** Quadro da animacao no instante t (ou a arte antiga, se faltar a animada). */
    static BufferedImage quadro(double t) { return ANIM == null ? ARTE : QUADROS[(int) (t * 3.5) % 4]; }
    private double x = 1000, y = 200, tempo, parada;
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
    Rectangle doca() { return ARTE != null ? new Rectangle((int) x, (int) y + 78, 130, 70) : new Rectangle((int) x - 10, (int) y + 52, 60, 46); }

    void desenhar(Graphics2D original) {
        Graphics2D g = (Graphics2D) original.create();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        int bx = (int) x, by = (int) y + (int) Math.round(Math.sin(tempo * 1.3) * 3);
        if (ARTE != null) g.drawImage(ARTE, bx, by, null);
        else casco(g, bx, by);
        // luzes da doca: as tres acendem da direita para a esquerda, chamando a nave
        if (aguardando()) {
            if (LUZES != null) {
                int q = (int) (tempo * 4) % 4, fw = LUZES.getWidth() / 4;
                int lx = ARTE != null ? bx + 4 : bx - 50, ly = ARTE != null ? by + 56 : by + 66;
                g.drawImage(LUZES, lx, ly, lx + fw, ly + LUZES.getHeight(), q * fw, 0, q * fw + fw, LUZES.getHeight(), null);
            } else {
                boolean liga = (int) (tempo * 4) % 2 == 0;
                for (int i = 0; i < 3; i++) { g.setColor(liga ? new Color(90, 255, 140) : new Color(30, 90, 50)); g.fillRect(bx - 4 - i * 14, by + 74, 8, 4); }
            }
            g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 13));
            g.setColor(new Color(130, 255, 160));
            g.drawString("BASE ALIADA - ATRAQUE NA DOCA (" + (int) Math.ceil(restante()) + "s)", bx - 60, by + 8);
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
    private static final BufferedImage[] FRENTE = {ler("nave_0_frente.png"), ler("nave_1_frente.png"), ler("nave_2_frente.png"), ler("nave_3_frente.png"), ler("nave_4_frente.png")};
    static final String[] OPCOES = {"MISSOES", "REPARAR CASCO", "MERCADO", "PARTIR"};
    static final int CUSTO_REPARO = 30;

    static void desenharMenu(Graphics2D g, boolean menu, int cursor, Nave nave, ProgressaoNave inv, int modelo, String aviso, double tempo) { desenharMenu(g, menu, cursor, nave, inv, modelo, aviso, tempo, false); }
    /** Opcoes da base; com o hangar clandestino aberto, ele entra antes de PARTIR. */
    static String[] opcoes(boolean hangar) { return hangar ? new String[]{"MISSOES", "REPARAR CASCO", "MERCADO", "HANGAR CLANDESTINO", "PARTIR"} : OPCOES; }
    static void desenharMenu(Graphics2D g, boolean menu, int cursor, Nave nave, ProgressaoNave inv, int modelo, String aviso, double tempo, boolean hangar) {
        String[] OPCOES = opcoes(hangar);
        if (INTERIOR != null) g.drawImage(INTERIOR, 0, 0, null); else { g.setColor(new Color(0, 0, 0, 190)); g.fillRect(0, 0, 960, 600); }
        // nave estacionada no centro da plataforma
        Graphics2D a = (Graphics2D) g.create();
        a.setColor(new Color(0, 0, 0, 100)); a.fillOval(330, 420, 300, 46);
        BufferedImage frente = FRENTE[Math.max(0, Math.min(4, modelo))];
        if (frente != null) {
            int fh = 300, fw = frente.getWidth() * fh / frente.getHeight();
            a.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            a.drawImage(frente, 480 - fw / 2, 190, fw, fh, null);
        } else { a.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR); a.translate(370, 350); a.scale(2.6, 2.6); ArteNaves.desenharJogador(a, nave.getNivel(), modelo, 0, 0, tempo, 0, 0, 0, false); }
        a.dispose();
        g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 13)); g.setColor(new Color(110, 222, 245));
        String cr = "CR " + inv.saldo() + "   CASCO " + nave.getVida() + "/" + nave.getVidaMaxima();
        faixa(g, 0, 0, 960, 30); g.drawString("BASE ALIADA  /  PONTO DE RETOMADA SALVO", 16, 20); g.drawString(cr, 944 - g.getFontMetrics().stringWidth(cr), 20);
        if (!menu) {
            boolean pisca = (int) (tempo * 2) % 2 == 0;
            g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 34)); g.setColor(new Color(255, 206, 98, pisca ? 255 : 150));
            g.drawString("<", 250, 345); g.drawString(">", 690, 345);
            faixa(g, 0, 556, 960, 44);
            g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 14)); g.setColor(Color.WHITE);
            String dica = "< > TROCAR NAVE     ENTER: MENU DA BASE     ESC: PARTIR";
            g.drawString(dica, 480 - g.getFontMetrics().stringWidth(dica) / 2, 583);
            if (aviso != null && !aviso.isEmpty()) { g.setColor(new Color(255, 206, 98)); g.drawString(aviso, 480 - g.getFontMetrics().stringWidth(aviso) / 2, 545); }
            return;
        }
        // menu translucido por cima da nave
        Graphics2D m = (Graphics2D) g.create();
        Graphics2D fundo = (Graphics2D) m.create(); fundo.setComposite(java.awt.AlphaComposite.SrcOver.derive(.5f));
        HudMetal.painel(fundo, 290, hangar ? 120 : 150, 380, hangar ? 380 : 320); fundo.dispose();
        int falta = nave.getVidaMaxima() - nave.getVida();
        String[] detalhe = hangar ? new String[]{"CONTRATOS DO SETOR", falta == 0 ? "CASCO INTEIRO" : "CR " + falta * CUSTO_REPARO + " / " + falta + " PONTOS", "PECAS E MELHORIAS", "UM CONTATO NOS FUNDOS... SO HOJE", "PROXIMO PLANETA"}
                : new String[]{"CONTRATOS DO SETOR", falta == 0 ? "CASCO INTEIRO" : "CR " + falta * CUSTO_REPARO + " / " + falta + " PONTOS", "PECAS E MELHORIAS", "PROXIMO PLANETA"};
        for (int i = 0; i < OPCOES.length; i++) {
            int oy = (hangar ? 176 : 206) + i * 62; boolean sel = i == cursor;
            m.setColor(sel ? new Color(40, 90, 60, 150) : new Color(10, 16, 26, 95)); m.fillRect(312, oy - 28, 336, 50);
            m.setColor(sel ? new Color(255, 206, 98) : new Color(48, 76, 101)); m.drawRect(312, oy - 28, 336, 50);
            m.setFont(new Font(Font.MONOSPACED, Font.BOLD, 18)); m.setColor(sel ? new Color(255, 206, 98) : hangar && i == 3 ? new Color(255, 170, 60) : Color.WHITE); m.drawString((sel ? "> " : "  ") + OPCOES[i], 324, oy - 5);
            m.setFont(new Font(Font.MONOSPACED, Font.BOLD, 12));
            m.setColor(i == 1 && falta > 0 && falta * CUSTO_REPARO > inv.saldo() ? new Color(255, 90, 90) : new Color(170, 190, 210)); m.drawString(detalhe[i], 348, oy + 13);
        }
        m.setFont(new Font(Font.MONOSPACED, Font.BOLD, 12)); m.setColor(new Color(255, 206, 98));
        if (aviso != null && !aviso.isEmpty()) m.drawString(aviso, 480 - m.getFontMetrics().stringWidth(aviso) / 2, 452);
        m.dispose();
        faixa(g, 0, 556, 960, 44); g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 14)); g.setColor(Color.WHITE);
        String dica = "SETAS: ESCOLHER     ENTER: CONFIRMAR     ESC: FECHAR MENU";
        g.drawString(dica, 480 - g.getFontMetrics().stringWidth(dica) / 2, 583);
    }
    private static void faixa(Graphics2D g, int x, int y, int w, int h) { g.setColor(new Color(4, 6, 12, 190)); g.fillRect(x, y, w, h); g.setColor(new Color(110, 90, 50)); g.drawLine(x, y == 0 ? h - 1 : y, x + w, y == 0 ? h - 1 : y); }
}
