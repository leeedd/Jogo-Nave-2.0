package Jogo;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

/**
 * Palco dos creditos montado com a arte do proprio jogo: o palco do festival ao fundo, o emblema
 * da banda no telao e a banda inteira tocando (a mesma arte das lutas). Assim quem sobe no palco
 * e exatamente quem o jogador enfrentou.
 */
final class PalcoCreditos {
    static final BufferedImage PALCO = ler("palco_festival.png");
    static final BufferedImage PALCO_PB = cinza(PALCO);
    /** Arco final (grade 4x4): Jack, Kyle, Deezleboss e Dio, 4 quadros cada. */
    private static final BufferedImage ARCO = ler("arco_final_099.png");
    /** Banda especial: Tenacious D com o Deezleboss no meio. */
    static final int TENACIOUS = 8;
    private static final double TELAO_X0 = 568 / 1586.0, TELAO_Y0 = 348 / 992.0, TELAO_X1 = 1018 / 1586.0, TELAO_Y1 = 558 / 992.0;
    /** Linha do chao do palco na tela (960x600). */
    private static final int PISO = 404;

    private PalcoCreditos() { }

    /** Fundo do palco (colorido ou em preto e branco), ocupando a tela toda. */
    static void fundo(Graphics2D g, int w, int h, boolean pb) {
        BufferedImage p = pb ? PALCO_PB : PALCO;
        if (p == null) { g.setColor(new Color(10, 6, 18)); g.fillRect(0, 0, w, h); return; }
        Graphics2D a = (Graphics2D) g.create();
        a.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        a.drawImage(p, 0, 0, w, h, null); a.dispose();
    }

    /** Banda (indice de Fase.BANDAS, ou TENACIOUS) tocando no palco, com o emblema no telao. */
    static void banda(Graphics2D g, int w, int h, int banda, double t) {
        Graphics2D a = (Graphics2D) g.create();
        a.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        if (banda == TENACIOUS) tenacious(a, t);
        else {
            telao(a, w, h, CamisaShow.emblema(banda), t);
            String vocal = Turne.VOCALISTAS[Turne.setor(banda)];
            int n = Turne.integrantes(banda);
            // baterista atras, no praticavel; o resto na frente, com o vocalista no meio
            java.util.List<String> frente = new java.util.ArrayList<>();
            String bateria = null;
            for (int p = 0; p < n; p++) {
                String nome = Turne.integrante(banda, p);
                if (Turne.INSTRUMENTOS[Turne.setor(banda)].charAt(p) == 'D' && bateria == null) bateria = nome; else frente.add(nome);
            }
            if (bateria != null) membro(a, bateria, 480, PISO - 30, 145, t, 0, false);
            if (!vocal.isEmpty()) frente.add(frente.size() / 2, vocal);
            int m = frente.size();
            for (int k = 0; k < m; k++) {
                String nome = frente.get(k); boolean eVocal = nome.equals(vocal);
                int x = m == 1 ? 480 : (int) (235 + k * 490.0 / (m - 1));
                membro(a, nome, x, eVocal ? PISO + 10 : PISO + 4, eVocal ? 205 : 172, t, k + 1, x < 470);
            }
        }
        a.dispose();
    }

    private static void telao(Graphics2D g, int w, int h, Image e, double t) {
        if (e == null) return;
        int x0 = (int) (TELAO_X0 * w), y0 = (int) (TELAO_Y0 * h), x1 = (int) (TELAO_X1 * w), y1 = (int) (TELAO_Y1 * h);
        double s = Math.min((x1 - x0) / (double) e.getWidth(null), (y1 - y0) / (double) e.getHeight(null)) * .9 * (1 + .03 * Math.sin(t * 5));
        int ew = (int) (e.getWidth(null) * s), eh = (int) (e.getHeight(null) * s);
        g.drawImage(e, (x0 + x1 - ew) / 2, (y0 + y1 - eh) / 2, ew, eh, null);
    }

    /** Um integrante tocando: alterna o quadro de tocar com o bate-cabeca, cada um no seu tempo. */
    private static void membro(Graphics2D g, String nome, int cx, int pe, int alvo, double t, int fase, boolean virar) {
        BufferedImage arte = AtlasInimigos.integrante(nome);
        if (arte == null) return;
        boolean batida = (int) (t * 2.4 + fase * .37) % 2 == 1;
        int q = batida ? (Turne.quadroQueda(nome) ? 1 : 3) : 0;
        int fw = arte.getWidth() / 4, fh = arte.getHeight();
        double e = Math.min(alvo / (double) fh, alvo * 1.3 / fw);
        int w = (int) (fw * e), hh = (int) (fh * e);
        // o corpo fica a 2/3 do quadro (o golpe sai para a esquerda); virado, o corpo vai para 1/3
        int x0 = cx - (int) (w * (virar ? .33 : .67)), y0 = pe - hh;
        int pulo = batida ? 0 : 2;
        if (virar) g.drawImage(arte, x0 + w, y0 + pulo, x0, y0 + hh + pulo, q * fw, 0, q * fw + fw, fh, null);
        else g.drawImage(arte, x0, y0 + pulo, x0 + w, y0 + hh + pulo, q * fw, 0, q * fw + fw, fh, null);
    }

    /** Tenacious D: Jack e Kyle nas pontas, o Deezleboss no meio, sem emblema (telao em chamas). */
    private static void tenacious(Graphics2D g, double t) {
        if (ARCO == null) return;
        int c = ARCO.getWidth() / 4;
        int[] linha = {0, 2, 1}, xs = {320, 480, 640}, alvo = {150, 200, 150};
        for (int k = 0; k < 3; k++) {
            int q = new int[]{0, 1, 0, 3}[(int) (t * 2.4 + k * .5) % 4];
            int s = alvo[k], x0 = xs[k] - s / 2, y0 = PISO + 6 - s;
            boolean virar = k == 0; // Jack olha para o centro
            if (virar) g.drawImage(ARCO, x0 + s, y0, x0, y0 + s, q * c, linha[k] * c, q * c + c, linha[k] * c + c, null);
            else g.drawImage(ARCO, x0, y0, x0 + s, y0 + s, q * c, linha[k] * c, q * c + c, linha[k] * c + c, null);
        }
    }

    private static BufferedImage cinza(BufferedImage src) {
        if (src == null) return null;
        BufferedImage b = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < src.getHeight(); y++) for (int x = 0; x < src.getWidth(); x++) {
            int p = src.getRGB(x, y), v = ((p >> 16 & 255) * 3 + (p >> 8 & 255) * 6 + (p & 255)) / 10;
            b.setRGB(x, y, v << 16 | v << 8 | v);
        }
        return b;
    }

    private static BufferedImage ler(String n) {
        java.net.URL u = PalcoCreditos.class.getResource("/ImagemIcon/" + n);
        if (u == null) return null;
        try { return javax.imageio.ImageIO.read(u); } catch (java.io.IOException e) { return null; }
    }
}
