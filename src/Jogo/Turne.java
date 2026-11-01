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
    /** Instrumento de cada integrante (mesma ordem de INTEGRANTES): G guitarra, B baixo, D bateria, C criatura. */
    static final String[] INSTRUMENTOS = {"CC", "GGBD", "BGGGD", "GD", "GDB", "GGD", "GGB", "GBDG"};
    /** Integrantes cuja arte traz o quadro 4 caido (derrotado) em vez do bate-cabeca. */
    static boolean quadroQueda(String nome) { return nome.equals("GLENN TIPTON") || nome.equals("ROB HALFORD"); }
    static char instrumento(String nome) {
        for (int s = 0; s < 8; s++) for (int p = 0; p < INTEGRANTES[s].length; p++) if (INTEGRANTES[s][p].equals(nome)) return INSTRUMENTOS[s].charAt(p);
        return idVocalista(nome) > 0 ? 'V' : 'C';
    }
    /**
     * Ataque proprio de cada integrante: PADRAO PROJETIL R G B (mesma ordem de INTEGRANTES).
     * Padroes: RAIO, RAIO2 (dois feixes), VARRE (feixe que varre), LEQUE, LEQUE7, PAREDE (com buraco),
     * CHUVA (de cima), ANEL, ESPIRAL, ZIGUE, RAJADA (metralhadora), CRUZ (de cima e de baixo).
     * Projeteis: ENERGIA, NOTA, PRATO, ONDA, AS.
     */
    static final String[][] ATAQUES = {
        {"ZIGUE ENERGIA 60 220 255", "ANEL ENERGIA 170 70 220"},
        {"ESPIRAL ENERGIA 255 150 40", "LEQUE7 ENERGIA 255 90 30", "PAREDE ONDA 255 150 60", "CHUVA PRATO 255 200 80"},
        {"RAJADA NOTA 70 140 255", "RAIO2 ENERGIA 80 230 255", "VARRE ENERGIA 120 240 255", "ESPIRAL NOTA 90 220 255", "ANEL PRATO 255 210 90"},
        {"LEQUE AS 255 120 40", "RAJADA PRATO 255 120 30"},
        {"RAIO ENERGIA 170 60 255", "LEQUE PRATO 255 210 90", "LEQUE ONDA 80 150 255"},
        {"CRUZ ENERGIA 240 40 40", "PAREDE ENERGIA 220 30 30", "ZIGUE PRATO 230 60 50"},
        {"RAJADA ENERGIA 80 240 110", "LEQUE7 NOTA 110 255 140", "ZIGUE ONDA 80 230 120"},
        {"CHUVA ENERGIA 150 70 220", "RAJADA ONDA 150 90 255", "PAREDE PRATO 220 190 120", "ANEL ENERGIA 255 210 60"}};
    /** Vocalistas (os chefes do setor), que entram junto com a banda reunida na fortaleza. Abismo nao tem: a Lula e o chefe. */
    static final String[] VOCALISTAS = {"", "ROB HALFORD", "BRUCE DICKINSON", "LEMMY KILMISTER", "JAMES HETFIELD", "TOM ARAYA", "CHUCK BILLY", "OZZY OSBOURNE"};
    /** Id do chefe de cada setor (mesma ordem da Fase: 8 Lula, 2 Rob, 3 Bruce, 4 Lemmy, 5 James, 6 Tom, 7 Chuck, 1 Ozzy). */
    static final int[] ID_CHEFE = {8, 2, 3, 4, 5, 6, 7, 1};
    static final String[] ATAQUES_VOCAL = {"", "CHUVA ENERGIA 255 120 30", "CRUZ NOTA 90 230 220", "PAREDE AS 255 150 60", "RAIO2 ENERGIA 80 160 255", "CHUVA NOTA 240 60 70", "ANEL NOTA 100 240 130", "ESPIRAL ENERGIA 190 90 240"};
    static int idVocalista(String nome) { for (int s = 1; s < 8; s++) if (VOCALISTAS[s].equals(nome)) return ID_CHEFE[s]; return -1; }
    static String[] ataque(String nome) {
        for (int s = 1; s < 8; s++) if (VOCALISTAS[s].equals(nome)) return ATAQUES_VOCAL[s].split(" ");
        for (int s = 0; s < 8; s++) for (int p = 0; p < INTEGRANTES[s].length; p++) if (INTEGRANTES[s][p].equals(nome)) return ATAQUES[s][p].split(" ");
        return "RAIO ENERGIA 240 60 50".split(" ");
    }
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
