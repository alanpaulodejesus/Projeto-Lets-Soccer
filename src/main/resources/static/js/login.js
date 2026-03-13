document.addEventListener("DOMContentLoaded", () => {

const form = document.getElementById("loginForm");
const mensagemDiv = document.getElementById("mensagem");

form.addEventListener("submit", async function(e){

e.preventDefault();

const email = document.getElementById("email").value;
const senha = document.getElementById("senha").value;

try{

const response = await fetch("/login",{

method:"POST",

headers:{
"Content-Type":"application/json"
},

body: JSON.stringify({
email: email,
senha: senha
})

});

const data = await response.json();

if(!response.ok){

throw new Error(data?.mensagem || "Erro ao realizar login");

}

localStorage.setItem("token", data.token);

exibirMensagem("Login realizado com sucesso!", true);

setTimeout(()=>{

window.location.href = "/pages/dashboard.html";

},1000);

}catch(error){

exibirMensagem(error.message, false);

}

});

function exibirMensagem(texto, sucesso=true){

const cor = sucesso ? "green" : "red";

mensagemDiv.innerHTML="";

const div = document.createElement("div");

div.className = `card-panel ${cor} lighten-4`;

div.innerHTML = `<span class="${cor}-text text-darken-4">${texto}</span>`;

mensagemDiv.appendChild(div);

setTimeout(()=>{

div.remove();

},4000);

}

});