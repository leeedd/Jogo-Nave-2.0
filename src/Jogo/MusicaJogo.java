package Jogo;
import java.net.URL;
/** Trilha original do trailer, em loop; duracao do loop nao altera as ondas. */
public final class MusicaJogo {
 private static String atual="";private MusicaJogo(){}
 public static URL localizar(String nome){return MusicaJogo.class.getResource("/Audio098/trilha_metal.wav");}
 public static double duracaoSegundos(String nome){return -1;}
 public static void iniciar(String nome){if(atual.isEmpty())trocarSeDisponivel(nome);}
 public static void trocarSeDisponivel(String nome){atual=nome;SomJogo.iniciar();}
 static String getMusicaAtualTeste(){return atual;}
}
