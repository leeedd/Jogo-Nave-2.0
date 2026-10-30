package Jogo;
import java.awt.*;import java.awt.image.BufferedImage;import java.io.File;import javax.imageio.ImageIO;
public final class Prancha094 {
 public static void main(String[] args)throws Exception{BufferedImage b=new BufferedImage(1440,1680,BufferedImage.TYPE_INT_RGB);Graphics2D g=b.createGraphics();g.setColor(new Color(3,8,20));g.fillRect(0,0,1440,1680);String[] nomes={"OZZY","HALFORD","BRUCE / EDDIE","LEMMY","JAMES","TOM","CHUCK"};for(int id=1;id<=7;id++){int y=(id-1)*240;for(int forma=1;forma<=2;forma++)for(int q=0;q<3;q++){Graphics2D a=(Graphics2D)g.create();a.translate((forma-1)*720+q*240,y+20);Sprites16Bits.desenhar(a,id,forma,240,220,0,q==0?0:q==1?-.5:.7);a.dispose();}g.setColor(new Color(80,190,230));g.setFont(new Font("Monospaced",1,14));g.drawString(nomes[id-1]+" / FORMA 1",12,y+19);g.drawString("FORMA 2",730,y+19);g.setColor(new Color(30,52,76));g.drawLine(0,y+239,1440,y+239);}g.dispose();ImageIO.write(b,"png",new File("Chefes_Metal_Space_094.png"));}
}
