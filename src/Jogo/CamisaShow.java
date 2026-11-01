package Jogo;

import java.awt.Image;

/** Show escolhido no fim do jogo: o emblema da banda vai para a camisa do piloto e para o telao do palco. */
final class CamisaShow {
    static final int METALLICA = 4, SLAYER = 5;
    private CamisaShow() { }
    static Image emblema(int banda) { try { return Assets.imagem("emblema_banda_" + banda + ".png"); } catch (IllegalStateException e) { return null; } }
}
