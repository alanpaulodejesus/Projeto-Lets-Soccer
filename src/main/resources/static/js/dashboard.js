document.addEventListener("DOMContentLoaded", () => {

    const mensagem = document.getElementById("mensagem");
    const mensagemModal = document.getElementById("mensagemModal");
    const containerTimes = document.getElementById("listaTimes");
    const clubeSelecionadoDiv = document.getElementById("clubeSelecionado");
    const textoEscolha = document.getElementById("textoEscolha");

    const token = localStorage.getItem("token");

    let clubeJaSelecionado = false;

    if (!token) {
        window.location.href = "/";
        return;
    }

    // 🔥 Modal NÃO fecha clicando fora
    const modals = document.querySelectorAll('.modal');
    M.Modal.init(modals, {
        dismissible: false
    });

    // 🔎 Busca clube
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

            clubeJaSelecionado = true;

            let escudo = "";

            if (data.id === 1) {
                escudo = "/img/cruzeiro.svg";
                destacarCard("card-cruzeiro");
            } else if (data.id === 2) {
                escudo = "/img/atletico-mineiro.svg";
                destacarCard("card-atleticomg");
            }

            mostrarClube(data.nome, escudo);

            containerTimes.style.display = "none";
            textoEscolha.style.display = "none";
        }

    });

    // 🌐 selecionar clube
    window.selecionarClube = function (clubeId) {

        if (clubeJaSelecionado) {
            mostrarMensagem("Você já escolheu seu time!", "red");
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
                return res.json().then(err => {
                    throw new Error(err.mensagem);
                });
            }
            return res.json();
        })
        .then(data => {

            clubeJaSelecionado = true;

            let nome = "";
            let escudo = "";

            if (clubeId === 1) {
                nome = "Cruzeiro";
                escudo = "/img/cruzeiro.svg";
                destacarCard("card-cruzeiro");
            } else if (clubeId === 2) {
                nome = "Atlético";
                escudo = "/img/atletico-mineiro.svg";
                destacarCard("card-atleticomg");
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

    // 🎯 SALVAR ESQUEMA (COM VALIDAÇÃO CORRETA)
    window.salvarEsquema = function () {

        const selecionado = document.querySelector('input[name="esquema"]:checked');

        // ❌ NÃO FECHA O MODAL
        if (!selecionado) {

            mensagemModal.innerHTML = `
                <div class="card-panel red lighten-4">
                    <span>Selecione um esquema tático!</span>
                </div>
            `;

            return;
        }

        mensagemModal.innerHTML = "";

        const valor = selecionado.value;

        const body = {
            esquema442: valor === "442",
            esquema352: valor === "352",
            esquema541: valor === "541",
            esquema244: valor === "244"
        };

        fetch("/clube/1/esquema-tatico", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                "Authorization": `Bearer ${token}`
            },
            body: JSON.stringify(body)
        })
        .then(res => {
            if (!res.ok) throw new Error("Erro ao salvar esquema");
            return res.json();
        })
        .then(data => {

            mensagemModal.innerHTML = `
                <div class="card-panel green lighten-4">
                    <span>Esquema salvo com sucesso!</span>
                </div>
            `;

            // 👇 fecha depois de feedback
            setTimeout(() => {
                const modal = M.Modal.getInstance(document.getElementById('modalEsquema'));
                modal.close();
                mensagemModal.innerHTML = "";
            }, 1500);

        })
        .catch(err => {

            mensagemModal.innerHTML = `
                <div class="card-panel red lighten-4">
                    <span>${err.message}</span>
                </div>
            `;
        });

    };

    function destacarCard(id) {
        const card = document.getElementById(id);
        if (card) {
            card.style.border = "3px solid green";
            card.style.borderRadius = "10px";
        }
    }

    function mostrarClube(nome, escudo) {

        clubeSelecionadoDiv.innerHTML = `
            <div class="center" style="margin-top:20px;">
                <img src="${escudo}" width="80">
                <h6>Seu time é o <strong>${nome}</strong></h6>
            </div>
        `;
    }

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