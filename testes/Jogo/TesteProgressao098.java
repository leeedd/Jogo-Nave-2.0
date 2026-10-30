package Jogo;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.prefs.Preferences;
import javax.imageio.ImageIO;
import javax.swing.SwingUtilities;
public final class TesteProgressao098 {
 static int testes;
 static void ok(boolean b,String msg){if(!b)throw new AssertionError(msg);testes++;}
 static Object get(Object o,String nome)throws Exception{Field f=o.getClass().getDeclaredField(nome);f.setAccessible(true);return f.get(o);}
 static void set(Object o,String nome,Object v)throws Exception{Field f=o.getClass().getDeclaredField(nome);f.setAccessible(true);f.set(o,v);}
 static Object call(Object o,String nome,Class<?>[] types,Object...args)throws Exception{Method m=o.getClass().getDeclaredMethod(nome,types);m.setAccessible(true);return m.invoke(o,args);}
 static void key(Fase f,int k){for(KeyListener l:f.getKeyListeners()){l.keyPressed(new KeyEvent(f,KeyEvent.KEY_PRESSED,0,0,k,KeyEvent.CHAR_UNDEFINED));l.keyReleased(new KeyEvent(f,KeyEvent.KEY_RELEASED,0,0,k,KeyEvent.CHAR_UNDEFINED));}}
 @SuppressWarnings("unchecked") public static void main(String[] args)throws Exception{
  Preferences prefs=Preferences.userRoot().node("teste098");prefs.clear();
  ProgressaoNave p=new ProgressaoNave(prefs);
  for(int i=0;i<4;i++)ok(p.descobrirTanque(i),"descoberta unica");
  ok(!p.descobrirTanque(0)&&!p.descobrirTanque(4),"limite de tanques");
  ok(p.abastecerAutomatico(95)==0&&p.carga(0)==95,"primeira reserva");
  p.abastecerAutomatico(10);ok(p.carga(0)==100&&p.carga(1)==5,"transbordo sem desperdicio");
  ok(p.usar(0,20,20)==0&&p.carga(0)==100,"nao gasta com casco cheio");
  ok(p.usar(0,18,20)==2&&p.carga(0)==90,"consumo proporcional");
  ProgressaoNave copia=p.copiaTeste();copia.equiparTeste();
  ok(new ProgressaoNave(prefs).carga(0)==90,"teste isolado");
  p.creditar(1000);ok(!p.comprar(1),"elemental requer retencao");
  ok(p.comprar(0)&&p.saldo()==400,"compra desconta");
  ok(!p.comprar(0)&&p.saldo()==400,"nao recompra");
  p.registrarElemental(true,false);ok(new ProgressaoNave(prefs).gelo()==1,"arma persistida");
  ok(!p.setorLiberado(1),"chave obrigatoria");p.registrarChave(8);ok(p.setorLiberado(1),"chave abre universo");
  Energia098 e=new Energia098();for(int i=0;i<25;i++)ok(e.gastar(0,4),"disparo pago");
  ok(!e.gastar(0,4)&&e.carga(0)==0,"sem energia sem tiro");
  ok(e.recarregar(true,false)==10&&e.carga(0)==10,"recarga dez por cento");
  e.restaurar(0);e.gastar(0,4);e.gastar(1,20);e.selecionar(0);e.recarregar(true,true);
  ok(e.carga(0)==100&&e.carga(1)==86,"prioridade e excedente");
  final Fase[] holder=new Fase[1];SwingUtilities.invokeAndWait(()->{holder[0]=new Fase(98);holder[0].pararTimerTeste();});Fase f=holder[0];
  ProgressaoNave inv=new ProgressaoNave(null);inv.descobrirTanque(0);set(f,"inventario",inv);
  Nave nave=f.getNave();nave.curar(100);nave.danoDiretoTeste(1);
  int vida=nave.getVida();call(f,"recolherReparo",new Class<?>[]{});
  ok(nave.getVida()>vida,"coleta cura casco primeiro");
  int antes=inv.carga(0);call(f,"recolherReparo",new Class<?>[]{});ok(inv.carga(0)==antes+10,"casco cheio carrega automaticamente");
  set(f,"geloColetado",true);key(f,KeyEvent.VK_Q);Energia098 en=(Energia098)get(f,"energia");
  ok(en.carga(0)==96,"tecla paga energia");key(f,KeyEvent.VK_Q);ok(en.carga(0)==96,"recarga impede spam");
  set(f,"recargaElemental",0.0);while(en.gastar(0,4)){};
  List<DisparoElemental> tiros=(List<DisparoElemental>)get(f,"tirosElementais");int quant=tiros.size();key(f,KeyEvent.VK_Q);ok(tiros.size()==quant,"tecla nao burla energia");
  inv.creditar(600);inv.comprar(0);inv.coletarReator();set(f,"fogoColetado",true);en.restaurar(0);en.restaurar(1);
  key(f,KeyEvent.VK_V);ok(en.carga(0)==60&&en.carga(1)==60,"fusao paga duas barras");
  key(f,KeyEvent.VK_V);ok(en.carga(0)==60&&(double)get(f,"recargaFusao")==30,"fusao nao burla recarga");
  set(f,"tempoSetor",19.0);call(f,"atualizarSegredo",new Class<?>[]{double.class},.01);Segredo098 segredo=(Segredo098)get(f,"segredo");
  ok(segredo==null,"nao repete tanque adquirido");
  set(f,"chefesDerrotados",2);call(f,"iniciarRitmoSetor",new Class<?>[]{});set(f,"tempoSetor",19.0);call(f,"atualizarSegredo",new Class<?>[]{double.class},.01);
  segredo=(Segredo098)get(f,"segredo");ok(segredo!=null&&segredo.tanque==1,"segredo do setor correto");
  ok(segredo.atingir(20)&&segredo.liberar().origem()==1,"cofre libera tanque identificado");
  int hp=nave.getVida();MapaEstelar mapa=(MapaEstelar)get(f,"mapaEstelar");set(mapa,"selecionado",1);
  call(f,"viajarMapa",new Class<?>[]{});ok((int)get(f,"chefesDerrotados")==2,"destino bloqueado");
  inv.registrarChave(8);call(f,"viajarMapa",new Class<?>[]{});ok((int)get(f,"chefesDerrotados")==1&&nave.getVida()==hp,"viagem autorizada sem cura gratis");
  set(f,"trofeuPendente",8);((boolean[])get(f,"trofeusAdquiridos"))[8]=true;
  call(f,"receberTrofeu",new Class<?>[]{int.class},8);ok((int)get(f,"trofeuPendente")==0,"revisita nao trava recompensa");
  BufferedImage b=new BufferedImage(960,600,BufferedImage.TYPE_INT_RGB);Graphics2D g=b.createGraphics();Painel098.desenhar(g,p,1);g.dispose();
  try(FileOutputStream out=new FileOutputStream(args[0])){ImageIO.write(b,"png",out);}
  prefs.removeNode();System.out.println("OK: "+testes+" verificacoes de progressao, combate e persistencia.");System.exit(0);
 }
}
