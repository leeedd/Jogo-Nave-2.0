/*
 * Decompiled with CFR 0.152.
 */
package Jogo;























import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RectangularShape;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;
import javax.swing.JPanel;
import javax.swing.Timer;

public class Fase
extends JPanel
implements ActionListener {
    private static final long serialVersionUID = 1L;
    private static final int TOTAL_INIMIGOS = 330;
    private static final int PONTOS_POR_INIMIGO = 100;
    private static final int MAX_PARTICULAS = 600;
    private static final int[] FINAIS_SETORES = new int[]{56, 120, 192, 272, 360, 456, 560, 672};
    private static final int TOTAL_SETORES = FINAIS_SETORES.length;
    private static final int[] ORDEM_CHEFOES = new int[]{8, 2, 3, 4, 5, 6, 7, 1};
    private static final String[] NOMES_SETORES = new String[]{"LULA DO ABISMO", "JUDAS PRIEST", "IRON MAIDEN", "MOTORHEAD", "METALLICA", "SLAYER", "TESTAMENT", "OZZY OSBOURNE"};
    private static final int TAMANHO_ESQUADRAO = 5;
    private static final int MODO_X = 818;
    private static final int MODO_Y = 49;
    private static final int LARGURA_BOTAO_MODO = 40;
    private static final int ESPACO_BOTAO_MODO = 4;
    private static final double DURACAO_ALERTA = 4.8;
    private static final double DURACAO_INTERLUDIO = 3.0;
    private Etapa etapa = Etapa.COMBATE;
    private double tempoEtapa;
    private int chefePendente;
    private EntradaChefe apresentacao;
    private static final double DURACAO_NUCLEAR = 0.72;
    private static final double DURACAO_TREMOR = 0.42;
    private static final double DURACAO_CLARAO = 0.26;
    private FundoEspacial fundoEspacial = new FundoEspacial();
    private final Image gameOver = Assets.imagem("game_over.jpg");
    private final Random aleatorio = new Random();
    private final Timer timer;
    private Nave nave;
    private List<Alien> alien;
    private int lancadosNoSetor;
    private int numeroEsquadrao;
    private double esperaProximoEsquadrao;
    private double esperaRajada;
    private double avisoMini = -1.0;
    private int proximaFormacao;
    private final List<Integer> sacoFormacoes = new ArrayList<Integer>();
    private static final String[] FORMACOES = new String[]{"FILEIRA", "V", "DUPLA", "DIAGONAL", "ESPIRAL", "ARCO", "SERPENTE", "TENAZ"};
    private static final int[] TAMANHOS = new int[]{6, 7, 6, 8, 7, 6, 8, 7};
    private int indiceRajada;
    private final List<ProjetilInimigo> tirosInimigos = new ArrayList<ProjetilInimigo>();
    private final List<Item> itens = new ArrayList<Item>();
    private final List<DisparoElemental> tirosElementais = new ArrayList<DisparoElemental>();
    private boolean geloColetado;
    private boolean fogoColetado;
    private double recargaElemental;
    private final List<TiroChefe> tirosChefes = new ArrayList<TiroChefe>();
    private final List<Asteroide> asteroides = new ArrayList<Asteroide>();
    private boolean emboscadaUsada, emboscadaPendente, intercepcao;
    private double tempoInterdicao, saidaIntercepcao;
    private double esperaEnxame;
    private int reforcosEnxame, ultimaFormacaoEnxame=-1;
    private boolean rainhaInterdicao=true;
    private int eliminadosAntesIntercepcao;
    private boolean painelTeste, partidaTeste, pausaAntesTeste;
    private int setorTeste = 1;
    private final java.util.Set<Integer> teclasTeste = new java.util.HashSet<Integer>();
    private MiniChefe miniChefe;
    private boolean miniChefeEnfrentado;
    private int totalMiniChefesDerrotados;
    private double proximoAsteroide = 8.0;
    private double tempoOndaEscudo;
    private double ondaX;
    private double ondaY;
    private static final double RAIO_ONDA_ESCUDO = 190.0;
    private Chefe chefe;
    private CenaFinal cenaFinal;
    private CinematicaNave cenaNave;
    private double tempoSetor;
    private double duracaoMusicaSetor = 180.0;
    private int secaoMusicalAnterior = -1;
    private int chefesDerrotados;
    /** Setor em que a nave esta (0..7) e setores ja vencidos; a ordem e escolhida no Mapa Estelar. */
    private int setorAtual, setoresVencidos;
    private boolean escolhendoSetor;
    /** Poder (indice de Poderes099) que e o ponto fraco de cada chefe, pelo id do chefe. */
    static final int[] FRAQUEZA = {-1, 3, 4, 1, 2, 6, 0, 5, 7};
    private double tempoCritico;
    private boolean oculosOzzyColetados;
    private boolean emblemaHalfordColetado;
    private boolean insigniaEddieColetada;
    private final boolean[] trofeusOutrasLendas = new boolean[4];
    private final boolean[] trofeusAdquiridos = new boolean[9];
    private int trofeuPendente;
    private boolean novoJogoMais;
    private boolean usarLendaria;
    private int modeloVisual;
    private Dificuldade dificuldade = Dificuldade.VETERANO;
    private final List<DerrotaEnxame> derrotasEnxame=new ArrayList<DerrotaEnxame>();
    private final List<ExplosaoCurta> explosoes = new ArrayList<ExplosaoCurta>();
    private final List<Particula> particulas = new ArrayList<Particula>();
    private boolean emJogo = true;
    private boolean venceu = false;
    private boolean espacoPressionado;
    private int pontos;
    private int creditos, kitsReparo, meteorosGerados;
    private ProgressaoNave inventario = new ProgressaoNave();
    private Energia098 energia = new Energia098();
    private Poderes099 poderes = new Poderes099();
    private int cursorPausa;
    private Segredo098 segredo;
    private boolean segredoGerado;
    private int produtoLoja;
    private boolean oficinaAberta;
    private Fusao098 efeitoFusao;
    private double recargaFusao;
    private boolean ativarFusao(){
        if(!emJogo||pausado||!(etapa==Etapa.COMBATE||etapa==Etapa.BATALHA_CHEFE)||recargaFusao>0)return false;
        if(!inventario.retencao()||!inventario.reator()||!geloColetado||!fogoColetado||energia.carga(0)<40||energia.carga(1)<40){mostrarAviso("FUSAO: REATOR + RETENCAO + GELO E FOGO 40%");return false;}
        SomJogo.tocar(SomJogo.Efeito.FUSAO);energia.gastar(0,40);energia.gastar(1,40);recargaFusao=30;
        efeitoFusao=new Fusao098(0,nave.getX()+25,nave.getY()+18);
        for(Alien a:alien)if(a.isVisible()&&efeitoFusao.alcanca(a.getBounds())){a.incendiarPor(3);if(a.receberImpacto(0,14))eliminarAlien(a,true);}
        for(ProjetilInimigo t:tirosInimigos)if(t.isVisible()&&efeitoFusao.alcanca(t.getBounds()))t.setVisible(false);
        for(TiroChefe t:tirosChefes)if(t.isVisible()&&efeitoFusao.alcanca(t.getBounds()))t.setVisible(false);
        if(miniChefe!=null&&efeitoFusao.alcanca(miniChefe.getBounds())&&miniChefe.receberDano(14))derrotarMiniChefe();
        if(chefe!=null&&efeitoFusao.alcanca(chefe.getBounds())&&chefe.receberDano(14))derrotarChefao();
        mostrarAviso("CHOQUE TERMICO!");return true;
    }
    private void aplicarInventario(){
        nave.aplicarBlindagem(inventario.blindagem());
        int m=nave.getModeloVisual();
        nave.aplicarPecas(inventario.peca(m,0),inventario.peca(m,1),inventario.peca(m,2));
        if(inventario.retencao()){geloColetado=inventario.gelo()>0;fogoColetado=inventario.fogo()>0;}
    }
    private void usarPoderDoChefe(){
        if(etapa!=Etapa.COMBATE && etapa!=Etapa.BATALHA_CHEFE)return;
        if(poderes.cloneAtivo()){poderes.trocarComClone(nave);return;}
        // Sem aviso quando nao ha energia: o jogador confere na pausa ou na barra do HUD.
        if(poderes.ativar(inventario,nave))mostrarAviso("PODER: "+Poderes099.NOMES[poderes.selecionado]);
        else SomJogo.tocar(SomJogo.Efeito.ERRO);
    }
    /** Capsulas de energia dos poderes: {x, y, valor, idade}. Caem dos inimigos e somem apos 9 s. */
    private final List<double[]> capsulas = new ArrayList<double[]>();
    private void soltarCapsula(int x, int y, boolean garantida, boolean grande) {
        boolean temPoder = false; for (int i = 0; i < 8; i++) temPoder |= inventario.temChaveIndice(i);
        if (!temPoder) return;
        if (!garantida && aleatorio.nextDouble() > .16) return;
        capsulas.add(new double[]{x, y, grande || aleatorio.nextDouble() < .12 ? 50 : 20, 0});
    }
    private void atualizarCapsulas(double d) {
        Rectangle pega = nave.getBounds(); pega.grow(10, 10);
        for (double[] c : capsulas) {
            c[0] -= 45 * d; c[3] += d;
            if (pega.contains(c[0], c[1])) {
                poderes.recarregar(inventario, (int) c[2]); SomJogo.tocar(SomJogo.Efeito.ENERGIA);
                gerarParticulas((int) c[0], (int) c[1], 8); c[3] = 99;
            }
        }
        capsulas.removeIf(c -> c[3] > 9 || c[0] < -20);
    }
    private void desenharCapsulas(Graphics2D g) {
        Color cor = Poderes099.CORES_SIMBOLO[poderes.selecionado];
        for (double[] c : capsulas) {
            if (c[3] > 7 && (int) (c[3] * 10) % 2 == 0) continue; // pisca antes de sumir
            boolean grande = c[2] >= 50; int w = grande ? 26 : 18, h = grande ? 14 : 10;
            int x = (int) c[0] - w / 2, y = (int) (c[1] + Math.sin(c[3] * 4) * 3) - h / 2;
            g.setColor(new Color(cor.getRed(), cor.getGreen(), cor.getBlue(), 70)); g.fillOval(x - 6, y - 6, w + 12, h + 12);
            g.setColor(new Color(30, 34, 48)); g.fillRoundRect(x - 1, y - 1, w + 2, h + 2, h, h);
            g.setColor(cor); g.fillRoundRect(x, y, w / 2 + h / 2, h, h, h);
            g.setColor(new Color(235, 240, 250)); g.fillRoundRect(x + w / 2 - h / 2, y, w / 2 + h / 2, h, h, h); g.setColor(cor); g.fillRect(x + h / 2, y, w / 2 - h / 2, h);
            g.setColor(new Color(255, 255, 255, 200)); g.fillRect(x + 3, y + 2, w - 6, 2);
        }
    }
    private void atualizarPoderes(double d){
        atualizarCapsulas(d);
        if(poderes.ativo>=0||poderes.pendente>=0){java.util.List<double[]> alvos=new java.util.ArrayList<>();
            for(Alien a:alien)if(a.isVisible())alvos.add(new double[]{a.getBounds().getCenterX(),a.getBounds().getCenterY()});
            if(miniChefe!=null)alvos.add(new double[]{miniChefe.getBounds().getCenterX(),miniChefe.getBounds().getCenterY()});
            if(chefe!=null)alvos.add(new double[]{chefe.getBounds().getCenterX(),chefe.getBounds().getCenterY()});
            poderes.mirar(alvos,nave);}
        poderes.atualizar(d,nave);
        if(poderes.ativo<0)return;
        for(ProjetilInimigo t:tirosInimigos)if(t.isVisible()&&poderes.interceptar(t.getBounds()))t.setVisible(false);
        for(TiroChefe t:tirosChefes)if(t.isVisible()&&poderes.interceptar(t.getBounds()))t.setVisible(false);
        if(!poderes.pulso())return;
        Rectangle area=poderes.alcance();int dano=poderes.dano();
        for(Alien a:alien)if(a.isVisible()&&poderes.atinge(a.getBounds())){
            if(poderes.ativo==0)a.agarrarPor(.6);
            if(poderes.ativo==4)a.agarrarPor(1.2);
            if(poderes.ativo==5)a.incendiarPor(2);
            if(a.receberImpacto(0,dano))eliminarAlien(a,true);
        }
        if(miniChefe!=null&&poderes.atinge(miniChefe.getBounds())&&miniChefe.receberDano(dano))derrotarMiniChefe();
        if(chefe!=null&&poderes.atinge(chefe.getBounds())){
            // Ponto fraco: o poder certo causa dano dobrado, como numa tabela de fraquezas.
            boolean critico=FRAQUEZA[chefe.getFase()]==poderes.ativo;
            if(critico){dano*=2;tempoCritico=.6;}
            chefe.reagir(poderes.ativo,critico);
            if(chefe.receberDano(dano))derrotarChefao();
        }
    }
    private boolean dispararElemental(boolean gelo){
        if(recargaElemental>0 || !(gelo?geloColetado:fogoColetado))return false;
        int i=gelo?0:1;energia.selecionar(i);
        if(!energia.gastar(i,gelo?4:3))return false;
        SomJogo.tocar(gelo?SomJogo.Efeito.GELO:SomJogo.Efeito.FOGO);
        int nivel=Math.max(1,gelo?inventario.gelo():inventario.fogo());
        tirosElementais.add(new DisparoElemental(gelo?DisparoElemental.Tipo.GELO:DisparoElemental.Tipo.FOGO,nave.getX()+42,nave.getY()+16,nivel));
        recargaElemental=(gelo?.58:.34)/(1+.15*(nivel-1));return true;
    }
    private boolean recolherReparo(){
        int falta=nave.getVidaMaxima()-nave.getVida();
        if(falta<=0&&!inventario.reservaDisponivel())return false;
        SomJogo.tocar(SomJogo.Efeito.REPARO);int unidade=(int)Math.ceil(nave.getVidaMaxima()*.10);
        int cura=Math.min(falta,unidade);nave.curar(cura);
        int sobra=(int)Math.floor(10.0*(unidade-cura)/unidade);
        inventario.abastecerAutomatico(sobra);
        mostrarAviso(cura>0?"CASCO REPARADO / EXCEDENTE NOS SUBTANQUES":"SUBTANQUES ABASTECIDOS");return true;
    }
    private void usarSubtanque(){int cura=inventario.usar(inventario.primeiroCarregado(),nave.getVida(),nave.getVidaMaxima());if(cura>0){SomJogo.tocar(SomJogo.Efeito.REPARO);nave.curar(cura);mostrarAviso("SUBTANQUE: CASCO REPARADO");}}
    private void atualizarSegredo(double dt){
        if(etapa!=Etapa.COMBATE||intercepcao)return;
        if(!segredoGerado&&tempoSetor>=18){segredoGerado=true;int setor=setorAtual;
            if(setor<8&&setor%2==0&&!inventario.temTanque(setor/2))segredo=new Segredo098(setor,false);
            else if(setor==3&&!inventario.reator())segredo=new Segredo098(setor,true);
        }
        if(segredo==null||!segredo.ativo())return;
        segredo.atualizar(dt);
        for(Missel m:nave.getMisseis())if(segredo.ativo()&&m.isVisible()&&m.getBounds().intersects(segredo.bounds())){m.setVisible(false);if(segredo.atingir(nave.danoBalistico()))itens.add(segredo.liberar());}
        for(DisparoElemental t:tirosElementais)if(segredo.ativo()&&t.ativo()&&t.getBounds().intersects(segredo.bounds())){t.apagar();if(segredo.atingir(t.dano()))itens.add(segredo.liberar());}
    }

    private final Placar placar = new Placar();
    private String iniciais = "";
    private boolean placarConfirmado, mostrarPlacarFinal;
    private String erroPlacar = "";
    private void premiar(int valor){pontos+=valor;creditos+=Math.max(1,valor/10);inventario.creditar(Math.max(1,valor/10));}
    private void desenharPlacarFinal(Graphics2D g){
        if (partidaTeste) {
            HudMetal.painel(g,180,220,600,150);g.setColor(Color.WHITE);
            g.setFont(new Font("Monospaced",Font.BOLD,22));g.drawString("TESTE ENCERRADO — SEM REGISTRO",210,270);
            g.setFont(new Font("Monospaced",Font.BOLD,16));g.drawString("ENTER: REPETIR SETOR / F8: ESCOLHER TESTE",204,320);return;
        }

        String rodape=placarConfirmado?"ENTER: NOVA PARTIDA":pontos==0?"SEM PONTOS NESTA PARTIDA | ENTER: TENTAR NOVAMENTE":
            "INICIAIS: "+String.format(java.util.Locale.ROOT,"%-3s",iniciais).replace(' ','_')+" | A-Z / 0-9 | BACKSPACE | ENTER: GRAVAR";
        Placar.desenhar(g,placar.ler(),erroPlacar.isEmpty()?rodape:erroPlacar);
        g.setColor(Color.WHITE);g.setFont(new Font("Monospaced",1,16));
        g.drawString("SUA PONTUACAO: "+pontos+"    CREDITOS DA PARTIDA: "+creditos,170,505);
    }

    private int eliminados;
    private boolean pausado;
    private int abaPausa;
    private final MapaEstelar mapaEstelar = new MapaEstelar();
    private CenaInterdicao cenaInterdicao;
    private final EnumMap<Item.Tipo, Integer> coleta = new EnumMap<>(Item.Tipo.class);
    private int ultimoPadraoEsquadrao = -1;
    private int padraoEsquadrao;
    private double tempoNuclear = 0.72;
    private int explosaoX;
    private int explosaoY;
    private double tempoTotal;
    private double proximoItem = 22.0;
    private double tempoAviso;
    private String aviso = "";
    private long ultimoQuadroNanos = System.nanoTime();

    private void prepararProximaOnda() {
        if (this.sacoFormacoes.isEmpty()) {
            for (int i = 0; i < 8; ++i) {
                this.sacoFormacoes.add(i);
            }
            Collections.shuffle(this.sacoFormacoes, this.aleatorio);
            if (this.sacoFormacoes.get(0) == this.ultimoPadraoEsquadrao) {
                Collections.swap(this.sacoFormacoes, 0, 1);
            }
        }
        this.proximaFormacao = this.sacoFormacoes.remove(0);
    }

    public Fase() {
        this(System.nanoTime());
    }

    Fase(long l) {SomJogo.pausar(false);SomJogo.limpar();SomJogo.iniciar();
        this.aleatorio.setSeed(l);
        this.setPreferredSize(new Dimension(960, 600));
        this.setFocusable(true);
        this.setDoubleBuffered(true);
        this.setBackground(Color.BLACK);
        this.addKeyListener(new TecladoAdapter());
        MouseAdapter mouseMapa = new MouseAdapter() {
            @Override public void mouseWheelMoved(java.awt.event.MouseWheelEvent e) {
                if (mapaAberto()) { mapaEstelar.roda(e.getX(),e.getY(),e.getPreciseWheelRotation());repaint(); }
            }
            @Override public void mousePressed(MouseEvent e) {if(PainelSom.aberto())return; if(mapaAberto())mapaEstelar.pressionar(e.getX(),e.getY()); }
            @Override public void mouseDragged(MouseEvent e) { if(mapaAberto()){mapaEstelar.arrastar(e.getX(),e.getY());repaint();} }
            @Override public void mouseReleased(MouseEvent e) { mapaEstelar.soltar(); }
            @Override public void mouseClicked(MouseEvent e) {
                if(mapaAberto()){mapaEstelar.clicar(e.getX(),e.getY(),e.getClickCount());repaint();return;}
                if(pausado && !painelTeste && e.getY()>=58 && e.getY()<=100 && e.getX()>=20 && e.getX()<950){
                    abaPausa=Math.min(5,(e.getX()-20)/154);cursorPausa=0;repaint();
                }
            }
        };
        addMouseListener(mouseMapa);addMouseMotionListener(mouseMapa);addMouseWheelListener(mouseMapa);
        this.addFocusListener(new FocusAdapter(){

            @Override
            public void focusLost(FocusEvent focusEvent) {
                Fase.this.espacoPressionado = false;geloPressionado=false;fogoPressionado=false;
                Fase.this.nave.soltarTeclas();
            }
        });
        this.nave = new Nave();
        aplicarInventario();
        this.inicializaInimigos();
        this.iniciarRitmoSetor();
        this.timer = new Timer(16, this);
        this.timer.start();
    }

    public void inicializaInimigos() {
        this.alien = new ArrayList<Alien>();
        this.lancadosNoSetor = 0;
        this.numeroEsquadrao = 0;
        this.esperaProximoEsquadrao = 0.0;
        this.esperaRajada = 2.0;
        this.indiceRajada = 0;
        this.sacoFormacoes.clear();
        this.prepararProximaOnda();
        this.lancarProximoEsquadrao();
    }

    private int quantidadeSetor() {
        int n = Math.min(this.chefesDerrotados, FINAIS_SETORES.length - 1);
        return FINAIS_SETORES[n] - (n == 0 ? 0 : FINAIS_SETORES[n - 1]);
    }

    private void lancarProximoEsquadrao() {
        if (this.lancadosNoSetor >= this.quantidadeSetor()) {
            return;
        }
        int n = this.quantidadeSetor() - this.lancadosNoSetor;
        int n2 = Math.min(TAMANHOS[this.proximaFormacao], n);
        int n3 = this.proximaFormacao;
        double d = 510 + this.aleatorio.nextInt(150);
        double d2 = 126 + this.aleatorio.nextInt(48);
        double d3 = (490.0 - d2) / (double)Math.max(1, n2 - 1);
        boolean bl = this.aleatorio.nextBoolean();
        int n4 = this.aleatorio.nextInt(3);
        int n5 = this.aleatorio.nextInt(5);
        this.ultimoPadraoEsquadrao = n3;
        this.padraoEsquadrao = n3;
        double d4 = 1.0 + (double)this.chefesDerrotados * 0.075 + (double)this.faseMusical() * 0.065;
        for (int i = 0; i < n2; ++i) {
            Alien.Classe[] classeArray;
            Alien.Classe classe;
            int n6;
            Alien.Tipo tipo;
            double d5 = d;
            double d6 = d2 + (double)i * d3;
            if (n3 == 1) {
                d5 += Math.abs((double)i - (double)(n2 - 1) / 2.0) * 40.0;
            }
            if (n3 == 2) {
                d5 += (double)(i % 2 * 95);
            }
            if (n3 == 3) {
                d5 += (double)(i * 26);
            }
            if (n3 == 4) {
                d5 += Math.cos((double)i * 1.5) * 68.0;
            }
            if (n3 == 5) {
                d5 += Math.sin((double)i * Math.PI / (double)Math.max(1, n2 - 1)) * 110.0;
            }
            if (n3 == 6) {
                d5 += (double)(i * 23);
            }
            if (n3 == 7) {
                d5 += (double)(i % 2 == 0 ? 90 : -20);
            }
            if (bl) {
                d6 = 620.0 - d6;
            }
            d5 = Math.max(440.0, Math.min(842.0, d5));
            d6 = Math.max(122.0, Math.min(500.0, d6));
            Alien.Classe[][] grupos={
                {Alien.Classe.ORGANICO,Alien.Classe.ESQUADRAO,Alien.Classe.INTERCEPTADOR},
                {Alien.Classe.INTERCEPTADOR,Alien.Classe.LASER,Alien.Classe.TANQUE},
                {Alien.Classe.ORGANICO,Alien.Classe.KAMIKAZE},
                {Alien.Classe.TANQUE,Alien.Classe.ESQUADRAO},
                {Alien.Classe.LASER,Alien.Classe.INTERCEPTADOR},
                {Alien.Classe.KAMIKAZE,Alien.Classe.TANQUE},
                {Alien.Classe.ORGANICO,Alien.Classe.LASER},
                {Alien.Classe.ORGANICO,Alien.Classe.TANQUE,Alien.Classe.KAMIKAZE}};
            Alien.Classe[] grupo=grupos[Math.min(7,chefesDerrotados)];classe=grupo[(i+n5)%grupo.length];
            tipo=classe==Alien.Classe.ORGANICO?Alien.Tipo.ALIENIGENA_ORIGINAL:Alien.Tipo.PIRATA;
            n6=n4==0?(int)d6:n4==1?110+aleatorio.nextInt(80):440+aleatorio.nextInt(80);
            Alien novo = new Alien(1045 + i * 54, n6, tipo, classe);
            novo.definirSetor(setorAtual,aleatorio);
            novo.entrarEmEsquadrao(d5, d6, i, n3, d4);
            this.alien.add(novo);
        }
        this.lancadosNoSetor += n2;
        ++this.numeroEsquadrao;
        this.prepararProximaOnda();
        this.esperaRajada = 2.0;
        this.esperaProximoEsquadrao = Math.max(2.2, 3.7 - (double)this.chefesDerrotados * 0.18);
    }

    private void dispararRajadaEsquadrao(double d) {
        this.esperaRajada -= d;
        if (this.esperaRajada <= 0.0) {
            ArrayList<Alien> arrayList = new ArrayList<>();
            for (Alien alien : this.alien) {
                if (!alien.ataqueCoordenado() || !alien.podeAtirar(this.nave.getX())) continue;
                arrayList.add(alien);
            }
            int n = Math.min(3, 1 + this.chefesDerrotados / 3);
            for (int i = 0; i < n && !arrayList.isEmpty() && this.tirosInimigos.size() < 52; ++i) {
                Alien alien = arrayList.get((this.indiceRajada + i) % arrayList.size());
                atirarInimigo(alien);
                alien.resetarRecarga(this.aleatorio);
            }
            ++this.indiceRajada;
            this.esperaRajada = Math.max(0.76, 2.2 - (double)this.chefesDerrotados * 0.12 - (double)this.faseMusical() * 0.15);
        }
        for (Alien alien : this.alien) {
            if (alien.ataqueCoordenado() || !alien.podeAtirar(this.nave.getX()) || alien.getClasse() == Alien.Classe.KAMIKAZE || this.tirosInimigos.size() >= 52) continue;
            atirarInimigo(alien);
            alien.resetarRecarga(this.aleatorio);
        }
    }

    /** Para onde os inimigos miram: a isca de cristal, quando existe, atrai os disparos. */
    private int alvoX(){return poderes.cloneAtivo()?(int)poderes.cloneX():nave.getX();}
    private int alvoY(){return poderes.cloneAtivo()?(int)poderes.cloneY():nave.getY();}
    private void atirarInimigo(Alien a){a.animarAtaqueEnxame();SomJogo.tocar(SomJogo.Efeito.INIMIGO,a.getX());
        tirosInimigos.add(new ProjetilInimigo(a,alvoX()+20,alvoY()+18));
        if(a.getEquipamento()==Item.Tipo.ARMA_2){tirosInimigos.add(new ProjetilInimigo(a,alvoX()+20,alvoY()-100));tirosInimigos.add(new ProjetilInimigo(a,alvoX()+20,alvoY()+136));}
    }
    private void atualizarEsquadroes(double d) {
        if(intercepcao&&miniChefe!=null&&!miniChefe.morrendo()){
            miniChefe.comandanteTeste().observar(nave.getMisseis());
            int guarda=0;for(Alien a:alien)if(a.isVisible()&&a.especieEnxame()==1&&guarda<2)a.escoltar(miniChefe.getBounds(),guarda++);
        }
        block0: for (Alien alien : this.alien) {
            if (!alien.isVisible()) continue;
            alien.verificarKamikaze(this.nave.getY() + 18);
            if (alien.podeCapturarMelhoria()) {
                for (Item item : this.itens) {
                    if (!item.isVisible() || !item.podeSerCapturado() || item.recuperado() || Recompensas.identificador(item.getTipo())!=0 || item.getTipo()==Item.Tipo.REPARO) continue;
                    Rectangle rectangle = item.getBounds();
                    alien.mirarMelhoria(rectangle.x, rectangle.y);
                    break;
                }
            }
            alien.mexer(d * this.dificuldade.velocidadeInimigos, this.nave.getMisseis(), this.aleatorio);
            if(alien.getResistencia()<=0){eliminarAlien(alien,false);continue;}
            if (!alien.podeCapturarMelhoria()) continue;
            for (Item item : this.itens) {
                if (!item.isVisible() || !item.podeSerCapturado() || item.recuperado() || Recompensas.identificador(item.getTipo())!=0 || item.getTipo()==Item.Tipo.REPARO || !alien.getBounds().intersects(item.getBounds())) continue;
                alien.capturar(item.getTipo());
                item.setVisible(false);
                this.gerarParticulas(alien.getX() + 20, alien.getY() + 20, 15);
                continue block0;
            }
        }
        this.dispararRajadaEsquadrao(d);
        this.esperaProximoEsquadrao -= d;
        if (this.miniChefe != null) {
            this.miniChefe.atualizar(d, this.nave.getY());
            if(this.miniChefe.derrotado()){derrotarMiniChefe();return;}
            if(intercepcao && !miniChefe.morrendo()){esperaEnxame-=d;if(esperaEnxame<=0)lancarEnxame();}
            else if(!intercepcao && esperaProximoEsquadrao<=0 && alien.stream().filter(Alien::isVisible).count()+TAMANHOS[proximaFormacao]<=18)lancarProximoEsquadrao();
            return;
        }
        if (intercepcao) return;
        if (!this.miniChefeEnfrentado && this.lancadosNoSetor >= this.quantidadeSetor() / 2) {
            if (this.avisoMini < 0.0) {
                this.avisoMini = 2.4;
            }
            this.avisoMini -= d;
            if (this.avisoMini <= 0.0) {
                this.miniChefeEnfrentado = true;
                this.miniChefe = new MiniChefe(this.setorAtual + 1);
                this.avisoMini = -1.0;
            }
            return;
        }
        if (this.lancadosNoSetor >= this.quantidadeSetor()) {
            return;
        }
        int n = Math.min(TAMANHOS[this.proximaFormacao], this.quantidadeSetor() - this.lancadosNoSetor);
        long l = this.alien.stream().filter(Alien::isVisible).count();
        if (this.esperaProximoEsquadrao <= 0.0 && l + (long)n <= 18L) {
            this.lancarProximoEsquadrao();
        }
    }

    private void desenharRadar(Graphics2D graphics2D) {
        if (!this.emJogo || this.etapa != Etapa.COMBATE) {
            return;
        }
        Graphics2D graphics2D2 = (Graphics2D)graphics2D.create();
        graphics2D2.setColor(new Color(3, 9, 22, 220));
        graphics2D2.fillRect(0, 72, 960, 34);
        graphics2D2.setColor(new Color(64, 105, 132));
        graphics2D2.drawLine(0, 105, 960, 105);
        graphics2D2.setFont(new Font("Monospaced", 1, 11));
        if (intercepcao) {
            graphics2D2.setColor(Color.CYAN);
            graphics2D2.drawString("FORA DA ROTA / INTERDICAO HOSTIL",14,85);
            graphics2D2.drawString("ENXAME: " + alien.stream().filter(Alien::isVisible).count() +
                    " / " + (miniChefe != null ? miniChefe.nome() : "COMANDANTE DESTRUIDO"),375,85);
            graphics2D2.drawString("ELIMINE AS AMEACAS PARA RETOMAR A DOBRA",375,100);
            graphics2D2.dispose();return;
        }
        int n = this.quantidadeSetor() - this.lancadosNoSetor;
        int n2 = (int)this.alien.stream().filter(Alien::isVisible).count();
        graphics2D2.setColor(new Color(112, 226, 255));
        graphics2D2.drawString("A CAMINHO " + n + "  |  EM CAMPO " + n2, 14, 85);
        for (int i = 0; i < this.quantidadeSetor(); ++i) {
            int n3 = 14 + i * 7;
            graphics2D2.setColor(i < n ? new Color(74, 210, 245) : new Color(29, 54, 72));
            graphics2D2.fillPolygon(new int[]{n3, n3 + 3, n3 + 6, n3 + 3}, new int[]{97, 91, 97, 95}, 4);
        }
        String string = this.miniChefe != null ? "MINICHEFE EM COMBATE" : (this.avisoMini >= 0.0 ? "SENTINELA CHEGANDO: " + (int)Math.ceil(this.avisoMini) + "s" : (n == 0 ? "ELIMINE OS RESTANTES / CHEFE A FRENTE" : "PROXIMA: " + FORMACOES[this.proximaFormacao] + " / " + Math.min(TAMANHOS[this.proximaFormacao], n) + " NAVES" + (n2 + Math.min(TAMANHOS[this.proximaFormacao], n) > 18 ? " / AGUARDANDO ESPACO" : " / " + (int)Math.ceil(Math.max(0.0, this.esperaProximoEsquadrao)) + "s")));
        graphics2D2.setColor(this.avisoMini >= 0.0 ? new Color(255, 128, 100) : Color.WHITE);
        graphics2D2.drawString(string, 375, 85);
        graphics2D2.setColor(new Color(162, 186, 213));
        graphics2D2.drawString((this.miniChefeEnfrentado ? "SENTINELA: " + (this.miniChefe == null ? "VENCIDA" : "ATIVA") : "SENTINELA NO MEIO DO SETOR") + "   " + (this.geloColetado ? "Q: GELO  " : "") + (this.fogoColetado ? "E: FOGO" : ""), 375, 100);
        graphics2D2.dispose();
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D graphics2D = (Graphics2D)graphics.create();
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        Graphics2D graphics2D2 = (Graphics2D)graphics2D.create();
        graphics2D2.setClip(0, 72, 960, 528);
        if (this.tempoNuclear < 0.42) {
            int n = (int)Math.round(9.0 * (1.0 - this.tempoNuclear / 0.42));
            graphics2D2.translate(this.aleatorio.nextInt(n * 2 + 1) - n, this.aleatorio.nextInt(n * 2 + 1) - n);
        }
        if(intercepcao&&miniChefe!=null&&miniChefe.morrendo())graphics2D2.translate((int)(Math.sin(miniChefe.comandanteTeste().relogio()*70)*4),(int)(Math.cos(miniChefe.comandanteTeste().relogio()*70)*3));
        this.fundoEspacial.desenhar(graphics2D2);
        if(segredo!=null)segredo.desenhar(graphics2D2,tempoTotal);
        if (this.emJogo) {
            for (Asteroide object : this.asteroides) {
                if (!object.isVisible()) continue;
                object.desenhar(graphics2D2);
            }
            desenharCapsulas(graphics2D2);
            for (Item item : this.itens) {
                if (!item.isVisible()) continue;
                item.desenhar(graphics2D2, this.tempoTotal);
            }
            this.nave.desenhar(graphics2D2);
            for (DisparoElemental disparoElemental : this.tirosElementais) {
                if (!disparoElemental.ativo()) continue;
                disparoElemental.desenhar(graphics2D2);
            }
            for (Missel missel : this.nave.getMisseis()) {
                if (!missel.isVisible()) continue;
                if (missel.getTipo() == 1) {
                    Graphics2D graphics2D3 = (Graphics2D)graphics2D2.create();
                    graphics2D3.translate(missel.getX(), missel.getY());
                    graphics2D3.rotate(Math.toRadians(missel.getAnguloGraus()));
                    graphics2D3.setColor(new Color(0, 103, 255, 65));
                    graphics2D3.fillPolygon(new int[]{-24, 3, 21, 3}, new int[]{5, -4, 5, 14}, 4);
                    graphics2D3.setColor(new Color(12, 218, 255));
                    graphics2D3.fillPolygon(new int[]{-14, 4, 17, 4}, new int[]{5, 1, 5, 9}, 4);
                    graphics2D3.setColor(new Color(224, 255, 255));
                    graphics2D3.fillPolygon(new int[]{-5, 5, 13, 5}, new int[]{5, 3, 5, 7}, 4);
                    graphics2D3.dispose();
                    continue;
                }
                graphics2D2.drawImage(missel.getImagem(), missel.getX(), missel.getY(), null);
            }
            if (this.etapa == Etapa.COMBATE) {
                poderes.desenharAtras(graphics2D2);
                for (Alien alien : this.alien) {
                    if (!alien.isVisible()) continue;
                    alien.desenhar(graphics2D2);
                }
                for (ProjetilInimigo projetilInimigo : this.tirosInimigos) {
                    if (!projetilInimigo.isVisible()) continue;
                    projetilInimigo.desenhar(graphics2D2);
                }
                if (this.miniChefe != null) {
                    this.miniChefe.desenhar(graphics2D2);
                }
            } else if (this.etapa == Etapa.BATALHA_CHEFE && this.chefe != null) {
                this.chefe.desenhar(graphics2D2);
                for (TiroChefe tiroChefe : this.tirosChefes) {
                    if (!tiroChefe.isVisible()) continue;
                    tiroChefe.desenhar(graphics2D2);
                }
            }
        }
        if(efeitoFusao!=null)efeitoFusao.desenhar(graphics2D2);
        poderes.desenhar(graphics2D2);
        if(cenaDerrota!=null)cenaDerrota.desenhar(graphics2D2);
        this.desenharParticulas(graphics2D2);
        this.desenharExplosoes(graphics2D2);
        this.desenharBombaNuclear(graphics2D2);
        this.desenharOndaEscudo(graphics2D2);
        if (this.cenaNave != null && (this.etapa == Etapa.DOBRA || !this.emJogo && !this.venceu)) {
            this.cenaNave.desenhar(graphics2D2);
        }
        graphics2D2.dispose();
        if (etapa == Etapa.INTERDICAO) desenharInterdicao(graphics2D);
        this.desenharClarao(graphics2D);
        this.desenharHud(graphics2D);
        this.desenharRadar(graphics2D);
        graphics2D.setColor(new Color(2,5,14,225));graphics2D.fillRect(630,chefe!=null?78:575,330,25);
        Arte095.desenhar(graphics2D,8,635,chefe!=null?80:577,20,20);
        graphics2D.setColor(Color.WHITE);graphics2D.setFont(new Font("Monospaced",1,12));
        graphics2D.drawString("CR "+inventario.saldo()+"  Q "+energia.carga(0)+"% E "+energia.carga(1)+"%",630,chefe!=null?95:592);
        graphics2D.drawString("R: RESERVAS "+inventario.carga(0)+"/"+inventario.carga(1)+"/"+inventario.carga(2)+"/"+inventario.carga(3),16,572);
        graphics2D.drawString("C: PODER  X: TROCAR  "+Poderes099.NOMES[poderes.selecionado],16,592);
        // Energia do poder equipado: gasta a cada uso e recarrega aos poucos (ou com relíquias).
        if(inventario.temChaveIndice(poderes.selecionado)){int sel=poderes.selecionado,bx=16+graphics2D.getFontMetrics().stringWidth("C: PODER  X: TROCAR  "+Poderes099.NOMES[sel])+10;
            Poderes099.simbolo(graphics2D,sel,bx+9,587,1);graphics2D.setColor(new Color(20,30,44));graphics2D.fillRect(bx+22,582,104,10);
            graphics2D.setColor(poderes.energia[sel]>=Poderes099.CUSTO[sel]&&poderes.cooldown[sel]<=0?Poderes099.CORES_SIMBOLO[sel]:new Color(110,110,120));
            graphics2D.fillRect(bx+24,584,poderes.energia[sel],6);graphics2D.setColor(Color.WHITE);graphics2D.drawLine(bx+24+Poderes099.CUSTO[sel],582,bx+24+Poderes099.CUSTO[sel],592);
            if(poderes.cooldown[sel]>0)graphics2D.drawString((int)Math.ceil(poderes.cooldown[sel])+"s",bx+132,592);}
        if (this.chefe != null && this.emJogo) {
            this.chefe.desenharVida(graphics2D);
        }
        if (this.etapa == Etapa.APRESENTACAO_CHEFE && this.apresentacao != null) {
            this.apresentacao.desenhar(graphics2D);
        }
        if (this.emJogo && (this.etapa == Etapa.ALERTA_CHEFE || this.etapa == Etapa.INTERLUDIO)) {
            this.desenharTransicao(graphics2D);
        }
        if (this.etapa == Etapa.CREDITOS && this.cenaFinal != null) {
            this.cenaFinal.desenhar(graphics2D);
            graphics2D.setColor(Color.WHITE);graphics2D.drawString(partidaTeste ? "ENTER: REPETIR TESTE / F8: PAINEL" : "ENTER: REGISTRAR INICIAIS NO PLACAR",280,580);
            if(mostrarPlacarFinal)desenharPlacarFinal(graphics2D);
        } else if (!this.emJogo && (this.cenaNave == null || this.cenaNave.terminou())) {
            this.desenharEncerramento(graphics2D);
        } else if (this.tempoAviso > 0.0 && this.etapa != Etapa.INTERLUDIO && this.etapa != Etapa.DOBRA) {
            this.desenharAviso(graphics2D);
        }
        if (tempoCritico > 0 && chefe != null) {
            graphics2D.setFont(new Font(Font.MONOSPACED, Font.BOLD, 28));
            graphics2D.setColor(new Color(255, 230, 60, (int) (255 * Math.min(1, tempoCritico / .3))));
            graphics2D.drawString("PONTO FRACO! x2", chefe.getBounds().x - 40, chefe.getBounds().y - 12 - (int) ((.6 - tempoCritico) * 40));
        }
        if (escolhendoSetor || this.pausado && !painelTeste) this.desenharMenuPausa(graphics2D);
        graphics2D.setColor(Color.CYAN);
        graphics2D.setFont(new Font("Monospaced", Font.BOLD, 12));
        graphics2D.drawString(partidaTeste ? "TESTE / SEM PLACAR — F8: PAINEL" : "F8: TESTAR SETORES", 12, 591);
        if (painelTeste) desenharPainelTeste(graphics2D);
        PainelSom.desenhar(graphics2D);
        graphics2D.dispose();
    }

    private void desenharPainelTeste(Graphics2D g) {
        g.setColor(new Color(0, 3, 12, 225));g.fillRect(0,72,960,528);
        HudMetal.painel(g,140,140,680,370);
        g.setColor(Color.WHITE);g.setFont(new Font("Monospaced",Font.BOLD,24));
        g.drawString("CAMPO DE TESTES / SETOR " + setorTeste + "/8",185,192);
        g.setFont(new Font("Monospaced",Font.BOLD,16));g.setColor(Color.CYAN);
        g.drawString("1–8 OU SETAS: ESCOLHER SETOR",185,232);
        g.setColor(Color.WHITE);
        g.drawString("ENTER: JOGAR SETOR     S: SENTINELA",185,272);
        g.drawString("B: CHEFAO             D: DOBRA PARA ESTE SETOR",185,306);
        g.drawString("I: INTERDICAO / ENXAME",185,338);
        g.drawString("N: NOVA CAMPANHA      F8 / ESC: VOLTAR",185,368);
        g.setColor(new Color(255,188,120));
        g.drawString("NAVE NIVEL 8 / ARMAS / 4 SUBTANQUES",185,395);
        g.drawString("VIDA NORMAL. SEM RECORDES OU DESBLOQUEIOS.",185,427);
        g.setColor(Color.LIGHT_GRAY);g.setFont(new Font("Monospaced",Font.PLAIN,13));
        g.drawString("Iniciar teste substitui a partida atual. Voltar apenas fecha o painel.",165,476);
    }

    private void iniciarSessaoTeste(int setor, int encontro) {
        reiniciar();
        inventario=inventario.copiaTeste();inventario.equiparTeste();aplicarInventario();
        poderes = new Poderes099();
        partidaTeste = true; painelTeste = false; pausado = false;
        setorTeste = Math.max(1, Math.min(8, setor));
        chefesDerrotados = setorTeste - 1; setorAtual = setorTeste - 1; setoresVencidos = (1 << (setorTeste - 1)) - 1; escolhendoSetor = false;
        eliminados = chefesDerrotados == 0 ? 0 : FINAIS_SETORES[chefesDerrotados - 1];
        alien.clear(); tirosInimigos.clear(); itens.clear(); asteroides.clear();
        lancadosNoSetor = 0; numeroEsquadrao = 0; miniChefeEnfrentado = false;
        esperaProximoEsquadrao = 0; avisoMini = -1;
        for (int i=0;i<Nave.MAX_EVOLUCAO;i++) nave.melhorar();
        nave.coletarArmaSecundaria(); Missel.setTipoTiro(0);
        geloColetado = fogoColetado = true; kitsReparo = 1;
        iniciarRitmoSetor();
        fundoEspacial.definirProgresso(0, chefesDerrotados);
        if (encontro == KeyEvent.VK_S) {
            miniChefeEnfrentado = true; miniChefe = new MiniChefe(setorTeste);
        } else if (encontro == KeyEvent.VK_B) {
            miniChefeEnfrentado = true; iniciarChefao(ORDEM_CHEFOES[setorAtual]);
        } else if (encontro == KeyEvent.VK_D || encontro == KeyEvent.VK_I) {
            emboscadaPendente = encontro == KeyEvent.VK_I;
            etapa = Etapa.DOBRA; cenaNave = new CinematicaNave(nave, false, chefesDerrotados);
        } else { lancarProximoEsquadrao(); }
        ultimoQuadroNanos = System.nanoTime(); requestFocusInWindow();
    }

    private boolean mapaAberto() { return escolhendoSetor || pausado && !painelTeste && abaPausa == 3; }

    private void desenharMenuPausa(Graphics2D graphics2D) {
        if (mapaAberto()) {
            int setor=setorAtual;
            mapaEstelar.estado(setor,etapa==Etapa.DOBRA&&!escolhendoSetor,intercepcao||etapa==Etapa.INTERDICAO,cenaNave==null?0:cenaNave.getTempo()/4.2);
            mapaEstelar.desenhar(graphics2D);return;
        }
        Pausa099.desenhar(graphics2D,abaPausa,cursorPausa,inventario,nave,energia,poderes,geloColetado,fogoColetado,tempoTotal,tempoAviso>0?aviso:"");
    }

    private void desenharHud(Graphics2D graphics2D) {
        HudMetal.desenhar(graphics2D, this.nave.getVida(), this.nave.getVidaMaxima(), this.nave.getNivel(), (int)Math.ceil(this.nave.getTempoEscudo()), this.pontos, Math.min(TOTAL_SETORES, this.chefesDerrotados + 1), this.contarTrofeus(), Missel.getTipoMissel(), this.nave.temArmaSecundaria(), this.tempoSetor);
    }

    private void desenharTransicao(Graphics2D graphics2D) {
        boolean bl = this.etapa == Etapa.ALERTA_CHEFE;
        Graphics2D graphics2D2 = (Graphics2D)graphics2D.create();
        double d = (1.0 + Math.sin(this.tempoTotal * 7.0)) * 0.5;
        if (bl) {
            int n = 177;
            int n2 = 206;
            graphics2D2.setColor(new Color(3, 12, 27, 226));
            graphics2D2.fillRoundRect(n, n2, 606, 160, 24, 24);
            graphics2D2.setColor(d > 0.48 ? new Color(255, 78, 58) : new Color(255, 214, 139));
            graphics2D2.setStroke(new BasicStroke(3.0f));
            graphics2D2.drawRoundRect(n, n2, 606, 160, 24, 24);
            graphics2D2.setFont(new Font("SansSerif", 1, 28));
            Fase.centralizar(graphics2D2, "DANGER! DANGER! DANGER!", 257);
            graphics2D2.setFont(new Font("Monospaced", 1, 17));
            Fase.centralizar(graphics2D2, "CHEGANDO: " + NOMES_SETORES[Math.max(0, Math.min(7, this.chefePendente - 1))], 301);
            graphics2D2.setFont(new Font("Monospaced", 1, 14));
            Fase.centralizar(graphics2D2, "CHEGADA EM " + Math.max(1, (int)Math.ceil(this.tempoEtapa)), 342);
        } else if (this.trofeuPendente != 0) {
            graphics2D2.setColor(new Color(2,5,18,220));graphics2D2.fillRect(150,110,660,70);
            graphics2D2.setColor(Color.WHITE);graphics2D2.setFont(new Font("Monospaced",1,21));
            Fase.centralizar(graphics2D2,"CHEFE DESTRUIDO — COLETE A RECOMPENSA",140);
            graphics2D2.setFont(new Font("Monospaced",1,14));Fase.centralizar(graphics2D2,Recompensas.nome(trofeuPendente)+" | APROXIME SUA NAVE DO ITEM",164);
        }
        graphics2D2.dispose();
    }

    private static void centralizar(Graphics2D graphics2D, String string, int n) {
        graphics2D.drawString(string, (960 - graphics2D.getFontMetrics().stringWidth(string)) / 2, n);
    }

    private void desenharAviso(Graphics2D graphics2D) {
        graphics2D.setFont(new Font("SansSerif", 1, 16));
        int n = graphics2D.getFontMetrics().stringWidth(this.aviso);
        int n2 = 480 - n / 2;
        graphics2D.setColor(new Color(2, 9, 24, 210));
        int n3 = this.chefe == null ? 112 : 134;
        graphics2D.fillRoundRect(n2 - 17, n3, n + 34, 36, 12, 12);
        graphics2D.setColor(new Color(255, 214, 114));
        graphics2D.drawString(this.aviso, n2, n3 + 26);
    }

    private void desenharEncerramento(Graphics2D g) {desenharPlacarFinal(g);}

    private void desenharExplosoes(Graphics2D graphics2D) {
        for(DerrotaEnxame e:derrotasEnxame)e.desenhar(graphics2D);
        for (ExplosaoCurta explosaoCurta : this.explosoes) {
            double p=explosaoCurta.tempo/explosaoCurta.duracao;
            int n=Math.max(2,(int)(explosaoCurta.tamanho*(.6+Math.sin(p*Math.PI)*1.5)));
            Graphics2D graphics2D2=(Graphics2D)graphics2D.create();
            graphics2D2.setComposite(AlphaComposite.SrcOver.derive((float)Math.max(0,1-p)));
            Arte094.desenhar(graphics2D2,11,explosaoCurta.x-n,explosaoCurta.y-n,n*2,n*2);
            graphics2D2.dispose();
        }
    }

    private void desenharParticulas(Graphics2D graphics2D) {
        for (Particula particula : this.particulas) {
            Graphics2D graphics2D2 = (Graphics2D)graphics2D.create();
            graphics2D2.setComposite(AlphaComposite.getInstance(3, (float)Math.max(0.0, Math.min(1.0, particula.tempo / particula.duracao))));
            graphics2D2.setColor(particula.cor);
            int n = Math.max(1, (int)particula.tamanho);
            graphics2D2.fillOval((int)particula.x, (int)particula.y, n, n);
            graphics2D2.dispose();
        }
    }

    private void desenharBombaNuclear(Graphics2D graphics2D) {
        if (this.tempoNuclear >= 0.72) {
            return;
        }
        double d = this.tempoNuclear / 0.72;
        int n = 20 + (int)(900.0 * d);
        Graphics2D graphics2D2 = (Graphics2D)graphics2D.create();
        graphics2D2.setComposite(AlphaComposite.getInstance(3, (float)Math.max(0.0, 0.44 * (1.0 - d))));
        graphics2D2.setColor(new Color(255, 150, 39));
        graphics2D2.fillOval(this.explosaoX - n, this.explosaoY - n, n * 2, n * 2);
        graphics2D2.setColor(new Color(255, 245, 199));
        graphics2D2.setStroke(new BasicStroke(8.0f));
        graphics2D2.drawOval(this.explosaoX - n, this.explosaoY - n, n * 2, n * 2);
        graphics2D2.setColor(Color.WHITE);
        graphics2D2.setStroke(new BasicStroke(3.0f));
        graphics2D2.drawOval(this.explosaoX - n - 12, this.explosaoY - n - 12, n * 2 + 24, n * 2 + 24);
        graphics2D2.dispose();
    }

    private void desenharOndaEscudo(Graphics2D graphics2D) {
        if (this.tempoOndaEscudo <= 0.0) {
            return;
        }
        double d = 1.0 - this.tempoOndaEscudo / 0.65;
        int n = (int)(190.0 * d);
        Graphics2D graphics2D2 = (Graphics2D)graphics2D.create();
        graphics2D2.setStroke(new BasicStroke(6.0f));
        graphics2D2.setColor(new Color(91, 225, 255, Math.max(0, (int)(220.0 * (1.0 - d)))));
        graphics2D2.drawOval((int)this.ondaX - n, (int)this.ondaY - n, 2 * n, 2 * n);
        graphics2D2.setColor(new Color(102, 240, 253, Math.max(0, (int)(60.0 * (1.0 - d)))));
        graphics2D2.fillOval((int)this.ondaX - n, (int)this.ondaY - n, 2 * n, 2 * n);
        graphics2D2.dispose();
    }

    private void explodirEscudoSeNecessario() {
        if (!this.nave.consumirExplosaoEscudo()) {
            return;
        }
        this.ondaX = this.nave.getX() + 25;
        this.ondaY = this.nave.getY() + 20;
        this.tempoOndaEscudo = 0.65;
        this.gerarParticulas((int)this.ondaX, (int)this.ondaY, 32);
        this.mostrarAviso("ESCUDO EXPLODIU! ONDA DE CHOQUE!");
        for (Asteroide object : this.asteroides) {
            if (!object.isVisible() || !(Math.hypot((double)object.getX() - this.ondaX, (double)object.getY() - this.ondaY) < 190.0)) continue;
            object.setVisible(false);
        }
        if (this.etapa == Etapa.COMBATE) {
            Rectangle rectangle;
            if (this.miniChefe != null && Math.hypot(this.miniChefe.getBounds().getCenterX() - this.ondaX, this.miniChefe.getBounds().getCenterY() - this.ondaY) < 255.0 && this.miniChefe.receberDano(8)) {
                this.derrotarMiniChefe();
            }
            for (Alien alien : this.alien) {
                if (!alien.isVisible() || !(Math.hypot((rectangle = alien.getBounds()).getCenterX() - this.ondaX, rectangle.getCenterY() - this.ondaY) < 190.0)) continue;
                this.eliminarAlien(alien, false);
            }
            for (ProjetilInimigo projetilInimigo : this.tirosInimigos) {
                rectangle = projetilInimigo.getBounds();
                if (!(Math.hypot(rectangle.getCenterX() - this.ondaX, rectangle.getCenterY() - this.ondaY) < 190.0)) continue;
                projetilInimigo.setVisible(false);
            }
        } else if (this.etapa == Etapa.BATALHA_CHEFE && this.chefe != null) {
            for (TiroChefe tiroChefe : this.tirosChefes) {
                Rectangle rectangle = tiroChefe.getBounds();
                if (!(Math.hypot(rectangle.getCenterX() - this.ondaX, rectangle.getCenterY() - this.ondaY) < 190.0)) continue;
                tiroChefe.setVisible(false);
            }
            if (Math.hypot(this.chefe.getBounds().getCenterX() - this.ondaX, this.chefe.getBounds().getCenterY() - this.ondaY) < 290.0 && this.chefe.receberDano(14)) {
                this.derrotarChefao();
            }
        }
    }

    private void desenharClarao(Graphics2D graphics2D) {
        if (this.tempoNuclear >= 0.26) {
            return;
        }
        double d = this.tempoNuclear / 0.26;
        Graphics2D graphics2D2 = (Graphics2D)graphics2D.create();
        graphics2D2.setComposite(AlphaComposite.getInstance(3, (float)(0.59 * Math.pow(1.0 - d, 2.0))));
        graphics2D2.setColor(Color.WHITE);
        graphics2D2.fillRect(0, 72, 960, 528);
        graphics2D2.dispose();
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        long l = System.nanoTime();
        double d = Math.min(0.045, Math.max(0.0, (double)(l - this.ultimoQuadroNanos) / 1.0E9));
        this.ultimoQuadroNanos = l;
        this.atualizarSimulacao(d);
    }

    void atualizarSimulacao(double d) {SomJogo.pausar(pausado);
        if ((d = Math.min(0.045, Math.max(0.0, d))) == 0.0) {
            return;
        }
        if (escolhendoSetor) { mapaEstelar.atualizar(d); this.repaint(); return; }
        tempoCritico = Math.max(0, tempoCritico - d);
        if (this.pausado) {
            if (mapaAberto()) mapaEstelar.atualizar(d);
            this.repaint();
            return;
        }
        if(intercepcao&&miniChefe!=null&&miniChefe.morrendo()){
            tirosInimigos.clear();tirosChefes.clear();tirosElementais.clear();nave.getMisseis().clear();
            espacoPressionado=geloPressionado=fogoPressionado=false;nave.soltarTeclas();
            miniChefe.atualizar(d,nave.getY());atualizarEfeitos(d);fundoEspacial.atualizar(d);
            if(miniChefe.derrotado())derrotarMiniChefe();repaint();return;
        }
        if (etapa == Etapa.INTERDICAO) {
            if (cenaInterdicao == null) cenaInterdicao = novaInterdicao();
            cenaInterdicao.atualizar(d);tempoInterdicao=cenaInterdicao.getTempo();
            if (cenaInterdicao.terminou()) iniciarCombateIntercepcao();
            repaint(); return;
        }
        if (intercepcao && emJogo && miniChefe == null && alien.stream().noneMatch(Alien::isVisible)) {
            saidaIntercepcao += d;
            if (saidaIntercepcao >= 2.0) {
                intercepcao = false; eliminados = eliminadosAntesIntercepcao;
                etapa = Etapa.DOBRA; cenaNave = new CinematicaNave(nave,false,chefesDerrotados);
                tirosInimigos.clear(); tirosElementais.clear(); nave.getMisseis().clear(); itens.clear();
            }
        }
        if(etapa==Etapa.DERROTA_CHEFE){cenaDerrota.atualizar(d);if(cenaDerrota.terminou()){cenaDerrota=null;etapa=Etapa.INTERLUDIO;itens.add(new Item(Recompensas.tipo(trofeuPendente),550,280,true));tempoEtapa=.8;}repaint();return;}
        if (this.etapa == Etapa.DOBRA) {
            if (this.cenaNave != null) {
                this.cenaNave.atualizar(d);
                if (emboscadaPendente && cenaNave.getTempo() >= 1.5) {
                    emboscadaPendente = false; emboscadaUsada = true;
                    etapa = Etapa.INTERDICAO; tempoInterdicao = 0; cenaInterdicao = novaInterdicao();
                } else if (this.cenaNave.terminou()) {
                    this.finalizarDobra();
                }
            }
            this.repaint();
            return;
        }
        if (this.emJogo && !intercepcao) {
            int n = this.chefesDerrotados == 0 ? 0 : FINAIS_SETORES[Math.min(this.chefesDerrotados - 1, 7)];
            this.fundoEspacial.definirProgresso(Math.min(1.0, Math.max(0.0, (double)(this.eliminados - n) / (double)this.quantidadeSetor())), this.setorAtual);
            this.fundoEspacial.atualizar(d);
        }
        if (intercepcao && emJogo) fundoEspacial.atualizar(d);
        recargaFusao=Math.max(0,recargaFusao-d);
        atualizarPoderes(d);
        if(efeitoFusao!=null){efeitoFusao.atualizar(d);if(!efeitoFusao.ativa())efeitoFusao=null;}
        atualizarSegredo(d);
        this.recargaElemental = Math.max(0.0, this.recargaElemental - d);
        if(emJogo&&(etapa==Etapa.COMBATE||etapa==Etapa.BATALHA_CHEFE)&&recargaElemental<=0){
            if(geloPressionado&&geloColetado){dispararElemental(true);}
            else if(fogoPressionado&&fogoColetado){dispararElemental(false);}
        }
        if (this.emJogo) {
            this.tempoTotal += d;
            if (this.etapa == Etapa.COMBATE) {
                if (!intercepcao) this.atualizarRitmoSetor(d);
            } else if (this.etapa == Etapa.BATALHA_CHEFE) {
                this.tempoSetor += d;
            }
            this.nave.mexer(d);
            this.explodirEscudoSeNecessario();
            if (this.espacoPressionado && (this.etapa == Etapa.COMBATE || this.etapa == Etapa.BATALHA_CHEFE)) {
                this.nave.atira();
            }
            for (Missel object : this.nave.getMisseis()) {
                if (!object.isVisible()) continue;
                object.mexer(d);
            }
            for (DisparoElemental disparoElemental2 : this.tirosElementais) {
                if (!disparoElemental2.ativo()) continue;
                disparoElemental2.mexer(d);
            }
            this.atualizarTransicao(d);
            if (this.etapa == Etapa.COMBATE) {
                this.atualizarEsquadroes(d);
            }
            if (this.etapa == Etapa.COMBATE) {
                for (ProjetilInimigo projetilInimigo2 : this.tirosInimigos) {
                    if (!projetilInimigo2.isVisible()) continue;
                    projetilInimigo2.atualizarAlvo(alvoX() + 20, alvoY() + 18);
                    projetilInimigo2.mexer(d);
                }
            } else if (this.etapa == Etapa.BATALHA_CHEFE && this.chefe != null) {
                int tirosAntes=tirosChefes.size();this.chefe.atualizar(d, alvoX(), alvoY(), this.dificuldade, this.tirosChefes);if(tirosChefes.size()>tirosAntes)SomJogo.tocar(SomJogo.Efeito.CHEFE_TIRO);
                for (TiroChefe tiroChefe2 : this.tirosChefes) {
                    if (!tiroChefe2.isVisible()) continue;
                    tiroChefe2.mexer(d);
                }
            }
            if (this.etapa == Etapa.COMBATE && !intercepcao) {
                this.proximoAsteroide -= d;
                if (this.proximoAsteroide <= 0.0 && this.asteroides.size() < 6) {
                    int n = 18 + this.aleatorio.nextInt(19);
                    this.asteroides.add(new Asteroide(960 + n, 142 + this.aleatorio.nextInt(378), n, (++meteorosGerados%3==1?7:this.aleatorio.nextInt(7))));
                    this.proximoAsteroide = 5.5 + this.aleatorio.nextDouble() * 4.5;
                }
                for (Asteroide asteroide2 : this.asteroides) {
                    if (!asteroide2.isVisible()) continue;
                    asteroide2.mexer(d);
                }
            }
            for (Item item2 : this.itens) {
                if (!item2.isVisible()) continue;
                item2.mexer(d);
            }
            if (this.etapa == Etapa.COMBATE || this.etapa == Etapa.BATALHA_CHEFE) {
                this.proximoItem -= d;
                if (this.proximoItem <= 0.0) {
                    this.criarItemPeriodico();
                    this.proximoItem = 22.0 + this.aleatorio.nextDouble() * 15.0;
                }
            }
            this.checarColisoes();
            this.explodirEscudoSeNecessario();
            this.nave.getMisseis().removeIf(missel -> !missel.isVisible());
            this.tirosElementais.removeIf(disparoElemental -> !disparoElemental.ativo());
            this.alien.removeIf(alien -> !alien.isVisible());
            this.tirosInimigos.removeIf(projetilInimigo -> !projetilInimigo.isVisible());
            this.asteroides.removeIf(asteroide -> !asteroide.isVisible());
            this.tirosChefes.removeIf(tiroChefe -> !tiroChefe.isVisible());
            this.itens.removeIf(item -> !item.isVisible());
            this.verificarProgressao();
        }
        if (this.etapa == Etapa.CREDITOS && this.cenaFinal != null) {
            this.cenaFinal.atualizar(d);
        }
        if (!this.emJogo && !this.venceu && this.cenaNave != null) {
            this.cenaNave.atualizar(d);
        }
        this.tempoOndaEscudo = Math.max(0.0, this.tempoOndaEscudo - d);
        this.tempoAviso = Math.max(0.0, this.tempoAviso - d);
        this.atualizarEfeitos(d);
        this.repaint();
    }

    private void verificarProgressao() {
        if (intercepcao || !this.emJogo || this.etapa != Etapa.COMBATE || this.chefesDerrotados >= TOTAL_SETORES) {
            return;
        }
        if (this.eliminados < FINAIS_SETORES[this.chefesDerrotados] || this.lancadosNoSetor < this.quantidadeSetor() || !this.alien.isEmpty() || this.miniChefe != null || !this.miniChefeEnfrentado) {
            return;
        }
        this.etapa = Etapa.ALERTA_CHEFE;
        this.chefePendente = this.setorAtual + 1;
        this.tempoEtapa = 4.8;
        this.tirosInimigos.clear();
        this.asteroides.clear();
        this.nave.getMisseis().clear();
        this.espacoPressionado = false;
        this.tempoAviso = 0.0;
    }

    private void atualizarTransicao(double d) {
        if (d <= 0.0 || this.etapa != Etapa.ALERTA_CHEFE && this.etapa != Etapa.APRESENTACAO_CHEFE && this.etapa != Etapa.INTERLUDIO) {
            return;
        }
        this.tempoEtapa = Math.max(0.0, this.tempoEtapa - d);
        if (this.etapa == Etapa.APRESENTACAO_CHEFE && this.apresentacao != null) {
            this.apresentacao.atualizar(d);
        }
        if (this.tempoEtapa > 0.0) {
            return;
        }
        if (this.etapa == Etapa.ALERTA_CHEFE) {
            this.apresentacao = new EntradaChefe(ORDEM_CHEFOES[this.chefePendente - 1]);
            this.etapa = Etapa.APRESENTACAO_CHEFE;
            this.tempoEtapa = 3.8;
            return;
        }
        if (this.etapa == Etapa.APRESENTACAO_CHEFE) {
            this.iniciarChefao(ORDEM_CHEFOES[this.chefePendente - 1]);
            this.apresentacao = null;
            return;
        }
        if(trofeuPendente!=0)return;
        if(chefesDerrotados==TOTAL_SETORES){emJogo=false;venceu=true;etapa=Etapa.CREDITOS;cenaFinal=new CenaFinal();MusicaJogo.trocarSeDisponivel("GoGoPowerRangers");return;}
        this.itens.clear(); capsulas.clear();
        this.tempoAviso = 0.0;
        // Ordem livre: o jogador escolhe no Mapa Estelar o proximo setor ainda nao vencido.
        escolhendoSetor = true;
        mapaEstelar.iniciarEscolha(setoresVencidos, setorAtual);
        nave.soltarTeclas(); espacoPressionado = geloPressionado = fogoPressionado = false;
    }

    private void confirmarSetor() {
        int destino = mapaEstelar.escolhaValida();
        if (destino < 0) { SomJogo.tocar(SomJogo.Efeito.ERRO); return; }
        SomJogo.tocar(SomJogo.Efeito.CONFIRMAR);
        escolhendoSetor = false;
        setorAtual = destino;
        this.etapa = Etapa.DOBRA;
        this.cenaNave = new CinematicaNave(this.nave, false, setorAtual);
        emboscadaPendente = !partidaTeste && !emboscadaUsada && chefesDerrotados >= 2
                && nave.getVida() > nave.getVidaMaxima()/3 && aleatorio.nextDouble() < .12;
    }

    private void finalizarDobra() {SomJogo.tocar(SomJogo.Efeito.SAIDA_DOBRA);
        this.fundoEspacial.definirProgresso(0, this.setorAtual);
        this.cenaNave = null;
        this.etapa = Etapa.COMBATE;
        MusicaJogo.trocarSeDisponivel(Recompensas.trilhaDoSetor(this.setorAtual));
        this.iniciarRitmoSetor();
        this.lancadosNoSetor = 0;
        this.numeroEsquadrao = 0;
        this.miniChefe = null;
        this.miniChefeEnfrentado = false;
        this.avisoMini = -1.0;
        this.esperaProximoEsquadrao = 0.0;
        this.alien.clear();
        this.lancarProximoEsquadrao();
        this.mostrarAviso("SETOR " + Math.min(TOTAL_SETORES, this.chefesDerrotados + 1) + " - NOVOS ESQUADROES!");
    }

    private void iniciarRitmoSetor() {
        segredo=null;segredoGerado=false;
        this.tempoSetor = 0.0;
        this.secaoMusicalAnterior = -1;
        double d = MusicaJogo.duracaoSegundos(Recompensas.trilhaDoSetor(this.chefesDerrotados));
        this.duracaoMusicaSetor = d > 0.0 ? d : 180.0;
    }

    private int faseMusical() {
        return Math.min(3, (int)(this.tempoSetor / Math.max(1.0, this.duracaoMusicaSetor) * 4.0));
    }

    private static String relogioSetor(double d) {
        int n = Math.max(0, (int)d);
        return String.format(Locale.ROOT, "%d:%02d", n / 60, n % 60);
    }

    private void atualizarRitmoSetor(double d) {
        this.tempoSetor += d;
        int n = this.faseMusical();
        if (n == this.secaoMusicalAnterior) {
            return;
        }
        this.secaoMusicalAnterior = n;
        if (n > 0) {
            this.mostrarAviso((new String[]{"", "RIFF: FORMACOES MAIS RAPIDAS", "SOLO: NOVA PRESSAO", "CLIMAX: SOBREVIVA!"})[n]);
        }
    }

    private void criarItemPeriodico() {
        Item.Tipo tipo;
        int n = this.aleatorio.nextInt(100);
        if (n < (this.nave.getVida() <= 3 ? 8 : 3)) {
            tipo = Item.Tipo.REPARO;
        } else if (n < 17) {
            tipo = Item.Tipo.ESCUDO;
        } else {
            return;
        }
        this.itens.add(new Item(tipo, 920, 112 + this.aleatorio.nextInt(430), false));
    }

    public void checarColisoes() {
        Object object;
        if (!this.emJogo) {
            return;
        }
        Rectangle rectangle = this.nave.getBounds();
        for (ProjetilInimigo object2 : this.tirosInimigos) {
            if (!object2.isVisible() || !object2.destrutivel()) continue;
            object = object2.getBounds();
            boolean bl = false;
            for (Missel missel : this.nave.getMisseis()) {
                if (!missel.isVisible() || !((Rectangle)object).intersects(missel.getBounds())) continue;
                missel.setVisible(false);
                bl = true;
                break;
            }
            if (!bl) {
                for (DisparoElemental disparoElemental : this.tirosElementais) {
                    if (!disparoElemental.ativo() || !((Rectangle)object).intersects(disparoElemental.getBounds())) continue;
                    SomJogo.tocar(SomJogo.Efeito.IMPACTO);disparoElemental.apagar();
                    bl = true;
                    break;
                }
            }
            if (!bl) continue;
            SomJogo.tocar(SomJogo.Efeito.EXPLOSAO);object2.setVisible(false);
            premiar(25);
            this.explosoes.add(new ExplosaoCurta((int)((RectangularShape)object).getCenterX(), (int)((RectangularShape)object).getCenterY(), 13));
            this.gerarParticulas((int)((RectangularShape)object).getCenterX(), (int)((RectangularShape)object).getCenterY(), 10);
        }
        for (Item item : this.itens) {
            if (!item.isVisible() || !rectangle.intersects(item.getBounds())) continue;
            item.setVisible(false);

            if (item.getTipo() == Item.Tipo.REPARO) {
                if(!recolherReparo()){item.setVisible(true);continue;}
                continue;
            }
            if(item.getTipo()==Item.Tipo.SUBTANQUE){if(inventario.descobrirTanque(item.origem()))SomJogo.tocar(SomJogo.Efeito.SUBTANQUE);mostrarAviso("SUBTANQUE ENCONTRADO: "+inventario.quantidadeTanques()+"/4");continue;}
            if(item.getTipo()==Item.Tipo.REATOR_CONVERGENCIA){inventario.coletarReator();SomJogo.tocar(SomJogo.Efeito.RELIQUIA);mostrarAviso("REATOR DE CONVERGENCIA ENCONTRADO");continue;}
            if(item.getTipo()==Item.Tipo.ENERGIA){if(energia.recarregar(geloColetado,fogoColetado)==0)item.setVisible(true);else SomJogo.tocar(SomJogo.Efeito.ENERGIA);continue;}
            this.coleta.put(item.getTipo(),this.coleta.containsKey(item.getTipo())?this.coleta.get(item.getTipo())+1:1);
            if (item.getTipo() == Item.Tipo.MELHORIA) {
                SomJogo.tocar(SomJogo.Efeito.EVOLUCAO);this.nave.melhorar();
                this.mostrarAviso("EVOLUCAO " + nave.getNivel() + "/8 - MODULO INTEGRADO");
                continue;
            }
            if (Recompensas.identificador(item.getTipo()) != 0) {
                this.receberTrofeu(Recompensas.identificador(item.getTipo()));
                if(etapa==Etapa.INTERLUDIO)tempoEtapa=.8;
                continue;
            }
            if (item.getTipo() == Item.Tipo.ARMA_GELO) {
                SomJogo.tocar(SomJogo.Efeito.COLETA);this.geloColetado = true;energia.restaurar(0);inventario.registrarElemental(geloColetado,fogoColetado);
                this.mostrarAviso("RAJADA DE GELO LIBERADA - Q");
                continue;
            }
            if (item.getTipo() == Item.Tipo.ARMA_FOGO) {
                SomJogo.tocar(SomJogo.Efeito.COLETA);this.fogoColetado = true;energia.restaurar(1);inventario.registrarElemental(geloColetado,fogoColetado);
                this.mostrarAviso("LANCA-CHAMAS LIBERADO - E");
                continue;
            }
            if (item.getTipo() == Item.Tipo.ARMA_2) {
                SomJogo.tocar(SomJogo.Efeito.COLETA);this.nave.coletarArmaSecundaria();
                this.mostrarAviso(item.recuperado() ? "ARMA 2 RECUPERADA!" : "DISPERSAO EQUIPADA - ESPACO: ATIRAR | F: TROCAR");
                continue;
            }
            this.nave.ativarEscudo();
            this.mostrarAviso("ESCUDO ATIVADO!");
        }
        if (this.etapa == Etapa.COMBATE) {
            block4: for (Asteroide asteroide : this.asteroides) {
                if (!asteroide.isVisible()) continue;
                if (rectangle.intersects(asteroide.getBounds())) {
                    asteroide.setVisible(false);
                    this.processarDano(this.dificuldade.ajustarDano(1), null);
                    if (!this.emJogo) {
                        return;
                    }
                }
                for (Missel missel : this.nave.getMisseis()) {
                    if (!missel.isVisible() || !missel.getBounds().intersects(asteroide.getBounds())) continue;
                    if (asteroide.receberTiro()) {
                        premiar(50);
                        if(asteroide.temSuprimento())itens.add(new Item(Item.Tipo.REPARO,asteroide.getX(),asteroide.getY(),false));
                        this.explosoes.add(new ExplosaoCurta(asteroide.getX(), asteroide.getY(), 14));
                        this.gerarParticulas(asteroide.getX(), asteroide.getY(), 18);
                    }
                    missel.setVisible(false);
                    continue block4;
                }
            }
        }
        if (this.etapa == Etapa.ALERTA_CHEFE || this.etapa == Etapa.APRESENTACAO_CHEFE || this.etapa == Etapa.INTERLUDIO) {
            return;
        }
        if (this.chefe != null) {
            if (this.chefe.consumirAcertoTentaculo(rectangle)) {
                this.gerarParticulas(this.nave.getX() + 22, this.nave.getY() + 17, 18);
                this.processarDano(this.dificuldade.ajustarDano(3), null);
                if (!this.emJogo) {
                    return;
                }
            }
            for (TiroChefe tiroChefe : this.tirosChefes) {
                if (!tiroChefe.isVisible() || !rectangle.intersects(tiroChefe.getBounds())) continue;
                tiroChefe.setVisible(false);
                this.processarDano(this.dificuldade.ajustarDano(tiroChefe.dano()), null);
                if (this.emJogo) continue;
                return;
            }
            if (this.chefe != null && rectangle.intersects(this.chefe.getBounds())) {
                this.processarDano(this.dificuldade.ajustarDano(3), null);
                if (!this.emJogo) {
                    return;
                }
            }
        } else {
            for (ProjetilInimigo projetilInimigo : this.tirosInimigos) {
                if (!projetilInimigo.isVisible() || !rectangle.intersects(projetilInimigo.getBounds())) continue;
                projetilInimigo.setVisible(false);
                if(projetilInimigo.gelado()&&nave.getInvulneravel()<=0&&nave.getTempoEscudo()<=0)nave.congelar();
                this.processarDano(this.dificuldade.ajustarDano(projetilInimigo.dano()), projetilInimigo.getDono());
                if (this.emJogo) continue;
                return;
            }
        }
        if (this.miniChefe != null && this.miniChefe.consumirDano(rectangle)) {
            this.processarDano(this.dificuldade.ajustarDano(3), null);
            if (!this.emJogo) {
                return;
            }
        }
        if (this.chefe == null) {
            for (Alien alien : this.alien) {
                if (!alien.isVisible() || !rectangle.intersects(alien.getBounds())) continue;
                if (alien.getClasse() == Alien.Classe.KAMIKAZE) {
                    alien.setVisible(false);
                    if(alien.especieEnxame()>=0)derrotasEnxame.add(new DerrotaEnxame(alien));
                    ++this.eliminados;
                    this.gerarParticulas(alien.getX() + 20, alien.getY() + 18, 32);
                    this.explosoes.add(new ExplosaoCurta(alien.getX() + 20, alien.getY() + 18, 26));
                }
                this.processarDano(this.dificuldade.ajustarDano(alien.getClasse() == Alien.Classe.KAMIKAZE ? 5 : 3), alien);
                if (this.emJogo) continue;
                return;
            }
        }
        block9: for (DisparoElemental disparoElemental : this.tirosElementais) {
            if (!disparoElemental.ativo()) continue;
            object = disparoElemental.getBounds();
            if (this.chefe != null) {
                if (!((Rectangle)object).intersects(this.chefe.getBounds())) continue;
                SomJogo.tocar(SomJogo.Efeito.IMPACTO);disparoElemental.apagar();
                if (!this.chefe.receberDano(disparoElemental.dano())) continue;
                this.derrotarChefao();
                return;
            }
            if (this.etapa != Etapa.COMBATE) continue;
            if (this.miniChefe != null && ((Rectangle)object).intersects(this.miniChefe.getBounds())) {
                SomJogo.tocar(SomJogo.Efeito.IMPACTO);disparoElemental.apagar();
                if (disparoElemental.getTipo() == DisparoElemental.Tipo.GELO) {
                    this.miniChefe.atrapalharCarga();
                }
                if (!this.miniChefe.receberImpacto((Rectangle)object,disparoElemental.dano())) continue;
                this.derrotarMiniChefe();
                continue;
            }
            for (Alien alien : this.alien) {
                int n;
                if (!alien.isVisible() || !((Rectangle)object).intersects(alien.getBounds())) continue;
                SomJogo.tocar(SomJogo.Efeito.IMPACTO);disparoElemental.apagar();
                int n2 = n = disparoElemental.getTipo() == DisparoElemental.Tipo.FOGO ? 1 : 2;
                if(disparoElemental.getTipo()==DisparoElemental.Tipo.FOGO)alien.incendiarPor(3);else alien.congelarPor(2.8);
                if (alien.receberImpacto(0,disparoElemental.dano())) {
                    this.eliminarAlien(alien, false);
                    continue block9;
                }
                alien.verificarKamikaze(this.nave.getY() + 18);
                this.gerarParticulas(alien.getX() + 20, alien.getY() + 18, 7);
                continue block9;
            }
        }
        block11: for (Missel missel : this.nave.getMisseis()) {
            if (!missel.isVisible()) continue;
            object = missel.getBounds();
            if (this.chefe != null) {
                int n;
                if (!((Rectangle)object).intersects(this.chefe.getBounds())) continue;
                n = missel.getTipo() == 2 ? 43 : ((missel.getTipo() == 1 ? 5 : 2) * this.nave.danoBalistico());
                if (missel.getTipo() == 2) {
                    this.explosaoX = missel.getX();
                    this.explosaoY = missel.getY();
                    this.tempoNuclear = 0.0;
                    this.gerarParticulas(this.explosaoX, this.explosaoY, 36);
                } else {
                    this.gerarParticulas(missel.getX(), missel.getY(), 5);
                }
                missel.setVisible(false);
                SomJogo.tocar(SomJogo.Efeito.IMPACTO);if (!this.chefe.receberDano(n)) continue;
                this.derrotarChefao();
                return;
            }
            if (this.miniChefe != null && ((Rectangle)object).intersects(this.miniChefe.getBounds())) {
                if (this.miniChefe.receberImpacto((Rectangle)object,missel.getTipo() == 2 ? 18 : (missel.getTipo() == 1 ? 3 : 2) * this.nave.danoBalistico())) {
                    this.derrotarMiniChefe();
                }
                missel.setVisible(false);
                this.gerarParticulas(missel.getX(), missel.getY(), 12);
                continue;
            }
            for (Alien alien : this.alien) {
                if (!alien.isVisible() || !((Rectangle)object).intersects(alien.getBounds())) continue;
                if (missel.getTipo() == 2) {
                    missel.setVisible(false);
                    this.explodirBombaNaTela(missel);
                } else {
                    if (alien.receberImpacto(0, this.nave.danoBalistico())) {
                        this.eliminarAlien(alien, false);
                    } else {
                        alien.verificarKamikaze(this.nave.getY() + 18);
                        this.gerarParticulas(alien.getX() + 20, alien.getY() + 18, 4);
                    }
                    SomJogo.tocar(SomJogo.Efeito.IMPACTO);missel.registrarAcerto();
                }
                if (missel.isVisible()) continue;
                continue block11;
            }
        }
    }

    private void derrotarMiniChefe() {
        if (this.miniChefe == null || (miniChefe.invasor() && !miniChefe.derrotado())) {
            return;
        }
        SomJogo.tocar(SomJogo.Efeito.EXPLOSAO_GRANDE);Rectangle rectangle = this.miniChefe.getBounds();
        soltarCapsula((int) rectangle.getCenterX(), (int) rectangle.getCenterY(), true, true);
        this.gerarParticulas((int)rectangle.getCenterX(), (int)rectangle.getCenterY(), 60);
        this.explosoes.add(new ExplosaoCurta((int)rectangle.getCenterX(), (int)rectangle.getCenterY(), 45));
        this.miniChefe = null;
        ++this.totalMiniChefesDerrotados;
        premiar(1500);
        if (this.aleatorio.nextInt(5) == 0) {
            this.itens.add(new Item(Item.Tipo.ESCUDO, rectangle.x, rectangle.y, false));
        }
        this.esperaProximoEsquadrao = 1.5;
        this.mostrarAviso(intercepcao?"COMANDANTE DESTRUIDO! ELIMINE O ENXAME RESTANTE":"SENTINELA DESTRUIDA! +1500 PONTOS");
    }

    private void iniciarChefao(int n) {SomJogo.tocar(SomJogo.Efeito.CHEFE_ENTRADA);
        this.etapa = Etapa.BATALHA_CHEFE;
        this.chefePendente = 0;
        this.tempoEtapa = 0.0;
        this.apresentacao = null;
        this.chefe = new Chefe(n, this.dificuldade);
        this.tirosInimigos.clear();
        this.tirosChefes.clear();
        this.mostrarAviso("ALERTA: " + this.chefe.getNome() + "!");
        MusicaJogo.trocarSeDisponivel(Recompensas.trilhaDoChefe(n));
    }

    private void derrotarChefao() {
        if (this.chefe == null) {
            return;
        }
        int id=chefe.getFase();cenaDerrota=new CenaDerrotaChefe(chefe);
        premiar(id*2500);++chefesDerrotados;setoresVencidos|=1<<setorAtual;trofeuPendente=id;chefe=null;
        tirosChefes.clear();tirosInimigos.clear();tirosElementais.clear();nave.getMisseis().clear();itens.clear();asteroides.clear();
        etapa=Etapa.DERROTA_CHEFE;espacoPressionado=false;geloPressionado=false;fogoPressionado=false;tempoAviso=0;
    }

    private void receberTrofeu(int n) {
        if (n < 1 || n > 8) {
            return;
        }
        SomJogo.tocar(SomJogo.Efeito.RELIQUIA);inventario.registrarChave(n);poderes.recarregar(inventario);
        if(this.trofeusAdquiridos[n]){if(trofeuPendente==n)trofeuPendente=0;return;}
        this.trofeusAdquiridos[n] = true;
        if (!partidaTeste) ProgressoJogo.registrarTrofeu(n);
        Item.Tipo tipo = Recompensas.tipo(n);
        this.coleta.put(tipo, 1);
        if (this.trofeuPendente == n) {
            this.trofeuPendente = 0;
        }
        for (Item item : this.itens) {
            if (!item.isVisible() || Recompensas.identificador(item.getTipo()) != n) continue;
            item.setVisible(false);
        }
        switch (n) {
            case 8: {
                this.nave.curar(2);
                SomJogo.tocar(SomJogo.Efeito.ESCUDO);this.nave.ativarEscudo();
                break;
            }
            case 2: {
                this.emblemaHalfordColetado = true;
                this.nave.ativarBlindagemHalford();
                break;
            }
            case 3: {
                this.insigniaEddieColetada = true;
                SomJogo.tocar(SomJogo.Efeito.ESCUDO);this.nave.ativarEscudo();
                break;
            }
            case 4: {
                this.trofeusOutrasLendas[0] = true;
                this.nave.ativarMotorLemmy();
                break;
            }
            case 5: {
                this.trofeusOutrasLendas[1] = true;
                this.nave.ativarRiffJames();
                break;
            }
            case 6: {
                this.trofeusOutrasLendas[2] = true;
                this.nave.ativarRajadaTom();
                break;
            }
            case 7: {
                this.trofeusOutrasLendas[3] = true;
                this.nave.ativarEscudoTestament();
                break;
            }
            case 1: {
                this.oculosOzzyColetados = true;
                this.nave.desbloquearAtaqueSonico();
                this.novoJogoMais = true;
                if (!partidaTeste) ProgressoJogo.desbloquearLendaria();
                break;
            }
            default: {
                throw new IllegalArgumentException("trofeu invalido");
            }
        }
        this.mostrarAviso("TROFEU: " + Recompensas.nome(n) + " - " + Recompensas.habilidade(n));
    }

    private int contarTrofeus() {
        int n = 0;
        for (int i = 1; i < this.trofeusAdquiridos.length; ++i) {
            if (!this.trofeusAdquiridos[i]) continue;
            ++n;
        }
        return n;
    }

    private void processarDano(int n, Alien alien) {
        boolean escudoAntes=nave.getTempoEscudo()>0;
        if (!this.nave.receberDano(n)) {
            if(escudoAntes&&nave.getTempoEscudo()==0&&alien!=null&&alien.isVisible()){alien.capturar(Item.Tipo.ESCUDO);mostrarAviso("INIMIGO CAPTUROU SEU ESCUDO!");}
            return;
        }
        this.gerarParticulas(this.nave.getX() + 25, this.nave.getY() + 20, 24);
        this.explosoes.add(new ExplosaoCurta(this.nave.getX() + 25, this.nave.getY() + 18, 11));
        Item.Tipo perdido=null;
        if(fogoColetado&&!inventario.retencao()){fogoColetado=false;perdido=Item.Tipo.ARMA_FOGO;}else if(geloColetado&&!inventario.retencao()){geloColetado=false;perdido=Item.Tipo.ARMA_GELO;}
        int n2=perdido==null?nave.perderModulo():1;
        if(perdido==null&&n2>=0)perdido=n2==1?Item.Tipo.ARMA_2:Item.Tipo.MELHORIA;
        if (n2 >= 0) {
            Alien alien2 = alien;
            if (alien2 == null || !alien2.isVisible() || alien2.estaArmado()) {
                alien2 = this.alienMaisProximoSemArma();
            }
            if(alien2!=null){alien2.capturar(perdido);
                this.mostrarAviso("INIMIGO ROUBOU SUA ARMA!");
            } else {
                this.mostrarAviso("MODULO DESTRUIDO: ENCONTRE OUTRO!");
            }
        } else {
            this.mostrarAviso("NAVE ATINGIDA!");
        }
        if (!this.nave.estaViva()) {
            this.cenaNave = new CinematicaNave(this.nave, true);
            this.emJogo = false;
            this.venceu = false;
            this.espacoPressionado = false;
        }
    }

    private Alien alienMaisProximoSemArma() {
        Alien alien = null;
        double d = Double.POSITIVE_INFINITY;
        for (Alien alien2 : this.alien) {
            double d2;
            if (!alien2.isVisible() || alien2.estaArmado() || alien2.getX() < 0 || alien2.getX() >= 960 || !((d2 = Math.hypot(alien2.getX() - this.nave.getX(), alien2.getY() - this.nave.getY())) < d)) continue;
            d = d2;
            alien = alien2;
        }
        return alien;
    }

    private void eliminarAlien(Alien alien, boolean bl) {SomJogo.tocar(SomJogo.Efeito.EXPLOSAO,alien.getX());
        if (!alien.isVisible()) {
            return;
        }
        Rectangle rectangle = alien.getBounds();
        int n = rectangle.x + rectangle.width / 2;
        int n2 = rectangle.y + rectangle.height / 2;
        alien.setVisible(false);
        if(alien.getEquipamento()!=null)itens.add(new Item(alien.getEquipamento(),n,n2,true));
        ++this.eliminados;
        int recompensa;
        switch(alien.getClasse()){
            case TANQUE: recompensa=250;break;
            case LASER: recompensa=200;break;
            case INTERCEPTADOR: recompensa=150;break;
            case ORGANICO: recompensa=175;break;
            case KAMIKAZE: recompensa=125;break;
            default: recompensa=100;
        }
        premiar(recompensa+(alien.evoluido()?150:0));
        soltarCapsula(n, n2, false, false);
        if (alien.getClasse() == Alien.Classe.TANQUE || alien.evoluido()) soltarCapsula(n, n2 + 14, false, false);
        double suprimento=aleatorio.nextDouble();if(suprimento<.18)itens.add(new Item(Item.Tipo.REPARO,n,n2,false));else if(suprimento<.36)itens.add(new Item(Item.Tipo.ENERGIA,n,n2,false));
        if (this.eliminados == 7 || this.eliminados % 16 == 0 && !this.geloColetado) {
            this.itens.add(new Item(Item.Tipo.ARMA_GELO, n, n2, false));
        }
        if (this.eliminados == 17 || this.eliminados % 22 == 0 && !this.fogoColetado) {
            this.itens.add(new Item(Item.Tipo.ARMA_FOGO, n, n2, false));
        }
        if(alien.especieEnxame()>=0)derrotasEnxame.add(new DerrotaEnxame(alien));
        else this.explosoes.add(new ExplosaoCurta(n, n2, bl ? 12 : 9));
        this.gerarParticulas(n, n2, bl ? 7 : 16);
        if (alien.getClasse() == Alien.Classe.KAMIKAZE) {
            this.gerarParticulas(n, n2, 22);
            this.explosoes.add(new ExplosaoCurta(n, n2, 28));
            if (Math.hypot(n - (this.nave.getX() + 20), n2 - (this.nave.getY() + 18)) < 86.0) {
                this.processarDano(2, null);
            }
        }
        if (alien.estaArmado() || alien.evoluido()) {
            this.gerarParticulas(n, n2, 24);
        } else {
            boolean bl2 = false;
            for (Item item : this.itens) {
                if (!item.isVisible() || item.getTipo() != Item.Tipo.ARMA_2) continue;
                bl2 = true;
            }
            if (!this.nave.temArmaSecundaria() && !bl2 && (this.eliminados == 16 || this.eliminados > 25 && this.eliminados % 24 == 0)) {
                this.itens.add(new Item(Item.Tipo.ARMA_2, n, n2, false));
            } else {
                int n3 = this.aleatorio.nextInt(bl ? 200 : 120);
                if (this.eliminados == 24 || this.eliminados == 100 || n3 == 0 && this.eliminados > 45) {
                    this.itens.add(new Item(Item.Tipo.MELHORIA, n, n2, false));
                } else if (n3 == 1) {
                    this.itens.add(new Item(Item.Tipo.REPARO, n, n2, false));
                } else if (n3 < 5) {
                    this.itens.add(new Item(Item.Tipo.ESCUDO, n, n2, false));
                }
            }
        }
    }

    private void explodirBombaNaTela(Missel missel) {SomJogo.tocar(SomJogo.Efeito.EXPLOSAO_GRANDE);
        if (this.miniChefe != null && this.miniChefe.receberDano(18)) {
            this.derrotarMiniChefe();
        }
        this.explosaoX = missel.getX();
        this.explosaoY = missel.getY();
        this.tempoNuclear = 0.0;
        for (Alien alien : this.alien) {
            if (!alien.isVisible()) continue;
            Rectangle rectangle = alien.getBounds();
            if (rectangle.x >= 960 || rectangle.x + rectangle.width <= 0 || rectangle.y >= 600 || rectangle.y + rectangle.height <= 72) continue;
            this.eliminarAlien(alien, true);
        }
        this.gerarParticulas(this.explosaoX, this.explosaoY, 34);
    }

    private void mostrarAviso(String string) {
        this.aviso = string;
        this.tempoAviso = 1.3;
    }

    private void gerarParticulas(int n, int n2, int n3) {
        for (int i = 0; i < n3 && this.particulas.size() < 600; ++i) {
            Color color;
            double d = this.aleatorio.nextDouble() * Math.PI * 2.0;
            double d2 = 80.0 + this.aleatorio.nextDouble() * 175.0;
            double d3 = 0.35 + this.aleatorio.nextDouble() * 0.58;
            switch (this.aleatorio.nextInt(4)) {
                case 0: {
                    color = Color.WHITE;
                    break;
                }
                case 1: {
                    color = new Color(255, 228, 111);
                    break;
                }
                case 2: {
                    color = new Color(255, 144, 43);
                    break;
                }
                default: {
                    color = new Color(252, 69, 31);
                }
            }
            this.particulas.add(new Particula(n, n2, Math.cos(d) * d2, Math.sin(d) * d2, 2 + this.aleatorio.nextInt(4), d3, color));
        }
    }

    private void atualizarEfeitos(double d) {
        for(DerrotaEnxame e:derrotasEnxame)e.atualizar(d);derrotasEnxame.removeIf(DerrotaEnxame::terminou);
        Object object;
        this.tempoNuclear = Math.min(0.72, this.tempoNuclear + d);
        Iterator<?> iterator = this.explosoes.iterator();
        while (iterator.hasNext()) {
            object = iterator.next();
            ((ExplosaoCurta)object).tempo += d;
            double d2 = ((ExplosaoCurta)object).tempo;
            Objects.requireNonNull(object);
            if (!(d2 >= 0.28)) continue;
            iterator.remove();
        }
        iterator = this.particulas.iterator();
        while (iterator.hasNext()) {
            object = (Particula)iterator.next();
            ((Particula)object).tempo -= d;
            if (((Particula)object).tempo <= 0.0) {
                iterator.remove();
                continue;
            }
            ((Particula)object).x += ((Particula)object).vx * d;
            ((Particula)object).y += ((Particula)object).vy * d;
            ((Particula)object).vx *= Math.max(0.0, 1.0 - 1.9 * d);
            ((Particula)object).vy *= Math.max(0.0, 1.0 - 1.9 * d);
        }
    }

    private CenaInterdicao novaInterdicao(){rainhaInterdicao=aleatorio.nextBoolean();return new CenaInterdicao(nave,rainhaInterdicao);}

    private void desenharInterdicao(Graphics2D g) {
        if (cenaInterdicao != null) cenaInterdicao.desenhar(g);
    }

    private void iniciarCombateIntercepcao() {
        if(cenaInterdicao!=null) fundoEspacial=cenaInterdicao.getFundo();
        cenaInterdicao=null;nave.reposicionarAposInterdicao();
        intercepcao=true;saidaIntercepcao=0;eliminadosAntesIntercepcao=eliminados;
        etapa=Etapa.COMBATE;cenaNave=null;alien.clear();itens.clear();asteroides.clear();
        tirosInimigos.clear();tirosChefes.clear();tirosElementais.clear();nave.getMisseis().clear();
        espacoPressionado=geloPressionado=fogoPressionado=false;nave.soltarTeclas();
        fundoEspacial.definirProgresso(0,6);miniChefeEnfrentado=true;
        miniChefe=new MiniChefe(Math.max(1,chefesDerrotados+1),true,rainhaInterdicao);miniChefe.posicionarInterdicao();
        reforcosEnxame=60;esperaEnxame=0;ultimaFormacaoEnxame=-1;
        lancarEnxame();
        mostrarAviso("ALERTA: "+miniChefe.nome()+" E ENXAME!");
    }

    private void lancarEnxame(){
        int vivos=(int)alien.stream().filter(Alien::isVisible).count();
        int quantidade=Math.min(reforcosEnxame,Math.min(10,18-vivos));
        if(quantidade<=0){esperaEnxame=.5;return;}
        int padrao;do{padrao=aleatorio.nextInt(8);}while(padrao==ultimaFormacaoEnxame);
        ultimaFormacaoEnxame=padrao;
        Alien.Classe[] classes={Alien.Classe.INTERCEPTADOR,Alien.Classe.TANQUE,Alien.Classe.KAMIKAZE,Alien.Classe.KAMIKAZE,Alien.Classe.LASER,Alien.Classe.ESQUADRAO};
        int especieBase=aleatorio.nextInt(6);
        double centro=265+aleatorio.nextInt(110);
        for(int i=0;i<quantidade;i++){
            int especie=(especieBase+i+aleatorio.nextInt(3))%6;
            Alien a=new Alien(1020+i*45,125+aleatorio.nextInt(370),Alien.Tipo.PIRATA,classes[especie]);
            a.definirEnxame(especie);
            double ang=i*Math.PI*2/quantidade;
            double ax=padrao%3==0?625+Math.cos(ang)*145:480+(i%4)*85;
            double ay=padrao%3==0?centro+Math.sin(ang)*145:145+(i/4)*125+aleatorio.nextInt(35);
            a.entrarEmEsquadrao(ax,Math.max(130,Math.min(495,ay)),i,padrao,1.1+miniChefe.estagio()*.15);alien.add(a);
        }
        reforcosEnxame-=quantidade;esperaEnxame=4.2-miniChefe.estagio()*.6;
    }

    private void voltarAoMenuInicial() {SomJogo.pausar(false);SomJogo.limpar();SomJogo.tocar(SomJogo.Efeito.VOLTAR);
        timer.stop(); nave.soltarTeclas(); espacoPressionado = geloPressionado = fogoPressionado = false;
        java.awt.Window janela = javax.swing.SwingUtilities.getWindowAncestor(this);
        if (janela != null) janela.dispose();
        if (!java.awt.GraphicsEnvironment.isHeadless()) TelaInicial.abrir();
    }

    private void reiniciar() {
        partidaTeste = false; painelTeste = false;
        emboscadaUsada = emboscadaPendente = intercepcao = false; tempoInterdicao = saidaIntercepcao = 0;
        cenaDerrota=null;geloPressionado=false;fogoPressionado=false;
        this.nave = new Nave();
        aplicarInventario();
        this.nave.ativarLendaria(this.usarLendaria && ProgressoJogo.lendariaDisponivel());
        this.nave.definirModeloVisual(this.modeloVisual);
        aplicarInventario();
        this.pausado = false;
        this.abaPausa = 0; mapaEstelar.redefinir(); cenaInterdicao=null;
        this.coleta.clear();
        this.ultimoPadraoEsquadrao = -1;
        Missel.setTipoTiro(0);
        this.emJogo = true;
        this.venceu = false;
        this.espacoPressionado = false;
        this.pontos = 0;creditos=0;kitsReparo=0;meteorosGerados=0;iniciais="";placarConfirmado=false;mostrarPlacarFinal=false;erroPlacar="";
        this.eliminados = 0;
        this.tempoTotal = 0.0;
        this.tempoAviso = 0.0;
        this.proximoItem = 22.0;
        this.proximoAsteroide = 8.0;
        this.miniChefe = null;
        this.miniChefeEnfrentado = false;
        this.avisoMini = -1.0;
        this.totalMiniChefesDerrotados = 0;
        this.fogoColetado = false;
        this.geloColetado = false;
        this.recargaElemental = 0.0;
        this.tirosElementais.clear();
        this.trofeuPendente = 0;
        this.asteroides.clear();
        this.tempoOndaEscudo = 0.0;
        this.chefe = null;
        this.cenaFinal = null;
        this.chefesDerrotados = 0; setorAtual = 0; setoresVencidos = 0; escolhendoSetor = false;
        this.oculosOzzyColetados = false;
        this.emblemaHalfordColetado = false;
        this.insigniaEddieColetada = false;
        Arrays.fill(this.trofeusOutrasLendas, false);
        Arrays.fill(this.trofeusAdquiridos, false);
        if (this.novoJogoMais) {
            this.nave.desbloquearAtaqueSonico();
            this.oculosOzzyColetados = true;
            this.trofeusAdquiridos[1] = true;
        }
        this.etapa = Etapa.COMBATE;
        this.apresentacao = null;
        this.tempoEtapa = 0.0;
        this.chefePendente = 0;
        this.tirosChefes.clear();
        this.tirosInimigos.clear();
        this.itens.clear(); capsulas.clear();
        this.explosoes.clear();derrotasEnxame.clear();
        this.particulas.clear();
        this.tempoNuclear = 0.72;
        this.cenaNave = null;
        inventario = new ProgressaoNave();energia=new Energia098();poderes=new Poderes099();recargaFusao=0;efeitoFusao=null;oficinaAberta=false;cursorPausa=0;aplicarInventario();
        this.iniciarRitmoSetor();
        this.fundoEspacial.reiniciar();
        this.ultimoQuadroNanos = System.nanoTime();
        this.inicializaInimigos();
        MusicaJogo.trocarSeDisponivel("HammerFall");
        this.requestFocusInWindow();
    }

    int getPontos() {
        return this.pontos;
    }

    boolean temTrofeuOutraLendaTeste(int n) {
        return this.trofeusOutrasLendas[n];
    }

    boolean temTrofeuTeste(int n) {
        return this.trofeusAdquiridos[n];
    }

    int getTrofeuPendenteTeste() {
        return this.trofeuPendente;
    }

    boolean novoJogoMaisTeste() {
        return this.novoJogoMais;
    }

    void receberTrofeuTeste(int n) {
        this.receberTrofeu(n);
    }

    int getTotalSetoresTeste() {
        return TOTAL_SETORES;
    }

    int getProximoChefeCampanhaTeste() {
        return ORDEM_CHEFOES[Math.min(this.chefesDerrotados, ORDEM_CHEFOES.length - 1)];
    }

    boolean creditosAtivosTeste() {
        return this.etapa == Etapa.CREDITOS && this.cenaFinal != null;
    }

    void avancarCreditosTeste(double d) {
        if (this.cenaFinal != null) {
            this.cenaFinal.atualizar(d);
        }
    }

    int getEliminados() {
        return this.eliminados;
    }

    Nave getNave() {
        return this.nave;
    }

    List<Alien> getAliens() {
        return this.alien;
    }

    List<Item> getItens() {
        return this.itens;
    }

    boolean isEmJogo() {
        return this.emJogo;
    }

    int getVidaPersistenteTeste() {
        return this.nave.getVida();
    }

    int getFaseMusicalTeste() {
        return this.faseMusical();
    }

    String getEtapaCinematicaTeste() {
        return this.etapa.name();
    }

    boolean emDestruicaoTeste() {
        return !this.emJogo && this.cenaNave != null && !this.cenaNave.terminou();
    }

    void danificarNaveTeste(int n) {
        this.nave.danoDiretoTeste(n);
    }

    void entrarDobraTeste() {
        this.etapa = Etapa.DOBRA;
        this.cenaNave = new CinematicaNave(this.nave, false, this.chefesDerrotados);
        emboscadaPendente = !partidaTeste && !emboscadaUsada && chefesDerrotados >= 2
                && nave.getVida() > nave.getVidaMaxima()/3 && aleatorio.nextDouble() < .12;
    }

    void atualizarDobraTeste(double d) {
        if (this.cenaNave != null) {
            this.cenaNave.atualizar(d);
            if (this.cenaNave.terminou()) {
                this.finalizarDobra();
            }
        }
    }

    void pararTimerTeste() {
        this.timer.stop();
    }

    void simularDanoTeste(Alien alien, int n) {
        this.processarDano(n, alien);
    }

    void detonarTeste(Missel missel) {
        this.explodirBombaNaTela(missel);
    }

    Chefe getChefeTeste() {
        return this.chefe;
    }

    void iniciarChefeTeste(int n) {
        this.iniciarChefao(n);
    }

    int getChefesDerrotadosTeste() {
        return this.chefesDerrotados;
    }

    String getEtapaTeste() {
        return this.etapa.name();
    }

    void verificarProgressaoTeste() {
        this.verificarProgressao();
    }

    void avancarTransicaoTeste(double d) {
        this.atualizarTransicao(d);
    }

    int getChefePendenteTeste() {
        return this.chefePendente;
    }

    EntradaChefe getApresentacaoTeste() {
        return this.apresentacao;
    }

    int totalTirosChefeTeste() {
        return this.tirosChefes.size();
    }

    void tiroNoChefeTeste(int n) {
        int n2;
        if (this.chefe == null) {
            throw new IllegalStateException("sem chefe");
        }
        if (this.chefe.getEntrada() < 1.0) {
            this.chefe.atualizar(2.0, 50, 280, this.dificuldade, this.tirosChefes);
        }
        if (this.chefe.getFormaMetalTeste() == 2) {
            this.chefe.atualizar(1.55, 50, 280, this.dificuldade, this.tirosChefes);
        }
        n2 = n == 2 ? 43 : (n == 1 ? 5 : 2);
        if (this.chefe.receberDano(n2)) {
            this.derrotarChefao();
        }
    }

    public void selecionarModeloNave(int n) {
        if (!ProgressoJogo.modeloDisponivel(n)) {
            return;
        }
        this.modeloVisual = n;
        this.nave.definirModeloVisual(n);
        this.selecionarNaveLendaria(n == 4);
    }

    public void selecionarNaveLendaria(boolean bl) {
        this.usarLendaria = bl && ProgressoJogo.lendariaDisponivel();
        this.nave.ativarLendaria(this.usarLendaria);
    }

    void modoTeste(Dificuldade dificuldade) {
        this.reiniciar();
    }

    void definirPausaTeste(boolean bl) {
        this.pausado = bl;
    }

    void darArmaElementalTeste(DisparoElemental.Tipo tipo) {
        if (tipo == DisparoElemental.Tipo.FOGO) {
            this.fogoColetado = true;
        } else {
            this.geloColetado = true;
        }
    }

    boolean pausadoTeste() {
        return this.pausado;
    }

    int abaPausaTeste() {
        return this.abaPausa;
    }

    void selecionarAbaTeste(int n) {
        this.abaPausa = n;
    }

    int coletadosTeste(Item.Tipo tipo) {
        return this.coleta.containsKey((Object)tipo) ? this.coleta.get((Object)tipo) : 0;
    }

    int formacaoTeste() {
        return this.padraoEsquadrao;
    }

    MiniChefe getMiniChefeTeste() {
        return this.miniChefe;
    }

    int getMiniChefesDerrotadosTeste() {
        return this.totalMiniChefesDerrotados;
    }

    void invocarMiniChefeTeste() {
        this.miniChefeEnfrentado = true;
        this.miniChefe = new MiniChefe(2);
    }

    void mostrarConquistaTeste(int n) {
        this.trofeuPendente = n;
        this.etapa = Etapa.INTERLUDIO;
        this.tempoEtapa = 3.0;
    }

    void destruirMiniChefeTeste() {
        this.derrotarMiniChefe();
    }

    int totalAsteroidesTeste() {
        return this.asteroides.size();
    }

    int totalDisparosInimigosTeste() {
        return this.tirosInimigos.size();
    }

    void simularFormacaoTeste(double d) {
        this.atualizarEsquadroes(d);
    }

    int getLancadosNoSetorTeste() {
        return this.lancadosNoSetor;
    }

    int getQuantidadeSetorTeste() {
        return this.quantidadeSetor();
    }

    Dificuldade getDificuldadeTeste() {
        return this.dificuldade;
    }

    void proximoEsquadraoTeste() {
        this.alien.removeIf(alien -> !alien.isVisible());
        if (this.alien.isEmpty() && this.lancadosNoSetor < this.quantidadeSetor()) {
            this.lancarProximoEsquadrao();
        }
    }

    void eliminarEsquadraoTeste() {
        for (Alien alien : new ArrayList<Alien>(this.alien)) {
            this.eliminarAlien(alien, false);
        }
        this.proximoEsquadraoTeste();
    }

    void selecionarDificuldadeTeste(Dificuldade dificuldade) {
    }

    void adicionarAsteroideTeste(Asteroide asteroide) {
        this.asteroides.add(asteroide);
    }

    void explodirEscudoTeste() {
        this.explodirEscudoSeNecessario();
    }

    private CenaDerrotaChefe cenaDerrota;
    private boolean geloPressionado,fogoPressionado;
    private static enum Etapa {
        INTERDICAO,
        DERROTA_CHEFE,
        COMBATE,
        ALERTA_CHEFE,
        APRESENTACAO_CHEFE,
        BATALHA_CHEFE,
        INTERLUDIO,
        DOBRA,
        CREDITOS;

    }

    private class TecladoAdapter
    extends KeyAdapter {
        private TecladoAdapter() {
        }

        @Override
        public void keyPressed(KeyEvent keyEvent) {
            int codigo = keyEvent.getKeyCode();
            if (escolhendoSetor) {
                if (codigo == KeyEvent.VK_ENTER || codigo == KeyEvent.VK_SPACE) confirmarSetor();
                else if (codigo == KeyEvent.VK_LEFT || codigo == KeyEvent.VK_UP) mapaEstelar.escolherProximo(-1);
                else if (codigo == KeyEvent.VK_RIGHT || codigo == KeyEvent.VK_DOWN) mapaEstelar.escolherProximo(1);
                else mapaEstelar.tecla(codigo);
                repaint(); return;
            }
            if(codigo==KeyEvent.VK_F9||codigo==KeyEvent.VK_F10){
                if(!teclasTeste.add(codigo))return;
                if(codigo==KeyEvent.VK_F9&&emJogo){pausado=true;SomJogo.pausar(true);nave.soltarTeclas();espacoPressionado=geloPressionado=fogoPressionado=false;}
                PainelSom.tecla(codigo);repaint();return;
            }
            if(PainelSom.aberto()){PainelSom.tecla(codigo);repaint();return;}
            if (codigo == KeyEvent.VK_F8) {
                if (!teclasTeste.add(codigo)) return;
                if (!painelTeste) {
                    pausaAntesTeste = pausado; pausado = true; painelTeste = true;
                    nave.soltarTeclas(); espacoPressionado = geloPressionado = fogoPressionado = false;
                } else { painelTeste = false; pausado = pausaAntesTeste; }
                repaint(); return;
            }
            if (painelTeste) {
                if (!teclasTeste.add(codigo)) return;
                if (codigo >= KeyEvent.VK_1 && codigo <= KeyEvent.VK_8) setorTeste = codigo - KeyEvent.VK_0;
                else if (codigo == KeyEvent.VK_LEFT) setorTeste = setorTeste == 1 ? 8 : setorTeste - 1;
                else if (codigo == KeyEvent.VK_RIGHT) setorTeste = setorTeste == 8 ? 1 : setorTeste + 1;
                else if (codigo == KeyEvent.VK_ESCAPE) { painelTeste = false; pausado = pausaAntesTeste; }
                else if (codigo == KeyEvent.VK_N) reiniciar();
                else if (codigo == KeyEvent.VK_ENTER || codigo == KeyEvent.VK_S || codigo == KeyEvent.VK_B || codigo == KeyEvent.VK_D || codigo == KeyEvent.VK_I)
                    iniciarSessaoTeste(setorTeste, codigo);
                repaint(); return;
            }
            if (partidaTeste && !emJogo && codigo == KeyEvent.VK_ENTER) {
                iniciarSessaoTeste(setorTeste, KeyEvent.VK_ENTER); return;
            }

            if(!emJogo){
                if(venceu&&!mostrarPlacarFinal){if(keyEvent.getKeyCode()==KeyEvent.VK_ENTER && cenaFinal!=null&&cenaFinal.podeEncerrar())mostrarPlacarFinal=true;return;}
                if(!venceu&&cenaNave!=null&&!cenaNave.terminou())return;
                int k=keyEvent.getKeyCode();
                if(k==KeyEvent.VK_ENTER){
                    if(placarConfirmado||pontos==0){reiniciar();return;}
                    if(!partidaTeste && iniciais.length()==3){placarConfirmado=placar.salvar(iniciais,pontos,"CAMPANHA"+(novoJogoMais?"+":""));erroPlacar=placarConfirmado?"":"FALHA AO SALVAR. ENTER: TENTAR NOVAMENTE";}
                }else if(!placarConfirmado){
                    erroPlacar="";
                    if(k==KeyEvent.VK_BACK_SPACE&&!iniciais.isEmpty())iniciais=iniciais.substring(0,iniciais.length()-1);
                    else if(iniciais.length()<3&&((k>=65&&k<=90)||(k>=48&&k<=57)))iniciais+=(char)k;
                }
                repaint();return;
            }
            if(codigo==KeyEvent.VK_C&&!pausado){if(teclasTeste.add(codigo))usarPoderDoChefe();return;}
            if(codigo==KeyEvent.VK_X&&!pausado){if(teclasTeste.add(codigo))poderes.proximo(inventario);return;}
            if(codigo==KeyEvent.VK_V&&!pausado){if(teclasTeste.add(codigo))ativarFusao();return;}
            if(codigo==KeyEvent.VK_R&&!pausado&&(etapa==Etapa.COMBATE||etapa==Etapa.BATALHA_CHEFE)){
                if(teclasTeste.add(codigo))usarSubtanque();return;
            }
            if (keyEvent.getKeyCode() == KeyEvent.VK_ENTER || keyEvent.getKeyCode() == 80 || keyEvent.getKeyCode() == 27) {
                if (!teclasTeste.add(keyEvent.getKeyCode())) return;
                if (Fase.this.etapa != Etapa.CREDITOS) {
                    Fase.this.pausado = !Fase.this.pausado;SomJogo.pausar(pausado);SomJogo.tocar(pausado?SomJogo.Efeito.PAUSA:SomJogo.Efeito.RETOMAR);
                    Fase.this.espacoPressionado = false;geloPressionado=false;fogoPressionado=false;
                    Fase.this.nave.soltarTeclas();
                    Fase.this.repaint();
                }
                return;
            }
            if (Fase.this.pausado) {
                if(mapaAberto() && mapaEstelar.tecla(codigo)){repaint();return;}
                if(codigo==KeyEvent.VK_M){voltarAoMenuInicial();return;}
                if(codigo==KeyEvent.VK_TAB){if(teclasTeste.add(codigo)){abaPausa=(abaPausa+1)%6;cursorPausa=0;}repaint();return;}
                if(codigo>=KeyEvent.VK_1&&codigo<=KeyEvent.VK_6){abaPausa=codigo-KeyEvent.VK_1;cursorPausa=0;repaint();return;}
                int total=abaPausa==4?6:abaPausa==0||abaPausa==2?8:4;
                if(codigo==KeyEvent.VK_UP||codigo==KeyEvent.VK_LEFT)cursorPausa=(cursorPausa+total-1)%total;
                if(codigo==KeyEvent.VK_DOWN||codigo==KeyEvent.VK_RIGHT)cursorPausa=(cursorPausa+1)%total;
                if(codigo==KeyEvent.VK_O){abaPausa=4;cursorPausa=0;}
                if(codigo==KeyEvent.VK_R && abaPausa==1 && teclasTeste.add(codigo))usarSubtanque();
                if(codigo==KeyEvent.VK_SPACE && teclasTeste.add(codigo)){
                    if(abaPausa==0){poderes.selecionar(cursorPausa,inventario);mostrarAviso("PODER EQUIPADO: "+Poderes099.NOMES[poderes.selecionado]);}
                    if(abaPausa==4){
                        if(inventario.comprarMercado(nave.getModeloVisual(),cursorPausa)){
                            SomJogo.tocar(SomJogo.Efeito.COMPRA);aplicarInventario();
                            mostrarAviso("PECA INSTALADA PERMANENTEMENTE / SALVA");
                        }else mostrarAviso("CREDITOS INSUFICIENTES OU NIVEL MAXIMO");
                    }
                }
                repaint();return;
            }
            if (!Fase.this.emJogo) {
                return;
            }
            if (keyEvent.getKeyCode() == 81 || keyEvent.getKeyCode() == 69) {
                if(keyEvent.getKeyCode()==81)geloPressionado=true;else fogoPressionado=true;
                if(etapa==Etapa.COMBATE||etapa==Etapa.BATALHA_CHEFE)dispararElemental(codigo==81);
            } else if (keyEvent.getKeyCode() == 32) {
                Fase.this.espacoPressionado = true;
            } else {
                Fase.this.nave.keyPressed(keyEvent);
            }
        }

        @Override
        public void keyReleased(KeyEvent keyEvent) {
            teclasTeste.remove(keyEvent.getKeyCode());
            if(keyEvent.getKeyCode()==81)geloPressionado=false;if(keyEvent.getKeyCode()==69)fogoPressionado=false;
            if (keyEvent.getKeyCode() == 32) {
                Fase.this.espacoPressionado = false;
            }
            if (Fase.this.emJogo && !Fase.this.pausado) {
                Fase.this.nave.keyReleased(keyEvent);
            }
        }
    }

    private static final class ExplosaoCurta {
        final int x;
        final int y;
        final int tamanho;
        final double duracao = 0.28;
        double tempo;

        ExplosaoCurta(int n, int n2, int n3) {SomJogo.tocar(SomJogo.Efeito.EXPLOSAO,n);
            this.x = n;
            this.y = n2;
            this.tamanho = n3;
        }
    }

    private static final class Particula {
        double x;
        double y;
        double vx;
        double vy;
        final double tamanho;
        final double duracao;
        double tempo;
        final Color cor;

        Particula(double d, double d2, double d3, double d4, double d5, double d6, Color color) {
            this.x = d;
            this.y = d2;
            this.vx = d3;
            this.vy = d4;
            this.tamanho = d5;
            this.duracao = d6;
            this.tempo = d6;
            this.cor = color;
        }
    }
}

