package Jogo;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

/** Disparo em leque com animacao de pulso e rastro de energia. */
public final class TiroChefe {
    public enum Movimento { RETO, ONDA, ACELERADO, PULSANTE, ARCO }
    private final Movimento movimento;
    private double x, y, tempo;
    private final double vx, vy;
    /** Queda acumulada do movimento em ARCO (barris arremessados). */
    private double queda;
    private final boolean pesado;
    private final int estilo;
    /** Cor dos projeteis genericos (ENERGIA e NOTA) dos integrantes. */
    private Color cor = new Color(240, 80, 60);
    TiroChefe comCor(Color c) { if (c != null) cor = c; return this; }
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
        if (movimento == Movimento.ARCO) queda += 560 * dt;
        x += vx * dt * velocidade + perpx * curva;
        y += (vy + queda) * dt * velocidade + perpy * curva;
        tempo += dt;
        if (estilo == RASTRO && tempo > 1.4) visivel = false;
        int margem = estilo == VAGAO ? 130 : 40;
        if (x < -margem || x > Config.LARGURA + margem || y < Config.HUD - margem || y > Config.ALTURA + margem)
            visivel = false;
    }
    public void desenhar(Graphics2D g) {
        int px = (int) x, py = (int) y;
        // Cada artista tem um projetil com forma propria, combinando com o golpe da prancha.
        // tiros dos vocalistas com arte propria (girada para onde voam)
        int linha = estilo == 2 ? SpritesExtras.ROB : estilo == 3 ? SpritesExtras.BRUCE : estilo == 5 ? SpritesExtras.JAMES : estilo == 6 ? SpritesExtras.TOM : estilo == 7 ? SpritesExtras.CHUCK : -1;
        if (linha >= 0 && SpritesExtras.temTiros()) { SpritesExtras.tiro(g, linha, x, y, vx, vy, pesado ? 82 : 60, tempo); return; }
        switch (estilo) {
            case FLECHA: case LANCA: case CORTE: desenharArmaTormenta(g, px, py); return;
            case RAIO_CEU: desenharRaioCeu(g, px, py); return;
            case FUMACA: desenharFumaca(g, px, py); return;
            case VAGAO: desenharVagao(g, px, py); return;
            case FOGO_ROXO: desenharFogoRoxo(g, px, py); return;
            case BARRIL: desenharBarril(g, px, py); return;
            case RASTRO: desenharRastro(g, px, py); return;
            case ONDA_SOM: desenharOndaLemmy(g, px, py); return;
            case PRATO: desenharPrato(g, px, py); return;
            case ENERGIA: desenharEnergia(g, px, py); return;
            case NOTA: desenharNota(g, px, py); return;
            case AS_ESPADAS: desenharAs(g, px, py); return;
            case MORCEGO_OZZY: desenharMorcegoOzzy(g, px, py); return;
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

    private void desenharMorcego(Graphics2D g, int px, int py) { morcego(g, px, py, pesado ? 3 : 2, tempo, vx > 0, true); }

    /** Morcego em pixel art reutilizavel (tambem usado pela Revoada do jogador). */
    static void morcego(Graphics2D g, int px, int py, int p, double tempo, boolean paraDireita, boolean aura) {
        if (aura) { g.setColor(new Color(150, 60, 210, 42)); g.fillOval(px - 10 * p, py - 6 * p, 20 * p, 12 * p); }
        int[] ciclo = {0, 1, 2, 1};
        String[] quadro = MORCEGO[ciclo[(int) (Math.max(0, tempo) * 14) % 4]];
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
        if (estilo == RAIO_CEU) return new Rectangle((int) x - 9, (int) y - 40, 18, 46);
        if (estilo == CORTE) return new Rectangle((int) x - 14, (int) y - 34, 28, 68);
        if (estilo == LANCA || estilo == FLECHA) { int r = estilo == LANCA ? 12 : 8; return new Rectangle((int) x - r, (int) y - r, r * 2, r * 2); }
        if (estilo == VAGAO) return new Rectangle((int) x - 70, (int) y - 26, 150, 52);
        if (estilo == ONDA_SOM) { int h = alturaOnda() * 3 / 4; return new Rectangle((int) x - 16, (int) y - h / 2, 32, h); }
        int r = estilo == ENERGIA ? 9 : estilo == NOTA ? 9 : estilo == PRATO ? 14 : estilo == AS_ESPADAS ? 14 : estilo == MORCEGO_OZZY ? 13 : estilo == BARRIL ? 13 : estilo == RASTRO ? 10 : estilo == FUMACA ? (int) (16 + Math.min(14, tempo * 16)) : estilo == FOGO_ROXO ? 13 : pesado ? 11 : 6;
        return new Rectangle((int) x - r, (int) y - r, r * 2, r * 2);
    }
    public int dano() { return estilo == ONDA_SOM ? 1 : estilo == VAGAO ? 5 : estilo == FOGO_ROXO ? 3 : pesado ? 3 : 2; }

    // ---- Kanoa (tormenta): flecha, lanca, corte do machado e raio do ceu ----
    static final int FLECHA = 31, LANCA = 32, CORTE = 33, RAIO_CEU = 34;
    private static final Color AZUL_RAIO = new Color(140, 210, 255), BRANCO_RAIO = new Color(235, 248, 255);
    private void desenharArmaTormenta(Graphics2D tela, int px, int py) {
        Graphics2D g = (Graphics2D) tela.create(); g.translate(px, py); g.rotate(Math.atan2(vy, vx));
        java.util.Random r = new java.util.Random((long) (tempo * 30));
        if (estilo == CORTE) {
            // meia-lua do machado: arco de luz grossa, com faiscas na borda
            g.setStroke(new java.awt.BasicStroke(10)); g.setColor(new Color(90, 170, 255, 120)); g.drawArc(-34, -36, 44, 72, -70, 140);
            g.setStroke(new java.awt.BasicStroke(5)); g.setColor(AZUL_RAIO); g.drawArc(-30, -34, 40, 68, -65, 130);
            g.setStroke(new java.awt.BasicStroke(2)); g.setColor(BRANCO_RAIO); g.drawArc(-27, -32, 36, 64, -60, 120);
            for (int k = 0; k < 4; k++) g.fillRect(4 + r.nextInt(8), -30 + r.nextInt(60), 3, 3);
        } else {
            int L = estilo == LANCA ? 54 : 30;
            // haste de raio em zigue-zague e ponta de cristal
            int[] xs = new int[6], ys = new int[6];
            for (int k = 0; k < 6; k++) { xs[k] = -L + k * (L - 8) / 5; ys[k] = k == 0 || k == 5 ? 0 : r.nextInt(7) - 3; }
            g.setStroke(new java.awt.BasicStroke(estilo == LANCA ? 7 : 5)); g.setColor(new Color(90, 170, 255, 120)); g.drawPolyline(xs, ys, 6);
            g.setStroke(new java.awt.BasicStroke(estilo == LANCA ? 3 : 2)); g.setColor(BRANCO_RAIO); g.drawPolyline(xs, ys, 6);
            int c = estilo == LANCA ? 14 : 9;
            g.setColor(AZUL_RAIO); g.fillPolygon(new int[]{-8, c, -8}, new int[]{-c / 2 - 2, 0, c / 2 + 2}, 3);
            g.setColor(BRANCO_RAIO); g.fillPolygon(new int[]{-4, c - 4, -4}, new int[]{-c / 4, 0, c / 4}, 3);
            if (estilo == FLECHA) { g.setColor(AZUL_RAIO); g.drawLine(-L, 0, -L - 6, -6); g.drawLine(-L, 0, -L - 6, 6); }
        }
        g.dispose();
    }
    private void desenharRaioCeu(Graphics2D tela, int px, int py) {
        // o raio desce do topo da tela ate a ponta, em zigue-zague
        Graphics2D g = (Graphics2D) tela.create();
        java.util.Random r = new java.util.Random((long) (tempo * 25) + px);
        int n = Math.max(2, (py - Config.HUD) / 28 + 1); int[] xs = new int[n], ys = new int[n];
        for (int k = 0; k < n; k++) { ys[k] = Config.HUD + (py - Config.HUD) * k / (n - 1); xs[k] = px + (k == n - 1 ? 0 : r.nextInt(17) - 8); }
        g.setStroke(new java.awt.BasicStroke(9)); g.setColor(new Color(90, 170, 255, 90)); g.drawPolyline(xs, ys, n);
        g.setStroke(new java.awt.BasicStroke(4)); g.setColor(AZUL_RAIO); g.drawPolyline(xs, ys, n);
        g.setStroke(new java.awt.BasicStroke(2)); g.setColor(BRANCO_RAIO); g.drawPolyline(xs, ys, n);
        g.setColor(new Color(235, 248, 255, 200)); g.fillOval(px - 8, py - 8, 16, 16);
        g.dispose();
    }

    // ---- Ozzy (Crazy Train e dragao) ----
    static final int FUMACA = 20, VAGAO = 21, FOGO_ROXO = 22, BARRIL = 23, RASTRO = 24, ONDA_SOM = 25, AS_ESPADAS = 26, MORCEGO_OZZY = 27, PRATO = 28, ENERGIA = 29, NOTA = 30;
    private void desenharEnergia(Graphics2D g, int px, int py) {
        double d = Math.atan2(vy, vx);
        for (int k = 4; k >= 0; k--) { int tx = px - (int) (Math.cos(d) * k * 6), ty = py - (int) (Math.sin(d) * k * 6), r = 10 - k * 2; g.setColor(new Color(cor.getRed(), cor.getGreen(), cor.getBlue(), 230 - k * 40)); g.fillRect(tx - r, ty - r, r * 2, r * 2); }
        g.setColor(Color.WHITE); g.fillRect(px - 3, py - 3, 6, 6);
    }
    private void desenharNota(Graphics2D g, int px, int py) {
        int b = (int) (Math.sin(tempo * 12) * 2);
        g.setColor(cor.darker()); g.fillOval(px - 9, py + 2 + b, 12, 9); g.fillRect(px + 1, py - 14 + b, 3, 18); g.fillRect(px + 1, py - 14 + b, 9, 4);
        g.setColor(cor); g.fillOval(px - 8, py + 3 + b, 10, 7); g.fillRect(px + 2, py - 13 + b, 2, 16);
    }
    /** Prato de bateria girando como serra, em pixel art: disco dourado com dentes. */
    private void desenharPrato(Graphics2D tela, int px, int py) {
        Graphics2D g = (Graphics2D) tela.create(); g.rotate(tempo * 18, px, py);
        int r = 15;
        g.setColor(new Color(150, 100, 20)); for (int k = 0; k < 8; k++) { double a = k * Math.PI / 4; g.fillRect(px + (int) (Math.cos(a) * r) - 3, py + (int) (Math.sin(a) * r) - 3, 6, 6); }
        g.setColor(new Color(200, 150, 40)); g.fillOval(px - r, py - r, r * 2, r * 2);
        g.setColor(new Color(245, 205, 90)); g.fillOval(px - r + 4, py - r + 4, r * 2 - 8, r * 2 - 8);
        g.setColor(new Color(170, 120, 30)); g.drawOval(px - 8, py - 8, 16, 16);
        g.setColor(new Color(255, 245, 200)); g.fillRect(px - 6, py - 9, 5, 3); g.setColor(new Color(90, 60, 15)); g.fillRect(px - 2, py - 2, 4, 4);
        g.dispose();
    }
    private static final java.awt.image.BufferedImage ARTE_AS = ler("tiro_as_espadas.png"), ARTE_MORCEGO = ler("tiro_morcego_ozzy.png");
    /** As de espadas em chamas do Lemmy: gira como estrela ninja. */
    private void desenharAs(Graphics2D tela, int px, int py) {
        Graphics2D g = (Graphics2D) tela.create(); g.rotate(tempo * 13, px, py);
        if (ARTE_AS != null) { g.setRenderingHint(java.awt.RenderingHints.KEY_INTERPOLATION, java.awt.RenderingHints.VALUE_INTERPOLATION_BILINEAR); g.drawImage(ARTE_AS, px - 25, py - 22, 50, 44, null); }
        else { g.setColor(new Color(240, 225, 190)); g.fillRect(px - 9, py - 12, 18, 24); g.setColor(Color.BLACK); g.fillOval(px - 4, py - 4, 8, 8); }
        g.dispose();
    }
    /** Morcego do Ozzy: bate as asas (achata e abre) e olha para onde voa. */
    private void desenharMorcegoOzzy(Graphics2D tela, int px, int py) {
        if (ARTE_MORCEGO == null) { desenharMorcego(tela, px, py); return; }
        Graphics2D g = (Graphics2D) tela.create();
        g.setRenderingHint(java.awt.RenderingHints.KEY_INTERPOLATION, java.awt.RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        double asa = .55 + .45 * Math.abs(Math.sin(tempo * 16)); int w = (int) (44 * (.85 + .15 * asa)), h = (int) (38 * asa);
        g.translate(px, py); g.rotate(Math.atan2(vy, Math.abs(vx)) * (vx > 0 ? 1 : -1) * .5);
        if (vx > 0) g.drawImage(ARTE_MORCEGO, w / 2, -h / 2, -w, h, null); else g.drawImage(ARTE_MORCEGO, -w / 2, -h / 2, w, h, null);
        g.dispose();
    }
    private static final java.awt.image.BufferedImage ARTE_ONDA = ler("onda_som_lemmy.png");
    /** A onda do paredao de caixas empurra a nave em vez de so ferir. */
    boolean empurra() { return estilo == ONDA_SOM; }
    private int alturaOnda() { return 58 + (int) (Math.min(1, tempo * 1.2) * 46); }
    private void desenharOndaLemmy(Graphics2D tela, int px, int py) {
        Graphics2D g = (Graphics2D) tela.create();
        int h = alturaOnda();
        if (ARTE_ONDA != null) {
            int fw = ARTE_ONDA.getWidth() / 4, k = Math.min(3, (int) (tempo * 5)), w = fw * h / ARTE_ONDA.getHeight();
            g.setRenderingHint(java.awt.RenderingHints.KEY_INTERPOLATION, java.awt.RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g.drawImage(ARTE_ONDA, px - w / 3, py - h / 2, px - w / 3 + w, py + h / 2, k * fw, 0, k * fw + fw, ARTE_ONDA.getHeight(), null);
        } else { g.setColor(new Color(255, 150, 40)); g.setStroke(new java.awt.BasicStroke(5)); g.drawArc(px - 20, py - h / 2, 40, h, 90, 180); }
        g.dispose();
    }
    double getX() { return x; }
    double getY() { return y; }
    double idade() { return tempo; }
    int estilo() { return estilo; }
    /** Barril de Jack arremessado pela janela da van: gira no ar e pisca antes de estourar. */
    private void desenharBarril(Graphics2D tela, int px, int py) {
        Graphics2D g = (Graphics2D) tela.create(); g.rotate(tempo * 7, px, py);
        g.setColor(new Color(70, 40, 20)); g.fillRect(px - 11, py - 14, 22, 28);
        g.setColor(new Color(120, 72, 34)); g.fillRect(px - 9, py - 14, 6, 28);
        g.setColor(new Color(40, 40, 46)); g.fillRect(px - 11, py - 9, 22, 3); g.fillRect(px - 11, py + 6, 22, 3);
        g.setColor(Color.BLACK); g.fillRect(px - 6, py - 4, 12, 8); g.setColor(Color.WHITE); g.fillRect(px - 4, py - 2, 8, 1); g.fillRect(px - 4, py + 1, 6, 1);
        if (tempo > .7 && (int) (tempo * 14) % 2 == 0) { g.setColor(new Color(255, 80, 40, 170)); g.fillOval(px - 16, py - 16, 32, 32); }
        g.dispose();
    }
    /** Pneu em chamas deixa fogo na pista: some sozinho em 1,4 s. */
    private void desenharRastro(Graphics2D tela, int px, int py) {
        Graphics2D g = (Graphics2D) tela.create();
        double vida = 1 - tempo / 1.4; int a = (int) (230 * Math.max(0, Math.min(1, vida * 2)));
        for (int k = 0; k < 3; k++) {
            int h = 10 + (int) ((Math.sin(tempo * 22 + px * .1 + k * 2) + 1) * 7), lx = px - 9 + k * 6;
            g.setColor(new Color(255, 90 + k * 40, 20, a)); g.fillPolygon(new int[]{lx - 5, lx, lx + 5}, new int[]{py + 6, py + 6 - h, py + 6}, 3);
        }
        g.setColor(new Color(255, 230, 140, a)); g.fillRect(px - 6, py + 2, 12, 4);
        g.dispose();
    }
    private static final java.awt.image.BufferedImage ARTE_VAGAO = ler("ozzy_trem_vagao.png");
    private static java.awt.image.BufferedImage ler(String n) { java.net.URL u = TiroChefe.class.getResource("/ImagemIcon/" + n); if (u == null) return null; try { return javax.imageio.ImageIO.read(u); } catch (java.io.IOException e) { return null; } }
    /** Baforada da chamine: nuvem roxa que cresce e se desfaz enquanto anda. */
    private void desenharFumaca(Graphics2D tela, int px, int py) {
        Graphics2D g = (Graphics2D) tela.create();
        int r = (int) (16 + Math.min(14, tempo * 16));
        for (int k = 0; k < 6; k++) {
            double a = k * 1.05 + tempo * 2.2; int ox = (int) (Math.cos(a) * r * .55), oy = (int) (Math.sin(a) * r * .45), rr = r * (k % 2 == 0 ? 7 : 5) / 9;
            g.setColor(new Color(k % 3 == 0 ? 190 : 130, 60, k % 3 == 0 ? 240 : 190, 95));
            g.fillOval(px + ox - rr, py + oy - rr, rr * 2, rr * 2);
        }
        g.setColor(new Color(240, 200, 255, 150)); g.fillOval(px - r / 4, py - r / 4, r / 2, r / 2);
        g.dispose();
    }
    /** Vagao de espinhos arrancado do Crazy Train, voando de nariz para a nave. */
    private void desenharVagao(Graphics2D tela, int px, int py) {
        Graphics2D g = (Graphics2D) tela.create();
        double ang = Math.atan2(vy, vx) - Math.PI; // a arte ja aponta para a esquerda
        g.rotate(ang * .6, px, py);
        if (ARTE_VAGAO != null) {
            int w = 190, h = ARTE_VAGAO.getHeight() * w / ARTE_VAGAO.getWidth();
            g.setRenderingHint(java.awt.RenderingHints.KEY_INTERPOLATION, java.awt.RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g.drawImage(ARTE_VAGAO, px - w / 2 + 10, py - h / 2, w, h, null);
        } else {
            g.setColor(new Color(40, 34, 44)); g.fillRect(px - 60, py - 22, 130, 44);
            g.setColor(new Color(200, 80, 255)); for (int k = 0; k < 5; k++) g.fillRect(px - 40 + k * 22, py - 10, 10, 14);
            g.setColor(new Color(170, 170, 180)); g.fillPolygon(new int[]{px - 60, px - 90, px - 60}, new int[]{py - 18, py, py + 18}, 3);
        }
        g.dispose();
    }
    /** Labareda roxa do dragao: nucleo claro, borda violeta, rastro curto. */
    private void desenharFogoRoxo(Graphics2D tela, int px, int py) {
        Graphics2D g = (Graphics2D) tela.create();
        double d = Math.atan2(vy, vx);
        for (int k = 4; k >= 0; k--) {
            int tx = px - (int) (Math.cos(d) * k * 7), ty = py - (int) (Math.sin(d) * k * 7), r = 13 - k * 2;
            g.setColor(new Color(150 + k * 10, 40, 230, 200 - k * 35)); g.fillRect(tx - r, ty - r, r * 2, r * 2);
        }
        int f = (int) (tempo * 20) % 2;
        g.setColor(new Color(225, 150, 255)); g.fillRect(px - 7 + f, py - 7, 14, 14);
        g.setColor(new Color(255, 235, 255)); g.fillRect(px - 3, py - 3, 6, 6);
        g.dispose();
    }
    public boolean isVisible() { return visivel; }
    public void setVisible(boolean v) { visivel = v; }
}
