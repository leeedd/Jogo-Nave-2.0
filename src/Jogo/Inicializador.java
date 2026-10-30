package Jogo;

import javax.swing.SwingUtilities;

/** Inicializacao atual: tela 16 bits; menu historico opcional pela tecla M/menu. */
public final class Inicializador {
    private Inicializador() { }
    public static void main(String[] args) {
        SomJogo.iniciar();
        // Prepara texturas antes da simulacao: evita pausas na primeira luta.
        for (int id=1; id<=8; id++) for (int forma=1; forma<=2; forma++)
            Sprites16Bits.carregarAnimado(id, forma, 0);
        SwingUtilities.invokeLater(TelaInicial::abrir);
    }
}
