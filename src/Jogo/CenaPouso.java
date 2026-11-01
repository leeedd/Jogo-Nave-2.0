package Jogo;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

/**
 * Cutscene que abre os creditos. O jogador escolhe o show; a nave pousa de frente no festival,
 * o piloto desce pela escada, tira o macacao laranja e esta com a camisa da banda escolhida
 * (o emblema dela tambem acende no telao do palco). Ele corre para a multidao
 * e cai no show de bracos para cima. Depois seguem os creditos.
 */
final class CenaPouso {
    private static final BufferedImage PALCO = ler("palco_festival.png");
    /** Piloto (12 quadros): parado, escada, andando x2, ziper, tirando, camisa, chifrinho, correndo x2 (costas), bracos para cima, pulo.
     *  final_piloto_sprites.png e o Wenger; enquanto ele nao chega, usa a folha antiga. */
    private static final BufferedImage FLAVIO = ler("final_piloto_sprites.png") != null ? ler("final_piloto_sprites.png") : ler("final_flavio_sprites.png");
    /** Folha da galera: 4 quadros do Wenger e 4 fas de costas (os fas enchem a plateia). */
    private static final BufferedImage WENGER = ler("final_wenger_sprites.png");
    private static final BufferedImage[] FRENTE = new BufferedImage[5];
    static { for (int i = 0; i < 5; i++) FRENTE[i] = ler("nave_" + i + "_frente.png"); }
    /** Onde fica a estampa no peito/costas de cada quadro do Flavio (fracoes do quadro). */
    /** Estampa no peito/costas do Wenger (folha final_piloto_sprites.png); no peito fica abaixo da barba. */
    private static final double[][] ESTAMPA_PILOTO = {null, null, null, null, null,
        {.37, .41, .70, .56}, {.30, .41, .62, .56}, {.32, .39, .64, .54},
        {.347, .317, .727, .587}, {.379, .318, .734, .570}, {.354, .33, .685, .54}, {.321, .31, .687, .54}};
    private static final boolean TEM_PILOTO = ler("final_piloto_sprites.png") != null;
    private static final double[][] ESTAMPA_FLAVIO = {null, null, null, null, null,
        {.333, .316, .634, .500}, {.352, .317, .640, .493}, {.320, .300, .592, .465},
        {.430, .393, .674, .542}, {.439, .384, .703, .545}, {.350, .308, .633, .480}, {.372, .263, .616, .411}};
    /** Telao preto do palco (fracoes da arte do palco), onde acende o emblema da banda. */
    private static final double TELAO_X0 = 568 / 1586.0, TELAO_Y0 = 348 / 992.0, TELAO_X1 = 1018 / 1586.0, TELAO_Y1 = 558 / 992.0;
    private static final double[][] ESTAMPA = TEM_PILOTO ? ESTAMPA_PILOTO : ESTAMPA_FLAVIO;
    static final double DURACAO = 21;
    private static final int CHAO = 572;

    private final int modelo;
    private double t;
    /** Show escolhido (indice de Fase.BANDAS) ou -1 enquanto o jogador ainda esta escolhendo. */
    private int banda = -1, cursor = CamisaShow.SLAYER;
    private double tEscolha;
    private BufferedImage flavio = FLAVIO, fasLonge, fasPerto;

    /** Palcos abertos: bit por banda (1 a 7), ganho com o passe de cada uma. A Lula e chefe generico, nao tem show. */
    private final int abertos;
    private double avisoFechado;
    CenaPouso(int modelo) { this(modelo, 0xFF); }
    CenaPouso(int modelo, int reliquias) {
        this.modelo = modelo; abertos = reliquias & 0xFE;
        cursor = 0; for (int i = Fase.BANDAS.length - 1; i > 0; i--) if (aberto(i)) { cursor = i; break; }
        // sem passe: zeramento comum; com um passe so: direto para aquele show. Escolha so com dois ou mais.
        int passes = Integer.bitCount(abertos);
        if (passes == 0) escolher(COMUM);
        else if (passes == 1) escolher(Integer.numberOfTrailingZeros(abertos));
    }
    /** Zeramento comum: camiseta lisa, trilha do jogo e as bandas se revezando nos creditos. */
    static final int COMUM = 0;
    boolean comum() { return banda == COMUM; }
    /** Opcao extra para quem juntou todas as reliquias: o festival inteiro, banda por banda. */
    static final int FESTIVAL = 8;
    boolean aberto(int b) { return b == FESTIVAL ? palcosAbertos() == 7 : b >= 1 && b <= 7 && (abertos >> b & 1) != 0; }
    /** Opcoes da tela, na ordem: as 7 bandas e, com todos os passes, o festival completo. */
    private int[] opcoes() { return palcosAbertos() == 7 ? new int[]{1, 2, 3, 4, 5, 6, 7, FESTIVAL} : new int[]{1, 2, 3, 4, 5, 6, 7}; }
    int palcosAbertos() { return Integer.bitCount(abertos); }
    boolean escolhendo() { return banda < 0; }
    int banda() { return banda; }
    void mover(int d) { if (banda < 0) { int[] o = opcoes(); int i = 0; for (int k = 0; k < o.length; k++) if (o[k] == cursor) i = k; cursor = o[Math.floorMod(i + d, o.length)]; SomJogo.tocar(SomJogo.Efeito.INTERDICAO); } }
    void moverLinha(int d) { if (banda < 0) mover(d * 4); }
    /** Fecha a escolha: a camisa do Flavio e o telao do festival passam a ser dessa banda. */
    void escolher(int b) {
        if (banda >= 0) return;
        banda = Math.max(0, Math.min(aberto(FESTIVAL) ? FESTIVAL : Fase.BANDAS.length - 1, b));
        // no festival completo a camisa e a da Lula, onde a jornada comecou
        if (FLAVIO != null) flavio = banda == COMUM || banda == FESTIVAL ? FLAVIO : estampar(FLAVIO, CamisaShow.emblema(banda));
        if (banda != COMUM) MusicaJogo.tocarShow(banda);
        if (WENGER != null) { fasLonge = escurecer(WENGER, .45f); fasPerto = escurecer(WENGER, .7f); }
    }
    void confirmar() { if (!aberto(cursor)) { avisoFechado = 1.6; SomJogo.tocar(SomJogo.Efeito.ERRO); return; } escolher(cursor); }
    void atualizar(double dt) { if (banda < 0) { tEscolha += Math.max(0, dt); avisoFechado = Math.max(0, avisoFechado - Math.max(0, dt)); return; } t = Math.min(DURACAO, t + Math.max(0, dt)); }
    boolean terminou() { return t >= DURACAO; }
    void pular() { if (banda < 0) confirmar(); else t = DURACAO; }

    private static double suave(double x) { x = Math.max(0, Math.min(1, x)); return x * x * (3 - 2 * x); }

    void desenhar(Graphics2D base) {
        if (banda < 0) { desenharEscolha(base); return; }
        Graphics2D g = (Graphics2D) base.create();
        int W = Config.LARGURA, H = Config.ALTURA;
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.setColor(Color.BLACK); g.fillRect(0, 0, W, H);
        if (PALCO != null) g.drawImage(PALCO, 0, 0, W, H, null);
        telao(g, W, H);
        luzes(g, W);
        fogos(g);
        // fas la no fundo, colados na grade do palco, balancando
        if (fasLonge != null) for (int i = 0; i < 16; i++) {
            double bal = Math.abs(Math.sin(t * 3.2 + i * 1.3)) * 4;
            sprite(g, fasLonge, 8, 4 + (i * 3) % 4, 40 + i * 60 + (i % 2) * 18, (int) (488 - bal), .2, i % 3 == 0);
        }
        nave(g);
        flavioEWenger(g);
        // a galera da frente chega no fim, quando o show pega fogo
        if (fasPerto != null && t > 16) {
            double sob = suave((t - 16) / 1.2);
            for (int i = 0; i < 7; i++) {
                double bal = Math.abs(Math.sin(t * 4 + i * 2.1)) * 8;
                if (i == 3) continue; // vao no meio da galera: e ali que o piloto esta
                sprite(g, fasPerto, 8, 4 + (i * 5) % 4, 40 + i * 140, (int) (H + 200 - sob * 110 - bal), .42, i % 2 == 1);
            }
        }
        if (t < .6) cobrir(g, 1 - t / .6);
        if (t > DURACAO - 1) cobrir(g, t - (DURACAO - 1));
        g.dispose();
    }

    private static void cobrir(Graphics2D g, double a) {
        Graphics2D q = (Graphics2D) g.create(); q.setComposite(AlphaComposite.SrcOver.derive((float) Math.max(0, Math.min(1, a))));
        q.setColor(Color.BLACK); q.fillRect(0, 0, Config.LARGURA, Config.ALTURA); q.dispose();
    }

    /** O emblema da banda escolhida acende no telao, piscando como lampada velha antes de firmar. */
    private void telao(Graphics2D g, int W, int H) {
        // festival completo: o telao passa pelos emblemas de todas as bandas
        Image e = CamisaShow.emblema(banda == FESTIVAL || banda == COMUM ? 1 + (int) (t / 1.5) % 7 : banda);
        if (e == null || t < 1) return;
        boolean pisca = t < 1.8 && (int) (t * 14) % 3 == 0;
        if (pisca) return;
        int x0 = (int) (TELAO_X0 * W), y0 = (int) (TELAO_Y0 * H), x1 = (int) (TELAO_X1 * W), y1 = (int) (TELAO_Y1 * H);
        double pulso = 1 + .03 * Math.sin(t * 5);
        double s = Math.min((x1 - x0) / (double) e.getWidth(null), (y1 - y0) / (double) e.getHeight(null)) * .92 * pulso;
        int ew = (int) (e.getWidth(null) * s), eh = (int) (e.getHeight(null) * s);
        g.drawImage(e, (x0 + x1 - ew) / 2, (y0 + y1 - eh) / 2, ew, eh, null);
    }

    /** Fachos de luz varrendo do teto do palco. */
    private void luzes(Graphics2D g, int W) {
        for (int k = 0; k < 6; k++) {
            double a = Math.sin(t * 1.1 + k * 1.9); int x0 = 280 + k * 80;
            g.setColor(new Color(k % 2 == 0 ? 255 : 170, 50, k % 2 == 0 ? 90 : 255, 26));
            int pe = x0 + (int) (a * 260);
            g.fillPolygon(new int[]{x0, pe - 60, pe + 60}, new int[]{150, 500, 500}, 3);
        }
    }

    /** Fogos estourando no ceu, em pixel: anel de pontos que abre e apaga. */
    private void fogos(Graphics2D g) {
        for (int k = 0; k < 5; k++) {
            double ciclo = 2.6 + k * .4, fase = (t + k * 1.3) % ciclo / ciclo;
            if (t < 2 + k) continue;
            java.util.Random r = new java.util.Random(k * 31 + (long) ((t + k * 1.3) / ciclo));
            int cx = 60 + r.nextInt(840), cy = 30 + r.nextInt(110), raio = (int) (8 + fase * 55);
            Color c = new Color[]{new Color(255, 80, 90), new Color(200, 100, 255), new Color(255, 200, 90)}[r.nextInt(3)];
            int al = (int) (255 * (1 - fase));
            g.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), Math.max(0, al)));
            for (int i = 0; i < 16; i++) { double an = i * Math.PI / 8; g.fillRect(cx + (int) (Math.cos(an) * raio), cy + (int) (Math.sin(an) * raio), 3, 3); }
        }
    }

    /** A nave de frente, como no hangar: desce na vertical com os jatos e pousa na pista. */
    private void nave(Graphics2D g) {
        BufferedImage frente = FRENTE[Math.max(0, Math.min(4, modelo))];
        double p = suave(t / 4.0);
        int cx = 250, fh = 210;
        int ny = CHAO - fh + 6 - (int) ((1 - p) * (CHAO + 80));
        int sombra = (int) (80 + 220 * p); g.setColor(new Color(0, 0, 0, (int) (40 + 90 * p))); g.fillOval(cx - sombra / 2, CHAO - 12, sombra, 24);
        if (frente == null) return;
        int fw = frente.getWidth() * fh / frente.getHeight();
        if (p < 1) for (int k = -1; k <= 1; k += 2) {
            int jx = cx + k * fw / 4;
            for (int j = 0; j < 7; j++) { g.setColor(new Color(120, 200, 255, Math.max(0, 190 - j * 26))); g.fillRect(jx - 7 + (int) (Math.sin(t * 40 + j) * 2), ny + fh - 12 + j * 11, 14, 11); }
        }
        g.drawImage(frente, cx - fw / 2, ny, fw, fh, null);
        if (t > 3.2 && t < 5.6) { // poeira do pouso
            double q = (t - 3.2) / 2.4; g.setColor(new Color(150, 120, 150, (int) (140 * (1 - q))));
            for (int k = 0; k < 14; k++) { int r = (int) (12 + q * 44); g.fillOval(cx - 150 + k * 22 - r / 2, CHAO - r / 2 - 6, r, r / 2); }
        }
    }

    private void flavioEWenger(Graphics2D g) {
        if (flavio == null || t < 4.4) return;
        int q; double x, pe, s = .6;
        if (t < 5.6) { q = 1; x = 400; pe = CHAO - 70 * (1 - (t - 4.4) / 1.2); }          // desce a escada
        else if (t < 6.4) { q = 0; x = 400; pe = CHAO; }                                 // olha o festival
        else if (t < 8) { q = 2 + (int) (t / .2) % 2; x = 400 + (t - 6.4) / 1.6 * 130; pe = CHAO; } // anda
        else if (t < 8.9) { q = 4; x = 530; pe = CHAO; }                                 // ziper
        else if (t < 9.8) { q = 5; x = 530; pe = CHAO; }                                 // tira o macacao
        else if (t < 10.6) { q = 6; x = 530; pe = CHAO; }                                // a camisa da banda
        else if (t < 12) { q = 7; x = 530; pe = CHAO; }                                  // chifrinho
        else if (t < 14.6) { double c = suave((t - 12) / 2.6); q = 8 + (int) (t / .14) % 2; x = 530 - c * 90; pe = CHAO - c * 78; s = .6 - c * .24; } // corre
        else if (t < 16) { q = 10; x = 440; pe = CHAO - 78; s = .36; }                   // chega na galera
        else { q = (int) ((t - 16) / .45) % 3 == 2 ? 11 : 10; x = 440; pe = CHAO - 78 - (q == 11 ? 10 : 0); s = .36; } // pula com a galera
        // o macacao fica jogado no chao depois que ele tira
        if (t >= 9.8) { g.setColor(new Color(230, 110, 30)); g.fillRoundRect(500, CHAO - 10, 60, 12, 6, 6); g.setColor(new Color(160, 70, 20)); g.fillRect(508, CHAO - 6, 44, 3); }
        sprite(g, flavio, 12, q, (int) x, (int) pe, s, false);
    }

    /** Desenha o quadro q de uma tira de n quadros com o pe em (cx, pe). */
    private static void sprite(Graphics2D g, BufferedImage tira, int n, int q, int cx, int pe, double s, boolean espelhar) {
        int fw = tira.getWidth() / n, fh = tira.getHeight(), w = (int) (fw * s), h = (int) (fh * s);
        int x0 = cx - w / 2, y0 = pe - h;
        if (espelhar) g.drawImage(tira, x0 + w, y0, x0, y0 + h, q * fw, 0, q * fw + fw, fh, null);
        else g.drawImage(tira, x0, y0, x0 + w, y0 + h, q * fw, 0, q * fw + fw, fh, null);
    }

    /** Pinta o emblema da banda na camisa preta do Flavio (so onde e camisa, nunca em braco ou mao). */
    static BufferedImage estampar(BufferedImage tira, Image emblema) {
        BufferedImage b = new BufferedImage(tira.getWidth(), tira.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = b.createGraphics(); g.drawImage(tira, 0, 0, null); g.dispose();
        if (emblema == null) return b;
        int fw = tira.getWidth() / 12, fh = tira.getHeight();
        for (int k = 0; k < 12; k++) {
            if (ESTAMPA[k] == null) continue;
            int x0 = k * fw + (int) (ESTAMPA[k][0] * fw), y0 = (int) (ESTAMPA[k][1] * fh), x1 = k * fw + (int) (ESTAMPA[k][2] * fw), y1 = (int) (ESTAMPA[k][3] * fh);
            int cw = x1 - x0, ch = y1 - y0;
            double s = Math.min(cw / (double) emblema.getWidth(null), ch / (double) emblema.getHeight(null));
            int ew = Math.max(1, (int) (emblema.getWidth(null) * s)), eh = Math.max(1, (int) (emblema.getHeight(null) * s));
            BufferedImage e = new BufferedImage(ew, eh, BufferedImage.TYPE_INT_ARGB);
            Graphics2D eg = e.createGraphics(); eg.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            eg.drawImage(emblema, 0, 0, ew, eh, null); eg.dispose();
            int ex = x0 + (cw - ew) / 2, ey = y0 + (ch - eh) / 2;
            for (int y = 0; y < eh; y++) for (int x = 0; x < ew; x++) {
                int o = b.getRGB(ex + x, ey + y), r = o >> 16 & 255, gg = o >> 8 & 255, bl = o & 255;
                if ((o >>> 24) < 200 || Math.max(r, Math.max(gg, bl)) > 70) continue; // so tecido preto
                int p = e.getRGB(x, y); double f = (p >>> 24) / 255.0 * .9;
                if (f <= .05) continue;
                int nr = (int) (r * (1 - f) + (p >> 16 & 255) * .85 * f), ng = (int) (gg * (1 - f) + (p >> 8 & 255) * .85 * f), nb = (int) (bl * (1 - f) + (p & 255) * .85 * f);
                b.setRGB(ex + x, ey + y, 0xFF000000 | nr << 16 | ng << 8 | nb);
            }
        }
        return b;
    }

    private static BufferedImage escurecer(BufferedImage src, float f) {
        return new java.awt.image.RescaleOp(new float[]{f, f, f * 1.1f, 1}, new float[4], null).filter(src, null);
    }

    private static BufferedImage ler(String n) {
        java.net.URL u = CenaPouso.class.getResource("/ImagemIcon/" + n);
        if (u == null) return null;
        try { BufferedImage i = javax.imageio.ImageIO.read(u); if (i == null) return null;
            BufferedImage b = new BufferedImage(i.getWidth(), i.getHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = b.createGraphics(); g.drawImage(i, 0, 0, null); g.dispose(); return b;
        } catch (java.io.IOException e) { return null; }
    }

    /** Antes do pouso: o jogador escolhe qual palco do festival vai ver. */
    private void desenharEscolha(Graphics2D base) {
        Graphics2D g = (Graphics2D) base.create();
        int W = Config.LARGURA, H = Config.ALTURA;
        g.setColor(new Color(10, 6, 16)); g.fillRect(0, 0, W, H);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 30)); g.setColor(new Color(250, 225, 140));
        String titulo = "QUAL SHOW VOCE VAI VER?"; g.drawString(titulo, (W - g.getFontMetrics().stringWidth(titulo)) / 2, 70);
        g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 14)); g.setColor(new Color(190, 170, 200));
        int n0 = palcosAbertos();
        String sub = n0 == 7 ? "FESTIVAL COMPLETO! TODOS OS 7 PALCOS ABERTOS." : n0 + " DE 7 PALCOS ABERTOS. CADA PASSE ABRE UM PALCO."; g.drawString(sub, (W - g.getFontMetrics().stringWidth(sub)) / 2, 98);
        for (int i = 1; i <= 7; i++) {
            int k = i - 1, cx = k < 4 ? 135 + k * 230 : 250 + (k - 4) * 230, cy = k < 4 ? 195 : 380; boolean sel = i == cursor;
            double pulso = sel ? 1 + .05 * Math.sin(tEscolha * 6) : .82;
            if (sel) { g.setColor(new Color(200, 40, 40)); g.fillRect(cx - 100, cy - 85, 200, 175); g.setColor(new Color(20, 10, 16)); g.fillRect(cx - 96, cy - 81, 192, 167); }
            Image e = CamisaShow.emblema(i);
            boolean ab = aberto(i);
            if (e != null && !ab) {
                // palco fechado: so a sombra do emblema e um cadeado
                double s = Math.min(150.0 / e.getWidth(null), 110.0 / e.getHeight(null));
                int ew = (int) (e.getWidth(null) * s), eh = (int) (e.getHeight(null) * s);
                Graphics2D q = (Graphics2D) g.create(); q.setComposite(AlphaComposite.SrcOver.derive(.18f));
                q.drawImage(e, cx - ew / 2, cy - 10 - eh / 2, ew, eh, null); q.dispose();
                g.setColor(new Color(120, 110, 120)); g.fillRect(cx - 14, cy - 12, 28, 22); g.drawArc(cx - 10, cy - 30, 20, 30, 0, 180);
                g.setColor(new Color(30, 20, 30)); g.fillRect(cx - 2, cy - 4, 4, 8);
            } else if (e != null) {
                double s = Math.min(170.0 / e.getWidth(null), 125.0 / e.getHeight(null)) * pulso;
                int ew = (int) (e.getWidth(null) * s), eh = (int) (e.getHeight(null) * s);
                Graphics2D q = (Graphics2D) g.create(); if (!sel) q.setComposite(AlphaComposite.SrcOver.derive(.55f));
                q.drawImage(e, cx - ew / 2, cy - 10 - eh / 2, ew, eh, null); q.dispose();
            }
            g.setFont(new Font(Font.MONOSPACED, Font.BOLD, sel ? 15 : 13)); g.setColor(!ab ? new Color(90, 80, 90) : sel ? Color.WHITE : new Color(150, 130, 150));
            String n = ab ? Fase.BANDAS[i] : "PALCO FECHADO"; g.drawString(n, cx - g.getFontMetrics().stringWidth(n) / 2, cy + 75);
        }
        if (opcoes().length == 8) {
            boolean sel = cursor == FESTIVAL; int bx = W / 2 - 170, by = 490;
            g.setColor(sel ? new Color(255, 200, 60) : new Color(120, 90, 40)); g.fillRect(bx, by, 340, 44);
            g.setColor(new Color(20, 10, 16)); g.fillRect(bx + 4, by + 4, 332, 36);
            g.setFont(new Font(Font.MONOSPACED, Font.BOLD, sel ? 22 : 19)); g.setColor(sel ? new Color(255, 220, 120) : new Color(170, 140, 90));
            String f = "FESTIVAL COMPLETO"; g.drawString(f, W / 2 - g.getFontMetrics().stringWidth(f) / 2, by + 30);
        }
        g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 14)); g.setColor(new Color(250, 225, 140));
        String dica = avisoFechado > 0 ? "FALTOU A RELIQUIA DESSA BANDA. FICA PRA PROXIMA JORNADA." : "SETAS: ESCOLHER    ENTER: IR PARA O SHOW";
        if (avisoFechado > 0) g.setColor(new Color(255, 110, 100)); g.drawString(dica, (W - g.getFontMetrics().stringWidth(dica)) / 2, H - 30);
        if (tEscolha < .4) { g.setComposite(AlphaComposite.SrcOver.derive((float) (1 - tEscolha / .4))); g.setColor(Color.BLACK); g.fillRect(0, 0, W, H); }
        g.dispose();
    }
    /** Plaquinha de nome em pixel: caixa preta com borda vermelha e setinha para baixo. */
    private static void etiqueta(Graphics2D g, String nome, int cx, int y, float a) {
        Graphics2D q = (Graphics2D) g.create(); q.setComposite(AlphaComposite.SrcOver.derive(a));
        q.setFont(new java.awt.Font(java.awt.Font.MONOSPACED, java.awt.Font.BOLD, 16));
        int w = q.getFontMetrics().stringWidth(nome) + 16, x = cx - w / 2;
        q.setColor(new Color(12, 8, 10)); q.fillRect(x, y, w, 24); q.fillPolygon(new int[]{cx - 7, cx + 7, cx}, new int[]{y + 24, y + 24, y + 32}, 3);
        q.setColor(new Color(200, 40, 40)); q.drawRect(x, y, w, 24); q.drawRect(x + 2, y + 2, w - 4, 20);
        q.setColor(new Color(250, 235, 200)); q.drawString(nome, x + 8, y + 18); q.dispose();
    }
}
