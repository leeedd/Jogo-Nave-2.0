package Jogo;
import java.lang.reflect.Method;
/** Evolucao comprada no mercado define o estagio; o item do setor so enche ate o topo dele e se perde com dano. */
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
  Nave n=f.getNave();ok(n.coletarMelhoria()&&n.coletarMelhoria()&&n.getNivel()==5,"item do setor enche ate o topo do estagio II");
  ok(!n.coletarMelhoria()&&n.getNivel()==5&&ArteNaves.estagio(n.getNivel())==1,"item nao passa para o estagio III");
  for(int i=0;i<4;i++){TesteInvasao093.set(n,"invulneravel",0.0);dano.invoke(f,1,null);}
  ok(n.getNivel()==3,"dano tira o extra do setor mas nunca o nivel comprado");
  for(int i=0;i<5;i++)p.comprarMercado(m,0);ok(p.peca(m,0)==8&&p.precoMercado(m,0)==0&&!p.comprarMercado(m,0),"para no nivel 8");
  ProgressaoNave q=new ProgressaoNave((java.util.prefs.Preferences)null);TesteInvasao093.set(q,"saldo",20000);
  ok(q.quantidadeTanques()==0&&q.preco(5)==1500,"primeiro subtanque custa 1500");
  for(int i=0;i<4;i++)ok(q.comprar(5)&&q.temTanque(i)&&q.carga(i)==100,"subtanque "+(i+1)+" chega cheio");
  ok(q.quantidadeTanques()==4&&q.preco(5)==0&&!q.comprar(5)&&q.saldo()==20000-1500-2500-4000-6000,"para em 4 subtanques");
  System.out.println("OK evolucao no mercado");}}
