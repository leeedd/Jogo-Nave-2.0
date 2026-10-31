/*
 * Decompiled with CFR 0.152.
 */
package Jogo;


import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;

public final class MiniChefe {
    private double x = 1060.0;
    private double y = 305.0;
    private double tempo;
    private double carga = 2.4;
    private int vida;
    private int vidaMaxima;
    private int raioAlvo;
    private boolean carregando;
    private boolean disparando;
    private boolean atingiu;
    private double relogioRaio;
    private final int setor;
    private final boolean invasor;
    private ComandanteEnxame comandante;
    boolean invasor(){return invasor;}
    boolean derrotado(){return comandante!=null && comandante.terminou();}
    boolean morrendo(){return comandante!=null && comandante.morrendo();}
    int estagio(){return comandante==null?0:comandante.estagio();}
    /** Integrante da banda que a sentinela representa (provisorio ate a arte de cada um chegar). */
    private String integrante="";
    /** Bateristas e baixistas com arte propria atiram projeteis em vez do raio (pratos e ondas graves). */
    private final java.util.List<TiroChefe> projeteis = new java.util.ArrayList<>();
    private char instrumento() { return AtlasInimigos.integrante(integrante) == null ? 'C' : Turne.instrumento(integrante); }
    private boolean atiraProjetil() { char c = instrumento(); return c == 'D' || c == 'B'; }
    java.util.List<TiroChefe> projeteisTeste() { return projeteis; }
    void definirIntegrante(String nome,int risco){integrante=nome==null?"":nome;vida=vidaMaxima=(int)Math.round(vidaMaxima*Turne.premio(risco));}
    String nome(){if(comandante==null&&!integrante.isEmpty())return integrante;return comandante==null?"SENTINELA":comandante.rainha?"RAINHA DO ENXAME":"PIRATA DOMADOR";}
    ComandanteEnxame comandanteTeste(){return comandante;}
    boolean receberImpacto(Rectangle r,int dano){return comandante==null?receberDano(dano):comandante.impacto(r,dano);}
    MiniChefe(int n,boolean invasor,boolean rainha){this(n,invasor);if(invasor)comandante=new ComandanteEnxame(rainha,n);}

    public MiniChefe(int n) { this(n, false); }

    MiniChefe(int n, boolean invasor) {SomJogo.tocar(SomJogo.Efeito.CHEFE_ENTRADA);
        this.invasor = invasor;
        if(invasor)comandante=new ComandanteEnxame(true,n);
        this.setor = n;
        this.vida = this.vidaMaxima = 24 + n * 7;
    }

    void posicionarInterdicao() { x=740; y=314; }

    public void atualizar(double d, int n) {
        if(comandante!=null){comandante.atualizar(d,n);return;}
        d = Math.max(0.0, Math.min(0.05, d));
        this.tempo += d;
        for (TiroChefe t : projeteis) t.mexer(d);
        projeteis.removeIf(t -> !t.isVisible());
        this.x += (700.0 - this.x) * (1.0 - Math.exp(-1.7 * d));
        double destino = (this.carregando || this.disparando) ? this.raioAlvo
                : 305.0 + Math.sin(this.tempo * 1.15) * 82.0;
        double deslocamento = (destino - this.y) * (1.0 - Math.exp(-4.5 * d));
        this.y += Math.max(-260 * d, Math.min(260 * d, deslocamento));
        if (!this.carregando && !this.disparando) {
            this.carga -= d;
            if (this.carga <= 0.0) {
                this.raioAlvo = Math.max(172, Math.min(505, n + 18));
                this.carregando = true;SomJogo.tocar(SomJogo.Efeito.ALERTA);
                this.relogioRaio = 1.05;
                this.atingiu = false;
            }
        } else if (this.carregando) {
            this.relogioRaio -= d;
            if (this.relogioRaio <= 0.0) {
                this.carregando = false;
                this.raioAlvo = (int)Math.round(this.y);
                this.disparando = true;SomJogo.tocar(atiraProjetil() ? SomJogo.Efeito.CHEFE_TIRO : SomJogo.Efeito.LASER);
                if (atiraProjetil()) {
                    this.atingiu = true; // sem raio: o perigo sao os projeteis
                    double ox = this.x - 60, oy = this.y, mira = Math.atan2(n + 18 - oy, 120 - ox);
                    if (instrumento() == 'D') for (int i = -2; i <= 2; i++) projeteis.add(new TiroChefe(ox, oy, Math.cos(mira + i * .2) * 300, Math.sin(mira + i * .2) * 300, false, TiroChefe.PRATO, TiroChefe.Movimento.RETO));
                    else for (int i = -1; i <= 1; i++) projeteis.add(new TiroChefe(ox, oy + i * 50, -320, i * 30, false, TiroChefe.ONDA_SOM, TiroChefe.Movimento.RETO));
                }
                this.relogioRaio = 0.43;
            }
        } else {
            this.relogioRaio -= d;
            if (this.relogioRaio <= 0.0) {
                this.disparando = false;
                this.carga = Math.max(1.4, 3.2 - (double)this.setor * 0.13);
            }
        }
    }

    public boolean consumirDano(Rectangle rectangle) {
        if(comandante!=null)return comandante.consumirDano(rectangle);
        for (TiroChefe t : projeteis) if (t.isVisible() && t.getBounds().intersects(rectangle)) { t.setVisible(false); return true; }
        if (!this.disparando || this.atingiu) {
            return false;
        }
        Rectangle rectangle2 = new Rectangle(0, this.raioAlvo - 12, Math.max(0, fimRaio()), 24);
        if (!rectangle2.intersects(rectangle)) {
            return false;
        }
        this.atingiu = true;
        return true;
    }

    /** Onde o raio do codigo termina: na boca do raio da arte (olho da sentinela ou caveira do integrante). */
    private int fimRaio() {
        if (AtlasInimigos.integrante(integrante) != null) return (int) this.x - 131;
        if (AtlasInimigos.temSentinelaOlho()) return (int) this.x - 81;
        return (int) this.x - 65;
    }
    /** Raio em pixel art: blocos de 4 px, borda serrilhada que treme, nucleo claro; roxo no integrante. */
    private void desenharRaio(Graphics2D g) {
        boolean roxo = AtlasInimigos.integrante(integrante) != null;
        Color borda = roxo ? new Color(110, 30, 190) : new Color(150, 20, 30), meio = roxo ? new Color(190, 90, 255) : new Color(240, 60, 50),
              claro = roxo ? new Color(235, 200, 255) : new Color(255, 200, 120);
        int fim = fimRaio(), y = this.raioAlvo; java.util.Random r = new java.util.Random((long) (this.tempo * 30));
        for (int px = 0; px < fim; px += 4) {
            int h = 9 + r.nextInt(3) * 2;
            g.setColor(borda); g.fillRect(px, y - h, 4, h * 2);
            g.setColor(meio); g.fillRect(px, y - h + 4, 4, h * 2 - 8);
            g.setColor(claro); g.fillRect(px, y - 2, 4, 4);
            if (r.nextInt(7) == 0) { g.setColor(claro); g.fillRect(px, y + (r.nextBoolean() ? -h - 4 : h), 4, 4); } // faisca solta
        }
        // dois raios eletricos serpenteando em volta do laser, em sentidos opostos
        for (int fio = 0; fio < 2; fio++) {
            int ant = y;
            for (int px = fim; px > 0; px -= 4) {
                int yy = y + (int) (Math.sin(px * .045 + this.tempo * (fio == 0 ? 26 : -22) + fio * Math.PI) * 17) + r.nextInt(3) - 1;
                g.setColor(fio == 0 ? Color.WHITE : claro);
                g.fillRect(px - 4, Math.min(ant, yy), 4, Math.abs(yy - ant) + 3); ant = yy;
            }
        }
    }

    public boolean receberDano(int n) {
        if(comandante!=null){comandante.dano(n);return comandante.terminou();}
        this.vida = Math.max(0, this.vida - Math.max(0, n));
        return this.vida == 0;
    }

    public void atrapalharCarga() {
        if (this.carregando) {
            this.relogioRaio += 0.35;
        }
    }

    public Rectangle getBounds() {
        if(comandante!=null)return comandante.corpo();
        return new Rectangle((int)this.x - 70, (int)this.y - 48, 140, 96);
    }

    public int getVida() {
        return comandante==null?this.vida:comandante.vida();
    }

    public int getVidaMaxima() {
        return comandante==null?this.vidaMaxima:comandante.max();
    }

    public boolean avisando() {
        return this.carregando;
    }

    public boolean disparando() {
        return this.disparando;
    }

    public int getRaioAlvo() {
        return this.raioAlvo;
    }

    public void desenhar(Graphics2D graphics2D) {
        if(comandante!=null){comandante.desenhar(graphics2D);return;}
        Graphics2D graphics2D2 = (Graphics2D)graphics2D.create();
        // sem linha de aviso: enquanto carrega, o aviso e o proprio olho arregalado; o raio so aparece no disparo
        if (this.disparando && !atiraProjetil()) desenharRaio(graphics2D2);
        for (TiroChefe t : projeteis) t.desenhar(graphics2D2);
        int n = (int)this.x;
        int n2 = (int)this.y;
        int n3 = this.disparando ? 2 : (this.carregando ? 1 : ((int)(this.tempo * 4.0)%2==0?0:3));
        if (invasor) {
            Graphics2D monstro = (Graphics2D)graphics2D2.create();
            monstro.translate(n-64,n2-56);monstro.scale(2,2);
            InimigosUniversos.desenhar(monstro,6,Alien.Classe.ORGANICO,0,0,tempo,false);monstro.dispose();
        } else if (AtlasInimigos.integrante(integrante) != null) {
            // o integrante toca o tempo todo: alterna tocar e bate-cabeca; arregala no preparo e solta o golpe
            int q = this.disparando ? 2 : this.carregando ? 1 : (int) (this.tempo * 2.2) % 2 == 0 ? 0 : 3;
            AtlasInimigos.integrante(graphics2D2, AtlasInimigos.integrante(integrante), n, n2 + 5, q);
        } else if (AtlasInimigos.temSentinelaOlho()) {
            AtlasInimigos.sentinelaOlho(graphics2D2, n, n2 - 11, this.disparando ? 2 : this.carregando ? 1 : vida < vidaMaxima * .35 ? 3 : 0);
        } else AtlasInimigos.sentinela(graphics2D2, n, n2, n3);
        graphics2D2.setColor(new Color(255, 205, 115));
        graphics2D2.setFont(new Font("Monospaced", 1, 12));
        {String rot=invasor ? "DEVORADOR DO VAZIO" : nome();graphics2D2.drawString(rot, n - graphics2D2.getFontMetrics().stringWidth(rot) / 2 + 2, n2 - (!invasor && AtlasInimigos.integrante(integrante) != null ? 108 : AtlasInimigos.temSentinelaOlho() && !invasor ? 92 : 65));}
        int bx = Math.max(8, Math.min(824, n - 64));
        int by = Math.min(564, n2 + (!invasor && AtlasInimigos.integrante(integrante) != null ? 104 : AtlasInimigos.temSentinelaOlho() && !invasor ? 90 : 66));
        HudMetal.painel(graphics2D2, bx, by, 128, 27);
        for (int i = 0; i < 16; i++) {
            int px = bx + 10 + i * 7;
            boolean aceso = i < (int)Math.ceil(16.0 * vida / vidaMaxima);
            graphics2D2.setColor(aceso ? new Color(141, 24, 34) : new Color(13, 19, 27));
            graphics2D2.fillRect(px, by + 8, 5, 11);
            if (aceso) {
                graphics2D2.setColor(new Color(248, 79, 55));
                graphics2D2.fillRect(px, by + 8, 5, 6);
                graphics2D2.setColor(new Color(255, 192, 131));
                graphics2D2.fillRect(px + 1, by + 8, 3, 1);
            }
        }
        graphics2D2.dispose();
    }
}

