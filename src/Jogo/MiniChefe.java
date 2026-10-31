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
                this.disparando = true;SomJogo.tocar(SomJogo.Efeito.LASER);
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
        if (!this.disparando || this.atingiu) {
            return false;
        }
        Rectangle rectangle2 = new Rectangle(0, this.raioAlvo - 12, Math.max(0, (int)this.x - 65), 24);
        if (!rectangle2.intersects(rectangle)) {
            return false;
        }
        this.atingiu = true;
        return true;
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
        if (this.disparando) {
            boolean roxo = AtlasInimigos.integrante(integrante) != null; // o golpe do Kirk e roxo, como na arte
            graphics2D2.setColor(roxo ? new Color(170, 60, 255, 190) : new Color(250, 70, 72, this.disparando ? 190 : 75));
            graphics2D2.fillRect(0, this.raioAlvo - (this.disparando ? 10 : 2), (int)this.x - 65, this.disparando ? 20 : 4);
            graphics2D2.setColor(roxo ? new Color(235, 200, 255, 220) : new Color(255, 216, 149, this.disparando ? 220 : 120));
            graphics2D2.fillRect(0, this.raioAlvo - (this.disparando ? 3 : 1), (int)this.x - 65, this.disparando ? 6 : 2);
        }
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
            AtlasInimigos.integrante(graphics2D2, AtlasInimigos.integrante(integrante), n, n2, q);
        } else if (AtlasInimigos.temSentinelaOlho()) {
            AtlasInimigos.sentinelaOlho(graphics2D2, n, n2, this.disparando ? 2 : this.carregando ? 1 : vida < vidaMaxima * .35 ? 3 : 0);
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

