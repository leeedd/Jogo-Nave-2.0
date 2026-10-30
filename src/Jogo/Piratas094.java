package Jogo;
import java.awt.*;import java.awt.image.BufferedImage;import javax.imageio.ImageIO;
final class Piratas094 {
 private static final BufferedImage[] NAVES=new BufferedImage[6];
 static{try{BufferedImage a=ImageIO.read(Piratas094.class.getResource("/ImagemIcon/piratas_094.png"));for(int i=0;i<6;i++){BufferedImage b=new BufferedImage(84,64,2);Graphics2D g=b.createGraphics();g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);int w=a.getWidth()/3,h=a.getHeight()/2;g.drawImage(a,0,0,84,64,i%3*w,i/3*h,i%3*w+w,i/3*h+h,null);g.dispose();NAVES[i]=b;}}catch(java.io.IOException e){throw new ExceptionInInitializerError(e);}}
 static void desenhar(Graphics2D g,Alien.Classe c,int x,int y,boolean elite,double t){int i=elite?5:c==Alien.Classe.TANQUE?1:c==Alien.Classe.LASER?2:c==Alien.Classe.KAMIKAZE?3:c==Alien.Classe.ESQUADRAO?4:0;BufferedImage b=NAVES[i];Graphics2D a=(Graphics2D)g.create();a.translate(x-10,y-4);for(int lado=0;lado<2;lado++){Graphics2D asa=(Graphics2D)a.create();asa.rotate(Math.sin(t*4)*.035*(lado==0?1:-1),45,32);int sy=lado==0?0:42;asa.drawImage(b,0,sy,84,sy+22,0,sy,84,sy+22,null);asa.dispose();}a.drawImage(b,0,22,84,42,0,22,84,42,null);a.setColor((int)(t*12)%2==0?new Color(255,96,48):new Color(255,216,169));a.fillRect(78,30,3+(int)(Math.sin(t*24)*2+2),3);a.dispose();}
 private Piratas094(){}
}
