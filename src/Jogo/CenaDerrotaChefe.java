package Jogo;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;
/** Tremor e explosoes sobre a silhueta inteira, sem recortes retangulares. */
final class CenaDerrotaChefe {
 private final BufferedImage retrato=new BufferedImage(960,600,BufferedImage.TYPE_INT_ARGB);
 private final Rectangle corpo;private double tempo;
 CenaDerrotaChefe(Chefe c){SomJogo.tocar(SomJogo.Efeito.EXPLOSAO_GRANDE);corpo=c.getBounds();Graphics2D g=retrato.createGraphics();c.desenhar(g);g.dispose();}
 void atualizar(double dt){double antes=tempo;tempo+=Math.max(0,dt);if(tempo<2.2&&(int)(antes/.3)!=(int)(tempo/.3))SomJogo.tocar(SomJogo.Efeito.EXPLOSAO);if(antes<2.2&&tempo>=2.2)SomJogo.tocar(SomJogo.Efeito.EXPLOSAO_GRANDE);}boolean terminou(){return tempo>=4.2;}
 void desenhar(Graphics2D g){
  Graphics2D a=(Graphics2D)g.create();
  a.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
  if(tempo<2.75){
   float op=(float)Math.max(0,Math.min(1,(2.75-tempo)/.6));a.setComposite(AlphaComposite.SrcOver.derive(op));
   int dx=(int)(Math.sin(tempo*87)*(4+tempo*3)),dy=(int)(Math.cos(tempo*73)*(3+tempo*2));
   a.drawImage(retrato,dx,dy,null);
  }
  Random r=new Random(950);
  for(int i=0;i<38;i++){
   int x=corpo.x+r.nextInt(Math.max(1,corpo.width)),y=corpo.y+r.nextInt(Math.max(1,corpo.height));
   double idade=tempo-i*.075;if(idade<0||idade>1.2)continue;
   int tamanho=(int)(35+Math.sin(idade/1.2*Math.PI)*125);
   a.setComposite(AlphaComposite.SrcOver.derive((float)Math.max(0,1-idade/1.2)));
   Arte094.desenhar(a,11,x-tamanho/2,y-tamanho/2,tamanho,tamanho);
  }
  double fim=tempo-2.2;
  if(fim>=0&&fim<1.8){
   int tamanho=(int)(120+Math.sin(fim/1.8*Math.PI)*390);
   a.setComposite(AlphaComposite.SrcOver.derive((float)(1-fim/1.8)));
   Arte094.desenhar(a,11,(int)corpo.getCenterX()-tamanho/2,(int)corpo.getCenterY()-tamanho/2,tamanho,tamanho);
  }
  a.dispose();
 }
}
