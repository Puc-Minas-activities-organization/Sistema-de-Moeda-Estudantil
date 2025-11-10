document.addEventListener('DOMContentLoaded', () => {
  const perfilEl = document.getElementById('perfil');
  const listaEl = document.getElementById('empresasList');
  const refreshBtn = document.getElementById('refreshEmpresas');
  const logoutBtn = document.getElementById('logoutBtn');

  if (logoutBtn) logoutBtn.addEventListener('click', () => { apiHelpers.logoutAndRedirect(); });

  function escapeHtml(str) {
    if (str === null || str === undefined) return '';
    return String(str)
      .replace(/&/g, '&amp;')
      .replace(/</g, '&lt;')
      .replace(/>/g, '&gt;')
      .replace(/\"/g, '&quot;')
      .replace(/'/g, '&#39;');
  }

  // Ler File como DataURL (base64)
  function readFileAsDataURL(file) {
    return new Promise((resolve, reject) => {
      const reader = new FileReader();
      reader.onload = () => resolve(reader.result);
      reader.onerror = (e) => reject(e);
      reader.readAsDataURL(file);
    });
  }

  async function carregarPerfil() {
    try {
      const res = await fetch(apiHelpers.API_BASE + '/empresa/perfil', { headers: apiHelpers.authHeaders() });
      if (!res.ok) {
        if (res.status === 401) { apiHelpers.logoutAndRedirect(); return; }
        perfilEl.textContent = 'Erro ao carregar perfil';
        return;
      }
      const data = await res.json();
      perfilEl.innerHTML = `
        <div class="p-4 bg-white rounded shadow">
          <div class="flex justify-between items-start">
            <div>
              <h3 class="text-lg font-semibold">${data.nome || '(Sem nome)'}</h3>
              <p class="text-sm text-gray-600">${data.email || ''}</p>
            </div>
            <div class="flex gap-2">
              <button id="editarPerfilBtn" class="px-3 py-1 bg-yellow-400 rounded">Editar perfil</button>
              <button id="deletarContaBtn" class="px-3 py-1 bg-red-500 text-white rounded">Deletar conta</button>
            </div>
          </div>

          <div class="mt-4 grid grid-cols-1 md:grid-cols-2 gap-2 text-sm text-gray-700">
            <div><strong>CNPJ:</strong> ${data.cnpj || '-'}</div>
            <div class="col-span-2"><strong>Endereço:</strong> ${data.endereco || '-'}</div>
          </div>

          <div id="perfilMsg" class="mt-3 text-sm"></div>

          <!-- Formulário de edição oculto -->
          <form id="editarPerfilForm" class="mt-4 space-y-2 hidden bg-slate-50 p-3 rounded">
            <div class="grid grid-cols-1 md:grid-cols-2 gap-2">
              <input id="edit_nome" class="w-full border rounded p-2" placeholder="Nome" />
              <input id="edit_email" class="w-full border rounded p-2" placeholder="Email" />
              <input id="edit_endereco" class="w-full border rounded p-2" placeholder="Endereço" />
              <input id="edit_senha" type="password" class="w-full border rounded p-2" placeholder="Nova senha (opcional)" />
            </div>
            <div class="flex gap-2">
              <button type="submit" class="px-3 py-1 bg-indigo-600 text-white rounded">Salvar</button>
              <button type="button" id="cancelEditBtn" class="px-3 py-1 bg-gray-300 rounded">Cancelar</button>
            </div>
          </form>
        </div>
      `;

      document.getElementById('edit_nome').value = data.nome || '';
      document.getElementById('edit_email').value = data.email || '';
      document.getElementById('edit_endereco').value = data.endereco || '';

      const editarBtn = document.getElementById('editarPerfilBtn');
      const deletarBtn = document.getElementById('deletarContaBtn');
      const editarForm = document.getElementById('editarPerfilForm');
      const cancelEditBtn = document.getElementById('cancelEditBtn');
      const perfilMsg = document.getElementById('perfilMsg');

      editarBtn.addEventListener('click', () => { editarForm.classList.remove('hidden'); window.scrollTo({ top: 0, behavior: 'smooth' }); });
      cancelEditBtn.addEventListener('click', () => { editarForm.classList.add('hidden'); perfilMsg.textContent = ''; });

      editarForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        perfilMsg.textContent = '';
        const body = {
          nome: document.getElementById('edit_nome').value,
          endereco: document.getElementById('edit_endereco').value,
          email: document.getElementById('edit_email').value,
          senha: document.getElementById('edit_senha').value || undefined
        };
        try {
          const res = await fetch(apiHelpers.API_BASE + '/empresa/perfil', {
            method: 'PUT', headers: apiHelpers.authHeaders(), body: JSON.stringify(body)
          });
          const resp = await res.json().catch(() => ({}));
          if (!res.ok) { perfilMsg.textContent = resp.message || resp.error || 'Erro ao atualizar perfil'; return; }
          perfilMsg.classList.remove('text-red-600'); perfilMsg.classList.add('text-green-600');
          perfilMsg.textContent = 'Perfil atualizado com sucesso.';
       
          carregarPerfil();
        } catch (err) { perfilMsg.textContent = 'Erro de conexão: ' + err.message; }
      });

      deletarBtn.addEventListener('click', async () => {
        if (!confirm('Tem certeza que deseja deletar sua conta? Esta ação é irreversível.')) return;
        try {
          const res = await fetch(apiHelpers.API_BASE + '/empresa/deletar-conta', { method: 'DELETE', headers: apiHelpers.authHeaders() });
          if (!res.ok) { const e = await res.json().catch(() => ({})); alert(e.message || 'Erro ao deletar conta'); return; }
          alert('Conta deletada. Você será deslogado.');
          apiHelpers.logoutAndRedirect();
        } catch (err) { alert('Erro: ' + err.message); }
      });
    } catch (err) {
      perfilEl.textContent = 'Erro: ' + err.message;
    }
  }

  // ===== Benefícios da empresa (criar / listar) =====
  const beneficioForm = document.getElementById('beneficioForm');
  const meusBeneficiosList = document.getElementById('meusBeneficiosList');
  const refreshBeneficiosBtn = document.getElementById('refreshBeneficiosBtn');
  const beneficioMsg = document.getElementById('beneficioMsg');

  async function listarMeusBeneficios() {
    if (!meusBeneficiosList) return;
    try {
      const res = await fetch(apiHelpers.API_BASE + '/empresa/beneficios', { headers: apiHelpers.authHeaders() });
      if (!res.ok) { meusBeneficiosList.textContent = 'Erro ao listar benefícios'; return; }
      const arr = await res.json();
      if (!Array.isArray(arr) || arr.length === 0) { meusBeneficiosList.innerHTML = '<p class="text-sm text-gray-500">Nenhum benefício.</p>'; return; }

      // renderizar cada benefício com um id no container para permitir substituir por formulário de edição
      meusBeneficiosList.innerHTML = arr.map(b => `
        <div id="ben-${b.id}" class="border rounded p-3 mb-2 flex gap-4 items-start">
          <img src="${escapeHtml(b.foto) || 'https://via.placeholder.com/80x80?text=Sem'}" alt="foto" class="w-20 h-20 object-cover rounded" />
          <div class="flex-1">
            <div class="flex justify-between items-start">
              <div>
                <div class="font-semibold">${escapeHtml(b.nome)}</div>
                <div class="text-sm text-gray-600">${escapeHtml(b.descricao || '')}</div>
              </div>
              <div class="text-sm text-gray-700"><strong>${b.custo}</strong> moedas</div>
            </div>
            <div class="mt-2 flex gap-2">
              <button data-id="${b.id}" class="editarBenBtn px-2 py-1 bg-yellow-400 rounded">Editar</button>
              <button data-id="${b.id}" class="delBenBtn px-2 py-1 bg-red-500 text-white rounded">Remover</button>
            </div>
          </div>
        </div>
      `).join('\n');

      // attach actions
      meusBeneficiosList.querySelectorAll('.delBenBtn').forEach(btn => {
        btn.addEventListener('click', async (e) => {
          const id = e.currentTarget.getAttribute('data-id');
          if (!confirm('Remover benefício id=' + id + '?')) return;
          try {
            const res = await fetch(apiHelpers.API_BASE + '/empresa/beneficios/' + id, { method: 'DELETE', headers: apiHelpers.authHeaders() });
            if (!res.ok) { alert('Erro ao remover'); return; }
            listarMeusBeneficios();
          } catch (err) { alert('Erro: ' + err.message); }
        });
      });

      // Editar: substituir o cartão pelo formulário de edição preenchido
      meusBeneficiosList.querySelectorAll('.editarBenBtn').forEach(btn => {
        btn.addEventListener('click', async (e) => {
          const id = e.currentTarget.getAttribute('data-id');
          const ben = arr.find(x => String(x.id) === String(id));
          const container = document.getElementById('ben-' + id);
          if (!container) return;

          container.innerHTML = `
            <div class="w-full">
              <form id="editForm-${id}" class="grid grid-cols-1 md:grid-cols-2 gap-2">
                <input name="nome" placeholder="Nome" required class="w-full border rounded p-2" value="${escapeHtml(ben.nome)}" />
                <input name="custo" type="number" step="0.01" placeholder="Custo" required class="w-full border rounded p-2" value="${ben.custo || 0}" />
                <input name="foto" placeholder="URL da foto" class="w-full border rounded p-2" value="${escapeHtml(ben.foto || '')}" />
                <input name="foto_file" type="file" accept="image/*" class="w-full border rounded p-2" />
                <label class="flex items-center gap-2"><input name="ativo" type="checkbox" ${ben.ativo ? 'checked' : ''}/> Ativo</label>
                <textarea name="descricao" placeholder="Descrição" rows="3" class="col-span-1 md:col-span-2 w-full border rounded p-2">${escapeHtml(ben.descricao || '')}</textarea>
                <div class="col-span-1 md:col-span-2 flex gap-2">
                  <button type="submit" class="px-3 py-1 bg-indigo-600 text-white rounded">Salvar</button>
                  <button type="button" id="cancelEdit-${id}" class="px-3 py-1 bg-gray-300 rounded">Cancelar</button>
                </div>
              </form>
              <div id="editMsg-${id}" class="text-sm mt-2 text-red-600"></div>
            </div>
          `;

          // submit handler
          const editForm = document.getElementById(`editForm-${id}`);
          const editMsg = document.getElementById(`editMsg-${id}`);
          document.getElementById(`cancelEdit-${id}`).addEventListener('click', () => { listarMeusBeneficios(); });

          editForm.addEventListener('submit', async (ev) => {
            ev.preventDefault();
            editMsg.textContent = '';
            try {
              const formData = new FormData(editForm);
              // se o usuário subiu um arquivo, priorizamos ele
              let fotoVal = formData.get('foto');
              const fileInput = editForm.querySelector('[name=foto_file]');
              if (fileInput && fileInput.files && fileInput.files[0]) {
                fotoVal = await readFileAsDataURL(fileInput.files[0]);
              }
              const body = {
                nome: formData.get('nome'),
                custo: parseFloat(formData.get('custo') || 0),
                descricao: formData.get('descricao'),
                foto: fotoVal,
                ativo: editForm.querySelector('[name=ativo]').checked
              };
              const res = await fetch(apiHelpers.API_BASE + '/empresa/beneficios/' + id, {
                method: 'PUT', headers: Object.assign({}, apiHelpers.authHeaders()), body: JSON.stringify(body)
              });
              const d = await res.json().catch(() => ({}));
              if (!res.ok) { editMsg.textContent = d.message || d.error || 'Erro ao atualizar benefício'; return; }
              // sucesso: recarregar lista
              listarMeusBeneficios();
            } catch (err) { editMsg.textContent = 'Erro: ' + err.message; }
          });
        });
      });

    } catch (err) { meusBeneficiosList.textContent = 'Erro: ' + err.message; }
  }

  if (refreshBeneficiosBtn) refreshBeneficiosBtn.addEventListener('click', listarMeusBeneficios);

  if (beneficioForm) {
    beneficioForm.addEventListener('submit', async (e) => {
      e.preventDefault();
      beneficioMsg.textContent = '';
      const nome = document.getElementById('beneficio_nome').value;
      const custo = parseFloat(document.getElementById('beneficio_custo').value || 0);
      const fotoUrl = document.getElementById('beneficio_foto').value;
      const fotoFileEl = document.getElementById('beneficio_foto_file');
      let foto = fotoUrl;
      if (fotoFileEl && fotoFileEl.files && fotoFileEl.files[0]) {
        try {
          foto = await readFileAsDataURL(fotoFileEl.files[0]);
        } catch (err) {
          beneficioMsg.textContent = 'Erro ao ler arquivo: ' + err.message; return;
        }
      }
      const descricao = document.getElementById('beneficio_descricao').value;
      const ativo = document.getElementById('beneficio_ativo').checked;
      try {
        const body = { nome, custo, descricao, foto, ativo };
        const res = await fetch(apiHelpers.API_BASE + '/empresa/beneficios', { method: 'POST', headers: apiHelpers.authHeaders(), body: JSON.stringify(body) });
        const data = await res.json().catch(() => ({}));
        if (!res.ok) { beneficioMsg.textContent = data.message || data.error || 'Erro ao cadastrar benefício'; return; }
        beneficioMsg.classList.remove('text-red-600'); beneficioMsg.classList.add('text-green-600');
        beneficioMsg.textContent = 'Benefício cadastrado com sucesso.';
        beneficioForm.reset();
        listarMeusBeneficios();
      } catch (err) { beneficioMsg.textContent = 'Erro de conexão: ' + err.message; }
    });
  }

  async function listarEmpresas() {
    try {
      const res = await fetch(apiHelpers.API_BASE + '/empresa/todas');
      if (!res.ok) { listaEl.textContent = 'Erro ao listar empresas'; return; }
      const arr = await res.json();
      if (!Array.isArray(arr)) { listaEl.textContent = 'Resposta inesperada'; return; }

      if (arr.length === 0) { listaEl.innerHTML = '<p class="text-sm text-gray-500">Nenhuma empresa cadastrada.</p>'; return; }

      const rows = arr.map(a => {
        const id = a.id || a.usuarioId || a.userId || 'n/a';
        return `<div class="border rounded p-3 mb-2 flex justify-between items-center">
          <div class="text-sm">${a.nome || a.email || ''} <br/><span class="text-xs text-gray-500">ID: ${id}</span></div>
          <div class="flex gap-2">
            <button data-id="${id}" class="editBtn px-2 py-1 bg-yellow-400 rounded">Editar</button>
            <button data-id="${id}" class="delBtn px-2 py-1 bg-red-500 text-white rounded">Deletar</button>
          </div>
        </div>`;
      }).join('\n');

      listaEl.innerHTML = rows;

      listaEl.querySelectorAll('.delBtn').forEach(btn => {
        btn.addEventListener('click', async (e) => {
          const id = e.currentTarget.getAttribute('data-id');
          if (!confirm('Deseja deletar a empresa id=' + id + '?')) return;
          try {
            const res = await fetch(apiHelpers.API_BASE + '/empresa/' + id, { method: 'DELETE' });
            if (!res.ok) { alert('Erro ao deletar'); return; }
            alert('Empresa deletada');
            listarEmpresas();
          } catch (err) { alert('Erro: ' + err.message); }
        });
      });

      listaEl.querySelectorAll('.editBtn').forEach(btn => {
        btn.addEventListener('click', async (e) => {
          const id = e.currentTarget.getAttribute('data-id');
          const novoNome = prompt('Novo nome para a empresa id=' + id + '?');
          if (!novoNome) return;
          try {
            const body = { nome: novoNome };
            const res = await fetch(apiHelpers.API_BASE + '/empresa/' + id, {
              method: 'PUT', headers: apiHelpers.authHeaders(), body: JSON.stringify(body)
            });
            if (!res.ok) { alert('Erro ao atualizar'); return; }
            alert('Atualizado');
            listarEmpresas();
          } catch (err) { alert('Erro: ' + err.message); }
        });
      });

    } catch (err) {
      listaEl.textContent = 'Erro: ' + err.message;
    }
  }

  if (refreshBtn) refreshBtn.addEventListener('click', listarEmpresas);

  carregarPerfil();
  listarEmpresas();
  listarMeusBeneficios();
});
