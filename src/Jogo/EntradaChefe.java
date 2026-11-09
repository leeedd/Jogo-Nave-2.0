package Jogo;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

/** Vinheta de entrada: arte 16 bits original do projeto, sem habilitar combate. */
public final class EntradaChefe {
    public static final double DURACAO = 3.8;
    private static final String[] NOMES = {
        "", "MALACHAR", "VALDRAK", "ALMIRANTE KORVUS", "GRIMBO",
        "HEXAR", "MORGATH", "KANOA", "LULA DO ABISMO"
    };
    private static final String[] SUBTITULOS = {
        "", "O REI DAS SOMBRAS", "LEGIAO CROMADA - MOTO DE CROMO", "VORTICE NEGRO - O CORSARIO DO VACUO",
        "FERRUGEM ATOMICA - CANHAO DE GRAVES", "CINZAS DE ORION - O MARIONETISTA", "NECROPOLIS - CHUVA DE FOGO",
        "FURIA DA TORMENTA - VOZ DO TROVAO", "OS TENTACULOS DESPERTARAM"
    };
    private static final Color[] CORES = {
        Color.WHITE, new Color(204, 87, 236), new Color(255, 139, 53), new Color(101, 220, 246),
        new Color(255, 149, 64), new Color(77, 164, 255), new Color(255, 74, 73),
        new Color(105, 246, 155), new Color(73, 225, 209)
    };

    private final int id;
    private double tempo;
    public EntradaChefe(int id) {
        if (id < 1 || id > 8) throw new IllegalArgumentException("chefe invalido: " + id);
        this.id = id;
    }
    public void atualizar(double dt) { tempo = Math.min(DURACAO, tempo + Math.max(0, dt)); }
    public boolean terminou() { return tempo >= DURACAO; }
    public int getId() { return id; }
    public double getTempo() { return tempo; }
    public String getNome() { return NOMES[id]; }
    public String getSubtitulo() { return SUBTITULOS[id]; }

    public void desenhar(Graphics2D tela) {
        Graphics2D g = (Graphics2D) tela.create();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        int W = Config.LARGURA, H = Config.ALTURA;
        double fade = Math.min(1, tempo / .48);
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) fade));
        g.setColor(new Color(2, 5, 15, 238));
        g.fillRect(0, Config.HUD, W, H - Config.HUD);
        Color cor = CORES[id];
        double pulso = .55 + .45 * Math.sin(tempo * 8);
        // Identidade propria de cada lenda, inclusive no movimento do fundo.
        for (int i = 0; i < 26; i++) {
            int xx = (i * 107 + 31) % W;
            int yy = Config.HUD + 40 + ((i * 57 + 17) % (H - Config.HUD - 70));
            int deslocamento = (int) (tempo * (id == 2 || id == 4 ? 95 : 45));
            g.setColor(new Color(cor.getRed(), cor.getGreen(), cor.getBlue(), 55 + i % 3 * 29));
            g.fillRect(Math.floorMod(xx - deslocamento, W), yy, i % 3 == 0 ? 8 : 3, 2);
        }
        // Identidades especiais: Lemmy pulsos de baixo; James raio; Tom chuva de fogo;
        // Chuck cristais; Bruce relampagos; Ozzy eclipse; Rob chamas; Lula tentaculos.
        g.setStroke(new BasicStroke(3));
        switch (id) {
            case 1:
                g.setColor(new Color(125, 62, 160, 94));
                g.fillOval(655, 95, 260, 260);
                g.setColor(new Color(22, 9, 31));
                g.fillOval(688, 116, 204, 204);
                break;
            case 2:
                for (int i = 0; i < 7; i++) {
                    int px = 650 + i * 41;
                    g.setColor(new Color(255, 78 + i * 13, 24, 155));
                    g.drawLine(px, 500, px - 67, 510 - (int)(30 * Math.sin(tempo * 12 + i)));
                }
                break;
            case 3: case 5:
                for (int i = 0; i < 3; i++) {
                    int xx = 610 + i * 125;
                    g.setColor(new Color(85, 188, 255, 132));
                    g.drawPolyline(new int[]{xx, xx + 22, xx - 10, xx + 19},
                            new int[]{95, 150, 207, 270}, 4);
                }
                break;
            case 4:
                for (int i = 0; i < 4; i++) {
                    int raio = (int)(54 + i * 48 + Math.sin(tempo * 7) * 12);
                    g.setColor(new Color(254, 151, 55, 55 + i * 26));
                    g.drawOval(775 - raio, 320 - raio, 2 * raio, 2 * raio);
                }
                break;
            case 6:
                for (int i = 0; i < 11; i++) {
                    int xx = 580 + i * 31;
                    int yy = 90 + Math.floorMod((int)(tempo * 155) + i * 39, 420);
                    g.setColor(new Color(255, 51, 57, 157));
                    g.fillOval(xx, yy, 8, 23);
                }
                break;
            case 7:
                for (int i = 0; i < 10; i++) {
                    int xx = 580 + i * 33;
                    int yy = 130 + (i * 91 % 310);
                    g.setColor(new Color(91, 248, 130, 110));
                    g.fillPolygon(new int[]{xx, xx + 11, xx, xx - 11},
                            new int[]{yy - 20, yy, yy + 20, yy}, 4);
                }
                break;
            case 8:
                for (int i = 0; i < 6; i++) {
                    int xx = 635 + i * 51;
                    g.setColor(new Color(71, 223, 203, 121));
                    g.drawArc(xx - 70, 210 + i * 11, 142, 197, (int)(tempo * 40 + i * 18), 168);
                }
                break;
            default: break;
        }
        // Personagem principal sempre entra deslizando da direita para a esquerda.
        BufferedImage imagem = Sprites16Bits.carregarAnimado(id, 1, tempo);
        if (imagem != null) {
            int largura = 388, altura = 360;
            double aprox = Math.min(1, Math.max(0, (tempo - .25) / 1.20));
            double suavidade = 1 - Math.pow(1 - aprox, 3);
            int xx = (int)(W + 30 - suavidade * 485);
            int yy = 130 + (int)(Math.sin(tempo * 3.2) * 10);
            // Mantem proporcao do sprite ao desenhar.
            double escala = Math.min(largura / (double) imagem.getWidth(), altura / (double)imagem.getHeight());
            int sw = (int)(imagem.getWidth() * escala), sh = (int)(imagem.getHeight() * escala);
            Sprites16Bits.desenharVirado(g, imagem, id, 1, xx + (largura - sw) / 2, yy + (altura - sh) / 2, sw, sh);
        }
        // A segunda forma aparece em retrato: o jogador ja conhece o musico por tras da maquina.
        BufferedImage retrato = Sprites16Bits.carregarAnimado(id, 2, tempo);
        if (retrato != null && id != 8 && tempo > .8) {
            float alpha = (float)Math.min(.95, (tempo - .8) / .65);
            Graphics2D retratoG = (Graphics2D)g.create();
            retratoG.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
            retratoG.setColor(new Color(9, 16, 34));
            retratoG.fillRoundRect(37, 218, 202, 209, 15, 15);
            retratoG.setColor(cor);
            retratoG.drawRoundRect(37, 218, 202, 209, 15, 15);
            double e = Math.min(190d / retrato.getWidth(), 176d / retrato.getHeight());
            int rw = (int)(retrato.getWidth() * e), rh = (int)(retrato.getHeight() * e);
            Sprites16Bits.desenharVirado(retratoG, retrato, id, 2, 43 + (190 - rw) / 2, 224 + (181 - rh) / 2, rw, rh);
            retratoG.dispose();
        }
        g.setColor(new Color(0, 0, 0, 214));
        g.fillRect(0, Config.HUD + 15, W, 112);
        g.setColor(cor);
        g.fillRect(0, Config.HUD + 15, W, 4);
        g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 26));
        String titulo = id == 6 ? "NECROPOLIS  /  MORGATH" : NOMES[id];
        g.drawString(titulo, 26, Config.HUD + 60);
        g.setColor(Color.WHITE);
        g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 15));
        g.drawString(id == 6 ? "PILOTO IDENTIFICADO: MORGATH" : SUBTITULOS[id], 28, Config.HUD + 91);
        g.setColor(new Color(cor.getRed(), cor.getGreen(), cor.getBlue(), (int)(125 + 100 * pulso)));
        g.fillRect(0, H - 40, (int)(W * Math.min(1, tempo / DURACAO)), 6);
        g.setColor(Color.WHITE);
        g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 13));
        g.drawString("CHEFE ENTRANDO   //   COMBATE EM " + Math.max(1, (int)Math.ceil(DURACAO - tempo)), 33, H - 55);
        g.dispose();
    }
}
