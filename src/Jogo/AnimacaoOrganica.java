package Jogo;

import java.awt.image.BufferedImage;

/** Deformacao local da textura aprovada. Centro do corpo fixo; sem zoom global.
 * Os quadros sao preparados uma vez por forma, nunca dentro do loop de desenho.
 * Nao substitui a textura por formas geometricas nem depende do relogio de parede.
 */
final class AnimacaoOrganica {
    static final int MARGEM = 12;
    private final BufferedImage[] repouso = new BufferedImage[24];
    private final BufferedImage[] ataque = new BufferedImage[8];
    private final BufferedImage[] preparo = new BufferedImage[4];

    AnimacaoOrganica(BufferedImage base, int artista, int forma) {
        int w = base.getWidth(), h = base.getHeight();
        // x, y, raio, amplitude x/y. Coordenadas relativas a cada silhueta.
        double[][] pontos;
        if (forma == 2) {
            pontos = new double[][]{
                {.51,.18,.25,.006,.014}, // cabeca: inclinacao e cabelo
                {.24,.55,.21,-.010,.018}, // mao esquerda
                {.79,.56,.24,.012,-.016}, // mao/instrumento
                {.18,.80,.25,.009,.012}, // capa/cabelo com atraso
                {.84,.82,.23,-.008,.011}
            };
        } else if (artista == 1 || artista == 6) {
            pontos = new double[][]{
                {.12,.27,.36,.008,.043}, {.88,.27,.36,-.008,-.043},
                {.50,.60,.17,0,.014}, {.30,.72,.18,-.010,.013}, {.70,.72,.18,.010,.013}
            };
        } else if (artista == 3) {
            pontos = new double[][]{
                {.50,.76,.22,0,.025}, {.14,.34,.25,-.014,.013},
                {.87,.36,.25,.014,-.013}, {.50,.21,.17,.004,.006}
            };
        } else if (artista == 7) {
            pontos = new double[][]{
                {.13,.38,.16,0,.025}, {.88,.42,.17,0,-.024},
                {.30,.78,.16,-.008,.016}, {.71,.79,.17,.008,-.016}
            };
        } else {
            pontos = new double[][]{
                {.85,.52,.21,.016,.008}, {.20,.44,.17,-.004,.009},
                {.48,.19,.18,.004,.007}, {.74,.77,.18,.009,-.007}
            };
        }
        int ow=w+MARGEM*2, oh=h+MARGEM*2;
        float[][] pesos = new float[pontos.length][ow*oh];
        for(int y=0;y<oh;y++) for(int x=0;x<ow;x++) {
            double nx=(x-MARGEM)/(double)w, ny=(y-MARGEM)/(double)h;
            for(int k=0;k<pontos.length;k++) {
                double[] p=pontos[k];
                double r2=((nx-p[0])*(nx-p[0])+(ny-p[1])*(ny-p[1]))/(p[2]*p[2]);
                pesos[k][y*ow+x]=(float)Math.exp(-r2*2.5);
            }
        }
        int[] original=base.getRGB(0,0,w,h,null,0,w);
        for(int q=0;q<24;q++) repouso[q]=quadro(original,w,h,pontos,pesos,q*Math.PI*2/24,0);
        for(int q=0;q<8;q++) ataque[q]=quadro(original,w,h,pontos,pesos,0,Math.sin(q*Math.PI/7));
        for(int q=0;q<4;q++) preparo[q]=quadro(original,w,h,pontos,pesos,0,-(q+1)/4.0);
    }

    private BufferedImage quadro(int[] original,int w,int h,double[][] pontos,float[][] pesos,double fase,double golpe) {
        int ow=w+MARGEM*2, oh=h+MARGEM*2;
        int[] pixels=new int[ow*oh];
        double[] dx=new double[pontos.length],dy=new double[pontos.length];
        for(int k=0;k<pontos.length;k++) {
            double onda=Math.sin(fase+k*.8);
            double acao=golpe*(k==1||k==2?2.7:1.1);
            dx[k]=w*pontos[k][3]*(onda+acao);
            dy[k]=h*pontos[k][4]*(onda-acao);
        }
        for(int y=0;y<oh;y++) for(int x=0;x<ow;x++) {
            int i=y*ow+x; double sx=x-MARGEM, sy=y-MARGEM;
            for(int k=0;k<pontos.length;k++) {sx-=pesos[k][i]*dx[k];sy-=pesos[k][i]*dy[k];}
            int ix=(int)Math.round(sx),iy=(int)Math.round(sy);
            if(ix>=0&&iy>=0&&ix<w&&iy<h) pixels[i]=original[iy*w+ix];
        }
        BufferedImage out=new BufferedImage(ow,oh,BufferedImage.TYPE_INT_ARGB);
        out.setRGB(0,0,ow,oh,pixels,0,ow);return out;
    }

    BufferedImage quadro(double tempo,double gesto) {
        if(gesto<0) return preparo[Math.min(3,(int)(-gesto*4))];
        if(gesto>0) return ataque[Math.min(7,(int)((1-Math.min(1,gesto))*8))];
        return repouso[Math.floorMod((int)Math.floor(tempo*12),24)];
    }
}
