package Jogo;
import java.awt.*;import java.awt.image.BufferedImage;import java.io.*;import java.lang.reflect.*;import javax.imageio.ImageIO;import javax.swing.*;
public final class Preview094 {
 static int frame;static final OutputStream OUT=new BufferedOutputStream(System.out,1048576);
 static void salvar(JPanel f,String texto)throws Exception{
  BufferedImage b=new BufferedImage(960,600,BufferedImage.TYPE_INT_RGB);Graphics2D g=b.createGraphics();f.setSize(960,600);f.paint(g);g.setColor(new Color(0,0,0,220));g.fillRect(0,580,960,20);g.setColor(Color.WHITE);g.setFont(new Font("Monospaced",1,11));g.drawString("DEMONSTRACAO 0.9.4 | "+texto,12,594);g.dispose();
  if(frame==20||frame==105||frame==180||frame==340||frame==480)ImageIO.write(b,"png",new File("preview094_"+frame+".png"));
  int[] pix=b.getRGB(0,0,960,600,null,0,960);byte[] rgb=new byte[pix.length*3];for(int i=0;i<pix.length;i++){rgb[i*3]=(byte)(pix[i]>>16);rgb[i*3+1]=(byte)(pix[i]>>8);rgb[i*3+2]=(byte)pix[i];}OUT.write(rgb);frame++;
 }
 static void avancar(Fase f)throws Exception{TesteInvasao093.set(f.getNave(),"invulneravel",5.0);for(int k=0;k<3;k++)f.atualizarSimulacao(1.0/60);}
 public static void main(String[] args)throws Exception{
  Constructor<TelaInicial> c=TelaInicial.class.getDeclaredConstructor(JFrame.class);c.setAccessible(true);TelaInicial menu=c.newInstance((JFrame)null);((Timer)TesteInvasao093.get(menu,"animador")).stop();Field pagina=TelaInicial.class.getDeclaredField("pagina");pagina.setAccessible(true);for(Object e:pagina.getType().getEnumConstants())if(e.toString().equals("HANGAR"))pagina.set(menu,e);
  for(int i=0;i<40;i++){TesteInvasao093.set(menu,"modeloSelecionado",i/8);salvar(menu,"FROTA / ATRIBUTOS REAIS");}
  Fase f=TesteInvasao093.fase(94);for(int i=0;i<60;i++){avancar(f);salvar(f,"FORMACOES / NOVOS PIRATAS / ALIENS");}
  f.getAliens().clear();TesteInvasao093.set(f,"esperaProximoEsquadrao",100.0);f.invocarMiniChefeTeste();for(int i=0;i<80;i++){avancar(f);salvar(f,"SENTINELA / CARGA / DISPARO");}
  f=TesteInvasao093.fase(94);f.getAliens().clear();TesteInvasao093.set(f,"esperaProximoEsquadrao",100.0);int j=0;for(Item.Tipo t:new Item.Tipo[]{Item.Tipo.ESCUDO,Item.Tipo.ARMA_2,Item.Tipo.ARMA_GELO,Item.Tipo.ARMA_FOGO,Item.Tipo.REPARO,Item.Tipo.MELHORIA})f.getItens().add(new Item(t,320+j++*95,260,true));TesteInvasao093.set(f,"espacoPressionado",true);for(int i=0;i<40;i++){avancar(f);salvar(f,"ITENS / NOVO TIRO PADRAO");}
  f=TesteInvasao093.fase(94);f.getAliens().clear();f.iniciarChefeTeste(2);for(int i=0;i<70;i++){avancar(f);salvar(f,"HALFORD / POSES DE ATAQUE");}
  f=TesteInvasao093.fase(94);f.getAliens().clear();f.iniciarChefeTeste(8);for(int i=0;i<60;i++)avancar(f);TesteRevisao094.call(f,"derrotarChefao");for(int i=0;i<95;i++){avancar(f);salvar(f,"DERROTA / EXPLOSOES / FRAGMENTOS");}
  for(int i=0;i<55;i++){if(i>15){double nx=(double)TesteInvasao093.get(f.getNave(),"x");TesteInvasao093.set(f.getNave(),"x",Math.min(550,nx+14));TesteInvasao093.set(f.getNave(),"y",280.0);}avancar(f);salvar(f,"COLETE A RECOMPENSA PARA AVANCAR");}
  for(int i=0;i<90;i++){avancar(f);salvar(f,"DOBRA ESPACIAL EM PIXELS");}OUT.flush();System.err.println("Preview real: "+frame+" quadros.");System.exit(0);
 }
}
