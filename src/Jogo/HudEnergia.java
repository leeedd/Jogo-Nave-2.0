package Jogo;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

/** Barra de energia do poder equipado: moldura de metal em gomos, simbolo no anel, cadeado sem poder. */
final class HudEnergia {
    private static final BufferedImage MOLDURA = ler("hud_energia_moldura.png"), CADEADO = ler("hud_cadeado.png");
    private static final BufferedImage[] CAPSULA = {ler("capsula_pequena.png"), ler("capsula_grande.png")};
    /** Gomos (y inicio, y fim) e largura do tubo, medidos nas areas vazias da propria moldura. */
    private static final int[][] GOMOS;
    private static int tuboX0, tuboX1, anelX, anelY, anelR;
    static {
        java.util.List<int[]> vazios = new java.util.ArrayList<>();
        if (MOLDURA != null) {
            int cx = MOLDURA.getWidth() / 2, ini = -1;
            for (int y = 0; y <= MOLDURA.getHeight(); y++) {
                boolean vazio = y < MOLDURA.getHeight() && (MOLDURA.getRGB(cx, y) >>> 24) < 40;
                if (vazio && ini < 0) ini = y;
                if (!vazio && ini >= 0) { vazios.add(new int[]{ini, y}); ini = -1; }
            }
        }
        if (vazios.size() >= 3) {
            int[] anel = vazios.remove(0);
            anelY = (anel[0] + anel[1]) / 2; anelR = (anel[1] - anel[0]) / 2; anelX = MOLDURA.getWidth() / 2;
            int[] meio = vazios.get(vazios.size() / 2); int ym = (meio[0] + meio[1]) / 2;
            // So o vazio que contem o centro: a partir do meio ate encostar no metal dos dois lados.
            tuboX0 = tuboX1 = MOLDURA.getWidth() / 2;
            while (tuboX0 > 0 && (MOLDURA.getRGB(tuboX0 - 1, ym) >>> 24) < 40) tuboX0--;
            while (tuboX1 < MOLDURA.getWidth() - 1 && (MOLDURA.getRGB(tuboX1 + 1, ym) >>> 24) < 40) tuboX1++;
        }
        GOMOS = vazios.toArray(new int[0][]);
    }
    private HudEnergia() { }

    private static BufferedImage ler(String nome) {
        java.net.URL u = HudEnergia.class.getResource("/ImagemIcon/" + nome);
        if (u == null) return null;
        try { return javax.imageio.ImageIO.read(u); } catch (java.io.IOException e) { return null; }
    }

    static int largura() { return MOLDURA == null ? 0 : MOLDURA.getWidth(); }

    /** energiaExibida acompanha a energia real gomo a gomo (animacao de encher). */
    static void desenhar(Graphics2D tela, int x, int y, Poderes099 p, ProgressaoNave inv, double energiaExibida, double tempo) {
        if (MOLDURA == null || GOMOS.length == 0) return;
        Graphics2D g = (Graphics2D) tela.create();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        boolean temPoder = false; for (int i = 0; i < 8; i++) temPoder |= inv.temChaveIndice(i);
        int sel = p.selecionado, n = GOMOS.length;
        if (temPoder && inv.temChaveIndice(sel)) {
            Color cor = Poderes099.CORES_SIMBOLO[sel];
            boolean pode = p.energia[sel] >= Poderes099.CUSTO[sel] && p.cooldown[sel] <= 0;
            int cheios = (int) Math.round(energiaExibida / 100.0 * n), custo = (int) Math.ceil(Poderes099.CUSTO[sel] / 100.0 * n);
            for (int k = 0; k < n; k++) {
                int[] gm = GOMOS[n - 1 - k]; // enche de baixo para cima
                boolean cheio = k < cheios;
                Color c = cheio ? (pode ? cor : cor.darker().darker()) : new Color(16, 20, 30);
                g.setColor(c); g.fillRect(x + tuboX0, y + gm[0], tuboX1 - tuboX0 + 1, gm[1] - gm[0]);
                if (cheio) { g.setColor(new Color(255, 255, 255, pode ? 150 : 60)); g.fillRect(x + tuboX0 + 1, y + gm[0], Math.max(1, (tuboX1 - tuboX0) / 3), Math.max(1, gm[1] - gm[0] - 1)); }
                // gomos de um uso: marca lateral na altura do custo
                if (k == custo - 1) { g.setColor(new Color(255, 255, 255, 200)); g.fillRect(x + tuboX0 - 2, y + gm[0], 2, gm[1] - gm[0]); }
            }
            // recarga: brilho subindo pelos gomos
            if (p.cooldown[sel] > 0) {
                int k = (int) (tempo * 14) % n; int[] gm = GOMOS[n - 1 - k];
                g.setColor(new Color(255, 255, 255, 110)); g.fillRect(x + tuboX0, y + gm[0], tuboX1 - tuboX0 + 1, gm[1] - gm[0]);
            }
            g.drawImage(MOLDURA, x, y, null);
            Poderes099.icone(g, sel, x + anelX - anelR, y + anelY - anelR, anelR * 2, tempo);
        } else {
            g.setComposite(AlphaComposite.SrcOver.derive(.55f));
            g.drawImage(MOLDURA, x, y, null);
            g.setComposite(AlphaComposite.SrcOver);
            if (CADEADO != null) { int s = anelR * 2 + 4; g.drawImage(CADEADO, x + anelX - s / 2, y + anelY - s / 2, s, s * CADEADO.getHeight() / CADEADO.getWidth(), null); }
        }
        g.dispose();
    }

    private static final java.util.Map<Integer, BufferedImage> TINTAS = new java.util.HashMap<>();
    /** Capsula com o vidro tingido da cor do poder (o metal cromado fica como esta). */
    static boolean capsula(Graphics2D g, int cx, int cy, boolean grande, Color cor) {
        BufferedImage base = CAPSULA[grande ? 1 : 0];
        if (base == null) return false;
        int chave = (grande ? 1 << 24 : 0) | (cor.getRGB() & 0xFFFFFF);
        BufferedImage t = TINTAS.get(chave);
        if (t == null) {
            t = new BufferedImage(base.getWidth(), base.getHeight(), BufferedImage.TYPE_INT_ARGB);
            int vidro = base.getWidth() * 55 / 100;
            for (int y = 0; y < base.getHeight(); y++) for (int x = 0; x < base.getWidth(); x++) {
                int c = base.getRGB(x, y), a = c >>> 24, r = c >> 16 & 255, gr = c >> 8 & 255, b = c & 255;
                if (x < vidro && a > 0) {
                    double l = (r + gr + b) / 765.0;
                    r = (int) Math.min(255, cor.getRed() * (.35 + l)); gr = (int) Math.min(255, cor.getGreen() * (.35 + l)); b = (int) Math.min(255, cor.getBlue() * (.35 + l));
                    if (l > .93) { r = gr = b = 255; }
                }
                t.setRGB(x, y, a << 24 | r << 16 | gr << 8 | b);
            }
            TINTAS.put(chave, t);
        }
        g.drawImage(t, cx - t.getWidth() / 2, cy - t.getHeight() / 2, null);
        return true;
    }
}
