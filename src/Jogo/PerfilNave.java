/*
 * Decompiled with CFR 0.152.
 */
package Jogo;

public final class PerfilNave {
    public final String funcao;
    public final int velocidade;
    public final int casco;
    public final int dano;
    public final int defesa;
    public final int escudo;
    public final double intervalo;
    private static final PerfilNave[] TODOS = new PerfilNave[]{new PerfilNave("EQUILIBRADA", 270, 12, 1, 0, 0.28, 18), new PerfilNave("BATEDORA", 340, 9, 1, 0, 0.2, 14), new PerfilNave("SUPORTE", 295, 11, 1, 0, 0.22, 26), new PerfilNave("ARTILHARIA", 225, 16, 2, 0, 0.42, 16), new PerfilNave("LENDARIA", 310, 16, 2, 1, 0.26, 22)};

    private PerfilNave(String string, int n, int n2, int n3, int n4, double d, int n5) {
        this.funcao = string;
        this.velocidade = n;
        this.casco = n2;
        this.dano = n3;
        this.defesa = n4;
        this.intervalo = d;
        this.escudo = n5;
    }

    public static PerfilNave obter(int n) {
        return TODOS[Math.max(0, Math.min(4, n))];
    }
}

