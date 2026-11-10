package Jogo;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.util.Random;

/** Cartografia ficcional: camera continua, sistemas e orbitas em coordenadas persistentes. */
final class MapaEstelar {
 static final String[] NOMES={"ABISMO","FORNALHA","CRISTAIS","SUCATA","GEADA","COLMEIA","VORTICE","CITADELA"};
 /** Subgenero do metal de cada sistema (docs/SUBGENEROS.md). */
 static final String[] GENEROS={"DOOM METAL","HEAVY / SPEED","POWER EPICO","INDUSTRIAL","THRASH METAL","DEATH METAL","FOLK METAL","SINFONICO"};
 static final Color[] CORES={new Color(60,170,255),new Color(255,85,40),new Color(90,240,230),new Color(220,165,65),new Color(160,215,255),new Color(90,220,90),new Color(185,105,255),new Color(255,65,75)};
 static final double[][] SISTEMAS={{-830,390},{-640,-210},{-270,-650},{410,-510},{930,-120},{680,550},{120,790},{35,60}};
 private static final Rectangle VISTA=new Rectangle(16,82,706,440);
 private static final double MIN_ZOOM=.025,MAX_ZOOM=160;
 private static final BufferedImage GALAXIA=criarGalaxia();
 private static final BufferedImage[][] PLANETAS=criarPlanetas();
 /** Estrela de cada sistema em pixel art (mapa_estrela_0..7.png); sem a arte, fica a bolinha antiga. */
 private static final BufferedImage[] ESTRELAS=new BufferedImage[8];
 /** 12 planetas em pixel art (0..9 comuns, 10 e 11 com anel) e o marcador da nave (4 quadros). */
 private static final BufferedImage[] ARTE_PLANETA=new BufferedImage[12];private static BufferedImage NAVE_MAPA;
 static{for(int i=0;i<12;i++)ARTE_PLANETA[i]=ler("mapa_planeta_"+i+".png");NAVE_MAPA=ler("mapa_nave.png");}
 private static BufferedImage ler(String n){java.net.URL u=MapaEstelar.class.getResource("/ImagemIcon/"+n);if(u==null)return null;try{return javax.imageio.ImageIO.read(u);}catch(java.io.IOException e){return null;}}
 /** Tema de cada sistema: o primeiro planeta usa a cara do setor; os outros variam. */
 private static final int[] TEMA={3,1,5,8,2,6,9,0};
 static BufferedImage estrela(int i){return ESTRELAS[Math.max(0,Math.min(7,i))];}
 static BufferedImage planeta(int i,int j){return arteDoPlaneta(i,j);}
 /** Planetas proprios de cada sistema (planetas_sistema_N.png: tira com um planeta por integrante e o do chefe). */
 private static final BufferedImage[][] DO_SISTEMA=new BufferedImage[8][];
 static{for(int i=0;i<8;i++){BufferedImage t=ler("planetas_sistema_"+i+".png");if(t==null)continue;int n=Turne.planetas(i),fw=t.getWidth()/n;DO_SISTEMA[i]=new BufferedImage[n];for(int j=0;j<n;j++)DO_SISTEMA[i][j]=t.getSubimage(j*fw,0,fw,t.getHeight());}}
 private static BufferedImage arteDoPlaneta(int i,int j){
  if(DO_SISTEMA[i]!=null&&j>=0&&j<DO_SISTEMA[i].length)return DO_SISTEMA[i][j];
  if(Turne.anel(i,j))return ARTE_PLANETA[i%2==0?11:10];
  int k=j==0?TEMA[i]:(TEMA[i]+j*3+i)%10;return ARTE_PLANETA[k]!=null?ARTE_PLANETA[k]:PLANETAS[i][j];
 }
 static{for(int i=0;i<8;i++){java.net.URL u=MapaEstelar.class.getResource("/ImagemIcon/mapa_estrela_"+i+".png");if(u!=null)try{ESTRELAS[i]=javax.imageio.ImageIO.read(u);}catch(java.io.IOException e){}}}
 private double cameraX,cameraY,zoom=.19,alvoX,alvoY,alvoZoom=.19;
 private double tempo,viagem;
 private int atual,selecionado,planeta=-1;
 int selecionado(){return selecionado;}
 private boolean dobra,foraRota,arrastando,arrastou;
 /** Modo de escolha do proximo setor: setores vencidos ficam bloqueados. */
 private boolean escolha;private int vencidos;
 static final String[] CHEFES={"LULA DO ABISMO","VALDRAK","ALMIRANTE KORVUS","KROVAX","HEXAR","MORGATH","KANOA","MALACHAR"};
 void iniciarEscolha(int setoresVencidos,int atualSetor){tocando=-1;escolha=true;vencidos=setoresVencidos;atual=atualSetor;selecionado=atualSetor;escolherProximo(1);visaoGalactica();}
 boolean vencido(int i){return (vencidos&(1<<i))!=0;}
 void escolherProximo(int passo){for(int n=1;n<=8;n++){int i=Math.floorMod(selecionado+passo*n,8);if(!vencido(i)){selecionado=i;planeta=-1;alvoX=SISTEMAS[i][0]*.35;alvoY=SISTEMAS[i][1]*.35;return;}}}
 int escolhaValida(){if(!escolha||vencido(selecionado))return -1;escolha=false;return selecionado;}
 private int ultimoX,ultimoY;

 /** Planeta da turne em que a nave esta no setor atual (os anteriores ja foram cancelados). */
 private int planetaTurne;
 void planetaTurne(int p){planetaTurne=Math.max(0,p);}
 /** Integrante (ou chefe) do planeta j do sistema i ja foi derrotado. */
 private boolean cancelado(int i,int j){return vencido(i)||(i==atual&&j<planetaTurne);}
 void estado(int setor,boolean emDobra,boolean fora,double fracao){
  atual=Math.max(0,Math.min(7,setor));dobra=emDobra;foraRota=fora;
  viagem=Math.max(0,Math.min(1,fracao));
 }
 void atualizar(double dt){
  double f=1-Math.exp(-10*Math.max(0,dt));tempo+=Math.max(0,dt);orbita+=Math.max(0,dt);
  // aproximado num planeta: a camera acompanha ele na orbita
  if(planeta>=0&&alvoZoom>=100){double[] p=posicaoPlaneta(selecionado,planeta);alvoX=p[0];alvoY=p[1];}
  cameraX+=(alvoX-cameraX)*f;cameraY+=(alvoY-cameraY)*f;
  zoom+=(alvoZoom-zoom)*f;
 }
 void redefinir(){escolha=false;vencidos=0;cameraX=cameraY=alvoX=alvoY=0;zoom=alvoZoom=.19;selecionado=atual=0;planeta=-1;arrastando=false;}
 private double mundoX(int x){return cameraX+(x-VISTA.getCenterX())/zoom;}
 private double mundoY(int y){return cameraY+(y-VISTA.getCenterY())/zoom;}
 private int telaX(double x){return (int)Math.round(VISTA.getCenterX()+(x-cameraX)*zoom);}
 private int telaY(double y){return (int)Math.round(VISTA.getCenterY()+(y-cameraY)*zoom);}
 private static double limitar(double v,double a,double b){return Math.max(a,Math.min(b,v));}
 void roda(int x,int y,double giros){
  if(!VISTA.contains(x,y))return;
  double novo=limitar(alvoZoom*Math.pow(1.35,-giros),MIN_ZOOM,MAX_ZOOM);
  double wx=mundoX(x),wy=mundoY(y);
  alvoX=limitar(wx-(x-VISTA.getCenterX())/novo,-9000,9000);
  alvoY=limitar(wy-(y-VISTA.getCenterY())/novo,-7000,7000);alvoZoom=novo;
 }
 void pressionar(int x,int y){arrastando=VISTA.contains(x,y);arrastou=false;ultimoX=x;ultimoY=y;}
 void arrastar(int x,int y){
  if(!arrastando)return;
  int dx=x-ultimoX,dy=y-ultimoY;if(Math.abs(dx)+Math.abs(dy)>1)arrastou=true;
  cameraX=alvoX=limitar(cameraX-dx/zoom,-9000,9000);
  cameraY=alvoY=limitar(cameraY-dy/zoom,-7000,7000);ultimoX=x;ultimoY=y;
 }
 void soltar(){arrastando=false;}
 void localizar(){selecionado=atual;planeta=-1;alvoX=posicaoJogador()[0];alvoY=posicaoJogador()[1];alvoZoom=3.5;}
 void visaoGalactica(){alvoX=alvoY=0;alvoZoom=.19;planeta=-1;}
 private void aproximarSelecionado(){alvoX=SISTEMAS[selecionado][0];alvoY=SISTEMAS[selecionado][1];alvoZoom=7;planeta=-1;}
 boolean tecla(int k){
  switch(k){
   case KeyEvent.VK_ADD:case KeyEvent.VK_EQUALS:roda(369,302,-1);return true;
   case KeyEvent.VK_SUBTRACT:case KeyEvent.VK_MINUS:roda(369,302,1);return true;
   case KeyEvent.VK_F:localizar();return true;
   case KeyEvent.VK_G:visaoGalactica();return true;
   case KeyEvent.VK_Z:aproximarSelecionado();return true;
   case KeyEvent.VK_BACK_SPACE:alvoZoom=Math.max(MIN_ZOOM,alvoZoom/4);planeta=-1;return true;
   case KeyEvent.VK_LEFT:alvoX=limitar(alvoX-90/zoom,-9000,9000);return true;
   case KeyEvent.VK_RIGHT:alvoX=limitar(alvoX+90/zoom,-9000,9000);return true;
   case KeyEvent.VK_UP:alvoY=limitar(alvoY-90/zoom,-7000,7000);return true;
   case KeyEvent.VK_DOWN:alvoY=limitar(alvoY+90/zoom,-7000,7000);return true;
   default:return false;
  }
 }
 void clicar(int x,int y,int cliques){
  if(arrastou){arrastou=false;return;}
  if(new Rectangle(744,423,192,29).contains(x,y)){localizar();return;}
  if(new Rectangle(744,460,192,29).contains(x,y)){visaoGalactica();return;}
  if(new Rectangle(744,386,192,29).contains(x,y)){aproximarSelecionado();return;}
  if(!VISTA.contains(x,y))return;
  double melhor=22;int escolhido=-1,corpo=-1;
  for(int i=0;i<8;i++){
   double d=Math.hypot(x-telaX(SISTEMAS[i][0]),y-telaY(SISTEMAS[i][1]));
   if(d<melhor){melhor=d;escolhido=i;corpo=-1;}
   if(zoom>1.2)for(int j=0;j<Turne.planetas(i);j++){
    double[] pos=posicaoPlaneta(i,j);
    d=Math.hypot(x-telaX(pos[0]),y-telaY(pos[1]));
    if(d<Math.max(12,raioPlaneta(j)*zoom)&&d<melhor){melhor=d;escolhido=i;corpo=j;}
   }
  }
  if(escolhido>=0){
   selecionado=escolhido;planeta=corpo;
   if(cliques>=2){
    double[] pos=corpo<0?SISTEMAS[escolhido]:posicaoPlaneta(escolhido,corpo);
    alvoX=pos[0];alvoY=pos[1];alvoZoom=corpo<0?7:120;
   }
  }
 }
 private static double raioPlaneta(int j){return .48+j*.12;}
 /** Relogio das orbitas: os planetas giram em volta do sol, os de dentro mais rapido. */
 private static double orbita;
 private static double velocidadeOrbita(int j){return .16/(1+j*.55);}
 private static double[] posicaoPlaneta(int i,int j){
  double ang=.9+i*.73+j*1.48+orbita*velocidadeOrbita(j)*(i%2==0?1:-1),r=5+j*3.5;
  return new double[]{SISTEMAS[i][0]+Math.cos(ang)*r,SISTEMAS[i][1]+Math.sin(ang)*r*.78};
 }
 private double[] posicaoJogador(){
  if(!dobra||atual==0)return SISTEMAS[atual];
  return new double[]{SISTEMAS[atual-1][0]*(1-viagem)+SISTEMAS[atual][0]*viagem,SISTEMAS[atual-1][1]*(1-viagem)+SISTEMAS[atual][1]*viagem};
 }
 private int tocando=-1;
 void desenhar(Graphics2D original){
  if(escolha&&selecionado!=tocando){tocando=selecionado;MusicaJogo.trocarSeDisponivel("universo_"+selecionado+"_"+MusicaChip.TEMA);} // tema do universo selecionado
  Graphics2D g=(Graphics2D)original.create();
  g.setColor(new Color(3,7,15));g.fillRect(0,0,960,600);
  g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
  texto(g,"ATLAS ESTELAR",22,31,23,Color.WHITE);
  texto(g,"METAL SPACE / CARTOGRAFIA DA CAMPANHA",22,54,11,new Color(124,151,179));
  texto(g,zoom<.085?"ESCALA: GRUPO GALACTICO":zoom<1.2?"ESCALA: GALAXIA":zoom<15?"ESCALA: SISTEMA":"ESCALA: PLANETAS",500,35,13,Color.CYAN);
  Graphics2D v=(Graphics2D)g.create();v.clip(VISTA);v.setColor(new Color(2,5,13));v.fill(VISTA);
  Random estrelas=new Random(911);
  for(int i=0;i<220;i++){int x=16+estrelas.nextInt(706),y=82+estrelas.nextInt(440);v.setColor(new Color(140,172,202,40+estrelas.nextInt(80)));v.fillRect(x,y,1,1);}
  desenharGalaxia(v,0,0,1,"GALAXIA DO METAL");
  desenharGalaxia(v,-4200,-2000,.65,"NECROPOLIS");
  desenharGalaxia(v,4700,-1100,.8,"COROA SOMBRIA");
  desenharGalaxia(v,2900,3300,.55,"CINZAS DISTANTES");
  desenharGrade(v);
  if(zoom>.07&&zoom<1.1)desenharRegioes(v);
  v.setStroke(new BasicStroke(1.2f));
  for(int i=0;i<7;i++){
   v.setColor(i<atual?new Color(75,218,233,170):new Color(130,158,180,75));
   v.draw(new Line2D.Double(telaX(SISTEMAS[i][0]),telaY(SISTEMAS[i][1]),telaX(SISTEMAS[i+1][0]),telaY(SISTEMAS[i+1][1])));
  }
  for(int i=0;i<8;i++)desenharSistema(v,i);
  double[] pos=posicaoJogador();int px=telaX(pos[0]),py=telaY(pos[1]);
  if(NAVE_MAPA!=null&&!foraRota){int q=(int)(tempo*6)%4,fw=NAVE_MAPA.getWidth()/4;v.drawImage(NAVE_MAPA,px-17,py-50,px+17,py-16,q*fw,0,q*fw+fw,NAVE_MAPA.getHeight(),null);}
  else{v.setColor(foraRota?Color.ORANGE:Color.CYAN);v.fillPolygon(new int[]{px-7,px+7,px},new int[]{py-28,py-28,py-17},3);}
  texto(v,foraRota?"SINAL INTERROMPIDO":dobra?"EM DOBRA":"VOCE",px+12,py-20,11,foraRota?Color.ORANGE:Color.CYAN);
  v.dispose();g.setColor(new Color(55,92,120));g.draw(VISTA);
  desenharPainel(g);
  texto(g,"RODA: ZOOM   ARRASTAR: MOVER   DUPLO CLIQUE: APROXIMAR",22,550,13,Color.WHITE);
  texto(g,"+ / -: ZOOM   SETAS: MOVER   F: NAVE   G: GALAXIA   Z: SISTEMA",22,571,12,new Color(145,191,215));
  texto(g,"1–5: ABAS DA PAUSA   ENTER / ESC: CONTINUAR   M: MENU INICIAL",22,590,11,new Color(145,191,215));
  g.dispose();
 }
 private void desenharGalaxia(Graphics2D g,double x,double y,double escala,String nome){
  int tamanho=(int)Math.round(3500*escala*zoom);
  int sx=telaX(x-1750*escala),sy=telaY(y-1750*escala);
  if(tamanho<1||sx>722||sy>522||sx+tamanho<16||sy+tamanho<82)return;
  Graphics2D a=(Graphics2D)g.create();
  a.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,(float)Math.max(.08,Math.min(1,1.2-zoom*.14))));
  a.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_BILINEAR);
  // Limitar o recorte evita coordenadas gigantes em grandes aproximacoes.
  int dx=Math.max(16,sx),dy=Math.max(82,sy),dw=Math.min(722,sx+tamanho)-dx,dh=Math.min(522,sy+tamanho)-dy;
  if(dw>0&&dh>0)a.drawImage(GALAXIA,dx,dy,dx+dw,dy+dh,(int)((dx-sx)*1024.0/tamanho),(int)((dy-sy)*1024.0/tamanho),(int)((dx+dw-sx)*1024.0/tamanho),(int)((dy+dh-sy)*1024.0/tamanho),null);
  a.dispose();
  if(zoom<.085)texto(g,nome,telaX(x)-55,telaY(y)+tamanho/3+18,11,new Color(179,194,217));
 }
 private void desenharGrade(Graphics2D g){
  double passo=zoom<.1?2000:zoom<1?250:zoom<6?25:5;
  g.setColor(new Color(40,101,125,45));
  double inicio=Math.floor(mundoX(16)/passo)*passo;
  for(double x=inicio;x<mundoX(722);x+=passo)g.drawLine(telaX(x),82,telaX(x),522);
  inicio=Math.floor(mundoY(82)/passo)*passo;
  for(double y=inicio;y<mundoY(522);y+=passo)g.drawLine(16,telaY(y),722,telaY(y));
 }
 private void desenharRegioes(Graphics2D g){
  g.setStroke(new BasicStroke(1));
  for(int i=0;i<8;i++){
   double ang=i*Math.PI/4-.1;
   int[] xs=new int[9],ys=new int[9];
   for(int k=0;k<9;k++){double r=120+k*175,a=ang+Math.sin(k*2.7+i)*.09;xs[k]=telaX(Math.cos(a)*r);ys[k]=telaY(Math.sin(a)*r*.74);}
   g.setColor(new Color(185,200,219,80));g.drawPolyline(xs,ys,9);
  }
  for(int r:new int[]{400,900,1450}){
   g.setColor(new Color(160,181,210,55));g.drawOval(telaX(-r),telaY(-r*.74),(int)(2*r*zoom),(int)(1.48*r*zoom));
  }
 }
 private void desenharSistema(Graphics2D g,int i){
  int x=telaX(SISTEMAS[i][0]),y=telaY(SISTEMAS[i][1]);
  if(zoom>1.2){
   double alcance=22*zoom;if(x+alcance<16||x-alcance>722||y+alcance<82||y-alcance>522)return;
   for(int j=0;j<Turne.planetas(i);j++){
    double r=(5+j*3.5)*zoom;
    g.setColor(new Color(100,145,170,65));g.drawOval((int)(x-r),(int)(y-r*.78),(int)(r*2),(int)(r*1.56));
    double[] pos=posicaoPlaneta(i,j);int qx=telaX(pos[0]),qy=telaY(pos[1]);
    int tamanho=Math.max(4,(int)(raioPlaneta(j)*zoom*2.9));
    g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
    // cor do risco: halo em volta; risco alto ganha anel, como Saturno
    int risco=Turne.risco(i,j);Color cr=Turne.CORES_RISCO[risco];boolean anel=Turne.anel(i,j);
    for(int k=3;k>0;k--){g.setColor(new Color(cr.getRed(),cr.getGreen(),cr.getBlue(),26));int hr=tamanho/2+k*Math.max(2,tamanho/10);g.fillOval(qx-hr,qy-hr,hr*2,hr*2);}
    BufferedImage arte=arteDoPlaneta(i,j);boolean arteNova=arte!=PLANETAS[i][j];
    // os sprites tem margem, por isso saem um pouco maiores
    int t2=arteNova?(int)(tamanho*(anel?1.6:1.35)):tamanho;
    // anel de Saturno (o campo de meteoros do planeta): metade de tras antes, metade da frente depois
    if(anel)anelSaturno(g,i,j,qx,qy,t2,cr,false);
    // o planeta gira no proprio eixo (os de anel nao: a arte deles ja traz cinturao)
    Graphics2D pg=(Graphics2D)g.create();
    if(!anel){pg.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_BILINEAR);pg.rotate(tempo*(.25+j*.04)*(j%2==0?1:-1),qx,qy);}
    pg.drawImage(arte,qx-t2/2,qy-t2/2,t2,t2,null);pg.dispose();
    if(anel)anelSaturno(g,i,j,qx,qy,t2,cr,true);
    if(i==selecionado&&j==planeta){g.setColor(Color.WHITE);g.drawOval(qx-tamanho/2-4,qy-tamanho/2-4,tamanho+8,tamanho+8);}
    boolean chefe=j==Turne.planetas(i)-1;String quem=chefe?CHEFES[i]:Turne.integrante(i,j);
    if(zoom>4){texto(g,(j+1)+". "+quem,qx+tamanho/2+5,qy+3,10,chefe?new Color(255,199,111):new Color(170,199,219));
     if(cancelado(i,j)){Graphics2D c=(Graphics2D)g.create();c.rotate(-.25,qx,qy);c.setColor(new Color(230,40,40,220));c.setStroke(new BasicStroke(2));c.drawRect(qx-34,qy-9,68,16);texto(c,"CANCELADO",qx-30,qy+4,10,new Color(255,70,70));c.dispose();}}
   }
  }
  if(x<0||x>740||y<60||y>540)return;
  int r=Math.max(3,(int)Math.min(18,zoom*1.2));
  if(ESTRELAS[i]!=null){
   // estrela pulsando devagar, cada uma no seu ritmo
   int t=(int)(Math.max(22,r*5)*(1+.05*Math.sin(tempo*2+i)));
   Graphics2D e=(Graphics2D)g.create();e.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_BILINEAR);e.drawImage(ESTRELAS[i],x-t/2,y-t/2,t,t,null);
   // o sol e o ponto de referencia da cartografia; a base aliada do sistema orbita perto dele
   if(zoom>1.2){BufferedImage base=EstacaoAliada.quadro(tempo+i);double ab=-orbita*.3+i+2.2,rb=6.7*zoom;
    if(base!=null){int bw=(int)Math.max(18,3.4*zoom),bh=bw*base.getHeight()/base.getWidth(),bx=(int)(x+Math.cos(ab)*rb),by=(int)(y+Math.sin(ab)*rb*.78);e.drawImage(base,bx-bw/2,by-bh/2,bw,bh,null);
     if(zoom>4)texto(e,"BASE ALIADA",bx+bw/2+3,by+3,9,new Color(130,255,160));}
    if(zoom>3)texto(e,"SOL DE "+NOMES[i],x-30,y+t/2+12,10,CORES[i]);}
   e.dispose();
  }else{
  for(int k=3;k>0;k--){g.setColor(new Color(CORES[i].getRed(),CORES[i].getGreen(),CORES[i].getBlue(),35));g.fillOval(x-r*k,y-r*k,r*k*2,r*k*2);}
  g.setColor(CORES[i]);g.fillOval(x-r,y-r,2*r,2*r);g.setColor(Color.WHITE);g.fillOval(x-r/3,y-r/3,Math.max(2,r*2/3),Math.max(2,r*2/3));}
  if(i==selecionado&&planeta<0){g.setColor(new Color(255,199,111));g.drawOval(x-r-5,y-r-5,r*2+10,r*2+10);}
  if(escolha&&vencido(i)){g.setColor(new Color(110,230,120));g.setStroke(new BasicStroke(3));g.drawLine(x-r-2,y,x-2,y+r);g.drawLine(x-2,y+r,x+r+4,y-r-2);g.setStroke(new BasicStroke(1));}
  if(zoom>.09)texto(g,(i+1)+" / "+(escolha?CHEFES[i]:NOMES[i]),x+10+r,y+16,11,escolha?(vencido(i)?new Color(110,130,120):Color.WHITE):i<=atual?Color.WHITE:new Color(153,166,190));
 }
 /** Anel inclinado de pedras, como Saturno, girando em volta do planeta. frente: so a metade que passa na frente. */
 private void anelSaturno(Graphics2D g,int i,int j,int qx,int qy,int t,Color cr,boolean frente){
  Graphics2D a=(Graphics2D)g.create();a.rotate(-.32+(i%3)*.12,qx,qy);
  double rx=t*.95,ry=rx*.26;int n=54,p=Math.max(1,t/26);java.util.Random r=new java.util.Random(i*31+j);
  a.setColor(new Color(cr.getRed(),cr.getGreen(),cr.getBlue(),frente?70:45));a.setStroke(new BasicStroke(Math.max(1,t/40f)));
  a.drawArc((int)(qx-rx*.88),(int)(qy-ry*.88),(int)(rx*1.76),(int)(ry*1.76),frente?180:0,180);
  for(int k=0;k<n;k++){
   double ang=k*Math.PI*2/n+tempo*.35+r.nextDouble()*.1,f=.82+r.nextDouble()*.26;
   boolean naFrente=Math.sin(ang)>0;if(naFrente!=frente)continue;
   int x=(int)(qx+Math.cos(ang)*rx*f),y=(int)(qy+Math.sin(ang)*ry*f),c=110+r.nextInt(90);
   a.setColor(r.nextInt(4)==0?cr:new Color(c,c-10,c-25));a.fillRect(x,y,p+(k%3==0?1:0),p);
  }
  a.dispose();
 }
 private void desenharPainel(Graphics2D g){
  g.setColor(new Color(9,16,29));g.fillRect(736,82,208,440);
  texto(g,"SISTEMA SELECIONADO",748,109,11,new Color(131,158,180));
  if(ESTRELAS[selecionado]!=null&&planeta<0)g.drawImage(ESTRELAS[selecionado],884,88,52,52,null);
  texto(g,NOMES[selecionado],748,137,19,CORES[selecionado]);
  texto(g,"SETOR "+(selecionado+1)+" / 8",748,159,12,Color.WHITE);
  texto(g,GENEROS[selecionado],834,159,10,CORES[selecionado]);
  if(escolha){
   texto(g,vencido(selecionado)?"CHEFE JA VENCIDO":"CHEFE: "+CHEFES[selecionado],748,181,11,vencido(selecionado)?new Color(120,130,150):new Color(255,199,111));
   texto(g,Turne.planetas(selecionado)+" PLANETAS / RISCO ATE "+Turne.NOMES_RISCO[Turne.risco(selecionado,Turne.planetas(selecionado)-1)],748,197,10,Turne.CORES_RISCO[Turne.risco(selecionado,Turne.planetas(selecionado)-1)]);
   texto(g,"ESCOLHA O PROXIMO SETOR",748,223,13,new Color(255,199,111));
   texto(g,"SETAS: TROCAR SETOR",748,251,11,Color.WHITE);
   texto(g,"ENTER: INICIAR DOBRA",748,269,11,Color.WHITE);
   texto(g,"Siga o sol do sistema: a base",748,300,10,new Color(154,182,205));
   texto(g,"aliada orbita perto dele.",748,316,10,new Color(154,182,205));
   texto(g,"Cada poder e o ponto fraco",748,332,10,new Color(154,182,205));
   texto(g,"de outro chefe.",748,348,10,new Color(154,182,205));
  }else
  texto(g,selecionado<atual?"PERCORRIDO":selecionado==atual?"POSICAO DA CAMPANHA":"AINDA NAO ALCANCADO",748,181,11,new Color(165,190,214));
  if(planeta>=0){
   g.drawImage(arteDoPlaneta(selecionado,planeta),796,203,88,88,null);
   boolean chefe=planeta==Turne.planetas(selecionado)-1;int risco=Turne.risco(selecionado,planeta);
   texto(g,"PLANETA "+(planeta+1)+" / "+Turne.planetas(selecionado),748,302,13,Color.WHITE);
   texto(g,chefe?"CHEFE: "+CHEFES[selecionado]:Turne.integrante(selecionado,planeta),748,318,11,chefe?new Color(255,199,111):Color.WHITE);
   texto(g,"RISCO "+Turne.NOMES_RISCO[risco]+(Turne.anel(selecionado,planeta)?"  (ANEL)":"")+"  x"+String.format(java.util.Locale.ROOT,"%.2f",Turne.premio(risco)),748,334,10,Turne.CORES_RISCO[risco]);
  }else if(!escolha){
   texto(g,Turne.planetas(selecionado)+" PLANETAS NA TURNE",748,223,11,Color.WHITE);
   texto(g,"Aproxime para observar",748,251,11,new Color(154,182,205));
   texto(g,"as orbitas e superficies.",748,269,11,new Color(154,182,205));
  }
  texto(g,"Coordenadas locais",748,342,10,new Color(124,150,170));
  texto(g,String.format(java.util.Locale.ROOT,"X %+.0f / Y %+.0f",SISTEMAS[selecionado][0],SISTEMAS[selecionado][1]),748,360,11,Color.LIGHT_GRAY);
  botao(g,386,"Z  APROXIMAR SISTEMA");botao(g,423,"F  LOCALIZAR NAVE");botao(g,460,"G  VISAO GALACTICA");
  texto(g,escolha?"ROTA LIVRE / ESCOLHA SEU DESAFIO":"ATLAS DE OBSERVACAO",740,515,9,new Color(125,150,173));
 }
 private static void botao(Graphics2D g,int y,String texto){g.setColor(new Color(22,48,68));g.fillRect(744,y,192,29);g.setColor(new Color(62,117,148));g.drawRect(744,y,192,29);texto(g,texto,754,y+19,11,Color.WHITE);}
 private static void texto(Graphics2D g,String s,int x,int y,int tamanho,Color cor){g.setFont(new Font("Monospaced",Font.BOLD,tamanho));g.setColor(new Color(0,0,0,180));g.drawString(s,x+1,y+1);g.setColor(cor);g.drawString(s,x,y);}
 private static BufferedImage criarGalaxia(){
  BufferedImage b=new BufferedImage(1024,1024,BufferedImage.TYPE_INT_ARGB);Graphics2D g=b.createGraphics();Random r=new Random(971);
  for(int i=0;i<45000;i++){
   double raio=Math.pow(r.nextDouble(),.65)*475,ang=(i%4)*Math.PI/2+raio*.012+r.nextGaussian()*(.12+raio*.0002);
   double x=512+Math.cos(ang)*raio,y=512+Math.sin(ang)*raio*.74;
   int alpha=12+r.nextInt(65);g.setColor(raio<120?new Color(255,209,150,alpha):i%3==0?new Color(156,114,215,alpha):new Color(83,135,210,alpha));
   int size=1+r.nextInt(4);g.fillOval((int)x,(int)y,size,size);
  }
  for(int i=0;i<8500;i++){int x=512+(int)(r.nextGaussian()*45),y=512+(int)(r.nextGaussian()*33);g.setColor(new Color(255,229,178,12+r.nextInt(60)));g.fillOval(x,y,2,2);}
  g.dispose();return b;
 }
 private static BufferedImage[][] criarPlanetas(){
  BufferedImage[][] result=new BufferedImage[8][6];
  for(int i=0;i<8;i++)for(int j=0;j<6;j++){
   BufferedImage b=new BufferedImage(64,64,BufferedImage.TYPE_INT_ARGB);Random r=new Random(200+i*17+j);
   Color c=CORES[i];
   for(int y=0;y<64;y++)for(int x=0;x<64;x++){
    double nx=(x-31.5)/31,ny=(y-31.5)/31,rr=nx*nx+ny*ny;if(rr>1)continue;
    double z=Math.sqrt(1-rr),l=Math.max(.1,(-nx*.5-ny*.3+z*.8));
    double textura=.65+.25*Math.sin(x*.21+Math.sin(y*.26+j)*3)+r.nextDouble()*.12;
    int red=(int)Math.min(255,(c.getRed()*textura+25)*l),green=(int)Math.min(255,(c.getGreen()*textura+25)*l),blue=(int)Math.min(255,(c.getBlue()*textura+25)*l);
    b.setRGB(x,y,0xff000000|red<<16|green<<8|blue);
   }
   result[i][j]=b;
  }
  return result;
 }
 // Compatibilidade com previews antigos; o jogo usa uma instancia persistente.
 static void desenhar(Graphics2D g,int setor,boolean dobra){MapaEstelar m=new MapaEstelar();m.estado(setor,dobra,false,0);m.desenhar(g);}
}
