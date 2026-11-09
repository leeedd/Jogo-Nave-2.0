package Jogo;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 * Memorial: quem ja partiu volta como fantasma para ajudar o piloto na luta contra a propria banda.
 * Aparece uma vez por luta quando o casco cai abaixo de 30%, toca ao lado da nave e atira no chefe
 * por 15 s. Arte: fantasma_N.png (4 quadros virados para a direita); sem ela, uma silhueta azul.
 */
final class FantasmaAliado {
    static final double DURACAO = 15;
    /** Quem ajuda em cada luta (fase do chefe): 1 Ozzy, 4 Lemmy, 5 Metallica, 6 Slayer. */
    static int[] da(int faseChefe) {
        switch (faseChefe) {
            case 1: return new int[]{2};       // Randy Rhoads
            case 4: return new int[]{3, 4};    // Phil Taylor e Fast Eddie Clarke
            case 5: return new int[]{0};       // Cliff Burton
            case 6: return new int[]{1};       // Jeff Hanneman
            default: return new int[0];
        }
    }
    static final String[] NOMES = {"CLIFF BURTON", "JEFF HANNEMAN", "RANDY RHOADS", "PHIL TAYLOR", "FAST EDDIE CLARKE"};
    /** Instrumento: B baixo, G guitarra, D bateria (muda o tiro). */
    private static final char[] INSTRUMENTO = {'B', 'G', 'G', 'D', 'G'};
    /** Silhueta provisoria (so a forma, sem rosto) enquanto a arte do fantasma nao chega. */
    private static final String[] MOLDE = {"integrante_robert_trujillo.png", "integrante_kerry_king.png", "integrante_zakk_wylde.png", "integrante_mikkey_dee.png", "integrante_phil_campbell.png"};
    private static final BufferedImage[] ARTE = new BufferedImage[5];

    final int quem;
    private final int lugar;
    private double tempo, recarga = .6, x, y;
    private final List<double[]> tiros = new ArrayList<>();

    FantasmaAliado(int quem, int lugar, double naveX, double naveY) {
        this.quem = quem; this.lugar = lugar; x = naveX - 120; y = naveY;
        SomJogo.tocar(SomJogo.Efeito.RELIQUIA);
    }
    boolean ativo() { return tempo < DURACAO || !tiros.isEmpty(); }
    private double alfa() { return Math.min(1, tempo / .8) * Math.min(1, (DURACAO - tempo) / 1.2); }

    /** Segue a nave, atira no chefe e devolve o dano causado neste passo. */
    int atualizar(double dt, double naveX, double naveY, Chefe chefe) {
        tempo += dt;
        double ax = naveX - 20 + Math.sin(tempo * 1.3 + lugar) * 10, ay = naveY + (lugar == 0 ? -78 : 78) + Math.sin(tempo * 2.1 + lugar) * 8;
        x += (ax - x) * Math.min(1, dt * 4); y += (ay - y) * Math.min(1, dt * 4);
        int dano = 0;
        if (tempo < DURACAO && chefe != null) {
            recarga -= dt;
            if (recarga <= 0) {
                Rectangle b = chefe.getBounds(); double ox = x + 60, oy = y + 10;
                double a = Math.atan2(b.getCenterY() - oy, b.getCenterX() - ox), v = 620;
                char i = INSTRUMENTO[quem];
                if (i == 'D') for (int k = -1; k <= 1; k++) tiros.add(new double[]{ox, oy, Math.cos(a + k * .12) * v, Math.sin(a + k * .12) * v, 0});
                else tiros.add(new double[]{ox, oy, Math.cos(a) * v, Math.sin(a) * v, 0});
                recarga = i == 'B' ? .55 : i == 'D' ? .75 : .38;
            }
        }
        for (int k = tiros.size() - 1; k >= 0; k--) {
            double[] t = tiros.get(k); t[0] += t[2] * dt; t[1] += t[3] * dt; t[4] += dt;
            Rectangle r = new Rectangle((int) t[0] - 8, (int) t[1] - 8, 16, 16);
            if (chefe != null && chefe.colide(r)) { dano += INSTRUMENTO[quem] == 'B' ? 6 : 4; tiros.remove(k); }
            else if (t[0] > Config.LARGURA + 20 || t[4] > 2) tiros.remove(k);
        }
        return dano;
    }

    void desenhar(Graphics2D tela) {
        float a = (float) Math.max(0, alfa());
        Graphics2D g = (Graphics2D) tela.create();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        // notas azuis do tiro
        for (double[] t : tiros) {
            int px = (int) t[0], py = (int) t[1];
            g.setColor(new Color(120, 200, 255, 90)); g.fillOval(px - 10, py - 10, 20, 20);
            g.setColor(new Color(220, 245, 255)); g.fillOval(px - 5, py, 8, 6); g.fillRect(px + 1, py - 10, 2, 12);
        }
        if (a > 0) {
            // aura
            g.setComposite(AlphaComposite.SrcOver.derive(a * .35f));
            g.setColor(new Color(110, 190, 255)); g.fillOval((int) x - 20, (int) y - 60, 130, 140);
            g.setComposite(AlphaComposite.SrcOver.derive(a * (.75f + .15f * (float) Math.sin(tempo * 6))));
            BufferedImage arte = arte(quem);
            if (arte != null) {
                int fw = arte.getWidth() / 4, fh = arte.getHeight(), q = (int) (tempo * 6) % 4, h = 120, w = fw * h / fh;
                g.drawImage(arte, (int) x - w / 4, (int) y - h / 2 - 10, (int) x - w / 4 + w, (int) y + h / 2 - 10, q * fw, 0, q * fw + fw, fh, null);
            }
            g.setComposite(AlphaComposite.SrcOver.derive(a));
            g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 11)); g.setColor(new Color(190, 230, 255));
            int tw = g.getFontMetrics().stringWidth(NOMES[quem]); g.drawString(NOMES[quem], (int) x + 40 - tw / 2, (int) y + 66);
        }
        g.dispose();
    }

    /** fantasma_N.png se existir; senao a silhueta azul do molde, virada para a direita. */
    private static synchronized BufferedImage arte(int i) {
        if (ARTE[i] != null) return ARTE[i];
        BufferedImage propria = ler("fantasma_" + i + ".png");
        if (propria != null) return ARTE[i] = propria;
        BufferedImage m = ler(MOLDE[i]); if (m == null) return null;
        int W = m.getWidth(), H = m.getHeight(), fw = W / 4;
        BufferedImage s = new BufferedImage(W, H, BufferedImage.TYPE_INT_ARGB);
        for (int q = 0; q < 4; q++) for (int yy = 0; yy < H; yy++) for (int xx = 0; xx < fw; xx++) {
            int p = m.getRGB(q * fw + xx, yy), al = p >>> 24; if (al < 40) continue;
            int v = ((p >> 16 & 255) + (p >> 8 & 255) + (p & 255)) / 3, br = 120 + v / 2;
            s.setRGB(q * fw + (fw - 1 - xx), yy, 200 << 24 | Math.min(255, br - 70) << 16 | Math.min(255, br) << 8 | 255);
        }
        return ARTE[i] = s;
    }
    private static BufferedImage ler(String n) {
        java.net.URL u = FantasmaAliado.class.getResource("/ImagemIcon/" + n);
        if (u == null) return null;
        try { return javax.imageio.ImageIO.read(u); } catch (java.io.IOException e) { return null; }
    }
}
