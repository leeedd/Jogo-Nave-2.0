package Jogo;

/** Modos de dificuldade selecionados pelas teclas 1, 2 e 3. */
public enum Dificuldade {
    RECRUTA("RECRUTA", 0.78, 0.78, 0.72, 6.5, -1),
    VETERANO("VETERANO", 1.0, 1.0, 1.0, 10.0, 0),
    PESADELO("PESADELO", 1.28, 1.30, 1.30, 14.0, 1);

    public final String nome;
    public final double velocidadeInimigos;
    public final double cadenciaInimigos;
    public final double resistenciaChefe;
    public final double intervaloItens;
    public final int modificadorDano;

    Dificuldade(String nome, double velocidadeInimigos, double cadenciaInimigos,
            double resistenciaChefe, double intervaloItens, int modificadorDano) {
        this.nome = nome;
        this.velocidadeInimigos = velocidadeInimigos;
        this.cadenciaInimigos = cadenciaInimigos;
        this.resistenciaChefe = resistenciaChefe;
        this.intervaloItens = intervaloItens;
        this.modificadorDano = modificadorDano;
    }
    public int ajustarDano(int dano) {
        return Math.max(1, dano + modificadorDano);
    }
}
