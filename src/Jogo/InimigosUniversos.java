package Jogo;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
final class InimigosUniversos {
 private static final BufferedImage[] SPRITES=new BufferedImage[8];
 static {
  try {
   BufferedImage atlas=ImageIO.read(InimigosUniversos.class.getResource("/ImagemIcon/inimigos_universos_096.png"));
   for(int i=0;i<8;i++){
    int sx=(i%4)*atlas.getWidth()/4,sy=(i/4)*atlas.getHeight()/2;
    int w=atlas.getWidth()/4,h=atlas.getHeight()/2,minX=w,minY=h,maxX=0,maxY=0;
    for(int y=0;y<h;y++)for(int x=0;x<w;x++)if((atlas.getRGB(sx+x,sy+y)>>>24)>128){minX=Math.min(minX,x);minY=Math.min(minY,y);maxX=Math.max(maxX,x);maxY=Math.max(maxY,y);}
    if(minX>maxX)throw new IllegalStateException("Sprite vazio: "+i);
    BufferedImage b=new BufferedImage(84,64,BufferedImage.TYPE_INT_ARGB);Graphics2D g=b.createGraphics();
    double escala=Math.min(80.0/(maxX-minX+1),60.0/(maxY-minY+1));int dw=(int)((maxX-minX+1)*escala),dh=(int)((maxY-minY+1)*escala);
    g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
    g.drawImage(atlas,(84-dw)/2,(64-dh)/2,(84+dw)/2,(64+dh)/2,sx+minX,sy+minY,sx+maxX+1,sy+maxY+1,null);g.dispose();SPRITES[i]=b;
   }
  } catch(java.io.IOException ex){throw new ExceptionInInitializerError(ex);}
 }
 private InimigosUniversos(){}
 static void desenhar(Graphics2D g,int setor,Alien.Classe classe,int x,int y,double tempo,boolean congelado){
  BufferedImage b=SPRITES[Math.max(0,Math.min(7,setor))];Graphics2D a=(Graphics2D)g.create();a.translate(x-10,y-4);
  a.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
  double t=congelado?0:tempo;
  for(int lado=0;lado<2;lado++){
   Graphics2D asa=(Graphics2D)a.create();asa.rotate(Math.sin(t*4.5)*.055*(lado==0?1:-1),42,32);
   int sy=lado==0?0:43;asa.drawImage(b,0,sy,84,sy+21,0,sy,84,sy+21,null);asa.dispose();
  }
  a.drawImage(b,0,21,84,43,0,21,84,43,null);a.dispose();
 }
}
