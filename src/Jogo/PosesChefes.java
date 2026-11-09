package Jogo;
import java.awt.image.BufferedImage;import javax.imageio.ImageIO;
/** Separa silhuetas completas, sem depender de linhas de corte aproximadas. */
final class PosesChefes {
 private static final BufferedImage[][][] CACHE=new BufferedImage[8][3][4];
 static synchronized BufferedImage obter(int id,int forma,int quadro){if(forma==2){BufferedImage[] n=novaArte(id);if(n!=null)return n[quadro];}if(CACHE[id][forma][0]==null)carregar(id);return CACHE[id][forma][quadro];}
 private static final BufferedImage[][] NOVA=new BufferedImage[8][];private static final boolean[] CONSULTADA=new boolean[8];
 /** Arte nova do ato II (ImagemIcon/chefe_N_ato2.png), ja virada para a esquerda. */
 static synchronized boolean temNovaArte(int id){return id>=1&&id<=7&&novaArte(id)!=null;}
 private static BufferedImage[] novaArte(int id){
  if(id<1||id>7)return null;if(CONSULTADA[id])return NOVA[id];CONSULTADA[id]=true;
  java.net.URL u=PosesChefes.class.getResource("/ImagemIcon/chefe_"+id+"_ato2.png");if(u==null)return null;
  try{BufferedImage a=ImageIO.read(u);
   if(a.getWidth()>=a.getHeight()*3){
    // Prancha com 4 celulas iguais: repouso, preparo, golpe (com o efeito) e bate-cabeca.
    int c=a.getWidth()/4;NOVA[id]=new BufferedImage[4];for(int q=0;q<4;q++)NOVA[id][q]=a.getSubimage(q*c,0,c,a.getHeight());if(id==5)NOVA[id][3]=NOVA[id][0]; // James: o bate-cabeca vinha de cabelo comprido, destoando dos outros quadros
   }else{
    // Imagem unica: quatro quadros do mesmo tamanho gerados por inclinacao.
    NOVA[id]=new BufferedImage[]{pose(a,0,1,1),pose(a,.05,1,1),pose(a,-.07,1.03,1.03),pose(a,0,1,.985)};
   }
  }catch(java.io.IOException e){System.err.println("Arte do ato II ilegivel: "+id);}
  return NOVA[id];
 }
 private static BufferedImage pose(BufferedImage a,double giro,double ex,double ey){
  int w=a.getWidth(),h=a.getHeight(),cw=(int)(w*1.16),ch=(int)(h*1.08);BufferedImage o=new BufferedImage(cw,ch,2);java.awt.Graphics2D g=o.createGraphics();
  g.setRenderingHint(java.awt.RenderingHints.KEY_INTERPOLATION,java.awt.RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
  g.translate(cw/2.0,ch-4);g.rotate(giro);g.scale(ex,ey);g.drawImage(a,-w/2,-h,null);g.dispose();return o;
 }
 private static void carregar(int id){try{
  BufferedImage a=ImageIO.read(PosesChefes.class.getResource("/ImagemIcon/boss_"+id+"_atlas_094.png"));int w=a.getWidth(),h=a.getHeight();int[] labels=new int[w*h],fila=new int[w*h];java.util.List<int[]> partes=new java.util.ArrayList<>();int rotulo=0;
  for(int y=0;y<h;y++)for(int x=0;x<w;x++){int inicio=y*w+x;if(labels[inicio]!=0||(a.getRGB(x,y)>>>24)<=100)continue;int codigo=++rotulo,ler=0,fim=1,minX=x,maxX=x,minY=y,maxY=y;fila[0]=inicio;labels[inicio]=codigo;
   while(ler<fim){int atual=fila[ler++],ax=atual%w,ay=atual/w;minX=Math.min(minX,ax);maxX=Math.max(maxX,ax);minY=Math.min(minY,ay);maxY=Math.max(maxY,ay);
    for(int d=0;d<4;d++){int nx=ax+(d==0?-1:d==1?1:0),ny=ay+(d==2?-1:d==3?1:0);if(nx<0||nx>=w||ny<0||ny>=h)continue;int n=ny*w+nx;if(labels[n]==0&&(a.getRGB(nx,ny)>>>24)>100){labels[n]=codigo;fila[fim++]=n;}}}
   if(fim>1000)partes.add(new int[]{codigo,fim,minX,minY,maxX,maxY});
  }
  partes.sort((b,c)->Integer.compare(c[1],b[1]));if(partes.size()<8)throw new IllegalStateException("Prancha incompleta: "+id);partes=new java.util.ArrayList<>(partes.subList(0,8));partes.sort((b,c)->Integer.compare(b[3]+b[5],c[3]+c[5]));
  for(int row=0;row<2;row++){java.util.List<int[]> linha=partes.subList(row*4,row*4+4);linha.sort((b,c)->Integer.compare(b[2],c[2]));int cw=1,ch=1;for(int[] c:linha){cw=Math.max(cw,c[4]-c[2]+1);ch=Math.max(ch,c[5]-c[3]+1);}for(int q=0;q<4;q++){int[] c=linha.get(q);BufferedImage out=new BufferedImage(cw+8,ch+8,2);int dx=(cw-(c[4]-c[2]+1))/2+4,dy=ch-(c[5]-c[3]+1)+4;for(int yy=c[3];yy<=c[5];yy++)for(int xx=c[2];xx<=c[4];xx++)if(labels[yy*w+xx]==c[0])out.setRGB(dx+xx-c[2],dy+yy-c[3],a.getRGB(xx,yy));CACHE[id][row+1][q]=out;}}
 }catch(java.io.IOException e){throw new IllegalStateException(e);}}
 private PosesChefes(){}
}
