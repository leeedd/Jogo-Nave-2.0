package Jogo;
import java.awt.*;import java.awt.event.*;import java.awt.image.BufferedImage;import java.lang.reflect.*;import java.util.*;
public final class TesteRevisao094 {
 static int checks;static void ok(boolean b,String m){if(!b)throw new AssertionError(m);checks++;}
 static void call(Object o,String n)throws Exception{Method m=o.getClass().getDeclaredMethod(n);m.setAccessible(true);m.invoke(o);}
 static void tick(Fase f,double s){for(int i=0;i<(int)(s*60);i++)f.atualizarSimulacao(1.0/60);}
 static void key(Fase f,int k,boolean p){KeyEvent e=new KeyEvent(f,p?KeyEvent.KEY_PRESSED:KeyEvent.KEY_RELEASED,0,0,k,KeyEvent.CHAR_UNDEFINED);for(KeyListener l:f.getKeyListeners())if(p)l.keyPressed(e);else l.keyReleased(e);}
 public static void main(String[] args)throws Exception{
 TesteInvasao093.main(args);
 for(int id=1;id<=8;id++){
  Fase f=TesteInvasao093.fase(id);f.getAliens().clear();f.iniciarChefeTeste(id);if(id==1)TesteInvasao093.set(f,"chefesDerrotados",7);call(f,"derrotarChefao");ok(f.getEtapaTeste().equals("DERROTA_CHEFE"),"cutscene "+id);
  tick(f,4.3);ok(f.getEtapaTeste().equals("INTERLUDIO"),"espera item "+id);tick(f,12);ok(f.getEtapaTeste().equals("INTERLUDIO"),"sem coleta automatica "+id);
  Rectangle r=f.getItens().get(0).getBounds();TesteInvasao093.set(f.getNave(),"x",(double)r.x);TesteInvasao093.set(f.getNave(),"y",(double)r.y);f.checarColisoes();ok(f.temTrofeuTeste(id),"coleta real "+id);tick(f,1);ok(f.getEtapaTeste().equals(id==1?"CREDITOS":"DOBRA"),"transicao "+id);
  if(id!=1){int hp=f.getNave().getVida();tick(f,4.3);ok(f.getEtapaTeste().equals("COMBATE"),"proximo setor "+id);ok(f.getNave().getVida()==hp,"sem reparo na dobra "+id);}
 }
 for(int arma=0;arma<3;arma++){
  Fase f=TesteInvasao093.fase(94);f.getAliens().clear();TesteInvasao093.set(f,"esperaProximoEsquadrao",100.0);
  int k=arma==0?KeyEvent.VK_SPACE:arma==1?KeyEvent.VK_Q:KeyEvent.VK_E;
  if(arma>0)f.darArmaElementalTeste(arma==1?DisparoElemental.Tipo.GELO:DisparoElemental.Tipo.FOGO);
  key(f,k,true);tick(f,1.3);java.util.List<?> es=arma==0?f.getNave().getMisseis():(java.util.List<?>)TesteInvasao093.get(f,"tirosElementais");ok(es.size()>=2,"segurar arma "+arma);key(f,k,false);int n=es.size();tick(f,.2);ok(es.size()<=n,"soltar arma "+arma);
 }
 Alien a=new Alien(600,300,Alien.Tipo.PIRATA,Alien.Classe.TANQUE);a.capturar(Item.Tipo.ARMA_2);ok(a.estiloDisparo()==3,"dispersao tem prioridade");a.capturar(Item.Tipo.ARMA_GELO);ok(a.estiloDisparo()==6,"gelo capturado");a.capturar(Item.Tipo.ARMA_FOGO);ok(a.estiloDisparo()==7,"fogo capturado");int hp=a.getResistencia();a.capturar(Item.Tipo.ESCUDO);for(int i=0;i<3;i++)a.receberImpacto(0,2);ok(a.getResistencia()==hp,"escudo absorve tres");a.receberImpacto(0,2);ok(a.getResistencia()==hp-2,"escudo acaba");
 Alien b=new Alien(600,300);hp=b.getResistencia();b.capturar(Item.Tipo.MELHORIA);ok(b.evoluido()&&b.getResistencia()>hp,"modulo evolui");Fase f=TesteInvasao093.fase(5);f.getAliens().add(b);TesteInvasao093.set(f.getNave(),"invulneravel",0.0);f.getNave().ativarEscudo();f.simularDanoTeste(b,1);ok(b.getEquipamento()==Item.Tipo.ESCUDO,"roubo escudo");
 Set<String> pools=new HashSet<>();for(int setor=0;setor<8;setor++){Fase ff=TesteInvasao093.fase(99);TesteInvasao093.set(ff,"chefesDerrotados",setor);ff.inicializaInimigos();Set<String> cl=new TreeSet<>();for(Alien aa:ff.getAliens())cl.add(aa.getClasse().name());pools.add(cl.toString());}ok(pools.size()>=6,"setores diferentes");
 for(int id=1;id<=7;id++)for(int forma=1;forma<=2;forma++){BufferedImage bb=new BufferedImage(360,360,2);Graphics2D g=bb.createGraphics();Sprites16Bits.desenhar(g,id,forma,360,360,0,0);g.dispose();long h=TesteInvasao093.hash(bb);bb=new BufferedImage(360,360,2);g=bb.createGraphics();Sprites16Bits.desenhar(g,id,forma,360,360,0,.5);g.dispose();ok(h!=TesteInvasao093.hash(bb),"pose ataque "+id+"/"+forma);}
 System.out.println("OK Revisao 0.9.4: "+checks+" verificacoes adicionais.");System.exit(0);
 }
}
