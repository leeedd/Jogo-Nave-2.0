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
    static final double DURACAO = 17;
    private static final Image PALCO = Assets.imagem("show_final_098.png");
    /** Arte do piloto (piloto_099.png, grade 4x2): andando (0-3), tirando o macacao (4-5), chifrinho (6), bracos para cima (7). */
    private static final Image PILOTO = Assets.imagem("piloto_099.png");
    private final int modelo;
    private double t;

    CenaPouso(int modelo) { this.modelo = modelo; }
    void atualizar(double dt) { t = Math.min(DURACAO, t + Math.max(0, dt)); }
    boolean terminou() { return t >= DURACAO; }
    void pular() { t = DURACAO; }

    private static final int CHAO = 470;
    private static double suave(double x) { x = Math.max(0, Math.min(1, x)); return x * x * (3 - 2 * x); }

    void desenhar(Graphics2D base) {
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
        double p = suave(t / 5.0);
        int nx = (int) (-240 + 360 * p), ny = (int) (-120 + (CHAO - 110 + 120) * p);
        Graphics2D n = (Graphics2D) g.create(); n.translate(nx, ny); n.scale(3.2, 3.2);
        ArteNaves.desenharJogador(n, 6, modelo, 0, 0, t);
        n.dispose();
        if (t > 3.6 && t < 6.5) { // poeira do pouso
            double q = (t - 3.6) / 2.9; g.setColor(new Color(150, 130, 120, (int) (150 * (1 - q))));
            for (int k = 0; k < 10; k++) { int r = (int) (10 + q * 40); g.fillOval(nx + 20 + k * 14 - r / 2, CHAO - r / 2 - 4, r, r / 2); }
        }

        // 2) o piloto: desce (6 s), anda (7-9 s), tira o macacao (9-11 s), vai para a multidao (11-15 s)
        if (t > 6) {
            double andar = t < 7 ? 0 : t < 9 ? (t - 7) / 2 : 1;
            double sai = t < 11.5 ? 0 : (t - 11.5) / 3.5;
            int px = nx + 230 + (int) (andar * 120) + (int) (suave(sai) * 300), py = CHAO + (int) (suave(sai) * 60);
            boolean semMacacao = t > 10.2;
            double tirando = t > 9 && t < 10.2 ? (t - 9) / 1.2 : 0;
            if (semMacacao && PILOTO == null) macacaoNoChao(g, nx + 230 + 120 - 10, CHAO);
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
