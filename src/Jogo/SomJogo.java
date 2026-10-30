package Jogo;
import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.concurrent.*;
import java.util.prefs.Preferences;
import javax.sound.sampled.*;
public final class SomJogo {
 public enum Efeito {
  TIRO(.35f,1,35),DISPERSAO(.4f,1,65),NUCLEAR(.55f,3,120),GELO(.4f,1,80),FOGO(.4f,1,90),LASER(.4f,2,100),INIMIGO(.16f,0,80),CHEFE_TIRO(.3f,2,120),
  IMPACTO(.3f,1,55),DANO(.65f,4,150),EXPLOSAO(.5f,2,70),EXPLOSAO_GRANDE(.75f,5,250),DERROTA(.75f,5,500),FUSAO(.65f,5,300),
  COLETA(.6f,3,80),ENERGIA(.55f,3,80),REPARO(.6f,3,100),SUBTANQUE(.65f,4,150),RELIQUIA(.7f,5,300),EVOLUCAO(.65f,4,150),ESCUDO(.5f,3,100),ESCUDO_QUEBRA(.6f,4,150),
  TROCA(.45f,2,80),MENU(.3f,1,65),CONFIRMAR(.45f,2,100),VOLTAR(.4f,2,100),ERRO(.35f,2,200),PAUSA(.4f,3,100),RETOMAR(.4f,3,100),COMPRA(.6f,4,150),
  ALERTA(.4f,4,2000),CHEFE_ENTRADA(.7f,5,1000),DOBRA(.6f,4,500),SAIDA_DOBRA(.6f,4,500),INTERDICAO(.7f,5,500),TROVAO(.7f,5,100),VITORIA(.7f,5,500);
  final float volume;final int prioridade,intervalo;final String arquivo;
  Efeito(float v,int p,int i){volume=v;prioridade=p;intervalo=i;arquivo=name().toLowerCase(Locale.ROOT);}
  boolean ui(){return this==MENU||this==CONFIRMAR||this==VOLTAR||this==ERRO||this==PAUSA||this==RETOMAR||this==COMPRA;}
 }
 private static final MotorAudio MOTOR=new MotorAudio();
 private static final Map<Efeito,float[]> DADOS=new EnumMap<Efeito,float[]>(Efeito.class);
 private static final ArrayBlockingQueue<Pedido> FILA=new ArrayBlockingQueue<Pedido>(64);
 private static final long[] ULTIMO=new long[Efeito.values().length],CONTAGEM=new long[Efeito.values().length];
 private static final Preferences PREFS=Preferences.userNodeForPackage(SomJogo.class).node("audio098");
 private static volatile int musica=Math.max(0,Math.min(100,PREFS.getInt("musica",55))),efeitos=Math.max(0,Math.min(100,PREFS.getInt("efeitos",80)));
 private static volatile boolean mudo=PREFS.getBoolean("mudo",false),iniciado,pausado;
 private static volatile String diagnostico="AUDIO INICIANDO";
 private static final boolean OFFLINE=Boolean.getBoolean("metalspace.audio.offline");
 private SomJogo(){}
 public static int musica(){return musica;}public static int efeitos(){return efeitos;}public static boolean mudo(){return mudo;}public static String diagnostico(){return OFFLINE?"AUDIO OFFLINE":diagnostico;}
 public static void volumeMusica(int v){musica=Math.max(0,Math.min(100,v));PREFS.putInt("musica",musica);}
 public static void volumeEfeitos(int v){efeitos=Math.max(0,Math.min(100,v));PREFS.putInt("efeitos",efeitos);}
 public static void alternarMudo(){mudo=!mudo;PREFS.putBoolean("mudo",mudo);}
 public static synchronized void iniciar(){if(iniciado||OFFLINE)return;iniciado=true;Thread t=new Thread(SomJogo::executar,"MetalSpace-Audio");t.setDaemon(true);t.start();}
 public static void tocar(Efeito e){tocar(e,480);}
 public static synchronized void tocar(Efeito e,double x){if(pausado&&!e.ui())return;long now=System.nanoTime();int i=e.ordinal();if(now-ULTIMO[i]<e.intervalo*1000000L)return;ULTIMO[i]=now;CONTAGEM[i]++;if(OFFLINE)return;iniciar();FILA.offer(new Pedido(e,(float)Math.max(-.8,Math.min(.8,(x-480)/600)),now));}
 public static void pausar(boolean p){if(pausado!=p){pausado=p;MOTOR.pausar(p);if(p)FILA.clear();}}
 public static void limpar(){FILA.clear();MOTOR.limpar();}
 static long emitidosTeste(Efeito e){return CONTAGEM[e.ordinal()];}
 static void resetEventosTeste(){Arrays.fill(ULTIMO,0);Arrays.fill(CONTAGEM,0);pausar(false);limpar();}
 static float[] carregar(URL url,int limite)throws Exception{
  if(url==null)throw new IOException("Recurso de audio ausente");AudioFormat fmt=new AudioFormat(44100,16,1,true,false);
  try(AudioInputStream origem=AudioSystem.getAudioInputStream(url);AudioInputStream in=AudioSystem.getAudioInputStream(fmt,origem);ByteArrayOutputStream bytes=new ByteArrayOutputStream()){
   byte[] b=new byte[8192];int n;while((n=in.read(b))!=-1){if(bytes.size()+n>limite)throw new IOException("Audio muito longo");bytes.write(b,0,n);}b=bytes.toByteArray();float[] a=new float[b.length/2];for(int i=0;i<a.length;i++)a[i]=(short)((b[i*2]&255)|(b[i*2+1]<<8))/32768f;return a;}
 }
 private static void executar(){SourceDataLine line=null;try{
  for(Efeito e:Efeito.values())DADOS.put(e,carregar(SomJogo.class.getResource("/Audio098/"+e.arquivo+".wav"),2097152));
  MOTOR.musica(carregar(SomJogo.class.getResource("/Audio098/trilha_metal.wav"),33554432));
  AudioFormat fmt=new AudioFormat(44100,16,2,true,false);line=(SourceDataLine)AudioSystem.getLine(new DataLine.Info(SourceDataLine.class,fmt));line.open(fmt,8192);line.start();diagnostico="AUDIO ATIVO";
  byte[] b=new byte[2048];while(!Thread.currentThread().isInterrupted()){Pedido p;while((p=FILA.poll())!=null)if(System.nanoTime()-p.quando<250000000L)MOTOR.emitir(DADOS.get(p.e),p.e.volume,p.pan,p.e.prioridade,p.e.ui());MOTOR.renderizar(b,512,musica/100f,efeitos/100f,mudo,1);int off=0;while(off<b.length){int n=line.write(b,off,b.length-off);if(n<=0)throw new IOException("Saida interrompida");off+=n;}}
 }catch(Exception e){diagnostico="AUDIO INDISPONIVEL NESTE DISPOSITIVO";System.err.println(diagnostico+": "+e);}finally{FILA.clear();if(line!=null){line.stop();line.close();}}}
 private static final class Pedido{final Efeito e;final float pan;final long quando;Pedido(Efeito e,float p,long q){this.e=e;pan=p;quando=q;}}
}
