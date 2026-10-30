package Jogo;
import java.awt.Graphics2D;
/** Efeito visual finito; nao reaplica dano nem concede recompensas. */
final class DerrotaEnxame {
 final int especie,x,y;double tempo;
 DerrotaEnxame(Alien a){especie=a.especieEnxame();x=a.getX();y=a.getY();}
 void atualizar(double dt){tempo+=Math.max(0,dt);}
 boolean terminou(){return tempo>=1.25;}
 void desenhar(Graphics2D g){ArteEnxame.derrota(g,especie,x,y,tempo);}
}
