package Jogo;

import java.util.Random;

/**
 * Comando inimigo: observa o piloto sem avisar e adapta os esquadroes que ainda vao entrar.
 * Nivel de ameaca 0..5 sobe com os chefes vencidos e com o poder da nave; cai um pouco quando o
 * casco esta no fim, para nunca passar do que um bom piloto consegue vencer.
 * As adaptacoes: mira com antecipacao, fogo de supressao na faixa favorita, kamikaze contra quem
 * fica parado, casco resistente a arma mais usada, desvio e recuo dos feridos.
 * De vez em quando o radio pirata deixa escapar uma pista.
 */
final class ComandoInimigo {
    static final int CASCO_FOGO = 1, CASCO_GELO = 2, DESVIO = 4, RECUO = 8;
    private static final int FAIXAS = 6;

    private final double[] faixas = new double[FAIXAS];
    private double tempoObservado, tempoParado, ultimoX = Double.NaN, ultimoY, vx, vy;
    private double acertosTiro, acertosFogo, acertosGelo;
    private int nivel;
    private boolean avisouMira, avisouFaixa, avisouParado, avisouFogo, avisouGelo, avisouDesvio;
    private String radio;

    ComandoInimigo copia() {
        ComandoInimigo c = new ComandoInimigo();
        System.arraycopy(faixas, 0, c.faixas, 0, FAIXAS);
        c.tempoObservado = tempoObservado; c.acertosTiro = acertosTiro; c.acertosFogo = acertosFogo; c.acertosGelo = acertosGelo;
        c.nivel = nivel; c.avisouMira = avisouMira; c.avisouFaixa = avisouFaixa; c.avisouParado = avisouParado;
        c.avisouFogo = avisouFogo; c.avisouGelo = avisouGelo; c.avisouDesvio = avisouDesvio;
        return c;
    }

    /** Chamado a cada quadro de combate com a posicao da nave. */
    void observar(double dt, double nx, double ny) {
        if (dt <= 0) return;
        if (!Double.isNaN(ultimoX)) {
            double f = Math.min(1, dt * 6);
            vx += ((nx - ultimoX) / dt - vx) * f; vy += ((ny - ultimoY) / dt - vy) * f;
            tempoParado = Math.hypot(nx - ultimoX, ny - ultimoY) < 90 * dt ? tempoParado + dt : Math.max(0, tempoParado - dt * 3);
        }
        ultimoX = nx; ultimoY = ny;
        // memoria com esquecimento lento: o habito recente pesa mais
        for (int i = 0; i < FAIXAS; i++) faixas[i] *= Math.pow(.985, dt);
        faixas[faixa(ny)] += dt; tempoObservado += dt;
    }
    private static int faixa(double y) { return Math.max(0, Math.min(FAIXAS - 1, (int) ((y - 80) / (480.0 / FAIXAS)))); }

    /** Como o piloto mata: 0 tiro comum, 1 gelo, 2 fogo. */
    void registrarAcerto(int tipo) {
        if (tipo == 1) acertosGelo++; else if (tipo == 2) acertosFogo++; else acertosTiro++;
        double esquece = .995; acertosTiro *= esquece; acertosFogo *= esquece; acertosGelo *= esquece;
    }

    void atualizarNivel(int chefes, int nivelNave, boolean reator, int blindagem, double fracaoCasco) {
        double base = chefes * .5 + nivelNave * .16 + (reator ? .6 : 0) + blindagem * .15;
        if (fracaoCasco < .3) base -= 1; // piedade: com o casco no fim, eles aliviam
        nivel = (int) Math.max(0, Math.min(5, Math.floor(base)));
    }
    int nivel() { return nivel; }

    /** Ponto de mira: onde a nave vai estar, com erro proposital. Antecipacao vai de 0 a 80%. */
    double[] mira(double tx, double ty, double ox, double oy, Random r) {
        if (nivel <= 0) return new double[]{tx, ty};
        double ant = Math.min(.8, nivel * .18), tempoVoo = Math.hypot(tx - ox, ty - oy) / 255.0;
        double erro = (1 - ant) * 30;
        if (!avisouMira && nivel >= 2 && Math.abs(vy) > 120) { avisouMira = true; dizer("Nao mirem nele. Mirem onde ele VAI estar."); }
        return new double[]{
            Math.max(10, Math.min(950, tx + vx * tempoVoo * ant + (r.nextDouble() - .5) * erro)),
            Math.max(90, Math.min(560, ty + vy * tempoVoo * ant + (r.nextDouble() - .5) * erro))};
    }

    /** Faixa onde o piloto mais fica (centro em y), ou NaN se ele nao tem mania. */
    double faixaFavorita() {
        if (nivel < 2 || tempoObservado < 12) return Double.NaN;
        double total = 0; int melhor = 0;
        for (int i = 0; i < FAIXAS; i++) { total += faixas[i]; if (faixas[i] > faixas[melhor]) melhor = i; }
        if (total <= 0 || faixas[melhor] / total < .34) return Double.NaN;
        if (!avisouFaixa) { avisouFaixa = true; dizer(melhor < 2 ? "Ele gosta de voar la em cima. Cubram o teto." : melhor > 3 ? "Ele se esconde la embaixo. Cubram o chao." : "Ele fica no meio. Fechem o corredor."); }
        return 80 + (melhor + .5) * 480.0 / FAIXAS;
    }

    /** Piloto parado demais: manda um kamikaze desalojar. */
    boolean pedirKamikaze() {
        if (nivel < 1 || tempoParado < 2.6) return false;
        tempoParado = 0;
        if (!avisouParado) { avisouParado = true; dizer("Ele parou. Mandem o suicida."); }
        return true;
    }

    /** Adaptacoes de um inimigo que vai entrar agora. */
    int adaptacoes(Random r) {
        int a = 0;
        double total = acertosTiro + acertosFogo + acertosGelo + 1;
        if (nivel >= 2 && acertosFogo / total > .4 && r.nextDouble() < .55) { a |= CASCO_FOGO; if (!avisouFogo) { avisouFogo = true; dizer("Ele so usa fogo. Blindem os cascos."); } }
        if (nivel >= 2 && acertosGelo / total > .4 && r.nextDouble() < .55) { a |= CASCO_GELO; if (!avisouGelo) { avisouGelo = true; dizer("O gelo dele ta acabando com a gente. Aquecam os motores."); } }
        if (nivel >= 3 && r.nextDouble() < .2 + nivel * .1) { a |= DESVIO; if (!avisouDesvio) { avisouDesvio = true; dizer("Pilotos novos na esquadra. Esses desviam."); } }
        if (nivel >= 2 && r.nextDouble() < .5) a |= RECUO;
        return a;
    }

    private void dizer(String s) { if (radio == null) radio = s; }
    /** Proxima pista do radio pirata (uma por vez), ou null. */
    String radio() { String s = radio; radio = null; return s; }
}
