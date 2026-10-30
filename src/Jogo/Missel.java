package Jogo;

import java.awt.Image;
import java.awt.Rectangle;

/** O tipo e gravado individualmente ao atirar. */
public class Missel {
    private static int tipoSelecionado = 0;
    private final int tipo;
    private final Image imagem;
    private final int largura;
    private final int altura;
    private double x;
    private double y;
    private boolean visivel = true;
    private int alvosAtingidos = 0;
    private final double anguloGraus;

    public Missel(int x, int y) {
        this(x, y, tipoSelecionado);
    }
    public Missel(int x, int y, int tipo) {
        this(x, y, tipo, 0);
    }
    /** Angulo: negativo sobe, positivo desce. Aplicado somente no tipo 1. */
    public Missel(int x, int y, int tipo, double anguloGraus) {
        this.anguloGraus = tipo == 1 ? anguloGraus : 0;
        this.x = x;
        this.y = y;
        this.tipo = Math.max(0, Math.min(2, tipo));
        this.imagem=this.tipo==0?Arte094.imagem(10,32,12):Assets.imagem(this.tipo==1?"missel2.png":"missel.jpg");
        this.largura = imagem.getWidth(null);
        this.altura = imagem.getHeight(null);
    }
    public static int getTipoMissel() { return tipoSelecionado; }
    public static void setTipoTiro(int t) { tipoSelecionado = Math.floorMod(t, 3); }
    public int getTipo() { return tipo; }
    public double getAnguloGraus() { return anguloGraus; }
    /** Cada missel espalhado atinge um alvo; os outros dois continuam voando. */
    public void registrarAcerto() {
        alvosAtingidos++;
        visivel = false;
    }
    public int getAlvosAtingidos() { return alvosAtingidos; }
    public void mexer(double dt) {
        double velocidade = tipo == 2 ? 320 : tipo == 1 ? 690 : 560;
        double radianos = Math.toRadians(anguloGraus);
        x += Math.cos(radianos) * velocidade * dt;
        y += Math.sin(radianos) * velocidade * dt;
        if (x > Config.LARGURA + 60 || y < Config.HUD - 60 || y > Config.ALTURA + 60)
            visivel = false;
    }
    public boolean isVisible() { return visivel; }
    public void setVisible(boolean b) { visivel = b; }
    public Image getImagem() { return imagem; }
    public int getX() { return (int) x; }
    public int getY() { return (int) y; }
    public Rectangle getBounds() {
        // Caixa de colisao acompanha o giro do desenho.
        double r = Math.toRadians(anguloGraus);
        double c = Math.cos(r), s = Math.sin(r);
        double[] xs = {0, largura * c, -altura * s, largura * c - altura * s};
        double[] ys = {0, largura * s, altura * c, largura * s + altura * c};
        double minX = 0, maxX = 0, minY = 0, maxY = 0;
        for (int i = 0; i < xs.length; i++) {
            minX = Math.min(minX, xs[i]); maxX = Math.max(maxX, xs[i]);
            minY = Math.min(minY, ys[i]); maxY = Math.max(maxY, ys[i]);
        }
        return new Rectangle((int) Math.floor(x + minX), (int) Math.floor(y + minY),
                Math.max(1, (int) Math.ceil(maxX - minX)),
                Math.max(1, (int) Math.ceil(maxY - minY)));
    }
}
