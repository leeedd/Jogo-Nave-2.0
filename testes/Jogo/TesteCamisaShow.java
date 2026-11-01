package Jogo;

import java.awt.image.BufferedImage;

/** O final comeca na escolha do show, e a camisa preta do piloto ganha o emblema da banda escolhida. */
public class TesteCamisaShow {
    public static void main(String[] a) throws Exception {
        CenaPouso p = new CenaPouso(0);
        if (!p.escolhendo()) throw new AssertionError("o final deve comecar na escolha do show");
        p.mover(-3); p.confirmar();
        if (p.escolhendo() || p.banda() != 4) throw new AssertionError("escolha errada: " + p.banda());
        // sem passe nenhum: zeramento comum, sem tela de escolha
        CenaPouso comum = new CenaPouso(0, 0);
        if (comum.escolhendo() || !comum.comum()) throw new AssertionError("sem passe o final e o zeramento comum, direto");
        CenaPouso um = new CenaPouso(0, 1 << 6);
        if (um.escolhendo() || um.banda() != 6) throw new AssertionError("com um passe so, vai direto para o show dele");
        // so abrem os palcos das reliquias conquistadas; a Lula sempre
        CenaPouso r = new CenaPouso(0, 1 << 3 | 1 << 6);
        if (!r.aberto(0) || !r.aberto(3) || r.aberto(5) || r.palcosAbertos() != 3) throw new AssertionError("palcos abertos errados");
        r.mover(-1); r.confirmar();
        if (!r.escolhendo()) throw new AssertionError("palco fechado nao pode ser escolhido");
        r.mover(-2); r.confirmar();
        if (r.escolhendo() || r.banda() != 3) throw new AssertionError("palco aberto deveria ser escolhido: " + r.banda());
        // reliquias douradas: salvas entre jornadas, um fragmento por integrante; passe so no hangar
        ProgressoJogo.zerarFestivalTeste();
        try {
            if (ProgressoJogo.passesFestival() != 1) throw new AssertionError("comeca so com a Lula aberta");
            Contrato sd = new Contrato(Contrato.Tipo.SEM_DANO_INTEGRANTE, 0, 1, 1, -1, "DOURADO"); sd.banda = 3; sd.integranteDerrotado(10);
            if (!sd.cumprido) throw new AssertionError("sem dano no integrante deveria cumprir");
            Contrato ab = new Contrato(Contrato.Tipo.ABATES, 99, 1, 1, -1, "DOURADO"); ab.banda = 3; ab.integranteDerrotado(10);
            if (!ab.falhou) throw new AssertionError("dourado de abates expira quando o integrante cai");
            for (int k = 1; k < Turne.integrantes(3); k++) ProgressoJogo.adicionarFragmento(3);
            if (ProgressoJogo.temReliquiaBanda(3)) throw new AssertionError("falta um fragmento");
            if (ProgressoJogo.trocarPorPasse(3)) throw new AssertionError("reliquia incompleta nao vira passe");
            ProgressoJogo.adicionarFragmento(3);
            if (!ProgressoJogo.temReliquiaBanda(3)) throw new AssertionError("reliquia do Motorhead deveria se montar");
            if (ProgressoJogo.temPasse(3)) throw new AssertionError("a reliquia sozinha nao abre o palco");
            if (!ProgressoJogo.trocarPorPasse(3) || !ProgressoJogo.temPasse(3)) throw new AssertionError("hangar deveria trocar pelo passe");
            // hangar: so aparece na parada depois de um dourado cumprido, e some ao partir
            Fase f = TesteInvasao093.fase(7);
            java.lang.reflect.Method resolver = Fase.class.getDeclaredMethod("resolverContrato"); resolver.setAccessible(true);
            TesteInvasao093.set(f, "partidaTeste", true);
            Contrato c = new Contrato(Contrato.Tipo.SEM_DANO_INTEGRANTE, 0, 1, 1, -1, "DOURADO"); c.banda = 2; c.cumprido = true;
            TesteInvasao093.set(f, "contrato", c); resolver.invoke(f);
            if (!(boolean) TesteInvasao093.get(f, "hangarPendente")) throw new AssertionError("dourado cumprido chama o contato");
            java.lang.reflect.Method partir = Fase.class.getDeclaredMethod("partirDaBase"); partir.setAccessible(true); partir.invoke(f);
            if ((boolean) TesteInvasao093.get(f, "hangarPendente")) throw new AssertionError("partiu sem entrar: o hangar some");
        } finally { ProgressoJogo.zerarFestivalTeste(); }
        for (int i = 0; i < 400; i++) p.atualizar(.1);
        if (!p.terminou()) throw new AssertionError("a cutscene deveria terminar");
        System.out.println("TesteCamisaShow OK");
    }
}
