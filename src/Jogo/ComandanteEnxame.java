package Jogo;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

/** Ciclo deterministico: cacada, aviso, ataque, pane/exaustao e derrota. */
final class ComandanteEnxame {
 enum Estado { CACADA, AVISO, ATAQUE, EXPOSTO, MORRENDO, MORTO }
 final boolean rainha;
 private Estado estado=Estado.CACADA;
 private double x=740,y=314,t,restante=2.5,morte;
 private int vida,max,estagio,sequencia;
 private boolean atingiu;
 private final int[] faixas=new int[3];
 private int numeroFaixas;
 private double desvio,recargaDesvio;
 void observar(java.util.List<Missel> tiros){
  if(!rainha||estado!=Estado.CACADA||recargaDesvio>0)return;
  for(Missel m:tiros)if(m.isVisible()&&m.getX()<x&&m.getX()>x-300&&Math.abs(m.getY()-y)<45){desvio=y<310?90:-90;recargaDesvio=1.5;break;}
 }
 ComandanteEnxame(boolean rainha,int setor){this.rainha=rainha;vida=max=70+setor*10;}
 int vida(){return vida;} int max(){return max;} int estagio(){return estagio;}
 double relogio(){return t;}
 Estado estado(){return estado;} boolean terminou(){return estado==Estado.MORTO;}
 boolean morrendo(){return estado==Estado.MORRENDO||terminou();}
 boolean exposto(){return estado==Estado.EXPOSTO;}
 Rectangle corpo(){return new Rectangle((int)x-72,(int)y-58,144,116);}
 Rectangle nucleo(){return new Rectangle((int)x-44,(int)y-18,32,36);}
 boolean impacto(Rectangle r,int dano){if(exposto()&&nucleo().intersects(r))dano(dano);return terminou();}
 void dano(int valor){
  if(!exposto()||valor<=0)return;
  vida=Math.max(0,vida-valor);
  // Novos padroes comecam no proximo ciclo; nunca cortam a abertura atual.
  if(vida==0){estado=Estado.MORRENDO;morte=0;SomJogo.tocar(SomJogo.Efeito.EXPLOSAO_GRANDE);}
 }
 void atualizar(double dt,int alvo){
  dt=Math.max(0,Math.min(.05,dt));t+=dt;recargaDesvio=Math.max(0,recargaDesvio-dt);desvio*=Math.exp(-dt*1.8);
  if(morrendo()){morte+=dt;if(morte>=2.8)estado=Estado.MORTO;return;}
  if(estado==Estado.CACADA){
   double destino=rainha?310+Math.sin(t*(1.3+estagio*.35))*125:310+Math.sin(t)*85;
   destino=Math.max(160,Math.min(470,destino+desvio));
   y+=Math.max(-dt*(150+estagio*45),Math.min(dt*(150+estagio*45),(destino-y)*dt*3));
  }else if(exposto())y+=Math.sin(t*2)*dt*5;
  restante-=dt;if(restante>0)return;
  switch(estado){
   case CACADA:
    estado=Estado.AVISO;restante=1.1;atingiu=false;
    numeroFaixas=estagio==0?1:estagio==1?2:3;
    int centro=Math.max(175,Math.min(470,alvo+18));
    for(int i=0;i<numeroFaixas;i++){
     int deslocamento=(i-(numeroFaixas-1)/2)*100;
     if(rainha&&estagio==2)deslocamento=(i-1)*125+(sequencia%2==0?30:-30);
     faixas[i]=Math.max(130,Math.min(530,centro+deslocamento));
    }
    SomJogo.tocar(SomJogo.Efeito.ALERTA);break;
   case AVISO:estado=Estado.ATAQUE;restante=.4;SomJogo.tocar(SomJogo.Efeito.LASER);break;
   case ATAQUE:
    sequencia++;
    if(sequencia%(estagio+1)==0){estado=Estado.EXPOSTO;restante=3.1-estagio*.3;SomJogo.tocar(SomJogo.Efeito.IMPACTO);}
    else{estado=Estado.CACADA;restante=.9;}
    break;
   case EXPOSTO:
    estagio=vida*100<=max*30?2:vida*100<=max*65?1:0;
    estado=Estado.CACADA;restante=2.1-estagio*.45;break;
   default:break;
  }
 }
 boolean consumirDano(Rectangle r){
  if(estado!=Estado.ATAQUE||atingiu)return false;
  for(int i=0;i<numeroFaixas;i++)if(new Rectangle(0,faixas[i]-9,(int)x-45,18).intersects(r)){atingiu=true;return true;}
  return false;
 }
 void desenhar(Graphics2D g){
  if(terminou())return;
  if(estado==Estado.AVISO||estado==Estado.ATAQUE){
   for(int i=0;i<numeroFaixas;i++){
    g.setColor(estado==Estado.ATAQUE?new Color(255,125,70):new Color(220,75,62));
    if(estado==Estado.AVISO){for(int a=0;a<x-70;a+=18)g.fillRect(a,faixas[i]-1,9,2);}
    else{g.fillRect(0,faixas[i]-9,(int)x-45,18);g.setColor(new Color(255,242,195));g.fillRect(0,faixas[i]-3,(int)x-45,6);}
   }
  }
  int tremor=morrendo()?(int)(Math.sin(t*70)*7):exposto()&&!rainha?(int)(Math.sin(t*40)*2):0;
  ArteEnxame.comandante(g,(int)x+tremor,(int)y,rainha,t,exposto(),morrendo()?morte:0,estado==Estado.AVISO?1:estado==Estado.ATAQUE?2:0);
  if(morrendo())return;
  int bx=(int)x-75,by=(int)y+72;
  HudMetal.painel(g,bx,by,150,25);
  for(int i=0;i<20;i++){g.setColor(i<(int)Math.ceil(20.0*vida/max)?new Color(240,65,49):new Color(25,30,40));g.fillRect(bx+7+i*7,by+7,5,10);}
  g.setColor(exposto()?Color.ORANGE:Color.WHITE);g.setFont(new java.awt.Font("Monospaced",1,11));
  String nome=rainha?"RAINHA DO ENXAME":"PIRATA DOMADOR";
  g.drawString(exposto()?(rainha?"ACERTE O CORACAO!":"REATOR EXPOSTO!"):nome+" / "+(estagio+1),(int)x-95,(int)y-72);
 }
}
