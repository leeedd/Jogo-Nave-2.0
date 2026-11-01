package Jogo;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;

/**
 * Prologo dos creditos (versao provisoria, desenhada em codigo ate chegar a arte do PROMPTS_FINAL):
 * a nave pousa no festival, o Flavio desce de macacao laranja, tira o macacao, esta com a camisa
 * da banda e entra na multidao. Depois seguem os creditos.
 */
final class CenaPouso {
    /** Abertura em quadros (final_pouso.png, 4 paineis): a nave chega ao festival e pousa. */
    /** Tiras de 4 quadros em sequencia: pouso, Flavio descendo e tirando o macacao, Flavio na multidao. */
    private static final Image[] TIRAS = existentes(opcional("final_pouso.png"), opcional("final_flavio_desce.png"), opcional("final_flavio_multidao.png"));
    private static final Image QUADROS = TIRAS.length > 0 ? TIRAS[0] : null;
    private static Image opcional(String n) { try { return Assets.imagem(n); } catch (IllegalStateException e) { return null; } }
    private static Image[] existentes(Image... l) { java.util.List<Image> r = new java.util.ArrayList<>(); for (Image i : l) if (i != null) r.add(i); return r.toArray(new Image[0]); }
    private static final double PAINEL = 1.9, ABERTURA = PAINEL * 4 * TIRAS.length;
    /** Com o Flavio em quadros, a cena desenhada em codigo nao entra mais: dos quadros vai direto aos creditos. */
    private static final boolean SO_QUADROS = TIRAS.length >= 2;
    /** Com a abertura em quadros, a cena ja comeca com a nave pousada (pula a descida). */
    private static final double PULO = QUADROS != null ? 5.5 : 0;
    static final double DURACAO = SO_QUADROS ? ABERTURA + .8 : 17 + ABERTURA - PULO;
    private static final Image PALCO = Assets.imagem("show_final_098.png");
    /** Arte do piloto (piloto_099.png, grade 4x2): andando (0-3), tirando o macacao (4-5), chifrinho (6), bracos para cima (7). */
    private static final Image PILOTO = Assets.imagem("piloto_099.png");
    private static final java.awt.image.BufferedImage[] FRENTE = new java.awt.image.BufferedImage[5];
    static { for (int i = 0; i < 5; i++) { java.net.URL u = CenaPouso.class.getResource("/ImagemIcon/nave_" + i + "_frente.png"); if (u != null) try { FRENTE[i] = javax.imageio.ImageIO.read(u); } catch (java.io.IOException e) { } } }
    private final int modelo;
    private double t;

    /** Show escolhido (indice de Fase.BANDAS) ou -1 enquanto o jogador ainda esta escolhendo. */
    private int banda = -1, cursor = CamisaShow.SLAYER;
    private double tEscolha;
    /** Tiras desta partida, ja com a camisa e o palco do show escolhido. */
    private final Image[] tiras = TIRAS.clone();

    CenaPouso(int modelo) { this.modelo = modelo; if (TIRAS.length == 0) banda = CamisaShow.SLAYER; }
    boolean escolhendo() { return banda < 0; }
    int banda() { return banda; }
    void mover(int d) { if (banda < 0) { int n = Fase.BANDAS.length; cursor = ((cursor + d) % n + n) % n; SomJogo.tocar(SomJogo.Efeito.INTERDICAO); } }
    void moverLinha(int d) { if (banda < 0) mover(d * 4); }
    /** Fecha a escolha: a camisa do Flavio e o palco do festival passam a ser dessa banda. */
    void escolher(int b) {
        if (banda >= 0) return;
        banda = Math.max(0, Math.min(Fase.BANDAS.length - 1, b));
        for (int i = 0; i < TIRAS.length; i++) {
            int linha = TIRAS[i] == DESCE ? 0 : TIRAS[i] == MULTIDAO ? 1 : -1;
            if (linha >= 0) tiras[i] = CamisaShow.ajustar(TIRAS[i], linha, banda);
        }
    }
    void confirmar() { escolher(cursor); }
    void atualizar(double dt) { if (banda < 0) { tEscolha += Math.max(0, dt); return; } t = Math.min(DURACAO, t + Math.max(0, dt)); }
    boolean terminou() { return t >= DURACAO; }
    void pular() { if (banda < 0) confirmar(); else t = DURACAO; }

    private static final int CHAO = 470;
    private static double suave(double x) { x = Math.max(0, Math.min(1, x)); return x * x * (3 - 2 * x); }

    void desenhar(Graphics2D base) {
        if (banda < 0) { desenharEscolha(base); return; }
        if (t < ABERTURA || SO_QUADROS) { abertura(base); return; }
        double tReal = t; t = t - ABERTURA + PULO;
        try { desenharCena(base); } finally { t = tReal; }
    }
    /** Os 4 paineis com fusao e um leve zoom, como as cutscenes do jogo. */
    private void abertura(Graphics2D base) {
        Graphics2D g = (Graphics2D) base.create(); g.setColor(Color.BLACK); g.fillRect(0, 0, Config.LARGURA, Config.ALTURA);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        int k = Math.min(4 * TIRAS.length - 1, (int) (t / PAINEL)); double dentro = Math.min(PAINEL + .8, t - k * PAINEL);
        if (k > 0 && dentro < .35) painel(g, k - 1, 1, PAINEL + dentro);
        painel(g, k, (float) Math.min(1, dentro / .35), dentro);
        // no aperto de mao da multidao, o parceiro de show: Wenger
        if (MULTIDAO != null && TIRAS[k / 4] == MULTIDAO && k % 4 == 1 && dentro > .5) etiqueta(g, "WENGER", 655, 150, (float) Math.min(1, (dentro - .5) / .3));
        if (SO_QUADROS && t > DURACAO - .8) { g.setComposite(AlphaComposite.SrcOver.derive((float) Math.min(1, (t - (DURACAO - .8)) / .8))); g.setColor(Color.BLACK); g.fillRect(0, 0, Config.LARGURA, Config.ALTURA); g.setComposite(AlphaComposite.SrcOver); }
        if (t < .4) { g.setComposite(AlphaComposite.SrcOver.derive((float) (1 - t / .4))); g.setColor(Color.BLACK); g.fillRect(0, 0, Config.LARGURA, Config.ALTURA); }
        g.dispose();
    }
    private static final Image MULTIDAO = opcional("final_flavio_multidao.png"), DESCE = opcional("final_flavio_desce.png");
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
        String sub = "O FESTIVAL TEM 8 PALCOS. A CAMISA VAI SER DA BANDA ESCOLHIDA."; g.drawString(sub, (W - g.getFontMetrics().stringWidth(sub)) / 2, 98);
        for (int i = 0; i < Fase.BANDAS.length; i++) {
            int cx = 135 + (i % 4) * 230, cy = 210 + (i / 4) * 200; boolean sel = i == cursor;
            double pulso = sel ? 1 + .05 * Math.sin(tEscolha * 6) : .82;
            if (sel) { g.setColor(new Color(200, 40, 40)); g.fillRect(cx - 100, cy - 85, 200, 175); g.setColor(new Color(20, 10, 16)); g.fillRect(cx - 96, cy - 81, 192, 167); }
            Image e = CamisaShow.emblema(i);
            if (e != null) {
                double s = Math.min(170.0 / e.getWidth(null), 125.0 / e.getHeight(null)) * pulso;
                int ew = (int) (e.getWidth(null) * s), eh = (int) (e.getHeight(null) * s);
                Graphics2D q = (Graphics2D) g.create(); if (!sel) q.setComposite(AlphaComposite.SrcOver.derive(.55f));
                q.drawImage(e, cx - ew / 2, cy - 10 - eh / 2, ew, eh, null); q.dispose();
            }
            g.setFont(new Font(Font.MONOSPACED, Font.BOLD, sel ? 15 : 13)); g.setColor(sel ? Color.WHITE : new Color(150, 130, 150));
            String n = Fase.BANDAS[i]; g.drawString(n, cx - g.getFontMetrics().stringWidth(n) / 2, cy + 75);
        }
        g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 14)); g.setColor(new Color(250, 225, 140));
        String dica = "SETAS: ESCOLHER    ENTER: IR PARA O SHOW"; g.drawString(dica, (W - g.getFontMetrics().stringWidth(dica)) / 2, H - 30);
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
    private void painel(Graphics2D g, int k, float a, double dt) {
        Image tira = tiras[k / 4]; k %= 4;
        int fw = tira.getWidth(null) / 4, fh = tira.getHeight(null); double z = 1 + .06 * dt / PAINEL;
        int w = (int) (Config.LARGURA * z), h = (int) (Config.LARGURA * fh / (double) fw * z), x = (Config.LARGURA - w) / 2, y = (Config.ALTURA - h) / 2;
        Graphics2D q = (Graphics2D) g.create(); q.setComposite(AlphaComposite.SrcOver.derive(a));
        q.drawImage(tira, x, y, x + w, y + h, k * fw, 0, k * fw + fw, fh, null); q.dispose();
    }
    private void desenharCena(Graphics2D base) {
        Graphics2D g = (Graphics2D) base.create();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        int W = Config.LARGURA, H = Config.ALTURA;
        g.setColor(Color.BLACK); g.fillRect(0, 0, W, H);
        // o palco do festival ao fundo, com luzes varrendo
        // ceu de estrelas e o palco (arte em pe) na direita, sem esticar
        java.util.Random ce = new java.util.Random(4); for (int k = 0; k < 160; k++) { int v = 80 + ce.nextInt(170); g.setColor(new Color(v, v, Math.min(255, v + 30))); g.fillRect(ce.nextInt(W), ce.nextInt(CHAO), 2, 2); }
        if (PALCO != null) { int q = (int) (t * 6) % 4, sw = PALCO.getWidth(null) / 2, sh = PALCO.getHeight(null) / 2, lado = CHAO + 20, px0 = W - lado + 40;
            g.drawImage(PALCO, px0, -20, px0 + lado, lado - 20, (q % 2) * sw, (q / 2) * sh, (q % 2) * sw + sw, (q / 2) * sh + sh, null);
            g.setColor(new Color(0, 0, 0, 70)); g.fillRect(px0, -20, lado, lado); }
        for (int k = 0; k < 5; k++) {
            double a = Math.sin(t * 1.3 + k * 1.7) * .5; int x0 = 560 + k * 60;
            g.setColor(new Color(k % 2 == 0 ? 255 : 160, 60 + k * 30, 255 - k * 40, 38));
            g.fillPolygon(new int[]{x0, x0 + (int) (Math.sin(a) * 600) - 70, x0 + (int) (Math.sin(a) * 600) + 70}, new int[]{60, CHAO, CHAO}, 3);
        }
        // chao
        g.setColor(new Color(20, 16, 22)); g.fillRect(0, CHAO, W, H - CHAO);
        g.setColor(new Color(50, 40, 50)); for (int x = 0; x < W; x += 8) g.fillRect(x, CHAO + (x * 7 % 11), 4, 2);

        // 1) nave descendo e pousando (0 a 5 s)
        // nave de frente, como no hangar da base: desce na vertical, freia e pousa sobre a sombra
        double p = suave(t / 5.0);
        int baseX = 250, fh = 230, nx = baseX - 110, ny = CHAO - fh + 14 - (int) ((1 - p) * (CHAO + 60));
        java.awt.image.BufferedImage frente = FRENTE[Math.max(0, Math.min(4, modelo))];
        int sombra = (int) (60 + 200 * p); g.setColor(new Color(0, 0, 0, (int) (60 + 80 * p))); g.fillOval(baseX - sombra / 2, CHAO - 10, sombra, 22);
        if (frente != null) {
            int fw = frente.getWidth() * fh / frente.getHeight(); Graphics2D n = (Graphics2D) g.create();
            n.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            n.drawImage(frente, baseX - fw / 2, ny, fw, fh, null);
            // jatos de pouso empurrando para baixo enquanto desce
            if (p < 1) { for (int k = -1; k <= 1; k += 2) { int jx = baseX + k * fw / 4; for (int j = 0; j < 6; j++) { n.setColor(new Color(120, 200, 255, 180 - j * 28)); n.fillRect(jx - 6 + (int) (Math.sin(t * 40 + j) * 2), ny + fh - 10 + j * 10, 12, 10); } } }
            n.dispose();
        } else { Graphics2D n = (Graphics2D) g.create(); n.translate(nx, ny + 120); n.scale(3.2, 3.2); ArteNaves.desenharJogador(n, 6, modelo, 0, 0, t); n.dispose(); }
        if (t > 3.6 && t < 6.5) { // poeira do pouso
            double q = (t - 3.6) / 2.9; g.setColor(new Color(150, 130, 120, (int) (150 * (1 - q))));
            for (int k = 0; k < 12; k++) { int r = (int) (10 + q * 40); g.fillOval(baseX - 100 + k * 18 - r / 2, CHAO - r / 2 - 4, r, r / 2); }
        }

        // 2) o piloto: desce (6 s), anda (7-9 s), tira o macacao (9-11 s), vai para a multidao (11-15 s)
        if (t > 6) {
            double andar = t < 7 ? 0 : t < 9 ? (t - 7) / 2 : 1;
            double sai = t < 11.5 ? 0 : (t - 11.5) / 3.5;
            int px = baseX + 30 + (int) (andar * 190) + (int) (suave(sai) * 300), py = CHAO + (int) (suave(sai) * 60);
            boolean semMacacao = t > 10.2;
            double tirando = t > 9 && t < 10.2 ? (t - 9) / 1.2 : 0;
            if (semMacacao && PILOTO == null) macacaoNoChao(g, baseX + 210, CHAO);
            if (PILOTO != null) {
                int q = t < 7 ? 0 : t < 9 ? (int) (t * 8) % 4 : t < 9.6 ? 4 : t < 10.2 ? 5 : t < 11.5 ? 6 : sai < 1 ? (int) (t * 8) % 2 == 0 ? 6 : 7 : 7;
                int cw = PILOTO.getWidth(null) / 4, ch = PILOTO.getHeight(null) / 2, h = 190, w = cw * h / ch;
                Graphics2D pg = (Graphics2D) g.create(); pg.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                pg.drawImage(PILOTO, px - w / 2, py - h + 6, px + w / 2, py + 6, (q % 4) * cw, (q / 4) * ch, (q % 4) * cw + cw, (q / 4) * ch + ch, null); pg.dispose();
            } else piloto(g, px, py, semMacacao, tirando, (andar > 0 && andar < 1) || sai > 0, t);
        }

        // 3) multidao em primeiro plano, pulando
        for (int k = 0; k < 24; k++) {
            int x = 520 + k * 18 + (k % 3) * 5, pulo = (int) (Math.abs(Math.sin(t * 6 + k * 1.3)) * 8), h = 70 + (k * 7 % 18);
            g.setColor(new Color(150, 70, 200)); g.fillRect(x - 1, H - h - pulo - 1, 22, h + pulo); g.fillOval(x + 1, H - h - pulo - 17, 18, 20); g.setColor(new Color(12, 10, 14)); g.fillRect(x, H - h - pulo, 20, h + pulo); g.fillOval(x + 2, H - h - pulo - 16, 16, 18); g.setColor(new Color(40 + k % 3 * 20, 30, 40)); g.fillRect(x + 2, H - h - pulo - 14, 16, 4); g.setColor(new Color(12, 10, 14));
            if (k % 2 == 0) { g.fillRect(x + (k % 4 == 0 ? -4 : 18), H - h - pulo - 30, 5, 22); g.fillRect(x + (k % 4 == 0 ? -6 : 18), H - h - pulo - 34, 3, 6); g.fillRect(x + (k % 4 == 0 ? -1 : 21), H - h - pulo - 34, 3, 6); }
        }

        // textos
        g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 22));
        texto(g, t < 6 ? "O ULTIMO GRANDE FESTIVAL DO UNIVERSO" : t < 11.5 ? "O SHOW VAI COMECAR" : "HEAVY METAL NEVER DIES", t < 6 ? 0 : 6, W);
        if (t > DURACAO - 1) { g.setComposite(AlphaComposite.SrcOver.derive((float) Math.min(1, t - (DURACAO - 1)))); g.setColor(Color.BLACK); g.fillRect(0, 0, W, H); }
        g.dispose();
    }

    private void texto(Graphics2D g, String s, double inicio, int W) {
        float a = (float) Math.max(0, Math.min(1, (t - inicio) / .6));
        Graphics2D q = (Graphics2D) g.create(); q.setComposite(AlphaComposite.SrcOver.derive(a));
        int w = q.getFontMetrics().stringWidth(s);
        q.setColor(new Color(0, 0, 0, 170)); q.fillRect((W - w) / 2 - 14, 22, w + 28, 36);
        q.setColor(new Color(255, 206, 98)); q.drawString(s, (W - w) / 2, 48); q.dispose();
    }

    /** Flavio em pixel art provisorio: macacao laranja de piloto, ou camisa preta de banda com pentagrama. */
    private static void piloto(Graphics2D g, int x, int pe, boolean camisa, double tirando, boolean andando, double t) {
        int u = 5, perna = andando ? (int) (Math.sin(t * 12) * 2) : 0, y = pe - 34 * u;
        Color pele = new Color(222, 172, 130), cabelo = new Color(40, 28, 22), laranja = new Color(240, 120, 30), preto = new Color(22, 22, 26);
        // pernas
        g.setColor(camisa ? new Color(30, 32, 44) : laranja); g.fillRect(x, y + 20 * u, 3 * u, 12 * u + perna * u); g.fillRect(x + 4 * u, y + 20 * u, 3 * u, 12 * u - perna * u);
        g.setColor(preto); g.fillRect(x - u, pe - 2 * u + perna * u, 5 * u, 2 * u); g.fillRect(x + 3 * u, pe - 2 * u - perna * u, 5 * u, 2 * u);
        // tronco
        g.setColor(camisa ? preto : laranja); g.fillRect(x - u, y + 9 * u, 9 * u, 12 * u);
        if (camisa) { // estampa de banda: pentagrama vermelho
            g.setColor(new Color(220, 30, 30)); int cx = x + 3 * u + 2, cy = y + 14 * u;
            int[] xs = new int[5], ys = new int[5]; for (int k = 0; k < 5; k++) { double a = -Math.PI / 2 + k * 4 * Math.PI / 5; xs[k] = cx + (int) (Math.cos(a) * 9); ys[k] = cy + (int) (Math.sin(a) * 9); }
            g.drawPolygon(xs, ys, 5); g.drawOval(cx - 10, cy - 10, 20, 20);
        } else {
            g.setColor(new Color(245, 245, 240)); g.fillRect(x - u, y + 9 * u, 9 * u, 3 * u); // colete branco
            if (tirando > 0) { g.setColor(new Color(30, 30, 30)); g.fillRect(x + 3 * u, y + 9 * u, u, (int) (12 * u * tirando)); } // ziper abrindo
        }
        // bracos: um erguido fazendo chifrinho quando ja esta de camisa
        g.setColor(camisa ? pele : laranja);
        g.fillRect(x - 3 * u, y + 10 * u, 2 * u, 9 * u);
        if (camisa) { g.fillRect(x + 8 * u, y + 2 * u, 2 * u, 9 * u); g.setColor(pele); g.fillRect(x + 8 * u, y, u, 3 * u); g.fillRect(x + 10 * u - u, y, u, 3 * u); }
        else { g.fillRect(x + 8 * u, y + 10 * u, 2 * u, 9 * u); g.setColor(Color.WHITE); g.fillRect(x + 9 * u, y + 15 * u, 4 * u, 4 * u); } // capacete na mao
        // cabeca
        g.setColor(pele); g.fillRect(x, y + 2 * u, 7 * u, 7 * u);
        g.setColor(cabelo); g.fillRect(x, y + u, 7 * u, 2 * u); g.fillRect(x, y + 6 * u, 7 * u, 3 * u); // cabelo e barba
        g.setColor(pele); g.fillRect(x + u, y + 6 * u, 5 * u, u);
        g.setColor(Color.BLACK); g.fillRect(x + 4 * u, y + 4 * u, u, u);
    }
    private static void macacaoNoChao(Graphics2D g, int x, int pe) {
        g.setColor(new Color(200, 95, 25)); g.fillRect(x, pe - 10, 44, 10); g.setColor(new Color(240, 120, 30)); g.fillRect(x + 4, pe - 14, 30, 6);
        g.setColor(Color.WHITE); g.fillOval(x + 36, pe - 18, 16, 16);
    }
}
