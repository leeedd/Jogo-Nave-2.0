package Jogo;

import java.awt.Color;
import java.awt.Graphics2D;

/** Pecas de pixel art ancoradas ao casco: acompanham inclinacao e animacao da nave. */
final class EvolucaoVisual {
    private EvolucaoVisual() { }

    static void desenhar(Graphics2D original, int modelo, int nivel, int x, int y,
                         double tempo, double disparo, boolean frente) {
        if (nivel <= 0) return;
        Graphics2D g = (Graphics2D) original.create();
        g.translate(x, y);
        Color cor = ArteNaves.corModelo(modelo);
        int recuo = disparo > .35 ? 2 : 0;
        // Cada modelo preserva sua proporcao e sua cor, inclusive nas pecas novas.
        int asa = modelo == 1 ? 3 : modelo == 3 ? 7 : 5;
        if (!frente) {
            if (nivel >= 4) {
                placa(g, new int[]{0, 12, 29, 16, -6}, new int[]{9, -7-asa, -9-asa, 9, 16}, cor);
                placa(g, new int[]{0, 12, 29, 16, -6}, new int[]{30, 46+asa, 48+asa, 30, 23}, cor);
                rebite(g, 13, -3-asa); rebite(g, 13, 42+asa);
            }
            if (nivel >= 2) {
                motor(g, -11, 3, tempo, cor, nivel);
                motor(g, -11, 31, tempo+.3, cor, nivel);
            }
            if (nivel >= 7) {
                placa(g, new int[]{-13,-7,8,4,-5}, new int[]{2,-9,-15,-1,5}, cor);
                placa(g, new int[]{-13,-7,8,4,-5}, new int[]{37,48,54,40,34}, cor);
            }
        } else {
            if (nivel >= 1) {
                placa(g, new int[]{8,18,28,24,10}, new int[]{7,4,8,13,13}, cor);
                placa(g, new int[]{8,18,28,24,10}, new int[]{32,35,31,26,26}, cor);
                rebite(g, 14, 8); rebite(g, 14, 29);
            }
            if (nivel >= 3) {
                canhao(g, 22-recuo, 2, cor, disparo, false);
                canhao(g, 22-recuo, 32, cor, disparo, false);
            }
            if (nivel >= 5) {
                placa(g, new int[]{29,38,51,39,30}, new int[]{14,12,19,25,24}, cor);
                g.setColor(new Color(213,230,238));g.fillRect(36,17,7,2);
                g.setColor(new Color(16,28,42));g.fillRect(33,20,9,2);
            }
            if (nivel >= 6) {
                canhao(g, 18-recuo, -6, cor, disparo, true);
                canhao(g, 18-recuo, 40, cor, disparo, true);
            }
            if (nivel >= 8) {
                placa(g, new int[]{-1,5,17,23,16,4}, new int[]{17,12,12,20,27,27}, cor);
                g.setColor(new Color(10,19,29));g.fillRect(4,16,12,8);
                g.setColor(cor.darker());g.fillRect(6,17,8,6);
                g.setColor(Math.sin(tempo*7)>0?new Color(224,251,255):cor);
                g.fillRect(8,18,4,4);
                rebite(g,2,18);rebite(g,18,18);
            }
        }
        g.dispose();
    }

    private static void placa(Graphics2D g, int[] x, int[] y, Color cor) {
        g.setColor(new Color(8,12,21));g.fillPolygon(x,y,x.length);
        g.setColor(new Color(133,153,166));g.drawPolygon(x,y,x.length);
        int[] internoX=x.clone(),internoY=y.clone();int cx=0,cy=0;
        for(int i=0;i<x.length;i++){cx+=x[i];cy+=y[i];}cx/=x.length;cy/=x.length;
        for(int i=0;i<x.length;i++){internoX[i]=(x[i]*3+cx)/4;internoY[i]=(y[i]*3+cy)/4;}
        g.setColor(cor.darker().darker());g.fillPolygon(internoX,internoY,x.length);
        g.setColor(cor.darker());g.drawPolyline(internoX,internoY,3);
    }

    private static void rebite(Graphics2D g,int x,int y){
        g.setColor(new Color(5,10,16));g.fillRect(x-1,y-1,4,4);
        g.setColor(new Color(197,207,211));g.fillRect(x,y,2,1);
    }

    private static void canhao(Graphics2D g,int x,int y,Color cor,double tiro,boolean pesado){
        int comprimento=pesado?29:24;
        g.setColor(new Color(8,12,20));g.fillRect(x,y,comprimento,6);
        g.setColor(new Color(158,171,185));g.fillRect(x+2,y+1,comprimento-3,2);
        g.setColor(new Color(48,59,74));g.fillRect(x+2,y+3,comprimento-3,2);
        g.setColor(cor.darker());g.fillRect(x+3,y+2,7,3);
        g.setColor(new Color(17,22,31));
        for(int i=12;i<comprimento-2;i+=4)g.fillRect(x+i,y+1,1,4);
        if(tiro>.5){g.setColor(new Color(224,252,255));g.fillRect(x+comprimento,y+2,4,2);}
    }

    private static void motor(Graphics2D g,int x,int y,double t,Color cor,int nivel){
        int chama=5+(int)(3*(1+Math.sin(t*23)))+(nivel>=8?4:0);
        g.setColor(cor.darker());g.fillRect(x-chama,y+2,chama,5);
        g.setColor(cor);g.fillRect(x-chama+3,y+3,chama-3,3);
        g.setColor(new Color(222,248,255));g.fillRect(x-4,y+4,5,1);
        placa(g,new int[]{x,x+12,x+18,x+12,x},new int[]{y,y,y+4,y+8,y+8},cor);
        g.setColor(new Color(186,204,215));g.fillRect(x+1,y+2,2,5);
        g.setColor(new Color(15,22,32));g.fillRect(x+5,y+3,6,3);
    }
}
