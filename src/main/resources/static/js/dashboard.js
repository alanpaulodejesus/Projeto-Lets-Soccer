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

    // =========================
    // 🔥 POSIÇÕES (estilo FIFA)
    const posicoesEsquemas = {
        "442":[
            {top:"90%",left:"50%"}, // GOL
            {top:"70%",left:"20%"},{top:"70%",left:"80%"},
            {top:"60%",left:"35%"},{top:"60%",left:"65%"},
            {top:"45%",left:"20%"},{top:"45%",left:"40%"},{top:"45%",left:"60%"},{top:"45%",left:"80%"},
            {top:"20%",left:"40%"},{top:"20%",left:"60%"}
        ],
        "352":[
            {top:"90%",left:"50%"},
            {top:"70%",left:"30%"},{top:"70%",left:"50%"},{top:"70%",left:"70%"},
            {top:"50%",left:"15%"},{top:"50%",left:"35%"},{top:"50%",left:"65%"},{top:"50%",left:"85%"},
            {top:"25%",left:"40%"},{top:"25%",left:"60%"},
            {top:"10%",left:"50%"}
        ],
        "541":[
            {top:"90%",left:"50%"},
            {top:"75%",left:"10%"},{top:"75%",left:"30%"},{top:"75%",left:"50%"},{top:"75%",left:"70%"},{top:"75%",left:"90%"},
            {top:"50%",left:"30%"},{top:"50%",left:"50%"},{top:"50%",left:"70%"},
            {top:"20%",left:"50%"},
            {top:"10%",left:"50%"}
        ],
        "244":[
            {top:"90%",left:"50%"},
            {top:"65%",left:"30%"},{top:"65%",left:"70%"},
            {top:"50%",left:"10%"},{top:"50%",left:"30%"},{top:"50%",left:"50%"},{top:"50%",left:"70%"},{top:"50%",left:"90%"},
            {top:"25%",left:"20%"},{top:"25%",left:"50%"},{top:"25%",left:"80%"}
        ]
    };

    // =========================
    // 🔥 BUSCAR CLUBE AO ENTRAR
    fetch("/usuarios/clube", {
        headers: { Authorization: `Bearer ${token}` }
    })
    .then(res => res.status === 204 ? null : res.json())
    .then(data => {

        if (data) {
            clubeJaSelecionado = true;
            clubeId = data.id;

            mostrarClube(
                data.nome,
                data.id === 1 ? "/img/cruzeiro.svg" : "/img/atletico-mineiro.svg"
            );

            containerTimes.style.display = "none";
            textoEscolha.style.display = "none";
            btnEsquema.style.display = "inline-block";
        } else {
            btnEsquema.style.display = "none";
        }
    });

    // =========================
    // 🔥 ESCOLHER CLUBE
    window.selecionarClube = function(id){

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
            body: JSON.stringify({clubeId:id})
        })
        .then(res => !res.ok
            ? res.json().then(err=>{throw new Error(err.mensagem)})
            : res.json()
        )
        .then(data=>{

            clubeJaSelecionado = true;
            clubeId = id;

            mostrarClube(
                id===1?"Cruzeiro":"Atlético",
                id===1?"/img/cruzeiro.svg":"/img/atletico-mineiro.svg"
            );

            // 🔥 AQUI ESTAVA O BUG (mensagem perdida)
            mostrarMensagem(data.mensagem || "Clube definido com sucesso!", "green");

            containerTimes.style.display="none";
            textoEscolha.style.display="none";
            btnEsquema.style.display="inline-block";
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

                div.innerHTML = `
                    <div class="jogador-circle">
                        ${jogador.nome.split(" ")[0]}
                    </div>
                    <div class="jogador-nome">
                        ${jogador.nome}
                    </div>
                `;

                div.onclick = () => {

                    if (div.classList.contains("ocupada")) {
                        div.classList.remove("ocupada");
                        jogadoresSelecionados =
                            jogadoresSelecionados.filter(id => id !== jogador.id);
                    } else {

                        if (jogadoresSelecionados.length >= 11) {
                            alert("Só 11 jogadores!");
                            return;
                        }

                        div.classList.add("ocupada");
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
        .then(() => {
            alert("Escalação salva com sucesso!");
            M.Modal.getInstance(document.getElementById('modalJogadores')).close();
        });
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

});