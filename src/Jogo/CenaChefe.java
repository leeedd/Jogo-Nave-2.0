package Jogo;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

/**
 * Cutscene no meio da batalha: o combate congela e os paineis da prancha aparecem um a um,
 * com fusao e um leve zoom. ENTER ou ESPACO pulam. A van do Hellraiser e desenhada em codigo.
 */
final class CenaChefe {
    static final String TRANSFORMACAO = "ozzy_transformacao", DRAGAO = "ozzy_vira_dragao", VAN = "hellraiser_van";
    private static final double PAINEL = 1.55, FUSAO = .4;
    private static final java.util.Map<String, BufferedImage> CACHE = new java.util.HashMap<>();

    private final String nome;
    private final BufferedImage arte;
    private final int paineis;
    private final String[] legendas;
    private final double duracao;
    private double tempo;

    private CenaChefe(String nome, int paineis, String[] legendas, double duracao) {
        this.nome = nome; this.arte = ler(nome + ".png"); this.paineis = paineis; this.legendas = legendas; this.duracao = duracao;
    }
    static CenaChefe criar(String nome) {
        switch (nome) {
            case TRANSFORMACAO: return new CenaChefe(nome, 6, new String[]{"O MORCEGO CAI...", "", "", "A NOITE SE PARTE", "", "O PRINCIPE DAS TREVAS!"}, 6 * PAINEL + .6);
            case DRAGAO: return new CenaChefe(nome, 6, new String[]{"AINDA NAO ACABOU...", "", "", "", "", "OZZY, O DRAGAO!"}, 6 * PAINEL + .6);
            case VAN: return new CenaChefe(nome, 4, null, 6.2);
            default: return null;
        }
    }
    String nome() { return nome; }
    void atualizar(double dt) { tempo = Math.min(duracao, tempo + Math.max(0, dt)); }
    void pular() { if (tempo > .4) tempo = duracao; }
    boolean terminou() { return tempo >= duracao; }

    void desenhar(Graphics2D tela) {
        Graphics2D g = (Graphics2D) tela.create();
        int W = Config.LARGURA, H = Config.ALTURA;
        float entra = (float) Math.min(1, tempo / .3), sai = (float) Math.min(1, (duracao - tempo) / .3);
        g.setComposite(AlphaComposite.SrcOver.derive(Math.max(0, Math.min(entra, sai))));
        g.setColor(Color.BLACK); g.fillRect(0, 0, W, H);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        if (VAN.equals(nome)) van(g, W, H);
        else if (arte != null) {
            int atual = Math.min(paineis - 1, (int) (Math.max(0, tempo - .3) / PAINEL));
            double dentro = Math.max(0, tempo - .3) - atual * PAINEL;
            if (atual > 0 && dentro < FUSAO) painel(g, atual - 1, 1, PAINEL + dentro, W, H);
            painel(g, atual, (float) Math.min(1, dentro / FUSAO), dentro, W, H);
            String txt = legendas != null && atual < legendas.length ? legendas[atual] : "";
            if (!txt.isEmpty()) legenda(g, txt, H - 34, atual == paineis - 1 ? 26 : 18, atual == paineis - 1 ? new Color(220, 120, 255) : Color.WHITE);
        }
        g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 11)); g.setColor(new Color(150, 150, 170));
        g.drawString("ENTER: PULAR", W - 100, H - 10);
        g.dispose();
    }

    private void painel(Graphics2D g, int k, float alfa, double t, int W, int H) {
        int cw = arte.getWidth() / paineis, ch = arte.getHeight();
        double zoom = 1 + .05 * Math.min(1, t / PAINEL);
        int h = (int) ((H - 70) * zoom), w = cw * h / ch;
        Graphics2D q = (Graphics2D) g.create();
        q.setComposite(AlphaComposite.SrcOver.derive(alfa * ((AlphaComposite) g.getComposite()).getAlpha()));
        q.drawImage(arte, (W - w) / 2, (H - 70 - h) / 2 + 10, (W - w) / 2 + w, (H - 70 - h) / 2 + 10 + h, k * cw, 0, k * cw + cw, ch, null);
        q.dispose();
    }

    /** Ozzy e Lemmy cruzando o espaco na van: estrelas correndo, van chega, encara e acelera. */
    private void van(Graphics2D g, int W, int H) {
        java.util.Random r = new java.util.Random(7);
        for (int i = 0; i < 140; i++) {
            int vel = 120 + r.nextInt(500), x = Math.floorMod((int) (r.nextInt(W) - tempo * vel), W), y = r.nextInt(H);
            g.setColor(new Color(200, 210, 255, 60 + r.nextInt(150))); g.fillRect(x, y, vel > 450 ? 14 : vel > 300 ? 6 : 2, 2);
        }
        if (arte != null) {
            int fw = arte.getWidth() / 4, fh = arte.getHeight(), h = 300, w = fw * h / fh;
            double chegada = Math.min(1, tempo / 1.8), parte = Math.max(0, tempo - 4.4) / 1.4;
            double x = W + 40 - (W + 40 - (W - w) / 2.0) * (1 - Math.pow(1 - chegada, 3)) - parte * parte * (W + w);
            int y = H / 2 - h / 2 + 30 + (int) (Math.sin(tempo * 9) * 4);
            int q = tempo > 4.2 ? 1 : (int) (tempo * 8) % 2 == 0 ? 0 : 1;
            g.drawImage(arte, (int) x, y, (int) x + w, y + h, q * fw, 0, q * fw + fw, fh, null);
        }
        if (tempo > .8 && tempo < 4.4) legenda(g, tempo < 2.6 ? "OZZY E LEMMY PEGARAM A ESTRADA..." : "E VIERAM BUSCAR VOCE.", 120, 18, Color.WHITE);
        if (tempo > 4.4) {
            float a = (float) Math.min(1, (tempo - 4.4) / .4);
            Graphics2D t = (Graphics2D) g.create(); t.setComposite(AlphaComposite.SrcOver.derive(a));
            legenda(t, "HELLRAISER", H / 2 - 40, 54, new Color(255, 120, 40)); t.dispose();
        }
    }

    private static void legenda(Graphics2D g, String s, int y, int tam, Color c) {
        g.setFont(new Font(Font.MONOSPACED, Font.BOLD, tam));
        int w = g.getFontMetrics().stringWidth(s), x = (Config.LARGURA - w) / 2;
        g.setColor(new Color(0, 0, 0, 190)); g.fillRect(x - 12, y - tam, w + 24, tam + 12);
        g.setColor(Color.BLACK); g.drawString(s, x + 2, y + 2);
        g.setColor(c); g.drawString(s, x, y);
    }

    private static BufferedImage ler(String n) {
        return CACHE.computeIfAbsent(n, k -> {
            java.net.URL u = CenaChefe.class.getResource("/ImagemIcon/" + k);
            if (u == null) return null;
            try { return javax.imageio.ImageIO.read(u); } catch (java.io.IOException e) { return null; }
        });
    }
}
