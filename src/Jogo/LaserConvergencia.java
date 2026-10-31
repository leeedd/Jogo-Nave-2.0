package Jogo;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Random;

/**
 * Laser de Convergencia: gelo e fogo fundidos num raio roxo eletrico que vaporiza a tela.
 * Com um poder de chefe equipado, o laser se funde com ele e vira outro disparo (tipo 0 a 7).
 */
final class LaserConvergencia {
    static final double DURACAO = 1.6;
    static final String[] NOMES = {"MARE ABISSAL", "LANCA PAINKILLER", "LAMINAS DO EDDIE", "LEQUE DE ASES",
            "TEMPESTADE DE RAIOS", "PENTAGRAMA INFERNAL", "NATIVE BLOOD", "ECLIPSE DO PRINCIPE"};
    static final String[] EFEITO_CHEFE = {"Congela o chefe", "Dano dobrado no chefe", "Corta todos os tiros", "Quatro feixes",
            "Paralisa o chefe", "Queima o chefe", "Aguia de trovao", "Rouba vida: +3 casco"};
    /** Dano no chefe/mini-chefe por tipo (-1 = laser base). */
    static int danoChefe(int tipo) { return tipo < 0 ? 40 : new int[]{40, 80, 50, 60, 50, 50, 65, 45}[tipo]; }

    /** Arte em pixel art de cada disparo (tira de 4 quadros): fusao_base.png e fusao_0..7.png. */
    private static final java.awt.image.BufferedImage[] ARTE = new java.awt.image.BufferedImage[9];
    static { for (int i = -1; i < 8; i++) { java.net.URL u = LaserConvergencia.class.getResource("/ImagemIcon/fusao_" + (i < 0 ? "base" : String.valueOf(i)) + ".png");
        if (u != null) try { ARTE[i + 1] = javax.imageio.ImageIO.read(u); } catch (java.io.IOException e) { System.err.println("Fusao ilegivel: " + i); } } }
    /** Feixes esticam ate a borda; os demais sao projeteis que atravessam a tela. */
    /** Arte opcional so da carga: carga_base.png / carga_0..7.png (4 quadros); carga_base serve para todas. */
    private static final java.awt.image.BufferedImage[] CARGA = new java.awt.image.BufferedImage[9];
    static { for (int i = -1; i < 8; i++) { java.net.URL u = LaserConvergencia.class.getResource("/ImagemIcon/carga_" + (i < 0 ? "base" : String.valueOf(i)) + ".png");
        if (u != null) try { CARGA[i + 1] = javax.imageio.ImageIO.read(u); } catch (java.io.IOException e) { System.err.println("Carga ilegivel: " + i); } } }
    private static boolean feixe(int tipo) { return tipo < 0 || tipo == 0 || tipo == 1 || tipo == 4; }

    private boolean desenharArte(Graphics2D g, int ox, int cy, double t, double forca) {
        java.awt.image.BufferedImage a = ARTE[tipo + 1]; if (a == null) return false;
        int fw = a.getWidth() / 4, fh = a.getHeight();
        int k = t < .14 ? 0 : t < .2 ? 1 : 2 + (int) (tempo * 12) % 2;
        Graphics2D q = (Graphics2D) g.create();
        q.setRenderingHint(java.awt.RenderingHints.KEY_INTERPOLATION, java.awt.RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        q.setComposite(AlphaComposite.SrcOver.derive((float) Math.max(.15, forca)));
        if (feixe(tipo)) {
            int h = (int) (150 * (tipo == 4 ? 1.4 : 1)), x0 = ox - h / 5;
            int w = k == 0 ? fw * h / fh : Config.LARGURA + 40 - x0;
            q.drawImage(a, x0, cy - h / 2, x0 + w, cy + h / 2, k * fw, 0, k * fw + fw, fh, null);
        } else {
            int h = tipo == 7 ? 300 : tipo == 3 ? 260 : 230, w = fw * h / fh;
            double voo = Math.max(0, (t - .14) / .66);
            int x = ox - w / 4 + (int) (voo * (Config.LARGURA + w));
            q.drawImage(a, x - w / 2, cy - h / 2, x + w / 2, cy + h / 2, k * fw, 0, k * fw + fw, fh, null);
        }
        q.dispose(); return true;
    }

    final int tipo; private double tempo; private final int vaporizados;
    private final Random acaso = new Random(9);
    private final double[][] alvos;
    LaserConvergencia(int tipo, int vaporizados, double[][] alvos) { this.tipo = tipo; this.vaporizados = vaporizados; this.alvos = alvos; }
    void atualizar(double dt) { tempo += dt; }

    /** Carga: particulas convergindo para o nariz da nave e um anel que fecha conforme o progresso (0..1). */
    static void desenharCarga(Graphics2D original, int naveX, int naveY, double p, int tipo, double t) {
        Graphics2D g = (Graphics2D) original.create();
        Color cor = tipo < 0 ? new Color(190, 90, 255) : Poderes099.CORES_SIMBOLO[tipo];
        int cx = naveX + 46, cy = naveY + 20;
        java.awt.image.BufferedImage arte = ARTE[tipo + 1];
        java.awt.image.BufferedImage carga = CARGA[tipo + 1] != null ? CARGA[tipo + 1] : CARGA[0];
        if (carga != null || arte != null) {
            // carga em pixel art: arte propria de carga (carga_*.png) ou o 1o quadro do disparo, crescendo e pulsando
            Graphics2D q = (Graphics2D) g.create();
            q.setRenderingHint(java.awt.RenderingHints.KEY_INTERPOLATION, java.awt.RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
            int fw, fh, sx; java.awt.image.BufferedImage img;
            if (carga != null) { img = carga; fw = carga.getWidth() / 4; fh = carga.getHeight(); sx = ((int) (t * 12) % 4) * fw; }
            else { img = arte; fw = arte.getWidth() / 4; fh = arte.getHeight(); sx = 0; }
            int h = (int) ((40 + 90 * p) * (1 + .08 * Math.sin(t * 24))), w = fw * h / fh;
            q.setComposite(AlphaComposite.SrcOver.derive((float) (.55 + .45 * p)));
            int x0 = carga != null ? cx - w / 2 : cx - w / 5;
            q.drawImage(img, x0, cy - h / 2, x0 + w, cy + h / 2, sx, 0, sx + Math.min(fw, carga != null ? fw : fw * 2 / 5), fh, null);
            q.dispose();
            for (int k = 0; k < 18; k++) {
                double an = k * 2.399 + t * 3, dist = (1 - ((t * 1.8 + k * .07) % 1)) * 110;
                g.setColor(c(cor.getRed(), cor.getGreen(), cor.getBlue(), .5 + .5 * p)); g.fillRect(cx + (int) (Math.cos(an) * dist), cy + (int) (Math.sin(an) * dist), 3, 3);
            }
            g.dispose(); return;
        }
        for (int k = 0; k < 24; k++) {
            double an = k * 2.399 + t * 3, dist = (1 - ((t * 1.8 + k * .07) % 1)) * 120 * (1 - p * .5);
            g.setColor(c(cor.getRed(), cor.getGreen(), cor.getBlue(), .4 + .6 * p)); g.fillRect(cx + (int) (Math.cos(an) * dist), cy + (int) (Math.sin(an) * dist), 4, 4);
        }
        int r = (int) (6 + 26 * p + Math.sin(t * 30) * 2);
        g.setColor(c(cor.getRed(), cor.getGreen(), cor.getBlue(), .5 + .4 * p)); g.fillOval(cx - r, cy - r, r * 2, r * 2);
        g.setColor(c(255, 255, 255, .6 + .4 * p)); g.fillOval(cx - r / 2, cy - r / 2, r, r);
        g.setStroke(new BasicStroke(3)); g.setColor(c(255, 230, 140, 1)); g.drawArc(naveX - 14, naveY - 22, 72, 84, 90, -(int) (360 * p));
        g.setStroke(new BasicStroke(1)); g.setColor(c(40, 50, 70, .8)); g.drawOval(naveX - 14, naveY - 22, 72, 84);
        g.dispose();
    }
    boolean ativo() { return tempo < DURACAO; }

    private static Color c(int r, int g, int b, double a) { return new Color(r, g, b, (int) Math.max(0, Math.min(255, a * 255))); }

    void desenhar(Graphics2D original, int naveX, int naveY) {
        Graphics2D g = (Graphics2D) original.create();
        double t = tempo / DURACAO, forca = t < .15 ? t / .15 : t < .75 ? 1 : 1 - (t - .75) / .25;
        int ox = naveX + 46, cy = naveY + 20, W = Config.LARGURA;
        if (t >= .12 && t < .3) { g.setColor(c(255, 255, 255, .5 * (1 - (t - .12) / .18))); g.fillRect(0, 0, W, Config.ALTURA); }
        if (t >= .12 && !desenharArte(g, ox, cy, t, forca)) switch (tipo) {
            case 0: feixe(g, ox, cy, forca, new Color(20, 120, 200), new Color(90, 230, 255)); bolhas(g, ox, cy, forca); break;
            case 1: feixeFino(g, ox, cy, forca); break;
            case 2: laminas(g, forca); break;
            case 3: for (int k = -3; k <= 3; k += 2) leque(g, ox, cy, k * 5, forca); break;
            case 4: feixe(g, ox, cy, forca * .6, new Color(30, 80, 220), new Color(160, 200, 255)); raios(g, ox, cy, forca, true); break;
            case 5: pentagrama(g, forca); break;
            case 6: prisma(g, ox, cy, forca); break;
            case 7: eclipse(g, forca); break;
            default: feixe(g, ox, cy, forca, new Color(110, 30, 200), new Color(210, 120, 255)); raios(g, ox, cy, forca, false); break;
        }
        // esfera de carga na ponta da nave, na cor do disparo
        Color nucleo = tipo < 0 ? new Color(190, 90, 255) : Poderes099.CORES_SIMBOLO[tipo];
        if (ARTE[tipo + 1] == null) {
            int r = (int) (10 + 22 * forca + Math.sin(tempo * 40) * 3);
            g.setColor(c(nucleo.getRed(), nucleo.getGreen(), nucleo.getBlue(), Math.max(.3, forca))); g.fillOval(ox - r, cy - r, r * 2, r * 2);
            g.setColor(c(255, 255, 255, Math.max(.3, forca))); g.fillOval(ox - r / 2, cy - r / 2, r, r);
        }
        g.dispose();
    }

    /** Feixe largo em camadas, com nucleo branco. */
    private void feixe(Graphics2D g, int ox, int cy, double f, Color borda, Color meio) {
        int h = (int) (40 + 74 * f);
        for (int k = 0; k < 4; k++) {
            int hh = h - k * h / 4;
            Color cor = k == 3 ? Color.WHITE : k == 2 ? meio : borda;
            g.setColor(c(cor.getRed(), cor.getGreen(), cor.getBlue(), f * (k == 0 ? .45 : .85)));
            g.fillRect(ox, cy - hh / 2, Config.LARGURA - ox, hh);
        }
    }
    /** Arcos eletricos em zigue-zague em volta do feixe (e, na tempestade, ate cada alvo). */
    private void raios(Graphics2D g, int ox, int cy, double f, boolean ateAlvos) {
        g.setStroke(new BasicStroke(3));
        for (int b = 0; b < 7; b++) {
            int n = 14, px = ox, py = cy; double lado = b % 2 == 0 ? 1 : -1;
            for (int k = 1; k <= n; k++) {
                int nx = ox + (Config.LARGURA - ox) * k / n, ny = cy + (int) (lado * (20 + acaso.nextInt(46)) * f * (k % 2 == 0 ? 1 : .3));
                g.setColor(c(230, 200, 255, f)); g.drawLine(px, py, nx, ny); px = nx; py = ny;
            }
        }
        if (ateAlvos) for (double[] a : alvos) {
            int px = ox, py = cy;
            for (int k = 1; k <= 8; k++) {
                double q = k / 8.0; int nx = (int) (ox + (a[0] - ox) * q) + (k < 8 ? acaso.nextInt(24) - 12 : 0), ny = (int) (cy + (a[1] - cy) * q) + (k < 8 ? acaso.nextInt(24) - 12 : 0);
                g.setColor(c(170, 210, 255, f)); g.drawLine(px, py, nx, ny); px = nx; py = ny;
            }
            g.setColor(c(255, 255, 255, f)); g.fillOval((int) a[0] - 10, (int) a[1] - 10, 20, 20);
        }
        g.setStroke(new BasicStroke(1));
    }
    private void bolhas(Graphics2D g, int ox, int cy, double f) {
        for (int k = 0; k < 50; k++) {
            int x = ox + (int) ((k * 37 + tempo * 900) % (Config.LARGURA - ox)), y = cy + (int) (Math.sin(k * 1.7 + tempo * 9) * 50 * f);
            g.setColor(c(200, 250, 255, f * .9)); g.drawOval(x, y, 6 + k % 6, 6 + k % 6);
        }
    }
    private void feixeFino(Graphics2D g, int ox, int cy, double f) {
        int h = (int) (16 + 18 * f);
        g.setColor(c(255, 90, 20, f * .5)); g.fillRect(ox, cy - h, Config.LARGURA - ox, h * 2);
        g.setColor(c(255, 200, 60, f)); g.fillRect(ox, cy - h / 2, Config.LARGURA - ox, h);
        g.setColor(c(255, 255, 230, f)); g.fillRect(ox, cy - 3, Config.LARGURA - ox, 6);
        for (int k = 0; k < 40; k++) { int x = ox + (int) ((k * 29 + tempo * 1600) % (Config.LARGURA - ox)); int alto = 10 + acaso.nextInt(28);
            g.setColor(c(255, 120 + acaso.nextInt(100), 30, f)); g.fillPolygon(new int[]{x - 6, x, x + 6}, new int[]{cy - h / 2, cy - h / 2 - alto, cy - h / 2}, 3);
            g.fillPolygon(new int[]{x - 6, x, x + 6}, new int[]{cy + h / 2, cy + h / 2 + alto, cy + h / 2}, 3); }
    }
    private void laminas(Graphics2D g, double f) {
        for (int k = 0; k < 3; k++) {
            double ida = Math.max(0, Math.min(1, (tempo - .2 - k * .25) / .6)); int x = (int) (-200 + ida * 1300), y = 150 + k * 150;
            g.setStroke(new BasicStroke(18)); g.setColor(c(40, 200, 80, f * .6)); g.drawArc(x - 220, y - 160, 260, 320, -70, 140);
            g.setStroke(new BasicStroke(8)); g.setColor(c(170, 255, 180, f)); g.drawArc(x - 216, y - 156, 252, 312, -66, 132);
            g.setStroke(new BasicStroke(2)); g.setColor(c(255, 255, 255, f)); g.drawArc(x - 212, y - 152, 244, 304, -60, 120);
        }
        g.setStroke(new BasicStroke(1));
    }
    private void leque(Graphics2D g, int ox, int cy, double graus, double f) {
        Graphics2D a = (Graphics2D) g.create(); a.rotate(Math.toRadians(graus), ox, cy);
        a.setColor(c(255, 200, 70, f * .5)); a.fillRect(ox, cy - 18, 1200, 36);
        a.setColor(c(255, 240, 200, f)); a.fillRect(ox, cy - 6, 1200, 12);
        for (int k = 0; k < 10; k++) { int x = ox + (int) ((k * 113 + tempo * 1200) % 1100); a.setColor(c(30, 20, 30, f)); a.fillOval(x - 6, cy - 7, 12, 10); a.fillPolygon(new int[]{x - 6, x + 6, x}, new int[]{cy - 3, cy - 3, cy - 14}, 3); }
        a.dispose();
    }
    private void pentagrama(Graphics2D g, double f) {
        int cx = Config.LARGURA / 2, cy = (Config.ALTURA + Config.HUD) / 2, r = (int) (60 + 200 * f);
        g.setColor(c(120, 0, 0, f * .35)); g.fillRect(0, Config.HUD, Config.LARGURA, Config.ALTURA - Config.HUD);
        Graphics2D a = (Graphics2D) g.create(); a.rotate(tempo * 1.5, cx, cy); a.setStroke(new BasicStroke(10));
        a.setColor(c(255, 60, 30, f)); a.drawOval(cx - r, cy - r, 2 * r, 2 * r);
        int[] xs = new int[5], ys = new int[5]; for (int k = 0; k < 5; k++) { double an = -Math.PI / 2 + k * 4 * Math.PI / 5; xs[k] = cx + (int) (Math.cos(an) * r * .95); ys[k] = cy + (int) (Math.sin(an) * r * .95); }
        a.drawPolygon(xs, ys, 5); a.setStroke(new BasicStroke(3)); a.setColor(c(255, 220, 120, f)); a.drawPolygon(xs, ys, 5); a.dispose();
        for (int k = 0; k < 70; k++) { int x = acaso.nextInt(Config.LARGURA), y = Config.HUD + (int) ((acaso.nextInt(600) + tempo * 500) % (Config.ALTURA - Config.HUD));
            g.setColor(c(255, 100 + acaso.nextInt(120), 20, f)); g.fillPolygon(new int[]{x - 5, x, x + 5}, new int[]{y, y - 16, y}, 3); }
    }
    /** Provisorio do Native Blood: feixe turquesa com uma aguia de luz na ponta e penas douradas. */
    private void prisma(Graphics2D g, int ox, int cy, double f) {
        feixe(g, ox, cy, f * .7, new Color(20, 140, 140), new Color(110, 240, 230));
        int ax = ox + (int) ((tempo / DURACAO) * (Config.LARGURA - ox)), bat = (int) (Math.sin(tempo * 16) * 60);
        g.setColor(c(110, 240, 230, f)); g.fillPolygon(new int[]{ax - 90, ax - 20, ax + 30, ax - 20}, new int[]{cy - bat - 40, cy - 10, cy, cy + 10}, 4);
        g.fillPolygon(new int[]{ax - 90, ax - 20, ax + 30, ax - 20}, new int[]{cy + bat + 40, cy + 10, cy, cy - 10}, 4);
        g.setColor(c(255, 215, 100, f)); g.fillPolygon(new int[]{ax + 30, ax + 50, ax + 30}, new int[]{cy - 7, cy, cy + 7}, 3);
        for (int k = 0; k < 40; k++) { int x = ox + (int) ((k * 41 + tempo * 600) % Math.max(1, ax - ox)), y = cy + (int) (Math.sin(k * 2.1 + tempo * 6) * 60);
            g.setColor(c(255, 215, 100, f * .9)); g.fillRect(x, y, 6, 2); }
    }
    private void eclipse(Graphics2D g, double f) {
        g.setColor(c(10, 0, 20, f * .75)); g.fillRect(0, Config.HUD, Config.LARGURA, Config.ALTURA - Config.HUD);
        int cx = Config.LARGURA / 2, cy = 300, r = 90; g.setColor(c(200, 120, 255, f)); g.fillOval(cx - r - 8, cy - r - 8, 2 * r + 16, 2 * r + 16); g.setColor(c(0, 0, 0, f)); g.fillOval(cx - r, cy - r, 2 * r, 2 * r);
        for (int k = 0; k < 40; k++) { int x = (int) ((k * 97 + tempo * 700) % (Config.LARGURA + 200)) - 100, y = Config.HUD + 20 + (k * 53) % (Config.ALTURA - Config.HUD - 40);
            TiroChefe.morcego(g, x, y + (int) (Math.sin(tempo * 8 + k) * 10), 3, tempo + k * .1, true, k % 3 == 0); }
    }
}
