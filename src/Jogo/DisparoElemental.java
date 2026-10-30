package Jogo;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

/** Projeteis coletaveis com efeitos reais, visuais e colisao propria. */
public final class DisparoElemental {
    public enum Tipo { FOGO, GELO }
    private final Tipo tipo;
    private final int nivel;
    public int dano(){return tipo==Tipo.GELO?2+nivel:3+nivel*2;}
    public int nivel(){return nivel;}
    private double x, y, tempo;
    private boolean ativo = true;
    public DisparoElemental(Tipo tipo, int x, int y) {this(tipo,x,y,1);}
    public DisparoElemental(Tipo tipo,int x,int y,int nivel) {
        this.nivel=Math.max(1,Math.min(3,nivel));
        this.tipo = tipo;
        this.x = x;
        this.y = y;
    }
    public void mexer(double dt) {
        tempo += dt;
        x += (tipo == Tipo.GELO ? 390 : 520) * dt;
        y += Math.sin(tempo * 10) * (tipo == Tipo.GELO ? 17 : 11) * dt;
        if (x >= Config.LARGURA + 25) ativo = false;
    }
    public Rectangle getBounds() {
        return new Rectangle((int)x, (int)y, tipo == Tipo.GELO ? 27 : 37, 16);
    }
    public boolean ativo() { return ativo; }
    public void apagar() { ativo = false; }
    public Tipo getTipo() { return tipo; }
    public void desenhar(Graphics2D g) {
        if (!ativo) return;
        Color cor = tipo == Tipo.GELO ? new Color(91, 218, 255) : new Color(255, 109, 34);
        g.setColor(new Color(cor.getRed(), cor.getGreen(), cor.getBlue(), 85));
        g.fillOval((int)x - 8, (int)y - 8, 46, 34);
        g.setColor(cor);
        g.fillRoundRect((int)x, (int)y, tipo == Tipo.GELO ? 23 : 33, 13, 8, 8);
        g.setColor(Color.WHITE);
        g.fillOval((int)x + 12, (int)y + 4, 6, 5);
    }
}
