package Jogo;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

/**
 * Viagem curta entre o planeta e a base aliada, que fica em orbita da estrela do sistema (e nao
 * dentro do planeta). Na ida a estacao cresce na tela ate a doca; na volta a nave sai da base e
 * o planeta do proximo integrante cresce.
 */
final class ViagemBase {
    static final double DURACAO = 2.6;
    private ViagemBase() { }

    /** t de 0 a DURACAO. ida = indo para a base; senao, saindo para o planeta. */
    static void desenhar(Graphics2D base, int setor, int planeta, double t, boolean ida) {
        Graphics2D g = (Graphics2D) base.create();
        int W = Config.LARGURA, H = Config.ALTURA;
        double p = Math.max(0, Math.min(1, t / DURACAO));
        g.setColor(new Color(2, 3, 10)); g.fillRect(0, 0, W, H);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        // estrelas correndo (a nave em velocidade de cruzeiro)
        java.util.Random r = new java.util.Random(setor * 7 + 1);
        for (int k = 0; k < 140; k++) {
            double sx = r.nextDouble() * W, sy = r.nextDouble() * H, v = 200 + r.nextDouble() * 500;
            int x = (int) ((sx - t * v) % W + W) % W, c = 120 + r.nextInt(130);
            g.setColor(new Color(c, c, Math.min(255, c + 30))); g.fillRect(x, (int) sy, 2 + (int) (v / 300), 1);
        }
        // a estrela do sistema ao fundo, sempre la: a base orbita ela
        BufferedImage estrela = MapaEstelar.estrela(setor);
        if (estrela != null) { int s = 220; g.drawImage(estrela, W - s - 40, 40, s, s, null); }
        if (ida) {
            BufferedImage est = EstacaoAliada.arte();
            double z = .25 + p * p * 1.6; // cresce ate encher a tela
            if (est != null) { int w = (int) (est.getWidth() * z), h = (int) (est.getHeight() * z); g.drawImage(est, W / 2 - w / 2, H / 2 - h / 2, w, h, null); }
            titulo(g, "BASE ALIADA DO SISTEMA " + MapaEstelar.NOMES[Math.max(0, Math.min(7, setor))], "DESCANSO, REPARO, MERCADO E MISSOES", W);
        } else {
            BufferedImage pl = MapaEstelar.planeta(setor, Math.max(0, Math.min(Turne.planetas(setor) - 1, planeta)));
            double z = .2 + p * p * 1.4;
            if (pl != null) { int s = (int) (300 * z); g.drawImage(pl, W / 2 - s / 2, H / 2 - s / 2, s, s, null); }
            boolean chefe = planeta >= Turne.integrantes(setor);
            titulo(g, "PLANETA " + (planeta + 1) + "/" + Turne.planetas(setor), chefe ? "O CHEFE DO SISTEMA ESPERA" : Turne.integrante(setor, planeta) + " ESTA TOCANDO LA", W);
        }
        // fade nas pontas
        double f = Math.max(Math.max(0, 1 - t / .35), Math.max(0, (t - (DURACAO - .45)) / .45));
        if (f > 0) { g.setComposite(AlphaComposite.SrcOver.derive((float) Math.min(1, f))); g.setColor(Color.BLACK); g.fillRect(0, 0, W, H); }
        g.dispose();
    }

    private static void titulo(Graphics2D g, String a, String b, int W) {
        g.setColor(new Color(0, 0, 0, 150)); g.fillRect(0, 480, W, 78);
        g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 24)); g.setColor(new Color(255, 206, 98));
        g.drawString(a, (W - g.getFontMetrics().stringWidth(a)) / 2, 512);
        g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 15)); g.setColor(new Color(190, 210, 230));
        g.drawString(b, (W - g.getFontMetrics().stringWidth(b)) / 2, 540);
    }
}
