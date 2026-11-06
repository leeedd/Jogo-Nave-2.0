package Jogo;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.lang.reflect.Method;
import java.util.prefs.Preferences;
import javax.imageio.ImageIO;
import java.io.File;
public final class TestePrevia095 {
 static int n;
 static void ok(boolean b,String s){if(!b)throw new AssertionError(s);n++;}
 static void key(Fase f,int k){TesteRevisao094.key(f,k,true);}
 public static void main(String[] args)throws Exception{
  new File("work095/Metal_Space_0.9.4").mkdirs();
  Preferences prefs=Preferences.userRoot().node("metal095-fixture");prefs.clear();Placar p=new Placar(prefs);
  ok(!p.salvar("AB",100,"RECRUTA"),"tres iniciais obrigatorias");
  ok(!p.salvar("ABC",0,"RECRUTA"),"zero nao entra");
  for(int i=1;i<=12;i++)ok(p.salvar("ABC",i*100,"VETERANO"),"persistencia "+i);
  ok(new Placar(prefs).ler().size()==10,"limite dez");
  ok(p.ler().get(0).pontos==1200&&p.ler().get(9).pontos==300,"ordem e corte");
  prefs.put("r0","corrompido");ok(p.ler().size()==9,"linha invalida ignorada");prefs.removeNode();
  Fase f=TesteInvasao093.fase(95);f.getAliens().clear();
  Method eliminar=Fase.class.getDeclaredMethod("eliminarAlien",Alien.class,boolean.class);eliminar.setAccessible(true);
  Alien a=new Alien(600,300,Alien.Tipo.PIRATA,Alien.Classe.TANQUE);eliminar.invoke(f,a,false);
  ok(f.getPontos()==250,"tanque vale 250");ok((int)TesteInvasao093.get(f,"creditos")==25,"creditos separados");
  eliminar.invoke(f,a,false);ok(f.getPontos()==250,"morte nao duplica recompensa");
  TesteInvasao093.set(f,"creditos",0);ok(f.getPontos()==250,"saldo nao altera placar");
  f.getItens().clear();Nave nave=f.getNave();nave.danoDiretoTeste(8);int hp=nave.getVida();
  Item kit=new Item(Item.Tipo.REPARO,nave.getX(),nave.getY(),false);f.getItens().add(kit);f.checarColisoes();
  // 0.9.9: o reparo cura na hora 10% do casco; o excedente vai para os subtanques
  int unidade=(int)Math.ceil(nave.getVidaMaxima()*.10);
  ok(nave.getVida()==Math.min(nave.getVidaMaxima(),hp+unidade),"coleta cura dez por cento");ok(!kit.isVisible(),"kit coletado some");
  nave.curar(1000);ProgressaoNave inv=(ProgressaoNave)TesteInvasao093.get(f,"inventario");
  Item extra=new Item(Item.Tipo.REPARO,nave.getX(),nave.getY(),false);f.getItens().add(extra);f.checarColisoes();
  ok(extra.isVisible()==!inv.reservaDisponivel(),"vida cheia sem subtanque deixa o kit no campo");
  int cheia=nave.getVida();key(f,KeyEvent.VK_R);ok(nave.getVida()==cheia,"subtanque nao gasta com vida cheia");
  Fase mf=TesteInvasao093.fase(951);mf.getAliens().clear();
  @SuppressWarnings("unchecked") java.util.List<Asteroide> ast=(java.util.List<Asteroide>)TesteInvasao093.get(mf,"asteroides");
  Asteroide mineral=new Asteroide(600,300,25,7);ast.add(mineral);
  ok(mineral.temSuprimento(),"mineral identificavel");
  for(int i=0;i<2;i++){mf.getNave().getMisseis().add(new Missel(590,290,0));mf.checarColisoes();}
  ok(!mineral.isVisible(),"meteoro destruido");
  ok(mf.getItens().stream().anyMatch(it->it.getTipo()==Item.Tipo.REPARO),"meteoro libera reparo");
  TesteInvasao093.set(f,"emJogo",false);TesteInvasao093.set(f,"cenaNave",null);
  key(f,KeyEvent.VK_A);key(f,KeyEvent.VK_B);key(f,KeyEvent.VK_BACK_SPACE);key(f,KeyEvent.VK_C);key(f,KeyEvent.VK_9);key(f,KeyEvent.VK_X);
  ok(TesteInvasao093.get(f,"iniciais").equals("AC9"),"edicao limita tres caracteres");
  key(f,KeyEvent.VK_ENTER);ok((boolean)TesteInvasao093.get(f,"placarConfirmado"),"grava no encerramento");
  f.setSize(960,600);BufferedImage b=new BufferedImage(960,600,2);Graphics2D g=b.createGraphics();f.paint(g);g.dispose();ImageIO.write(b,"png",new File("work095/Metal_Space_0.9.4/Placar_095.png"));
  key(f,KeyEvent.VK_ENTER);ok(f.getPontos()==0&&(int)TesteInvasao093.get(f,"creditos")==0,"nova partida limpa estado");
  b=new BufferedImage(960,600,2);g=b.createGraphics();g.setColor(new Color(2,4,12));g.fillRect(0,0,960,600);g.setColor(Color.WHITE);g.setFont(new Font("Monospaced",1,24));g.drawString("BASE SENTINELA — ESTUDOS DE ANIMACAO",75,50);
  for(int i=0;i<4;i++)Arte095.desenhar(g,i,20+i*235,70,220,300);
  for(int i=4;i<12;i++)Arte095.desenhar(g,i,25+(i-4)*115,420,105,105);
  g.dispose();ImageIO.write(b,"png",new File("work095/Metal_Space_0.9.4/Sprites_095.png"));
  System.out.println("OK Previa 0.9.5: "+n+" verificacoes.");System.exit(0);
 }
}
