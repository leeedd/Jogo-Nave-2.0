package Jogo;
import java.awt.*;import java.awt.image.BufferedImage;import javax.imageio.ImageIO;
final class Arte094 {
 private static final BufferedImage[] CELULAS=new BufferedImage[12];
 static{try{BufferedImage a=ImageIO.read(Arte094.class.getResource("/ImagemIcon/equipamentos_094.png"));for(int i=0;i<12;i++)CELULAS[i]=a.getSubimage(i%4*a.getWidth()/4,i/4*a.getHeight()/3,a.getWidth()/4,a.getHeight()/3);}catch(java.io.IOException e){throw new ExceptionInInitializerError(e);}}
 static BufferedImage imagem(int i,int w,int h){BufferedImage b=new BufferedImage(w,h,2);Graphics2D g=b.createGraphics();desenhar(g,i,0,0,w,h);g.dispose();return b;}
 static void desenhar(Graphics2D g,int i,int x,int y,int w,int h){g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);g.drawImage(CELULAS[i],x,y,w,h,null);}
 static int item(Item.Tipo t){switch(t){case ESCUDO:return 4;case ARMA_2:return 5;case ARMA_GELO:return 6;case ARMA_FOGO:return 7;case REPARO:return 8;default:return 9;}}
 private Arte094(){}
}
