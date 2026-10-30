package Jogo;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

/**
 * Epilogo 16 bits: lendas no palco com creditos, transicao para a foto dos criadores,
 * silhueta do Megazord, corte para a cabeca com olhos acesos, dois relampagos e "THE END?".
 */
public final class CenaFinal {
    private static final double INICIO_CREDITOS = 14.0;
    private static final double FINAL_CREDITOS = 58.0;
    private static final double TRANSICAO = 1.2;
    private static final double FINAL_FOTO = 66.0;
    private static final double INICIO_SILHUETA = 67.0;
    private static final double CORTE_CABECA = 72.5;
    private static final double[][] RELAMPAGOS = {{74.2, 74.36}, {74.72, 74.94}};
    private static final double TEXTO_FINAL = 76.0;
    private static final Image PALCO = Assets.imagem("show_final_098.png");
    private static final Image SILHUETA = olhosAmarelos(Assets.imagem("megazord_relampago_16bits.png"));
    private static final Image REVELACAO = Assets.imagem("megazord_cabeca_099.png");
    private static final BufferedImage FOTO_EQUIPE = pixelar(Assets.imagem("flavio_wenger.jpg"), 100, 8);
    private static final BufferedImage CABECA_NO_ESCURO = cabecaNoEscuro(REVELACAO);
    private static final String[] CREDITOS = {
        "METAL SPACE - LEGENDS OF METAL", "", "UMA HOMENAGEM AO HEAVY METAL",
        "", "OZZY OSBOURNE", "ROB HALFORD", "BRUCE DICKINSON",
        "LEMMY KILMISTER", "JAMES HETFIELD", "TOM ARAYA", "CHUCK BILLY",
        "", "A JORNADA COMECOU COM A LULA DO ABISMO",
        "", "DESENVOLVIDO POR FLAVIO PRADO", "JOGO ORIGINAL - 2019",
        "", "RONNIE JAMES DIO", "TENACIOUS D / DEEZLEBOSS",
        "", "DE FAS DE METAL, PARA FAS DE METAL",
        "", "OBRIGADO POR JOGAR!", "LONG LIVE ROCK N ROLL"
    };
    private double tempo;

    public CenaFinal(){SomJogo.tocar(SomJogo.Efeito.VITORIA);}
    public void atualizar(double dt) {
        double antes = tempo;
        if (!Double.isNaN(dt) && !Double.isInfinite(dt)) tempo += Math.max(0, dt);
        if (passou(antes, INICIO_SILHUETA + .3)) SomJogo.tocar(SomJogo.Efeito.TROVAO);
        for (double[] r : RELAMPAGOS) if (passou(antes, r[0])) SomJogo.tocar(SomJogo.Efeito.TROVAO);
    }
    private boolean passou(double antes, double marco) { return antes < marco && tempo >= marco; }
    public double getTempo() { return tempo; }
    public boolean mostrandoMegazord() { return tempo >= INICIO_SILHUETA; }
    public boolean podeEncerrar() { return tempo >= TEXTO_FINAL; }

    public void desenhar(Graphics2D base) {
        Graphics2D g = (Graphics2D) base.create();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        int w = Config.LARGURA, h = Config.ALTURA;
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, w, h);
        if (tempo < FINAL_CREDITOS) desenharPalco(g, w, h);
        else if (tempo < FINAL_FOTO) desenharFoto(g, w, h);
        else if (tempo < INICIO_SILHUETA) {
            // Pausa no escuro antes da cutscene.
        } else if (tempo < CORTE_CABECA) desenharSilhueta(g, w, h);
        else desenharCabeca(g, w, h);
        g.dispose();
    }

    private void desenharPalco(Graphics2D g, int w, int h) {
        g.setColor(new Color(1, 1, 12));
        g.fillRect(0, 0, w, h);
        int quadro = ((int) (tempo * 6)) % 4;
        int sw = PALCO.getWidth(null) / 2, sh = PALCO.getHeight(null) / 2;
        int sx = (quadro % 2) * sw, sy = (quadro / 2) * sh;
        // Preserve aspect ratio: concert artwork is square, never stretch musicians.
        int lado = Math.min(w, h), margem = (w - lado) / 2;
        g.drawImage(PALCO, margem, 0, margem + lado, lado,
                sx, sy, sx + sw, sy + sh, null);
        for (int i = 0; i < 6; i++) {
            int x = 45 + i * 170;
            int ponta = x + (int) (Math.sin(tempo * 1.4 + i) * 110);
            g.setColor(new Color(i % 2 == 0 ? 35 : 180, 70, 230, 22));
            g.fillPolygon(new int[]{x, ponta - 55, ponta + 55}, new int[]{0, h, h}, 3);
        }
        if (tempo < INICIO_CREDITOS) {
            g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 20));
            g.setColor(new Color(255, 218, 134));
            String titulo = "DE FAS DE METAL, PARA FAS DE METAL";
            g.drawString(titulo, (w - g.getFontMetrics().stringWidth(titulo)) / 2, 35);
            return;
        }
        g.setColor(new Color(0, 0, 15, 170));
        g.fillRect(0, 0, w, h);
        g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 25));
        g.setColor(new Color(253, 214, 120));
        g.drawString("AS LENDAS SOBEM AO PALCO!", 245, 58);
        g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 21));
        // Os nomes sobem durante a musica, como nos creditos de um arcade.
        int yInicial = (int) (h + 100 - (tempo - INICIO_CREDITOS) * 47);
        for (int i = 0; i < CREDITOS.length; i++) {
            int y = yInicial + i * 48;
            if (y < 95 || y > h + 20) continue;
            String linha = CREDITOS[i];
            int x = (w - g.getFontMetrics().stringWidth(linha)) / 2;
            g.setColor(i == 0 || i == 12 ? new Color(255, 210, 94) : Color.WHITE);
            g.drawString(linha, x, y);
        }
        escurecer(g, w, h, (tempo - (FINAL_CREDITOS - TRANSICAO)) / TRANSICAO);
    }

    private void desenharFoto(Graphics2D g, int w, int h) {
        // Foto dos criadores em pixel art, moldura do HUD e o lema do jogo.
        int fw = FOTO_EQUIPE.getWidth() * 3, fh = FOTO_EQUIPE.getHeight() * 3;
        int fx = (w - fw) / 2, fy = 48;
        HudMetal.painel(g, fx - 14, fy - 14, fw + 28, fh + 28);
        g.drawImage(FOTO_EQUIPE, fx, fy, fw, fh, null);
        g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 40));
        String lema = "HEAVY METAL NEVER DIES";
        int lx = (w - g.getFontMetrics().stringWidth(lema)) / 2, ly = fy + fh + 68;
        g.setColor(new Color(60, 8, 14));
        g.drawString(lema, lx + 3, ly + 3);
        g.setColor(new Color(238, 72, 83));
        g.drawString(lema, lx, ly);
        g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 14));
        g.setColor(new Color(255, 213, 98));
        String nomes = "FLAVIO & WENGER";
        g.drawString(nomes, (w - g.getFontMetrics().stringWidth(nomes)) / 2, ly + 26);
        double entrada = 1 - (tempo - FINAL_CREDITOS) / TRANSICAO;
        double saida = (tempo - (FINAL_FOTO - TRANSICAO)) / TRANSICAO;
        escurecer(g, w, h, Math.max(entrada, saida));
    }

    private void desenharSilhueta(Graphics2D g, int w, int h) {
        // Aproximacao lenta da silhueta completa sob a tempestade.
        double t = tempo - INICIO_SILHUETA;
        double zoom = 1 + t * .018;
        int sw = (int) (w * zoom), sh = (int) (h * zoom);
        g.drawImage(SILHUETA, (w - sw) / 2, (h - sh) / 2, sw, sh, null);
        escurecer(g, w, h, 1 - t / 1.0);
    }

    private void desenharCabeca(Graphics2D g, int w, int h) {
        // Corte seco: so os olhos amarelos encaram o jogador ate os relampagos.
        int cx = 240, cy = 75, cw = 480, ch = 385;
        boolean clarao = false;
        for (double[] r : RELAMPAGOS) clarao |= tempo >= r[0] && tempo < r[1];
        if (clarao) {
            g.drawImage(REVELACAO, cx, cy, cw, ch, null);
            g.setComposite(AlphaComposite.SrcOver.derive(.13f));
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, w, h);
            g.setComposite(AlphaComposite.SrcOver);
        } else {
            float brilho = (float) (.78 + .22 * Math.sin(tempo * 3.1));
            g.setComposite(AlphaComposite.SrcOver.derive(Math.min(1, (float) (tempo - CORTE_CABECA) / .5f) * brilho));
            g.drawImage(CABECA_NO_ESCURO, cx, cy, cw, ch, null);
            g.setComposite(AlphaComposite.SrcOver);
        }
        if (tempo >= TEXTO_FINAL) {
            g.setColor(new Color(238, 72, 83));
            g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 75));
            String titulo = "THE END?";
            g.drawString(titulo, (w - g.getFontMetrics().stringWidth(titulo)) / 2, 540);
            if (((int) (tempo * 2)) % 2 == 0) {
                g.setColor(new Color(225, 221, 231));
                g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 15));
                String dica = "ENTER - VER O PLACAR";
                g.drawString(dica, (w - g.getFontMetrics().stringWidth(dica)) / 2, 580);
            }
        }
    }

    private static void escurecer(Graphics2D g, int w, int h, double quanto) {
        if (quanto <= 0) return;
        g.setComposite(AlphaComposite.SrcOver.derive((float) Math.min(1, quanto)));
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, w, h);
        g.setComposite(AlphaComposite.SrcOver);
    }

    /** Reduz a foto para uma grade de pixels e poucos tons por canal, no visual 16 bits. */
    static BufferedImage pixelar(Image foto, int largura, int tons) {
        int altura = Math.max(1, Math.round(largura * foto.getHeight(null) / (float) foto.getWidth(null)));
        BufferedImage p = new BufferedImage(largura, altura, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = p.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.drawImage(foto, 0, 0, largura, altura, null);
        g.dispose();
        double passo = 255.0 / (tons - 1);
        for (int y = 0; y < altura; y++) {
            for (int x = 0; x < largura; x++) {
                int rgb = p.getRGB(x, y), cor = 0;
                for (int desloc = 16; desloc >= 0; desloc -= 8) {
                    int c = (rgb >> desloc) & 255;
                    // Leve contraste antes de reduzir os tons, como em sprites de arcade.
                    c = Math.max(0, Math.min(255, (int) ((c - 128) * 1.12 + 128)));
                    cor = (cor << 8) | (int) (Math.round(c / passo) * passo);
                }
                p.setRGB(x, y, cor);
            }
        }
        return p;
    }

    /** Cabeca apagada: silhueta escura com contorno discreto; apenas os olhos amarelos acesos. */
    static BufferedImage cabecaNoEscuro(Image revelacao) {
        int w = revelacao.getWidth(null), h = revelacao.getHeight(null);
        BufferedImage o = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = o.createGraphics();
        g.drawImage(revelacao, 0, 0, null);
        g.dispose();
        boolean[] corpo = new boolean[w * h];
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int c = o.getRGB(x, y), r = c >> 16 & 255, gr = c >> 8 & 255, b = c & 255;
                boolean raio = b > 150 && b > r + 30;
                corpo[y * w + x] = !raio && r + gr + b > 54;
            }
        }
        // Fundo = o que se alcanca a partir das bordas; o resto vira silhueta solida, sem detalhes internos.
        // Fecha as frestas escuras entre as placas para o preenchimento nao vazar para dentro.
        corpo = dilatar(corpo, w, h, 6);
        boolean[] fora = new boolean[w * h];
        int[] fila = new int[w * h];
        int ler = 0, fim = 0;
        for (int x = 0; x < w; x++) for (int y : new int[]{0, h - 1}) if (!corpo[y * w + x] && !fora[y * w + x]) { fora[y * w + x] = true; fila[fim++] = y * w + x; }
        for (int y = 0; y < h; y++) for (int x : new int[]{0, w - 1}) if (!corpo[y * w + x] && !fora[y * w + x]) { fora[y * w + x] = true; fila[fim++] = y * w + x; }
        while (ler < fim) {
            int i = fila[ler++], x = i % w, y = i / w;
            int[] viz = {x > 0 ? i - 1 : -1, x < w - 1 ? i + 1 : -1, y > 0 ? i - w : -1, y < h - 1 ? i + w : -1};
            for (int n : viz) if (n >= 0 && !fora[n] && !corpo[n]) { fora[n] = true; fila[fim++] = n; }
        }
        BufferedImage s = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        for (int y = 1; y < h - 1; y++) {
            for (int x = 1; x < w - 1; x++) {
                int i = y * w + x;
                if (fora[i]) continue;
                int c = o.getRGB(x, y), r = c >> 16 & 255, gr = c >> 8 & 255, b = c & 255;
                double nx = x / (double) w, ny = y / (double) h;
                boolean olho = nx > .3 && nx < .7 && Math.abs(nx - .5) > .045 && ny > .46 && ny < .62 && r > 150 && gr > 105 && b < r - 40;
                boolean borda = fora[i - 1] || fora[i + 1] || fora[i - w] || fora[i + w];
                s.setRGB(x, y, olho ? 0xFFFFE04A : borda ? 0xFF22223A : 0xFF06060B);
            }
        }
        // Preenche o miolo de cada olho, linha a linha, entre as bordas acesas.
        for (int y = (int) (h * .46); y < h * .62; y++) {
            for (int lado = 0; lado < 2; lado++) {
                int ini = -1, fimOlho = -1;
                for (int x = (int) (w * (lado == 0 ? .3 : .5)); x < w * (lado == 0 ? .5 : .7); x++)
                    if (s.getRGB(x, y) == 0xFFFFE04A) { if (ini < 0) ini = x; fimOlho = x; }
                if (ini >= 0 && fimOlho - ini < w * .12)
                    for (int x = ini; x <= fimOlho; x++) s.setRGB(x, y, 0xFFFFE04A);
            }
        }
        // Brilho suave em volta dos olhos.
        Graphics2D a = s.createGraphics();
        a.setComposite(AlphaComposite.getInstance(AlphaComposite.DST_OVER));
        for (double ex : new double[]{.415, .585}) {
            for (int k = 4; k >= 1; k--) {
                a.setColor(new Color(255, 200, 40, 10 * k));
                int rx = (int) (w * .045 * (5 - k) / 2.0), ry = (int) (h * .03 * (5 - k) / 2.0);
                a.fillOval((int) (w * ex) - rx, (int) (h * .545) - ry, rx * 2, ry * 2);
            }
        }
        a.dispose();
        return s;
    }

    /** Na silhueta completa, os olhos vermelhos passam a amarelos, como na cabeca. */
    static BufferedImage olhosAmarelos(Image silhueta) {
        int w = silhueta.getWidth(null), h = silhueta.getHeight(null);
        BufferedImage o = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = o.createGraphics();
        g.drawImage(silhueta, 0, 0, null);
        g.dispose();
        for (int y = (int) (h * .1); y < h * .22; y++) {
            for (int x = (int) (w * .5); x < w * .6; x++) {
                int c = o.getRGB(x, y), r = c >> 16 & 255, gr = c >> 8 & 255, b = c & 255;
                if (r > 90 && r > gr * 1.6 && r > b * 1.6) o.setRGB(x, y, Math.min(255, r + 20) << 16 | Math.min(255, r * 85 / 100) << 8 | b / 3);
            }
        }
        return o;
    }

    private static boolean[] dilatar(boolean[] m, int w, int h, int raio) {
        boolean[] lin = new boolean[w * h], out = new boolean[w * h];
        for (int y = 0; y < h; y++) for (int x = 0; x < w; x++) if (m[y * w + x])
            for (int d = Math.max(0, x - raio); d <= Math.min(w - 1, x + raio); d++) lin[y * w + d] = true;
        for (int x = 0; x < w; x++) for (int y = 0; y < h; y++) if (lin[y * w + x])
            for (int d = Math.max(0, y - raio); d <= Math.min(h - 1, y + raio); d++) out[d * w + x] = true;
        return out;
    }
}
