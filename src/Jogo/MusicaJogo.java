package Jogo;
import java.net.URL;
/** Trilha original do trailer, em loop; duracao do loop nao altera as ondas. */
public final class MusicaJogo {
 private static String atual="";private MusicaJogo(){}
 public static URL localizar(String nome){return MusicaJogo.class.getResource("/Audio098/trilha_metal.wav");}
 public static double duracaoSegundos(String nome){return -1;}
 public static void iniciar(String nome){if(atual.isEmpty())trocarSeDisponivel(nome);}
 public static void trocarSeDisponivel(String nome){atual=nome;SomJogo.iniciar();}
 /** Musica de cada show do festival (arquivo musicas/NOME.wav, colocado pelo jogador). Indice 8 = festival completo. */
 static final String[] SHOWS={"crias_do_abismo","painkiller","the_trooper","ace_of_spades","master_of_puppets","raining_blood","parabellum","crazy_train","festival_completo"};
 /** Toca a musica do show escolhido no final; sem o arquivo, segue a trilha do jogo. */
 static void tocarShow(int banda){if(banda<0||banda>=SHOWS.length)return;atual=SHOWS[banda];SomJogo.iniciar();SomJogo.trocarMusica(SHOWS[banda]);}
 static String getMusicaAtualTeste(){return atual;}
}
