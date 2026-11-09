package Jogo;
import java.net.URL;
/** Trilha original do trailer, em loop; duracao do loop nao altera as ondas. */
public final class MusicaJogo {
 private static String atual="";private MusicaJogo(){}
 public static URL localizar(String nome){return MusicaJogo.class.getResource("/Audio098/trilha_metal.wav");}
 public static double duracaoSegundos(String nome){return -1;}
 public static void iniciar(String nome){if(atual.isEmpty())trocarSeDisponivel(nome);}
 /** Troca a musica: toca musicas/arquivo.wav se o jogador tiver o arquivo; senao, a trilha do jogo. */
 public static void trocarSeDisponivel(String nome){if(nome==null||nome.equals(atual))return;atual=nome;SomJogo.iniciar();SomJogo.trocarMusica(arquivo(nome));}
 /** Nome do arquivo na pasta musicas: "MasterOfPuppets" vira "master_of_puppets". */
 static String arquivo(String nome){return nome.replaceAll("([a-z0-9])([A-Z])","$1_$2").toLowerCase();}
 /** Musica de cada show do festival (arquivo musicas/NOME.wav, colocado pelo jogador). Indice 8 = festival completo. */
 static final String[] SHOWS={"show_0","show_1","show_2","show_3","show_4","show_5","show_6","show_7","show_8"};
 /** Toca a musica do show escolhido no final; sem o arquivo, segue a trilha do jogo. */
 /** Gostinho do show escolhido: os primeiros 8 s da musica. */
 static void previaShow(int banda){if(banda>=0&&banda<SHOWS.length)SomJogo.previaMusica(SHOWS[banda],10);}
 static void tocarShow(int banda){if(banda<0||banda>=SHOWS.length)return;atual=SHOWS[banda];SomJogo.iniciar();SomJogo.trocarMusica(SHOWS[banda]);}
 static String getMusicaAtualTeste(){return atual;}
}
