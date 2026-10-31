package Jogo;
import java.awt.Rectangle;import java.util.*;
public class TesteGancho{static void ok(boolean b,String m){if(!b)throw new AssertionError(m);System.out.println("ok "+m);}
 public static void main(String[] a){List<TiroChefe> t=new ArrayList<>();
  Chefe h=new Chefe(9,Dificuldade.VETERANO);int nx=150,ny=300;double[] p=null;boolean empurrou=false;
  for(int i=0;i<60*30&&p==null;i++){h.atualizar(1/60.0,nx,ny,Dificuldade.VETERANO,t);for(TiroChefe x:t){x.mexer(1/60.0);if(x.empurra())empurrou=true;}t.removeIf(x->!x.isVisible());p=h.puxaoNave();}
  ok(empurrou,"paredao solta ondas que empurram");ok(p!=null,"gancho agarra a nave parada");ok(p[0]>nx,"puxa para a van");
  int v=h.getVida();int tiros=0;
  while(h.puxaoNave()!=null&&tiros<40){double mx=(nx+22+p[0])/2,my=(ny+18+p[1])/2;Rectangle r=new Rectangle((int)mx-4,(int)my-2,8,4);if(h.colide(r)){h.receberDano(3);tiros++;}h.atualizar(1/60.0,nx,ny,Dificuldade.VETERANO,t);}
  ok(h.puxaoNave()==null&&tiros<=12,"atirar na corrente solta ("+tiros+" tiros)");ok(h.getVida()==v,"tiro na corrente nao fere a van");
  System.out.println("OK gancho");}}
