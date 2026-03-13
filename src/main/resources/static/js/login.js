document.addEventListener("DOMContentLoaded", () => {

const form = document.getElementById("loginForm");
const mensagem = document.getElementById("mensagem");

const toggleSenha = document.getElementById("toggleSenha");
const senhaInput = document.getElementById("senha");

// limpa campos
document.querySelectorAll("input").forEach(input=>{
input.value="";
});

if(typeof M !== "undefined"){
M.updateTextFields();
}

// mostrar / ocultar senha
toggleSenha.addEventListener("click",()=>{

senhaInput.type =
senhaInput.type === "password" ? "text" : "password";

});

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

body: JSON.stringify({
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