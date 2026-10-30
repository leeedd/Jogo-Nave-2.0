/*
 * Decompiled with CFR 0.152.
 */
package Jogo;



import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.util.Locale;

public final class HudMetal {
    private static final Image MOLDURA = Assets.imagem("hud_metal_moldura_091.png");
    private static final Color AZUL = new Color(46, 225, 255);

    private HudMetal() {
    }

    private static void texto(Graphics2D graphics2D, String string, int n, int n2, int n3, Color color) {
        graphics2D.setFont(new Font("Monospaced", 1, n3));
        graphics2D.setColor(new Color(0, 0, 0, 210));
        graphics2D.drawString(string, n + 1, n2 + 1);
        graphics2D.setColor(color);
        graphics2D.drawString(string, n, n2);
    }

    private static Polygon placa(int n, int n2, int n3, int n4, int n5) {
        return new Polygon(new int[]{n + n5, n + n3 - n5, n + n3, n + n3, n + n3 - n5, n + n5, n, n}, new int[]{n2, n2, n2 + n5, n2 + n4 - n5, n2 + n4, n2 + n4, n2 + n4 - n5, n2 + n5}, 8);
    }

    static void painel(Graphics2D graphics2D, int n, int n2, int n3, int n4) {
        graphics2D.setColor(new Color(4, 5, 13));
        graphics2D.fillPolygon(HudMetal.placa(n, n2, n3, n4, 9));
        for (int i = 0; i < 5; ++i) {
            graphics2D.setColor((new Color[]{new Color(90, 119, 157), new Color(23, 30, 49), new Color(142, 164, 186), new Color(36, 54, 78), new Color(7, 12, 24)})[i]);
            graphics2D.drawPolygon(HudMetal.placa(n + i, n2 + i, n3 - 2 * i, n4 - 2 * i, 9));
        }
        for (int n5 : new int[]{n + 9, n + n3 - 10}) {
            for (int n6 : new int[]{n2 + 9, n2 + n4 - 10}) {
                graphics2D.setColor(new Color(5, 6, 12));
                graphics2D.fillRect(n5 - 4, n6 - 4, 8, 8);
                graphics2D.setColor(new Color(94, 111, 139));
                graphics2D.drawRect(n5 - 4, n6 - 4, 8, 8);
                graphics2D.setColor(new Color(188, 20, 38));
                graphics2D.fillRect(n5 - 2, n6 - 2, 4, 4);
                graphics2D.setColor(new Color(255, 173, 92));
                graphics2D.fillRect(n5 - 1, n6 - 1, 2, 2);
            }
        }
        for (int i = n + 28; i < n + n3 - 20; i += 46) {
            graphics2D.setColor(new Color(52, 64, 83));
            graphics2D.fillRect(i, n2 + n4 - 4, 24, 3);
            graphics2D.setColor(new Color(195, 34, 43));
            graphics2D.fillRect(i + 7, n2 + n4 - 3, 10, 2);
        }
    }

    public static void desenhar(Graphics2D graphics2D, int n, int n2, int n3, int n4, int n5, int n6, int n7, boolean bl, double d) {
        HudMetal.desenhar(graphics2D, n, 12, n2, n3, n4, n5, n6, n7, bl, d);
    }

    public static void desenhar(Graphics2D graphics2D, int n, int n2, int n3, int n4, int n5, int n6, int n7, int n8, boolean bl, double d) {
        int n9;
        int n10;
        Graphics2D graphics2D2 = (Graphics2D)graphics2D.create();
        graphics2D2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        graphics2D2.setColor(new Color(5, 8, 18));
        graphics2D2.fillRect(0, 0, 960, 72);
        graphics2D2.drawImage(MOLDURA, 0, 0, 960, 72, 9, 284, 2162, 439, null);
        HudMetal.texto(graphics2D2, "INTEGRIDADE DO CASCO", 94, 23, 12, new Color(200, 224, 248));
        for (n10 = 0; n10 < 12; ++n10) {
            n9 = 94 + n10 * 17;
            graphics2D2.setColor(new Color(4, 10, 20));
            graphics2D2.fillRect(n9, 31, 14, 23);
            graphics2D2.setColor(new Color(75, 118, 161));
            graphics2D2.drawRect(n9, 31, 14, 23);
            if (n10 >= (int)Math.ceil(12.0 * (double)n / (double)n2)) continue;
            Color color = n <= 4 ? new Color(255, 209, 43) : new Color(178, 255, 30);
            Color color2 = n <= 4 ? new Color(232, 46, 26) : new Color(0, 182, 101);
            graphics2D2.setPaint(new GradientPaint(n9, 33.0f, color, n9, 54.0f, color2));
            graphics2D2.fillRect(n9 + 2, 33, 10, 19);
            graphics2D2.setColor(new Color(226, 255, 194));
            graphics2D2.fillRect(n9 + 3, 34, 8, 3);
            graphics2D2.setColor(new Color(9, 70, 58));
            graphics2D2.drawLine(n9 + 3, 51, n9 + 10, 51);
        }
        HudMetal.texto(graphics2D2, n + "/" + n2, 306, 48, 14, AZUL);
        HudMetal.texto(graphics2D2, "EVOLUCAO " + n3 + "/8", 386, 23, 12, new Color(200, 224, 248));
        for (n10 = 0; n10 < 8; ++n10) {
            n9 = 389 + n10 * 13;
            graphics2D2.setColor(new Color(87, 104, 126));
            graphics2D2.drawRect(n9, 30, 10, 13);
            graphics2D2.setPaint(new GradientPaint(n9, 31.0f, new Color(255, 238, 76), n9, 43.0f, new Color(244, 133, 11)));
            if (n10 >= n3) continue;
            graphics2D2.fillRect(n9 + 2, 32, 7, 10);
        }
        HudMetal.texto(graphics2D2, n4 > 0 ? "ESCUDO " + n4 + "s" : "ESCUDO INATIVO", 385, 57, 10, AZUL);
        HudMetal.texto(graphics2D2, "PONTOS", 535, 23, 12, new Color(200, 224, 248));
        HudMetal.texto(graphics2D2, Integer.toString(n5), 598, 23, 13, new Color(255, 232, 30));
        HudMetal.texto(graphics2D2, "SETOR " + n6 + "/8", 535, 40, 12, new Color(200, 224, 248));
        HudMetal.texto(graphics2D2, String.format(Locale.ROOT, "%02d:%02d", (int)d / 60, (int)d % 60), 648, 40, 11, new Color(255, 62, 218));
        HudMetal.texto(graphics2D2, "TROFEUS " + n7 + "/8", 535, 57, 12, new Color(255, 62, 218));
        HudMetal.texto(graphics2D2, "ARMA " + (n8 == 1 ? "DISPERSAO" : (n8 == 2 ? "NUCLEAR" : "BASICA")), 732, 23, 12, AZUL);
        ArteNaves.desenharMiniatura(graphics2D2, 0, 725, 28, 47, 28, d);
        for (n10 = 0; n10 < 6; ++n10) {
            n9 = 779 + n10 * 21 + (int)(d * 22.0) % 21;
            graphics2D2.setColor(new Color(20, 101, 230, 100));
            graphics2D2.fillOval(n9 - 7, 36, 18, 10);
            graphics2D2.setColor(new Color(20, 213, 255));
            graphics2D2.fillPolygon(new int[]{n9 - 7, n9, n9 + 8, n9}, new int[]{41, 37, 41, 45}, 4);
            graphics2D2.setColor(Color.WHITE);
            graphics2D2.drawLine(n9 - 2, 41, n9 + 5, 41);
        }
        HudMetal.texto(graphics2D2, bl ? "F: TROCAR | ARMA 2 OK" : "ARMA 2: COLETAR", 724, 59, 9, new Color(199, 218, 236));
        graphics2D2.dispose();
    }

    static void chefe(Graphics2D graphics2D, String string, int n, int n2) {
        HudMetal.texto(graphics2D, string, 132, 565, 14, Color.WHITE);
        HudMetal.painel(graphics2D, 102, 569, 770, 25);
        graphics2D.setColor(new Color(12, 4, 17));
        graphics2D.fillRect(114, 576, 745, 10);
        int n3 = (int)(745.0 * (double)Math.max(0, n) / (double)Math.max(1, n2));
        graphics2D.setPaint(new GradientPaint(114.0f, 576.0f, new Color(255, 76, 72), 114.0f, 587.0f, new Color(137, 0, 35)));
        graphics2D.fillRect(114, 576, n3, 10);
        graphics2D.setColor(new Color(255, 158, 148));
        graphics2D.drawLine(114, 576, 114 + n3, 576);
    }
}

