package Jogo;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.ArrayDeque;
import java.util.Random;

/**
 * Radio pirata: a frequencia do inimigo vaza na cabine. Janela no canto com o retrato do capitao,
 * chiado ao abrir e fechar e o texto batido letra por letra. Uma fala por vez, com intervalo,
 * para nao virar falacao. Retrato: radio_capitao.png (4 quadros: boca fechada, aberta, meio, estatica).
 */
final class RadioPirata {
    private static final BufferedImage RETRATO = ler("radio_capitao.png");
    private static final double ABRIR = .35, LETRAS_POR_S = 38, LEITURA = 2.6, FECHAR = .3, INTERVALO = 7;
    private static final int X = 12, Y = 448, W = 372, H = 74;

    private final ArrayDeque<String> fila = new ArrayDeque<>();
    private final Random acaso = new Random(42);
    private String atual;
    private double tempo, espera;

    /** Fala normal: entra na fila e respeita o intervalo. Repetida na fila e ignorada. */
    void falar(String s) { if (s != null && !s.isEmpty() && !s.equals(atual) && !fila.contains(s) && fila.size() < 3) fila.add(s); }
    /** Fala urgente: fura a fila (vai para a frente) e nao espera o intervalo. */
    void urgente(String s) { if (s == null || s.equals(atual) || fila.contains(s)) return; fila.addFirst(s); espera = 0; }
    boolean falando() { return atual != null; }
    String atualTeste() { return atual; }
    void limpar() { fila.clear(); atual = null; espera = 0; }

    private double duracao() { return ABRIR + atual.length() / LETRAS_POR_S + LEITURA + FECHAR; }

    void atualizar(double dt) {
        if (dt <= 0) return;
        if (atual == null) {
            espera = Math.max(0, espera - dt);
            if (espera == 0 && !fila.isEmpty()) { atual = fila.poll(); tempo = 0; SomJogo.tocar(SomJogo.Efeito.INTERDICAO); }
            return;
        }
        tempo += dt;
        if (tempo >= duracao()) { atual = null; espera = INTERVALO; }
    }

    void desenhar(Graphics2D tela) {
        if (atual == null) return;
        Graphics2D g = (Graphics2D) tela.create();
        double fim = duracao(), abre = Math.min(1, tempo / ABRIR), fecha = Math.min(1, (fim - tempo) / FECHAR), a = Math.min(abre, fecha);
        int h = (int) (H * Math.max(.08, a)), y = Y + (H - h) / 2;
        g.setComposite(AlphaComposite.SrcOver.derive(.9f));
        g.setColor(new Color(10, 6, 8)); g.fillRect(X, y, W, h);
        g.setColor(new Color(150, 40, 40)); g.drawRect(X, y, W, h); g.drawRect(X + 2, y + 2, W - 4, h - 4);
        if (a < 1) { estatica(g, X + 3, y + 3, W - 6, h - 6, 1); g.dispose(); return; }
        // retrato do capitao: boca mexe enquanto o texto ainda esta saindo
        int letras = (int) Math.min(atual.length(), Math.max(0, tempo - ABRIR) * LETRAS_POR_S);
        boolean fala = letras < atual.length() && atual.charAt(Math.max(0, letras - 1)) != ' ';
        int px = X + 8, py = Y + 8, lado = H - 16;
        g.setColor(new Color(30, 18, 20)); g.fillRect(px, py, lado, lado);
        if (RETRATO != null) {
            int fw = RETRATO.getWidth() / 4, q = fala ? 1 + (int) (tempo * 12) % 2 : 0;
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
            g.drawImage(RETRATO, px, py, px + lado, py + lado, q * fw, 0, q * fw + fw, RETRATO.getHeight(), null);
        } else capitao(g, px, py, lado, fala && (int) (tempo * 12) % 2 == 0);
        estatica(g, px, py, lado, lado, .18);
        // linhas de varredura na tela do retrato
        g.setColor(new Color(0, 0, 0, 70)); for (int k = py; k < py + lado; k += 3) g.drawLine(px, k, px + lado, k);
        g.setColor(new Color(150, 40, 40)); g.drawRect(px, py, lado, lado);
        g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 11)); g.setColor(new Color(255, 90, 80));
        g.drawString("RADIO PIRATA", px + lado + 10, Y + 18);
        if ((int) (tempo * 3) % 2 == 0) { g.fillOval(X + W - 16, Y + 10, 6, 6); }
        g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 13)); g.setColor(new Color(240, 225, 200));
        quebrar(g, atual.substring(0, letras), px + lado + 10, Y + 36, W - lado - 30);
        g.dispose();
    }

    private void quebrar(Graphics2D g, String s, int x, int y, int largura) {
        String[] palavras = s.split(" "); StringBuilder linha = new StringBuilder(); int ly = y;
        for (String p : palavras) {
            String teste = linha.length() == 0 ? p : linha + " " + p;
            if (g.getFontMetrics().stringWidth(teste) > largura && linha.length() > 0) { g.drawString(linha.toString(), x, ly); ly += 15; linha = new StringBuilder(p); }
            else linha = new StringBuilder(teste);
        }
        if (linha.length() > 0) g.drawString(linha.toString(), x, ly);
    }

    private void estatica(Graphics2D g, int x, int y, int w, int h, double forca) {
        for (int k = 0; k < (int) (w * h / 18 * forca); k++) {
            int v = 90 + acaso.nextInt(160); g.setColor(new Color(v, v, v, (int) (200 * forca) + 30));
            g.fillRect(x + acaso.nextInt(Math.max(1, w)), y + acaso.nextInt(Math.max(1, h)), 2, 1);
        }
    }

    /** Retrato provisorio em pixel art: caveira de capitao com tapa-olho e chapeu de pirata. */
    private static void capitao(Graphics2D g, int x, int y, int s, boolean bocaAberta) {
        int p = Math.max(2, s / 14);
        g.setColor(new Color(20, 20, 24)); g.fillRect(x + p * 2, y + p, p * 10, p * 3);           // chapeu
        g.setColor(new Color(220, 200, 120)); g.fillRect(x + p * 6, y + p * 2, p * 2, p);          // caveirinha do chapeu
        g.setColor(new Color(215, 205, 180)); g.fillRect(x + p * 3, y + p * 4, p * 8, p * 7);      // cranio
        g.setColor(Color.BLACK); g.fillRect(x + p * 4, y + p * 6, p * 2, p * 2);                   // olho
        g.fillRect(x + p * 8, y + p * 5, p * 2, p * 3); g.drawLine(x + p * 3, y + p * 5, x + p * 11, y + p * 4); // tapa-olho
        g.setColor(new Color(170, 30, 30)); g.fillRect(x + p * 5, y + p * 9, p * 4, bocaAberta ? p * 2 : p / 2 + 1);
        g.setColor(new Color(215, 205, 180)); for (int k = 0; k < 4; k++) g.fillRect(x + p * 5 + k * p, y + p * 9, p / 2 + 1, p / 2 + 1);
    }

    private static BufferedImage ler(String n) {
        java.net.URL u = RadioPirata.class.getResource("/ImagemIcon/" + n);
        if (u == null) return null;
        try { return javax.imageio.ImageIO.read(u); } catch (java.io.IOException e) { return null; }
    }
}
