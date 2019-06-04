package Jogo;

import java.awt.Image;
import java.awt.Rectangle;

import javax.swing.ImageIcon;

public class Missel {

	private Image imagem;
	private int x, y;
	private int largura, altura;
	private boolean isVisivel;

	private static final int LARGURA_TELA = 500;
	private static final int VELOCIDADE = 2;

	private static int tipoMissel = 0;

	public Missel(int x, int y) {

		this.x = x;
		this.y = y;

		ImageIcon referencia = null;

		if (tipoMissel == 0) {
			referencia = new ImageIcon("ImagemIcon\\missel1.png");
		} else if (tipoMissel == 1) {
			referencia = new ImageIcon("ImagemIcon\\missel2.png");
		} else {
			referencia = new ImageIcon("ImagemIcon\\missel.jpg");
		}

		imagem = referencia.getImage();

		this.largura = imagem.getWidth(null);
		this.altura = imagem.getHeight(null);

		isVisivel = true;

	}
	public static int getTipoMissel() {
		return tipoMissel;
	}

	public static void setTipoTiro(int tipoMissel) {
		Missel.tipoMissel = tipoMissel;
	}

	public void mexer() {

		this.x += VELOCIDADE;

		if (this.x > LARGURA_TELA) {
			isVisivel = false;
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
