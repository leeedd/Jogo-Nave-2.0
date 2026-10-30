package Jogo;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

/** Atlas original separado em sprites; articulacao e efeitos em grade de pixels. */
final class ArteEnxame {
 private static final BufferedImage[] SPRITES=new BufferedImage[8];
 static {
  int[][] areas={{18,127,379,501},{404,131,307,495},{719,237,246,263},{977,228,265,300},{28,705,329,419},{363,751,285,355},{669,741,312,391},{994,761,246,363}};
  try{
   java.net.URL u=ArteEnxame.class.getResource("/Enxame098C/atlas.png");
   if(u==null)throw new IOException("Atlas do enxame ausente");BufferedImage atlas=ImageIO.read(u);
   for(int i=0;i<8;i++){
    int[] r=areas[i];BufferedImage sprite=atlas.getSubimage(r[0],r[1],r[2],r[3]);
    int w=i<2?90:32,h=i<2?80:28;
    SPRITES[i]=new BufferedImage(w,h,BufferedImage.TYPE_INT_ARGB);Graphics2D g=SPRITES[i].createGraphics();
    g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);g.drawImage(sprite,0,0,w,h,null);g.dispose();
   }
  }catch(IOException ex){throw new ExceptionInInitializerError(ex);}
 }
 static void criatura(Graphics2D original,int especie,int x,int y,double t){
  BufferedImage s=SPRITES[especie+2];Graphics2D g=(Graphics2D)original.create();
  g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
  int pulso=(int)Math.round(Math.sin(t*(especie==0?19:7)+especie)*2);
  // Extremidades se articulam independentemente; centro permanece estavel.
  g.drawImage(s,x,y-pulso,x+64,y+16,0,0,32,8,null);
  g.drawImage(s,x,y+16,x+64,y+40,0,8,32,20,null);
  g.drawImage(s,x,y+40,x+64,y+56+pulso,0,20,32,28,null);
  g.dispose();
 }
 static void ataque(Graphics2D original,int especie,int x,int y,double t,double ataque){
  if(ataque<=0){criatura(original,especie,x,y,t);return;}
  Graphics2D g=(Graphics2D)original.create();BufferedImage s=SPRITES[especie+2];
  double p=Math.sin(Math.PI*Math.min(1,ataque/.65));int abertura=(int)(p*9);
  switch(especie){
   case 0: // asas abertas e ferrao projetado
    g.drawImage(s,x-abertura,y-abertura,64+abertura,56+abertura*2,null);break;
   case 1: // placas se separam enquanto o canhao organico dispara
    g.drawImage(s,x,y-abertura,x+64,y+24,0,0,32,12,null);
    g.drawImage(s,x,y+24,x+64,y+56+abertura,0,12,32,28,null);break;
   case 2: // duas foices articuladas em direcoes opostas
    g.rotate(-p*.3,x+32,y+28);g.drawImage(s,x,y-4,64,56,null);break;
   case 3: // bolsas inflam antes da investida
    g.drawImage(s,x-abertura/2,y-abertura/2,64+abertura,56+abertura,null);break;
   case 4: // pernas abrem enquanto o abdomen contrai
    g.drawImage(s,x,y-abertura,x+64,y+20,0,0,32,10,null);
    g.drawImage(s,x+abertura,y+20,64-abertura,18,null);
    g.drawImage(s,x,y+38,x+64,y+56+abertura,0,19,32,28,null);break;
   default: // boca avanca e cauda se recolhe
    g.drawImage(s,x-abertura,y+abertura/2,64+abertura,56-abertura,null);break;
  }
  g.dispose();
 }
 static void derrota(Graphics2D original,int especie,int x,int y,double t){
  Graphics2D g=(Graphics2D)original.create();g.setComposite(AlphaComposite.SrcOver.derive((float)Math.max(0,1-t/1.25)));
  BufferedImage s=SPRITES[especie+2];
  // Cada especie tem trajetorias e ritmo de desintegracao proprios.
  for(int i=0;i<8;i++){
   int sx=(i%4)*8,sy=(i/4)*14;double ang=i*Math.PI/4;
   int dx,dy;
   switch(especie){
    case 0:dx=(int)(-t*95+i*9);dy=(int)(Math.sin(ang)*t*60);break; // asas arrancadas para tras
    case 1:dx=(int)(Math.cos(ang)*t*44);dy=(int)(Math.sin(ang)*t*44+t*t*28);break; // placas pesadas
    case 2:dx=(int)((i%2==0?-1:1)*t*76);dy=(int)((i-4)*t*12);break; // foices se partem
    case 3:dx=(int)(Math.cos(ang)*t*90);dy=(int)(Math.sin(ang)*t*90);break; // bolsas estouram radialmente
    case 4:dx=(int)(Math.cos(ang)*t*55);dy=(int)(Math.sin(ang)*t*70);break; // patas se destacam
    default:dx=(int)(Math.sin(i+t*9)*t*25);dy=(int)(t*t*70+i*t*9);break; // segmentos desabam
   }
   g.drawImage(s,x+sx*2+dx,y+sy*2+dy,x+sx*2+dx+16,y+sy*2+dy+28,sx,sy,sx+8,sy+14,null);
  }
  if(t<.55){int r=4+(int)(t*45);g.setColor(especie==3?new Color(219,246,79):especie==4?new Color(135,239,245):new Color(255,160,61));
   for(int i=0;i<8;i++){double a=i*Math.PI/4;int px=x+32+(int)(Math.cos(a)*r),py=y+28+(int)(Math.sin(a)*r);g.fillRect(px,py,4,4);}
  }
  g.dispose();
 }
 static void comandante(Graphics2D original,int x,int y,boolean rainha,double t,boolean aberto,double morte){comandante(original,x,y,rainha,t,aberto,morte,0);}
 static void comandante(Graphics2D original,int x,int y,boolean rainha,double t,boolean aberto,double morte,int ataque){
  BufferedImage b=new BufferedImage(110,105,BufferedImage.TYPE_INT_ARGB);Graphics2D g=b.createGraphics();
  BufferedImage s=SPRITES[rainha?1:0];int movimento=(int)Math.round(Math.sin(t*(ataque>0?12:5))*(ataque>0?5:2));
  if(morte<1.8){
   g.drawImage(s,10,8-movimento,100,28,0,0,90,20,null);
   g.drawImage(s,10,28,100,68,0,20,90,60,null);
   g.drawImage(s,10,68,100,88+movimento,0,60,90,80,null);
   if(ataque>0){
    g.setColor(rainha?new Color(195,245,101):new Color(255,117,49));
    int pulso=ataque==2?6:2+(int)(Math.abs(Math.sin(t*13))*3);
    for(int k=0;k<(rainha?3:2);k++){int yy=rainha?30+k*15:25+k*38;g.fillRect(13,yy,pulso*2,pulso);g.setColor(new Color(255,244,203));g.fillRect(13,yy,3,2);}
   }
   if(aberto){
    // O nucleo esta em x-44..x-12, y-18..y+18; arte e colisao compartilham posicao.
    g.setColor(new Color(12,7,13));g.fillPolygon(new int[]{34,46,51,51,46,34,29,29},new int[]{35,35,40,53,59,59,53,40},8);
    int p=(int)Math.round(Math.sin(t*(rainha?11:8)));
    if(rainha){
     g.setColor(new Color(109,19,38));g.fillRect(36,32,3,9);g.fillRect(43,34,3,8);
     g.fillPolygon(new int[]{31,34,38,41,45,49,49,46,40,34,31},new int[]{42,38,38,41,38,41,47,53,58,53,47},11);
     g.setColor(new Color(219,37,65));g.fillPolygon(new int[]{33-p,36,40,44,47+p,47,40,34},new int[]{43,40-p,44,40-p,43,49,55+p,49},8);
     g.setColor(new Color(255,105,114));g.fillRect(35,42,3,5);g.fillRect(44,42,2,4);
     g.setColor(new Color(132,18,47));g.fillRect(39,45,2,8);g.fillRect(36,48,4,2);g.fillRect(41,50,3,2);
     g.setColor(new Color(255,190,176));g.fillRect(35,42,2,2);
    }else{
     g.setColor(new Color(111,119,135));g.fillPolygon(new int[]{35,45,49,49,45,35,31,31},new int[]{37,37,41,51,55,55,51,41},8);
     g.setColor(new Color(35,39,48));g.fillRect(34,40,12,12);
     g.setColor(new Color(250,108,20));g.fillRect(36-p,41-p,8+p*2,10+p*2);
     g.setColor(new Color(255,225,101));g.fillRect(38,42,4,8);g.setColor(new Color(255,255,218));g.fillRect(39,44,2,4);
     for(int k=0;k<4;k++){double ang=t*5+k*Math.PI/2;g.setColor(new Color(255,196,70));g.fillRect(39+(int)(Math.cos(ang)*8),45+(int)(Math.sin(ang)*8),2,2);}
    }
    // Duas placas da propria textura se afastam, sem substituir o corpo.
    g.drawImage(s,29,26,53,35,19,28,43,37,null);
    g.drawImage(s,29,59,53,68,19,43,43,52,null);
    if(!rainha)for(int i=0;i<8;i++){g.setColor(i%2==0?Color.ORANGE:new Color(96,101,110));g.fillRect(32+(i*11)%22,29-(int)((t*23+i*6)%22),2,3);}
   }
  }
  if(morte>0){
   g.setComposite(AlphaComposite.SrcOver.derive((float)Math.max(0,Math.min(1,(2.8-morte)/1.1))));
   // Fragmentos reais se separam, seguidos de varias detonacoes locais.
   for(int i=0;i<12;i++){
    double a=i*2.399,p=Math.max(0,morte-.35);int xx=45+(int)(Math.cos(a)*p*(rainha?18:30)),yy=44+(int)(Math.sin(a)*p*24+(rainha?p*p*6:0));
    int sx=(i%4)*20,sy=(i/4)*24;
    g.drawImage(s,xx,yy,xx+12,yy+12,sx,sy,sx+12,sy+12,null);
   }
   for(int i=0;i<18;i++){
    double p=morte-i*.085;if(p<0||p>.9)continue;
    int xx=24+(i*29)%62,yy=17+(i*17)%65,raio=2+(int)(Math.sin(p/.9*Math.PI)*12);
    Arte094.desenhar(g,11,xx-raio,yy-raio,raio*2,raio*2);
   }
  }
  g.dispose();original.drawImage(b,x-110,y-90,220,210,null);
 }
}
