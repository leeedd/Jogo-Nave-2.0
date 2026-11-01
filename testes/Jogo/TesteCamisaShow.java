package Jogo;

import java.awt.image.BufferedImage;

/** A camisa e o palco do final seguem o show escolhido: o pentagrama do Slayer some nas outras bandas. */
public class TesteCamisaShow {
    public static void main(String[] a) {
        java.awt.Image tira = Assets.imagem("final_flavio_multidao.png");
        int slayer = vermelhos(CamisaShow.ajustar(tira, 1, CamisaShow.SLAYER)), judas = vermelhos(CamisaShow.ajustar(tira, 1, 1));
        if (slayer < 500) throw new AssertionError("pentagrama do Slayer deveria continuar: " + slayer);
        if (judas > slayer / 3) throw new AssertionError("pentagrama deveria sumir no show do Judas: " + judas + " de " + slayer);
        CenaPouso p = new CenaPouso(0);
        if (!p.escolhendo()) throw new AssertionError("o final deve comecar na escolha do show");
        p.mover(-1); p.confirmar();
        if (p.escolhendo() || p.banda() != CamisaShow.SLAYER - 1) throw new AssertionError("escolha errada: " + p.banda());
        System.out.println("TesteCamisaShow OK");
    }
    /** Vermelho de estampa dentro da caixa da camisa do painel 4. */
    private static int vermelhos(BufferedImage b) {
        double[] c = CamisaShow.ESTAMPA[1][3]; int fw = b.getWidth() / 4, n = 0;
        for (int y = (int) (c[1] * b.getHeight()); y < c[3] * b.getHeight(); y++)
            for (int x = 3 * fw + (int) (c[0] * fw); x < 3 * fw + c[2] * fw; x++) {
                int p = b.getRGB(x, y), r = p >> 16 & 255, g = p >> 8 & 255, bl = p & 255;
                if (r > 120 && g < r * .4 && bl < r * .45) n++;
            }
        return n;
    }
}
