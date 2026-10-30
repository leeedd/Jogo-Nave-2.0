/*
 * Decompiled with CFR 0.152.
 */
package Jogo;




import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

public class Nave {
    private double x = 55.0;
    private double y = 300.0;
    private boolean cima;
    private boolean baixo;
    private boolean esquerda;
    private boolean direita;
    private int vida = 12;
    public static final int MAX_EVOLUCAO = 8;
    private int nivel = 0;
    private int blindagemComprada, nucleosCasco;
    /** Cada Nucleo de Casco encontrado aumenta o casco maximo em 2; o novo casco entra cheio. */
    void aplicarNucleos(int n){int antes=getVidaMaxima();nucleosCasco=Math.max(0,Math.min(8,n));vida=Math.max(1,Math.min(getVidaMaxima(),vida+getVidaMaxima()-antes));}
    private int pisoPermanente,canhaoPermanente,motorPermanente;
    void aplicarPecas(int piso,int canhao,int motor){int antes=getVidaMaxima();pisoPermanente=piso;nivel=Math.max(nivel,piso);canhaoPermanente=canhao;motorPermanente=motor;vida=Math.min(getVidaMaxima(),vida+getVidaMaxima()-antes);}
    int pisoPermanente(){return pisoPermanente;}
    int motorPermanente(){return motorPermanente;}

    private double impulso;
    void aplicarBlindagem(int valor){int antes=getVidaMaxima();blindagemComprada=Math.max(0,Math.min(3,valor));vida=Math.max(1,Math.min(getVidaMaxima(),vida+getVidaMaxima()-antes));}
    int blindagemComprada(){return blindagemComprada;}
    void escudoPor(double s){escudo=Math.max(escudo,s);}
    void impulsoPor(double s){impulso=Math.max(impulso,s);}
    private double invulneravel = 0.0;
    private double recarga = 0.0;
    private double geloCapturado;public void congelar(){geloCapturado=1.5;}
    private double escudo = 0.0;
    private boolean explosaoEscudoPendente;
    private double tempoVisual;
    private double inclinacaoVisual;
    private double claraoDisparo;
    private boolean motorFalhando;
    private final List<Missel> misseis = new ArrayList<Missel>();
    private boolean armaSecundariaColetada = false;
    private boolean ataqueSonico = false;
    private int tirosAposSonico;
    private int tirosAposRiff;
    private boolean blindagemHalford;
    private boolean motorLemmy;
    private boolean riffJames;
    private boolean rajadaTom;
    private boolean escudoTestament;
    private boolean lendaria;
    private int modeloVisual;

    public static double velocidadeBase(boolean bl) {
        return bl ? 310.0 : 270.0;
    }

    public void mexer(double d) {
        impulso=Math.max(0,impulso-d);
        int n = (this.direita ? 1 : 0) - (this.esquerda ? 1 : 0);
        int n2 = (this.baixo ? 1 : 0) - (this.cima ? 1 : 0);
        double d2 = n != 0 && n2 != 0 ? 0.7071068 : 1.0;
        this.motorFalhando = this.vida <= 3 && Math.sin(this.tempoVisual * 7.0) > 0.79;
        geloCapturado=Math.max(0,geloCapturado-d);
        double d3 = (double)this.perfil().velocidade * (1+motorPermanente*(modeloVisual==1?.085:.045)) * (impulso>0?1.35:1.0) * (1.0 + (this.nivel / 2) * 0.025) * (this.motorFalhando ? 0.85 : 1.0)*(geloCapturado>0?.6:1);
        this.x = Nave.limitar(this.x + (double)n * d3 * d * d2, 8.0, 905.0);
        this.y = Nave.limitar(this.y + (double)n2 * d3 * d * d2, 90.0, 554.0);
        this.invulneravel = Math.max(0.0, this.invulneravel - d);
        this.recarga = Math.max(0.0, this.recarga - d);
        this.tempoVisual += d;
        double d4 = (this.baixo ? 1 : 0) - (this.cima ? 1 : 0);
        this.inclinacaoVisual += (d4 - this.inclinacaoVisual) * Math.min(1.0, d * 7.5);
        this.claraoDisparo = Math.max(0.0, this.claraoDisparo - d * 6.0);
        if (this.escudo > 0.0) {
            this.escudo = Math.max(0.0, this.escudo - d);
            if (this.escudo == 0.0) {
                SomJogo.tocar(SomJogo.Efeito.ESCUDO_QUEBRA);this.explosaoEscudoPendente = true;
            }
        }
    }

    private static double limitar(double d, double d2, double d3) {
        return Math.max(d2, Math.min(d3, d));
    }

    public void atira() {
        if (this.recarga > 0.0) {
            return;
        }
        this.claraoDisparo = 1.0;
        int n = Missel.getTipoMissel();
        if (!this.podeUsarArma(n)) {
            n = 0;
        }
        SomJogo.tocar(n==1?SomJogo.Efeito.DISPERSAO:n==2?SomJogo.Efeito.NUCLEAR:SomJogo.Efeito.TIRO,x);
        if (n == 1) {
            int[] nArray;
            int[] nArray2;
            if (this.rajadaTom) {
                int[] nArray3 = new int[5];
                nArray3[0] = -25;
                nArray3[1] = -12;
                nArray3[2] = 0;
                nArray3[3] = 12;
                nArray2 = nArray3;
                nArray3[4] = 25;
            } else {
                int[] nArray4 = new int[3];
                nArray4[0] = -17;
                nArray4[1] = 0;
                nArray2 = nArray4;
                nArray4[2] = 17;
            }
            for (int n2 : nArray = nArray2) {
                this.misseis.add(new Missel((int)this.x + 40, (int)this.y + 18, 1, n2));
            }
            this.recarga = 0.52 * this.perfil().intervalo / 0.28;
        } else {
            this.misseis.add(new Missel((int)this.x + 40, (int)this.y + 12, n));
            if (this.nivel >= 3 && n == 0) {
                this.misseis.add(new Missel((int)this.x + 35, (int)this.y + 27, n));
            }
            this.recarga = n == 2 ? 0.95 : this.perfil().intervalo * (this.nivel >= 2 ? 0.65 : 1.0);
            if (this.riffJames && n == 0 && ++this.tirosAposRiff % 4 == 0) {
                this.misseis.add(new Missel((int)this.x + 40, (int)this.y + 14, 0, -18.0));
                this.misseis.add(new Missel((int)this.x + 40, (int)this.y + 14, 0, 18.0));
            }
            if (this.ataqueSonico && n == 0 && ++this.tirosAposSonico % 5 == 0) {
                this.misseis.add(new Missel((int)this.x + 40, (int)this.y + 15, 0, -12.0));
                this.misseis.add(new Missel((int)this.x + 40, (int)this.y + 15, 0, 12.0));
            }
        }
        this.recarga *= 1-motorPermanente*(modeloVisual==4?.10:.05);
        this.recarga *= 1.0 - Math.max(0, this.nivel - 3) * 0.04;
        if (this.motorLemmy) {
            this.recarga /= 1.22;
        }
    }

    public boolean receberDano(int n) {
        if (this.invulneravel > 0.0 || this.vida <= 0) {
            return false;
        }
        if (this.escudo > 0.0) {
            this.escudo = 0.0;
            SomJogo.tocar(SomJogo.Efeito.ESCUDO_QUEBRA);this.explosaoEscudoPendente = true;
            this.invulneravel = 0.55;
            return false;
        }
        this.vida = Math.max(0, this.vida - Math.max(1, n - (this.blindagemHalford ? 1 : 0) - this.perfil().defesa));
        SomJogo.tocar(SomJogo.Efeito.DANO);this.invulneravel = 1.3;
        return true;
    }

    public int perderModulo() {
        if (this.armaSecundariaColetada) {
            this.armaSecundariaColetada = false;
            this.atualizarArma();
            return 1;
        }
        if (this.nivel <= pisoPermanente) {
            return -1;
        }
        int n = this.nivel >= 2 ? 2 : 0;
        --this.nivel;
        this.vida = Math.min(this.vida, this.getVidaMaxima());
        this.atualizarArma();
        return n;
    }

    public void melhorar() {
        int vidaMaximaAnterior = this.getVidaMaxima();
        this.nivel = Math.min(MAX_EVOLUCAO, this.nivel + 1);
        if (this.vida > 0) this.vida += this.getVidaMaxima() - vidaMaximaAnterior;
    }

    public void curar(int n) {
        this.vida = Math.min(this.getVidaMaxima(), this.vida + n);
    }

    public void ativarEscudo() {
        this.escudo = this.perfil().escudo + (this.nivel >= 1 ? 2 : 0) + (this.escudoTestament ? 9 : 0);
        this.explosaoEscudoPendente = false;
    }

    public boolean consumirExplosaoEscudo() {
        if (!this.explosaoEscudoPendente) {
            return false;
        }
        this.explosaoEscudoPendente = false;
        return true;
    }

    public double getTempoEscudo() {
        return this.escudo;
    }

    public void desbloquearAtaqueSonico() {
        this.ataqueSonico = true;
    }

    public boolean temAtaqueSonico() {
        return this.ataqueSonico;
    }

    public void ativarBlindagemHalford() {
        this.blindagemHalford = true;
    }

    public void ativarMotorLemmy() {
        this.motorLemmy = true;
    }

    public void ativarRiffJames() {
        this.riffJames = true;
    }

    public void ativarRajadaTom() {
        this.rajadaTom = true;
    }

    public void ativarEscudoTestament() {
        this.escudoTestament = true;
        this.ativarEscudo();
    }

    public void ativarLendaria(boolean bl) {
        this.lendaria = bl;
    }

    public void definirModeloVisual(int n) {
        int n2 = this.getVidaMaxima() - this.vida;
        if(modeloVisual!=Math.max(0,Math.min(4,n))){nivel=Math.max(0,nivel-pisoPermanente);pisoPermanente=canhaoPermanente=motorPermanente=0;}
        this.modeloVisual = Math.max(0, Math.min(4, n));
        this.vida = Math.max(1, this.getVidaMaxima() - n2);
    }

    public PerfilNave perfil() {
        return PerfilNave.obter(this.modeloVisual);
    }

    public int getVidaMaxima() {
        return this.perfil().casco + this.nivel / 2 + blindagemComprada*4 + nucleosCasco*2;
    }

    public int danoBalistico() {
        return this.perfil().dano + (this.nivel >= 6 ? 1 : 0)+canhaoPermanente*(modeloVisual==3?2:1);
    }

    public int getModeloVisual() {
        return this.modeloVisual;
    }

    public boolean ehLendaria() {
        return this.lendaria;
    }

    boolean temBlindagemTeste() {
        return this.blindagemHalford;
    }

    boolean temMotorTeste() {
        return this.motorLemmy;
    }

    boolean temRiffTeste() {
        return this.riffJames;
    }

    boolean temRajadaTeste() {
        return this.rajadaTom;
    }

    boolean temEscudoTestamentTeste() {
        return this.escudoTestament;
    }

    public boolean temArmaSecundaria() {
        return this.armaSecundariaColetada;
    }

    public void coletarArmaSecundaria() {
        this.armaSecundariaColetada = true;
        Missel.setTipoTiro(1);
    }

    public boolean podeUsarArma(int n) {
        return n == 0 || n == 1 && this.armaSecundariaColetada || n == 2 && this.nivel >= 2;
    }

    public void atualizarArma() {
        if (!this.podeUsarArma(Missel.getTipoMissel())) {
            Missel.setTipoTiro(0);
        }
    }

    private void proximaArma() {
        int n = Missel.getTipoMissel();
        for (int i = 1; i <= 3; ++i) {
            int n2 = (n + i) % 3;
            if (!this.podeUsarArma(n2)) continue;
            if(n2!=n)SomJogo.tocar(SomJogo.Efeito.TROCA);Missel.setTipoTiro(n2);
            return;
        }
    }

    public void desenhar(Graphics2D graphics2D) {
        if (this.invulneravel > 0.0 && (int)(this.invulneravel * 14.0) % 2 == 0) {
            return;
        }
        this.desenharSemPiscar(graphics2D);
    }

    void desenharSemPiscar(Graphics2D graphics2D) {
        int n;
        Graphics2D graphics2D2 = (Graphics2D)graphics2D.create();
        graphics2D2.translate((int)this.x, (int)this.y);
        ArteNaves.desenharJogador(graphics2D2, this.nivel, this.modeloVisual, 0, 0, this.tempoVisual, this.inclinacaoVisual, this.claraoDisparo, (double)this.vida / (double)this.getVidaMaxima(), this.motorFalhando);
        int n2 = this.getVidaMaxima() - this.vida;
        if (n2 >= 3) {
            graphics2D2.setStroke(new BasicStroke(1.5f));
            graphics2D2.setColor(new Color(26, 18, 18, Math.min(235, 95 + n2 * 10)));
            graphics2D2.drawPolyline(new int[]{12, 21, 18, 32, 30}, new int[]{8, 12, 19, 25, 33}, 5);
            graphics2D2.drawPolyline(new int[]{39, 32, 36, 19}, new int[]{12, 19, 25, 29}, 4);
        }
        if (n2 >= 6) {
            graphics2D2.setColor(new Color(247, 111, 36, 175));
            graphics2D2.fillRect(28, 23, 4, 3);
            graphics2D2.fillRect(12, 28, 3, 3);
        }
        if (n2 >= 9) {
            n = (int)(this.tempoVisual * 11.0);
            for (int i = 0; i < 5; ++i) {
                int n3 = 25 - (int)((this.tempoVisual * 22.0 + (double)(i * 9)) % 38.0);
                int n4 = -5 - i * 5 + (int)(3.0 * Math.sin(this.tempoVisual * 2.0 + (double)i));
                graphics2D2.setColor(new Color(118, 124, 135, 145 - i * 19));
                graphics2D2.fillOval(n4, n3, 8 + i, 6 + i);
            }
            if (this.motorFalhando || n % 4 == 0) {
                graphics2D2.setColor(new Color(255, 172, 70, 190));
                graphics2D2.fillRect(-7, 15, 6, 2);
            }
        }
        if (this.lendaria) {
            graphics2D2.setColor(new Color(249, 182, 77, 160));
            graphics2D2.drawOval(-10, -8, 66, 58);
            graphics2D2.setColor(new Color(117, 229, 255));
            graphics2D2.fillOval(-8 - (int)(2.0 * Math.sin(this.tempoVisual * 15.0)), 11, 13, 5);
            graphics2D2.fillOval(-8 - (int)(2.0 * Math.cos(this.tempoVisual * 15.0)), 27, 13, 5);
        }
        if (this.escudo > 0.0) {
            graphics2D2.setColor(new Color(81, 220, 255, 160));
            n = (int)(3.0 * Math.sin(this.tempoVisual * 5.0));
            graphics2D2.drawOval(-5 - n, -7 - n, 57 + 2 * n, 52 + 2 * n);
            graphics2D2.drawOval(-7 - n, -9 - n, 61 + 2 * n, 56 + 2 * n);
        }
        graphics2D2.dispose();
    }

    public void keyPressed(KeyEvent keyEvent) {
        switch (keyEvent.getKeyCode()) {
            case 38: 
            case 87: {
                this.cima = true;
                break;
            }
            case 40: 
            case 83: {
                this.baixo = true;
                break;
            }
            case 37: 
            case 65: {
                this.esquerda = true;
                break;
            }
            case 39: 
            case 68: {
                this.direita = true;
                break;
            }
            case 32: {
                this.atira();
                break;
            }
            case 70: {
                this.proximaArma();
                break;
            }
        }
    }

    public void keyReleased(KeyEvent keyEvent) {
        switch (keyEvent.getKeyCode()) {
            case 38: 
            case 87: {
                this.cima = false;
                break;
            }
            case 40: 
            case 83: {
                this.baixo = false;
                break;
            }
            case 37: 
            case 65: {
                this.esquerda = false;
                break;
            }
            case 39: 
            case 68: {
                this.direita = false;
                break;
            }
        }
    }

    void reposicionarAposInterdicao() {
        x=55;y=300;invulneravel=Math.max(invulneravel,1.2);soltarTeclas();
    }

    public void soltarTeclas() {
        this.direita = false;
        this.esquerda = false;
        this.baixo = false;
        this.cima = false;
    }

    public int getVida() {
        return this.vida;
    }

    boolean motorFalhandoTeste() {
        return this.motorFalhando;
    }

    void danoDiretoTeste(int n) {
        this.vida = Math.max(0, this.vida - n);
    }

    public int getNivel() {
        return this.nivel;
    }

    public boolean temEscudo() {
        return this.escudo > 0.0;
    }

    public boolean estaViva() {
        return this.vida > 0;
    }

    public double getInvulneravel() {
        return this.invulneravel;
    }

    public List<Missel> getMisseis() {
        return this.misseis;
    }

    public int getX() {
        return (int)this.x;
    }

    public int getY() {
        return (int)this.y;
    }

    /** Poderes: Revoada deixa a nave intocavel; o Clone de cristal troca a nave de lugar. */
    void tornarIntangivel(double s) { this.invulneravel = Math.max(this.invulneravel, s); }
    /** Restaura o casco gravado (retomada, troca de nave). */
    void definirVida(int v) { this.vida = Math.max(1, Math.min(getVidaMaxima(), v)); }
    void teleportar(double nx, double ny) { this.x = Nave.limitar(nx, 8.0, 905.0); this.y = Nave.limitar(ny, 90.0, 554.0); }

    public Rectangle getBounds() {
        return new Rectangle(this.getX() + 5, this.getY() + 5, 37, 29);
    }
}

