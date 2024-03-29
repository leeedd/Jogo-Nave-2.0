package Jogo;

import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.net.URL;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JSeparator;
import javax.swing.UIManager;

@SuppressWarnings("serial")
public class Janelas extends JFrame {

	public Janelas() {

		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
		} catch (Exception e) {
			// erro
		}

		this.addWindowListener(new WindowListener() {

			@Override
			public void windowOpened(WindowEvent e) {
				
				play("HammerFall");
			}

			@Override
			public void windowIconified(WindowEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void windowDeiconified(WindowEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void windowDeactivated(WindowEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void windowClosing(WindowEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void windowClosed(WindowEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void windowActivated(WindowEvent e) {
				// TODO Auto-generated method stub

			}
		});

		JMenuBar barraMenu = new JMenuBar();
		JMenu file = new JMenu("File");

		JMenuItem instrucoes = new JMenuItem("Instructions");
		instrucoes.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {

				JOptionPane.showMessageDialog(null,
						"Bem vindo ao Dead Space. \nDestrua os inimigos para sobreviver no espa�o e voltar para casa. "
								+ "\nComandos. Utilize as setas para desviar dos alienig�nas."
								+ "\nBarra de espa�o faz a nave disparar o missel."
								+ "\n'F' muda para 3 tipos de armas diferentes. Missel 1, Missel 2 e a Bomba Nuclear"
								+ "\nDivirta-se. ",
						"Informa��es", JOptionPane.INFORMATION_MESSAGE);
			}
		});

		JMenuItem sobre = new JMenuItem("Info");
		sobre.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {

				JOptionPane.showMessageDialog(null, "Jogo desenvolvido por Fl�vio Prado!", "Informa��es",
						JOptionPane.INFORMATION_MESSAGE);

			}
		});

		JMenuItem sair = new JMenuItem("Exit");

		sair.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				System.exit(0);
			}
		});
		file.add(instrucoes);
		file.add(new JSeparator());
		file.add(sobre);
		file.add(new JSeparator());
		file.add(sair);

		barraMenu.add(file);

		setJMenuBar(barraMenu);

		add(new Fase());
		setTitle("Dead Space");
		setSize(500, 420);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
		setVisible(true);
	}

	public void play(String HammerFall) {

		URL url = getClass().getResource(HammerFall + ".wav");
		AudioClip audio = Applet.newAudioClip(url);
		audio.play();
		audio.loop();

	}

	public static void main(String[] args) {
		new Janelas();

	}

}