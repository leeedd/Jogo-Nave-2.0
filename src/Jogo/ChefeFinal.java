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
 * Luta final, depois da fortaleza. Ato I: Jack Black (deixa cair metade da Palheta do Destino).
 * Ato II: Kyle Gass (deixa a outra metade). Ato III: a Palheta do Destino se junta e invoca o
 * Deezleboss. Ato IV: o Deezleboss derrotado se revela Ronnie James Dio (arco-iris).
 * Ato V: o Dio vira o demonio do Holy Diver, com a corrente que agarra, o chicote e o mergulho.
 * Arte: arco_final_099.png (grade 4x4: Jack, Kyle, Deezleboss e Dio; quadros repouso,
 * preparo, golpe e bate-cabeca, todos virados para a esquerda).
 */
final class ChefeFinal {
    static final int JACK = 1, KYLE = 2, DEEZLE = 3, DIO = 4, HOLY = 5;
    private static final BufferedImage ARCO = ler("arco_final_099.png"), ELO = ler("hellraiser_corrente_elo.png"),
            GANCHO_ABERTO = ler("hellraiser_gancho_aberto.png"), GANCHO_FECHADO = ler("hellraiser_gancho_fechado.png");
    /** Demonio do Holy Diver (4 quadros: girando a corrente, preparando, chicoteando, mergulhando). Sem a arte, usa o Deezleboss tingido. */
    private static final BufferedImage HOLY_DIVER = ler("holy_diver_demonio.png");
    /** Dio de espada (4 quadros: espada erguida, preparando, corte com a meia-lua prateada, chifres). Sem a arte, usa a linha do Dio da grade. */
    private static final BufferedImage DIO_ESPADA = ler("dio_espada.png");
    /** Jack e Kyle de VIOLAO (4 quadros cada: repouso, preparo, golpe, bate-cabeca). Sem a arte, a grade (de guitarra). */
    private static final BufferedImage JACK_VIOLAO = ler("jack_violao.png"), KYLE_VIOLAO = ler("kyle_violao.png");
    /** Beelzeboss (4 quadros: guitarra em estrela, preparando, riff de fogo, na bateria 666). Sem a arte, usa a linha do demonio da grade. */
    private static final BufferedImage DEEZLE_ARTE = ler("deezleboss.png");
    /** Palheta do Destino (3 quadros: metade esquerda, metade direita, inteira brilhando). Feita do dente do demonio. */
    private static final BufferedImage PALHETA = ler("palheta_destino.png");
    /** Moldura da barra de vida (faixa preta da vida e dois escudos para as metades da palheta). */
    private static final BufferedImage BARRA = ler("barra_chefe_final.png");
    /** Portal do inferno que a palheta abre (4 quadros girando). Sem a arte, um redemoinho de fagulhas. */
    private static final BufferedImage PORTAL = ler("portal_inferno.png");
    /** Duracao da cena da invocacao: metades erguidas, juntando, raio, portal abrindo, demonio saindo, portal fechando. */
    static final double INVOCACAO = 7.0;
    private static final int W = Config.LARGURA, H = Config.ALTURA, TOPO = Config.HUD;
    /** Tamanho na tela de cada ato (a celula da grade e quadrada). */
    private static final int[] TAM = {0, 250, 250, 400, 290, 360};

    private int ato = JACK, dentes;
    private final int vidaJack, vidaKyle, vidaDeezle, vidaDio, vidaHoly;
    private int vida;
    private double tempo, entrada, flash, gesto, intervalo = 1.6, transicao;
    private String aviso;
    private int golpes;
    private double x = W + 40, y;

    // Deezleboss: investida com aviso
    private double investida, alvoInvX, alvoInvY, baseX, bateria, batida;
    // Dio: raio arco-iris (carga e disparo), corrente do Holy Diver e o demonio
    private double cargaRaio, raio, direcaoRaio;
    private int gancho; // 0 parado, 1 mirando, 2 voando, 3 preso, 4 recolhendo
    private double hx, hy, hvx, hvy, faseGancho, tempoPreso, faiscaCorrente;
    private int vidaCorrente, danoGancho;
    private boolean acertouCorrente;
    private double demonioX, demonioY, demonio, avisoDemonio; // mergulho: demonio > 0 atravessando a tela
    // chicote da corrente: aviso do arco e depois a corrente varre de cima para baixo (ou o contrario)
    private double avisoChicote, chicote, anguloChicote, inicioChicote, fimChicote;
    private boolean acertouChicote;

    ChefeFinal(Dificuldade dif) {
        double r = dif.resistenciaChefe;
        vidaJack = (int) Math.round(230 * r); vidaKyle = (int) Math.round(250 * r);
        vidaDeezle = (int) Math.round(480 * r); vidaDio = (int) Math.round(340 * r); vidaHoly = (int) Math.round(460 * r);
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
        double alvoX = W - t - (ato == DEEZLE ? (DEEZLE_ARTE != null ? 110 : 20) : 40);
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
        else if (ato == DIO) dio(dt, naveX, naveY, cad * (furia ? 1.25 : 1), tiros, furia);
        else holy(dt, naveX, naveY, cad * (furia ? 1.25 : 1), tiros, furia);
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
        double ox = x + TAM[JACK] * (JACK_VIOLAO != null ? .12 : .3), oy = y + TAM[JACK] * .45, mira = Math.atan2(naveY + 18 - oy, naveX + 22 - ox);
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
        double ox = x + TAM[KYLE] * (KYLE_VIOLAO != null ? .12 : .25), oy = y + TAM[KYLE] * .5;
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
        if (bateria > 0) {
            // na bateria 666: cada batida do bumbo solta uma onda grave e os pratos saem girando como serra
            bateria = Math.max(0, bateria - dt); batida -= dt;
            if (batida <= 0 && tiros.size() < 50) {
                batida = furia ? .38 : .5; gesto = .2;
                double ox = x + t * .3, oy = y + t * .55, mira = Math.atan2(naveY + 18 - oy, naveX + 22 - ox);
                tiros.add(new TiroChefe(ox, oy + (golpes % 2 == 0 ? -60 : 60), -300, 0, false, TiroChefe.ONDA_SOM, TiroChefe.Movimento.RETO));
                for (int i = -1; i <= 1; i += 2) tiro(tiros, ox, oy - 80, mira + i * .25, 260, true, TiroChefe.PRATO, TiroChefe.Movimento.ARCO, null);
                golpes++;
            }
            if (bateria == 0) intervalo = 1.2;
            return;
        }
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
        int g = golpes % 4;
        if (g == 3) { bateria = furia ? 2.4 : 2.0; batida = .3; return; }
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
    private boolean avisandoInvestida() { return ato == DEEZLE && investida > 1.1 || ato == DIO && investida > .8; }

    // ---------------- ato IV: Ronnie James Dio ----------------
    private double maoX() { return ato == HOLY ? x + TAM[HOLY] * (HOLY_DIVER != null ? .5 : .28) : x + TAM[DIO] * .32; }
    private double maoY() { return ato == HOLY ? y + TAM[HOLY] * (HOLY_DIVER != null ? .4 : .3) : y + TAM[DIO] * .42; }
    /** O arco-iris sai da mao erguida nos chifres (quadro 4 do Dio de espada). */
    private double raioX() { return DIO_ESPADA != null ? x + TAM[DIO] * .86 : maoX(); }
    private double raioY() { return DIO_ESPADA != null ? y + TAM[DIO] * .11 : maoY(); }
    private static final double ALCANCE_RAIO = 1100;
    private void dio(double dt, int naveX, int naveY, double cad, List<TiroChefe> tiros, boolean furia) {
        if (investida > 0) {
            // investida de espada: avanca ate o ponto travado e volta
            investida = Math.max(0, investida - dt);
            double p = investida > .8 ? 0 : 1 - investida / .8, arco = Math.sin(p * Math.PI);
            x = baseX + (alvoInvX - baseX) * arco; y += (alvoInvY - y) * Math.min(1, dt * 5) * arco;
            return;
        }
        flutuar(dt, naveY, .9);
        x += (baseX + Math.sin(tempo * .9) * 24 - x) * Math.min(1, dt * 2);
        double alvo = Math.atan2(naveY + 18 - raioY(), naveX + 22 - raioX()), alvoMao = Math.atan2(naveY + 18 - maoY(), naveX + 22 - maoX());
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
        intervalo -= dt * cad;
        if (intervalo > 0 || tiros.size() > 50) return;
        golpes++; gesto = .55;
        Color[] cores = {new Color(255, 60, 60), new Color(255, 160, 40), new Color(255, 240, 60), new Color(80, 230, 90), new Color(70, 150, 255), new Color(170, 90, 255)};
        int g = golpes % 4;
        if (g == 1) cargaRaio = 1.0;
        else if (g == 2) {
            // cortes da espada: meias-luas prateadas, duas levas
            for (int i = -1; i <= 1; i++) { tiro(tiros, maoX(), maoY(), alvoMao + i * .2, 330, true, TiroChefe.ENERGIA, TiroChefe.Movimento.ACELERADO, new Color(220, 235, 255)); if (furia) tiro(tiros, maoX() + 40, maoY(), alvoMao + i * .2 + .1, 280, true, TiroChefe.ENERGIA, TiroChefe.Movimento.ACELERADO, new Color(170, 200, 255)); }
        } else if (g == 3) {
            // orbes do arco-iris, cada uma de uma cor
            for (int i = 0; i < 6; i++) tiro(tiros, maoX(), maoY(), alvo + (i - 2.5) * .15, 250, false, TiroChefe.ENERGIA, TiroChefe.Movimento.ONDA, cores[i]);
        } else {
            investida = .8 + .6; int t = TAM[DIO];
            alvoInvX = Math.max(-40, naveX - t * .3); alvoInvY = Math.max(TOPO, Math.min(H - t, naveY + 18 - t * .45));
            SomJogo.tocar(SomJogo.Efeito.CHEFE_ENTRADA);
        }
        intervalo = furia ? 1.2 : 1.6;
    }

    // ---------------- ato V: o demonio do Holy Diver ----------------
    private void holy(double dt, int naveX, int naveY, double cad, List<TiroChefe> tiros, boolean furia) {
        atualizarGancho(dt, naveX, naveY);
        atualizarDemonio(dt, naveY);
        if (avisoChicote > 0) { avisoChicote = Math.max(0, avisoChicote - dt); if (avisoChicote == 0) { chicote = .75; acertouChicote = false; SomJogo.tocar(SomJogo.Efeito.CHEFE_TIRO); } return; }
        if (chicote > 0) { chicote = Math.max(0, chicote - dt); double p = 1 - chicote / .75; anguloChicote = inicioChicote + (fimChicote - inicioChicote) * p * p * (3 - 2 * p); if (chicote == 0) intervalo = Math.max(intervalo, .7); return; }
        if (demonio > 0 || avisoDemonio > 0) return; // mergulhando: o corpo esta atravessando a tela
        if (gancho != 3) flutuar(dt, naveY, .9);
        x += (baseX + Math.sin(tempo * .9) * 24 - x) * Math.min(1, dt * 2);
        if (gancho != 0) return;
        intervalo -= dt * cad;
        if (intervalo > 0 || tiros.size() > 50) return;
        golpes++; gesto = .55;
        int g = golpes % 3;
        if (g == 1) { gancho = 1; faseGancho = 0; }
        else if (g == 2) {
            // chicote: a corrente varre um arco grande a partir da mao; o aviso mostra o caminho
            boolean deCima = naveY + 18 < maoY(); avisoChicote = .8;
            inicioChicote = Math.PI + (deCima ? .95 : -.95); fimChicote = Math.PI + (deCima ? -.95 : .95); anguloChicote = inicioChicote;
            if (furia) { inicioChicote += deCima ? .25 : -.25; fimChicote += deCima ? -.25 : .25; }
        } else { avisoDemonio = 1.0; demonioY = Math.max(TOPO - 40, Math.min(H - TAM[HOLY] + 40, naveY + 18 - TAM[HOLY] / 2.0)); }
        intervalo = furia ? 1.15 : 1.5;
    }
    private static final double ALCANCE_CHICOTE = 760;
    private boolean noChicote(Rectangle r) {
        if (chicote <= 0) return false;
        double ax = r.getCenterX() - maoX(), ay = r.getCenterY() - maoY(), ux = Math.cos(anguloChicote), uy = Math.sin(anguloChicote);
        double ao = ax * ux + ay * uy; if (ao < 0 || ao > ALCANCE_CHICOTE) return false;
        return Math.abs(ax * uy - ay * ux) < 20;
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
        demonioX -= 820 * dt; x = demonioX; y = demonioY;
        if (demonioX < -TAM[HOLY] - 40) { demonio = 0; x = W + 40; entrada = 0; } // volta entrando pela direita
    }
    int consumirDanoGancho() { int d = danoGancho; danoGancho = 0; return d; }
    boolean naveAgarrada() { return ato == HOLY && gancho == 3; }
    double[] puxaoNave() { return ato == HOLY && gancho == 3 ? new double[]{maoX(), maoY()} : null; }
    private boolean naCorrente(Rectangle r) {
        if (gancho != 2 && gancho != 3) return false;
        double ax = maoX(), ay = maoY(), dx2 = hx - ax, dy2 = hy - ay, l2 = dx2 * dx2 + dy2 * dy2;
        double t = l2 == 0 ? 0 : Math.max(0, Math.min(1, ((r.getCenterX() - ax) * dx2 + (r.getCenterY() - ay) * dy2) / l2));
        return Math.hypot(r.getCenterX() - (ax + dx2 * t), r.getCenterY() - (ay + dy2 * t)) < 16 && t < .92;
    }
    private boolean noRaio(double cx, double cy) {
        if (raio <= 0) return false;
        double ax = cx - raioX(), ay = cy - raioY(), ux = Math.cos(direcaoRaio), uy = Math.sin(direcaoRaio);
        double ao = ax * ux + ay * uy; if (ao < 0 || ao > ALCANCE_RAIO) return false;
        return Math.abs(ax * uy - ay * ux) < 22;
    }

    private void tiro(List<TiroChefe> tiros, double x0, double y0, double a, double v, boolean pesado, int estilo, TiroChefe.Movimento m, Color cor) {
        TiroChefe t = new TiroChefe(x0, y0, Math.cos(a) * v, Math.sin(a) * v, pesado, estilo, m);
        tiros.add(cor != null ? t.comCor(cor) : t);
    }

    // ---------------- dano e colisao ----------------
    boolean colide(Rectangle r) {
        if (ato == HOLY && naCorrente(r)) { acertouCorrente = true; return true; }
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
        if (ato == HOLY) return true;
        SomJogo.tocar(SomJogo.Efeito.EXPLOSAO_GRANDE);
        if (ato == JACK) { dentes = 1; aviso = "JACK CAIU! METADE DA PALHETA DO DESTINO"; proximo(KYLE, vidaKyle, 2.2); }
        else if (ato == KYLE) { dentes = 2; aviso = "A PALHETA DO DESTINO SE JUNTOU! A DUPLA INVOCA O DEEZLEBOSS!"; proximo(DEEZLE, vidaDeezle, INVOCACAO); }
        else if (ato == DEEZLE) { aviso = "O DEMONIO SE REVELA: RONNIE JAMES DIO!"; proximo(DIO, vidaDio, 3.0); }
        else { aviso = "DIO VIRA O DEMONIO DO HOLY DIVER!"; proximo(HOLY, vidaHoly, 3.2); }
        return false;
    }
    private void proximo(int novo, int v, double pausa) {
        ato = novo; vida = v; transicao = pausa; entrada = 0; x = W + 40; intervalo = 1.6; golpes = 0; investida = 0; bateria = 0;
        y = TOPO + (H - TOPO - TAM[novo]) / 2.0;
        if (novo == DEEZLE) {
            // o demonio ja comeca de pe onde saiu do portal, sem voar de novo para a tela
            int t = TAM[DEEZLE]; entrada = 1; baseX = W - t - (DEEZLE_ARTE != null ? 110 : 20);
            x = W - 250 - t / 2.0; y = TOPO + (H - TOPO) / 2 - 20 - t / 2.0 + 20;
        }
    }
    boolean estaVivo() { return ato != HOLY || vida > 0; }
    int getVida() { return vida; }
    int getVidaMaxima() { return ato == JACK ? vidaJack : ato == KYLE ? vidaKyle : ato == DEEZLE ? vidaDeezle : ato == DIO ? vidaDio : vidaHoly; }
    String getNome() { return ato == JACK ? "JACK BLACK - TENACIOUS D" : ato == KYLE ? "KYLE GASS - TENACIOUS D" : ato == DEEZLE ? "DEEZLEBOSS" : ato == DIO ? "RONNIE JAMES DIO" : "O DEMONIO DO HOLY DIVER"; }
    Rectangle getBounds() {
        int t = TAM[ato];
        if (ato == DEEZLE || ato == HOLY) return new Rectangle((int) (x + t * .22), (int) (y + t * .12), (int) (t * .56), (int) (t * .78));
        if ((ato == JACK ? JACK_VIOLAO : ato == KYLE ? KYLE_VIOLAO : null) != null) return new Rectangle((int) (x + t * (PE_DUPLA - .22)), (int) (y + t * .1), (int) (t * .44), (int) (t * .82));
        return new Rectangle((int) (x + t * .42), (int) (y + t * .1), (int) (t * .42), (int) (t * .82));
    }
    boolean tocaNave(Rectangle nave) {
        if (transicao > 0 || entrada < 1) return false;
        if (ato == DIO && noRaio(nave.getCenterX(), nave.getCenterY())) return true;
        if (ato == HOLY && noChicote(nave) && !acertouChicote) { acertouChicote = true; return true; }
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
            if (ato == HOLY) desenharAvisos(g);
            int q = quadro();
            if (avisandoInvestida()) { int t = TAM[ato]; g.setColor(new Color(255, 40, 20, (int) (90 + 80 * Math.sin(tempo * 30)))); g.setStroke(new BasicStroke(3)); g.drawLine((int) (x + t * .4), (int) (y + t * .5), (int) alvoInvX + 60, (int) alvoInvY + t / 2); }
            corpo(g, ato, q, x, y + (q == 3 && ato != HOLY ? 4 : 0), TAM[ato], 1f);
            if (ato == DIO) {
                if (cargaRaio > 0) { int r = (int) (10 + (1 - cargaRaio) * 28 + Math.sin(tempo * 40) * 3); g.setColor(Color.getHSBColor((float) (tempo * 2 % 1), .8f, 1f)); g.fillOval((int) raioX() - r, (int) raioY() - r, r * 2, r * 2); }
                if (raio > 0) desenharRaio(g);
            }
            if (ato == HOLY) { if (gancho >= 2) desenharCorrente(g); if (chicote > 0) desenharChicote(g, anguloChicote, 1f); }
        }
        if (flash > 0) {
            g.setColor(new Color(255, 220, 160, (int) (flash / .16 * 200))); Rectangle b = getBounds();
            for (int i = 0; i < 6; i++) { double an = i * Math.PI / 3; int r = 15 + (int) ((.16 - flash) * 180); g.fillRect((int) b.getCenterX() + (int) (Math.cos(an) * r), (int) b.getCenterY() + (int) (Math.sin(an) * r), 4, 4); }
        }
        g.dispose();
    }
    private int quadro() {
        if (ato == DIO) return raio > 0 || cargaRaio > 0 ? (DIO_ESPADA != null ? 3 : 2) : investida > 0 && investida <= .8 ? 2 : avisandoInvestida() ? 1 : gesto > .3 ? 2 : gesto > 0 ? 1 : 0;
        if (ato == HOLY) return demonio > 0 ? 3 : chicote > 0 || gancho >= 2 ? 2 : avisoChicote > 0 || gancho == 1 || avisoDemonio > 0 ? 1 : 0;
        if (ato == DEEZLE && bateria > 0) return DEEZLE_ARTE != null ? 3 : 1;
        if (avisandoInvestida()) return 1;
        if (gesto > .25) return 2;
        if (gesto > 0) return 1;
        return (int) (tempo * 1.6) % 4 == 3 ? 3 : 0;
    }
    /** Jack e Kyle tocando la no fundo enquanto o Deezleboss luta (nao levam tiro). */
    private void dupla(Graphics2D g, float a) {
        int s = 150; int q = (int) (tempo * 2.4) % 2 == 0 ? 0 : 3;
        corpo(g, JACK, q, W - s - 10, TOPO + 4 + Math.sin(tempo * 2) * 6, s, a * .85f);
        corpo(g, KYLE, q == 0 ? 3 : 0, W - s - 10, H - s - 4 + Math.sin(tempo * 2 + 1) * 6, s, a * .85f);
    }
    private void desenharTransicao(Graphics2D g) {
        // o ato que caiu some piscando; as metades da Palheta do Destino contam a historia
        double p = 1 - transicao / (ato == KYLE ? 2.2 : ato == DEEZLE ? INVOCACAO : ato == HOLY ? 3.2 : 3.0);
        int ant = ato - 1;
        if (ato == HOLY) {
            // o Dio ergue os chifres, some na fumaca roxa e o demonio sai dela girando a corrente
            if (p < .6) corpo(g, DIO, 3, W - TAM[DIO] - 40, TOPO + (H - TOPO - TAM[DIO]) / 2.0, TAM[DIO], (float) Math.max(0, 1 - p / .6));
            java.util.Random r = new java.util.Random(5);
            int forca = (int) (220 * Math.sin(Math.min(1, p) * Math.PI));
            for (int k = 0; k < 90; k++) { double a = r.nextDouble() * Math.PI * 2 + p * (3 + r.nextDouble() * 3), d = 30 + r.nextInt(170) * (.4 + p); int q = 4 + r.nextInt(3) * 2;
                g.setColor(k % 4 == 0 ? new Color(255, 80, 60, forca) : new Color(110 + r.nextInt(70), 30, 150 + r.nextInt(80), forca)); g.fillRect(W - 210 + (int) (Math.cos(a) * d), H / 2 + (int) (Math.sin(a) * d * .8), q, q); }
            if (p > .45) { float a = (float) Math.min(1, (p - .45) / .4); int t = (int) (TAM[HOLY] * (.5 + .5 * a)); corpo(g, HOLY, 0, W - t - 30, TOPO + (H - TOPO - t) / 2.0, t, a); }
            return;
        }
        if (ato == DEEZLE) { invocacao(g, transicao > 0 ? INVOCACAO - transicao : INVOCACAO); return; }
        if (p < .5) {
            float a = (float) Math.max(0, 1 - p * 2);
            if ((int) (tempo * 20) % 2 == 0) corpo(g, ant, 3, W - TAM[ant] - 40, TOPO + (H - TOPO - TAM[ant]) / 2.0, TAM[ant], a);
        }
        if (ato == DIO) {
            // clarao arco-iris: o demonio vira o Dio
            float a = (float) Math.min(1, p * 1.6);
            if (p < .6) corpo(g, DEEZLE, 3, W - TAM[DEEZLE] - 20, TOPO + (H - TOPO - TAM[DEEZLE]) / 2.0, TAM[DEEZLE], 1f - a * .9f);
            if (p > .4) corpo(g, DIO, 0, W - TAM[DIO] - 40, TOPO + (H - TOPO - TAM[DIO]) / 2.0, TAM[DIO], (float) Math.min(1, (p - .4) / .4));
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
    /** Invocacao do Deezleboss, em segundos desde o Kyle cair. */
    private void invocacao(Graphics2D g, double t) {
        int px = W - 250, py = TOPO + (H - TOPO) / 2 - 20;            // centro do portal
        int s = 190, q = (int) (tempo * 3) % 2 == 0 ? 0 : 3;
        int jx = 230, kx = 390, chao = H - s - 8;                       // a dupla de pe a esquerda, olhando o portal
        double maos = chao + s * .35;
        // 1) portal: abre com o raio (2.6 s), fica aberto e fecha depois que o demonio sai (6.2 s)
        double abre = t < 2.6 ? 0 : Math.min(1, (t - 2.6) / .7), fecha = t < 6.2 ? 1 : Math.max(0, 1 - (t - 6.2) / .6);
        double tam = abre * fecha;
        if (tam > 0) portal(g, px, py, (int) (330 * tam));
        // 2) o demonio sai do portal de bracos erguidos (3.4 s a 5.6 s) e fica na frente dele
        if (t > 3.4) { double e = Math.min(1, (t - 3.4) / 2.2), ee = e * e * (3 - 2 * e); int tt = (int) (TAM[DEEZLE] * (.25 + .75 * ee));
            corpo(g, DEEZLE, 1, px - tt / 2.0, py - tt / 2.0 + (1 - ee) * 30 + 20, tt, (float) Math.min(1, e * 2)); }
        // 3) a dupla: aparece e ergue as metades; sai de cena quando o demonio ja esta de pe
        float d = (float) Math.min(1, t / .5) * (float) Math.max(0, Math.min(1, (INVOCACAO - t) / .6));
        corpo(g, JACK, t < 2.6 ? 3 : q, jx, chao, s, d); corpo(g, KYLE, t < 2.6 ? 3 : 3 - q, kx, chao + 4, s, d);
        // 4) as metades: sobem das maos (0.4-1.2 s), giram e se juntam no meio (1.2-2.0 s), brilho (2.0-2.6 s)
        double mx = (jx + kx + s) / 2.0, my = TOPO + 120;
        if (t > .4 && t < 2.0) {
            double sobe = Math.min(1, (t - .4) / .8), junta = t < 1.2 ? 0 : Math.min(1, (t - 1.2) / .8), jj = junta * junta;
            double ax = jx + s * .45 + (mx - 40 - (jx + s * .45)) * jj, bx = kx + s * .45 + (mx - (kx + s * .45)) * jj;
            double ay = maos - (maos - my) * sobe + Math.sin(t * 9) * 6 * (1 - jj), by = ay + Math.sin(t * 9 + 1) * 6 * (1 - jj);
            brilho(g, ax + 20, ay + 34, 34 + 10 * Math.sin(t * 12)); brilho(g, bx + 20, by + 34, 34 + 10 * Math.sin(t * 12 + 1));
            palheta(g, 0, ax, ay, 68, 1f); palheta(g, 1, bx, by, 68, 1f);
        } else if (t >= 2.0 && t < 6.2) {
            double fim = t < 3.4 ? 1 : Math.max(0, 1 - (t - 3.4) / .6);
            if (t < 2.4) { // clarao do encaixe
                int r = (int) (40 + (t - 2.0) * 900); g.setColor(new Color(160, 255, 170, (int) (230 * (1 - (t - 2.0) / .4)))); g.setStroke(new BasicStroke(6)); g.drawOval((int) mx - r, (int) my + 34 - r, r * 2, r * 2);
                g.setColor(new Color(220, 255, 220, (int) (120 * (1 - (t - 2.0) / .4)))); g.fillRect(0, TOPO, W, H - TOPO); }
            if (fim > .05) brilho(g, mx, my + 34, (46 + 12 * Math.sin(t * 10)) * fim);
            if (t >= 2.4 && t < 3.2) { // o raio da palheta rasga o ar ate o centro do portal
                float a = (float) (1 - (t - 2.4) / .8); Graphics2D r = (Graphics2D) g.create();
                r.setColor(new Color(120, 255, 140, (int) (230 * a))); r.setStroke(new BasicStroke(10)); r.drawLine((int) mx, (int) my + 34, px, py);
                r.setColor(new Color(240, 255, 240, (int) (255 * a))); r.setStroke(new BasicStroke(4)); r.drawLine((int) mx, (int) my + 34, px, py); r.dispose(); }
            Graphics2D a = (Graphics2D) g.create(); a.setComposite(AlphaComposite.SrcOver.derive((float) fim));
            palheta(a, 2, mx - 38, my, 76, 1f); a.dispose();
        }
        // legendas da cena
        String txt = t < 1.2 ? "AS DUAS METADES..." : t < 2.4 ? "A PALHETA DO DESTINO!" : t < 3.6 ? "O PORTAL SE ABRE..." : t < 6.2 ? "DEEZLEBOSS!" : "";
        if (!txt.isEmpty()) { g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, txt.equals("DEEZLEBOSS!") ? 34 : 22)); int w = g.getFontMetrics().stringWidth(txt);
            g.setColor(new Color(0, 0, 0, 180)); g.drawString(txt, (W - w) / 2 + 2, H - 40 + 2); g.setColor(txt.equals("DEEZLEBOSS!") ? new Color(255, 90, 60) : new Color(170, 255, 180)); g.drawString(txt, (W - w) / 2, H - 40); }
    }
    private static void brilho(Graphics2D g, double cx, double cy, double r) {
        for (int k = 3; k >= 1; k--) { int rr = (int) (r * k / 3); g.setColor(new Color(90, 255, 120, 40 + (3 - k) * 30)); g.fillOval((int) cx - rr, (int) cy - rr, rr * 2, rr * 2); }
    }
    /** Portal do inferno girando; sem a arte, aneis de fagulhas vermelhas e laranjas puxadas para o centro. */
    private void portal(Graphics2D tela, int cx, int cy, int d) {
        if (d <= 2) return;
        Graphics2D g = (Graphics2D) tela.create();
        if (PORTAL != null) { int fw = PORTAL.getWidth() / 4, fh = PORTAL.getHeight(), q = (int) (tempo * 10) % 4, w = d * fw / fh;
            g.drawImage(PORTAL, cx - w / 2, cy - d / 2, cx + w / 2, cy + d / 2, q * fw, 0, q * fw + fw, fh, null); g.dispose(); return; }
        g.setColor(new Color(20, 0, 10, 230)); g.fillOval(cx - d / 2 + d / 8, cy - d / 2 + d / 8, d * 3 / 4, d * 3 / 4);
        java.util.Random r = new java.util.Random(9);
        for (int k = 0; k < 220; k++) {
            double raio = d / 2.0 * (.35 + .65 * r.nextDouble()), a = r.nextDouble() * Math.PI * 2 + tempo * (2.5 - raio / d * 2);
            int x = cx + (int) (Math.cos(a) * raio), y = cy + (int) (Math.sin(a) * raio), tq = 3 + r.nextInt(3) * 2;
            g.setColor(r.nextInt(3) == 0 ? new Color(255, 210, 80) : r.nextBoolean() ? new Color(255, 90, 30) : new Color(190, 20, 30)); g.fillRect(x, y, tq, tq);
        }
        g.dispose();
    }
    private void desenharRaio(Graphics2D tela) {
        Graphics2D g = (Graphics2D) tela.create();
        double bx = raioX(), by = raioY(), ux = Math.cos(direcaoRaio), uy = Math.sin(direcaoRaio);
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
    /** Avisos do demonio do Holy Diver: faixa do mergulho e o arco que a corrente vai varrer. */
    private void desenharAvisos(Graphics2D g) {
        if (avisoDemonio > 0) {
            g.setColor(new Color(170, 40, 255, (int) (60 + 60 * Math.sin(tempo * 25))));
            g.fillRect(0, (int) (demonioY + TAM[HOLY] * .25), W, (int) (TAM[HOLY] * .5));
            g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 18)); g.setColor(new Color(230, 180, 255));
            g.drawString("<<< HOLY DIVER", W - 200, (int) (demonioY + TAM[HOLY] * .5));
        }
        if (avisoChicote > 0) {
            Graphics2D a = (Graphics2D) g.create(); a.setColor(new Color(255, 60, 60, (int) (70 + 60 * Math.sin(tempo * 30))));
            int r = (int) ALCANCE_CHICOTE, gi = (int) Math.toDegrees(-inicioChicote), gf = (int) Math.toDegrees(-fimChicote);
            a.fillArc((int) maoX() - r, (int) maoY() - r, r * 2, r * 2, Math.min(gi, gf), Math.abs(gf - gi)); a.dispose();
            desenharChicote(g, inicioChicote, .5f);
        }
    }
    /** A corrente esticada a partir da mao, na direcao do angulo. */
    private void desenharChicote(Graphics2D tela, double ang, float alfa) {
        Graphics2D g = (Graphics2D) tela.create(); g.setComposite(AlphaComposite.SrcOver.derive(alfa));
        double ax = maoX(), ay = maoY();
        if (ELO != null) {
            int ew = ELO.getWidth() * 3 / 4, eh = ELO.getHeight() * 3 / 4;
            g.translate(ax, ay); g.rotate(ang);
            for (double t = 0; t < ALCANCE_CHICOTE; t += ew * .8) g.drawImage(ELO, (int) t, -eh / 2 + (int) (Math.sin(t * .02 + tempo * 20) * 4), ew, eh, null);
        } else { g.setColor(new Color(150, 150, 160)); g.setStroke(new BasicStroke(5)); g.drawLine((int) ax, (int) ay, (int) (ax + Math.cos(ang) * ALCANCE_CHICOTE), (int) (ay + Math.sin(ang) * ALCANCE_CHICOTE)); }
        g.dispose();
    }
    /** Corpo de cada ato: arte propria do Dio de espada e do demonio do Holy Diver quando existir; senao a grade. */
    private static void corpo(Graphics2D g, int ato, int q, double x, double y, int t, float alfa) {
        BufferedImage arte = ato == DIO ? DIO_ESPADA : ato == HOLY ? HOLY_DIVER : ato == DEEZLE ? DEEZLE_ARTE : ato == JACK ? JACK_VIOLAO : ato == KYLE ? KYLE_VIOLAO : null;
        if (arte == null && ato == HOLY && ARCO != null) arte = escuro();
        if (arte == null) { sprite(g, ato - 1, q, x, y, t, alfa); return; }
        int fw = arte.getWidth() / (arte == demonioEscuro ? 2 : 4), fh = arte.getHeight(), qq = arte == demonioEscuro ? (q == 2 || q == 3 ? 1 : 0) : q;
        int w = t * fw / fh;
        // Jack e Kyle: o golpe ocupa a esquerda do quadro; os pes ficam no mesmo ponto da caixa do ato
        int x0 = ato == JACK || ato == KYLE ? (int) (x + t * PE_DUPLA - pe(arte, fw, fh) * w) : (int) x + (t - w) / 2;
        Graphics2D a = (Graphics2D) g.create();
        if (alfa < 1) a.setComposite(AlphaComposite.SrcOver.derive(Math.max(0f, Math.min(1f, alfa))));
        a.drawImage(arte, x0, (int) y, x0 + w, (int) y + t, qq * fw, 0, qq * fw + fw, fh, null);
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
    /** Metade (0 esquerda, 1 direita) ou a palheta inteira (2). Sem a arte, uma ponta verde simples. */
    private static void palheta(Graphics2D g, int q, double x, double y, int t, float alfa) {
        Graphics2D a = (Graphics2D) g.create(); a.setComposite(AlphaComposite.SrcOver.derive(Math.max(0f, Math.min(1f, alfa))));
        if (PALHETA != null) { int fw = PALHETA.getWidth() / 3, fh = PALHETA.getHeight(), w = t * fw / fh; a.drawImage(PALHETA, (int) x, (int) y, (int) x + w, (int) y + t, q * fw, 0, q * fw + fw, fh, null); }
        else {
            int w = q == 2 ? t : t / 2, x0 = (int) x, y0 = (int) y;
            int[] px = q == 0 ? new int[]{x0, x0 + w, x0 + w} : q == 1 ? new int[]{x0, x0 + w, x0} : new int[]{x0, x0 + w, x0 + w / 2};
            int[] py = q == 0 ? new int[]{y0, y0, y0 + t} : q == 1 ? new int[]{y0, y0, y0 + t} : new int[]{y0, y0, y0 + t};
            a.setColor(new Color(40, 150, 70)); a.fillPolygon(px, py, 3); a.setColor(new Color(10, 40, 20)); a.drawPolygon(px, py, 3);
        }
        a.dispose();
    }
    /** Onde ficam os pes do Jack e do Kyle dentro da caixa do ato (com a arte de violao). */
    private static final double PE_DUPLA = .35;
    private static final java.util.Map<BufferedImage, Double> PES = new java.util.HashMap<>();
    /** Onde ficam os pes no primeiro quadro (fracao da largura), medido uma vez. */
    private static double pe(BufferedImage arte, int fw, int fh) {
        Double v = PES.get(arte); if (v != null) return v;
        long soma = 0, n = 0;
        for (int yy = fh * 9 / 10; yy < fh; yy++) for (int xx = 0; xx < fw; xx++) if ((arte.getRGB(xx, yy) >>> 24) > 0) { soma += xx; n++; }
        v = n == 0 ? .5 : soma / (double) n / fw; PES.put(arte, v); return v;
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
        String[] atos = {"", "  [ATO I]", "  [ATO II]", "  [ATO III]", "  [ATO IV]", "  [ATO V]"};
        Color c = ato == JACK ? new Color(255, 120, 40) : ato == KYLE ? new Color(80, 220, 90) : ato == DEEZLE ? new Color(220, 40, 30) : ato == DIO ? new Color(170, 90, 255) : new Color(120, 40, 200);
        double frac = Math.max(0, vida) / (double) Math.max(1, getVidaMaxima());
        if (BARRA != null) {
            // moldura da arte: a vida enche a faixa preta e as metades da palheta acendem nos escudos
            int bx = 140, bw = 680, bh = bw * BARRA.getHeight() / BARRA.getWidth(), by = TOPO - 2;
            int fx = bx + (int) (bw * .118), fw = (int) (bw * (.773 - .118)), fy = by + (int) (bh * .364), fh = (int) (bh * (.616 - .364));
            g.drawImage(BARRA, bx, by, bw, bh, null);
            // o miolo da moldura e preto opaco: a vida vai por cima dele
            g.setPaint(new GradientPaint(fx, fy, c.brighter(), fx, fy + fh, c.darker().darker()));
            g.fillRect(fx + 2, fy + 2, (int) ((fw - 4) * frac), fh - 4);
            g.setColor(new Color(255, 255, 255, 90)); g.fillRect(fx + 2, fy + 3, (int) ((fw - 4) * frac), 2);
            g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13)); String nome = getNome() + atos[ato];
            int tx = fx + 10, ty = fy + fh / 2 + 5;
            g.setColor(new Color(0, 0, 0, 200)); g.drawString(nome, tx + 1, ty + 1); g.setColor(Color.WHITE); g.drawString(nome, tx, ty);
            double[] escudo = {.791, .84};
            for (int k = 0; k < 2; k++) { int sx = bx + (int) (bw * escudo[k]), sy = by + (int) (bh * .36), sh = (int) (bh * .26);
                palheta(g, k, sx + 2, sy, sh, k < dentes ? 1f : .18f); }
            return;
        }
        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14)); g.setColor(Color.WHITE);
        g.drawString(getNome() + atos[ato], 217, TOPO + 24);
        g.setColor(new Color(8, 22, 34, 222)); g.fillRoundRect(218, TOPO + 31, 526, 22, 10, 10);
        g.setPaint(new GradientPaint(218, TOPO + 32, c, 744, TOPO + 52, new Color(222, 36, 70)));
        g.fillRoundRect(220, TOPO + 33, (int) (522.0 * frac), 18, 8, 8);
        g.setColor(new Color(255, 255, 255, 130)); g.drawRoundRect(218, TOPO + 31, 526, 22, 10, 10);
        for (int k = 0; k < 2; k++) palheta(g, k, 760 + k * 15, TOPO + 30, 26, k < dentes ? 1f : .25f);
    }

    private static BufferedImage ler(String n) {
        java.net.URL u = ChefeFinal.class.getResource("/ImagemIcon/" + n);
        if (u == null) return null;
        try { return javax.imageio.ImageIO.read(u); } catch (java.io.IOException e) { return null; }
    }
}
