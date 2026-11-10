package Jogo;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.Random;

/**
 * Fundo do universo do Metal Industrial (Sucata): fabricas espalhadas no espaco montando, camada a
 * camada, as naves que o jogador vai enfrentar naquela etapa. A nave pronta sai da doca e some na
 * distancia: o jogador ve o que vem pela frente. Arte da fabrica: fabrica_industrial.png (doca vazia
 * no meio). Sem a arte, so a montagem aparece.
 */
final class FabricasIndustriais {
    private static final BufferedImage ARTE = ler("fabrica_industrial.png");
    static final int SUCATA = 3;
    private static final int N = 3, FW = 300;
    private static final double TEMPO_MONTAGEM = 4.2, TEMPO_SAIDA = 1.6, VELOCIDADE = 14, ESCALA = 1.15;
    private final double[] fx = {120, 520, 920}, fy = {Config.ALTURA - 250, Config.HUD + 30, Config.ALTURA - 230}, relogio = {0, 1.7, 3.1};
    private final int[] indice = {0, 1, 2};
    private final Random sorte = new Random(3);
    private double tempo;

    void atualizar(double dt) {
        tempo += dt;
        for (int i = 0; i < N; i++) {
            fx[i] -= VELOCIDADE * dt; if (fx[i] < -FW) fx[i] += N * 400;
            relogio[i] += dt; if (relogio[i] > TEMPO_MONTAGEM + TEMPO_SAIDA + .6) { relogio[i] = 0; indice[i] += N; }
        }
    }

    void desenhar(Graphics2D tela, Alien.Classe[] tipos) {
        if (tipos == null || tipos.length == 0) return;
        Graphics2D g = (Graphics2D) tela.create();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        for (int i = 0; i < N; i++) {
            int x = (int) fx[i], y = (int) fy[i];
            g.setComposite(AlphaComposite.SrcOver.derive(.75f));
            if (ARTE != null) g.drawImage(ARTE, x, y, FW, FW * ARTE.getHeight() / ARTE.getWidth(), null);
            int dx = x + FW / 2, dy = y + (ARTE != null ? (FW * ARTE.getHeight() / ARTE.getWidth()) * 45 / 100 : 60); // centro da doca
            Alien.Classe c = tipos[indice[i] % tipos.length];
            BufferedImage nave = sprite(c, indice[i]);
            int w = (int) (nave.getWidth() * ESCALA), h = (int) (nave.getHeight() * ESCALA);
            double t = relogio[i];
            if (t < TEMPO_MONTAGEM) { // montagem: a nave aparece de baixo para cima, com a solda correndo na linha
                double p = t / TEMPO_MONTAGEM; int corte = (int) (nave.getHeight() * (1 - p));
                g.setComposite(AlphaComposite.SrcOver.derive(.22f)); g.drawImage(nave, dx - w / 2, dy - h / 2, w, h, null); // esqueleto
                g.setComposite(AlphaComposite.SrcOver.derive(.9f));
                g.drawImage(nave, dx - w / 2, dy - h / 2 + (int) (corte * ESCALA), dx + w / 2, dy + h / 2, 0, corte, nave.getWidth(), nave.getHeight(), null);
                int ly = dy - h / 2 + (int) (corte * ESCALA);
                g.setColor(new Color(255, 220, 120)); g.fillRect(dx - w / 2, ly, w, 2);
                for (int k = 0; k < 4; k++) { int sx = dx - w / 2 + sorte.nextInt(Math.max(1, w)); g.setColor(k % 2 == 0 ? new Color(255, 250, 200) : new Color(255, 150, 40)); g.fillRect(sx, ly - 2 - sorte.nextInt(6), 2, 2); }
            } else if (t < TEMPO_MONTAGEM + TEMPO_SAIDA) { // pronta: sai da doca para a esquerda e some na distancia
                double p = (t - TEMPO_MONTAGEM) / TEMPO_SAIDA, k = 1 - .5 * p;
                g.setComposite(AlphaComposite.SrcOver.derive((float) (.9 * (1 - p))));
                int ww = (int) (w * k), hh = (int) (h * k), sx = dx - (int) (p * p * 260);
                g.drawImage(nave, sx - ww / 2, dy - hh / 2, ww, hh, null);
                g.setColor(new Color(120, 200, 255, (int) (160 * (1 - p)))); g.fillRect(sx + ww / 2, dy - 1, 6 + (int) (p * 20), 3); // propulsor
            }
        }
        g.dispose();
    }

    private final java.util.Map<String, BufferedImage> cache = new java.util.HashMap<>();
    private BufferedImage sprite(Alien.Classe c, int semente) {
        String k = c.name() + semente % 3;
        BufferedImage b = cache.get(k);
        if (b != null) return b;
        b = new BufferedImage(160, 120, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = b.createGraphics();
        Alien a = new Alien(40, 30, c == Alien.Classe.ORGANICO ? Alien.Tipo.ALIENIGENA_ORIGINAL : Alien.Tipo.PIRATA, c);
        a.definirSetor(SUCATA, new Random(semente % 3));
        a.desenhar(g); g.dispose();
        int x0 = b.getWidth(), y0 = b.getHeight(), x1 = 0, y1 = 0; // recorta so a nave
        for (int y = 0; y < b.getHeight(); y++) for (int x = 0; x < b.getWidth(); x++) if ((b.getRGB(x, y) >>> 24) > 20) { x0 = Math.min(x0, x); y0 = Math.min(y0, y); x1 = Math.max(x1, x); y1 = Math.max(y1, y); }
        if (x1 > x0 && y1 > y0) b = b.getSubimage(x0, y0, x1 - x0 + 1, y1 - y0 + 1);
        cache.put(k, b); return b;
    }

    private static BufferedImage ler(String n) {
        java.net.URL u = FabricasIndustriais.class.getResource("/ImagemIcon/" + n);
        if (u == null) return null;
        try { return javax.imageio.ImageIO.read(u); } catch (java.io.IOException e) { return null; }
    }
}
