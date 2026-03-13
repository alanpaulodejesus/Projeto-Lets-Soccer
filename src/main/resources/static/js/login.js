document.addEventListener("DOMContentLoaded", () => {

const form = document.getElementById("loginForm");
const mensagem = document.getElementById("mensagem");

const senhaInput = document.getElementById("senha");
const toggleSenha = document.getElementById("toggleSenha");

const inputs = document.querySelectorAll("input");

// limpa campos imediatamente
inputs.forEach(i => i.value = "");

// limpa novamente após autofill do navegador
setTimeout(()=>{

inputs.forEach(i => i.value = "");

if(window.M){
M.updateTextFields();
}

},200);


// mostrar / ocultar senha
toggleSenha.addEventListener("click",()=>{

senhaInput.type =
senhaInput.type === "password" ? "text" : "password";

});


// login
form.addEventListener("submit", async (e)=>{

e.preventDefault();

const email = document.getElementById("email").value;
const senha = senhaInput.value;

try{

const response = await fetch("/login",{

method:"POST",

headers:{
"Content-Type":"application/json"
},

body:JSON.stringify({
email:email,
senha:senha
})

});

const data = await response.json();

if(!response.ok){
throw new Error(data.message || "Erro no login");
}

localStorage.setItem("token",data.token);

window.location.href="/pages/dashboard.html";

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