package Jogo;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
final class Poderes099 {
 static final String[] NOMES={"TENTACULOS ABISSAIS","PAINKILLER","ESPADA DO EDDIE","AS DE ESPADAS","RIDE THE LIGHTNING","ESCUDO DE FOGO","CLONE DE CRISTAL","REVOADA"};
 static final String[] DICAS={"Agarram, prendem e drenam a vida","Moto aliada investe e bloqueia tiros","Tres cortes que destroem tiros","Carta vai e volta; pegue de volta p/ recuperar energia","Raio salta entre inimigos e paralisa","Fogo orbita a nave e queima","Isca de cristal; use de novo p/ trocar","Vira morcegos: intocavel e fere"};
 static final int[] CUSTO={25,40,30,25,40,35,30,30};
 static final double[] RECARGA={11,18,8,7,12,16,14,14},DURACAO={3.2,10,2.0,4.0,1.2,8,6,2.5};
 private static final BufferedImage[][] ARTE=new BufferedImage[8][4];
 static{try{BufferedImage a=ImageIO.read(Poderes099.class.getResource("/ImagemIcon/poderes_099.png"));for(int r=0;r<8;r++)for(int c=0;c<4;c++){int x=c*a.getWidth()/4,y=r*a.getHeight()/8;ARTE[r][c]=a.getSubimage(x,y,a.getWidth()/4,a.getHeight()/8);}}catch(java.io.IOException e){throw new ExceptionInInitializerError(e);}}
 final int[] energia={100,100,100,100,100,100,100,100};final double[] cooldown=new double[8];int selecionado,ativo=-1,vidaMoto,pendente=-1;double tempo,restante,pulso,x,y,recargaPassiva,preparo,naveX,naveY;
 /** Simbolo do chefe acende em volta da nave antes do disparo: o jogador se compromete com o golpe. */
 static final double PREPARO=.4;
 void selecionar(int i,ProgressaoNave p){if(p.temChaveIndice(i))selecionado=i;}
 void proximo(ProgressaoNave p){for(int n=1;n<=8;n++){int i=(selecionado+n)%8;if(p.temChaveIndice(i)){selecionado=i;return;}}}
 boolean ativar(ProgressaoNave p,Nave n){int i=selecionado;if(!p.temChaveIndice(i)||ativo>=0||pendente>=0||cooldown[i]>0||energia[i]<CUSTO[i])return false;energia[i]-=CUSTO[i];cooldown[i]=RECARGA[i]*(1-.06*n.motorPermanente());pendente=i;preparo=PREPARO;naveX=n.getX()+22;naveY=n.getY()+20;SomJogo.tocar(SomJogo.Efeito.ENERGIA);return true;}
 private void disparar(int i,Nave n){nave=n;ativo=i;restante=DURACAO[i];tempo=pulso=0;vidaMoto=8;x=n.getX()+95;y=n.getY()+18;proximaCorrente=0;corrente.clear();
  if(i==6){vidaClone=6;cloneX=n.getX();cloneY=n.getY()+(n.getY()>320?-110:110);}SomJogo.tocar(i==5?SomJogo.Efeito.FOGO:SomJogo.Efeito.FUSAO);}
 int recarregar(ProgressaoNave p){int sobra=20;for(int j=0;j<8;j++){int i=(selecionado+j)%8;if(p.temChaveIndice(i)){int v=Math.min(sobra,100-energia[i]);energia[i]+=v;sobra-=v;}}return 20-sobra;}
 void atualizar(double dt,Nave n){
    for(int i=0;i<8;i++)cooldown[i]=Math.max(0,cooldown[i]-dt);
    recargaPassiva+=dt;
    if(recargaPassiva>=.65){int ganho=(int)(recargaPassiva/.65);recargaPassiva-=ganho*.65;for(int i=0;i<8;i++)energia[i]=Math.min(100,energia[i]+ganho);}
    naveX=n.getX()+22;naveY=n.getY()+20;
    if(pendente>=0){preparo-=dt;if(preparo<=0){int i=pendente;pendente=-1;disparar(i,n);}return;}
    pegouCarta=Math.max(0,pegouCarta-dt);
    if(ativo<0)return;tempo+=dt;restante-=dt;pulso-=dt;if(restante<=0){ativo=-1;return;}if(ativo==0)atualizarTentaculos(dt,n);else atualizarPoder(dt,n);}
 // ---- Tentaculos da Lula: saem da nave, buscam ate 3 alvos a frente, agarram e voltam ----
 private final double[][] alvosTentaculo=new double[3][2];private final double[][] pontasTentaculo=new double[3][2];private int alvosDefinidos;private final boolean[] preso=new boolean[3];
 /** Fase informa os centros dos inimigos visiveis; os tentaculos escolhem os mais proximos a frente da nave. */
 void mirar(java.util.List<double[]> centros,Nave n){
  alvos=centros;
  if(ativo!=0&&pendente!=0)return;double ox=n.getX()+30,oy=n.getY()+20;
  java.util.List<double[]> a=new java.util.ArrayList<>();for(double[] c:centros)if(c[0]>ox+20&&Math.hypot(c[0]-ox,c[1]-oy)<620)a.add(c);
  a.sort((p,q)->Double.compare(Math.hypot(p[0]-ox,p[1]-oy),Math.hypot(q[0]-ox,q[1]-oy)));
  if(ativo==0&&DURACAO[0]-restante>.3){
   // Ja agarrou: cada tentaculo segue o MESMO inimigo (o centro mais perto de onde estava);
   // se ele morreu, procura outro alvo livre.
   alvosDefinidos=0;
   for(int i=0;i<3;i++){double[] melhor=null;double d=70;
    for(double[] c:centros){double dd=Math.hypot(c[0]-alvosTentaculo[i][0],c[1]-alvosTentaculo[i][1]);if(dd<d){d=dd;melhor=c;}}
    if(melhor==null)for(double[] c:a){boolean livre=true;for(int j=0;j<i;j++)if(Math.hypot(c[0]-alvosTentaculo[j][0],c[1]-alvosTentaculo[j][1])<30)livre=false;if(livre){melhor=c;break;}}
    if(melhor!=null){alvosTentaculo[i][0]=melhor[0];alvosTentaculo[i][1]=melhor[1];preso[i]=true;alvosDefinidos++;}else preso[i]=false;}
   return;
  }
  alvosDefinidos=Math.min(3,a.size());
  for(int i=0;i<3;i++){double[] c=i<alvosDefinidos?a.get(i):new double[]{ox+340,oy+(i-1)*110};alvosTentaculo[i][0]=c[0];alvosTentaculo[i][1]=c[1];preso[i]=i<alvosDefinidos;}
 }
 private java.util.List<double[]> alvos=new java.util.ArrayList<>();
 // estado dos poderes 1 a 7
 private double cloneX,cloneY,giroCarta;private int vidaClone;private final java.util.List<double[]> corrente=new java.util.ArrayList<>();private double proximaCorrente;
 private double cartaX0,cartaY0,pegouCarta;
 private final java.util.Random acaso=new java.util.Random(7);private Nave nave;
 private double extensao(){double t=DURACAO[0]-restante;return t<.3?t/.3:restante<.45?Math.max(0,restante/.45):1;}
 private double[] raizTentaculo(int i,Nave n){return new double[]{n.getX()+6,n.getY()+20+(i-1)*12};}
 private void atualizarTentaculos(double dt,Nave n){
  double e=extensao();e=1-Math.pow(1-e,3);
  for(int i=0;i<3;i++){double[] r=raizTentaculo(i,n);double bal=preso[i]&&e>.95?Math.sin(tempo*30+i)*2:Math.sin(tempo*9+i*2)*14*e;
   pontasTentaculo[i][0]=r[0]+(alvosTentaculo[i][0]-r[0])*e;pontasTentaculo[i][1]=r[1]+(alvosTentaculo[i][1]-r[1])*e+bal;}
  x=n.getX()+30;y=n.getY()+20;
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
   if(e>.9&&preso[i]){
    // Enrolado no alvo: aneis apertando e gotas de energia sendo drenadas de volta para a nave.
    espiral(g,tx,ty,true);
    for(int k=0;k<4;k++){double f=((tempo*1.6+k*.25)%1);double gx=tx+(nx-tx)*f,gy=ty+(ny+(i-1)*12-ty)*f;g.setColor(new Color(120,255,200,(int)(220*(1-f))));g.fillOval((int)gx-4,(int)gy-4,8,8);}
   }
  }
 }
 /** Espiral de 3 voltas em volta do alvo: a metade de tras (frente=false) fica atras do inimigo, a da frente por cima. */
 private void espiral(Graphics2D g,double tx,double ty,boolean frente){
  double aperto=1-.1*Math.abs(Math.sin(tempo*5)),rx2=42*aperto,ry2=15;int passos=72;
  for(int k=0;k<=passos;k++){double a2=-Math.PI/2+k*(3*2*Math.PI)/passos;boolean naFrente=Math.sin(a2)>0;if(naFrente!=frente)continue;
   double ex=tx+Math.cos(a2)*rx2,ey=ty-24+k*48.0/passos+Math.sin(a2)*ry2;double w=15-k*5.0/passos;
   LulaVisual.elo(g,ex,ey,a2+Math.PI/2,w);
   if(!frente){g.setColor(new Color(10,0,20,120));g.fillOval((int)(ex-w/2),(int)(ey-w/2),(int)w,(int)w);}}
 }
 /** Desenhado antes dos inimigos: as voltas que passam por tras do corpo agarrado. */
 void desenharAtras(Graphics2D g){
  if(ativo!=0||extensao()<.9)return;
  for(int i=0;i<3;i++)if(preso[i])espiral(g,pontasTentaculo[i][0],pontasTentaculo[i][1],false);
 }
 boolean pulso(){if(ativo<0||pulso>0)return false;pulso=ativo==1?.3:ativo==2?.1:ativo==3?.18:.25;return true;}
 boolean paralisa(){return ativo==4;}
 int dano(){return ativo==0?3:ativo==1?5:ativo==2?6:ativo==3?6:ativo==4?4:ativo==5?3:ativo==6?8:4;}
 Rectangle alcance(){int r=70;return new Rectangle((int)x-r,(int)y-r,r*2,r*2);}
 boolean cloneAtivo(){return ativo==6;}
 double cloneX(){return cloneX;}double cloneY(){return cloneY;}
 /** Clone de cristal: a nave e a isca trocam de lugar (o raio do James acerta a isca). */
 void trocarComClone(Nave n){if(ativo!=6)return;double ax=n.getX(),ay=n.getY();n.teleportar(cloneX,cloneY);cloneX=ax;cloneY=ay;n.tornarIntangivel(.25);SomJogo.tocar(SomJogo.Efeito.TROCA);}

 private void atualizarPoder(double dt,Nave n){
  double nx=n.getX()+22,ny=n.getY()+20;
  switch(ativo){
   case 1:{ // Painkiller: escolta a frente e a cada 2,2 s investe ate a borda e volta
    double ciclo=tempo%2.2,alvoX=nx+120;if(ciclo>1.3){double f=(ciclo-1.3)/.9;alvoX=nx+120+Math.sin(Math.PI*f)*(820-nx);}
    x+=(alvoX-x)*Math.min(1,dt*(ciclo>1.3?14:6));y+=(ny-2-y)*Math.min(1,dt*6);break;}
   case 2:x=nx+26;y=ny;break; // Espada: o pivo e o nariz da nave
   case 3:{ // As de Espadas: vai ate longe e volta em linha reta; nao segue a nave, o jogador tem que pegar
    if(tempo<=dt){cartaX0=nx+30;cartaY0=ny;}giroCarta+=dt*16;
    x=tempo<1.0?cartaX0+560*Math.sin(Math.PI/2*tempo):cartaX0+560-720*(tempo-1.0);y=cartaY0+Math.sin(tempo*5)*18;
    if(tempo>1.0&&Math.hypot(x-nx,y-ny)<42){energia[3]=Math.min(100,energia[3]+CUSTO[3]);pegouCarta=.5;SomJogo.tocar(SomJogo.Efeito.COLETA);ativo=-1;}
    else if(x<-60)ativo=-1;
    break;}
   case 4:{x=nx+30;y=ny;proximaCorrente-=dt;if(proximaCorrente<=0){proximaCorrente=.35;montarCorrente(x,y);}break;}
   case 5:case 7:x=nx;y=ny;if(ativo==7)n.tornarIntangivel(.1);break;
   case 6:cloneY+=Math.sin(tempo*2)*12*dt;x=cloneX+22;y=cloneY+20;break;
   default:break;
  }
 }
 /** Ride the Lightning: do nariz ao alvo mais proximo a frente e depois saltando ate 4 vezes. */
 private void montarCorrente(double ox,double oy){
  corrente.clear();corrente.add(new double[]{ox,oy});java.util.List<double[]> livres=new java.util.ArrayList<>(alvos);double cx=ox,cy=oy;
  for(int salto=0;salto<5;salto++){double[] melhor=null;double d=salto==0?560:270;
   for(double[] c:livres){double dd=Math.hypot(c[0]-cx,c[1]-cy);if((salto>0||c[0]>ox)&&dd<d){d=dd;melhor=c;}}
   if(melhor==null)break;livres.remove(melhor);corrente.add(melhor);cx=melhor[0];cy=melhor[1];}
  if(corrente.size()==1)corrente.add(new double[]{ox+420,oy});
 }
 // Espada do Eddie: tres cortes; cada um forma uma meia-lua de energia que voa para frente e atravessa os inimigos.
 private static final double CORTE=.42,GIRO=.12,VOO=1.0;
 /** Centro da meia-lua k (ou null se ainda nao saiu ou ja sumiu) e o quanto ela ja se formou/apagou. */
 private double[] onda(int k){double idade=tempo-k*CORTE;if(idade<0||idade>GIRO+VOO)return null;
  double forma=Math.min(1,idade/GIRO),voo=Math.max(0,idade-GIRO);
  return new double[]{naveX+40+voo*760+voo*voo*200,naveY,forma,Math.max(0,(voo-VOO*.6)/(VOO*.4)),k==2?1.35:1};}
 private boolean cortaRetangulo(Rectangle b){
  for(int k=0;k<3;k++){double[] o=onda(k);if(o==null||o[2]<.5)continue;double R=70*o[4];
   if(new Rectangle((int)(o[0]-R*.35),(int)(o[1]-R*1.25),(int)(R*.75),(int)(R*2.5)).intersects(b))return true;}
  return false;
 }
 /** Uma lamina de energia em meia-lua: fina, afiada, inclinada; desenhada em (cx,cy) com rotacao rot. */
 private static void lamina(Graphics2D g,double cx,double cy,double R,double abre,double rot,float alfa,double espessura){
  int n=34;double[][] camadas={{1.0,1.0,.35},{.97,.62,.8},{.95,.28,1}};Color[] cores={new Color(20,170,60),new Color(110,255,140),new Color(240,255,240)};
  for(int c=0;c<3;c++){int[] xs=new int[2*n+2],ys=new int[2*n+2];double Rc=R*camadas[c][0];
   for(int i=0;i<=n;i++){double t=-abre+2*abre*i/n;double esp=R*espessura*camadas[c][1]*Math.pow(Math.cos(Math.PI/2*t/abre),1.6);
    double ox=Math.cos(t)*Rc,oy=Math.sin(t)*Rc,ix=Math.cos(t)*(Rc-esp),iy=Math.sin(t)*(Rc-esp);
    xs[i]=(int)(cx+ox*Math.cos(rot)-oy*Math.sin(rot));ys[i]=(int)(cy+ox*Math.sin(rot)+oy*Math.cos(rot));
    xs[2*n+1-i]=(int)(cx+ix*Math.cos(rot)-iy*Math.sin(rot));ys[2*n+1-i]=(int)(cy+ix*Math.sin(rot)+iy*Math.cos(rot));}
   g.setComposite(AlphaComposite.SrcOver.derive((float)(alfa*camadas[c][2])));g.setColor(cores[c]);g.fillPolygon(xs,ys,2*n+2);}
  g.setComposite(AlphaComposite.SrcOver);
 }
 private void desenharCorte(Graphics2D g){
  int k=Math.min(2,(int)(tempo/CORTE));double idade=tempo-k*CORTE;
  // 1) O giro da espada na nave: arco largo que acompanha a lamina, grosso na ponta e fino atras (como um cometa).
  if(idade<GIRO+.12){double f=Math.min(1,idade/GIRO),sumir=Math.max(0,(idade-GIRO)/.12);double dir=k==1?-1:1,R=k==2?150:120;
   int n=30;for(int c=0;c<3;c++){int[] xs=new int[2*n+2],ys=new int[2*n+2];double larg=(c==0?30:c==1?18:8)*(k==2?1.3:1);
    for(int i=0;i<=n;i++){double u=i/(double)n,t=dir*(-1.35+2.7*f*u);double esp=larg*Math.pow(u,1.4);
     xs[i]=(int)(naveX+20+Math.cos(t)*R);ys[i]=(int)(naveY+Math.sin(t)*R);xs[2*n+1-i]=(int)(naveX+20+Math.cos(t)*(R-esp));ys[2*n+1-i]=(int)(naveY+Math.sin(t)*(R-esp));}
    g.setComposite(AlphaComposite.SrcOver.derive((float)((1-sumir)*(c==0?.4:c==1?.8:1))));g.setColor(c==0?new Color(20,170,60):c==1?new Color(110,255,140):new Color(240,255,240));g.fillPolygon(xs,ys,2*n+2);}
   g.setComposite(AlphaComposite.SrcOver);
   BufferedImage[] q=quadrosSimbolo(2);if(q!=null&&sumir<.5){double t=dir*(-1.35+2.7*f);Graphics2D e=(Graphics2D)g.create();e.translate(naveX+20+Math.cos(t)*R*.5,naveY+Math.sin(t)*R*.5);e.rotate(t+Math.PI/2);e.drawImage(q[2],-24,-R*.62>-200?(int)(-R*.62):-90,48,(int)(R*.72),null);e.dispose();}
  }
  // 2) As laminas que voam: inclinadas, afiadas, com copias de rastro e linhas de velocidade.
  for(int j=0;j<3;j++){double[] o=onda(j);if(o==null||o[2]<1)continue;
   double cx=o[0],cy=o[1],R=72*o[4],rot=(j==1?-1:1)*.42;float alfa=(float)(1-o[3]);
   for(int r=4;r>=1;r--)lamina(g,cx-r*22,cy,R,1.15,rot,alfa*(.12f*(5-r)/4f),.22);
   lamina(g,cx,cy,R,1.15,rot,alfa,.26);
   g.setComposite(AlphaComposite.SrcOver.derive(alfa*.7f));g.setColor(new Color(170,255,190));
   for(int i=0;i<7;i++){int ly=(int)(cy-R*.9+i*R*.3),lx=(int)(cx-R*.1-(i%3)*18);g.fillRect(lx-60-(i%2)*30,ly,60+(i%2)*30,2);}
   g.setComposite(AlphaComposite.SrcOver);
  }
 }
 private double[][] orbes(){double[][] o=new double[6][];for(int k=0;k<6;k++){double a=tempo*3.2+k*Math.PI/3;o[k]=new double[]{x+Math.cos(a)*74,y+Math.sin(a)*62};}return o;}

 /** Onde o poder acerta neste quadro. */
 boolean atinge(Rectangle b){
  switch(ativo){
   case 0:if(extensao()<.55)return false;for(double[] p:pontasTentaculo)if(new Rectangle((int)p[0]-34,(int)p[1]-34,68,68).intersects(b))return true;return false;
   case 1:return new Rectangle((int)x-45,(int)y-26,90,52).intersects(b);
   case 2:return cortaRetangulo(b);
   case 3:return new Rectangle((int)x-34,(int)y-34,68,68).intersects(b);
   case 4:for(int i=1;i<corrente.size();i++){double[] c=corrente.get(i);if(b.contains(c[0],c[1])||b.intersectsLine(corrente.get(i-1)[0],corrente.get(i-1)[1],c[0],c[1])&&i<corrente.size())return true;}return false;
   case 5:for(double[] o:orbes())if(new Rectangle((int)o[0]-20,(int)o[1]-20,40,40).intersects(b))return true;return false;
   case 6:return false;
   case 7:return b.intersects(new Rectangle((int)x-75,(int)y-65,150,130));
   default:return false;
  }
 }
 /** Tiros inimigos destruidos ou absorvidos pelo poder. */
 boolean interceptar(Rectangle b){
  switch(ativo){
   case 1:{boolean bate=new Rectangle((int)x-45,(int)y-26,90,52).intersects(b);if(bate&&--vidaMoto<=0){ativo=-1;SomJogo.tocar(SomJogo.Efeito.EXPLOSAO);}return bate;}
   case 2:return cortaRetangulo(b);
   case 5:for(double[] o:orbes())if(Math.hypot(b.getCenterX()-o[0],b.getCenterY()-o[1])<24)return true;return false;
   case 6:{boolean bate=new Rectangle((int)cloneX,(int)cloneY,46,40).intersects(b);if(bate&&--vidaClone<=0){ativo=-1;SomJogo.tocar(SomJogo.Efeito.ESCUDO_QUEBRA);}return bate;}
   case 7:return b.intersects(new Rectangle((int)x-75,(int)y-65,150,130));
   default:return false;
  }
 }
 static void sprite(Graphics2D g,int i,int x,int y,int s,double t){g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);g.drawImage(ARTE[Math.max(0,Math.min(7,i))][((int)(Math.max(0,t)*8))%4],x,y,s,s,null);}
 void desenhar(Graphics2D g){if(pegouCarta>0&&ativo<0){int r=(int)(20+(.5-pegouCarta)*80);g.setColor(new Color(255,230,140,(int)(400*pegouCarta)));g.drawOval((int)naveX-r,(int)naveY-r,2*r,2*r);g.setFont(new Font(Font.MONOSPACED,Font.BOLD,14));g.drawString("+ENERGIA",(int)naveX-30,(int)naveY-30-(int)((.5-pegouCarta)*30));}
  if(pendente>=0){simbolo(g,pendente,(int)naveX,(int)naveY,1-preparo/PREPARO);return;}if(ativo<0)return;int px=(int)x,py=(int)y;
  Graphics2D a=(Graphics2D)g.create();a.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
  switch(ativo){
   case 0:desenharTentaculos(a,px,py);break;
   case 1:{ // moto com rastro de fogo
    for(int k=0;k<10;k++){double f=k/10.0;int fx=px-40-(int)(k*9+Math.sin(tempo*30+k)*3),fy=py+12+(int)(Math.sin(tempo*25+k*1.3)*4);a.setColor(new Color(255,(int)(200-150*f),40,(int)(200*(1-f))));a.fillOval(fx-6,fy-6,12-k/2,12-k/2);}
    sprite(a,1,px-55,py-55,110,tempo);a.setColor(Color.DARK_GRAY);a.fillRect(px-28,py+40,56,4);a.setColor(new Color(255,150,40));a.fillRect(px-28,py+40,7*vidaMoto,4);break;}
   case 2:desenharCorte(a);break;
   case 3:{ // carta girando com rastro de cartas fantasma
    BufferedImage[] q=quadrosSimbolo(3);
    for(int k=3;k>=0;k--){Graphics2D c=(Graphics2D)a.create();c.setComposite(AlphaComposite.SrcOver.derive(k==0?1f:.18f));c.translate(px-k*14*(tempo<1.0?1:-1),py);c.rotate(giroCarta-k*.3);if(q!=null)c.drawImage(q[2],-38,-38,76,76,null);c.dispose();}
    break;}
   case 4:{ // raios em zigue-zague entre os alvos
    for(int i=1;i<corrente.size();i++){double[] p0=corrente.get(i-1),p1=corrente.get(i);int seg=9;int[] xs=new int[seg+1],ys=new int[seg+1];
     for(int k=0;k<=seg;k++){double f=k/(double)seg;double j=(k==0||k==seg)?0:(acaso.nextDouble()-.5)*26;xs[k]=(int)(p0[0]+(p1[0]-p0[0])*f-(p1[1]-p0[1])/Math.max(1,Math.hypot(p1[0]-p0[0],p1[1]-p0[1]))*j);ys[k]=(int)(p0[1]+(p1[1]-p0[1])*f+(p1[0]-p0[0])/Math.max(1,Math.hypot(p1[0]-p0[0],p1[1]-p0[1]))*j);}
     a.setStroke(new BasicStroke(7));a.setColor(new Color(60,120,255,110));a.drawPolyline(xs,ys,seg+1);a.setStroke(new BasicStroke(2.5f));a.setColor(new Color(225,240,255));a.drawPolyline(xs,ys,seg+1);
     if(i>0){a.setColor(new Color(120,180,255,150));a.fillOval((int)p1[0]-14,(int)p1[1]-14,28,28);}}
    break;}
   case 5:{ // pentagrama sob a nave e bolas de fogo orbitando
    BufferedImage[] q=quadrosSimbolo(5);
    if(q!=null){Graphics2D c=(Graphics2D)a.create();c.setComposite(AlphaComposite.SrcOver.derive(.35f));c.translate(px,py);c.rotate(tempo*.8);c.drawImage(q[2],-80,-80,160,160,null);c.dispose();}
    for(double[] o:orbes()){int ox=(int)o[0],oy=(int)o[1];for(int k=0;k<4;k++){a.setColor(new Color(255,90+k*30,30,200-k*40));int r2=14-k*3;a.fillOval(ox-r2-k*4,oy-r2+(int)(Math.sin(tempo*20+k)*2),r2*2,r2*2);}a.setColor(new Color(255,240,160));a.fillOval(ox-5,oy-5,10,10);}
    break;}
   case 6:{ // isca: copia da nave em cristal verde
    if(nave!=null){BufferedImage cam=new BufferedImage(200,120,BufferedImage.TYPE_INT_ARGB);Graphics2D c=cam.createGraphics();ArteNaves.desenharJogador(c,nave.getNivel(),nave.getModeloVisual(),60,40,tempo);
     c.setComposite(AlphaComposite.SrcAtop.derive(.6f));c.setColor(new Color(80,255,140));c.fillRect(0,0,200,120);c.dispose();
     a.setComposite(AlphaComposite.SrcOver.derive((float)(.75+.2*Math.sin(tempo*8))));a.drawImage(cam,(int)cloneX-60,(int)cloneY-40,null);a.setComposite(AlphaComposite.SrcOver);}
    for(int k=0;k<5;k++){double an=tempo*2+k*1.26;int sx=(int)cloneX+22+(int)(Math.cos(an)*40),sy=(int)cloneY+20+(int)(Math.sin(an)*26);a.setColor(new Color(180,255,200));a.fillPolygon(new int[]{sx,sx+3,sx,sx-3},new int[]{sy-6,sy,sy+6,sy},4);}
    a.setColor(new Color(90,240,120));for(int k=0;k<vidaClone;k++)a.fillRect((int)cloneX+k*8,(int)cloneY-10,6,4);
    break;}
   case 7:{ // revoada: morcegos rodando em volta da nave
    for(int k=0;k<22;k++){double an=tempo*(3+k%3)+k*.9;double rr=26+(k*13)%48;int bx=px+(int)(Math.cos(an)*rr*1.25),by=py+(int)(Math.sin(an)*rr*.9);TiroChefe.morcego(a,bx,by,2,tempo+k*.11,Math.cos(an+Math.PI/2)>0,k%4==0);}
    break;}
   default:break;
  }
  a.dispose();
 }

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
