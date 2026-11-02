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
    static final double DURACAO = 34.2;
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
    int cursor() { return cursor; }
    void mover(int d) { if (banda < 0) { int[] o = opcoes(); int i = 0; for (int k = 0; k < o.length; k++) if (o[k] == cursor) i = k; cursor = o[Math.floorMod(i + d, o.length)]; SomJogo.tocar(SomJogo.Efeito.INTERDICAO); } }
    void moverLinha(int d) { if (banda < 0) mover(d * 4); }
    /** Fecha a escolha: a camisa do Flavio e o telao do festival passam a ser dessa banda. */
    void escolher(int b) {
        if (banda >= 0) return;
        banda = Math.max(0, Math.min(aberto(FESTIVAL) ? FESTIVAL : Fase.BANDAS.length - 1, b));
        // no festival completo a camisa e a da Lula, onde a jornada comecou
        if (FLAVIO != null) flavio = banda == COMUM || banda == FESTIVAL ? FLAVIO : estampar(FLAVIO, CamisaShow.emblema(banda));
        // confirmou: o gostinho acabou, a cutscene segue com a trilha do jogo
        if (previaTocando != -1) { previaTocando = -1; SomJogo.trocarMusica("trilha_metal"); }
        if (WENGER != null) { fasFundo = escurecer(WENGER, .3f); fasLonge = escurecer(WENGER, .45f); fasMeio = escurecer(WENGER, .6f); fasPerto = escurecer(WENGER, .7f); }
    }
    private int previaTocando = -1, ultimoCursor = -1;
    private double paradoNoCursor;
    void confirmar() { if (!aberto(cursor)) { avisoFechado = 1.6; SomJogo.tocar(SomJogo.Efeito.ERRO); return; } escolher(cursor); }
    /** Sons da cutscene: (instante, efeito). A trilha de metal toca por baixo o tempo todo. */
    private static final Object[][] SONS = {
        {.05, SomJogo.Efeito.DOBRA}, {.8, SomJogo.Efeito.DOBRA}, {1.5, SomJogo.Efeito.DOBRA}, {2.0, SomJogo.Efeito.SAIDA_DOBRA}, {2.15, SomJogo.Efeito.TROVAO},
        {5.7, SomJogo.Efeito.FUSAO}, {6.6, SomJogo.Efeito.NUCLEAR}, {8.0, SomJogo.Efeito.FUSAO}, {9.3, SomJogo.Efeito.LASER},
        {11.5, SomJogo.Efeito.FUSAO}, {13.0, SomJogo.Efeito.ENERGIA}, {13.8, SomJogo.Efeito.IMPACTO}, {13.85, SomJogo.Efeito.EXPLOSAO},
        {16.8, SomJogo.Efeito.ESCUDO}, {17.3, SomJogo.Efeito.TIRO}, {18.1, SomJogo.Efeito.IMPACTO}, {18.8, SomJogo.Efeito.TROCA}, {19.6, SomJogo.Efeito.EVOLUCAO},
        {24.2, SomJogo.Efeito.CHEFE_ENTRADA}, {25.6, SomJogo.Efeito.EXPLOSAO_GRANDE}, {26.1, SomJogo.Efeito.VITORIA}, {28.7, SomJogo.Efeito.TROVAO}, {31.7, SomJogo.Efeito.VITORIA}};
    void atualizar(double dt) {
        if (banda < 0) {
            tEscolha += Math.max(0, dt); avisoFechado = Math.max(0, avisoFechado - Math.max(0, dt));
            // o cursor parou numa banda: toca 10 s da musica dela (um gostinho), depois volta a trilha
            if (cursor != ultimoCursor) { ultimoCursor = cursor; paradoNoCursor = 0; if (previaTocando >= 0) { previaTocando = -1; SomJogo.trocarMusica("trilha_metal"); } }
            paradoNoCursor += Math.max(0, dt);
            if (previaTocando < 0 && paradoNoCursor > .4 && paradoNoCursor < 10 && aberto(cursor) && cursor != FESTIVAL) { previaTocando = cursor; MusicaJogo.previaShow(cursor); }
            if (previaTocando >= 0 && paradoNoCursor >= 10.4) { previaTocando = -2; SomJogo.trocarMusica("trilha_metal"); }
            return;
        }
        double antes = t; t = Math.min(DURACAO, t + Math.max(0, dt));
        for (Object[] s : SONS) { double m = ((Number) s[0]).doubleValue(); if (antes < m && t >= m) SomJogo.tocar((SomJogo.Efeito) s[1], 480); }
    }
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
    /** Tomadas da cutscene (inicio de cada uma); cada corte entra como um quadro novo deslizando sobre o gibi. */
    private static final double[] TOMADA = {0, 5.6, 11.4, 16.0, 23.2, 26.0};
    private int nivelNave = 6;
    void nivelNave(int n) { nivelNave = Math.max(0, n); }

    void desenhar(Graphics2D base) {
        if (banda < 0) { desenharEscolha(base); return; }
        Graphics2D g = (Graphics2D) base.create();
        int W = Config.LARGURA, H = Config.ALTURA; double tReal = t;
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        papel(g, W, H);
        int i = 0; while (i + 1 < TOMADA.length && tReal >= TOMADA[i + 1]) i++;
        double tl = tReal - TOMADA[i], entra = Math.min(1, tl / .45);
        try {
            // o quadro anterior fica parado embaixo enquanto o novo desliza por cima
            if (i > 0 && entra < 1) { tomada(g, i - 1, TOMADA[i] - TOMADA[i - 1] - .01, 0, -.004); }
            tomada(g, i, tl, (1 - suave(entra)) * (W + 40), i % 2 == 0 ? -.004 : .004);
        } finally { t = tReal; }
        if (tReal < .5) cobrir(g, 1 - tReal / .5);
        if (tReal > DURACAO - 1) cobrir(g, tReal - (DURACAO - 1));
        g.dispose();
    }

    /** Desenha a tomada i no instante tl, como um quadro de gibi em tela cheia (deslocado em dx). */
    private void tomada(Graphics2D g, int i, double tl, double dx, double giro) {
        Graphics2D c = telaG();
        switch (i) {
            case 0: cenaCabine(c, tl); break;
            case 1: cenaSobrevoo(c, tl); break;
            case 2: if (HELIPONTO != null) cenaHeliponto(c, tl, false); else { t = Math.min(4.6, tl); quadroPouso(c); } break;
            case 3: if (HELIPONTO != null) cenaHeliponto(c, tl, true); else { t = Math.min(11.6, 4.4 + tl); quadroPouso(c); } break;
            case 4: if (VISAO_PALCO != null) cenaMergulho(c, tl); else cenaShow(c, tl, 0); break;
            default: if (VISAO_CENTRO != null) cenaCarregado(c, tl); else cenaShow(c, tl, 1);
        }
        c.dispose();
        Graphics2D q = (Graphics2D) g.create(); q.translate(dx, 0);
        painel(q, 16, 14, Config.LARGURA - 32, Config.ALTURA - 28, 0, 0, Config.LARGURA, Config.ALTURA, 99, 0, giro);
        switch (i) {
            case 0:
                legenda(q, 34, 30, "SAINDO DA DOBRA. O FESTIVAL ESTA LOGO ALI.");
                if (tl > 2.0 && tl < 3.6) sfx(q, "VWOOOOM!", 330, 200, 70, new Color(120, 220, 255), -.12, tl - 2.0);
                break;
            case 1:
                legenda(q, 34, 30, "100 MIL HEADBANGERS. E UM CACADOR CHEGANDO.");
                if (tl > .8 && tl < 2.8) sfx(q, "VRRRROOOM!", 480, 140, 64, new Color(255, 170, 60), .08, tl - .8);
                break;
            case 2:
                legenda(q, 34, 30, "POUSO AUTORIZADO.");
                if (tl > 2.6 && tl < 4.4) sfx(q, "FSSSHHH!", 250, 470, 50, new Color(255, 200, 90), .1, tl - 2.6);
                break;
            case 3:
                if (tl > 1.0 && tl < 3.6) legenda(q, 34, 30, "HORA DE TIRAR O MACACAO.");
                break;
            case 4:
                if (tl > 1.0) sfx(q, "STAGE DIVE!!", 540, 150, 64, new Color(255, 90, 70), -.14, tl - 1.0);
                break;
            default:
                legenda(q, 34, 30, "E A GALERA LEVOU O CACADOR NOS OMBROS.");
                if ((int) (tl * 1.1) % 2 == 0) sfx(q, "HEY! HEY! HEY!", 700, 520, 38, new Color(255, 230, 120), .06, tl % 1.8);
                if (tl > 3.5) legenda(q, 34, 528, "...E FOI ASSIM QUE O CACADOR ZEROU A TURNE.");
        }
        q.dispose();
    }

    /** Arte da cabine por cima do ombro (cabine_wenger.png); o para-brisa preto vira janela pro espaco. */
    /** Cabine de cada nave (cabine_N.png); sem a da nave, usa a generica (cabine_wenger.png). */
    private static final BufferedImage[] CABINES = new BufferedImage[5];
    private static final BufferedImage[][] LUZES = new BufferedImage[5][];
    private static final BufferedImage CABINE_PADRAO = janela(ler("cabine_wenger.png"));
    static { for (int i = 0; i < 5; i++) { CABINES[i] = janela(ler("cabine_" + i + ".png")); LUZES[i] = luzes(CABINES[i]); } }
    /** Tubo do nucleo em cada cabine: centro x, centro y, largura, altura (fracoes da tela) e cor do brilho. */
    private static final double[][] NUCLEO = {{.94, .48, .07, .25, 110, 180, 255}, null, null, null, null};
    /** Luzes da propria arte da cabine: vermelhas (piscam em 3 grupos) e telas verdes (tremulam). */
    private static BufferedImage[] luzes(BufferedImage a) {
        if (a == null) return null;
        int w = a.getWidth(), h = a.getHeight(); BufferedImage[] l = new BufferedImage[4];
        for (int k = 0; k < 4; k++) l[k] = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < h; y++) for (int x = 0; x < w; x++) {
            int c = a.getRGB(x, y); if ((c >>> 24) < 200) continue; int r = c >> 16 & 255, g = c >> 8 & 255, b = c & 255;
            if (r > 200 && g < 60 && b < 60) l[(x * 3 / w + y * 2 / h) % 3].setRGB(x, y, 0xFF000000);   // luz vermelha: grupo por regiao
            else if (g > 140 && r < 140 && b < 140) l[3].setRGB(x, y, 0xFF000000);                         // tela verde
        }
        return l;
    }
    /** Centro e raio do radar em cada cabine (fracoes da tela), para a varredura. */
    private static final double[][] RADAR = {{.515, .655, .062}, null, null, null, null};
    private BufferedImage cabine() { BufferedImage c = CABINES[Math.max(0, Math.min(4, modelo))]; return c != null ? c : CABINE_PADRAO; }
    /** A galera jogando o Wenger pra cima (wenger_galera.png, 4 quadros com as maos embaixo). */
    private static final BufferedImage GALERA = ler("wenger_galera.png");
    /** O Wenger surfando na galera, sem as maos recortadas (wenger_surf.png: 3 quadros). */
    private static final BufferedImage SURF = ler("wenger_surf.png");
    private static BufferedImage janela(BufferedImage a) {
        if (a == null) return null;
        int w = a.getWidth(), h = a.getHeight(); boolean[] v = new boolean[w * h]; java.util.ArrayDeque<int[]> f = new java.util.ArrayDeque<>();
        int[] semente = {w / 2, h / 4}; f.add(semente); v[semente[1] * w + semente[0]] = true;
        while (!f.isEmpty()) { int[] p = f.poll(); int x = p[0], y = p[1], c = a.getRGB(x, y); if (Math.max(c >> 16 & 255, Math.max(c >> 8 & 255, c & 255)) > 22) continue; a.setRGB(x, y, 0);
            int[][] viz = {{x + 1, y}, {x - 1, y}, {x, y + 1}, {x, y - 1}}; for (int[] n : viz) if (n[0] >= 0 && n[1] >= 0 && n[0] < w && n[1] < h && !v[n[1] * w + n[0]]) { v[n[1] * w + n[0]] = true; f.add(n); } }
        return a;
    }

    /** Tomada 1: dentro da cabine, por cima do ombro do Wenger, saindo da dobra espacial. */
    private void cenaCabine(Graphics2D g, double tl) {
        int W = Config.LARGURA, H = Config.ALTURA, cx = W / 2, cy = 220;
        double treme = tl < 2.2 ? Math.sin(tl * 60) * 4 : tl < 2.9 ? Math.sin(tl * 45) * 6 * (2.9 - tl) / .7 : 0;
        // turbulencia: na dobra a nave joga de um lado pro outro; ao sair, vai estabilizando
        double turb = tl < 2.2 ? 1 : Math.exp(-(tl - 2.2) * 1.8), roll = (Math.sin(tl * 6.5) * .055 + Math.sin(tl * 17) * .018) * turb, lado = (Math.sin(tl * 5.2) * 18 + Math.sin(tl * 13) * 6) * turb;
        // camera inclinada (dutch angle) e um pouco mais perto: a cabine e o espaco giram juntos
        Graphics2D v = (Graphics2D) g.create(); v.translate(treme, treme * .6);
        boolean artePropria = CABINES[Math.max(0, Math.min(4, modelo))] != null;
        // voo: a cabine balanca devagar (respiro da nave)
        if (artePropria) { v.rotate(roll + Math.sin(tl * 1.3) * .012, W / 2.0, H * .7); v.translate(lado + Math.sin(tl * .9) * 4, Math.sin(tl * 1.7) * 3 + Math.abs(lado) * .3); v.translate(W / 2.0, H / 2.0); v.scale(1.08, 1.08); v.translate(-W / 2.0, -H / 2.0); }
        if (!artePropria) { v.rotate(-.11, W / 2.0, H / 2.0); v.translate(W / 2.0, H / 2.0); v.scale(1.16, 1.16); v.translate(-W / 2.0, -H / 2.0); }
        // o espaco la fora: tunel de dobra, clarao e depois as estrelas com o sol do festival ao longe
        v.setColor(Color.BLACK); v.fillRect(-10, -10, W + 20, H + 20);
        java.util.Random r = new java.util.Random(5);
        double vel = tl < 2.2 ? 1 : Math.max(0, 1 - (tl - 2.2) * 2);
        for (int k = 0; k < 320; k++) {
            double an = r.nextDouble() * Math.PI * 2, d0 = (r.nextDouble() * 700 + tl * 1100 * (.15 + vel)) % 720, len = 2 + vel * 110;
            int c = 140 + r.nextInt(110); v.setColor(vel > .2 ? new Color(c / 2, c * 3 / 4, c) : new Color(c, c, c));
            v.setStroke(new java.awt.BasicStroke(1 + (float) vel * 2));
            v.drawLine(cx + (int) (Math.cos(an) * d0), cy + (int) (Math.sin(an) * d0 * .7), cx + (int) (Math.cos(an) * (d0 + len)), cy + (int) (Math.sin(an) * (d0 + len) * .7));
        }
        if (tl > 1.9 && tl < 2.6) { v.setColor(new Color(225, 240, 255, (int) (255 * Math.max(0, 1 - Math.abs(tl - 2.2) / .35)))); v.fillRect(-10, -10, W + 20, H + 20); }
        BufferedImage cab = cabine();
        if (cab != null) v.drawImage(cab, 0, 0, W, H, null);
        BufferedImage[] luz = LUZES[Math.max(0, Math.min(4, modelo))];
        if (artePropria && luz != null) {
            // cada grupo de luzes vermelhas apaga no seu ritmo; as telas verdes tremulam
            for (int k = 0; k < 3; k++) { double on = Math.sin(tl * (2.2 + k * 1.3) + k * 2); if (on < -.2) { Graphics2D q = (Graphics2D) v.create(); q.setComposite(AlphaComposite.SrcOver.derive(.6f)); q.drawImage(luz[k], 0, 0, W, H, null); q.dispose(); } }
            float fl = (float) (.08 + .1 * Math.abs(Math.sin(tl * 13)) + (Math.sin(tl * 3.1) > .96 ? .3 : 0));
            Graphics2D q = (Graphics2D) v.create(); q.setComposite(AlphaComposite.SrcOver.derive(fl)); q.drawImage(luz[3], 0, 0, W, H, null); q.dispose();
            double[] rd = RADAR[Math.max(0, Math.min(4, modelo))];
            if (rd != null) {
                int rx = (int) (rd[0] * W), ry = (int) (rd[1] * H), rr = (int) (rd[2] * W);
                Graphics2D rq = (Graphics2D) v.create(); rq.clip(new java.awt.geom.Ellipse2D.Double(rx - rr, ry - rr * .8, rr * 2, rr * 1.6));
                double an = tl * 3;
                for (int k = 0; k < 10; k++) { rq.setColor(new Color(120, 255, 120, 120 - k * 12)); rq.fillArc(rx - rr, (int) (ry - rr * .8), rr * 2, (int) (rr * 1.6), (int) Math.toDegrees(-an) + k * 6, 6); }
                rq.dispose();
            }
        }
        else cabineCodigo(v, tl);
        v.dispose();
    }
    /** Cabine provisoria (ate chegar a arte): montantes do vidro, painel aceso e o Wenger de costas, de macacao. */
    private void cabineCodigo(Graphics2D g, double tl) {
        int W = Config.LARGURA, H = Config.ALTURA;
        g.setColor(new Color(22, 22, 30));
        g.fillPolygon(new int[]{0, 260, 150, 0}, new int[]{0, 0, 600, 600}, 4); g.fillPolygon(new int[]{W, W - 260, W - 150, W}, new int[]{0, 0, 600, 600}, 4);
        g.fillRect(0, 0, W, 28);
        g.setColor(new Color(50, 50, 64)); g.setStroke(new java.awt.BasicStroke(6)); g.drawLine(260, 0, 150, 600); g.drawLine(W - 260, 0, W - 150, 600);
        // painel
        g.setColor(new Color(30, 30, 40)); g.fillPolygon(new int[]{0, W, W, 0}, new int[]{410, 380, H, H}, 4);
        g.setColor(new Color(60, 60, 76)); g.drawLine(0, 410, W, 380);
        for (int k = 0; k < 6; k++) { boolean on = (int) (tl * 3 + k) % 4 != 0; g.setColor(on ? (k % 3 == 0 ? new Color(255, 80, 80) : new Color(80, 255, 140)) : new Color(40, 60, 50)); g.fillRect(80 + k * 70, 440 + (k % 2) * 30, 46, 20); }
        g.setColor(new Color(20, 60, 50)); g.fillRect(300, 430, 160, 90); g.setColor(new Color(100, 255, 170)); g.drawRect(300, 430, 160, 90);
        g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 13)); g.drawString(tl < 2.2 ? "DOBRA ATIVA" : "DESTINO: FESTIVAL", 310, 455); g.drawString(tl < 2.2 ? (int) (99 - tl * 40) + "% ..." : "DIST: 0.3 UA", 310, 480);
        // o Wenger de costas: ombros do macacao laranja, nuca careca, fones e a barba aparecendo dos lados
        int bx = 700, by = 470;
        g.setColor(new Color(170, 80, 20)); g.fillRoundRect(bx - 170, by + 30, 340, 200, 80, 80);
        g.setColor(new Color(220, 110, 30)); g.fillRoundRect(bx - 160, by + 38, 320, 60, 60, 60);
        g.setColor(new Color(90, 50, 30)); g.fillOval(bx - 78, by - 18, 156, 80);                    // barba larga
        g.setColor(new Color(200, 140, 100)); g.fillOval(bx - 70, by - 130, 140, 160);              // cabeca careca
        g.setColor(new Color(230, 170, 125)); g.fillOval(bx - 48, by - 120, 70, 60);                 // brilho da careca
        g.setColor(new Color(180, 120, 85)); g.fillOval(bx - 80, by - 70, 24, 40); g.fillOval(bx + 56, by - 70, 24, 40); // orelhas
        g.setColor(new Color(30, 30, 30)); g.setStroke(new java.awt.BasicStroke(8)); g.drawArc(bx - 74, by - 140, 148, 120, 10, 160);  // fone
        g.fillRect(bx - 86, by - 74, 18, 34); g.fillRect(bx + 68, by - 74, 18, 34);
    }

    /** Pouso ao lado do palco, vista do palco olhando a galera (lado da escada) e vista central do palco. */
    private static final BufferedImage HELIPONTO = ler("pouso_heliponto.png"), VISAO_PALCO = ler("palco_visao_galera.png"), VISAO_CENTRO = ler("palco_visao_centro.png");

    /** Fundo com um zoom lento (camera viva), de 'de' ate 'ate' ao longo de 'dur' segundos, centrado em (fx, fy). */
    private static void fundo(Graphics2D g, BufferedImage img, double tl, double dur, double de, double ate, double fx, double fy) {
        int W = Config.LARGURA, H = Config.ALTURA; double z = de + (ate - de) * Math.min(1, tl / dur);
        int w = (int) (W * z), h = (int) (H * z), x = (int) (fx * W - fx * w), y = (int) (fy * H - fy * h);
        g.drawImage(img, x, y, w, h, null);
    }

    /** Heliponto: a nave desce no H (parte 1); o Wenger desce, tira o macacao e sobe a escadinha do palco (parte 2). */
    private void cenaHeliponto(Graphics2D g, double tl, boolean desce) {
        int W = Config.LARGURA, H = Config.ALTURA;
        fundo(g, HELIPONTO, tl + (desce ? 4.6 : 0), 11.8, 1.2, 1.22, 0, .7);
        BufferedImage frente = FRENTE[Math.max(0, Math.min(4, modelo))];
        int cx = 700, chao = 515, fh = 245;
        // desce balancando, paira um instante, toca o chao e quica no trem de pouso
        double p = desce ? 1 : .55 + .45 * suave(Math.min(1, tl / 2.4)), paira = !desce && tl < 2.4 ? Math.sin(tl * 5) * 5 : 0;
        double quica = !desce && tl > 2.6 && tl < 3.4 ? Math.abs(Math.sin((tl - 2.6) * 8)) * 10 * (3.4 - tl) : 0;
        double giro = desce ? 0 : Math.sin(tl * 2.3) * .06 * (1 - p); // balanca na descida e pousa reta
        int ny = chao - fh - (int) ((1 - p) * 560) - (int) paira - (int) quica + (desce ? (int) (Math.sin(tl * 2) * 1.5) : 0);
        g.setColor(new Color(0, 0, 0, (int) (40 + 100 * p))); int so = (int) (160 + 260 * p); g.fillOval(cx - so / 2, chao - 22, so, 36);
        // correndo pra escada ele passa por tras da nave
        boolean atras = desce && SALTO != null && tl >= 4.4;
        if (atras) saltoWenger(g, tl, cx, ny, fh, chao);
        if (frente != null) {
            int fw = frente.getWidth() * fh / frente.getHeight();
            Graphics2D n = (Graphics2D) g.create(); n.rotate(giro, cx, ny + fh / 2.0);
            // jatos de pouso saindo da boca de cada motor (posicao do bocal na arte de frente de cada nave)
            double[] bocal = BOCAIS[Math.max(0, Math.min(4, modelo))];
            if (!desce && tl < 3.6) { double forca = tl < 2.6 ? 1 : (3.6 - tl); for (int k = -1; k <= 1; k += 2) chama(n, cx + (int) (k * bocal[0] * fw), ny + (int) (bocal[1] * fh), (int) (bocal[2] * fw * 1.5), (int) ((120 + (1 - p) * 90) * forca), tl + k); }
            n.drawImage(frente, cx - fw / 2, ny, fw, fh, null); n.dispose();
        }
        if (!desce && tl > 2.2) { double q = Math.min(1, (tl - 2.2) / 1.6); g.setColor(new Color(170, 150, 170, (int) (140 * (1 - q)))); for (int k = 0; k < 16; k++) { int r = (int) (16 + q * 60); g.fillOval(cx - 230 + k * 30 - r / 2, chao - r / 2 - 8, r, r / 2); } }
        if (!desce) return;
        if (SALTO != null) { if (!atras) saltoWenger(g, tl, cx, ny, fh, chao); return; }
        if (flavio == null) return;
        // Wenger: desce do lado da nave, tira o macacao e corre ate a escadinha (545,412) e sobe ate (608,300)
        int q; double x, pe, s = .36;
        if (tl < 1.6) { q = 1; x = 560; pe = chao + 6 - 70 * (1 - suave(tl / 1.6)); }
        else if (tl < 2.3) { q = 0; x = 560; pe = chao + 6; }
        else if (tl < 2.9) { q = 4; x = 560; pe = chao + 6; } else if (tl < 2.95) { q = 5; x = 560; pe = chao + 6; }
        else if (tl < 3.5) { q = 6; x = 560; pe = chao + 6; }
        else if (tl < 4.3) { q = 7; x = 560; pe = chao + 6; }
        else if (tl < 5.6) { double c = suave((tl - 4.3) / 1.3); q = 8 + (int) (tl / .13) % 2; x = 560 - c * 10; pe = chao + 6 - c * 94; s = .36 - c * .06; }
        else { double c = Math.min(1, (tl - 5.6) / 1.4); q = 8 + (int) (tl / .16) % 2; x = 550 + c * 58; pe = 418 - c * 112; s = .3 - c * .04; }
        if (tl >= 2.95) { g.setColor(new Color(230, 110, 30)); g.fillRoundRect(585, chao, 44, 9, 5, 5); g.setColor(new Color(160, 70, 20)); g.fillRect(591, chao + 3, 32, 2); }
        sprite(g, flavio, 12, q, (int) x, (int) pe, s, false);
    }

    /** Bocal dos motores na arte de frente de cada nave: distancia do centro, altura e largura (fracoes da arte). */
    private static final double[][] BOCAIS = {{.26, .47, .1}, {.2, .5, .07}, {.2, .52, .07}, {.36, .62, .12}, {.2, .52, .07}};
    /** Chama de motor apontando pra baixo: halo azul, corpo ciano e miolo branco, tremulando. */
    private static void chama(Graphics2D g, int x, int y, int w, int len, double tt) {
        if (len <= 2) return;
        Graphics2D c = (Graphics2D) g.create();
        double fl = 1 + Math.sin(tt * 47) * .12 + Math.sin(tt * 23) * .08; int L = (int) (len * fl);
        int[][] camadas = {{60, 140, 255, 70}, {80, 190, 255, 150}, {170, 235, 255, 210}, {255, 255, 255, 255}};
        double[] larg = {1.5, 1.0, .62, .3}, comp = {1.1, 1.0, .72, .42};
        for (int k = 0; k < 4; k++) {
            int ww = (int) (w * larg[k]), ll = (int) (L * comp[k]);
            c.setColor(new Color(camadas[k][0], camadas[k][1], camadas[k][2], camadas[k][3]));
            c.fillPolygon(new int[]{x - ww / 2, x + ww / 2, x + ww / 4, x, x - ww / 4}, new int[]{y, y, y + ll * 2 / 3, y + ll, y + ll * 2 / 3}, 5);
            c.fillOval(x - ww / 2, y - ww / 4, ww, ww / 2);
        }
        // faiscas soltas caindo da chama
        java.util.Random r = new java.util.Random((long) (tt * 20));
        for (int k = 0; k < 5; k++) { c.setColor(new Color(150, 220, 255, 200)); c.fillRect(x - w / 2 + r.nextInt(w + 1), y + L / 2 + r.nextInt(L / 2 + 1), 3, 3); }
        c.dispose();
    }

    /** Wenger saindo do cockpit e pulando da nave (wenger_salto.png, 8 quadros: na cabine, impulso, salto com o capacete,
     *  aterrissagem, arrancando o macacao, chifrinho, correndo de costas x2). */
    private static final BufferedImage SALTO = ler("wenger_salto.png");
    /** Fas de frente, colados na grade (galera_frente.png: 6 fas x 4 quadros). */
    private static final BufferedImage FAS_FRENTE = ler("galera_frente.png");
    private void saltoWenger(Graphics2D g, double tl, int cx, int ny, int fh, int chao) {
        int q; double x, pe, s = .36;
        double cabX = cx, cabPe = ny + fh * .5, chaoX = 400, chaoPe = chao + 6;
        if (tl < .9) { q = 0; x = cabX; pe = cabPe; }                                              // de pe na cabine
        else if (tl < 1.3) { q = 1; x = cabX; pe = cabPe; }                                       // impulso
        else if (tl < 2.1) { double c = (tl - 1.3) / .8; q = 2; x = cabX + (chaoX - cabX) * c; pe = cabPe + (chaoPe - cabPe) * c - Math.sin(c * Math.PI) * 90; } // salto
        else if (tl < 2.7) { q = 3; x = chaoX; pe = chaoPe; }                                     // aterrissa estilo heroi
        else if (tl < 3.5) { q = 4; x = chaoX; pe = chaoPe; }                                     // arranca o macacao
        else if (tl < 4.4) { q = 5; x = chaoX; pe = chaoPe; }                                     // chifrinho
        else if (tl < 5.6) { double c = suave((tl - 4.4) / 1.2); q = 6 + (int) (tl / .13) % 2; x = chaoX + c * 261; pe = chaoPe - c * 103; s = .36 - c * .06; } // desce da plataforma e corre pra escada
        else { double c = Math.min(1, (tl - 5.6) / 1.4); q = 6 + (int) (tl / .16) % 2; x = 661 + c * 66; pe = 418 - c * 126; s = .3 - c * .05; } // sobe a escada
        if (tl >= 3.5) { g.setColor(new Color(230, 110, 30)); g.fillRoundRect(425, chao, 44, 9, 5, 5); g.setColor(new Color(160, 70, 20)); g.fillRect(431, chao + 3, 32, 2); }
        if (tl > 1.5 && tl < 2.6) { // o capacete voando longe
            double c = (tl - 1.5) / 1.1; g.setColor(Color.WHITE); int hx = (int) (cabX - 40 - c * 260), hy = (int) (cabPe - 100 + c * c * 220); g.fillOval(hx, hy, 18, 16); g.setColor(new Color(200, 30, 40)); g.fillRect(hx + 4, hy + 3, 3, 10); }
        // o quadro 2 da arte ja traz o capacete; nos demais o sprite vai sozinho
        sprite(g, SALTO, 8, q, (int) x, (int) pe, s, q < 6); // virado pra esquerda: pula pro lado da escada
    }

    /** Fila da frente da galera colada na grade: cada fa no seu ritmo; a grade e redesenhada por cima. */
    private void filaFrente(Graphics2D g, BufferedImage fundoImg, double tl, double dur, double de, double ate, double fx, double fy, double grade, int pe, double esc) {
        if (FAS_FRENTE == null) return;
        int n = 9;
        for (int k = 0; k < n; k++) {
            int tipo = (k * 5) % 6, q = (int) (tl * (3 + (k % 3)) + k * 1.3) % 4, x = 55 + k * 106 + (k % 2) * 14;
            sprite(g, FAS_FRENTE, 24, tipo * 4 + q, x, pe + (k % 2) * 6, esc, k % 3 == 1);
        }
        // a grade (e o palco) passam na frente dos fas
        int W = Config.LARGURA, H = Config.ALTURA; double z = de + (ate - de) * Math.min(1, tl / dur);
        int gy = (int) (fy * H - fy * H * z + grade * H * z);
        Graphics2D c = (Graphics2D) g.create(); c.clipRect(0, gy, W, H - gy); fundo(c, fundoImg, tl, dur, de, ate, fx, fy); c.dispose();
    }

    /** Vista do palco: o Wenger entra pela escada da direita, corre de costas pela beira e mergulha na galera. */
    private void cenaMergulho(Graphics2D g, double tl) {
        fundo(g, VISAO_PALCO, tl, 2.8, 1.0, 1.06, .5, .9);
        bandeiras(g, new int[][]{{100, 320, 30}, {230, 270, 24}, {640, 250, 24}, {760, 300, 30}, {420, 230, 18}, {330, 330, 34}, {560, 340, 34}, {170, 230, 18}, {720, 220, 16}});
        filaFrente(g, VISAO_PALCO, tl, 2.8, 1.0, 1.06, .5, .9, .742, 458, .38);
        if (flavio == null) return;
        double x, pe, s, alt = 0; int q; float a = 1;
        if (tl < 1.2) { double c = suave(tl / 1.2); q = 8 + (int) (tl / .12) % 2; x = 820 - c * 330; pe = 520 + c * 30; s = .52 + c * .06; }
        else { double c = Math.min(1, (tl - 1.2) / 1.3); q = 11; x = 490 - c * 20; pe = 500 - c * 160; s = .58 - c * .36; alt = Math.sin(c * Math.PI) * 120; a = (float) Math.max(0, 1 - Math.max(0, c - .75) * 4); }
        Graphics2D w = (Graphics2D) g.create(); w.setComposite(AlphaComposite.SrcOver.derive(a));
        sprite(w, flavio, 12, q, (int) x, (int) (pe - alt), s, true); w.dispose(); // virado pra esquerda, pro lado da galera
    }

    /** Bandeira de pais em pixel, tremulando num mastro (pe do mastro em x,y). */
    private void bandeira(Graphics2D g, int pais, int x, int y, int w, double fase) {
        int h = w * 2 / 3, mast = (int) (w * 1.3);
        g.setColor(new Color(70, 60, 50)); g.fillRect(x, y - mast - h, Math.max(2, w / 18), mast + h);
        int faixas = Math.max(6, w / 3);
        for (int k = 0; k < faixas; k++) {
            int fx = x + 2 + k * w / faixas, fw = w / faixas + 1, dy = (int) (Math.sin(t * 6 + fase - k * .5) * h * .12 * k / faixas);
            Graphics2D f = (Graphics2D) g.create(); f.clipRect(fx, y - mast - h + dy - 2, fw, h + 4); f.translate(x + 2, y - mast - h + dy);
            pintarPais(f, pais, w, h); f.dispose();
        }
    }
    private static void pintarPais(Graphics2D f, int p, int w, int h) {
        switch (p) {
            case 0: f.setColor(new Color(0, 150, 60)); f.fillRect(0, 0, w, h); f.setColor(new Color(250, 210, 0)); f.fillPolygon(new int[]{w / 2, w - w / 10, w / 2, w / 10}, new int[]{h / 10, h / 2, h - h / 10, h / 2}, 4); f.setColor(new Color(20, 40, 140)); f.fillOval(w / 2 - h / 4, h / 4, h / 2, h / 2); break; // Brasil
            case 1: for (int k = 0; k < 7; k++) { f.setColor(k % 2 == 0 ? new Color(200, 30, 40) : Color.WHITE); f.fillRect(0, k * h / 7, w, h / 7 + 1); } f.setColor(new Color(30, 40, 110)); f.fillRect(0, 0, w * 2 / 5, h * 4 / 7); break; // EUA
            case 2: f.setColor(new Color(20, 40, 120)); f.fillRect(0, 0, w, h); f.setColor(Color.WHITE); f.fillRect(0, h * 2 / 5, w, h / 5); f.fillRect(w * 2 / 5, 0, w / 5, h); f.setColor(new Color(200, 20, 40)); f.fillRect(0, h * 9 / 20, w, h / 10); f.fillRect(w * 9 / 20, 0, w / 10, h); break; // Reino Unido
            case 3: f.setColor(Color.BLACK); f.fillRect(0, 0, w, h / 3 + 1); f.setColor(new Color(220, 0, 0)); f.fillRect(0, h / 3, w, h / 3 + 1); f.setColor(new Color(255, 205, 0)); f.fillRect(0, h * 2 / 3, w, h / 3 + 1); break; // Alemanha
            case 4: f.setColor(Color.WHITE); f.fillRect(0, 0, w, h); f.setColor(new Color(200, 20, 40)); f.fillOval(w / 2 - h / 4, h / 4, h / 2, h / 2); break; // Japao
            case 5: f.setColor(new Color(110, 180, 230)); f.fillRect(0, 0, w, h); f.setColor(Color.WHITE); f.fillRect(0, h / 3, w, h / 3 + 1); f.setColor(new Color(250, 190, 40)); f.fillOval(w / 2 - h / 10, h / 2 - h / 10, h / 5, h / 5); break; // Argentina
            case 6: f.setColor(new Color(0, 90, 170)); f.fillRect(0, 0, w, h); f.setColor(new Color(255, 205, 0)); f.fillRect(0, h * 2 / 5, w, h / 5); f.fillRect(w * 3 / 10, 0, w / 7, h); break; // Suecia
            case 7: f.setColor(new Color(0, 110, 70)); f.fillRect(0, 0, w / 3 + 1, h); f.setColor(Color.WHITE); f.fillRect(w / 3, 0, w / 3 + 1, h); f.setColor(new Color(200, 20, 40)); f.fillRect(w * 2 / 3, 0, w / 3 + 1, h); f.setColor(new Color(130, 90, 40)); f.fillOval(w / 2 - h / 8, h / 2 - h / 8, h / 4, h / 4); break; // Mexico
            default: f.setColor(new Color(0, 140, 70)); f.fillRect(0, 0, w / 3 + 1, h); f.setColor(Color.WHITE); f.fillRect(w / 3, 0, w / 3 + 1, h); f.setColor(new Color(200, 30, 40)); f.fillRect(w * 2 / 3, 0, w / 3 + 1, h); // Italia
        }
    }
    /** Bandeiras de varios paises espalhadas pela galera (x, pe do mastro, largura). */
    private void bandeiras(Graphics2D g, int[][] pos) { for (int k = 0; k < pos.length; k++) bandeira(g, k % 9, pos[k][0], pos[k][1], (int) (pos[k][2] * 1.5), k * 1.7); }

    /** Vista central do palco: a galera de frente jogando o Wenger pra cima, la no meio. */
    private void cenaCarregado(Graphics2D g, double tl) {
        fundo(g, VISAO_CENTRO, tl, 8.2, 1.0, 1.1, .5, .55);
        bandeiras(g, new int[][]{{120, 300, 34}, {250, 250, 26}, {700, 260, 28}, {820, 320, 36}, {380, 230, 22}, {600, 225, 22}, {60, 380, 40}, {900, 400, 40}, {480, 210, 18}});
        filaFrente(g, VISAO_CENTRO, tl, 8.2, 1.0, 1.1, .5, .55, .735, 450, .4);
        if (GALERA == null) return;
        double x = 480 + Math.sin(tl * .45) * 150;
        if (SURF != null) {
            // deitado em cima das maos da propria galera do fundo, sendo jogado pra cima
            int q = new int[]{0, 1, 2, 1}[(int) (tl * 1.9) % 4];
            sprite(g, SURF, 3, q, (int) x, 470, .4, true);
        } else sprite(g, GALERA, 4, (int) (tl * 2.1 / 1.1) % 4, (int) x, 400, .42, false);
    }

    /** Festival visto de cima (festival_de_cima.png, em pe: palco no topo, galera, heliponto no canto direito). */
    private static final BufferedImage FESTIVAL_CIMA = ler("festival_de_cima.png");
    /** Nave vista de cima (nave_N_cima.png); sem ela, o sprite do jogo virado com o bico pra cima. */
    private static final BufferedImage[] NAVE_CIMA = new BufferedImage[5];
    private static final BufferedImage[] SOMBRA_CIMA = new BufferedImage[5];
    static { for (int i = 0; i < 5; i++) { NAVE_CIMA[i] = ler("nave_" + i + "_cima.png"); if (NAVE_CIMA[i] != null) SOMBRA_CIMA[i] = new java.awt.image.RescaleOp(new float[]{0, 0, 0, .7f}, new float[]{0, 0, 0, 0}, null).filter(NAVE_CIMA[i], null); } }

    /** Tomada 2: camera la em cima; a nave sobrevoa a galera de baixo pra cima e faz a curva ate o heliponto. */
    private void cenaSobrevoo(Graphics2D g, double tl) {
        int W = Config.LARGURA, H = Config.ALTURA;
        if (FESTIVAL_CIMA == null) { cenaShow(g, tl, 3); return; }
        double e = W / (double) FESTIVAL_CIMA.getWidth(); int ih = (int) (FESTIVAL_CIMA.getHeight() * e);
        double c = suave(Math.min(1, tl / 3.4)), topo = (ih - H) * (1 - c);
        g.drawImage(FESTIVAL_CIMA, 0, (int) -topo, W, ih, null);
        java.util.Random r = new java.util.Random(12);
        // a galera viva: bracos subindo e descendo (pontinhos de pele piscando sobre a multidao)
        for (int k = 0; k < 900; k++) {
            double ix = 125 + r.nextDouble() * 750, iy = 370 + r.nextDouble() * 1200; int y = (int) (iy * e - topo);
            if (y < -4 || y > H + 4) continue;
            if (Math.sin(t * 9 + k * 1.7) > .55) { g.setColor(k % 7 == 0 ? new Color(255, 120, 90) : new Color(230, 170, 125)); g.fillRect((int) (ix * e), y - 3, 2, 4); }
        }
        // fachos de luz varrendo
        for (int k = 0; k < 3; k++) {
            double a = Math.sin(t * .9 + k * 2.1); int x0 = 300 + k * 180, y0 = (int) (330 * e - topo);
            Graphics2D f = (Graphics2D) g.create(); f.setComposite(AlphaComposite.SrcOver.derive(.16f)); f.setColor(new Color(190, 90, 255));
            f.fillPolygon(new int[]{x0, x0 + (int) (a * 300) - 60, x0 + (int) (a * 300) + 60}, new int[]{y0, y0 + 700, y0 + 700}, 3); f.dispose();
        }
        // bolas de praia quicando na galera
        for (int k = 0; k < 7; k++) {
            double ix = 180 + r.nextDouble() * 640, iy = 450 + r.nextDouble() * 1050, px = ix * e + Math.sin(t * .7 + k) * 40;
            int y = (int) (iy * e - topo), quica = (int) (Math.abs(Math.sin(t * 2.6 + k * 1.3)) * 16), rr = 14;
            if (y < -20 || y > H + 20) continue;
            g.setColor(new Color(0, 0, 0, 70)); g.fillOval((int) px - rr + 4, y - rr + 6, rr * 2, rr * 2 - 4);
            Color[] cor = {new Color(255, 70, 70), new Color(255, 230, 80), new Color(80, 160, 255), Color.WHITE};
            for (int q = 0; q < 4; q++) { g.setColor(cor[(q + k) % 4]); g.fillArc((int) px - rr - quica / 5, y - rr - quica, rr * 2 + quica / 3, rr * 2 + quica / 3, q * 90 + (int) (t * 90), 90); }
        }
        // a nave: sobe pelo meio e faz a curva ate o heliponto (915,310 na arte)
        double hx = 915 * e, hy = 310 * e - topo;
        double sx = W / 2.0, sy = H + 80, mx = W / 2.0 + 60, my = H * .35;
        double u = Math.min(1, tl / 3.4), nx = (1 - u) * (1 - u) * sx + 2 * u * (1 - u) * mx + u * u * hx, ny = (1 - u) * (1 - u) * sy + 2 * u * (1 - u) * my + u * u * hy;
        double dx = 2 * (1 - u) * (mx - sx) + 2 * u * (hx - mx), dy = 2 * (1 - u) * (my - sy) + 2 * u * (hy - my), ang = Math.atan2(dy, dx) + Math.PI / 2;
        // chegou no H: termina a curva (gira de nariz pra cima) e desce ate pousar em cima da propria sombra
        double pousa = .55 * suave(Math.max(0, Math.min(1, (tl - 3.4) / 1.8))); // desce so ate pairar sobre o H
        // inclinacao lateral (bank) na curva: a nave se deita pro lado da curva e endireita ao pousar
        double bank = Math.pow(Math.sin(u * Math.PI), 2) * .18 * (1 - pousa);
        double esc = (1 - u * .25) * (1 - .3 * pousa), desloc = 1 - pousa;
        BufferedImage semCima = NAVE_CIMA[Math.max(0, Math.min(4, modelo))];
        if (semCima == null) { // sombra em forma de nave (asa delta) cruzando a galera
            Graphics2D sb = (Graphics2D) g.create(); sb.translate(nx, ny); sb.rotate(ang); sb.scale(esc * 1.6, esc * 1.6);
            sb.setColor(new Color(0, 0, 0, 120)); sb.fillPolygon(new int[]{0, 46, 14, 0, -14, -46}, new int[]{-50, 26, 18, 36, 18, 26}, 6); sb.dispose();
        } else g.setColor(new Color(0, 0, 0, 90));
        BufferedImage sombra = SOMBRA_CIMA[Math.max(0, Math.min(4, modelo))];
        if (semCima != null && sombra != null) {
            // silhueta da nave correndo sobre a galera, deslocada pela luz do palco
            boolean folha = sombra.getWidth() >= sombra.getHeight() * 1.5; int fw = folha ? sombra.getWidth() / 3 : sombra.getWidth(), qs = folha ? Math.max(0, Math.min(2, ArteNaves.estagio(nivelNave))) : 0;
            Graphics2D sb = (Graphics2D) g.create(); sb.translate(nx + 120 * esc * desloc + 6, ny + 150 * esc * desloc + 6); sb.rotate(ang + bank); sb.scale(esc * 1.15, esc * 1.15);
            int w = 190, h = w * sombra.getHeight() / fw; sb.drawImage(sombra, -w / 2, -h / 2, w / 2, h / 2, qs * fw, 0, qs * fw + fw, sombra.getHeight(), null); sb.dispose();
        }
        Graphics2D n = (Graphics2D) g.create(); n.translate(nx, ny); n.rotate(ang + bank); n.scale(esc, esc);
        BufferedImage cima = NAVE_CIMA[Math.max(0, Math.min(4, modelo))];
        if (cima != null) {
            // folha com os 3 estagios de evolucao lado a lado: usa o estagio em que o jogador terminou
            boolean folha = cima.getWidth() >= cima.getHeight() * 1.5; int fw = folha ? cima.getWidth() / 3 : cima.getWidth(), q = folha ? Math.max(0, Math.min(2, ArteNaves.estagio(nivelNave))) : 0;
            int w = 190, h = w * cima.getHeight() / fw; n.drawImage(cima, -w / 2, -h / 2, w / 2, h / 2, q * fw, 0, q * fw + fw, cima.getHeight(), null); }
        // sem a arte de cima, so a sombra da nave passa sobre a galera (nada de sprite torto)
        n.dispose();
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
        if (modo == 3) { for (int k = 0; k < pes.length; k++) if (imgs[k] != null) fila(g, imgs[k], pes[k], escs[k], k * 17, (int) (escs[k] * 230), 10 + k, -1); return; }
        if (modo == 0) {
            // vem correndo e salta por cima das cabecas
            double wx = 480, wpe, ws, alt = 0; int wq;
            if (ts < 1.1) { double c = ts / 1.1; wpe = H + 90 - c * 60; ws = .55; wq = 8 + (int) (t / .12) % 2; }
            else { double c = Math.min(1, (ts - 1.1) / 1.5); wpe = H + 30 - c * 160; ws = .55 - c * .2; wx = 480 + c * 20; alt = Math.sin(c * Math.PI) * 110; wq = 11; }
            for (int k = 0; k < pes.length; k++) if (imgs[k] != null) fila(g, imgs[k], pes[k], escs[k], k * 17, (int) (escs[k] * 230), 10 + k, -1);
            if (flavio != null) { Graphics2D q = (Graphics2D) g.create(); if (ts > 1.1) q.rotate(-Math.min(1.2, (ts - 1.1) * 1.4), wx, wpe - alt - 60); sprite(q, flavio, 12, wq, (int) wx, (int) (wpe - alt), ws, false); q.dispose(); }
            return;
        }
        // a galera carrega ele deitado sobre as maos e joga pra cima de tempos em tempos
        double esc = modo == 1 ? .5 : .24, ritmo = ts * 2.1, joga = GALERA != null ? 0 : Math.pow(Math.abs(Math.sin(ritmo)), 3) * (modo == 1 ? 70 : 30);
        double sx = modo == 1 ? 330 + Math.sin(ts * .5) * 180 : 300 + (ts * 30) % 380, sy = modo == 1 ? 425 - joga : 392 - joga;
        int ultimo = 3;
        for (int k = 0; k < pes.length; k++) {
            if (k == ultimo) surf(g, sx, sy, esc, ritmo);
            if (imgs[k] != null) fila(g, imgs[k], pes[k], escs[k], k * 17, (int) (escs[k] * 230), 10 + k, -1);
        }
    }
    /** O Wenger deitado sobre a galera, bracos pra cima, girando um pouco; maos embaixo segurando. */
    private void surf(Graphics2D g, double x, double y, double esc, double ritmo) {
        if (GALERA != null) {
            // a arte ja traz as maos e a altura de cada jogada: so troca o quadro no ritmo
            int q = (int) (ritmo / 1.1) % 4; double e = esc * .85;
            sprite(g, GALERA, 4, q, (int) x, (int) (y + esc * 150), e, false);
            return;
        }
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
