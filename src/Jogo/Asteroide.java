package Jogo;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;

/** Meteoro com sprite 16-bit: obstaculo destrutivel que conserva a colisao original. */
public final class Asteroide {
    private static final Image[] SPRITES = {
        Assets.imagem("meteoro_16bits_0.png"),
        Assets.imagem("meteoro_16bits_1.png"),
        Assets.imagem("meteoro_16bits_2.png"),
        Assets.imagem("meteoro_16bits_3.png")
    };
    private double x, y, tempo;
    private final int raio, variante;
    private int vida;
    private boolean visivel = true;

    public Asteroide(int x, int y, int raio, int variante) {
        this.x = x; this.y = y; this.raio = raio; this.variante = variante;
        this.vida = raio >= 30 ? 3 : 2;
    }
    public void mexer(double dt) {
        tempo += dt;
        x -= (39 + raio * .25) * dt;
        y += Math.sin(tempo * 1.3 + variante) * 4 * dt;
        if (x < -raio * 2) visivel = false;
    }
    public void desenhar(Graphics2D g) {
        if (!visivel) return;
        Graphics2D a = (Graphics2D) g.create();
        a.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        a.translate(Math.round(x), Math.round(y));
        a.rotate(tempo * (variante % 2 == 0 ? .23 : -.20));
        int tamanho = raio * 2;
        if(temSuprimento())Arte095.desenhar(a,11,-raio,-raio,tamanho,tamanho);else a.drawImage(SPRITES[Math.floorMod(variante, SPRITES.length)],
                -raio, -raio, tamanho, tamanho, null);
        a.dispose();
    }
    public Rectangle getBounds() {
        return new Rectangle((int)x-raio+6, (int)y-raio+6,
                2*(raio-6), 2*(raio-6));
    }
    public boolean receberTiro() {
        if (--vida <= 0) visivel = false;
        return !visivel;
    }
    public boolean isVisible() { return visivel; }
    public void setVisible(boolean b) { visivel = b; }
    public int getX() { return (int)x; }
    public int getY() { return (int)y; }
    public boolean temSuprimento() { return variante==7; }
    public int getRaio() { return raio; }
}
