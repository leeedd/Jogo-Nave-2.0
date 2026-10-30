package Jogo;
import java.awt.*;
import java.awt.image.*;
import java.io.*;
public final class PreviewEnxame {
 public static void main(String[] args)throws Exception{
  Process ff=new ProcessBuilder("ffmpeg","-y","-f","rawvideo","-pixel_format","bgr24","-video_size","960x600","-framerate","24","-i","-","-an","-c:v","libx264","-preset","fast","-crf","20","-pix_fmt","yuv420p","/tmp/MetalSpace_Enxame_098C.mp4").redirectError(new File("swarm_build/preview.log")).start();
  CenaInterdicao cena=new CenaInterdicao(new Nave(),true);
  FundoEspacial fundo=new FundoEspacial();fundo.definirProgresso(0,6);
  try(OutputStream out=ff.getOutputStream()){
   for(int frame=0;frame<24*20;frame++){
    double t=frame/24.0;BufferedImage b=new BufferedImage(960,600,BufferedImage.TYPE_3BYTE_BGR);Graphics2D g=b.createGraphics();
    if(t<6.2){cena.atualizar(1.0/24);cena.desenhar(g);}
    else{
     fundo.atualizar(1.0/24);fundo.desenhar(g);g.setColor(new Color(0,0,0,150));g.fillRect(0,0,960,600);
     if(t<12.2){
      double q=(t-6.2)%3;
      String[] nomes={"FERROES","CARAPACAS","CEIFADORES","VESICULAS","TECELOES","SANGUESSUGAS"};
      for(int i=0;i<6;i++){
       int x=125+(i%3)*300,y=150+(i/3)*220;Graphics2D a=(Graphics2D)g.create();a.translate(x,y);a.scale(2,2);
       if(q<1.8)ArteEnxame.ataque(a,i,0,0,t,.1+(.5*Math.abs(Math.sin(t*3))));else ArteEnxame.derrota(a,i,0,0,q-1.8);a.dispose();
       g.setColor(Color.WHITE);g.setFont(new Font("Monospaced",1,15));g.drawString(nomes[i],x-10,y+135);
      }
     }else{
      double q=t-12.2;boolean aberto=q<4.5;double morte=aberto?0:q-4.5;
      ArteEnxame.comandante(g,270,300,false,t,aberto,morte);ArteEnxame.comandante(g,700,300,true,t,aberto,morte);
      g.setColor(Color.ORANGE);g.setFont(new Font("Monospaced",1,20));g.drawString(aberto?"REATOR EXPOSTO":"PIRATA DESTRUIDO",175,445);g.drawString(aberto?"CORACAO EXPOSTO":"RAINHA DESTRUIDA",595,445);
     }
    }
    g.setColor(new Color(6,10,22));g.fillRect(0,0,960,60);g.setColor(new Color(240,195,107));g.setFont(new Font("Monospaced",1,21));g.drawString("METAL SPACE / ENXAME 0.9.8C",280,28);g.setFont(new Font("Monospaced",1,13));g.setColor(Color.WHITE);g.drawString(t<6.2?"CUTSCENE: CAPTURA E SAIDA FORCADA DA DOBRA":t<12.2?"SEIS ESPECIES / ATAQUES E DERROTAS DIFERENTES":"PONTOS FRACOS / SEQUENCIA DE EXPLOSOES",270,48);
    g.dispose();out.write(((DataBufferByte)b.getRaster().getDataBuffer()).getData());
   }
  }
  if(ff.waitFor()!=0)throw new IOException("Falha no encoder");System.out.println("PREVIEW OK");System.exit(0);
 }
}
