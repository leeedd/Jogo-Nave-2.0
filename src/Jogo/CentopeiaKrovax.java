package Jogo;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 * Krovax, a centopeia de sucata (Ferrugem Atomica). A cabeca serpenteia pela metade direita da tela
 * e o corpo segue o mesmo caminho. Os segmentos seguram os tiros e podem ser destruidos um a um;
 * so a cabeca leva dano no chefe. Ataques: fogo das patas-escapamento, grito em ondas, mordida em
 * investida e veneno do ferrao. Na furia (ato II) fica mais rapida e se enrola em volta da nave.
 */
final class CentopeiaKrovax {
    private static final BufferedImage[] CABECA = {ler("centopeia_cabeca_0.png"), ler("centopeia_cabeca_1.png"), ler("centopeia_cabeca_2.png")};
    private static final BufferedImage[] SEGMENTO = {ler("centopeia_segmento_0.png"), ler("centopeia_segmento_1.png")};
    private static final BufferedImage CAUDA = ler("centopeia_cauda.png");
    static boolean temArte() { return CABECA[0] != null && SEGMENTO[0] != null && CAUDA != null; }

    private static final int W = Config.LARGURA, H = Config.ALTURA, TOPO = Config.HUD;
    private static final double ESC = .21, PASSO = 62;
    private static final int SEGMENTOS = 10, VIDA_SEG = 7;

    private final List<double[]> rastro = new ArrayList<>();   // posicoes passadas da cabeca (x, y)
    private final int[] vidaSeg = new int[SEGMENTOS];
    private final double[] flashSeg = new double[SEGMENTOS];
    private double hx = W + 120, hy = TOPO + 200, ang = Math.PI, tempo, entrada, fase, flash, recarga = 1.4;
    private double grito, mordida, enrolar, alvoX, alvoY, anguloEnrolar;
    private int golpes, ultimoSegAtingido = -1;
    private boolean furia, bloqueou, acertou;
    private double esperaContato, wx = W * .7, wy = TOPO + 220, agitacao;
    private final java.util.Random sorte = new java.util.Random(4);

    CentopeiaKrovax() { java.util.Arrays.fill(vidaSeg, VIDA_SEG); for (int i = 0; i < 400; i++) rastro.add(new double[]{hx + i * 3, hy}); }
    double entrada() { return entrada; }
    void furia() { furia = true; enrolar = 0; mordida = 0; }

    private int vivos() { int n = 0; for (int v : vidaSeg) if (v > 0) n++; return n; }

    void atualizar(double dt, int naveX, int naveY, Dificuldade dif, List<TiroChefe> tiros, double vidaFrac) {
        tempo += dt; flash = Math.max(0, flash - dt); esperaContato = Math.max(0, esperaContato - dt);
        for (int i = 0; i < SEGMENTOS; i++) flashSeg[i] = Math.max(0, flashSeg[i] - dt);
        entrada = Math.min(1, entrada + dt / 1.6);
        double nx = naveX + 22, ny = naveY + 18;
        // velocidade sobe quando perde segmentos e na furia
        // agitacao 0..1: sobe quando perde vida (no ato I ate .5, na furia ate 1)
        agitacao = furia ? .5 + .5 * (1 - vidaFrac) : .5 * (1 - vidaFrac);
        double vel = (170 + (SEGMENTOS - vivos()) * 12) * (1 + 1.3 * agitacao);
        double tx, ty;
        if (enrolar > 0) {         // furia: circula em volta da nave fechando o cerco
            enrolar = Math.max(0, enrolar - dt); anguloEnrolar += dt * 3.2;
            double r = 150 + 90 * enrolar / 3.0;
            tx = alvoX + Math.cos(anguloEnrolar) * r; ty = alvoY + Math.sin(anguloEnrolar) * r * .8; vel *= 1.9;
        } else if (mordida > 0) {  // investida: vai direto no ponto travado e volta
            mordida = Math.max(0, mordida - dt);
            tx = mordida > .55 ? alvoX : W * .8; ty = mordida > .55 ? alvoY : TOPO + 220; vel = mordida > .55 ? 620 : 340;
        } else {                   // rasteja pelo mapa todo: vai de um ponto a outro, as vezes saindo pela borda
            if (Math.hypot(wx - hx, wy - hy) < 70 || sorte.nextDouble() < agitacao * .6 * dt) novoPonto();
            tx = wx; ty = wy;
            if (entrada < 1) { tx = W * .78; ty = TOPO + 220; }
        }
        double desejado = Math.atan2(ty - hy, tx - hx), dif2 = Math.atan2(Math.sin(desejado - ang), Math.cos(desejado - ang));
        double giro = (mordida > .55 || enrolar > 0 ? 6 : 2.4 + 3 * agitacao) * dt;
        ang += Math.max(-giro, Math.min(giro, dif2));
        hx += Math.cos(ang) * vel * dt; hy += Math.sin(ang) * vel * dt;
        hx = Math.max(-140, Math.min(W + 140, hx)); hy = Math.max(TOPO - 90, Math.min(H + 90, hy));
        rastro.add(0, new double[]{hx, hy}); if (rastro.size() > 900) rastro.remove(rastro.size() - 1);
        grito = Math.max(0, grito - dt);
        if (entrada < 1 || mordida > 0 || enrolar > 0) return;
        recarga -= dt * dif.cadenciaInimigos * (1 + agitacao);
        if (recarga > 0 || tiros.size() > 50) return;
        // calma: ataques em sequencia; ferida: golpe sorteado e ritmo irregular
        int g = agitacao < .3 ? golpes % 4 : sorte.nextInt(4); golpes++;
        if (g == 0) {              // fogo das patas: cada segmento vivo cospe uma bola de fogo na nave
            for (int i = 0; i < SEGMENTOS; i++) { if (vidaSeg[i] <= 0 || i % 2 != golpes % 2) continue; double[] p = posSeg(i);
                double a = Math.atan2(ny - p[1], nx - p[0]); tiros.add(new TiroChefe(p[0], p[1] + 14, Math.cos(a) * 260, Math.sin(a) * 260, false, TiroChefe.BOLA_FOGO, TiroChefe.Movimento.RETO)); }
        } else if (g == 1) {       // grito: ondas de som saindo da boca
            grito = .7; double a = Math.atan2(ny - hy, nx - hx);
            for (int i = -1; i <= 1; i++) tiros.add(new TiroChefe(hx, hy, Math.cos(a + i * .22) * 300, Math.sin(a + i * .22) * 300, false, TiroChefe.ONDA_SOM, TiroChefe.Movimento.RETO));
        } else if (g == 2) {       // mordida: trava a posicao da nave e avanca
            mordida = 1.5; alvoX = nx; alvoY = ny; acertou = false; SomJogo.tocar(SomJogo.Efeito.CHEFE_ENTRADA);
        } else {                   // veneno do ferrao, em arco; na furia tambem se enrola
            double[] c = rastro.get(Math.min(rastro.size() - 1, indiceRastro(SEGMENTOS)));
            for (int i = 0; i < (furia ? 6 : 4); i++) tiros.add(new TiroChefe(c[0], c[1], -120 - i * 45, -200 + i * 30, false, TiroChefe.ENERGIA, TiroChefe.Movimento.ARCO).comCor(new Color(110, 255, 90)));
            if (furia) { enrolar = 3; alvoX = nx; alvoY = ny; anguloEnrolar = Math.atan2(hy - ny, hx - nx); }
        }
        recarga = agitacao < .3 ? 1.5 : .45 + sorte.nextDouble() * 1.4 * (1 - agitacao * .5);
        if (agitacao > .6 && sorte.nextDouble() < .3) recarga = .15; // as vezes emenda dois golpes
    }
    /** Proximo ponto: qualquer lugar do campo; as vezes um pouco fora da borda, para sumir e voltar. */
    private void novoPonto() {
        if (sorte.nextDouble() < .2) { int b = sorte.nextInt(4); wx = b == 0 ? -110 : b == 1 ? W + 110 : 60 + sorte.nextInt(W - 120); wy = b == 2 ? TOPO - 70 : b == 3 ? H + 70 : TOPO + 40 + sorte.nextInt(H - TOPO - 80); }
        else { wx = 60 + sorte.nextInt(W - 120); wy = TOPO + 40 + sorte.nextInt(H - TOPO - 80); }
    }

    private int indiceRastro(int seg) { // o rastro e gravado por quadro: acha o ponto a (seg+1)*PASSO de distancia percorrida
        double alvo = (seg + 1) * PASSO, d = 0;
        for (int i = 1; i < rastro.size(); i++) { double[] a = rastro.get(i - 1), b = rastro.get(i); d += Math.hypot(a[0] - b[0], a[1] - b[1]); if (d >= alvo) return i; }
        return rastro.size() - 1;
    }
    private double[] posSeg(int i) { return rastro.get(indiceRastro(i)); }
    private double angSeg(int i) { int k = indiceRastro(i); double[] a = rastro.get(Math.max(0, k - 3)), b = rastro.get(Math.min(rastro.size() - 1, k + 3)); return Math.atan2(a[1] - b[1], a[0] - b[0]); }

    Rectangle cabeca() { return new Rectangle((int) hx - 44, (int) hy - 38, 88, 76); }
    private Rectangle seg(int i) { double[] p = posSeg(i); return new Rectangle((int) p[0] - 36, (int) p[1] - 34, 72, 68); }
    Rectangle bounds() { return cabeca(); }

    /** Tiro do jogador: segmento vivo segura (e perde vida); a cabeca e o alvo do chefe. */
    boolean colide(Rectangle r) {
        if (r.intersects(cabeca())) return true;
        for (int i = 0; i < SEGMENTOS; i++) if (vidaSeg[i] > 0 && r.intersects(seg(i))) { bloqueou = true; ultimoSegAtingido = i; return true; }
        return false;
    }
    /** true se o acerto foi num segmento (o dano vai para ele, nao para o chefe). */
    boolean consumirBloqueio() {
        if (!bloqueou) return false; bloqueou = false;
        int i = ultimoSegAtingido; if (i >= 0 && vidaSeg[i] > 0) { flashSeg[i] = .14; if (--vidaSeg[i] == 0) SomJogo.tocar(SomJogo.Efeito.EXPLOSAO); }
        return true;
    }
    void ferido() { flash = .16; }
    /** Encosto: a cabeca na mordida machuca uma vez; o corpo machuca a cada segundo. */
    boolean contato(Rectangle nave) {
        if (mordida > 0 && !acertou && nave.intersects(cabeca())) { acertou = true; return true; }
        if (esperaContato > 0) return false;
        for (int i = 0; i < SEGMENTOS; i++) if (vidaSeg[i] > 0 && nave.intersects(seg(i))) { esperaContato = 1; return true; }
        return false;
    }
    int getVidaSegmentosTeste() { int s = 0; for (int v : vidaSeg) s += Math.max(0, v); return s; }

    void desenhar(Graphics2D tela) {
        Graphics2D g = (Graphics2D) tela.create();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        // da cauda para a cabeca, para cada parte cobrir a de tras
        int ultimo = SEGMENTOS;
        double[] pc = posSeg(ultimo); peca(g, CAUDA, pc[0], pc[1], angSeg(ultimo), .4, 0, 1f);
        for (int i = SEGMENTOS - 1; i >= 0; i--) {
            double[] p = posSeg(i); double a = angSeg(i);
            if (vidaSeg[i] <= 0) { // segmento destruido: carcaca escura e fumaca
                Graphics2D c = (Graphics2D) g.create(); c.setComposite(AlphaComposite.SrcOver.derive(.45f)); peca(c, SEGMENTO[0], p[0], p[1], a, .5, 0, 1f); c.dispose();
                g.setColor(new Color(80, 80, 80, 120)); int r = 14 + (int) ((tempo * 30 + i * 7) % 12); g.fillOval((int) p[0] - r, (int) p[1] - r - 20, r * 2, r * 2);
                continue;
            }
            int q = (int) (tempo * 8 + i) % 2;
            peca(g, SEGMENTO[q], p[0], p[1], a, .5, flashSeg[i], 1f);
        }
        int qc = mordida > .55 ? 2 : grito > 0 || enrolar > 0 ? 1 : 0;
        peca(g, CABECA[qc], hx, hy, ang, .62, flash, 1f);
        if (mordida > 1.2) { g.setColor(new Color(255, 60, 30, (int) (100 + 80 * Math.sin(tempo * 30)))); g.setStroke(new java.awt.BasicStroke(3)); g.drawLine((int) hx, (int) hy, (int) alvoX, (int) alvoY); }
        g.dispose();
    }
    /** Desenha a peca centrada em (x, y), girada para a direcao do corpo (a arte aponta para a esquerda). */
    private static void peca(Graphics2D tela, BufferedImage img, double x, double y, double a, double ancoraX, double flash, float alfa) {
        if (img == null) return;
        Graphics2D g = (Graphics2D) tela.create();
        int w = (int) (img.getWidth() * ESC), h = (int) (img.getHeight() * ESC);
        g.translate(x, y); g.rotate(a - Math.PI);
        boolean deCabeca = Math.cos(a - Math.PI) < 0;           // indo para a direita: espelha na vertical para nao ficar de ponta-cabeca
        if (deCabeca) g.scale(1, -1);
        g.drawImage(img, (int) (-w * ancoraX), -h / 2, w, h, null);
        if (flash > 0) { g.setComposite(AlphaComposite.SrcOver.derive((float) Math.min(.8, flash * 6))); g.drawImage(clara(img), (int) (-w * ancoraX), -h / 2, w, h, null); }
        g.dispose();
    }
    private static final java.util.Map<BufferedImage, BufferedImage> CLARAS = new java.util.HashMap<>();
    /** Versao clareada da peca (piscar ao levar tiro), so onde ha desenho. */
    private static BufferedImage clara(BufferedImage img) {
        return CLARAS.computeIfAbsent(img, i -> new java.awt.image.RescaleOp(new float[]{1.6f, 1.6f, 1.6f, 1f}, new float[]{70, 70, 70, 0}, null).filter(i, null));
    }
    private static BufferedImage ler(String n) {
        java.net.URL u = CentopeiaKrovax.class.getResource("/ImagemIcon/" + n);
        if (u == null) return null;
        try { return javax.imageio.ImageIO.read(u); } catch (java.io.IOException e) { return null; }
    }
}
