document.addEventListener('DOMContentLoaded', () => {
  // ------------------------------------------------------------
  // Elementos da página
  // ------------------------------------------------------------
  const perfilEl = document.getElementById('perfil');
  const empresasListEl = document.getElementById('empresasList');
  const refreshEmpresasBtn = document.getElementById('refreshEmpresas');
  const logoutBtn = document.getElementById('logoutBtn');

  const beneficioForm = document.getElementById('beneficioForm');
  const meusBeneficiosList = document.getElementById('meusBeneficiosList');
  const beneficioMsg = document.getElementById('beneficioMsg');
  const refreshBeneficiosBtn = document.getElementById('refreshBeneficiosBtn');

  if (logoutBtn) logoutBtn.addEventListener('click', apiHelpers.logoutAndRedirect);

  // ------------------------------------------------------------
  // Utilitários
  // ------------------------------------------------------------
  const escapeHtml = (str) =>
    str?.replace(/[&<>"']/g, (m) =>
      ({ "&": "&amp;", "<": "&lt;", ">": "&gt;", '"': "&quot;", "'": "&#39;" })[m]
    ) || "";

  const readFileAsDataURL = (file) =>
    new Promise((resolve, reject) => {
      const reader = new FileReader();
      reader.onload = () => resolve(reader.result);
      reader.onerror = reject;
      reader.readAsDataURL(file);
    });

  async function apiGet(url) {
    const res = await fetch(url, { headers: apiHelpers.authHeaders() });
    const data = await res.json().catch(() => ({}));
    return { ok: res.ok, data, status: res.status };
  }

  async function apiSend(url, method, body = {}) {
    const res = await fetch(url, {
      method,
      headers: apiHelpers.authHeaders(),
      body: JSON.stringify(body),
    });
    const data = await res.json().catch(() => ({}));
    return { ok: res.ok, data };
  }

  // ------------------------------------------------------------
  // PERFIL DA EMPRESA
  // ------------------------------------------------------------
  async function carregarPerfil() {
    const { ok, data, status } = await apiGet(apiHelpers.API_BASE + "/empresa/perfil");

    if (status === 401) return apiHelpers.logoutAndRedirect();
    if (!ok) return (perfilEl.textContent = "Erro ao carregar perfil");

    perfilEl.innerHTML = `
      <div class="p-4 bg-white rounded shadow">
        <div class="flex justify-between items-start">
          <div>
            <h3 class="text-lg font-semibold">${data.nome || "(Sem nome)"}</h3>
            <p class="text-sm text-gray-600">${data.email || ""}</p>
          </div>
          <div class="flex gap-2">
            <button id="editarPerfilBtn" class="px-3 py-1 bg-yellow-400 rounded">Editar</button>
            <button id="deletarContaBtn" class="px-3 py-1 bg-red-500 text-white rounded">Deletar</button>
          </div>
        </div>

        <div class="mt-4 text-sm grid grid-cols-1 md:grid-cols-2 gap-2">
          <div><strong>CNPJ:</strong> ${data.cnpj || "-"}</div>
          <div class="col-span-2"><strong>Endereço:</strong> ${data.endereco || "-"}</div>
        </div>

        <div id="perfilMsg" class="mt-3 text-sm"></div>

        <form id="editarPerfilForm" class="hidden bg-slate-50 p-3 rounded mt-4 space-y-2">
          <div class="grid grid-cols-1 md:grid-cols-2 gap-2">
            <input id="edit_nome" value="${data.nome || ""}" class="border p-2 rounded" />
            <input id="edit_email" value="${data.email || ""}" class="border p-2 rounded" />
            <input id="edit_endereco" value="${data.endereco || ""}" class="border p-2 rounded" />
            <input id="edit_senha" type="password" placeholder="Nova senha (opcional)" class="border p-2 rounded" />
          </div>

          <div class="flex gap-2">
            <button type="submit" class="px-3 py-1 bg-indigo-600 text-white rounded">Salvar</button>
            <button type="button" id="cancelEditBtn" class="px-3 py-1 bg-gray-300 rounded">Cancelar</button>
          </div>
        </form>
      </div>
    `;

    adicionarEventosPerfil();
  }

  function adicionarEventosPerfil() {
    const editarBtn = document.getElementById("editarPerfilBtn");
    const deletarBtn = document.getElementById("deletarContaBtn");
    const editarForm = document.getElementById("editarPerfilForm");
    const cancelEditBtn = document.getElementById("cancelEditBtn");
    const perfilMsg = document.getElementById("perfilMsg");

    editarBtn.onclick = () => editarForm.classList.remove("hidden");
    cancelEditBtn.onclick = () => editarForm.classList.add("hidden");

    editarForm.onsubmit = async (e) => {
      e.preventDefault();
      perfilMsg.textContent = "";

      const body = {
        nome: document.getElementById("edit_nome").value,
        email: document.getElementById("edit_email").value,
        endereco: document.getElementById("edit_endereco").value,
        senha: document.getElementById("edit_senha").value || undefined,
      };

      const { ok, data } = await apiSend(apiHelpers.API_BASE + "/empresa/perfil", "PUT", body);

      if (!ok) return (perfilMsg.textContent = data.message || "Erro ao atualizar");

      perfilMsg.classList.add("text-green-600");
      perfilMsg.textContent = "Perfil atualizado!";
      carregarPerfil();
    };

    deletarBtn.onclick = async () => {
      if (!confirm("Deseja deletar sua conta?")) return;

      const res = await fetch(apiHelpers.API_BASE + "/empresa/deletar-conta", {
        method: "DELETE",
        headers: apiHelpers.authHeaders(),
      });

      if (!res.ok) return alert("Erro ao deletar conta");

      alert("Conta deletada.");
      apiHelpers.logoutAndRedirect();
    };
  }

  // ------------------------------------------------------------
  // BENEFÍCIOS
  // ------------------------------------------------------------
  async function listarMeusBeneficios() {
    if (!meusBeneficiosList) return;

    const { ok, data: arr } = await apiGet(apiHelpers.API_BASE + "/empresa/beneficios");

    if (!ok) return (meusBeneficiosList.textContent = "Erro ao listar benefícios");
    if (!Array.isArray(arr) || arr.length === 0)
      return (meusBeneficiosList.innerHTML = "<p class='text-sm'>Nenhum benefício.</p>");

    meusBeneficiosList.innerHTML = arr
      .map((b) => `
        <div class="beneficio-item border rounded p-3 mb-2 flex gap-4" data-id="${b.id}">
          <img src="${escapeHtml(b.foto) || "https://via.placeholder.com/80"}" class="w-20 h-20 object-cover rounded"/>
          <div class="flex-1">
            <div class="flex justify-between">
              <div>
                <strong>${escapeHtml(b.nome)}</strong>
                <p class="text-sm text-gray-600">${escapeHtml(b.descricao || "")}</p>
              </div>
              <span class="font-bold">${b.custo} moedas</span>
            </div>

            <div class="mt-2 flex gap-2">
              <button class="editarBenBtn bg-yellow-400 px-2 py-1 rounded">Editar</button>
              <button class="delBenBtn bg-red-600 text-white px-2 py-1 rounded">Remover</button>
            </div>
          </div>
        </div>
      `)
      .join("");

    adicionarEventosBeneficios(arr);
  }

  function adicionarEventosBeneficios(arr) {
    meusBeneficiosList.querySelectorAll(".delBenBtn").forEach((btn) => {
      btn.onclick = async (e) => {
        const id = e.target.closest(".beneficio-item").dataset.id;
        if (!confirm("Remover benefício?")) return;

        const res = await fetch(apiHelpers.API_BASE + "/empresa/beneficios/" + id, {
          method: "DELETE",
          headers: apiHelpers.authHeaders(),
        });

        if (!res.ok) return alert("Erro ao remover");

        listarMeusBeneficios();
      };
    });

    meusBeneficiosList.querySelectorAll(".editarBenBtn").forEach((btn) => {
      btn.onclick = (e) => abrirEdicaoBeneficio(e, arr);
    });
  }

  function abrirEdicaoBeneficio(e, arr) {
    const id = e.target.closest(".beneficio-item").dataset.id;
    const ben = arr.find((b) => String(b.id) === id);
    const container = e.target.closest(".beneficio-item");

    container.innerHTML = `
      <form class="edit-beneficio-form grid grid-cols-1 md:grid-cols-2 gap-2">
        <input name="nome" value="${escapeHtml(ben.nome)}" class="border p-2 rounded"/>
        <input name="custo" type="number" value="${ben.custo}" class="border p-2 rounded"/>
        <input name="foto" value="${escapeHtml(ben.foto || "")}" class="border p-2 rounded"/>
        <input name="foto_file" type="file" accept="image/*" class="border p-2 rounded"/>
        <label class="flex items-center gap-2"><input name="ativo" type="checkbox" ${ben.ativo ? "checked" : ""}/> Ativo</label>
        <textarea name="descricao" class="border p-2 rounded col-span-2">${escapeHtml(ben.descricao || "")}</textarea>

        <div class="col-span-2 flex gap-2">
          <button type="submit" class="bg-indigo-600 text-white px-3 py-1 rounded">Salvar</button>
          <button type="button" class="cancelEdit px-3 py-1 bg-gray-300 rounded">Cancelar</button>
        </div>
      </form>
      <div class="editMsg text-sm mt-2 text-red-600"></div>
    `;

    const form = container.querySelector(".edit-beneficio-form");
    const msgEl = container.querySelector(".editMsg");

    container.querySelector(".cancelEdit").onclick = listarMeusBeneficios;

    form.onsubmit = async (ev) => {
      ev.preventDefault();

      const fd = new FormData(form);
      let foto = fd.get("foto");
      if (form.foto_file.files[0]) {
        foto = await readFileAsDataURL(form.foto_file.files[0]);
      }

      const body = {
        nome: fd.get("nome"),
        custo: parseFloat(fd.get("custo")),
        descricao: fd.get("descricao"),
        ativo: form.ativo.checked,
        foto,
      };

      const { ok, data } = await apiSend(apiHelpers.API_BASE + "/empresa/beneficios/" + id, "PUT", body);
      if (!ok) return (msgEl.textContent = data.message || "Erro ao atualizar");

      listarMeusBeneficios();
    };
  }

  // Cadastro de benefício
  if (beneficioForm) {
    beneficioForm.onsubmit = async (e) => {
      e.preventDefault();
      beneficioMsg.textContent = "";

      const nome = document.getElementById("beneficio_nome").value;
      const custo = parseFloat(document.getElementById("beneficio_custo").value);
      const descricao = document.getElementById("beneficio_descricao").value;
      const ativo = document.getElementById("beneficio_ativo").checked;

      let foto = document.getElementById("beneficio_foto").value;
      const file = document.getElementById("beneficio_foto_file").files[0];
      if (file) foto = await readFileAsDataURL(file);

      const body = { nome, custo, descricao, ativo, foto };

      const { ok, data } = await apiSend(apiHelpers.API_BASE + "/empresa/beneficios", "POST", body);

      if (!ok) return (beneficioMsg.textContent = data.message || "Erro ao cadastrar");

      beneficioMsg.classList.add("text-green-600");
      beneficioMsg.textContent = "Benefício cadastrado!";
      beneficioForm.reset();
      listarMeusBeneficios();
    };
  }

  // ------------------------------------------------------------
  // LISTAR TODAS AS EMPRESAS
  // ------------------------------------------------------------
  async function listarEmpresas() {
    const { ok, data: arr } = await apiGet(apiHelpers.API_BASE + "/empresa/todas");

    if (!ok) return (empresasListEl.textContent = "Erro ao listar empresas");
    if (!Array.isArray(arr) || arr.length === 0)
      return (empresasListEl.innerHTML = "<p>Nenhuma empresa cadastrada.</p>");

    empresasListEl.innerHTML = arr
      .map(
        (a) => `
        <div class="empresa border p-3 rounded mb-2 flex justify-between" data-id="${a.id}">
          <div>
            <strong>${a.nome || a.email}</strong>
            <p class="text-xs text-gray-600">ID: ${a.id}</p>
          </div>
          <div class="flex gap-2">
            <button class="editEmpresa bg-yellow-400 px-2 py-1 rounded">Editar</button>
            <button class="delEmpresa bg-red-500 px-2 py-1 text-white rounded">Deletar</button>
          </div>
        </div>
      `
      )
      .join("");

    adicionarEventosEmpresas();
  }

  function adicionarEventosEmpresas() {
    empresasListEl.querySelectorAll(".delEmpresa").forEach((btn) => {
      btn.onclick = async (e) => {
        const id = e.target.closest(".empresa").dataset.id;
        if (!confirm("Deletar empresa?")) return;

        const res = await fetch(apiHelpers.API_BASE + "/empresa/" + id, {
          method: "DELETE",
        });

        if (!res.ok) return alert("Erro ao deletar");

        listarEmpresas();
      };
    });

    empresasListEl.querySelectorAll(".editEmpresa").forEach((btn) => {
      btn.onclick = async (e) => {
        const id = e.target.closest(".empresa").dataset.id;
        const novoNome = prompt("Novo nome:");
        if (!novoNome) return;

        const { ok } = await apiSend(apiHelpers.API_BASE + "/empresa/" + id, "PUT", { nome: novoNome });

        if (!ok) return alert("Erro ao atualizar");

        listarEmpresas();
      };
    });
  }

  // ------------------------------------------------------------
  // Inicialização
  // ------------------------------------------------------------
  carregarPerfil();
  listarEmpresas();
  listarMeusBeneficios();

  if (refreshEmpresasBtn) refreshEmpresasBtn.onclick = listarEmpresas;
  if (refreshBeneficiosBtn) refreshBeneficiosBtn.onclick = listarMeusBeneficios;
});
