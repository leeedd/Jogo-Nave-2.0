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
    static final double DURACAO = 24;
    private static final int CHAO = 572;

    private final int modelo;
    private double t;
    /** Show escolhido (indice de Fase.BANDAS) ou -1 enquanto o jogador ainda esta escolhendo. */
    private int banda = -1, cursor = CamisaShow.SLAYER;
    private double tEscolha;
    private BufferedImage flavio = FLAVIO, fasFundo, fasLonge, fasMeio, fasPerto;
    /** Onde o piloto fica no meio da galera. */
    private static final int PILOTO_X = 600;

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
        if (WENGER != null) { fasFundo = escurecer(WENGER, .3f); fasLonge = escurecer(WENGER, .45f); fasMeio = escurecer(WENGER, .6f); fasPerto = escurecer(WENGER, .7f); }
    }
    void confirmar() { if (!aberto(cursor)) { avisoFechado = 1.6; SomJogo.tocar(SomJogo.Efeito.ERRO); return; } escolher(cursor); }
    void atualizar(double dt) { if (banda < 0) { tEscolha += Math.max(0, dt); avisoFechado = Math.max(0, avisoFechado - Math.max(0, dt)); return; } t = Math.min(DURACAO, t + Math.max(0, dt)); }
    boolean terminou() { return t >= DURACAO; }
    void pular() { if (banda < 0) confirmar(); else t = DURACAO; }

    private static double suave(double x) { x = Math.max(0, Math.min(1, x)); return x * x * (3 - 2 * x); }

    /** Final do zeramento em paginas de gibi: cada quadro e uma cena animada recortada dentro do painel. */
    private BufferedImage tela;
    private Graphics2D telaG() {
        if (tela == null) tela = new BufferedImage(Config.LARGURA, Config.ALTURA, BufferedImage.TYPE_INT_RGB);
        Graphics2D c = tela.createGraphics(); c.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        c.setColor(Color.BLACK); c.fillRect(0, 0, Config.LARGURA, Config.ALTURA); return c;
    }
    private static final double P2 = 5.6, P3 = 11.6, P4 = 17.2;

    void desenhar(Graphics2D base) {
        if (banda < 0) { desenharEscolha(base); return; }
        Graphics2D g = (Graphics2D) base.create();
        int W = Config.LARGURA, H = Config.ALTURA; double tReal = t;
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        papel(g, W, H);
        try {
            if (tReal < P2) {
                // pagina 1: saida da dobra e o festival visto de cima
                Graphics2D c = telaG(); cenaDobra(c, tReal); c.dispose();
                painel(g, 24, 22, 912, 268, 0, 0, 960, 600, tReal, 0, -.008);
                legenda(g, 40, 38, "SISTEMA DO FESTIVAL. A TURNE ACABOU... O SHOW NAO.");
                if (tReal > 1.1 && tReal < 2.6) sfx(g, "VWOOOOM!", 700, 220, 64, new Color(120, 220, 255), -.12, tReal - 1.1);
                if (tReal > 2.2) {
                    c = telaG(); cenaAerea(c, tReal - 2.2); c.dispose();
                    painel(g, 24, 306, 912, 272, 0, 0, 960, 600, tReal, 2.2, .006);
                    legenda(g, 40, 322, "LA EMBAIXO: 100 MIL HEADBANGERS.");
                }
            } else if (tReal < P3) {
                // pagina 2: o pouso e o piloto descendo
                double tl = tReal - P2;
                Graphics2D c = telaG(); t = Math.min(4.6, tl); quadroPouso(c); c.dispose();
                painel(g, 24, 22, 560, 556, 20, 180, 560, 600, tReal, P2, -.006);
                legenda(g, 40, 38, "POUSO AUTORIZADO.");
                if (tl > 2.6 && tl < 4.4) sfx(g, "FSSSHHH!", 330, 520, 48, new Color(255, 200, 90), .1, tl - 2.6);
                if (tl > 2.8) {
                    c = telaG(); t = Math.min(9.39, 4.4 + (tl - 2.8) * 1.75); quadroPouso(c); c.dispose();
                    painel(g, 600, 60, 336, 518, 300, 260, 520, 600, tReal, P2 + 2.8, .012);
                    legenda(g, 612, 76, "HORA DE TIRAR O MACACAO.");
                }
            } else if (tReal < P4) {
                // pagina 3: corre, mergulha e a galera carrega ele
                double tl = tReal - P3;
                Graphics2D c = telaG(); cenaShow(c, Math.min(2.7, tl), 0); c.dispose();
                painel(g, 24, 22, 912, 268, 0, 150, 960, 500, tReal, P3, .007);
                if (tl > 1.1 && tl < 2.8) sfx(g, "STAGE DIVE!!", 560, 120, 58, new Color(255, 90, 70), -.14, tl - 1.1);
                if (tl > 2.4) {
                    c = telaG(); cenaShow(c, tl - 2.4, 1); c.dispose();
                    painel(g, 24, 306, 912, 272, 0, 230, 960, 560, tReal, P3 + 2.4, -.006);
                    legenda(g, 40, 322, "E A GALERA SEGUROU O CACADOR NOS OMBROS.");
                    if (((int) ((tl - 2.4) * 1.1)) % 2 == 0) sfx(g, "HEY! HEY! HEY!", 640, 560, 34, new Color(255, 230, 120), .06, (tl - 2.4) % 1.8);
                }
            } else {
                // pagina 4: pagina inteira, o show; ele la no meio, sendo carregado
                double tl = tReal - P4;
                Graphics2D c = telaG(); cenaShow(c, tl, 2); c.dispose();
                painel(g, 24, 22, 912, 556, 0, 0, 960, 600, tReal, P4, 0);
                if (tl > .8) legenda(g, 40, 38, "...E FOI ASSIM QUE O CACADOR VIU O SHOW.");
            }
        } finally { t = tReal; }
        if (t < .5) cobrir(g, 1 - t / .5);
        if (t > DURACAO - 1) cobrir(g, t - (DURACAO - 1));
        g.dispose();
    }

    /** Fundo de papel de gibi, amarelado, com pontinhos de retícula. */
    private static void papel(Graphics2D g, int W, int H) {
        g.setColor(new Color(236, 226, 200)); g.fillRect(0, 0, W, H);
        g.setColor(new Color(205, 190, 160)); for (int y = 0; y < H; y += 8) for (int x = (y / 8) % 2 * 4; x < W; x += 8) g.fillRect(x, y, 2, 2);
    }
    /** Painel: recorte da cena (sx,sy,sw,sh) dentro do quadro, com borda grossa; entra com um tranco de zoom. */
    private void painel(Graphics2D g, int x, int y, int w, int h, int sx, int sy, int sx1, int sy1, double agora, double inicio, double giro) {
        int sw = sx1 - sx, sh = sy1 - sy;
        double e = Math.min(1, (agora - inicio) / .28), z = .86 + .14 * suave(e);
        Graphics2D p = (Graphics2D) g.create();
        p.translate(x + w / 2.0, y + h / 2.0); p.rotate(giro); p.scale(z, z); p.translate(-w / 2.0, -h / 2.0);
        p.setColor(new Color(0, 0, 0, 90)); p.fillRect(8, 8, w, h);                       // sombra do quadro
        // recorta mantendo a proporcao do quadro
        double ar = w / (double) h; int cw = sw, ch = (int) (sw / ar);
        if (ch > sh) { ch = sh; cw = (int) (sh * ar); }
        int cx = sx + (sw - cw) / 2, cy = sy + (sh - ch) / 2;
        p.drawImage(tela, 0, 0, w, h, cx, cy, cx + cw, cy + ch, null);
        p.setColor(Color.BLACK); p.setStroke(new java.awt.BasicStroke(6)); p.drawRect(0, 0, w, h);
        p.setColor(Color.WHITE); p.setStroke(new java.awt.BasicStroke(2)); p.drawRect(-5, -5, w + 10, h + 10);
        p.dispose();
    }
    /** Caixa de legenda amarela, como narrador de gibi. */
    private static void legenda(Graphics2D g, int x, int y, String s) {
        g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 15)); int w = g.getFontMetrics().stringWidth(s) + 20;
        g.setColor(new Color(255, 228, 110)); g.fillRect(x, y, w, 28);
        g.setColor(Color.BLACK); g.setStroke(new java.awt.BasicStroke(3)); g.drawRect(x, y, w, 28); g.drawString(s, x + 10, y + 19);
    }
    /** Onomatopeia grande, inclinada, com contorno preto; estoura ao entrar. */
    private static void sfx(Graphics2D g, String s, int x, int y, int tam, Color cor, double giro, double idade) {
        double z = idade < .18 ? .4 + idade / .18 * .8 : 1.2 - Math.min(.2, (idade - .18) * .6);
        Graphics2D q = (Graphics2D) g.create(); q.translate(x, y); q.rotate(giro); q.scale(z, z);
        java.awt.Font f = new Font(Font.SANS_SERIF, Font.BOLD | Font.ITALIC, tam);
        java.awt.Shape forma = f.createGlyphVector(q.getFontRenderContext(), s).getOutline();
        java.awt.Rectangle b = forma.getBounds(); q.translate(-b.width / 2.0, b.height / 2.0);
        q.setStroke(new java.awt.BasicStroke(9, java.awt.BasicStroke.CAP_ROUND, java.awt.BasicStroke.JOIN_ROUND)); q.setColor(Color.BLACK); q.draw(forma);
        q.setColor(cor); q.fill(forma);
        q.setStroke(new java.awt.BasicStroke(2)); q.setColor(Color.WHITE); q.draw(forma); q.dispose();
    }

    /** Saida da dobra espacial: tunel de luz, clarao e a nave chegando de frente. */
    private void cenaDobra(Graphics2D g, double tl) {
        int W = Config.LARGURA, H = Config.ALTURA, cx = W / 2, cy = H / 2;
        java.util.Random r = new java.util.Random(5);
        double vel = tl < 1.2 ? 1 : Math.max(0, 1 - (tl - 1.2) * 1.5);
        for (int k = 0; k < 260; k++) {
            double an = r.nextDouble() * Math.PI * 2, d0 = (r.nextDouble() * 600 + tl * 900 * (.3 + vel)) % 620, len = 6 + vel * 90;
            int c = 140 + r.nextInt(110); g.setColor(new Color(c / 2, c * 3 / 4, c));
            g.setStroke(new java.awt.BasicStroke(1 + (float) vel * 2));
            g.drawLine(cx + (int) (Math.cos(an) * d0), cy + (int) (Math.sin(an) * d0), cx + (int) (Math.cos(an) * (d0 + len)), cy + (int) (Math.sin(an) * (d0 + len)));
        }
        if (tl > 1.0 && tl < 1.6) { g.setColor(new Color(220, 240, 255, (int) (255 * (1 - Math.abs(tl - 1.25) / .35 > 0 ? 1 - Math.abs(tl - 1.25) / .35 : 0)))); g.fillRect(0, 0, W, H); }
        BufferedImage frente = FRENTE[Math.max(0, Math.min(4, modelo))];
        if (frente != null && tl > 1.1) {
            double z = .25 + Math.min(1, (tl - 1.1) / 1.6) * .9; int fh = (int) (240 * z), fw = frente.getWidth() * fh / frente.getHeight();
            int oy = (int) (Math.sin(tl * 3) * 6);
            g.drawImage(frente, cx - fw / 2, cy - fh / 2 + oy, fw, fh, null);
            g.setColor(new Color(120, 200, 255, 160)); for (int k = -1; k <= 1; k += 2) g.fillOval(cx + k * fw / 4 - 8, cy + fh / 2 - 20 + oy, 16, 16);
        }
    }

    /** O festival visto de cima: palco, mar de 100 mil cabecas, tendas, fachos de luz e o heliponto; a nave chega. */
    private void cenaAerea(Graphics2D g, double tl) {
        int W = Config.LARGURA, H = Config.ALTURA;
        g.setColor(new Color(16, 22, 18)); g.fillRect(0, 0, W, H);
        java.util.Random r = new java.util.Random(3);
        for (int k = 0; k < 60; k++) { g.setColor(new Color(22 + r.nextInt(10), 32 + r.nextInt(12), 24)); g.fillRect(r.nextInt(W), r.nextInt(H), 40 + r.nextInt(80), 30 + r.nextInt(60)); }
        // palco no topo, com telao e luzes piscando
        g.setColor(new Color(40, 36, 50)); g.fillRect(300, 160, 360, 70); g.setColor(new Color(10, 8, 14)); g.fillRect(380, 166, 200, 44);
        for (int k = 0; k < 10; k++) { boolean on = (int) (tl * 6 + k) % 3 == 0; g.setColor(on ? (k % 2 == 0 ? new Color(255, 60, 90) : new Color(180, 80, 255)) : new Color(60, 40, 60)); g.fillOval(310 + k * 35, 216, 10, 10); }
        Image e = CamisaShow.emblema(banda == FESTIVAL || banda == COMUM ? 1 + (int) (tl / 1.2) % 7 : banda);
        if (e != null) { double es = Math.min(190.0 / e.getWidth(null), 44.0 / e.getHeight(null)); int ew = (int) (e.getWidth(null) * es), eh = (int) (e.getHeight(null) * es); g.drawImage(e, 480 - ew / 2, 188 - eh / 2, ew, eh, null); }
        // fachos de luz varrendo a galera
        for (int k = 0; k < 4; k++) { double a = Math.sin(tl * 1.3 + k * 1.6); int x0 = 360 + k * 80; g.setColor(new Color(k % 2 == 0 ? 255 : 160, 70, 230, 34)); g.fillPolygon(new int[]{x0, x0 + (int) (a * 250) - 60, x0 + (int) (a * 250) + 60}, new int[]{230, 470, 470}, 3); }
        // o mar de gente em leque na frente do palco (cabecas vistas de cima)
        for (int k = 0; k < 9000; k++) {
            double v = Math.sqrt(r.nextDouble()), y = 236 + v * 220, meia = 160 + v * 260, x = 480 + (r.nextDouble() * 2 - 1) * meia;
            int c = r.nextInt(6); g.setColor(c == 0 ? new Color(150, 70, 30) : c == 1 ? new Color(170, 140, 70) : c == 2 ? new Color(220, 170, 130) : new Color(25 + r.nextInt(30), 18, 22));
            int pulo = (int) (Math.sin(tl * 8 + k) * .8); g.fillRect((int) x, (int) y + pulo, 3, 3);
        }
        // tendas e o heliponto ao lado
        for (int k = 0; k < 6; k++) { int tx = k < 3 ? 60 + k * 70 : 740 + (k - 3) * 70, ty = 170 + (k % 3) * 34; g.setColor(k % 2 == 0 ? new Color(160, 40, 60) : new Color(90, 50, 150)); g.fillPolygon(new int[]{tx, tx + 25, tx + 50}, new int[]{ty + 30, ty, ty + 30}, 3); }
        int hx = 840, hy = 390; g.setColor(new Color(60, 60, 70)); g.fillOval(hx - 60, hy - 60, 120, 120); g.setColor(new Color(240, 200, 40)); g.setStroke(new java.awt.BasicStroke(4)); g.drawOval(hx - 50, hy - 50, 100, 100);
        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 46)); g.drawString("H", hx - 16, hy + 16);
        // a nave chegando por cima, com a sombra no chao
        BufferedImage frente = FRENTE[Math.max(0, Math.min(4, modelo))];
        double c = suave(Math.min(1, tl / 3.0)); int nx = (int) (990 - c * 150), ny = (int) (120 + c * 270);
        g.setColor(new Color(0, 0, 0, 90)); g.fillOval(nx - 30 + 30, ny + 20, 60, 22);
        if (frente != null) { int fh = 70, fw = frente.getWidth() * fh / frente.getHeight(); g.drawImage(frente, nx - fw / 2, ny - fh / 2, fw, fh, null); }
    }

    /** Quadro 1: pista de pouso fora do festival. O show brilha no horizonte. */
    private void quadroPouso(Graphics2D g) {
        int W = Config.LARGURA, H = Config.ALTURA, HOR = 330;
        for (int y = 0; y < HOR; y += 4) { double f = y / (double) HOR; g.setColor(new Color((int) (12 + 40 * f), (int) (6 + 10 * f), (int) (28 + 50 * f))); g.fillRect(0, y, W, 4); }
        java.util.Random r = new java.util.Random(9);
        for (int k = 0; k < 110; k++) { int c = 120 + r.nextInt(130); g.setColor(new Color(c, c, Math.min(255, c + 30))); g.fillRect(r.nextInt(W), r.nextInt(HOR - 60), 2, 2); }
        fogos(g);
        // o palco ali do lado, com os fachos de luz
        int pw = 560, px = 420;
        for (int k = 0; k < 5; k++) { double a = Math.sin(t * 1.2 + k * 1.7); int x0 = px + 100 + k * 80; g.setColor(new Color(k % 2 == 0 ? 255 : 170, 60, 220, 30)); g.fillPolygon(new int[]{x0, x0 + (int) (a * 200) - 35, x0 + (int) (a * 200) + 35}, new int[]{HOR - 120, 0, 0}, 3); }
        if (PALCO != null) {
            int sx0 = (int) (PALCO.getWidth() * .05), sx1 = (int) (PALCO.getWidth() * .95), sy0 = (int) (PALCO.getHeight() * .1), sy1 = (int) (PALCO.getHeight() * .7);
            int dh = pw * (sy1 - sy0) / (sx1 - sx0); g.drawImage(PALCO, px, HOR + 20 - dh, px + pw, HOR + 20, sx0, sy0, sx1, sy1, null); }
        // chao da pista
        g.setColor(new Color(18, 16, 26)); g.fillRect(0, HOR + 20, W, H - HOR - 20);
        g.setColor(new Color(40, 36, 52)); for (int k = -6; k <= 6; k++) g.drawLine(250 + k * 30, HOR + 20, 250 + k * 200, H);
        g.setColor(new Color(240, 200, 40, 90)); g.drawOval(250 - 170, CHAO - 34, 340, 64); g.drawOval(250 - 150, CHAO - 28, 300, 52);
        for (int k = 0; k < 10; k++) { double an = k * Math.PI / 5; boolean on = (int) (t * 4 + k) % 3 == 0; g.setColor(on ? new Color(120, 255, 140) : new Color(40, 80, 50)); g.fillRect(250 + (int) (Math.cos(an) * 175) - 3, CHAO + (int) (Math.sin(an) * 34) - 3, 6, 6); }
        // a galera de costas, logo ali entre a pista e o palco
        if (fasLonge != null) { fila(g, fasFundo, 372, .12, 430, 24, 21, -1); fila(g, fasLonge, 392, .15, 440, 30, 22, -1); fila(g, fasLonge, 416, .19, 460, 36, 23, -1); fila(g, fasMeio, 444, .23, 490, 44, 24, -1); }
        nave(g);
        piloto(g);
    }

    /** O Wenger no quadro 1: desce, troca de roupa e corre para o festival (encolhendo ate sumir no horizonte). */
    private void piloto(Graphics2D g) {
        if (flavio == null || t < 4.4) return;
        int q; double x, pe, s = .6; float alfa = 1;
        if (t < 5.6) { q = 1; x = 400; pe = CHAO - 70 * (1 - (t - 4.4) / 1.2); }          // desce a escada
        else if (t < 6.2) { q = 0; x = 400; pe = CHAO; }                                 // olha o show
        else if (t < 7) { q = 4; x = 400; pe = CHAO; }                                   // ziper
        else if (t < 7.8) { q = 5; x = 400; pe = CHAO; }                                 // tira o macacao
        else if (t < 8.4) { q = 6; x = 400; pe = CHAO; }                                 // a camisa da banda
        else if (t < 9.4) { q = 7; x = 400; pe = CHAO; }                                 // chifrinho
        else { double c = suave((t - 9.4) / 2.2); q = 8 + (int) (t / .13) % 2; x = 400 + c * 330; pe = CHAO - c * 110; s = .6 - c * .3; } // corre para a galera
        if (t >= 7.8) { g.setColor(new Color(230, 110, 30)); g.fillRoundRect(370, CHAO - 10, 60, 12, 6, 6); g.setColor(new Color(160, 70, 20)); g.fillRect(378, CHAO - 6, 44, 3); }
        Graphics2D a = (Graphics2D) g.create(); a.setComposite(AlphaComposite.SrcOver.derive(Math.max(0, alfa)));
        sprite(a, flavio, 12, q, (int) x, (int) pe, s, false); a.dispose();
    }

    /** Quadro 2: o show lotado. Um mar de gente ate o palco; o Wenger entra correndo e se perde na multidao. */
    /** modo 0: corre e mergulha; 1: a galera carrega ele nos ombros; 2: o show, ele surfando la no meio. */
    private void cenaShow(Graphics2D g, double ts, int modo) {
        int W = Config.LARGURA, H = Config.ALTURA;
        if (PALCO != null) g.drawImage(PALCO, 0, -70, W, H - 70, null);
        telao(g, W, H - 70, -70);
        luzes(g, W);
        fogos(g);
        // mar de cabecas: milhares de pontinhos em perspectiva, cada um pulando, bracos subindo e descendo
        java.util.Random r = new java.util.Random(77);
        for (int y = 330, linha = 0; y < 470; y += 2 + linha / 9, linha++) {
            double esc = .5 + (y - 330) / 140.0 * 3.2; int passo = Math.max(3, (int) (esc * 2.6));
            for (int x = -(r.nextInt(passo)); x < W; x += passo) {
                int pulo = (int) (Math.max(0, Math.sin(t * 7 + x * .37 + y * .91)) * esc * 1.2), hs = Math.max(1, (int) esc);
                int c = r.nextInt(5); Color cab = c == 0 ? new Color(140, 60, 25) : c == 1 ? new Color(150, 120, 60) : new Color(18 + r.nextInt(25), 12 + r.nextInt(15), 16 + r.nextInt(20));
                g.setColor(new Color(10, 8, 14)); g.fillRect(x - hs / 2, y - pulo + hs, hs + 1, hs + 2);   // ombros de camiseta preta
                g.setColor(cab); g.fillRect(x, y - pulo, hs, hs);                                    // cabeca
                if (((x * 31 + y * 17) & 3) == 0) { g.setColor(new Color(210, 150, 110)); int ar = (int) (esc * 1.6) + (int) (Math.sin(t * 5 + x) * esc * .6); g.fillRect(x - hs, y - pulo - ar, Math.max(1, hs / 2), Math.max(1, ar)); g.fillRect(x + hs + 1, y - pulo - ar, Math.max(1, hs / 2), Math.max(1, ar)); }
            }
        }
        // a frente da galera em sprite, por profundidade
        int[] pes = {486, 506, 530, 558, 592};
        double[] escs = {.18, .22, .27, .33, .4};
        BufferedImage[] imgs = {fasFundo, fasLonge, fasLonge, fasMeio, fasPerto};
        if (modo == 0) {
            // vem correndo e salta por cima das cabecas
            double wx = 480, wpe, ws, alt = 0; int wq;
            if (ts < 1.1) { double c = ts / 1.1; wpe = H + 90 - c * 60; ws = .55; wq = 8 + (int) (t / .12) % 2; }
            else { double c = Math.min(1, (ts - 1.1) / 1.5); wpe = H + 30 - c * 160; ws = .55 - c * .2; wx = 480 + c * 20; alt = Math.sin(Math.min(1, c * 1.3) * Math.PI * .5) * 230; wq = 11; }
            for (int k = 0; k < pes.length; k++) if (imgs[k] != null) fila(g, imgs[k], pes[k], escs[k], k * 17, (int) (escs[k] * 230), 10 + k, -1);
            if (flavio != null) { Graphics2D q = (Graphics2D) g.create(); if (ts > 1.1) q.rotate(-Math.min(1.2, (ts - 1.1) * 1.4), wx, wpe - alt - 60); sprite(q, flavio, 12, wq, (int) wx, (int) (wpe - alt), ws, false); q.dispose(); }
            return;
        }
        // a galera carrega ele deitado sobre as maos e joga pra cima de tempos em tempos
        double esc = modo == 1 ? .34 : .24, ritmo = ts * 2.1, joga = Math.pow(Math.abs(Math.sin(ritmo)), 3) * (modo == 1 ? 70 : 30);
        double sx = modo == 1 ? 260 + (ts * 55) % 520 : 300 + (ts * 30) % 380, sy = modo == 1 ? 385 - joga : 392 - joga;
        int ultimo = 3;
        for (int k = 0; k < pes.length; k++) {
            if (k == ultimo) surf(g, sx, sy, esc, ritmo);
            if (imgs[k] != null) fila(g, imgs[k], pes[k], escs[k], k * 17, (int) (escs[k] * 230), 10 + k, -1);
        }
    }
    /** O Wenger deitado sobre a galera, bracos pra cima, girando um pouco; maos embaixo segurando. */
    private void surf(Graphics2D g, double x, double y, double esc, double ritmo) {
        if (flavio == null) return;
        // maos levantadas embaixo dele
        g.setColor(new Color(225, 165, 125));
        for (int k = 0; k < 7; k++) { int hx = (int) (x - esc * 230 + k * esc * 75), hy = (int) (y + esc * 40), alt = (int) (esc * 60 + Math.sin(ritmo * 2 + k) * esc * 14); g.fillRect(hx, hy, Math.max(3, (int) (esc * 14)), alt); g.fillRect(hx - 2, hy - 4, Math.max(5, (int) (esc * 22)), Math.max(4, (int) (esc * 14))); }
        Graphics2D q = (Graphics2D) g.create();
        q.rotate(-Math.PI / 2 + Math.sin(ritmo) * .25, x, y);
        sprite(q, flavio, 12, 10, (int) x, (int) (y + esc * 160), esc, false);
        q.dispose();
    }

    private static void cobrir(Graphics2D g, double a) {
        Graphics2D q = (Graphics2D) g.create(); q.setComposite(AlphaComposite.SrcOver.derive((float) Math.max(0, Math.min(1, a))));
        q.setColor(Color.BLACK); q.fillRect(0, 0, Config.LARGURA, Config.ALTURA); q.dispose();
    }

    /** O emblema da banda escolhida acende no telao, piscando como lampada velha antes de firmar. */
    private void telao(Graphics2D g, int W, int H) { telao(g, W, H, 0); }
    private void telao(Graphics2D g, int W, int H, int dy) {
        // festival completo: o telao passa pelos emblemas de todas as bandas
        Image e = CamisaShow.emblema(banda == FESTIVAL || banda == COMUM ? 1 + (int) (t / 1.5) % 7 : banda);
        if (e == null || t < 1) return;
        boolean pisca = t < 1.8 && (int) (t * 14) % 3 == 0;
        if (pisca) return;
        int x0 = (int) (TELAO_X0 * W), y0 = (int) (TELAO_Y0 * H) + dy, x1 = (int) (TELAO_X1 * W), y1 = (int) (TELAO_Y1 * H) + dy;
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

    /** Uma fileira de fas de costas, de x0 ate a borda, cada um pulando no seu tempo; vao em volta de 'vao' (ou -1). */
    private void fila(Graphics2D g, BufferedImage img, int pe, double esc, int x0, int passo, int semente, int vao) {
        java.util.Random r = new java.util.Random(semente);
        for (int x = x0; x < Config.LARGURA + passo; x += passo) {
            int fx = x + r.nextInt(passo / 3 + 1) - passo / 6, q = 4 + r.nextInt(4); boolean esp = r.nextBoolean(); double fase = r.nextDouble() * 6;
            if (vao >= 0 && Math.abs(fx - vao) < passo * .9) continue;
            double pulo = Math.max(0, Math.sin(t * (3.4 + semente * .2) + fase)) * 9 * esc / .2;
            // headbang: o corpo inclina pra frente e volta no ritmo
            double bate = Math.max(0, Math.sin(t * 9 + fase * 3)) * .16 * (esp ? 1 : -1);
            Graphics2D h = (Graphics2D) g.create(); h.rotate(bate, fx, pe);
            sprite(h, img, 8, q, fx, (int) (pe - pulo), esc, esp); h.dispose();
        }
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
