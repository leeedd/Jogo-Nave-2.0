package Jogo;

import java.awt.image.BufferedImage;

/** O final comeca na escolha do show, e a camisa preta do piloto ganha o emblema da banda escolhida. */
public class TesteCamisaShow {
    public static void main(String[] a) throws Exception {
        CenaPouso p = new CenaPouso(0);
        if (!p.escolhendo()) throw new AssertionError("o final deve comecar na escolha do show");
        p.mover(-3); p.confirmar();
        if (p.escolhendo() || p.banda() != 4) throw new AssertionError("escolha errada: " + p.banda());
        // so abrem os palcos das reliquias conquistadas; a Lula sempre
        CenaPouso r = new CenaPouso(0, 1 << 3);
        if (!r.aberto(0) || !r.aberto(3) || r.aberto(5) || r.palcosAbertos() != 2) throw new AssertionError("palcos abertos errados");
        r.mover(2); r.confirmar();
        if (!r.escolhendo()) throw new AssertionError("palco fechado nao pode ser escolhido");
        r.mover(-2); r.confirmar();
        if (r.escolhendo() || r.banda() != 3) throw new AssertionError("palco aberto deveria ser escolhido: " + r.banda());
        // rota de risco: +1 de risco no setor; o chefe vencido entrega a reliquia da banda
        Fase f = TesteInvasao093.fase(7); int base = f.riscoAtual();
        TesteInvasao093.set(f, "rotaRisco", true);
        if (f.riscoAtual() != Math.min(4, base + 1)) throw new AssertionError("rota de risco deveria subir o risco");
        if (f.reliquiasBanda() != 1) throw new AssertionError("comeca so com a Lula aberta");
        // contratos dourados: um fragmento por integrante; completou todos, a reliquia se monta
        java.util.Random rnd = new java.util.Random(3);
        Contrato d = Contrato.dourado(3, false, false, 2, rnd);
        if (!d.dourado() || d.banda != 3) throw new AssertionError("dourado deveria ser da banda");
        Contrato sd = new Contrato(Contrato.Tipo.SEM_DANO_INTEGRANTE, 0, 1, 1, -1, "DOURADO"); sd.banda = 3; sd.integranteDerrotado(10);
        if (!sd.cumprido) throw new AssertionError("sem dano no integrante deveria cumprir");
        Contrato ab = new Contrato(Contrato.Tipo.ABATES, 99, 1, 1, -1, "DOURADO"); ab.banda = 3; ab.integranteDerrotado(10);
        if (!ab.falhou) throw new AssertionError("dourado de abates expira quando o integrante cai");
        java.lang.reflect.Method resolver = Fase.class.getDeclaredMethod("resolverContrato"); resolver.setAccessible(true);
        for (int k = 0; k < Turne.integrantes(3); k++) {
            Contrato c = new Contrato(Contrato.Tipo.SEM_DANO_INTEGRANTE, 0, 1, 1, -1, "DOURADO"); c.banda = 3; c.cumprido = true;
            TesteInvasao093.set(f, "contrato", c); resolver.invoke(f);
        }
        if ((f.reliquiasBanda() >> 3 & 1) == 0 || f.fragmentosReliquia(3) != Turne.integrantes(3)) throw new AssertionError("reliquia do Motorhead deveria se montar");
        Contrato perdido = new Contrato(Contrato.Tipo.SEM_DANO_INTEGRANTE, 0, 1, 1, -1, "DOURADO"); perdido.banda = 5; perdido.falhar("TESTE");
        TesteInvasao093.set(f, "contrato", perdido); resolver.invoke(f);
        if (((int) TesteInvasao093.get(f, "douradoPerdido") >> 5 & 1) == 0) throw new AssertionError("falhar o dourado perde a reliquia da jornada");
        BufferedImage tira = javax.imageio.ImageIO.read(CenaPouso.class.getResource("/ImagemIcon/final_flavio_sprites.png"));
        BufferedImage com = CenaPouso.estampar(tira, CamisaShow.emblema(1));
        int fw = tira.getWidth() / 12, mudou = 0;
        for (int y = 0; y < tira.getHeight(); y++) for (int x = 6 * fw; x < 7 * fw; x++) if ((tira.getRGB(x, y) & 0xFFFFFF) != (com.getRGB(x, y) & 0xFFFFFF) && (tira.getRGB(x, y) >>> 24) > 200) mudou++;
        if (mudou < 300) throw new AssertionError("a camisa deveria ganhar o emblema: " + mudou);
        for (int x = 0; x < fw; x++) for (int y = 0; y < tira.getHeight(); y++) if (tira.getRGB(x, y) != com.getRGB(x, y) && (tira.getRGB(x, y) >>> 24) > 200) throw new AssertionError("de macacao nao tem estampa");
        for (int i = 0; i < 400; i++) p.atualizar(.1);
        if (!p.terminou()) throw new AssertionError("a cutscene deveria terminar");
        System.out.println("TesteCamisaShow OK");
    }
}
