package Jogo;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
/** Teste mínimo dos componentes 0.9.9, sem interface gráfica nem gravação persistente. */
public final class TesteIntegracao099 {
    private static int checks;
    private static void ok(boolean b, String msg){if(!b)throw new AssertionError(msg);checks++;}
    public static void main(String[] args){
        ProgressaoNave inventario = new ProgressaoNave(null);
        inventario.creditar(10000);
        ok(inventario.comprarMercado(0,0),"comprar chassi");
        ok(inventario.peca(0,0)==1,"chassi comprado");
        ok(inventario.peca(1,0)==0,"peças independentes por nave");
        Nave n = new Nave();n.aplicarPecas(inventario.peca(0,0),inventario.peca(0,1),inventario.peca(0,2));
        ok(n.pisoPermanente()==1,"evolução aplicada");
        inventario.registrarChave(8);
        Poderes099 poderes = new Poderes099();poderes.selecionar(0,inventario);
        ok(poderes.ativar(inventario,n),"poder desbloqueado pelo troféu");
        ok(poderes.ativo==0,"poder ativado");
        poderes.atualizar(.04,n);ok(poderes.energia[0]<100,"energia gasta");
        BufferedImage b=new BufferedImage(960,600,BufferedImage.TYPE_INT_ARGB);
        Graphics2D g=b.createGraphics();
        Pausa099.desenhar(g,4,0,inventario,n,new Energia098(),poderes,true,true,1.0,"");
        ok(b.getRGB(300,130)!=0,"mercado renderizado");
        CenaFinal c=new CenaFinal();for(int i=0;i<780;i++)c.atualizar(.1);
        c.desenhar(g);ok(c.mostrandoMegazord(),"teaser final alcançado");
        g.dispose();
        System.out.println("OK 0.9.9: "+checks+" verificações.");
        System.exit(0);
    }
}
