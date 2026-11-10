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

    static boolean reconhece(String nome) { return nome != null && (nome.startsWith("universo_") || nome.matches("chefe_[1-8]") || nome.equals("abertura")); }
    static final int ABERTURA = 77;

    /** Monta a trilha do nome (universo_S_V ou chefe_N). */
    static float[] gerar(String nome) {
        if (nome.equals("abertura")) return abertura();
        if (nome.startsWith("chefe_")) { int c = Integer.parseInt(nome.substring(6)); int[] ordem = {8, 2, 3, 4, 5, 6, 7, 1}; for (int s = 0; s < 8; s++) if (ordem[s] == c) return gerar(s, CHEFE); }
        String[] p = nome.split("_"); return gerar(Integer.parseInt(p[1]), Integer.parseInt(p[2]));
    }

    static float[] gerar(int u, int v) {
        u = Math.max(0, Math.min(8, u));
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
     * Abertura: thrash cru e rapido, composto para o jogo. Prato e riff ja no primeiro segundo,
     * refrao de guitarra que gruda (guitarras gemeas em tercas), parada com pancadas secas,
     * quebradeira em meio-tempo e volta dobrada. Em mi menor, 184 bpm, som seco.
     */
    static float[] abertura() {
        Random r = new Random(1984);
        double b = 60 / 184.0, d = b / 4; Mix m = new Mix((int) (40 * TAXA), r);
        int[] riff = {0, 0, 0, 0, 3, 0, 0, 2, 0, 0, 0, 0, 6, 5, 3, 2}; // mi pedal com cromatismo
        int[] h1 = {71, 71, 76, 74, 71, 69, 67, 69, 71, 71, 76, 78, 79, 78, 76, -1}, h2 = {71, 71, 76, 74, 71, 69, 67, 69, 67, 69, 71, 74, 76, -1, -1, -1};
        int[] esc = {64, 66, 67, 69, 71, 72, 74};
        double t = 0;
        // compassos: R riff, P parada, H refrao (riff por baixo), Q quebradeira, F final
        String forma = "RRRRPHHHHQQRHHHHF";
        for (char c : forma.toCharArray()) {
            int bar = (int) (t / (4 * b));
            if (c == 'R' || c == 'H') {
                for (int k = 0; k < 16; k++) { int x = riff[k]; double tk = t + k * d; boolean acento = x != 0;
                    m.nota64("dist", 40 + x, tk, d * (acento ? .95 : .55), acento ? .14 : .11, 0); if (acento) m.nota64("dist", 47 + x, tk, d * .95, .09, 0);
                    m.nota("tri", 28 + x, tk, d * .9, 0, .1, 0); }
                for (int k = 0; k < 4; k++) { m.bumbo64(t + k * b, .38); m.caixa64(t + k * b + b / 2, .26); m.chimbal(t + k * b, .05); m.chimbal(t + k * b + b / 2, .05); }
                if (bar % 2 == 0) m.ruido(t, .8, 4.5, .12);
            } else if (c == 'P') { // parada: tres pancadas secas e silencio, depois rufar
                for (int k : new int[]{0, 3, 6}) { double tk = t + k * d * 2; m.nota64("dist", 40, tk, d * 1.6, .16, 0); m.nota64("dist", 47, tk, d * 1.6, .1, 0); m.bumbo64(tk, .45); m.ruido(tk, .5, 6, .12); }
                for (int k = 0; k < 8; k++) m.caixa64(t + 2 * b + k * d, .1 + k * .025);
            } else if (c == 'Q') { // quebradeira: meio-tempo esmagador
                int[] q = {0, -1, -1, 0, -1, 0, -1, -1, 1, -1, 0, -1, -1, 0, 0, 0};
                for (int k = 0; k < 16; k++) if (q[k] >= 0) { double tk = t + k * d; m.nota64("dist", 40 + q[k], tk, d * .9, .15, 0); m.nota64("dist", 47 + q[k], tk, d * .9, .09, 0); m.nota("tri", 28 + q[k], tk, d, 0, .12, 0); m.bumbo64(tk, .4); }
                m.caixa64(t + 2 * b, .32); m.ruido(t, 1.2, 3, .1);
            } else { // final: acorde aberto com prato
                for (int n : new int[]{40, 47, 52, 59}) m.nota64("dist", n, t, 2.4, .11, 0); m.bumbo64(t, .5); m.ruido(t, 2.2, 1.8, .2); t += 2.6; break;
            }
            t += 4 * b;
        }
        // refrao: duas guitarras em tercas por cima dos compassos H (h1 e h2 se alternando)
        double tt = 0; int hb = 0;
        for (char c : forma.toCharArray()) {
            if (c == 'F') break;
            if (c == 'H') { int[] h = (hb / 2) % 2 == 0 ? h1 : h2; int metade = hb % 2; hb++;
                for (int k = 0; k < 8; k++) { int n = h[metade * 8 + k]; if (n < 0) continue; double tk = tt + k * 2 * d, dur = 2 * d * .95;
                    int nx = k + 1 < 8 ? h[metade * 8 + k + 1] : 0; if (nx < 0) dur = 2 * d * (8 - k) * .95;
                    int g = 0; for (int e = 0; e < 7; e++) if (esc[e] % 12 == n % 12) g = e; int terca = esc[(g + 5) % 7] % 12 + (n / 12) * 12; if (terca >= n) terca -= 12;
                    m.nota64("solo", n, tk, dur, .12, 2.5); m.nota64("solo", terca, tk, dur, .08, 2.5); } }
            tt += 4 * b;
        }
        return m.fechar64((int) (Math.min(40, t + .2) * TAXA), .12);
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
            int ata = Math.min(n / 2, (int) ((coro ? .6 : .05) * TAXA)), sol = Math.min(n / 2, (int) ((coro ? .8 : .25) * TAXA));
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
        void bumbo64(double t0, double g) { int i0 = (int) (t0 * TAXA), n = (int) (.3 * TAXA); double ph = 0;
            for (int i = 0; i < n && i0 + i < a.length; i++) { double t = i / (double) TAXA; ph += (48 + 110 * Math.exp(-t * 32)) / TAXA; a[i0 + i] += g * (Math.tanh(1.6 * Math.sin(2 * Math.PI * ph)) * Math.exp(-t * 11) + (i < 90 ? (r.nextDouble() - .5) * .6 * (1 - i / 90.0) : 0)); } }
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
