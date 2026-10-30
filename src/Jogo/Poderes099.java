package Jogo;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
final class Poderes099 {
 static final String[] NOMES={"TENTACULOS ABISSAIS","PAINKILLER","EGIDE DE EDDIE","AS DE LEMMY","RIFF PEM","PENTAGRAMA","PACTO ROTATIVO","GRITO PARANOID"};
 static final String[] DICAS={"Tentaculos agarram e congelam","Moto aliada com vida propria","Escudos interceptam tiros","Cometa pesado perfurante","Paralisa mobs e chefes","Zona de fogo persistente","Cristais orbitais de defesa","Onda sonica perfurante"};
 static final int[] CUSTO={25,40,30,25,40,35,30,30};
 static final double[] RECARGA={8,18,14,7,22,12,16,9},DURACAO={1.8,12,8,2.6,3,5,10,2.2};
 private static final BufferedImage[][] ARTE=new BufferedImage[8][4];
 static{try{BufferedImage a=ImageIO.read(Poderes099.class.getResource("/ImagemIcon/poderes_099.png"));for(int r=0;r<8;r++)for(int c=0;c<4;c++){int x=c*a.getWidth()/4,y=r*a.getHeight()/8;ARTE[r][c]=a.getSubimage(x,y,a.getWidth()/4,a.getHeight()/8);}}catch(java.io.IOException e){throw new ExceptionInInitializerError(e);}}
 final int[] energia={100,100,100,100,100,100,100,100};final double[] cooldown=new double[8];int selecionado,ativo=-1,vidaMoto,pendente=-1;double tempo,restante,pulso,x,y,recargaPassiva,preparo,naveX,naveY;
 /** Simbolo do chefe acende em volta da nave antes do disparo: o jogador se compromete com o golpe. */
 static final double PREPARO=.4;
 void selecionar(int i,ProgressaoNave p){if(p.temChaveIndice(i))selecionado=i;}
 void proximo(ProgressaoNave p){for(int n=1;n<=8;n++){int i=(selecionado+n)%8;if(p.temChaveIndice(i)){selecionado=i;return;}}}
 boolean ativar(ProgressaoNave p,Nave n){int i=selecionado;if(!p.temChaveIndice(i)||ativo>=0||pendente>=0||cooldown[i]>0||energia[i]<CUSTO[i])return false;energia[i]-=CUSTO[i];cooldown[i]=RECARGA[i]*(1-.06*n.motorPermanente());pendente=i;preparo=PREPARO;naveX=n.getX()+22;naveY=n.getY()+20;SomJogo.tocar(SomJogo.Efeito.ENERGIA);return true;}
 private void disparar(int i,Nave n){ativo=i;restante=DURACAO[i];tempo=pulso=0;vidaMoto=8;x=n.getX()+95;y=n.getY()+18;if(i==5)x=Math.min(760,x+260);SomJogo.tocar(i==5?SomJogo.Efeito.FOGO:SomJogo.Efeito.FUSAO);}
 int recarregar(ProgressaoNave p){int sobra=20;for(int j=0;j<8;j++){int i=(selecionado+j)%8;if(p.temChaveIndice(i)){int v=Math.min(sobra,100-energia[i]);energia[i]+=v;sobra-=v;}}return 20-sobra;}
 void atualizar(double dt,Nave n){
    for(int i=0;i<8;i++)cooldown[i]=Math.max(0,cooldown[i]-dt);
    recargaPassiva+=dt;
    if(recargaPassiva>=.65){int ganho=(int)(recargaPassiva/.65);recargaPassiva-=ganho*.65;for(int i=0;i<8;i++)energia[i]=Math.min(100,energia[i]+ganho);}
    naveX=n.getX()+22;naveY=n.getY()+20;
    if(pendente>=0){preparo-=dt;if(preparo<=0){int i=pendente;pendente=-1;disparar(i,n);}return;}
    if(ativo<0)return;tempo+=dt;restante-=dt;pulso-=dt;if(restante<=0){ativo=-1;return;}if(ativo==1){x+=(n.getX()+130-x)*Math.min(1,dt*6);y+=(n.getY()+18-y)*Math.min(1,dt*6);}if(ativo==2||ativo==6||ativo==4){x=n.getX()+23;y=n.getY()+18;}if(ativo==3||ativo==7)x+=dt*(ativo==3?420:360);if(ativo==0)atualizarTentaculos(dt,n);}
 // ---- Tentaculos da Lula: saem da nave, buscam ate 3 alvos a frente, agarram e voltam ----
 private final double[][] alvosTentaculo=new double[3][2];private final double[][] pontasTentaculo=new double[3][2];private int alvosDefinidos;
 /** Fase informa os centros dos inimigos visiveis; os tentaculos escolhem os mais proximos a frente da nave. */
 void mirar(java.util.List<double[]> centros,Nave n){
  if(ativo!=0&&pendente!=0)return;double ox=n.getX()+30,oy=n.getY()+20;
  java.util.List<double[]> a=new java.util.ArrayList<>();for(double[] c:centros)if(c[0]>ox+20&&Math.hypot(c[0]-ox,c[1]-oy)<460)a.add(c);
  a.sort((p,q)->Double.compare(Math.hypot(p[0]-ox,p[1]-oy),Math.hypot(q[0]-ox,q[1]-oy)));
  alvosDefinidos=Math.min(3,a.size());
  for(int i=0;i<3;i++){double[] c=i<alvosDefinidos?a.get(i):new double[]{ox+340,oy+(i-1)*110};alvosTentaculo[i][0]=c[0];alvosTentaculo[i][1]=c[1];}
 }
 private double extensao(){double t=DURACAO[0]-restante;return t<.3?t/.3:restante<.45?Math.max(0,restante/.45):1;}
 private double[] raizTentaculo(int i,Nave n){return new double[]{n.getX()+6,n.getY()+20+(i-1)*12};}
 private void atualizarTentaculos(double dt,Nave n){
  double e=extensao();e=1-Math.pow(1-e,3);
  for(int i=0;i<3;i++){double[] r=raizTentaculo(i,n);double bal=Math.sin(tempo*9+i*2)*14*e;
   pontasTentaculo[i][0]=r[0]+(alvosTentaculo[i][0]-r[0])*e;pontasTentaculo[i][1]=r[1]+(alvosTentaculo[i][1]-r[1])*e+bal;}
  x=n.getX()+30;y=n.getY()+20;
 }
 /** Area atingida: ponta de cada tentaculo (a Lula agarra com a ponta). */
 boolean atinge(Rectangle b){
  if(ativo!=0)return alcance().intersects(b);
  if(extensao()<.55)return false;
  for(double[] p:pontasTentaculo)if(new Rectangle((int)p[0]-34,(int)p[1]-34,68,68).intersects(b))return true;return false;
 }
 private void desenharTentaculos(Graphics2D g,int nx,int ny){
  double e=1-Math.pow(1-extensao(),3);
  for(int i=0;i<3;i++){
   double rx=nx-16,ry=ny+(i-1)*12,tx=pontasTentaculo[i][0],ty=pontasTentaculo[i][1];
   // curva em S: sai para tras da nave, faz a volta e avanca ate o alvo
   double c1x=rx-70*e,c1y=ry+(i-1)*70,c2x=rx+(tx-rx)*.55,c2y=ty+(i-1)*-60+Math.sin(tempo*7+i)*30;
   java.util.List<double[]> pts=new java.util.ArrayList<>();double ax=rx,ay=ry,acum=0;pts.add(new double[]{rx,ry});
   for(int k=1;k<=80;k++){double p=k/80.0,q=1-p;double sx=q*q*q*rx+3*q*q*p*c1x+3*q*p*p*c2x+p*p*p*tx,sy=q*q*q*ry+3*q*q*p*c1y+3*q*p*p*c2y+p*p*p*ty;acum+=Math.hypot(sx-ax,sy-ay);ax=sx;ay=sy;if(acum>=9){pts.add(new double[]{sx,sy});acum=0;}}
   pts.add(new double[]{tx,ty});
   for(int k=0;k<pts.size();k++){double[] p=pts.get(k),ant=pts.get(Math.max(0,k-1));double w=6+20*Math.sqrt(1-k/(double)Math.max(1,pts.size()-1));LulaVisual.elo(g,p[0],p[1],Math.atan2(p[1]-ant[1],p[0]-ant[0]),w);}
   if(e>.9&&i<alvosDefinidos){g.setColor(new Color(120,220,255,(int)(120+80*Math.sin(tempo*20))));g.drawOval((int)tx-22,(int)ty-22,44,44);}
  }
 }
 boolean pulso(){if(ativo<0||pulso>0)return false;pulso=ativo==1?.35:.25;return true;}
 boolean paralisa(){return ativo==4;}
 int dano(){return ativo==3?10:ativo==1?4:ativo==5?3:ativo==0?5:ativo==7?6:2;}
 Rectangle alcance(){if(ativo==4)return new Rectangle(0,80,960,520);if(ativo==1)return new Rectangle((int)x,(int)y-8,Math.max(1,960-(int)x),16);int r=ativo==5?115:ativo==0?85:ativo==3?40:ativo==7?95:70;return new Rectangle((int)x-r,(int)y-r,r*2,r*2);}
 boolean interceptar(Rectangle b){if(ativo!=1&&ativo!=2&&ativo!=6)return false;boolean bate=ativo==1?new Rectangle((int)x-34,(int)y-22,68,44).intersects(b):Math.hypot(b.getCenterX()-x,b.getCenterY()-y)<76;if(bate&&ativo==1&&--vidaMoto<=0){ativo=-1;SomJogo.tocar(SomJogo.Efeito.EXPLOSAO);}return bate;}
 static void sprite(Graphics2D g,int i,int x,int y,int s,double t){g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);g.drawImage(ARTE[Math.max(0,Math.min(7,i))][((int)(Math.max(0,t)*8))%4],x,y,s,s,null);}
 void desenhar(Graphics2D g){if(pendente>=0){simbolo(g,pendente,(int)naveX,(int)naveY,1-preparo/PREPARO);return;}if(ativo<0)return;int px=(int)x,py=(int)y;if(ativo==2||ativo==6){for(int j=0;j<4;j++){double a=tempo*3+j*Math.PI/2;sprite(g,ativo,px+(int)(Math.cos(a)*62)-20,py+(int)(Math.sin(a)*62)-20,40,tempo+j*.13);}}else if(ativo==4){sprite(g,ativo,px-48,py-48,96,tempo);g.setColor(new Color(80,215,255,110));int r=(int)((tempo%1)*850);for(int i=0;i<3;i++)g.drawOval(px-r-i*8,py-r-i*8,r*2+i*16,r*2+i*16);}else if(ativo==0){desenharTentaculos(g,px,py);}else{int s=ativo==5?230:ativo==1?110:ativo==7?160:120;sprite(g,ativo,px-s/2,py-s/2,s,tempo);if(ativo==1){g.setColor(Color.DARK_GRAY);g.fillRect(px-28,py+35,56,4);g.setColor(Color.CYAN);g.fillRect(px-28,py+35,7*vidaMoto,4);if(pulso>.22)g.fillRect(px+40,py-2,Math.max(0,960-px),4);}}}

 static final Color[] CORES_SIMBOLO={new Color(70,200,255),new Color(255,140,40),new Color(150,230,255),new Color(245,235,200),new Color(80,150,255),new Color(255,50,40),new Color(90,240,120),new Color(190,90,255)};
 /** Simbolo de cada chefe: ondas, roda em chamas, espada, as de espadas, raio, pentagrama, cristal e morcego. */
 private static final String[] ARQ_SIMBOLO={"lula","rob","bruce","lemmy","james","tom","chuck","ozzy"};
 private static final BufferedImage[][] SIMBOLOS=new BufferedImage[8][];private static final boolean[] LIDO=new boolean[8];
 /** Prancha 1072x256: 4 quadros de 256 com 16 de intervalo; o preto puro vira transparencia e o brilho e mantido. */
 private static synchronized BufferedImage[] quadrosSimbolo(int i){
  if(LIDO[i])return SIMBOLOS[i];LIDO[i]=true;
  java.net.URL u=Poderes099.class.getResource("/ImagemIcon/simbolo_"+ARQ_SIMBOLO[i]+".png");if(u==null)return null;
  try{BufferedImage a=ImageIO.read(u);BufferedImage[] q=new BufferedImage[4];
   for(int k=0;k<4;k++){BufferedImage o=new BufferedImage(256,256,BufferedImage.TYPE_INT_ARGB);
    // So o escuro ligado a borda vira fundo; o miolo escuro do simbolo (carta, pentagrama) fica solido.
    int[] px=new int[256*256];boolean[] fora=new boolean[256*256];int[] fila=new int[256*256];int ini=0,fim=0;
    for(int y=0;y<256;y++)for(int x=0;x<256;x++)px[y*256+x]=a.getRGB(k*272+x,y);
    for(int t=0;t<256;t++)for(int idx:new int[]{t,255*256+t,t*256,t*256+255})if(!fora[idx]&&brilho(px[idx])<40){fora[idx]=true;fila[fim++]=idx;}
    while(ini<fim){int idx=fila[ini++],x=idx%256,y=idx/256;int[] viz={x>0?idx-1:-1,x<255?idx+1:-1,y>0?idx-256:-1,y<255?idx+256:-1};
     for(int n:viz)if(n>=0&&!fora[n]&&brilho(px[n])<40){fora[n]=true;fila[fim++]=n;}}
    for(int idx=0;idx<px.length;idx++){int c=px[idx],r=c>>16&255,gr=c>>8&255,b=c&255,m=brilho(c);
     if(!fora[idx]&&!vizinhoFora(fora,idx)){o.setRGB(idx%256,idx/256,0xFF000000|c);continue;}
     if(m<12)continue;int al=Math.min(255,m*2);o.setRGB(idx%256,idx/256,al<<24|Math.min(255,r*255/m)<<16|Math.min(255,gr*255/m)<<8|Math.min(255,b*255/m));}
    q[k]=o;}
   SIMBOLOS[i]=q;}catch(java.io.IOException e){System.err.println("Simbolo ilegivel: "+ARQ_SIMBOLO[i]);}
  return SIMBOLOS[i];
 }
 private static int brilho(int c){return Math.max(c>>16&255,Math.max(c>>8&255,c&255));}
 private static boolean vizinhoFora(boolean[] f,int i){int x=i%256,y=i/256;return x>0&&f[i-1]||x<255&&f[i+1]||y>0&&f[i-256]||y<255&&f[i+256];}
 static void simbolo(Graphics2D tela,int i,int cx,int cy,double p){
  BufferedImage[] q=quadrosSimbolo(Math.max(0,Math.min(7,i)));
  if(q!=null){
   // Quadros 1 a 3 acompanham a carga; no fim, pulsa no quadro 4.
   int k=p<.3?0:p<.6?1:p<.9?2:3;int lado=(int)(70+Math.min(1,p)*50);
   Graphics2D g=(Graphics2D)tela.create();g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
   g.drawImage(q[k],cx-lado/2,cy-lado/2,lado,lado,null);g.dispose();return;
  }
  Graphics2D g=(Graphics2D)tela.create();g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
  Color c=CORES_SIMBOLO[Math.max(0,Math.min(7,i))];double cresce=Math.min(1,p*1.6);int r=(int)(18+cresce*30);
  g.setColor(new Color(c.getRed(),c.getGreen(),c.getBlue(),(int)(60*cresce)));g.fillOval(cx-r-10,cy-r-10,2*r+20,2*r+20);
  g.setColor(new Color(c.getRed(),c.getGreen(),c.getBlue(),(int)(120+120*cresce)));g.setStroke(new BasicStroke(3.2f));
  g.rotate((i==5||i==1)?p*1.2:0,cx,cy);
  switch(i){
   case 0:for(int k=0;k<3;k++)g.drawArc(cx-r+k*6,cy-r/2+k*r/3-r/3,2*r-k*12,r,0,180);break;
   case 1:g.drawOval(cx-r,cy-r,2*r,2*r);for(int k=0;k<8;k++){double a=k*Math.PI/4;g.drawLine(cx,cy,cx+(int)(Math.cos(a)*r),cy+(int)(Math.sin(a)*r));g.fillPolygon(new int[]{cx+(int)(Math.cos(a)*r),cx+(int)(Math.cos(a+.2)*(r+12)),cx+(int)(Math.cos(a+.35)*r)},new int[]{cy+(int)(Math.sin(a)*r),cy+(int)(Math.sin(a+.2)*(r+12)),cy+(int)(Math.sin(a+.35)*r)},3);}break;
   case 2:g.fillPolygon(new int[]{cx,cx+5,cx+5,cx-5,cx-5},new int[]{cy-r,cy-r+10,cy+r/2,cy+r/2,cy-r+10},5);g.fillRect(cx-r/2,cy+r/2,r,5);g.fillRect(cx-3,cy+r/2+5,6,r/2);break;
   case 3:{int s2=r/2;g.fillOval(cx-s2-3,cy-2,s2+2,s2+2);g.fillOval(cx+1,cy-2,s2+2,s2+2);g.fillPolygon(new int[]{cx,cx+s2+3,cx-s2-3},new int[]{cy-r,cy+4,cy+4},3);g.fillPolygon(new int[]{cx,cx+7,cx-7},new int[]{cy+2,cy+r,cy+r},3);break;}
   case 4:g.fillPolygon(new int[]{cx+6,cx-r/2,cx-2,cx-8,cx+r/2,cx+3},new int[]{cy-r,cy+4,cy+4,cy+r,cy-6,cy-6},6);break;
   case 5:{g.drawOval(cx-r,cy-r,2*r,2*r);int[] xs=new int[5],ys=new int[5];for(int k=0;k<5;k++){double a=-Math.PI/2+k*4*Math.PI/5;xs[k]=cx+(int)(Math.cos(a)*r*.92);ys[k]=cy+(int)(Math.sin(a)*r*.92);}g.drawPolygon(xs,ys,5);break;}
   case 6:g.drawPolygon(new int[]{cx,cx+r*2/3,cx,cx-r*2/3},new int[]{cy-r,cy,cy+r,cy},4);g.drawLine(cx,cy-r,cx,cy+r);g.drawLine(cx-r*2/3,cy,cx+r*2/3,cy);break;
   default:g.fillPolygon(new int[]{cx-r,cx-r/2,cx-r/4,cx-5,cx-4,cx,cx+4,cx+5,cx+r/4,cx+r/2,cx+r,cx+r/2,cx,cx-r/2},new int[]{cy-r/3,cy-r/2,cy-r/6,cy-r/3,cy-r/2,cy-r/3,cy-r/2,cy-r/3,cy-r/6,cy-r/2,cy-r/3,cy+r/4,cy+r/6,cy+r/4},14);break;
  }
  g.dispose();
 }
}
