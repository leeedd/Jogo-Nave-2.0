package Jogo;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

/** Retratos 16 bits desenhados em grade 64x64 para as lendas sem sprite de chefe. */
final class RetratosLendas {
    static final int CHESTER = 1, JOEY = 2, CLIFF = 3, DIO = 4, TENACIOUS_D = 5;
    private static final Color PELE = new Color(226, 176, 142), PELE_SOMBRA = new Color(178, 124, 96),
            PRETO = new Color(18, 16, 20), BRANCO = new Color(240, 238, 232);
    private static final BufferedImage[] CACHE = new BufferedImage[6];
    private RetratosLendas() { }

    static synchronized BufferedImage obter(int qual) {
        if (qual < 1 || qual > 5) return null;
        if (CACHE[qual] == null) CACHE[qual] = arteFinal(qual);
        if (CACHE[qual] == null) CACHE[qual] = desenhar(qual);
        return CACHE[qual];
    }

    private static final String[] ARQUIVOS = {"", "lenda_chester.png", "lenda_joey.png", "lenda_cliff.png", "lenda_dio.png", "lenda_tenacious_d.png"};

    /** Arte final em ImagemIcon tem prioridade; o desenho em codigo e so o provisorio. */
    private static BufferedImage arteFinal(int qual) {
        java.net.URL u = RetratosLendas.class.getResource("/ImagemIcon/" + ARQUIVOS[qual]);
        if (u == null) return null;
        try { return javax.imageio.ImageIO.read(u); }
        catch (java.io.IOException e) { System.err.println("Retrato ilegivel: " + ARQUIVOS[qual]); return null; }
    }

    private static BufferedImage desenhar(int qual) {
        BufferedImage b = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = b.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        fundo(g);
        if (qual == CHESTER) chester(g);
        else if (qual == JOEY) joey(g);
        else if (qual == CLIFF) cliff(g);
        else if (qual == DIO) dio(g);
        else tenaciousD(g);
        g.dispose();
        return b;
    }

    private static void fundo(Graphics2D g) {
        g.setColor(new Color(16, 16, 24));
        g.fillRect(0, 0, 64, 64);
        // Canhao de luz do palco.
        g.setColor(new Color(34, 34, 48));
        g.fillPolygon(new int[]{24, 40, 60, 4}, new int[]{0, 0, 64, 64}, 4);
        g.setColor(new Color(46, 46, 62));
        g.fillPolygon(new int[]{28, 36, 50, 14}, new int[]{0, 0, 64, 64}, 4);
    }

    private static void px(Graphics2D g, Color c, int x, int y, int w, int h) {
        g.setColor(c);
        g.fillRect(x, y, w, h);
    }

    /** Chester: cabelo espetado, cavanhaque, grito no microfone e chamas tatuadas no antebraco. */
    private static void chester(Graphics2D g) {
        px(g, PRETO, 6, 48, 52, 16);
        g.fillPolygon(new int[]{10, 22, 42, 54, 58, 6}, new int[]{50, 44, 44, 50, 64, 64}, 6);
        px(g, PELE_SOMBRA, 27, 38, 10, 9);
        g.setColor(PELE);
        g.fillOval(20, 13, 24, 29);
        px(g, PELE_SOMBRA, 19, 25, 2, 7);
        px(g, PELE_SOMBRA, 43, 25, 2, 7);
        px(g, PRETO, 19, 29, 2, 2);
        px(g, PRETO, 43, 29, 2, 2);
        // Cabelo curto espetado com pontas descoloridas.
        g.setColor(new Color(42, 32, 26));
        g.fillPolygon(new int[]{20, 22, 24, 27, 29, 32, 34, 37, 39, 42, 44, 44, 20},
                new int[]{22, 12, 15, 9, 13, 8, 12, 9, 14, 11, 21, 18, 18}, 13);
        g.setColor(new Color(214, 200, 150));
        for (int[] p : new int[][]{{22, 12}, {27, 9}, {32, 8}, {37, 9}, {42, 11}}) px(g, g.getColor(), p[0], p[1], 2, 2);
        // Olhos fechados e sobrancelhas tensas.
        px(g, PRETO, 24, 23, 6, 1);
        px(g, PRETO, 34, 23, 6, 1);
        px(g, PRETO, 25, 27, 4, 1);
        px(g, PRETO, 35, 27, 4, 1);
        px(g, PELE_SOMBRA, 31, 28, 2, 5);
        // Boca aberta no grito e cavanhaque.
        px(g, new Color(60, 20, 24), 28, 33, 8, 6);
        px(g, BRANCO, 29, 33, 6, 1);
        px(g, new Color(52, 38, 30), 27, 39, 10, 3);
        px(g, new Color(52, 38, 30), 29, 42, 6, 1);
        // Microfone junto a boca, seguro pela mao esquerda.
        g.setColor(new Color(150, 150, 156));
        g.fillPolygon(new int[]{19, 23, 12, 8}, new int[]{38, 40, 54, 52}, 4);
        g.setColor(new Color(70, 70, 76));
        g.fillOval(18, 32, 9, 9);
        px(g, new Color(120, 120, 126), 20, 34, 2, 2);
        g.setColor(PELE);
        g.fillRoundRect(8, 40, 11, 11, 4, 4);
        g.fillPolygon(new int[]{8, 18, 14, 0, 0}, new int[]{48, 50, 64, 64, 56}, 5);
        // Chamas tatuadas no antebraco.
        g.setColor(new Color(64, 104, 196));
        g.fillPolygon(new int[]{2, 5, 7, 9, 11, 13, 13, 2}, new int[]{64, 56, 60, 54, 59, 55, 64, 64}, 8);
        g.setColor(new Color(214, 70, 46));
        g.fillPolygon(new int[]{4, 7, 9, 11, 12, 4}, new int[]{64, 59, 62, 58, 64, 64}, 6);
        px(g, PELE_SOMBRA, 9, 44, 9, 1);
        px(g, PELE_SOMBRA, 9, 47, 9, 1);
    }

    /** Joey: mascara branca com pintura preta, cabelo longo, baquetas e o numero 1 do Slipknot. */
    private static void joey(Graphics2D g) {
        // Pratos da bateria ao fundo.
        g.setColor(new Color(188, 150, 70));
        g.fillOval(-4, 8, 22, 6);
        g.fillOval(46, 6, 22, 6);
        px(g, new Color(120, 92, 40), 6, 13, 1, 10);
        px(g, new Color(120, 92, 40), 57, 11, 1, 12);
        // Macacao escuro com o numero 1.
        g.setColor(new Color(58, 58, 64));
        g.fillPolygon(new int[]{8, 22, 42, 56, 60, 4}, new int[]{50, 44, 44, 50, 64, 64}, 6);
        px(g, new Color(40, 40, 44), 31, 46, 2, 18);
        g.setColor(new Color(40, 40, 44));
        g.fillPolygon(new int[]{22, 32, 26}, new int[]{44, 50, 52}, 3);
        g.fillPolygon(new int[]{42, 32, 38}, new int[]{44, 50, 52}, 3);
        px(g, BRANCO, 42, 52, 2, 8);
        px(g, BRANCO, 41, 53, 1, 1);
        px(g, BRANCO, 41, 59, 4, 1);
        // Cabelo preto liso e longo emoldurando a mascara.
        g.setColor(PRETO);
        g.fillRoundRect(16, 8, 32, 46, 14, 14);
        // Mascara branca estilo kabuki.
        g.setColor(BRANCO);
        g.fillOval(21, 13, 22, 29);
        g.setColor(new Color(200, 198, 194));
        g.fillPolygon(new int[]{39, 43, 41, 36}, new int[]{20, 26, 38, 41}, 4);
        // Franja reta sobre a testa.
        px(g, PRETO, 21, 12, 22, 5);
        px(g, PRETO, 23, 17, 3, 2);
        px(g, PRETO, 38, 17, 3, 2);
        // Pintura preta: olhos angulosos e lagrimas escorrendo.
        g.setColor(PRETO);
        g.fillPolygon(new int[]{23, 30, 30, 26, 24}, new int[]{22, 23, 27, 29, 26}, 5);
        g.fillPolygon(new int[]{41, 34, 34, 38, 40}, new int[]{22, 23, 27, 29, 26}, 5);
        px(g, PRETO, 26, 29, 1, 6);
        px(g, PRETO, 38, 29, 1, 6);
        px(g, new Color(150, 30, 34), 27, 25, 2, 1);
        px(g, new Color(150, 30, 34), 35, 25, 2, 1);
        px(g, new Color(170, 168, 164), 31, 29, 2, 4);
        // Labios pretos costurados.
        px(g, PRETO, 28, 35, 8, 2);
        for (int x = 28; x <= 35; x += 2) px(g, PRETO, x, 34, 1, 4);
        // Baquetas erguidas.
        g.setColor(new Color(222, 196, 150));
        g.fillPolygon(new int[]{4, 7, 19, 17}, new int[]{62, 63, 33, 32}, 4);
        g.fillPolygon(new int[]{60, 57, 45, 47}, new int[]{62, 63, 33, 32}, 4);
        px(g, BRANCO, 17, 31, 2, 2);
        px(g, BRANCO, 45, 31, 2, 2);
    }

    /** Cliff: cabelo longo caindo no rosto, bigode, jaqueta jeans e baixo Rickenbacker. */
    private static void cliff(Graphics2D g) {
        Color cabelo = new Color(110, 62, 34), mecha = new Color(150, 92, 52), sombra = new Color(70, 38, 20);
        Color jeans = new Color(70, 96, 140), jeansClaro = new Color(110, 136, 178);
        // Jaqueta jeans.
        g.setColor(jeans);
        g.fillPolygon(new int[]{6, 22, 42, 58, 62, 2}, new int[]{50, 44, 44, 50, 64, 64}, 6);
        px(g, jeansClaro, 22, 46, 2, 18);
        px(g, jeansClaro, 40, 46, 2, 18);
        px(g, new Color(200, 190, 150), 23, 54, 1, 1);
        px(g, new Color(200, 190, 150), 40, 54, 1, 1);
        px(g, PRETO, 24, 44, 16, 20);
        // Cabelo comprido e ondulado por tras.
        g.setColor(cabelo);
        g.fillRoundRect(14, 8, 36, 50, 16, 16);
        px(g, PELE_SOMBRA, 28, 38, 8, 7);
        g.setColor(PELE);
        g.fillOval(22, 14, 20, 26);
        // Mechas caindo sobre a testa e as laterais do rosto (cabeca inclinada).
        g.setColor(cabelo);
        g.fillPolygon(new int[]{20, 44, 44, 38, 32, 26, 20}, new int[]{12, 12, 24, 20, 23, 19, 25}, 7);
        g.fillPolygon(new int[]{20, 26, 25, 21}, new int[]{20, 22, 44, 46}, 4);
        g.fillPolygon(new int[]{44, 38, 39, 43}, new int[]{20, 22, 44, 46}, 4);
        g.setColor(mecha);
        for (int x : new int[]{17, 22, 41, 46}) {
            for (int y = 16; y < 56; y += 6) px(g, mecha, x + ((y / 6) % 2), y, 1, 4);
        }
        px(g, sombra, 18, 54, 3, 4);
        px(g, sombra, 43, 54, 3, 4);
        // Olhos sob a franja, nariz e bigode farto.
        px(g, PELE_SOMBRA, 26, 25, 12, 2);
        px(g, PRETO, 28, 26, 3, 1);
        px(g, PRETO, 34, 26, 3, 1);
        px(g, PELE_SOMBRA, 31, 28, 2, 5);
        g.setColor(sombra);
        g.fillPolygon(new int[]{26, 32, 38, 37, 32, 27}, new int[]{34, 32, 34, 37, 35, 37}, 6);
        px(g, new Color(90, 40, 36), 30, 37, 4, 1);
        // Baixo Rickenbacker em diagonal: corpo com a "onda" caracteristica e braco subindo.
        g.setColor(new Color(150, 32, 30));
        g.fillPolygon(new int[]{30, 40, 50, 58, 62, 60, 52, 40, 32},
                new int[]{56, 50, 49, 44, 48, 58, 64, 64, 61}, 9);
        g.setColor(BRANCO);
        g.fillPolygon(new int[]{36, 44, 52, 50, 42}, new int[]{56, 52, 52, 58, 60}, 5);
        g.setColor(new Color(60, 40, 30));
        g.fillPolygon(new int[]{38, 41, 6, 3}, new int[]{54, 57, 33, 30}, 4);
        g.setColor(new Color(196, 196, 200));
        g.fillPolygon(new int[]{6, 2, 0, 3}, new int[]{33, 34, 29, 27}, 4);
        px(g, new Color(230, 230, 230), 44, 55, 4, 1);
        px(g, new Color(230, 230, 230), 44, 57, 4, 1);
        // Mao no braco do baixo.
        g.setColor(PELE);
        g.fillRoundRect(13, 34, 7, 6, 3, 3);
    }

    /** Dio: cabeleira cacheada volumosa, medalhao e os chifres do metal erguidos. */
    private static void dio(Graphics2D g) {
        Color cabelo = new Color(40, 30, 28), cacho = new Color(74, 58, 50);
        g.setColor(PRETO);
        g.fillPolygon(new int[]{8, 22, 42, 56, 60, 4}, new int[]{50, 44, 44, 50, 64, 64}, 6);
        px(g, new Color(196, 170, 90), 31, 50, 3, 3);
        px(g, new Color(150, 124, 60), 29, 46, 1, 4);
        px(g, new Color(150, 124, 60), 35, 46, 1, 4);
        // Cabeleira cacheada: varias bolhas formando o volume.
        g.setColor(cabelo);
        for (int[] c : new int[][]{{12, 10, 18}, {24, 4, 18}, {36, 8, 18}, {10, 24, 18}, {38, 22, 18}, {12, 36, 16}, {38, 34, 16}})
            g.fillOval(c[0], c[1], c[2], c[2]);
        g.setColor(cacho);
        for (int[] c : new int[][]{{16, 14}, {28, 8}, {40, 12}, {14, 28}, {44, 26}, {16, 40}, {42, 38}, {22, 20}, {38, 18}})
            g.fillOval(c[0], c[1], 4, 4);
        px(g, PELE_SOMBRA, 28, 38, 8, 7);
        g.setColor(PELE);
        g.fillOval(22, 16, 20, 25);
        g.setColor(cabelo);
        g.fillPolygon(new int[]{21, 43, 41, 32, 23}, new int[]{20, 20, 15, 12, 15}, 5);
        // Olhos firmes, sorriso aberto cantando.
        px(g, PRETO, 25, 24, 5, 1);
        px(g, PRETO, 34, 24, 5, 1);
        px(g, PRETO, 26, 27, 3, 2);
        px(g, PRETO, 35, 27, 3, 2);
        px(g, PELE_SOMBRA, 31, 28, 2, 5);
        px(g, new Color(70, 24, 26), 28, 34, 8, 3);
        px(g, BRANCO, 29, 34, 6, 1);
        // Mao direita com os chifres (indicador e minimo erguidos).
        g.setColor(PELE);
        g.fillPolygon(new int[]{48, 56, 60, 52}, new int[]{64, 64, 46, 46}, 4);
        g.fillRoundRect(48, 36, 12, 11, 3, 3);
        px(g, PELE, 48, 26, 3, 11);
        px(g, PELE, 57, 26, 3, 11);
        px(g, PELE_SOMBRA, 51, 38, 6, 2);
        px(g, PELE_SOMBRA, 51, 41, 6, 1);
        px(g, PELE_SOMBRA, 46, 40, 3, 4);
    }

    /** Tenacious D: a dupla lado a lado com violoes, o maior tributo ao rock. */
    private static void tenaciousD(Graphics2D g) {
        Color violao = new Color(196, 140, 70), boca = new Color(36, 24, 20);
        // Jack: cabelo escuro na altura do pescoco, sobrancelhas arqueadas e barba por fazer.
        g.setColor(new Color(30, 28, 34));
        g.fillPolygon(new int[]{0, 10, 26, 32, 32, 0}, new int[]{52, 46, 46, 52, 64, 64}, 6);
        g.setColor(new Color(44, 32, 26));
        g.fillRoundRect(4, 12, 24, 30, 10, 10);
        g.setColor(PELE);
        g.fillOval(7, 16, 18, 22);
        px(g, new Color(44, 32, 26), 7, 15, 18, 5);
        px(g, PRETO, 9, 21, 5, 1);
        px(g, PRETO, 18, 21, 5, 1);
        px(g, PRETO, 10, 24, 3, 2);
        px(g, PRETO, 19, 24, 3, 2);
        px(g, PELE_SOMBRA, 15, 25, 2, 5);
        px(g, boca, 12, 31, 8, 3);
        px(g, BRANCO, 13, 31, 6, 1);
        px(g, new Color(150, 106, 84), 10, 34, 12, 3);
        px(g, PELE_SOMBRA, 13, 38, 6, 8);
        // Kyle: careca brilhando no alto e barba cheia.
        g.setColor(new Color(60, 64, 76));
        g.fillPolygon(new int[]{32, 38, 54, 64, 64, 32}, new int[]{52, 46, 46, 52, 64, 64}, 6);
        px(g, PELE_SOMBRA, 45, 38, 6, 8);
        g.setColor(PELE);
        g.fillOval(38, 14, 20, 24);
        px(g, new Color(250, 214, 190), 44, 16, 6, 2);
        px(g, new Color(120, 90, 66), 38, 22, 2, 8);
        px(g, new Color(120, 90, 66), 56, 22, 2, 8);
        px(g, PRETO, 41, 23, 5, 1);
        px(g, PRETO, 50, 23, 5, 1);
        px(g, PRETO, 42, 25, 3, 2);
        px(g, PRETO, 51, 25, 3, 2);
        px(g, PELE_SOMBRA, 47, 26, 2, 5);
        g.setColor(new Color(150, 112, 80));
        g.fillPolygon(new int[]{39, 57, 55, 48, 41}, new int[]{30, 30, 40, 44, 40}, 5);
        px(g, boca, 44, 33, 8, 2);
        // Dois violoes cruzados na frente.
        g.setColor(violao);
        g.fillOval(2, 50, 18, 14);
        g.fillOval(44, 50, 18, 14);
        px(g, PRETO, 9, 55, 4, 4);
        px(g, PRETO, 51, 55, 4, 4);
        g.setColor(new Color(90, 56, 30));
        g.fillPolygon(new int[]{14, 17, 36, 33}, new int[]{54, 57, 40, 38}, 4);
        g.fillPolygon(new int[]{50, 47, 28, 31}, new int[]{54, 57, 40, 38}, 4);
    }
}
