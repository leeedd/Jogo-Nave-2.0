# Metal Space: prompts de arte pendentes

Cole cada prompt no GPT e me mande a imagem. Eu corto, limpo o fundo e encaixo.
Entre parenteses: o nome do arquivo que o jogo vai usar.

---

## 1. Retrato do capitao do radio pirata (radio_capitao.png)
Prioridade alta: o retrato atual e provisorio.

```
Pixel art 16-bit estilo arcade, retrato de busto de um capitão pirata espacial falando num rádio, tira horizontal com 4 quadros QUADRADOS do mesmo tamanho, fundo preto liso. Rosto marcado com cicatriz, tapa-olho cibernético com luz vermelha, chapéu de pirata preto com caveira dourada, barba grisalha curta, gola de casaco de couro preto, fone de rádio com microfone perto da boca. Quadro 1: boca fechada. Quadro 2: boca aberta falando. Quadro 3: boca meio aberta. Quadro 4: a mesma imagem tomada por estática e chiado de TV. Tons escuros com detalhes em vermelho e dourado, contorno bem marcado.
```

## 2. Moldura da janela do radio (radio_moldura.png)

```
Pixel art 16-bit, moldura de interface de rádio militar retangular e comprida (proporção 5 por 1), metal escuro enferrujado com rebites, uma tela pequena quadrada à esquerda para o retrato, uma luz vermelha de transmissão no canto superior direito, grade de alto-falante, área vazia escura no meio para texto. Fundo branco liso, sem texto.
```

## 3. Sentinela de olho mecanico (sentinela_olho.png + sentinela_iris.png)
Substitui a sentinela atual, que parece um pirata grande e nao tem expressao.

```
Pixel art 16-bit estilo arcade Metal Slug, fundo branco liso, tira horizontal com 4 quadros do mesmo tamanho, vista lateral virada para a ESQUERDA. Uma sentinela espacial guardiã, estação de combate pesada e simétrica (não é uma nave de caça): no centro um OLHO MECÂNICO GIGANTE com íris vermelha brilhante e lente de vidro, cercado por um anel de blindagem preta com detalhes dourados; duas torres de canhão nas pontas de cima e de baixo; placas de blindagem em camadas como pétalas. Quadro 1: repouso, placas fechadas, olho meio fechado. Quadro 2: placas abrindo, olho arregalado e brilhando, torres apontando. Quadro 3: disparo, raio vermelho saindo do olho, torres com clarão. Quadro 4: danificada, olho rachado soltando faíscas, placas quebradas e fumaça. Paleta preto, dourado escuro e vermelho, diferente das naves piratas cinza. Mesma escala e mesma posição nos 4 quadros.
```

Iris separada (para a pupila seguir a nave):

```
Pixel art 16-bit, fundo branco liso, 2 objetos lado a lado: 1) a íris vermelha mecânica do olho da sentinela, redonda, com pupila preta em fenda e reflexo de luz, vista de frente; 2) a mesma íris rachada e piscando. Contorno bem marcado.
```

## 4. Naves piratas evoluidas (pirata_evo_N.png)
Uma prancha por classe. Troque o trecho entre colchetes por cada uma:
- caça vermelho interceptador
- nave branca de esquadrão
- tanque cinza com canhão
- nave azul de laser
- kamikaze laranja
- nave grande de canhões

```
Pixel art 16-bit estilo arcade, fundo branco liso, a mesma nave pirata espacial [caça vermelho interceptador] vista de lado e virada para a ESQUERDA, tira horizontal com 3 versões do mesmo tamanho lado a lado. Versão 1: a nave normal. Versão 2: a mesma nave com placas de blindagem extras parafusadas e um canhão a mais. Versão 3: versão elite, com escudo de energia vermelho translúcido em volta, canhões duplos e marcas de caveira pirata. Mesmo estilo, mesma escala e mesma posição nas 3.
```

## 5. Brilho do casco adaptado (casco_fogo.png, casco_gelo.png)

```
Pixel art 16-bit, fundo preto liso, duas tiras horizontais de 4 quadros cada: 1) aura de energia laranja e vermelha em forma de escudo oval tremulando, como casco resistente a fogo; 2) a mesma aura em azul-claro e branco com cristais de gelo pequenos, como casco resistente a gelo. Brilho suave, centro vazio para a nave aparecer por dentro.
```

## 6. Alvos procurados das cacadas (procurado_N.png)
Opcional: hoje o procurado e uma nave comum com a mira vermelha por cima.

```
Pixel art 16-bit estilo arcade, fundo branco liso, 4 naves inimigas de elite lado a lado do mesmo tamanho, vistas de lado e viradas para a ESQUERDA, cada uma com aura própria: 1) O ESPECTRO, nave negra semitransparente com névoa roxa e caveira na proa; 2) A HARPIA DOURADA, nave com asas de penas douradas e garras; 3) O CARNICEIRO VERDE, nave tosca de sucata com presas e veias verdes brilhantes; 4) O FERREIRO, nave pesada de forja com bigorna na frente e brasas laranja. Contorno escuro marcado.
```

## 7. Mapa estelar (mapa_estrelas.png, mapa_planetas.png, mapa_nave.png)
Hoje o mapa e todo desenhado em codigo: estrela e uma bolinha, planetas sao esferas geradas,
a nave e um triangulo.

Estrelas (uma por sistema, nas cores dos setores):
```
Pixel art 16-bit, fundo preto liso, 8 estrelas lado a lado do mesmo tamanho, vistas de longe como num mapa espacial, cada uma com coroa de luz e raios: 1) azul, 2) laranja-fogo, 3) turquesa cristalina, 4) amarelo-ferrugem, 5) branco-gelo, 6) verde-ácido, 7) roxa com vórtice, 8) vermelha pulsante. Brilho suave, contorno limpo.
```

Planetas (12 tipos variados, para sortear nos sistemas):
```
Pixel art 16-bit, fundo preto liso, grade de 12 planetas pequenos do mesmo tamanho, estilo mapa de jogo espacial: rochoso cinza com crateras, lava com rachaduras, gelo azul, oceano, gasoso listrado tipo Júpiter, cristalino roxo, tóxico verde, deserto laranja, metálico com cidades acesas, sombrio com névoa, e 2 planetas com ANEL tipo Saturno (um dourado, um gelo). Iluminação vindo da esquerda, contorno marcado.
```

Marcador da nave no mapa:
```
Pixel art 16-bit, fundo preto liso, 4 quadros pequenos do mesmo tamanho: ícone de mapa da nave do jogador vista de cima, caça vermelho e branco com propulsores azuis, dentro de um anel de mira ciano que pulsa (o anel cresce um pouco a cada quadro). Estilo HUD de jogo arcade.
```

## 8. Meteoros (meteoros.png)
Hoje sao 4 sprites pequenos repetidos; o minerio e o gigante sao desenhados por cima em codigo.

```
Pixel art 16-bit, fundo preto liso, folha de meteoros espaciais do mesmo estilo, lado a lado: 4 meteoros pequenos rochosos diferentes; 2 meteoros com VEIOS DE MINÉRIO azul-ciano brilhante saindo das rachaduras; 1 METEORO GIGANTE (3 vezes maior) com crateras fundas e rachaduras incandescentes; 3 fragmentos do gigante quebrado. Iluminação vinda da esquerda, contorno escuro marcado.
```

Minerio coletavel:
```
Pixel art 16-bit, fundo preto liso, 4 quadros do mesmo tamanho: um cristal de minério raro azul-ciano flutuando e girando devagar, com brilho e faíscas pequenas. Contorno marcado.
```

---

## Turne ao contrario (depois do polimento)

### Integrantes das bandas (um por prancha)
Comece pela Metallica: Kirk Hammett, Lars Ulrich e Robert Trujillo.
Troque o trecho entre colchetes. Guitarra: rajada de energia; bateria: pratos voando como serra; baixo: onda grave.

```
Pixel art 16-bit estilo arcade Metal Slug, fundo branco liso, tira horizontal com 4 quadros do mesmo tamanho, corpo inteiro virado para a ESQUERDA, flutuando no espaço. [KIRK HAMMETT, guitarrista do Metallica: cabelo preto cacheado comprido, jaqueta de couro preta, guitarra preta estilo ESP com gráficos de terror]. Quadro 1: repouso, tocando. Quadro 2: preparando o golpe, instrumento erguido e brilhando. Quadro 3: golpe, o ataque saindo do instrumento para a esquerda. Quadro 4: bate-cabeça. Mesma escala e mesma posição nos 4 quadros, contorno escuro marcado.
```

### Criaturas do Abismo (crias da Lula)

```
Pixel art 16-bit estilo arcade, tom sombrio e ameaçador, fundo branco liso, tira horizontal com 4 quadros do mesmo tamanho, vista lateral virada para a ESQUERDA. Criatura espacial monstruosa: [ENGUIA DAS PROFUNDEZAS, corpo longo e escuro azul-petróleo com bioluminescência ciano, mandíbula enorme cheia de dentes de agulha, olhos brancos sem pupila]. Quadro 1: repouso, respirando. Quadro 2: preparando o ataque, boca abrindo e olhos brilhando. Quadro 3: atacando. Quadro 4: ferida, rachada e sangrando luz. Contorno escuro bem marcado, mesma escala e mesma posição nos 4 quadros.
```

---

## Dicas
- Fundo branco liso ou preto liso, sem cenário: facilita o recorte.
- Peça sempre "mesma escala e mesma posição" nos quadros: evita a arte pular na animação.
- Se o GPT colocar texto, barba ou efeito errado, me mande assim mesmo e diga o que ficou errado.
