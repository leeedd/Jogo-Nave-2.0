package Jogo;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.lang.reflect.*;
import java.util.*;
import javax.imageio.ImageIO;
import java.io.*;
public final class TesteEnxame {
 static int checks;
 static void ok(boolean b,String s){if(!b)throw new AssertionError(s);checks++;}
 static Object get(Object o,String n)throws Exception{Field f=o.getClass().getDeclaredField(n);f.setAccessible(true);return f.get(o);}
 static void set(Object o,String n,Object v)throws Exception{Field f=o.getClass().getDeclaredField(n);f.setAccessible(true);f.set(o,v);}
 static Object call(Object o,String n)throws Exception{Method m=o.getClass().getDeclaredMethod(n);m.setAccessible(true);return m.invoke(o);}
 static void tick(ComandanteEnxame c,double sec){for(int i=0;i<(int)(sec*100);i++)c.atualizar(.01,280);}
 static void abrir(ComandanteEnxame c){for(int i=0;i<3000&&!c.exposto();i++)c.atualizar(.01,280);ok(c.exposto(),"abertura garantida");}
 static void imagem(String n,ComandanteEnxame c)throws Exception{BufferedImage b=new BufferedImage(960,600,2);Graphics2D g=b.createGraphics();g.setColor(new Color(4,8,20));g.fillRect(0,0,960,600);c.desenhar(g);for(int i=0;i<6;i++)ArteEnxame.criatura(g,i,100+i*80,200,.3);g.dispose();try(OutputStream o=new FileOutputStream("swarm_build/"+n+".png")){ImageIO.write(b,"png",o);}}
 public static void main(String[] args)throws Exception{
  for(boolean rainha:new boolean[]{false,true}){
   ComandanteEnxame c=new ComandanteEnxame(rainha,3);int max=c.max();
   c.dano(999);ok(c.vida()==max,"blindagem fechada");
   abrir(c);c.impacto(new Rectangle(0,0,1,1),999);ok(c.vida()==max,"tiro fora do nucleo");
   imagem(rainha?"Rainha_Coracao":"Pirata_Reator",c);
   c.impacto(c.nucleo(),(int)(max*.4));ok(c.vida()<max*.65,"dano no ponto fraco");
   ok(c.exposto(),"limiar nao encerra abertura");tick(c,3.2);ok(c.estagio()==1,"segundo padrao");
   abrir(c);c.impacto(c.nucleo(),(int)(max*.35));tick(c,3.2);ok(c.estagio()==2,"terceiro padrao");
   abrir(c);c.impacto(c.nucleo(),9999);ok(c.morrendo()&&!c.terminou(),"derrota nao instantanea");
   ok(!c.consumirDano(new Rectangle(0,0,960,600)),"morte nao dispara");tick(c,1);imagem(rainha?"Rainha_Explosao":"Pirata_Explosao",c);
   ok(!c.terminou(),"sequencia de explosoes visivel");tick(c,2);ok(c.terminou(),"explosao termina");
  }
  Fase f=new Fase(564);f.pararTimerTeste();
  ok(f.getAliens().size()>=6,"onda normal maior");ok((int)call(f,"quantidadeSetor")==56,"primeiro setor 56");
  call(f,"iniciarCombateIntercepcao");ok(f.getAliens().size()==10,"enxame inicial dez");
  Set<Integer> formas=new HashSet<>(),especies=new HashSet<>();int anterior=-1,total=10;
  for(Alien a:f.getAliens())especies.add(a.especieEnxame());
  for(int w=0;w<5;w++){
   f.getAliens().clear();call(f,"lancarEnxame");total+=f.getAliens().size();int forma=(int)get(f,"ultimaFormacaoEnxame");ok(forma!=anterior,"formacao nao repete consecutivamente");anterior=forma;formas.add(forma);
   for(Alien a:f.getAliens())especies.add(a.especieEnxame());
  }
  ok(total==60,"sessenta participantes por emboscada");ok(especies.size()==6,"seis especies");ok(formas.size()>1,"formacoes variam");
  f.getAliens().clear();call(f,"lancarEnxame");ok(f.getAliens().isEmpty(),"reforcos finitos");
  call(f,"iniciarCombateIntercepcao");call(f,"lancarEnxame");ok(f.getAliens().size()==18,"limite dezoito");call(f,"lancarEnxame");ok(f.getAliens().size()==18,"nao excede limite");
  MiniChefe m=f.getMiniChefeTeste();abrir(m.comandanteTeste());m.receberImpacto(m.comandanteTeste().nucleo(),999);call(f,"derrotarMiniChefe");ok(f.getMiniChefeTeste()!=null,"nao remove antes da explosao");
  tick(m.comandanteTeste(),3);call(f,"derrotarMiniChefe");ok(f.getMiniChefeTeste()==null,"remove depois da explosao");int pontos=f.getPontos();call(f,"derrotarMiniChefe");ok(f.getPontos()==pontos,"premio unico");
  // Pausa congela o comandante e a fila de reforcos.
  call(f,"iniciarCombateIntercepcao");set(f,"pausado",true);double antes=(double)get(f.getMiniChefeTeste().comandanteTeste(),"t");for(int i=0;i<100;i++)f.atualizarSimulacao(.01);ok((double)get(f.getMiniChefeTeste().comandanteTeste(),"t")==antes,"pausa congela comandante");
  set(f,"pausado",false);ComandanteEnxame c=f.getMiniChefeTeste().comandanteTeste();abrir(c);c.impacto(c.nucleo(),9999);
  int hp=f.getNave().getVida(),nx=f.getNave().getX(),restantes=f.getAliens().size();
  for(int i=0;i<150;i++)f.atualizarSimulacao(.01);
  ok(f.getMiniChefeTeste()!=null,"cinematica mantem corpo");ok(hp==f.getNave().getVida()&&nx==f.getNave().getX(),"cinematica protege e congela nave");ok(restantes==f.getAliens().size(),"nao repoe durante explosao");
  for(int i=0;i<150;i++)f.atualizarSimulacao(.01);ok(f.getMiniChefeTeste()==null,"cinematica conclui pela simulacao");
  f.getAliens().clear();for(int i=0;i<220;i++)f.atualizarSimulacao(.01);ok(f.getEtapaTeste().equals("DOBRA"),"volta a dobra apos limpar enxame");
  for(int e=0;e<6;e++){
   BufferedImage a=new BufferedImage(160,160,2),b=new BufferedImage(160,160,2),d=new BufferedImage(160,160,2);Graphics2D ga=a.createGraphics(),gb=b.createGraphics(),gd=d.createGraphics();
   ArteEnxame.criatura(ga,e,50,50,.1);ArteEnxame.ataque(gb,e,50,50,.1,.3);ArteEnxame.derrota(gd,e,50,50,.6);ga.dispose();gb.dispose();gd.dispose();
   ok(!Arrays.equals(a.getRGB(0,0,160,160,null,0,160),b.getRGB(0,0,160,160,null,0,160)),"ataque proprio "+e);
   ok(!Arrays.equals(a.getRGB(0,0,160,160,null,0,160),d.getRGB(0,0,160,160,null,0,160)),"derrota propria "+e);
  }
  call(f,"iniciarCombateIntercepcao");
  // Render de integracao usando recursos do pacote.
  f.setSize(960,600);set(f,"pausado",false);BufferedImage img=new BufferedImage(960,600,2);Graphics2D g=img.createGraphics();f.paint(g);g.dispose();try(OutputStream o=new FileOutputStream("swarm_build/Emboscada.png")){ImageIO.write(img,"png",o);}
  System.out.println("OK Enxame: "+checks+" verificacoes");System.exit(0);
 }
}
