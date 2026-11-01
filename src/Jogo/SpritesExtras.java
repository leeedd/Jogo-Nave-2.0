package Jogo;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

/**
 * Arte de acabamento: tiros dos chefes, escudo dos inimigos adaptados pela IA e as naves dos
 * procurados das cacadas. Sem a arte, quem chama continua desenhando em codigo.
 */
final class SpritesExtras {
    /** Tiros (5 linhas x 4 quadros, todos apontando para a esquerda): Rob, Bruce, James, Tom, Chuck. */
    private static final BufferedImage TIROS = ler("tiros_chefes.png");
    /** Escudo oval: 4 quadros de fogo e 4 de gelo. */
    private static final BufferedImage CASCO = ler("casco_escudo.png");
    /** Procurados (virados para a esquerda): Espectro, Harpia Dourada, Carniceiro Verde, Ferreiro. */
    private static final BufferedImage PROCURADOS = ler("procurados.png");
    static final int ROB = 0, BRUCE = 1, JAMES = 2, TOM = 3, CHUCK = 4;
    /** Direcao para onde a arte de cada tiro aponta: a gota do Tom ja vem caindo para a esquerda. */
    private static final double[] ANGULO_ARTE = {Math.PI, Math.PI, Double.NaN, Math.PI * .75, Math.PI};

    private SpritesExtras() { }

    static boolean temTiros() { return TIROS != null; }

    /** Tiro de um chefe centrado em (cx, cy), girado para a direcao do voo. */
    static void tiro(Graphics2D g, int linha, double cx, double cy, double vx, double vy, int largura, double tempo) {
        if (TIROS == null) return;
        int fw = TIROS.getWidth() / 20, fh = TIROS.getHeight(), q = linha * 4 + (int) (tempo * 12) % 4;
        double s = largura / (double) fw, base = ANGULO_ARTE[linha];
        Graphics2D a = (Graphics2D) g.create();
        a.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        a.translate(cx, cy);
        boolean deCabeca = false;
        if (!Double.isNaN(base) && (vx != 0 || vy != 0)) {
            double giro = Math.atan2(vy, vx) - base; a.rotate(giro);
            // indo para a direita a arte ficaria de cabeca para baixo: espelha na vertical
            deCabeca = Math.cos(giro) < 0;
        }
        a.scale(s, deCabeca ? -s : s);
        a.drawImage(TIROS, -fw / 2, -fh / 2, fw - fw / 2, fh - fh / 2, q * fw, 0, q * fw + fw, fh, null);
        a.dispose();
    }

    /** Escudo do casco adaptado em volta do inimigo (fogo ou gelo), tremulando. */
    static boolean casco(Graphics2D g, boolean fogo, double cx, double cy, double tempo) {
        if (CASCO == null) return false;
        int fw = CASCO.getWidth() / 8, fh = CASCO.getHeight(), q = (fogo ? 0 : 4) + (int) (tempo * 10) % 4;
        int w = 104, h = w * fh / fw;
        Graphics2D a = (Graphics2D) g.create();
        a.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        a.setComposite(AlphaComposite.SrcOver.derive((float) (.7 + .15 * Math.sin(tempo * 5))));
        int x0 = (int) cx - w / 2, y0 = (int) cy - h / 2;
        a.drawImage(CASCO, x0, y0, x0 + w, y0 + h, q * fw, 0, q * fw + fw, fh, null);
        a.dispose();
        return true;
    }

    /** Nave do procurado (1 a 4) centrada em (cx, cy), balancando um pouco. */
    static boolean procurado(Graphics2D g, int nave, double cx, double cy, double tempo, boolean congelado) {
        if (PROCURADOS == null || nave < 1 || nave > 4) return false;
        int fw = PROCURADOS.getWidth() / 4, fh = PROCURADOS.getHeight(), q = nave - 1;
        int w = 150, h = w * fh / fw, x0 = (int) cx - w / 2, y0 = (int) (cy - h / 2 + Math.sin(tempo * 3) * 3);
        Graphics2D a = (Graphics2D) g.create();
        a.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        if (nave == 1) a.setComposite(AlphaComposite.SrcOver.derive((float) (.75 + .2 * Math.sin(tempo * 2.3)))); // o Espectro some e volta
        a.drawImage(PROCURADOS, x0, y0, x0 + w, y0 + h, q * fw, 0, q * fw + fw, fh, null);
        if (congelado) { a.setComposite(AlphaComposite.SrcOver.derive(.35f)); a.setColor(new java.awt.Color(150, 220, 255)); a.fillOval(x0 + 10, y0 + 10, w - 20, h - 20); }
        a.dispose();
        return true;
    }

    private static BufferedImage ler(String n) {
        java.net.URL u = SpritesExtras.class.getResource("/ImagemIcon/" + n);
        if (u == null) return null;
        try { return javax.imageio.ImageIO.read(u); } catch (java.io.IOException e) { return null; }
    }
}
