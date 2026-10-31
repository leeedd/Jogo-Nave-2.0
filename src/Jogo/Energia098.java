package Jogo;
/** Energia de combate; desbloqueio e energia sao estados diferentes. */
final class Energia098 {
 private final int[] carga={100,100}; private int selecionada;
 int carga(int i){return carga[i];}
 void selecionar(int i){if(i>=0&&i<2)selecionada=i;}
 boolean gastar(int i,int custo){if(custo<=0||carga[i]<custo)return false;carga[i]-=custo;selecionada=i;return true;}
 int recarregar(boolean gelo,boolean fogo){boolean[] tem={gelo,fogo};int sobra=10;for(int j=0;j<2;j++){int i=(selecionada+j)%2;if(tem[i]){int n=Math.min(sobra,100-carga[i]);carga[i]+=n;sobra-=n;}}return 10-sobra;}
 void restaurar(int i){carga[i]=100;}
 /** Calor das armas de gelo e fogo: cada tiro esquenta; no maximo superaquece e trava por alguns segundos. */
 static final double CALOR_TIRO[]={22,14},RESFRIA=30,TRAVA=5;
 private final double[] calor=new double[2],travada=new double[2];
 double calor(int i){return calor[i];} double travada(int i){return travada[i];} boolean superaquecida(int i){return travada[i]>0;}
 /** Registra um tiro; devolve true se a arma acabou de superaquecer. */
 boolean esquentar(int i){calor[i]+=CALOR_TIRO[i];if(calor[i]>=100){calor[i]=100;travada[i]=TRAVA;return true;}return false;}
 void esfriar(double dt){for(int i=0;i<2;i++){if(travada[i]>0){travada[i]=Math.max(0,travada[i]-dt);if(travada[i]==0)calor[i]=0;}else calor[i]=Math.max(0,calor[i]-RESFRIA*dt);}}
}
