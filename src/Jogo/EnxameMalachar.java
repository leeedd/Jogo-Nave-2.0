package Jogo;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.List;

/**
 * Ato I do Malachar: ele se desfaz numa nuvem de morcegos que gira em volta de um ponto e ataca
 * rapido. No meio da nuvem voa um morcego diferente, o morcego-rei (coroado, azul): so ele sente o
 * dano do chefe. Os outros seguram os tiros e, depois de tres acertos, caem e voltam pela borda.
 * Golpes: mergulho (alguns piscam vermelho e mergulham na nave), revoada (a nuvem inteira passa
 * em fila pela tela) e guincho (o rei grita e os morcegos cospem tiros). No fim do ato os morcegos
 * se juntam num ponto e dali sai o Maestro (juntar()).
 * Arte: tiro_morcego_ozzy.png (morcego comum) e malachar_morcego.png (rei, 4 quadros).
 */
final class EnxameMalachar {
    private static final BufferedImage COMUM = ler("tiro_morcego_ozzy.png");
    static boolean temArte() { return COMUM != null && PosesChefes.morcegoMalachar() != null; }
    private static final int W = Config.LARGURA, H = Config.ALTURA, TOPO = Config.HUD, N = 16, CW = 52, RW = 96;

    private final double[] bx = new double[N], by = new double[N], ang = new double[N], raio = new double[N], vx = new double[N], vy = new double[N],
            aviso = new double[N], flash = new double[N], volta = new double[N];
    private final int[] estado = new int[N], vida = new int[N]; // estado: 0 nuvem, 1 aviso, 2 mergulho, 3 caido (volta pela borda)
    private int rei, golpes, ultimoAtingido = -1;
    private double cx = W - 230, cy = 300, tempo, entrada, recarga = 1.4, trocaRei = 3, esperaContato, guincho, flashRei, vidaFrac = 1, juntar = -1;
    private boolean bloqueou;
    private final java.util.Random sorte = new java.util.Random(11);

    EnxameMalachar() {
        for (int i = 0; i < N; i++) {
            ang[i] = i * Math.PI * 2 / N + sorte.nextDouble() * .3; raio[i] = 70 + (i % 4) * 34 + sorte.nextInt(20);
            bx[i] = W + 60 + sorte.nextInt(260); by[i] = TOPO + 40 + sorte.nextInt(H - TOPO - 80); vida[i] = 3;
        }
        rei = 5;
    }

    double entrada() { return entrada; }
    void vida(double f) { vidaFrac = f; }
    /** Comeca a juntar os morcegos num ponto (transicao para o Maestro). */
    void juntar(double alvoX, double alvoY) { juntar = 0; cx = alvoX; cy = alvoY; }

    private double slotX(int i) { return cx + Math.cos(ang[i]) * raio[i]; }
    private double slotY(int i) { return cy + Math.sin(ang[i]) * raio[i] * .72; }

    void atualizar(double dt, int naveX, int naveY, Dificuldade dif, List<TiroChefe> tiros) {
        tempo += dt; esperaContato = Math.max(0, esperaContato - dt); guincho = Math.max(0, guincho - dt); flashRei = Math.max(0, flashRei - dt);
        double nx = naveX + 22, ny = naveY + 18, furia = vidaFrac < .5 ? 1.35 : 1;
        if (juntar >= 0) { // todos voam para o mesmo ponto
            juntar = Math.min(1, juntar + dt / 1.3);
            for (int i = 0; i < N; i++) { double k = Math.min(1, dt * (3 + juntar * 8)); bx[i] += (cx - bx[i]) * k; by[i] += (cy - by[i]) * k; ang[i] += dt * 6; }
            return;
        }
        entrada = Math.min(1, entrada + dt / 1.4);
        // o centro da nuvem passeia pela metade direita e acompanha a nave devagar
        cx += (W - 250 + Math.sin(tempo * .55) * 110 - cx) * Math.min(1, dt * 1.2);
        cy += (Math.max(TOPO + 150, Math.min(H - 150, ny)) - cy) * Math.min(1, dt * .9);
        for (int i = 0; i < N; i++) {
            flash[i] = Math.max(0, flash[i] - dt);
            ang[i] += dt * (1.5 + (i % 3) * .35) * furia * (i % 2 == 0 ? 1 : -1) * .9;
            if (estado[i] == 0) { double k = Math.min(1, dt * (entrada < 1 ? 2.2 : 5)); bx[i] += (slotX(i) - bx[i]) * k; by[i] += (slotY(i) - by[i]) * k; }
            else if (estado[i] == 1) { aviso[i] -= dt; bx[i] += (slotX(i) - bx[i]) * Math.min(1, dt * 5); by[i] += (slotY(i) - by[i]) * Math.min(1, dt * 5);
                if (aviso[i] <= 0) { double a = Math.atan2(ny - by[i], nx - bx[i]), v = 760 * furia; vx[i] = Math.cos(a) * v; vy[i] = Math.sin(a) * v; estado[i] = 2; SomJogo.tocar(SomJogo.Efeito.CHEFE_TIRO); } }
            else if (estado[i] == 2) { bx[i] += vx[i] * dt; by[i] += vy[i] * dt;
                if (bx[i] < -60 || bx[i] > W + 60 || by[i] < TOPO - 60 || by[i] > H + 60) { estado[i] = 3; volta[i] = .6; vida[i] = 3; } }
            else { volta[i] -= dt; if (volta[i] <= 0) { estado[i] = 0; bx[i] = W + 50; by[i] = TOPO + 30 + sorte.nextInt(H - TOPO - 60); } }
        }
        // o rei troca de lugar na nuvem para o jogador ter que segui-lo
        trocaRei -= dt;
        if (trocaRei <= 0 && estado[rei] == 0) { int novo = sorte.nextInt(N); if (novo != rei && estado[novo] == 0) { double a = ang[rei], r = raio[rei]; ang[rei] = ang[novo]; raio[rei] = raio[novo]; ang[novo] = a; raio[novo] = r; } trocaRei = 2.2 + sorte.nextDouble() * 1.5; }
        if (entrada < 1) return;
        recarga -= dt * dif.cadenciaInimigos * furia;
        if (recarga > 0) return;
        int g = golpes++ % 4;
        if (g == 0 || g == 2) { // mergulho: 3 (ou 5 na furia) piscam e mergulham
            int n = vidaFrac < .5 ? 5 : 3;
            for (int k = 0, t = 0; k < n && t < 40; t++) { int i = sorte.nextInt(N); if (i != rei && estado[i] == 0) { estado[i] = 1; aviso[i] = .38 + k * .1; k++; } }
            recarga = .9;
        } else if (g == 1) { // guincho: o rei grita e os comuns cospem um tiro roxo na nave
            guincho = .5; flashRei = .5; SomJogo.tocar(SomJogo.Efeito.ERRO);
            for (int i = 0; i < N; i++) if (i != rei && estado[i] == 0 && i % 2 == golpes / 4 % 2) {
                double a = Math.atan2(ny - by[i], nx - bx[i]);
                tiros.add(new TiroChefe(bx[i], by[i], Math.cos(a) * 300, Math.sin(a) * 300, false, TiroChefe.ENERGIA, TiroChefe.Movimento.RETO).comCor(new Color(190, 90, 255)));
            }
            recarga = 1.1;
        } else { // revoada: a nuvem inteira mergulha em fila, um atras do outro
            for (int i = 0, k = 0; i < N; i++) if (i != rei && estado[i] == 0) { estado[i] = 1; aviso[i] = .45 + k++ * .07; }
            recarga = 2.4;
        }
    }

    private Rectangle caixa(int i) { int s = i == rei ? RW : CW; return new Rectangle((int) bx[i] - s / 2 + 6, (int) by[i] - s / 3, s - 12, s * 2 / 3); }
    /** O alvo do chefe e o rei. */
    Rectangle bounds() { return caixa(rei); }

    /** Tiro do jogador: o rei recebe o dano; um morcego comum segura o tiro. */
    boolean colide(Rectangle r) {
        if (juntar >= 0) return false;
        if (r.intersects(caixa(rei))) return true;
        for (int i = 0; i < N; i++) if (i != rei && estado[i] != 3 && r.intersects(caixa(i))) { bloqueou = true; ultimoAtingido = i; return true; }
        return false;
    }
    boolean consumirBloqueio() {
        if (!bloqueou) return false; bloqueou = false;
        int i = ultimoAtingido; if (i >= 0) { flash[i] = .12; if (--vida[i] <= 0) { estado[i] = 3; volta[i] = 2.5; vida[i] = 3; SomJogo.tocar(SomJogo.Efeito.EXPLOSAO); } }
        return true;
    }
    void ferido() { flashRei = .16; }
    /** Encosto: morcego mergulhando ou a propria nuvem machuca, com pausa entre um dano e outro. */
    boolean contato(Rectangle nave) {
        if (esperaContato > 0 || juntar >= 0) return false;
        for (int i = 0; i < N; i++) if (estado[i] != 3 && nave.intersects(caixa(i))) { esperaContato = .8; if (estado[i] == 2) { estado[i] = 3; volta[i] = .8; } return true; }
        return false;
    }

    void desenhar(Graphics2D tela, float alfa) {
        if (COMUM == null) return;
        Graphics2D g = (Graphics2D) tela.create();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        if (alfa < 1) g.setComposite(AlphaComposite.SrcOver.derive(Math.max(0, alfa)));
        for (int i = 0; i < N; i++) {
            if (i == rei || estado[i] == 3) continue;
            double bate = .7 + .3 * Math.abs(Math.sin(tempo * 16 + i));
            int w = CW, h = (int) (CW * COMUM.getHeight() / (double) COMUM.getWidth() * bate), x = (int) bx[i], y = (int) by[i];
            boolean praDireita = estado[i] == 2 && vx[i] > 0;
            if (praDireita) g.drawImage(COMUM, x + w / 2, y - h / 2, -w, h, null); else g.drawImage(COMUM, x - w / 2, y - h / 2, w, h, null);
            if (estado[i] == 1 && (int) (tempo * 20) % 2 == 0) { g.setColor(new Color(255, 40, 40)); g.fillRect(x - 7, y - 4, 4, 4); g.fillRect(x + 3, y - 4, 4, 4); } // olhos piscando antes do mergulho
            if (flash[i] > 0) { g.setColor(new Color(255, 255, 255, 170)); g.fillRect(x - 8, y - 6, 16, 12); }
        }
        BufferedImage[] q = PosesChefes.morcegoMalachar();
        if (q != null) {
            BufferedImage f = q[(int) (tempo * 9) % 4];
            int w = RW + (guincho > 0 ? 10 : 0), h = w * f.getHeight() / f.getWidth(), x = (int) bx[rei], y = (int) by[rei];
            if (juntar < 0) { g.setColor(new Color(170, 225, 255)); // faiscas de pixel em volta do rei
                for (int k = 0; k < 6; k++) { double aa = tempo * 2.5 + k * Math.PI / 3; int px = x + (int) (Math.cos(aa) * w * .55), py = y + (int) (Math.sin(aa) * h * .5);
                    if ((int) (tempo * 8 + k) % 3 != 0) { g.fillRect(px - 1, py - 4, 3, 9); g.fillRect(px - 4, py - 1, 9, 3); } } }
            g.drawImage(f, x - w / 2, y - h / 2, w, h, null);
            if (flashRei > 0) { g.setComposite(AlphaComposite.SrcOver.derive((float) Math.min(1, flashRei * 4) * .5f * Math.max(0, alfa))); g.drawImage(f, x - w / 2 - 2, y - h / 2 - 2, w + 4, h + 4, null); }
        }
        g.dispose();
    }

    private static BufferedImage ler(String n) {
        java.net.URL u = EnxameMalachar.class.getResource("/ImagemIcon/" + n);
        if (u == null) return null;
        try { return javax.imageio.ImageIO.read(u); } catch (java.io.IOException e) { return null; }
    }
}
