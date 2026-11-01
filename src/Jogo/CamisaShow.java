package Jogo;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

/**
 * Ajusta os paineis do final para o show escolhido. A arte original foi feita com a camisa do
 * Slayer (pentagrama) e o palco do Metallica (M neon). Para outra banda, a estampa vermelha da
 * camisa e o neon azul do palco sao apagados e o emblema da banda entra no lugar. A estampa so
 * pinta onde era camisa, para nao passar por cima de mao e braco.
 */
final class CamisaShow {
    static final int SLAYER = 5, METALLICA = 4;
    /** Caixas por painel (fracoes do painel: x0, y0, x1, y1). Linha 0 = tira "desce", linha 1 = tira "multidao". */
    static final double[][][] ESTAMPA = {
        {null, null, {.414, .411, .509, .557}, {.420, .400, .520, .549}},
        {{.295, .343, .387, .474}, {.289, .691, .435, .929}, {.327, .656, .492, .904}, {.277, .734, .397, .909}}};
    static final double[][][] PALCO = {
        {{.705, .248, .905, .467}, {.70, .248, .905, .467}, {.705, .248, .908, .457}, {.70, .248, .905, .455}},
        {{.556, .18, .71, .34}, {.57, .17, .71, .33}, {.556, .16, .71, .31}, {.49, .15, .61, .32}}};

    private CamisaShow() { }

    /** Copia da tira (4 paineis) com camisa e palco da banda. linha: 0 desce, 1 multidao. */
    static BufferedImage ajustar(Image tira, int linha, int banda) {
        int w = tira.getWidth(null), h = tira.getHeight(null);
        BufferedImage b = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = b.createGraphics(); g.drawImage(tira, 0, 0, null); g.dispose();
        if (banda < 0 || banda == SLAYER) { if (banda == SLAYER) palcoSo(b, linha, banda); return b; }
        Image emblema = emblema(banda);
        if (emblema == null) return b;
        int fw = w / 4;
        for (int k = 0; k < 4; k++) {
            if (banda != SLAYER && ESTAMPA[linha][k] != null) estampa(b, emblema, caixa(ESTAMPA[linha][k], k * fw, fw, h, .1));
            if (banda != METALLICA && PALCO[linha][k] != null) palco(b, emblema, caixa(PALCO[linha][k], k * fw, fw, h, .06));
        }
        return b;
    }

    private static void palcoSo(BufferedImage b, int linha, int banda) {
        Image emblema = emblema(banda); if (emblema == null) return; int fw = b.getWidth() / 4;
        for (int k = 0; k < 4; k++) if (PALCO[linha][k] != null) palco(b, emblema, caixa(PALCO[linha][k], k * fw, fw, b.getHeight(), .06));
    }
    static Image emblema(int banda) { try { return Assets.imagem("emblema_banda_" + banda + ".png"); } catch (IllegalStateException e) { return null; } }

    private static int[] caixa(double[] c, int ox, int fw, int h, double folga) {
        double mx = (c[2] - c[0]) * folga, my = (c[3] - c[1]) * folga;
        return new int[]{ox + (int) ((c[0] - mx) * fw), (int) ((c[1] - my) * h), ox + (int) Math.ceil((c[2] + mx) * fw), (int) Math.ceil((c[3] + my) * h)};
    }

    private static boolean estampaVermelha(int r, int g, int b) { return r > 70 && g < r * .45 && b < r * .5; }
    private static int lum(int r, int g, int b) { return (r * 3 + g * 6 + b) / 10; }

    /** Apaga o pentagrama com a cor da camisa e pinta o emblema so onde era camisa. */
    private static void estampa(BufferedImage b, Image emblema, int[] c) {
        long sr = 0, sg = 0, sb = 0, n = 0;
        for (int y = c[1]; y < c[3]; y++) for (int x = c[0]; x < c[2]; x++) {
            int p = b.getRGB(x, y), r = p >> 16 & 255, g = p >> 8 & 255, bl = p & 255;
            if (!estampaVermelha(r, g, bl) && lum(r, g, bl) < 70) { sr += r; sg += g; sb += bl; n++; }
        }
        int cr = n > 0 ? (int) (sr / n) : 28, cg = n > 0 ? (int) (sg / n) : 24, cb = n > 0 ? (int) (sb / n) : 30;
        int cw = c[2] - c[0], ch = c[3] - c[1];
        boolean[] camisa = new boolean[cw * ch];
        for (int y = c[1]; y < c[3]; y++) for (int x = c[0]; x < c[2]; x++) {
            int p = b.getRGB(x, y), r = p >> 16 & 255, g = p >> 8 & 255, bl = p & 255;
            boolean vermelha = estampaVermelha(r, g, bl);
            if (vermelha) {
                // mantem um pouco do vinco do tecido: mistura a cor media com a luz original
                int l = Math.min(40, lum(r, g, bl) / 4);
                b.setRGB(x, y, cor(cr + l / 3, cg + l / 3, cb + l / 3));
            }
            camisa[(y - c[1]) * cw + x - c[0]] = vermelha || lum(r, g, bl) < 75 && Math.abs(r - cr) < 45 && Math.abs(bl - cb) < 45;
        }
        // emblema centrado, do tamanho da estampa antiga
        BufferedImage e = escalar(emblema, cw, ch, .95);
        int ex = c[0] + (cw - e.getWidth()) / 2, ey = c[1] + (ch - e.getHeight()) / 2;
        for (int y = 0; y < e.getHeight(); y++) for (int x = 0; x < e.getWidth(); x++) {
            int px = ex + x, py = ey + y, i = (py - c[1]) * cw + px - c[0];
            if (px < c[0] || py < c[1] || px >= c[2] || py >= c[3] || !camisa[i]) continue;
            int q = e.getRGB(x, y), a = q >>> 24;
            if (a < 30) continue;
            // estampa de camisa: um pouco mais apagada que o emblema original
            double f = a / 255.0 * .92;
            int o = b.getRGB(px, py);
            b.setRGB(px, py, mistura(o, q, f, .88));
        }
    }

    /** Apaga o neon azul do palco e acende o emblema da banda no lugar. */
    private static void palco(BufferedImage b, Image emblema, int[] c) {
        for (int y = Math.max(0, c[1]); y < Math.min(b.getHeight(), c[3]); y++) for (int x = c[0]; x < c[2]; x++) {
            int p = b.getRGB(x, y), r = p >> 16 & 255, g = p >> 8 & 255, bl = p & 255;
            if (bl > 110 && bl > r + 25 && bl > g + 5) b.setRGB(x, y, cor(r / 4 + 26, g / 5 + 10, bl / 6 + 24));
        }
        int cw = c[2] - c[0], ch = c[3] - c[1];
        BufferedImage e = escalar(emblema, cw, ch, 1.15);
        Graphics2D g = b.createGraphics();
        g.drawImage(e, c[0] + (cw - e.getWidth()) / 2, c[1] + (ch - e.getHeight()) / 2, null);
        g.dispose();
    }

    private static BufferedImage escalar(Image src, int cw, int ch, double f) {
        double s = Math.min(cw / (double) src.getWidth(null), ch / (double) src.getHeight(null)) * f;
        int w = Math.max(1, (int) (src.getWidth(null) * s)), h = Math.max(1, (int) (src.getHeight(null) * s));
        BufferedImage e = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = e.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.drawImage(src, 0, 0, w, h, null); g.dispose();
        return e;
    }

    private static int mistura(int o, int q, double f, double brilho) {
        int r = (int) ((o >> 16 & 255) * (1 - f) + (q >> 16 & 255) * brilho * f);
        int g = (int) ((o >> 8 & 255) * (1 - f) + (q >> 8 & 255) * brilho * f);
        int b = (int) ((o & 255) * (1 - f) + (q & 255) * brilho * f);
        return cor(r, g, b);
    }
    private static int cor(int r, int g, int b) { return 0xFF000000 | clamp(r) << 16 | clamp(g) << 8 | clamp(b); }
    private static int clamp(int v) { return Math.max(0, Math.min(255, v)); }
}
