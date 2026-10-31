package Jogo;
import java.util.prefs.Preferences;
/** Inventario persistente; copia isolada para testes. Um registro por transacao. */
final class ProgressaoNave {
 private final Preferences prefs;
 private int pecasCompradas,nucleos,saldo,tanques,celulas,chaves,blindagem,gelo,fogo,fusao;
 private final int[] carga=new int[4];
 private boolean retencao,reator;
 private final int[][] pecas=new int[5][3];
 /** Peca equipada em cada encaixe de cada nave (-1 vazio). */
 private final int[][] equipadas={{-1,-1,-1,-1},{-1,-1,-1,-1},{-1,-1,-1,-1},{-1,-1,-1,-1},{-1,-1,-1,-1}};
 static final int[] ORDEM={8,2,3,4,5,6,7,1};
 ProgressaoNave(){this(Preferences.userNodeForPackage(ProgressaoNave.class).node("progressao098"));for(int i=0;i<8;i++)if(ProgressoJogo.temTrofeu(ORDEM[i]))chaves|=1<<i;}
 ProgressaoNave(Preferences p){prefs=p;if(p!=null)try{ler(p.get("perfil",""));}catch(SecurityException ignored){}}
 ProgressaoNave copiaTeste(){ProgressaoNave c=new ProgressaoNave(null);c.ler(serializar());return c;}
 private String serializar(){String base= saldo+";"+tanques+";"+celulas+";"+chaves+";"+blindagem+";"+gelo+";"+fogo+";"+(retencao?1:0)+";"+(reator?1:0)+";"+fusao+";"+carga[0]+";"+carga[1]+";"+carga[2]+";"+carga[3];StringBuilder b=new StringBuilder(base);for(int[] nave:pecas)for(int n:nave)b.append(';').append(n);b.append(';').append(nucleos);b.append(';').append(pecasCompradas);b.append(';').append(cargasAmuleto);for(int[] e:equipadas)for(int v:e)b.append(';').append(v);return b.toString();}
 private void ler(String s){try{String[] a=s.split(";");if(a.length<14)return;int[] v=new int[14];for(int i=0;i<14;i++)v[i]=Integer.parseInt(a[i]);saldo=lim(v[0],0,9999999);tanques=v[1]&15;celulas=lim(v[2],0,99);chaves=v[3]&255;blindagem=lim(v[4],0,3);gelo=lim(v[5],0,3);fogo=lim(v[6],0,3);retencao=v[7]==1;reator=v[8]==1;fusao=lim(v[9],0,8);for(int i=0;i<4;i++)carga[i]=temTanque(i)?lim(v[10+i],0,100):0;for(int m=0;m<5;m++)for(int k=0;k<3;k++)if(a.length>14+m*3+k)pecas[m][k]=lim(Integer.parseInt(a[14+m*3+k]),0,k==0?8:3);if(a.length>29)nucleos=Integer.parseInt(a[29])&255;if(a.length>30)pecasCompradas=Integer.parseInt(a[30]);if(a.length>31)cargasAmuleto=lim(Integer.parseInt(a[31]),1,CARGAS_AMULETO);for(int m=0;m<5;m++)for(int k=0;k<4;k++)if(a.length>32+m*4+k){int pv=Integer.parseInt(a[32+m*4+k]);Pecas p=Pecas.obter(pv);equipadas[m][k]=p!=null&&p.encaixe==k&&p.serveEm(m)&&temPeca(pv)?pv:-1;}}catch(NumberFormatException ignored){}}
 private static int lim(int n,int min,int max){return Math.max(min,Math.min(max,n));}
 private void salvar(){if(prefs!=null)try{prefs.put("perfil",serializar());prefs.flush();}catch(java.util.prefs.BackingStoreException|SecurityException e){System.err.println("Nao foi possivel persistir inventario: "+e.getMessage());}}
 /** Nucleos de Casco: um escondido em cada setor, +2 de casco maximo cada (permanente). */
 /** Amuleto do Metal God: aguenta 3 golpes que roubariam uma arma, depois se despedaca e precisa ser comprado de novo. */
 static final int CARGAS_AMULETO=3; private int cargasAmuleto=CARGAS_AMULETO;
 int cargasAmuleto(){return retencao?cargasAmuleto:0;}
 /** Gasta uma carga; devolve true se o amuleto acabou de quebrar. */
 boolean desgastarAmuleto(){if(!retencao)return false;if(--cargasAmuleto>0){salvar();return false;}retencao=false;cargasAmuleto=CARGAS_AMULETO;salvar();return true;}
 boolean temPeca(int id){return id>=0&&id<31&&(pecasCompradas&(1<<id))!=0;}
 int equipada(int modelo,int encaixe){return equipadas[lim(modelo,0,4)][lim(encaixe,0,3)];}
 int[] equipamento(int modelo){return equipadas[lim(modelo,0,4)].clone();}
 /** Compra (uma vez) e ja equipa; se ja tem: equipa, ou desequipa se ja estava no encaixe. */
 String usarPeca(int modelo,int id){Pecas p=Pecas.obter(id);if(p==null||!p.serveEm(modelo))return "PECA INCOMPATIVEL";
  if(!temPeca(id)){if(saldo<p.preco)return "CREDITOS INSUFICIENTES";saldo-=p.preco;pecasCompradas|=1<<id;equipadas[modelo][p.encaixe]=id;salvar();return "COMPRADA E EQUIPADA: "+p.nome;}
  if(equipadas[modelo][p.encaixe]==id){equipadas[modelo][p.encaixe]=-1;salvar();return "DESEQUIPADA: "+p.nome;}
  equipadas[modelo][p.encaixe]=id;salvar();return "EQUIPADA: "+p.nome;}
 boolean temNucleo(int setor){return setor>=0&&setor<8&&(nucleos&(1<<setor))!=0;}
 int quantidadeNucleos(){return Integer.bitCount(nucleos);}
 boolean coletarNucleo(int setor){if(setor<0||setor>7||temNucleo(setor))return false;nucleos|=1<<setor;salvar();return true;}
 int saldo(){return saldo;}int celulas(){return celulas;}int blindagem(){return blindagem;}int gelo(){return gelo;}int fogo(){return fogo;}int fusao(){return fusao;}boolean retencao(){return retencao;}boolean reator(){return reator;}
 boolean temTanque(int i){return i>=0&&i<4&&(tanques&(1<<i))!=0;}
 int quantidadeTanques(){return Integer.bitCount(tanques);}int carga(int i){return i>=0&&i<4?carga[i]:0;}
 boolean descobrirTanque(int i){if(i<0||i>=4||temTanque(i))return false;tanques|=1<<i;salvar();return true;}
 /** Deposita automaticamente nos reservatorios encontrados, em ordem. Retorna sobra. */
 int abastecerAutomatico(int porcentagem){int sobra=Math.max(0,porcentagem);for(int i=0;i<4&&sobra>0;i++)if(temTanque(i)){int n=Math.min(100-carga[i],sobra);carga[i]+=n;sobra-=n;}if(sobra!=porcentagem)salvar();return sobra;}
 boolean reservaDisponivel(){for(int i=0;i<4;i++)if(temTanque(i)&&carga[i]<100)return true;return false;}
 boolean coletarCelula(){if(celulas>=99)return false;celulas++;salvar();return true;}
 boolean abastecer(int i){if(!temTanque(i)||celulas==0||carga[i]>=100)return false;celulas--;carga[i]=Math.min(100,carga[i]+25);salvar();return true;}
 int usar(int i,int vida,int maxima){if(!temTanque(i)||carga[i]==0||vida>=maxima)return 0;int pontos=Math.min(maxima-vida,(int)Math.ceil(maxima*carga[i]/100.0));int gasto=Math.min(carga[i],(int)Math.ceil(100.0*pontos/maxima));carga[i]-=gasto;salvar();return pontos;}
 int primeiroCarregado(){for(int i=0;i<4;i++)if(carga[i]>0)return i;return -1;}
 boolean gastar(int n){if(n<=0||saldo<n)return false;saldo-=n;salvar();return true;}
 void creditar(int n){if(n<=0)return;saldo=(int)Math.min(9999999L,(long)saldo+n);salvar();}
 void registrarChave(int chefe){for(int i=0;i<8;i++)if(ORDEM[i]==chefe)chaves|=1<<i;salvar();}
 boolean temChaveIndice(int i){return i>=0&&i<8&&(chaves&(1<<i))!=0;}
 boolean setorLiberado(int s){if(s<0||s>7)return false;int requeridas=(1<<s)-1;return (chaves&requeridas)==requeridas;}
 boolean fusaoLiberada(int n){return n==0||n>=1&&n<=8&&temChaveIndice(n-1);}
 void selecionarFusao(int n){if(fusaoLiberada(n)){fusao=n;salvar();}}
 void coletarReator(){reator=true;salvar();}
 void registrarElemental(boolean geloColetado,boolean fogoColetado){if(!retencao)return;gelo=Math.max(gelo,geloColetado?1:0);fogo=Math.max(fogo,fogoColetado?1:0);salvar();}
 int preco(int produto){switch(produto){case 0:return retencao?0:3000;case 1:return gelo>=3?0:new int[]{400,800,1400}[gelo];case 2:return fogo>=3?0:new int[]{400,800,1400}[fogo];case 3:return blindagem>=3?0:new int[]{450,900,1600}[blindagem];case 4:return reator?0:15000;case 5:return quantidadeTanques()>=4?0:PRECO_TANQUE[quantidadeTanques()];default:return 0;}}
 /** Subtanques vendidos no mercado (ate 4): chegam vazios e enchem em combate com o reparo que sobra. */
 static final int[] PRECO_TANQUE={1500,2500,4000,6000};
 boolean comprar(int produto){int custo=preco(produto);if(custo<=0||saldo<custo)return false;saldo-=custo;switch(produto){case 0:retencao=true;break;case 1:gelo++;break;case 2:fogo++;break;case 4:reator=true;break;case 3:blindagem++;break;case 5:{int t=0;while(temTanque(t))t++;tanques|=1<<t;carga[t]=0;break;}default:return false;}salvar();return true;}
 int peca(int m,int t){return pecas[lim(m,0,4)][lim(t,0,2)];}
 int precoMercado(int m,int t){if(t<0||t>5)return 0;if(t>=3)return preco(t==3?1:t==4?2:3);int n=peca(m,t);return n>=(t==0?8:3)?0:t==0?PRECO_EVOLUCAO[n]:450+350*n;}
 /** Evolucao da nave no mercado: os niveis 3 e 6 trocam o estagio da nave e custam mais. */
 static final int[] PRECO_EVOLUCAO={400,700,1600,1000,1400,3200,2000,2600};
 boolean comprarMercado(int m,int t){if(m<0||m>4||t<0||t>5)return false;if(t>=3)return comprar(t==3?1:t==4?2:3);int v=precoMercado(m,t);if(v==0||saldo<v)return false;saldo-=v;pecas[m][t]++;salvar();return true;}
 void equiparTeste(){for(int i=0;i<4;i++){tanques|=1<<i;carga[i]=100;}retencao=reator=true;gelo=fogo=3;blindagem=3;chaves=255;celulas=12;saldo=10000;}
}
