package Jogo;


import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

/** Galeria de insignias: silhouette escura ate a conquista, depois cores originais. */
public final class Medalhas {
    private static final int[] ORDEM = {8, 2, 3, 4, 5, 6, 7, 1};
    private static final BufferedImage[] NORMAL = new BufferedImage[8];
    private static final BufferedImage[] BLOQUEADA = new BufferedImage[8];
    private Medalhas() { }
    private static void carregar(int indice) {
        if (NORMAL[indice] != null) return;
        BufferedImage original = Arte098.chave(indice);
        BufferedImage img = new BufferedImage(96, 96, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.drawImage(original, 0, 0, 96, 96, null);
        g.dispose();
        NORMAL[indice] = img;
        BufferedImage cinza = new BufferedImage(96, 96, BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < 96; y++) for (int x = 0; x < 96; x++) {
            int argb = img.getRGB(x, y);
            int a = (argb >>> 24) & 255;
            if (a == 0) continue;
            int r = (argb >>> 16) & 255, v = (argb >>> 8) & 255, b = argb & 255;
            int luminancia = (int)(r*.21 + v*.72 + b*.07);
            int cinzaEscuro = Math.max(17, Math.min(65, (int)(luminancia*.20)+10));
            cinza.setRGB(x, y, (a << 24) | (cinzaEscuro << 16) | ((cinzaEscuro+4)<<8) | (cinzaEscuro+11));
        }
        BLOQUEADA[indice] = cinza;
    }
    public static void desenhar(Graphics2D g, int id, boolean obtida, int x, int y, int tamanho, double tempo) {
        int indice = -1;
        for (int i = 0; i < ORDEM.length; i++) if (ORDEM[i] == id) indice = i;
        if (indice < 0) throw new IllegalArgumentException("Emblema invalido: " + id);
        carregar(indice);
        Graphics2D a = (Graphics2D)g.create();
        a.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        a.setColor(obtida ? new Color(34, 57, 83) : new Color(11, 18, 30));
        a.fillRect(x, y, tamanho, tamanho);
        a.setStroke(new BasicStroke(2));
        a.setColor(obtida ? new Color(249, 192, 80) : new Color(54, 69, 85));
        a.drawRect(x, y, tamanho, tamanho);
        if (obtida && tempo >= 0) {
            int brilho = (int)(15 + 17 * (1 + Math.sin(tempo*4)));
            a.setColor(new Color(242, 185, 89, Math.min(80, brilho)));
            a.fillRect(x+4, y+4, tamanho-8, tamanho-8);
        }
        a.drawImage(obtida ? NORMAL[indice] : BLOQUEADA[indice], x+3, y+3, tamanho-6, tamanho-6, null);
        a.dispose();
    }
    public static int[] ordem() { return ORDEM.clone(); }
}
