package Jogo;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.lang.reflect.*;
import java.util.*;
public final class TesteInvasao093 {
    private static int checks;
    static void ok(boolean v,String m){if(!v)throw new AssertionError(m);checks++;}
    static void set(Object o,String n,Object v)throws Exception{Field f=o.getClass().getDeclaredField(n);f.setAccessible(true);f.set(o,v);}
    static Object get(Object o,String n)throws Exception{Field f=o.getClass().getDeclaredField(n);f.setAccessible(true);return f.get(o);}
    static Fase fase(long seed){Fase f=new Fase(seed);f.pararTimerTeste();return f;}
    @SuppressWarnings("unchecked") static java.util.List<ProjetilInimigo> tiros(Fase f)throws Exception{return (java.util.List<ProjetilInimigo>)get(f,"tirosInimigos");}
    static long hash(BufferedImage b){long h=7;for(int y=0;y<b.getHeight();y++)for(int x=0;x<b.getWidth();x++)h=h*31+b.getRGB(x,y);return h;}
    public static void main(String[] args)throws Exception{
        for(int i=0;i<5;i++) {
            Nave n=new Nave();n.definirModeloVisual(i);PerfilNave p=PerfilNave.obter(i);
            ok(n.getVida()==p.casco&&n.getVidaMaxima()==p.casco,"casco real do modelo "+i);
            int x=n.getX();n.keyPressed(new java.awt.event.KeyEvent(new javax.swing.JPanel(),java.awt.event.KeyEvent.KEY_PRESSED,0,0,java.awt.event.KeyEvent.VK_RIGHT,' '));
            n.mexer(.2);n.soltarTeclas();ok(n.getX()-x==(int)(p.velocidade*.2),"velocidade real "+i);
            n.danoDiretoTeste(2);n.curar(100);ok(n.getVida()==p.casco,"reparo limitado ao casco "+i);
            n.ativarEscudo();ok(n.getTempoEscudo()==p.escudo,"duracao do escudo "+i);
            set(n,"escudo",0.0);n.receberDano(4);ok(n.getVida()==p.casco-(4-p.defesa),"blindagem real "+i);
            n.atira();ok(Math.abs((double)get(n,"recarga")-p.intervalo)<.00001,"cadencia real "+i);
            Alien alvo=new Alien(600,300,Alien.Tipo.PIRATA,Alien.Classe.TANQUE);int vida=alvo.getResistencia();alvo.receberImpacto(0,n.danoBalistico());ok(alvo.getResistencia()==vida-p.dano,"dano real "+i);
        }
        for(int arma=0;arma<4;arma++) {
            Fase f=fase(3);f.getAliens().clear();Nave n=f.getNave();
            Alien tanque=new Alien(500,300,Alien.Tipo.PIRATA,Alien.Classe.TANQUE);
            ProjetilInimigo guiado=new ProjetilInimigo(tanque,100,300);tiros(f).add(guiado);Rectangle r=guiado.getBounds();
            if(arma<2)n.getMisseis().add(new Missel(r.x,r.y,arma));
            else {
                @SuppressWarnings("unchecked") java.util.List<DisparoElemental> es=(java.util.List<DisparoElemental>)get(f,"tirosElementais");
                es.add(new DisparoElemental(arma==2?DisparoElemental.Tipo.GELO:DisparoElemental.Tipo.FOGO,r.x,r.y));
            }
            f.checarColisoes();ok(!guiado.isVisible(),"interceptacao com arma "+arma);ok(f.getPontos()==25,"pontuacao de interceptacao");f.checarColisoes();ok(f.getPontos()==25,"nao pontuar duas vezes");
        }
        Set<String> entradas=new HashSet<>();
        for(int seed=0;seed<12;seed++){
            Fase f=fase(seed);Alien primeiro=f.getAliens().get(0);entradas.add(f.formacaoTeste()+":"+get(primeiro,"alvoX")+":"+get(primeiro,"alvoY"));
            int prevista=(int)get(f,"proximaFormacao");f.getAliens().clear();f.simularFormacaoTeste(6);
            ok(f.formacaoTeste()==prevista,"radar deve prever formacao real");
        }
        ok(entradas.size()>9,"entradas diferentes entre partidas");
        Fase f=fase(6);Set<Integer> formas=new HashSet<>();Method lancar=Fase.class.getDeclaredMethod("lancarProximoEsquadrao");lancar.setAccessible(true);
        for(int i=0;i<6;i++){formas.add(f.formacaoTeste());f.getAliens().clear();lancar.invoke(f);}
        ok(formas.size()==6,"saco evita repetir padroes antes de variar");
        for(int v=0;v<5;v++) {
            long[] hashes=new long[4];for(int q=0;q<4;q++){BufferedImage b=new BufferedImage(120,100,2);Graphics2D g=b.createGraphics();AtlasInimigos.organico(g,v,18,16,q/(v==3?10.0:7.0)+.001,false);g.dispose();hashes[q]=hash(b);}
            ok(hashes[0]!=hashes[1]&&hashes[1]!=hashes[2],"quadros distintos alien "+v);
        }
        BufferedImage b=new BufferedImage(200,160,2);Graphics2D g=b.createGraphics();AtlasInimigos.sentinela(g,100,80,0);g.dispose();long idle=hash(b);g=b.createGraphics();g.setComposite(AlphaComposite.Clear);g.fillRect(0,0,200,160);g.setComposite(AlphaComposite.SrcOver);AtlasInimigos.sentinela(g,100,80,2);g.dispose();ok(idle!=hash(b),"sentinela possui pose de carga propria");
        System.out.println("OK Invasao 0.9.3: "+checks+" verificacoes.");
    }
}
