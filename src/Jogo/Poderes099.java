package Jogo;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
final class Poderes099 {
 static final String[] NOMES={"MARE ABISSAL","PAINKILLER","EGIDE DE EDDIE","AS DE LEMMY","RIFF PEM","PENTAGRAMA","PACTO ROTATIVO","GRITO PARANOID"};
 static final String[] DICAS={"Onda congela e perfura","Moto aliada com vida propria","Escudos interceptam tiros","Cometa pesado perfurante","Paralisa mobs e chefes","Zona de fogo persistente","Cristais orbitais de defesa","Onda sonica perfurante"};
 static final int[] CUSTO={25,40,30,25,40,35,30,30};
 static final double[] RECARGA={8,18,14,7,22,12,16,9},DURACAO={1.8,12,8,2.6,3,5,10,2.2};
 private static final BufferedImage[][] ARTE=new BufferedImage[8][4];
 static{try{BufferedImage a=ImageIO.read(Poderes099.class.getResource("/ImagemIcon/poderes_099.png"));for(int r=0;r<8;r++)for(int c=0;c<4;c++){int x=c*a.getWidth()/4,y=r*a.getHeight()/8;ARTE[r][c]=a.getSubimage(x,y,a.getWidth()/4,a.getHeight()/8);}}catch(java.io.IOException e){throw new ExceptionInInitializerError(e);}}
 final int[] energia={100,100,100,100,100,100,100,100};final double[] cooldown=new double[8];int selecionado,ativo=-1,vidaMoto;double tempo,restante,pulso,x,y,recargaPassiva;
 void selecionar(int i,ProgressaoNave p){if(p.temChaveIndice(i))selecionado=i;}
 void proximo(ProgressaoNave p){for(int n=1;n<=8;n++){int i=(selecionado+n)%8;if(p.temChaveIndice(i)){selecionado=i;return;}}}
 boolean ativar(ProgressaoNave p,Nave n){int i=selecionado;if(!p.temChaveIndice(i)||ativo>=0||cooldown[i]>0||energia[i]<CUSTO[i])return false;energia[i]-=CUSTO[i];cooldown[i]=RECARGA[i]*(1-.06*n.motorPermanente());ativo=i;restante=DURACAO[i];tempo=pulso=0;vidaMoto=8;x=n.getX()+95;y=n.getY()+18;if(i==5)x=Math.min(760,x+260);SomJogo.tocar(i==5?SomJogo.Efeito.FOGO:SomJogo.Efeito.FUSAO);return true;}
 int recarregar(ProgressaoNave p){int sobra=20;for(int j=0;j<8;j++){int i=(selecionado+j)%8;if(p.temChaveIndice(i)){int v=Math.min(sobra,100-energia[i]);energia[i]+=v;sobra-=v;}}return 20-sobra;}
 void atualizar(double dt,Nave n){
    for(int i=0;i<8;i++)cooldown[i]=Math.max(0,cooldown[i]-dt);
    recargaPassiva+=dt;
    if(recargaPassiva>=.65){int ganho=(int)(recargaPassiva/.65);recargaPassiva-=ganho*.65;for(int i=0;i<8;i++)energia[i]=Math.min(100,energia[i]+ganho);}
    if(ativo<0)return;tempo+=dt;restante-=dt;pulso-=dt;if(restante<=0){ativo=-1;return;}if(ativo==1){x+=(n.getX()+130-x)*Math.min(1,dt*6);y+=(n.getY()+18-y)*Math.min(1,dt*6);}if(ativo==2||ativo==6||ativo==4){x=n.getX()+23;y=n.getY()+18;}if(ativo==0||ativo==3||ativo==7)x+=dt*(ativo==3?420:360);}
 boolean pulso(){if(ativo<0||pulso>0)return false;pulso=ativo==1?.35:.25;return true;}
 boolean paralisa(){return ativo==4;}
 int dano(){return ativo==3?10:ativo==1?4:ativo==5?3:ativo==0?5:ativo==7?6:2;}
 Rectangle alcance(){if(ativo==4)return new Rectangle(0,80,960,520);if(ativo==1)return new Rectangle((int)x,(int)y-8,Math.max(1,960-(int)x),16);int r=ativo==5?115:ativo==0?85:ativo==3?40:ativo==7?95:70;return new Rectangle((int)x-r,(int)y-r,r*2,r*2);}
 boolean interceptar(Rectangle b){if(ativo!=1&&ativo!=2&&ativo!=6)return false;boolean bate=ativo==1?new Rectangle((int)x-34,(int)y-22,68,44).intersects(b):Math.hypot(b.getCenterX()-x,b.getCenterY()-y)<76;if(bate&&ativo==1&&--vidaMoto<=0){ativo=-1;SomJogo.tocar(SomJogo.Efeito.EXPLOSAO);}return bate;}
 static void sprite(Graphics2D g,int i,int x,int y,int s,double t){g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);g.drawImage(ARTE[Math.max(0,Math.min(7,i))][((int)(Math.max(0,t)*8))%4],x,y,s,s,null);}
 void desenhar(Graphics2D g){if(ativo<0)return;int px=(int)x,py=(int)y;if(ativo==2||ativo==6){for(int j=0;j<4;j++){double a=tempo*3+j*Math.PI/2;sprite(g,ativo,px+(int)(Math.cos(a)*62)-20,py+(int)(Math.sin(a)*62)-20,40,tempo+j*.13);}}else if(ativo==4){sprite(g,ativo,px-48,py-48,96,tempo);g.setColor(new Color(80,215,255,110));int r=(int)((tempo%1)*850);for(int i=0;i<3;i++)g.drawOval(px-r-i*8,py-r-i*8,r*2+i*16,r*2+i*16);}else{int s=ativo==5?230:ativo==1?110:ativo==7?160:120;sprite(g,ativo,px-s/2,py-s/2,s,tempo);if(ativo==1){g.setColor(Color.DARK_GRAY);g.fillRect(px-28,py+35,56,4);g.setColor(Color.CYAN);g.fillRect(px-28,py+35,7*vidaMoto,4);if(pulso>.22)g.fillRect(px+40,py-2,Math.max(0,960-px),4);}}}
}
