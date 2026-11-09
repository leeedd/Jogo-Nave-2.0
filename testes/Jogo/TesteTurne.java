package Jogo;
import java.lang.reflect.Method;
/** Turne ao contrario: planetas por banda, integrante por planeta, risco e chefe no ultimo planeta. */
public class TesteTurne{static void ok(boolean b,String m){if(!b)throw new AssertionError(m);System.out.println("ok "+m);}
 static Object call(Object o,String n)throws Exception{Method m=o.getClass().getDeclaredMethod(n);m.setAccessible(true);return m.invoke(o);}
 @SuppressWarnings("unchecked") public static void main(String[] a)throws Exception{
  int[] esperado={3,5,6,3,4,4,4,5};for(int s=0;s<8;s++)ok(Turne.planetas(s)==esperado[s],"setor "+(s+1)+" tem "+esperado[s]+" planetas");
  ok("VEX".equals(Turne.integrante(4,0))&&"ORUK".equals(Turne.integrante(4,2)),"Cinzas de Orion: Vex primeiro, Oruk por ultimo");
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
  // campo de meteoros: planeta com anel tem mais meteoros e minerio raro
  Fase c=TesteInvasao093.fase(56);TesteInvasao093.set(c,"partidaTeste",true);TesteInvasao093.set(c,"setorAtual",4);TesteInvasao093.set(c,"planetaAtual",3);
  ok(Turne.anel(4,3),"planeta do James tem anel");TesteInvasao093.set(c,"esperaProximoEsquadrao",999.0);c.getAliens().clear();
  java.util.List<?> ast=(java.util.List<?>)TesteInvasao093.get(c,"asteroides");int maxAst=0,minerio=0;java.util.Set<Object> vistos=new java.util.HashSet<>();
  for(int i=0;i<60*40;i++){c.getNave().tornarIntangivel(1);c.getNave().teleportar(20,90);c.atualizarSimulacao(1/60.0);maxAst=Math.max(maxAst,ast.size());for(Object o:ast)if(((Asteroide)o).temMinerio()&&vistos.add(o))minerio++;}
  ok(maxAst>6,"campo de meteoros passa de 6 meteoros na tela ("+maxAst+")");ok(minerio>0,"aparece meteoro com minerio ("+minerio+")");
  Asteroide m=new Asteroide(500,300,24,Asteroide.MINERIO);int tiros=0;while(!m.receberTiro())tiros++;ok(tiros>=2,"meteoro de minerio e mais duro");
  Asteroide gig=new Asteroide(500,300,70,1);ok(gig.gigante()&&gig.danoColisao()==3,"meteoro gigante bate forte");int tg=0;while(!gig.receberTiro())tg++;ok(tg>=8,"gigante aguenta muito tiro ("+(tg+1)+")");
  boolean viuGigante=false;for(int i=0;i<60*60&&!viuGigante;i++){c.getNave().tornarIntangivel(1);c.getNave().teleportar(20,90);c.atualizarSimulacao(1/60.0);for(Object o:ast)if(((Asteroide)o).gigante())viuGigante=true;}ok(viuGigante,"campo traz meteoro gigante");
  // estilhaco ricocheteia: atinge o inimigo, quica e nao fere o piloto
  Fase e=TesteInvasao093.fase(58);TesteInvasao093.set(e,"partidaTeste",true);e.getAliens().clear();TesteInvasao093.set(e,"esperaProximoEsquadrao",999.0);
  Alien alvoE=new Alien(600,300,Alien.Tipo.PIRATA,Alien.Classe.TANQUE);TesteInvasao093.set(alvoE,"x",600.0);TesteInvasao093.set(alvoE,"y",300.0);TesteInvasao093.set(alvoE,"esperaEntrada",0.0);e.getAliens().add(alvoE);int r0=alvoE.getResistencia();
  Asteroide est=new Asteroide(620,325,12,1).lancar(300,0);((java.util.List<Asteroide>)TesteInvasao093.get(e,"asteroides")).add(est);
e.getNave().teleportar(100,480);Asteroide perto=new Asteroide(120,495,12,1).lancar(300,0);((java.util.List<Asteroide>)TesteInvasao093.get(e,"asteroides")).add(perto);int vida0=e.getNave().getVida();e.checarColisoes();
  ok(alvoE.getResistencia()<r0||!alvoE.isVisible(),"estilhaco fere o inimigo");ok(e.getNave().getVida()==vida0,"estilhaco nao fere o piloto");
  Asteroide q=new Asteroide(500,100,10,1).lancar(200,-300);for(int i=0;i<30;i++)q.mexer(1/60.0);ok(q.getY()>=92,"estilhaco ricocheteia no teto");
  Contrato g=new Contrato(Contrato.Tipo.MINERACAO,2,1,1,-1,"GARIMPO");g.minerio();ok(!g.cumprido,"falta minerio");g.minerio();ok(g.cumprido,"garimpo cumprido");
  ok(Contrato.ofertas(4,4,false,new java.util.Random(1),true).stream().anyMatch(x->x.tipo==Contrato.Tipo.MINERACAO),"quadro oferece garimpo com campo a frente");
  ok(Turne.instrumento("BRAKKA")=='D'&&Turne.instrumento("ORUK")=='B'&&Turne.instrumento("VEX")=='G'&&Turne.instrumento("GORM")=='D',"cada integrante tem o seu instrumento");
  // fortaleza: depois do Hellraiser, cada banda reunida, todas as 8, e so entao o final
  Fase z=TesteInvasao093.fase(80);TesteInvasao093.set(z,"partidaTeste",true);TesteInvasao093.set(z,"chefesDerrotados",8);TesteInvasao093.set(z,"hellraiserVencido",true);
  Method ir=Fase.class.getDeclaredMethod("iniciarReuniao",int.class);ir.setAccessible(true);ir.invoke(z,0);
  for(int b=0;b<8;b++){MiniChefe gr=(MiniChefe)TesteInvasao093.get(z,"miniChefe");ok(gr!=null&&gr.bandaReunida(),"fortaleza "+(b+1)+": banda reunida em campo");
   java.util.List<?> mem=(java.util.List<?>)TesteInvasao093.get(gr,"banda");ok(mem.size()==Turne.integrantes(b)+(b>0?1:0),"banda completa com o vocalista: "+mem.size());
   while(!gr.receberDano(50)){}call(z,"derrotarMiniChefe");TesteInvasao093.set(z,"base",null);
   if(b<7){for(int i=0;i<300&&TesteInvasao093.get(z,"miniChefe")==null;i++)z.atualizarSimulacao(1/60.0);}}
  ok(z.getEtapaTeste().equals("BATALHA_CHEFE")&&z.getChefeTeste()!=null&&z.getChefeTeste().getFase()==Fase.FINAL,"depois da oitava banda vem a luta final");
  java.lang.reflect.Method dc=Fase.class.getDeclaredMethod("derrotarChefao");dc.setAccessible(true);dc.invoke(z);
  for(int i=0;i<60*20&&!z.getEtapaTeste().equals("CREDITOS");i++)z.atualizarSimulacao(1/60.0);
  ok(z.getEtapaTeste().equals("CREDITOS"),"depois do Dio vem os creditos");
  System.out.println("OK turne");}}
