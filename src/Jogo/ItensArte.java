package Jogo;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

/** Icones de itens colecionaveis: apagados antes da coleta, iluminados depois. */
public final class ItensArte {
    private static final Item.Tipo[] ITENS = {
            Item.Tipo.REPARO, Item.Tipo.MELHORIA, Item.Tipo.ESCUDO,
            Item.Tipo.ARMA_2, Item.Tipo.ARMA_GELO, Item.Tipo.ARMA_FOGO
    };
    private static final BufferedImage[] NORMAL = new BufferedImage[ITENS.length];
    private static final BufferedImage[] CINZA = new BufferedImage[ITENS.length];
    private ItensArte() { }
    public static Item.Tipo[] tipos(){return ITENS.clone();}
    private static void carregar(int i) {
        if(NORMAL[i]!=null) return;
        String nome = "item_icone_" + ITENS[i].name().toLowerCase(java.util.Locale.ROOT) + ".png";
        Image arquivo=Assets.imagem(nome);
        BufferedImage original=new BufferedImage(80,80,BufferedImage.TYPE_INT_ARGB);
        Graphics2D g=original.createGraphics();g.drawImage(arquivo,0,0,80,80,null);g.dispose();
        NORMAL[i]=original;
        BufferedImage apagado=new BufferedImage(80,80,BufferedImage.TYPE_INT_ARGB);
        for(int y=0;y<80;y++)for(int x=0;x<80;x++){
            int cor=original.getRGB(x,y), alfa=cor>>>24;
            if(alfa==0)continue;
            int c=19+((cor>>>16)&255)/18;
            apagado.setRGB(x,y,(alfa<<24)|(c<<16)|((c+4)<<8)|(c+10));
        }
        CINZA[i]=apagado;
    }
    public static void desenhar(Graphics2D g,int indice,boolean obtido,int x,int y,int tamanho){
        carregar(indice);
        Graphics2D a=(Graphics2D)g.create();
        a.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        a.setColor(new Color(11,21,35));a.fillRect(x,y,tamanho,tamanho);
        a.setStroke(new BasicStroke(2));
        a.setColor(obtido?new Color(243,184,91):new Color(54,69,86));
        a.drawRect(x,y,tamanho,tamanho);
        a.drawImage(obtido?NORMAL[indice]:CINZA[indice],x+3,y+3,tamanho-6,tamanho-6,null);
        a.dispose();
    }
}
