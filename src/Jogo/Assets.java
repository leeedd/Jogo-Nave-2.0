package Jogo;

import java.awt.Image;
import java.net.URL;
import java.io.File;
import javax.swing.ImageIcon;

/** Carrega as imagens originais do classpath (JAR) ou da pasta ImagemIcon. */
public final class Assets {
    private static final java.util.Map<String, Image> CACHE = new java.util.HashMap<>();
    private Assets() { }

    public static synchronized Image imagem(String nome) {
        Image existente = CACHE.get(nome);
        if (existente != null) return existente;
        URL recurso = Assets.class.getResource("/ImagemIcon/" + nome);
        if (recurso != null) {
            Image imagem = new ImageIcon(recurso).getImage();
            CACHE.put(nome, imagem);
            return imagem;
        }
        File arquivo = new File("ImagemIcon", nome);
        if (!arquivo.isFile()) arquivo = new File("src/ImagemIcon", nome);
        if (arquivo.isFile()) {
            Image imagem = new ImageIcon(arquivo.getPath()).getImage();
            CACHE.put(nome, imagem);
            return imagem;
        }
        throw new IllegalStateException("Imagem do jogo nao encontrada: " + nome);
    }
}
