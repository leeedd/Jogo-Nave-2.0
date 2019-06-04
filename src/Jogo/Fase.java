package Jogo;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.Timer;

//import jogo.Tiro;

@SuppressWarnings("serial")
public class Fase extends JPanel implements ActionListener {

	private Image fundo;
	private Nave nave;
	private Timer timer;

	private boolean emJogo;

	private List<Alien> alien;

	private int[][] coordenadas = new int[120][3];

	public Fase() {

		setFocusable(true);
		setDoubleBuffered(true);
		addKeyListener(new TecladoAdpter());

		ImageIcon referencia = new ImageIcon("ImagemIcon\\fundo.png");
		fundo = referencia.getImage();
		nave = new Nave();

		emJogo = true;
		inicializaInimigos();

		timer = new Timer(4, this);
		timer.start();

	}

	public void inicializaInimigos() {
		
		alien = new ArrayList<Alien>();
		inicializaCoordenadas(coordenadas);
		
		for (int i = 0; i < coordenadas.length; i++) {
			alien.add(new Alien(coordenadas[i][0], coordenadas[i][1]));

		}

	}
	
	private void inicializaCoordenadas(int[][] coordenadas1) {

		for (int i = 0; i < coordenadas1.length; i++) {

			Random random = new Random();

			coordenadas1[i][0] = random.nextInt(1500) + 500;
			coordenadas1[i][1] = random.nextInt(350);

		}

	}


	public void paint(Graphics g) {

		Graphics graficos = (Graphics2D) g;
		graficos.drawImage(fundo, 0, 0, null);

		if (emJogo) {

			graficos.drawImage(nave.getImagem(), nave.getX(), nave.getY(), this);

			List<Missel> misseis = nave.getMisseis();

			for (int i = 0; i < misseis.size(); i++) {

				Missel m = (Missel) misseis.get(i);
				graficos.drawImage(m.getImagem(), m.getX(), m.getY(), this);

			}
			for (int i = 0; i < alien.size(); i++) {

				Alien al = alien.get(i);
				graficos.drawImage(al.getImagem(), al.getX(), al.getY(), this);

			}
			graficos.setColor(Color.WHITE);
			graficos.drawString("VOCÊ MATOU: " + (120 - alien.size()), 5, 10);

		} else {

			ImageIcon fimJogo = new ImageIcon("ImagemIcon\\game_over.jpg");

			graficos.drawImage(fimJogo.getImage(), 0, 0, null);
		}
		g.dispose();

	}

	@Override
	public void actionPerformed(ActionEvent arg0) {

		if (alien.size() == 0) {
			emJogo = false;
		}

		List<Missel> misseis = nave.getMisseis();

		for (int i = 0; i < misseis.size(); i++) {

			Missel m = (Missel) misseis.get(i);

			if (m.isVisible()) {
				m.mexer();
			} else {
				misseis.remove(i);

			}
		}
		for (int i = 0; i < alien.size(); i++) {

			Alien al = alien.get(i);

			if (al.isVisible()) {
				al.mexer();
			} else {
				alien.remove(i);

			}
		}

		nave.mexer();
		checarColisoes();
		repaint();
	}

	public void checarColisoes() {

		Rectangle formaNave = nave.getBounds();
		Rectangle formaInimigo;
		Rectangle formaMissel;

		for (int i = 0; i < alien.size(); i++) {

			Alien tempInimigo = alien.get(i);
			formaInimigo = tempInimigo.getBounds();

			if (formaNave.intersects(formaInimigo)) {

				nave.setVisivel(false);
				tempInimigo.setVisible(false);

				emJogo = false;
			}

		}

		List<Missel> misseis = nave.getMisseis();

		for (int i = 0; i < misseis.size(); i++) {

			Missel tempMissel = misseis.get(i);
			formaMissel = tempMissel.getBounds();

			for (int j = 0; j < alien.size(); j++) {

				Alien tempInimigo = alien.get(j);
				formaInimigo = tempInimigo.getBounds();

				if (formaMissel.intersects(formaInimigo)) {
					
					if (Missel.getTipoMissel() == 2) {
						alien = new ArrayList<Alien>();
						inicializaInimigos();
					}
					
					tempInimigo.setVisible(false);
					tempMissel.setVisible(false);
				}

			}

		}
	}

	private class TecladoAdpter extends KeyAdapter {

		@Override
		public void keyPressed(KeyEvent e) {

			if (e.getKeyCode() == KeyEvent.VK_ENTER) {
				emJogo = true;
				nave = new Nave();
				inicializaInimigos();
			}

			nave.keyPressed(e);
		}

		@Override
		public void keyReleased(KeyEvent e) {

			nave.KeyReleased(e);
		}

	}
}
