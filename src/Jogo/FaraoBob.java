package Jogo;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.List;

/**
 * Tutan-Bob, o Farao das Ataduras (ato I do setor do Vortice Negro). Gira como piao soltando
 * ataduras, solta raios dos olhos de rubi, ergue a piramide e chama o exercito. A cada golpe de
 * piao desenrola um pouco; desenrolado de vez, fica so o esqueleto, olha para a tela envergonhado
 * e cobre as partes: e a hora de bater (dano dobrado). Depois sai correndo e se enrola de novo.
 * Arte: tutan_bob.png (6 quadros: enfaixado, piao, meio desenrolado, quase osso, envergonhado, correndo).
 */
final class FaraoBob {
    private static final BufferedImage ARTE = ler("tutan_bob.png");
    static final BufferedImage SOLDADO0 = ler("egito_soldado_0.png"), SOLDADO1 = ler("egito_soldado_1.png"), CARRUAGEM = ler("egito_carruagem.png"),
            ESFINGE = ler("egito_esfinge.png"), RAINHA = ler("egito_rainha.png"), BLOCO = ler("egito_bloco.png");
    static boolean temArte() { return ARTE != null; }
    private static final int W = Config.LARGURA, H = Config.ALTURA, TOPO = Config.HUD, ALT = 340;

    private double x = W + 40, y = TOPO + 140, tempo, entrada, recarga = 1.6, flash, giro, olhos, ergue, vergonha, corrida;
    private int golpes, desenrolo; // 0 enfaixado, 1 meio, 2 quase osso
    private boolean usouVergonha;
    private double tvx, tvy, esperaContato, tonto;
    private final java.util.Random sorte = new java.util.Random(7);
    private double vidaFrac = 1;

    double entrada() { return entrada; }
    void vida(double f) { vidaFrac = f; }
    boolean envergonhado() { return vergonha > 0; }

    void atualizar(double dt, int naveX, int naveY, Dificuldade dif, List<TiroChefe> tiros) {
        tempo += dt; flash = Math.max(0, flash - dt); esperaContato = Math.max(0, esperaContato - dt); tonto = Math.max(0, tonto - dt); olhos = Math.max(0, olhos - dt); ergue = Math.max(0, ergue - dt);
        double baseX = W - 250;
        if (entrada < 1) { entrada = Math.min(1, entrada + dt / 1.4); x = W + 40 - (W + 40 - baseX) * (1 - Math.pow(1 - entrada, 3)); return; }
        double nx = naveX + 22, ny = naveY + 18;
        // desenrolado de vez (abaixo de 30%): vergonha uma vez so
        if (!usouVergonha && vidaFrac < .3) { usouVergonha = true; vergonha = 3.2; giro = 0; SomJogo.tocar(SomJogo.Efeito.ERRO); }
        if (vergonha > 0) { vergonha = Math.max(0, vergonha - dt); if (vergonha == 0) { corrida = 1.2; desenrolo = 0; } return; }
        if (corrida > 0) { corrida = Math.max(0, corrida - dt); x = baseX + Math.sin(corrida * 6) * 40; return; }
        desenrolo = vidaFrac > .66 ? 0 : vidaFrac > .3 ? 1 : 2;
        x += (baseX + Math.sin(tempo * .7) * 30 - x) * Math.min(1, dt * 2);
        y += (Math.max(TOPO, Math.min(H - ALT, ny - ALT * .5)) - y) * Math.min(1, dt * (giro > 0 ? 2.2 : .8));
        if (tonto > 0) { x += (baseX - x) * Math.min(1, dt * 2); return; }
        if (giro > 0) { // tornado: quica pela tela toda, cada batida mais rapido, e solta ataduras
            giro = Math.max(0, giro - dt);
            x += tvx * dt; y += tvy * dt;
            boolean bateu = false;
            if (x < -60) { x = -60; tvx = Math.abs(tvx); bateu = true; } else if (x > W - 240) { x = W - 240; tvx = -Math.abs(tvx); bateu = true; }
            if (y < TOPO - 30) { y = TOPO - 30; tvy = Math.abs(tvy); bateu = true; } else if (y > H - ALT + 20) { y = H - ALT + 20; tvy = -Math.abs(tvy); bateu = true; }
            if (bateu) {
                double v = Math.min(980, Math.hypot(tvx, tvy) * 1.18), a = Math.atan2(tvy, tvx) + (sorte.nextDouble() - .5) * .9;
                if (sorte.nextDouble() < .4) a = Math.atan2(ny - (y + ALT * .5), nx - (x + 150)); // as vezes vem direto na nave
                tvx = Math.cos(a) * v; tvy = Math.sin(a) * v; SomJogo.tocar(SomJogo.Efeito.IMPACTO);
            }
            if (giro == 0) tonto = 1.2;
            if ((int) (giro * 12) != (int) ((giro + dt) * 12)) { double a = tempo * 7, cx = x + 150, cy = y + ALT * .5;
                for (int k = 0; k < 2; k++) { double aa = a + k * Math.PI; tiros.add(new TiroChefe(cx, cy, Math.cos(aa) * 260, Math.sin(aa) * 260, false, TiroChefe.ATADURA, TiroChefe.Movimento.ONDA)); } }
            return;
        }
        recarga -= dt * dif.cadenciaInimigos * (vidaFrac < .5 ? 1.3 : 1);
        if (recarga > 0 || tiros.size() > 55) return;
        int g = golpes++ % 4;
        if (g == 0) { giro = 4.2; double a = Math.atan2(ny - (y + ALT * .5), nx - (x + 150)); tvx = Math.cos(a) * 380; tvy = Math.sin(a) * 380; }
        else if (g == 1) { // raio dos olhos: dois feixes vermelhos em X
            olhos = .5; double ex = x + 135, ey = y + 62;
            for (int i = -1; i <= 1; i += 2) { double a = Math.atan2(ny - ey, nx - ex) + i * .22;
                for (int k = 0; k < 5; k++) tiros.add(new TiroChefe(ex, ey, Math.cos(a) * (380 + k * 30), Math.sin(a) * (380 + k * 30), k == 0, TiroChefe.ENERGIA, TiroChefe.Movimento.RETO).comCor(new Color(255, 40, 40))); }
        } else if (g == 2) { // piramide: blocos caem do teto numa piramide, com um buraco perto da nave
            ergue = .8; int buraco = (int) Math.max(1, Math.min(7, (nx - 40) / 110));
            for (int c = 0; c < 8; c++) { if (Math.abs(c - buraco) < 1) continue; double bx = 60 + c * 110;
                tiros.add(new TiroChefe(bx, TOPO - 20 - Math.abs(c - 3.5) * 40, -10, 120, true, TiroChefe.BLOCO, TiroChefe.Movimento.ARCO)); }
        } else { // exercito: aponta e a tropa atravessa a tela da direita para a esquerda
            ergue = 1.0; int tipo = golpes / 4 % 4;
            if (tipo == 0) for (int i = 0; i < 4; i++) tiros.add(new TiroChefe(W + 40 + i * 70, TOPO + 60 + i * 110, -170, 0, true, TiroChefe.SOLDADO, TiroChefe.Movimento.RETO));
            else if (tipo == 1) tiros.add(new TiroChefe(W + 120, ny, -420, 0, true, TiroChefe.CARRUAGEM, TiroChefe.Movimento.RETO));
            else if (tipo == 2) tiros.add(new TiroChefe(W + 150, H - 120, -150, 0, true, TiroChefe.ESFINGE, TiroChefe.Movimento.RETO));
            else { tiros.add(new TiroChefe(W + 100, TOPO + 120, -110, 0, true, TiroChefe.RAINHA, TiroChefe.Movimento.RETO));
                for (int i = -1; i <= 1; i++) tiros.add(new TiroChefe(W - 40, TOPO + 140, -300, i * 90, false, TiroChefe.ENERGIA, TiroChefe.Movimento.ONDA).comCor(new Color(120, 220, 90))); }
        }
        recarga = vidaFrac < .5 ? 1.25 : 1.7;
    }

    void ferido() { flash = .16; }
    Rectangle bounds() { return new Rectangle((int) x + 80, (int) y + 30, 150, ALT - 40); }
    boolean emGiro() { return giro > 0; }
    /** Encosto no tornado: machuca, com uma pausa curta entre um dano e outro. */
    boolean contato(Rectangle nave) { if (giro <= 0 || esperaContato > 0 || !bounds().intersects(nave)) return false; esperaContato = .7; return true; }

    void desenhar(Graphics2D tela) {
        if (ARTE == null) return;
        Graphics2D g = (Graphics2D) tela.create();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        int q = vergonha > 0 ? 4 : corrida > 0 ? 5 : giro > 0 ? 1 : desenrolo == 0 ? 0 : desenrolo == 1 ? 2 : 3;
        int fw = ARTE.getWidth() / 6, fh = ARTE.getHeight(), w = fw * ALT / fh, px = (int) x + (300 - w) / 2, py = (int) y;
        if (giro > 0) { // girando de verdade: espelha rapido e um redemoinho de ataduras em volta
            boolean vira = (int) (tempo * 22) % 2 == 0; double sq = .75 + .25 * Math.abs(Math.cos(tempo * 34));
            int ww = (int) (w * sq), xx = px + (w - ww) / 2;
            if (vira) g.drawImage(ARTE, xx + ww, py, xx, py + ALT, q * fw, 0, q * fw + fw, fh, null); else g.drawImage(ARTE, xx, py, xx + ww, py + ALT, q * fw, 0, q * fw + fw, fh, null);
            g.setStroke(new java.awt.BasicStroke(5));
            for (int k = 0; k < 6; k++) { double fase = tempo * 14 + k; int ry = py + 30 + k * (ALT - 60) / 5, rw = (int) (w * (.7 + .25 * Math.sin(fase)));
                g.setColor(new Color(232, 215, 175, 190)); g.drawArc(px + (w - rw) / 2, ry - 10, rw, 22, (int) Math.toDegrees(fase) % 360, 200); }
        } else if (tonto > 0) { g.drawImage(ARTE, px, py, px + w, py + ALT, 0, 0, fw, fh, null);
            g.setColor(new Color(255, 235, 110)); for (int k = 0; k < 3; k++) { double a = tempo * 6 + k * 2.1; g.fillOval(px + w / 2 + (int) (Math.cos(a) * 40) - 5, py + 6 + (int) (Math.sin(a) * 10), 10, 10); }
        } else g.drawImage(ARTE, px, py, px + w, py + ALT, q * fw, 0, q * fw + fw, fh, null);
        if (flash > 0) { g.setColor(new Color(255, 230, 170, (int) (flash / .16 * 230))); for (int k = 0; k < 6; k++) { double a = k * Math.PI / 3; int r = 12 + (int) ((.16 - flash) * 160); g.fillRect(px + w / 2 + (int) (Math.cos(a) * r), py + ALT / 2 + (int) (Math.sin(a) * r), 4, 4); } }
        if (olhos > 0) { g.setColor(new Color(255, 60, 40, 200)); g.fillOval((int) x + 125, (int) y + 52, 22, 18); }
        if (ergue > 0) { // olho de horus no ceu chamando a tropa
            float a = (float) Math.min(1, ergue * 2); g.setComposite(AlphaComposite.SrcOver.derive(a * .8f)); g.setColor(new Color(255, 215, 90));
            g.setStroke(new java.awt.BasicStroke(5)); g.drawArc(W / 2 - 90, TOPO + 20, 180, 70, 0, 180); g.drawArc(W / 2 - 90, TOPO + 20, 180, 70, 180, 180); g.fillOval(W / 2 - 18, TOPO + 38, 36, 36);
        }
        if (vergonha > 0) {
            g.setComposite(AlphaComposite.SrcOver); g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 18)); g.setColor(new Color(255, 230, 120));
            g.drawString("...", px + w / 2 - 12, py - 6);
            if ((int) (tempo * 4) % 2 == 0) { g.setColor(new Color(255, 80, 80)); g.drawString("DANO x2!", px + w / 2 - 40, py + ALT + 18); }
        }
        g.dispose();
    }
    private static BufferedImage ler(String n) {
        java.net.URL u = FaraoBob.class.getResource("/ImagemIcon/" + n);
        if (u == null) return null;
        try { return javax.imageio.ImageIO.read(u); } catch (java.io.IOException e) { return null; }
    }
}
