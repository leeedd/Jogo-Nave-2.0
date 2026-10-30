package Jogo;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.Arrays;
import javax.imageio.ImageIO;
public final class TesteEvolucoes {
 private static int verificacoes;
 private static void ok(boolean valor,String nome){if(!valor)throw new AssertionError(nome);verificacoes++;}
 public static void main(String[] args)throws Exception {
  BufferedImage prancha=new BufferedImage(1440,670,2);Graphics2D g=prancha.createGraphics();
  g.setColor(new Color(5,9,19));g.fillRect(0,0,1440,670);
  g.setColor(Color.WHITE);g.setFont(new Font("Monospaced",Font.BOLD,23));g.drawString("METAL SPACE / EVOLUCOES 0 A 8",24,34);
  String[] nomes={"RAPTOR","SHADOW","VALKYRIE","BERSERKER","METAL GOD"};
  for(int modelo=0;modelo<5;modelo++){
   Nave nave=new Nave();nave.definirModeloVisual(modelo);int base=nave.getVidaMaxima();int[] anterior=null;
   for(int nivel=0;nivel<=8;nivel++){
    if(nivel>0)nave.melhorar();ok(nave.getNivel()==nivel,"nivel "+nivel);
    ok(nave.getVidaMaxima()==base+nivel/2,"casco "+nivel);
    ok(nave.getVida()==nave.getVidaMaxima(),"nova blindagem preenchida");
    BufferedImage sprite=new BufferedImage(112,96,2);Graphics2D a=sprite.createGraphics();
    ArteNaves.desenharJogador(a,nivel,modelo,40,30,0);a.dispose();
    int[] pixels=sprite.getRGB(0,0,112,96,null,0,112);
    if(anterior!=null)ok(!Arrays.equals(anterior,pixels),"silhueta distinta "+modelo+":"+nivel);anterior=pixels;
    g.drawImage(sprite,nivel*158,67+modelo*116,156,110,null);
    g.setFont(new Font("Monospaced",Font.BOLD,11));g.setColor(ArteNaves.corModelo(modelo));g.drawString(nomes[modelo]+" / "+nivel,nivel*158+13,67+modelo*116);
   }
   nave.melhorar();ok(nave.getNivel()==8,"limite8");
   Missel.setTipoTiro(0);nave.atira();int[] tiros={2,2,3,2,3};ok(nave.getMisseis().size()==tiros[modelo],"arma de fabrica "+modelo+" no nivel 8");
   nave.perderModulo();ok(nave.getNivel()==7,"perda modulo");ok(nave.getVida()<=nave.getVidaMaxima(),"vida limitada");
   for(int i=0;i<10;i++)nave.perderModulo();ok(nave.getNivel()==0,"minimo0");
  }
  g.dispose();ImageIO.write(prancha,"png",new File("evolution/Evolucoes_MetalSpace.png"));
  Fase f=TesteInvasao093.fase(963);TesteInvasao093.set(f,"pontos",765);
  TesteRevisao094.key(f,KeyEvent.VK_ENTER,true);ok((boolean)TesteInvasao093.get(f,"pausado"),"enter pausa");
  TesteRevisao094.key(f,KeyEvent.VK_ENTER,false);TesteRevisao094.key(f,KeyEvent.VK_ENTER,true);
  ok(!(boolean)TesteInvasao093.get(f,"pausado"),"enter retoma");ok(f.getPontos()==765,"sem reiniciar");
  System.out.println("PASSOU: "+verificacoes+" verificacoes");System.exit(0);
 }
}
