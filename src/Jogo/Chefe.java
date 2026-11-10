package Jogo;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Random;

/** Boss em pixel art; a lula usa palpebras animadas e quatro membros articulados. */
public final class Chefe {
    private final int fase, largura, altura, vidaMaxima;
    private final ChefeMetal artista;
    /** Fase 9: setor Hellraiser (van, Ozzy + Lemmy, dragao). */
    private final ChefeHellraiser hell;
    /** Fase 10: luta final (Jack, Kyle, Deezleboss e Dio). */
    private final ChefeFinal fim;
    private final BufferedImage[] quadros;
    private int vida, ataques;
    private double x, y, tempo, entrada = 0, recarga = 1.1, avisoTiro, flashDano;

    // Investida surpresa com aviso visual e alvo travado: nunca teleporta no jogador.
    private enum EstadoInvestida { ESPERA, PREPARANDO, AVANCANDO, SEGURANDO, RECUANDO }
    private EstadoInvestida estadoInvestida = EstadoInvestida.ESPERA;
    private final Random sorteInvestida = new Random();
    private double relogioInvestida = 4.8;
    private double deslocamentoInvestida, deslocamentoY, alcanceInvestida, alvoInvestidaY;
    private double tempoOlho = 0, proximaPiscada = 3.2;
    private int quadroOlho = 0;
    private static final double AVISO_INVESTIDA = 0.88;
    private static final double DURACAO_AVANCO = 0.48;
    private static final double DURACAO_RECUO = 0.72;

    // Quatro tentaculos com estados independentes, alvos travados e colisao propria.
    private enum EstadoTentaculo { ESPERA, AVISO, ESTICANDO, SEGURANDO, RECOLHENDO }
    private static final class Tentaculo {
        EstadoTentaculo estado = EstadoTentaculo.ESPERA;
        double relogio, progresso, alvoX, alvoY, anteriorX, anteriorY;
        boolean acertou;
        final int indice;
        Tentaculo(int indice) { this.indice=indice; relogio = 1.9 + indice * 1.12; }
    }
    private final Tentaculo[] tentaculos = {
        new Tentaculo(0),new Tentaculo(1),new Tentaculo(2),new Tentaculo(3)
    };
    private int tentaculosLiberados = 1;
    private static final double AVISO_GOLPE = .95;
    private static final double TEMPO_ESTICAR = .42;
    private static final double TEMPO_SEGURAR = .16;
    private static final double TEMPO_RECOLHER = .37;

    /** Reacao ao golpe: o ponto fraco paralisa o chefe por um instante; outros golpes so marcam o corpo. */
    private double reacao, paralisia; private int tipoReacao = -1;
    private BufferedImage camada;
    void reagir(int poder, boolean critico) {
        tipoReacao = poder; reacao = critico ? 1.5 : .3;
        if (critico) paralisia = Math.max(paralisia, 1.5);
    }
    boolean paralisado() { return paralisia > 0; }

    public Chefe(int fase, Dificuldade dificuldade) {
        if (fase < 1 || fase > 10) throw new IllegalArgumentException("fase invalida");
        this.artista = fase <= 7 ? new ChefeMetal(fase, dificuldade) : null;
        this.hell = fase == 9 ? new ChefeHellraiser(dificuldade) : null;
        this.fim = fase == 10 ? new ChefeFinal(dificuldade) : null;
        this.fase = fase;
        this.largura = fase <= 7 ? 412 : 420;
        this.altura = fase <= 7 ? 385 : 426;
        this.vidaMaxima = (int) Math.round((fase == 1 ? 135 : fase == 2 ? 190 : fase == 3 ? 215 : fase == 8 ? 360 : 180 + fase * 12) * dificuldade.resistenciaChefe);
        this.vida = vidaMaxima;
        this.quadros = new BufferedImage[0];
        this.x = Config.LARGURA + 40;
        this.y = Config.HUD + 25;
    }

    public void atualizar(double dt, int naveX, int naveY, Dificuldade dificuldade, List<TiroChefe> tiros) {
        reacao = Math.max(0, reacao - Math.max(0, dt));
        // Paralisado: nao se move nem ataca, mas continua vulneravel aos tiros do jogador.
        if (paralisia > 0) { paralisia = Math.max(0, paralisia - Math.max(0, dt)); return; }
        if (artista != null) {
            artista.atualizar(dt, naveX, naveY, dificuldade, tiros);
            return;
        }
        if (hell != null) { hell.atualizar(dt, naveX, naveY, dificuldade, tiros); return; }
        if (fim != null) { fim.atualizar(dt, naveX, naveY, dificuldade, tiros); return; }
        if (dt <= 0) return;
        tempo += dt;
        if (fase == 8) atualizarPiscada(dt);
        flashDano = Math.max(0, flashDano - dt);
        avisoTiro = Math.max(0, avisoTiro - dt);
        entrada = Math.min(1, entrada + dt / 1.25);
        double chegada = 1 - Math.pow(1 - entrada, 3);
        x = (Config.LARGURA + 40) * (1 - chegada)
                + (Config.LARGURA - largura - 114) * chegada;
        y = Config.HUD + (Config.ALTURA - Config.HUD - altura) / 2.0
                + Math.sin(tempo * (fase == 1 ? 0.8 : 1.12)) * 24;
        if (entrada < 1) return;
        if (fase == 8) {
            atualizarInvestida(dt, naveX + 20, naveY + 17);
            x -= deslocamentoInvestida;
            y += deslocamentoY;
            atualizarTentaculos(dt, naveX + 20, naveY + 17);
        }
        recarga -= dt;
        if (recarga > 0 && recarga < 0.56) avisoTiro = Math.max(avisoTiro, recarga);
        if ((fase == 8 && estadoInvestida != EstadoInvestida.ESPERA)
                || recarga > 0 || tiros.size() >= 45) return;
        disparar(naveX + 23, naveY + 17, dificuldade, tiros);
        ataques++;
        double furia = vida <= vidaMaxima * 0.33 ? 0.74 : vida <= vidaMaxima * 0.65 ? 0.88 : 1.0;
        recarga = (fase == 1 ? 2.25 : 1.85) * furia / dificuldade.cadenciaInimigos;
    }

    /** Uma piscada curta com intervalo natural; o olho fica aberto entre piscadas. */
    private void atualizarPiscada(double dt) {
        tempoOlho += dt;
        if (tempoOlho < proximaPiscada) {
            quadroOlho = 0;
            return;
        }
        int quadro = (int) ((tempoOlho - proximaPiscada) / .065);
        if (quadro >= 12) {
            tempoOlho = 0;
            quadroOlho = 0;
            proximaPiscada = 2.9 + sorteInvestida.nextDouble() * 2.3;
        } else {
            quadroOlho = Math.max(0, quadro);
        }
    }

    /** Investidas eventuais, sem perseguir a nave durante o ataque. */
    private void atualizarInvestida(double dt, double naveX, double naveY) {
        relogioInvestida -= dt;
        switch (estadoInvestida) {
            case ESPERA:
                deslocamentoInvestida = 0;
                deslocamentoY = 0;
                if (relogioInvestida <= 0) {
                    estadoInvestida = EstadoInvestida.PREPARANDO;
                    relogioInvestida = AVISO_INVESTIDA;
                    // Aproxima de repente, mas deixa espaco para fugir: a nave escolhe a rota.
                    alcanceInvestida = 145 + sorteInvestida.nextInt(86);
                    alvoInvestidaY = Math.max(-42, Math.min(42,
                            (naveY - (y + altura / 2.0)) * .38));
                }
                break;
            case PREPARANDO:
                if (relogioInvestida <= 0) {
                    estadoInvestida = EstadoInvestida.AVANCANDO;
                    relogioInvestida = DURACAO_AVANCO;
                }
                break;
            case AVANCANDO: {
                double t = suave(1 - Math.max(0, relogioInvestida) / DURACAO_AVANCO);
                deslocamentoInvestida = alcanceInvestida * t;
                deslocamentoY = alvoInvestidaY * t;
                if (relogioInvestida <= 0) {
                    estadoInvestida = EstadoInvestida.SEGURANDO;
                    relogioInvestida = .18;
                }
                break;
            }
            case SEGURANDO:
                deslocamentoInvestida = alcanceInvestida;
                deslocamentoY = alvoInvestidaY;
                if (relogioInvestida <= 0) {
                    estadoInvestida = EstadoInvestida.RECUANDO;
                    relogioInvestida = DURACAO_RECUO;
                }
                break;
            case RECUANDO: {
                double t = suave(Math.max(0, relogioInvestida) / DURACAO_RECUO);
                deslocamentoInvestida = alcanceInvestida * t;
                deslocamentoY = alvoInvestidaY * t;
                if (relogioInvestida <= 0) {
                    estadoInvestida = EstadoInvestida.ESPERA;
                    deslocamentoInvestida = 0;
                    deslocamentoY = 0;
                    relogioInvestida = (vida < vidaMaxima*.35 ? 3.5 : 5.0)
                            + sorteInvestida.nextDouble()*2.6;
                }
                break;
            }
            default: break;
        }
    }
    String getEstadoInvestidaTeste() { return estadoInvestida.name(); }
    int getQuadroOlhoTeste() { return quadroOlho; }

    // Os quatro membros nascem das juntas da carapaca e permanecem visiveis,
    // mesmo antes de serem liberados como armas. Isso evita uma criatura "pela metade".
    private double raizTentaculoX(Tentaculo t) { return x + (t.indice%2==0 ? 135 : 325); }
    private double raizTentaculoY(Tentaculo t) { return y + (t.indice<2 ? 147 : 291); }
    private double repousoX(Tentaculo t) {
        return x + new double[]{-70,430,-180,400}[t.indice] + Math.sin(tempo*1.25+t.indice)*17;
    }
    private double repousoY(Tentaculo t) {
        return y + new double[]{155,100,270,345}[t.indice] + Math.sin(tempo*1.7+t.indice)*18;
    }
    private double pontaX(Tentaculo t) {
        return repousoX(t) + (t.alvoX-repousoX(t))*t.progresso;
    }
    private double pontaY(Tentaculo t) {
        return repousoY(t) + (t.alvoY-repousoY(t))*t.progresso;
    }
    private static double suave(double t) {
        t = Math.max(0,Math.min(1,t)); return t*t*(3-2*t);
    }
    private void atualizarTentaculos(double dt, double jogadorX, double jogadorY) {
        // Um tentaculo no inicio, ate quatro na fase de furia.
        tentaculosLiberados = vida > vidaMaxima * .78 ? 1 : vida > vidaMaxima * .54 ? 2
                : vida > vidaMaxima * .29 ? 3 : 4;
        for (Tentaculo t : tentaculos) {
            if (t.indice >= tentaculosLiberados) continue;
            t.anteriorX = pontaX(t); t.anteriorY = pontaY(t);
            t.relogio -= dt;
            switch (t.estado) {
                case ESPERA:
                    if (t.relogio <= 0) {
                        t.estado=EstadoTentaculo.AVISO;
                        t.relogio=AVISO_GOLPE + t.indice * .13;
                        // Alterna altura do alvo, sem perseguir depois do aviso.
                        t.alvoX = Math.max(38, Math.min(Config.LARGURA-65,jogadorX));
                        t.alvoY = Math.max(Config.HUD+76,Math.min(Config.ALTURA-35,
                                jogadorY + (t.indice%2==0 ? -17 : 17)));
                        t.progresso=0; t.acertou=false;
                    }
                    break;
                case AVISO:
                    if (t.relogio <= 0) { t.estado=EstadoTentaculo.ESTICANDO;
                        t.relogio=TEMPO_ESTICAR; }
                    break;
                case ESTICANDO:
                    t.progresso=suave(1-t.relogio/TEMPO_ESTICAR);
                    if (t.relogio <= 0) { t.progresso=1;
                        t.estado=EstadoTentaculo.SEGURANDO; t.relogio=TEMPO_SEGURAR; }
                    break;
                case SEGURANDO:
                    if (t.relogio <= 0) { t.estado=EstadoTentaculo.RECOLHENDO;
                        t.relogio=TEMPO_RECOLHER; }
                    break;
                case RECOLHENDO:
                    t.progresso=suave(t.relogio/TEMPO_RECOLHER);
                    if (t.relogio <= 0) { t.progresso=0;
                        t.estado=EstadoTentaculo.ESPERA;
                        t.relogio=tentaculosLiberados==4? .85+t.indice*.34 : 1.65+t.indice*.48; }
                    break;
                default: break;
            }
        }
    }
    /** Hitbox de cada ponta; no maximo um dano por tentaculo em cada investida. */
    public boolean consumirAcertoTentaculo(Rectangle nave) {
        if (artista != null) return artista.consumirColisaoCorporal(nave);
        if (hell != null || fim != null) return false;
        if (fase != 8 || !estaVivo() || entrada < 1 || nave == null) return false;
        Rectangle2D.Double area = new Rectangle2D.Double(nave.x-13,nave.y-13,
                nave.width+26,nave.height+26);
        for (Tentaculo t : tentaculos) {
            if (t.indice>=tentaculosLiberados || t.acertou) continue;
            if (t.estado!=EstadoTentaculo.ESTICANDO && t.estado!=EstadoTentaculo.SEGURANDO) continue;
            if (area.contains(pontaX(t),pontaY(t)) || area.intersectsLine(
                    t.anteriorX,t.anteriorY,pontaX(t),pontaY(t))) {
                t.acertou=true; return true;
            }
        }
        return false;
    }
    private void desenharAtaqueTentaculo(Graphics2D canvas) {
        for(Tentaculo t:tentaculos) {
            double rx=raizTentaculoX(t)-x,ry=raizTentaculoY(t)-y;
            double tx=pontaX(t)-x,ty=pontaY(t)-y;
            double enrolar=1-t.progresso;
            double aviso=t.estado==EstadoTentaculo.AVISO ? suave(1-t.relogio/(AVISO_GOLPE+t.indice*.13)) : 0;
            double[][] cx={{135,15,-15,-130,-245,-150,-70},
                           {325,390,450,370,300,525,430},
                           {135,-25,-70,-85,-100,-210,-180},
                           {325,440,400,485,570,455,400}};
            double[][] cy={{147,130,-55,5,70,245,155},
                           {147,125,60,0,-65,0,100},
                           {291,260,220,310,400,320,270},
                           {291,315,450,410,370,280,345}};
            double[] bx=new double[7],by=new double[7];
            for(int j=0;j<7;j++) {
                double f=j/6.0;
                double oscilacao=Math.sin(tempo*1.7+t.indice+j*.4)*10*f;
                double ex=rx+(cx[t.indice][j]-rx)*(1-aviso*.13);
                double ey=ry+(cy[t.indice][j]-ry)*(1-aviso*.13)+oscilacao;
                bx[j]=ex*enrolar+(rx+(tx-rx)*f)*(1-enrolar);
                by[j]=ey*enrolar+(ry+(ty-ry)*f)*(1-enrolar);
            }
            bx[0]=rx;by[0]=ry;bx[6]=tx;by[6]=ty;
            double[][] px={{bx[0],bx[1],bx[2],bx[3]},{bx[3],bx[4],bx[5],bx[6]}};
            double[][] py={{by[0],by[1],by[2],by[3]},{by[3],by[4],by[5],by[6]}};
            // Distribui elos por comprimento de arco para evitar aglomerados nas curvas.
            java.util.ArrayList<double[]> pontos=new java.util.ArrayList<>();
            double ax=rx,ay=ry,restante=0;
            pontos.add(new double[]{rx,ry});
            for(int j=0;j<2;j++)for(int n=1;n<=90;n++){
                double p=n/90.0,q=1-p;
                double sx=q*q*q*px[j][0]+3*q*q*p*px[j][1]+3*q*p*p*px[j][2]+p*p*p*px[j][3];
                double sy=q*q*q*py[j][0]+3*q*q*p*py[j][1]+3*q*p*p*py[j][2]+p*p*p*py[j][3];
                restante+=Math.hypot(sx-ax,sy-ay);ax=sx;ay=sy;
                if(restante>=15){pontos.add(new double[]{sx,sy});restante=0;}
            }
            pontos.add(new double[]{tx,ty});
            for(int i=0;i<pontos.size();i++){
                double[] p=pontos.get(i),anterior=pontos.get(Math.max(0,i-1));
                double w=5+58*Math.sqrt(1-i/(double)Math.max(1,pontos.size()-1));
                LulaVisual.elo(canvas,p[0],p[1],Math.atan2(p[1]-anterior[1],p[0]-anterior[0]),w);
            }
        }
    }
    String getEstadoTentaculoTeste() { return tentaculos[0].estado.name(); }
    int getTentaculosAtivosTeste() { return tentaculosLiberados; }

    private void disparar(double alvoX, double alvoY, Dificuldade dificuldade, List<TiroChefe> tiros) {
        boolean furioso = vida <= vidaMaxima * 0.35;
        double velocidade = (fase == 1 ? 215 : 242) * dificuldade.velocidadeInimigos;
        int quantidade = fase == 1 ? 5 : fase == 2 ? 7 : 9;
        if (furioso) quantidade += 2;
        double abertura = fase == 1 ? 0.20 : fase == 2 ? 0.16 : 0.14;
        double origemX = x + 230;
        double origemY = y + 233;
        if (ataques % 4 == 3) {
            // Rajada direcionada: o leque acompanha a altura da nave, mas nao a perseguira depois.
            double angulo = Math.atan2(alvoY - origemY, alvoX - origemX);
            for (int i = 0; i < 3; i++) {
                double a = angulo + (i - 1) * 0.17;
                tiros.add(new TiroChefe(origemX, origemY, Math.cos(a) * velocidade,
                        Math.sin(a) * velocidade, i == 1, 8));
            }
        } else {
            // Leque aberto que se espalha para a esquerda, com espacos para esquivar.
            for (int i = 0; i < quantidade; i++) {
                double a = Math.PI + (i - (quantidade - 1) / 2.0) * abertura;
                tiros.add(new TiroChefe(origemX, origemY,
                        Math.cos(a) * velocidade, Math.sin(a) * velocidade, false, 8));
            }
        }
    }

    public boolean receberDano(int dano) {
        if (artista != null) return artista.receberDano(dano);
        if (hell != null) return hell.receberDano(dano);
        if (fim != null) return fim.receberDano(dano);
        if (vida <= 0) return false;
        vida = Math.max(0, vida - Math.max(0, dano));
        flashDano = 0.15;
        return vida == 0;
    }

    public void desenhar(Graphics2D g) {
        if (reacao <= 0 || tipoReacao < 0) { desenharCorpo(g); return; }
        if (camada == null) camada = new BufferedImage(Config.LARGURA, Config.ALTURA, BufferedImage.TYPE_INT_ARGB);
        Graphics2D c = camada.createGraphics();
        c.setComposite(java.awt.AlphaComposite.Clear); c.fillRect(0, 0, Config.LARGURA, Config.ALTURA);
        c.setComposite(java.awt.AlphaComposite.SrcOver);
        desenharCorpo(c);
        // Tinge so a silhueta do chefe com a cor do golpe.
        Color cor = Poderes099.CORES_SIMBOLO[tipoReacao];
        float forca = (float) Math.min(.62, reacao * (paralisia > 0 ? .5 : 1.6));
        c.setComposite(java.awt.AlphaComposite.SrcAtop.derive(forca));
        c.setColor(cor); c.fillRect(0, 0, Config.LARGURA, Config.ALTURA);
        c.dispose();
        Graphics2D a = (Graphics2D) g.create();
        int tremor = tipoReacao == 4 || tipoReacao == 7 ? (int) (Math.sin(reacao * 90) * 4) : 0;
        a.drawImage(camada, tremor, 0, null);
        if (paralisia > 0) efeitoParalisia(a, getBounds(), reacao);
        a.dispose();
    }

    /** Detalhe de cada estado: cristais de gelo, chamas, faiscas, cristais verdes ou estrelas de atordoamento. */
    private void efeitoParalisia(Graphics2D g, Rectangle b, double t) {
        java.util.Random r = new java.util.Random(77);
        for (int i = 0; i < 14; i++) {
            int px = b.x + r.nextInt(Math.max(1, b.width)), py = b.y + r.nextInt(Math.max(1, b.height));
            switch (tipoReacao) {
                case 0: g.setColor(new Color(200, 240, 255, 210)); g.fillPolygon(new int[]{px, px + 6, px, px - 6}, new int[]{py - 12, py, py + 12, py}, 4); break;
                case 5: g.setColor(i % 2 == 0 ? new Color(255, 170, 40) : new Color(255, 60, 30)); int h = 8 + (int) ((t * 40 + i * 7) % 16); g.fillPolygon(new int[]{px - 5, px, px + 5}, new int[]{py, py - h, py}, 3); break;
                case 4: g.setColor(new Color(170, 210, 255)); g.drawPolyline(new int[]{px, px + 6, px - 2, px + 8}, new int[]{py, py + 6 + (int) (Math.sin(t * 50 + i) * 3), py + 10, py + 18}, 4); break;
                case 6: g.setColor(new Color(110, 255, 140, 200)); g.drawPolygon(new int[]{px, px + 7, px, px - 7}, new int[]{py - 10, py, py + 10, py}, 4); break;
                default: if (i < 5) { double a = t * 6 + i * 1.256; int sx = (int) b.getCenterX() + (int) (Math.cos(a) * 60), sy = b.y + 20 + (int) (Math.sin(a) * 14); g.setColor(new Color(255, 235, 110)); g.fillPolygon(new int[]{sx, sx + 3, sx + 9, sx + 3, sx, sx - 3, sx - 9, sx - 3}, new int[]{sy - 9, sy - 3, sy, sy + 3, sy + 9, sy + 3, sy, sy - 3}, 8); } break;
            }
        }
    }

    private void desenharCorpo(Graphics2D g) {
        if(artista!=null){artista.desenhar(g);return;}
        if(hell!=null){hell.desenhar(g);return;}
        if(fim!=null){fim.desenhar(g);return;}
        Graphics2D a=(Graphics2D)g.create();
        a.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        a.translate((int)x,(int)y);
        desenharAtaqueTentaculo(a);
        LulaVisual.corpo(a,quadroOlho,avisoTiro>0?1-avisoTiro/.56:0,flashDano);
        a.dispose();
    }

    public void desenharVida(Graphics2D g) {
        if(artista!=null){artista.desenharVida(g);return;}
        if(hell!=null){hell.desenharVida(g);return;}
        if(fim!=null){fim.desenharVida(g);return;}
        HudMetal.chefe(g,getNome(),vida,vidaMaxima);
    }

    public Rectangle getBounds() {
        if (artista != null) return artista.getBounds();
        if (hell != null) return hell.getBounds();
        if (fim != null) return fim.getBounds();
        return new Rectangle((int) x + 118, (int) y + 50, 238, 320);
    }
    public boolean estaVivo() { return artista != null ? artista.estaVivo() : hell != null ? hell.estaVivo() : fim != null ? fim.estaVivo() : vida > 0; }
    public int getVida() { return artista != null ? artista.getVida() : hell != null ? hell.getVida() : fim != null ? fim.getVida() : vida; }
    public int getVidaMaxima() { return artista != null ? artista.getVidaMaxima() : hell != null ? hell.getVidaMaxima() : fim != null ? fim.getVidaMaxima() : vidaMaxima; }
    public int getFase() { return fase; }
    public int getX() { return artista != null ? artista.getX() : hell != null ? hell.getX() : fim != null ? fim.getX() : (int) x; }
    public int getY() { return artista != null ? artista.getY() : hell != null ? hell.getY() : fim != null ? fim.getY() : (int) y; }
    public String getNome() { return artista != null ? artista.getNome() : hell != null ? hell.getNome() : fim != null ? fim.getNome() : "LULA DO ABISMO"; }
    /** Acerto de um tiro: no Hellraiser cada corpo da dupla responde por si. */
    boolean colide(Rectangle r) { return hell != null ? hell.colide(r) : fim != null ? fim.colide(r) : artista != null ? artista.colide(r) : r.intersects(getBounds()); }
    /** Encosto da nave no corpo do chefe. */
    boolean tocaNave(Rectangle r) { return hell != null ? hell.tocaNave(r) : fim != null ? fim.tocaNave(r) : r.intersects(getBounds()); }
    /** Cutscene pedida no meio da luta (Ozzy virando Principe, dragao). */
    String consumirCena() { return artista != null ? artista.consumirCena() : hell != null ? hell.consumirCena() : null; }
    /** Ponto para onde o gancho da van esta arrastando a nave (null se ninguem esta puxando). */
    double[] puxaoNave() { return hell != null ? hell.puxaoNave() : fim != null ? fim.puxaoNave() : null; }
    /** Aviso de meio de luta (dente arrancado, Deezleboss invocado, Dio revelado). */
    String consumirAviso() { return fim != null ? fim.consumirAviso() : artista != null ? artista.consumirAviso() : hell != null ? hell.consumirAviso() : null; }
    int cinema() { return artista != null ? artista.cinema() : 0; }
    int getAtoFinalTeste() { return fim == null ? 0 : fim.ato(); }
    int getDentesTeste() { return fim == null ? 0 : fim.dentes(); }
    int consumirDanoGancho() { return hell != null ? hell.consumirDanoGancho() : fim != null ? fim.consumirDanoGancho() : 0; }
    boolean naveAgarrada() { return hell != null && hell.naveAgarrada() || fim != null && fim.naveAgarrada(); }
    int getAtoHellraiserTeste() { return hell == null ? 0 : hell.ato(); }
    int getTotalQuadros() { return quadros.length; }
    int getFormaMetalTeste() { return artista == null ? 0 : artista.getForma(); }
    double getEntrada() { return artista != null ? artista.getEntrada() : hell != null ? hell.getEntrada() : fim != null ? fim.getEntrada() : entrada; }
}
