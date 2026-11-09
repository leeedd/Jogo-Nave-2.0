package Jogo;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.List;

/**
 * Ato I do James Hetfield (Master of Puppets): uma mao de marionetista no alto controla um boneco de
 * madeira do James. Tiro no boneco nao adianta: so a mao leva dano. Quando a nave fica na altura da
 * mao, ela puxa o boneco para cima, na frente dela, como escudo (com folego limitado). Ataques
 * comandados pela mao: raio da guitarra (puxa), chuva de notas (abre os dedos), investida do boneco
 * (fecha o punho). Mao destruida: o boneco desmonta e quem puxava os fios aparece.
 */
final class MarionetePuppets {
    private static final BufferedImage MAO = ler("mao_marionetista.png"), BONECO = ler("hetfield_marionete.png"), QUEBRA = ler("hetfield_marionete_quebra.png");
    static boolean temArte() { return MAO != null && BONECO != null; }
    private static final int W = Config.LARGURA, H = Config.ALTURA, TOPO = Config.HUD;
    private static final int MAO_T = 230, BON_T = 300;

    private double tempo, entrada, hx = W + 40, hy, bOff = 150, bDx, recarga = 1.8, comando, protegendo, folego = 2.6, cansado, flash, faisca;
    private int golpes, comandoTipo = -1;
    private boolean acertouBoneco;
    private double vidaFrac = 1;

    double entrada() { return entrada; }
    void vida(double f) { vidaFrac = f; }

    private double baseX() { return W - MAO_T / 2.0 - 70; }
    private double bonecoY() { return hy + bOff; }
    private double bonecoX() { return hx + bDx; }
    Rectangle mao() { return new Rectangle((int) hx - 88, (int) hy + 40, 176, 170); }
    Rectangle boneco() { return new Rectangle((int) bonecoX() - 70, (int) bonecoY() + 50, 140, BON_T - 70); }
    Rectangle bounds() { return mao().union(boneco()); }

    void atualizar(double dt, int naveX, int naveY, Dificuldade dif, List<TiroChefe> tiros) {
        tempo += dt; flash = Math.max(0, flash - dt); faisca = Math.max(0, faisca - dt);
        if (entrada < 1) { entrada = Math.min(1, entrada + dt / 1.4); hx = W + 40 - (W + 40 - baseX()) * (1 - Math.pow(1 - entrada, 3)); }
        else hx += (baseX() + Math.sin(tempo * .6) * 26 - hx) * Math.min(1, dt * 2);
        // a mao patrulha a metade de cima da tela
        double alvoY = TOPO + 20 + (Math.sin(tempo * .55) * .5 + .5) * 170;
        hy += (alvoY - hy) * Math.min(1, dt * 1.6);
        // escudo: se a nave esta na altura da mao, o boneco sobe na frente dela, ate cansar
        double naveCy = naveY + 18; Rectangle m = mao();
        boolean naMira = naveCy > m.y - 10 && naveCy < m.y + m.height + 10 && comandoTipo != 2;
        if (cansado > 0) { cansado = Math.max(0, cansado - dt); protegendo = 0; }
        else if (naMira) { protegendo += dt; folego -= dt; if (folego <= 0) { cansado = 1.6; folego = 2.6; } }
        else { protegendo = 0; folego = Math.min(2.6, folego + dt * .8); }
        double alvoOff = protegendo > 0 && cansado <= 0 ? -10 : 150;
        bOff += (alvoOff - bOff) * Math.min(1, dt * 7);
        if (entrada < 1) return;
        // comandos da mao
        if (comando > 0) {
            comando = Math.max(0, comando - dt);
            if (comandoTipo == 2) { double p = 1 - comando / 1.3, arco = Math.sin(p * Math.PI); bDx = -arco * Math.max(0, bonecoX() - bDx - (naveX + 140)); }
            if (comando == 0) { comandoTipo = -1; bDx = 0; }
            return;
        }
        recarga -= dt * dif.cadenciaInimigos * (vidaFrac < .4 ? 1.35 : 1);
        if (recarga > 0 || tiros.size() > 45) return;
        golpes++; comandoTipo = golpes % 3; comando = comandoTipo == 2 ? 1.3 : .6;
        double ox = bonecoX() - BON_T * .38, oy = bonecoY() + BON_T * .58;
        if (comandoTipo == 0) {          // puxa o fio: raio da guitarra
            double a = Math.atan2(naveCy - oy, naveX + 22 - ox);
            for (int i = -1; i <= 1; i++) tiros.add(new TiroChefe(ox, oy, Math.cos(a + i * .12) * 360, Math.sin(a + i * .12) * 360, i == 0, TiroChefe.ENERGIA, TiroChefe.Movimento.ACELERADO).comCor(new Color(110, 190, 255)));
        } else if (comandoTipo == 1) {   // abre os dedos: notas caindo das pontas dos dedos
            for (int i = 0; i < 5; i++) tiros.add(new TiroChefe(hx - 70 + i * 35, hy + MAO_T - 10, -170 - i * 25, -40, false, TiroChefe.NOTA, TiroChefe.Movimento.ARCO).comCor(new Color(200, 160, 255)));
        } else SomJogo.tocar(SomJogo.Efeito.CHEFE_ENTRADA); // fecha o punho: o boneco avanca
        recarga = vidaFrac < .4 ? 1.3 : 1.7;
    }

    /** Tiro do jogador: o boneco segura (sem dano); so a mao conta. */
    boolean colide(Rectangle r) {
        if (r.intersects(boneco())) { acertouBoneco = true; faisca = .12; return true; }
        return r.intersects(mao());
    }
    /** true se o ultimo acerto foi no boneco (o dano nao vale). */
    boolean consumirBloqueio() { boolean b = acertouBoneco; acertouBoneco = false; return b; }
    void ferido() { flash = .16; }
    boolean investindo() { return comandoTipo == 2 && comando > 0; }

    void desenhar(Graphics2D tela, float alfa) {
        Graphics2D g = (Graphics2D) tela.create();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        if (alfa < 1) g.setComposite(AlphaComposite.SrcOver.derive(Math.max(0, alfa)));
        int qb = comandoTipo == 0 ? 2 : protegendo > 0 && cansado <= 0 ? 1 : cansado > 0 ? 3 : 0;
        // o boneco fica atras da mao quando esta pendurado e na frente quando protege
        boolean frente = bOff < 60;
        if (!frente) quadro(g, BONECO, qb, bonecoX(), bonecoY(), BON_T);
        int qm = vidaFrac < .35 || comandoTipo == 2 ? 3 : comandoTipo == 1 ? 2 : comandoTipo == 0 || (protegendo > 0 && cansado <= 0) ? 1 : 0;
        quadro(g, MAO, qm, hx, hy, MAO_T);
        if (frente) quadro(g, BONECO, qb, bonecoX(), bonecoY(), BON_T);
        if (flash > 0) { Rectangle m = mao(); g.setColor(new Color(255, 120, 120, (int) (flash / .16 * 230))); for (int k = 0; k < 8; k++) { double a = k * Math.PI / 4; int r = 20 + (int) ((.16 - flash) * 260); g.fillRect((int) m.getCenterX() + (int) (Math.cos(a) * r) - 3, (int) m.getCenterY() + (int) (Math.sin(a) * r) - 3, 6, 6); } }
        if (faisca > 0) { Rectangle b = boneco(); g.setColor(new Color(255, 230, 150)); for (int k = 0; k < 6; k++) g.fillRect(b.x + 10 + (k * 37) % b.width, b.y + 30 + (k * 53) % 120, 4, 4); }
        g.dispose();
    }

    /** O boneco desmonta (p de 0 a 1) e a mao cai rachada. */
    void desenharQuebra(Graphics2D tela, double p, double cx, double topo, int t) {
        Graphics2D g = (Graphics2D) tela.create();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.setComposite(AlphaComposite.SrcOver.derive((float) Math.max(0, 1 - p * 1.4)));
        quadro(g, MAO, 3, hx + p * 40, hy - p * 260, MAO_T);   // a mao some para cima, rachada
        g.setComposite(AlphaComposite.SrcOver.derive((float) Math.max(0, Math.min(1, (1 - p) * 2.2))));
        BufferedImage q = QUEBRA != null ? QUEBRA : BONECO;
        int k = Math.min(3, (int) (p * 5));
        quadro(g, q, k, cx, topo + p * 30, t);
        g.dispose();
    }

    private static void quadro(Graphics2D g, BufferedImage folha, int q, double cx, double topo, int t) {
        if (folha == null) return;
        int fw = folha.getWidth() / 4, fh = folha.getHeight(), w = t * fw / fh;
        int x = (int) cx - w / 2, y = (int) topo;
        g.drawImage(folha, x, y, x + w, y + t, q * fw, 0, q * fw + fw, fh, null);
    }
    private static BufferedImage ler(String n) {
        java.net.URL u = MarionetePuppets.class.getResource("/ImagemIcon/" + n);
        if (u == null) return null;
        try { return javax.imageio.ImageIO.read(u); } catch (java.io.IOException e) { return null; }
    }
}
