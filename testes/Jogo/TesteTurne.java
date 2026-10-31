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
  // campo de meteoros: planeta com anel tem mais meteoros e minerio raro
  Fase c=TesteInvasao093.fase(56);TesteInvasao093.set(c,"partidaTeste",true);TesteInvasao093.set(c,"setorAtual",4);TesteInvasao093.set(c,"planetaAtual",3);
  ok(Turne.anel(4,3),"planeta do James tem anel");TesteInvasao093.set(c,"esperaProximoEsquadrao",999.0);c.getAliens().clear();
  java.util.List<?> ast=(java.util.List<?>)TesteInvasao093.get(c,"asteroides");int maxAst=0,minerio=0;java.util.Set<Object> vistos=new java.util.HashSet<>();
  for(int i=0;i<60*40;i++){c.getNave().tornarIntangivel(1);c.getNave().teleportar(20,90);c.atualizarSimulacao(1/60.0);maxAst=Math.max(maxAst,ast.size());for(Object o:ast)if(((Asteroide)o).temMinerio()&&vistos.add(o))minerio++;}
  ok(maxAst>6,"campo de meteoros passa de 6 meteoros na tela ("+maxAst+")");ok(minerio>0,"aparece meteoro com minerio ("+minerio+")");
  Asteroide m=new Asteroide(500,300,24,Asteroide.MINERIO);int tiros=0;while(!m.receberTiro())tiros++;ok(tiros>=2,"meteoro de minerio e mais duro");
  Asteroide gig=new Asteroide(500,300,70,1);ok(gig.gigante()&&gig.danoColisao()==3,"meteoro gigante bate forte");int tg=0;while(!gig.receberTiro())tg++;ok(tg>=8,"gigante aguenta muito tiro ("+(tg+1)+")");
  boolean viuGigante=false;for(int i=0;i<60*60&&!viuGigante;i++){c.getNave().tornarIntangivel(1);c.getNave().teleportar(20,90);c.atualizarSimulacao(1/60.0);for(Object o:ast)if(((Asteroide)o).gigante())viuGigante=true;}ok(viuGigante,"campo traz meteoro gigante");
  Contrato g=new Contrato(Contrato.Tipo.MINERACAO,2,1,1,-1,"GARIMPO");g.minerio();ok(!g.cumprido,"falta minerio");g.minerio();ok(g.cumprido,"garimpo cumprido");
  ok(Contrato.ofertas(4,4,false,new java.util.Random(1),true).stream().anyMatch(x->x.tipo==Contrato.Tipo.MINERACAO),"quadro oferece garimpo com campo a frente");
  System.out.println("OK turne");}}
