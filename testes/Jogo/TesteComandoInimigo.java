package Jogo;
import java.util.Random;
/** Comando inimigo: aprende com o piloto e adapta os esquadroes. */
public class TesteComandoInimigo{static void ok(boolean b,String m){if(!b)throw new AssertionError(m);System.out.println("ok "+m);}
 public static void main(String[] a){Random r=new Random(3);ComandoInimigo c=new ComandoInimigo();
  double[] m=c.mira(100,300,800,300,r);ok(c.nivel()==0&&m[0]==100&&m[1]==300,"nivel 0 mira reto");
  c.atualizarNivel(5,6,true,2,1);ok(c.nivel()>=3,"chefes e nave forte sobem a ameaca ("+c.nivel()+")");
  c.atualizarNivel(5,6,true,2,.2);int piedade=c.nivel();c.atualizarNivel(5,6,true,2,1);ok(piedade==c.nivel()-1,"casco no fim alivia um nivel");
  c.atualizarNivel(20,8,true,3,1);ok(c.nivel()==5,"teto de ameaca e 5");
  double y=200;for(int i=0;i<60;i++){y+=4;c.observar(1/60.0,100,y);}
  m=c.mira(120,y,800,300,r);ok(m[1]>y+40,"mira onde a nave vai estar (" +(int)(m[1]-y)+" px a frente)");
  ok(c.radio()!=null,"radio pirata solta a pista da mira");
  for(int i=0;i<60*20;i++)c.observar(1/60.0,100,520);
  double f=c.faixaFavorita();ok(!Double.isNaN(f)&&f>430,"descobre que o piloto vive embaixo");
  ok(c.pedirKamikaze(),"piloto parado chama kamikaze");ok(!c.pedirKamikaze(),"kamikaze nao repete na hora");
  for(int i=0;i<200;i++)c.registrarAcerto(2);int fogo=0;for(int i=0;i<200;i++)if((c.adaptacoes(r)&ComandoInimigo.CASCO_FOGO)!=0)fogo++;
  ok(fogo>60,"quem so usa fogo enfrenta casco contra fogo ("+fogo+"/200)");
  Alien al=new Alien(500,300,Alien.Tipo.PIRATA,Alien.Classe.TANQUE);al.adaptar(ComandoInimigo.CASCO_FOGO);
  ok(al.danoElemental(true,6)==3&&al.danoElemental(false,6)==6,"casco contra fogo corta o fogo pela metade e nao o gelo");
  ComandoInimigo nov=new ComandoInimigo();ok((nov.adaptacoes(r)&ComandoInimigo.CASCO_FOGO)==0,"sem historico nao adapta");
  ComandoInimigo cp=c.copia();ok(cp.nivel()==c.nivel()&&!Double.isNaN(cp.faixaFavorita()),"retomada guarda o que ele aprendeu");
  System.out.println("OK comando inimigo");}}
