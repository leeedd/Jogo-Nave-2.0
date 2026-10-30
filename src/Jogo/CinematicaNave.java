package Jogo;

import java.awt.BasicStroke;
import java.awt.Color;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/** Cenas dirigidas pelo timer do jogo: sem thread adicional ou reparo automatico. */
final class CinematicaNave {
    private static final double TEMPO_DESTRUICAO = 3.8;
    private static final double TEMPO_DOBRA = 4.2;
    private final Random sorteio = new Random(4891);
    private final List<Fragmento> fragmentos = new ArrayList<>();
    private final BufferedImage retrato;
    private final int centroX, centroY;
    private double tempo;
    private final boolean destruicao;
    private final int destino;

    CinematicaNave(Nave nave, boolean destruicao) { this(nave, destruicao, 0); }

    CinematicaNave(Nave nave, boolean destruicao, int destino) {SomJogo.tocar(destruicao?SomJogo.Efeito.DERROTA:SomJogo.Efeito.DOBRA);
        this.destino = Math.max(0, Math.min(7, destino));
        this.destruicao = destruicao;
        centroX = nave.getX() + 23;
        centroY = nave.getY() + 20;
        retrato = new BufferedImage(112, 96, BufferedImage.TYPE_INT_ARGB);
        Graphics2D a = retrato.createGraphics();
        a.translate(40 - nave.getX(), 30 - nave.getY());
        nave.desenharSemPiscar(a);
        a.dispose();
        if (destruicao) {
            for (int linha = 0; linha < 6; linha++) {
                for (int coluna = 0; coluna < 7; coluna++) {
                    int sx = coluna * 15, sy = linha * 15;
                    double angulo = sorteio.nextDouble() * Math.PI * 2;
                    double velocidade = 55 + sorteio.nextDouble() * 185;
                    fragmentos.add(new Fragmento(sx, sy, 15, 15, Math.cos(angulo) * velocidade,
                            Math.sin(angulo) * velocidade - 20, (sorteio.nextDouble() - .5) * 8));
                }
            }
        }
    }

    void atualizar(double dt) { tempo += Math.max(0, dt); }
    boolean terminou() { return tempo >= (destruicao ? TEMPO_DESTRUICAO : TEMPO_DOBRA); }
    double getTempo() { return tempo; }
    int quantidadeFragmentos() { return fragmentos.size(); }

    void desenhar(Graphics2D g) {
        if (destruicao) desenharDestruicao(g);
        else desenharDobra(g);
    }

    private void desenharDestruicao(Graphics2D g) {
        Graphics2D a = (Graphics2D)g.create();
        a.setClip(0, Config.HUD, Config.LARGURA, Config.ALTURA - Config.HUD);
        if (tempo < .78) {
            int tremor = (int)(3 + 9 * tempo);
            int dx = (int)(Math.sin(tempo * 91) * tremor);
            int dy = (int)(Math.cos(tempo * 68) * tremor);
            a.drawImage(retrato, centroX - 63 + dx, centroY - 50 + dy, null);
            for (int i = 0; i < 8; i++) {
                double t = tempo * 11 + i * 2.4;
                int px = centroX + (int)(Math.cos(t) * 23);
                int py = centroY + (int)(Math.sin(t * .7) * 16);
                a.setColor(i % 2 == 0 ? Color.ORANGE : Color.WHITE);
                a.fillRect(px, py, 3 + i % 3, 3 + i % 3);
            }
        } else {
            double movimento = tempo - .78;
            for (Fragmento f : fragmentos) {
                double px = centroX + f.vx * movimento;
                double py = centroY + f.vy * movimento + 32 * movimento * movimento;
                Graphics2D pedaco = (Graphics2D)a.create();
                pedaco.translate(px, py);
                pedaco.rotate(f.rotacao * movimento);
                pedaco.drawImage(retrato, -f.largura/2, -f.altura/2,
                        f.largura/2, f.altura/2, f.x, f.y,
                        f.x + f.largura, f.y + f.altura, null);
                pedaco.dispose();
                if (movimento < 1.6 && (f.x + f.y) % 3 == 0) {
                    a.setColor(new Color(248, 131, 42, Math.max(0, (int)(180 - movimento * 95))));
                    a.fillOval((int)px - 2, (int)py - 2, 4, 4);
                }
            }
            if (movimento < 1.15) {
                int raio = (int)(movimento * 310);
                a.setStroke(new BasicStroke(5));
                a.setColor(new Color(255, 151, 58, Math.max(0, (int)(230 * (1 - movimento / 1.15)))));
                a.drawOval(centroX - raio, centroY - raio, 2*raio, 2*raio);
                if (movimento < .24) {
                    a.setColor(new Color(255, 248, 220, Math.max(0, (int)(155*(1-movimento/.24)))));
                    a.fillOval(centroX-55, centroY-55, 110, 110);
                }
            }
        }
        a.dispose();
    }

    private void desenharDobra(Graphics2D g){
        BufferedImage quadro=new BufferedImage(320,176,BufferedImage.TYPE_INT_RGB);Graphics2D p=quadro.createGraphics();
        p.setColor(new Color(2,3,17));p.fillRect(0,0,320,176);double t=Math.min(1,tempo/TEMPO_DOBRA);int cx=236+(int)(Math.sin(tempo*1.8+destino)*9),cy=88+(int)(Math.cos(tempo*2.1)*7);
        p.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        double zoom=1+t*.28;p.drawImage(Assets.imagem("dobra_094.png"),cx-(int)(236*zoom),cy-(int)(88*zoom),(int)(320*zoom),(int)(176*zoom),null);
        Random r=new Random(94+destino*101);for(int i=0;i<210;i++){double ang=r.nextDouble()*Math.PI*2,fase=(r.nextDouble()+tempo*(.18+t*.7))%1,dist=5+fase*fase*340;int x=cx+(int)(Math.cos(ang)*dist),y=cy+(int)(Math.sin(ang)*dist*.65);p.setColor(i%4==0?new Color(165,189,255):i%3==0?new Color(36,92,189):new Color(85,208,247));int len=1+(int)(fase*14*t);for(int k=0;k<len;k++)p.fillRect(x+(int)(Math.cos(ang)*k),y+(int)(Math.sin(ang)*k),1,1);}
        Color cor=MapaEstelar.CORES[destino];
        p.setColor(new Color(cor.getRed(),cor.getGreen(),cor.getBlue(),48));p.fillRect(0,0,320,176);
        // Aneis fragmentados aceleram e giram: geometrias variam conforme o destino.
        for(int anel=0;anel<9;anel++){
            double fase=(anel/9.0+tempo*(.24+t*.38))%1;
            double raio=4+fase*fase*240;
            int alpha=Math.min(210,(int)(fase*180+20));
            p.setColor(new Color(cor.getRed(),cor.getGreen(),cor.getBlue(),alpha));
            for(int ponto=0;ponto<48;ponto++){
                if((ponto+anel)%5==0)continue;
                double ang=ponto*Math.PI/24+tempo*(.35+destino*.04)+anel*.12;
                int px=cx+(int)(Math.cos(ang)*raio),py=cy+(int)(Math.sin(ang)*raio*.60);
                int lado=1+(int)(fase*3);p.fillRect(px,py,lado,lado);
            }
        }
        int nx=(int)(30+206*Math.pow(t,1.5)),ny=cy+(int)(Math.sin(tempo*3)*3),w=Math.max(2,(int)(40*(1-t*.92))),h=Math.max(2,w*96/112);
        if(t<.93){p.drawImage(retrato,nx-w/2,ny-h/2,w,h,null);}
        if(t>.91){p.setColor(new Color(190,234,255,(int)Math.min(255,(t-.91)*2800)));p.fillRect(0,0,320,176);}p.dispose();Graphics2D a=(Graphics2D)g.create();a.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);a.drawImage(quadro,0,Config.HUD,960,528,null);a.dispose();
    }

    private static final class Fragmento {
        final int x,y,largura,altura;
        final double vx,vy,rotacao;
        Fragmento(int x,int y,int w,int h,double vx,double vy,double rotacao) {
            this.x=x;this.y=y;largura=w;altura=h;this.vx=vx;this.vy=vy;this.rotacao=rotacao;
        }
    }
}
