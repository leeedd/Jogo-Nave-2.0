package Jogo;
import java.lang.reflect.Method;import java.util.*;
/** Cacador de recompensas: quadro da base, contratos, pagamento e patente. */
public class TesteContratos{static void ok(boolean b,String m){if(!b)throw new AssertionError(m);System.out.println("ok "+m);}
 static Object call(Object o,String n)throws Exception{Method m=o.getClass().getDeclaredMethod(n);m.setAccessible(true);return m.invoke(o);}
 public static void main(String[] a)throws Exception{
  ok(Contrato.patente(0)==0&&Contrato.patente(299)==0&&Contrato.patente(300)==1&&Contrato.patente(99999)==5,"patentes por XP");
  List<Contrato> of=Contrato.ofertas(2,2,false,new Random(1));ok(of.size()>=3,"quadro oferece pelo menos tres contratos");
  Contrato c=new Contrato(Contrato.Tipo.ABATES,3,500,80,-1,"T");c.abate();c.abate();ok(!c.cumprido,"ainda falta abate");c.abate();ok(c.cumprido,"abates cumprem");
  Contrato g=new Contrato(Contrato.Tipo.ABATES,50,1,1,-1,"G");g.golpe();g.golpe();ok(!g.falhou,"dois golpes ainda valem");g.golpe();ok(g.falhou,"terceiro golpe perde o contrato");
  Contrato s=new Contrato(Contrato.Tipo.SEM_DANO_CHEFE,0,1,1,-1,"S");s.danoNoChefe();s.chefeDerrotado(10);ok(s.falhou&&!s.cumprido,"levou dano no chefe: falhou");
  Contrato s2=new Contrato(Contrato.Tipo.SEM_DANO_CHEFE,0,1,1,-1,"S");s2.chefeDerrotado(10);ok(s2.cumprido,"chefe sem dano: cumprido");
  Contrato r=new Contrato(Contrato.Tipo.CHEFE_RAPIDO,60,1,1,-1,"R");r.chefeDerrotado(75);ok(r.falhou,"chefe lento: falhou");
  Contrato e=new Contrato(Contrato.Tipo.ABATES,99,1,1,-1,"E");e.chefeDerrotado(5);ok(e.falhou,"abates nao feitos expiram quando o chefe cai");
  ok(Contrato.daNave(1).nave==1&&Contrato.daNave(4).tipo==Contrato.Tipo.VAPORIZAR,"contratos especiais de nave");
  // na Fase: aceita na base, cumpre em combate e recebe os creditos
  Fase f=TesteInvasao093.fase(12);TesteInvasao093.set(f,"partidaTeste",true);ProgressaoNave p=new ProgressaoNave((java.util.prefs.Preferences)null);TesteInvasao093.set(f,"inventario",p);
  call(f,"atracar");List<?> ofs=(List<?>)TesteInvasao093.get(f,"ofertasContrato");ok(!ofs.isEmpty(),"atracar gera ofertas");
  TesteInvasao093.set(f,"ofertasContrato",new ArrayList<>(Arrays.asList(new Contrato(Contrato.Tipo.ABATES,2,700,90,-1,"LIMPEZA"))));
  call(f,"aceitarContrato");ok(TesteInvasao093.get(f,"contrato")!=null,"contrato aceito");TesteInvasao093.set(f,"naBase",false);
  int saldo=p.saldo();Method el=Fase.class.getDeclaredMethod("eliminarAlien",Alien.class,boolean.class);el.setAccessible(true);
  for(int i=0;i<2;i++){Alien x=new Alien(500,300,Alien.Tipo.PIRATA,Alien.Classe.TANQUE);f.getAliens().add(x);el.invoke(f,x,true);}
  ok(TesteInvasao093.get(f,"contrato")==null&&p.saldo()>=saldo+700,"contrato cumprido paga os creditos");
  System.out.println("OK contratos");}}
