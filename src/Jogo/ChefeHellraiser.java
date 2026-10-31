package Jogo;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.List;

/**
 * Setor Hellraiser, obrigatorio depois dos oito chefes: Ozzy e Lemmy voltam juntos.
 * Ato I: a van atropela tudo (a nave tem que desviar). Ato II: os dois lutam ao mesmo tempo,
 * cada um com a propria vida. Ato III: Ozzy vira dragao (com cutscene antes).
 */
final class ChefeHellraiser {
    static final int VAN = 1, DUO = 2, DRAGAO = 3;
    private static final BufferedImage ARTE_VAN = ler("hellraiser_van.png"), ARTE_DRAGAO = ler("ozzy_dragao.png");
    private static final int W = Config.LARGURA, H = Config.ALTURA, TOPO = Config.HUD;

    private int ato = VAN;
    private final int vidaVan, vidaDuo, vidaDragao;
    private int vida, vidaOzzy, vidaLemmy;
    private double tempo, entrada, flash, intervalo = 1.5, transicao;
    private String cenaPendente;
    private int rajadas;
    /** Quem levou o ultimo tiro no ato II (0 Ozzy, 1 Lemmy), escolhido em colide(). */
    private int alvoDuo = -1;

    // van
    private final int vanH = 200, vanW;
    private double vx = W + 40, vy, vAlvoY, aviso, proximaInvestida = 3.2, faixaTravada, percorrido, ultimoRastro;
    private boolean investindo, voltando;
    /** Manobras da van: rasante reto com pista em chamas, zigue-zague, volta por tras e chuva de barris. */
    private static final int RASANTE = 0, ZIGUE = 1, POR_TRAS = 2, BARRIS = 3;
    private static final int[] ROTEIRO = {RASANTE, BARRIS, ZIGUE, BARRIS, RASANTE, POR_TRAS, BARRIS, ZIGUE};
    private int manobra = RASANTE, passoRoteiro, barrisFaltando;
    private double sentido = -1, proximoBarril;
    private final java.util.List<TiroChefe> barris = new java.util.ArrayList<>();
    // dupla: cada um com posicao propria; trocam de lugar em arco
    private final double[] px = new double[2], py = new double[2];
    private double troca = 6, trocando, gesto;
    private int trocas;
    private static final int DUO_W = 290, DUO_H = 270;
    // dragao
    private final int dragH = 340, dragW;
    private double dx = W + 40, dy, carga, sopro, garra, proximoGolpe = 2.2, direcaoSopro;
    private double garraX, garraY;
    private int golpesDragao;

    ChefeHellraiser(Dificuldade dif) {
        double r = dif.resistenciaChefe;
        vidaVan = (int) Math.round(260 * r); vidaDuo = (int) Math.round(170 * r); vidaDragao = (int) Math.round(430 * r);
        vida = vidaVan;
        vanW = ARTE_VAN != null ? ARTE_VAN.getWidth() / 4 * vanH / ARTE_VAN.getHeight() : 280;
        dragW = ARTE_DRAGAO != null ? ARTE_DRAGAO.getWidth() / 4 * dragH / ARTE_DRAGAO.getHeight() : 300;
        vy = vAlvoY = TOPO + (H - TOPO - vanH) / 2.0;
        dy = TOPO + (H - TOPO - dragH) / 2.0;
        Sprites16Bits.preparar(1, 2); Sprites16Bits.preparar(4, 2);
    }

    int ato() { return ato; }
    String consumirCena() { String c = cenaPendente; cenaPendente = null; return c; }
    double getEntrada() { return entrada; }

    void atualizar(double dt, int naveX, int naveY, Dificuldade dif, List<TiroChefe> tiros) {
        if (dt <= 0) return;
        tempo += dt; flash = Math.max(0, flash - dt); gesto = Math.max(0, gesto - dt);
        if (transicao > 0) { transicao = Math.max(0, transicao - dt); if (ato == DUO) posicionarDuo(dt, true); return; }
        double cad = dif.cadenciaInimigos;
        if (ato == VAN) van(dt, naveX, naveY, cad, tiros);
        else if (ato == DUO) duo(dt, naveX, naveY, cad, tiros);
        else dragao(dt, naveX, naveY, cad, tiros);
    }

    // ---------------- ato I: a van ----------------
    private double baseVan() { return W - vanW - 30; }
    private double faixaVan(double naveY) { return Math.max(TOPO + 12, Math.min(H - vanH - 8, naveY - vanH * .5)); }
    private void van(double dt, int naveX, int naveY, double cad, List<TiroChefe> tiros) {
        boolean furia = vida < vidaVan * .4;
        estourarBarris(tiros);
        if (investindo) {
            double vel = (furia ? 1150 : 980) * (manobra == ZIGUE ? .72 : 1);
            vx += sentido * vel * dt; percorrido += vel * dt;
            if (manobra == ZIGUE) vy = Math.max(TOPO + 6, Math.min(H - vanH - 6, faixaTravada + Math.sin(percorrido / (W + vanW) * Math.PI * 3) * 150));
            // pneus em chamas: a pista fica pegando fogo atras da van
            if (manobra != ZIGUE && percorrido - ultimoRastro > 34) { ultimoRastro = percorrido;
                for (int r = 0; r < 2; r++) tiros.add(new TiroChefe(vx + vanW * (sentido < 0 ? .3 + r * .45 : .7 - r * .45), vy + vanH * .86, 0, 0, false, TiroChefe.RASTRO, TiroChefe.Movimento.RETO)); }
            boolean saiu = sentido < 0 ? vx < -vanW - 30 : vx > W + 30;
            if (!saiu) return;
            investindo = false;
            // na furia o rasante emenda direto na volta por tras
            if (furia && manobra == RASANTE) { iniciarManobra(POR_TRAS, naveY); return; }
            sentido = -1; voltando = true; vx = W + 40; vy = vAlvoY = faixaVan(naveY);
            return;
        }
        if (aviso > 0) {
            aviso = Math.max(0, aviso - dt); vx += Math.sin(tempo * 60) * 1.5;
            if (aviso == 0) { investindo = true; percorrido = ultimoRastro = 0; SomJogo.tocar(SomJogo.Efeito.CHEFE_ENTRADA); }
            return;
        }
        if (vx > baseVan()) vx = Math.max(baseVan(), vx - (voltando ? 700 : 520) * dt);
        else voltando = false;
        if (!voltando) entrada = Math.min(1, entrada + dt / 1.3);
        vAlvoY += (faixaVan(naveY) - vAlvoY) * Math.min(1, dt * .9);
        vy += (vAlvoY + Math.sin(tempo * 2.2) * 40 - vy) * Math.min(1, dt * 2.5);
        if (entrada < 1 || voltando) return;
        if (barrisFaltando > 0) {
            // Lemmy joga os barris pela janela, um atras do outro, em arco
            proximoBarril -= dt;
            if (proximoBarril <= 0) {
                barrisFaltando--; proximoBarril = furia ? .3 : .42; gesto = .4;
                double ox = vx + vanW * .35, oy = vy + vanH * .3, alcance = Math.max(80, ox - naveX - 22 + (barrisFaltando - 1) * 90);
                double sobe = (naveY + 18 - oy) / 1.05 - .5 * 560 * 1.05; // cai em cima da nave quando estoura
                TiroChefe b = new TiroChefe(ox, oy, -alcance / 1.05, sobe, true, TiroChefe.BARRIL, TiroChefe.Movimento.ARCO);
                tiros.add(b); barris.add(b);
            }
            return;
        }
        proximaInvestida -= dt * cad * (furia ? 1.35 : 1);
        if (proximaInvestida <= 0) {
            int m = ROTEIRO[passoRoteiro++ % ROTEIRO.length];
            proximaInvestida = m == BARRIS ? 2.6 : 3.6;
            if (m == BARRIS) { barrisFaltando = furia ? 4 : 3; proximoBarril = .2; }
            else iniciarManobra(m, naveY);
            return;
        }
        intervalo -= dt * cad * (furia ? 1.3 : 1);
        if (intervalo > 0 || tiros.size() > 46) return;
        rajadas++; gesto = .4;
        double ox = vx + vanW * .3, oy = vy + vanH * .4, mira = Math.atan2(naveY + 18 - oy, naveX + 22 - ox);
        if (rajadas % 2 == 0) for (int i = -2; i <= 2; i++) tiro(tiros, ox, oy, mira + i * .2, 240, i == 0, 1, TiroChefe.Movimento.PULSANTE);
        else for (int i = -3; i <= 3; i++) tiro(tiros, ox + vanW * .2, oy, Math.PI + i * .17, 190, i % 3 == 0, 4, TiroChefe.Movimento.ONDA);
        intervalo = 1.45;
    }
    private void iniciarManobra(int m, int naveY) {
        manobra = m; aviso = m == POR_TRAS ? 1.0 : .85;
        faixaTravada = faixaVan(naveY); vy = vAlvoY = faixaTravada;
        if (m == POR_TRAS) { sentido = 1; vx = -vanW - 30; SomJogo.tocar(SomJogo.Efeito.ALERTA); } // buzina antes de vir por tras
        else sentido = -1;
    }
    /** Barril estoura no ar ou ao cair: oito labaredas para todos os lados. */
    private void estourarBarris(List<TiroChefe> tiros) {
        for (java.util.Iterator<TiroChefe> it = barris.iterator(); it.hasNext();) {
            TiroChefe b = it.next();
            if (!b.isVisible()) { it.remove(); continue; }
            if (b.idade() < 1.05 && b.getY() < H - 24) continue;
            b.setVisible(false); it.remove();
            for (int k = 0; k < 8; k++) tiros.add(new TiroChefe(b.getX(), b.getY(), Math.cos(k * Math.PI / 4) * 210, Math.sin(k * Math.PI / 4) * 210, false, 2, TiroChefe.Movimento.RETO));
        }
    }

    // ---------------- ato II: Ozzy e Lemmy juntos ----------------
    private void posicionarDuo(double dt, boolean chegando) {
        double t = trocando > 0 ? 1 - trocando / 1.2 : 0;
        for (int k = 0; k < 2; k++) {
            int lugar = (trocas + k) % 2; // 0 em cima, 1 embaixo
            double ax = W - DUO_W - 10 - (lugar == 0 ? 0 : 150), ay = lugar == 0 ? TOPO + 6 : H - DUO_H - 6;
            ax += Math.sin(tempo * (1.1 + k * .3) + k) * 26; ay += Math.sin(tempo * (1.4 + k * .2)) * 22;
            if (trocando > 0) ax -= Math.sin(t * Math.PI) * (k == 0 ? 160 : -60);
            if (chegando) { px[k] += (ax - px[k]) * Math.min(1, dt * 3); py[k] += (ay - py[k]) * Math.min(1, dt * 3); }
            else { px[k] += (ax - px[k]) * Math.min(1, dt * 4); py[k] += (ay - py[k]) * Math.min(1, dt * 4); }
        }
    }
    private void duo(double dt, int naveX, int naveY, double cad, List<TiroChefe> tiros) {
        entrada = 1;
        boolean sozinho = vidaOzzy <= 0 || vidaLemmy <= 0;
        if (trocando > 0) trocando = Math.max(0, trocando - dt);
        troca -= dt;
        if (troca <= 0 && !sozinho) { troca = 6; trocando = 1.2; trocas++; }
        posicionarDuo(dt, false);
        intervalo -= dt * cad * (sozinho ? 1.55 : 1);
        if (intervalo > 0 || tiros.size() > 46) return;
        rajadas++; gesto = .42;
        int quem = vidaOzzy <= 0 ? 1 : vidaLemmy <= 0 ? 0 : rajadas % 2;
        double ox = px[quem] + 40, oy = py[quem] + DUO_H * .45, mira = Math.atan2(naveY + 18 - oy, naveX + 22 - ox);
        if (quem == 0) {
            // Ozzy: leque de ondas sonoras com morcegos
            for (int i = -3; i <= 3; i++) tiro(tiros, ox, oy, mira + i * .15, 270, i == 0, 1, TiroChefe.Movimento.PULSANTE);
        } else {
            // Lemmy: graves ondulando e, sozinho, uma parede de baixo com um buraco
            for (int i = -3; i <= 3; i++) tiro(tiros, ox, oy, Math.PI + i * .16, 200, i % 3 == 0, 4, TiroChefe.Movimento.ONDA);
            if (sozinho) { int buraco = (naveY - TOPO) / 60; for (int j = 0; j < 9; j++) if (Math.abs(j - buraco) > 1) tiro(tiros, W + 10, TOPO + 30 + j * 60, Math.PI, 230, false, 4, TiroChefe.Movimento.RETO); }
        }
        intervalo = 1.15;
    }
    private Rectangle corpoDuo(int k) { return new Rectangle((int) px[k] + 70, (int) py[k] + 30, DUO_W - 140, DUO_H - 50); }

    // ---------------- ato III: o dragao ----------------
    private double bocaX() { return dx + dragW * .18; }
    private double bocaY() { return dy + dragH * .41; }
    private static final double ALCANCE_SOPRO = 1000;
    private void dragao(double dt, int naveX, int naveY, double cad, List<TiroChefe> tiros) {
        boolean furia = vida < vidaDragao * .35;
        double baseX = W - dragW - 10, baseY = Math.max(TOPO, Math.min(H - dragH, naveY - dragH * .4));
        if (entrada < 1) { entrada = Math.min(1, entrada + dt / 1.2); dx = W + 40 - (W + 40 - baseX) * (1 - Math.pow(1 - entrada, 3)); return; }
        if (garra > 0) {
            // garra: mergulha ate o ponto travado e volta
            garra = Math.max(0, garra - dt); double p = 1 - garra / .9, arco = Math.sin(p * Math.PI);
            dx = baseX + (garraX - baseX) * arco; dy += (garraY - dy) * Math.min(1, dt * 5) * arco;
            return;
        }
        dx += (baseX + Math.sin(tempo * .9) * 30 - dx) * Math.min(1, dt * 2);
        if (carga <= 0 && sopro <= 0) dy += (baseY + Math.sin(tempo * 1.3) * 34 - dy) * Math.min(1, dt * 1.2);
        if (carga > 0) {
            carga = Math.max(0, carga - dt);
            if (carga == 0) { sopro = furia ? 2.0 : 1.6; direcaoSopro = Math.atan2(naveY + 18 - bocaY(), naveX + 22 - bocaX()); }
            else direcaoSopro = Math.atan2(naveY + 18 - bocaY(), naveX + 22 - bocaX());
            return;
        }
        if (sopro > 0) {
            // lanca-chamas: o jato sai da boca direto na nave e vai virando atras dela, mais devagar que a nave
            sopro = Math.max(0, sopro - dt);
            double alvo = Math.atan2(naveY + 18 - bocaY(), naveX + 22 - bocaX()), dif = Math.atan2(Math.sin(alvo - direcaoSopro), Math.cos(alvo - direcaoSopro));
            double giro = (furia ? .62 : .45) * dt;
            direcaoSopro += Math.max(-giro, Math.min(giro, dif));
            if (sopro == 0) intervalo = .8;
            return;
        }
        proximoGolpe -= dt * cad * (furia ? 1.3 : 1);
        if (proximoGolpe > 0 || tiros.size() > 50) return;
        golpesDragao++; gesto = .4;
        if (golpesDragao % 3 == 1) carga = .9;
        else if (golpesDragao % 3 == 2) { garra = .9; garraX = Math.max(40, naveX - dragW * .25); garraY = Math.max(TOPO, Math.min(H - dragH, naveY - dragH * .45)); }
        else {
            double m = Math.atan2(naveY + 18 - bocaY(), naveX + 22 - bocaX());
            for (int i = -2; i <= 2; i++) tiro(tiros, bocaX(), bocaY(), m + i * .22, 300, true, TiroChefe.FOGO_ROXO, TiroChefe.Movimento.ACELERADO);
        }
        proximoGolpe = furia ? 1.5 : 2.1;
    }

    private void tiro(List<TiroChefe> tiros, double x0, double y0, double a, double v, boolean pesado, int estilo, TiroChefe.Movimento m) {
        tiros.add(new TiroChefe(x0, y0, Math.cos(a) * v, Math.sin(a) * v, pesado, estilo, m));
    }

    // ---------------- dano e colisao ----------------
    /** Acerto pontual: no ato II guarda quem foi atingido para o dano cair no certo. */
    boolean colide(Rectangle r) {
        if (ato != DUO) return r.intersects(getBounds());
        for (int k = 0; k < 2; k++) if ((k == 0 ? vidaOzzy : vidaLemmy) > 0 && r.intersects(corpoDuo(k))) { alvoDuo = k; return true; }
        return false;
    }
    boolean receberDano(int dano) {
        if (entrada < 1 && ato != DUO || transicao > 0 || cenaPendente != null) return false;
        dano = Math.max(0, dano); flash = .16;
        if (ato == DUO) {
            int k = alvoDuo >= 0 ? alvoDuo : vidaOzzy >= vidaLemmy ? 0 : 1;
            if (k == 0 && vidaOzzy <= 0) k = 1; else if (k == 1 && vidaLemmy <= 0) k = 0;
            alvoDuo = -1;
            if (k == 0) vidaOzzy = Math.max(0, vidaOzzy - dano); else vidaLemmy = Math.max(0, vidaLemmy - dano);
            if (vidaOzzy <= 0 && vidaLemmy <= 0) {
                // Lemmy cai, Ozzy se recusa a perder: cutscene do dragao
                ato = DRAGAO; vida = vidaDragao; entrada = 0; cenaPendente = CenaChefe.DRAGAO; proximoGolpe = 2.4; intervalo = 1;
            }
            return false;
        }
        vida = Math.max(0, vida - dano);
        if (vida > 0) return false;
        if (ato == VAN) {
            ato = DUO; vidaOzzy = vidaLemmy = vidaDuo; transicao = 1.3; intervalo = 1.6; rajadas = 0; investindo = false; aviso = 0;
            for (int k = 0; k < 2; k++) { px[k] = vx + vanW * .3; py[k] = vy; }
            return false;
        }
        return true;
    }
    boolean estaVivo() { return ato != DRAGAO || vida > 0; }
    int getVida() { return ato == DUO ? vidaOzzy + vidaLemmy : vida; }
    int getVidaMaxima() { return ato == VAN ? vidaVan : ato == DUO ? vidaDuo * 2 : vidaDragao; }
    String getNome() { return ato == VAN ? "HELLRAISER - A VAN DO INFERNO" : ato == DUO ? "OZZY + LEMMY - HELLRAISER" : "OZZY - O DRAGAO"; }
    Rectangle getBounds() {
        if (ato == VAN) return new Rectangle((int) vx + 16, (int) vy + vanH / 4, vanW - 40, vanH * 3 / 5);
        if (ato == DUO) {
            Rectangle r = null;
            for (int k = 0; k < 2; k++) if ((k == 0 ? vidaOzzy : vidaLemmy) > 0 || transicao > 0) r = r == null ? corpoDuo(k) : r.union(corpoDuo(k));
            return r != null ? r : corpoDuo(0);
        }
        return new Rectangle((int) (dx + dragW * .2), (int) (dy + dragH * .14), (int) (dragW * .62), (int) (dragH * .78));
    }
    /** Contato com o corpo: no ato II so conta quem esta de pe. */
    boolean tocaNave(Rectangle nave) {
        if (ato == DRAGAO && sopro > 0 && naChama(nave.getCenterX(), nave.getCenterY())) return true;
        if (ato != DUO) return nave.intersects(getBounds());
        for (int k = 0; k < 2; k++) if ((k == 0 ? vidaOzzy : vidaLemmy) > 0 && nave.intersects(corpoDuo(k))) return true;
        return false;
    }
    int getX() { return getBounds().x; }
    int getY() { return getBounds().y; }

    // ---------------- desenho ----------------
    void desenhar(Graphics2D tela) {
        Graphics2D g = (Graphics2D) tela.create();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        if (ato == VAN) desenharVan(g);
        else if (ato == DUO) {
            if (transicao > 0) { Graphics2D t = (Graphics2D) g.create(); t.setComposite(AlphaComposite.SrcOver.derive((float) (transicao / 1.3))); desenharVan(t); t.dispose(); }
            float a = (float) (transicao > 0 ? 1 - transicao / 1.3 : 1);
            for (int k = 0; k < 2; k++) {
                int vidaK = k == 0 ? vidaOzzy : vidaLemmy;
                if (vidaK <= 0 && transicao <= 0) continue;
                Graphics2D q = (Graphics2D) g.create(); q.setComposite(AlphaComposite.SrcOver.derive(a));
                q.translate((int) px[k], (int) py[k]);
                boolean atirando = gesto > 0 && (rajadas % 2 == k || vidaOzzy <= 0 || vidaLemmy <= 0);
                Sprites16Bits.desenhar(q, k == 0 ? 1 : 4, 2, DUO_W, DUO_H, tempo + k, atirando ? gesto / .42 : 0);
                q.dispose();
            }
        } else desenharDragao(g);
        if (flash > 0) {
            g.setColor(new Color(255, 200, 150, (int) (flash / .16 * 200)));
            Rectangle b = getBounds();
            for (int i = 0; i < 6; i++) { double an = i * Math.PI / 3; int r = 15 + (int) ((.16 - flash) * 180); g.fillRect(b.x + 30 + (int) (Math.cos(an) * r), (int) b.getCenterY() + (int) (Math.sin(an) * r), 4, 4); }
        }
        g.dispose();
    }
    private void desenharVan(Graphics2D g) {
        int x = (int) vx, y = (int) vy + ((int) (tempo * 16) % 2);
        // sem faixa na tela: o aviso e a propria van tremendo no turbo (quadro 1)
        int q = investindo ? 2 : aviso > 0 ? 1 : vida < vidaVan * .4 ? 3 : (int) (tempo * 6) % 4 == 0 ? 1 : 0;
        boolean virada = sentido > 0;
        if (ARTE_VAN != null) {
            int fw = ARTE_VAN.getWidth() / 4;
            if (virada) g.drawImage(ARTE_VAN, x + vanW, y, x, y + vanH, q * fw, 0, q * fw + fw, ARTE_VAN.getHeight(), null);
            else g.drawImage(ARTE_VAN, x, y, x + vanW, y + vanH, q * fw, 0, q * fw + fw, ARTE_VAN.getHeight(), null);
        } else { g.setColor(new Color(30, 30, 34)); g.fillRoundRect(x, y + 40, vanW, vanH - 70, 20, 20); }
        if (investindo) for (int k = 0; k < 10; k++) { g.setColor(new Color(255, 150, 40, 160 - k * 14)); int lx = virada ? x - 44 - k * 20 : x + vanW + k * 20; g.fillRect(lx, y + vanH / 3 + (k % 3) * 30, 44, 3); }
    }
    /** O jato engrossa com a distancia: pega a nave se o centro dela cair dentro do cone. */
    private boolean naChama(double cx, double cy) {
        double ax = cx - bocaX(), ay = cy - bocaY(), ux = Math.cos(direcaoSopro), uy = Math.sin(direcaoSopro);
        double ao_longo = ax * ux + ay * uy; if (ao_longo < 0 || ao_longo > ALCANCE_SOPRO) return false;
        return Math.abs(ax * uy - ay * ux) < 14 + ao_longo * .06 + 14;
    }
    private void desenharSopro(Graphics2D tela) {
        Graphics2D g = (Graphics2D) tela.create();
        double bx = bocaX(), by = bocaY(), ux = Math.cos(direcaoSopro), uy = Math.sin(direcaoSopro);
        double entra = Math.min(1, (sopro > 0 ? 1 : 0) * 1), fim = Math.min(1, sopro / .25);
        java.util.Random r = new java.util.Random((long) (tempo * 30));
        // camadas: borda violeta larga, meio rosa, miolo branco; bolhas de fogo tremendo ao longo do jato
        for (int camada = 0; camada < 3; camada++) {
            Color c = camada == 0 ? new Color(120, 30, 200, (int) (150 * fim)) : camada == 1 ? new Color(220, 100, 255, (int) (200 * fim)) : new Color(255, 235, 255, (int) (230 * fim));
            g.setColor(c);
            for (double d = 0; d < ALCANCE_SOPRO; d += 14) {
                double larg = (14 + d * .06) * (camada == 0 ? 1.25 : camada == 1 ? .8 : .35) * entra;
                double tremor = (r.nextDouble() - .5) * larg * .5;
                int cx = (int) (bx + ux * d - uy * tremor), cy = (int) (by + uy * d + ux * tremor), rr = (int) (larg * (.8 + r.nextDouble() * .4));
                g.fillOval(cx - rr, cy - rr, rr * 2, rr * 2);
            }
        }
        for (int k = 0; k < 30; k++) {
            double d = r.nextDouble() * ALCANCE_SOPRO, lado = (r.nextDouble() - .5) * (40 + d * .14);
            g.setColor(new Color(255, 180, 255, (int) (200 * fim))); g.fillRect((int) (bx + ux * d - uy * lado), (int) (by + uy * d + ux * lado), 4, 4);
        }
        g.dispose();
    }
    private void desenharDragao(Graphics2D g) {
        int x = (int) dx, y = (int) dy;
        int q = carga > 0 || sopro > 0 ? 1 : garra > 0 ? 3 : 0;
        if (ARTE_DRAGAO != null) {
            int fw = ARTE_DRAGAO.getWidth() / 4;
            g.drawImage(ARTE_DRAGAO, x, y, x + dragW, y + dragH, q * fw, 0, q * fw + fw, ARTE_DRAGAO.getHeight(), null);
        } else { g.setColor(new Color(60, 20, 80)); g.fillOval(x + 40, y + 40, dragW - 80, dragH - 80); }
        if (sopro > 0) desenharSopro(g);
        if (carga > 0) {
            // aviso do sopro: so a boca brilhando e crescendo
            int r = (int) (10 + (.9 - carga) * 30 + Math.sin(tempo * 40) * 3);
            g.setColor(new Color(220, 120, 255, 150)); g.fillOval((int) bocaX() - r, (int) bocaY() - r, r * 2, r * 2);
        }
    }

    void desenharVida(Graphics2D g) {
        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14)); g.setColor(Color.WHITE);
        String atoTxt = ato == VAN ? "  [ATO I]" : ato == DUO ? "  [ATO II]" : "  [ATO III]";
        if (ato == DUO) {
            g.drawString("OZZY" + atoTxt, 217, TOPO + 24); g.drawString("LEMMY", 487, TOPO + 24);
            barra(g, 218, 256, vidaOzzy, vidaDuo, new Color(179, 68, 219));
            barra(g, 488, 256, vidaLemmy, vidaDuo, new Color(255, 154, 65));
            return;
        }
        g.drawString(getNome() + atoTxt, 217, TOPO + 24);
        barra(g, 218, 526, vida, getVidaMaxima(), ato == VAN ? new Color(255, 120, 40) : new Color(170, 60, 230));
    }
    private static void barra(Graphics2D g, int x, int w, int v, int max, Color c) {
        g.setColor(new Color(8, 22, 34, 222)); g.fillRoundRect(x, TOPO + 31, w, 22, 10, 10);
        g.setPaint(new GradientPaint(x, TOPO + 32, c, x + w, TOPO + 52, new Color(222, 36, 70)));
        g.fillRoundRect(x + 2, TOPO + 33, (int) ((w - 4.0) * Math.max(0, v) / Math.max(1, max)), 18, 8, 8);
        g.setColor(new Color(255, 255, 255, 130)); g.drawRoundRect(x, TOPO + 31, w, 22, 10, 10);
    }

    private static BufferedImage ler(String n) {
        java.net.URL u = ChefeHellraiser.class.getResource("/ImagemIcon/" + n);
        if (u == null) return null;
        try { return javax.imageio.ImageIO.read(u); } catch (java.io.IOException e) { return null; }
    }
}
