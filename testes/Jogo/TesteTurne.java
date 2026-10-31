package Jogo;
import java.lang.reflect.Method;
/** Turne ao contrario: planetas por banda, integrante por planeta, risco e chefe no ultimo planeta. */
public class TesteTurne{static void ok(boolean b,String m){if(!b)throw new AssertionError(m);System.out.println("ok "+m);}
 static Object call(Object o,String n)throws Exception{Method m=o.getClass().getDeclaredMethod(n);m.setAccessible(true);return m.invoke(o);}
 public static void main(String[] a)throws Exception{
  int[] esperado={3,5,6,3,4,4,4,5};for(int s=0;s<8;s++)ok(Turne.planetas(s)==esperado[s],"setor "+(s+1)+" tem "+esperado[s]+" planetas");
  ok("KIRK HAMMETT".equals(Turne.integrante(4,0))&&"ROBERT TRUJILLO".equals(Turne.integrante(4,2)),"Metallica: Kirk primeiro, Trujillo por ultimo");
  for(int s=0;s<8;s++){int ant=-1;for(int p=0;p<Turne.planetas(s);p++){ok(Turne.risco(s,p)>=ant,"risco nao cai no setor "+(s+1)+" planeta "+(p+1));ant=Turne.risco(s,p);}
   ok(Turne.risco(s,Turne.planetas(s)-1)>=Turne.risco(s,0),"planeta do chefe e o mais perigoso");}
  ok(Turne.anel(4,3)&&!Turne.anel(0,0),"risco alto ganha anel");
  // na Fase: Metallica (setor 5)
  Fase f=TesteInvasao093.fase(55);TesteInvasao093.set(f,"partidaTeste",true);TesteInvasao093.set(f,"setorAtual",4);TesteInvasao093.set(f,"chefesDerrotados",4);
  TesteInvasao093.set(f,"lancadosNoSetor",(int)call(f,"quantidadeSetor"));TesteInvasao093.set(f,"eliminados",9999);f.getAliens().clear();
  for(int p=0;p<3;p++){
   call(f,"verificarProgressao");ok(f.getEtapaTeste().equals("COMBATE"),"sem chefe antes de vencer "+Turne.integrante(4,p));
   MiniChefe m=new MiniChefe(5);m.definirIntegrante(Turne.integrante(4,p),Turne.risco(4,p));ok(m.nome().equals(Turne.integrante(4,p)),"sentinela leva o nome do integrante");
   TesteInvasao093.set(f,"miniChefe",m);m.receberDano(99999);call(f,"derrotarMiniChefe");
   ok((int)TesteInvasao093.get(f,"planetaAtual")==p+1,"vencer "+Turne.integrante(4,p)+" leva ao planeta "+(p+2));}
  TesteInvasao093.set(f,"miniChefe",null);TesteInvasao093.set(f,"base",null);call(f,"verificarProgressao");
  ok(f.getEtapaTeste().equals("ALERTA_CHEFE"),"no ultimo planeta vem o chefe da banda");
  System.out.println("OK turne");}}
