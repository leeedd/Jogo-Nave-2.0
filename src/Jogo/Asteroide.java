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
        this.vida = gigante() ? 9 : (raio >= 30 ? 3 : 2) + (variante == MINERIO ? 2 : 0);
    }
    public void mexer(double dt) {
        tempo += dt;
        x -= (gigante() ? 26 : 39 + raio * .25) * dt;
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
        if (temMinerio()) {
            // veios de cristal brilhando na rocha
            java.util.Random r = new java.util.Random(variante * 31 + raio);
            for (int k = 0; k < 6; k++) {
                int cx = (int) ((r.nextDouble() - .5) * raio * 1.1), cy = (int) ((r.nextDouble() - .5) * raio * 1.1), s = 3 + r.nextInt(4);
                int brilho = (int) (160 + 90 * Math.sin(tempo * 5 + k));
                a.setColor(new java.awt.Color(80, Math.min(255, brilho), 255, 230)); a.fillPolygon(new int[]{cx, cx + s, cx, cx - s}, new int[]{cy - s * 2, cy, cy + s * 2, cy}, 4);
            }
        }
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
    /** Meteoro com veios de minerio raro (so nos campos dos planetas com anel). */
    static final int MINERIO = 8;
    public boolean temMinerio() { return variante == MINERIO; }
    /** Meteoro gigante dos campos: lento, duro, bate forte e se parte em tres ao quebrar. */
    public boolean gigante() { return raio >= 55; }
    /** Dano de colisao na nave. */
    public int danoColisao() { return gigante() ? 3 : 1; }
    public int getRaio() { return raio; }
}
