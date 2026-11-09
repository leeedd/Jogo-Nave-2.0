package Jogo;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Contrato de cacador de recompensas, pego no quadro de MISSOES da base aliada.
 * Vale ate o chefe do setor cair: cumprido paga CR e XP; falhou ou expirou, so perde o contrato.
 * Os contratos especiais de nave (Fantasma, Escudo de Ouro, Furia Verde, Forja Final) liberam a nave.
 */
final class Contrato {
    enum Tipo { ABATES, SEM_DANO_CHEFE, CHEFE_RAPIDO, VAPORIZAR, CACADA, TROCA, MINERACAO, SEM_DANO_INTEGRANTE, INTEGRANTE_RAPIDO }

    final Tipo tipo;
    final int alvo, premioCR, premioXP, nave;
    final String titulo;
    int progresso, golpes;
    /** Golpes que o piloto pode levar antes de perder o contrato (abates e vaporizar). */
    static final int GOLPES_MAX = 2;
    boolean falhou, cumprido;
    /** Contrato dourado: banda dona do fragmento (-1 = contrato comum). Vale so ate o proximo integrante cair
     *  (ou, depois do ultimo integrante, ate o chefe). Cada um cumprido e um fragmento da reliquia da banda. */
    int banda = -1;
    boolean dourado() { return banda >= 0; }
    /** Por que o contrato acabou (vai para o arquivo da pausa). */
    String motivo = "";
    void falhar(String m) { if (!terminado()) { falhou = true; motivo = m; } }

    Contrato(Tipo tipo, int alvo, int cr, int xp, int nave, String titulo) {
        this.tipo = tipo; this.alvo = alvo; this.premioCR = cr; this.premioXP = xp; this.nave = nave; this.titulo = titulo;
    }

    String descricao() {
        switch (tipo) {
            case ABATES: return "Abater " + alvo + " antes do chefe, levando no maximo " + GOLPES_MAX + " golpes";
            case SEM_DANO_CHEFE: return "Vencer o chefe sem levar dano";
            case CHEFE_RAPIDO: return "Vencer o chefe em ate " + alvo + " s";
            case SEM_DANO_INTEGRANTE: return "Derrubar o proximo integrante sem levar dano";
            case INTEGRANTE_RAPIDO: return "Derrubar o proximo integrante em ate " + alvo + " s";
            case CACADA: return "Cacar " + ALVO_NAVE[nave] + " e recuperar " + ARTIGO[nave] + " " + RELIQUIA_NAVE[nave];
            case MINERACAO: return "Extrair " + alvo + " minerios raros dos campos de meteoros";
            case TROCA: return "Entregar " + ARTIGO[nave] + " " + RELIQUIA_NAVE[nave] + " e liberar a nave";
            default: return "Vaporizar " + alvo + " com o laser, levando no maximo " + GOLPES_MAX + " golpes";
        }
    }
    /** Texto curto para o HUD durante o combate. */
    String hud() {
        switch (tipo) {
            case ABATES: return titulo + ": " + Math.min(progresso, alvo) + "/" + alvo + " ABATES  GOLPES " + golpes + "/" + GOLPES_MAX;
            case SEM_DANO_CHEFE: return titulo + ": CHEFE SEM DANO";
            case CHEFE_RAPIDO: return titulo + ": CHEFE EM " + alvo + "s";
            case SEM_DANO_INTEGRANTE: return titulo + ": INTEGRANTE SEM DANO";
            case INTEGRANTE_RAPIDO: return titulo + ": INTEGRANTE EM " + alvo + "s";
            case CACADA: return "CACAR " + ALVO_NAVE[nave];
            case MINERACAO: return titulo + ": " + Math.min(progresso, alvo) + "/" + alvo + " MINERIOS";
            case TROCA: return "";
            default: return titulo + ": " + Math.min(progresso, alvo) + "/" + alvo + " VAPORIZADOS  GOLPES " + golpes + "/" + GOLPES_MAX;
        }
    }
    boolean terminado() { return falhou || cumprido; }

    // ---- eventos do combate ----
    void abate() { if (tipo == Tipo.ABATES && !terminado() && ++progresso >= alvo) cumprido = true; }
    void vaporizou(int n) { if (tipo == Tipo.VAPORIZAR && !terminado() && (progresso += n) >= alvo) cumprido = true; }
    /** Piloto levou um golpe em qualquer momento do contrato. */
    void golpe() { if ((tipo == Tipo.ABATES || tipo == Tipo.VAPORIZAR) && !terminado() && ++golpes > GOLPES_MAX) falhar("LEVOU " + golpes + " GOLPES"); }
    /** Reliquia do alvo procurado foi recolhida. */
    void minerio() { if (tipo == Tipo.MINERACAO && !terminado() && ++progresso >= alvo) cumprido = true; }
    void recuperou() { if (tipo == Tipo.CACADA && !terminado()) cumprido = true; }
    void danoNoIntegrante() { if (tipo == Tipo.SEM_DANO_INTEGRANTE) falhar("LEVOU DANO DO INTEGRANTE"); }
    /** Um integrante caiu: resolve os contratos do integrante; o dourado que dependia de outra coisa expira. */
    void integranteDerrotado(double segundos) {
        if (terminado()) return;
        if (tipo == Tipo.SEM_DANO_INTEGRANTE) cumprido = true;
        else if (tipo == Tipo.INTEGRANTE_RAPIDO) { if (segundos <= alvo) cumprido = true; else falhar("INTEGRANTE CAIU EM " + (int) segundos + "s"); }
        else if (dourado()) falhar("O INTEGRANTE CAIU ANTES");
    }
    void danoNoChefe() { if (tipo == Tipo.SEM_DANO_CHEFE) falhar("LEVOU DANO NO CHEFE"); }
    /** Chefe caiu: resolve quem depende dele; quem nao chegou no alvo expira. */
    void chefeDerrotado(double segundos) {
        if (terminado()) return;
        if (tipo == Tipo.SEM_DANO_CHEFE) cumprido = true;
        else if (tipo == Tipo.CHEFE_RAPIDO) { if (segundos <= alvo) cumprido = true; else falhar("CHEFE CAIU EM " + (int) segundos + "s"); }
        else falhar(tipo == Tipo.CACADA ? "O ALVO ESCAPOU" : tipo == Tipo.MINERACAO ? "FALTOU MINERIO" : "O CHEFE CAIU ANTES");
    }

    // ---- patente do piloto ----
    static final String[] PATENTES = {"RECRUTA", "CADETE", "CACADOR", "VETERANO", "AS", "LENDA DO METAL"};
    static final int[] XP_PATENTE = {0, 300, 900, 2000, 3600, 6000};
    static int patente(int xp) { int p = 0; for (int i = 0; i < XP_PATENTE.length; i++) if (xp >= XP_PATENTE[i]) p = i; return p; }

    // ---- requisitos das naves bloqueadas (indice = modelo) ----
    static final int[] TROFEUS_NAVE = {0, 1, 3, 5, 8}, PATENTE_NAVE = {0, 1, 2, 3, 5};
    static final String[] CONTRATO_NAVE = {"", "FANTASMA", "ESCUDO DE OURO", "FURIA VERDE", "FORJA FINAL"};
    /** Cacada da nave: um alvo procurado aparece no setor e solta a reliquia que libera a nave. */
    static final String[] ALVO_NAVE = {"", "O ESPECTRO", "A HARPIA DOURADA", "O CARNICEIRO VERDE", "O FERREIRO"};
    static final String[] ARTIGO = {"", "o", "a", "a", "o"};
    static final String[] RELIQUIA_NAVE = {"", "VEU FANTASMA", "PENA DE OURO", "PRESA VERDE", "CORACAO DA FORJA"};
    /** Resistencia do alvo: quanto melhor a nave, mais duro o procurado. */
    static final int[] FORCA_ALVO = {1, 5, 6, 7, 9};
    static Color corReliquia(int nave) { switch (nave) { case 1: return new Color(150, 120, 255); case 2: return new Color(255, 210, 80); case 3: return new Color(110, 240, 120); default: return new Color(255, 120, 60); } }
    static Contrato daNave(int modelo) {
        if (modelo < 1 || modelo > 4) return null;
        int[] cr = {0, 1500, 1800, 2500, 5000}, xp = {0, 400, 500, 700, 1200};
        return new Contrato(Tipo.CACADA, 0, cr[modelo], xp[modelo], modelo, CONTRATO_NAVE[modelo]);
    }
    static Contrato troca(int modelo) { return new Contrato(Tipo.TROCA, 0, 0, 0, modelo, "ENTREGA: " + RELIQUIA_NAVE[modelo]); }

    /** Contrato dourado da banda: curto e duro. Depois do ultimo integrante, o alvo e o chefe. */
    static Contrato dourado(int banda, boolean chefeAFrente, boolean campoAFrente, int chefes, Random r) {
        Contrato c;
        if (chefeAFrente) c = r.nextBoolean() ? new Contrato(Tipo.SEM_DANO_CHEFE, 0, 1500 + chefes * 200, 350, -1, "DOURADO")
                : new Contrato(Tipo.CHEFE_RAPIDO, Math.max(30, 60 - chefes * 4), 1300 + chefes * 180, 320, -1, "DOURADO");
        else switch (r.nextInt(campoAFrente ? 4 : 3)) {
            case 0: c = new Contrato(Tipo.SEM_DANO_INTEGRANTE, 0, 1100 + chefes * 150, 280, -1, "DOURADO"); break;
            case 1: c = new Contrato(Tipo.INTEGRANTE_RAPIDO, Math.max(25, 50 - chefes * 3), 1000 + chefes * 140, 260, -1, "DOURADO"); break;
            case 2: { int a = 14 + chefes * 3; c = new Contrato(Tipo.ABATES, a, 900 + a * 20, 240, -1, "DOURADO"); break; }
            default: { int q = 3 + chefes / 3; c = new Contrato(Tipo.MINERACAO, q, 900 + q * 160, 240, -1, "DOURADO"); }
        }
        c.banda = banda; return c;
    }
    /** Tres contratos comuns sorteados para o setor, mais o contrato de nave que estiver liberado. */
    static List<Contrato> ofertas(int setor, int chefes, boolean reator, Random r) { return ofertas(setor, chefes, reator, r, false); }
    /** campoAFrente: ainda ha planeta com anel (campo de meteoros) neste sistema. */
    /** Missoes de cada universo: o mesmo tipo de servico, com o nome e o clima da banda dona do sistema.
     *  Ordem: limpeza, sem dano no chefe, chefe rapido, vaporizar, garimpo. */
    static final String[][] MISSOES = {
        {"MAR DE TINTA", "SEM TENTACULOS", "MARE ALTA", "FUNDO DO POCO", "PEROLA NEGRA"},
        {"LEI DO CROMO", "DEUSES DE ACO", "ESPORA ELETRICA", "OLHO VERMELHO", "ACO DE FORNALHA"},
        {"ABORDAGEM", "SALVA DE CANHOES", "VENTO SOLAR", "ESCRAVOS DO VACUO", "MARE DE ESTRELAS"},
        {"PISTAO QUENTE", "MORTE NA ESTRADA", "CARTA MARCADA", "BOMBA DE GRAXA", "PUNHO DE FERRUGEM"},
        {"COLMEIA EM CHAMAS", "FIOS CORTADOS", "NUVEM DE ESPOROS", "QUATRO BRACOS", "OVO DE ORION"},
        {"TUMULO ABERTO", "OSSOS DE OBSIDIANA", "FOGO DO SUBSOLO", "COROA DE OSSOS", "CINZAS ETERNAS"},
        {"OLHO DO FURACAO", "PARA-RAIOS", "TROVAO ROLANTE", "NUVEM CARREGADA", "PEDRA DE RAIO"},
        {"NOITE SEM FIM", "ASAS DE BREU", "SANGUE DE LUA", "CRIPTA REAL", "ECLIPSE TOTAL"}};
    static String missao(int setor, int k) { return MISSOES[Math.max(0, Math.min(7, setor))][k]; }
    static List<Contrato> ofertas(int setor, int chefes, boolean reator, Random r, boolean campoAFrente) {
        List<Contrato> l = new ArrayList<>();
        int abates = 30 + chefes * 6 + r.nextInt(10);
        l.add(new Contrato(Tipo.ABATES, abates, 300 + abates * 15, 60 + abates * 3, -1, missao(setor, 0)));
        l.add(new Contrato(Tipo.SEM_DANO_CHEFE, 0, 900 + chefes * 150, 220 + chefes * 30, -1, missao(setor, 1)));
        if (reator && r.nextBoolean()) { int v = 15 + chefes * 3; l.add(new Contrato(Tipo.VAPORIZAR, v, 700 + v * 30, 160 + v * 6, -1, missao(setor, 3))); }
        else { int t = Math.max(35, 75 - chefes * 5); l.add(new Contrato(Tipo.CHEFE_RAPIDO, t, 700 + chefes * 120, 180 + chefes * 25, -1, missao(setor, 2))); }
        if (campoAFrente) { int q = 4 + chefes / 2; l.add(new Contrato(Tipo.MINERACAO, q, 600 + q * 140, 150 + q * 25, -1, missao(setor, 4))); }
        for (int m = 1; m <= 4; m++) if (ProgressoJogo.podeTentarNave(m) && !ProgressoJogo.temReliquia(m)) { l.add(daNave(m)); break; }
        l.addAll(0, trocas());
        return l;
    }
    /** Reliquias guardadas que ainda nao viraram nave: podem ser entregues no quadro. */
    static List<Contrato> trocas() {
        List<Contrato> l = new ArrayList<>();
        for (int m = 1; m <= 4; m++) if (ProgressoJogo.temReliquia(m) && !ProgressoJogo.naveLiberada(m)) l.add(troca(m));
        return l;
    }

    // ---- quadro da base ----
    static void desenharQuadro(Graphics2D g, List<Contrato> ofertas, int cursor, Contrato ativo, String aviso) {
        g.setColor(new Color(4, 6, 12)); g.fillRect(0, 557, 960, 43); // cobre o rodape do hangar
        g.setColor(new Color(4, 8, 14, 225)); g.fillRect(150, 70, 660, 440);
        g.setColor(new Color(110, 90, 50)); g.drawRect(150, 70, 660, 440); g.drawRect(153, 73, 654, 434);
        int xp = ProgressoJogo.xp(), p = patente(xp);
        texto(g, "QUADRO DE CONTRATOS", 170, 100, 18, new Color(255, 206, 98));
        String pat = "PATENTE: " + PATENTES[p] + (p < PATENTES.length - 1 ? "  (XP " + xp + "/" + XP_PATENTE[p + 1] + ")" : "  (XP " + xp + ")");
        texto(g, pat, 790 - largura(g, pat, 12), 100, 12, new Color(110, 222, 245));
        int y0 = 128;
        if (ativo != null) {
            texto(g, "CONTRATO ATIVO: " + ativo.titulo, 170, 140, 15, Color.WHITE);
            texto(g, ativo.descricao(), 170, 162, 13, new Color(190, 205, 220));
            texto(g, "PREMIO: CR " + ativo.premioCR + "  /  XP " + ativo.premioXP, 170, 184, 13, new Color(255, 206, 98));
            texto(g, "Termine este antes de pegar outro. Ele vale ate o chefe do setor cair.", 170, 210, 12, new Color(150, 160, 175));
            y0 = 228;
        }
        for (int i = 0; i < ofertas.size(); i++) {
            int passo = Math.min(82, (478 - y0) / Math.max(1, ofertas.size())), alto = passo - 8, q = passo < 82 ? 3 : 0;
            Contrato c = ofertas.get(i); int y = y0 + i * passo; boolean sel = i == cursor, especial = c.nave >= 0, ouro = c.dourado();
            g.setColor(sel ? new Color(40, 90, 60, 170) : new Color(12, 20, 32, 200)); g.fillRect(168, y, 624, alto);
            g.setColor(sel ? new Color(255, 206, 98) : ouro ? new Color(220, 170, 40) : especial ? new Color(200, 120, 255) : new Color(48, 76, 101)); g.drawRect(168, y, 624, alto);
            if (ouro) { g.setColor(new Color(255, 200, 60, 40)); g.fillRect(169, y + 1, 623, alto - 1); }
            if (c.tipo == Tipo.TROCA || c.tipo == Tipo.CACADA) Item.reliquia(g, 770, y + alto / 2, System.nanoTime() / 1e9, c.nave);
            texto(g, (sel ? "> " : "  ") + c.titulo + (especial ? "   [LIBERA " + Pausa099.nomeNave(c.nave) + "]" : ouro ? "   [VALE SO ATE O PROXIMO ALVO]" : ""), 180, y + 20 - q / 3, 15, especial ? new Color(220, 160, 255) : ouro ? new Color(255, 210, 80) : Color.WHITE);
            texto(g, c.descricao(), 196, y + 41 - q * 2, 13, new Color(190, 205, 220));
            texto(g, c.tipo == Tipo.TROCA ? "ENTER: ENTREGAR" : "CR " + c.premioCR + "   XP " + c.premioXP + (c.tipo == Tipo.CACADA ? "   + RELIQUIA" : c.dourado() ? "   + FRAGMENTO" : ""), 196, y + 60 - q * 3, 12, new Color(255, 206, 98));
        }
        if (aviso != null && !aviso.isEmpty()) texto(g, aviso, 480 - largura(g, aviso, 13) / 2, 470, 13, new Color(255, 206, 98));
        texto(g, ativo != null ? "ESC: VOLTAR" : "SETAS: ESCOLHER    ENTER: ACEITAR    ESC: VOLTAR", 170, 496, 12, new Color(150, 160, 175));
    }
    private static int largura(Graphics2D g, String s, int t) { g.setFont(new Font(Font.MONOSPACED, Font.BOLD, t)); return g.getFontMetrics().stringWidth(s); }
    private static void texto(Graphics2D g, String s, int x, int y, int t, Color c) {
        g.setFont(new Font(Font.MONOSPACED, Font.BOLD, t)); g.setColor(new Color(0, 0, 0, 170)); g.drawString(s, x + 1, y + 1); g.setColor(c); g.drawString(s, x, y);
    }
}
