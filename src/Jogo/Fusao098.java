package Jogo;
import java.awt.*;
/** Um pulso por ativacao; recarga e dano permanecem responsabilidade da fase. */
final class Fusao098 {
 static final String[] NOMES={"CHOQUE TERMICO","MARE ABISSAL","LANCA CROMADA","EGIDE DO CORSARIO","COMETA DE SUCATA","CORRENTE DE RIFF","GRITO DEVASTADOR","PACTO DA TORMENTA","ECO SOMBRIO"};
 static final String[] EFEITOS={"Area + queimadura","Area ampla + congelamento","Feixe perfurante frontal","Area curta + escudo 5s","Area + impulso por 6s","Ate 3 alvos em cadeia","Cone frontal amplo","Escudo 8s + limpa tiros","Onda em toda a tela"};
 static final int[] DANO={14,16,38,10,18,20,24,8,18};
 static final double[] RECARGA={30,32,36,28,26,30,32,35,50};
 final int tipo,x,y;private double tempo;
 Fusao098(int t,int x,int y){tipo=t;this.x=x;this.y=y;}
 void atualizar(double dt){tempo+=dt;}boolean ativa(){return tempo<1.1;}
 boolean alcanca(Rectangle b){double dx=b.getCenterX()-x,dy=b.getCenterY()-y;switch(tipo){case 2:return dx>=-20&&dx<900&&Math.abs(dy)<48;case 5:return dx>-40&&Math.hypot(dx,dy)<800;case 6:return dx>=0&&dx<700&&Math.abs(dy)<60+dx*.6;case 8:return true;default:return Math.hypot(dx,dy)<(tipo==1?330:tipo==3?190:tipo==7?220:260);}}
 void desenhar(Graphics2D original){if(!ativa())return;Graphics2D g=(Graphics2D)original.create();g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,(float)Math.max(0,1-tempo/1.1)));Color c=tipo==0?Color.ORANGE:MapaEstelar.CORES[(tipo-1)%8];g.setColor(c);
  if(tipo==2){for(int k=0;k<6;k++)g.fillRect(x,y-16+k*6,960-x,3);}else if(tipo==6){for(int i=-3;i<=3;i++)for(int k=0;k<80;k++)g.fillRect(x+k*10,y+(int)(k*5*i/3.0),6,3);}else{
   int raio=(int)(tempo*(tipo==8?1000:tipo==1?480:380));
   for(int i=0;i<160;i++){double a=i*Math.PI/80;int xx=x+(int)(Math.cos(a)*raio),yy=y+(int)(Math.sin(a)*raio);g.fillRect(xx,yy,5,5);}
  }
  Arte098.fusao(g,tipo,x-32,y-32,64);g.dispose();
 }
}
