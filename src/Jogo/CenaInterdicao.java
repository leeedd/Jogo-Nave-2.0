package Jogo;

import java.awt.*;
import java.util.Random;

/** Captura, arrasto e saida da dobra; nao aplica dano durante a cinematica. */
final class CenaInterdicao {
 static final double DURACAO=6.2;
 private final int nivel,modelo;
 private final FundoEspacial espaco=new FundoEspacial();
 private double tempo;
 private final boolean rainha;
 CenaInterdicao(Nave nave){this(nave,true);}
 CenaInterdicao(Nave nave,boolean rainha){this.rainha=rainha;SomJogo.limpar();SomJogo.tocar(SomJogo.Efeito.INTERDICAO);
  nivel=nave.getNivel();modelo=nave.getModeloVisual();
  espaco.definirProgresso(0,6);for(int i=0;i<20;i++)espaco.atualizar(.05);
 }
 void atualizar(double dt){double antes=tempo;tempo=Math.min(DURACAO,tempo+Math.max(0,dt));espaco.atualizar(dt);if(antes<3.8&&tempo>=3.8)SomJogo.tocar(SomJogo.Efeito.SAIDA_DOBRA);}
 boolean terminou(){return tempo>=DURACAO;}
 double getTempo(){return tempo;}
 FundoEspacial getFundo(){return espaco;}
 private static double suave(double t){t=Math.max(0,Math.min(1,t));return t*t*(3-2*t);}
 double naveX(){
  if(tempo<.8)return 300;
  if(tempo<2.2)return 300+120*suave((tempo-.8)/1.4);
  if(tempo<3.8)return 420+230*suave((tempo-2.2)/1.6);
  return 650-595*suave((tempo-3.8)/2.2);
 }
 double naveY(){return tempo<3.8?300-52*Math.sin(suave((tempo-.8)/3)*Math.PI):300;}
 void desenhar(Graphics2D original){
  Graphics2D g=(Graphics2D)original.create();g.clipRect(0,72,960,528);
  espaco.desenhar(g);
  float tunel=(float)(1-suave((tempo-3.65)/.8));
  if(tunel>0){
   Graphics2D a=(Graphics2D)g.create();a.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,tunel));
   a.setColor(new Color(3,5,24));a.fillRect(0,72,960,528);
   double colapso=suave((tempo-2.2)/1.6);int cx=700+(int)(Math.sin(tempo*36)*colapso*12),cy=310;
   Random r=new Random(9631);
   for(int i=0;i<420;i++){
    double ang=r.nextDouble()*Math.PI*2,progresso=(r.nextDouble()+tempo*(.28+colapso*.42))%1;
    double dist=15+progresso*progresso*1100;
    double deformacao=1-colapso*.65;
    int x=cx+(int)(Math.cos(ang)*dist),y=cy+(int)(Math.sin(ang)*dist*.6*deformacao);
    a.setColor(i%4==0?new Color(205,190,255):i%3==0?new Color(75,185,240):new Color(67,53,141));
    int tam=2+(int)(progresso*4);a.fillRect(x,y,tam*2,tam);
    if(progresso>.65)a.drawLine(x,y,x+(int)(Math.cos(ang)*30),y+(int)(Math.sin(ang)*18*deformacao));
   }
   for(int anel=0;anel<7;anel++){
    double p=(anel/7.0+tempo*.35)%1,raio=15+p*p*620;
    a.setColor(new Color(150,88,255,80+(int)(p*90)));
    for(int k=0;k<90;k++){
     double ang=k*Math.PI/45+tempo*.8;int x=cx+(int)(Math.cos(ang)*raio),y=cy+(int)(Math.sin(ang)*raio*.58*(1-colapso*.7));
     a.fillRect(x,y,3,3);
    }
   }
   a.dispose();
  }
  int nx=(int)naveX(),ny=(int)naveY();
  int bx=tempo<3.8?900-(int)(160*suave(tempo/1.2)):740;
  if(tempo>.65&&tempo<4.1){
   double forca=Math.sin(Math.min(1,(tempo-.65)/.5)*Math.PI/2);
   g.setColor(rainha?new Color(125,210,95,(int)(forca*40)):new Color(225,130,55,(int)(forca*40)));
   g.fillPolygon(new int[]{bx-25,nx+30,nx+30},new int[]{312,ny-12,ny+58},3);
   for(int fio=0;fio<3;fio++){
    g.setColor(rainha?(fio==1?new Color(215,247,150):new Color(94,172,85)):(fio==1?new Color(230,224,205):new Color(120,130,145)));
    for(int k=0;k<55;k++){
     double p=k/54.0;int x=(int)((nx+26)*(1-p)+(bx-25)*p);
     int y=(int)((ny+19)*(1-p)+312*p+(rainha?Math.sin(p*22-tempo*13+fio*2)*9*Math.sin(p*Math.PI):(fio-1)*3));
     g.fillRect(x,y,4,2);
    }
   }
   if(!rainha){g.setColor(new Color(225,225,210));g.fillPolygon(new int[]{nx+24,nx+42,nx+38},new int[]{ny+20,ny+12,ny+29},3);}
   // Arrasto visivel contra o empuxo: rastro se alonga enquanto a nave perde terreno.
   g.setColor(new Color(79,217,255,125));
   int cauda=32+(int)(24*Math.abs(Math.sin(tempo*24)));
   g.fillPolygon(new int[]{nx-8,nx-cauda,nx-8},new int[]{ny+10,ny+20,ny+30},3);
  }
  Graphics2D nav=(Graphics2D)g.create();
  double giro=tempo>.8&&tempo<3.8?-.12+Math.sin(tempo*21)*.07:Math.sin(tempo*17)*.12*(1-suave((tempo-4.2)/1.8));
  nav.rotate(giro,nx+23,ny+20);
  ArteNaves.desenharJogador(nav,nivel,modelo,nx,ny,tempo);nav.dispose();
  ArteEnxame.comandante(g,bx,312,rainha,tempo,false,0);
  if(tempo>4.4)for(int i=0;i<6;i++){int xx=1020-(int)((tempo-4.4)*110)+i*40;ArteEnxame.criatura(g,i,xx,165+(i%3)*135,tempo);}
  if(tempo>3.7&&tempo<4.25){
   float flash=(float)Math.sin((tempo-3.7)/.55*Math.PI);
   g.setColor(new Color(231,218,255,Math.max(0,Math.min(235,(int)(flash*235)))));g.fillRect(0,72,960,528);
  }
  String estado=tempo<.8?"ASSINATURA HOSTIL":tempo<2.2?"CAMPO DE CAPTURA — MOTORES NO LIMITE":tempo<3.8?"NAVE SENDO ARRASTADA / RUPTURA DA DOBRA":tempo<5.8?"EJECAO PARA O ESPACO NORMAL":"CONTROLE RESTABELECIDO";
  g.setFont(new Font("Monospaced",Font.BOLD,19));g.setColor(new Color(0,0,0,180));g.fillRect(150,112,690,40);
  g.setColor(Color.WHITE);g.drawString(estado,480-g.getFontMetrics().stringWidth(estado)/2,139);
  g.setFont(new Font("Monospaced",Font.BOLD,12));g.setColor(new Color(206,218,243));
  g.drawString("INTERDICAO HOSTIL / COMBATE APOS A SAIDA",330,550);
  g.dispose();
 }
}
