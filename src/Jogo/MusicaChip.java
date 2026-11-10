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
    private static final int[] BPM = {66, 170, 150, 112, 200, 190, 132, 100, 220};
    private static final int[] TOM = {38, 40, 45, 40, 40, 37, 50, 38, 42};
    private static final int[] TEMPOS = {4, 4, 4, 4, 4, 4, 6, 4, 4};
    private static final int[][] ESCALA = {
        {0, 2, 3, 5, 7, 8, 10}, {0, 2, 3, 5, 7, 8, 10}, {0, 2, 4, 5, 7, 9, 11}, {0, 1, 3, 5, 7, 8, 10}, {0, 1, 3, 5, 6, 8, 10},
        {0, 1, 3, 5, 7, 8, 11}, {0, 2, 3, 5, 7, 9, 10}, {0, 2, 3, 5, 7, 8, 11}, {0, 1, 3, 5, 7, 8, 10}};
    /** Progressoes (graus da escala) que cada subgenero gosta. */
    private static final int[][][] PROG = {
        {{0, 5, 3, 4}, {0, 3, 5, 4}}, {{0, 5, 6, 0}, {0, 6, 5, 6}}, {{0, 4, 5, 3}, {5, 3, 0, 4}}, {{0, 1, 0, 6}, {0, 0, 1, 1}},
        {{0, 1, 0, 4}, {0, 6, 5, 1}}, {{0, 1, 5, 4}, {0, 4, 1, 0}}, {{0, 6, 3, 0}, {0, 3, 6, 4}}, {{0, 5, 2, 6}, {0, 3, 4, 4}}, {{0, 5, 3, 1}, {0, 1, 5, 4}}};

    static boolean reconhece(String nome) { return nome != null && (nome.startsWith("universo_") || nome.matches("chefe_[1-8]")); }

    /** Monta a trilha do nome (universo_S_V ou chefe_N). */
    static float[] gerar(String nome) {
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
                case 5: { // death: tremolo grave e blast beat
                    double d = batida / 4;
                    for (int k = 0; k < tempos * 4; k++) m.nota("sq", (banda ? raiz : raiz + 12) + (k % 16 >= 12 ? 1 : 0), t0 + k * d, d * .8, .5, banda ? .07 : .05, 0);
                    if (banda) for (int k = 0; k < tempos * 4; k++) { if (segunda) { if (k % 2 == 0) m.bumbo(t0 + k * d, .28); else m.caixa(t0 + k * d, .12); } else { m.bumbo(t0 + k * d, .25); if (k % 4 == 2) m.caixa(t0 + k * d, .2); } if (k % 2 == 0) m.chimbal(t0 + k * d, .035); }
                    if (banda) m.nota("sq", raiz - 12, t0, comp * .98, .5, .1, 0);
                    break; }
                case 6: { // folk: compasso 6/8, flauta, drone e tambor tribal
                    m.nota("tri", raiz - 12, t0, comp, 0, .12, 0); m.nota("sq", quinta, t0, comp, .5, .025, 0);
                    if (banda) for (int k = 0; k < 6; k++) { if (k == 0 || k == 3) m.bumbo(t0 + k * batida, .35); if (k == 2 || k == 5) m.tom(t0 + k * batida, raiz + 12, .2); if (k == 3) m.caixa(t0 + k * batida, .14); m.chimbal(t0 + k * batida, .025); }
                    break; }
            }
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
        return m.fechar((int) (total * comp * TAXA));
    }

    private static final class Mix {
        final float[] a; final Random r;
        Mix(int n, Random r) { a = new float[n]; this.r = r; }
        static double f(double n) { return 440 * Math.pow(2, (n - 69) / 12); }
        void nota(String tipo, double midi, double t0, double dur, double duty, double g, double vib) {
            int i0 = (int) (t0 * TAXA), n = (int) (dur * TAXA), ata = Math.min(n / 4, (int) (.006 * TAXA)), sol = Math.min(n / 3, (int) (.03 * TAXA)); double fr = f(midi), ph = 0;
            for (int i = 0; i < n && i0 + i < a.length; i++) {
                double t = i / (double) TAXA; ph += (fr * (1 + (vib > 0 ? .004 * vib * Math.sin(2 * Math.PI * 5.5 * t) : 0))) / TAXA; double p = ph % 1, s;
                s = tipo.equals("sq") ? (p < (duty > 0 ? duty : .5) ? 1 : -1) : tipo.equals("tri") ? 4 * Math.abs(p - .5) - 1 : 2 * p - 1;
                double e = i < ata ? i / (double) ata : i > n - sol ? (n - i) / (double) sol : 1; a[i0 + i] += s * e * g;
            }
        }
        void acorde(int[] ns, double t0, double dur, double duty, double g, boolean vib) { for (int n : ns) nota("sq", n, t0, dur, duty, g, vib ? .6 : 0); }
        void sino(double midi, double t0, double g) { int i0 = (int) (t0 * TAXA), n = (int) (2.2 * TAXA); double f1 = f(midi), f2 = f(midi + 7);
            for (int i = 0; i < n && i0 + i < a.length; i++) { double t = i / (double) TAXA; a[i0 + i] += g * (Math.sin(2 * Math.PI * f1 * t) * Math.exp(-t * 2.5) + .4 * Math.sin(2 * Math.PI * f2 * t) * Math.exp(-t * 4)); } }
        void bumbo(double t0, double g) { int i0 = (int) (t0 * TAXA), n = (int) (.12 * TAXA); double ph = 0;
            for (int i = 0; i < n && i0 + i < a.length; i++) { double t = i / (double) TAXA; ph += (55 + 90 * Math.exp(-t * 40)) / TAXA; a[i0 + i] += g * Math.sin(2 * Math.PI * ph) * Math.exp(-t * 25); } }
        void caixa(double t0, double g) { ruido(t0, .15, 22, g); }
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
