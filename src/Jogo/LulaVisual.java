package Jogo;
import java.awt.*;
import java.awt.image.BufferedImage;

/** Pecas reais: carapaca rigida, olho com poses desenhadas e elos articulados. */
final class LulaVisual {
    private static BufferedImage imagem(String nome){
        Image i=Assets.imagem(nome);BufferedImage b=new BufferedImage(i.getWidth(null),i.getHeight(null),BufferedImage.TYPE_INT_ARGB);
        Graphics2D g=b.createGraphics();g.drawImage(i,0,0,null);g.dispose();return b;
    }
    private static final BufferedImage CORPO=imagem("abismo_corpo_091.png");
    private static final BufferedImage ATLAS=imagem("abismo_olho_atlas_091.png");
    private static final BufferedImage ELO=imagem("abismo_segmento_091.png").getSubimage(530,295,480,435);
    private static final BufferedImage[] OLHOS=new BufferedImage[4];
    static {for(int i=0;i<4;i++)OLHOS[i]=ATLAS.getSubimage(i*ATLAS.getWidth()/4,0,ATLAS.getWidth()/4,ATLAS.getHeight());}
    private LulaVisual() { }
    static void elo(Graphics2D g,double x,double y,double angulo,double largura){
        Graphics2D a=(Graphics2D)g.create();a.translate(x,y);a.rotate(angulo);
        // Juncoes sobrepostas, com textura e ventosas; nao ha deformacao do bitmap.
        a.drawImage(ELO,-(int)(largura*.55),-(int)(largura*.5),(int)(largura*1.1),(int)largura,null);a.dispose();
    }
    static void corpo(Graphics2D g,int quadro,double carga,double dano){
        g.drawImage(CORPO,58,-8,344,408,null);
        int q=quadro==0?0:quadro<4?1:quadro<8?2:3;
        g.drawImage(OLHOS[q],148,115,164,219,null);
        if(carga>0){
            g.setColor(new Color(255,153,19,(int)(60*carga)));
            g.fillOval(203,204,54,62);
        }
        if(dano>0){
            g.setColor(new Color(255,232,160));
            for(int i=0;i<5;i++){int x=180+i*19,y=218+(i%2)*31-(int)((.15-dano)*90);g.fillRect(x,y,3,3);}
        }
    }
}
