package Jogo;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.ImageIO;
public final class PreviewInterdicao097 {
 public static void main(String[] args)throws Exception{
  new File("mapa/frames").mkdirs();CenaInterdicao cena=new CenaInterdicao(new Nave());
  BufferedImage prancha=new BufferedImage(1440,900,BufferedImage.TYPE_INT_RGB);Graphics2D pg=prancha.createGraphics();pg.setColor(Color.BLACK);pg.fillRect(0,0,1440,900);
  for(int i=0;i<94;i++){
   BufferedImage b=new BufferedImage(960,600,BufferedImage.TYPE_INT_RGB);Graphics2D g=b.createGraphics();g.setColor(Color.BLACK);g.fillRect(0,0,960,600);cena.desenhar(g);g.dispose();
   try(OutputStream out=new FileOutputStream(String.format(java.util.Locale.ROOT,"mapa/frames/%03d.png",i))){ImageIO.write(b,"png",out);}
   if(i%18==0){int j=i/18;pg.drawImage(b,(j%3)*480,(j/3)*450,480,400,null);pg.setColor(Color.WHITE);pg.drawString(String.format(java.util.Locale.ROOT,"%.1fs",i/15.0),(j%3)*480+20,(j/3)*450+424);}
   cena.atualizar(1.0/15);
  }
  pg.dispose();try(OutputStream out=new FileOutputStream("mapa/05_Sequencia_Interdicao.png")){ImageIO.write(prancha,"png",out);}
  System.out.println("94 quadros renderizados");
 }
}
