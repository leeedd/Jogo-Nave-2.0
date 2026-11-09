package Jogo;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;
import javax.swing.Timer;

/**
 * Abertura antes do menu: a placa "UM JOGO ORIGINAL 2019" pisca na tela e depois o logo do
 * Metal Space acende do escuro (brilho vermelho, letras clareando e um clarao com a capa).
 * ENTER, ESPACO, ESC ou um clique pulam.
 */
final class AberturaJogo extends JPanel {
    static final double PLACA = 3.6, LOGO = 3.0, SEGURA = 1.2, DURACAO = PLACA + LOGO + SEGURA;
    private static final BufferedImage PLACA_IMG = ler("placa_jogo_original_2019.png"), LOGO_IMG = ler("abertura_logo.png"),
            LETRAS = ler("abertura_letras.png"), BRILHO = ler("abertura_brilho.png");
    private final Runnable aoTerminar;
    private final Timer timer;
    private double tempo;
    private boolean terminou, estourou;
    private long ultimo = System.nanoTime();

    AberturaJogo(Runnable aoTerminar) {
        this.aoTerminar = aoTerminar;
        setPreferredSize(new Dimension(Config.LARGURA, Config.ALTURA));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) {
                int k = e.getKeyCode();
                if (k == KeyEvent.VK_ENTER || k == KeyEvent.VK_SPACE || k == KeyEvent.VK_ESCAPE) encerrar();
            }
        });
        addMouseListener(new MouseAdapter() { @Override public void mousePressed(MouseEvent e) { encerrar(); } });
        timer = new Timer(16, e -> { long agora = System.nanoTime(); atualizar(Math.min(.1, (agora - ultimo) / 1e9)); ultimo = agora; repaint(); });
    }

    void comecar() { ultimo = System.nanoTime(); timer.start(); requestFocusInWindow(); }

    void atualizar(double dt) {
        tempo += dt;
        if (!estourou && tempo >= PLACA + LOGO * .72) { estourou = true; SomJogo.tocar(SomJogo.Efeito.EXPLOSAO_GRANDE); }
        if (tempo >= DURACAO) encerrar();
    }
    double getTempoTeste() { return tempo; }
    boolean terminouTeste() { return terminou; }

    private void encerrar() {
        if (terminou) return;
        terminou = true; timer.stop();
        if (aoTerminar != null) aoTerminar.run();
    }

    @Override protected void paintComponent(Graphics tela) {
        super.paintComponent(tela);
        Graphics2D g = (Graphics2D) tela.create();
        int W = Config.LARGURA, H = Config.ALTURA;
        g.setColor(Color.BLACK); g.fillRect(0, 0, W, H);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        if (tempo < PLACA) placa(g, W, H);
        else logo(g, W, H, Math.min(1, (tempo - PLACA) / LOGO));
        g.dispose();
    }

    private void placa(Graphics2D g, int W, int H) {
        double p = tempo - .25;
        // entra piscando, fica firme e some no fim
        boolean vis = p > 0 && ((int) (p * 20) % 3 != 0 || p > .5) && tempo < PLACA - .25;
        if (!vis || PLACA_IMG == null) return;
        double s = 1.08 - .08 * Math.min(1, p / .5);
        int w = (int) (760 * s), h = w * PLACA_IMG.getHeight() / PLACA_IMG.getWidth();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        g.drawImage(PLACA_IMG, (W - w) / 2, (H - h) / 2, w, h, null);
    }

    private void logo(Graphics2D g, int W, int H, double p) {
        if (LOGO_IMG == null) return;
        int w = LOGO_IMG.getWidth(), h = LOGO_IMG.getHeight(), x = (W - w) / 2, y = (H - h) / 2 - 10;
        double luz = Math.min(1, p / .72); luz = luz * luz * (3 - 2 * luz);
        double fundo = Math.max(0, Math.min(1, (p - .72) / .2));
        if (BRILHO != null) { alfa(g, (.4 + .6 * luz) * (1 - fundo)); g.drawImage(BRILHO, x, y, null); }
        if (LETRAS != null) { alfa(g, .12 + .88 * luz); g.drawImage(LETRAS, x, y, null); }
        if (fundo > 0) { alfa(g, fundo); g.drawImage(LOGO_IMG, x, y, null); }
        alfa(g, 1);
        if (p > .72 && p < .8) { g.setColor(new Color(255, 255, 255, 150)); g.fillRect(0, 0, W, H); }
    }
    private static void alfa(Graphics2D g, double a) { g.setComposite(AlphaComposite.SrcOver.derive((float) Math.max(0, Math.min(1, a)))); }

    private static BufferedImage ler(String n) {
        java.net.URL u = AberturaJogo.class.getResource("/ImagemIcon/" + n);
        if (u == null) return null;
        try { return javax.imageio.ImageIO.read(u); } catch (java.io.IOException e) { return null; }
    }
}
