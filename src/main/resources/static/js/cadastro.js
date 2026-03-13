document.addEventListener("DOMContentLoaded", () => {

const form = document.getElementById("cadastroForm");
const mensagem = document.getElementById("mensagem");

const toggleSenha = document.getElementById("toggleSenha");
const toggleConfirmar = document.getElementById("toggleConfirmarSenha");

const senhaInput = document.getElementById("senha");
const confirmarInput = document.getElementById("confirmacaoSenha");

// limpa campos
document.querySelectorAll("input").forEach(input=>{
input.value="";
});

if(typeof M !== "undefined"){
M.updateTextFields();
}

// mostrar senha
toggleSenha.addEventListener("click",()=>{
senhaInput.type =
senhaInput.type === "password" ? "text" : "password";
});

// mostrar confirmar senha
toggleConfirmar.addEventListener("click",()=>{
confirmarInput.type =
confirmarInput.type === "password" ? "text" : "password";
});

form.addEventListener("submit", async (e)=>{

e.preventDefault();

const dto = {

nome:document.getElementById("nome").value,
email:document.getElementById("email").value,
senha:senhaInput.value,
confirmacaoSenha:confirmarInput.value

};

try{

const response = await fetch("/usuarios/cadastro",{

method:"POST",

headers:{
"Content-Type":"application/json"
},

body: JSON.stringify(dto)

});

const data = await response.json();

if(!response.ok){
throw new Error(data.mensagem || "Erro ao cadastrar");
}

mensagem.innerHTML=`

<div class="card-panel green lighten-4">
<span class="green-text text-darken-4">
${data.mensagem}
</span>
</div>

`;

setTimeout(()=>{
window.location.href="/";
},2000);

}catch(error){

mensagem.innerHTML=`

<div class="card-panel red lighten-4">
<span class="red-text text-darken-4">
${error.message}
</span>
</div>

`;

}

});

});