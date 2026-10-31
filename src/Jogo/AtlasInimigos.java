/*
 * Decompiled with CFR 0.152.
 */
package Jogo;


import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

public final class AtlasInimigos {
    private static final BufferedImage[][] ORGANICOS;
    private static final BufferedImage[] SENTINELA;

    private static BufferedImage imagem(String string) {
        Image image = Assets.imagem(string);
        BufferedImage bufferedImage = new BufferedImage(image.getWidth(null), image.getHeight(null), 2);
        Graphics2D graphics2D = bufferedImage.createGraphics();
        graphics2D.drawImage(image, 0, 0, null);
        graphics2D.dispose();
        return bufferedImage;
    }

    private static void normalizar(BufferedImage[] bufferedImageArray, int n, int n2) {
        int n3;
        int n4;
        int n5;
        Rectangle[] rectangleArray = new Rectangle[bufferedImageArray.length];
        int n6 = 1;
        int n7 = 1;
        for (int i = 0; i < bufferedImageArray.length; ++i) {
            BufferedImage bufferedImage = bufferedImageArray[i];
            n5 = bufferedImage.getWidth();
            int n8 = bufferedImage.getHeight();
            int n9 = 0;
            int n10 = 0;
            for (n4 = 0; n4 < bufferedImage.getHeight(); ++n4) {
                for (n3 = 0; n3 < bufferedImage.getWidth(); ++n3) {
                    if (bufferedImage.getRGB(n3, n4) >>> 24 <= 100) continue;
                    n5 = Math.min(n5, n3);
                    n8 = Math.min(n8, n4);
                    n9 = Math.max(n9, n3);
                    n10 = Math.max(n10, n4);
                }
            }
            rectangleArray[i] = new Rectangle(n5, n8, Math.max(1, n9 - n5 + 1), Math.max(1, n10 - n8 + 1));
            n6 = Math.max(n6, rectangleArray[i].width);
            n7 = Math.max(n7, rectangleArray[i].height);
        }
        double d = Math.min(((double)n - 4.0) / (double)n6, ((double)n2 - 4.0) / (double)n7);
        for (n5 = 0; n5 < bufferedImageArray.length; ++n5) {
            Rectangle rectangle = rectangleArray[n5];
            BufferedImage bufferedImage = new BufferedImage(n, n2, 2);
            Graphics2D graphics2D = bufferedImage.createGraphics();
            graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
            n4 = (int)((double)rectangle.width * d);
            n3 = (int)((double)rectangle.height * d);
            int n11 = (n - n4) / 2;
            int n12 = n2 - 2 - n3;
            graphics2D.drawImage(bufferedImageArray[n5], n11, n12, n11 + n4, n12 + n3, rectangle.x, rectangle.y, rectangle.x + rectangle.width, rectangle.y + rectangle.height, null);
            graphics2D.dispose();
            bufferedImageArray[n5] = bufferedImage;
        }
    }

    public static void organico(Graphics2D graphics2D, int n, int n2, int n3, double d, boolean bl) {
        int n4 = Math.floorMod(n, 5);
        int n5 = bl ? 0 : (int)(d * (double)(n4 == 3 ? 10 : 7) + (double)n4) % 4;
        graphics2D.drawImage((Image)ORGANICOS[n4][n5], n2 - 16, n3 - 15, null);
    }

    public static void sentinela(Graphics2D graphics2D, int n, int n2, int n3) {
        Arte094.desenhar(graphics2D,Math.floorMod(n3,4),n-88,n2-65,176,130);
    }
    /** Sentinela de olho mecanico (sentinela_olho.png, 4 quadros: repouso, olho aberto, raio, danificada). */
    static final BufferedImage OLHO = imagemOpcional("sentinela_olho.png");
    static boolean temSentinelaOlho() { return OLHO != null; }
    /** Arte do integrante (integrante_nome_sobrenome.png, 4 quadros: tocando, carregando, golpe, bate-cabeca). */
    private static final java.util.Map<String, BufferedImage> INTEGRANTES = new java.util.HashMap<>();
    static BufferedImage integrante(String nome) {
        if (nome == null || nome.isEmpty()) return null;
        return INTEGRANTES.computeIfAbsent(nome, n -> imagemOpcional("integrante_" + n.toLowerCase().replace(' ', '_') + ".png"));
    }
    /** Desenha o integrante com o corpo centrado em (x, y); o golpe sai para a esquerda, fora do corpo. */
    static void integrante(Graphics2D g, BufferedImage arte, int x, int y, int quadro) {
        int fw = arte.getWidth() / 4, h = 200; double e = h / (double) arte.getHeight(); int w = (int) (fw * e);
        int x0 = x - (int) (fw * .67 * e), y0 = y - h / 2;
        Graphics2D a = (Graphics2D) g.create();
        a.setRenderingHint(java.awt.RenderingHints.KEY_INTERPOLATION, java.awt.RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        a.drawImage(arte, x0, y0, x0 + w, y0 + h, quadro * fw, 0, quadro * fw + fw, arte.getHeight(), null); a.dispose();
    }
    static void sentinelaOlho(Graphics2D g, int x, int y, int quadro) {
        int fw = OLHO.getWidth() / 4, h = 175, w = fw * h / OLHO.getHeight();
        Graphics2D a = (Graphics2D) g.create();
        a.setRenderingHint(java.awt.RenderingHints.KEY_INTERPOLATION, java.awt.RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        a.drawImage(OLHO, x - w / 2, y - h / 2, x + w / 2, y + h / 2, quadro * fw, 0, quadro * fw + fw, OLHO.getHeight(), null); a.dispose();
    }
    private static BufferedImage imagemOpcional(String n) {
        java.net.URL u = AtlasInimigos.class.getResource("/ImagemIcon/" + n);
        if (u == null) return null;
        try { return javax.imageio.ImageIO.read(u); } catch (java.io.IOException e) { return null; }
    }

    private AtlasInimigos() {
    }

    static {
        int n;
        int n2;
        ORGANICOS = new BufferedImage[5][4];
        SENTINELA = new BufferedImage[4];
        BufferedImage bufferedImage = AtlasInimigos.imagem("organicos_atlas_093.png");
        int[] nArray = new int[]{0, 425, 765, 1070, bufferedImage.getWidth()};
        int[] nArray2 = new int[]{0, 380, 744, bufferedImage.getHeight()};
        for (int i = 0; i < 3; ++i) {
            for (n2 = 0; n2 < 4; ++n2) {
                AtlasInimigos.ORGANICOS[i][n2] = bufferedImage.getSubimage(nArray[n2], nArray2[i], nArray[n2 + 1] - nArray[n2], nArray2[i + 1] - nArray2[i]);
            }
        }
        BufferedImage bufferedImage2 = AtlasInimigos.imagem("organicos_extra_093.png");
        for (n2 = 0; n2 < 2; ++n2) {
            for (n = 0; n < 4; ++n) {
                AtlasInimigos.ORGANICOS[n2 + 3][n] = bufferedImage2.getSubimage(n * bufferedImage2.getWidth() / 4, n2 * bufferedImage2.getHeight() / 2, bufferedImage2.getWidth() / 4, bufferedImage2.getHeight() / 2);
            }
        }
        BufferedImage bufferedImage3 = AtlasInimigos.imagem("sentinela_atlas_093.png");
        for (n = 0; n < 4; ++n) {
            AtlasInimigos.SENTINELA[n] = bufferedImage3.getSubimage(n * bufferedImage3.getWidth() / 4, 0, bufferedImage3.getWidth() / 4, bufferedImage3.getHeight());
        }
        for (n = 0; n < 5; ++n) {
            AtlasInimigos.normalizar(ORGANICOS[n], 96, 82);
        }
        AtlasInimigos.normalizar(SENTINELA, 156, 118);
    }
}

