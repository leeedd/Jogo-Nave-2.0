package Jogo;

/**
 * Catalogo do mercado: pecas compradas uma vez (permanentes) e equipadas em 4 encaixes por nave.
 * Cada peca tem um lado bom e um lado ruim; as exclusivas so encaixam na nave delas.
 */
final class Pecas {
    static final int ARMA = 0, MOTOR = 1, BLINDAGEM = 2, MODULO = 3;
    static final String[] ENCAIXES = {"ARMA", "MOTOR", "BLINDAGEM", "MODULO"};

    final int id, encaixe, nave, preco;
    final String nome, bom, ruim;
    final int dano, casco, defesa;
    final double recarga, velocidade, truqueRecarga, truqueDuracao, capsulas;
    final boolean ima, scanner;

    private Pecas(int id, int encaixe, int nave, int preco, String nome, String bom, String ruim,
                  int dano, int casco, int defesa, double recarga, double velocidade,
                  double truqueRecarga, double truqueDuracao, double capsulas, boolean ima, boolean scanner) {
        this.id = id; this.encaixe = encaixe; this.nave = nave; this.preco = preco; this.nome = nome; this.bom = bom; this.ruim = ruim;
        this.dano = dano; this.casco = casco; this.defesa = defesa; this.recarga = recarga; this.velocidade = velocidade;
        this.truqueRecarga = truqueRecarga; this.truqueDuracao = truqueDuracao; this.capsulas = capsulas; this.ima = ima; this.scanner = scanner;
    }

    /** nave -1: serve em todas. recarga/velocidade/truque: multiplicadores. */
    static final Pecas[] TODAS = {
        new Pecas(0, ARMA, -1, 900, "CANHAO PERFURANTE", "+1 dano", "Tiro 15% mais lento", 1, 0, 0, 1.15, 1, 1, 1, 1, false, false),
        new Pecas(1, ARMA, -1, 900, "METRALHADORA RAPIDA", "Tiro 30% mais rapido", "Nave 5% mais lenta", 0, 0, 0, .70, .95, 1, 1, 1, false, false),
        new Pecas(2, MOTOR, -1, 700, "MOTOR TURBO", "Nave 18% mais rapida", "-2 casco", 0, -2, 0, 1, 1.18, 1, 1, 1, false, false),
        new Pecas(3, MOTOR, -1, 700, "MOTOR BLINDADO", "+3 casco", "Nave 8% mais lenta", 0, 3, 0, 1, .92, 1, 1, 1, false, false),
        new Pecas(4, BLINDAGEM, -1, 800, "PLACAS DE CERAMICA", "-1 dano recebido", "Nave 7% mais lenta", 0, 0, 1, 1, .93, 1, 1, 1, false, false),
        new Pecas(5, BLINDAGEM, -1, 1000, "CASCO REFORCADO", "+4 casco", "Tiro 8% mais lento", 0, 4, 0, 1.08, 1, 1, 1, 1, false, false),
        new Pecas(6, MODULO, -1, 600, "IMA DE CAPSULAS", "Atrai capsulas de energia", "Nenhum", 0, 0, 0, 1, 1, 1, 1, 1, true, false),
        new Pecas(7, MODULO, -1, 600, "SCANNER DE SEGREDOS", "Revela cofres e nucleos", "Nenhum", 0, 0, 0, 1, 1, 1, 1, 1, false, true),
        new Pecas(8, MODULO, -1, 900, "BATERIA DE PODER", "Capsulas rendem 50% mais", "-1 casco", 0, -1, 0, 1, 1, 1, 1, 1.5, false, false),
        new Pecas(9, MOTOR, 0, 1200, "TURBINAS GEMEAS", "Impulso recarrega 40% antes", "Nave 4% mais lenta", 0, 0, 0, 1, .96, .6, 1, 1, false, false),
        new Pecas(10, BLINDAGEM, 1, 1200, "ASAS FANTASMA", "Camuflagem dura o dobro", "-1 casco", 0, -1, 0, 1, 1, 1, 2, 1, false, false),
        new Pecas(11, MODULO, 2, 1200, "HALO DA VALQUIRIA", "Escudo recarrega 40% antes", "Tiro 5% mais lento", 0, 0, 0, 1.05, 1, .6, 1, 1, false, false),
        new Pecas(12, ARMA, 3, 1200, "TORRE DE OBUS", "+2 dano", "Tiro 25% mais lento", 2, 0, 0, 1.25, 1, 1, 1, 1, false, false),
        new Pecas(13, MODULO, 4, 1500, "CORACAO DE FORJA", "Furia dura 50% mais, +1 dano", "-2 casco", 1, -2, 0, 1, 1, 1, 1.5, 1, false, false),
    };

    /** Icone da peca: pecas_icones.png (grade 7x2, na ordem do catalogo) ou peca_N.png avulso. */
    private static final java.awt.image.BufferedImage[] ICONES = new java.awt.image.BufferedImage[14];
    static {
        java.awt.image.BufferedImage folha = ler("pecas_icones.png");
        for (int i = 0; i < ICONES.length; i++) {
            java.awt.image.BufferedImage avulso = ler("peca_" + i + ".png");
            if (avulso != null) ICONES[i] = avulso;
            else if (folha != null) { int cw = folha.getWidth() / 7, ch = folha.getHeight() / 2; ICONES[i] = folha.getSubimage(i % 7 * cw, i / 7 * ch, cw, ch); }
        }
    }
    private static java.awt.image.BufferedImage ler(String n) { java.net.URL u = Pecas.class.getResource("/ImagemIcon/" + n); if (u == null) return null; try { return javax.imageio.ImageIO.read(u); } catch (java.io.IOException e) { return null; } }
    static boolean temIcone(int id) { return id >= 0 && id < ICONES.length && ICONES[id] != null; }
    static void icone(java.awt.Graphics2D g, int id, int x, int y, int lado) {
        if (!temIcone(id)) return;
        java.awt.Graphics2D a = (java.awt.Graphics2D) g.create();
        a.setRenderingHint(java.awt.RenderingHints.KEY_INTERPOLATION, java.awt.RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        a.drawImage(ICONES[id], x, y, lado, lado, null); a.dispose();
    }

    static Pecas obter(int id) { return id >= 0 && id < TODAS.length ? TODAS[id] : null; }
    boolean serveEm(int modelo) { return nave < 0 || nave == modelo; }

    /** Pecas de um encaixe que a nave pode usar, gerais primeiro e exclusivas depois. */
    static java.util.List<Pecas> doEncaixe(int encaixe, int modelo) {
        java.util.List<Pecas> l = new java.util.ArrayList<>();
        for (Pecas p : TODAS) if (p.encaixe == encaixe && p.nave < 0) l.add(p);
        for (Pecas p : TODAS) if (p.encaixe == encaixe && p.nave == modelo) l.add(p);
        return l;
    }
}
