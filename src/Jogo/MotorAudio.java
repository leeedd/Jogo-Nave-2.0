package Jogo;
import java.util.ArrayList;
/** Mixer limitado, sem alocacao de clips durante os tiros. */
final class MotorAudio {
 private final ArrayList<Voz> vozes=new ArrayList<Voz>();
 private float[] musica;private int pos;private boolean pausa;private float vm,ve;private int duck;
 synchronized void musica(float[] a){musica=a;pos=0;}
 synchronized void pausar(boolean p){pausa=p;if(p)limpar();}
 synchronized void limpar(){vozes.clear();}
 synchronized int vozes(){return vozes.size();}
 synchronized boolean emitir(float[] a,float volume,float pan,int prioridade,boolean ui){
  if(a==null||(pausa&&!ui))return false;
  if(vozes.size()>=24){int menor=0;for(int i=1;i<vozes.size();i++)if(vozes.get(i).prioridade<vozes.get(menor).prioridade)menor=i;
   if(vozes.get(menor).prioridade>prioridade)return false;vozes.remove(menor);}
  vozes.add(new Voz(a,volume,pan,prioridade));if(prioridade>=4)duck=17640;return true;
 }
 synchronized void renderizar(byte[] b,int frames,float musicaVol,float efeitosVol,boolean mute,float ambiente){
  for(int i=0;i<frames;i++){
   vm+=(musicaVol-vm)*.002f;ve+=(efeitosVol-ve)*.002f;float m=0;
   if(musica!=null&&musica.length>0){m=musica[pos++]*vm*ambiente*(pausa?.2f:1)*(duck>0?.55f:1);if(pos==musica.length)pos=0;}
   if(duck>0)duck--;float l=m,r=m;
   for(int j=vozes.size()-1;j>=0;j--){Voz v=vozes.get(j);float s=v.a[v.pos++]*ve;l+=s*v.l;r+=s*v.r;if(v.pos==v.a.length)vozes.remove(j);}
   short left=mute?0:(short)(Math.tanh(l)*30000),right=mute?0:(short)(Math.tanh(r)*30000);
   int off=i*4;b[off]=(byte)left;b[off+1]=(byte)(left>>8);b[off+2]=(byte)right;b[off+3]=(byte)(right>>8);
  }
 }
 private static final class Voz{final float[] a;final float l,r;final int prioridade;int pos;
  Voz(float[] a,float vol,float pan,int p){this.a=a;prioridade=p;pan=Math.max(-.8f,Math.min(.8f,pan));l=vol*(float)Math.sqrt((1-pan)/2);r=vol*(float)Math.sqrt((1+pan)/2);}}
}
