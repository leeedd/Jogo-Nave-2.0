package Jogo;
/** Energia de combate; desbloqueio e energia sao estados diferentes. */
final class Energia098 {
 private final int[] carga={100,100}; private int selecionada;
 int carga(int i){return carga[i];}
 void selecionar(int i){if(i>=0&&i<2)selecionada=i;}
 boolean gastar(int i,int custo){if(custo<=0||carga[i]<custo)return false;carga[i]-=custo;selecionada=i;return true;}
 int recarregar(boolean gelo,boolean fogo){boolean[] tem={gelo,fogo};int sobra=10;for(int j=0;j<2;j++){int i=(selecionada+j)%2;if(tem[i]){int n=Math.min(sobra,100-carga[i]);carga[i]+=n;sobra-=n;}}return 10-sobra;}
 void restaurar(int i){carga[i]=100;}
}
