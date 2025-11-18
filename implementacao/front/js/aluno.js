// aluno.js - carrega perfil do aluno e lista pública de alunos (CRUD básico)

document.addEventListener('DOMContentLoaded', () => {
  const perfilEl = document.getElementById('perfil');
  const listaEl = document.getElementById('alunosList');
  const refreshBtn = document.getElementById('refreshAlunos');
  const logoutBtn = document.getElementById('logoutBtn');

  if (logoutBtn) logoutBtn.addEventListener('click', () => { apiHelpers.logoutAndRedirect(); });

  async function carregarPerfil() {
    try {
      const res = await fetch(apiHelpers.API_BASE + '/aluno/perfil', { headers: apiHelpers.authHeaders() });
      if (!res.ok) {
        if (res.status === 401) { apiHelpers.logoutAndRedirect(); return; }
        perfilEl.textContent = 'Erro ao carregar perfil';
        function formatDate(iso) {
          if (!iso) return '-';
          const d = new Date(iso);
          return d.toLocaleDateString('pt-BR', { day: '2-digit', month: '2-digit', year: 'numeric' }) +
            ' ' + d.toLocaleTimeString('pt-BR', { hour: '2-digit', minute: '2-digit' });
        }
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
            <div><strong>CPF:</strong> ${data.cpf || '-'}</div>
            <div><strong>RG:</strong> ${data.rg || '-'}</div>
            <div><strong>Instituição:</strong> ${data.instituicao || '-'}</div>
            <div><strong>Curso:</strong> ${data.curso || '-'}</div>
            <div class="col-span-2"><strong>Endereço:</strong> ${data.endereco || '-'}</div>
            <div class="col-span-2 text-green-600"><strong class="text-gray-700">Saldo Moedas:</strong> ${data.saldoMoedas || '-'}</div>
          </div>

          <div id="perfilMsg" class="mt-3 text-sm"></div>

          <!-- Formulário de edição oculto -->
          <form id="editarPerfilForm" class="mt-4 space-y-2 hidden bg-slate-50 p-3 rounded">
            <div class="grid grid-cols-1 md:grid-cols-2 gap-2">
              <input id="edit_nome" class="w-full border rounded p-2" placeholder="Nome" />
              <input id="edit_email" class="w-full border rounded p-2" placeholder="Email" />
              <input id="edit_curso" class="w-full border rounded p-2" placeholder="Curso" />
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
      document.getElementById('edit_curso').value = data.curso || '';
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
          curso: document.getElementById('edit_curso').value,
          email: document.getElementById('edit_email').value,
          senha: document.getElementById('edit_senha').value || undefined
        };
        try {
          const res = await fetch(apiHelpers.API_BASE + '/aluno/perfil', {
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
          const res = await fetch(apiHelpers.API_BASE + '/aluno/deletar-conta', { method: 'DELETE', headers: apiHelpers.authHeaders() });
          if (!res.ok) { const e = await res.json().catch(() => ({})); alert(e.message || 'Erro ao deletar conta'); return; }
          alert('Conta deletada. Você será deslogado.');
          apiHelpers.logoutAndRedirect();
        } catch (err) { alert('Erro: ' + err.message); }
      });
    } catch (err) {
      perfilEl.textContent = 'Erro: ' + err.message;
    }
  }

  async function listarAlunos() {
    try {
      const res = await fetch(apiHelpers.API_BASE + '/aluno/todos');
      if (!res.ok) { listaEl.textContent = 'Erro ao listar alunos'; return; }
      const arr = await res.json();
      if (!Array.isArray(arr)) { listaEl.textContent = 'Resposta inesperada'; return; }

      if (arr.length === 0) { listaEl.innerHTML = '<p class="text-sm text-gray-500">Nenhum aluno cadastrado.</p>'; return; }

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
          if (!confirm('Deseja deletar o aluno id=' + id + '?')) return;
          try {
            const res = await fetch(apiHelpers.API_BASE + '/aluno/' + id, { method: 'DELETE' });
            if (!res.ok) { alert('Erro ao deletar'); return; }
            alert('Aluno deletado');
            listarAlunos();
          } catch (err) { alert('Erro: ' + err.message); }
        });
      });

      listaEl.querySelectorAll('.editBtn').forEach(btn => {
        btn.addEventListener('click', async (e) => {
          const id = e.currentTarget.getAttribute('data-id');
          const novoNome = prompt('Novo nome para o aluno id=' + id + '?');
          if (!novoNome) return;
          try {
       
            const body = { nome: novoNome };
            const res = await fetch(apiHelpers.API_BASE + '/aluno/' + id, {
              method: 'PUT', headers: apiHelpers.authHeaders(), body: JSON.stringify(body)
            });
            if (!res.ok) { alert('Erro ao atualizar'); return; }
            alert('Atualizado');
            listarAlunos();
          } catch (err) { alert('Erro: ' + err.message); }
        });
      });

    } catch (err) {
      listaEl.textContent = 'Erro: ' + err.message;
    }
  }

  // ===== Benefícios disponíveis para alunos =====
  const beneficiosListEl = document.getElementById('beneficiosList');
  const refreshBeneficiosAlunoBtn = document.getElementById('refreshBeneficiosAluno');

    const meusResgatesListEl = document.getElementById('meusResgatesList');
    const refreshMeusResgatesBtn = document.getElementById('refreshMeusResgates');
  // ===== Meus Resgates (benefícios adquiridos) =====
  async function listarMeusResgates() {
    if (!meusResgatesListEl) return;
    meusResgatesListEl.innerHTML = '<span class="text-gray-500">Carregando...</span>';
    try {
      const res = await fetch(apiHelpers.API_BASE + '/aluno/meus-resgates', { headers: apiHelpers.authHeaders() });
      if (!res.ok) {
        if (res.status === 401) { apiHelpers.logoutAndRedirect(); return; }
        meusResgatesListEl.textContent = 'Erro ao listar resgates'; return;
      }
      const arr = await res.json();
      window.meusResgatesCache = Array.isArray(arr) ? arr : [];
      if (!Array.isArray(arr) || arr.length === 0) {
        meusResgatesListEl.innerHTML = '<p class="text-sm text-gray-500">Nenhum resgate realizado.</p>';
        return;
      }
      meusResgatesListEl.innerHTML = arr.map(r => `
        <div class="border rounded p-3 mb-3 flex gap-4 items-start bg-white">
          <img src="${r.beneficio?.foto || 'https://via.placeholder.com/100x100?text=Sem'}" alt="foto" class="w-20 h-20 object-cover rounded" />
          <div class="flex-1">
            <div class="flex justify-between items-start">
              <div>
                <div class="font-semibold">${r.beneficio?.nome || 'Benefício'}</div>
                <div class="text-sm text-gray-600">${r.beneficio?.descricao || ''}</div>
              </div>
              <div class="text-sm text-gray-700"><strong>${r.valorPago}</strong> moedas</div>
            </div>
            <div class="mt-2 text-sm text-gray-700">Código: <span class="font-mono">${r.codigoResgate}</span></div>
            <div class="mt-1 text-sm text-gray-700">Data: ${new Date(r.dataResgate).toLocaleString('pt-BR')}</div>
          </div>
        </div>
      `).join('\n');
    } catch (err) {
      meusResgatesListEl.textContent = 'Erro: ' + err.message;
    }
  }

  async function listarBeneficiosAluno() {
    if (!beneficiosListEl) return;
    try {
      const res = await fetch(apiHelpers.API_BASE + '/aluno/beneficios', { headers: apiHelpers.authHeaders() });
      if (!res.ok) {
        if (res.status === 401) { apiHelpers.logoutAndRedirect(); return; }
        beneficiosListEl.textContent = 'Erro ao listar benefícios'; return;
      }
      const arr = await res.json();
      if (!Array.isArray(arr) || arr.length === 0) { beneficiosListEl.innerHTML = '<p class="text-sm text-gray-500">Nenhum benefício disponível.</p>'; return; }

      beneficiosListEl.innerHTML = arr.map(b => {
        return `
          <div class="border rounded p-3 mb-3 flex gap-4 items-start bg-white">
            <img src="${b.foto || 'https://via.placeholder.com/100x100?text=Sem'}" alt="foto" class="w-24 h-24 object-cover rounded" />
            <div class="flex-1">
              <div class="flex justify-between items-start">
                <div>
                  <div class="font-semibold">${b.nome}</div>
                  <div class="text-sm text-gray-600">${b.descricao || ''}</div>
                </div>
                <div class="text-sm text-gray-700"><strong>${b.custo}</strong> moedas</div>
              </div>
              <div class="mt-3">
                <button data-id="${b.id}" class="resgatarBtn px-3 py-1 bg-green-600 text-white rounded">Resgatar</button>
              </div>
            </div>
          </div>
        `;
      }).join('\n');

      beneficiosListEl.querySelectorAll('.resgatarBtn').forEach(btn => {
        btn.addEventListener('click', async (e) => {
          const id = e.currentTarget.getAttribute('data-id');
          if (!confirm('Deseja resgatar este benefício?')) return;
          try {
            const res = await fetch(apiHelpers.API_BASE + '/aluno/resgatar/' + id, { method: 'POST', headers: apiHelpers.authHeaders() });
            const data = await res.json().catch(() => ({}));
            if (!res.ok) { alert(data.message || 'Erro ao resgatar'); return; }
            alert('Resgate solicitado: ' + (data?.message || 'Verifique histórico'));
            await listarMeusResgates();
            await listarBeneficiosAluno();
            await carregarExtrato();
          } catch (err) { alert('Erro: ' + err.message); }
        });
      });

    } catch (err) { beneficiosListEl.textContent = 'Erro: ' + err.message; }
  }

  if (refreshBeneficiosAlunoBtn) refreshBeneficiosAlunoBtn.addEventListener('click', listarBeneficiosAluno);

    if (refreshMeusResgatesBtn) refreshMeusResgatesBtn.addEventListener('click', listarMeusResgates);

  if (refreshBtn) refreshBtn.addEventListener('click', listarAlunos);

  carregarPerfil();
  listarAlunos();
  listarBeneficiosAluno();
    listarMeusResgates();
    carregarExtrato();

    async function carregarExtrato() {
      const extratoEl = document.getElementById('extrato');
      if (!extratoEl) return;
      extratoEl.innerHTML = '<span class="text-gray-500">Carregando...</span>';
      try {
        const res = await fetch(apiHelpers.API_BASE + '/aluno/extrato', { headers: apiHelpers.authHeaders() });
        if (!res.ok) {
          if (res.status === 401) { apiHelpers.logoutAndRedirect(); return; }
          extratoEl.textContent = 'Erro ao carregar extrato'; return;
        }
        const arr = await res.json();
        if (!arr || !Array.isArray(arr.transacoes) || arr.transacoes.length === 0) {
          extratoEl.innerHTML = '<p class="text-sm text-gray-500">Nenhuma transação encontrada.</p>';
          return;
        }
        extratoEl.innerHTML = arr.transacoes.map(item => `
          <div class="border rounded p-3 mb-2 flex justify-between items-center bg-white">
            <div>
              <div class="font-semibold">${item.tipo === 'RESGATE' ? 'Resgate' : 'Recebimento'}</div>
              <div class="text-sm text-gray-600">${item.descricao || ''}</div>
              <div class="text-xs text-gray-500">${new Date(item.data).toLocaleString('pt-BR')}</div>
            </div>
            <div class="font-mono text-right ${item.valor < 0 ? 'text-red-600' : 'text-green-600'}">${item.valor < 0 ? '-' : '+'}${Math.abs(item.valor)} moedas</div>
          </div>
        `).join('\n');
      } catch (err) {
        extratoEl.textContent = 'Erro: ' + err.message;
      }
    }
});
