document.addEventListener("DOMContentLoaded", () => {

    const mensagem = document.getElementById("mensagem");
    const mensagemModal = document.getElementById("mensagemModal");
    const containerTimes = document.getElementById("listaTimes");
    const clubeSelecionadoDiv = document.getElementById("clubeSelecionado");
    const textoEscolha = document.getElementById("textoEscolha");
    const btnEsquema = document.getElementById("btnEsquema");

    const token = localStorage.getItem("token");

    let clubeJaSelecionado = false;

    if (!token) {
        window.location.href = "/";
        return;
    }

    if (btnEsquema) btnEsquema.style.display = "none";

    const modals = document.querySelectorAll('.modal');
    M.Modal.init(modals, { dismissible: false });

    fetch("/usuarios/clube", {
        method: "GET",
        headers: { "Authorization": `Bearer ${token}` }
    })
    .then(res => res.status === 204 ? null : res.json())
    .then(data => {
        if (data) {
            clubeJaSelecionado = true;
            let escudo = data.id === 1 ? "/img/cruzeiro.svg" : "/img/atletico-mineiro.svg";
            if(data.id ===1) destacarCard("card-cruzeiro"); else destacarCard("card-atleticomg");
            mostrarClube(data.nome, escudo);
            containerTimes.style.display = "none";
            textoEscolha.style.display = "none";
            if (btnEsquema) btnEsquema.style.display = "inline-block";
        }
    });

    window.selecionarClube = function (clubeId) {
        if (clubeJaSelecionado) { mostrarMensagem("Você já escolheu seu time!", "red"); return; }
        fetch("/usuarios/clube", {
            method: "POST",
            headers: { "Content-Type": "application/json", "Authorization": `Bearer ${token}` },
            body: JSON.stringify({ clubeId })
        })
        .then(res => !res.ok ? res.json().then(err=>{throw new Error(err.mensagem)}) : res.json())
        .then(data => {
            clubeJaSelecionado = true;
            let nome = clubeId===1?"Cruzeiro":"Atlético";
            let escudo = clubeId===1?"/img/cruzeiro.svg":"/img/atletico-mineiro.svg";
            destacarCard(clubeId===1?"card-cruzeiro":"card-atleticomg");
            mostrarClube(nome, escudo);
            mostrarMensagem(data.mensagem,"green");
            containerTimes.style.display="none";
            textoEscolha.style.display="none";
            if(btnEsquema) btnEsquema.style.display="inline-block";
        })
        .catch(error => mostrarMensagem(error.message,"red"));
    };

    window.salvarEsquema = function () {
        if(!clubeJaSelecionado){
            mensagemModal.innerHTML = `<div class="card-panel red lighten-4"><span>Escolha um time primeiro!</span></div>`;
            return;
        }
        const selecionado = document.querySelector('input[name="esquema"]:checked');
        if(!selecionado){
            mensagemModal.innerHTML = `<div class="card-panel red lighten-4"><span>Selecione um esquema tático!</span></div>`;
            setTimeout(()=>{mensagemModal.innerHTML="";},2000);
            return;
        }
        mensagemModal.innerHTML = "";
        const valor = selecionado.value;
        const body = {
            esquema442: valor==="442",
            esquema352: valor==="352",
            esquema541: valor==="541",
            esquema244: valor==="244"
        };
        fetch("/clube/1/esquema-tatico", {
            method:"POST",
            headers: { "Content-Type":"application/json","Authorization":`Bearer ${token}` },
            body: JSON.stringify(body)
        })
        .then(res => { if(!res.ok) throw new Error("Erro ao salvar esquema"); return res.json(); })
        .then(data => {
            mensagemModal.innerHTML = `<div class="card-panel green lighten-4"><span>Esquema salvo com sucesso!</span></div>`;
            setTimeout(()=>{
                const modal = M.Modal.getInstance(document.getElementById('modalEsquema'));
                modal.close();
                mensagemModal.innerHTML="";
                abrirModalJogadores(valor);
            },1500);
        })
        .catch(err => { mensagemModal.innerHTML = `<div class="card-panel red lighten-4"><span>${err.message}</span></div>`; });
    };

    function destacarCard(id){
        const card = document.getElementById(id);
        if(card){ card.style.border="3px solid green"; card.style.borderRadius="10px"; }
    }

    function mostrarClube(nome, escudo){
        clubeSelecionadoDiv.innerHTML = `<div class="center" style="margin-top:20px;"><img src="${escudo}" width="80"><h6>Seu time é o <strong>${nome}</strong></h6></div>`;
    }

    function mostrarMensagem(texto, cor){
        mensagem.innerHTML = `<div class="card-panel ${cor} lighten-4"><span>${texto}</span></div>`;
        setTimeout(()=>{mensagem.innerHTML="";},2000);
    }

    // ==========================
    // Modal jogadores
    let jogadoresSelecionados = [];
    let jogadoresClube = [];

    window.abrirModalJogadores = function(esquema){
        const modal = M.Modal.getInstance(document.getElementById('modalJogadores'));
        modal.open();
        const campo = document.getElementById("campoFutebol");
        campo.innerHTML="";
        jogadoresSelecionados=[];
        fetch("/clube/1/jogador",{headers:{"Authorization":`Bearer ${token}`}})
        .then(res=>res.json())
        .then(data=>{
            jogadoresClube=data;
            data.forEach(j=>{
                const div = document.createElement("div");
                div.classList.add("jogador");
                div.innerText=j.nome.split(" ")[0];
                div.dataset.id=j.id;
                div.style.top=`${Math.random()*300 +50}px`;
                div.style.left=`${Math.random()*600 +50}px`;
                div.onclick = ()=>{
                    if(div.classList.contains("selecionado")){
                        div.classList.remove("selecionado");
                        jogadoresSelecionados=jogadoresSelecionados.filter(id=>id!=j.id);
                    }else{
                        if(jogadoresSelecionados.length>=11){ alert("Só é permitido selecionar 11 jogadores!"); return; }
                        div.classList.add("selecionado");
                        jogadoresSelecionados.push(j.id);
                    }
                };
                campo.appendChild(div);
            });
        });
    }

    window.confirmarJogadores = function(){
        if(jogadoresSelecionados.length!==11){
            document.getElementById("mensagemJogadores").innerText="Selecione exatamente 11 jogadores!";
            return;
        }
        fetch(`http://localhost:8080/clubes/1/escalacoes?esquema=4-4-2`,{
            method:"POST",
            headers:{ "Content-Type":"application/json","Authorization":`Bearer ${token}` },
            body: JSON.stringify({jogadoresIds:jogadoresSelecionados})
        })
        .then(res=>{ if(!res.ok) throw new Error("Erro ao salvar escalação"); return res.json(); })
        .then(data=>{ alert("Escalação salva com sucesso!"); const modal=M.Modal.getInstance(document.getElementById('modalJogadores')); modal.close(); })
        .catch(err=>{ alert(err.message); });
    }

});