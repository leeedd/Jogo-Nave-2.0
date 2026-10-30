package Jogo;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 * Epilogo 16 bits: lendas no palco com os creditos de cada chefe, homenagem em preto e branco
 * a quem ja partiu, silhueta do Megazord com dois relampagos e "THE END?", e por fim a foto
 * dos criadores com "HEAVY METAL NEVER DIES".
 */
public final class CenaFinal {
    private static final double INICIO_CREDITOS = 14.0, VELOCIDADE = 47, TRANSICAO = 1.5;
    private static final int TOPO_ROLAGEM = 80;
    private static final Image PALCO = Assets.imagem("show_final_098.png");
    private static final BufferedImage PALCO_PB = cinza(PALCO);
    private static final Image SILHUETA = olhosAmarelos(Assets.imagem("megazord_relampago_16bits.png"));
    private static final Image REVELACAO = Assets.imagem("megazord_cabeca_099.png");
    private static final BufferedImage FOTO_EQUIPE = escalar(pixelar(nitida(Assets.imagem("flavio_wenger.jpg"), 150), 12), 2);
    private static final BufferedImage CABECA_NO_ESCURO = cabecaNoEscuro(REVELACAO);

    /** id do sprite (Sprites16Bits, forma 2), nome, banda, contribuicao em duas linhas. */
    private static final Object[][] CHEFES = {
        {2, "ROB HALFORD", "JUDAS PRIEST", "O METAL GOD. CONSAGROU O VISUAL DE COURO", "E TACHAS E OS AGUDOS QUE VIRARAM MARCA DO METAL."},
        {3, "BRUCE DICKINSON", "IRON MAIDEN", "A 'SIRENE ANTIAEREA'. LIDEROU A NOVA ONDA DO", "METAL BRITANICO COM THE NUMBER OF THE BEAST."},
        {4, "LEMMY KILMISTER", "MOTORHEAD", "UNIU A VELOCIDADE DO PUNK AO PESO DO METAL.", "ACE OF SPADES ABRIU CAMINHO PARA O THRASH."},
        {5, "JAMES HETFIELD", "METALLICA", "RIFFS DE PALHETADA PRECISA QUE DEFINIRAM", "O THRASH. MASTER OF PUPPETS VIROU CLASSICO."},
        {6, "TOM ARAYA", "SLAYER", "BAIXO E VOZ DO THRASH MAIS EXTREMO. REIGN IN", "BLOOD INSPIROU O DEATH E O BLACK METAL."},
        {7, "CHUCK BILLY", "TESTAMENT", "VOZ PODEROSA DO THRASH DA BAY AREA. DESDE", "THE LEGACY, EM 1987, MANTEM O THRASH VIVO."},
        {1, "OZZY OSBOURNE", "BLACK SABBATH", "O PRINCIPE DAS TREVAS. COM O BLACK SABBATH,", "AJUDOU A CRIAR O HEAVY METAL EM 1970."},
        {-5, "TENACIOUS D", "AGRADECIMENTO ESPECIAL / DEEZLEBOSS", "TRIBUTE E THE PICK OF DESTINY CELEBRARAM O", "ROCK COM HUMOR, RIFFS E MUITA REVERENCIA."}
    };
    /** Lendas que partiram; id negativo = retrato de RetratosLendas. O ultimo fecha a homenagem. */
    private static final Object[][] LENDAS = {
        {-3, "CLIFF BURTON", "METALLICA", "1962 - 1986", "O BAIXO DE MASTER OF PUPPETS. TROUXE MELODIA", "E HARMONIA AO THRASH, COMO EM ORION."},
        {-4, "RONNIE JAMES DIO", "RAINBOW / BLACK SABBATH / DIO", "1942 - 2010", "UMA DAS MAIORES VOZES DO METAL. ESPALHOU", "OS CHIFRES ERGUIDOS PELOS PALCOS DO MUNDO."},
        {4, "LEMMY KILMISTER", "MOTORHEAD", "1945 - 2015", "VIVEU DO JEITO QUE TOCAVA: ALTO, RAPIDO E", "SEM CONCESSOES. BORN TO LOSE, LIVE TO WIN."},
        {-1, "CHESTER BENNINGTON", "LINKIN PARK", "1976 - 2017", "A VOZ DE HYBRID THEORY. SEU GRITO E SUA DOR", "FALARAM POR UMA GERACAO INTEIRA."},
        {-2, "JOEY JORDISON", "SLIPKNOT", "1975 - 2021", "BATERISTA FUNDADOR DO SLIPKNOT. VELOCIDADE E", "TECNICA QUE INSPIRARAM INCONTAVEIS BATERISTAS."},
        {1, "OZZY OSBOURNE", "BLACK SABBATH", "1948 - 2025", "O PRINCIPE DAS TREVAS. ONDE TUDO COMECOU.", "OBRIGADO, OZZY."}
    };
    private static final String[] ENCERRAMENTO_CREDITOS = {
        "A JORNADA COMECOU COM A LULA DO ABISMO", "", "DESENVOLVIDO POR FLAVIO PRADO", "JOGO ORIGINAL - 2019",
        "", "DE FAS DE METAL, PARA FAS DE METAL"
    };

    private static final BufferedImage ROLO_CHEFES = rolo(false);
    private static final BufferedImage ROLO_LENDAS = rolo(true);
    private static int centroOzzy;

    // Linha do tempo, calculada a partir da altura dos creditos.
    private static final double FIM_CHEFES = INICIO_CREDITOS + (Config.ALTURA - TOPO_ROLAGEM + ROLO_CHEFES.getHeight()) / VELOCIDADE;
    private static final double INICIO_LENDAS = FIM_CHEFES + TRANSICAO;
    private static final double INICIO_ROLO_LENDAS = INICIO_LENDAS + 5.5;
    private static final double OZZY_NO_CENTRO = INICIO_ROLO_LENDAS + (Config.ALTURA - Config.ALTURA / 2.0 + centroOzzy) / VELOCIDADE;
    private static final double FIM_LENDAS = OZZY_NO_CENTRO + 5.0;
    private static final double INICIO_SILHUETA = FIM_LENDAS + TRANSICAO + 1.0;
    private static final double CORTE_CABECA = INICIO_SILHUETA + 5.5;
    private static final double[][] RELAMPAGOS = {{CORTE_CABECA + 1.7, CORTE_CABECA + 1.86}, {CORTE_CABECA + 2.22, CORTE_CABECA + 2.44}};
    private static final double TEXTO_FINAL = CORTE_CABECA + 3.5;
    private static final double INICIO_FOTO = TEXTO_FINAL + 4.0;
    private static final double FOTO_COMPLETA = INICIO_FOTO + TRANSICAO;

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
    public boolean podeEncerrar() { return tempo >= FOTO_COMPLETA; }
    static double duracaoTeste() { return FOTO_COMPLETA; }

    public void desenhar(Graphics2D base) {
        Graphics2D g = (Graphics2D) base.create();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        int w = Config.LARGURA, h = Config.ALTURA;
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, w, h);
        if (tempo < FIM_CHEFES) desenharPalco(g, w, h);
        else if (tempo < FIM_LENDAS) desenharLendas(g, w, h);
        else if (tempo < INICIO_SILHUETA) {
            // Tela escura antes do Megazord.
        } else if (tempo < CORTE_CABECA) desenharSilhueta(g, w, h);
        else if (tempo < INICIO_FOTO) desenharCabeca(g, w, h);
        else desenharFoto(g, w, h);
        g.dispose();
    }

    private void desenharFundoPalco(Graphics2D g, int w, int h, Image palco) {
        int quadro = ((int) (tempo * 6)) % 4;
        int sw = palco.getWidth(null) / 2, sh = palco.getHeight(null) / 2;
        int sx = (quadro % 2) * sw, sy = (quadro / 2) * sh;
        // Preserve aspect ratio: concert artwork is square, never stretch musicians.
        int lado = Math.min(w, h), margem = (w - lado) / 2;
        g.drawImage(palco, margem, 0, margem + lado, lado, sx, sy, sx + sw, sy + sh, null);
    }

    private void desenharPalco(Graphics2D g, int w, int h) {
        g.setColor(new Color(1, 1, 12));
        g.fillRect(0, 0, w, h);
        desenharFundoPalco(g, w, h, PALCO);
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
        centralizar(g, "AS LENDAS SOBEM AO PALCO!", w, 58);
        // Os creditos sobem durante a musica, como em um arcade.
        rolar(g, ROLO_CHEFES, (int) (h - (tempo - INICIO_CREDITOS) * VELOCIDADE), w, h);
        escurecer(g, w, h, (tempo - (FIM_CHEFES - TRANSICAO)) / TRANSICAO);
    }

    private void desenharLendas(Graphics2D g, int w, int h) {
        // O palco fica em preto e branco: homenagem a quem partiu.
        desenharFundoPalco(g, w, h, PALCO_PB);
        g.setColor(new Color(0, 0, 0, 200));
        g.fillRect(0, 0, w, h);
        if (tempo < INICIO_ROLO_LENDAS) {
            double t = tempo - INICIO_LENDAS;
            g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 30));
            g.setColor(new Color(230, 230, 230));
            centralizar(g, "ELES NOS DEIXARAM,", w, 270);
            centralizar(g, "MAS SUA OBRA PERMANECE VIVA", w, 320);
            escurecer(g, w, h, Math.max(1 - t / TRANSICAO, (t - 4.0) / TRANSICAO));
        } else {
            double rolagem = Math.min(tempo, OZZY_NO_CENTRO) - INICIO_ROLO_LENDAS;
            rolar(g, ROLO_LENDAS, (int) (h - rolagem * VELOCIDADE), w, h);
            g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 22));
            g.setColor(new Color(205, 205, 205));
            centralizar(g, "LENDAS ETERNAS", w, 58);
        }
        escurecer(g, w, h, (tempo - (FIM_LENDAS - TRANSICAO)) / TRANSICAO);
    }

    private static void rolar(Graphics2D g, BufferedImage rolo, int y, int w, int h) {
        Graphics2D a = (Graphics2D) g.create();
        a.clipRect(0, TOPO_ROLAGEM, w, h - TOPO_ROLAGEM);
        a.drawImage(rolo, 0, y, null);
        a.dispose();
    }

    private static void centralizar(Graphics2D g, String s, int w, int y) {
        g.drawString(s, (w - g.getFontMetrics().stringWidth(s)) / 2, y);
    }

    /** Monta a faixa de creditos uma unica vez: retrato em pixel art ao lado do texto. */
    private static BufferedImage rolo(boolean lendas) {
        int w = Config.LARGURA;
        Object[][] itens = lendas ? LENDAS : CHEFES;
        int altura = 60 + itens.length * 190 + (lendas ? 160 : ENCERRAMENTO_CREDITOS.length * 44 + 40);
        BufferedImage b = new BufferedImage(w, altura, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = b.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        int y = 20;
        if (!lendas) {
            g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 24));
            g.setColor(new Color(255, 210, 94));
            centralizar(g, "METAL SPACE - LEGENDS OF METAL", w, y + 24);
            g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 18));
            g.setColor(Color.WHITE);
            centralizar(g, "UMA HOMENAGEM AO HEAVY METAL", w, y + 56);
            y += 100;
        }
        for (int i = 0; i < itens.length; i++) {
            Object[] it = itens[i];
            boolean fecho = lendas && i == itens.length - 1;
            int lado = fecho ? 200 : 140;
            if (fecho) {
                y += 30;
                centroOzzy = y + lado / 2;
                int x = (w - lado) / 2;
                retrato(g, (Integer) it[0], x, y, lado);
                int ty = y + lado + 42;
                g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 32));
                g.setColor(Color.WHITE);
                centralizar(g, (String) it[1], w, ty);
                g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 18));
                g.setColor(new Color(185, 185, 185));
                centralizar(g, it[2] + "  /  " + it[3], w, ty + 30);
                g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 17));
                g.setColor(new Color(230, 230, 230));
                centralizar(g, (String) it[4], w, ty + 60);
                centralizar(g, (String) it[5], w, ty + 84);
                centroOzzy = y + (lado + 126) / 2;
                y = ty + 110;
                continue;
            }
            int x = 120;
            retrato(g, (Integer) it[0], x, y, lado);
            int tx = x + lado + 30;
            g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 26));
            g.setColor(lendas ? Color.WHITE : new Color(255, 210, 94));
            g.drawString((String) it[1], tx, y + 34);
            g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 18));
            g.setColor(lendas ? new Color(185, 185, 185) : new Color(110, 222, 245));
            g.drawString(lendas ? it[2] + "  /  " + it[3] : (String) it[2], tx, y + 62);
            g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 16));
            g.setColor(new Color(232, 232, 232));
            int base = lendas ? 4 : 3;
            g.drawString((String) it[base], tx, y + 96);
            g.drawString((String) it[base + 1], tx, y + 120);
            y += 190;
        }
        if (!lendas) {
            g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 21));
            y += 20;
            for (int i = 0; i < ENCERRAMENTO_CREDITOS.length; i++) {
                String linha = ENCERRAMENTO_CREDITOS[i];
                g.setColor(i == 0 || linha.startsWith("AGRADECIMENTOS") ? new Color(255, 210, 94) : Color.WHITE);
                centralizar(g, linha, w, y + i * 44);
            }
        }
        g.dispose();
        return lendas ? cinza(b) : b;
    }

    /** Retrato em moldura do HUD: sprite do chefe, retrato desenhado, ou uma vela quando nao ha nenhum. */
    private static void retrato(Graphics2D g, int id, int x, int y, int lado) {
        HudMetal.painel(g, x - 8, y - 8, lado + 16, lado + 16);
        BufferedImage s = id < 0 ? RetratosLendas.obter(-id) : RetratosLendas.chefe(id);
        boolean sprite = s == null && id > 0;
        if (sprite) s = Sprites16Bits.carregar(id, 2);
        if (s != null) {
            double e = Math.min(lado / (double) s.getWidth(), lado / (double) s.getHeight());
            int sw = (int) (s.getWidth() * e), sh = (int) (s.getHeight() * e);
            // Sprites do jogo encaram a esquerda como nas fases; retratos novos ja vem virados.
            if (sprite) Sprites16Bits.desenharVirado(g, s, id, 2, x + (lado - sw) / 2, y + (lado - sh) / 2, sw, sh);
            else g.drawImage(s, x + (lado - sw) / 2, y + (lado - sh) / 2, sw, sh, null);
            return;
        }
        int p = lado / 28, cx = x + lado / 2;
        g.setColor(new Color(40, 40, 40));
        g.fillRect(x, y, lado, lado);
        g.setColor(new Color(255, 255, 255, 30));
        g.fillOval(cx - 9 * p, y + 3 * p, 18 * p, 18 * p);
        g.setColor(new Color(220, 220, 220));
        g.fillRect(cx - 2 * p, y + 11 * p, 4 * p, 12 * p);
        g.setColor(new Color(150, 150, 150));
        g.fillRect(cx + p, y + 11 * p, p, 12 * p);
        g.fillRect(cx - 5 * p, y + 23 * p, 10 * p, 2 * p);
        g.setColor(new Color(90, 90, 90));
        g.fillRect(cx - 1, y + 9 * p, 2, 2 * p);
        g.setColor(new Color(250, 250, 250));
        g.fillRect(cx - p, y + 5 * p, 2 * p, 4 * p);
        g.fillRect(cx - p / 2, y + 4 * p, p, p);
        g.setColor(new Color(200, 200, 200));
        g.fillRect(cx - p / 2, y + 7 * p, p, 2 * p);
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
            centralizar(g, "THE END?", w, 540);
        }
        escurecer(g, w, h, (tempo - (INICIO_FOTO - TRANSICAO)) / TRANSICAO);
    }

    private void desenharFoto(Graphics2D g, int w, int h) {
        // Chave de ouro: foto dos criadores em pixel art nitido, o lema e o agradecimento.
        int fw = FOTO_EQUIPE.getWidth(), fh = FOTO_EQUIPE.getHeight();
        int fx = (w - fw) / 2, fy = 34;
        HudMetal.painel(g, fx - 14, fy - 14, fw + 28, fh + 28);
        g.drawImage(FOTO_EQUIPE, fx, fy, fw, fh, null);
        g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 40));
        String lema = "HEAVY METAL NEVER DIES";
        int ly = fy + fh + 62;
        FontMetrics fm = g.getFontMetrics();
        int lx = (w - fm.stringWidth(lema)) / 2;
        g.setColor(new Color(60, 8, 14));
        g.drawString(lema, lx + 3, ly + 3);
        g.setColor(new Color(238, 72, 83));
        g.drawString(lema, lx, ly);
        g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 20));
        g.setColor(new Color(255, 213, 98));
        centralizar(g, "OBRIGADO POR JOGAR!", w, ly + 40);
        if (podeEncerrar() && ((int) (tempo * 2)) % 2 == 0) {
            g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 13));
            g.setColor(new Color(225, 221, 231));
            centralizar(g, "ENTER - VER O PLACAR", w, h - 8);
        }
        escurecer(g, w, h, 1 - (tempo - INICIO_FOTO) / TRANSICAO);
    }

    /** Copia em tons de cinza, mantendo a transparencia. */
    static BufferedImage cinza(Image img) {
        int w = img.getWidth(null), h = img.getHeight(null);
        BufferedImage o = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = o.createGraphics();
        g.drawImage(img, 0, 0, null);
        g.dispose();
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int c = o.getRGB(x, y);
                int l = (int) (.3 * (c >> 16 & 255) + .59 * (c >> 8 & 255) + .11 * (c & 255));
                o.setRGB(x, y, c & 0xFF000000 | l << 16 | l << 8 | l);
            }
        }
        return o;
    }

    private static void escurecer(Graphics2D g, int w, int h, double quanto) {
        if (quanto <= 0) return;
        g.setComposite(AlphaComposite.SrcOver.derive((float) Math.min(1, quanto)));
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, w, h);
        g.setComposite(AlphaComposite.SrcOver);
    }

    /** Reduz a foto em etapas de metade, com interpolacao bicubica, para manter a nitidez. */
    static BufferedImage nitida(Image foto, int largura) {
        int w = foto.getWidth(null), h = foto.getHeight(null);
        BufferedImage atual = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = atual.createGraphics();
        g.drawImage(foto, 0, 0, null);
        g.dispose();
        int alvoH = Math.round(largura * h / (float) w);
        while (w > largura) {
            w = Math.max(largura, w / 2);
            h = w == largura ? alvoH : h / 2;
            BufferedImage menor = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
            g = menor.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g.drawImage(atual, 0, 0, w, h, null);
            g.dispose();
            atual = menor;
        }
        return atual;
    }

    /** Pixel art nitido: realca contornos, clareia as sombras e reduz para poucos tons por canal. */
    static BufferedImage pixelar(BufferedImage foto, int tons) {
        int w = foto.getWidth(), h = foto.getHeight();
        int[] o = foto.getRGB(0, 0, w, h, null, 0, w);
        BufferedImage p = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        double passo = 255.0 / (tons - 1);
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int cor = 0;
                for (int desloc = 16; desloc >= 0; desloc -= 8) {
                    int c = canal(o, w, h, x, y, desloc), soma = 0;
                    for (int[] d : new int[][]{{-1, 0}, {1, 0}, {0, -1}, {0, 1}}) soma += canal(o, w, h, x + d[0], y + d[1], desloc);
                    double v = c + (c - soma / 4.0) * .9;
                    v = 255 * Math.pow(Math.max(0, Math.min(255, v)) / 255.0, .82);
                    cor = (cor << 8) | (int) (Math.round(v / passo) * passo);
                }
                p.setRGB(x, y, cor);
            }
        }
        return p;
    }

    private static int canal(int[] o, int w, int h, int x, int y, int desloc) {
        return o[Math.max(0, Math.min(h - 1, y)) * w + Math.max(0, Math.min(w - 1, x))] >> desloc & 255;
    }

    /** Amplia sem suavizar, mantendo cada pixel como um bloco. */
    static BufferedImage escalar(BufferedImage img, int fator) {
        BufferedImage o = new BufferedImage(img.getWidth() * fator, img.getHeight() * fator, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = o.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        g.drawImage(img, 0, 0, o.getWidth(), o.getHeight(), null);
        g.dispose();
        return o;
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
