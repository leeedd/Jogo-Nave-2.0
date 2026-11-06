package Jogo;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.reflect.Method;
import javax.imageio.ImageIO;
/** Luta final: Jack (1o dente), Kyle (2o dente), Deezleboss invocado, Dio com a corrente do Holy Diver, e os creditos. */
public final class TesteFinal099 {
 static int n;
 static void ok(boolean b,String m){if(!b)throw new AssertionError(m);n++;}
 static Object get(Object o,String c)throws Exception{return TesteInvasao093.get(o,c);}
 static void call(Fase f,String m)throws Exception{Method x=Fase.class.getDeclaredMethod(m);x.setAccessible(true);x.invoke(f);}
 static String pasta;
 static void foto(Fase f,String nome)throws Exception{if(pasta==null)return;f.setSize(960,600);BufferedImage b=new BufferedImage(960,600,BufferedImage.TYPE_INT_RGB);Graphics2D g=b.createGraphics();f.paint(g);g.dispose();ImageIO.write(b,"png",new File(pasta,nome+".png"));}
 /** Simula com a nave sempre inteira (o teste e da luta, nao do piloto). */
 static void tick(Fase f,double s){for(int i=0;i<(int)(s*60);i++){f.getNave().curar(1000);f.atualizarSimulacao(1.0/60);}}
 static void bater(Fase f,int ato)throws Exception{Chefe c=f.getChefeTeste();for(int i=0;i<500&&c.getAtoFinalTeste()==ato;i++){if(c.receberDano(25)){call(f,"derrotarChefao");return;}tick(f,.02);}}
 public static void main(String[] a)throws Exception{
  pasta=a.length>0?a[0]:null;if(pasta!=null)new File(pasta).mkdirs();
  Fase f=TesteInvasao093.fase(100);TesteInvasao093.set(f,"chefesDerrotados",8);TesteInvasao093.set(f,"hellraiserVencido",true);TesteInvasao093.set(f,"reuniaoVencida",true);call(f,"iniciarFinal");
  Chefe c=f.getChefeTeste();
  ok(c!=null&&c.getFase()==Fase.FINAL,"luta final comeca");ok(c.getAtoFinalTeste()==ChefeFinal.JACK&&c.getDentesTeste()==0,"primeiro o Jack");
  ok(!c.receberDano(50)&&c.getVida()==c.getVidaMaxima(),"entrando nao leva dano");
  tick(f,3.3);foto(f,"final_1_jack");ok(!f.getTirosChefesTeste().isEmpty(),"Jack ataca");
  bater(f,ChefeFinal.JACK);ok(c.getAtoFinalTeste()==ChefeFinal.KYLE&&c.getDentesTeste()==1,"Jack arranca o 1o dente");
  tick(f,.1);ok(f.getTirosChefesTeste().isEmpty(),"troca de ato limpa os tiros");
  tick(f,3.6);foto(f,"final_2_kyle");
  bater(f,ChefeFinal.KYLE);ok(c.getAtoFinalTeste()==ChefeFinal.DEEZLE&&c.getDentesTeste()==2,"Kyle arranca o 2o dente");
  tick(f,1.8);foto(f,"final_3_invocacao");tick(f,3.4);foto(f,"final_4_deezleboss");
  ok(c.getVida()==c.getVidaMaxima(),"Deezleboss com a vida cheia");
  bater(f,ChefeFinal.DEEZLE);ok(c.getAtoFinalTeste()==ChefeFinal.DIO&&c.estaVivo(),"Deezleboss vira o Dio");
  tick(f,1.6);foto(f,"final_5_revelacao");tick(f,3);
  boolean agarrou=false,raio=false,demonio=false;double x0=0;
  for(int i=0;i<60*24&&!(agarrou&&raio&&demonio);i++){
   f.getNave().curar(1000);f.getNave().teleportar(120,300);f.atualizarSimulacao(1.0/60);
   Object fim=get(c,"fim");
   if(!raio&&(double)get(fim,"raio")>0){raio=true;foto(f,"final_6_raio_arco_iris");}
   if(!demonio&&(double)get(fim,"demonio")>0&&(double)get(fim,"demonioX")<700){demonio=true;foto(f,"final_7_demonio_holy_diver");}
   if(!agarrou&&c.naveAgarrada()){agarrou=true;x0=f.getNave().getX();for(int k=0;k<20;k++){f.getNave().curar(1000);f.atualizarSimulacao(1.0/60);}foto(f,"final_8_corrente");ok(f.getNave().getX()>x0,"corrente arrasta a nave para o Dio");}
  }
  ok(raio,"Dio solta o arco-iris");ok(demonio,"demonio do Holy Diver ataca");ok(agarrou,"corrente do Holy Diver agarra a nave");
  bater(f,ChefeFinal.DIO);ok(f.getChefeTeste()==null,"Dio derrotado");ok((boolean)get(f,"finalVencido"),"final vencido");
  for(int i=0;i<60*20&&!f.getEtapaTeste().equals("CREDITOS");i++){f.getNave().curar(1000);f.atualizarSimulacao(1.0/60);}
  ok(f.getEtapaTeste().equals("CREDITOS"),"depois do Dio vem os creditos");
  System.out.println("OK Final: "+n+" verificacoes");System.exit(0);
 }
}
