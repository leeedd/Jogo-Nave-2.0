package Jogo;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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

		JMenuBar barraMenu = new JMenuBar();
		JMenu file = new JMenu("File");
		
		
		JMenuItem instrucoes = new JMenuItem("Instructions");
		instrucoes.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				JOptionPane.showMessageDialog(null, "Bem vindo ao Dead Space. \nDestrua os inimigos para sobreviver no espaço e voltar para casa. "
						+ "\nComandos. Utilize as setas para desviar dos alienigênas."
						+ "\nBarra de espaço faz a nave disparar o missel."
						+ "\n'F' muda para 3 tipos de armas diferentes. Missel 1, Missel 2 e a Bomba Nuclear"
						+ "\nDivirta-se. ", "Informações", JOptionPane.INFORMATION_MESSAGE);
			}
		});

		JMenuItem sobre = new JMenuItem("Info");
		sobre.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				JOptionPane.showMessageDialog(null, "Jogo desenvolvido por Flávio Prado!", "Informações", JOptionPane.INFORMATION_MESSAGE);
				
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

	public static void main(String[] args) {
		new Janelas();

	}

}