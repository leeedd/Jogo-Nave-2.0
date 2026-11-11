package Jogo;

import java.util.Random;

/**
 * Trilha 16-bit original gerada na hora, uma por planeta: cada universo tem o seu subgenero do
 * metal (docs/SUBGENEROS.md) e cada planeta, o tema do mapa e o chefe ganham melodia, tom e
 * arranjo proprios a partir de uma semente. Nada de arquivo: o som sai daqui em 44,1 kHz mono.
 * Nomes: "universo_S_V" (S = 0..8; V = planeta, 50 = tema do mapa, 99 = chefe).
 */
final class MusicaChip {
    static final int TAXA = 44100, TEMA = 50, CHEFE = 99;
    // por universo: bpm, tom (midi da tonica), escala, tempos por compasso
    private static final int[] BPM = {66, 170, 150, 112, 200, 124, 132, 100, 220};
    private static final int[] TOM = {38, 40, 45, 40, 40, 37, 50, 38, 42};
    private static final int[] TEMPOS = {4, 4, 4, 4, 4, 4, 6, 4, 4};
    private static final int[][] ESCALA = {
        {0, 2, 3, 5, 7, 8, 10}, {0, 2, 3, 5, 7, 8, 10}, {0, 2, 4, 5, 7, 9, 11}, {0, 1, 3, 5, 7, 8, 10}, {0, 1, 3, 5, 6, 8, 10},
        {0, 1, 3, 5, 7, 8, 11}, {0, 2, 3, 5, 7, 9, 10}, {0, 2, 3, 5, 7, 8, 11}, {0, 1, 3, 5, 7, 8, 10}};
    /** Progressoes (graus da escala) que cada subgenero gosta. */
    private static final int[][][] PROG = {
        {{0, 5, 3, 4}, {0, 3, 5, 4}}, {{0, 5, 6, 0}, {0, 6, 5, 6}}, {{0, 4, 5, 3}, {5, 3, 0, 4}}, {{0, 1, 0, 6}, {0, 0, 1, 1}},
        {{0, 1, 0, 4}, {0, 6, 5, 1}}, {{0, 1, 5, 4}, {0, 4, 1, 0}}, {{0, 6, 3, 0}, {0, 3, 6, 4}}, {{0, 5, 2, 6}, {0, 3, 4, 4}}, {{0, 5, 3, 1}, {0, 1, 5, 4}}};

    static boolean reconhece(String nome) { return nome != null && (nome.startsWith("universo_") || nome.matches("chefe_[1-8]") || nome.equals("abertura") || nome.startsWith("mapa_")); }
    static final int ABERTURA = 77;

    /** Monta a trilha do nome (universo_S_V ou chefe_N). */
    static float[] gerar(String nome) {
        if (nome.equals("abertura")) return abertura();
        if (nome.startsWith("mapa_")) return nome.endsWith("0") ? aberturaRock() : hino(); // mapa: rock de videogame e hino, revezando
        if (nome.startsWith("chefe_")) { int c = Integer.parseInt(nome.substring(6)); int[] ordem = {8, 2, 3, 4, 5, 6, 7, 1}; for (int s = 0; s < 8; s++) if (ordem[s] == c) return gerar(s, CHEFE); }
        String[] p = nome.split("_"); return gerar(Integer.parseInt(p[1]), Integer.parseInt(p[2]));
    }

    static float[] gerar(int u, int v) {
        u = Math.max(0, Math.min(8, u));
        if (u == 1 && v != CHEFE) return abertura(v == TEMA ? 0 : v % 3); // Fornalha (heavy classico): galope, cavalgada e hino nos planetas
        Random r = new Random(u * 1000L + v * 7919L + 13);
        int tempos = TEMPOS[u]; double bpm = BPM[u] * (v == CHEFE ? 1.06 : 1 + (r.nextInt(5) - 2) * .02);
        double batida = 60 / bpm * (tempos == 6 ? .5 : 1), comp = batida * tempos;
        int tom = TOM[u] + (v == TEMA ? 0 : v == CHEFE ? 0 : new int[]{0, 2, -2, 3, 5, -3, 1}[v % 7]);
        int[] esc = ESCALA[u], prog = PROG[u][(v + (v == CHEFE ? 1 : 0)) % 2];
        int intro = v == TEMA ? 1 : 2, partes = Math.max(8, (int) (34 / comp) / 4 * 4), total = intro + partes;
        Mix m = new Mix((int) (total * comp * TAXA) + TAXA, r);
        int[] melodia = new int[tempos * 4]; int grau = 4 + r.nextInt(3);
        for (int i = 0; i < melodia.length; i++) { grau = Math.max(0, Math.min(9, grau + r.nextInt(5) - 2)); melodia[i] = r.nextInt(6) == 0 ? -1 : grau; }
        boolean duplo = v == CHEFE || u == 2 || u == 5 || u == 8;
        for (int c = 0; c < total; c++) {
            double t0 = c * comp; int g = prog[c % 4]; int raiz = tom + esc[g % 7];
            int terca = tom + esc[(g + 2) % 7] + ((g + 2) >= 7 ? 12 : 0), quinta = tom + esc[(g + 4) % 7] + ((g + 4) >= 7 ? 12 : 0);
            boolean banda = c >= intro, segunda = c >= intro + partes / 2;
            switch (u) {
                case 0: // doom: acorde arrastado e pesado, sino fundo, bumbo no 1, caixa no 3
                    m.acorde(new int[]{raiz + 12, quinta + 12, raiz + 24}, t0, comp, .5, .09, true);
                    if (c % 2 == 0) m.sino(raiz + 36, t0, .1);
                    if (banda) { m.nota("sq", raiz, t0, comp * .98, .5, .14, 0); m.bumbo(t0, .45); m.caixa(t0 + 2 * batida, .25); for (int k = 0; k < 4; k++) m.chimbal(t0 + k * batida, .03); }
                    break;
                case 1: case 4: { // heavy/speed e thrash: palhetada em colcheias (thrash em semicolcheias abafadas)
                    int sub = u == 4 ? 4 : 2; double d = batida / sub;
                    if (banda) for (int k = 0; k < tempos * sub; k++) { int n = k % 8 == 6 && u == 4 ? raiz + 1 : raiz; m.nota("sq", n + 12, t0 + k * d, d * .7, u == 4 ? .125 : .25, .08, 0); m.nota("sq", n + 19, t0 + k * d, d * .7, .5, .03, 0); }
                    if (banda) for (int k = 0; k < tempos; k++) {
                        if (u == 1) { m.nota("sq", raiz - 12, t0 + k * batida, batida * .45, .5, .1, 0); m.nota("sq", raiz - 12, t0 + k * batida + batida / 2, batida / 4 * .9, .5, .1, 0); m.nota("sq", raiz - 12, t0 + k * batida + batida * .75, batida / 4 * .9, .5, .1, 0); }
                        else m.nota("sq", raiz - 12, t0 + k * batida, batida * .9, .5, .1, 0);
                        m.bumbo(t0 + k * batida, .35); if (u == 4 || duplo && segunda) m.bumbo(t0 + k * batida + batida / 2, .3);
                        if (k % 2 == 1 || u == 4) m.caixa(t0 + k * batida + (u == 4 ? batida / 2 : 0), .2); m.chimbal(t0 + k * batida + batida / 2, .04);
                    } else m.acorde(new int[]{raiz + 12, quinta + 12}, t0, comp, .25, .08, false);
                    break; }
                case 2: case 7: case 8: { // power, sinfonico e black: orgao/acorde, arpejo, bumbo duplo
                    m.acorde(new int[]{raiz + 12, terca + 12, quinta + 12, raiz + 24}, t0, comp, .5, u == 8 ? .05 : .045, u == 7);
                    if (u == 7 && (c < intro || c % 4 == 0)) m.sino(raiz + 36, t0, c < intro ? .12 : .05);
                    if (!banda) break;
                    int[] arp = {raiz + 24, terca + 24, quinta + 24, terca + 24}; double d = batida / 4;
                    for (int k = 0; k < tempos * 4; k++) m.nota("sq", u == 8 ? arp[(k / 2) % 4] + 12 : arp[k % 4] + (segunda && k % 8 >= 4 ? 12 : 0), t0 + k * d, d * .8, .25, u == 8 ? .035 : .05, 0);
                    for (int k = 0; k < tempos; k++) {
                        m.nota("sq", raiz - 12, t0 + k * batida, batida / 2 * .9, .5, .09, 0); m.nota("sq", raiz - 12, t0 + k * batida + batida / 2, batida / 4 * .9, .5, .09, 0); m.nota("sq", raiz - 12, t0 + k * batida + batida * .75, batida / 4 * .9, .5, .09, 0);
                        m.bumbo(t0 + k * batida, .33); if (duplo && segunda || u == 8) { m.bumbo(t0 + k * batida + batida / 2, .3); }
                        if (u == 8) { m.caixa(t0 + k * batida + batida / 4, .12); m.caixa(t0 + k * batida + batida * .75, .12); } else if (k % 2 == 1) m.caixa(t0 + k * batida, .18);
                        m.chimbal(t0 + k * batida + batida / 2, .045);
                    }
                    break; }
                case 3: { // industrial: baixo serrilhado em semicolcheias, batida metalica 4 no chao
                    double d = batida / 4;
                    if (banda) for (int k = 0; k < tempos * 4; k++) if (k % 4 != 3) m.nota("saw", raiz - 12 + (k % 8 == 5 ? 12 : 0), t0 + k * d, d * .7, 0, .1, 0);
                    for (int k = 0; k < tempos; k++) { m.bumbo(t0 + k * batida, banda ? .4 : .2); if (banda && k % 2 == 1) m.metal(t0 + k * batida, .16); if (banda) m.chimbal(t0 + k * batida + batida / 2, .05); }
                    if (c % 2 == 0) m.acorde(new int[]{raiz + 13, raiz + 19}, t0, batida, .125, .06, false);
                    break; }
                case 5: { // death: groove arrastado e grave (escola da Florida); na segunda metade acelera pro blast
                    double d = batida / 4;
                    if (!segunda) { // riff em meio-tempo: palhetada abafada, cromatismo descendo, pausas pesadas
                        int[] riff = {0, 0, -1, 0, 1, 0, -1, -1, 0, 0, -1, 3, 2, 1, 0, -1};
                        for (int k = 0; k < tempos * 4; k++) { int x = riff[k % 16]; if (x < 0 && k % 16 != 2) continue; int n = raiz + (x < 0 ? 0 : x);
                            m.nota("sq", n, t0 + k * d, d * (x < 0 ? .4 : .85), .5, banda ? .09 : .05, 0); m.nota("saw", n - 12, t0 + k * d, d * .85, 0, banda ? .07 : .03, 0); }
                        if (banda) for (int k = 0; k < tempos; k++) { m.bumbo(t0 + k * batida, .4); if (k == 1) m.bumbo(t0 + k * batida + batida / 2, .35); if (k == 2) m.caixa(t0 + k * batida, .26); m.chimbal(t0 + k * batida + batida / 2, .04); }
                    } else { // blast: tremolo grave e caixa/bumbo alternando em semicolcheias
                        for (int k = 0; k < tempos * 4; k++) m.nota("sq", raiz + (k % 16 >= 12 ? 1 : 0), t0 + k * d / 1.5, d / 1.5 * .8, .5, .07, 0);
                        for (int k = 0; k < tempos * 6; k++) { if (k % 2 == 0) m.bumbo(t0 + k * d / 1.5, .28); else m.caixa(t0 + k * d / 1.5, .12); if (k % 2 == 0) m.chimbal(t0 + k * d / 1.5, .035); }
                        m.nota("sq", raiz - 12, t0, comp * .98, .5, .1, 0);
                    }
                    break; }
                case 6: { // folk: compasso 6/8, flauta, drone e tambor tribal
                    m.nota("tri", raiz - 12, t0, comp, 0, .12, 0); m.nota("sq", quinta, t0, comp, .5, .025, 0);
                    if (banda) for (int k = 0; k < 6; k++) { if (k == 0 || k == 3) m.bumbo(t0 + k * batida, .35); if (k == 2 || k == 5) m.tom(t0 + k * batida, raiz + 12, .2); if (k == 3) m.caixa(t0 + k * batida, .14); m.chimbal(t0 + k * batida, .025); }
                    break; }
            }
            if ((u == 2 || u == 7 || u == 8 || v == CHEFE) && c % 2 == 0) { m.timpano(t0, raiz + 12, banda ? .25 : .32); if (banda && c % 4 == 0) for (int n : new int[]{raiz + 12, terca + 12, quinta + 12}) m.orquestra("metais", n, t0, batida * 2, .06); }
            if (c == intro - 1) m.subida(t0 + comp, Math.min(2, comp), .2);
            // melodia: entra na banda; flauta/triangular em quase todos, pulso fino no thrash/death
            if (banda && (c >= intro + 2 || v == TEMA)) {
                int passo = u == 4 || u == 5 ? 2 : 1, n = tempos * passo; double d = batida / passo;
                for (int k = 0; k < n; k++) {
                    int idx = ((c - intro) * n + k) % melodia.length, gm = melodia[idx]; if (gm < 0) continue;
                    int nota = tom + 24 + esc[gm % 7] + 12 * (gm / 7) + (u == 6 ? 12 : 0);
                    m.nota("tri", nota, t0 + k * d, d * .92, 0, u == 0 ? .11 : .1, u == 3 ? 0 : 2);
                    m.nota("sq", nota - 12, t0 + k * d, d * .92, .125, .025, 2);
                    if (u == 2) m.nota("tri", tom + 24 + esc[(gm + 2) % 7] + 12 * ((gm + 2) / 7), t0 + k * d, d * .92, 0, .05, 2); // terca dobrada do power
                }
            }
        }
        return m.m64 ? m.fechar64((int) (total * comp * TAXA)) : m.fechar((int) (total * comp * TAXA));
    }


    /**
     * Abertura: heavy metal classico, composto para o jogo. Prato e riff em galope ja no primeiro
     * segundo, guitarras gemeas em tercas, parada com rufar, refrao de hino com acordes abertos e
     * volta ao galope. Mi menor, 152 bpm, guitarra com amplificador simulado.
     */
    static float[] abertura() { return menu(); }
    /** Versoes da abertura para comparar: 0 galope classico, 1 cavalgada rapida (la menor), 2 hino de meio-tempo (re menor). */
    static float[] abertura(int versao) {
        Random r = new Random(1980 + versao);
        int tr = versao == 1 ? 5 : versao == 2 ? -2 : 0; double bpm = versao == 1 ? 176 : versao == 2 ? 128 : 152;
        double b = 60 / bpm, d = b / 4, comp = 4 * b; Mix m = new Mix((int) (60 * TAXA), r);
        int[] riff = versao == 1 ? new int[]{40, 40, 43, 41, 40, 40, 36, 38} : versao == 2 ? new int[]{40, 36, 38, 35, 40, 36, 43, 38} : new int[]{40, 40, 36, 38, 40, 40, 43, 38}; // raiz de cada compasso
        int[][] gancho = versao == 1 ? new int[][]{{76, 74, 76, 79, 76, 74, 72, 71}, {72, 74, 76, 72, 71, 69, 67, 69}, {76, 74, 76, 79, 81, 79, 76, 79}, {83, 81, 79, 78, 79, -1, 76, -1},
                          {76, 74, 76, 79, 76, 74, 72, 71}, {72, 74, 76, 72, 71, 69, 67, 69}, {67, 69, 71, 72, 74, 76, 78, 79}, {76, -1, -1, -1, -1, -1, -1, -1}}
                       : versao == 2 ? new int[][]{{64, -1, 67, 69, 71, -1, 69, 67}, {72, -1, 71, 69, 67, -1, 64, -1}, {74, -1, 72, 71, 69, -1, 71, 72}, {71, -1, -1, -1, 66, -1, -1, -1},
                          {64, -1, 67, 69, 71, -1, 74, 76}, {79, -1, 78, 76, 74, -1, 72, -1}, {71, 72, 74, 72, 71, -1, 69, -1}, {64, -1, -1, -1, -1, -1, -1, -1}}
                       : new int[][]{{71, 76, 78, 79, 78, 76, 74, 76}, {71, 76, 78, 79, 81, 79, 78, 74}, {72, 76, 79, 84, 83, 79, 76, 72}, {74, 78, 81, 86, 84, 81, 78, 81},
                          {71, 76, 78, 79, 78, 76, 74, 76}, {71, 76, 78, 79, 81, 79, 78, 74}, {72, 74, 76, 79, 78, 76, 74, 71}, {76, -1, -1, -1, -1, -1, -1, -1}};
        int[] coroRaiz = versao == 1 ? new int[]{40, 36, 43, 38, 40, 36, 38, 35} : versao == 2 ? new int[]{40, 43, 38, 36, 40, 43, 35, 35} : new int[]{36, 38, 40, 40, 36, 38, 35, 35}; // refrao
        double[][] coroMel = versao == 1 ? new double[][]{{76, 1, 79, 1, 83, 2}, {81, 2, 79, 2}, {79, 1, 81, 1, 83, 1, 86, 1}, {81, 4}, {76, 1, 79, 1, 83, 2}, {84, 2, 83, 1, 81, 1}, {79, 2, 78, 2}, {76, 4}}
                           : versao == 2 ? new double[][]{{71, 3, 72, 1}, {74, 2, 71, 2}, {69, 3, 71, 1}, {72, 2, 69, 2}, {71, 3, 74, 1}, {76, 2, 79, 2}, {78, 3, 75, 1}, {76, 4}}
                           : new double[][]{{79, 2, 78, 1, 76, 1}, {74, 2, 76, 2}, {79, 1, 78, 1, 76, 1, 74, 1}, {76, 4}, {72, 1, 74, 1, 76, 1, 79, 1}, {78, 2, 81, 2}, {78, 2, 75, 2}, {71, 4}};
        int[] esc = {64, 66, 67, 69, 71, 72, 74};
        double t = 0; m.prato(0, .22, 1.4);
        String forma = versao == 1 ? "GGGGLLLLLLLLPCCCCCCCCLLLLF" : versao == 2 ? "CCCCGGGGLLLLLLLLPCCCCCCCCF" : "GGGGLLLLLLLLPCCCCCCCCGGF"; int g = 0, l = 0, c = 0;
        for (char x : forma.toCharArray()) {
            if (x == 'G' || x == 'L') { int raiz = riff[(g++) % 8];
                for (int k = 0; k < 4; k++) { double tk = t + k * b; // galope: colcheia + duas semicolcheias
                    for (double[] q : versao == 2 ? new double[][]{{0, 2}, {2, 2}} : new double[][]{{0, 2}, {2, 1}, {3, 1}}) { double tq = tk + q[0] * d, dq = q[1] * d * .92;
                        m.guitarra(raiz + tr, tq, dq, .1, true, 0); m.guitarra(raiz + 7 + tr, tq, dq, .07, true, 0); m.nota("tri", raiz - 12 + tr, tq, dq, 0, .11, 0); }
                    m.bumbo64(tk, .36); if (k % 2 == 1) m.caixa64(tk, .3); m.chimbal64(tk + b / 2, .05, false); m.chimbal64(tk, .05, false); }
                if (g % 4 == 1) m.prato(t, .14, 1.8);
                if (x == 'L') { int[] h = gancho[(l++) % 8];
                    for (int k = 0; k < 8; k++) { int n = h[k]; if (n < 0) continue; int fim = k + 1; while (fim < 8 && h[fim] < 0) fim++; double dur = (fim - k) * 2 * d * .96;
                        int gr = 0; for (int e = 0; e < 7; e++) if (esc[e] % 12 == n % 12) gr = e; int terca = esc[(gr + 5) % 7] % 12 + (n / 12) * 12; if (terca >= n) terca -= 12;
                        m.guitarra(n + tr, t + k * 2 * d, dur, .075, false, 2); m.guitarra(terca + tr, t + k * 2 * d, dur, .05, false, 2); } }
            } else if (x == 'P') { // parada: acorde que soa, rufar de caixa e tons
                m.guitarra(40 + tr, t, comp * .5, .1, false, 0); m.guitarra(47 + tr, t, comp * .5, .07, false, 0); m.guitarra(52 + tr, t, comp * .5, .06, false, 0); m.bumbo64(t, .45); m.prato(t, .2, 1.6);
                for (int k = 8; k < 16; k++) m.caixa64(t + k * d, .1 + (k - 8) * .03); m.tom(t + 12 * d, 57, .2); m.tom(t + 14 * d, 52, .25);
            } else if (x == 'C') { int raiz = coroRaiz[c % 8]; double[] mel = coroMel[c % 8]; c++;
                for (int n : new int[]{raiz, raiz + 7, raiz + 12}) m.guitarra(n + tr, t, comp * .98, .075, false, 0); // acorde aberto
                m.nota("tri", raiz - 12 + tr, t, comp, 0, .13, 0);
                for (int k = 0; k < 8; k++) { m.bumbo64(t + k * 2 * d, .32); m.chimbal64(t + k * 2 * d + d, .04, k % 2 == 1); } m.caixa64(t + b, .3); m.caixa64(t + 3 * b, .3); if (c % 2 == 1) m.prato(t, .15, 1.6);
                double tm = t; for (int i = 0; i < mel.length; i += 2) { int n = (int) mel[i]; double dur = mel[i + 1] * b * .97;
                    int gr = 0; for (int e = 0; e < 7; e++) if (esc[e] % 12 == n % 12) gr = e; int terca = esc[(gr + 5) % 7] % 12 + (n / 12) * 12; if (terca >= n) terca -= 12;
                    m.guitarra(n + tr, tm, dur, .085, false, 2.5); m.guitarra(terca + tr, tm, dur, .055, false, 2.5); tm += mel[i + 1] * b; }
            } else { // final
                for (int n : new int[]{40, 47, 52, 55, 59}) m.guitarra(n + tr, t, 2.6, .07, false, 1); m.nota("tri", 28 + tr, t, 2.6, 0, .14, 0); m.bumbo64(t, .5); m.prato(t, .25, 1.2); t += 2.8; break;
            }
            t += comp;
        }
        return m.fechar64((int) (Math.min(60, t) * TAXA), .16);
    }


    /**
     * Abertura: rock de videogame dos anos 90, composto para o jogo. Riff sincopado no primeiro
     * compasso, baixo correndo em colcheias, gancho que gruda com eco na guitarra solo, subida e
     * refrao em tom maior, que repete um tom acima para fechar. 160 bpm.
     */
    static float[] aberturaRock() {
        Random r = new Random(1994);
        double b = 60 / 160.0, d = b / 4, comp = 4 * b;
        Mix m = new Mix((int) (48 * TAXA), r), solo = new Mix((int) (48 * TAXA), r);
        int[] r1 = {0, 0, -1, 0, -1, 0, 3, -1, 0, 0, -1, 0, 5, -1, 3, -1}, r2 = {0, 0, -1, 0, -1, 0, 3, -1, 0, 0, -1, 0, 7, 5, 3, 2};
        int[][] gancho = {{71, -1, 76, -1, 78, 79, 78, 76}, {74, -1, 76, -1, 71, -2, -2, -2}, {71, -1, 76, -1, 78, 79, 81, 79}, {78, -2, -2, -2, 74, -2, -2, -2},
                          {71, -1, 76, -1, 78, 79, 78, 76}, {74, 76, 78, 81, 83, -1, 81, -1}, {79, -1, 78, -1, 76, -1, 74, -1}, {76, -2, -2, -2, -2, -2, -2, -2}};
        int[] coroRaiz = {43, 38, 40, 36, 43, 38, 40, 36};
        double[][] coroMel = {{74, 1, 79, 1, 81, 1, 83, 1}, {81, 2, 78, 1, 74, 1}, {79, 1, 78, 1, 76, 1, 83, 1}, {81, 4},
                              {74, 1, 79, 1, 81, 1, 83, 1}, {86, 2, 83, 1, 81, 1}, {79, 1, 81, 1, 83, 1, 88, 1}, {86, 4}};
        double t = 0;
        String forma = "IIAAAAAAAASSSSCCCCCCCCKKKKF"; int ia = 0, ic = 0, is = 0;
        for (char x : forma.toCharArray()) {
            if (x == 'I' || x == 'A') { // riff + baixo + bateria (no I a bateria entra no segundo compasso)
                int[] rf = (ia % 2 == 0) ? r1 : r2; boolean intro = x == 'I';
                for (int k = 0; k < 16; k++) { int v = rf[k]; if (v < 0) continue; double tk = t + k * d;
                    m.guitarra(40 + v, tk, d * .9, .1, true, 0); m.guitarra(47 + v, tk, d * .9, .07, true, 0); }
                if (!intro || ia == 1) for (int k = 0; k < 8; k++) { double tk = t + k * 2 * d; int v = rf[k * 2] < 0 ? rf[Math.max(0, k * 2 - 1)] : rf[k * 2]; if (v < 0) v = 0;
                    m.nota("sq", 28 + v, tk, 2 * d * .7, .5, .12, 0); m.chimbal64(tk, .045, false); if (k % 2 == 0) m.bumbo64(tk, .38); else m.caixa64(tk, .28); }
                if (intro && ia == 1) for (int k = 12; k < 16; k++) m.tom(t + k * d, 50 - (k - 12) * 3, .22);
                if (x == 'A') { int[] h = gancho[(ia - 2) % 8];
                    for (int k = 0; k < 8; k++) { int n = h[k]; if (n < 0) continue; int fim = k + 1; while (fim < 8 && h[fim] == -2) fim++; double dur = (fim - k) * 2 * d * .95;
                        solo.nota64("solo", n, t + k * 2 * d, dur, .13, 2.5); solo.nota("sq", n + 12, t + k * 2 * d, dur, .25, .025, 2.5); } }
                if (ia == 2 || ia == 6) m.prato(t, .17, 1.6);
                ia++;
            } else if (x == 'S') { // subida: acordes longos, caixa acelerando, arpejo subindo
                int raiz = is % 2 == 0 ? 36 : 38;
                for (int n : new int[]{raiz + 12, raiz + 19, raiz + 24}) m.guitarra(n, t, comp * .97, .07, false, 0);
                for (int k = 0; k < 8; k++) m.nota("sq", raiz - 12 + 12, t + k * 2 * d, 2 * d * .7, .5, .12, 0);
                int passos = is < 2 ? 8 : 16; for (int k = 0; k < passos; k++) m.caixa64(t + k * comp / passos, .1 + .2 * (is * passos + k) / (4.0 * passos));
                m.bumbo64(t, .38); m.bumbo64(t + 2 * b, .38);
                int[] arp = {raiz + 36, raiz + 40, raiz + 43, raiz + 48};
                for (int k = 0; k < 8; k++) solo.nota64("solo", arp[k % 4] + (k >= 4 ? 12 : 0) - 12, t + k * 2 * d, 2 * d * .9, .09, 1);
                is++;
            } else if (x == 'C' || x == 'K') { // refrao (K = um tom acima)
                int tr = x == 'K' ? 2 : 0, i = x == 'K' ? ic % 4 : ic % 8; int raiz = coroRaiz[i] + tr; double[] mel = coroMel[i];
                for (int n : new int[]{raiz, raiz + 7, raiz + 12}) m.guitarra(n, t, comp * .97, .075, false, 0);
                for (int k = 0; k < 8; k++) { double tk = t + k * 2 * d; m.nota("sq", raiz - 12, tk, 2 * d * .7, .5, .12, 0); m.chimbal64(tk, .05, k % 2 == 1); if (k % 2 == 0) m.bumbo64(tk, .4); else m.caixa64(tk, .3); }
                if (i % 2 == 0) m.prato(t, .16, 1.6);
                double tm = t; for (int k = 0; k < mel.length; k += 2) { int n = (int) mel[k] + tr; double dur = mel[k + 1] * b * .96;
                    solo.nota64("solo", n, tm, dur, .14, 3); solo.nota64("solo", n - 12, tm, dur, .06, 3); tm += mel[k + 1] * b; }
                ic++;
            } else { // fecho: acorde maior aberto, prato e eco soando
                for (int n : new int[]{45, 52, 57, 61, 64}) m.guitarra(n, t, 2.8, .07, false, 1); m.nota("sq", 33, t, 2.8, .5, .12, 0); m.bumbo64(t, .5); m.prato(t, .25, 1.2);
                solo.nota64("solo", 81, t, 2.6, .12, 3); t += 3; break;
            }
            t += comp;
        }
        // eco do solo (o delay classico dos consoles de 16 bits), somado ao resto
        int n = (int) (Math.min(48, t) * TAXA), atraso = (int) (b * .75 * TAXA);
        for (int i = 0; i < n; i++) { double e = solo.a[i] + (i >= atraso ? solo.a[i - atraso] * .38 : 0) + (i >= 2 * atraso ? solo.a[i - 2 * atraso] * .16 : 0); m.a[i] += e; }
        return m.fechar64(n, .14);
    }


    /**
     * Hino do Metal Space: tudo nasce do motivo "ME-TAL SPA-CE" (curto, curto, LONGO; si, re, MI e
     * si, re, SOL, fa#, MI). A torcida entoa o motivo com pisadas e palmas, as guitarras viram o motivo
     * em riff, o solo desenvolve, o refrao canta o motivo em coro e guitarra, repete um tom acima e
     * fecha com tres pancadas no ritmo do nome. 132 bpm, mi menor.
     */
    static float[] hino() {
        Random r = new Random(2026);
        double b = 60 / 132.0, d = b / 4, comp = 4 * b;
        Mix m = new Mix((int) (60 * TAXA), r), solo = new Mix((int) (60 * TAXA), r);
        // motivo em dois compassos: notas e duracao em tempos
        double[] motivoA = {71, .5, 74, .5, 76, 3}, motivoB = {71, .5, 74, .5, 79, 1, 78, 1, 76, 5};
        int[] acordes = {40, 36, 43, 38}; // Em C G D
        double t = 0;
        String forma = "TTTTRRRRVVVVVVVVCCCCCCCCKKKKF"; int it = 0, ir = 0, iv = 0, ic = 0;
        for (char x : forma.toCharArray()) {
            int raiz = acordes[(x == 'T' ? 0 : (x == 'R' ? ir : x == 'V' ? iv : ic)) % 4] + (x == 'K' ? 2 : 0);
            if (x == 'T') { // a torcida: coro cantando o motivo, pisadas e palmas
                double[] mo = it % 2 == 0 ? motivoA : new double[]{71, .5, 74, .5, 79, 1, 78, 1};
                double tm = t; for (int k = 0; k < mo.length; k += 2) { m.orquestra("coro", mo[k], tm, mo[k + 1] * b * .95, .16); m.orquestra("coro", mo[k] - 12, tm, mo[k + 1] * b * .95, .1); tm += mo[k + 1] * b; }
                for (int k = 0; k < 4; k++) { m.tom(t + k * b, 40, .3); if (k % 2 == 1) { m.ruido(t + k * b, .12, 30, .22); m.ruido(t + k * b + .012, .1, 35, .14); } }
                if (it == 3) for (int k = 8; k < 16; k++) m.caixa64(t + k * d, .1 + (k - 8) * .03);
                it++;
            } else if (x == 'R') { // riff: o motivo em power chord, palhetada em colcheias
                double[] mo = ir % 2 == 0 ? motivoA : new double[]{71, .5, 74, .5, 79, 1, 78, 1};
                double tm = t; for (int k = 0; k < mo.length; k += 2) { int n = (int) mo[k] - 36; int passos = (int) Math.round(mo[k + 1] * 2);
                    for (int q = 0; q < passos; q++) { m.guitarra(n, tm + q * 2 * d, 2 * d * .85, .1, q > 0, 0, true); m.guitarra(n + 7, tm + q * 2 * d, 2 * d * .85, .07, q > 0, 0, true); m.nota("sq", n - 12, tm + q * 2 * d, 2 * d * .7, .5, .12, 0); }
                    tm += mo[k + 1] * b; }
                bateria(m, t, b, true); if (ir % 2 == 0) m.prato(t, .18, 1.6); ir++;
            } else if (x == 'V') { // verso: palhetada no acorde, solo desenvolvendo o motivo (com eco)
                for (int q = 0; q < 16; q++) { int nq = raiz + (q == 14 ? 1 : q == 15 ? 3 : 0); m.guitarra(nq, t + q * d, d * .8, .1, true, 0, true); m.guitarra(nq + 7, t + q * d, d * .8, .06, true, 0, true); if (q % 2 == 0) m.nota("sq", nq - 12, t + q * d, 2 * d * .7, .5, .12, 0); }
                double[][] verso = {{76, 1, 79, 1, 81, 2}, {83, 1, 81, .5, 79, .5, 78, 2}, {76, 1, 79, 1, 83, 2}, {86, 2, 83, 2}, {84, 1, 83, 1, 81, 1, 79, 1}, {81, 1, 79, 1, 78, 2}, {76, 1, 78, 1, 79, 1, 81, 1}, {83, 4}};
                double tm = t; double[] v = verso[iv % 8]; for (int k = 0; k < v.length; k += 2) { solo.guitarra(v[k], tm, v[k + 1] * b * .95, .09, false, 4, true); tm += v[k + 1] * b; }
                for (int k = 0; k < 8; k++) { double tk = t + k * 2 * d; m.bumbo64(tk, .34); m.caixa64(tk + d, .26); m.chimbal64(tk, .04, false); } iv++; // batida de thrash
            } else if (x == 'C' || x == 'K') { // refrao: o motivo cantado por coro e guitarra, acordes abertos
                for (int n : new int[]{raiz, raiz + 7, raiz + 12}) m.guitarra(n, t, comp * .97, .075, false, 0, true);
                for (int q = 0; q < 8; q++) m.nota("sq", raiz - 12, t + q * 2 * d, 2 * d * .7, .5, .12, 0);
                int tr = x == 'K' ? 2 : 0; int fase = (x == 'K' ? ic : ic) % 2;
                if (fase == 0) { double[] mo = (ic / 2) % 2 == 0 ? motivoA : motivoB; double tm = t;
                    for (int k = 0; k < mo.length; k += 2) { double n = mo[k] + tr + 12, dur = Math.min(mo[k + 1], 8) * b * .95;
                        m.orquestra("coro", n - 12, tm, dur, .14); m.orquestra("coro", n - 24, tm, dur, .09); solo.guitarra(n, tm, dur, .1, false, 4, true); tm += mo[k + 1] * b; } }
                bateria(m, t, b, true); if (fase == 0) m.prato(t, .18, 1.5); ic++;
            } else { // ME-TAL SPA-CE! tres pancadas, silencio e acorde final
                double[] golpes = {0, .5, 1};
                for (double g : golpes) { double tg = t + g * b; for (int n : new int[]{42, 49, 54}) m.guitarra(n, tg, g == 1 ? b * .4 : b * .3, .1, false, 0, true); m.bumbo64(tg, .5); m.prato(tg, .12, 6); m.orquestra("coro", g == 0 ? 73 : g == .5 ? 76 : 78, tg, b * .4, .16); }
                double tf = t + 2.5 * b; for (int n : new int[]{42, 49, 54, 58, 61}) m.guitarra(n, tf, 3, .07, false, 1, true); m.orquestra("coro", 66, tf, 3, .14); m.orquestra("coro", 73, tf, 3, .12);
                m.nota("sq", 30, tf, 3, .5, .12, 0); m.bumbo64(tf, .5); m.prato(tf, .25, 1.1); solo.nota64("solo", 85, tf, 2.8, .12, 3); t = tf + 3.2; break;
            }
            t += comp;
        }
        int n = (int) (Math.min(60, t) * TAXA), atraso = (int) (b * .75 * TAXA);
        for (int i = 0; i < n; i++) m.a[i] += solo.a[i] + (i >= atraso ? solo.a[i - atraso] * .35 : 0) + (i >= 2 * atraso ? solo.a[i - 2 * atraso] * .14 : 0);
        return m.fechar64(n, .08);
    }
    /** Bateria de hino: bumbo no 1 e no 3 (com extras), caixa no 2 e no 4, chimbal em colcheias. */
    private static void bateria(Mix m, double t, double b, boolean cheio) {
        for (int k = 0; k < 4; k++) { double tk = t + k * b; if (k % 2 == 0) m.bumbo64(tk, .4); else m.caixa64(tk, .32); if (cheio && k == 2) m.bumbo64(tk + b / 2, .3); m.chimbal64(tk, .045, false); m.chimbal64(tk + b / 2, .04, cheio); }
    }


    /**
     * Tela inicial: sombria e crescente, para instigar a apertar JOGAR. Clima (zumbido, coro,
     * guitarra com eco e tambores ao longe), entra o riff lento e pesado em si grave, explode com
     * bumbo duplo e o motivo ME-TAL SPA-CE no solo sujo, e cai numa quebradeira que emenda no inicio.
     * Si menor, 100 bpm, loop de ponta a ponta.
     */
    static float[] menu() {
        Random r = new Random(1313);
        double b = .6, d = b / 4, comp = 4 * b;
        int[] acordes = {35, 31, 28, 30}; // Bm G Em F#
        int total = 20; double fim = total * comp;
        Mix m = new Mix((int) (fim * TAXA) + TAXA, r), solo = new Mix((int) (fim * TAXA) + TAXA, r);
        for (int c = 0; c < total; c++) {
            double t = c * comp; int raiz = acordes[c % 4]; boolean menor = raiz == 35 || raiz == 28;
            int terca = raiz + (menor ? 3 : 4);
            // coro e zumbido sempre (o clima)
            m.orquestra("coro", raiz + 24, t, comp + .3, c < 4 ? .1 : .08); m.orquestra("coro", terca + 24, t, comp + .3, c < 4 ? .07 : .06); m.orquestra("coro", raiz + 31, t, comp + .3, .05);
            m.nota("saw", raiz - 12, t, comp, 0, c < 4 ? .06 : .04, 0);
            if (c < 4) { // A: clima. guitarra com eco dedilhando, tambores ao longe
                int[] dedilhado = {raiz + 24, raiz + 31, terca + 24, raiz + 36, terca + 24, raiz + 31};
                for (int k = 0; k < 6; k++) solo.nota64("arp", dedilhado[k], t + k * comp / 6, comp / 6 * .9, .09, 0);
                m.timpano(t, raiz + 12, .22); if (c % 2 == 1) m.timpano(t + 2 * b, raiz + 12, .15);
                if (c == 3) { m.subida(fim > 0 ? t + comp : 0, 2.2, .3); for (int k = 8; k < 16; k++) m.caixa64(t + k * d, .06 + (k - 8) * .025); }
            } else if (c < 8) { // B: o riff lento e pesado entra (meio-tempo)
                int[] golpes = {0, 6, 10}; for (int k : golpes) { double tk = t + k * d, dur = (k == 0 ? 6 : 4) * d * .95;
                    m.guitarra(raiz, tk, dur, .11, false, 0, true); m.guitarra(raiz + 7, tk, dur, .07, false, 0, true); m.nota("tri", raiz - 12, tk, dur, 0, .14, 0); m.bumbo64(tk, .45); }
                m.caixa64(t + 2 * b, .34); m.prato(t, .13, 1.6); for (int k = 0; k < 4; k++) m.chimbal64(t + k * b, .04, k % 2 == 1);
            } else if (c < 16) { // C: explode. palhetada sincopada, bumbo duplo, solo com o motivo
                int[] pal = {0, 0, -1, 0, 0, -1, 0, 0, 0, 0, -1, 0, 3, -1, 1, -1};
                for (int k = 0; k < 16; k++) { int v = pal[k]; if (v < 0) continue; double tk = t + k * d;
                    m.guitarra(raiz + v, tk, d * .85, .11, true, 0, true); m.guitarra(raiz + 7 + v, tk, d * .85, .065, true, 0, true); if (k % 2 == 0) m.nota("tri", raiz - 12 + v, tk, 2 * d * .8, 0, .13, 0); }
                for (int k = 0; k < 16; k++) m.bumbo64(t + k * d, k % 2 == 0 ? .32 : .22);
                m.caixa64(t + b, .32); m.caixa64(t + 3 * b, .32); if (c % 2 == 0) m.prato(t, .16, 1.5); for (int k = 0; k < 8; k++) m.chimbal64(t + k * 2 * d, .035, false);
                double[][] frase = {{78, .5, 81, .5, 83, 3}, {78, .5, 81, .5, 86, 1, 85, 1, 83, 1}, {81, 1, 79, 1, 78, 2}, {78, 1, 76, 1, 78, 2},
                                    {78, .5, 81, .5, 83, 3}, {78, .5, 81, .5, 86, 1, 88, 1, 90, 1}, {88, 1, 86, 1, 85, 2}, {83, 4}};
                double tm = t; double[] f = frase[(c - 8) % 8];
                for (int k = 0; k < f.length; k += 2) { solo.guitarra(f[k], tm, f[k + 1] * b * .96, .1, false, 4, true); tm += f[k + 1] * b; }
                if (c % 4 == 0) for (int n : new int[]{raiz + 12, terca + 12, raiz + 19}) m.orquestra("metais", n, t, b * 2, .06);
            } else { // D: quebradeira e respiro, volta para o clima
                int[] q = {0, -1, -1, 0, -1, -1, 0, -1, 1, -1, 0, -1, -1, -1, -1, -1};
                if (c < 18) for (int k = 0; k < 16; k++) if (q[k] >= 0) { double tk = t + k * d; m.guitarra(raiz + q[k], tk, d * 1.8, .12, false, 0, true); m.guitarra(raiz + 7 + q[k], tk, d * 1.8, .07, false, 0, true); m.nota("tri", raiz - 12 + q[k], tk, d * 2, 0, .14, 0); m.bumbo64(tk, .45); }
                if (c < 18) { m.caixa64(t + 2 * b, .34); m.prato(t, .15, 1.4); }
                else { m.guitarra(raiz, t, comp * .9, .08, false, 1, true); m.timpano(t, raiz + 12, .25); if (c == total - 1) m.timpano(t + 2 * b, raiz + 12, .18); }
            }
        }
        int n = (int) (fim * TAXA), atraso = (int) (b * .75 * TAXA);
        for (int i = 0; i < n; i++) { double e = solo.a[i]; int j = i - atraso, k2 = i - 2 * atraso; if (j < 0) j += n; if (k2 < 0) k2 += n; m.a[i] += e + solo.a[j] * .4 + solo.a[k2] * .18; } // eco que da a volta no loop
        for (int i = n; i < m.a.length; i++) m.a[i - n] += m.a[i]; // caudas do fim caem no comeco (loop sem emenda)
        return m.fechar64(n, .22);
    }

    private static final class Mix {
        final float[] a; final Random r; boolean m64 = true; // producao cheia (guitarra, coro, reverb); false = chip puro
        Mix(int n, Random r) { a = new float[n]; this.r = r; }
        static double f(double n) { return 440 * Math.pow(2, (n - 69) / 12); }
        void nota(String tipo, double midi, double t0, double dur, double duty, double g, double vib) {
            if (m64) {
                if (tipo.equals("saw") || tipo.equals("sq") && midi < 60 && duty != .125) { nota64("dist", midi, t0, dur, g * 1.2, 0); return; }
                if (tipo.equals("sq") && duty == .125) { nota64("solo", midi, t0, dur, g * 1.4, vib); return; }
                if (tipo.equals("sq")) { nota64("arp", midi, t0, dur, g * 1.5, 0); return; }
                if (tipo.equals("tri") && vib > 0) { nota64("solo", midi, t0, dur, g * .9, vib); return; }
            }
            int i0 = (int) (t0 * TAXA), n = (int) (dur * TAXA), ata = Math.min(n / 4, (int) (.006 * TAXA)), sol = Math.min(n / 3, (int) (.03 * TAXA)); double fr = f(midi), ph = 0;
            for (int i = 0; i < n && i0 + i < a.length; i++) {
                double t = i / (double) TAXA; ph += (fr * (1 + (vib > 0 ? .004 * vib * Math.sin(2 * Math.PI * 5.5 * t) : 0))) / TAXA; double p = ph % 1, s;
                s = tipo.equals("sq") ? (p < (duty > 0 ? duty : .5) ? 1 : -1) : tipo.equals("tri") ? 4 * Math.abs(p - .5) - 1 : 2 * p - 1;
                double e = i < ata ? i / (double) ata : i > n - sol ? (n - i) / (double) sol : 1; a[i0 + i] += s * e * g;
            }
        }
        /** Instrumentos "64 bits": guitarra distorcida dobrada (dist), solo encorpado (solo) e cordas suaves (pad). */
        void nota64(String tipo, double midi, double t0, double dur, double g, double vib) {
            int i0 = (int) (t0 * TAXA), n = (int) (dur * TAXA), ata = Math.min(n / 4, (int) ((tipo.equals("pad") ? .25 : .004) * TAXA)), sol = Math.min(n / 3, (int) ((tipo.equals("pad") ? .3 : .04) * TAXA));
            double fr = f(midi), p1 = r.nextDouble(), p2 = r.nextDouble(), p3 = r.nextDouble(), lp = 0, corte = tipo.equals("dist") ? .32 : tipo.equals("solo") ? .45 : tipo.equals("arp") ? .22 : .12;
            for (int i = 0; i < n && i0 + i < a.length; i++) {
                double t = i / (double) TAXA, v = 1 + (vib > 0 ? .005 * vib * Math.sin(2 * Math.PI * 5.2 * t) * Math.min(1, t * 3) : 0);
                p1 += fr * v / TAXA; p2 += fr * v * 1.0045 / TAXA; p3 += fr * v * .9958 / TAXA;
                double sw = (2 * (p1 % 1) - 1) + (2 * (p2 % 1) - 1) + .6 * (2 * (p3 % 1) - 1), x;
                if (tipo.equals("dist")) x = Math.tanh(sw * 3.2) * .7;
                else if (tipo.equals("solo")) x = Math.tanh((sw + ((p1 * 2) % 1 < .5 ? .5 : -.5)) * 1.8) * .7;
                else x = sw * .4;
                lp += (x - lp) * corte;
                double e = i < ata ? i / (double) ata : i > n - sol ? (n - i) / (double) sol : 1;
                if (tipo.equals("dist")) e *= .75 + .25 * Math.exp(-t * 6); // palhetada: ataque e sustentacao
                a[i0 + i] += lp * e * g;
            }
        }
        /** Camada de orquestra: coro "aah" (5 vozes desafinadas, filtro fechado) e metais (filtro que abre no ataque). */
        void orquestra(String tipo, double midi, double t0, double dur, double g) {
            int i0 = (int) (t0 * TAXA), n = (int) (dur * TAXA); boolean coro = tipo.equals("coro");
            int ata = Math.min(n / 2, (int) ((coro ? Math.min(.6, dur * .25) : .05) * TAXA)), sol = Math.min(n / 2, (int) ((coro ? .8 : .25) * TAXA));
            double fr = f(midi), lp1 = 0, lp2 = 0; double[] ph = new double[5]; double[] dt = {1, 1.006, .994, 1.011, .989}; for (int k = 0; k < 5; k++) ph[k] = r.nextDouble();
            for (int i = 0; i < n && i0 + i < a.length; i++) {
                double t = i / (double) TAXA, v = 1 + .004 * Math.sin(2 * Math.PI * 4.8 * t + midi), x = 0;
                for (int k = 0; k < (coro ? 5 : 3); k++) { ph[k] += fr * v * dt[k] / TAXA; x += 2 * (ph[k] % 1) - 1; }
                double corte = coro ? .05 : .04 + .3 * Math.min(1, t / .08) * Math.exp(-t * 1.5);
                lp1 += (x - lp1) * corte; lp2 += (lp1 - lp2) * corte;
                double e = i < ata ? i / (double) ata : i > n - sol ? (n - i) / (double) sol : 1;
                a[i0 + i] += lp2 * e * g;
            }
        }
        void timpano(double t0, double midi, double g) { int i0 = (int) (t0 * TAXA), n = (int) (1.6 * TAXA); double ph = 0, fr = f(midi);
            for (int i = 0; i < n && i0 + i < a.length; i++) { double t = i / (double) TAXA; ph += fr * (1 + .3 * Math.exp(-t * 18)) / TAXA;
                a[i0 + i] += g * (Math.sin(2 * Math.PI * ph) + .4 * Math.sin(2 * Math.PI * ph * 1.5) + (i < 200 ? (r.nextDouble() - .5) * (1 - i / 200.0) : 0)) * Math.exp(-t * 2.6); } }
        /** Prato invertido: chiado que sobe ate o tempo forte. */
        void subida(double tFim, double dur, double g) { int n = (int) (dur * TAXA), i0 = (int) ((tFim - dur) * TAXA); double lp = 0;
            for (int i = 0; i < n && i0 + i < a.length; i++) { if (i0 + i < 0) continue; double p = i / (double) n, w = r.nextDouble() * 2 - 1; lp += (w - lp) * (.05 + .6 * p); a[i0 + i] += g * (w - lp) * p * p; } }
        /** Guitarra com amplificador simulado: 3 cordas desafinadas, pre-filtro, saturacao assimetrica e caixa de som. */
        void guitarra(double midi, double t0, double dur, double g, boolean abafada, double vib) { guitarra(midi, t0, dur, g, abafada, vib, false); }
        /** suja = thrash: ganho no talo, corte duro, medio cavado e chiado do amplificador. */
        void guitarra(double midi, double t0, double dur, double g, boolean abafada, double vib, boolean suja) {
            int i0 = (int) (t0 * TAXA), n = (int) (dur * TAXA), sol = Math.min(n / 3, (int) ((abafada ? .015 : .05) * TAXA));
            double fr = f(midi), p1 = r.nextDouble(), p2 = r.nextDouble(), p3 = r.nextDouble(), hp = 0, c1 = 0, c2 = 0, c3 = 0, corte = abafada ? .1 : .2;
            for (int i = 0; i < n && i0 + i < a.length; i++) {
                double t = i / (double) TAXA, v = 1 + (vib > 0 ? .006 * vib * Math.sin(2 * Math.PI * 5.4 * t) * Math.min(1, t * 2.5) : 0);
                p1 += fr * v / TAXA; p2 += fr * v * 1.0035 / TAXA; p3 += fr * v * 2.001 / TAXA;
                double x = (2 * (p1 % 1) - 1) + (2 * (p2 % 1) - 1) + .35 * (2 * (p3 % 1) - 1);
                if (i < 160) x += (r.nextDouble() - .5) * 1.5 * (1 - i / 160.0); // ataque da palheta
                hp += (x - hp) * .02; x -= hp; // tira o grave embolado antes de distorcer
                if (suja) { x = x * 18 + (r.nextDouble() - .5) * .25; x = Math.max(-1, Math.min(1, x)) * .7 + Math.tanh(x * .4) * .3; } // corte duro + chiado
                else x = Math.tanh(x * 7 + .25) - Math.tanh(.25); // saturacao assimetrica (valvula)
                double cc = suja ? Math.min(.5, corte * 1.9) : corte;
                c1 += (x - c1) * cc; c2 += (c1 - c2) * cc; c3 += (x - c3) * .05; // caixa de som + corpo de medio
                double y = suja ? c2 * .95 - (c1 - c3) * .25 + (x - c1) * .18 : c2 * .8 + (c1 - c3) * .35; // suja: medio cavado e brilho rasgado
                double e = i > n - sol ? (n - i) / (double) sol : 1; if (abafada) e *= Math.exp(-t * 9) * .7 + .3;
                a[i0 + i] += y * e * g;
            }
        }
        void prato(double t0, double g, double dec) { int i0 = (int) (t0 * TAXA), n = (int) (3 / dec * TAXA); double lp = 0, ph1 = 0, ph2 = 0, ph3 = 0;
            for (int i = 0; i < n && i0 + i < a.length; i++) { double t = i / (double) TAXA, w = r.nextDouble() * 2 - 1; lp += (w - lp) * .3; ph1 += 3150.0 / TAXA; ph2 += 4370.0 / TAXA; ph3 += 6210.0 / TAXA;
                double met = Math.signum(Math.sin(2 * Math.PI * ph1)) + Math.signum(Math.sin(2 * Math.PI * ph2)) + Math.signum(Math.sin(2 * Math.PI * ph3));
                a[i0 + i] += g * ((w - lp) + .15 * met) * Math.exp(-t * dec); } }
        void chimbal64(double t0, double g, boolean aberto) { prato(t0, g, aberto ? 9 : 45); }
        void bumbo64(double t0, double g) { int i0 = (int) (t0 * TAXA), n = (int) (.3 * TAXA); double ph = 0;
            for (int i = 0; i < n && i0 + i < a.length; i++) { double t = i / (double) TAXA; ph += (48 + 110 * Math.exp(-t * 32)) / TAXA; a[i0 + i] += g * (Math.tanh(1.6 * Math.sin(2 * Math.PI * ph)) * Math.exp(-t * 11) + (i < 90 ? (r.nextDouble() - .5) * .6 * (1 - i / 90.0) : 0) + (i < 60 ? Math.sin(2 * Math.PI * 3200 * t) * .5 * (1 - i / 60.0) : 0)); } }
        void caixa64(double t0, double g) { int i0 = (int) (t0 * TAXA), n = (int) (.28 * TAXA); double lp = 0;
            for (int i = 0; i < n && i0 + i < a.length; i++) { double t = i / (double) TAXA, w = r.nextDouble() * 2 - 1; lp += (w - lp) * .55; a[i0 + i] += g * (lp * Math.exp(-t * 13) + .5 * Math.sin(2 * Math.PI * 185 * t) * Math.exp(-t * 30)); } }
        /** Fecha com filtro suave e reverb de sala (Schroeder), o acabamento "64 bits". */
        float[] fechar64(int n) { return fechar64(n, .38); }
        float[] fechar64(int n, double molhado) {
            n = Math.min(n, a.length); double[] x = new double[n]; double lp = 0; for (int i = 0; i < n; i++) { lp += (a[i] - lp) * .55; x[i] = lp; }
            int[] cd = {2557, 2617, 2491, 2422, 3011, 2203}; double[] y = new double[n];
            for (int d : cd) { double[] b = new double[d]; int k = 0; double f = 0; for (int i = 0; i < n; i++) { double o = b[k]; f = o * .7 + f * .3; b[k] = x[i] + f * .86; k = (k + 1) % d; y[i] += o * .17; } }
            for (int d : new int[]{225, 556}) { double[] b = new double[d]; int k = 0; for (int i = 0; i < n; i++) { double o = b[k], in = y[i]; b[k] = in + o * .5; y[i] = o - in * .5; k = (k + 1) % d; } }
            float[] o = new float[n]; double mx = 1e-6; for (int i = 0; i < n; i++) { x[i] = x[i] * .8 + y[i] * molhado; mx = Math.max(mx, Math.abs(x[i])); }
            for (int i = 0; i < n; i++) o[i] = (float) (x[i] * .88 / mx); return o;
        }
        void acorde(int[] ns, double t0, double dur, double duty, double g, boolean vib) { if (m64) { for (int n : ns) orquestra("coro", n + 12, t0, dur + .25, g * 1.6); return; } for (int n : ns) nota("sq", n, t0, dur, duty, g, vib ? .6 : 0); }
        void sino(double midi, double t0, double g) { int i0 = (int) (t0 * TAXA), n = (int) (2.2 * TAXA); double f1 = f(midi), f2 = f(midi + 7);
            for (int i = 0; i < n && i0 + i < a.length; i++) { double t = i / (double) TAXA; a[i0 + i] += g * (Math.sin(2 * Math.PI * f1 * t) * Math.exp(-t * 2.5) + .4 * Math.sin(2 * Math.PI * f2 * t) * Math.exp(-t * 4)); } }
        void bumbo(double t0, double g) { if (m64) { bumbo64(t0, g * 1.05); return; } int i0 = (int) (t0 * TAXA), n = (int) (.12 * TAXA); double ph = 0;
            for (int i = 0; i < n && i0 + i < a.length; i++) { double t = i / (double) TAXA; ph += (55 + 90 * Math.exp(-t * 40)) / TAXA; a[i0 + i] += g * Math.sin(2 * Math.PI * ph) * Math.exp(-t * 25); } }
        void caixa(double t0, double g) { if (m64) caixa64(t0, g * 1.15); else ruido(t0, .15, 22, g); }
        void chimbal(double t0, double g) { ruido(t0, .03, 90, g); }
        void metal(double t0, double g) { int i0 = (int) (t0 * TAXA), n = (int) (.25 * TAXA);
            for (int i = 0; i < n && i0 + i < a.length; i++) { double t = i / (double) TAXA; a[i0 + i] += g * (Math.signum(Math.sin(2 * Math.PI * 1170 * t)) * .5 + Math.signum(Math.sin(2 * Math.PI * 1630 * t)) * .5 + r.nextDouble() - .5) * Math.exp(-t * 14); } }
        void tom(double t0, double midi, double g) { int i0 = (int) (t0 * TAXA), n = (int) (.25 * TAXA); double ph = 0, fr = f(midi);
            for (int i = 0; i < n && i0 + i < a.length; i++) { double t = i / (double) TAXA; ph += fr * (1 + Math.exp(-t * 20)) / TAXA; a[i0 + i] += g * Math.sin(2 * Math.PI * ph) * Math.exp(-t * 12); } }
        void ruido(double t0, double dur, double dec, double g) { int i0 = (int) (t0 * TAXA), n = (int) (dur * TAXA);
            for (int i = 0; i < n && i0 + i < a.length; i++) a[i0 + i] += g * (r.nextDouble() * 2 - 1) * Math.exp(-i / (double) TAXA * dec); }
        float[] fechar(int n) { float[] o = new float[Math.min(n, a.length)]; double mx = 1e-6; for (int i = 0; i < o.length; i++) mx = Math.max(mx, Math.abs(a[i]));
            float k = (float) (.85 / mx); for (int i = 0; i < o.length; i++) o[i] = a[i] * k; return o; }
    }
}
