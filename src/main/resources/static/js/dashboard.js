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
    // ESTADO DA APLICAÇÃO (CRÍTICO)
    let clubeSelecionado = false;
    let esquemaSelecionado = false;

    let jogadoresSelecionados = [];
    let esquemaAtual = "442";
    let clubeId = null;

    // 🔥 GARANTE UI FECHADA NO INÍCIO
    btnEsquema.style.display = "none";
    modalEsquemaEl.style.display = "none";
    modalJogadoresEl.style.display = "none";

    // =========================
    // POSIÇÕES
    const posicoesEsquemas = {
        "442": [
            { top: "90%", left: "50%" },
            { top: "70%", left: "20%" }, { top: "70%", left: "80%" },
            { top: "60%", left: "35%" }, { top: "60%", left: "65%" },
            { top: "45%", left: "20%" }, { top: "45%", left: "40%" }, { top: "45%", left: "60%" }, { top: "45%", left: "80%" },
            { top: "20%", left: "40%" }, { top: "20%", left: "60%" }
        ]
    };

    const nomesPosicoes = {
        "442": ["GOL", "LD", "LE", "ZAG", "ZAG", "MEI", "MEI", "MEI", "MEI", "ATA", "ATA"]
    };

    // =========================
    // BUSCAR CLUBE
    fetch("/usuarios/clube", {
        headers: { Authorization: `Bearer ${token}` }
    })
        .then(res => (res.status === 204 ? null : res.json()))
        .then(data => {

            if (data) {
                clubeSelecionado = true;
                clubeId = data.id;

                mostrarClube(
                    data.nome,
                    data.id === 1 ? "/img/cruzeiro.svg" : "/img/atletico-mineiro.svg"
                );

                containerTimes.style.display = "none";
                textoEscolha.style.display = "none";

                btnEsquema.style.display = "inline-block";
            }
        });

    // =========================
    // SELECIONAR CLUBE
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

                setTimeout(() => location.reload(), 1500);
            })
            .catch(() => {
                mostrarMensagem("Erro ao definir clube", "red");
            });
    };

    // =========================
    // SALVAR ESQUEMA (TRAVADO)
    window.salvarEsquema = function () {

        if (!clubeSelecionado) {
            mostrarMensagem("Selecione um clube primeiro!", "red");
            return;
        }

        const selecionado = document.querySelector('input[name="esquema"]:checked');

        if (!selecionado) {
            mensagemModal.innerHTML = "Selecione um esquema!";
            return;
        }

        esquemaAtual = selecionado.value;
        esquemaSelecionado = true;

        const modal = M.Modal.getInstance(modalEsquemaEl);
        modal.close();

        abrirModalJogadores();
    };

    // =========================
    // ABRIR ESCALAÇÃO (BLOQUEIO TOTAL)
    function abrirModalJogadores() {

        if (!clubeSelecionado || !esquemaSelecionado) {
            mostrarMensagem("Complete as etapas antes de montar a escalação!", "red");
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

                posicoesEsquemas[esquemaAtual].forEach((pos, i) => {

                    const jogador = data[i] || null;
                    const posicaoNome = nomesPosicoes[esquemaAtual][i];

                    const div = document.createElement("div");
                    div.classList.add("posicao");

                    div.style.top = pos.top;
                    div.style.left = pos.left;

                    div.innerHTML = `
                        <div class="jogador-circle">${posicaoNome}</div>
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
    // CONFIRMAR JOGADORES
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
                <h6>Seu time é o <strong>${nome}</strong></h6>
            </div>`;
    }

    function mostrarMensagem(texto, cor) {
        mensagem.innerHTML = `<div class="card-panel ${cor} lighten-4">${texto}</div>`;
        setTimeout(() => (mensagem.innerHTML = ""), 2000);
    }

});