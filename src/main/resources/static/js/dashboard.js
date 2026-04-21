document.addEventListener("DOMContentLoaded", () => {

    const mensagem = document.getElementById("mensagem");
    const mensagemModal = document.getElementById("mensagemModal");
    const containerTimes = document.getElementById("listaTimes");
    const clubeSelecionadoDiv = document.getElementById("clubeSelecionado");
    const textoEscolha = document.getElementById("textoEscolha");
    const btnEsquema = document.getElementById("btnEsquema");

    const modalEsquemaEl = document.getElementById("modalEsquema");
    const modalJogadoresEl = document.getElementById("modalJogadores");

    const token = localStorage.getItem("token");

    if (!token) {
        window.location.href = "/";
        return;
    }

    M.Modal.init(document.querySelectorAll(".modal"), { dismissible: false });

    // =========================
    // ESTADO
    let clubeSelecionado = false;
    let esquemaSelecionado = false;

    let clubeId = null;
    let esquemaAtual = null;
    let jogadoresSelecionados = [];

    // trava inicial
    btnEsquema.style.display = "none";

    modalEsquemaEl.style.display = "none";
    modalJogadoresEl.style.display = "none";

    // =========================
    // ESQUEMAS (COMPLETO)
    const posicoesEsquemas = {
        "442": [
            { top: "90%", left: "50%" },
            { top: "70%", left: "20%" }, { top: "70%", left: "80%" },
            { top: "60%", left: "35%" }, { top: "60%", left: "65%" },
            { top: "45%", left: "20%" }, { top: "45%", left: "40%" }, { top: "45%", left: "60%" }, { top: "45%", left: "80%" },
            { top: "20%", left: "40%" }, { top: "20%", left: "60%" }
        ],
        "352": [
            { top: "90%", left: "50%" },
            { top: "70%", left: "30%" }, { top: "70%", left: "50%" }, { top: "70%", left: "70%" },
            { top: "50%", left: "15%" }, { top: "50%", left: "35%" }, { top: "50%", left: "65%" }, { top: "50%", left: "85%" },
            { top: "25%", left: "40%" }, { top: "25%", left: "60%" },
            { top: "10%", left: "50%" }
        ],
        "541": [
            { top: "90%", left: "50%" },
            { top: "75%", left: "10%" }, { top: "75%", left: "30%" }, { top: "75%", left: "50%" },
            { top: "75%", left: "70%" }, { top: "75%", left: "90%" },
            { top: "50%", left: "30%" }, { top: "50%", left: "50%" }, { top: "50%", left: "70%" },
            { top: "20%", left: "50%" },
            { top: "10%", left: "50%" }
        ],
        "244": [
            { top: "90%", left: "50%" },
            { top: "65%", left: "30%" }, { top: "65%", left: "70%" },
            { top: "50%", left: "10%" }, { top: "50%", left: "30%" }, { top: "50%", left: "50%" },
            { top: "50%", left: "70%" }, { top: "50%", left: "90%" },
            { top: "25%", left: "20%" }, { top: "25%", left: "50%" }, { top: "25%", left: "80%" }
        ]
    };

    const nomesPosicoes = {
        "442": ["GOL","LD","LE","ZAG","ZAG","MEI","MEI","MEI","MEI","ATA","ATA"],
        "352": ["GOL","ZAG","ZAG","ZAG","ALA","MEI","MEI","ALA","ATA","ATA","MEI"],
        "541": ["GOL","LAT","ZAG","ZAG","ZAG","LAT","MEI","MEI","MEI","ATA","ATA"],
        "244": ["GOL","ZAG","ZAG","MEI","MEI","MEI","MEI","MEI","ATA","ATA","ATA"]
    };

    // =========================
    // CLUBE
    fetch("/usuarios/clube", {
        headers: { Authorization: `Bearer ${token}` }
    })
    .then(res => res.status === 204 ? null : res.json())
    .then(data => {

        if (!data) return;

        clubeSelecionado = true;
        clubeId = data.id;

        mostrarClube(
            data.nome,
            data.id === 1 ? "/img/cruzeiro.svg" : "/img/atletico-mineiro.svg"
        );

        containerTimes.style.display = "none";
        textoEscolha.style.display = "none";

        btnEsquema.style.display = "inline-block";
    });

    // =========================
    window.selecionarClube = function (id) {

        fetch("/usuarios/clube", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                Authorization: `Bearer ${token}`
            },
            body: JSON.stringify({ clubeId: id })
        })
        .then(res => res.json())
        .then(data => {
            mostrarMensagem(data.mensagem, "green");
            setTimeout(() => location.reload(), 1200);
        })
        .catch(() => mostrarMensagem("Erro ao definir clube", "red"));
    };

    // =========================
    window.salvarEsquema = function () {

        if (!clubeSelecionado) {
            mostrarMensagem("Escolha um clube primeiro!", "red");
            return;
        }

        const selecionado = document.querySelector('input[name="esquema"]:checked');

        if (!selecionado) {
            mensagemModal.innerHTML = "Selecione um esquema!";
            return;
        }

        esquemaAtual = selecionado.value;
        esquemaSelecionado = true;

        M.Modal.getInstance(modalEsquemaEl).close();

        abrirModalJogadores();
    };

    // =========================
    function abrirModalJogadores() {

        if (!clubeSelecionado || !esquemaSelecionado) {
            mostrarMensagem("Complete as etapas primeiro!", "red");
            return;
        }

        const posicoes = posicoesEsquemas[esquemaAtual];
        const nomes = nomesPosicoes[esquemaAtual];

        if (!posicoes || !nomes) {
            mostrarMensagem("Esquema inválido!", "red");
            return;
        }

        const modal = M.Modal.getInstance(modalJogadoresEl);
        modal.open();

        const campo = document.getElementById("campoFutebol");
        campo.innerHTML = "";
        jogadoresSelecionados = [];

        fetch(`/clube/${clubeId}/jogador`, {
            headers: { Authorization: `Bearer ${token}` }
        })
        .then(res => res.json())
        .then(data => {

            posicoes.forEach((pos, i) => {

                const jogador = data[i] || null;

                const div = document.createElement("div");
                div.classList.add("posicao");

                div.style.top = pos.top;
                div.style.left = pos.left;

                div.innerHTML = `
                    <div class="jogador-circle">${nomes[i]}</div>
                    <div class="jogador-nome">${jogador ? jogador.nome : ""}</div>
                `;

                div.onclick = () => {

                    if (!jogador) return;

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
    window.confirmarJogadores = function () {

        if (jogadoresSelecionados.length !== 11) {
            document.getElementById("mensagemJogadores").innerText =
                "Selecione exatamente 11 jogadores!";
            return;
        }

        fetch(`http://localhost:8080/clubes/${clubeId}/escalacoes?esquema=${esquemaAtual}`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                Authorization: `Bearer ${token}`
            },
            body: JSON.stringify({ jogadoresIds: jogadoresSelecionados })
        })
        .then(() => alert("Escalação salva com sucesso!"));
    };

    // =========================
    function mostrarClube(nome, escudo) {
        clubeSelecionadoDiv.innerHTML = `
            <div class="center" style="margin-top:20px;">
                <img src="${escudo}" width="80">
                <h6>Seu time: <strong>${nome}</strong></h6>
            </div>`;
    }

    function mostrarMensagem(texto, cor) {
        mensagem.innerHTML = `<div class="card-panel ${cor} lighten-4">${texto}</div>`;
        setTimeout(() => mensagem.innerHTML = "", 2000);
    }
});