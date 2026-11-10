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
    private static final Image SILHUETA = olhosAmarelos(Assets.imagem("megazord_relampago_16bits.png"));
    private static final Image REVELACAO = Assets.imagem("megazord_cabeca_099.png");
    /** Letreiro HEAVY METAL NEVER DIES em pixel art (caveira com chifres e correntes) embaixo da foto. */
    private static final BufferedImage LEMA = lerLema();
    private static final BufferedImage DESTEMIDOS = primeiroQuadro("jack_violao.png");
    private static BufferedImage primeiroQuadro(String n) { java.net.URL u = CenaFinal.class.getResource("/ImagemIcon/" + n); if (u == null) return null; try { BufferedImage a = javax.imageio.ImageIO.read(u); return a.getSubimage(0, 0, a.getWidth() / 4, a.getHeight()); } catch (java.io.IOException e) { return null; } }
    private static BufferedImage lerLema() { java.net.URL u = CenaFinal.class.getResource("/ImagemIcon/heavy_metal_never_dies_final.png"); if (u == null) return null; try { return javax.imageio.ImageIO.read(u); } catch (java.io.IOException e) { return null; } }
    private static final BufferedImage FOTO_EQUIPE = escalar(pixelar(nitida(Assets.imagem("flavio_wenger.jpg"), 150), 12), 2);
    private static final BufferedImage CABECA_NO_ESCURO = cabecaNoEscuro(REVELACAO);

    /** id do sprite (Sprites16Bits, forma 2), nome, banda, contribuicao em duas linhas. */
    private static final Object[][] CHEFES = {
        {2, "VALDRAK", "LEGIAO CROMADA", "CIBORGUE DE CROMO DA FORNALHA. SEU CHICOTE", "DE ENERGIA CORTA NAVES AO MEIO."},
        {3, "ALMIRANTE KORVUS", "VORTICE NEGRO", "PIRATA CEFALOPODE. SEU GRITO AGUDO RACHA", "O VIDRO DAS CABINES A ANOS-LUZ."},
        {4, "KROVAX", "FERRUGEM ATOMICA", "CENTOPEIA DE SUCATA. CANTA COM O MICROFONE", "PRESO NAS PRESAS E CUSPE FOGO PELAS PATAS."},
        {5, "HEXAR", "CINZAS DE ORION", "INSETOIDE DE QUATRO BRACOS. PUXAVA OS FIOS", "DE UM BONECO ATE O BONECO QUEBRAR."},
        {6, "MORGATH", "NECROPOLIS", "REI MORTO-VIVO DE OBSIDIANA. TRAZ O FOGO", "DO SUBSOLO EM CADA REFRAO."},
        {7, "KANOA", "FURIA DA TORMENTA", "COLOSSO DE ROCHA E RAIO. QUANDO CANTA,", "O CEU RESPONDE COM TROVOES."},
        {1, "MALACHAR", "ECLIPSE PROFANO", "O MAESTRO DO ECLIPSE. REGE A NOITE COM UMA", "BATUTA E APAGA AS ESTRELAS NO COMPASSO."},
        {-5, "OS DESTEMIDOS", "BARDO E MONGE / BELZEBU", "A DUPLA DE VIOLAO QUE GUARDAVA A PALHETA", "MALDITA. QUASE ACABOU COM O UNIVERSO."}
    };
    /** A homenagem e uma dedicatoria em texto, sem nomes nem retratos de pessoas reais. */
    private static final Object[][] LENDAS = {};
    private static final String[] ENCERRAMENTO_CREDITOS = {
        "A JORNADA COMECOU COM A LULA DO ABISMO", "", "DESENVOLVIDO POR FLAVIO PRADO", "JOGO ORIGINAL - 2019",
        "", "DE FAS DE METAL, PARA FAS DE METAL"
    };

    private static final BufferedImage ROLO_CHEFES = rolo(false);
    private static final BufferedImage ROLO_LENDAS = rolo(true);
    private static int centroOzzy;

    // Linha do tempo, calculada a partir da altura dos creditos.
    /** Letreiro: titulo no topo do rolo, as bandas a cada PASSO e o fecho, que para no meio da tela. */
    private static final int OFF_CHEFES = 200, PASSO_LETREIRO = 190, OFF_FECHO = OFF_CHEFES + CHEFES.length * PASSO_LETREIRO + 90;
    private static final double PARADA_LETREIRO = Config.ALTURA / 2.0 - 90 + OFF_FECHO;
    private static final double FIM_CHEFES = INICIO_CREDITOS + PARADA_LETREIRO / VELOCIDADE + 4.0;
    private static final double INICIO_LENDAS = FIM_CHEFES + TRANSICAO;
    private static final double INICIO_ROLO_LENDAS = INICIO_LENDAS + 5.5;
    private static final double OZZY_NO_CENTRO = INICIO_ROLO_LENDAS;
    private static final double FIM_LENDAS = INICIO_LENDAS + 8.0;
    private static final double INICIO_SILHUETA = FIM_LENDAS + TRANSICAO + 1.0;
    private static final double CORTE_CABECA = INICIO_SILHUETA + 5.5;
    private static final double[][] RELAMPAGOS = {{CORTE_CABECA + 1.7, CORTE_CABECA + 1.86}, {CORTE_CABECA + 2.22, CORTE_CABECA + 2.44}};
    private static final double TEXTO_FINAL = CORTE_CABECA + 3.5;
    private static final double INICIO_FOTO = TEXTO_FINAL + 4.0;
    private static final double FOTO_COMPLETA = INICIO_FOTO + TRANSICAO;

    private double tempo;

    /** Prologo: a nave pousa no festival e o piloto entra na multidao (CenaPouso), depois os creditos. */
    private final CenaPouso pouso;
    /** Sem escolha de show (testes e previas): vai direto ao palco do Slayer. */
    public CenaFinal(){this(0);pouso.escolher(CamisaShow.SLAYER);}
    public CenaFinal(int modelo){this(modelo,0xFF);}
    /** reliquias: bit por banda; so os shows das reliquias conquistadas abrem (a Lula sempre). */
    public CenaFinal(int modelo,int reliquias){SomJogo.tocar(SomJogo.Efeito.VITORIA);pouso=new CenaPouso(modelo,reliquias);}
    boolean emPouso(){return !pouso.terminou();}
    void pularPouso(){pouso.pular();}
    void nivelNave(int n){pouso.nivelNave(n);}
    boolean escolhendoShow(){return pouso.escolhendo();}
    void moverShow(int d){pouso.mover(d);}
    void moverShowLinha(int d){pouso.moverLinha(d);}
    void escolherShow(int b){pouso.escolher(b);}
    int bandaShow(){return pouso.banda();}
    int cursorShow(){return pouso.cursor();}
    public void atualizar(double dt) {
        if (!pouso.terminou()) { pouso.atualizar(dt); return; }
        double antes = tempo;
        if (!Double.isNaN(dt) && !Double.isInfinite(dt)) tempo += Math.max(0, dt);
        if (passou(antes, INICIO_SILHUETA + .3)) SomJogo.tocar(SomJogo.Efeito.TROVAO);
        // a silhueta aparece entre raios: mais trovoes enquanto ela esta na tela
        if (passou(antes, INICIO_SILHUETA + 2.4) || passou(antes, INICIO_SILHUETA + 4.2)) SomJogo.tocar(SomJogo.Efeito.TROVAO);
        for (double[] r : RELAMPAGOS) if (passou(antes, r[0])) SomJogo.tocar(SomJogo.Efeito.TROVAO);
    }
    private boolean passou(double antes, double marco) { return antes < marco && tempo >= marco; }
    public double getTempo() { return tempo; }
    public boolean mostrandoMegazord() { return tempo >= INICIO_SILHUETA; }
    public boolean podeEncerrar() { return tempo >= FOTO_COMPLETA; }
    static double duracaoTeste() { return FOTO_COMPLETA; }

    public void desenhar(Graphics2D base) {
        if (!pouso.terminou()) { pouso.desenhar(base); return; }
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

    /** Banda no palco: o show escolhido abre; durante o rolo, sobe a banda de cada credito. */
    int bandaNoPalco() {
        double rolado = (tempo - INICIO_CREDITOS) * VELOCIDADE - (Config.ALTURA / 2 + OFF_CHEFES - 40);
        // show unico: a banda escolhida toca do comeco ao fim
        if (pouso.banda() != CenaPouso.FESTIVAL && pouso.banda() != CenaPouso.COMUM) return Math.max(0, pouso.banda());
        // zeramento comum e festival completo: abre a Lula e cada banda sobe junto com a legenda dela
        if (rolado < 0) return 1;
        int i = (int) (rolado / 190);
        if (i < 7) return i + 1;
        if (i == 7) return PalcoCreditos.TENACIOUS;
        return 1;
    }

    private void desenharPalco(Graphics2D g, int w, int h) {
        g.setColor(new Color(1, 1, 12));
        g.fillRect(0, 0, w, h);
        PalcoCreditos.fundo(g, w, h, false);
        PalcoCreditos.banda(g, w, h, bandaNoPalco(), tempo);
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
        g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 25));
        g.setColor(new Color(253, 214, 120));
        sombreado(g, "AS LENDAS SOBEM AO PALCO!", w, 40);
        // Letreiro subindo: titulo, cada banda (a banda dela toca no palco) e o fecho, que para no meio.
        double s = Math.min(PARADA_LETREIRO, (tempo - INICIO_CREDITOS) * VELOCIDADE);
        Graphics2D c = (Graphics2D) g.create(); c.clipRect(0, 62, w, h - 62);
        int y0 = (int) (h - s);
        if (y0 > -120 && y0 < h) {
            faixa(c, w, y0 - 10, 90);
            c.setFont(new Font(Font.MONOSPACED, Font.BOLD, 24)); c.setColor(new Color(255, 210, 94)); sombreado(c, "METAL SPACE - LEGENDS OF METAL", w, y0 + 26);
            c.setFont(new Font(Font.MONOSPACED, Font.BOLD, 18)); c.setColor(Color.WHITE); sombreado(c, "UMA HOMENAGEM AO HEAVY METAL", w, y0 + 60);
        }
        for (int k = 0; k < CHEFES.length; k++) {
            int y = (int) (h - s + OFF_CHEFES + k * PASSO_LETREIRO);
            if (y < -150 || y > h) continue;
            Object[] it = CHEFES[k];
            int lado = 104, x = 60;
            faixa(c, w, y - 12, lado + 24);
            retrato(c, (Integer) it[0], x, y, lado);
            int tx = x + lado + 30;
            c.setFont(new Font(Font.MONOSPACED, Font.BOLD, 26)); c.setColor(new Color(255, 210, 94)); c.drawString((String) it[1], tx, y + 26);
            c.setFont(new Font(Font.MONOSPACED, Font.BOLD, 17)); c.setColor(new Color(110, 222, 245)); c.drawString((String) it[2], tx, y + 52);
            c.setFont(new Font(Font.MONOSPACED, Font.BOLD, 15)); c.setColor(new Color(232, 232, 232));
            c.drawString((String) it[3], tx, y + 80); c.drawString((String) it[4], tx, y + 100);
        }
        int yf = (int) (h - s + OFF_FECHO);
        if (yf < h + 40) {
            faixa(c, w, yf - 34, ENCERRAMENTO_CREDITOS.length * 44 + 20);
            c.setFont(new Font(Font.MONOSPACED, Font.BOLD, 21));
            for (int k = 0; k < ENCERRAMENTO_CREDITOS.length; k++) { c.setColor(k == 0 ? new Color(255, 210, 94) : Color.WHITE); sombreado(c, ENCERRAMENTO_CREDITOS[k], w, yf + k * 44); }
        }
        c.dispose();
        escurecer(g, w, h, (tempo - (FIM_CHEFES - TRANSICAO)) / TRANSICAO);
    }

    /** Faixa escura atras de cada bloco do letreiro, para ler sobre o palco. */
    private static void faixa(Graphics2D g, int w, int y, int altura) {
        g.setColor(new Color(0, 0, 12, 170)); g.fillRoundRect(30, y, w - 60, altura, 14, 14);
        g.setColor(new Color(255, 210, 94, 70)); g.drawRoundRect(30, y, w - 60, altura, 14, 14);
    }
    /** Faixa escura no pe da tela, onde entra a legenda de cada banda. */
    private static void rodape(Graphics2D g, int w, int h, double f) {
        for (int k = 0; k < 40; k++) {
            g.setColor(new Color(0, 0, 10, (int) (f * Math.min(215, k * 9))));
            g.fillRect(0, 440 + k * 2, w, 2);
        }
        g.setColor(new Color(0, 0, 10, (int) (f * 215))); g.fillRect(0, 520, w, h - 520);
    }
    /** Texto centralizado com sombra, para ler por cima do palco. */
    private static void sombreado(Graphics2D g, String s, int w, int y) {
        Color c = g.getColor(); int x = (w - g.getFontMetrics().stringWidth(s)) / 2;
        g.setColor(new Color(0, 0, 0, Math.min(220, c.getAlpha()))); g.drawString(s, x + 2, y + 2);
        g.setColor(c); g.drawString(s, x, y);
    }

    private void desenharLendas(Graphics2D g, int w, int h) {
        // Dedicatoria: o palco em preto e branco e uma mensagem a todos que fizeram (e fazem) o metal.
        PalcoCreditos.fundo(g, w, h, true);
        g.setColor(new Color(0, 0, 0, 200));
        g.fillRect(0, 0, w, h);
        double t = tempo - INICIO_LENDAS;
        g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 30));
        g.setColor(new Color(230, 230, 230));
        centralizar(g, "DEDICADO A TODOS QUE FIZERAM", w, 250);
        centralizar(g, "E FAZEM O HEAVY METAL", w, 300);
        g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 20));
        g.setColor(new Color(255, 210, 94));
        centralizar(g, "AOS QUE PARTIRAM: SUA OBRA PERMANECE VIVA.", w, 370);
        escurecer(g, w, h, Math.max(1 - t / TRANSICAO, (tempo - (FIM_LENDAS - TRANSICAO)) / TRANSICAO));
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
        // personagens do jogo: a propria arte das lutas (Os Destemidos: o Bardo de violao)
        BufferedImage s = id == -5 ? DESTEMIDOS : null;
        boolean sprite = s == null && id > 0;
        if (sprite) s = Sprites16Bits.carregar(id, 2);
        if (s != null) {
            double e = Math.min(lado / (double) s.getWidth(), lado / (double) s.getHeight());
            int sw = (int) (s.getWidth() * e), sh = (int) (s.getHeight() * e);
            // Sprites do jogo encaram a esquerda como nas fases; retratos novos ja vem virados.
            if (sprite) Sprites16Bits.desenharVirado(g, s, id, 2, x + (lado - sw) / 2, y + (lado - sh) / 2, sw, sh);
            else {
                // Retratos reais: reducao suave para manter a nitidez.
                Graphics2D a = (Graphics2D) g.create();
                a.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
                a.drawImage(s, x + (lado - sw) / 2, y + (lado - sh) / 2, sw, sh, null);
                a.dispose();
            }
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
        if (LEMA != null) { fw = fw * 78 / 100; fh = fh * 78 / 100; } // foto menor para o letreiro caber grande
        int fx = (w - fw) / 2, fy = 30;
        HudMetal.painel(g, fx - 14, fy - 14, fw + 28, fh + 28);
        g.drawImage(FOTO_EQUIPE, fx, fy, fw, fh, null);
        int ly;
        if (LEMA != null) {
            // letreiro em pixel art: ocupa a largura livre embaixo da foto
            int lw = Math.min(w - 60, (h - (fy + fh + 30) - 52) * LEMA.getWidth() / LEMA.getHeight()), lh = lw * LEMA.getHeight() / LEMA.getWidth();
            int lx = (w - lw) / 2, lt = fy + fh + 22;
            Graphics2D a = (Graphics2D) g.create(); a.setRenderingHint(java.awt.RenderingHints.KEY_INTERPOLATION, java.awt.RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            a.drawImage(LEMA, lx, lt, lw, lh, null); a.dispose(); ly = lt + lh - 2;
        } else {
            g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 40));
            String lema = "HEAVY METAL NEVER DIES";
            ly = fy + fh + 62;
            FontMetrics fm = g.getFontMetrics();
            int lx = (w - fm.stringWidth(lema)) / 2;
            g.setColor(new Color(60, 8, 14));
            g.drawString(lema, lx + 3, ly + 3);
            g.setColor(new Color(238, 72, 83));
            g.drawString(lema, lx, ly);
        }
        g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 20));
        g.setColor(new Color(255, 213, 98));
        centralizar(g, "OBRIGADO POR JOGAR!", w, LEMA != null ? ly + 18 : ly + 40);
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
