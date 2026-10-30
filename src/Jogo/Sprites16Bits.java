package Jogo;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;

/** Sprites 16 bits derivados das pranchas aprovadas; sem cenarios nos PNGs. */
public final class Sprites16Bits {
    private static final BufferedImage[][] CACHE = new BufferedImage[9][3];
    private static final boolean[][] CONSULTADO = new boolean[9][3];
    private Sprites16Bits() { }

    private static int pasta(int id) {
        switch (id) {
            case 8: return 1; // Lula do Abismo
            case 2: return 2; // Rob Halford
            case 3: return 3; // Bruce Dickinson
            case 4: return 4; // Lemmy
            case 5: return 5; // James
            case 6: return 6; // Tom
            case 7: return 7; // Chuck
            case 1: return 8; // Ozzy, chefe final
            default: return 0;
        }
    }
    public static synchronized BufferedImage carregar(int id, int forma) {
        if (id < 1 || id > 8 || forma < 1 || forma > 2) return null;
        if (CONSULTADO[id][forma]) return CACHE[id][forma];
        CONSULTADO[id][forma] = true;
        String nome = String.format("%02d_forma_%d.png", pasta(id), forma);
        try {
            URL url = Sprites16Bits.class.getResource("/Sprites16Bits/" + nome);
            if (url != null) CACHE[id][forma] = ImageIO.read(url);
            else {
                File f = new File("Sprites16Bits", nome);
                if (!f.isFile()) f = new File("src/Sprites16Bits", nome);
                if (f.isFile()) CACHE[id][forma] = ImageIO.read(f);
            }
        } catch (IOException ex) {
            System.err.println("Sprite nao pode ser lido: " + nome + ": " + ex.getMessage());
        }
        return CACHE[id][forma];
    }
    public static synchronized void preparar(int id, int forma) {
        BufferedImage base = carregar(id, forma);
        if (base == null) throw new IllegalStateException("Arte obrigatoria ausente: " + id + "/" + forma);
        // Musicos: nao deformar anatomia. Novas poses individuais ainda necessarias.
    }

    public static BufferedImage carregarAnimado(int id, int forma, double tempo) {
        preparar(id, forma);
        return id==8?carregar(id,forma):PosesChefes.obter(id,forma,((int)(tempo*2)%4==3?3:0));
    }

    /** Mantem compatibilidade com as chamadas originais da versao 0.5. */
    public static boolean desenhar(Graphics2D g, int id, int forma, int largura,
                                   int altura, double tempo) {
        return desenhar(g, id, forma, largura, altura, tempo, 0);
    }

    /**
     * Oito quadros em repouso, quatro para a reacao ao ataque.
     * progressoGolpe varia de 1.0 (comeco) ate 0 (fim); nao depende do FPS.
     */
    public static boolean desenhar(Graphics2D g, int id, int forma, int largura,
                                   int altura, double tempo, double progressoGolpe) {
        preparar(id, forma);
        int q=progressoGolpe<0?1:progressoGolpe>.25?2:progressoGolpe>0?3:((int)(tempo*2)%4==3?3:0);
        BufferedImage sprite = id==8?carregar(id,forma):PosesChefes.obter(id,forma,q);
        if (sprite == null) return false;
        Graphics2D a = (Graphics2D) g.create();
        a.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        // Ajusta a escala usando o tamanho ORIGINAL, nao a margem transparente
        // dos quadros animados; assim os chefes preservam o mesmo porte da 0.5.
        BufferedImage base = sprite;
        int margem = 0;
        // Escala constante: evita o efeito de lupa pulsante dos quadros anteriores.
        // Arte nova do ato II escala pela altura: o efeito do golpe pode passar da caixa para a esquerda.
        boolean arteNova = forma == 2 && PosesChefes.temNovaArte(id);
        double escala = arteNova ? (altura - 35.0) / base.getHeight()
                : Math.min((largura - 24.0) / base.getWidth(), (altura - 35.0) / base.getHeight());
        int w = Math.max(1, (int)Math.round(sprite.getWidth() * escala));
        int h = Math.max(1, (int)Math.round(sprite.getHeight() * escala));
        int baseW = Math.max(1, (int)Math.round(base.getWidth() * escala));
        int baseH = Math.max(1, (int)Math.round(base.getHeight() * escala));
        int x = (largura - baseW) / 2 - (int)Math.round(margem * escala);
        int y = (altura - baseH) / 2 - (int)Math.round(margem * escala)
                + (int)Math.round(Math.sin(tempo * 1.2) * 2);
        // O jogador fica a esquerda: artes desenhadas olhando para a direita sao espelhadas.
        if (voltadoParaDireita(id, forma)) a.drawImage(sprite, x + w, y, -w, h, null);
        else a.drawImage(sprite, x, y, w, h, null);
        a.dispose();
        return true;
    }

    /** Desenha a imagem do chefe sempre encarando a esquerda, onde fica o jogador. */
    public static void desenharVirado(Graphics2D g, BufferedImage img, int id, int forma, int x, int y, int w, int h) {
        if (voltadoParaDireita(id, forma)) g.drawImage(img, x + w, y, -w, h, null);
        else g.drawImage(img, x, y, w, h, null);
    }

    /** Poses cuja arte original encara a direita (Bruce no ato II e de frente, com a bandeira). */
    static boolean voltadoParaDireita(int id, int forma) {
        if (forma == 1) return id == 1 || id == 2 || id == 3;
        if (forma == 2 && PosesChefes.temNovaArte(id)) return false;
        return forma == 2 && (id == 1 || id == 2 || id == 4 || id == 5 || id == 6);
    }
}
