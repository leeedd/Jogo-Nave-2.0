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
        return this.idade >= 2.0 && tipo!=Tipo.ENERGIA && tipo!=Tipo.SUBTANQUE && tipo!=Tipo.REPARO && tipo!=Tipo.REATOR_CONVERGENCIA;
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
        if(Recompensas.identificador(tipo)==0)this.x -= (double)(this.recuperado ? 35 : 48) * d;
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
        MEDALHA_TESTAMENT;

    }
}

