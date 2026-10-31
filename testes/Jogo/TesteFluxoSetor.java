package Jogo;
import java.util.*;
/** O setor nao para: esquadroes atravessam a tela, os estacionados vao embora e quem escapa conta para o avanco. */
public class TesteFluxoSetor{static void ok(boolean b,String m){if(!b)throw new AssertionError(m);System.out.println("ok "+m);}
 public static void main(String[] a)throws Exception{
  Fase f=TesteInvasao093.fase(40);f.getNave().teleportar(60,300);
  Set<Alien> passaram=new HashSet<>();Map<Alien,Double> parado=new HashMap<>();double maisParado=0;int antes=(int)TesteInvasao093.get(f,"eliminados");
  for(int k=0;k<60*50;k++){f.getNave().tornarIntangivel(1);f.atualizarSimulacao(1/60.0);
   for(Alien x:f.getAliens()){if(!x.isVisible())continue;if(x.emPassagem())passaram.add(x);
    if(x.estaNaFormacao()&&!x.emPassagem()&&x.getClasse()!=Alien.Classe.KAMIKAZE){double t=parado.getOrDefault(x,0.0)+1/60.0;parado.put(x,t);maisParado=Math.max(maisParado,t);}}}
  ok(passaram.size()>=5,"esquadroes atravessam a tela ("+passaram.size()+" passaram)");
  ok(maisParado<20,"ninguem fica estacionado para sempre (maximo "+(int)maisParado+" s)");
  int depois=(int)TesteInvasao093.get(f,"eliminados");ok(depois>antes,"sem atirar, quem escapa ainda faz o setor andar ("+antes+" -> "+depois+")");
  System.out.println("OK fluxo do setor");}}
