package Jogo;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.util.List;

/** Chefes musicais com a arte aprovada e animacao local por forma. */
public final class ChefeMetal {
    private final int artista;
    private final int largura = 412;
    private final int altura = 385;
    private final int vidaPorForma;
    private int vida;
    private int forma = 1;
    private double x = Config.LARGURA + 40, y = 145;
    private double tempo, entrada, intervalo = 1.6, transformando, flash;
    private double investida = 0, prepararInvestida = 3.6, alvoInvestidaY;
    private boolean ataqueFisicoConsumido;
    private int rajadas;
    private static final double DURACAO_GESTO = .42;
    private double gestoDisparo;
    private double proximaManobra = 4.1;
    private double manobraAtiva;
    private double destinoManobraY;
    /** Ozzy tem tres atos: Crazy Train, morcego gigante e Principe das Trevas. */
    private final int ultimaForma;
    private String cenaPendente;
    private final Trem trem;
    /** James: ato I e a mao do marionetista com o boneco; quebra = boneco desmontando antes do James aparecer. */
    private final MarionetePuppets mar;
    /** Valdrak: ato I e a moto-robo; na quebra ela se transforma no Valdrak. */
    private final MotoValdrak moto;
    /** Krovax: a centopeia de sucata (substitui o chefe 4). */
    private final CentopeiaKrovax cento;
    private double quebra; private String aviso;
    /** Kanoa (ato II): golpe da vez (0 voz, 1 machado, 2 lanca, 3 arco), mostrado no quadro proprio da arte. */
    private int golpeKanoa = -1, ataquesKanoa; private double tempoGolpeKanoa;
    private static final double QUEBRA = 2.6, MONTAGEM = 4.4;
    private double quebraTotal = QUEBRA;
    /** Valdrak (ato II): chicotada que varre a tela, laco de plasma e faiscas dos cabos. */
    private double chicoteAviso, chicote, chicoteA0, chicoteA1, chicoteAng, tempoQuadroValdrak;
    private boolean chicoteAcertou; private int golpesValdrak, quadroValdrak = -1;
    private static final double CHICOTE_ALCANCE = 720;
    /** Pecas da montagem do Valdrak: x, y de destino, x, y de partida, atraso, giro. */
    private java.util.List<double[]> pecas; private java.awt.image.BufferedImage valdrakTela; private int vdx, vdy;


    public ChefeMetal(int artista, Dificuldade dificuldade) {
        if (artista < 1 || artista > 7) throw new IllegalArgumentException("artista invalido");
        this.artista = artista;
        Sprites16Bits.preparar(artista, 1);
        Sprites16Bits.preparar(artista, 2);
        this.vidaPorForma = (int) Math.round((artista == 1 ? 345 : artista == 2 ? 190 : artista == 3 ? 215 : 180 + artista * 12)
                * dificuldade.resistenciaChefe * (artista == 1 ? .85 : 1));
        this.vida = vidaPorForma;
        this.ultimaForma = artista == 1 ? 3 : 2;
        this.trem = artista == 1 ? new Trem() : null;
        this.mar = artista == 5 && MarionetePuppets.temArte() ? new MarionetePuppets() : null;
        this.moto = artista == 2 && MotoValdrak.temArte() ? new MotoValdrak() : null;
        this.cento = artista == 4 && CentopeiaKrovax.temArte() ? new CentopeiaKrovax() : null;
    }
    /** Cutscene pedida pela luta (lida uma unica vez pela Fase). */
    String consumirCena() { String c = cenaPendente; cenaPendente = null; return c; }
    /** Forma desenhada pelos sprites: no Ozzy o ato I e o trem, entao morcego = 1 e principe = 2. */
    private int formaSprite() { return artista == 1 ? Math.max(1, forma - 1) : forma; }

    public void atualizar(double dt, int naveX, int naveY, Dificuldade dificuldade,
                          List<TiroChefe> tiros) {
        if (dt <= 0) return;
        tempo += dt;
        flash = Math.max(0, flash - dt);
        gestoDisparo = Math.max(0, gestoDisparo - dt);
        if (tempoGolpeKanoa > 0 && (tempoGolpeKanoa = Math.max(0, tempoGolpeKanoa - dt)) == 0) golpeKanoa = -1;
        if (chicoteAviso > 0) { chicoteAviso -= dt; if (chicoteAviso <= 0) { chicoteAviso = 0; chicote = .55; chicoteAcertou = false; SomJogo.tocar(SomJogo.Efeito.CHEFE_TIRO); } }
        else if (chicote > 0) { chicote = Math.max(0, chicote - dt); double pc = 1 - chicote / .55; pc = pc * pc * (3 - 2 * pc); chicoteAng = chicoteA0 + (chicoteA1 - chicoteA0) * pc; }
        if (tempoQuadroValdrak > 0 && (tempoQuadroValdrak = Math.max(0, tempoQuadroValdrak - dt)) == 0 && chicote <= 0 && chicoteAviso <= 0) quadroValdrak = -1;
        if (trem != null && forma == 1) { trem.atualizar(dt, naveX, naveY, dificuldade, tiros); entrada = trem.entrada(); return; }
        if (mar != null && forma == 1) { mar.vida(vida / (double) vidaPorForma); mar.atualizar(dt, naveX, naveY, dificuldade, tiros); entrada = mar.entrada(); if (!mar.investindo()) ataqueFisicoConsumido = false; return; }
        if (cento != null) { if (transformando > 0) transformando = Math.max(0, transformando - dt); cento.atualizar(dt, naveX, naveY, dificuldade, tiros, vida / (double) vidaPorForma); entrada = cento.entrada(); return; }
        if (moto != null && forma == 1) { moto.atualizar(dt, naveX, naveY, dificuldade, tiros, vida < vidaPorForma * .4); entrada = moto.entrada(); return; }
        if (quebra > 0) { quebra = Math.max(0, quebra - dt); entrada = 1; x = Config.LARGURA - largura - 28; y = Config.HUD + 47; return; }
        entrada = Math.min(1.0, entrada + dt / 1.35);
        double suavizar = 1 - Math.pow(1 - entrada, 3);
        double baseX = (Config.LARGURA + 40) * (1 - suavizar)
                + (Config.LARGURA - largura - 28) * suavizar;
        // Cada artista patrulha o campo com uma amplitude e ritmo caracteristicos.
        double[] horizontal = {0, 32, 77, 38, 64, 47, 84, 58};
        double[] vertical = {0, 38, 49, 32, 27, 51, 43, 36};
        x = baseX + Math.sin(tempo * (.75 + artista * .08)) * horizontal[artista];
        y = Config.HUD + 47 + Math.sin(tempo * (1.05 + artista * .09)) * vertical[artista];
        if (entrada < 1) return;
        proximaManobra -= dt * dificuldade.cadenciaInimigos;
        if (proximaManobra <= 0 && manobraAtiva <= 0) {
            manobraAtiva = 1.45; // Preparacao, avanco e retorno.
            destinoManobraY = Math.max(Config.HUD + 16,
                    Math.min(Config.ALTURA - altura - 12, naveY - altura * .40));
            proximaManobra = 4.9 + (artista % 3) * .55;
        }
        if (manobraAtiva > 0) {
            manobraAtiva = Math.max(0, manobraAtiva - dt);
            double progresso = 1 - manobraAtiva / 1.45;
            // Primeiro 0.45 s sao de aviso; depois investe e retorna.
            if (progresso > .31) {
                double arco = Math.sin((progresso - .31) / .69 * Math.PI);
                double distancia = forma == 2 ? 205 : 122;
                x -= Math.max(0, arco) * distancia;
                y += (destinoManobraY - y) * Math.max(0, arco) * .5;
            }
        }
        x = Math.max(238, Math.min(Config.LARGURA - largura - 12, x));
        y = Math.max(Config.HUD + 7, Math.min(Config.ALTURA - altura - 7, y));
        if (transformando > 0) {
            transformando = Math.max(0, transformando - dt);
            return;
        }
        if (artista == 1 && forma == 2) {
            if (investida > 0) {
                investida = Math.max(0, investida - dt);
                // O monstro corre ate perto do jogador e volta ao posto original.
                double progresso = 1 - investida / 0.95;
                double deslocamento = Math.sin(progresso * Math.PI) * 330;
                x -= deslocamento;
                y += (alvoInvestidaY - y) * Math.min(1, dt * 2.1);
            } else {
                prepararInvestida -= dt * dificuldade.cadenciaInimigos;
                if (prepararInvestida <= 0) {
                    prepararInvestida = 3.8;
                    investida = 0.95;
                    alvoInvestidaY = Math.max(Config.HUD + 30, Math.min(Config.ALTURA - altura, naveY - 80));
                    ataqueFisicoConsumido = false;
                }
            }
        }
        intervalo -= dt * dificuldade.cadenciaInimigos;
        if (intervalo > 0 || tiros.size() > 48) return;
        disparar(naveX, naveY, tiros);
        gestoDisparo = DURACAO_GESTO;
        rajadas++;
        double[] ritmos = {0, 1.90, 2.35, 2.10, 2.55, 1.85, 2.20, 2.35};
        intervalo = ritmos[artista] * (forma == 2 ? .79 : 1.0)
                * (vida < vidaPorForma * .4 ? .85 : 1);
    }

    /** Ataques exclusivos por artista: tipos, origens, ritmo e trajetorias diferentes. */
    private void disparar(int naveX, int naveY, List<TiroChefe> tiros) {
        double ox = x + 60, oy = y + 190;
        double mira = Math.atan2(naveY + 18 - oy, naveX + 22 - ox);
        switch (artista) {
            case 1: // Ozzy: morcegos ondulantes -> ondas sonoras concentradas.
                if (forma == 1) {
                    trem.disparar(naveX, naveY, tiros);
                } else if (forma == 2) {
                    for (int i = -3; i <= 3; i++)
                        tiro(tiros, ox, oy + i * 12, Math.PI + i * .115, 220, false,
                                TiroChefe.Movimento.ONDA);
                } else {
                    // revoada: um anel de morcegos abre em volta do Principe a cada terceira rajada
                    if (rajadas % 3 == 2)
                        for (int i = 0; i < 10; i++) tiro(tiros, ox + 80, oy - 40, i * Math.PI / 5, 150, false, TiroChefe.Movimento.ONDA);
                    for (int i = -4; i <= 4; i++)
                        tiro(tiros, ox, oy, mira + i * .145, 275, i == 0,
                                TiroChefe.Movimento.PULSANTE);
                }
                break;
            case 2: // Valdrak: no ato II, golpes de chicote; no ato I antigo, misseis.
                if (forma == 2 && PosesChefes.temNovaArte(2)) { golpeValdrak(naveX, naveY, tiros); break; }
                for (int lado = -1; lado <= 1; lado += 2) {
                    double origemY = oy + lado * 90;
                    double direcao = forma == 1 ? Math.PI + lado * .22
                            : Math.atan2(naveY + 18 - origemY, naveX - (ox - 10));
                    for (int i = -1; i <= 1; i++)
                        tiro(tiros, ox + 20, origemY, direcao + i * .065,
                                230 + 20 * Math.abs(i), i == 0 && forma == 2,
                                TiroChefe.Movimento.ACELERADO);
                }
                break;
            case 3: // Bruce/Eddie: cruzamentos de lasers e mergulhos diagonais.
                for (int lado = -1; lado <= 1; lado += 2) {
                    double partidaY = oy + lado * 122;
                    double direcao = Math.atan2(naveY + 18 - partidaY,
                            naveX + 22 - ox);
                    for (int i = -1; i <= 1; i++)
                        tiro(tiros, ox, partidaY, direcao + i * (forma == 1 ? .07 : .17),
                                forma == 1 ? 300 : 340, false,
                                forma == 1 ? TiroChefe.Movimento.RETO : TiroChefe.Movimento.ACELERADO);
                }
                break;
            case 4: // Lemmy: ondas de baixo lentas, grandes, oscilando no espaco.
                for (int i = -3; i <= 3; i++)
                    tiro(tiros, ox, oy + i * 8, Math.PI + i * .155,
                            forma == 1 ? 170 : 220, (i % 3) == 0,
                            TiroChefe.Movimento.ONDA);
                break;
            case 5: // James: riff eletrico alterna diagonais e rajada afinada no jogador.
                for (int i = 0; i < (forma == 1 ? 6 : 8); i++) {
                    double origemY = oy + (i % 2 == 0 ? -122 : 122);
                    double angulo = forma == 1 ? Math.PI + (i % 4 - 1.5) * .23
                            : Math.atan2(naveY + 18 - origemY, naveX - ox) + (i - 3.5) * .045;
                    tiro(tiros, ox, origemY, angulo, forma == 1 ? 260 : 345, i % 4 == 0,
                            TiroChefe.Movimento.PULSANTE);
                }
                break;
            case 6: // Tom: chuva vertical vermelha, no segundo ato vem onda lateral.
                for (int i = 0; i < (forma == 1 ? 8 : 11); i++) {
                    double origemX = 90 + ((i * 91 + rajadas * 67) % 740);
                    tiro(tiros, origemX, Config.HUD + 9, Math.PI / 2 + (i % 2 == 0 ? -.09 : .09),
                            190 + (i % 3) * 21, false, TiroChefe.Movimento.ACELERADO);
                }
                if (forma == 2)
                    for (int i = -2; i <= 2; i++)
                        tiro(tiros, ox, oy, mira + i * .13, 255, i == 0,
                                TiroChefe.Movimento.RETO);
                break;
            case 7: // Kanoa: no ato II alterna voz de trovao, machado, lanca e arco de tempestade.
                if (forma == 2 && PosesChefes.temNovaArte(7)) { golpeKanoa(naveX, naveY, tiros); break; }
                for (int i = -4; i <= 4; i++) {
                    double partidaY = oy + (i % 2 == 0 ? -85 : 85);
                    double angulo = (forma == 1 ? Math.PI : mira) + i * .105;
                    tiro(tiros, ox + Math.abs(i) * 9, partidaY, angulo,
                            forma == 1 ? 228 : 285, i == 0,
                            TiroChefe.Movimento.PULSANTE);
                }
                break;
            default: throw new IllegalStateException("Artista nao cadastrado");
        }
    }

    private void arma(List<TiroChefe> tiros, int estilo, double x0, double y0, double a, double v, boolean pesado, TiroChefe.Movimento m) {
        tiros.add(new TiroChefe(x0, y0, Math.cos(a) * v, Math.sin(a) * v, pesado, estilo, m));
    }
    private double maoValdrakX() { return x + largura * .3; }
    private double maoValdrakY() { return y + altura * .42; }
    private void golpeValdrak(int naveX, int naveY, List<TiroChefe> tiros) {
        int k = golpesValdrak++ % 3; boolean furia = vida < vidaPorForma * .4;
        double nx = naveX + 22, ny = naveY + 18;
        if (k == 0) {        // laco: gira o chicote e solta um anel de plasma
            quadroValdrak = 1; tempoQuadroValdrak = .6;
            int n = furia ? 16 : 12; double cx = x + largura * .32, cy = y + altura * .15;
            for (int i = 0; i < n; i++) { double a = i * Math.PI * 2 / n + rajadas * .2;
                tiros.add(new TiroChefe(cx, cy, Math.cos(a) * 230, Math.sin(a) * 230, false, TiroChefe.BOLA_FOGO, TiroChefe.Movimento.RETO)); }
        } else if (k == 1) { // chicotada: aviso do arco e a varrida
            boolean deCima = ny < maoValdrakY(); chicoteAviso = .65; quadroValdrak = 1; tempoQuadroValdrak = 1.2;
            chicoteA0 = Math.PI + (deCima ? .95 : -.95); chicoteA1 = Math.PI + (deCima ? -.95 : .95); chicoteAng = chicoteA0;
            if (furia) { chicoteA0 += deCima ? .2 : -.2; chicoteA1 += deCima ? -.2 : .2; }
        } else {             // bate-cabeca: os cabos soltam uma chuva de faiscas sobre a nave
            quadroValdrak = 3; tempoQuadroValdrak = .7;
            for (int i = 0; i < (furia ? 9 : 7); i++)
                tiros.add(new TiroChefe(nx - 220 + i * 70, Config.HUD + 4, -50, 230 + (i % 3) * 40, false, TiroChefe.BOLA_FOGO, TiroChefe.Movimento.ARCO));
        }
    }
    private void golpeKanoa(int naveX, int naveY, List<TiroChefe> tiros) {
        int k = ataquesKanoa++ % 4; golpeKanoa = k; tempoGolpeKanoa = .6;
        boolean furia = vida < vidaPorForma * .4;
        double nx = naveX + 22, ny = naveY + 18, ox = x + 70, oy = y + 150, a = Math.atan2(ny - oy, nx - ox);
        if (k == 0) {        // a voz chama raios do ceu: caem na nave e ao lado dela
            for (int i = -1; i <= 1; i++) arma(tiros, TiroChefe.RAIO_CEU, nx + i * 90, Config.HUD + 4, Math.PI / 2, 560, i == 0, TiroChefe.Movimento.ACELERADO);
            if (furia) for (int i = 0; i < 2; i++) arma(tiros, TiroChefe.RAIO_CEU, nx - 230 + i * 460, Config.HUD + 4, Math.PI / 2, 480, false, TiroChefe.Movimento.RETO);
        } else if (k == 1) { // machado: tres cortes em meia-lua, abertos, com espaco para passar entre eles
            for (int i = -1; i <= 1; i++) arma(tiros, TiroChefe.CORTE, ox, oy, Math.PI + i * .34, 300, true, TiroChefe.Movimento.RETO);
            if (furia) for (int i = -1; i <= 1; i += 2) arma(tiros, TiroChefe.CORTE, ox, oy, Math.PI + i * .17, 240, true, TiroChefe.Movimento.RETO);
        } else if (k == 2) { // lanca: um arremesso pesado e rapido direto na nave
            arma(tiros, TiroChefe.LANCA, ox, oy, a, 600, true, TiroChefe.Movimento.RETO);
        } else {             // arco: leque de flechas eletricas e, na furia, chuva de flechas
            for (int i = -2; i <= 2; i++) arma(tiros, TiroChefe.FLECHA, ox, oy, a + i * .13, 360, false, TiroChefe.Movimento.RETO);
            if (furia) for (int i = 0; i < 5; i++) arma(tiros, TiroChefe.FLECHA, nx - 240 + i * 120, Config.HUD + 4, Math.PI / 2 + .25, 340, false, TiroChefe.Movimento.RETO);
        }
    }
    private void tiro(List<TiroChefe> tiros, double x0, double y0, double angulo,
                      double velocidade, boolean pesado, TiroChefe.Movimento movimento) {
        tiros.add(new TiroChefe(x0, y0, Math.cos(angulo) * velocidade,
                Math.sin(angulo) * velocidade, pesado, artista, movimento));
    }

    /** Rotulo util para testes e balanceamento dos estilos musicais. */
    String assinaturaAtaqueTeste() {
        switch (artista) {
            case 1: return "ONDAS SONORAS";
            case 2: return "MISSEIS ACELERADOS";
            case 3: return "RAIOS CRUZADOS";
            case 4: return "GRAVES OSCILANTES";
            case 5: return "RIFFS ELETRICOS";
            case 6: return "CHUVA DE FOGO";
            case 7: return "CRISTAIS";
            default: throw new IllegalStateException("Artista desconhecido");
        }
    }

    void forcarAtaqueTeste(int naveX, int naveY, List<TiroChefe> tiros) {
        disparar(naveX, naveY, tiros);
    }

    /** A primeira derrota transforma o artista; so a segunda encerra a batalha. */
    public boolean receberDano(int dano) {
        if (vida <= 0 || entrada < 1 || transformando > 0 || quebra > 0) return false;
        if (mar != null && forma == 1) { if (mar.consumirBloqueio()) return false; mar.ferido(); }
        if (moto != null && forma == 1) moto.ferido();
        if (cento != null) { if (cento.consumirBloqueio()) return false; cento.ferido(); }
        vida = Math.max(0, vida - Math.max(0, dano));
        flash = .16;
        if (vida != 0) return false;
        if (forma < ultimaForma) {
            forma++;
            vida = vidaPorForma;
            transformando = 1.45;
            intervalo = 1.7;
            // o morcego vira o Principe das Trevas numa cutscene propria
            if (trem != null && forma == 2) trem.parar();
            if (cento != null && forma == 2) { transformando = 0; cento.furia(); aviso = "KROVAX ENFURECEU!"; SomJogo.tocar(SomJogo.Efeito.EXPLOSAO_GRANDE); }
            if (moto != null && forma == 2) { quebra = quebraTotal = MONTAGEM; transformando = 0; aviso = "A MOTO SE TRANSFORMA: VALDRAK!"; SomJogo.tocar(SomJogo.Efeito.EVOLUCAO); }
            if (mar != null && forma == 2) { quebra = QUEBRA; transformando = 0; aviso = "O BONECO SE DESFAZ... QUEM PUXAVA OS FIOS ERA O HEXAR!"; SomJogo.tocar(SomJogo.Efeito.EXPLOSAO_GRANDE); }
            if (artista == 1 && forma == 3) { cenaPendente = CenaChefe.TRANSFORMACAO; transformando = .3; }
            return false;
        }
        return true;
    }

    String consumirAviso() { String a = aviso; aviso = null; return a; }
    /** Tiro do jogador: no ato I da marionete o boneco segura os tiros. */
    boolean colide(Rectangle r) { if (cento != null) return cento.colide(r); if (mar != null && forma == 1) return mar.colide(r); if (moto != null && forma == 1) return r.intersects(moto.bounds()); if (quebra > 0) return false; return r.intersects(getBounds()); }
    /** Ozzy so causa dano no mergulho, nao durante o aviso. */
    public boolean consumirColisaoCorporal(Rectangle areaNave) {
        if (artista == 2 && forma == 2 && chicote > 0 && !chicoteAcertou) {
            double ax = areaNave.getCenterX() - maoValdrakX(), ay = areaNave.getCenterY() - maoValdrakY(), ux = Math.cos(chicoteAng), uy = Math.sin(chicoteAng);
            double ao = ax * ux + ay * uy;
            if (ao > 0 && ao < CHICOTE_ALCANCE && Math.abs(ax * uy - ay * ux) < 24) { chicoteAcertou = true; return true; }
        }
        if (cento != null) return cento.contato(areaNave);
        if (moto != null && forma == 1) return moto.consumirColisao(areaNave);
        if (mar != null && forma == 1) { if (mar.investindo() && !ataqueFisicoConsumido && mar.boneco().intersects(areaNave)) { ataqueFisicoConsumido = true; return true; } return false; }
        if (artista != 1 || forma != 2 || investida <= 0 || ataqueFisicoConsumido ||
                transformando > 0) return false;
        if (getBounds().intersects(areaNave)) {
            ataqueFisicoConsumido = true;
            return true;
        }
        return false;
    }

    public void desenhar(Graphics2D tela) {
        Graphics2D g = (Graphics2D) tela.create();
        g.translate((int) x, (int) y);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        Color c = artista == 1 ? new Color(174, 79, 217) : artista == 2
                ? new Color(254, 133, 55) : artista == 3 ? new Color(88, 212, 221)
                : artista == 4 ? new Color(255, 126, 47) : artista == 5 ? new Color(88, 169, 255)
                : artista == 6 ? new Color(244, 70, 76) : new Color(108, 234, 128);
        double gesto = gestoDisparo > 0 ? gestoDisparo / DURACAO_GESTO
                : entrada >= 1 && transformando <= 0 && intervalo < .38
                    ? -(1 - Math.max(0, intervalo) / .38) : 0;
        // Transformacao dissolve as duas formas; nenhuma bolha cobre o personagem.
        if (trem != null && forma == 1) { g.dispose(); trem.desenhar(tela, flash); return; }
        if (mar != null && forma == 1) { g.dispose(); mar.desenhar(tela, 1f); return; }
        if (cento != null) { g.dispose(); cento.desenhar(tela); return; }
        if (moto != null && forma == 1) { g.dispose(); moto.desenhar(tela, 1f, -1); return; }
        if (quebra > 0) {
            // o boneco desmonta no lugar onde o James aparece, e ele vai surgindo no meio das lascas
            double p = 1 - quebra / quebraTotal; Graphics2D q = (Graphics2D) tela.create();
            if (mar != null) mar.desenharQuebra(q, p, x + largura * .55, y - 10, altura + 30);
            else { q.dispose(); g.dispose(); montagem(tela, p); return; }
            q.dispose();
            float a = (float) Math.max(0, Math.min(1, (p - .35) / .45));
            if (a > 0) { g.setComposite(AlphaComposite.SrcOver.derive(a)); Sprites16Bits.desenhar(g, artista, 2, largura, altura, tempo, 0); }
            g.dispose(); return;
        }
        if (transformando > 0) {
            float transicao = (float)Math.max(0, Math.min(1, 1 - transformando / 1.45));
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1-transicao));
            // o trem explode e o morcego sai da fumaca
            if (trem != null && forma == 2) { Graphics2D t = (Graphics2D) tela.create(); t.setComposite(g.getComposite()); trem.desenhar(t, 0); t.dispose(); }
            else Sprites16Bits.desenhar(g, artista, formaSprite() - 1, largura, altura, tempo, 0);
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, transicao));
        }
        if (artista == 2 && forma == 2 && PosesChefes.temNovaArte(2)) {
            int qv = chicote > 0 ? 2 : chicoteAviso > 0 ? 1 : quadroValdrak >= 0 ? quadroValdrak : 0;
            Sprites16Bits.desenharQuadro(g, 2, 2, largura, altura, tempo, qv);
            Graphics2D cg = (Graphics2D) tela.create(); desenharChicote(cg); cg.dispose();
        } else if (artista == 7 && forma == 2 && PosesChefes.temNovaArte(7)) Sprites16Bits.desenharQuadro(g, 7, 2, largura, altura, tempo, golpeKanoa < 0 ? 0 : golpeKanoa);
        else Sprites16Bits.desenhar(g, artista, formaSprite(), largura, altura, tempo, gesto);
        g.setComposite(AlphaComposite.SrcOver);
        if (manobraAtiva > 0 && manobraAtiva < 1.45 && manobraAtiva > .98) {
            // Aviso compacto, fora da silhueta e sem esconder os tiros.
            g.setColor(new Color(255, 176, 80, 220));
            g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 13));
            g.drawString("INVESTIDA", 135, 8);
        }
        if (flash > 0) {
            g.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), 210));
            for (int i=0;i<6;i++) {
                double angulo=i*Math.PI/3;
                int r=15+(int)((.16-flash)*180);
                int px=35+(int)(Math.cos(angulo)*r),py=altura/2+(int)(Math.sin(angulo)*r);
                g.fillRect(px,py,4,4);
            }
        }
        g.dispose();
    }

    /** Absolute cinema: faixas de cinema, a moto se abre e o Valdrak se monta peca por peca, dos pes a cabeca. */
    private void montagem(Graphics2D tela, double p) {
        Graphics2D g = (Graphics2D) tela.create();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        if (pecas == null) prepararPecas();
        int W = Config.LARGURA, H = Config.ALTURA, barra = (int) (58 * Math.min(1, Math.min(p / .08, (1 - p) / .06)));
        // a moto vai para o centro do palco da montagem, abre as pecas e se desfaz enquanto as pecas saem dela
        double mp = Math.min(1, p / .16);
        Graphics2D m = (Graphics2D) g.create();
        m.translate((x + largura * .5 - moto.largura() / 2.0 - moto.getX()) * mp, (y + altura * .62 - moto.altura() / 2.0 - moto.getY()) * mp);
        moto.desenhar(m, (float) Math.max(0, 1 - (p - .3) / .3), p < .1 ? 0 : 3);
        m.dispose();
        // pecas: saem do corpo da moto e encaixam no lugar, em arco, girando
        java.util.Random fa = new java.util.Random((long) (p * 40));
        for (double[] pc : pecas) {
            double t0 = .22 + pc[4] * .5, t = Math.max(0, Math.min(1, (p - t0) / .2)); if (t <= 0) continue;
            double e = 1 - Math.pow(1 - t, 3), px = pc[2] + (pc[0] - pc[2]) * e, py = pc[3] + (pc[1] - pc[3]) * e - Math.sin(e * Math.PI) * 70;
            Graphics2D c = (Graphics2D) g.create(); c.translate(px + 8, py + 8); c.rotate(pc[5] * (1 - e)); c.translate(-8, -8);
            int sx = (int) (pc[0] - vdx), sy = (int) (pc[1] - vdy);
            c.drawImage(valdrakTela, 0, 0, 16, 16, sx, sy, sx + 16, sy + 16, null); c.dispose();
            if (t >= 1 && p - t0 < .24) { g.setColor(new Color(255, 210, 120)); g.fillRect((int) pc[0] + fa.nextInt(16), (int) pc[1] + fa.nextInt(16), 3, 3); }
        }
        if (p > .9) { float a = (float) Math.min(1, (p - .9) / .06); Graphics2D c = (Graphics2D) g.create(); c.setComposite(AlphaComposite.SrcOver.derive(a)); c.drawImage(valdrakTela, vdx, vdy, null); c.dispose(); }
        if (p > .9 && p < .97) { g.setColor(new Color(255, 230, 190, (int) (200 * Math.sin((p - .9) / .07 * Math.PI)))); g.fillRect(0, 0, W, H); }
        g.dispose();
    }
    private void prepararPecas() {
        java.awt.image.BufferedImage v = PosesChefes.obter(2, 2, 0);
        double esc = (altura - 35.0) / v.getHeight(); int w = (int) Math.round(v.getWidth() * esc), h = (int) Math.round(v.getHeight() * esc);
        valdrakTela = new java.awt.image.BufferedImage(w, h, java.awt.image.BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = valdrakTela.createGraphics(); g.drawImage(v, 0, 0, w, h, null); g.dispose();
        vdx = (int) x + (largura - w) / 2; vdy = (int) y + (altura - h) / 2;
        pecas = new java.util.ArrayList<>(); java.util.Random r = new java.util.Random(2);
        Rectangle mb = moto.bounds();
        for (int ty = 0; ty < h; ty += 16) for (int tx = 0; tx < w; tx += 16) {
            int cheios = 0; for (int yy = ty; yy < Math.min(h, ty + 16); yy += 4) for (int xx = tx; xx < Math.min(w, tx + 16); xx += 4) if ((valdrakTela.getRGB(xx, yy) >>> 24) > 40) cheios++;
            if (cheios < 2) continue;
            double atraso = (1 - ty / (double) h) * .8 + r.nextDouble() * .2; // dos pes a cabeca
            pecas.add(new double[]{vdx + tx, vdy + ty, mb.x + r.nextInt(Math.max(1, mb.width)), mb.y + r.nextInt(Math.max(1, mb.height)), atraso, (r.nextDouble() - .5) * 8});
        }
    }
    /** Montagem sem faixas de cinema (pedido do jogo). */
    private static final boolean SEM_CINEMA = true;
    /** Altura das faixas de cinema da montagem (0 fora dela); a Fase desenha por cima de tudo. */
    int cinema() { if (SEM_CINEMA || moto == null || quebra <= 0) return 0; double p = 1 - quebra / quebraTotal; return (int) (62 * Math.min(1, Math.min(p / .08, (1 - p) / .06))); }
    /** A chicotada: aviso do arco e o chicote de plasma esticado da mao. */
    private void desenharChicote(Graphics2D tela) {
        Graphics2D g = (Graphics2D) tela.create();
        double hx = maoValdrakX(), hy = maoValdrakY();
        if (chicoteAviso > 0) {
            g.setColor(new Color(255, 120, 40, (int) (60 + 50 * Math.sin(tempo * 30))));
            int r = (int) CHICOTE_ALCANCE, gi = (int) Math.toDegrees(-chicoteA0), gf = (int) Math.toDegrees(-chicoteA1);
            g.fillArc((int) hx - r, (int) hy - r, r * 2, r * 2, Math.min(gi, gf), Math.abs(gf - gi));
        }
        if (chicote > 0) {
            int n = 24; int[] xs = new int[n], ys = new int[n]; double ux = Math.cos(chicoteAng), uy = Math.sin(chicoteAng);
            for (int k = 0; k < n; k++) { double d = CHICOTE_ALCANCE * k / (n - 1), onda = Math.sin(k * .9 + tempo * 40) * 7 * k / n;
                xs[k] = (int) (hx + ux * d - uy * onda); ys[k] = (int) (hy + uy * d + ux * onda); }
            g.setStroke(new java.awt.BasicStroke(12)); g.setColor(new Color(255, 110, 30, 110)); g.drawPolyline(xs, ys, n);
            g.setStroke(new java.awt.BasicStroke(5)); g.setColor(new Color(255, 170, 50)); g.drawPolyline(xs, ys, n);
            g.setStroke(new java.awt.BasicStroke(2)); g.setColor(new Color(255, 245, 200)); g.drawPolyline(xs, ys, n);
            g.setColor(new Color(255, 240, 180)); g.fillOval(xs[n - 1] - 9, ys[n - 1] - 9, 18, 18);
        }
        g.dispose();
    }

    public void desenharVida(Graphics2D g) {
        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        g.setColor(Color.WHITE);
        String nome = getNome() + "  [ATO " + (forma == 1 ? "I" : forma == 2 ? "II" : "III") + "]";
        g.drawString(nome, 217, Config.HUD + 24);
        g.setColor(new Color(8, 22, 34, 222));
        g.fillRoundRect(218, Config.HUD + 31, 526, 22, 10, 10);
        int preenchido = (int) (522.0 * vida / vidaPorForma);
        g.setPaint(new GradientPaint(218, Config.HUD + 32,
                artista == 1 ? new Color(179, 68, 219) : artista == 2
                        ? new Color(249, 157, 56) : artista == 3 ? new Color(94, 232, 215)
                        : artista == 4 ? new Color(255, 154, 65) : artista == 5 ? new Color(71, 155, 244)
                        : artista == 6 ? new Color(241, 63, 86) : new Color(100, 241, 126),
                741, Config.HUD + 52, new Color(222, 36, 70)));
        g.fillRoundRect(220, Config.HUD + 33, preenchido, 18, 8, 8);
        g.setColor(new Color(255, 255, 255, 130));
        g.drawRoundRect(218, Config.HUD + 31, 526, 22, 10, 10);
    }

    public Rectangle getBounds() {
        if (trem != null && forma == 1) return trem.getBounds();
        if (mar != null && forma == 1) return mar.bounds();
        if (moto != null && forma == 1) return moto.bounds();
        if (cento != null) return cento.bounds();
        return new Rectangle((int)x + 42, (int)y + 30, largura - 87, altura - 74);
    }
    public boolean estaVivo() { return vida > 0; }
    public int getVida() { return vida; }
    public int getVidaMaxima() { return vidaPorForma; }
    public int getX() { return cento != null || (trem != null || mar != null || moto != null) && forma == 1 ? getBounds().x : (int)x; }
    public int getY() { return cento != null || (trem != null || mar != null || moto != null) && forma == 1 ? getBounds().y : (int)y; }
    public int getForma() { return forma; }
    public double getEntrada() { return entrada; }
    public String getNome() {
        switch (artista) {
            case 1: return forma == 1 ? "MALACHAR - EXPRESSO DAS TREVAS" : forma == 2 ? "MALACHAR - ASA NOTURNA" : "MALACHAR - REI DAS SOMBRAS";
            case 2: return moto != null && forma == 1 ? "VALDRAK - MOTO DE CROMO" : "VALDRAK - CHICOTE DE PLASMA";
            case 3: return "KORVUS - CORSARIO DO VACUO";
            case 4: return forma == 1 ? "KROVAX - A CENTOPEIA DE SUCATA" : "KROVAX - FURIA DE FERRUGEM";
            case 5: return mar != null && forma == 1 ? "CINZAS DE ORION - O MARIONETISTA" : "HEXAR - RIFF DE QUATRO BRACOS";
            case 6: return "MORGATH - CHUVA DE FOGO";
            case 7: return "KANOA - VOZ DO TROVAO";
            default: throw new IllegalStateException("Artista invalido");
        }
    }

    /**
     * Ato I do Ozzy: o Crazy Train. Corre nos trilhos da direita, troca de faixa, solta baforadas
     * roxas pela chamine, arranca vagoes de espinhos contra a nave e, de tempos em tempos,
     * atravessa a tela inteira na faixa da nave depois de um aviso.
     */
    private final class Trem {
        private final java.awt.image.BufferedImage arte = lerArte("ozzy_trem.png");
        private final int h = 175, w;
        private double tx = Config.LARGURA + 40, ty, alvoY, entrar, relogio = 1.4, sopro, aviso, investidaTrem = 6.5;
        private boolean investindo, voltando;
        private int golpes;
        Trem() {
            w = arte != null ? arte.getWidth() / 3 * h / arte.getHeight() : 540;
            ty = alvoY = Config.HUD + (Config.ALTURA - Config.HUD - h) / 2.0;
        }
        double entrada() { return entrar; }
        void parar() { aviso = 0; investindo = false; if (tx < 0 || tx > base()) tx = base(); }
        private double base() { return Config.LARGURA - w - 8; }
        void atualizar(double dt, int naveX, int naveY, Dificuldade dif, List<TiroChefe> tiros) {
            sopro = Math.max(0, sopro - dt);
            double furia = vida < vidaPorForma * .4 ? 1.3 : 1;
            if (investindo) {
                tx -= 980 * furia * dt;
                if (tx < -w - 30) { investindo = false; voltando = true; tx = Config.LARGURA + 40; alvoY = faixa(naveY); ty = alvoY; }
                return;
            }
            if (aviso > 0) {
                aviso = Math.max(0, aviso - dt);
                if (aviso == 0) { investindo = true; SomJogo.tocar(SomJogo.Efeito.CHEFE_ENTRADA); }
                return;
            }
            double chegada = voltando ? .9 : 1.35;
            if (tx > base()) tx = Math.max(base(), tx - (Config.LARGURA + 40 - base()) / chegada * dt);
            else voltando = false;
            if (!voltando) entrar = Math.min(1, entrar + dt / 1.35);
            // troca de faixa suave, perseguindo a nave com atraso
            ty += (alvoY - ty) * Math.min(1, dt * 1.6);
            if (entrar < 1 || voltando) return;
            relogio -= dt * dif.cadenciaInimigos * furia;
            investidaTrem -= dt * dif.cadenciaInimigos * furia;
            if (investidaTrem <= 0 && sopro <= 0) {
                investidaTrem = 6.2 + golpes % 2 * 1.3;
                alvoY = ty = faixa(naveY); aviso = .95;
                return;
            }
            if (relogio > 0 || tiros.size() > 46) return;
            golpes++;
            if (golpes % 3 == 0) vagao(naveX, naveY, tiros); else baforada(tiros);
            alvoY = faixa(naveY + (golpes % 2 == 0 ? -120 : 120));
            relogio = (golpes % 3 == 0 ? 1.6 : 1.25);
        }
        void disparar(int naveX, int naveY, List<TiroChefe> tiros) { if (golpes++ % 2 == 0) baforada(tiros); else vagao(naveX, naveY, tiros); }
        private double faixa(double naveY) { return Math.max(Config.HUD + 14, Math.min(Config.ALTURA - h - 10, naveY - h * .55)); }
        private void baforada(List<TiroChefe> tiros) {
            sopro = .7; gestoDisparo = DURACAO_GESTO;
            double cx = tx + w * .09, cy = ty + h * .12;
            for (int i = -2; i <= 2; i++)
                tiros.add(new TiroChefe(cx, cy, Math.cos(Math.PI + i * .16) * (230 - Math.abs(i) * 15), Math.sin(Math.PI + i * .16) * 230 + i * 25,
                        false, TiroChefe.FUMACA, TiroChefe.Movimento.ONDA));
        }
        private void vagao(int naveX, int naveY, List<TiroChefe> tiros) {
            double ox = tx + 40, oy = ty + h * .55, a = Math.atan2(naveY + 18 - oy, naveX + 22 - ox);
            tiros.add(new TiroChefe(ox, oy, Math.cos(a) * 360, Math.sin(a) * 360, true, TiroChefe.VAGAO, TiroChefe.Movimento.RETO));
        }
        Rectangle getBounds() { return new Rectangle((int) tx + 12, (int) (ty + h * .3), w - 24, (int) (h * .66)); }
        void desenhar(Graphics2D tela, double flashDano) {
            Graphics2D g = (Graphics2D) tela.create();
            int x = (int) tx, y = (int) ty + ((int) (tempo * 14) % 2);
            // sem faixa na tela: o aviso e o olho da caveira acendendo (abaixo)
            int q = sopro > 0 ? 1 : vida < vidaPorForma * .4 ? 2 : 0;
            if (arte != null) {
                int fw = arte.getWidth() / 3;
                g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                g.drawImage(arte, x, y, x + w, y + h, q * fw, 0, q * fw + fw, arte.getHeight(), null);
            } else {
                g.setColor(new Color(36, 30, 42)); g.fillRect(x, y + h / 3, w, h / 2);
                g.setColor(new Color(190, 80, 255)); for (int k = 0; k < 8; k++) g.fillOval(x + 20 + k * w / 8, y + h * 3 / 4, 26, 26);
            }
            // olho da caveira acende no aviso e em investida
            if (aviso > 0 || investindo) {
                int ex = x + (int) (w * .045), ey = y + (int) (h * .56), r = 9 + (int) (Math.sin(tempo * 30) * 3);
                g.setColor(new Color(230, 140, 255, 200)); g.fillOval(ex - r, ey - r, r * 2, r * 2);
                g.setColor(Color.WHITE); g.fillOval(ex - r / 3, ey - r / 3, r * 2 / 3, r * 2 / 3);
            }
            if (investindo) for (int k = 0; k < 12; k++) { g.setColor(new Color(200, 90, 255, 150 - k * 10)); g.fillRect(x + w + k * 18, y + h / 2 + (k % 3 - 1) * 26, 40, 3); }
            if (flashDano > 0 && arte != null) {
                // brilho so na silhueta do trem, sem caixa por cima
                int fw = arte.getWidth() / 3; Graphics2D b = (Graphics2D) g.create();
                b.setComposite(AlphaComposite.SrcOver.derive((float) Math.min(.45, flashDano / .16 * .45)));
                b.setXORMode(new Color(0, 0, 0)); b.setPaintMode();
                b.drawImage(arte, x, y + 2, x + w, y + h + 2, q * fw, 0, q * fw + fw, arte.getHeight(), null); b.dispose();
            }
            g.dispose();
        }
    }
    private static java.awt.image.BufferedImage lerArte(String n) {
        java.net.URL u = ChefeMetal.class.getResource("/ImagemIcon/" + n);
        if (u == null) return null;
        try { return javax.imageio.ImageIO.read(u); } catch (java.io.IOException e) { return null; }
    }
}
