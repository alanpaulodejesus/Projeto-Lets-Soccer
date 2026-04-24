document.addEventListener("DOMContentLoaded", () => {

const mensagem = document.getElementById("mensagem");
const mensagemModal = document.getElementById("mensagemModal");
const containerTimes = document.getElementById("listaTimes");
const clubeSelecionadoDiv = document.getElementById("clubeSelecionado");
const textoEscolha = document.getElementById("textoEscolha");
const btnEsquema = document.getElementById("btnEsquema");

const modalEsquemaEl = document.getElementById("modalEsquema");
const modalJogadoresEl = document.getElementById("modalJogadores");
const modalSelecaoJogadorEl =
document.getElementById("modalSelecaoJogador");

const token = localStorage.getItem("token");

if (!token){
window.location.href="/";
return;
}

/* PRESERVA MODAIS ORIGINAIS */
M.Modal.init(
document.querySelectorAll(".modal"),
{
dismissible:false
}
);


/* ESTADO ORIGINAL + NOVO */
let clubeSelecionado=false;
let esquemaSelecionado=false;

let clubeId=null;
let esquemaAtual=null;

let jogadoresSelecionados=[];

let jogadoresDisponiveis=[];


/* ESQUEMAS ORIGINAIS */
const posicoesEsquemas={

"442":[
{top:"90%",left:"50%"},
{top:"80%",left:"35%"},
{top:"80%",left:"65%"},
{top:"70%",left:"15%"},
{top:"70%",left:"85%"},
{top:"45%",left:"20%"},
{top:"45%",left:"40%"},
{top:"45%",left:"60%"},
{top:"45%",left:"80%"},
{top:"20%",left:"40%"},
{top:"20%",left:"60%"}
],

"352":[
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

"541":[
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

"244":[
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

"442":[
"Goleiro",
"Lateral Direito",
"Lateral Esquerdo",
"Zagueiro",
"Zagueiro",
"Meio-campista",
"Meio-campista",
"Volante",
"Volante",
"Atacante",
"Atacante"
],

"352":[
"Goleiro",
"Zagueiro",
"Zagueiro",
"Zagueiro",
"Lateral Direito",
"Meio-campista",
"Volante",
"Volante",
"Lateral Esquerdo",
"Atacante",
"Atacante"
],

"541":[
"Goleiro",
"Lateral Direito",
"Zagueiro",
"Zagueiro",
"Zagueiro",
"Lateral Esquerdo",
"Meio-campista",
"Volante",
"Volante",
"Meio-campista",
"Atacante"
],

"244":[
"Goleiro",
"Zagueiro",
"Zagueiro",
"Meio-campista",
"Volante",
"Volante",
"Meio-campista",
"Atacante",
"Atacante",
"Atacante",
"Atacante"
]

};


btnEsquema.style.display="none";
modalEsquemaEl.style.display="none";
modalJogadoresEl.style.display="none";


/* CLUBE ORIGINAL */
fetch("/usuarios/clube",{
headers:{
Authorization:`Bearer ${token}`
}
})
.then(res=>res.status===204?null:res.json())
.then(data=>{

if(!data)return;

clubeSelecionado=true;
clubeId=data.id;

mostrarClube(
data.nome,
data.id===1?
"/img/cruzeiro.svg":
"/img/atletico-mineiro.svg"
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
.then(()=>{
location.reload();
});

};



window.salvarEsquema=function(){

if(!clubeSelecionado){
mostrarMensagem(
"Escolha clube primeiro",
"red"
);
return;
}

const selecionado=
document.querySelector(
'input[name="esquema"]:checked'
);

if(!selecionado){
mensagemModal.innerHTML=
"Selecione esquema";
return;
}

esquemaAtual=
selecionado.value;

esquemaSelecionado=true;

M.Modal
.getInstance(
modalEsquemaEl
)
.close();

abrirModalJogadores();

};



function abrirModalJogadores(){

const posicoes=
posicoesEsquemas[esquemaAtual];

const nomes=
nomesPosicoes[esquemaAtual];

const campo=
document.getElementById(
"campoFutebol"
);

campo.innerHTML="";

jogadoresSelecionados=[];

M.Modal
.getInstance(
modalJogadoresEl
)
.open();


fetch(`/clube/${clubeId}/jogador`,{

headers:{
Authorization:
`Bearer ${token}`
}

})
.then(r=>r.json())
.then(data=>{

jogadoresDisponiveis=data;

posicoes.forEach(
(pos,i)=>{

const div=
document.createElement(
"div"
);

div.classList.add(
"posicao"
);

div.style.top=pos.top;
div.style.left=pos.left;

div.dataset.posicao=
nomes[i];

div.dataset.jogadorId="";

div.innerHTML=`

<div class='jogador-circle'>
${nomes[i].substring(0,3)}
</div>

<div class='jogador-nome'>
Selecionar
</div>

`;

div.onclick=()=>{

abrirSelecaoJogador(
div
);

};

campo.appendChild(div);

});

});

}



/* AQUI ESTAVA O PROBLEMA */
function abrirSelecaoJogador(
posicaoDiv
){

const posicao=
posicaoDiv.dataset.posicao;

const lista=
document.getElementById(
"listaJogadores"
);

lista.innerHTML="";


const idsUsados=
jogadoresSelecionados.map(
j=>j.id
);


const candidatos=
jogadoresDisponiveis.filter(
j=>
j.posicao===posicao &&
!idsUsados.includes(
j.id
)
);


if(!candidatos.length){

lista.innerHTML=
"<p>Nenhum jogador disponível</p>";

}else{

candidatos.forEach(j=>{

const item=
document.createElement(
"div"
);

item.className=
"jogador-option";

item.innerHTML=
`<strong>${j.nome}</strong>
<br>
${j.posicao}`;

item.onclick=()=>{

const antigo=
posicaoDiv.dataset.jogadorId;

if(antigo){

jogadoresSelecionados=
jogadoresSelecionados.filter(
x=>x.id!=antigo
);

}

jogadoresSelecionados.push({
id:j.id
});

posicaoDiv.dataset.jogadorId=
j.id;

posicaoDiv.classList.add(
"ocupada"
);

posicaoDiv.querySelector(
".jogador-nome"
).innerText=j.nome;


M.Modal
.getInstance(
modalSelecaoJogadorEl
)
.close();

};

lista.appendChild(item);

});

}

M.Modal
.getInstance(
modalSelecaoJogadorEl
)
.open();

}



window.confirmarJogadores=
function(){

if(
jogadoresSelecionados.length!==11
){

document.getElementById(
"mensagemJogadores"
).innerText=
"Selecione exatamente 11";

return;

}

fetch(
`http://localhost:8080/clubes/${clubeId}/escalacoes?esquema=${esquemaAtual}`,
{

method:"POST",

headers:{
"Content-Type":
"application/json",
Authorization:
`Bearer ${token}`
},

body:JSON.stringify({

jogadoresIds:
jogadoresSelecionados.map(
j=>j.id
)

})

}

)
.then(()=>
alert(
"Escalação salva com sucesso"
)
);

};



function mostrarClube(
nome,
escudo
){

clubeSelecionadoDiv.innerHTML=`
<div class='center'
style='margin-top:20px;'>

<img src='${escudo}'
width='80'>

<h6>
Seu time:
<strong>${nome}</strong>
</h6>

</div>
`;

}


function mostrarMensagem(
texto,
cor
){

mensagem.innerHTML=
`<div class='card-panel ${cor} lighten-4'>
${texto}
</div>`;

setTimeout(
()=>mensagem.innerHTML="",
2000
);

}

});