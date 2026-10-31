/*
 * Decompiled with CFR 0.152.
 */
package Jogo;




import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.util.Locale;

public class Item {
    private final Tipo tipo;
    private final int origem;
    int origem(){return origem;}
    private double x;
    private double y;
    private boolean visivel = true;
    private double idade;
    private final boolean recuperado;

    public boolean podeSerCapturado() {
        return this.idade >= 2.0 && tipo!=Tipo.ENERGIA && tipo!=Tipo.NUCLEO_CASCO && tipo!=Tipo.SUBTANQUE && tipo!=Tipo.REPARO && tipo!=Tipo.REATOR_CONVERGENCIA && tipo!=Tipo.RELIQUIA_NAVE;
    }

    public Item(Tipo tipo, int n, int n2, boolean bl) { this(tipo,n,n2,bl,-1); }
    public Item(Tipo tipo, int n, int n2, boolean bl,int origem) {
        this.origem=origem;
        this.tipo = tipo;
        this.x = n;
        this.y = n2;
        this.recuperado = bl;
    }

    public void mexer(double d) {
        this.idade += d;
        if(Recompensas.identificador(tipo)==0&&tipo!=Tipo.RELIQUIA_NAVE)this.x -= (double)(this.recuperado ? 35 : 48) * d;
        if (this.x < -30.0) {
            this.visivel = false;
        }
    }

    public void desenhar(Graphics2D graphics2D, double d) {
        String string;
        int n = (int)this.x;
        int n2 = (int)(this.y + Math.sin(d * 4.0 + this.x * 0.02) * 4.0);
        Color color = this.tipo == Tipo.REPARO ? new Color(98, 255, 153) : (this.tipo == Tipo.MELHORIA ? new Color(255, 214, 91) : (this.tipo == Tipo.ARMA_2 ? new Color(255, 113, 222) : (this.tipo == Tipo.ARMA_GELO ? new Color(90, 222, 255) : (this.tipo == Tipo.ARMA_FOGO ? new Color(255, 104, 53) : (this.tipo == Tipo.PEROLA_ABISSAL ? new Color(47, 234, 229) : (this.tipo == Tipo.OCULOS_OZZY ? new Color(192, 129, 253) : (this.tipo == Tipo.EMBLEMA_HALFORD ? new Color(255, 171, 75) : (this.tipo == Tipo.INSIGNIA_EDDIE ? new Color(109, 235, 214) : (this.tipo == Tipo.CHAPEU_LEMMY ? new Color(255, 166, 77) : (this.tipo == Tipo.PALHETA_JAMES ? new Color(80, 166, 255) : (this.tipo == Tipo.MICROFONE_TOM ? new Color(249, 69, 82) : (this.tipo == Tipo.MEDALHA_TESTAMENT ? new Color(113, 246, 120) : new Color(84, 215, 255)))))))))))));
        Graphics2D graphics2D2 = (Graphics2D)graphics2D.create();
        if(tipo==Tipo.NUCLEO_CASCO){nucleo(graphics2D2,n+9,n2+10,d);graphics2D2.dispose();return;}
        if(tipo==Tipo.RELIQUIA_NAVE){reliquia(graphics2D2,n+12,n2+12,d,origem);graphics2D2.dispose();return;}
        if(tipo==Tipo.ENERGIA||tipo==Tipo.SUBTANQUE||tipo==Tipo.REPARO||tipo==Tipo.REATOR_CONVERGENCIA){
            Arte098.item(graphics2D2,tipo==Tipo.SUBTANQUE?0:tipo==Tipo.REPARO?4:8,n-9,n2-9,44);
            graphics2D2.dispose();return;
        }
        int n3 = Recompensas.identificador(this.tipo);
        if (n3 != 0) {
            Medalhas.desenhar(graphics2D2, n3, true, n - 6, n2 - 6, 37, d);
            graphics2D2.dispose();
            return;
        }
        String string2 = "item_icone_" + this.tipo.name().toLowerCase(Locale.ROOT) + ".png";
        graphics2D2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        graphics2D2.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 80));
        graphics2D2.fillRect(n - 2, n2 - 2, 28, 28);
        if(tipo==Tipo.REPARO)Arte095.desenhar(graphics2D2,4+(int)(d*5)%4,n-7,n2-7,38,38);else Arte094.desenhar(graphics2D2,Arte094.item(tipo),n-7,n2-7,38,38);
        string = this.tipo == Tipo.ARMA_2 ? "DISPERSAO" : (this.tipo == Tipo.ARMA_GELO ? "GELO [Q]" : (this.tipo == Tipo.ARMA_FOGO ? "FOGO [E]" : ""));
        if (!string.isEmpty()) {
            graphics2D2.setFont(new Font("Monospaced", 1, 10));
            int n4 = graphics2D2.getFontMetrics().stringWidth(string);
            int n5 = Math.max(4, Math.min(960 - n4 - 4, n + 12 - n4 / 2));
            graphics2D2.setColor(new Color(3, 9, 22, 225));
            graphics2D2.fillRect(n5 - 3, n2 + 29, n4 + 6, 14);
            graphics2D2.setColor(color);
            graphics2D2.drawString(string, n5, n2 + 39);
            graphics2D2.setColor(Color.WHITE);

        }
        if (this.recuperado) {
            graphics2D2.setColor(Color.WHITE);
            graphics2D2.fillOval(n + 21, n2 - 5, 5, 5);
        }
        graphics2D2.dispose();
    }

    public Tipo getTipo() {
        return this.tipo;
    }

    public boolean recuperado() {
        return this.recuperado;
    }

    public boolean isVisible() {
        return this.visivel;
    }

    public void setVisible(boolean bl) {
        this.visivel = bl;
    }

    private static final java.awt.image.BufferedImage ARTE_NUCLEO = arteNucleo();
    private static java.awt.image.BufferedImage arteNucleo(){java.net.URL u=Item.class.getResource("/ImagemIcon/nucleo_casco.png");if(u==null)return null;try{return javax.imageio.ImageIO.read(u);}catch(java.io.IOException e){return null;}}
    /** Nucleo de Casco: placa blindada com coracao de energia vermelho pulsando (arte em ImagemIcon/nucleo_casco.png tem prioridade). */
    static void nucleo(Graphics2D g, int cx, int cy, double t) {
        int pulso = (int) Math.round(Math.sin(t * 6) * 2);
        g.setColor(new Color(255, 60, 60, 60)); g.fillOval(cx - 26 - pulso, cy - 26 - pulso, 52 + 2 * pulso, 52 + 2 * pulso);
        if (ARTE_NUCLEO != null) { g.drawImage(ARTE_NUCLEO, cx - 22, cy - 22, 44, 44, null); return; }
        int[] xs = {cx - 17, cx + 17, cx + 17, cx, cx - 17}, ys = {cy - 16, cy - 16, cy + 4, cy + 19, cy + 4};
        g.setColor(new Color(40, 44, 56)); g.fillPolygon(xs, ys, 5);
        g.setColor(new Color(150, 160, 180)); g.drawPolygon(xs, ys, 5);
        g.setColor(new Color(90, 98, 115)); g.drawLine(cx - 14, cy - 13, cx + 14, cy - 13);
        int r = 7 + (pulso > 0 ? 1 : 0);
        g.setColor(new Color(200, 20, 30)); g.fillOval(cx - r - 3, cy - 6 - r / 2, r + 3, r + 3); g.fillOval(cx, cy - 6 - r / 2, r + 3, r + 3);
        g.fillPolygon(new int[]{cx - r - 3, cx + r + 3, cx}, new int[]{cy - 2, cy - 2, cy + 10}, 3);
        g.setColor(new Color(255, 150, 150)); g.fillRect(cx - 6, cy - 7, 3, 3);
        g.setColor(new Color(220, 225, 235)); for (int[] p : new int[][]{{cx - 13, cy - 10}, {cx + 11, cy - 10}, {cx - 13, cy + 2}, {cx + 11, cy + 2}}) g.fillRect(p[0], p[1], 2, 2);
    }

    /** Reliquia em pixel art provisorio: cristal na cor da nave, com aura girando. */
    static void reliquia(Graphics2D g,int cx,int cy,double t,int nave){
        Color c=Contrato.corReliquia(nave);
        for(int k=0;k<8;k++){double a=t*2.4+k*Math.PI/4;int r=20+(int)(Math.sin(t*6+k)*3);g.setColor(new Color(c.getRed(),c.getGreen(),c.getBlue(),120));g.fillRect(cx+(int)(Math.cos(a)*r)-2,cy+(int)(Math.sin(a)*r)-2,4,4);}
        int p=3,h=(int)(Math.sin(t*5)*2);
        g.setColor(c.darker());g.fillPolygon(new int[]{cx,cx+p*4,cx,cx-p*4},new int[]{cy-p*6+h,cy+h,cy+p*6+h,cy+h},4);
        g.setColor(c);g.fillPolygon(new int[]{cx,cx+p*3,cx,cx-p*3},new int[]{cy-p*5+h,cy+h,cy+p*5+h,cy+h},4);
        g.setColor(Color.WHITE);g.fillRect(cx-p,cy-p*3+h,p,p*2);
    }

    public Rectangle getBounds() {
        return new Rectangle((int)this.x - 6, (int)this.y - 8, 36, 40);
    }

    public static enum Tipo {
        REPARO,
        ENERGIA,
        SUBTANQUE,
        REATOR_CONVERGENCIA,
        MELHORIA,
        ESCUDO,
        ARMA_2,
        ARMA_GELO,
        ARMA_FOGO,
        PEROLA_ABISSAL,
        OCULOS_OZZY,
        EMBLEMA_HALFORD,
        INSIGNIA_EDDIE,
        CHAPEU_LEMMY,
        PALHETA_JAMES,
        MICROFONE_TOM,
        MEDALHA_TESTAMENT,
        NUCLEO_CASCO,
        /** Reliquia de caca: solta pelo alvo procurado; origem = nave que ela libera. */
        RELIQUIA_NAVE;

    }
}

