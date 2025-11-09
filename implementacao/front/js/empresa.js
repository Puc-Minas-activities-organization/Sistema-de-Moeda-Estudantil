document.addEventListener('DOMContentLoaded', () => {
  const perfilEl = document.getElementById('perfil');
  const listaEl = document.getElementById('empresasList');
  const refreshBtn = document.getElementById('refreshEmpresas');
  const logoutBtn = document.getElementById('logoutBtn');

  if (logoutBtn) logoutBtn.addEventListener('click', () => { apiHelpers.logoutAndRedirect(); });

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
});
