package Jogo;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.prefs.Preferences;

/** Menu de Opcoes em pixel art (F9 ou OPCOES na tela inicial): abas Audio, Jogo e Controles. */
final class PainelSom {
 private static boolean aberto;private static int aba,linha;
 private static final BufferedImage MOLDURA=img("ui_moldura"),TOGGLE_ON=img("ui_toggle_on"),TOGGLE_OFF=img("ui_toggle_off");
 private static final BufferedImage[] SLIDER={img("ui_slider_musica"),img("ui_slider_efeitos"),img("ui_slider_mudo")};
 private static final BufferedImage[] ABAS={img("ui_aba_audio"),img("ui_aba_jogo"),img("ui_aba_controles")};
 private static final String[] NOMES_ABAS={"AUDIO","JOGO","CONTROLES"};
 /** Posicao das abas e da area de conteudo dentro da moldura original (783x654). */
 private static final int[][] ABA_NA_MOLDURA={{96,119,193,72},{304,119,181,72},{496,119,196,72}};
 private static final Rectangle CONTEUDO=new Rectangle(74,199,640,365);
 private static final double ESCALA=.78;
 private static final Preferences PREFS=Preferences.userNodeForPackage(PainelSom.class).node("opcoes");
 private static boolean dicas=ler("dicas",true),tremor=ler("tremor",true);

 static boolean aberto(){return aberto;}
 static void abrir(){aberto=true;aba=0;linha=0;SomJogo.tocar(SomJogo.Efeito.MENU);}
 /** Mostrar as teclas de atalho (C, X) junto a barra de energia. */
 static boolean dicasTeclas(){return dicas;}
 /** Permitir tremor de tela nas explosoes. */
 static boolean tremorTela(){return tremor;}

 static boolean tecla(int k){
  if(k==KeyEvent.VK_F10){SomJogo.alternarMudo();return true;}
  if(k==KeyEvent.VK_F9){if(aberto)aberto=false;else abrir();return true;}
  if(!aberto)return false;
  if(k==KeyEvent.VK_ESCAPE){aberto=false;return true;}
  if(k==KeyEvent.VK_TAB||k==KeyEvent.VK_Q||k==KeyEvent.VK_E){aba=Math.floorMod(aba+(k==KeyEvent.VK_Q?-1:1),3);linha=0;SomJogo.tocar(SomJogo.Efeito.MENU);return true;}
  if(k>=KeyEvent.VK_1&&k<=KeyEvent.VK_3){aba=k-KeyEvent.VK_1;linha=0;SomJogo.tocar(SomJogo.Efeito.MENU);return true;}
  int linhas=aba==0?3:aba==1?2:0;
  if(linhas>0&&(k==KeyEvent.VK_UP||k==KeyEvent.VK_W)){linha=Math.floorMod(linha-1,linhas);SomJogo.tocar(SomJogo.Efeito.MENU);}
  if(linhas>0&&(k==KeyEvent.VK_DOWN||k==KeyEvent.VK_S)){linha=(linha+1)%linhas;SomJogo.tocar(SomJogo.Efeito.MENU);}
  boolean lado=k==KeyEvent.VK_LEFT||k==KeyEvent.VK_RIGHT||k==KeyEvent.VK_A||k==KeyEvent.VK_D,confirma=k==KeyEvent.VK_ENTER||k==KeyEvent.VK_SPACE;
  if(aba==0){
   int d=(k==KeyEvent.VK_LEFT||k==KeyEvent.VK_A)?-10:10;
   if(lado&&linha==0)SomJogo.volumeMusica(SomJogo.musica()+d);
   else if(lado&&linha==1){SomJogo.volumeEfeitos(SomJogo.efeitos()+d);SomJogo.tocar(SomJogo.Efeito.CONFIRMAR);}
   else if((lado||confirma)&&linha==2)SomJogo.alternarMudo();
  }else if(aba==1&&(lado||confirma)){
   if(linha==0){dicas=!dicas;gravar("dicas",dicas);}else{tremor=!tremor;gravar("tremor",tremor);}
   SomJogo.tocar(SomJogo.Efeito.CONFIRMAR);
  }
  return true;
 }

 static void desenhar(Graphics2D tela){if(!aberto)return;
  Graphics2D g=(Graphics2D)tela.create();
  g.setColor(new Color(0,0,0,200));g.fillRect(0,0,960,600);
  if(MOLDURA==null){g.dispose();return;}
  int mw=(int)(MOLDURA.getWidth()*ESCALA),mh=(int)(MOLDURA.getHeight()*ESCALA),mx=(960-mw)/2,my=(600-mh)/2;
  g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_BILINEAR);
  g.drawImage(MOLDURA,mx,my,mw,mh,null);
  // abas: a selecionada recebe a arte acesa; as outras ficam apagadas
  for(int i=0;i<3;i++){int[] a=ABA_NA_MOLDURA[i];int ax=mx+(int)(a[0]*ESCALA),ay=my+(int)(a[1]*ESCALA),aw=(int)(a[2]*ESCALA),ah=(int)(a[3]*ESCALA);
   if(i==aba){if(ABAS[i]!=null)g.drawImage(ABAS[i],ax,ay,aw,ah,null);g.setColor(new Color(255,210,110));g.setStroke(new BasicStroke(2));g.drawRect(ax,ay,aw,ah);g.setStroke(new BasicStroke(1));}
   else{g.setColor(new Color(0,0,0,150));g.fillRect(ax,ay,aw,ah);}
}
  texto(g,NOMES_ABAS[aba],480,my+(int)(CONTEUDO.y*ESCALA)+18,13,new Color(255,210,110),true);
  int cx=mx+(int)(CONTEUDO.x*ESCALA)+18,cy=my+(int)(CONTEUDO.y*ESCALA)+46,cw=(int)(CONTEUDO.width*ESCALA)-36;
  if(aba==0)audio(g,cx,cy,cw);else if(aba==1)jogo(g,cx,cy,cw);else controles(g,cx,cy,cw);
  texto(g,aba==2?"TAB / 1-3: ABAS    ESC / F9: VOLTAR":"TAB / 1-3: ABAS   SETAS: AJUSTAR   ESC / F9: VOLTAR",480,my+mh+18,11,new Color(170,180,200),true);
  g.dispose();
 }

 private static void audio(Graphics2D g,int x,int y,int w){
  String[] nomes={"MUSICA","EFEITOS","SILENCIAR TUDO"};int[] vol={SomJogo.musica(),SomJogo.efeitos()};
  for(int i=0;i<3;i++){int ly=y+i*58;boolean sel=i==linha;
   texto(g,(sel?"> ":"  ")+nomes[i],x,ly+16,15,sel?new Color(255,210,110):Color.WHITE,false);
   if(i<2){BufferedImage s=SomJogo.mudo()?SLIDER[2]:SLIDER[i];int sw=250,sh=36,sx=x+w-sw,sy=ly-4;if(s!=null)g.drawImage(s,sx,sy,sw,sh,null);
    // gomos de volume desenhados por cima do trilho vazio (10 passos)
    int tx=sx+(int)(sw*.21),tw=(int)(sw*.64),ty=sy+(int)(sh*.38),th=Math.max(4,(int)(sh*.26));g.setColor(new Color(14,18,26));g.fillRect(tx,ty,tw,th);
    int n=10,gw=(tw-(n-1)*2)/n,cheios=SomJogo.mudo()?0:Math.round(vol[i]/10f);Color c=i==0?new Color(40,150,255):new Color(235,50,50);
    for(int k=0;k<n;k++){g.setColor(k<cheios?c:new Color(32,38,50));g.fillRect(tx+k*(gw+2),ty,gw,th);if(k<cheios){g.setColor(new Color(255,255,255,120));g.fillRect(tx+k*(gw+2),ty,gw,1);}}
    texto(g,SomJogo.mudo()?"MUDO":vol[i]+"%",sx-12,ly+16,13,new Color(170,190,210),false,true);
   }else interruptor(g,x+w-90,ly-6,SomJogo.mudo());}
  texto(g,"F10: SILENCIAR A QUALQUER MOMENTO",x,y+190,11,new Color(140,150,170),false);
 }
 private static void jogo(Graphics2D g,int x,int y,int w){
  String[] nomes={"DICAS DE TECLAS NA TELA","TREMOR DE TELA"};String[] ajuda={"Mostra C (usar) e X (trocar) junto da barra de energia.","Sacode a tela nas explosoes grandes e chefes."};boolean[] v={dicas,tremor};
  for(int i=0;i<2;i++){int ly=y+i*72;boolean sel=i==linha;texto(g,(sel?"> ":"  ")+nomes[i],x,ly+16,15,sel?new Color(255,210,110):Color.WHITE,false);
   texto(g,ajuda[i],x+18,ly+38,11,new Color(150,165,185),false);interruptor(g,x+w-90,ly-6,v[i]);}
  texto(g,"O PROGRESSO E AS COMPRAS SAO SALVOS SOZINHOS.",x,y+190,11,new Color(140,150,170),false);
 }
 private static void controles(Graphics2D g,int x,int y,int w){
  String[][] t={{"SETAS / WASD","PILOTAR"},{"ESPACO","ATIRAR"},{"F","TROCAR ARMA"},{"Q / E","GELO / FOGO"},{"V","CONVERGENCIA"},{"C","USAR PODER DO CHEFE"},{"X","PROXIMO PODER"},{"SHIFT","TRUQUE DA NAVE"},{"1 - 8","ESCOLHER PODER"},{"R","USAR RESERVA"},{"ENTER / P / ESC","PAUSA"},{"F9","OPCOES"},{"F10","SILENCIAR"}};
  for(int i=0;i<t.length;i++){int col=i/7,lin=i%7,kx=x+col*(w/2),ky=y+lin*27;
   g.setFont(new Font(Font.MONOSPACED,Font.BOLD,12));int kw=Math.max(26,g.getFontMetrics().stringWidth(t[i][0])+14);
   g.setColor(new Color(34,38,50));g.fillRoundRect(kx,ky,kw,22,6,6);g.setColor(new Color(150,160,180));g.drawRoundRect(kx,ky,kw,22,6,6);g.setColor(new Color(90,20,24));g.drawLine(kx+3,ky+21,kx+kw-3,ky+21);
   texto(g,t[i][0],kx+kw/2,ky+16,12,Color.WHITE,true);texto(g,t[i][1],kx+kw+10,ky+16,12,i>=5&&i<=7?new Color(255,210,110):new Color(200,210,225),false);}
 }
 private static void interruptor(Graphics2D g,int x,int y,boolean ligado){BufferedImage t=ligado?TOGGLE_ON:TOGGLE_OFF;if(t!=null)g.drawImage(t,x,y,80,40,null);texto(g,ligado?"LIGADO":"DESLIGADO",x-12,y+26,12,ligado?new Color(110,230,120):new Color(230,90,90),false,true);}

 private static void texto(Graphics2D g,String s,int x,int y,int t,Color c,boolean centro){texto(g,s,x,y,t,c,centro,false);}
 private static void texto(Graphics2D g,String s,int x,int y,int t,Color c,boolean centro,boolean direita){g.setFont(new Font(Font.MONOSPACED,Font.BOLD,t));int w=g.getFontMetrics().stringWidth(s);int px=centro?x-w/2:direita?x-w:x;g.setColor(new Color(0,0,0,170));g.drawString(s,px+1,y+1);g.setColor(c);g.drawString(s,px,y);}
 private static BufferedImage img(String n){java.net.URL u=PainelSom.class.getResource("/ImagemIcon/"+n+".png");if(u==null)return null;try{return javax.imageio.ImageIO.read(u);}catch(java.io.IOException e){return null;}}
 private static boolean ler(String k,boolean p){try{return PREFS.getBoolean(k,p);}catch(SecurityException e){return p;}}
 private static void gravar(String k,boolean v){try{PREFS.putBoolean(k,v);PREFS.flush();}catch(Exception e){System.err.println("Opcao nao gravada: "+k);}}
}
