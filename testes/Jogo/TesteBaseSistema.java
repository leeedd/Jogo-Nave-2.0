package Jogo;

/** A base aliada fica em orbita da estrela: derrubou o integrante, a nave viaja ate ela e depois volta ao planeta. */
public class TesteBaseSistema {
    public static void main(String[] a) throws Exception {
        Fase f = TesteInvasao093.fase(21); f.setSize(960, 600);
        TesteInvasao093.set(f, "miniChefe", new MiniChefe(1)); TesteInvasao093.set(f, "planetaAtual", 0);
        TesteRevisao094.call(f, "derrotarMiniChefe");
        if ((double) TesteInvasao093.get(f, "esperaViagem") <= 0) throw new AssertionError("integrante caiu: deveria preparar a viagem");
        if (TesteInvasao093.get(f, "base") != null) throw new AssertionError("a estacao nao aparece mais dentro do planeta");
        TesteRevisao094.tick(f, 4);
        if (!f.viajando()) throw new AssertionError("deveria estar viajando para a base");
        TesteRevisao094.tick(f, 3);
        if (!(boolean) TesteInvasao093.get(f, "naBase")) throw new AssertionError("deveria ter atracado na base do sistema");
        if (!f.getAliens().isEmpty()) throw new AssertionError("na base nao tem inimigo");
        TesteRevisao094.call(f, "partirDaBase");
        if (!f.viajando()) throw new AssertionError("partindo: viagem de volta ao planeta");
        TesteRevisao094.tick(f, 3);
        if (f.viajando() || (boolean) TesteInvasao093.get(f, "naBase")) throw new AssertionError("deveria estar de volta ao combate");
        // missoes de cada universo
        java.util.List<Contrato> l = Contrato.ofertas(2, 1, false, new java.util.Random(1), false);
        if (!l.get(0).titulo.equals("RUN TO THE HILLS")) throw new AssertionError("missao do universo do Maiden: " + l.get(0).titulo);
        System.out.println("TesteBaseSistema OK");
    }
}
