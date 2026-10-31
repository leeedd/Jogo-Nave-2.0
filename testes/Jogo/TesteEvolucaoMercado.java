package Jogo;
import java.lang.reflect.Method;
/** Evolucao comprada no mercado: sobe o nivel e o estagio da nave e nao se perde com dano. */
public class TesteEvolucaoMercado{static void ok(boolean b,String m){if(!b)throw new AssertionError(m);System.out.println("ok "+m);}
 public static void main(String[] a)throws Exception{
  Fase f=TesteInvasao093.fase(7);ProgressaoNave p=new ProgressaoNave((java.util.prefs.Preferences)null);p.equiparTeste();TesteInvasao093.set(p,"saldo",50000);TesteInvasao093.set(f,"inventario",p);
  int m=f.getNave().getModeloVisual(),saldo=p.saldo();
  for(int i=0;i<3;i++)ok(p.comprarMercado(m,0),"compra nivel "+(i+1));
  ok(p.saldo()==saldo-400-700-1600,"cobra o preco da tabela");
  Method ap=Fase.class.getDeclaredMethod("aplicarInventario");ap.setAccessible(true);ap.invoke(f);
  ok(f.getNave().getNivel()==3&&ArteNaves.estagio(f.getNave().getNivel())==1,"nivel 3 vira estagio II");
  Method dano=Fase.class.getDeclaredMethod("processarDano",int.class,Alien.class);dano.setAccessible(true);
  TesteInvasao093.set(f,"fogoColetado",false);TesteInvasao093.set(f,"geloColetado",false);
  dano.invoke(f,1,null);ok(f.getNave().getNivel()==3,"levar dano nao tira o nivel comprado");
  for(int i=0;i<5;i++)p.comprarMercado(m,0);ok(p.peca(m,0)==8&&p.precoMercado(m,0)==0&&!p.comprarMercado(m,0),"para no nivel 8");
  System.out.println("OK evolucao no mercado");}}
