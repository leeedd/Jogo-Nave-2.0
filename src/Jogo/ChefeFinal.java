package Jogo;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.List;

/**
 * Luta final, depois da fortaleza. Ato I: Jack Black (arranca o 1o dente do demonio).
 * Ato II: Kyle Gass (arranca o 2o dente). Ato III: com os dois dentes a dupla invoca o
 * Deezleboss. Ato IV: o Deezleboss derrotado se revela Ronnie James Dio, com a corrente
 * do Holy Diver e o demonio do Holy Diver atacando a nave.
 * Arte: arco_final_099.png (grade 4x4: Jack, Kyle, Deezleboss e Dio; quadros repouso,
 * preparo, golpe e bate-cabeca, todos virados para a esquerda).
 */
final class ChefeFinal {
    static final int JACK = 1, KYLE = 2, DEEZLE = 3, DIO = 4;
    private static final BufferedImage ARCO = ler("arco_final_099.png"), ELO = ler("hellraiser_corrente_elo.png"),
            GANCHO_ABERTO = ler("hellraiser_gancho_aberto.png"), GANCHO_FECHADO = ler("hellraiser_gancho_fechado.png");
    private static final int W = Config.LARGURA, H = Config.ALTURA, TOPO = Config.HUD;
    /** Tamanho na tela de cada ato (a celula da grade e quadrada). */
    private static final int[] TAM = {0, 250, 250, 400, 290};

    private int ato = JACK, dentes;
    private final int vidaJack, vidaKyle, vidaDeezle, vidaDio;
    private int vida;
    private double tempo, entrada, flash, gesto, intervalo = 1.6, transicao;
    private String aviso;
    private int golpes;
    private double x = W + 40, y;

    // Deezleboss: investida com aviso
    private double investida, alvoInvX, alvoInvY, baseX;
    // Dio: raio arco-iris (carga e disparo), corrente do Holy Diver e o demonio
    private double cargaRaio, raio, direcaoRaio;
    private int gancho; // 0 parado, 1 mirando, 2 voando, 3 preso, 4 recolhendo
    private double hx, hy, hvx, hvy, faseGancho, tempoPreso, faiscaCorrente;
    private int vidaCorrente, danoGancho;
    private boolean acertouCorrente;
    private double demonioX, demonioY, demonio, avisoDemonio; // demonio > 0: atravessando a tela

    ChefeFinal(Dificuldade dif) {
        double r = dif.resistenciaChefe;
        vidaJack = (int) Math.round(230 * r); vidaKyle = (int) Math.round(250 * r);
        vidaDeezle = (int) Math.round(480 * r); vidaDio = (int) Math.round(420 * r);
        vida = vidaJack;
        y = TOPO + (H - TOPO - TAM[JACK]) / 2.0;
    }

    int ato() { return ato; }
    int dentes() { return dentes; }
    String consumirCena() { return null; }
    String consumirAviso() { String a = aviso; aviso = null; return a; }
    double getEntrada() { return entrada; }

    void atualizar(double dt, int naveX, int naveY, Dificuldade dif, List<TiroChefe> tiros) {
        if (dt <= 0) return;
        tempo += dt; flash = Math.max(0, flash - dt); gesto = Math.max(0, gesto - dt);
        faiscaCorrente = Math.max(0, faiscaCorrente - dt);
        if (transicao > 0) { transicao = Math.max(0, transicao - dt); gancho = 0; raio = cargaRaio = demonio = avisoDemonio = 0; return; }
        int t = TAM[ato];
        double alvoX = W - t - (ato == DEEZLE ? 20 : 40);
        if (entrada < 1) {
            entrada = Math.min(1, entrada + dt / 1.3);
            x = W + 40 - (W + 40 - alvoX) * (1 - Math.pow(1 - entrada, 3)); baseX = alvoX;
            return;
        }
        double cad = dif.cadenciaInimigos;
        boolean furia = vida < getVidaMaxima() * .35;
        if (ato == JACK) jack(dt, naveX, naveY, cad * (furia ? 1.3 : 1), tiros, furia);
        else if (ato == KYLE) kyle(dt, naveX, naveY, cad * (furia ? 1.3 : 1), tiros, furia);
        else if (ato == DEEZLE) deezle(dt, naveX, naveY, cad * (furia ? 1.25 : 1), tiros, furia);
        else dio(dt, naveX, naveY, cad * (furia ? 1.25 : 1), tiros, furia);
    }

    /** Acompanha a nave na vertical, devagar, balancando. */
    private void flutuar(double dt, int naveY, double vel) {
        int t = TAM[ato];
        double alvo = Math.max(TOPO, Math.min(H - t, naveY + 18 - t * .55));
        y += (alvo + Math.sin(tempo * 1.4) * 26 - y) * Math.min(1, dt * vel);
    }

    // ---------------- ato I: Jack Black ----------------
    private void jack(double dt, int naveX, int naveY, double cad, List<TiroChefe> tiros, boolean furia) {
        flutuar(dt, naveY, 1.1);
        x += (baseX + Math.sin(tempo * .8) * 30 - x) * Math.min(1, dt * 2);
        intervalo -= dt * cad;
        if (intervalo > 0 || tiros.size() > 46) return;
        golpes++; gesto = .5;
        double ox = x + TAM[JACK] * .3, oy = y + TAM[JACK] * .45, mira = Math.atan2(naveY + 18 - oy, naveX + 22 - ox);
        if (golpes % 3 == 0) {
            // meia-lua de fogo do riff: arco largo de chamas com um buraco onde a nave esta
            int n = furia ? 11 : 9;
            for (int i = 0; i < n; i++) { if (i == n / 2) continue; double a = Math.PI + (i - (n - 1) / 2.0) * .16; tiro(tiros, ox, oy, a, 260, true, TiroChefe.ENERGIA, TiroChefe.Movimento.ACELERADO, new Color(255, 120, 30)); }
        } else {
            // notas do solo em leque, ondulando ate a nave
            for (int i = -2; i <= 2; i++) tiro(tiros, ox, oy, mira + i * .18, 240, false, TiroChefe.NOTA, TiroChefe.Movimento.ONDA, new Color(255, 200, 60));
        }
        intervalo = furia ? 1.05 : 1.4;
    }

    // ---------------- ato II: Kyle Gass ----------------
    private void kyle(double dt, int naveX, int naveY, double cad, List<TiroChefe> tiros, boolean furia) {
        flutuar(dt, naveY, .8);
        x += (baseX + Math.sin(tempo * 1.1) * 40 - x) * Math.min(1, dt * 2);
        intervalo -= dt * cad;
        if (intervalo > 0 || tiros.size() > 46) return;
        golpes++; gesto = .5;
        double ox = x + TAM[KYLE] * .25, oy = y + TAM[KYLE] * .5;
        if (golpes % 2 == 0) {
            // parede de ondas verdes com um corredor so: tem que achar o buraco
            int buraco = Math.max(1, Math.min(7, (naveY - TOPO) / 60));
            for (int j = 0; j < 9; j++) if (Math.abs(j - buraco) > 1) tiros.add(new TiroChefe(W + 10, TOPO + 30 + j * 60, -280, 0, false, TiroChefe.ONDA_SOM, TiroChefe.Movimento.RETO));
        } else {
            double mira = Math.atan2(naveY + 18 - oy, naveX + 22 - ox);
            for (int i = -3; i <= 3; i++) tiro(tiros, ox, oy, mira + i * .13, 290, false, TiroChefe.ENERGIA, TiroChefe.Movimento.PULSANTE, new Color(90, 255, 110));
        }
        intervalo = furia ? 1.0 : 1.35;
    }

    // ---------------- ato III: Deezleboss ----------------
    private void deezle(double dt, int naveX, int naveY, double cad, List<TiroChefe> tiros, boolean furia) {
        int t = TAM[DEEZLE];
        if (investida > 0) {
            // avanca ate o ponto travado e volta: ha tempo de sair da frente durante o aviso
            investida = Math.max(0, investida - dt);
            double p = investida > 1.1 ? 0 : 1 - investida / 1.1, arco = Math.sin(p * Math.PI);
            x = baseX + (alvoInvX - baseX) * arco; y += (alvoInvY - y) * Math.min(1, dt * 4) * arco;
            return;
        }
        double alvo = Math.max(TOPO - 20, Math.min(H - t + 20, naveY + 18 - t * .5));
        y += (alvo + Math.sin(tempo * 1.2) * 30 - y) * Math.min(1, dt * .9);
        x += (baseX + Math.sin(tempo * .7) * 20 - x) * Math.min(1, dt * 2);
        intervalo -= dt * cad;
        if (intervalo > 0 || tiros.size() > 50) return;
        golpes++; gesto = .55;
        double ox = x + t * .45, oy = y + t * .4;
        int g = golpes % 3;
        if (g == 1) {
            // espiral de fogo do peito
            int n = furia ? 16 : 12;
            for (int i = 0; i < n; i++) tiro(tiros, ox, oy, tempo + i * Math.PI * 2 / n, 200, true, TiroChefe.FOGO_ROXO, TiroChefe.Movimento.ONDA, null);
        } else if (g == 2) {
            // chuva de brasas caindo em arco sobre a nave
            for (int i = 0; i < (furia ? 9 : 7); i++) tiros.add(new TiroChefe(naveX - 160 + i * 55, TOPO - 10, -40, 80 + i % 3 * 30, false, TiroChefe.ENERGIA, TiroChefe.Movimento.ARCO).comCor(new Color(255, 90, 30)));
        } else {
            investida = 1.1 + .55; alvoInvX = Math.max(-60, naveX - t * .35); alvoInvY = Math.max(TOPO - 20, Math.min(H - t + 20, naveY + 18 - t * .5));
            SomJogo.tocar(SomJogo.Efeito.CHEFE_ENTRADA);
        }
        intervalo = furia ? 1.3 : 1.7;
    }
    private boolean avisandoInvestida() { return ato == DEEZLE && investida > 1.1; }

    // ---------------- ato IV: Ronnie James Dio ----------------
    private double maoX() { return x + TAM[DIO] * .32; }
    private double maoY() { return y + TAM[DIO] * .42; }
    private static final double ALCANCE_RAIO = 1100;
    private void dio(double dt, int naveX, int naveY, double cad, List<TiroChefe> tiros, boolean furia) {
        atualizarGancho(dt, naveX, naveY);
        atualizarDemonio(dt, naveY);
        if (gancho != 3) flutuar(dt, naveY, .9);
        x += (baseX + Math.sin(tempo * .9) * 24 - x) * Math.min(1, dt * 2);
        double alvo = Math.atan2(naveY + 18 - maoY(), naveX + 22 - maoX());
        if (cargaRaio > 0) {
            cargaRaio = Math.max(0, cargaRaio - dt); direcaoRaio = alvo;
            if (cargaRaio == 0) raio = furia ? 1.6 : 1.2;
            return;
        }
        if (raio > 0) {
            // o arco-iris sai da mao e vira atras da nave, mais devagar que ela
            raio = Math.max(0, raio - dt);
            double dif = Math.atan2(Math.sin(alvo - direcaoRaio), Math.cos(alvo - direcaoRaio)), giro = (furia ? .55 : .4) * dt;
            direcaoRaio += Math.max(-giro, Math.min(giro, dif));
            return;
        }
        if (gancho != 0) return;
        intervalo -= dt * cad;
        if (intervalo > 0 || tiros.size() > 50) return;
        golpes++; gesto = .55;
        int g = golpes % 4;
        if (g == 1) cargaRaio = 1.0;
        else if (g == 2) { gancho = 1; faseGancho = 0; }
        else if (g == 3) {
            // orbes do arco-iris, cada uma de uma cor
            Color[] cores = {new Color(255, 60, 60), new Color(255, 160, 40), new Color(255, 240, 60), new Color(80, 230, 90), new Color(70, 150, 255), new Color(170, 90, 255)};
            for (int i = 0; i < 6; i++) tiro(tiros, maoX(), maoY(), alvo + (i - 2.5) * .15, 250, false, TiroChefe.ENERGIA, TiroChefe.Movimento.ONDA, cores[i]);
        } else if (demonio <= 0 && avisoDemonio <= 0) { avisoDemonio = 1.0; demonioY = Math.max(TOPO, Math.min(H - 190, naveY + 18 - 95)); }
        intervalo = furia ? 1.2 : 1.6;
    }
    /** Corrente do Holy Diver: sai da mao do Dio, agarra a nave e arrasta. Doze tiros na corrente soltam. */
    private void atualizarGancho(double dt, int naveX, int naveY) {
        if (gancho == 0) return;
        double bx = maoX(), by = maoY(), nx = naveX + 22, ny = naveY + 18;
        if (gancho == 1) {
            faseGancho += dt; hx = bx; hy = by;
            if (faseGancho >= .6) { double a = Math.atan2(ny - by, nx - bx); hvx = Math.cos(a) * 640; hvy = Math.sin(a) * 640; gancho = 2; faseGancho = 0; vidaCorrente = 12; SomJogo.tocar(SomJogo.Efeito.CHEFE_TIRO); }
        } else if (gancho == 2) {
            faseGancho += dt; hx += hvx * dt; hy += hvy * dt;
            if (Math.hypot(hx - nx, hy - ny) < 30) { gancho = 3; tempoPreso = 0; SomJogo.tocar(SomJogo.Efeito.IMPACTO); }
            else if (faseGancho > 1.5 || hx < -20 || hy < TOPO - 20 || hy > H + 20) gancho = 4;
        } else if (gancho == 3) {
            hx = nx; hy = ny; tempoPreso += dt;
            if (Math.hypot(nx - bx, ny - by) < 80) { gancho = 4; danoGancho = 4; SomJogo.tocar(SomJogo.Efeito.EXPLOSAO); }
            else if (tempoPreso > 4.5) gancho = 4;
        } else {
            double d = Math.hypot(bx - hx, by - hy);
            if (d < 30) { gancho = 0; intervalo = Math.max(intervalo, .8); return; }
            hx += (bx - hx) / d * 900 * dt; hy += (by - hy) / d * 900 * dt;
        }
    }
    /** O demonio do Holy Diver: aviso na faixa da nave e depois atravessa a tela da direita para a esquerda. */
    private void atualizarDemonio(double dt, int naveY) {
        if (avisoDemonio > 0) {
            avisoDemonio = Math.max(0, avisoDemonio - dt);
            if (avisoDemonio == 0) { demonio = 1; demonioX = W + 20; SomJogo.tocar(SomJogo.Efeito.CHEFE_ENTRADA); }
            return;
        }
        if (demonio <= 0) return;
        demonioX -= 780 * dt;
        if (demonioX < -260) demonio = 0;
    }
    private Rectangle corpoDemonio() { return new Rectangle((int) demonioX + 40, (int) demonioY + 40, 140, 120); }
    int consumirDanoGancho() { int d = danoGancho; danoGancho = 0; return d; }
    boolean naveAgarrada() { return ato == DIO && gancho == 3; }
    double[] puxaoNave() { return ato == DIO && gancho == 3 ? new double[]{maoX(), maoY()} : null; }
    private boolean naCorrente(Rectangle r) {
        if (gancho != 2 && gancho != 3) return false;
        double ax = maoX(), ay = maoY(), dx2 = hx - ax, dy2 = hy - ay, l2 = dx2 * dx2 + dy2 * dy2;
        double t = l2 == 0 ? 0 : Math.max(0, Math.min(1, ((r.getCenterX() - ax) * dx2 + (r.getCenterY() - ay) * dy2) / l2));
        return Math.hypot(r.getCenterX() - (ax + dx2 * t), r.getCenterY() - (ay + dy2 * t)) < 16 && t < .92;
    }
    private boolean noRaio(double cx, double cy) {
        if (raio <= 0) return false;
        double ax = cx - maoX(), ay = cy - maoY(), ux = Math.cos(direcaoRaio), uy = Math.sin(direcaoRaio);
        double ao = ax * ux + ay * uy; if (ao < 0 || ao > ALCANCE_RAIO) return false;
        return Math.abs(ax * uy - ay * ux) < 22;
    }

    private void tiro(List<TiroChefe> tiros, double x0, double y0, double a, double v, boolean pesado, int estilo, TiroChefe.Movimento m, Color cor) {
        TiroChefe t = new TiroChefe(x0, y0, Math.cos(a) * v, Math.sin(a) * v, pesado, estilo, m);
        tiros.add(cor != null ? t.comCor(cor) : t);
    }

    // ---------------- dano e colisao ----------------
    boolean colide(Rectangle r) {
        if (ato == DIO && naCorrente(r)) { acertouCorrente = true; return true; }
        return r.intersects(getBounds());
    }
    boolean receberDano(int dano) {
        if (entrada < 1 || transicao > 0) { acertouCorrente = false; return false; }
        if (acertouCorrente) {
            acertouCorrente = false; faiscaCorrente = .12;
            if (--vidaCorrente <= 0 && (gancho == 2 || gancho == 3)) { gancho = 4; SomJogo.tocar(SomJogo.Efeito.IMPACTO); }
            return false;
        }
        flash = .16; vida = Math.max(0, vida - Math.max(0, dano));
        if (vida > 0) return false;
        if (ato == DIO) return true;
        SomJogo.tocar(SomJogo.Efeito.EXPLOSAO_GRANDE);
        if (ato == JACK) { dentes = 1; aviso = "JACK CAIU! 1o DENTE DO DEMONIO ARRANCADO"; proximo(KYLE, vidaKyle, 2.2); }
        else if (ato == KYLE) { dentes = 2; aviso = "2o DENTE ARRANCADO! A DUPLA INVOCA O DEEZLEBOSS!"; proximo(DEEZLE, vidaDeezle, 3.2); }
        else { aviso = "O DEMONIO SE REVELA: RONNIE JAMES DIO!"; proximo(DIO, vidaDio, 3.0); }
        return false;
    }
    private void proximo(int novo, int v, double pausa) {
        ato = novo; vida = v; transicao = pausa; entrada = 0; x = W + 40; intervalo = 1.6; golpes = 0; investida = 0;
        y = TOPO + (H - TOPO - TAM[novo]) / 2.0;
    }
    boolean estaVivo() { return ato != DIO || vida > 0; }
    int getVida() { return vida; }
    int getVidaMaxima() { return ato == JACK ? vidaJack : ato == KYLE ? vidaKyle : ato == DEEZLE ? vidaDeezle : vidaDio; }
    String getNome() { return ato == JACK ? "JACK BLACK - TENACIOUS D" : ato == KYLE ? "KYLE GASS - TENACIOUS D" : ato == DEEZLE ? "DEEZLEBOSS" : "RONNIE JAMES DIO - HOLY DIVER"; }
    Rectangle getBounds() {
        int t = TAM[ato];
        if (ato == DEEZLE) return new Rectangle((int) (x + t * .2), (int) (y + t * .12), (int) (t * .6), (int) (t * .78));
        return new Rectangle((int) (x + t * .42), (int) (y + t * .1), (int) (t * .42), (int) (t * .82));
    }
    boolean tocaNave(Rectangle nave) {
        if (transicao > 0 || entrada < 1) return false;
        if (ato == DIO && noRaio(nave.getCenterX(), nave.getCenterY())) return true;
        if (ato == DIO && demonio > 0 && nave.intersects(corpoDemonio())) return true;
        return nave.intersects(getBounds());
    }
    int getX() { return getBounds().x; }
    int getY() { return getBounds().y; }

    // ---------------- desenho ----------------
    void desenhar(Graphics2D tela) {
        Graphics2D g = (Graphics2D) tela.create();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        if (transicao > 0) desenharTransicao(g);
        else {
            if (ato == DEEZLE) dupla(g, 1f);
            if (ato == DIO) desenharDemonio(g);
            int q = quadro();
            if (avisandoInvestida()) { g.setColor(new Color(255, 40, 20, (int) (90 + 80 * Math.sin(tempo * 30)))); g.setStroke(new BasicStroke(3)); g.drawLine((int) (x + TAM[DEEZLE] * .4), (int) (y + TAM[DEEZLE] * .5), (int) alvoInvX + 60, (int) alvoInvY + TAM[DEEZLE] / 2); }
            sprite(g, ato - 1, q, x, y + (q == 3 ? 4 : 0), TAM[ato], 1f);
            if (ato == DIO) {
                if (cargaRaio > 0) { int r = (int) (10 + (1 - cargaRaio) * 28 + Math.sin(tempo * 40) * 3); g.setColor(Color.getHSBColor((float) (tempo * 2 % 1), .8f, 1f)); g.fillOval((int) maoX() - r, (int) maoY() - r, r * 2, r * 2); }
                if (raio > 0) desenharRaio(g);
                if (gancho >= 2) desenharCorrente(g);
            }
        }
        if (flash > 0) {
            g.setColor(new Color(255, 220, 160, (int) (flash / .16 * 200))); Rectangle b = getBounds();
            for (int i = 0; i < 6; i++) { double an = i * Math.PI / 3; int r = 15 + (int) ((.16 - flash) * 180); g.fillRect((int) b.getCenterX() + (int) (Math.cos(an) * r), (int) b.getCenterY() + (int) (Math.sin(an) * r), 4, 4); }
        }
        g.dispose();
    }
    private int quadro() {
        if (ato == DIO) return raio > 0 ? 2 : cargaRaio > 0 || gancho == 1 ? 1 : gesto > 0 ? 3 : 0;
        if (avisandoInvestida()) return 1;
        if (gesto > .25) return 2;
        if (gesto > 0) return 1;
        return (int) (tempo * 1.6) % 4 == 3 ? 3 : 0;
    }
    /** Jack e Kyle tocando la no fundo enquanto o Deezleboss luta (nao levam tiro). */
    private void dupla(Graphics2D g, float a) {
        int s = 150; int q = (int) (tempo * 2.4) % 2 == 0 ? 0 : 3;
        sprite(g, 0, q, W - s - 10, TOPO + 4 + Math.sin(tempo * 2) * 6, s, a * .85f);
        sprite(g, 1, q == 0 ? 3 : 0, W - s - 10, H - s - 4 + Math.sin(tempo * 2 + 1) * 6, s, a * .85f);
    }
    private void desenharTransicao(Graphics2D g) {
        // o ato que caiu some piscando; os dentes do demonio contam a historia
        double p = 1 - transicao / (ato == KYLE ? 2.2 : ato == DEEZLE ? 3.2 : 3.0);
        int ant = ato - 1;
        if (p < .5) {
            float a = (float) Math.max(0, 1 - p * 2);
            if ((int) (tempo * 20) % 2 == 0) sprite(g, ant - 1, 3, W - TAM[ant] - 40, TOPO + (H - TOPO - TAM[ant]) / 2.0, TAM[ant], a);
        }
        if (ato == DEEZLE) {
            // Jack e Kyle juntos invocam: o demonio cresce do fogo
            dupla(g, (float) Math.min(1, p * 3));
            if (p > .35) { float a = (float) Math.min(1, (p - .35) / .5); int t = (int) (TAM[DEEZLE] * (.4 + .6 * a)); sprite(g, 2, 1, W - t - 20, TOPO + (H - TOPO - t) / 2.0, t, a); }
        } else if (ato == DIO) {
            // clarao arco-iris: o demonio vira o Dio
            float a = (float) Math.min(1, p * 1.6);
            if (p < .6) sprite(g, 2, 3, W - TAM[DEEZLE] - 20, TOPO + (H - TOPO - TAM[DEEZLE]) / 2.0, TAM[DEEZLE], 1f - a * .9f);
            if (p > .4) sprite(g, 3, 0, W - TAM[DIO] - 40, TOPO + (H - TOPO - TAM[DIO]) / 2.0, TAM[DIO], (float) Math.min(1, (p - .4) / .4));
            // aneis do arco-iris abrindo a partir do peito do demonio
            int cx = W - TAM[DEEZLE] / 2 - 20, cy = TOPO + (H - TOPO) / 2;
            g.setStroke(new BasicStroke(5));
            for (int k = 0; k < 6; k++) {
                double r = (p * 1.4 - k * .08) * 520; if (r <= 0) continue;
                float al = (float) Math.max(0, Math.min(.8, 1 - r / 520));
                Color c = Color.getHSBColor(k / 6f, .85f, 1f);
                g.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), (int) (255 * al)));
                g.drawOval(cx - (int) r, cy - (int) r, (int) r * 2, (int) r * 2);
            }
        }
    }
    private void desenharRaio(Graphics2D tela) {
        Graphics2D g = (Graphics2D) tela.create();
        double bx = maoX(), by = maoY(), ux = Math.cos(direcaoRaio), uy = Math.sin(direcaoRaio);
        float fim = (float) Math.min(1, raio / .25);
        Color[] cores = {new Color(255, 60, 60), new Color(255, 160, 40), new Color(255, 240, 60), new Color(80, 230, 90), new Color(70, 150, 255), new Color(170, 90, 255)};
        for (int k = 0; k < 6; k++) {
            double off = (k - 2.5) * 6 + Math.sin(tempo * 30 + k) * 1.5;
            g.setColor(new Color(cores[k].getRed(), cores[k].getGreen(), cores[k].getBlue(), (int) (220 * fim)));
            g.setStroke(new BasicStroke(7));
            g.drawLine((int) (bx - uy * off), (int) (by + ux * off), (int) (bx + ux * ALCANCE_RAIO - uy * off), (int) (by + uy * ALCANCE_RAIO + ux * off));
        }
        g.setColor(new Color(255, 255, 255, (int) (200 * fim))); g.setStroke(new BasicStroke(4));
        g.drawLine((int) bx, (int) by, (int) (bx + ux * ALCANCE_RAIO), (int) (by + uy * ALCANCE_RAIO));
        g.dispose();
    }
    private void desenharCorrente(Graphics2D tela) {
        Graphics2D g = (Graphics2D) tela.create();
        double ax = maoX(), ay = maoY(), d = Math.hypot(hx - ax, hy - ay), a = Math.atan2(hy - ay, hx - ax);
        if (ELO != null) {
            int ew = ELO.getWidth() * 3 / 4, eh = ELO.getHeight() * 3 / 4;
            Graphics2D c = (Graphics2D) g.create(); c.translate(ax, ay); c.rotate(a);
            for (double t = 0; t < d - 20; t += ew * .8) c.drawImage(ELO, (int) t, -eh / 2, ew, eh, null);
            c.dispose();
        } else { g.setColor(new Color(150, 150, 160)); g.setStroke(new BasicStroke(4)); g.drawLine((int) ax, (int) ay, (int) hx, (int) hy); }
        if (faiscaCorrente > 0) { g.setColor(new Color(255, 220, 120)); for (int k = 0; k < 6; k++) g.fillRect((int) (ax + (hx - ax) * (.3 + k * .1)) - 3, (int) (ay + (hy - ay) * (.3 + k * .1)) - 3, 4, 4); }
        BufferedImage gc = gancho == 3 ? GANCHO_FECHADO : GANCHO_ABERTO;
        if (gc != null) { Graphics2D h = (Graphics2D) g.create(); h.translate(hx, hy); h.rotate(a + Math.PI); int w = (int) (gc.getWidth() * .6), hh = (int) (gc.getHeight() * .6); h.drawImage(gc, -w / 3, -hh / 2, w, hh, null); h.dispose(); }
        g.dispose();
    }
    /** O demonio do Holy Diver: a silhueta do Deezleboss, escura e translucida, rasgando a faixa avisada. */
    private void desenharDemonio(Graphics2D g) {
        if (avisoDemonio > 0) {
            g.setColor(new Color(170, 40, 255, (int) (60 + 60 * Math.sin(tempo * 25))));
            g.fillRect(0, (int) demonioY + 40, W, 120);
            g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 18)); g.setColor(new Color(230, 180, 255));
            g.drawString("<<< HOLY DIVER", W - 200, (int) demonioY + 105);
            return;
        }
        if (demonio <= 0 || ARCO == null) return;
        BufferedImage s = escuro();
        int c = ARCO.getWidth() / 4, q = (int) (tempo * 8) % 2 == 0 ? 0 : 1, t = 220;
        Graphics2D a = (Graphics2D) g.create(); a.setComposite(AlphaComposite.SrcOver.derive(.8f));
        a.drawImage(s, (int) demonioX, (int) demonioY, (int) demonioX + t, (int) demonioY + t, q * c, 0, q * c + c, c, null);
        a.dispose();
    }
    private static BufferedImage demonioEscuro;
    /** Quadros 0 e 1 do Deezleboss tingidos de roxo: vira o demonio fantasma do Holy Diver. */
    private static BufferedImage escuro() {
        if (demonioEscuro != null) return demonioEscuro;
        int c = ARCO.getWidth() / 4;
        BufferedImage b = new BufferedImage(c * 2, c, BufferedImage.TYPE_INT_ARGB);
        for (int yy = 0; yy < c; yy++) for (int xx = 0; xx < c * 2; xx++) {
            int p = ARCO.getRGB(xx, 2 * c + yy), al = p >>> 24; if (al == 0) continue;
            int v = ((p >> 16 & 255) + (p >> 8 & 255) + (p & 255)) / 3;
            b.setRGB(xx, yy, al << 24 | Math.min(255, v / 2 + 70) << 16 | (v / 4 + 10) << 8 | Math.min(255, v + 90));
        }
        return demonioEscuro = b;
    }
    private static void sprite(Graphics2D g, int linha, int q, double x, double y, int t, float alfa) {
        if (ARCO == null) { g.setColor(new Color(150, 30, 30)); g.fillRect((int) x + t / 3, (int) y + t / 5, t / 2, t * 3 / 4); return; }
        int c = ARCO.getWidth() / 4;
        Graphics2D a = (Graphics2D) g.create();
        if (alfa < 1) a.setComposite(AlphaComposite.SrcOver.derive(Math.max(0f, Math.min(1f, alfa))));
        a.drawImage(ARCO, (int) x, (int) y, (int) x + t, (int) y + t, q * c, linha * c, q * c + c, linha * c + c, null);
        a.dispose();
    }

    void desenharVida(Graphics2D g) {
        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14)); g.setColor(Color.WHITE);
        String[] atos = {"", "  [ATO I]", "  [ATO II]", "  [ATO III]", "  [ATO IV]"};
        g.drawString(getNome() + atos[ato], 217, TOPO + 24);
        Color c = ato == JACK ? new Color(255, 120, 40) : ato == KYLE ? new Color(80, 220, 90) : ato == DEEZLE ? new Color(220, 40, 30) : new Color(170, 90, 255);
        g.setColor(new Color(8, 22, 34, 222)); g.fillRoundRect(218, TOPO + 31, 526, 22, 10, 10);
        g.setPaint(new GradientPaint(218, TOPO + 32, c, 744, TOPO + 52, new Color(222, 36, 70)));
        g.fillRoundRect(220, TOPO + 33, (int) (522.0 * Math.max(0, vida) / Math.max(1, getVidaMaxima())), 18, 8, 8);
        g.setColor(new Color(255, 255, 255, 130)); g.drawRoundRect(218, TOPO + 31, 526, 22, 10, 10);
        // os dois dentes do demonio: acesos quando arrancados
        for (int k = 0; k < 2; k++) {
            int dx = 756 + k * 22; g.setColor(k < dentes ? new Color(255, 245, 220) : new Color(90, 70, 70));
            g.fillPolygon(new int[]{dx, dx + 14, dx + 7}, new int[]{TOPO + 32, TOPO + 32, TOPO + 52}, 3);
        }
    }

    private static BufferedImage ler(String n) {
        java.net.URL u = ChefeFinal.class.getResource("/ImagemIcon/" + n);
        if (u == null) return null;
        try { return javax.imageio.ImageIO.read(u); } catch (java.io.IOException e) { return null; }
    }
}
