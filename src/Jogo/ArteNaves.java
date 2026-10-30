/*
 * Decompiled with CFR 0.152.
 */
package Jogo;



import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.Locale;

public final class ArteNaves {
    private static final BufferedImage[] JOGADOR = new BufferedImage[5];
    private static final Color[] CORES = new Color[]{new Color(40, 194, 255), new Color(246, 73, 83), new Color(255, 185, 76), new Color(69, 255, 171), new Color(242, 93, 243)};

    private ArteNaves() {
    }

    public static Color corModelo(int n) {
        return CORES[Math.max(0, Math.min(4, n))];
    }

    private static BufferedImage jogador(int n) {
        if (JOGADOR[n = Math.max(0, Math.min(4, n))] == null) {
            ArteNaves.JOGADOR[n] = n == 0 ? ArteNaves.naveReferencia() : ArteNaves.carregar("nave_hangar_" + n + ".png", 80, 64);
        }
        return JOGADOR[n];
    }

    public static void desenharJogador(Graphics2D graphics2D, int n, int n2, int n3, int n4, double d) {
        ArteNaves.desenharJogador(graphics2D, n, n2, n3, n4, d, 0.0, 0.0);
    }

    public static void desenharJogador(Graphics2D graphics2D, int n, int n2, int n3, int n4, double d, double d2, double d3) {
        ArteNaves.desenharJogador(graphics2D, n, n2, n3, n4, d, d2, d3, 1.0, false);
    }

    public static void desenharJogador(Graphics2D graphics2D, int n, int n2, int n3, int n4, double d, double d2, double d3, double d4, boolean bl) {
        int[] nArray;
        n2 = Math.max(0, Math.min(4, n2));
        Graphics2D graphics2D2 = (Graphics2D)graphics2D.create();
        graphics2D2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        graphics2D2.rotate(d2 * 0.135, n3 + 22, n4 + 20);
        int n5 = (int)Math.round(Math.sin(d * 14.0) * 0.8);
        int n6 = 15 + (int)(4.0 * Math.sin(d * 19.0));
        Color color = ArteNaves.corModelo(n2);
        int n7 = n3 - (n2 == 0 ? 20 : 0);
        for (int n8 : nArray = new int[]{13, 32}) {
            if (bl && n8 == 32) continue;
            int n9 = d4 <= 0.25 ? 90 : (d4 <= 0.5 ? 135 : 184);
            graphics2D2.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 42));
            graphics2D2.fillPolygon(new int[]{n7 + 4, n7 - 7, n7 - n6 - 17, n7 - 6, n7 + 4}, new int[]{n4 + n8 - 7, n4 + n8 - 7, n4 + n8, n4 + n8 + 7, n4 + n8 + 7}, 5);
            graphics2D2.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), n9));
            graphics2D2.fillPolygon(new int[]{n7 + 4, n7 - 5, n7 - n6 - 8, n7 - 4, n7 + 4}, new int[]{n4 + n8 - 3, n4 + n8 - 3, n4 + n8, n4 + n8 + 3, n4 + n8 + 3}, 5);
            graphics2D2.setColor(new Color(247, 251, 255, 222));
            graphics2D2.drawLine(n7 - n6 / 2, n4 + n8, n7 + 3, n4 + n8);
        }
        EvolucaoVisual.desenhar(graphics2D2, n2, n, n3, n4 + n5, d, d3, false);
        graphics2D2.drawImage(ArteNaves.jogador(n2), n3 - (n2 == 0 ? 16 : 8) - (int)(d3 * 4.0), n4 - 8 + n5, n2 == 0 ? 82 : 67, 54, null);
        EvolucaoVisual.desenhar(graphics2D2, n2, n, n3, n4 + n5, d, d3, true);
        graphics2D2.dispose();
    }

    public static void desenharMiniatura(Graphics2D graphics2D, int n, int n2, int n3, int n4, int n5, double d) {
        Color color = ArteNaves.corModelo(n);
        Graphics2D graphics2D2 = (Graphics2D)graphics2D.create();
        graphics2D2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        int n6 = (int)Math.round(Math.sin(d * 2.6 + (double)n * 0.65) * 3.0);
        int n7 = 15 + (int)(5.0 * Math.sin(d * 14.0 + (double)n));
        graphics2D2.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 175));
        for (int n8 : new int[]{n3 + n5 / 3, n3 + 2 * n5 / 3}) {
            graphics2D2.fillPolygon(new int[]{n2 + 9, n2 - 4, n2 - n7 - 12, n2 - 4, n2 + 9}, new int[]{n8 - 4, n8 - 4, n8, n8 + 4, n8 + 4}, 5);
            graphics2D2.setColor(new Color(238, 248, 255, 220));
            graphics2D2.drawLine(n2 - n7 / 2, n8, n2 + 8, n8);
            graphics2D2.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 175));
        }
        graphics2D2.drawImage(ArteNaves.jogador(n), n2, n3 + n6, n4, n5, null);
        graphics2D2.dispose();
    }

    public static void desenharPirata(Graphics2D graphics2D, int n, int n2, int n3, boolean bl, double d) {
        ArteNaves.desenharPirata(graphics2D, n, n2, n3, bl, d, "INTERCEPTADOR");
    }

    public static void desenharPirata(Graphics2D graphics2D, int n, int n2, int n3, boolean bl, double d, String string) {
        String string2 = bl ? "pirata_evoluido_087.png" : "pirata_" + string.toLowerCase(Locale.ROOT) + "_087.png";
        Graphics2D graphics2D2 = (Graphics2D)graphics2D.create();
        graphics2D2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        Image image = Assets.imagem(string2);
        int n4 = image.getWidth(null);
        int n5 = image.getHeight(null);
        int n6 = 0;
        int n7 = n4;
        if (!bl && string.equals("ESQUADRAO")) {
            n6 = 21;
        }
        if (!bl && string.equals("LASER")) {
            n6 = 23;
            n7 = 75;
        }
        int n8 = n2 - 6;
        int n9 = n3 - 5;
        graphics2D2.drawImage(image, n8 + n6 * 78 / n4, n9 + 22, n8 + n7 * 78 / n4, n9 + 45, n6, n5 / 3, n7, n5 * 2 / 3, null);
        for (int i = 0; i < 2; ++i) {
            Graphics2D graphics2D3 = (Graphics2D)graphics2D2.create();
            double d2 = Math.sin(d * 4.5 + (double)n * 0.8) * 0.075 * (double)(i == 0 ? -1 : 1);
            graphics2D3.rotate(d2, n2 + 32, n3 + (i == 0 ? 20 : 40));
            int n10 = i == 0 ? 0 : n5 * 2 / 3;
            int n11 = i == 0 ? n5 / 3 : n5;
            int n12 = i == 0 ? n9 : n9 + 44;
            int n13 = i == 0 ? 22 : 22;
            graphics2D3.drawImage(image, n8 + n6 * 78 / n4, n12, n8 + n7 * 78 / n4, n12 + n13, n6, n10, n7, n11, null);
            graphics2D3.dispose();
        }
        graphics2D2.dispose();
    }

    public static void desenharOrganico(Graphics2D graphics2D, int n, int n2, int n3, double d, boolean bl) {
        AtlasInimigos.organico(graphics2D, n, n2, n3, d, bl);
    }

    private static BufferedImage naveReferencia() {
        int n;
        Image image = Assets.imagem("nave_referencia_091.png");
        BufferedImage bufferedImage = new BufferedImage(image.getWidth(null), image.getHeight(null), 2);
        Graphics2D graphics2D = bufferedImage.createGraphics();
        graphics2D.drawImage(image, 0, 0, null);
        graphics2D.dispose();
        int n2 = bufferedImage.getWidth();
        int n3 = bufferedImage.getHeight();
        int n4 = 0;
        int n5 = 0;
        for (int i = 0; i < bufferedImage.getHeight(); ++i) {
            for (n = 0; n < bufferedImage.getWidth(); ++n) {
                if (bufferedImage.getRGB(n, i) >>> 24 <= 100) continue;
                n2 = Math.min(n2, n);
                n4 = Math.max(n4, n);
                n3 = Math.min(n3, i);
                n5 = Math.max(n5, i);
            }
        }
        BufferedImage bufferedImage2 = new BufferedImage(80, 64, 2);
        graphics2D = bufferedImage2.createGraphics();
        graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        n = (int)(80.0 * (double)(n5 - n3 + 1) / (double)(n4 - n2 + 1));
        graphics2D.drawImage(bufferedImage, 0, (64 - n) / 2, 80, (64 + n) / 2, n2, n3, n4 + 1, n5 + 1, null);
        graphics2D.dispose();
        return bufferedImage2;
    }

    private static BufferedImage carregar(String string, int n, int n2) {
        Image image = Assets.imagem(string);
        BufferedImage bufferedImage = new BufferedImage(n, n2, 2);
        Graphics2D graphics2D = bufferedImage.createGraphics();
        graphics2D.drawImage(image, 0, 0, n, n2, null);
        graphics2D.dispose();
        return bufferedImage;
    }
}

