package Jogo;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Random;

/** Laser de Convergencia: gelo e fogo fundidos num feixe que varre a tela e vaporiza quem estiver nela. */
final class LaserConvergencia {
    static final double DURACAO = 1.6;
    private final double y; private double tempo; private final int vaporizados;
    private final Random acaso = new Random(9);
    LaserConvergencia(double y, int vaporizados) { this.y = y; this.vaporizados = vaporizados; }
    void atualizar(double dt) { tempo += dt; }
    boolean ativo() { return tempo < DURACAO; }

    void desenhar(Graphics2D original, int naveX, int naveY) {
        Graphics2D g = (Graphics2D) original.create();
        double t = tempo / DURACAO;
        // carga (0-.15), disparo (.15-.75), apagando (.75-1)
        double forca = t < .15 ? t / .15 : t < .75 ? 1 : 1 - (t - .75) / .25;
        int ox = naveX + 46, cy = naveY + 20;
        if (t >= .12) {
            // clarao de tela inteira no inicio do disparo
            if (t < .3) { g.setComposite(AlphaComposite.SrcOver.derive((float) (.55 * (1 - (t - .12) / .18)))); g.setColor(Color.WHITE); g.fillRect(0, 0, Config.LARGURA, Config.ALTURA); }
            int h = (int) (46 + 70 * forca);
            for (int camada = 0; camada < 5; camada++) {
                float a = (float) (forca * (camada == 4 ? 1 : .35 + camada * .12));
                g.setComposite(AlphaComposite.SrcOver.derive(Math.min(1f, a)));
                int hh = h - camada * h / 6;
                // metade de cima gelo, metade de baixo fogo, nucleo branco
                Color cima = camada == 4 ? Color.WHITE : new Color(60 + camada * 40, 170 + camada * 18, 255);
                Color baixo = camada == 4 ? Color.WHITE : new Color(255, 110 + camada * 30, 30 + camada * 20);
                g.setColor(cima); g.fillRect(ox, cy - hh / 2, Config.LARGURA - ox, hh / 2);
                g.setColor(baixo); g.fillRect(ox, cy, Config.LARGURA - ox, hh / 2);
            }
            // espiral de energia correndo pelo feixe
            g.setComposite(AlphaComposite.SrcOver.derive((float) forca));
            for (int k = 0; k < 60; k++) {
                int x = ox + (int) ((k * 17 + tempo * 1400) % (Config.LARGURA - ox));
                int dy = (int) (Math.sin(x * .05 + tempo * 20) * h * .45);
                g.setColor(k % 2 == 0 ? new Color(180, 240, 255) : new Color(255, 200, 90));
                g.fillRect(x, cy + dy - 3, 6, 6);
            }
            // particulas de vaporizacao pela tela
            for (int k = 0; k < 90; k++) {
                int x = acaso.nextInt(Config.LARGURA), yy = Config.HUD + acaso.nextInt(Config.ALTURA - Config.HUD);
                int sobe = (int) (tempo * 120 * (1 + k % 3));
                g.setColor(k % 3 == 0 ? new Color(255, 255, 255, 180) : k % 3 == 1 ? new Color(120, 220, 255, 160) : new Color(255, 160, 60, 160));
                g.fillRect(x, yy - sobe, 3, 3);
            }
        }
        // esfera de carga na ponta da nave
        int r = (int) (10 + 22 * forca + Math.sin(tempo * 40) * 3);
        g.setComposite(AlphaComposite.SrcOver.derive((float) Math.max(.2, forca)));
        g.setColor(new Color(120, 220, 255)); g.fillOval(ox - r, cy - r, r * 2, r);
        g.setColor(new Color(255, 150, 50)); g.fillOval(ox - r, cy, r * 2, r);
        g.setColor(Color.WHITE); g.fillOval(ox - r / 2, cy - r / 2, r, r);
        if (vaporizados > 0 && t > .2) {
            g.setComposite(AlphaComposite.SrcOver.derive((float) Math.min(1, (1 - t) * 3)));
            g.setFont(new java.awt.Font(java.awt.Font.MONOSPACED, java.awt.Font.BOLD, 30));
            String s = "VAPORIZADOS x" + vaporizados;
            g.setColor(new Color(60, 0, 0)); g.drawString(s, 482 - g.getFontMetrics().stringWidth(s) / 2, 152);
            g.setColor(new Color(255, 230, 120)); g.drawString(s, 480 - g.getFontMetrics().stringWidth(s) / 2, 150);
        }
        g.dispose();
    }
}
