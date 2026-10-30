package Jogo;
import java.awt.*;
/** Cofre enterrado num asteroide: passa uma vez por visita, recuperavel ao revisitar. */
final class Segredo098 {
 private final Asteroide rocha;private int vida=8;private boolean visivel=true;final int tanque;final boolean reator;boolean casco;
 Segredo098(int setor,boolean nucleo){tanque=setor/2;reator=nucleo;rocha=new Asteroide(1030,setor%4==0?182:472,43,2);}
 void atualizar(double dt){rocha.mexer(dt);if(!rocha.isVisible())visivel=false;}
 boolean ativo(){return visivel;}Rectangle bounds(){return rocha.getBounds();}
 boolean atingir(int dano){if(!visivel)return false;vida-=Math.max(1,dano);if(vida<=0){visivel=false;return true;}return false;}
 void desenhar(Graphics2D g,double t){if(!visivel)return;rocha.desenhar(g);if(vida<8 || Math.sin(t*2)> .95){Graphics2D a=(Graphics2D)g.create();a.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,vida<8?.85f:.35f));Arte098.item(a,10,rocha.getX()-21,rocha.getY()-21,42);a.dispose();}}
 /** Nucleo de Casco: um por setor, escondido em outro asteroide, mais alto ou mais baixo que o cofre. */
 static Segredo098 nucleo(int setor){Segredo098 s=new Segredo098(setor,false,setor%2==0?120:528);s.casco=true;return s;}
 private Segredo098(int setor,boolean nucleo,int y){tanque=setor;reator=nucleo;rocha=new Asteroide(1030,y,43,3);}
 Item liberar(){return new Item(casco?Item.Tipo.NUCLEO_CASCO:reator?Item.Tipo.REATOR_CONVERGENCIA:Item.Tipo.SUBTANQUE,rocha.getX(),rocha.getY(),false,tanque);}
}
