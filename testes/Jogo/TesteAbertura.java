package Jogo;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
/** Abertura: passa pela placa, acende o logo e chama o menu no fim; ENTER pula. */
public final class TesteAbertura {
 static void ok(boolean b,String m){if(!b)throw new AssertionError(m);}
 static long brilho(AberturaJogo a){a.setSize(960,600);BufferedImage b=new BufferedImage(960,600,BufferedImage.TYPE_INT_RGB);Graphics2D g=b.createGraphics();a.paint(g);g.dispose();long s=0;for(int y=0;y<600;y+=4)for(int x=0;x<960;x+=4){int p=b.getRGB(x,y);s+=(p>>16&255)+(p>>8&255)+(p&255);}return s;}
 public static void main(String[] a)throws Exception{
  int[] chamou={0};AberturaJogo ab=new AberturaJogo(()->chamou[0]++);
  ab.atualizar(1.5);long placa=brilho(ab);ok(placa>0,"placa aparece");
  ab.atualizar(AberturaJogo.PLACA-1.5+.3);long escuro=brilho(ab);
  ab.atualizar(1.6);long acendendo=brilho(ab);ok(acendendo>escuro,"logo acende");
  ab.atualizar(AberturaJogo.DURACAO);ok(chamou[0]==1&&ab.terminouTeste(),"chama o menu no fim");
  ab.atualizar(1);ok(chamou[0]==1,"chama uma vez so");
  int[] c2={0};AberturaJogo pula=new AberturaJogo(()->c2[0]++);pula.atualizar(.5);
  for(java.awt.event.KeyListener l:pula.getKeyListeners())l.keyPressed(new java.awt.event.KeyEvent(pula,java.awt.event.KeyEvent.KEY_PRESSED,0,0,java.awt.event.KeyEvent.VK_ENTER,'\n'));
  ok(c2[0]==1,"ENTER pula");
  System.out.println("OK abertura");System.exit(0);}}
