document.addEventListener("DOMContentLoaded", () => {

    const mensagem = document.getElementById("mensagem");
    const containerTimes = document.querySelector(".row");
    const clubeSelecionadoDiv = document.getElementById("clubeSelecionado");

    // 🔄 Carrega clube salvo
    const clubeNome = localStorage.getItem("clubeNome");
    const clubeEscudo = localStorage.getItem("clubeEscudo");

    if (clubeNome && clubeEscudo) {
        mostrarClube(clubeNome, clubeEscudo);

        if (containerTimes) {
            containerTimes.style.display = "none";
        }
    }

    // 🌐 Função global para onclick
    window.selecionarClube = function (clubeId) {

        const token = localStorage.getItem("token");

        if (!token) {
            mostrarMensagem("Usuário não autenticado", "red");
            return;
        }

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
                throw new Error("Erro ao selecionar clube");
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

            // 💾 salva no navegador
            localStorage.setItem("clubeNome", nome);
            localStorage.setItem("clubeEscudo", escudo);

            mostrarClube(nome, escudo);

            // ✅ mensagem temporária
            mostrarMensagem(data.mensagem, "green");

            // 👇 esconde os cards
            if (containerTimes) {
                containerTimes.style.display = "none";
            }

        })
        .catch(error => {
            mostrarMensagem(error.message, "red");
        });

    };

    // 🎨 Função para renderizar o clube no topo
    function mostrarClube(nome, escudo) {

        if (!clubeSelecionadoDiv) return;

        clubeSelecionadoDiv.innerHTML = `
            <div class="center" style="margin-top:20px;">
                <img src="${escudo}" width="80">
                <h6>Seu time é o <strong>${nome}</strong></h6>
            </div>
        `;
    }

    // 🧠 Função reutilizável de mensagem com timeout
    function mostrarMensagem(texto, cor) {

        mensagem.innerHTML = `
            <div class="card-panel ${cor} lighten-4">
                <span>${texto}</span>
            </div>
        `;

        // ⏱ some depois de 3 segundos
        setTimeout(() => {
            mensagem.innerHTML = "";
        }, 3000);
    }

});