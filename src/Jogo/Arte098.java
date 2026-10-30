package Jogo;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
final class Arte098 {
 private static final BufferedImage[] CHAVES=carregar("chaves_098.png",2),ITENS=carregar("equipamentos_098.png",3),FUSOES=carregar("fusoes_098.png",2);
 private Arte098(){}
 private static BufferedImage[] carregar(String nome,int linhas){
  try{
   BufferedImage a=ImageIO.read(Arte098.class.getResource("/ImagemIcon/"+nome));BufferedImage[] out=new BufferedImage[4*linhas];
   for(int i=0;i<out.length;i++){
    int x0=i%4*a.getWidth()/4,y0=i/4*a.getHeight()/linhas,w=a.getWidth()/4,h=a.getHeight()/linhas;
    int minx=w,miny=h,maxx=0,maxy=0;
    for(int y=0;y<h;y++)for(int x=0;x<w;x++)if((a.getRGB(x0+x,y0+y)>>>24)>100){minx=Math.min(minx,x);miny=Math.min(miny,y);maxx=Math.max(maxx,x);maxy=Math.max(maxy,y);}
    if(minx>maxx)throw new IllegalStateException("Arte vazia: "+nome+"/"+i);
    BufferedImage b=new BufferedImage(128,128,2);Graphics2D g=b.createGraphics();g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
    double escala=Math.min(120.0/(maxx-minx+1),120.0/(maxy-miny+1));int dw=(int)((maxx-minx+1)*escala),dh=(int)((maxy-miny+1)*escala);
    g.drawImage(a,(128-dw)/2,(128-dh)/2,(128+dw)/2,(128+dh)/2,x0+minx,y0+miny,x0+maxx+1,y0+maxy+1,null);g.dispose();out[i]=b;
   }return out;
  }catch(java.io.IOException e){throw new ExceptionInInitializerError(e);}
 }
 static BufferedImage chave(int indice){return CHAVES[indice];}
 static void item(Graphics2D g,int i,int x,int y,int s){desenhar(g,ITENS[Math.max(0,Math.min(11,i))],x,y,s);}
 static void fusao(Graphics2D g,int i,int x,int y,int s){desenhar(g,i==0?ITENS[11]:FUSOES[i-1],x,y,s);}
 private static void desenhar(Graphics2D g,BufferedImage b,int x,int y,int s){Graphics2D a=(Graphics2D)g.create();a.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);a.drawImage(b,x,y,s,s,null);a.dispose();}
}
