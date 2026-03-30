document.addEventListener("DOMContentLoaded", () => {

    const mensagem = document.getElementById("mensagem");
    const mensagemModal = document.getElementById("mensagemModal");
    const containerTimes = document.getElementById("listaTimes");
    const clubeSelecionadoDiv = document.getElementById("clubeSelecionado");
    const textoEscolha = document.getElementById("textoEscolha");
    const btnEsquema = document.getElementById("btnEsquema");

    const token = localStorage.getItem("token");

    if (!token) {
        window.location.href = "/";
        return;
    }

    M.Modal.init(document.querySelectorAll('.modal'), { dismissible: false });

    let clubeJaSelecionado = false;
    let jogadoresSelecionados = [];
    let jogadoresClube = [];
    let esquemaAtual = "442";
    let clubeId = null;

    // 🔥 POSIÇÕES DOS ESQUEMAS
    const posicoesEsquemas = {
        "442":[
            {top:"85%", left:"50%"},{top:"65%", left:"15%"},{top:"65%", left:"85%"},
            {top:"50%", left:"30%"},{top:"50%", left:"70%"},{top:"35%", left:"20%"},
            {top:"35%", left:"40%"},{top:"35%", left:"60%"},{top:"35%", left:"80%"},
            {top:"15%", left:"40%"},{top:"15%", left:"60%"}
        ],
        "352":[
            {top:"85%", left:"50%"},{top:"60%", left:"25%"},{top:"60%", left:"50%"},{top:"60%", left:"75%"},
            {top:"40%", left:"20%"},{top:"40%", left:"40%"},{top:"40%", left:"60%"},{top:"40%", left:"80%"},
            {top:"20%", left:"40%"},{top:"20%", left:"60%"},{top:"10%", left:"50%"}
        ],
        "541":[
            {top:"85%", left:"50%"},{top:"65%", left:"10%"},{top:"65%", left:"30%"},{top:"65%", left:"50%"},{top:"65%", left:"70%"},{top:"65%", left:"90%"},
            {top:"45%", left:"25%"},{top:"45%", left:"50%"},{top:"45%", left:"75%"},{top:"20%", left:"35%"},{top:"20%", left:"65%"}
        ],
        "244":[
            {top:"85%", left:"50%"},{top:"55%", left:"25%"},{top:"55%", left:"75%"},
            {top:"40%", left:"10%"},{top:"40%", left:"30%"},{top:"40%", left:"50%"},{top:"40%", left:"70%"},{top:"40%", left:"90%"},
            {top:"20%", left:"10%"},{top:"20%", left:"50%"},{top:"20%", left:"90%"}
        ]
    };

    // =========================
    // 🔥 BUSCAR CLUBE AO ENTRAR
    fetch("/usuarios/clube", {
        method: "GET",
        headers: { Authorization: `Bearer ${token}` }
    })
    .then(res => {
        if (res.status === 204) return null;
        return res.json();
    })
    .then(data => {

        if (data) {

            clubeJaSelecionado = true;
            clubeId = data.id;

            let escudo = data.id === 1
                ? "/img/cruzeiro.svg"
                : "/img/atletico-mineiro.svg";

            destacarCard(data.id === 1 ? "card-cruzeiro" : "card-atleticomg");
            mostrarClube(data.nome, escudo);

            containerTimes.style.display = "none";
            textoEscolha.style.display = "none";

            if (btnEsquema) btnEsquema.style.display = "inline-block";
        } else {
            if (btnEsquema) btnEsquema.style.display = "none";
        }

    });

    // =========================
    window.selecionarClube = function(clubeIdSelecionado){

        if (clubeJaSelecionado) {
            mostrarMensagem("Você já escolheu seu time!", "red");
            return;
        }

        fetch("/usuarios/clube", {
            method:"POST",
            headers:{
                "Content-Type":"application/json",
                Authorization:`Bearer ${token}`
            },
            body: JSON.stringify({clubeId: clubeIdSelecionado})
        })
        .then(res => !res.ok ? res.json().then(err=>{throw new Error(err.mensagem)}) : res.json())
        .then(data => {

            clubeJaSelecionado = true;
            clubeId = clubeIdSelecionado;

            let nome = clubeId===1?"Cruzeiro":"Atlético";
            let escudo = clubeId===1?"/img/cruzeiro.svg":"/img/atletico-mineiro.svg";

            destacarCard(clubeId===1?"card-cruzeiro":"card-atleticomg");
            mostrarClube(nome, escudo);

            mostrarMensagem(data.mensagem,"green");

            containerTimes.style.display="none";
            textoEscolha.style.display="none";

            if(btnEsquema) btnEsquema.style.display="inline-block";
        })
        .catch(err => mostrarMensagem(err.message,"red"));
    };

    // =========================
    window.salvarEsquema = function(){

        const selecionado = document.querySelector('input[name="esquema"]:checked');

        if (!selecionado) {
            mensagemModal.innerHTML = `<div class="card-panel red lighten-4">Selecione um esquema!</div>`;
            setTimeout(()=>mensagemModal.innerHTML="",2000);
            return;
        }

        esquemaAtual = selecionado.value;

        M.Modal.getInstance(document.getElementById('modalEsquema')).close();

        abrirModalJogadores();
    };

    // =========================
    function abrirModalJogadores(){

        const modal = M.Modal.getInstance(document.getElementById('modalJogadores'));
        modal.open();

        const campo = document.getElementById("campoFutebol");
        campo.innerHTML = "";
        jogadoresSelecionados = [];

        fetch(`/clube/${clubeId}/jogador`, {
            headers: { Authorization: `Bearer ${token}` }
        })
        .then(res => res.json())
        .then(data => {

            jogadoresClube = data;

            posicoesEsquemas[esquemaAtual].forEach((pos, i) => {

                const jogador = data[i];

                const div = document.createElement("div");
                div.classList.add("posicao");

                div.style.top = pos.top;
                div.style.left = pos.left;

                div.title = jogador.nome;

                div.onclick = () => {

                    if (div.classList.contains("selecionada")) {
                        div.classList.remove("selecionada");
                        jogadoresSelecionados =
                            jogadoresSelecionados.filter(id => id !== jogador.id);
                    } else {

                        if (jogadoresSelecionados.length >= 11) {
                            alert("Só 11 jogadores!");
                            return;
                        }

                        div.classList.add("selecionada");
                        jogadoresSelecionados.push(jogador.id);
                    }
                };

                campo.appendChild(div);
            });

        });
    }

    // =========================
    window.confirmarJogadores = function(){

        if (jogadoresSelecionados.length !== 11) {
            document.getElementById("mensagemJogadores").innerText =
                "Selecione exatamente 11 jogadores!";
            return;
        }

        fetch(`http://localhost:8080/clubes/${clubeId}/escalacoes?esquema=${esquemaAtual}`, {
            method:"POST",
            headers:{
                "Content-Type":"application/json",
                Authorization:`Bearer ${token}`
            },
            body: JSON.stringify({ jogadoresIds: jogadoresSelecionados })
        })
        .then(res => {
            if (!res.ok) throw new Error("Erro ao salvar escalação");
            return res.json();
        })
        .then(() => {
            alert("Escalação salva com sucesso!");
            M.Modal.getInstance(document.getElementById('modalJogadores')).close();
        })
        .catch(err => alert(err.message));
    };

    // =========================
    function mostrarClube(nome, escudo){
        clubeSelecionadoDiv.innerHTML = `
            <div class="center" style="margin-top:20px;">
                <img src="${escudo}" width="80">
                <h6>Seu time é o <strong>${nome}</strong></h6>
            </div>`;
    }

    function mostrarMensagem(texto, cor){
        mensagem.innerHTML = `<div class="card-panel ${cor} lighten-4">${texto}</div>`;
        setTimeout(()=>mensagem.innerHTML="",2000);
    }

    function destacarCard(id){
        const card = document.getElementById(id);
        if(card){
            card.style.border="3px solid green";
            card.style.borderRadius="10px";
        }
    }

});