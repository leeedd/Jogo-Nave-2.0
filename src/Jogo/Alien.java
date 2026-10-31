/*
 * Decompiled with CFR 0.152.
 */
package Jogo;



import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.List;
import java.util.Random;

public class Alien {
    private static int contador;
    private final Tipo tipo;
    private final Classe classe;
    private int nivelEvolucao;
    private double impulsoKamikaze;
    private double alvoKamikazeY;
    private double alvoColetaX = Double.NaN;
    private double alvoColetaY = Double.NaN;
    private boolean ativouKamikaze;
    private final int largura = 64;
    private final int altura = 57;
    private int variante;
    private int universo;
    private int especieEnxame=-1;
    private double tempoAtaqueEnxame;
    void animarAtaqueEnxame(){tempoAtaqueEnxame=.65;}
    int especieEnxame(){return especieEnxame;}
    void escoltar(Rectangle r,int ordem){if(especieEnxame==1&&posicionado){alvoColetaX=r.x-85.0-ordem*18;alvoColetaY=Math.max(135,Math.min(490,r.y+ordem*72));}}
    void definirEnxame(int especie){especieEnxame=Math.floorMod(especie,6);resistencia=especieEnxame==1?7:especieEnxame==3?2:3;}
    private Item.Tipo equipamento;private int escudoCapturado;
    private double velocidadeSuaveX, velocidadeSuaveY;
    private double x;
    private double y;
    private double alvoX;
    private double alvoY;
    private double esperaEntrada;
    private boolean emFormacao;
    /** Enxame de passagem: cruza a tela da direita para a esquerda atirando e some do outro lado. */
    private boolean passagem;
    private double velPassagem, baseYPassagem, ampPassagem, freqPassagem, fasePassagem, derivaPassagem;
    void iniciarPassagem(double x0, double baseY, double vel, double amp, double freq, double fase, double deriva) {
        passagem = true; emFormacao = false; posicionado = true; x = x0; baseYPassagem = baseY; velPassagem = vel;
        ampPassagem = amp; freqPassagem = freq; fasePassagem = fase; derivaPassagem = deriva;
        y = Math.max(80, Math.min(540, baseY + Math.sin(fase) * amp)); recarga = .5 + (x0 % 7) * .12;
    }
    boolean emPassagem() { return passagem; }
    private boolean posicionado;
    private double tempoAnimacao;
    private double tempoDesvio;
    private double direcaoDesvio;
    private double descansoDesvio;
    private double recarga;
    private int padrao;
    private double rapidez = 1.0;
    private double faseMovimento;
    private boolean visivel = true;
    private int armaRoubada = -1;
    private int resistencia;
    private double congelado, queimando, pulsoQueimadura;
    /** Adaptacoes vindas do ComandoInimigo (casco contra fogo/gelo, desvio, recuo). */
    private int adaptacao;
    void adaptar(int a){adaptacao=a;}
    boolean adaptado(int f){return (adaptacao&f)!=0;}
    /** Casco adaptado: a arma que o piloto mais usa faz metade do dano. */
    int danoElemental(boolean fogo,int dano){return adaptado(fogo?ComandoInimigo.CASCO_FOGO:ComandoInimigo.CASCO_GELO)?Math.max(1,dano/2):dano;}
    void congelarPor(double s){congelado=Math.max(congelado,adaptado(ComandoInimigo.CASCO_GELO)?s*.3:s);enrolado=false;}
    /** Preso pelos tentaculos: fica imovel como congelado, mas sem o gelo (o tentaculo e desenhado por cima). */
    private boolean enrolado;
    void agarrarPor(double s){if(congelado<=0)enrolado=true;congelado=Math.max(congelado,s);}
    void incendiarPor(double s){queimando=Math.max(queimando,adaptado(ComandoInimigo.CASCO_FOGO)?s*.3:s);}

    public Alien(int n, int n2) {
        this(n, n2, Tipo.PIRATA, Classe.INTERCEPTADOR);
    }

    public Alien(int n, int n2, Tipo tipo) {
        this(n, n2, tipo, tipo == Tipo.ALIENIGENA_ORIGINAL ? Classe.ORGANICO : Classe.INTERCEPTADOR);
    }

    public Alien(int n, int n2, Tipo tipo, Classe classe) {
        this.classe = tipo == Tipo.ALIENIGENA_ORIGINAL ? Classe.ORGANICO : classe;
        this.x = n;
        this.y = n2;
        this.alvoX = n;
        this.alvoY = n2;
        this.tipo = tipo;
        this.resistencia = this.classe == Classe.TANQUE ? 9 : (this.classe == Classe.KAMIKAZE ? 2 : (this.classe == Classe.LASER ? 4 : (tipo == Tipo.PIRATA ? 3 : 2)));
        this.variante = contador++ % (tipo == Tipo.ALIENIGENA_ORIGINAL ? 5 : 3);
        if (tipo == Tipo.ALIENIGENA_ORIGINAL && this.variante == 4) {
            this.resistencia = 6;
        }
        if (tipo == Tipo.ALIENIGENA_ORIGINAL && this.variante == 3) {
            this.rapidez = 1.3;
        }
        this.recarga = 1.1 + (double)(contador % 9) * 0.21;
    }

    public void definirSetor(int setor,Random r){universo=Math.max(0,Math.min(7,setor));if(tipo==Tipo.ALIENIGENA_ORIGINAL){variante=setor==0?r.nextInt(3):setor==2?3:setor==6?4:r.nextInt(5);if(variante==4)resistencia=6;}}
    public Item.Tipo getEquipamento(){return equipamento;}
    public void capturar(Item.Tipo t){equipamento=t;if(t==Item.Tipo.ESCUDO)escudoCapturado=3;else if(t==Item.Tipo.MELHORIA)evoluir();else {armaRoubada=1;recarga=.4;}}
    public void entrarEmEsquadrao(double d, double d2, int n) {
        this.emFormacao = true;
        this.posicionado = false;
        this.alvoX = d;
        this.alvoY = d2;
        this.esperaEntrada = (double)n * 0.19;
    }

    public void entrarEmEsquadrao(double d, double d2, int n, int n2, double d3) {
        this.entrarEmEsquadrao(d, d2, n);
        this.padrao = n2;
        this.rapidez = Math.max(1.0, d3) * (this.tipo == Tipo.ALIENIGENA_ORIGINAL && this.variante == 3 ? 1.3 : 1.0);
        this.faseMovimento = (double)n * 0.61 + (double)n2 * 0.43;
        this.esperaEntrada = (double)n * Math.max(0.07, 0.19 / d3);
    }

    public int getPadrao() {
        return this.padrao;
    }

    public Classe getClasse() {
        return this.classe;
    }

    public boolean evoluido() {
        return this.nivelEvolucao > 0;
    }

    public int getNivelEvolucao() {
        return this.nivelEvolucao;
    }

    public boolean ataqueCoordenado() {
        return this.classe == Classe.ESQUADRAO;
    }

    public boolean kamikazeArmado() {
        return this.ativouKamikaze;
    }

    public boolean podeCapturarMelhoria() {
        return this.visivel && !this.evoluido() && this.congelado <= 0.0 && this.classe != Classe.KAMIKAZE && this.classe != Classe.ORGANICO;
    }

    public void mirarMelhoria(int n, int n2) {
        if (!this.podeCapturarMelhoria() || !this.posicionado) {
            return;
        }
        if (Math.hypot((double)n - this.x, (double)n2 - this.y) < 190.0) {
            this.alvoColetaX = n;
            this.alvoColetaY = n2;
        }
    }

    public void evoluir() {
        if (!this.podeCapturarMelhoria()) {
            return;
        }
        this.nivelEvolucao = 1;
        this.resistencia += this.classe == Classe.TANQUE ? 7 : 4;
        this.rapidez += 0.27;
        this.recarga = 0.4;
    }

    public int estiloDisparo() {
        if(equipamento==Item.Tipo.ARMA_GELO)return 6;if(equipamento==Item.Tipo.ARMA_FOGO)return 7;if(equipamento==Item.Tipo.ARMA_2||armaRoubada>=0)return 3;
        if (this.classe == Classe.LASER) {
            return 4;
        }
        if (this.classe == Classe.TANQUE) {
            return 5;
        }
        if (this.classe == Classe.KAMIKAZE) {
            return 0;
        }
        if (this.classe == Classe.ORGANICO) {
            return 1;
        }
        if (this.nivelEvolucao > 0 || this.armaRoubada >= 0) {
            return 3;
        }
        return this.classe == Classe.ESQUADRAO ? 2 : 0;
    }

    public void mexer(double d, List<Missel> list, Random random) {
        double restante = Math.max(0, Math.min(.1, d));
        while (restante > 0.000001) {
            double passo = Math.min(1.0 / 120.0, restante);
            double antesX = x, antesY = y;
            moverPasso(passo, list, random);
            // Reciclagem so ocorre depois de sair inteiramente pela esquerda.
            if (antesX < 0 && x > 960) {
                velocidadeSuaveX = velocidadeSuaveY = 0;
            } else if (congelado > 0 || esperaEntrada > 0) {
                velocidadeSuaveX = velocidadeSuaveY = 0;
            } else {
                double fator = 1 - Math.exp(-14 * passo);
                velocidadeSuaveX += ((x - antesX) / passo - velocidadeSuaveX) * fator;
                velocidadeSuaveY += ((y - antesY) / passo - velocidadeSuaveY) * fator;
                x = antesX + velocidadeSuaveX * passo;
                y = antesY + velocidadeSuaveY * passo;
            }
            restante -= passo;
        }
    }

    private void moverPasso(double d, List<Missel> list, Random random) {
        if(queimando>0){queimando=Math.max(0,queimando-d);pulsoQueimadura+=d;if(pulsoQueimadura>=.5){pulsoQueimadura-=.5;resistencia=Math.max(0,resistencia-1);}}

        double d2;
        double d3;
        this.tempoAnimacao += d;tempoAtaqueEnxame=Math.max(0,tempoAtaqueEnxame-d);
        if (this.classe == Classe.KAMIKAZE && this.ativouKamikaze) {
            this.impulsoKamikaze += d;
            double d4 = Math.max(88.0, Math.min(533.0, this.alvoKamikazeY));
            this.y += (d4 - this.y) * Math.min(1.0, d * 7.5);
            this.x -= (165.0 + this.impulsoKamikaze * 290.0) * d;
            if (this.x < -64.0) {
                this.x=1040+random.nextInt(170);this.y=120+random.nextInt(390);this.alvoY=130+random.nextInt(360);this.alvoX=480+random.nextInt(330);
                this.ativouKamikaze = false;
                this.impulsoKamikaze = 0.0;
                this.posicionado = false;
                this.recarga = 1.2;
            }
            return;
        }
        if (this.congelado > 0.0) {
            this.congelado = Math.max(0.0, this.congelado - d);
            if (this.congelado == 0) enrolado = false;
            return;
        }
        if (passagem) {
            // atravessa reto para o outro lado, ondulando; quem sai pela esquerda vai embora
            x -= velPassagem * d; baseYPassagem += derivaPassagem * d;
            y = Math.max(80, Math.min(540, baseYPassagem + Math.sin(tempoAnimacao * freqPassagem + fasePassagem) * ampPassagem));
            if (x < -90) visivel = false;
            recarga -= d;
            return;
        }
        if (this.emFormacao) {
            if (this.esperaEntrada > 0.0) {
                this.esperaEntrada = Math.max(0.0, this.esperaEntrada - d);
                return;
            }
            if (!this.posicionado) {
                this.x = Math.max(this.alvoX, this.x - (double)(215 + (this.armaRoubada >= 0 ? 35 : 0)) * this.rapidez * d);
                this.y += (this.alvoY - this.y) * Math.min(1.0, d * 2.7);
                if (this.x <= this.alvoX + 2.0) {
                    this.posicionado = true;
                }
            } else {
                double d5 = this.tempoAnimacao + this.faseMovimento;
                d3 = Math.sin(d5 * (1.25 + (double)this.padrao * 0.11)) * (double)(9 + this.padrao * 2);
                d2 = this.tipo == Tipo.ALIENIGENA_ORIGINAL && this.variante == 3 ? -Math.max(0.0, Math.sin(d5 * 1.2)) * 165.0 : (this.padrao == 4 ? Math.max(0.0, Math.sin(d5 * 0.72)) * -100.0 : (this.padrao == 3 ? -Math.max(0.0, Math.sin(d5 * 0.64)) * 77.0 : (this.padrao == 6 ? -Math.max(0.0, Math.sin(d5 * 0.81)) * 95.0 : (this.padrao == 7 ? -Math.max(0.0, Math.sin(d5 * 0.95 + this.faseMovimento)) * 116.0 : 0.0))));
                double d6 = (this.tipo == Tipo.ALIENIGENA_ORIGINAL && this.variante == 4 ? 0.5 : 1.0) * Math.sin(d5 * (this.padrao >= 6 ? 2.1 : 1.45)) * (double)(this.padrao == 5 ? 17 : (this.padrao == 6 ? 23 : (this.padrao == 7 ? 19 : 8)));
                double d7 = Double.isNaN(this.alvoColetaX) ? this.alvoX + d3 + d2 : this.alvoColetaX;
                // ferido e treinado para recuar: volta para o fundo e atira de longe
                if (adaptado(ComandoInimigo.RECUO) && resistencia <= 1 && Double.isNaN(this.alvoColetaX)) d7 = Math.min(880, d7 + 150);
                double d8 = Double.isNaN(this.alvoColetaY) ? this.alvoY + d6 : this.alvoColetaY;
                this.x += (d7 - this.x) * Math.min(1.0, 2.2 * d);
                this.y += (d8 - this.y) * Math.min(1.0, 3.6 * d);
                this.alvoColetaX = Double.NaN;
                this.alvoColetaY = Double.NaN;
            }
        } else {
            this.x -= (double)(this.armaRoubada >= 0 ? 94 : 70) * d;
            if (this.x < -74.0) {
                this.x = 1040 + random.nextInt(980);
                this.y = 97 + random.nextInt(453);
            }
        }
        this.tempoDesvio = Math.max(0.0, this.tempoDesvio - d);
        this.descansoDesvio = Math.max(0.0, this.descansoDesvio - d);
        if ((this.classe == Classe.INTERCEPTADOR || adaptado(ComandoInimigo.DESVIO)) && this.descansoDesvio == 0.0 && this.tempoDesvio == 0.0 && this.x > 20.0 && this.x < 1030.0) {
            for (Missel missel : list) {
                if (!missel.isVisible()) continue;
                d3 = this.x - (double)missel.getX();
                d2 = this.y + 28.5 - (double)missel.getY();
                if (!(d3 > 5.0) || !(d3 < 155.0) || !(Math.abs(d2) < 35.0)) continue;
                double d9 = this.direcaoDesvio = d2 >= 0.0 ? 1.0 : -1.0;
                if (this.y <= 108.0) {
                    this.direcaoDesvio = 1.0;
                }
                if (this.y >= 530.0) {
                    this.direcaoDesvio = -1.0;
                }
                this.tempoDesvio = 0.3;
                this.descansoDesvio = 2.4;
                break;
            }
        }
        if (this.tempoDesvio > 0.0) {
            this.y += 120.0 * this.direcaoDesvio * d;
            this.y = Math.max(78.0, Math.min(538.0, this.y));
        }
        this.recarga -= d;
    }

    public void prepararKamikaze(int n) {
        if (this.classe != Classe.KAMIKAZE || this.ativouKamikaze) {
            return;
        }
        this.ativouKamikaze = true;animarAtaqueEnxame();
        this.alvoKamikazeY = n;
        this.recarga = 100.0;
    }

    public void verificarKamikaze(int n) {
        if (this.classe == Classe.KAMIKAZE && !this.ativouKamikaze && this.resistencia <= (this.nivelEvolucao > 0 ? 3 : 1) && this.estaNaFormacao()) {
            this.prepararKamikaze(n);
        }
    }

    public boolean podeAtirar(int n) {
        return this.visivel && this.congelado <= 0.0 && !this.ativouKamikaze && (!this.emFormacao || this.posicionado) && this.recarga <= 0.0 && this.x < 930.0 && this.x > (double)(n + 70);
    }

    public void resetarRecarga(Random random) {
        this.recarga = (this.nivelEvolucao > 0 ? 1.15 : (this.classe == Classe.TANQUE ? 3.0 : (this.classe == Classe.LASER ? 2.8 : 1.95))) + random.nextDouble() * 0.9;
    }

    public boolean roubarArma(int n) {
        if (this.armaRoubada >= 0) {
            return false;
        }
        this.armaRoubada = Math.max(0, Math.min(2, n));
        if (this.nivelEvolucao == 0 && this.classe != Classe.ORGANICO) {
            this.evoluir();
        }
        this.recarga = 0.5;
        return true;
    }

    public boolean receberImpacto(int n) {
        return this.receberImpacto(n, 1);
    }

    public boolean receberImpacto(int n, int n2) {
        if(escudoCapturado>0){--escudoCapturado;return false;}
        int n3;
        if (n == 1) {
            n3 = this.tipo == Tipo.PIRATA ? 0 : 3;
        } else if (n == 2) {
            n3 = this.tipo == Tipo.PIRATA ? 2 : 1;
            this.congelado = 2.8;
        } else {
            n3 = Math.max(1, n2);
        }
        this.resistencia = Math.max(0, this.resistencia - n3);
        return this.resistencia == 0;
    }

    public boolean estaCongelado() {
        return this.congelado > 0.0;
    }

    public int getResistencia() {
        return this.resistencia;
    }

    public int getArmaRoubada() {
        return this.armaRoubada;
    }

    public boolean estaArmado() {
        return this.armaRoubada >= 0;
    }

    public Tipo getTipoInimigo() {
        return this.tipo;
    }

    public boolean estaNaFormacao() {
        return this.emFormacao && this.posicionado;
    }

    public void desenhar(Graphics2D graphics2D) {
        if (this.emFormacao && this.esperaEntrada > 0.0) {
            return;
        }
        if(especieEnxame>=0){ArteEnxame.ataque(graphics2D,especieEnxame,(int)x,(int)y,tempoAnimacao,tempoAtaqueEnxame);}
        else if (universo > 0 && classe != Classe.INTERCEPTADOR && classe != Classe.ESQUADRAO) {
            InimigosUniversos.desenhar(graphics2D, universo, classe, (int)x, (int)y, tempoAnimacao, congelado > 0);
        } else if (this.tipo == Tipo.PIRATA) {
            Piratas094.desenhar(graphics2D,classe,(int)x,(int)y,evoluido(),tempoAnimacao);
        } else {
            ArteNaves.desenharOrganico(graphics2D, this.variante, (int)this.x, (int)this.y, this.tempoAnimacao, this.congelado > 0.0);
        }
        // casco adaptado: brilho fraco na cor da arma que ele resiste
        if(adaptado(ComandoInimigo.CASCO_FOGO)||adaptado(ComandoInimigo.CASCO_GELO)){int a=(int)(60+40*Math.sin(tempoAnimacao*5));graphics2D.setColor(adaptado(ComandoInimigo.CASCO_FOGO)?new Color(255,150,60,a):new Color(120,220,255,a));graphics2D.drawRoundRect((int)x-3,(int)y-3,66,62,18,18);}
        if(queimando>0){for(int f=0;f<5;f++){graphics2D.setColor(f%2==0?Color.ORANGE:Color.RED);int yy=(int)y+45-(int)((tempoAnimacao*35+f*9)%40);graphics2D.fillRect((int)x+8+f*9,yy,4,8);}}
        if(escudoCapturado>0){graphics2D.setColor(new Color(110,225,255,170));graphics2D.drawPolygon(new int[]{(int)x-5,(int)x+10,(int)x+55,(int)x+70,(int)x+55,(int)x+10},new int[]{(int)y+28,(int)y-5,(int)y-5,(int)y+28,(int)y+62,(int)y+62},6);}
        if (this.congelado > 0.0 && !enrolado) {
            graphics2D.setColor(new Color(92, 224, 255, 130));
            graphics2D.fillRoundRect((int)this.x, (int)this.y, 64, 57, 10, 10);
            graphics2D.setColor(new Color(205, 248, 255));
            graphics2D.drawRoundRect((int)this.x, (int)this.y, 64, 57, 10, 10);
        }
        if (this.ativouKamikaze && (int)(this.tempoAnimacao * 13.0) % 2 == 0) {
            graphics2D.setColor(new Color(255, 77, 41, 165));
            graphics2D.fillRect((int)this.x + 12, (int)this.y + 13, 20, 11);
        }
    }

    public int getX() {
        return (int)this.x;
    }

    public int getY() {
        return (int)this.y;
    }

    public boolean isVisible() {
        return this.visivel;
    }

    public void setVisible(boolean bl) {
        this.visivel = bl;
    }

    public Rectangle getBounds() {
        return new Rectangle(this.getX(), this.getY(), 64, 57);
    }

    public static enum Tipo {
        PIRATA,
        ALIENIGENA_ORIGINAL;

    }

    public static enum Classe {
        INTERCEPTADOR,
        ESQUADRAO,
        TANQUE,
        LASER,
        KAMIKAZE,
        ORGANICO;

    }
}

