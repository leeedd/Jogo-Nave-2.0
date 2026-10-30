/*
 * Decompiled with CFR 0.152.
 */
package Jogo;



import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;

public class ProjetilInimigo {
    private double x;
    private double y;
    private double vx;
    private double vy;
    private final int tipo;
    private final Alien dono;
    private boolean visivel = true;
    private final int estilo;
    private double tempo;
    private double tempoLaser = 0.72;
    private final double alvoLaser;
    private double alvoMovelX;
    private double alvoMovelY;
    private double atrasoGuia = 0.25;

    public ProjetilInimigo(Alien alien, int n, int n2) {
        this.dono = alien;
        this.tipo = alien.getArmaRoubada();
        this.estilo = alien.estiloDisparo();
        Rectangle rectangle = alien.getBounds();
        this.x = rectangle.x - 4;
        this.y = (double)rectangle.y + (double)rectangle.height / 2.0;
        this.alvoLaser = n2 + 18;
        this.alvoMovelX = n;
        this.alvoMovelY = n2;
        double d = (double)n - this.x;
        double d2 = (double)n2 - this.y;
        double d3 = Math.max(1.0, Math.hypot(d, d2));
        double d4 = this.tipo == 2 ? 230.0 : (this.tipo == 1 ? 315.0 : 255.0);
        this.vx = d / d3 * d4;
        this.vy = d2 / d3 * d4;
    }

    public void atualizarAlvo(double d, double d2) {
        this.alvoMovelX = d;
        this.alvoMovelY = d2;
    }

    public void mexer(double d) {
        this.tempo += d;
        if (this.estilo == 4) {
            this.tempoLaser -= d;
            if (this.tempoLaser < -0.4) {
                this.visivel = false;
            }
            return;
        }
        if (this.estilo == 5) {
            this.atrasoGuia -= d;
            if (this.atrasoGuia <= 0.0) {
                double d2 = Math.atan2(this.alvoMovelY - this.y, this.alvoMovelX - this.x);
                double d3 = Math.atan2(this.vy, this.vx);
                double d4 = Math.atan2(Math.sin(d2 - d3), Math.cos(d2 - d3));
                double d5 = d3 + Math.max(-1.45 * d, Math.min(1.45 * d, d4));
                double d6 = 165.0 + Math.min(85.0, this.tempo * 16.0);
                this.vx = Math.cos(d5) * d6;
                this.vy = Math.sin(d5) * d6;
            }
        } else if (this.estilo == 1) {
            this.vy += Math.sin(this.tempo * 10.0) * 145.0 * d;
            this.vy = Math.max(-225.0, Math.min(225.0, this.vy));
        } else if (this.estilo == 2) {
            this.vx *= Math.max(0.96, 1.0 - d * 0.35);
        } else if (this.estilo == 3) {
            this.vx += (double)((this.vx < 0.0 ? -1 : 1) * 78) * d;
        }
        this.x += this.vx * d;
        this.y += this.vy * d;
        if (this.x < -50.0 || this.x > 1010.0 || this.y < 22.0 || this.y > 650.0) {
            this.visivel = false;
        }
    }

    public void desenhar(Graphics2D graphics2D) {
        if(estilo==6||estilo==7){graphics2D.setColor(estilo==6?new Color(90,220,255):new Color(255,85,25));graphics2D.fillPolygon(new int[]{(int)x-14,(int)x,(int)x+14,(int)x},new int[]{(int)y,(int)y-5,(int)y,(int)y+5},4);graphics2D.setColor(Color.WHITE);graphics2D.fillRect((int)x-9,(int)y-1,16,2);return;}
        if (this.estilo == 4) {
            boolean bl = this.tempoLaser <= 0.0;
            int n = (int)this.alvoLaser;
            graphics2D.setColor(new Color(255, 83, 86, bl ? 195 : 65));
            graphics2D.fillRect(0, n - (bl ? 8 : 2), (int)this.x, bl ? 16 : 4);
            graphics2D.setColor(new Color(255, 229, 179, bl ? 240 : 75));
            graphics2D.fillRect(0, n - (bl ? 2 : 1), (int)this.x, bl ? 5 : 2);
            return;
        }
        if (this.estilo == 5) {
            Image image = Assets.imagem("projetil_guiado_086.png");
            Graphics2D graphics2D2 = (Graphics2D)graphics2D.create();
            graphics2D2.translate(this.x, this.y);
            graphics2D2.rotate(Math.atan2(this.vy, this.vx) - Math.PI);
            graphics2D2.drawImage(image, -11, -8, 22, 16, null);
            graphics2D2.dispose();
            return;
        }
        if (this.tipo == 2) {
            graphics2D.setColor(new Color(255, 97, 64, 80));
            graphics2D.fillOval((int)this.x - 9, (int)this.y - 9, 22, 22);
            graphics2D.setColor(new Color(255, 240, 145));
            graphics2D.fillOval((int)this.x - 4, (int)this.y - 4, 12, 12);
        } else if (this.tipo == 1) {
            graphics2D.setColor(new Color(255, 63, 197));
            graphics2D.fillRoundRect((int)this.x - 5, (int)this.y - 3, 18, 6, 5, 5);
        } else {
            graphics2D.setColor(this.estilo == 1 ? new Color(112, 247, 108) : (this.estilo == 2 ? new Color(186, 102, 255) : new Color(255, 101, 58)));
            graphics2D.fillOval((int)this.x, (int)this.y, 9, 9);
            graphics2D.setColor(new Color(255, 228, 139));
            graphics2D.fillOval((int)this.x + 2, (int)this.y + 2, 4, 4);
        }
    }

    public boolean destrutivel() {
        return this.estilo == 5;
    }

    public boolean gelado(){return estilo==6;}
    public int dano() {
        return estilo==7?3:this.estilo == 5 ? 4 : (this.estilo == 4 ? 3 : (this.tipo == 2 ? 3 : 2));
    }

    public Alien getDono() {
        return this.dono;
    }

    public boolean isVisible() {
        return this.visivel;
    }

    public void setVisible(boolean bl) {
        this.visivel = bl;
    }

    public Rectangle getBounds() {
        if (this.estilo == 5) {
            return new Rectangle((int)this.x - 11, (int)this.y - 10, 26, 20);
        }
        if (this.estilo == 4) {
            return this.tempoLaser > 0.0 ? new Rectangle(0, 0, 0, 0) : new Rectangle(0, (int)this.alvoLaser - 9, Math.max(0, (int)this.x), 18);
        }
        return new Rectangle((int)this.x - 2, (int)this.y - 3, this.tipo == 2 ? 14 : 10, 12);
    }
}

