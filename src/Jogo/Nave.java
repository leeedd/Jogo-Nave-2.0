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
    /** Efeito somado das pecas equipadas no mercado. */
    private int danoPeca, cascoPeca, defesaPeca; private double recargaPeca = 1, velocidadePeca = 1, truqueRecPeca = 1, truqueDurPeca = 1, capsulasPeca = 1; private boolean ima, scanner;
    void aplicarEquipamento(int[] ids) {
        int antes = getVidaMaxima();
        danoPeca = cascoPeca = defesaPeca = 0; tempoCargaLaser = 1; recargaPeca = velocidadePeca = truqueRecPeca = truqueDurPeca = capsulasPeca = 1; ima = scanner = false;
        for (int id : ids) { Pecas p = Pecas.obter(id); if (p == null || !p.serveEm(modeloVisual)) continue;
            danoPeca += p.dano; cascoPeca += p.casco; defesaPeca += p.defesa; recargaPeca *= p.recarga; velocidadePeca *= p.velocidade;
            tempoCargaLaser *= p.cargaLaser; truqueRecPeca *= p.truqueRecarga; truqueDurPeca *= p.truqueDuracao; capsulasPeca *= p.capsulas; ima |= p.ima; scanner |= p.scanner; }
        vida = Math.max(1, Math.min(getVidaMaxima(), vida + getVidaMaxima() - antes));
    }
    double tempoCargaLaser = 1;
    boolean temIma() { return ima; } boolean temScanner() { return scanner; } double bonusCapsulas() { return capsulasPeca; }
    /** Cada Nucleo de Casco encontrado aumenta o casco maximo em 2; o novo casco entra cheio. */
    void aplicarNucleos(int n){int antes=getVidaMaxima();nucleosCasco=Math.max(0,Math.min(8,n));vida=Math.max(1,Math.min(getVidaMaxima(),vida+getVidaMaxima()-antes));}
    private int pisoPermanente,canhaoPermanente,motorPermanente;
    void aplicarPecas(int piso,int canhao,int motor){int antes=getVidaMaxima();pisoPermanente=piso;nivel=Math.max(nivel,piso);canhaoPermanente=canhao;motorPermanente=motor;vida=Math.min(getVidaMaxima(),vida+getVidaMaxima()-antes);}
    int pisoPermanente(){return pisoPermanente;}
    int motorPermanente(){return motorPermanente;}

    private double impulso;
    // ---- Truque exclusivo de cada nave (tecla SHIFT) ----
    static final String[] TRUQUES = {"IMPULSO", "CAMUFLAGEM", "ESCUDO DA VALQUIRIA", "INVESTIDA", "FURIA"};
    static final String[] TRUQUES_DICA = {"Arrancada rapida e intangivel", "Some da mira inimiga por 2 s", "Escudo instantaneo", "Atropela e destroi o que tocar", "Dobra a cadencia; mais dano quanto menor o casco"};
    static final double[] TRUQUE_RECARGA = {3, 9, 12, 6, 15}, TRUQUE_DURACAO = {.28, 2, 0, .6, 4};
    private double truqueRecarga, truqueAtivo, camuflagemX, camuflagemY;
    String nomeTruque() { return TRUQUES[Math.max(0, Math.min(4, modeloVisual))]; }
    double recargaTruque() { return truqueRecarga; }
    double maxRecargaTruque() { return TRUQUE_RECARGA[Math.max(0, Math.min(4, modeloVisual))]; }
    boolean investindo() { return modeloVisual == 3 && truqueAtivo > 0; }
    boolean camuflada() { return modeloVisual == 1 && truqueAtivo > 0; }
    boolean emFuria() { return modeloVisual == 4 && truqueAtivo > 0; }
    double camuflagemX() { return camuflagemX; } double camuflagemY() { return camuflagemY; }
    void usarTruque() {
        int m = Math.max(0, Math.min(4, modeloVisual));
        if (truqueRecarga > 0 || vida <= 0) { SomJogo.tocar(SomJogo.Efeito.ERRO); return; }
        truqueRecarga = TRUQUE_RECARGA[m] * truqueRecPeca; truqueAtivo = TRUQUE_DURACAO[m] * truqueDurPeca;
        switch (m) {
            case 0: invulneravel = Math.max(invulneravel, .3); SomJogo.tocar(SomJogo.Efeito.DOBRA); break;
            case 1: camuflagemX = x; camuflagemY = y; SomJogo.tocar(SomJogo.Efeito.ESCUDO); break;
            case 2: ativarEscudo(); SomJogo.tocar(SomJogo.Efeito.ESCUDO); break;
            case 3: invulneravel = Math.max(invulneravel, .65); SomJogo.tocar(SomJogo.Efeito.IMPACTO); break;
            default: SomJogo.tocar(SomJogo.Efeito.EVOLUCAO); break;
        }
    }
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
        impulso=Math.max(0,impulso-d);truqueRecarga=Math.max(0,truqueRecarga-d);truqueAtivo=Math.max(0,truqueAtivo-d);
        if (travada) soltarTeclas();
        int n = (this.direita ? 1 : 0) - (this.esquerda ? 1 : 0);
        int n2 = (this.baixo ? 1 : 0) - (this.cima ? 1 : 0);
        double d2 = n != 0 && n2 != 0 ? 0.7071068 : 1.0;
        this.motorFalhando = this.vida <= 3 && Math.sin(this.tempoVisual * 7.0) > 0.79;
        geloCapturado=Math.max(0,geloCapturado-d);
        double d3 = (double)this.perfil().velocidade * (1+motorPermanente*(modeloVisual==1?.085:.045)) * (impulso>0?1.35:1.0) * (1.0 + (this.nivel / 2) * 0.025) * (this.motorFalhando ? 0.85 : 1.0)*(geloCapturado>0?.6:1)*(modeloVisual==0&&truqueAtivo>0?3.2:1)*velocidadePeca;
        if (investindo()) { n = 1; d2 = 1; d3 = d3 * 2.6; }
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

    /** Travada carregando o Laser de Convergencia: nao se move nem atira. */
    private boolean travada;
    void travar(boolean t) { travada = t; if (t) soltarTeclas(); }
    boolean travada() { return travada; }
    public void atira() {
        if (travada) return;
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
        } else if (n == 0) {
            // Arma de fabrica de cada nave
            int bx = (int) this.x + 40, by = (int) this.y;
            switch (Math.max(0, Math.min(4, modeloVisual))) {
                case 0: misseis.add(new Missel(bx, by + 12, 0)); misseis.add(new Missel(bx - 5, by + 27, 0)); break; // tiro duplo
                case 1: misseis.add(new Missel(bx + 6, by + 19, 0)); if (nivel >= 3) misseis.add(new Missel(bx, by + 19, 0, 0)); break; // agulha rapida
                case 2: misseis.add(new Missel(bx, by + 19, 0, -7)); misseis.add(new Missel(bx, by + 19, 0)); misseis.add(new Missel(bx, by + 19, 0, 7)); break; // leque
                case 3: misseis.add(new Missel(bx, by + 19, 0)); if (nivel >= 3) misseis.add(new Missel(bx - 8, by + 19, 0)); break; // obus pesado
                default: misseis.add(new Missel(bx, by + 6, 0)); misseis.add(new Missel(bx + 6, by + 19, 0)); misseis.add(new Missel(bx, by + 32, 0)); break; // tridente
            }
            this.recarga = this.perfil().intervalo * (this.nivel >= 2 ? 0.65 : 1.0);
            if (this.riffJames && ++this.tirosAposRiff % 4 == 0) {
                this.misseis.add(new Missel((int)this.x + 40, (int)this.y + 14, 0, -18.0));
                this.misseis.add(new Missel((int)this.x + 40, (int)this.y + 14, 0, 18.0));
            }
            if (this.ataqueSonico && ++this.tirosAposSonico % 5 == 0) {
                this.misseis.add(new Missel((int)this.x + 40, (int)this.y + 15, 0, -12.0));
                this.misseis.add(new Missel((int)this.x + 40, (int)this.y + 15, 0, 12.0));
            }
        } else {
            this.misseis.add(new Missel((int)this.x + 40, (int)this.y + 12, n));
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
        this.recarga *= recargaPeca * (emFuria() ? .5 : 1); // furia acelera qualquer arma
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
        this.vida = Math.max(0, this.vida - Math.max(1, n - (this.blindagemHalford ? 1 : 0) - this.perfil().defesa - defesaPeca));
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

    /** Topo do estagio comprado: 2 no estagio I, 5 no II, 8 no III. O item do setor enche ate aqui e nunca troca o estagio. */
    int topoDoEstagio() { return pisoPermanente <= 2 ? 2 : pisoPermanente <= 5 ? 5 : MAX_EVOLUCAO; }
    /** Item de evolucao do setor: +1 nivel dentro do estagio; devolve false se ja esta no topo. */
    boolean coletarMelhoria() {
        if (nivel >= topoDoEstagio()) return false;
        int antes = getVidaMaxima(); nivel++; if (vida > 0) vida += getVidaMaxima() - antes; return true;
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
        return Math.max(3, this.perfil().casco + this.nivel / 2 + blindagemComprada*4 + nucleosCasco*2 + cascoPeca);
    }

    public int danoBalistico() {
        return Math.max(1, this.perfil().dano + (this.nivel >= 6 ? 1 : 0)+canhaoPermanente*(modeloVisual==3?2:1) + (modeloVisual == 4 ? (vida * 4 <= getVidaMaxima() ? 2 : vida * 2 <= getVidaMaxima() ? 1 : 0) : 0) + danoPeca);
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
        efeitoTruque(graphics2D);
        // durante o truque a nave nao pisca (impulso e investida deixam intangivel, mas sao golpes visiveis)
        if (truqueAtivo <= 0 && this.invulneravel > 0.0 && (int)(this.invulneravel * 14.0) % 2 == 0) {
            return;
        }
        if (camuflada()) {
            Graphics2D a = (Graphics2D) graphics2D.create();
            a.setComposite(java.awt.AlphaComposite.SrcOver.derive((float) (.22 + .08 * Math.sin(tempoVisual * 20))));
            this.desenharSemPiscar(a); a.dispose(); return;
        }
        this.desenharSemPiscar(graphics2D);
    }

    /** Visual de cada truque: rastro do impulso, chamas da investida, aura da furia, isca da camuflagem. */
    private static final java.awt.image.BufferedImage[] EFEITO = {img("efeito_impulso"), img("efeito_camuflagem"), img("efeito_escudo"), img("efeito_investida"), img("efeito_furia")};
    private static java.awt.image.BufferedImage img(String n) { java.net.URL u = Nave.class.getResource("/ImagemIcon/" + n + ".png"); if (u == null) return null; try { return javax.imageio.ImageIO.read(u); } catch (java.io.IOException e) { return null; } }
    /** Desenha o quadro atual da tira de efeito (4 quadros) com a borda direita em xDir, centrado em cy, na altura h. */
    private void quadroEfeito(Graphics2D g, int i, int xDir, int cy, int h, float alfa) {
        java.awt.image.BufferedImage e = EFEITO[i]; if (e == null) return;
        int fw = e.getWidth() / 4, q = (int) (tempoVisual * 12) % 4, w = fw * h / e.getHeight();
        Graphics2D a = (Graphics2D) g.create(); a.setComposite(java.awt.AlphaComposite.SrcOver.derive(alfa));
        a.setRenderingHint(java.awt.RenderingHints.KEY_INTERPOLATION, java.awt.RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        a.drawImage(e, xDir - w, cy - h / 2, xDir, cy + h / 2, q * fw, 0, q * fw + fw, e.getHeight(), null); a.dispose();
    }

    private void efeitoTruque(Graphics2D g) {
        int cx = (int) x + 22, cy = (int) y + 20;
        if (modeloVisual == 2 && escudo > 0 && EFEITO[2] != null) { quadroEfeito(g, 2, cx + 62, cy, 118, .85f); }
        if (truqueAtivo <= 0) return;
        if (EFEITO[modeloVisual] != null && modeloVisual != 2) {
            switch (modeloVisual) {
                case 0: quadroEfeito(g, 0, cx - 6, cy, 60, 1f); break;
                case 1: quadroEfeito(g, 1, (int) camuflagemX + 78, (int) camuflagemY + 20, 70, .9f); break;
                case 3: quadroEfeito(g, 3, cx + 92, cy, 96, 1f); break;
                default: quadroEfeito(g, 4, cx + 70, cy, 130, .9f); break;
            }
            return;
        }
        Graphics2D a = (Graphics2D) g.create();
        switch (modeloVisual) {
            case 0: for (int k = 1; k <= 4; k++) { a.setColor(new Color(120, 200, 255, 120 - k * 25)); a.fillRoundRect(cx - 30 - k * 22, cy - 12, 50, 24, 20, 20); } break;
            case 1: { // a isca fantasma fica onde a nave estava: e ali que os inimigos miram
                a.setComposite(java.awt.AlphaComposite.SrcOver.derive(.45f));
                Graphics2D b = (Graphics2D) a.create(); b.translate((int) camuflagemX, (int) camuflagemY);
                ArteNaves.desenharJogador(b, nivel, modeloVisual, 0, 0, tempoVisual); b.dispose();
                break; }
            case 3: for (int k = 0; k < 10; k++) { a.setColor(new Color(255, 120 + k * 12, 30, 200 - k * 18)); int r = 22 - k; a.fillOval(cx - 34 - k * 9 - r, cy - r + (int) (Math.sin(tempoVisual * 30 + k) * 4), r * 2, r * 2); }
                a.setColor(new Color(255, 230, 150, 160)); a.fillPolygon(new int[]{cx + 30, cx + 52, cx + 30}, new int[]{cy - 22, cy, cy + 22}, 3); break;
            case 4: { int r = 34 + (int) (Math.sin(tempoVisual * 12) * 4); a.setColor(new Color(255, 40, 30, 70)); a.fillOval(cx - r, cy - r, 2 * r, 2 * r);
                a.setColor(new Color(255, 120, 40, 150)); for (int k = 0; k < 8; k++) { double an = tempoVisual * 6 + k * Math.PI / 4; a.fillRect(cx + (int) (Math.cos(an) * r) - 2, cy + (int) (Math.sin(an) * r * .7) - 2, 4, 4); } break; }
            default: break;
        }
        a.dispose();
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
            case 16: {
                this.usarTruque();
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

