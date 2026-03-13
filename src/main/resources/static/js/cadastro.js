document.addEventListener("DOMContentLoaded", () => {

const form = document.getElementById("cadastroForm");
const mensagem = document.getElementById("mensagem");

form.addEventListener("submit", async (e) => {

e.preventDefault();

const dto = {

nome: document.getElementById("nome").value,
email: document.getElementById("email").value,
senha: document.getElementById("senha").value,
confirmacaoSenha: document.getElementById("confirmacaoSenha").value

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
throw new Error(data.mensagem || "Erro no cadastro");
}

mensagem.innerHTML = `
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

mensagem.innerHTML = `
<div class="card-panel red lighten-4">
<span class="red-text text-darken-4">
${error.message}
</span>
</div>
`;

}

});

});