package Jogo;

import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;

public class Nave {

	private int dx;
	private int dy;
	private int x;
	private int y;
	private int altura, largura;
	private boolean isVisivel;

	private Image imagem;

	private List<Missel> misseis;

	public Nave() {

		ImageIcon referencia = new ImageIcon("ImagemIcon\\nave2.gif");
		imagem = referencia.getImage();

		altura = imagem.getHeight(null);
		largura = imagem.getHeight(null);

		misseis = new ArrayList<Missel>();

		this.x = 20;
		this.y = 60;

	}

	public void mexer() {

		x += dx;
		y += dy;

		if (this.x < 1) {
			x = 1;
		}

		if (this.x > 462) {
			x = 462;
		}
		if (this.y < -3) {
			y = -3;
		}
		if (this.y > 335) {
			y = 335;
		}

	}

	public List<Missel> getMisseis() {
		return misseis;
	}

	public int getX() {

		return x;

	}

	public int getY() {

		return y;

	}

	public Image getImagem() {
		return imagem;
	}
	public boolean isVisivel() {
		return isVisivel;
	}

	public void setVisivel(boolean isVisivel) {
		this.isVisivel = isVisivel;
	}

	public void atira() {
		this.misseis.add(new Missel(x + largura, y + altura / 2));
	}

	public Rectangle getBounds() {

		return new Rectangle(x, y, largura, altura);

	}

	public void keyPressed(KeyEvent tecla) {

		int codigo = tecla.getKeyCode();

		if (codigo == KeyEvent.VK_SPACE) {
			atira();
		}
		
		if(codigo == KeyEvent.VK_F) {
			
			if(Missel.getTipoMissel() == 0) {
				Missel.setTipoTiro(1);
			} else if (Missel.getTipoMissel() == 1) {
				Missel.setTipoTiro(2);
			} else {
				Missel.setTipoTiro(0);
			}
		}

		if (codigo == KeyEvent.VK_UP) {

			dy = -3;

		}
		if (codigo == KeyEvent.VK_DOWN) {

			dy = 3;

		}
		if (codigo == KeyEvent.VK_LEFT) {

			dx = -3;

		}
		if (codigo == KeyEvent.VK_RIGHT) {

			dx = 3;

		}
	}

	public void KeyReleased(KeyEvent tecla) {

		int codigo = tecla.getKeyCode();

		if (codigo == KeyEvent.VK_UP) {
			dy = 0;
		}
		if (codigo == KeyEvent.VK_DOWN) {

			dy = 0;

		}
		if (codigo == KeyEvent.VK_LEFT) {

			dx = 0;

		}
		if (codigo == KeyEvent.VK_RIGHT) {

			dx = 0;

		}
	}

}
