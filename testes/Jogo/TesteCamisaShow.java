package Jogo;

import java.awt.image.BufferedImage;

/** O final comeca na escolha do show, e a camisa preta do piloto ganha o emblema da banda escolhida. */
public class TesteCamisaShow {
    public static void main(String[] a) throws Exception {
        CenaPouso p = new CenaPouso(0);
        if (!p.escolhendo()) throw new AssertionError("o final deve comecar na escolha do show");
        p.mover(-1); p.confirmar();
        if (p.escolhendo() || p.banda() != CamisaShow.SLAYER - 1) throw new AssertionError("escolha errada: " + p.banda());
        BufferedImage tira = javax.imageio.ImageIO.read(CenaPouso.class.getResource("/ImagemIcon/final_flavio_sprites.png"));
        BufferedImage com = CenaPouso.estampar(tira, CamisaShow.emblema(1));
        int fw = tira.getWidth() / 12, mudou = 0;
        for (int y = 0; y < tira.getHeight(); y++) for (int x = 6 * fw; x < 7 * fw; x++) if ((tira.getRGB(x, y) & 0xFFFFFF) != (com.getRGB(x, y) & 0xFFFFFF) && (tira.getRGB(x, y) >>> 24) > 200) mudou++;
        if (mudou < 300) throw new AssertionError("a camisa deveria ganhar o emblema: " + mudou);
        for (int x = 0; x < fw; x++) for (int y = 0; y < tira.getHeight(); y++) if (tira.getRGB(x, y) != com.getRGB(x, y) && (tira.getRGB(x, y) >>> 24) > 200) throw new AssertionError("de macacao nao tem estampa");
        for (int i = 0; i < 400; i++) p.atualizar(.1);
        if (!p.terminou()) throw new AssertionError("a cutscene deveria terminar");
        System.out.println("TesteCamisaShow OK");
    }
}
