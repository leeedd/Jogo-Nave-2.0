package Jogo;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
public final class TesteAtlas097 {
 static int checks;
 static void ok(boolean v,String s){if(!v)throw new AssertionError(s);checks++;}
 static Object get(Object o,String n)throws Exception{return TesteInvasao093.get(o,n);}
 static void render(MapaEstelar m,String nome)throws Exception{
  BufferedImage b=new BufferedImage(960,600,2);Graphics2D g=b.createGraphics();m.desenhar(g);g.dispose();ImageIO.write(b,"png",new File("mapa/"+nome+".png"));
 }
 static void estabilizar(MapaEstelar m){for(int i=0;i<120;i++)m.atualizar(1.0/60);}
 public static void main(String[] args)throws Exception{
  MapaEstelar m=new MapaEstelar();m.estado(2,false,false,0);render(m,"01_Galaxia");
  m.roda(369,302,8);estabilizar(m);ok((double)get(m,"zoom")<.085,"escala galactica distante");render(m,"02_Grupo_Galactico");
  m.localizar();estabilizar(m);ok(Math.abs((double)get(m,"cameraX")-MapaEstelar.SISTEMAS[2][0])<.01,"localiza nave");
  m.tecla(KeyEvent.VK_Z);estabilizar(m);render(m,"03_Sistema");ok((double)get(m,"zoom")>6.9,"escala sistema");
  double antesX=(double)get(m,"cameraX");m.pressionar(300,300);m.arrastar(370,300);m.soltar();ok((double)get(m,"cameraX")<antesX,"arraste");
  m.localizar();estabilizar(m);m.tecla(KeyEvent.VK_Z);estabilizar(m);
  // Posicao do primeiro planeta do sistema 2 no espaco do mapa.
  double ang=.9+2*.73,px=MapaEstelar.SISTEMAS[2][0]+Math.cos(ang)*5,py=MapaEstelar.SISTEMAS[2][1]+Math.sin(ang)*5*.78;
  int sx=(int)Math.round(369+(px-(double)get(m,"cameraX"))*(double)get(m,"zoom"));
  int sy=(int)Math.round(302+(py-(double)get(m,"cameraY"))*(double)get(m,"zoom"));
  m.clicar(sx,sy,1); // Descarta o clique residual do arraste anterior.
  m.clicar(sx,sy,2);estabilizar(m);ok((int)get(m,"planeta")==0,"seleciona planeta");render(m,"04_Planeta");
  m.roda(369,302,-1000);estabilizar(m);ok((double)get(m,"zoom")<=160.00001,"limite zoom maximo");
  m.roda(369,302,1000);estabilizar(m);ok((double)get(m,"zoom")>=.025,"limite zoom minimo");
  m.redefinir();double zoom=(double)get(m,"alvoZoom");m.roda(800,300,-2);ok((double)get(m,"alvoZoom")==zoom,"roda no painel nao afeta camera");
  Fase f=TesteInvasao093.fase(97);TesteRefino096.press(f,KeyEvent.VK_ENTER);TesteRefino096.press(f,KeyEvent.VK_4);
  ok((boolean)get(f,"pausado")&&(int)get(f,"abaPausa")==3,"mapa via pausa");
  MapaEstelar integrado=(MapaEstelar)get(f,"mapaEstelar");
  MouseWheelEvent ev=new MouseWheelEvent(f,MouseEvent.MOUSE_WHEEL,0,0,369,302,0,false,MouseWheelEvent.WHEEL_UNIT_SCROLL,1,-3);
  for(MouseWheelListener l:f.getMouseWheelListeners())l.mouseWheelMoved(ev);
  TesteRevisao094.tick(f,1);ok((double)get(integrado,"zoom")>.3,"camera anima enquanto jogo pausado");
  double tempo=(double)get(f,"tempoTotal");TesteRevisao094.tick(f,1);ok((double)get(f,"tempoTotal")==tempo,"mapa nao avanca combate");
  CenaInterdicao c=new CenaInterdicao(new Nave());double inicio=c.naveX();c.atualizar(2);ok(c.naveX()>inicio+80,"captura puxa nave");
  c.atualizar(1.5);ok(c.naveX()>600,"arrasto ate ruptura");
  c.atualizar(3);ok(c.terminou()&&c.naveX()==55&&c.naveY()==300,"fim na posicao de controle");
  BufferedImage contato=new BufferedImage(1440,900,2);Graphics2D pg=contato.createGraphics();double[] tempos={.4,1.5,2.8,3.8,4.8,6.2};
  for(int i=0;i<tempos.length;i++){
   CenaInterdicao cena=new CenaInterdicao(new Nave());cena.atualizar(tempos[i]);BufferedImage b=new BufferedImage(960,600,2);Graphics2D g=b.createGraphics();cena.desenhar(g);g.dispose();
   pg.drawImage(b,(i%3)*480,(i/3)*450,480,400,null);pg.setColor(Color.WHITE);pg.drawString(tempos[i]+"s",(i%3)*480+20,(i/3)*450+424);
  }
  pg.dispose();try(java.io.OutputStream out=new java.io.FileOutputStream("mapa/05_Sequencia_Interdicao.png")){ImageIO.write(contato,"png",out);}
  TesteRefino096.press(f,KeyEvent.VK_F8);TesteRefino096.press(f,KeyEvent.VK_I);TesteRevisao094.tick(f,3);
  ok(f.getEtapaTeste().equals("INTERDICAO"),"nova cutscene ativa");
  TesteRefino096.press(f,KeyEvent.VK_ENTER);double t=(double)get(f,"tempoInterdicao");TesteRevisao094.tick(f,2);ok((double)get(f,"tempoInterdicao")==t,"pausa conserva cutscene");TesteRefino096.press(f,KeyEvent.VK_ENTER);
  TesteRevisao094.tick(f,5);ok((boolean)get(f,"intercepcao"),"combate apos ejeção");ok(f.getNave().getX()==55&&f.getNave().getY()==300,"mesma posicao na entrega do controle");
  System.out.println("OK Atlas 097: "+checks+" verificacoes");System.exit(0);
 }
}
