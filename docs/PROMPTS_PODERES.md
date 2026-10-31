# Metal Space: prompts dos poderes e ataques

Tudo aqui ainda e desenhado em codigo. Cole um por vez no GPT e mande o PNG como ARQUIVO.
Entre parenteses: o nome do arquivo que o jogo vai usar.
Regra geral: fundo PRETO liso (efeitos brilham melhor), tira horizontal de 4 quadros do mesmo
tamanho, voando para a ESQUERDA quando for ataque inimigo e para a DIREITA quando for do jogador.

---

## PARTE A: PODERES DO JOGADOR (ganhos dos chefes)
Ja tem arte: Tentaculos Abissais (Lula) e Espiritos Ancestrais (Chuck). Faltam estes:

### A1. Painkiller (Rob) - moto de escolta (poder_painkiller.png)
```
Pixel art 16-bit estilo arcade, fundo preto liso, tira horizontal com 4 quadros do mesmo tamanho: uma moto espacial cromada e preta estilo Judas Priest Painkiller, com lâminas de serra no lugar das rodas e chamas laranja saindo do escapamento, vista de lado e virada para a DIREITA, sem piloto. Quadro 1: andando. Quadro 2: serras girando mais rápido. Quadro 3: investindo para a direita com rastro de fogo longo. Quadro 4: freando, faíscas. Mesma escala e posição.
```

### A2. Espada do Eddie (Bruce) - meia-lua de corte (poder_espada.png)
```
Pixel art 16-bit estilo arcade, fundo preto liso, tira horizontal com 4 quadros do mesmo tamanho: uma onda de corte em forma de MEIA-LUA verde-esmeralda brilhante, fina e afiada, como o golpe de sabre do Zero de Mega Man X, voando para a DIREITA. Quadro 1: lâmina se formando, fina. Quadro 2: meia-lua completa com miolo branco. Quadro 3: meia-lua maior com rastro. Quadro 4: se desfazendo em faíscas verdes.
```

### A3. As de Espadas (Lemmy) - carta bumerangue (poder_carta.png)
```
Pixel art 16-bit, fundo preto liso, tira horizontal com 4 quadros do mesmo tamanho: uma carta de baralho ÁS DE ESPADAS gigante com borda dourada e chamas laranja nas pontas, girando no ar (quadro 1 de frente, quadro 2 de lado fina, quadro 3 de costas com caveira, quadro 4 de lado de novo). Brilho suave, contorno marcado.
```

### A4. Ride the Lightning (James) - raio em cadeia (poder_raio.png)
```
Pixel art 16-bit, fundo preto liso, tira horizontal com 4 quadros do mesmo tamanho: um raio elétrico azul e branco em zigue-zague, horizontal, que vai de um lado ao outro do quadro, com faíscas e pequenos arcos nas pontas. Cada quadro com um zigue-zague diferente, para piscar. Miolo branco, borda azul-elétrico.
```
E o impacto do raio (poder_raio_impacto.png):
```
Pixel art 16-bit, fundo preto liso, tira horizontal com 4 quadros do mesmo tamanho: explosão elétrica pequena e redonda, azul e branca, com arcos saindo para fora, crescendo e sumindo.
```

### A5. Hell Awaits (Tom) - orbes de fogo e pentagrama (poder_orbe.png, poder_pentagrama.png)
```
Pixel art 16-bit, fundo preto liso, tira horizontal com 4 quadros do mesmo tamanho: uma esfera de fogo do inferno vermelha e laranja com rosto de caveira sutil dentro, chamas tremulando em volta. Quadros com as chamas em posições diferentes.
```
```
Pixel art 16-bit, fundo preto liso, 1 imagem: pentagrama vermelho brilhante dentro de um círculo com runas, visto de cima, linhas de fogo, levemente transparente no meio.
```

### A6. Prince of Darkness (Ozzy) - revoada de morcegos (poder_revoada.png)
```
Pixel art 16-bit, fundo preto liso, tira horizontal com 4 quadros do mesmo tamanho: uma nuvem de pequenos morcegos roxos e pretos com olhos vermelhos voando juntos em formação de redemoinho, batendo as asas (cada quadro com as asas em posição diferente). Névoa roxa leve.
```

---

## PARTE B: PROJETEIS DOS CHEFES
Ja tem arte: Ozzy (morcego), Lemmy (onda e as de espadas). Faltam:

### B1. Rob Halford - missil de fogo (tiro_rob.png)
```
Pixel art 16-bit, fundo preto liso, tira horizontal com 4 quadros do mesmo tamanho: míssil cromado com ponta de lâmina estilo Painkiller voando para a ESQUERDA, rastro de fogo laranja que muda a cada quadro.
```
### B2. Bruce e Eddie - lanca de energia (tiro_bruce.png)
```
Pixel art 16-bit, fundo preto liso, tira horizontal com 4 quadros do mesmo tamanho: projétil de energia ciano em forma de lança com caveira do Eddie na ponta, voando para a ESQUERDA, rastro brilhante.
```
### B3. James Hetfield - esfera eletrica (tiro_james.png)
```
Pixel art 16-bit, fundo preto liso, tira horizontal com 4 quadros do mesmo tamanho: esfera de eletricidade azul com raios pequenos estalando em volta, pulsando, núcleo branco.
```
### B4. Tom Araya - estilhaco de fogo (tiro_tom.png)
```
Pixel art 16-bit, fundo preto liso, tira horizontal com 4 quadros do mesmo tamanho: gota de fogo vermelho-sangue caindo como chuva, com chama em cima e brasa embaixo, tremulando.
```
### B5. Chuck Billy - pena de trovao (tiro_chuck.png)
```
Pixel art 16-bit, fundo preto liso, tira horizontal com 4 quadros do mesmo tamanho: pena indígena turquesa e branca envolta em raio dourado, girando, voando para a ESQUERDA.
```

---

## PARTE C: ATAQUES DOS INTEGRANTES
Uma prancha por banda: os ataques de cada integrante lado a lado.
Use o mesmo modelo, trocando a lista entre colchetes.

Modelo:
```
Pixel art 16-bit estilo arcade, fundo preto liso, uma folha com [N] projéteis de ataque lado a lado, cada um com 4 quadros de animação empilhados na vertical, todos voando para a ESQUERDA: [LISTA]. Brilho intenso, miolo branco, contorno marcado.
```

- [ ] Metallica (ataques_metallica.png), N=3: 1) Kirk: fantasma-caveira roxo saindo de uma onda de energia; 2) Lars: prato de bateria dourado girando como serra com faíscas; 3) Trujillo: onda grave azul em arco, como parênteses
- [ ] Judas Priest (ataques_priest.png), N=4: 1) Glenn: rajada de energia laranja em espiral; 2) Richie: bola de fogo em forma de Flying V; 3) Ian: onda grave laranja; 4) Scott: prato de bateria em chamas girando
- [ ] Iron Maiden (ataques_maiden.png), N=5: 1) Steve: rajada de notas azuis como tiros de metralhadora; 2) Dave Murray: raio ciano reto; 3) Adrian: raio ciano em zigue-zague; 4) Janick: rajada em espiral girando; 5) Nicko: prato dourado girando como serra
- [ ] Motorhead (ataques_motorhead.png), N=2: 1) Phil: rajada vermelha com ás de espadas; 2) Mikkey: prato em chamas girando
- [ ] Slayer (ataques_slayer.png), N=3: 1) Kerry: lâmina vermelha com espinhos; 2) Gary: serra vermelha; 3) Dave Lombardo: prato vermelho girando como serra
- [ ] Testament (ataques_testament.png), N=3: 1) Eric: rajada verde em forma de raio; 2) Alex: notas musicais verdes em leque; 3) Steve Di Giorgio: onda grave verde
- [ ] Banda do Ozzy (ataques_ozzy.png), N=4: 1) Tony Iommi: cruz roxa sombria girando; 2) Geezer: onda grave roxa; 3) Bill Ward: prato antigo girando; 4) Zakk: rajada dourada com alvo preto e branco
- [ ] Abismo (ataques_abismo.png), N=2: 1) Enguia: dente de agulha ciano luminoso; 2) Cria da Lula: jato de tinta roxa
