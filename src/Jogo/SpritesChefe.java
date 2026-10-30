package Jogo;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

/**
 * Carrega a arte do chefao sem alterar os sprites originais.
 * Aceita PNG, tira horizontal (4 quadros), GIF animado e atlas com 2 personagens.
 */
public final class SpritesChefe {
    private SpritesChefe() { }

    public static BufferedImage[] carregar(int fase) {
        String prefixo = "chefao_" + fase;
        for (String nome : new String[]{prefixo + ".gif", prefixo + ".png"}) {
            BufferedImage[] quadros = carregarFrames(nome);
            if (quadros.length > 0) return quadros;
        }
        BufferedImage[] atlas = carregarFrames("chefoes.png");
        if (atlas.length == 1) {
            BufferedImage origem = atlas[0];
            if (origem.getWidth() >= origem.getHeight() * 1.35) {
                int meio = origem.getWidth() / 2;
                return new BufferedImage[]{origem.getSubimage((fase - 1) * meio, 0, meio, origem.getHeight())};
            }
            if (origem.getHeight() >= origem.getWidth() * 1.35) {
                int meio = origem.getHeight() / 2;
                return new BufferedImage[]{origem.getSubimage(0, (fase - 1) * meio, origem.getWidth(), meio)};
            }
        }
        // Caso o PNG enviado ainda nao tenha sido instalado, preserva o inimigo original.
        return carregarFrames(fase == 1 ? "inimigo_1.gif" : "inimigo_2.gif");
    }

    private static InputStream abrir(String nome) throws IOException {
        InputStream recurso = SpritesChefe.class.getResourceAsStream("/ImagemIcon/" + nome);
        if (recurso != null) return recurso;
        for (String pasta : new String[]{"ImagemIcon", "src/ImagemIcon"}) {
            File arquivo = new File(pasta, nome);
            if (arquivo.isFile()) return new java.io.FileInputStream(arquivo);
        }
        return null;
    }

    private static BufferedImage[] carregarFrames(String nome) {
        try (InputStream in = abrir(nome)) {
            if (in == null) return new BufferedImage[0];
            if (!nome.toLowerCase().endsWith(".gif")) {
                BufferedImage imagem = ImageIO.read(in);
                if (imagem == null) return new BufferedImage[0];
                // Uma faixa horizontal com exatamente 4 quadros quadrados.
                if (imagem.getWidth() == imagem.getHeight() * 4) {
                    BufferedImage[] quadros = new BufferedImage[4];
                    for (int i = 0; i < 4; i++) {
                        quadros[i] = imagem.getSubimage(i * imagem.getHeight(), 0,
                                imagem.getHeight(), imagem.getHeight());
                    }
                    return quadros;
                }
                return new BufferedImage[]{imagem};
            }
            try (ImageInputStream dados = ImageIO.createImageInputStream(in)) {
                java.util.Iterator<ImageReader> leitores = ImageIO.getImageReaders(dados);
                if (!leitores.hasNext()) return new BufferedImage[0];
                ImageReader leitor = leitores.next();
                try {
                    leitor.setInput(dados, false, false);
                    int quantidade = Math.min(leitor.getNumImages(true), 24);
                    List<BufferedImage> quadros = new ArrayList<>();
                    for (int i = 0; i < quantidade; i++) {
                        BufferedImage quadro = leitor.read(i);
                        if (quadro != null) quadros.add(quadro);
                    }
                    return quadros.toArray(new BufferedImage[0]);
                } finally {
                    leitor.dispose();
                }
            }
        } catch (IOException | RuntimeException erro) {
            System.err.println("Nao foi possivel carregar " + nome + ": " + erro.getMessage());
            return new BufferedImage[0];
        }
    }
}
