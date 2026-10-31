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
  ok(Contrato.daNave(1).nave==1&&Contrato.daNave(4).tipo==Contrato.Tipo.CACADA,"contrato de nave e uma cacada");
  // na Fase: aceita na base, cumpre em combate e recebe os creditos
  Fase f=TesteInvasao093.fase(12);TesteInvasao093.set(f,"partidaTeste",true);ProgressaoNave p=new ProgressaoNave((java.util.prefs.Preferences)null);TesteInvasao093.set(f,"inventario",p);
  call(f,"atracar");List<?> ofs=(List<?>)TesteInvasao093.get(f,"ofertasContrato");ok(!ofs.isEmpty(),"atracar gera ofertas");
  TesteInvasao093.set(f,"ofertasContrato",new ArrayList<>(Arrays.asList(new Contrato(Contrato.Tipo.ABATES,2,700,90,-1,"LIMPEZA"))));
  call(f,"aceitarContrato");ok(TesteInvasao093.get(f,"contrato")!=null,"contrato aceito");TesteInvasao093.set(f,"naBase",false);
  int saldo=p.saldo();Method el=Fase.class.getDeclaredMethod("eliminarAlien",Alien.class,boolean.class);el.setAccessible(true);
  for(int i=0;i<2;i++){Alien x=new Alien(500,300,Alien.Tipo.PIRATA,Alien.Classe.TANQUE);f.getAliens().add(x);el.invoke(f,x,true);}
  ok(TesteInvasao093.get(f,"contrato")==null&&p.saldo()>=saldo+700,"contrato cumprido paga os creditos");
  // cacada: o alvo procurado aparece, solta a reliquia e o contrato e cumprido ao pegar
  Fase h=TesteInvasao093.fase(13);TesteInvasao093.set(h,"partidaTeste",true);TesteInvasao093.set(h,"inventario",new ProgressaoNave((java.util.prefs.Preferences)null));
  TesteInvasao093.set(h,"contrato",Contrato.daNave(2));h.getAliens().clear();TesteInvasao093.set(h,"lancadosNoSetor",0);call(h,"lancarProximoEsquadrao");
  Alien alvo=null;for(Alien x:h.getAliens())if(x.procurado())alvo=x;ok(alvo!=null&&alvo.reliquia()==2&&alvo.getClasse()!=Alien.Classe.KAMIKAZE,"esquadrao traz o alvo procurado");
  ok(alvo.getResistencia()>=10,"alvo procurado e mais duro ("+alvo.getResistencia()+")");
  call(h,"lancarProximoEsquadrao");int alvos=0;for(Alien x:h.getAliens())if(x.procurado())alvos++;ok(alvos==1,"so um procurado por vez");
  TesteInvasao093.set(alvo,"x",500.0);Method el2=Fase.class.getDeclaredMethod("eliminarAlien",Alien.class,boolean.class);el2.setAccessible(true);el2.invoke(h,alvo,true);
  Item rel=null;for(Item it:h.getItens())if(it.getTipo()==Item.Tipo.RELIQUIA_NAVE)rel=it;ok(rel!=null&&rel.origem()==2,"procurado solta a reliquia da nave");
  java.awt.Rectangle rb=rel.getBounds();h.getNave().teleportar(rb.x,rb.y);h.checarColisoes();ok(TesteInvasao093.get(h,"contrato")==null,"pegar a reliquia cumpre a cacada");
  // contrato que falha some, vai para o arquivo com o motivo, e a base sorteia outros
  Fase q=TesteInvasao093.fase(14);TesteInvasao093.set(q,"partidaTeste",true);TesteInvasao093.set(q,"inventario",new ProgressaoNave((java.util.prefs.Preferences)null));
  Contrato falho=new Contrato(Contrato.Tipo.ABATES,99,1,1,-1,"LIMPEZA");TesteInvasao093.set(q,"contrato",falho);
  Method dano=Fase.class.getDeclaredMethod("processarDano",int.class,Alien.class);dano.setAccessible(true);
  for(int i=0;i<3;i++){TesteInvasao093.set(q.getNave(),"invulneravel",0.0);dano.invoke(q,1,null);}
  java.util.List<?> arq=(java.util.List<?>)TesteInvasao093.get(q,"arquivoContratos");
  ok(TesteInvasao093.get(q,"contrato")==null&&arq.size()==1&&falho.falhou&&falho.motivo.contains("GOLPES"),"terceiro golpe expira o contrato e arquiva com o motivo ("+falho.motivo+")");
  call(q,"atracar");java.util.List<?> novas=(java.util.List<?>)TesteInvasao093.get(q,"ofertasContrato");ok(!novas.contains(falho)&&novas.size()>=3,"base sorteia contratos novos, o falho nao volta");
  Contrato vivo=new Contrato(Contrato.Tipo.SEM_DANO_CHEFE,0,1,1,-1,"INTOCAVEL");TesteInvasao093.set(q,"contrato",vivo);call(q,"reiniciar");
  ok(vivo.falhou&&"NAVE DESTRUIDA".equals(vivo.motivo)&&arq.size()==2,"morrer expira o contrato e ele vai para o arquivo");
  System.out.println("OK contratos");}}
