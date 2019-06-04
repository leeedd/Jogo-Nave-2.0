package Jogo;

import java.awt.Image;
import java.awt.Rectangle;

import javax.swing.ImageIcon;

public class Alien {

	private Image imagem;
	private int x, y;
	private int largura, altura;
	private boolean isVisivel;

	private static final int LARGURA_TELA = 500;
	private static final int VELOCIDADE = 1;

	private static int contador = 0;

	public Alien(int x, int y) {

		this.x = x;
		this.y = y;

		ImageIcon referencia;

		if (contador++ % 3 == 0) {

			referencia = new ImageIcon("ImagemIcon\\inimigo_2.gif");

		} else if (contador++ % 4 == 0) {
			referencia = new ImageIcon("ImagemIcon\\inimigo_1.gif");

		} else {
			referencia = new ImageIcon("ImagemIcon\\inimigo_3.png");
		}

		imagem = referencia.getImage();

		this.largura = imagem.getWidth(null);
		this.altura = imagem.getHeight(null);

		isVisivel = true;

	}

	public void mexer() {

		if (this.x < 0) {
			this.x = LARGURA_TELA;
		} else {
			this.x -= VELOCIDADE;
		}

	}

	public boolean isVisible() {
		return isVisivel;
	}

	public void setVisible(boolean isVisible) {
		this.isVisivel = isVisible;
	}

	public Image getImagem() {
		return imagem;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public Rectangle getBounds() {

		return new Rectangle(x, y, largura, altura);

	}

}
