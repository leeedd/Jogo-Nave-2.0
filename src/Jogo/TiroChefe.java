package Jogo;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

/** Disparo em leque com animacao de pulso e rastro de energia. */
public final class TiroChefe {
    public enum Movimento { RETO, ONDA, ACELERADO, PULSANTE }
    private final Movimento movimento;
    private double x, y, tempo;
    private final double vx, vy;
    private final boolean pesado;
    private final int estilo;
    private boolean visivel = true;

    public TiroChefe(double x, double y, double vx, double vy, boolean pesado) {
        this(x, y, vx, vy, pesado, 0);
    }
    public TiroChefe(double x, double y, double vx, double vy, boolean pesado, int estilo) {
        this(x, y, vx, vy, pesado, estilo, Movimento.RETO);
    }
    public TiroChefe(double x, double y, double vx, double vy, boolean pesado,
                     int estilo, Movimento movimento) {
        this.x = x; this.y = y; this.vx = vx; this.vy = vy; this.pesado = pesado;
        this.estilo = estilo; this.movimento = movimento;
    }
    public void mexer(double dt) {
        double velocidade = movimento == Movimento.ACELERADO ? 1 + Math.min(1.1, tempo * .47) : 1;
        double perpx = -vy / Math.max(1, Math.hypot(vx, vy));
        double perpy = vx / Math.max(1, Math.hypot(vx, vy));
        double curva = movimento == Movimento.ONDA ? Math.sin(tempo * 8) * 110 * dt
                : movimento == Movimento.PULSANTE ? Math.sin(tempo * 14) * 70 * dt : 0;
        x += vx * dt * velocidade + perpx * curva;
        y += vy * dt * velocidade + perpy * curva;
        tempo += dt;
        if (x < -40 || x > Config.LARGURA + 40 || y < Config.HUD - 40 || y > Config.ALTURA + 40)
            visivel = false;
    }
    public void desenhar(Graphics2D g) {
        int px = (int) x, py = (int) y;
        // Cada artista tem um projetil com forma propria, combinando com o golpe da prancha.
        switch (estilo) {
            case 1: desenharMorcego(g, px, py); return;
            case 2: desenharFogo(g, px, py); return;
            case 3: desenharRaioCruzado(g, px, py); return;
            case 4: desenharOndaSom(g, px, py); return;
            case 5: desenharEsferaEletrica(g, px, py); return;
            case 6: desenharEstilhaco(g, px, py); return;
            case 7: desenharCristal(g, px, py); return;
            default: break;
        }
        int r = pesado ? 12 : 7;
        Color c = estilo == 8 ? new Color(255,116,12) : estilo == 1 ? new Color(199, 76, 232) : estilo == 2
                ? new Color(255, 156, 44) : estilo == 3 ? new Color(74, 228, 223)
                : estilo == 4 ? new Color(244, 128, 52) : estilo == 5 ? new Color(81, 166, 255)
                : estilo == 6 ? new Color(244, 64, 75) : estilo == 7 ? new Color(125, 239, 110)
                : pesado ? new Color(255, 91, 61) : new Color(190, 94, 255);
        double direcao = Math.atan2(vy, vx);
        for (int i = 1; i <= 3; i++) {
            int tx = px - (int) (Math.cos(direcao) * i * 9);
            int ty = py - (int) (Math.sin(direcao) * i * 9);
            int alpha = 35 + (3 - i) * 30;
            g.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), alpha));
            g.fillOval(tx - r + i, ty - r + i, 2 * (r - i), 2 * (r - i));
        }
        int pulso = (int) (3 * Math.sin(tempo * 17));
        g.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), 55));
        g.fillOval(px - r - 7 - pulso, py - r - 7 - pulso, 2 * (r + 7 + pulso), 2 * (r + 7 + pulso));
        g.setColor(c); g.fillOval(px - r, py - r, 2 * r, 2 * r);
        if (estilo == 8) {
            g.setColor(new Color(255,220,35));g.fillOval(px-r+2,py-r+2,r*2-4,r*2-4);
            g.setColor(new Color(255,245,172));g.drawOval(px-r,py-r,r*2,r*2);
        } else if (estilo == 4) {
            g.setColor(new Color(255, 215, 118, 185));
            g.drawOval(px - r - 5, py - r - 5, 2*r + 10, 2*r + 10);
            g.drawOval(px - r - 11, py - r - 11, 2*r + 22, 2*r + 22);
        } else if (estilo == 5) {
            g.setColor(new Color(177, 232, 255));
            g.drawPolyline(new int[]{px-14,px-5,px-12,px+7,px+14},
                    new int[]{py-10,py-2,py+5,py+1,py+13},5);
        } else if (estilo == 6) {
            g.setColor(new Color(255, 215, 82));
            g.fillPolygon(new int[]{px,px-5,px+5},new int[]{py+12,py-7,py-7},3);
        } else if (estilo == 7) {
            g.setColor(new Color(196, 255, 221));
            g.fillPolygon(new int[]{px,px+r+6,px,px-r-6},
                    new int[]{py-r-8,py,py+r+8,py},4);
        } else if (estilo == 1) {
            g.setColor(new Color(224, 140, 255));
            g.drawArc(px - r * 2, py - r * 2, 4 * r, 4 * r, -75, 150);
        } else if (estilo == 2) {
            g.setColor(new Color(255, 222, 106));
            g.drawLine(px - r - 5, py, px + 1, py - r);
        } else if (estilo == 3) {
            g.setColor(new Color(170, 252, 253));
            g.drawArc(px - r * 2, py - r * 2, 4 * r, 4 * r, -75, 150);
        }
        g.setColor(Color.WHITE); g.fillOval(px - 3, py - 3, 6, 6);
    }
    /** Morcegos do Ozzy em pixel art: asas batendo, olhos voltados para onde voam. */
    private static final String[][] MORCEGO = {
        { // asas no alto
            "M...............M", "WM.............MW", "WWM...H...H...MWW", ".WWM..HBBBH..MWW.",
            ".WWWMBEBEBBBMWWW.", "..WWWWBBBBBWWWW..", "...WWWWBBBWWWW...", "....W.W.B.W.W...."
        }, { // asas abertas
            ".................", ".................", "......H...H......", "MMM...HBBBH...MMM",
            "WWWWMBEBEBBBMWWWW", ".WWWWWBBBBBWWWWW.", "..W.WWWBBBWWW.W..", "........B........"
        }, { // asas embaixo
            ".................", ".................", "......H...H......", "......HBBBH......",
            "...MMBEBEBBBMM...", "..MWWWBBBBBWWWM..", ".MWWWWWBBBWWWWWM.", "MWW.W.W.B.W.W.WWM"
        }
    };
    private static final Color MORCEGO_CORPO = new Color(44, 16, 58), MORCEGO_ASA = new Color(104, 42, 136),
            MORCEGO_BORDA = new Color(192, 110, 238), MORCEGO_OLHO = new Color(255, 58, 58),
            MORCEGO_ORELHA = new Color(78, 28, 100);

    private void desenharMorcego(Graphics2D g, int px, int py) {
        int p = pesado ? 3 : 2;
        g.setColor(new Color(150, 60, 210, 42));
        g.fillOval(px - 10 * p, py - 6 * p, 20 * p, 12 * p);
        int[] ciclo = {0, 1, 2, 1};
        String[] quadro = MORCEGO[ciclo[(int) (tempo * 14) % 4]];
        boolean paraDireita = vx > 0;
        int largura = quadro[0].length(), ox = px - largura * p / 2, oy = py - 4 * p;
        for (int linha = 0; linha < quadro.length; linha++) {
            for (int coluna = 0; coluna < largura; coluna++) {
                char c = quadro[linha].charAt(paraDireita ? largura - 1 - coluna : coluna);
                if (c == '.') continue;
                g.setColor(c == 'B' ? MORCEGO_CORPO : c == 'W' ? MORCEGO_ASA : c == 'M' ? MORCEGO_BORDA
                        : c == 'E' ? MORCEGO_OLHO : MORCEGO_ORELHA);
                g.fillRect(ox + coluna * p, oy + linha * p, p, p);
            }
        }
    }
    private double direcao() { return Math.atan2(vy, vx); }

    /** Rob: bola de fogo com rastro de chamas tremulando para tras. */
    private void desenharFogo(Graphics2D tela, int px, int py) {
        Graphics2D g = (Graphics2D) tela.create();
        g.rotate(direcao(), px, py);
        int r = pesado ? 11 : 7;
        for (int i = 4; i >= 1; i--) {
            int comp = r * 2 + i * 6 + (int) (Math.sin(tempo * 31 + i) * 3);
            g.setColor(i > 2 ? new Color(200, 40, 20, 120) : new Color(255, 120, 30, 170));
            g.fillPolygon(new int[]{px - comp, px, px}, new int[]{py + (int) (Math.sin(tempo * 23 + i * 2) * 3), py - r + i, py + r - i}, 3);
        }
        g.setColor(new Color(255, 140, 30));
        g.fillOval(px - r, py - r, 2 * r, 2 * r);
        g.setColor(new Color(255, 226, 90));
        g.fillOval(px - r + 3, py - r + 3, 2 * r - 6, 2 * r - 6);
        g.setColor(new Color(255, 255, 220));
        g.fillOval(px - 2, py - 3, 5, 5);
        g.dispose();
    }

    /** Bruce: dois raios ciano em X girando, com nucleo branco. */
    private void desenharRaioCruzado(Graphics2D tela, int px, int py) {
        Graphics2D g = (Graphics2D) tela.create();
        g.rotate(tempo * 7, px, py);
        int r = pesado ? 16 : 11;
        g.setColor(new Color(60, 220, 255, 60));
        g.fillOval(px - r, py - r, 2 * r, 2 * r);
        for (int k = 0; k < 2; k++) {
            g.rotate(Math.PI / 2, px, py);
            int[] xs = new int[7], ys = new int[7];
            for (int i = 0; i < 7; i++) {
                xs[i] = px - r + i * r / 3;
                ys[i] = py + (i % 2 == 0 ? -2 : 2) * (1 + (int) (Math.sin(tempo * 40 + i) * 1.5));
            }
            g.setStroke(new java.awt.BasicStroke(4));
            g.setColor(new Color(40, 190, 255));
            g.drawPolyline(xs, ys, 7);
            g.setStroke(new java.awt.BasicStroke(1.6f));
            g.setColor(new Color(225, 250, 255));
            g.drawPolyline(xs, ys, 7);
        }
        g.dispose();
    }

    /** Lemmy: ondas de som em arco, abertas para tras, pulsando. */
    private void desenharOndaSom(Graphics2D tela, int px, int py) {
        Graphics2D g = (Graphics2D) tela.create();
        g.rotate(direcao(), px, py);
        int r = pesado ? 16 : 11;
        g.setStroke(new java.awt.BasicStroke(pesado ? 4 : 3));
        for (int i = 0; i < 3; i++) {
            int raio = r - i * 4 + (int) (Math.sin(tempo * 18 - i) * 2);
            g.setColor(i == 0 ? new Color(255, 230, 130) : i == 1 ? new Color(255, 160, 50) : new Color(220, 90, 30, 190));
            g.drawArc(px - raio - i * 6, py - raio, raio * 2, raio * 2, -60, 120);
        }
        g.dispose();
    }

    /** James: esfera eletrica azul soltando faiscas em volta. */
    private void desenharEsferaEletrica(Graphics2D g, int px, int py) {
        int r = pesado ? 10 : 7;
        g.setColor(new Color(60, 120, 255, 70));
        g.fillOval(px - r - 6, py - r - 6, 2 * r + 12, 2 * r + 12);
        g.setColor(new Color(70, 140, 255));
        g.fillOval(px - r, py - r, 2 * r, 2 * r);
        g.setColor(new Color(200, 225, 255));
        g.fillOval(px - r / 2, py - r / 2, r, r);
        for (int i = 0; i < 4; i++) {
            double a = tempo * 9 + i * Math.PI / 2 + Math.sin(tempo * 21 + i);
            int x1 = px + (int) (Math.cos(a) * r), y1 = py + (int) (Math.sin(a) * r);
            int x2 = px + (int) (Math.cos(a + .4) * (r + 8)), y2 = py + (int) (Math.sin(a + .4) * (r + 8));
            int x3 = px + (int) (Math.cos(a - .2) * (r + 14)), y3 = py + (int) (Math.sin(a - .2) * (r + 14));
            g.setColor(new Color(150, 200, 255));
            g.drawPolyline(new int[]{x1, x2, x3}, new int[]{y1, y2, y3}, 3);
        }
    }

    /** Tom: estilhaco vermelho pontudo, apontando para onde voa. */
    private void desenharEstilhaco(Graphics2D tela, int px, int py) {
        Graphics2D g = (Graphics2D) tela.create();
        g.rotate(direcao(), px, py);
        int c = pesado ? 18 : 13, l = pesado ? 6 : 4;
        g.setColor(new Color(255, 40, 40, 80));
        g.fillPolygon(new int[]{px - c * 2, px - c / 2, px - c / 2}, new int[]{py, py - l, py + l}, 3);
        g.setColor(new Color(140, 10, 20));
        g.fillPolygon(new int[]{px + c, px, px - c, px}, new int[]{py, py - l - 1, py, py + l + 1}, 4);
        g.setColor(new Color(240, 40, 50));
        g.fillPolygon(new int[]{px + c - 2, px, px - c + 3, px}, new int[]{py, py - l + 1, py, py + l - 1}, 4);
        g.setColor(new Color(255, 190, 180));
        g.drawLine(px - c / 2, py, px + c - 4, py);
        g.dispose();
    }

    /** Chuck: cristal verde lapidado, girando devagar. */
    private void desenharCristal(Graphics2D tela, int px, int py) {
        Graphics2D g = (Graphics2D) tela.create();
        g.rotate(Math.sin(tempo * 3) * .5, px, py);
        int a = pesado ? 16 : 11, b = pesado ? 11 : 8;
        g.setColor(new Color(90, 255, 120, 60));
        g.fillOval(px - a - 4, py - a - 4, 2 * a + 8, 2 * a + 8);
        g.setColor(new Color(20, 120, 50));
        g.fillPolygon(new int[]{px, px + b, px, px - b}, new int[]{py - a, py, py + a, py}, 4);
        g.setColor(new Color(60, 210, 90));
        g.fillPolygon(new int[]{px, px + b, px}, new int[]{py - a, py, py + a}, 3);
        g.setColor(new Color(170, 255, 180));
        g.fillPolygon(new int[]{px, px - b, px}, new int[]{py - a, py, py}, 3);
        g.setColor(new Color(235, 255, 235));
        g.drawLine(px - 2, py - a + 4, px - b / 2, py - 2);
        g.dispose();
    }

    Movimento getMovimentoTeste() { return movimento; }
    int getEstiloTeste() { return estilo; }
    double getVxTeste() { return vx; }
    double getVyTeste() { return vy; }
    public Rectangle getBounds() {
        int r = pesado ? 11 : 6;
        return new Rectangle((int) x - r, (int) y - r, r * 2, r * 2);
    }
    public int dano() { return pesado ? 3 : 2; }
    public boolean isVisible() { return visivel; }
    public void setVisible(boolean v) { visivel = v; }
}
