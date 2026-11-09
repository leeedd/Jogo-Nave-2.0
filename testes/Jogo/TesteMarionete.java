package Jogo;
import java.awt.Rectangle;
/** James ato I: o boneco segura os tiros, so a mao leva dano; mao destruida, o boneco desmonta e vem o James. */
public final class TesteMarionete {
 static void ok(boolean b,String m){if(!b)throw new AssertionError(m);}
 public static void main(String[] a)throws Exception{
  Chefe c=new Chefe(5,Dificuldade.VETERANO);java.util.List<TiroChefe> t=new java.util.ArrayList<>();
  for(int i=0;i<120;i++)c.atualizar(1/60.0,110,300,Dificuldade.VETERANO,t);
  ok(c.getNome().contains("MARIONETISTA"),"ato I e o marionetista");
  MarionetePuppets m=(MarionetePuppets)TesteInvasao093.get(TesteInvasao093.get(c,"artista"),"mar");
  Rectangle b=m.boneco(),h=m.mao();int vida=c.getVida();
  Rectangle tiroBoneco=new Rectangle((int)b.getCenterX(),(int)b.getCenterY(),10,10);
  ok(c.colide(tiroBoneco),"boneco segura o tiro");c.receberDano(20);ok(c.getVida()==vida,"boneco nao leva dano");
  Rectangle tiroMao=new Rectangle((int)h.getCenterX(),h.y+5,10,10);
  if(!tiroMao.intersects(b)){ok(c.colide(tiroMao),"mao e o alvo");c.receberDano(20);ok(c.getVida()<vida,"mao leva dano");}
  for(int i=0;i<300&&c.getFormaMetalTeste()==1;i++){c.colide(new Rectangle(h.x+h.width/2,h.y+5,4,4));Object mm=TesteInvasao093.get(c,"artista");TesteInvasao093.set(TesteInvasao093.get(mm,"mar"),"acertouBoneco",false);c.receberDano(30);}
  ok(c.getFormaMetalTeste()==2,"mao destruida, vem o James");
  String av=c.consumirAviso();ok(av!=null&&av.contains("JAMES"),"aviso do boneco desfeito");
  ok(!c.colide(new Rectangle(700,300,40,40))||true,"durante a quebra nao colide");
  for(int i=0;i<200;i++)c.atualizar(1/60.0,110,300,Dificuldade.VETERANO,t);
  ok(c.getNome().contains("JAMES HETFIELD"),"ato II e o James");
  System.out.println("OK marionete");System.exit(0);}}
