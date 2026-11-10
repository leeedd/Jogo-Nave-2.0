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
    private static final BufferedImage ARTE_VAN = ler("hellraiser_van.png"), VAN_GANCHO = ler("hellraiser_van_gancho.png"), VAN_AMPS = ler("hellraiser_van_amps.png"),
            GANCHO_ABERTO = ler("hellraiser_gancho_aberto.png"), GANCHO_FECHADO = ler("hellraiser_gancho_fechado.png"), ELO = ler("hellraiser_corrente_elo.png"), ARTE_DRAGAO = ler("ozzy_dragao.png");
    private static final int W = Config.LARGURA, H = Config.ALTURA, TOPO = Config.HUD;

    private int ato = VAN;
    private final int vidaVan, vidaDuo, vidaDragao;
    private int vida, vidaOzzy, vidaLemmy;
    private double tempo, entrada, flash, intervalo = 1.5, transicao;
    private String cenaPendente;
    private int rajadas;
    /** Quem levou o ultimo tiro no ato II (0 Ozzy, 1 Lemmy), escolhido em colide(). */
    private int alvoDuo = -1;

    // van: as tres pranchas tem celulas de 700x380 (paredao 700x470) com a caveira da lateral no mesmo ponto
    private static final double E = .62;
    private final int vanW = (int) (700 * E), vanH = (int) (380 * E);
    private double vx = W + 40, vy, vAlvoY, aviso, proximaManobra = 2.6, percorrido, ultimoRastro;
    private boolean investindo, voltando;
    /** Manobras: paredao de caixas (empurra), gancho (puxa) e, so na furia, o rasante. */
    private static final int NADA = 0, PAREDAO = 1, GANCHO = 2, RASANTE = 3;
    private int manobra = NADA, passoRoteiro;
    private double fase;
    private int ondasFaltando;
    // gancho: 1 mirando, 2 voando, 3 preso na nave, 4 recolhendo
    private int gancho;
    private double hx, hy, hvx, hvy, tempoPreso, faiscaCorrente;
    private int vidaCorrente;
    private boolean acertouCorrente;
    /** Golpe do gancho para a Fase aplicar na nave (espinhos do para-choque) e brilho do impacto. */
    private int danoGancho;
    private double impactoGancho, batidaX, batidaY, batida;
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
        dragW = ARTE_DRAGAO != null ? ARTE_DRAGAO.getWidth() / 4 * dragH / ARTE_DRAGAO.getHeight() : 300;
        vy = vAlvoY = TOPO + (H - TOPO) / 2.0 - 137 * E;
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
    private double baseVan() { return W - 470 * E - 20; }
    private double faixaVan(double naveY) { return Math.max(TOPO - 60 * E, Math.min(H - 330 * E, naveY + 18 - 137 * E)); }
    private double bocaArpaoX() { return vx + 145 * E; }
    private double bocaArpaoY() { return vy + 248 * E; }
    private void van(double dt, int naveX, int naveY, double cad, List<TiroChefe> tiros) {
        boolean furia = vida < vidaVan * .4;
        faiscaCorrente = Math.max(0, faiscaCorrente - dt); impactoGancho = Math.max(0, impactoGancho - dt); batida = Math.max(0, batida - dt);
        atualizarGancho(dt, naveX, naveY);
        if (investindo) {
            // rasante da furia: atravessa a tela e os pneus deixam a pista pegando fogo
            double vel = 1150; vx -= vel * dt; percorrido += vel * dt;
            if (percorrido - ultimoRastro > 34) { ultimoRastro = percorrido;
                for (int r = 0; r < 2; r++) tiros.add(new TiroChefe(vx + (190 + r * 190) * E, vy + 330 * E, 0, 0, false, TiroChefe.RASTRO, TiroChefe.Movimento.RETO)); }
            if (vx < -vanW) { investindo = false; voltando = true; manobra = NADA; vx = W + 40; vy = vAlvoY = faixaVan(naveY); }
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
        // presa no gancho, a van segura a faixa; no resto do tempo persegue a nave devagar
        if (gancho != 3) vAlvoY += (faixaVan(naveY) - vAlvoY) * Math.min(1, dt * .9);
        vy += (vAlvoY + (gancho == 0 ? Math.sin(tempo * 2.2) * 30 : 0) - vy) * Math.min(1, dt * 2.5);
        if (entrada < 1 || voltando) return;
        if (manobra == PAREDAO) { paredao(dt, furia, tiros); return; }
        if (manobra == GANCHO) { if (gancho == 0) manobra = NADA; return; }
        proximaManobra -= dt * cad * (furia ? 1.3 : 1);
        if (proximaManobra <= 0) {
            int m = furia && passoRoteiro % 3 == 2 ? RASANTE : passoRoteiro % 2 == 0 ? PAREDAO : GANCHO;
            passoRoteiro++; manobra = m; proximaManobra = furia ? 1.6 : 2.2;
            if (m == PAREDAO) { fase = 0; ondasFaltando = furia ? 5 : 4; }
            else if (m == GANCHO) { gancho = 1; fase = 0; }
            else { aviso = .7; vy = vAlvoY = faixaVan(naveY); }
            return;
        }
        intervalo -= dt * cad * (furia ? 1.3 : 1);
        if (intervalo > 0 || tiros.size() > 46) return;
        rajadas++; gesto = .4;
        double ox = vx + 155 * E, oy = vy + 105 * E, mira = Math.atan2(naveY + 18 - oy, naveX + 22 - ox);
        for (int i = -2; i <= 2; i++) tiro(tiros, ox, oy, mira + i * .2, 240, false, TiroChefe.MORCEGO_OZZY, TiroChefe.Movimento.PULSANTE);
        intervalo = 1.6;
    }
    /** Paredao: o teto abre, as Marshall sobem e o Lemmy toca; cada nota e uma onda que empurra a nave. */
    private void paredao(double dt, boolean furia, List<TiroChefe> tiros) {
        fase += dt;
        if (fase < .55) return; // teto abrindo (quadro 1)
        if (ondasFaltando > 0) {
            intervalo -= dt;
            if (intervalo <= 0) {
                ondasFaltando--; intervalo = furia ? .42 : .55; gesto = .3;
                double ox = vx + 364 * E, oy = vy + 104 * E;
                for (int i = -1; i <= 1; i++) tiros.add(new TiroChefe(ox, oy + 40 + i * 62 + (ondasFaltando % 2) * 30, -330, i * 35, false, TiroChefe.ONDA_SOM, TiroChefe.Movimento.RETO));
            }
            return;
        }
        if (fase > 99) return;
        fase = 100; // marca o fim; a proxima atualizacao fecha o teto
        manobra = NADA; intervalo = 1.2;
    }
    private void atualizarGancho(double dt, int naveX, int naveY) {
        if (gancho == 0) return;
        double bx = bocaArpaoX(), by = bocaArpaoY(), nx = naveX + 22, ny = naveY + 18;
        if (gancho == 1) {
            fase += dt; hx = bx; hy = by;
            if (fase >= .6) { // dispara no ponto onde a nave estava
                double a = Math.atan2(ny - by, nx - bx); hvx = Math.cos(a) * 640; hvy = Math.sin(a) * 640;
                gancho = 2; fase = 0; vidaCorrente = 12; SomJogo.tocar(SomJogo.Efeito.CHEFE_TIRO);
            }
        } else if (gancho == 2) {
            fase += dt; hx += hvx * dt; hy += hvy * dt;
            if (Math.hypot(hx - nx, hy - ny) < 30) { gancho = 3; tempoPreso = 0; impactoGancho = .3; SomJogo.tocar(SomJogo.Efeito.IMPACTO); }
            else if (fase > 1.4 || hx < -20 || hy < TOPO - 20 || hy > H + 20) gancho = 4;
        } else if (gancho == 3) {
            hx = nx; hy = ny; tempoPreso += dt;
            // chegou nos espinhos do para-choque ou cansou de puxar: solta
            if (Math.hypot(nx - bx, ny - by) < 70) {
                // bateu nos espinhos: dano, faiscas e a nave e cuspida para tras
                gancho = 4; danoGancho = 4; batida = .45; batidaX = nx; batidaY = ny; SomJogo.tocar(SomJogo.Efeito.EXPLOSAO);
            } else if (tempoPreso > 4.5) gancho = 4;
        } else {
            double d = Math.hypot(bx - hx, by - hy);
            if (d < 30) { gancho = 0; return; }
            hx += (bx - hx) / d * 900 * dt; hy += (by - hy) / d * 900 * dt;
        }
    }
    /** Dano dos espinhos do para-choque, lido uma vez pela Fase. */
    int consumirDanoGancho() { int d = danoGancho; danoGancho = 0; return d; }
    boolean naveAgarrada() { return ato == VAN && gancho == 3; }
    /** Para onde a nave esta sendo arrastada (ou null): a Fase move a nave ate la. */
    double[] puxaoNave() { return ato == VAN && gancho == 3 ? new double[]{bocaArpaoX(), bocaArpaoY()} : null; }
    /** Tiro na corrente: distancia do ponto ao segmento boca-gancho. */
    private boolean naCorrente(Rectangle r) {
        if (gancho != 2 && gancho != 3) return false;
        double ax = bocaArpaoX(), ay = bocaArpaoY(), dx2 = hx - ax, dy2 = hy - ay, l2 = dx2 * dx2 + dy2 * dy2;
        double t = l2 == 0 ? 0 : Math.max(0, Math.min(1, ((r.getCenterX() - ax) * dx2 + (r.getCenterY() - ay) * dy2) / l2));
        return Math.hypot(r.getCenterX() - (ax + dx2 * t), r.getCenterY() - (ay + dy2 * t)) < 16 && t < .92;
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
            // Ozzy: revoada de morcegos ondulando ate a nave, em duas levas
            for (int i = -2; i <= 2; i++) { tiro(tiros, ox, oy, mira + i * .17, 250, false, TiroChefe.MORCEGO_OZZY, TiroChefe.Movimento.ONDA); tiro(tiros, ox + 30, oy, mira + i * .17 + .08, 200, false, TiroChefe.MORCEGO_OZZY, TiroChefe.Movimento.ONDA); }
        } else {
            // Lemmy: graves ondulando e, sozinho, uma parede de baixo com um buraco
            // Lemmy: leque de ases de espadas girando direto na nave
            for (int i = -2; i <= 2; i++) tiro(tiros, ox, oy, mira + i * .14, 310, false, TiroChefe.AS_ESPADAS, TiroChefe.Movimento.RETO);
            // sozinho, joga o baralho inteiro: uma parede de ases com um buraco so
            if (sozinho) { int buraco = (naveY - TOPO) / 60; for (int j = 0; j < 9; j++) if (Math.abs(j - buraco) > 1) tiro(tiros, W + 10, TOPO + 30 + j * 60, Math.PI, 230, false, TiroChefe.AS_ESPADAS, TiroChefe.Movimento.RETO); }
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
        if (ato == VAN && naCorrente(r)) { acertouCorrente = true; return true; }
        if (ato != DUO) return r.intersects(getBounds());
        for (int k = 0; k < 2; k++) if ((k == 0 ? vidaOzzy : vidaLemmy) > 0 && r.intersects(corpoDuo(k))) { alvoDuo = k; return true; }
        return false;
    }
    boolean receberDano(int dano) {
        if (entrada < 1 && ato != DUO || transicao > 0 || cenaPendente != null) return false;
        dano = Math.max(0, dano);
        if (acertouCorrente) {
            // cada tiro na corrente quebra um elo; doze e a nave se solta
            acertouCorrente = false; faiscaCorrente = .12;
            if (--vidaCorrente <= 0 && (gancho == 2 || gancho == 3)) { gancho = 4; SomJogo.tocar(SomJogo.Efeito.IMPACTO); }
            return false;
        }
        flash = .16;
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
            gancho = 0; manobra = NADA;
            for (int k = 0; k < 2; k++) { px[k] = vx + 200 * E; py[k] = vy; }
            return false;
        }
        return true;
    }
    boolean estaVivo() { return ato != DRAGAO || vida > 0; }
    int getVida() { return ato == DUO ? vidaOzzy + vidaLemmy : vida; }
    int getVidaMaxima() { return ato == VAN ? vidaVan : ato == DUO ? vidaDuo * 2 : vidaDragao; }
    String getNome() { return ato == VAN ? "A VAN DO INFERNO" : ato == DUO ? "MALACHAR + KROVAX - VAN DO INFERNO" : "MALACHAR - O DRAGAO"; }
    Rectangle getBounds() {
        if (ato == VAN) return new Rectangle((int) (vx + 150 * E), (int) (vy + 110 * E), (int) (320 * E), (int) (215 * E));
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
        if (ato == VAN && gancho == 3 && nave.intersects(getBounds())) return true;
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
        BufferedImage arte = ARTE_VAN; int q, ch = 380, oy = 0;
        boolean furia = vida < vidaVan * .4;
        if (manobra == PAREDAO && VAN_AMPS != null) {
            arte = VAN_AMPS; ch = 470; oy = -93;
            q = fase < .55 ? 1 : ondasFaltando > 0 || fase < 100 ? (furia ? 3 : 2) : 1;
        } else if (gancho != 0 && VAN_GANCHO != null) {
            arte = VAN_GANCHO; q = gancho == 1 ? 1 : gancho == 2 ? 2 : gancho == 3 ? 3 : 0;
        } else q = investindo ? 2 : aviso > 0 ? 1 : furia ? 3 : (int) (tempo * 6) % 4 == 0 ? 1 : 0;
        if (arte != null) {
            int fw = arte.getWidth() / 4, dy0 = y + (int) (oy * E);
            g.drawImage(arte, x, dy0, x + vanW, dy0 + (int) (ch * E), q * fw, 0, q * fw + fw, arte.getHeight(), null);
        } else { g.setColor(new Color(30, 30, 34)); g.fillRoundRect(x + 90, y + 70, 200, 120, 20, 20); }
        if (investindo) for (int k = 0; k < 10; k++) { g.setColor(new Color(255, 150, 40, 160 - k * 14)); g.fillRect(x + (int) (470 * E) + k * 20, y + (int) (150 * E) + (k % 3) * 30, 44, 3); }
        if (gancho >= 2) desenharCorrente(g);
        if (batida > 0) {
            // estilhacos e faiscas no ponto em que a nave bateu nos espinhos
            java.util.Random r = new java.util.Random(11); double t = 1 - batida / .45;
            for (int k = 0; k < 16; k++) { double a = r.nextDouble() * Math.PI * 2, d = 10 + t * (40 + r.nextInt(50));
                g.setColor(k % 3 == 0 ? new Color(255, 255, 200, (int) (255 * (1 - t))) : new Color(255, 140 + r.nextInt(80), 40, (int) (230 * (1 - t))));
                int sx = (int) (batidaX + Math.cos(a) * d), sy = (int) (batidaY + Math.sin(a) * d); g.fillRect(sx, sy, 5, 5); }
            g.setColor(new Color(255, 230, 160, (int) (180 * (1 - t)))); int rr = (int) (16 + t * 40); g.fillOval((int) batidaX - rr, (int) batidaY - rr, rr * 2, rr * 2);
        }
    }
    private void desenharCorrente(Graphics2D tela) {
        Graphics2D g = (Graphics2D) tela.create();
        double ax = bocaArpaoX(), ay = bocaArpaoY(), d = Math.hypot(hx - ax, hy - ay), a = Math.atan2(hy - ay, hx - ax);
        if (ELO != null) {
            // elos repetidos ao longo da corrente, girados na direcao do gancho
            int ew = ELO.getWidth() * 3 / 4, eh = ELO.getHeight() * 3 / 4;
            Graphics2D c = (Graphics2D) g.create(); c.translate(ax, ay); c.rotate(a);
            for (double t = 0; t < d - 20; t += ew * .8) c.drawImage(ELO, (int) t, -eh / 2, ew, eh, null);
            c.dispose();
        } else { g.setColor(new Color(90, 80, 70)); g.setStroke(new java.awt.BasicStroke(4)); g.drawLine((int) ax, (int) ay, (int) hx, (int) hy); }
        if (faiscaCorrente > 0) { g.setColor(new Color(255, 220, 120)); for (int k = 0; k < 6; k++) g.fillRect((int) (ax + (hx - ax) * (.3 + k * .1)) + (k % 2) * 6 - 3, (int) (ay + (hy - ay) * (.3 + k * .1)) - 3, 4, 4); }
        if (gancho == 3) {
            // agarrou: clarao no fechamento e faiscas do metal raspando enquanto puxa
            if (impactoGancho > 0) { int rr = (int) (20 + (.3 - impactoGancho) * 120); g.setColor(new Color(255, 240, 200, (int) (impactoGancho / .3 * 220))); g.fillOval((int) hx - rr, (int) hy - rr, rr * 2, rr * 2); }
            java.util.Random r = new java.util.Random((long) (tempo * 20));
            for (int k = 0; k < 5; k++) { g.setColor(new Color(255, 200 - k * 20, 80)); g.fillRect((int) hx - 8 + r.nextInt(30), (int) hy - 14 + r.nextInt(28), 3, 3); }
        }
        BufferedImage gc = gancho == 3 ? GANCHO_FECHADO : GANCHO_ABERTO;
        if (gc != null) {
            // a arte aponta para a esquerda: gira para seguir a corrente
            Graphics2D h = (Graphics2D) g.create(); h.translate(hx, hy); h.rotate(a + Math.PI);
            int w = (int) (gc.getWidth() * .6), hh = (int) (gc.getHeight() * .6);
            h.drawImage(gc, -w / 3, -hh / 2, w, hh, null); h.dispose();
        }
        g.dispose();
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
            g.drawString("MALACHAR" + atoTxt, 217, TOPO + 24); g.drawString("KROVAX", 487, TOPO + 24);
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
