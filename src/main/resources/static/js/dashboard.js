document.addEventListener("DOMContentLoaded",()=>{

const mensagem=document.getElementById("mensagem");
const mensagemModal=document.getElementById("mensagemModal");
const containerTimes=document.getElementById("listaTimes");
const clubeSelecionadoDiv=document.getElementById("clubeSelecionado");
const textoEscolha=document.getElementById("textoEscolha");
const btnEsquema=document.getElementById("btnEsquema");
const btnEditar=document.getElementById("btnEditar");

const modalEsquemaEl=document.getElementById("modalEsquema");
const modalJogadoresEl=document.getElementById("modalJogadores");
const modalEscolhaEl=document.getElementById("modalEscolhaJogador");

const token=localStorage.getItem("token");

if(!token){
window.location.href="/";
return;
}

M.Modal.init(
document.querySelectorAll(
"#modalEsquema,#modalJogadores"
),
{dismissible:false}
);

M.Modal.init(
document.querySelectorAll(
"#modalEscolhaJogador"
),
{dismissible:false}
);

modalEscolhaEl.style.display="none";

let clubeSelecionado=false;
let esquemaSelecionado=false;

let clubeId=null;
let esquemaAtual=null;

let jogadoresTime=[];
let jogadoresSelecionados=[];

// escalação já salva (resposta do GET) e modo de edição
let escalacaoAtual=null;
let modoEdicao=false;

btnEsquema.style.display="none";
btnEditar.style.display="none";

modalEsquemaEl.style.display="none";
modalJogadoresEl.style.display="none";


const posicoesEsquemas={

442:[
{top:"90%",left:"50%"},
{top:"70%",left:"15%"},
{top:"70%",left:"85%"},
{top:"80%",left:"35%"},
{top:"80%",left:"65%"},
{top:"45%",left:"20%"},
{top:"45%",left:"40%"},
{top:"45%",left:"60%"},
{top:"45%",left:"80%"},
{top:"20%",left:"40%"},
{top:"20%",left:"60%"}
],

352:[
{top:"90%",left:"50%"},
{top:"75%",left:"30%"},
{top:"75%",left:"50%"},
{top:"75%",left:"70%"},
{top:"50%",left:"15%"},
{top:"55%",left:"35%"},
{top:"55%",left:"50%"},
{top:"55%",left:"65%"},
{top:"50%",left:"85%"},
{top:"25%",left:"40%"},
{top:"25%",left:"60%"}
],

541:[
{top:"90%",left:"50%"},
{top:"70%",left:"10%"},
{top:"75%",left:"30%"},
{top:"75%",left:"50%"},
{top:"75%",left:"70%"},
{top:"70%",left:"90%"},
{top:"40%",left:"20%"},
{top:"55%",left:"40%"},
{top:"55%",left:"60%"},
{top:"40%",left:"80%"},
{top:"24%",left:"50%"}
],

244:[
{top:"90%",left:"50%"},
{top:"80%",left:"30%"},
{top:"80%",left:"70%"},
{top:"50%",left:"20%"},
{top:"60%",left:"35%"},
{top:"60%",left:"65%"},
{top:"50%",left:"80%"},
{top:"20%",left:"35%"},
{top:"35%",left:"35%"},
{top:"35%",left:"65%"},
{top:"20%",left:"65%"}
]

};


const nomesPosicoes={

442:[
"GOL","LAT","LAT",
"ZAG","ZAG",
"MEI","MEI","MEI","MEI",
"ATA","ATA"
],

352:[
"GOL",
"ZAG","ZAG","ZAG",
"LAT",
"MEI","MEI","MEI",
"LAT",
"ATA","ATA"
],

541:[
"GOL",
"LAT",
"ZAG","ZAG","ZAG",
"LAT",
"MEI",
"VOL","VOL",
"MEI",
"ATA"
],

244:[
"GOL",
"ZAG","ZAG",
"MEI",
"VOL","VOL",
"MEI",
"ATA","ATA","ATA","ATA"
]

};


const mapaPosicao={
GOL:["Goleiro"],
LAT:["Lateral"],
ZAG:["Zagueiro"],
VOL:["Volante"],
MEI:["Meia"],
ATA:["Atacante"]
};



/* ---------- AUXILIARES ---------- */

function sessaoExpirada(){
localStorage.removeItem("token");
window.location.href="/";
}

// transforma a resposta de erro do backend em Error com .status e .message
function lerErro(res){
return res.json().catch(()=>({})).then(err=>{
const e=new Error(
err.message||err.mensagem||err.erro||"Erro inesperado"
);
e.status=res.status;
throw e;
});
}

// "5-4-1" ou "541" -> "541"
function normalizarEsquema(v){
return String(v||"").replace(/\D/g,"");
}

function montarFotoJogador(nome){

let pasta=
clubeId===1
?"cruzeiro"
:"atletico";

let arquivo=nome
.toLowerCase()
.normalize("NFD")
.replace(/[\u0300-\u036f]/g,"")
.replace(/\s+/g,"-");

return `/img/jogadores/${pasta}/${arquivo}.png`;

}



/* ---------- CLUBE DO USUARIO ---------- */

fetch("/usuarios/clube",{
headers:{
Authorization:`Bearer ${token}`
}
})
.then(r=>{

if(r.status===401){
sessaoExpirada();
return null;
}

return r.status===204?null:r.json();

})
.then(data=>{

if(!data) return;

clubeSelecionado=true;
clubeId=data.id;

mostrarClube(
data.nome,
data.id===1
?"/img/cruzeiro.svg"
:"/img/atletico-mineiro.svg"
);

containerTimes.style.display="none";
textoEscolha.style.display="none";

// GET /clube/{id}/escalacao:
// 200 => mostra o time salvo e o botão Editar
// 204 => ainda não escalou, mostra o botão Escolher Esquema
carregarEscalacaoSalva();

});



window.selecionarClube=function(id){

fetch("/usuarios/clube",{

method:"POST",

headers:{
"Content-Type":"application/json",
Authorization:`Bearer ${token}`
},

body:JSON.stringify({
clubeId:id
})

})
.then(r=>{

if(r.status===401){
sessaoExpirada();
throw new Error("Sessão expirada");
}

if(!r.ok) return lerErro(r);

return r.json();

})
.then(d=>{

mostrarMensagem(
d.mensagem,
"green"
);

setTimeout(
()=>location.reload(),
1200
);

})
.catch(e=>mostrarMensagem(e.message,"red"));

};



/* ---------- BOTOES: NOVA ESCALACAO / EDITAR ---------- */

btnEsquema.addEventListener("click",()=>{

modoEdicao=false;
mensagemModal.innerHTML="";

});


btnEditar.addEventListener("click",()=>{

modoEdicao=true;
mensagemModal.innerHTML="";

// já deixa marcado o esquema atual da escalação
if(escalacaoAtual){

const radio=document.querySelector(
`input[name="esquema"][value="${normalizarEsquema(escalacaoAtual.esquema)}"]`
);

if(radio) radio.checked=true;

}

M.Modal.getInstance(modalEsquemaEl).open();

});



/* ---------- BUSCAR ESCALACAO SALVA (GET /clube/{id}/escalacao) ---------- */

// mostra o time salvo, guarda os dados para edição e troca os botões
function mostrarEscalacao(data){

escalacaoAtual=data;

btnEsquema.style.display="none";
btnEditar.style.display="inline-block";

exibirEscalacaoSalva(data);

}


// chamada em 3 situações:
// 1) ao abrir a página (200 = já escalou, 204 = ainda não)
// 2) logo depois de salvar ou atualizar a escalação
// 3) quando o POST avisa que a escalação já existe
// fallbackData: resposta do POST/PUT, usada só se o GET falhar
function carregarEscalacaoSalva(fallbackData){

return fetch(`/clube/${clubeId}/escalacao`,{
headers:{
Authorization:`Bearer ${token}`
}
})
.then(r=>{

if(r.status===401){
sessaoExpirada();
throw new Error("Sessão expirada");
}

if(r.status===204) return null;     // ainda não escalou

if(!r.ok) return lerErro(r);

return r.json();

})
.then(data=>{

if(data){
mostrarEscalacao(data);
return;
}

if(fallbackData){
mostrarEscalacao(fallbackData);
return;
}

// não existe escalação: libera o botão de criar
escalacaoAtual=null;
btnEditar.style.display="none";
btnEsquema.style.display="inline-block";

})
.catch(e=>{

if(fallbackData){

// o POST/PUT já tinha dado certo: mostra o que ele devolveu
mostrarEscalacao(fallbackData);
return;

}

mostrarMensagem(e.message,"red");

// não bloqueia o fluxo; o backend valida duplicidade
if(escalacaoAtual){
btnEditar.style.display="inline-block";
}else{
btnEsquema.style.display="inline-block";
}

});

}



/* ---------- ESQUEMA ---------- */

window.salvarEsquema=function(){

const selecionado=
document.querySelector(
'input[name="esquema"]:checked'
);

if(!selecionado){
mensagemModal.innerHTML=
"Selecione um esquema";
return;
}

esquemaAtual=
selecionado.value;


const payload={

esquema442:
esquemaAtual==="442",

esquema352:
esquemaAtual==="352",

esquema541:
esquemaAtual==="541",

esquema244:
esquemaAtual==="244"

};


fetch(
`/clube/${clubeId}/esquema-tatico`,
{
method:"POST",

headers:{
"Content-Type":"application/json",
Authorization:`Bearer ${token}`
},

body:JSON.stringify(
payload
)

})
.then(r=>{

if(r.status===401){
sessaoExpirada();
throw new Error("Sessão expirada");
}

if(!r.ok) return lerErro(r);

return r.json();

})
.then(()=>{

esquemaSelecionado=true;

M.Modal
.getInstance(
modalEsquemaEl
).close();

abrirModalJogadores();

})
.catch(e=>{

mensagemModal.innerHTML=
e.message;

});

};



/* ---------- MONTAR / EDITAR ESCALACAO ---------- */

// preenche um slot do campo com o jogador escolhido
function preencherSlot(slot,j){

jogadoresSelecionados=
jogadoresSelecionados.filter(
x=>x.slot!==slot
);

jogadoresSelecionados.push({
slot:slot,
id:j.id
});

document.getElementById(
`nome-${slot}`
).innerText=j.nome;

document.getElementById(
`slot-${slot}`
).innerHTML=`

<img
class='jogador-foto'
src='${montarFotoJogador(j.nome)}'
onerror="this.src='/img/jogadores/default.png'">

`;

document.querySelectorAll(
"#campoFutebol .posicao"
)[slot].classList.add(
"ocupada"
);

}


function abrirModalJogadores(){

if(!clubeSelecionado || !esquemaSelecionado){
return;
}

const campo=
document.getElementById(
"campoFutebol"
);

campo.innerHTML="";

document.getElementById(
"mensagemJogadores"
).innerText="";

jogadoresSelecionados=[];

M.Modal
.getInstance(
modalJogadoresEl
).open();


fetch(`/clube/${clubeId}/jogador`,{

headers:{
Authorization:`Bearer ${token}`
}

})
.then(r=>{

if(r.status===401){
sessaoExpirada();
throw new Error("Sessão expirada");
}

if(!r.ok) return lerErro(r);

return r.json();

})
.then(data=>{

jogadoresTime=data;

posicoesEsquemas[
esquemaAtual
].forEach((pos,i)=>{

const sigla=
nomesPosicoes[
esquemaAtual
][i];

const div=
document.createElement("div");

div.className="posicao";

div.style.top=pos.top;
div.style.left=pos.left;

div.innerHTML=`

<div
id='slot-${i}'
class='jogador-circle'>

${sigla}

</div>

<div
class='jogador-nome'
id='nome-${i}'>

Selecionar

</div>

`;

div.onclick=
()=>abrirEscolhaJogador(
i,
sigla
);

campo.appendChild(div);

});

// edição com o mesmo esquema: já vem com os 11 jogadores salvos.
// se o esquema mudou, as posições são outras e o campo começa vazio.
if(
modoEdicao
&& escalacaoAtual
&& normalizarEsquema(escalacaoAtual.esquema)===esquemaAtual
){

(escalacaoAtual.jogadores||[]).forEach((salvo,i)=>{

const j=jogadoresTime.find(x=>x.id===salvo.id);

if(j) preencherSlot(i,j);

});

}

})
.catch(e=>{

document.getElementById(
"mensagemJogadores"
).innerText=e.message;

});

}



function abrirEscolhaJogador(slot,sigla){

const lista=
document.getElementById(
"listaJogadores"
);

lista.innerHTML="";

const tipos=
mapaPosicao[sigla]||[];

// ignora o jogador do próprio slot, para poder trocá-lo
const usados=
jogadoresSelecionados
.filter(j=>j.slot!==slot)
.map(j=>j.id);

const candidatos=
jogadoresTime.filter(j=>
tipos.includes(j.posicao)
&& !usados.includes(j.id)
);


candidatos.forEach(j=>{

const item=
document.createElement("div");

item.className=
"jogador-opcao";

item.innerHTML=`

<div class='jogador-card-modal'>

<img
class='jogador-foto'
src='${montarFotoJogador(j.nome)}'
onerror="this.src='/img/jogadores/default.png'">

<div>

<strong>
${j.nome}
</strong>

<br>

${j.posicao}

</div>

</div>

`;

item.onclick=function(){

preencherSlot(slot,j);

M.Modal
.getInstance(
modalEscolhaEl
).close();

};

lista.appendChild(item);

});


M.Modal
.getInstance(
modalEscolhaEl
).open();

}



window.confirmarJogadores=function(){

if(
jogadoresSelecionados.length!==11
){

document.getElementById(
"mensagemJogadores"
).innerText=
"Selecione exatamente 11 jogadores";

return;

}

// edição: PUT na escalação existente; nova: POST
const url=modoEdicao
?`/clube/${clubeId}/escalacao?esquema=${esquemaAtual}`
:`/clube/${clubeId}/escalacoes?esquema=${esquemaAtual}`;

fetch(
url,
{
method:modoEdicao?"PUT":"POST",

headers:{
"Content-Type":"application/json",
Authorization:`Bearer ${token}`
},

body:JSON.stringify({

jogadoresIds:
jogadoresSelecionados
.slice()
.sort((a,b)=>
a.slot-b.slot
)
.map(x=>x.id)

})

})
.then(r=>{

if(r.status===401){
sessaoExpirada();
throw new Error("Sessão expirada");
}

if(!r.ok) return lerErro(r);

return r.json();

})
.then(data=>{

M.Modal.getInstance(modalJogadoresEl).close();

M.toast({
html:data.mensagem||"Escalação salva com sucesso!",
classes:"green darken-2"
});

modoEdicao=false;

// escalação salva/atualizada: busca no GET e exibe
carregarEscalacaoSalva(data);

})
.catch(e=>{

// tentou criar, mas a escalação já existe: passa a mostrar a salva
if(!modoEdicao && (e.status===409 || /já possui/i.test(e.message))){

M.Modal.getInstance(modalJogadoresEl).close();

M.toast({
html:"Você já possui uma escalação. Use o botão Editar para alterá-la.",
classes:"orange darken-2"
});

carregarEscalacaoSalva();
return;

}

// demais erros de validação ficam dentro do modal
document.getElementById(
"mensagemJogadores"
).innerText=e.message;

});

};



/* ---------- EXIBIR ESCALACAO ---------- */

function exibirEscalacaoSalva(data){

const container=
document.getElementById(
"escalacaoSalva"
);

const info=
document.getElementById(
"infoEscalacao"
);

const esquema=
normalizarEsquema(
data.esquema||esquemaAtual
);

const rotulo=
esquema.split("").join("-");

// painel da esquerda
info.innerHTML=`

<h5 class='center'>
Escalação salva
</h5>

<p class='center'>
Esquema:
<strong>${rotulo}</strong>
</p>

`;

// área da direita: só o campo
container.innerHTML=`

<div id='campoSalvo' class='campo-salvo'></div>

`;

const campo=
document.getElementById(
"campoSalvo"
);

const posicoes=
posicoesEsquemas[esquema]||[];

(data.jogadores||[]).forEach((j,i)=>{

const pos=posicoes[i];

if(!pos) return;

const div=
document.createElement("div");

div.className="posicao ocupada";

div.style.top=pos.top;
div.style.left=pos.left;

div.innerHTML=`

<img
class='jogador-foto'
src='${montarFotoJogador(j.nome)}'
alt='${j.nome}'
onerror="this.src='/img/jogadores/default.png'">

<div class='jogador-nome'>
${j.nome}
</div>

`;

campo.appendChild(div);

});

}



function mostrarClube(nome,escudo){

clubeSelecionadoDiv.innerHTML=`
<div class='center'>

<img src='${escudo}'
width='80'>

<h6>
Seu time:
<strong>${nome}</strong>
</h6>

</div>
`;

}


function mostrarMensagem(txt,cor){

mensagem.innerHTML=
`<div class='card-panel ${cor} lighten-4'>
${txt}
</div>`;

setTimeout(
()=>mensagem.innerHTML="",
2000
);

}

});