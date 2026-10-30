package Jogo;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;

/** Epilogo 16 bits: show das lendas, creditos, relampago e mensagem final. */
public final class CenaFinal {
    private static final double INICIO_CREDITOS = 14.0;
    private static final double FINAL_CREDITOS = 58.0;
    private static final double INICIO_RELAMPAGO = 61.5;
    private static final double TEXTO_FINAL = 65.0;
    private static final Image PALCO = Assets.imagem("show_final_098.png");
    private static final Image SILHUETA = Assets.imagem("megazord_relampago_16bits.png");
    private static final Image REVELACAO = Assets.imagem("megazord_cabeca_099.png");
    private static final Image FOTO_EQUIPE = Assets.imagem("flavio_wenger.jpg");
    private static final String[] CREDITOS = {
        "METAL SPACE - LEGENDS OF METAL", "", "UMA HOMENAGEM AO HEAVY METAL",
        "", "OZZY OSBOURNE", "ROB HALFORD", "BRUCE DICKINSON",
        "LEMMY KILMISTER", "JAMES HETFIELD", "TOM ARAYA", "CHUCK BILLY",
        "", "A JORNADA COMECOU COM A LULA DO ABISMO",
        "", "DESENVOLVIDO POR FLAVIO PRADO", "JOGO ORIGINAL - 2019",
        "", "RONNIE JAMES DIO", "TENACIOUS D / DEEZLEBOSS",
        "", "DE FAS DE METAL, PARA FAS DE METAL",
        "", "OBRIGADO POR JOGAR!", "LONG LIVE ROCK N ROLL"
    };
    private double tempo;

    public CenaFinal(){SomJogo.tocar(SomJogo.Efeito.VITORIA);}
    public void atualizar(double dt) {double antes=tempo; if (!Double.isNaN(dt) && !Double.isInfinite(dt)) tempo += Math.max(0, dt);if(antes<INICIO_RELAMPAGO&&tempo>=INICIO_RELAMPAGO)SomJogo.tocar(SomJogo.Efeito.TROVAO); }
    public double getTempo() { return tempo; }
    public boolean mostrandoMegazord() { return tempo >= INICIO_RELAMPAGO; }

    public void desenhar(Graphics2D base) {
        Graphics2D g = (Graphics2D) base.create();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        int w = Config.LARGURA, h = Config.ALTURA;
        g.setColor(new Color(1, 1, 12));
        g.fillRect(0, 0, w, h);
        if (tempo < FINAL_CREDITOS) {
            int quadro = ((int) (tempo * 6)) % 4;
            int sw = PALCO.getWidth(null) / 2, sh = PALCO.getHeight(null) / 2;
            int sx = (quadro % 2) * sw, sy = (quadro / 2) * sh;
            // Preserve aspect ratio: concert artwork is square, never stretch musicians.
            int lado = Math.min(w, h), margem = (w - lado) / 2;
            g.drawImage(PALCO, margem, 0, margem + lado, lado,
                    sx, sy, sx + sw, sy + sh, null);
            for (int i = 0; i < 6; i++) {
                int x = 45 + i * 170;
                int ponta = x + (int) (Math.sin(tempo * 1.4 + i) * 110);
                g.setColor(new Color(i % 2 == 0 ? 35 : 180, 70, 230, 22));
                g.fillPolygon(new int[]{x, ponta - 55, ponta + 55}, new int[]{0, h, h}, 3);
            }
            if (tempo < INICIO_CREDITOS) {
                g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 20));
                g.setColor(new Color(255, 218, 134));
                String titulo = "DE FAS DE METAL, PARA FAS DE METAL";
                g.drawString(titulo, (w - g.getFontMetrics().stringWidth(titulo)) / 2, 35);
                g.dispose();
                return;
            }
            g.setColor(new Color(0, 0, 15, 170));
            g.fillRect(0, 0, w, h);
            g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 25));
            g.setColor(new Color(253, 214, 120));
            g.drawString("AS LENDAS SOBEM AO PALCO!", 245, 58);
            g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 21));
            // Os nomes sobem durante a musica, como nos creditos de um arcade.
            int yInicial = (int) (h + 100 - (tempo - INICIO_CREDITOS) * 47);
            if(tempo>=28.0&&tempo<42.0){
                // Foto original dos criadores, em visual de arcade pixelado.
                g.setColor(new Color(5,12,26,220));g.fillRect(700,165,232,327);
                g.drawImage(FOTO_EQUIPE,713,180,215,285,null);
                g.setFont(new Font(Font.MONOSPACED,Font.BOLD,13));
                g.setColor(new Color(255,213,98));g.drawString("FLAVIO & WENGER",733,485);
                g.setFont(new Font(Font.MONOSPACED,Font.BOLD,21));
            }
            for (int i = 0; i < CREDITOS.length; i++) {
                int y = yInicial + i * 48;
                if (y < 95 || y > h + 20) continue;
                String linha = CREDITOS[i];
                int x = (w - g.getFontMetrics().stringWidth(linha)) / 2;
                g.setColor(i == 0 || i == 12 ? new Color(255, 210, 94) : Color.WHITE);
                g.drawString(linha, x, y);
            }
        } else if (tempo < INICIO_RELAMPAGO) {
            // Corte direto para preto, sem silhueta da cabeca antes do clarão.
            g.setColor(Color.BLACK);g.fillRect(0,0,w,h);
        } else {
            // A cabeca do Megazord aparece SOMENTE durante dois clarões curtos.
            boolean clarao = (tempo>=62.4&&tempo<62.55)||(tempo>=62.83&&tempo<63.04);
            g.setColor(Color.BLACK);g.fillRect(0,0,w,h);
            if(clarao){
                g.drawImage(REVELACAO,240,75,480,385,null);
                g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,.13f));
                g.setColor(Color.WHITE);g.fillRect(0,0,w,h);
                g.setComposite(AlphaComposite.SrcOver);
            }
            if(tempo>=TEXTO_FINAL){
                g.setColor(new Color(238,72,83));
                g.setFont(new Font(Font.MONOSPACED,Font.BOLD,75));
                String titulo="THE END?";
                g.drawString(titulo,(w-g.getFontMetrics().stringWidth(titulo))/2,320);
                g.setColor(new Color(225,221,231));
                g.setFont(new Font(Font.MONOSPACED,Font.BOLD,17));
                g.drawString("ENTER - VER O PLACAR",352,520);
            }
        }
        g.dispose();
    }
}
