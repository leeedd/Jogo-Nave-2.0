package Jogo;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
final class Painel098 {
 private Painel098(){}
 static void desenhar(Graphics2D g,ProgressaoNave p,int selecionado){
  g.setColor(new Color(4,10,22));g.fillRect(175,235,620,275);
  g.setFont(new Font(Font.MONOSPACED,Font.BOLD,14));g.setColor(Color.WHITE);
  g.drawString("OFICINA / CR "+p.saldo(),195,255);
  String[] nomes={"RETENCAO DE ARMAS","GELO NIVEL "+p.gelo(),"FOGO NIVEL "+p.fogo(),"BLINDAGEM NIVEL "+p.blindagem()};
  int[] icones={5,6,7,9};
  for(int i=0;i<4;i++){
   int y=270+i*46;g.setColor(i==selecionado?new Color(35,75,100):new Color(12,25,40));g.fillRect(190,y,365,43);
   Arte098.item(g,icones[i],194,y,40);g.setColor(Color.WHITE);
   g.drawString(nomes[i],240,y+18);g.setColor(new Color(255,210,100));
   g.drawString(p.preco(i)==0?"MAXIMO / ADQUIRIDO":p.preco(i)+" CR",240,y+35);
  }
  for(int i=0;i<4;i++){int x=575+i%2*95,y=275+i/2*90;
   int c=p.carga(i);Arte098.item(g,c==0?0:c<50?1:c<100?2:3,x,y,65);
   g.setColor(p.temTanque(i)?Color.CYAN:Color.GRAY);g.drawString(p.temTanque(i)?c+"%":"OCULTO",x,y+77);
  }
  g.setColor(Color.WHITE);g.drawString("SETAS: ESCOLHER | ESPACO: COMPRAR | O: VOLTAR",195,482);
  g.setFont(new Font(Font.MONOSPACED,Font.PLAIN,11));g.drawString("Reparo: casco primeiro; excedente abastece reservas. R: usar em combate.",190,503);
 }
}
