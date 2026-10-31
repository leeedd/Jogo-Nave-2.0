package Jogo;

import java.awt.Color;

/**
 * Turne ao contrario: cada sistema tem a quantidade de planetas da banda. Cada planeta antes do
 * ultimo termina num integrante; o ultimo planeta e o do chefe (o vocalista).
 * Risco 0..4 por planeta (verde, amarelo, laranja, vermelho, roxo): mais perigo, mais recompensa.
 * Planetas de risco 3 ou mais tem anel, como Saturno.
 */
final class Turne {
    private Turne() { }
    /** Integrantes por setor, na ordem do mapa (0 Abismo ... 7 Citadela). */
    static final String[][] INTEGRANTES = {
        {"ENGUIA DAS PROFUNDEZAS", "CRIA DA LULA"},
        {"GLENN TIPTON", "RICHIE FAULKNER", "IAN HILL", "SCOTT TRAVIS"},
        {"STEVE HARRIS", "DAVE MURRAY", "ADRIAN SMITH", "JANICK GERS", "NICKO MCBRAIN"},
        {"PHIL CAMPBELL", "MIKKEY DEE"},
        {"KIRK HAMMETT", "LARS ULRICH", "ROBERT TRUJILLO"},
        {"KERRY KING", "GARY HOLT", "DAVE LOMBARDO"},
        {"ERIC PETERSON", "ALEX SKOLNICK", "STEVE DI GIORGIO"},
        {"TONY IOMMI", "GEEZER BUTLER", "BILL WARD", "ZAKK WYLDE"}};
    static final String[] NOMES_RISCO = {"BAIXO", "MEDIO", "ALTO", "SEVERO", "EXTREMO"};
    static final Color[] CORES_RISCO = {new Color(90, 220, 110), new Color(240, 220, 80), new Color(255, 150, 50), new Color(240, 60, 60), new Color(190, 90, 255)};

    static int setor(int s) { return Math.max(0, Math.min(7, s)); }
    /** Integrantes antes do chefe. */
    static int integrantes(int s) { return INTEGRANTES[setor(s)].length; }
    /** Planetas do sistema: os integrantes mais o planeta do chefe. */
    static int planetas(int s) { return integrantes(s) + 1; }
    static String integrante(int s, int p) { String[] l = INTEGRANTES[setor(s)]; return p >= 0 && p < l.length ? l[p] : ""; }
    /** Risco sobe ao longo do sistema e com a profundidade da campanha; o planeta do chefe e o mais perigoso. */
    static int risco(int s, int p) {
        int n = planetas(s); double t = n <= 1 ? 1 : p / (double) (n - 1);
        return Math.max(0, Math.min(4, (int) Math.round(t * 3) + (setor(s) >= 4 ? 1 : 0)));
    }
    static boolean anel(int s, int p) { return risco(s, p) >= 3; }
    /** Multiplicador de recompensa do risco: de 1x (baixo) a 2x (extremo). */
    static double premio(int risco) { return 1 + .25 * risco; }
}
