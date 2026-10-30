package Jogo;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
final class Arte095 {
 private static final BufferedImage[] sprites=new BufferedImage[12];
 static {try{
  BufferedImage a=ImageIO.read(Arte095.class.getResource("/ImagemIcon/recursos_095.png"));
  int[] ys={0,a.getHeight()/2,a.getHeight()*7/10,a.getHeight()};
  for(int i=0;i<12;i++){int row=i/4; sprites[i]=a.getSubimage(i%4*a.getWidth()/4,ys[row],a.getWidth()/4,ys[row+1]-ys[row]);}
 }catch(java.io.IOException ex){throw new ExceptionInInitializerError(ex);}}
 static void desenhar(Graphics2D g,int i,int x,int y,int w,int h){
  g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
  g.drawImage(sprites[i],x,y,w,h,null);
 }
 private Arte095(){}
}
