package Jogo;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.prefs.*;
/** Placar local independente do saldo de creditos. */
final class Placar {
 static final class Entrada {
  final String iniciais,modo; final int pontos;
  Entrada(String i,int p,String m){iniciais=i;pontos=p;modo=m;}
 }
 private final Preferences dados;
 Placar(){this(Preferences.userNodeForPackage(Placar.class).node("placar095"));}
 Placar(Preferences p){dados=p;}
 List<Entrada> ler(){
  List<Entrada> lista=new ArrayList<>();
  try{for(int i=0;i<10;i++){String v=dados.get("r"+i,"");String[] s=v.split(";");if(s.length!=3)continue;
   try{int p=Integer.parseInt(s[1]);if(p>0&&s[0].matches("[A-Z0-9]{3}"))lista.add(new Entrada(s[0],p,s[2]));}catch(NumberFormatException ignored){}
  }}catch(SecurityException ignored){}
  lista.sort((a,b)->Integer.compare(b.pontos,a.pontos));return lista;
 }
 boolean salvar(String iniciais,int pontos,String modo){
  if(pontos<=0||!iniciais.matches("[A-Z0-9]{3}"))return false;
  List<Entrada> l=ler();l.add(new Entrada(iniciais,pontos,modo));l.sort((a,b)->Integer.compare(b.pontos,a.pontos));
  try{for(int i=0;i<Math.min(10,l.size());i++){Entrada e=l.get(i);dados.put("r"+i,e.iniciais+";"+e.pontos+";"+e.modo);}dados.flush();return true;}
  catch(SecurityException|BackingStoreException ex){return false;}
 }
 static void desenhar(Graphics2D g,List<Entrada> lista,String rodape){
  g.setColor(new Color(2,5,14,245));g.fillRect(130,110,700,450);
  g.setColor(new Color(211,51,39));g.drawRect(130,110,700,450);
  g.setFont(new Font("Monospaced",Font.BOLD,27));g.setColor(Color.WHITE);g.drawString("LENDAS DO METAL — TOP 10",190,155);
  g.setFont(new Font("Monospaced",Font.BOLD,16));g.setColor(Color.CYAN);g.drawString("POS  PILOTO       PONTOS",190,190);
  for(int i=0;i<lista.size();i++){Entrada e=lista.get(i);g.setColor(i==0?new Color(255,192,100):Color.WHITE);g.drawString(String.format(Locale.ROOT,"%02d   %s       %8d",i+1,e.iniciais,e.pontos),190,222+i*26);}
  if(lista.isEmpty()){g.setColor(Color.LIGHT_GRAY);g.drawString("SEJA O PRIMEIRO A DEIXAR SUAS INICIAIS",190,245);}
  g.setColor(Color.CYAN);g.setFont(new Font("Monospaced",Font.BOLD,14));g.drawString(rodape,158,535);
 }
}
