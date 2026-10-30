package Jogo;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.reflect.Method;
import java.util.*;
import javax.imageio.ImageIO;
public final class TesteRefino096 {
 static int checks;
 static void ok(boolean b,String msg){if(!b)throw new AssertionError(msg);checks++;}
 static Object get(Object o,String n)throws Exception{return TesteInvasao093.get(o,n);}
 static void press(Fase f,int k){TesteRevisao094.key(f,k,true);TesteRevisao094.key(f,k,false);}
 static void render(Fase f,String name)throws Exception{f.setSize(960,600);BufferedImage b=new BufferedImage(960,600,2);Graphics2D g=b.createGraphics();f.paint(g);g.dispose();ImageIO.write(b,"png",new File("refino/"+name+".png"));}
 public static void main(String[] args)throws Exception{
  MiniChefe mini=new MiniChefe(8);double anterior=305,maxSalto=0;boolean viuTiro=false;
  for(int i=0;i<2400;i++){mini.atualizar(1.0/60,i%600<300?90:550);double atual=(double)get(mini,"y");maxSalto=Math.max(maxSalto,Math.abs(atual-anterior));anterior=atual;viuTiro|=mini.disparando();}
  ok(viuTiro,"sentinela ainda dispara");ok(maxSalto<=260.0/60+1e-8,"sem teleporte em todas as transicoes");
  for(int padrao=0;padrao<8;padrao++){
   Alien a=new Alien(1040,130,Alien.Tipo.PIRATA,Alien.Classe.ESQUADRAO);a.entrarEmEsquadrao(650,400,0,padrao,1.4);
   double x=1040,y=130;double max=0;
   for(int i=0;i<900;i++){a.mexer(1.0/60,Collections.emptyList(),new Random(1));double nx=(double)get(a,"x"),ny=(double)get(a,"y");max=Math.max(max,Math.hypot(nx-x,ny-y));x=nx;y=ny;}
   ok(max<15,"formacao suave "+padrao);ok(a.estaNaFormacao(),"alcanca formacao "+padrao);
  }
  Alien reciclado=new Alien(-73,200,Alien.Tipo.PIRATA,Alien.Classe.ESQUADRAO);
  TesteInvasao093.set(reciclado,"x",-73.9);
  reciclado.mexer(1.0/30,Collections.emptyList(),new Random(2));
  ok(reciclado.getX()>960,"reciclagem fora da tela sem interpolar atraves do campo");
  Alien kamikaze=new Alien(-63,200,Alien.Tipo.PIRATA,Alien.Classe.KAMIKAZE);kamikaze.prepararKamikaze(200);
  kamikaze.mexer(1.0/30,Collections.emptyList(),new Random(2));
  ok(kamikaze.getX()>960,"kamikaze reaparece fora da tela");
  Fase f=TesteInvasao093.fase(96);TesteInvasao093.set(f,"pontos",765);
  press(f,KeyEvent.VK_F8);ok((boolean)get(f,"painelTeste")&&(boolean)get(f,"pausado"),"F8 abre e pausa");render(f,"Painel_Testes");
  press(f,KeyEvent.VK_ESCAPE);ok(!(boolean)get(f,"pausado")&&f.getPontos()==765,"cancelar preserva partida");
  Set<Long> fundos=new HashSet<>();BufferedImage prancha=new BufferedImage(1280,720,2);Graphics2D pg=prancha.createGraphics();
  for(int setor=1;setor<=8;setor++){
   press(f,KeyEvent.VK_F8);press(f,KeyEvent.VK_0+setor);press(f,KeyEvent.VK_ENTER);
   ok((boolean)get(f,"partidaTeste"),"teste ativo");ok(f.getChefesDerrotadosTeste()==setor-1,"seleciona setor "+setor);ok(f.getNave().getNivel()==8,"equipado");
   FundoEspacial fundo=(FundoEspacial)get(f,"fundoEspacial");for(int j=0;j<30;j++)fundo.atualizar(.04);
   BufferedImage b=new BufferedImage(960,600,2);Graphics2D g=b.createGraphics();fundo.desenhar(g);g.dispose();fundos.add(TesteInvasao093.hash(b));
   int px=(setor-1)%4*320,py=(setor-1)/4*360;pg.drawImage(b,px,py,px+320,py+320,0,72,960,600,null);pg.setColor(Color.WHITE);pg.setFont(new Font("Monospaced",Font.BOLD,18));pg.drawString("SETOR "+setor,px+16,py+345);
  }
  pg.dispose();ok(fundos.size()==8,"oito cenarios distintos");ImageIO.write(prancha,"png",new File("refino/Universos_No_Jogo.png"));
  press(f,KeyEvent.VK_F8);press(f,KeyEvent.VK_3);press(f,KeyEvent.VK_S);ok(f.getMiniChefeTeste()!=null,"atalho sentinela");
  for(int i=0;i<240;i++)f.getMiniChefeTeste().atualizar(1.0/60,300);
  FundoEspacial fundo=(FundoEspacial)get(f,"fundoEspacial");for(int j=0;j<30;j++)fundo.atualizar(.04);render(f,"Sentinela_Refinada");
  press(f,KeyEvent.VK_F8);press(f,KeyEvent.VK_B);ok(f.getChefeTeste()!=null&&f.getEtapaTeste().equals("BATALHA_CHEFE"),"atalho boss");
  int medalhas=ProgressoJogo.totalTrofeus();Method trofeu=Fase.class.getDeclaredMethod("receberTrofeu",int.class);trofeu.setAccessible(true);trofeu.invoke(f,8);ok(ProgressoJogo.totalTrofeus()==medalhas,"teste nao grava conquistas");
  int recordes=new Placar().ler().size();TesteInvasao093.set(f,"emJogo",false);TesteInvasao093.set(f,"iniciais","TST");press(f,KeyEvent.VK_ENTER);ok(new Placar().ler().size()==recordes,"teste nao grava placar");
  press(f,KeyEvent.VK_F8);press(f,KeyEvent.VK_5);press(f,KeyEvent.VK_D);ok(f.getEtapaTeste().equals("DOBRA"),"atalho dobra");TesteRevisao094.tick(f,4.3);ok(f.getEtapaTeste().equals("COMBATE")&&f.getChefesDerrotadosTeste()==4,"dobra chega selecionado");
  press(f,KeyEvent.VK_F8);press(f,KeyEvent.VK_N);ok(!(boolean)get(f,"partidaTeste")&&f.getChefesDerrotadosTeste()==0&&f.getNave().getNivel()==0,"nova campanha limpa teste");
  System.out.println("OK Refino: "+checks+" verificacoes. Maior salto Y Sentinela: "+maxSalto);System.exit(0);
 }
}
