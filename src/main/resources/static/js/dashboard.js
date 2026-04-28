document.addEventListener("DOMContentLoaded",()=>{

const mensagem=document.getElementById("mensagem");
const mensagemModal=document.getElementById("mensagemModal");
const containerTimes=document.getElementById("listaTimes");
const clubeSelecionadoDiv=document.getElementById("clubeSelecionado");
const textoEscolha=document.getElementById("textoEscolha");
const btnEsquema=document.getElementById("btnEsquema");

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

btnEsquema.style.display="none";

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



fetch("/usuarios/clube",{
headers:{
Authorization:`Bearer ${token}`
}
})
.then(r=>r.status===204?null:r.json())
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

btnEsquema.style.display="inline-block";

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
.then(r=>r.json())
.then(d=>{

mostrarMensagem(
d.mensagem,
"green"
);

setTimeout(
()=>location.reload(),
1200
);

});

};



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
`http://localhost:8080/clube/${clubeId}/esquema-tatico`,
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

if(!r.ok){
throw new Error(
"Erro ao salvar esquema"
);
}

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



function abrirModalJogadores(){

if(!clubeSelecionado || !esquemaSelecionado){
return;
}

const campo=
document.getElementById(
"campoFutebol"
);

campo.innerHTML="";

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
.then(r=>r.json())
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

const usados=
jogadoresSelecionados.map(
j=>j.id
);

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

document.querySelectorAll(".posicao")[
slot
].classList.add(
"ocupada"
);


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

fetch(
`http://localhost:8080/clube/${clubeId}/escalacoes?esquema=${esquemaAtual}`,
{
method:"POST",

headers:{
"Content-Type":"application/json",
Authorization:`Bearer ${token}`
},

body:JSON.stringify({

jogadoresIds:
jogadoresSelecionados
.sort((a,b)=>
a.slot-b.slot
)
.map(x=>x.id)

})

})
.then(r => {
  if (!r.ok) {
    return r.json().then(err => { throw new Error(err.message || "Erro"); });
  }
  return r.json();
})
.then(data => {

  M.Modal.getInstance(modalJogadoresEl).close();

  M.toast({
    html: data.mensagem || "Escalação salva com sucesso!",
    classes: "green darken-2"
  });

})
.catch(e => {
  document.getElementById("mensagemJogadores").innerText = e.message;
});

};



function mostrarClube(nome,escudo){

clubeSelecionadoDiv.innerHTML=`
<div class='center'
style='margin-top:20px'>

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