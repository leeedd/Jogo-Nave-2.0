package Jogo;
import java.awt.*;
import java.awt.image.BufferedImage;
final class Pausa099 {
 static final Color OURO=new Color(255,206,98),AZUL=new Color(65,219,238),CINZA=new Color(133,158,180);
 static final String[] NAVES={"RAPTOR MK-I","SHADOW","VALKYRIE","BERSERKER","METAL GOD"};
 static final String[] ABAS={"ARSENAL","NAVE","RELIQUIAS","MAPA","MERCADO","SISTEMA"};
 static final String[][] PECAS={{"CHASSI RAPTOR","CANHAO DUPLO","TURBINA RAPTOR"},{"CHASSI SHADOW","AGULHAS GEMEAS","MOTOR FANTASMA"},{"CHASSI VALKYRIE","LEQUE VALKYRIE","REATOR VALKYRIE"},{"CHASSI BERSERKER","OBUS PESADO","TURBINA CERCO"},{"CHASSI METAL GOD","TRIDENTE REAL","REATOR LENDARIO"}};
 static void txt(Graphics2D g,String s,int x,int y,Color c){g.setColor(c);g.setFont(new Font(Font.MONOSPACED,Font.BOLD,9));g.drawString(s,x,y);}
 /** Texto que encolhe a fonte para caber na largura do cartao. */
 static void txtCabe(Graphics2D g,String s,int x,int y,int largura,Color c){int t=9;g.setFont(new Font(Font.MONOSPACED,Font.BOLD,t));while(t>6&&g.getFontMetrics().stringWidth(s)>largura)g.setFont(new Font(Font.MONOSPACED,Font.BOLD,--t));g.setColor(c);g.drawString(s,x,y);}
 static void txtDir(Graphics2D g,String s,int x,int y,Color c){g.setFont(new Font(Font.MONOSPACED,Font.BOLD,9));txt(g,s,x-g.getFontMetrics().stringWidth(s),y,c);}
 static void txtCentro(Graphics2D g,String s,int x,int y,Color c){g.setFont(new Font(Font.MONOSPACED,Font.BOLD,9));txt(g,s,x-g.getFontMetrics().stringWidth(s)/2,y,c);}
 static void box(Graphics2D g,int x,int y,int w,int h,boolean sel){g.setColor(new Color(10,20,34));g.fillRect(x,y,w,h);g.setColor(sel?OURO:new Color(48,76,101));g.drawRect(x,y,w,h);g.setColor(sel?new Color(144,88,35):new Color(20,41,58));g.drawRect(x+1,y+1,w-2,h-2);}
 static void bar(Graphics2D g,int x,int y,int w,int carga,Color c){for(int i=0;i<w/4;i++){g.setColor(i*100<Math.max(0,carga)*(w/4)?c:new Color(32,47,62));g.fillRect(x+i*4,y,3,5);}}
 /** Encaixe selecionado no mercado (0 arma, 1 motor, 2 blindagem, 3 modulo). */
 static int encaixeMercado;
 static int CUSTO_I(int i){return Poderes099.CUSTO[i];}
 static int icone(int i){return new int[]{9,6,8,6,7,10}[i];}
 static void desenhar(Graphics2D tela,int aba,int cursor,ProgressaoNave inv,Nave nave,Energia098 energia,Poderes099 powers,boolean gelo,boolean fogo,double tempo,String aviso){
  BufferedImage b=new BufferedImage(480,300,1);Graphics2D g=b.createGraphics();g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_OFF);g.setColor(new Color(3,7,15));g.fillRect(0,0,480,300);
  for(int x=0;x<480;x+=16){g.setColor(new Color(26,42,59));g.fillRect(x,0,14,5);g.fillRect(x,295,14,5);g.setColor((x+(int)(tempo*8))%48<24?Color.RED:new Color(70,18,27));g.fillRect(x+3,1,6,2);}
  txt(g,"METAL SPACE / SISTEMAS DE BORDO",12,20,OURO);txtDir(g,"CR "+inv.saldo(),466,20,AZUL);for(int i=0;i<6;i++){box(g,12+i*76,29,74,21,i==aba);txt(g,(i+1)+" "+ABAS[i],17+i*76,43,i==aba?OURO:CINZA);}
  int m=nave.getModeloVisual();
  if(aba==0){for(int i=0;i<8;i++){int x=12+i%2*145,y=56+i/2*47;boolean tem=inv.temChaveIndice(i);box(g,x,y,137,42,cursor==i);if(tem)Poderes099.icone(g,i,x+3,y+4,34,tempo);else txt(g,"?",x+16,y+25,CINZA);g.setColor(new Color(10,20,34));g.fillRect(x+2,y+30,9,11);txt(g,""+(i+1),x+4,y+39,tem?Color.WHITE:CINZA);txtCabe(g,tem?Poderes099.NOMES[i]:"DESCONHECIDO",x+41,y+13,92,tem?Color.WHITE:CINZA);HudEnergia.miniBarra(g,x+41,y+20,90,powers.energia[i],CUSTO_I(i),Poderes099.CORES_SIMBOLO[i],tem);txt(g,!tem?"CHEFE NAO VENCIDO":powers.cooldown[i]>0?"RECARGA "+(int)Math.ceil(powers.cooldown[i])+"s":powers.selecionado==i?"EQUIPADO / C":"ESPACO: EQUIPAR",x+41,y+36,tem?OURO:CINZA);}
   box(g,302,56,164,183,false);Poderes099.icone(g,cursor,341,64,86,tempo);txt(g,Poderes099.NOMES[cursor],312,166,OURO);String s=Poderes099.DICAS[cursor];int at=s.lastIndexOf(' ',Math.min(s.length()-1,22));if(s.length()>23){txt(g,s.substring(0,at),312,182,CINZA);txt(g,s.substring(at+1),312,195,CINZA);}else txt(g,s,312,182,CINZA);txt(g,"ENERGIA "+powers.energia[cursor]+"  CUSTO "+Poderes099.CUSTO[cursor],312,214,AZUL);txt(g,"USOS RESTANTES: "+(powers.energia[cursor]/Poderes099.CUSTO[cursor]),312,228,OURO);
   txt(g,"Q GELO",12,256,AZUL);bar(g,52,250,100,gelo?energia.carga(0):0,AZUL);txt(g,"E FOGO",166,256,OURO);bar(g,206,250,100,fogo?energia.carga(1):0,OURO);txt(g,"V FUSAO",320,256,OURO);txtDir(g,"CAPSULAS RECARREGAM",466,256,CINZA);
  }else if(aba==1){box(g,12,56,282,183,false);txt(g,NAVES[m],24,74,OURO);Graphics2D a=(Graphics2D)g.create();a.translate(128,112);ArteNaves.desenharJogador(a,nave.getNivel(),m,0,0,tempo);a.dispose();txt(g,nave.perfil().funcao+"  /  SHIFT: "+Nave.TRUQUES[m],24,182,AZUL);txt(g,Nave.TRUQUES_DICA[m],24,192,CINZA);txt(g,"CASCO "+nave.getVida()+"/"+nave.getVidaMaxima(),24,200,Color.WHITE);bar(g,24,208,258,100*nave.getVida()/nave.getVidaMaxima(),Color.GREEN);txt(g,"DANO "+nave.danoBalistico()+" / EVOLUCAO "+nave.getNivel(),24,230,OURO);txtDir(g,"NUCLEOS DE CASCO "+inv.quantidadeNucleos()+"/8",282,230,new Color(255,110,110));for(int i=0;i<3;i++){txt(g,PECAS[m][i],302,68+i*28,Color.WHITE);bar(g,302,74+i*28,164,100*inv.peca(m,i)/(i==0?8:3),OURO);}txt(g,"RESERVAS / R: REPARAR",302,158,AZUL);for(int i=0;i<4;i++){box(g,302+i*42,166,38,73,false);Arte098.item(g,inv.temTanque(i)?Math.min(3,(inv.carga(i)+32)/33):9,306+i*42,176,30);bar(g,305+i*42,224,32,inv.carga(i),AZUL);}
  }else if(aba==2){for(int i=0;i<8;i++){int x=12+i%4*116,y=56+i/4*94;box(g,x,y,108,88,cursor==i);boolean tem=inv.temChaveIndice(i);Medalhas.desenhar(g,ProgressaoNave.ORDEM[i],tem,x+28,y+6,52,tempo);txtCentro(g,tem?Poderes099.NOMES[i]:"???",x+54,y+76,tem?OURO:CINZA);}txt(g,"RELIQUIAS LIBERAM PODERES PERMANENTES",12,256,AZUL);
  }else if(aba==4){
   // Mercado em encaixes: < > escolhe o encaixe, cima/baixo a peca, ESPACO compra / equipa / desequipa
   int e=encaixeMercado;Color VERDE=new Color(110,230,130),VERM=new Color(240,100,100);
   for(int k=0;k<4;k++){int bx=12+k*114;box(g,bx,56,110,40,k==e);txt(g,Pecas.ENCAIXES[k],bx+6,69,k==e?OURO:CINZA);Pecas q=Pecas.obter(inv.equipada(m,k));if(q!=null&&Pecas.temIcone(q.id)){Pecas.icone(g,q.id,bx+82,59,24);}txtCabe(g,q==null?"VAZIO":q.nome,bx+6,86,98,q==null?CINZA:Color.WHITE);}
   java.util.List<Pecas> lista=Pecas.doEncaixe(e,m);
   for(int i=0;i<lista.size();i++){Pecas q=lista.get(i);int y=102+i*28;box(g,12,y,282,25,cursor==i);boolean tem=inv.temPeca(q.id),eq=inv.equipada(m,e)==q.id;
    int ix=Pecas.temIcone(q.id)?42:20;Pecas.icone(g,q.id,16,y+1,23);
    txtCabe(g,q.nome+(q.nave>=0?"  EXCLUSIVA":""),ix,y+11,262-ix,q.nave>=0?OURO:Color.WHITE);
    txt(g,eq?"EQUIPADA":tem?"COMPRADA":"CR "+q.preco,ix,y+21,eq?VERDE:tem?AZUL:q.preco>inv.saldo()?VERM:OURO);}
   box(g,302,102,164,137,false);
   if(cursor<lista.size()){Pecas q=lista.get(cursor);boolean ic=Pecas.temIcone(q.id);if(ic)Pecas.icone(g,q.id,310,108,40);int tx=ic?356:310;txtCabe(g,q.nome,tx,118,460-tx,OURO);txt(g,q.nave>=0?"SO "+NAVES[q.nave]:"SERVE EM TODAS",tx,131,CINZA);
    txt(g,"+ "+q.bom,310,161,VERDE);txt(g,"- "+q.ruim,310,174,VERM);
    boolean tem=inv.temPeca(q.id),eq=inv.equipada(m,e)==q.id;txt(g,eq?"ESPACO: DESEQUIPAR":tem?"ESPACO: EQUIPAR":"ESPACO: COMPRAR",310,190,AZUL);}
   txt(g,"NAVE ATUAL",310,204,CINZA);txt(g,"DANO "+nave.danoBalistico()+"  CASCO "+nave.getVidaMaxima(),310,217,Color.WHITE);txt(g,"COMPRAS SAO PERMANENTES",310,232,AZUL);
  }else if(aba==5){String[] linhas={"ENTER / ESC: RETOMAR VOO","M: VOLTAR AO MENU INICIAL","F8: LABORATORIO DE TESTES","F9: OPCOES (AUDIO, JOGO, CONTROLES)","COMPRAS SALVAS AUTOMATICAMENTE","ROTA LIVRE: ESCOLHA O SETOR NO MAPA"};box(g,12,56,454,183,false);for(int i=0;i<linhas.length;i++)txt(g,linhas[i],30,82+i*28,i%2==0?Color.WHITE:OURO);}
  if(aviso!=null&&!aviso.isEmpty())txt(g,aviso.substring(0,Math.min(69,aviso.length())),12,267,OURO);txt(g,aba==4?"< >: ENCAIXE  CIMA/BAIXO: PECA  ESPACO: COMPRAR / EQUIPAR  TAB: ABA  ENTER: VOLTAR":aba==0?"C: USAR  X: TROCAR  1-8: ESCOLHER  ESPACO: EQUIPAR  TAB: ABA  ENTER: VOLTAR":"TAB: ABA  SETAS: SELECAO  ESPACO: CONFIRMAR  ENTER: VOLTAR",12,285,CINZA);g.dispose();tela.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);tela.drawImage(b,0,0,960,600,null);
 }
}
