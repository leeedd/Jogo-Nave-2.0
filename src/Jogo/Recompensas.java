package Jogo;

/** Catalogo unico: ordem da campanha, itens exclusivos e musica por setor. */
public final class Recompensas {
    private Recompensas() { }

    public static Item.Tipo tipo(int chefe) {
        switch (chefe) {
            case 8: return Item.Tipo.PEROLA_ABISSAL;
            case 2: return Item.Tipo.EMBLEMA_HALFORD;
            case 3: return Item.Tipo.INSIGNIA_EDDIE;
            case 4: return Item.Tipo.CHAPEU_LEMMY;
            case 5: return Item.Tipo.PALHETA_JAMES;
            case 6: return Item.Tipo.MICROFONE_TOM;
            case 7: return Item.Tipo.MEDALHA_TESTAMENT;
            case 1: return Item.Tipo.OCULOS_OZZY;
            default: throw new IllegalArgumentException("chefe invalido: " + chefe);
        }
    }
    public static int identificador(Item.Tipo tipo) {
        switch (tipo) {
            case PEROLA_ABISSAL: return 8;
            case EMBLEMA_HALFORD: return 2;
            case INSIGNIA_EDDIE: return 3;
            case CHAPEU_LEMMY: return 4;
            case PALHETA_JAMES: return 5;
            case MICROFONE_TOM: return 6;
            case MEDALHA_TESTAMENT: return 7;
            case OCULOS_OZZY: return 1;
            default: return 0;
        }
    }
    public static String nome(int chefe) {
        switch (chefe) {
            case 8: return "PEROLA ABISSAL";
            case 2: return "EMBLEMA CROMADO";
            case 3: return "INSIGNIA DO CORSARIO";
            case 4: return "OCULOS DE SUCATA";
            case 5: return "PALHETA DE HEXAR";
            case 6: return "COROA DE MORGATH";
            case 7: return "ESFERA DA TORMENTA";
            case 1: return "ANEL DE MALACHAR";
            default: throw new IllegalArgumentException("chefe invalido: " + chefe);
        }
    }
    public static String habilidade(int chefe) {
        switch (chefe) {
            case 8: return "+2 CASCO E ESCUDO";
            case 2: return "-1 DANO EM CADA IMPACTO";
            case 3: return "ESCUDO EXPLOSIVO";
            case 4: return "+22% CADENCIA DE TIRO";
            case 5: return "RIFF TRIPLO A CADA 4 TIROS";
            case 6: return "DISPERSAO COM 5 MISSEIS";
            case 7: return "ESCUDO DE 27 SEGUNDOS";
            case 1: return "ONDA SONICA NO NEW GAME+";
            default: throw new IllegalArgumentException("chefe invalido: " + chefe);
        }
    }
    /** Musica de cada chefe: musicas/chefe_N.wav se o jogador tiver; senao a trilha do jogo. */
    public static String trilhaDoChefe(int chefe) {
        if (chefe < 1 || chefe > 10) throw new IllegalArgumentException("chefe invalido: " + chefe);
        return chefe == 9 ? "chefe_van" : chefe == 10 ? "chefe_final" : "chefe_" + chefe;
    }
    public static String trilhaDoSetor(int chefesDerrotados) {
        int[] ordem = {8, 2, 3, 4, 5, 6, 7, 1};
        int indice = Math.max(0, Math.min(ordem.length - 1, chefesDerrotados));
        return trilhaDoChefe(ordem[indice]);
    }
}
