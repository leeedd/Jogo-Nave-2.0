package Jogo;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import java.lang.reflect.*;
import java.util.*;
import javax.imageio.ImageIO;
import javax.swing.SwingUtilities;
public final class TesteAudioAlpha {
 static int checks;static void ok(boolean b,String s){if(!b)throw new AssertionError(s);checks++;}
 static void set(Object o,String n,Object v)throws Exception{Field f=o.getClass().getDeclaredField(n);f.setAccessible(true);f.set(o,v);}
 static Object get(Object o,String n)throws Exception{Field f=o.getClass().getDeclaredField(n);f.setAccessible(true);return f.get(o);}
 static void key(Fase f,int k){for(KeyListener l:f.getKeyListeners()){l.keyPressed(new KeyEvent(f,KeyEvent.KEY_PRESSED,0,0,k,KeyEvent.CHAR_UNDEFINED));l.keyReleased(new KeyEvent(f,KeyEvent.KEY_RELEASED,0,0,k,KeyEvent.CHAR_UNDEFINED));}}
 static long energy(byte[] b,int ch){long sum=0;for(int i=ch*2;i<b.length;i+=4)sum+=Math.abs((short)((b[i]&255)|(b[i+1]<<8)));return sum;}
 @SuppressWarnings("unchecked") public static void main(String[] args)throws Exception{
  Map<SomJogo.Efeito,float[]> a=new EnumMap<SomJogo.Efeito,float[]>(SomJogo.Efeito.class);
  for(SomJogo.Efeito e:SomJogo.Efeito.values()){
   float[] d=SomJogo.carregar(SomJogo.class.getResource("/Audio098/"+e.arquivo+".wav"),2097152);a.put(e,d);
   double rms=0,peak=0;for(float x:d){rms+=x*x;peak=Math.max(peak,Math.abs(x));}
   ok(d.length>3000&&d.length<220500,"duracao "+e);ok(Math.sqrt(rms/d.length)>.015&&peak<.99,"amplitude "+e);
  }
  float[] music=SomJogo.carregar(MusicaJogo.localizar("MenuMetal"),33554432);ok(music.length>=29*44100,"musica completa");ok(MusicaJogo.duracaoSegundos("MenuMetal")==-1,"loop nao muda ondas");
  MotorAudio m=new MotorAudio();byte[] b=new byte[44100*4];
  for(int i=0;i<80;i++)m.emitir(a.get(SomJogo.Efeito.DOBRA),1,0,0,false);ok(m.vozes()==24,"vozes limitadas");
  m.emitir(a.get(SomJogo.Efeito.DANO),1,0,5,false);ok(m.vozes()==24,"prioridade substitui voz");m.renderizar(b,44100,0,1,false,1);
  int peak=0;for(int i=0;i<b.length;i+=2)peak=Math.max(peak,Math.abs((short)((b[i]&255)|(b[i+1]<<8))));ok(peak<=30000&&peak>1000,"sem saturacao");
  m=new MotorAudio();m.emitir(a.get(SomJogo.Efeito.TIRO),1,-.8f,1,false);m.renderizar(b,44100,0,1,false,1);ok(energy(b,0)>2*energy(b,1),"estereo");
  m.pausar(true);ok(!m.emitir(a.get(SomJogo.Efeito.TIRO),1,0,1,false),"pausa bloqueia combate");ok(m.emitir(a.get(SomJogo.Efeito.MENU),1,0,1,true),"UI na pausa");m.renderizar(b,44100,0,1,true,1);ok(energy(b,0)==0,"mute");
  m=new MotorAudio();m.musica(music);m.renderizar(b,44100,.55f,0,false,1);ok(energy(b,0)>0,"barramento musica");m.renderizar(b,44100,0,0,false,1);m.renderizar(b,44100,0,0,false,1);ok(energy(b,0)==0,"volumes zero");
  SomJogo.volumeMusica(150);SomJogo.volumeEfeitos(-10);ok(SomJogo.musica()==100&&SomJogo.efeitos()==0,"clamp volumes");SomJogo.volumeMusica(55);SomJogo.volumeEfeitos(80);
  Nave n=new Nave();SomJogo.resetEventosTeste();n.atira();n.atira();ok(SomJogo.emitidosTeste(SomJogo.Efeito.TIRO)==1,"tiro aceito uma vez");n.ativarEscudo();n.receberDano(1);ok(SomJogo.emitidosTeste(SomJogo.Efeito.ESCUDO_QUEBRA)==1,"escudo");set(n,"invulneravel",0.0);n.receberDano(1);ok(SomJogo.emitidosTeste(SomJogo.Efeito.DANO)==1,"dano");
  final Fase[] fs=new Fase[1];SwingUtilities.invokeAndWait(()->{fs[0]=new Fase(98);fs[0].pararTimerTeste();});Fase f=fs[0];f.setSize(960,600);set(f,"inventario",new ProgressaoNave(null));f.getAliens().clear();f.getNave().curar(100);
  java.util.List<Item> itens=(java.util.List<Item>)get(f,"itens");itens.clear();itens.add(new Item(Item.Tipo.REPARO,f.getNave().getX(),f.getNave().getY(),false));SomJogo.resetEventosTeste();f.checarColisoes();ok(SomJogo.emitidosTeste(SomJogo.Efeito.REPARO)==0,"coleta recusada silenciosa");f.getNave().danoDiretoTeste(1);f.checarColisoes();f.checarColisoes();ok(SomJogo.emitidosTeste(SomJogo.Efeito.REPARO)==1,"coleta aceita uma vez");
  SomJogo.resetEventosTeste();set(f,"geloColetado",true);key(f,KeyEvent.VK_Q);ok(SomJogo.emitidosTeste(SomJogo.Efeito.GELO)==1,"gelo");key(f,KeyEvent.VK_F9);ok(f.pausadoTeste()&&PainelSom.aberto(),"F9 pausa");key(f,KeyEvent.VK_ESCAPE);ok(f.pausadoTeste()&&!PainelSom.aberto(),"fechar audio preserva pausa");key(f,KeyEvent.VK_ENTER);ok(!f.pausadoTeste(),"retoma");
  SomJogo.resetEventosTeste();new CinematicaNave(n,false);ok(SomJogo.emitidosTeste(SomJogo.Efeito.DOBRA)==1,"dobra");CenaInterdicao c=new CenaInterdicao(n);c.atualizar(4);ok(SomJogo.emitidosTeste(SomJogo.Efeito.INTERDICAO)==1&&SomJogo.emitidosTeste(SomJogo.Efeito.SAIDA_DOBRA)==1,"saida forcada");
  SomJogo.resetEventosTeste();CenaDerrotaChefe d=new CenaDerrotaChefe(new Chefe(8,Dificuldade.VETERANO));ok(SomJogo.emitidosTeste(SomJogo.Efeito.EXPLOSAO_GRANDE)==1,"boss explosao inicial");SomJogo.resetEventosTeste();d.atualizar(2.3);ok(SomJogo.emitidosTeste(SomJogo.Efeito.EXPLOSAO_GRANDE)==1,"boss explosao final");
  PainelSom.tecla(KeyEvent.VK_F9);BufferedImage im=new BufferedImage(960,600,BufferedImage.TYPE_INT_RGB);Graphics2D g=im.createGraphics();PainelSom.desenhar(g);g.dispose();try(OutputStream out=new FileOutputStream("alpha/qa/audio.png")){ImageIO.write(im,"png",out);}
  System.out.println("OK AUDIO ALPHA: "+checks+" verificacoes");System.exit(0);
 }
}
