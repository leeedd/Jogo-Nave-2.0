package Jogo;
import java.awt.GraphicsEnvironment;
import java.io.*;
import java.net.URL;
import java.util.jar.*;
import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
/** Entrada do pacote: verifica assets e registra falhas para o teste externo. */
public final class AlphaLauncher {
 private AlphaLauncher(){}
 public static void main(String[] args){
  Thread.setDefaultUncaughtExceptionHandler((t,e)->falha(e));
  try{
   if(args.length>0&&"--verificar".equals(args[0])){verificar();return;}
   Inicializador.main(args);
  }catch(Throwable e){falha(e);System.exit(1);}
 }
 private static void falha(Throwable e){e.printStackTrace();if(!GraphicsEnvironment.isHeadless())JOptionPane.showMessageDialog(null,"Falha no alpha. Envie o arquivo ultimo-jogo.log da pasta\n%LOCALAPPDATA%/MetalSpaceAlpha/logs\n\n"+e.getClass().getSimpleName()+": "+e.getMessage(),"Metal Space - diagnostico",JOptionPane.ERROR_MESSAGE);}
 static void verificar()throws Exception{
  int imagens=0,audios=0;
  URL local=AlphaLauncher.class.getProtectionDomain().getCodeSource().getLocation();
  try(JarFile jar=new JarFile(new File(local.toURI()))){java.util.Enumeration<JarEntry> entries=jar.entries();while(entries.hasMoreElements()){JarEntry ent=entries.nextElement();String n=ent.getName();
   if(n.endsWith(".png")||n.endsWith(".jpg")||n.endsWith(".gif")){try(InputStream in=jar.getInputStream(ent)){if(ImageIO.read(in)==null)throw new IOException("Imagem invalida: "+n);imagens++;}}
   if(n.startsWith("Audio098/")&&n.endsWith(".wav")){SomJogo.carregar(AlphaLauncher.class.getResource("/"+n),33554432);audios++;}
  }}
  for(int id=1;id<=8;id++)for(int forma=1;forma<=2;forma++)Sprites16Bits.carregarAnimado(id,forma,0);
  Class.forName("Jogo.ArteEnxame");Class.forName("Jogo.TelaInicial");Class.forName("Jogo.Fase");
  if(audios!=38||imagens<100)throw new IOException("Pacote incompleto");
  System.out.println("OK PACOTE: "+imagens+" imagens, "+audios+" audios, sprites dos 8 chefes e classes acessiveis pelo JAR.");
 }
}
