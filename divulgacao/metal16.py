"""Trilha original de heavy metal em estilo 16 bits (sintetizada). Uso: metal16.py saida.wav duracao"""
import sys, numpy as np, wave
SR=32000; out_path=sys.argv[1]; DUR=float(sys.argv[2])
OFF=float(sys.argv[3]) if len(sys.argv)>3 else 0
N=int(DUR*SR); mix=np.zeros(N); rng=np.random.default_rng(4)
BPM=160; S16=60/BPM/4  # duracao de uma semicolcheia
def hz(m): return 440*2**((m-69)/12)
def put(x,t,g=1.0):
    i=int((t+OFF)*SR); j=min(N,i+len(x))
    if 0<=i<N: mix[i:j]+=x[:j-i]*g
def saw(f,d):
    t=np.arange(int(d*SR))/SR; return 2*((t*f)%1)-1
def sq(f,d,duty=.5):
    t=np.arange(int(d*SR))/SR; return np.where((t*f)%1<duty,1.,-1.)
def env(n,a=.003,r=.05,sus=1.):
    e=np.ones(n)*sus; ka=max(1,int(a*SR)); e[:ka]=np.linspace(0,1,ka); kr=min(n,int(r*SR)); e[-kr:]*=np.linspace(1,0,kr); return e
def lp(x,k): # passa-baixa simples (media movel) para tirar o chiado da serra
    c=np.cumsum(np.insert(x,0,0)); return (c[k:]-c[:-k])/k if len(x)>k else x
def guitarra(m,d,mudo=False):
    x=sum(saw(hz(m+o)*(1+dt),d) for o,dt in ((0,0),(7,.002),(12,-.003),(0,.004)))
    x=np.tanh(x*(9 if not mudo else 6)); x=np.concatenate([lp(x,3 if mudo else 2),np.zeros(3)])[:len(x)]
    e=env(len(x),r=.03 if mudo else .12); 
    if mudo: e*=np.exp(-np.arange(len(x))/SR*28)
    return x*e*.22
def baixo(m,d): x=sq(hz(m-12),d,.4)*.5+saw(hz(m-12),d)*.5; return np.tanh(x*2)*env(len(x),r=.04)*.25
def bumbo():
    n=int(.18*SR); t=np.arange(n)/SR; f=110*np.exp(-t*28)+45; return np.sin(2*np.pi*np.cumsum(f)/SR)*np.exp(-t*16)*.9
def caixa():
    n=int(.2*SR); t=np.arange(n)/SR; return (rng.uniform(-1,1,n)*.8+np.sin(2*np.pi*190*t)*.5)*np.exp(-t*18)*.55
def chimbal(aberto=False):
    n=int((.22 if aberto else .05)*SR); t=np.arange(n)/SR; x=rng.uniform(-1,1,n); x=x-np.concatenate([[0],x[:-1]]); return x*np.exp(-t*(9 if aberto else 60))*.18
def prato():
    n=int(1.6*SR); t=np.arange(n)/SR; x=rng.uniform(-1,1,n); x=x-np.concatenate([[0],x[:-1]]); return x*np.exp(-t*2.2)*.35
E,G,A,Bb,D,C,Fs,B=40,43,45,46,38,48,42,47
# riff A (2 compassos de 16 semicolcheias): galope em E com respostas
RIFF_A=[(0,E,1,1),(1,E,1,1),(2,E,1,1),(3,G,2,0),(5,E,1,1),(6,E,1,1),(7,A,2,0),(9,E,1,1),(10,E,1,1),(11,Bb,1,0),(12,A,2,0),(14,G,2,0),
        (16,E,1,1),(17,E,1,1),(18,E,1,1),(19,G,2,0),(21,E,1,1),(22,E,1,1),(23,D,2,0),(25,E,1,1),(26,E,1,1),(27,C-12,2,0),(29,B-12,3,0)]
# riff B: mais pesado, acentos sincopados
RIFF_B=[(0,E,2,0),(2,E,1,1),(3,E,1,1),(4,Fs,2,0),(6,E,1,1),(7,E,1,1),(8,G,3,0),(11,Fs,1,0),(12,E,1,1),(13,E,1,1),(14,D,2,0),
        (16,E,2,0),(18,E,1,1),(19,E,1,1),(20,G,2,0),(22,A,2,0),(24,Bb,3,0),(27,A,1,0),(28,G,2,0),(30,Fs,2,0)]
def tocar_riff(riff,t0,compassos,bat=True,dobro=True):
    L=32*S16
    for c in range(compassos//2):
        b=t0+c*L
        for st,m,l,mu in riff:
            put(guitarra(m,l*S16*.95,mu==1),b+st*S16); put(baixo(m,l*S16*.9),b+st*S16)
        if bat:
            for k in range(32):
                tk=b+k*S16
                if dobro or k%4==0: put(bumbo(),tk,.8 if dobro else 1)
                if k%8==4: put(caixa(),tk)
                if k%2==0: put(chimbal(),tk)
            put(prato(),b,.8)
# 0-4.4: drone sombrio e sinos
put(np.tanh(saw(hz(E-12),4.4+OFF)*3)*np.linspace(0,1,int((4.4+OFF)*SR))**2*.12,-OFF)
for k,m in enumerate([64,67,63,62]): x=sq(hz(m),.9,.25)*env(int(.9*SR),r=.6)*.09; put(x,.2+k*1.05)
# 4.4: o raio - acorde aberto de E soando
put(guitarra(E,2.4),4.4,1.3); put(prato(),4.4,1.2); put(bumbo(),4.4,1.2)
# virada de caixa ate 7.0
for k in range(12): put(caixa(),6.0+k*S16*.9,.6+k*.04)
t=7.0
L=32*S16
while t<14.8: tocar_riff(RIFF_A,t,2); t+=L
t=14.8
while t<DUR-OFF-2.6: tocar_riff(RIFF_B,t,2); t+=L
# final: acorde de E segurado com prato
put(guitarra(E,2.6),DUR-OFF-2.6,1.3); put(prato(),DUR-OFF-2.6,1.2); put(bumbo(),DUR-OFF-2.6,1.2)
mix=mix[:N]; mix=np.tanh(mix*1.3)/np.tanh(1.3)
# cara de 16 bits: amostra segurada a 16 kHz e um leve esmagamento de bits
mix=np.repeat(mix[::2],2)[:N]; mix=np.round(mix*2048)/2048
mix*=.92/np.max(np.abs(mix))
w=wave.open(out_path,'wb'); w.setnchannels(1); w.setsampwidth(2); w.setframerate(SR); w.writeframes((mix*32767).astype(np.int16).tobytes()); w.close()
print('ok',DUR)
