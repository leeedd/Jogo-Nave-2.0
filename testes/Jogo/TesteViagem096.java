package Jogo;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.reflect.Method;
import java.util.*;
import javax.imageio.ImageIO;
public final class TesteViagem096 {
 static int checks;
 static void ok(boolean v,String nome){if(!v)throw new AssertionError(nome);checks++;}
 static void key(Fase f,int k){TesteRefino096.press(f,k);}
 static Object get(Object o,String n)throws Exception{return TesteInvasao093.get(o,n);}
 static void call(Fase f,String n)throws Exception{TesteRevisao094.call(f,n);}
 public static void main(String[] args)throws Exception {
  Fase f=TesteInvasao093.fase(965);key(f,KeyEvent.VK_ENTER);key(f,KeyEvent.VK_3);
  ok((boolean)get(f,"pausado")&&(int)get(f,"abaPausa")==2,"insignias na pausa");
  key(f,KeyEvent.VK_4);ok((int)get(f,"abaPausa")==3,"mapa na pausa");TesteRefino096.render(f,"Mapa_Estelar");
  key(f,KeyEvent.VK_5);TesteRefino096.render(f,"Menu_Pausa");ok((int)get(f,"abaPausa")==4,"menu na pausa");
  key(f,KeyEvent.VK_ENTER);ok(!(boolean)get(f,"pausado"),"retoma");
  Set<Long> hashes=new HashSet<>();
  BufferedImage prancha=new BufferedImage(960,480,2);Graphics2D pg=prancha.createGraphics();
  for(int setor=0;setor<8;setor++){
   BufferedImage b=new BufferedImage(120,100,2);Graphics2D g=b.createGraphics();InimigosUniversos.desenhar(g,setor,Alien.Classe.ORGANICO,25,20,.2,false);g.dispose();hashes.add(TesteInvasao093.hash(b));
   pg.drawImage(b,(setor%4)*240,(setor/4)*240,240,200,null);pg.setColor(Color.WHITE);pg.drawString(MapaEstelar.NOMES[setor],(setor%4)*240+30,(setor/4)*240+220);
  }
  pg.dispose();ImageIO.write(prancha,"png",new File("refino/Inimigos_Tematicos.png"));ok(hashes.size()==8,"8 artes distintas");
  CinematicaNave c1=new CinematicaNave(new Nave(),false,1),c2=new CinematicaNave(new Nave(),false,6);
  c1.atualizar(1.2);c2.atualizar(1.2);BufferedImage b1=new BufferedImage(960,600,2),b2=new BufferedImage(960,600,2);
  Graphics2D g=b1.createGraphics();c1.desenhar(g);g.dispose();g=b2.createGraphics();c2.desenhar(g);g.dispose();ok(TesteInvasao093.hash(b1)!=TesteInvasao093.hash(b2),"dobra varia destino");
  long h=TesteInvasao093.hash(b2);c2.atualizar(.4);g=b2.createGraphics();c2.desenhar(g);g.dispose();ok(h!=TesteInvasao093.hash(b2),"dobra animada");ImageIO.write(b2,"png",new File("refino/Dobra_Animada.png"));
  key(f,KeyEvent.VK_F8);key(f,KeyEvent.VK_4);key(f,KeyEvent.VK_I);
  ok(f.getEtapaTeste().equals("DOBRA"),"entrada emboscada");
  TesteRevisao094.tick(f,1.7);ok(f.getEtapaTeste().equals("INTERDICAO"),"interrompe dobra");
  TesteRevisao094.tick(f,.7);TesteRefino096.render(f,"Cutscene_Interdicao");
  key(f,KeyEvent.VK_ENTER);double tempo=(double)get(f,"tempoInterdicao");TesteRevisao094.tick(f,1);ok((double)get(f,"tempoInterdicao")==tempo,"cutscene pausa");key(f,KeyEvent.VK_ENTER);
  TesteRevisao094.tick(f,5.6);ok((boolean)get(f,"intercepcao")&&f.getMiniChefeTeste()!=null,"boss aparece");ok(f.getAliens().size()==8,"enxame aparece");
  TesteRevisao094.tick(f,2);TesteRefino096.render(f,"Encontro_Interdicao");
  int setor=f.getChefesDerrotadosTeste(),eliminados=(int)get(f,"eliminadosAntesIntercepcao");
  f.getAliens().clear();f.getMiniChefeTeste().receberDano(10000);call(f,"derrotarMiniChefe");
  TesteRevisao094.tick(f,2.2);ok(f.getEtapaTeste().equals("DOBRA"),"retoma dobra apos limpar");
  ok(f.getChefesDerrotadosTeste()==setor,"emboscada nao avanca setor");ok((int)get(f,"eliminados")==eliminados,"nao contamina contagem");
  TesteRevisao094.tick(f,4.3);ok(f.getEtapaTeste().equals("COMBATE")&&!(boolean)get(f,"intercepcao"),"chega ao destino");ok((boolean)get(f,"emboscadaUsada"),"limite uma emboscada");
  key(f,KeyEvent.VK_ENTER);key(f,KeyEvent.VK_M);ok(!((javax.swing.Timer)get(f,"timer")).isRunning(),"voltar encerra timer");
  System.out.println("OK Viagem: "+checks+" verificacoes");System.exit(0);
 }
}
