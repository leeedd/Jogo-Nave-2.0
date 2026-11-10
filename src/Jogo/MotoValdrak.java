package Jogo;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.List;

/**
 * Ato I do Valdrak: uma moto espacial que e um robo. Acelera pela tela cuspindo fogo pelos canos,
 * empina e cruza a tela numa investida deixando a pista em chamas. Derrotada, se transforma (quadro 4)
 * e sai o Valdrak de jaqueta. Arte: chefe_2_moto.png (acelerando, empinando, atirando, transformando).
 */
final class MotoValdrak {
    private static final BufferedImage ARTE = ler("chefe_2_moto.png");
    static boolean temArte() { return ARTE != null; }
    private static final int W = Config.LARGURA, H = Config.ALTURA, TOPO = Config.HUD;
    private final int h = 170, w;
    private double x = W + 40, y, alvoY, tempo, entrada, recarga = 1.6, gesto, aviso, flash;
    private boolean investindo, voltando, acertou;
    private int golpes, quadroGesto;
    private double percorrido, ultimoRastro;

    MotoValdrak() {
        w = ARTE != null ? ARTE.getWidth() / 4 * h / ARTE.getHeight() : 360;
        y = alvoY = TOPO + (H - TOPO - h) / 2.0;
    }
    double entrada() { return entrada; }
    private double base() { return W - w - 20; }
    private double faixa(double naveY) { return Math.max(TOPO + 10, Math.min(H - h - 10, naveY + 18 - h * .55)); }

    void atualizar(double dt, int naveX, int naveY, Dificuldade dif, List<TiroChefe> tiros, boolean furia) {
        tempo += dt; flash = Math.max(0, flash - dt); gesto = Math.max(0, gesto - dt);
        if (investindo) {
            double vel = 1150; x -= vel * dt; percorrido += vel * dt;
            if (percorrido - ultimoRastro > 36) { ultimoRastro = percorrido;
                tiros.add(new TiroChefe(x + w * .75, y + h * .78, 0, 0, false, TiroChefe.RASTRO, TiroChefe.Movimento.RETO)); }
            if (x < -w - 20) { investindo = false; voltando = true; x = W + 40; y = alvoY = faixa(naveY); }
            return;
        }
        if (aviso > 0) { aviso = Math.max(0, aviso - dt); x += Math.sin(tempo * 60) * 1.5; if (aviso == 0) { investindo = true; acertou = false; percorrido = ultimoRastro = 0; SomJogo.tocar(SomJogo.Efeito.CHEFE_ENTRADA); } return; }
        if (x > base()) x = Math.max(base(), x - (voltando ? 700 : 480) * dt); else voltando = false;
        if (!voltando) entrada = Math.min(1, entrada + dt / 1.3);
        alvoY += (faixa(naveY) - alvoY) * Math.min(1, dt * .9);
        y += (alvoY + Math.sin(tempo * 2.4) * 22 - y) * Math.min(1, dt * 2.5);
        if (entrada < 1 || voltando) return;
        recarga -= dt * dif.cadenciaInimigos * (furia ? 1.3 : 1);
        if (recarga > 0 || tiros.size() > 45) return;
        golpes++;
        if (golpes % 3 == 0) { aviso = .8; quadroGesto = 1; y = alvoY = faixa(naveY); }   // empina e investe
        else {                                                                                 // fogo pela frente
            quadroGesto = 2; gesto = .45;
            double ox = x + w * .08, oy = y + h * .5, a = Math.atan2(naveY + 18 - oy, naveX + 22 - ox);
            int n = furia ? 5 : 3;
            for (int i = 0; i < n; i++) { double ang = a + (i - (n - 1) / 2.0) * .14;
                tiros.add(new TiroChefe(ox, oy, Math.cos(ang) * 380, Math.sin(ang) * 380, i == n / 2, TiroChefe.ENERGIA, TiroChefe.Movimento.RETO).comCor(new Color(255, 150, 40))); }
        }
        recarga = furia ? 1.25 : 1.6;
    }
    boolean investindo() { return investindo; }
    /** Encosto na investida: um dano por passada. */
    boolean consumirColisao(Rectangle nave) { if (!investindo || acertou || !bounds().intersects(nave)) return false; acertou = true; return true; }
    void ferido() { flash = .16; }
    Rectangle bounds() { return new Rectangle((int) x + w / 10, (int) y + h / 4, w * 8 / 10, h * 6 / 10); }
    int largura() { return w; }
    int altura() { return h; }
    double getX() { return x; }
    double getY() { return y; }

    void desenhar(Graphics2D tela, float alfa, int quadroForcado) {
        if (ARTE == null) return;
        Graphics2D g = (Graphics2D) tela.create();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        if (alfa < 1) g.setComposite(AlphaComposite.SrcOver.derive(Math.max(0, alfa)));
        int q = quadroForcado >= 0 ? quadroForcado : aviso > 0 || investindo ? 1 : gesto > 0 ? quadroGesto : 0;
        int fw = ARTE.getWidth() / 4, fh = ARTE.getHeight(), px = (int) x, py = (int) y + (int) (Math.sin(tempo * 30) * (investindo ? 2 : 0));
        g.drawImage(ARTE, px, py, px + w, py + h, q * fw, 0, q * fw + fw, fh, null);
        if (investindo) for (int k = 0; k < 8; k++) { g.setColor(new Color(255, 150, 40, 150 - k * 16)); g.fillRect(px + w + k * 22, py + h / 2 + (k % 3) * 18 - 18, 40, 3); }
        if (aviso > 0) { g.setColor(new Color(255, 60, 30, (int) (90 + 80 * Math.sin(tempo * 30)))); g.fillRect(0, py + h / 3, W, h / 3); }
        if (flash > 0) { g.setColor(new Color(255, 230, 180, (int) (flash / .16 * 170))); for (int k = 0; k < 6; k++) { double a = k * Math.PI / 3; int r = 15 + (int) ((.16 - flash) * 180); g.fillRect(px + w / 2 + (int) (Math.cos(a) * r), py + h / 2 + (int) (Math.sin(a) * r), 4, 4); } }
        g.dispose();
    }
    private static BufferedImage ler(String n) {
        java.net.URL u = MotoValdrak.class.getResource("/ImagemIcon/" + n);
        if (u == null) return null;
        try { return javax.imageio.ImageIO.read(u); } catch (java.io.IOException e) { return null; }
    }
}
