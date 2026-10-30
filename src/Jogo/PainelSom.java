package Jogo;
import java.awt.*;
import java.awt.event.KeyEvent;
final class PainelSom {
 private static boolean aberto;private static int linha;
 static boolean aberto(){return aberto;}
 static boolean tecla(int k){
  if(k==KeyEvent.VK_F10){SomJogo.alternarMudo();return true;}
  if(k==KeyEvent.VK_F9){aberto=!aberto;SomJogo.tocar(SomJogo.Efeito.MENU);return true;}
  if(!aberto)return false;if(k==27){aberto=false;return true;}
  if(k==38)linha=(linha+2)%3;if(k==40)linha=(linha+1)%3;
  if(k==37||k==39){int d=k==37?-10:10;if(linha==0)SomJogo.volumeMusica(SomJogo.musica()+d);else if(linha==1)SomJogo.volumeEfeitos(SomJogo.efeitos()+d);else SomJogo.alternarMudo();}
  if(k==10||k==32){if(linha==2)SomJogo.alternarMudo();else SomJogo.tocar(SomJogo.Efeito.CONFIRMAR);}return true;
 }
 static void desenhar(Graphics2D g){if(!aberto)return;g.setColor(new Color(0,0,0,210));g.fillRect(0,0,960,600);g.setColor(new Color(8,18,32));g.fillRect(160,115,640,370);g.setColor(Color.CYAN);g.drawRect(160,115,640,370);
  g.setFont(new Font("Monospaced",Font.BOLD,28));g.drawString("METAL SPACE / AUDIO",200,170);
  String[] nomes={"MUSICA","EFEITOS","SILENCIAR"};int[] vols={SomJogo.musica(),SomJogo.efeitos()};
  for(int i=0;i<3;i++){int y=225+i*65;g.setColor(i==linha?new Color(255,190,80):Color.WHITE);g.setFont(new Font("Monospaced",Font.BOLD,21));g.drawString((i==linha?"> ":"  ")+nomes[i],190,y);if(i<2){g.setColor(new Color(25,58,73));g.fillRect(430,y-20,240,24);g.setColor(Color.CYAN);g.fillRect(430,y-20,vols[i]*240/100,24);g.drawString(vols[i]+"%",690,y);}else g.drawString(SomJogo.mudo()?"SIM":"NAO",430,y);}
  g.setFont(new Font("Monospaced",Font.PLAIN,13));g.setColor(Color.LIGHT_GRAY);g.drawString("SETAS: AJUSTAR   ENTER: OUVIR   ESC/F9: VOLTAR",195,420);g.drawString("F10: SILENCIAR / REATIVAR",195,443);g.drawString(SomJogo.diagnostico(),195,465);
 }
}
