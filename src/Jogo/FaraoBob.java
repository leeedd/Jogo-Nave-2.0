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
    private static final BufferedImage ARTE = ler("tutan_bob.png"), VERGONHA = ler("tutan_bob_vergonha.png");
    static final BufferedImage SOLDADO0 = ler("egito_soldado_0.png"), SOLDADO1 = ler("egito_soldado_1.png"), CARRUAGEM = ler("egito_carruagem.png"),
            ESFINGE = ler("egito_esfinge.png"), RAINHA = ler("egito_rainha.png"), BLOCO = ler("egito_bloco.png");
    static boolean temArte() { return ARTE != null; }
    private static final int W = Config.LARGURA, H = Config.ALTURA, TOPO = Config.HUD, ALT = 340;

    private double x = W + 40, y = TOPO + 140, tempo, entrada, recarga = 1.6, flash, giro, olhos, ergue, vergonha, corrida, aponta; private int tropa;
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
        if (aponta > 0 && (aponta -= dt) <= 0) { aponta = 0; chamarTropa(tropa, naveY + 18, tiros); SomJogo.tocar(SomJogo.Efeito.IMPACTO); }
        double baseX = W - 250;
        if (entrada < 1) { entrada = Math.min(1, entrada + dt / 1.4); x = W + 40 - (W + 40 - baseX) * (1 - Math.pow(1 - entrada, 3)); return; }
        double nx = naveX + 22, ny = naveY + 18;
        // desenrolado de vez (abaixo de 30%): vergonha uma vez so
        if (!usouVergonha && vidaFrac < .3) { usouVergonha = true; vergonha = 3.2; giro = 0; SomJogo.tocar(SomJogo.Efeito.ERRO); }
        if (vergonha > 0) { vergonha = Math.max(0, vergonha - dt); if (vergonha == 0) { corrida = 1.2; desenrolo = 0; } return; }
        if (corrida > 0) { corrida = Math.max(0, corrida - dt); x = baseX + Math.sin(corrida * 6) * 40; return; }
        desenrolo = vidaFrac > .66 ? 0 : vidaFrac > .3 ? 1 : 2;
        if (giro <= 0) { // fora do tornado volta para o posto; no tornado so a velocidade manda
            x += (baseX + Math.sin(tempo * .7) * 30 - x) * Math.min(1, dt * 2);
            y += (Math.max(TOPO, Math.min(H - ALT, ny - ALT * .5)) - y) * Math.min(1, dt * .8);
        }
        if (tonto > 0) { x += (baseX - x) * Math.min(1, dt * 2); return; }
        if (giro > 0) { // tornado: quica pela tela toda, cada batida mais rapido, e solta ataduras
            giro = Math.max(0, giro - dt);
            x += tvx * dt; y += tvy * dt;
            boolean bateu = false;
            if (x < -60) { x = -60; tvx = Math.abs(tvx); bateu = true; } else if (x > W - 240) { x = W - 240; tvx = -Math.abs(tvx); bateu = true; }
            if (y < TOPO - 30) { y = TOPO - 30; tvy = Math.abs(tvy); bateu = true; } else if (y > H - ALT + 20) { y = H - ALT + 20; tvy = -Math.abs(tvy); bateu = true; }
            if (bateu) {
                double v = Math.min(1750, Math.hypot(tvx, tvy) * 1.25), a = Math.atan2(tvy, tvx) + (sorte.nextDouble() - .5) * .9;
                if (sorte.nextDouble() < .3) a = Math.atan2(ny - (y + ALT * .5), nx - (x + 150)); // as vezes vem direto na nave
                tvx = Math.cos(a) * v; tvy = Math.sin(a) * v;
                if (Math.abs(tvx) < v * .7) { tvx = Math.signum(tvx == 0 ? 1 : tvx) * v * .7; tvy = Math.signum(tvy) * v * .71; } // mais de lado a lado que de cima a baixo SomJogo.tocar(SomJogo.Efeito.IMPACTO);
            }
            if (giro == 0) tonto = 1.2;
            if ((int) (giro * 12) != (int) ((giro + dt) * 12)) { double a = tempo * 15, cx = x + 150, cy = y + ALT * .5;
                for (int k = 0; k < 2; k++) { double aa = a + k * Math.PI; tiros.add(new TiroChefe(cx, cy, Math.cos(aa) * 260, Math.sin(aa) * 260, false, TiroChefe.ATADURA, TiroChefe.Movimento.ONDA)); } }
            return;
        }
        recarga -= dt * dif.cadenciaInimigos * (vidaFrac < .5 ? 1.3 : 1);
        if (recarga > 0 || tiros.size() > 55) return;
        int g = golpes++ % 4;
        if (g == 0) { giro = 4.6; double a = Math.atan2(ny - (y + ALT * .5), nx - (x + 150)); tvx = Math.cos(a) * 950; tvy = Math.sin(a) * 950; }
        else if (g == 2) { // raio dos olhos: dois feixes vermelhos em X
            olhos = .5; double ex = x + 135, ey = y + 62;
            for (int i = -1; i <= 1; i += 2) { double a = Math.atan2(ny - ey, nx - ex) + i * .22;
                for (int k = 0; k < 5; k++) tiros.add(new TiroChefe(ex, ey, Math.cos(a) * (380 + k * 30), Math.sin(a) * (380 + k * 30), k == 0, TiroChefe.ENERGIA, TiroChefe.Movimento.RETO).comCor(new Color(255, 40, 40))); }
        } else if (g == 3) { // piramide: blocos caem do teto numa piramide, com um buraco perto da nave
            ergue = .8; int buraco = (int) Math.max(1, Math.min(7, (nx - 40) / 110));
            for (int c = 0; c < 8; c++) { if (Math.abs(c - buraco) < 1) continue; double bx = 60 + c * 110;
                tiros.add(new TiroChefe(bx, TOPO - 20 - Math.abs(c - 3.5) * 40, -10, 120, true, TiroChefe.BLOCO, TiroChefe.Movimento.ARCO)); }
        } else if (g == 1) { // exercito: aponta e a tropa atravessa a tela da direita para a esquerda
            ergue = 1.6; aponta = .7; tropa = golpes / 4 % 3; // aponta primeiro; a tropa entra quando o braco desce
        }
        recarga = vidaFrac < .5 ? 1.25 : 1.7;
    }

    /** A tropa chamada pelo dedo do farao atravessa a tela da direita para a esquerda. */
    private void chamarTropa(int tipo, double ny, List<TiroChefe> tiros) {
        for (int i = 0; i < 5; i++) tiros.add(new TiroChefe(W + 40 + i * 60, TOPO + 50 + i * 95, -190, 0, true, TiroChefe.SOLDADO, TiroChefe.Movimento.RETO));
        if (tipo == 0) tiros.add(new TiroChefe(W + 260, ny, -430, 0, true, TiroChefe.CARRUAGEM, TiroChefe.Movimento.RETO));
        else if (tipo == 1) tiros.add(new TiroChefe(W + 220, H - 110, -160, 0, true, TiroChefe.ESFINGE, TiroChefe.Movimento.RETO));
        else { tiros.add(new TiroChefe(W + 120, TOPO + 120, -110, 0, true, TiroChefe.RAINHA, TiroChefe.Movimento.RETO));
            for (int i = -1; i <= 1; i++) tiros.add(new TiroChefe(W - 40, TOPO + 140, -300, i * 90, false, TiroChefe.ENERGIA, TiroChefe.Movimento.ONDA).comCor(new Color(120, 220, 90))); }
    }

    void ferido() { flash = .16; }
    Rectangle bounds() { return new Rectangle((int) x + 80, (int) y + 30, 150, ALT - 40); }
    boolean emGiro() { return giro > 0; }
    /** Encosto no tornado: machuca, com uma pausa curta entre um dano e outro. */
    boolean contato(Rectangle nave) { if (giro <= 0 || esperaContato > 0 || !bounds().intersects(nave)) return false; esperaContato = .7; return true; }

    private static final int P = 4; // tamanho do pixel do tornado
    private static final Color[] TOR = {new Color(92, 58, 34), new Color(168, 138, 92), new Color(226, 206, 160), new Color(255, 246, 214)};
    void desenhar(Graphics2D tela) {
        if (ARTE == null) return;
        Graphics2D g = (Graphics2D) tela.create();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        int q = vergonha > 0 ? 4 : corrida > 0 ? 5 : giro > 0 ? 1 : desenrolo == 0 ? 0 : desenrolo == 1 ? 2 : 3;
        int fw = ARTE.getWidth() / 6, fh = ARTE.getHeight(), w = fw * ALT / fh, px = (int) x + (300 - w) / 2, py = (int) y;
        if (giro > 0) { // funil de tornado em pixel art: aneis de atadura em pixel grosso (P), paleta curta, sem suavizar
            int cx = px + w / 2, topo = py - 10, base = py + ALT + 14, n = 9, bw = (int) (w * 1.7) + 80, bx = cx - bw / 2;
            BufferedImage tras = new BufferedImage(bw / P, (base - topo + 30) / P, BufferedImage.TYPE_INT_ARGB), frente = new BufferedImage(tras.getWidth(), tras.getHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics2D gt = tras.createGraphics(), gf = frente.createGraphics();
            for (int k = 0; k < n; k++) {
                double f = k / (double) (n - 1); int ry = (int) ((base - topo) * f) / P + 4, rw = (int) ((w * 1.5 * (1 - f) + 30) / P), rh = (int) ((26 + 30 * (1 - f)) / P);
                int c = tras.getWidth() / 2 + (int) (Math.sin(tempo * 16 + f * 4) * 16 * f / P), ini = (int) ((tempo * 2400 + k * 40) % 360);
                gt.setColor(TOR[0]); gt.drawArc(c - rw / 2, ry - rh / 2 + 1, rw, rh, 0, 180); gt.setColor(TOR[1]); gt.drawArc(c - rw / 2, ry - rh / 2, rw, rh, 0, 180);
                gf.setColor(TOR[0]); gf.drawArc(c - rw / 2, ry - rh / 2 + 1, rw, rh, 180, 180);
                gf.setColor(TOR[2]); gf.drawArc(c - rw / 2, ry - rh / 2, rw, rh, 180, 180);
                gf.setColor(TOR[3]); gf.drawArc(c - rw / 2, ry - rh / 2, rw, rh, 180 + ini % 180, 50); // brilho correndo no anel
                if (k % 3 == 1) { int fx = c + (int) (Math.cos(tempo * 26 + k) * rw / 2), fy = ry + (int) (Math.sin(tempo * 26 + k) * rh / 2); gf.setColor(TOR[2]); gf.fillRect(fx, fy, 3, 1); gf.fillRect(fx + 2, fy + 1, 2, 1); }
            }
            java.util.Random r = new java.util.Random((long) (tempo * 15));
            for (int k = 0; k < 18; k++) { gf.setColor(TOR[r.nextInt(3)]); gf.fillRect(tras.getWidth() / 2 - 14 + r.nextInt(28), tras.getHeight() - 10 + r.nextInt(8), 1 + r.nextInt(2), 1); } // areia na ponta
            gt.dispose(); gf.dispose();
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
            g.drawImage(tras, bx, topo - 4, tras.getWidth() * P, tras.getHeight() * P, null);
            boolean vira = (int) (tempo * 40) % 2 == 0; double sq = .55 + .2 * Math.abs(Math.cos(tempo * 62));
            int ww = (int) (w * sq), hh = (int) (ALT * .8), xx = cx - ww / 2, yy = py + 10;
            if (vira) g.drawImage(ARTE, xx + ww, yy, xx, yy + hh, q * fw, 0, q * fw + fw, fh, null); else g.drawImage(ARTE, xx, yy, xx + ww, yy + hh, q * fw, 0, q * fw + fw, fh, null);
            g.drawImage(frente, bx, topo - 4, frente.getWidth() * P, frente.getHeight() * P, null);
        } else if (tonto > 0) { g.drawImage(ARTE, px, py, px + w, py + ALT, 0, 0, fw, fh, null);
            g.setColor(new Color(255, 235, 110)); for (int k = 0; k < 3; k++) { double a = tempo * 6 + k * 2.1; g.fillOval(px + w / 2 + (int) (Math.cos(a) * 40) - 5, py + 6 + (int) (Math.sin(a) * 10), 10, 10); }
            // olhos girando de tontura: espirais vermelhas no lugar dos olhos
            double e = ALT / (double) fh; int[][] olhosArte = {{168, 174}, {204, 176}};
            g.setStroke(new java.awt.BasicStroke(2));
            for (int[] o : olhosArte) { int cx = px + (int) (o[0] * e), cy = py + (int) (o[1] * e);
                g.setColor(new Color(40, 20, 20)); g.fillOval(cx - 9, cy - 9, 18, 18);
                g.setColor(new Color(255, 70, 50)); double a0 = tempo * 14;
                for (int k = 0; k < 14; k++) { double a = a0 + k * .55, r = 1 + k * .55; int x1 = cx + (int) (Math.cos(a) * r), y1 = cy + (int) (Math.sin(a) * r);
                    double a2 = a + .55, r2 = r + .55; g.drawLine(x1, y1, cx + (int) (Math.cos(a2) * r2), cy + (int) (Math.sin(a2) * r2)); } }
        } else if (q == 4 && VERGONHA != null) { int vw = VERGONHA.getWidth() * ALT / VERGONHA.getHeight(); g.drawImage(VERGONHA, px + (w - vw) / 2, py, vw, ALT, null); }
        else g.drawImage(ARTE, px, py, px + w, py + ALT, q * fw, 0, q * fw + fw, fh, null);
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
