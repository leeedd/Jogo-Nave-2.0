/*
 * Decompiled with CFR 0.152.
 */
package Jogo;


import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;

public final class MiniChefe {
    private double x = 1060.0;
    private double y = 305.0;
    private double tempo;
    private double carga = 2.4;
    private int vida;
    private int vidaMaxima;
    private int raioAlvo;
    private boolean carregando;
    private boolean disparando;
    private boolean atingiu;
    private double relogioRaio;
    private final int setor;
    private final boolean invasor;
    private ComandanteEnxame comandante;
    boolean invasor(){return invasor;}
    boolean derrotado(){return comandante!=null && comandante.terminou();}
    boolean morrendo(){return comandante!=null && comandante.morrendo();}
    int estagio(){return comandante==null?0:comandante.estagio();}
    /** Integrante da banda que a sentinela representa (provisorio ate a arte de cada um chegar). */
    private String integrante="";
    /** Bateristas e baixistas com arte propria atiram projeteis em vez do raio (pratos e ondas graves). */
    private final java.util.List<TiroChefe> projeteis = new java.util.ArrayList<>();
    private char instrumento() { return AtlasInimigos.integrante(integrante) == null ? 'C' : Turne.instrumento(integrante); }
    /** Ataque proprio do integrante com arte: padrao, projetil e cor (Turne.ATAQUES). */
    private String[] ataque() { return AtlasInimigos.integrante(integrante) == null ? null : Turne.ataque(integrante); }
    private String padrao() { String[] a = ataque(); return a == null ? "RAIO" : a[0]; }
    private Color corAtaque() { String[] a = ataque(); return a == null ? null : new Color(Integer.parseInt(a[2]), Integer.parseInt(a[3]), Integer.parseInt(a[4])); }
    private boolean atiraProjetil() { String p = padrao(); return !p.startsWith("RAIO") && !p.equals("VARRE"); }
    private double emissao; private int naveYAlvo;
    private TiroChefe proj(double x0, double y0, double ang, double vel, TiroChefe.Movimento m) {
        String[] a = ataque(); String e = a == null ? "ENERGIA" : a[1];
        int estilo = e.equals("NOTA") ? TiroChefe.NOTA : e.equals("PRATO") ? TiroChefe.PRATO : e.equals("ONDA") ? TiroChefe.ONDA_SOM : e.equals("AS") ? TiroChefe.AS_ESPADAS : TiroChefe.ENERGIA;
        TiroChefe t = new TiroChefe(x0, y0, Math.cos(ang) * vel, Math.sin(ang) * vel, false, estilo, m).comCor(corAtaque()); projeteis.add(t); return t;
    }
    /** Disparo de abertura do padrao (os padroes continuos emitem em atualizar). */
    private void abrirAtaque(int ny) {
        double ox = this.x - 60, oy = this.y, mira = Math.atan2(ny + 18 - oy, 120 - ox);
        switch (padrao()) {
            case "LEQUE": for (int i = -2; i <= 2; i++) proj(ox, oy, mira + i * .2, 300, TiroChefe.Movimento.RETO); break;
            case "LEQUE7": for (int i = -3; i <= 3; i++) proj(ox, oy, mira + i * .16, 330, TiroChefe.Movimento.RETO); break;
            case "PAREDE": { int buraco = (ny - 90) / 60; for (int j = 0; j < 8; j++) if (Math.abs(j - buraco) > 0) proj(ox, 110 + j * 60, Math.PI, 230, TiroChefe.Movimento.RETO); break; }
            case "CHUVA": for (int j = 0; j < 8; j++) proj(140 + j * 80, 92, Math.PI / 2 + (j % 2 == 0 ? .12 : -.12), 250, TiroChefe.Movimento.ACELERADO); break;
            case "ANEL": for (int j = 0; j < 12; j++) proj(ox + 40, oy, j * Math.PI / 6, 220, TiroChefe.Movimento.RETO); break;
            case "ZIGUE": for (int i = -1; i <= 1; i++) proj(ox, oy + i * 60, Math.PI, 260, TiroChefe.Movimento.ONDA); break;
            case "CRUZ": for (int j = 0; j < 3; j++) { proj(ox - j * 30, 100, Math.atan2(ny + 18 - 100, 120 - ox), 320, TiroChefe.Movimento.RETO); proj(ox - j * 30, 540, Math.atan2(ny + 18 - 540, 120 - ox), 320, TiroChefe.Movimento.RETO); } break;
            default: break; // ESPIRAL e RAJADA saem aos poucos, durante o disparo
        }
    }
    java.util.List<TiroChefe> projeteisTeste() { return projeteis; }
    void definirIntegrante(String nome,int risco){integrante=nome==null?"":nome;vida=vidaMaxima=(int)Math.round(vidaMaxima*Turne.premio(risco));}
    String nome(){if(banda!=null)return "BANDA REUNIDA";if(comandante==null&&!integrante.isEmpty())return integrante;return comandante==null?"SENTINELA":comandante.rainha?"RAINHA DO ENXAME":"PIRATA DOMADOR";}
    ComandanteEnxame comandanteTeste(){return comandante;}
    boolean receberImpacto(Rectangle r,int dano){
        if(banda!=null){for(MiniChefe m:vivos())if(m.getBounds().intersects(r)){m.receberMembro(dano);return vivos().isEmpty();}return receberDano(dano);}
        return comandante==null?receberDano(dano):comandante.impacto(r,dano);}
    private void receberMembro(int dano){vida=Math.max(0,vida-Math.max(0,dano));if(vida==0)SomJogo.tocar(SomJogo.Efeito.EXPLOSAO_GRANDE);}
    MiniChefe(int n,boolean invasor,boolean rainha){this(n,invasor);if(invasor)comandante=new ComandanteEnxame(rainha,n);}

    public MiniChefe(int n) { this(n, false); }

    MiniChefe(int n, boolean invasor) {SomJogo.tocar(SomJogo.Efeito.CHEFE_ENTRADA);
        this.invasor = invasor;
        if(invasor)comandante=new ComandanteEnxame(true,n);
        this.setor = n;
        this.vida = this.vidaMaxima = 24 + n * 7;
    }

    void posicionarInterdicao() { x=740; y=314; }

    // ---- banda reunida (fortaleza): varios integrantes ao mesmo tempo, cada um com o seu ataque ----
    private java.util.List<MiniChefe> banda;
    private double casaX = 700, casaY = 305, amplitude = 82;
    /** Membro da banda reunida: fica no proprio posto e atira de la, sem descer ate a nave. */
    private boolean membro;
    /** Todos os integrantes do setor juntos, espalhados pela direita da tela. */
    static MiniChefe banda(int setor) {
        MiniChefe g = new MiniChefe(setor + 1); g.banda = new java.util.ArrayList<>(); g.setorBanda = Turne.setor(setor);
        boolean vocal = !Turne.VOCALISTAS[Turne.setor(setor)].isEmpty();
        int n = Turne.integrantes(setor) + (vocal ? 1 : 0);
        for (int k = 0; k < n; k++) {
            // o vocalista fica no meio da banda, o mais protegido
            int meio = n / 2; boolean eVocal = vocal && k == meio; int idx = vocal && k > meio ? k - 1 : k;
            MiniChefe m = new MiniChefe(setor + 1, "membro"); m.definirIntegrante(eVocal ? Turne.VOCALISTAS[Turne.setor(setor)] : Turne.integrante(setor, idx), eVocal ? 4 : 3);
            m.casaX = 830 - (k % 2) * 230; m.casaY = n == 1 ? 330 : 205 + k * 270.0 / (n - 1); m.amplitude = 60.0 / n; m.x = 1060 + k * 60; m.y = m.casaY;
            m.carga = 1.5 + k * .7; g.banda.add(m);
        }
        return g;
    }
    boolean bandaReunida() { return banda != null; }
    /** Ataque combinado da banda reunida: aviso pelo emblema atras da banda. */
    private static final double COMBO = 1.7;
    private double combo, proximoCombo = 7;
    private int setorBanda;
    boolean comboTeste() { return combo > 0; }
    private static final java.util.Map<Integer, java.awt.image.BufferedImage> EMBLEMAS = new java.util.HashMap<>();
    private static java.awt.image.BufferedImage emblema(int s) {
        return EMBLEMAS.computeIfAbsent(s, k -> { java.net.URL u = MiniChefe.class.getResource("/ImagemIcon/emblema_banda_" + k + ".png"); if (u == null) return null; try { return javax.imageio.ImageIO.read(u); } catch (java.io.IOException e) { return null; } });
    }
    /** Emblema atras da banda: arte emblema_banda_N.png ou, sem ela, o logo desenhado em pixel art (o M da Metallica, as iniciais das outras). */
    private void desenharEmblema(Graphics2D tela) {
        double t = 1 - combo / COMBO; float a = (float) Math.min(1, t * 3) * (float) (.75 + .25 * Math.sin(tempo * 30));
        Rectangle b = getBounds(); int cx = (int) b.getCenterX(), cy = (int) b.getCenterY(), h = (int) (230 + t * 60);
        Graphics2D g = (Graphics2D) tela.create(); g.setComposite(java.awt.AlphaComposite.SrcOver.derive(Math.max(0, Math.min(1, a))));
        java.awt.image.BufferedImage arte = emblema(setorBanda);
        if (arte != null) { int w = arte.getWidth() * h / arte.getHeight(); g.drawImage(arte, cx - w / 2, cy - h / 2, w, h, null); g.dispose(); return; }
        Color[] cores = {new Color(60, 220, 255), new Color(255, 140, 40), new Color(80, 220, 255), new Color(255, 70, 40), new Color(120, 180, 255), new Color(230, 30, 30), new Color(90, 240, 120), new Color(170, 80, 255)};
        Color c = cores[Math.max(0, Math.min(7, setorBanda))];
        // brilho atras
        for (int k = 4; k > 0; k--) { g.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), 22)); g.fillOval(cx - h * k / 4, cy - h * k / 5, h * k / 2, h * k * 2 / 5); }
        if (setorBanda == 4) {
            // o M da Metallica: pernas com pontas de raio, em blocos de pixel
            int u = h / 14;
            int[][] pts = {{-7,-4},{-5,-5},{-4,2},{0,-3},{4,2},{5,-5},{7,-4},{5,5},{6,6},{3,6},{3,2},{0,5},{-3,2},{-3,6},{-6,6},{-5,5}};
            int[] xs = new int[pts.length], ys = new int[pts.length];
            for (int i = 0; i < pts.length; i++) { xs[i] = cx + pts[i][0] * u; ys[i] = cy + pts[i][1] * u; }
            g.setColor(c.darker().darker()); g.translate(u / 2, u / 2); g.fillPolygon(xs, ys, xs.length); g.translate(-u / 2, -u / 2);
            g.setColor(c); g.fillPolygon(xs, ys, xs.length);
            g.setColor(new Color(235, 250, 255)); g.setStroke(new java.awt.BasicStroke(Math.max(2, u / 3f))); g.drawPolygon(xs, ys, xs.length);
        } else {
            String[] ini = {"ABISMO", "JP", "IM", "MH", "M", "S", "T", "BS"};
            g.setFont(new Font(Font.SERIF, Font.BOLD, h * 2 / 3)); String txt = ini[Math.max(0, Math.min(7, setorBanda))];
            int w = g.getFontMetrics().stringWidth(txt);
            g.setColor(c.darker().darker()); g.drawString(txt, cx - w / 2 + 6, cy + h / 4 + 6);
            g.setColor(c); g.drawString(txt, cx - w / 2, cy + h / 4);
        }
        g.dispose();
    }
    private java.util.List<MiniChefe> vivos() { java.util.List<MiniChefe> l = new java.util.ArrayList<>(); for (MiniChefe m : banda) if (m.vida > 0) l.add(m); return l; }
    /** Construtor silencioso para membros da banda (sem o som de entrada repetido). */
    private MiniChefe(int n, String membro) { this.membro = true; this.invasor = false; this.setor = n; this.vida = this.vidaMaxima = 24 + n * 7; }

    public void atualizar(double d, int n) {
        if(comandante!=null){comandante.atualizar(d,n);return;}
        if(banda!=null){
            // ataque combinado: o emblema da banda acende atras dela e todos carregam e disparam juntos
            double dd=Math.max(0,Math.min(.05,d));tempo+=dd;
            if(combo>0){combo=Math.max(0,combo-dd);}
            else{proximoCombo-=dd;if(proximoCombo<=0&&vivos().size()>=2){combo=COMBO;proximoCombo=11+Math.random()*3;SomJogo.tocar(SomJogo.Efeito.CHEFE_ENTRADA);
                for(MiniChefe m:vivos()){m.raioAlvo=(int)m.y;m.carregando=true;m.disparando=false;m.relogioRaio=COMBO;m.atingiu=false;}}}
            for(MiniChefe m:vivos())m.atualizar(d,n);return;}
        d = Math.max(0.0, Math.min(0.05, d));
        this.tempo += d;
        for (TiroChefe t : projeteis) t.mexer(d);
        projeteis.removeIf(t -> !t.isVisible());
        this.x += (casaX - this.x) * (1.0 - Math.exp(-1.7 * d));
        double destino = (this.carregando || this.disparando) && !membro ? this.raioAlvo
                : casaY + Math.sin(this.tempo * 1.15) * amplitude;
        double deslocamento = (destino - this.y) * (1.0 - Math.exp(-4.5 * d));
        this.y += Math.max(-260 * d, Math.min(260 * d, deslocamento));
        if (!this.carregando && !this.disparando) {
            this.carga -= d;
            if (this.carga <= 0.0) {
                this.raioAlvo = membro ? (int) this.y : Math.max(172, Math.min(505, n + 18));
                this.carregando = true;SomJogo.tocar(SomJogo.Efeito.ALERTA);
                this.relogioRaio = 1.05;
                this.atingiu = false;
            }
        } else if (this.carregando) {
            this.relogioRaio -= d;
            if (this.relogioRaio <= 0.0) {
                this.carregando = false;
                this.raioAlvo = (int)Math.round(this.y);
                this.disparando = true;SomJogo.tocar(atiraProjetil() ? SomJogo.Efeito.CHEFE_TIRO : SomJogo.Efeito.LASER);
                naveYAlvo = n; emissao = 0;
                if (atiraProjetil()) { this.atingiu = true; abrirAtaque(n); } // sem raio: o perigo sao os projeteis
                String pd = padrao(); this.relogioRaio = pd.equals("ESPIRAL") || pd.equals("RAJADA") ? 1.0 : pd.equals("VARRE") ? .9 : 0.43;
            }
        } else {
            this.relogioRaio -= d;
            String pd = padrao(); emissao -= d;
            if (pd.equals("ESPIRAL") && emissao <= 0) { emissao = .07; double a0 = this.tempo * 7; for (int k = 0; k < 2; k++) proj(this.x - 40, this.y, a0 + k * Math.PI, 210, TiroChefe.Movimento.RETO); }
            if (pd.equals("RAJADA") && emissao <= 0) { emissao = .12; proj(this.x - 60, this.y, Math.atan2(n + 18 - this.y, 120 - (this.x - 60)) + (Math.random() - .5) * .12, 380, TiroChefe.Movimento.RETO); }
            if (pd.equals("VARRE")) { this.raioAlvo = (int) Math.max(110, Math.min(540, this.raioAlvo + Math.signum(n + 18 - this.raioAlvo) * 140 * d)); }
            if (this.relogioRaio <= 0.0) {
                this.disparando = false;
                this.carga = Math.max(1.4, 3.2 - (double)this.setor * 0.13);
            }
        }
    }

    public boolean consumirDano(Rectangle rectangle) {
        if (banda != null) { for (MiniChefe m : vivos()) if (m.consumirDano(rectangle)) return true; return false; }
        if(comandante!=null)return comandante.consumirDano(rectangle);
        for (TiroChefe t : projeteis) if (t.isVisible() && t.getBounds().intersects(rectangle)) { t.setVisible(false); return true; }
        if (!this.disparando || this.atingiu) {
            return false;
        }
        Rectangle rectangle2 = new Rectangle(0, this.raioAlvo - 12, Math.max(0, fimRaio()), 24);
        boolean duplo = padrao().equals("RAIO2");
        if (duplo) rectangle2 = new Rectangle(0, this.raioAlvo - 52, Math.max(0, fimRaio()), 24);
        if (duplo && !rectangle2.intersects(rectangle)) rectangle2 = new Rectangle(0, this.raioAlvo + 28, Math.max(0, fimRaio()), 24);
        if (!rectangle2.intersects(rectangle)) {
            return false;
        }
        this.atingiu = true;
        return true;
    }

    /** Onde o raio do codigo termina: na boca do raio da arte (olho da sentinela ou caveira do integrante). */
    private int fimRaio() {
        if (AtlasInimigos.integrante(integrante) != null) return (int) this.x - 131;
        if (AtlasInimigos.temSentinelaOlho()) return (int) this.x - 81;
        return (int) this.x - 65;
    }
    /** Raio em pixel art: blocos de 4 px, borda serrilhada que treme, nucleo claro; roxo no integrante. */
    private void desenharRaio(Graphics2D g) {
        Color c = corAtaque();
        if (padrao().equals("RAIO2")) { int y0 = this.raioAlvo; this.raioAlvo = y0 - 40; desenharFeixe(g, c); this.raioAlvo = y0 + 40; desenharFeixe(g, c); this.raioAlvo = y0; }
        else desenharFeixe(g, c);
    }
    private void desenharFeixe(Graphics2D g, Color c) {
        // mesma paleta e espessura do raio da arte: borda vermelha viva, meio laranja, miolo claro grosso
        Color borda = c != null ? c : new Color(215, 35, 35), meio = c != null ? new Color(Math.min(255, c.getRed() + 50), Math.min(255, c.getGreen() + 50), Math.min(255, c.getBlue() + 50)) : new Color(255, 110, 60),
              claro = c != null ? new Color(Math.min(255, c.getRed() + 120), Math.min(255, c.getGreen() + 120), Math.min(255, c.getBlue() + 120)) : new Color(255, 240, 215);
        // entra 24 px por baixo da arte para nao deixar emenda (a arte e desenhada por cima)
        int fim = fimRaio() + 24, y = this.raioAlvo; java.util.Random r = new java.util.Random((long) (this.tempo * 30));
        for (int px = 0; px < fim; px += 4) {
            int h = 10 + r.nextInt(3) * 2;
            g.setColor(borda); g.fillRect(px, y - h, 4, h * 2);
            g.setColor(meio); g.fillRect(px, y - h + 3, 4, h * 2 - 6);
            g.setColor(claro); g.fillRect(px, y - 4, 4, 8);
            if (r.nextInt(6) == 0) { g.setColor(meio); g.fillRect(px, y + (r.nextBoolean() ? -h - 4 : h), 4, 4); } // faisca solta
        }
        // eletricidade serpenteando, so ate a boca do raio da arte
        for (int fio = 0; fio < 2; fio++) {
            int ant = y;
            for (int px = fim - 24; px > 0; px -= 4) {
                int yy = y + (int) (Math.sin(px * .045 + this.tempo * (fio == 0 ? 26 : -22) + fio * Math.PI) * 15) + r.nextInt(3) - 1;
                g.setColor(fio == 0 ? Color.WHITE : claro);
                g.fillRect(px - 4, Math.min(ant, yy), 4, Math.abs(yy - ant) + 3); ant = yy;
            }
        }
    }

    public boolean receberDano(int n) {
        if(comandante!=null){comandante.dano(n);return comandante.terminou();}
        if(banda!=null){java.util.List<MiniChefe> v=vivos();if(!v.isEmpty())v.get(0).receberMembro(n);return vivos().isEmpty();}
        this.vida = Math.max(0, this.vida - Math.max(0, n));
        return this.vida == 0;
    }

    public void atrapalharCarga() {
        if (this.carregando) {
            this.relogioRaio += 0.35;
        }
    }

    public Rectangle getBounds() {
        if(comandante!=null)return comandante.corpo();
        if(banda!=null){Rectangle r=null;for(MiniChefe m:vivos())r=r==null?m.getBounds():r.union(m.getBounds());return r!=null?r:new Rectangle((int)x-70,(int)y-48,140,96);}
        return new Rectangle((int)this.x - 70, (int)this.y - 48, 140, 96);
    }

    public int getVida() {
        if(banda!=null){int t=0;for(MiniChefe m:banda)t+=m.vida;return t;}
        return comandante==null?this.vida:comandante.vida();
    }

    public int getVidaMaxima() {
        if(banda!=null){int t=0;for(MiniChefe m:banda)t+=m.vidaMaxima;return t;}
        return comandante==null?this.vidaMaxima:comandante.max();
    }

    public boolean avisando() {
        return this.carregando;
    }

    public boolean disparando() {
        return this.disparando;
    }

    public int getRaioAlvo() {
        return this.raioAlvo;
    }

    public void desenhar(Graphics2D graphics2D) {
        if(comandante!=null){comandante.desenhar(graphics2D);return;}
        if(banda!=null){if(combo>0)desenharEmblema(graphics2D);for(MiniChefe m:vivos())m.desenhar(graphics2D);return;}
        Graphics2D graphics2D2 = (Graphics2D)graphics2D.create();
        // sem linha de aviso: enquanto carrega, o aviso e o proprio olho arregalado; o raio so aparece no disparo
        if (this.disparando && !atiraProjetil()) desenharRaio(graphics2D2);
        for (TiroChefe t : projeteis) t.desenhar(graphics2D2);
        int n = (int)this.x;
        int n2 = (int)this.y;
        int n3 = this.disparando ? 2 : (this.carregando ? 1 : ((int)(this.tempo * 4.0)%2==0?0:3));
        if (invasor) {
            Graphics2D monstro = (Graphics2D)graphics2D2.create();
            monstro.translate(n-64,n2-56);monstro.scale(2,2);
            InimigosUniversos.desenhar(monstro,6,Alien.Classe.ORGANICO,0,0,tempo,false);monstro.dispose();
        } else if (AtlasInimigos.integrante(integrante) != null) {
            // o integrante toca o tempo todo: alterna tocar e bate-cabeca; arregala no preparo e solta o golpe
            // criatura: o quadro 4 e ela ferida, so com pouca vida; musicos alternam tocar e bate-cabeca
            int q = this.disparando ? 2 : this.carregando ? 1 : instrumento() == 'C' ? (vida < vidaMaxima * .35 ? 3 : 0) : (int) (this.tempo * 2.2) % 2 == 0 ? 0 : 3;
            AtlasInimigos.integrante(graphics2D2, AtlasInimigos.integrante(integrante), n, n2 + 5, q);
        } else if (AtlasInimigos.temSentinelaOlho()) {
            AtlasInimigos.sentinelaOlho(graphics2D2, n, n2 - 11, this.disparando ? 2 : this.carregando ? 1 : vida < vidaMaxima * .35 ? 3 : 0);
        } else AtlasInimigos.sentinela(graphics2D2, n, n2, n3);
        graphics2D2.setColor(new Color(255, 205, 115));
        graphics2D2.setFont(new Font("Monospaced", 1, 12));
        {String rot=invasor ? "DEVORADOR DO VAZIO" : nome();graphics2D2.drawString(rot, n - graphics2D2.getFontMetrics().stringWidth(rot) / 2 + 2, n2 - (!invasor && AtlasInimigos.integrante(integrante) != null ? 108 : AtlasInimigos.temSentinelaOlho() && !invasor ? 92 : 65));}
        int bx = Math.max(8, Math.min(824, n - 64));
        int by = Math.min(564, n2 + (!invasor && AtlasInimigos.integrante(integrante) != null ? 104 : AtlasInimigos.temSentinelaOlho() && !invasor ? 90 : 66));
        HudMetal.painel(graphics2D2, bx, by, 128, 27);
        for (int i = 0; i < 16; i++) {
            int px = bx + 10 + i * 7;
            boolean aceso = i < (int)Math.ceil(16.0 * vida / vidaMaxima);
            graphics2D2.setColor(aceso ? new Color(141, 24, 34) : new Color(13, 19, 27));
            graphics2D2.fillRect(px, by + 8, 5, 11);
            if (aceso) {
                graphics2D2.setColor(new Color(248, 79, 55));
                graphics2D2.fillRect(px, by + 8, 5, 6);
                graphics2D2.setColor(new Color(255, 192, 131));
                graphics2D2.fillRect(px + 1, by + 8, 3, 1);
            }
        }
        graphics2D2.dispose();
    }
}

