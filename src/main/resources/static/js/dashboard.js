document.addEventListener("DOMContentLoaded", () => {

    const mensagem = document.getElementById("mensagem");
    const containerTimes = document.getElementById("listaTimes");
    const clubeSelecionadoDiv = document.getElementById("clubeSelecionado");
    const textoEscolha = document.getElementById("textoEscolha");

    const token = localStorage.getItem("token");

    if (!token) {
        window.location.href = "/";
        return;
    }

    // 🔎 Busca clube no backend
    fetch("/usuarios/clube", {
        method: "GET",
        headers: {
            "Authorization": `Bearer ${token}`
        }
    })
    .then(res => {
        if (res.status === 204) return null;
        return res.json();
    })
    .then(data => {

        if (data) {

            let escudo = "";

            if (data.id === 1) {
                escudo = "/img/cruzeiro.svg";
            } else if (data.id === 2) {
                escudo = "/img/atletico-mineiro.svg";
            }

            mostrarClube(data.nome, escudo);

            // 🔒 esconde seleção
            containerTimes.style.display = "none";
            textoEscolha.style.display = "none";
        }

    });

    // 🌐 função global
    window.selecionarClube = function (clubeId) {

        fetch("/usuarios/clube", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                "Authorization": `Bearer ${token}`
            },
            body: JSON.stringify({ clubeId })
        })
        .then(res => {
            if (!res.ok) {
                return res.json().then(err => {
                    throw new Error(err.mensagem);
                });
            }
            return res.json();
        })
        .then(data => {

            let nome = "";
            let escudo = "";

            if (clubeId === 1) {
                nome = "Cruzeiro";
                escudo = "/img/cruzeiro.svg";
            } else if (clubeId === 2) {
                nome = "Atlético";
                escudo = "/img/atletico-mineiro.svg";
            }

            mostrarClube(nome, escudo);

            mostrarMensagem(data.mensagem, "green");

            containerTimes.style.display = "none";
            textoEscolha.style.display = "none";

        })
        .catch(error => {
            mostrarMensagem(error.message, "red");
        });

    };

    // 🎨 render clube
    function mostrarClube(nome, escudo) {

        clubeSelecionadoDiv.innerHTML = `
            <div class="center" style="margin-top:20px;">
                <img src="${escudo}" width="80">
                <h6>Seu time é o <strong>${nome}</strong></h6>
            </div>
        `;
    }

    // 💬 mensagem com timeout
    function mostrarMensagem(texto, cor) {

        mensagem.innerHTML = `
            <div class="card-panel ${cor} lighten-4">
                <span>${texto}</span>
            </div>
        `;

        setTimeout(() => {
            mensagem.innerHTML = "";
        }, 3000);
    }

});