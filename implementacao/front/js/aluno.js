// === aluno.js REFACTORED ===
// Organização em módulos internos, redução de duplicações e melhor legibilidade.

document.addEventListener('DOMContentLoaded', () => {

  // ========= ELEMENTOS DA PÁGINA =========
  const el = {
    perfil: document.getElementById('perfil'),
    perfilMsg: null,
    alunosList: document.getElementById('alunosList'),
    beneficiosList: document.getElementById('beneficiosList'),
    meusResgates: document.getElementById('meusResgatesList'),
    extrato: document.getElementById('extrato'),
    refreshAlunos: document.getElementById('refreshAlunos'),
    refreshBeneficiosAluno: document.getElementById('refreshBeneficiosAluno'),
    refreshMeusResgates: document.getElementById('refreshMeusResgates'),
    logout: document.getElementById('logoutBtn')
  };

  // ========= UTILITÁRIOS =========
  const api = (url, options = {}) =>
    fetch(apiHelpers.API_BASE + url, { headers: apiHelpers.authHeaders(), ...options })
      .then(async res => {
        const json = await res.json().catch(() => ({}));
        if (!res.ok) throw json;
        return json;
      });

  const setHTML = (target, html) => target && (target.innerHTML = html);

  const loadingMsg = msg => `<span class="text-gray-500">${msg}</span>`;
  const placeholderImg = 'https://via.placeholder.com/100x100?text=Sem';

  // ========= PERFIL DO ALUNO =========

  async function carregarPerfil() {
    if (!el.perfil) return;
    setHTML(el.perfil, loadingMsg('Carregando perfil...'));

    try {
      const data = await api('/aluno/perfil');

      el.perfil.innerHTML = `
        <div class="p-4 bg-white rounded shadow">
          <div class="flex justify-between items-start">
            <div>
              <h3 class="text-lg font-semibold">${data.nome || '-'}</h3>
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
            <div class="col-span-2 text-green-600"><strong>Saldo Moedas:</strong> ${data.saldoMoedas || '-'}</div>
          </div>

          <div id="perfilMsg" class="mt-3 text-sm"></div>

          <!-- Form de edição -->
          <form id="editarPerfilForm" class="mt-4 space-y-2 hidden bg-slate-50 p-3 rounded">
            <div class="grid grid-cols-1 md:grid-cols-2 gap-2">
              <input id="edit_nome" class="w-full border rounded p-2" placeholder="Nome" value="${data.nome || ''}" />
              <input id="edit_email" class="w-full border rounded p-2" placeholder="Email" value="${data.email || ''}" />
              <input id="edit_curso" class="w-full border rounded p-2" placeholder="Curso" value="${data.curso || ''}" />
              <input id="edit_endereco" class="w-full border rounded p-2" placeholder="Endereço" value="${data.endereco || ''}" />
              <input id="edit_senha" type="password" class="w-full border rounded p-2" placeholder="Nova senha (opcional)" />
            </div>

            <div class="flex gap-2">
              <button type="submit" class="px-3 py-1 bg-indigo-600 text-white rounded">Salvar</button>
              <button type="button" id="cancelEditBtn" class="px-3 py-1 bg-gray-300 rounded">Cancelar</button>
            </div>
          </form>
        </div>
      `;

      el.perfilMsg = document.getElementById('perfilMsg');
      bindPerfilActions();

    } catch (err) {
      el.perfil.innerHTML = 'Erro ao carregar perfil.';
    }
  }

  function bindPerfilActions() {
    document.getElementById('editarPerfilBtn').onclick = () => {
      document.getElementById('editarPerfilForm').classList.remove('hidden');
      window.scrollTo({ top: 0, behavior: 'smooth' });
    };

    document.getElementById('cancelEditBtn').onclick = () => {
      document.getElementById('editarPerfilForm').classList.add('hidden');
      el.perfilMsg.textContent = '';
    };

    document.getElementById('editarPerfilForm').onsubmit = async (e) => {
      e.preventDefault();
      el.perfilMsg.textContent = '';

      const body = {
        nome: edit_nome.value,
        email: edit_email.value,
        curso: edit_curso.value,
        endereco: edit_endereco.value,
        senha: edit_senha.value || undefined
      };

      try {
        await api('/aluno/perfil', { method: 'PUT', body: JSON.stringify(body) });
        el.perfilMsg.classList.add('text-green-600');
        el.perfilMsg.textContent = 'Perfil atualizado!';
        carregarPerfil();
      } catch (err) {
        el.perfilMsg.classList.add('text-red-600');
        el.perfilMsg.textContent = err.message || 'Erro ao atualizar perfil';
      }
    };

    document.getElementById('deletarContaBtn').onclick = async () => {
      if (!confirm('Tem certeza? Isto é irreversível.')) return;

      try {
        await api('/aluno/deletar-conta', { method: 'DELETE' });
        alert('Conta deletada.');
        apiHelpers.logoutAndRedirect();
      } catch (err) {
        alert(err.message || 'Erro ao deletar conta');
      }
    };
  }

  // ========= LISTA PÚBLICA DE ALUNOS (CRUD BÁSICO) =========

  async function listarAlunos() {
    if (!el.alunosList) return;
    setHTML(el.alunosList, loadingMsg('Carregando alunos...'));

    try {
      const arr = await api('/aluno/todos', { headers: { 'Content-Type': 'application/json' }});

      if (!Array.isArray(arr) || arr.length === 0) {
        return setHTML(el.alunosList, '<p class="text-sm text-gray-500">Nenhum aluno encontrado.</p>');
      }

      el.alunosList.innerHTML = arr.map(a => `
        <div class="border rounded p-3 mb-2 flex justify-between items-center bg-white">
          <div class="text-sm">
            ${a.nome || a.email} <br/>
            <span class="text-xs text-gray-500">ID: ${a.id}</span>
          </div>
          <div class="flex gap-2">
            <button class="editBtn px-2 py-1 bg-yellow-400 rounded" data-id="${a.id}">Editar</button>
            <button class="delBtn px-2 py-1 bg-red-500 text-white rounded" data-id="${a.id}">Deletar</button>
          </div>
        </div>
      `).join('');

      bindAlunosActions();
    } catch (err) {
      el.alunosList.innerHTML = 'Erro ao listar alunos.';
    }
  }

  function bindAlunosActions() {
    el.alunosList.querySelectorAll('.delBtn').forEach(btn => {
      btn.onclick = async () => {
        const id = btn.dataset.id;
        if (!confirm('Deletar aluno ID ' + id + '?')) return;

        try {
          await api('/aluno/' + id, { method: 'DELETE' });
          listarAlunos();
        } catch {
          alert('Erro ao deletar aluno.');
        }
      };
    });

    el.alunosList.querySelectorAll('.editBtn').forEach(btn => {
      btn.onclick = async () => {
        const id = btn.dataset.id;
        const novoNome = prompt('Novo nome para o aluno:');
        if (!novoNome) return;

        try {
          await api('/aluno/' + id, {
            method: 'PUT',
            body: JSON.stringify({ nome: novoNome })
          });
          listarAlunos();
        } catch {
          alert('Erro ao atualizar.');
        }
      };
    });
  }

  // ========= BENEFÍCIOS =========

  async function listarBeneficiosAluno() {
    if (!el.beneficiosList) return;
    setHTML(el.beneficiosList, loadingMsg('Carregando benefícios...'));

    try {
      const arr = await api('/aluno/beneficios');

      if (!Array.isArray(arr) || arr.length === 0)
        return setHTML(el.beneficiosList, '<p class="text-sm text-gray-500">Nenhum benefício disponível.</p>');

      el.beneficiosList.innerHTML = arr.map(b => `
        <div class="border rounded p-3 mb-3 flex gap-4 bg-white">
          <img src="${b.foto || placeholderImg}" class="w-24 h-24 object-cover rounded" />
          <div class="flex-1">
            <div class="flex justify-between items-start">
              <div>
                <div class="font-semibold">${b.nome}</div>
                <div class="text-sm text-gray-600">${b.descricao || ''}</div>
              </div>
              <div class="text-sm text-gray-700"><strong>${b.custo}</strong> moedas</div>
            </div>
            <button class="resgatarBtn px-3 py-1 bg-green-600 text-white rounded mt-3" data-id="${b.id}">
              Resgatar
            </button>
          </div>
        </div>
      `).join('');

      document.querySelectorAll('.resgatarBtn').forEach(btn => {
        btn.onclick = () => resgatarBeneficio(btn.dataset.id);
      });

    } catch (err) {
      el.beneficiosList.innerHTML = 'Erro ao listar benefícios.';
    }
  }

  async function resgatarBeneficio(id) {
    if (!confirm('Deseja resgatar este benefício?')) return;

    try {
      const data = await api('/aluno/resgatar/' + id, { method: 'POST' });
      alert(data.message || 'Resgate realizado!');
      listarMeusResgates();
      listarBeneficiosAluno();
      carregarExtrato();
    } catch (err) {
      alert(err.message || 'Erro ao resgatar.');
    }
  }

  // ========= RESGATES =========

  async function listarMeusResgates() {
    if (!el.meusResgates) return;
    setHTML(el.meusResgates, loadingMsg('Carregando...'));

    try {
      const arr = await api('/aluno/meus-resgates');

      if (!arr || !arr.length)
        return setHTML(el.meusResgates, '<p class="text-sm text-gray-500">Nenhum resgate encontrado.</p>');

      el.meusResgates.innerHTML = arr.map(r => `
        <div class="border rounded p-3 mb-3 flex gap-4 items-start bg-white">
          <img src="${r.beneficio?.foto || placeholderImg}" class="w-20 h-20 object-cover rounded" />
          <div class="flex-1">
            <div class="flex justify-between">
              <div>
                <div class="font-semibold">${r.beneficio?.nome}</div>
                <div class="text-sm text-gray-600">${r.beneficio?.descricao || ''}</div>
              </div>
              <div class="text-sm text-gray-700"><strong>${r.valorPago}</strong> moedas</div>
            </div>
            <div class="text-sm mt-2">Código: <span class="font-mono">${r.codigoResgate}</span></div>
            <div class="text-sm text-gray-600">${new Date(r.dataResgate).toLocaleString('pt-BR')}</div>
          </div>
        </div>
      `).join('');

    } catch (err) {
      el.meusResgates.innerHTML = 'Erro ao carregar resgates.';
    }
  }

  // ========= EXTRATO =========

  async function carregarExtrato() {
    if (!el.extrato) return;
    setHTML(el.extrato, loadingMsg('Carregando...'));

    try {
      const arr = await api('/aluno/extrato');

      if (!arr || !Array.isArray(arr.transacoes) || arr.transacoes.length === 0) {
        return setHTML(el.extrato, '<p class="text-sm text-gray-500">Nenhuma transação encontrada.</p>');
      }

      el.extrato.innerHTML = arr.transacoes.map(item => `
        <div class="border rounded p-3 mb-2 flex justify-between items-center bg-white">
          <div>
            <div class="font-semibold">${item.tipo === 'RESGATE' ? 'Resgate' : 'Recebimento'}</div>
            <div class="text-sm text-gray-600">${item.descricao || ''}</div>
            <div class="text-xs text-gray-500">${new Date(item.data).toLocaleString('pt-BR')}</div>
          </div>
          <div class="font-mono text-right ${item.valor < 0 ? 'text-red-600' : 'text-green-600'}">
            ${item.valor < 0 ? '-' : '+'}${Math.abs(item.valor)} moedas
          </div>
        </div>
      `).join('');

    } catch (err) {
      el.extrato.innerHTML = 'Erro ao carregar extrato.';
    }
  }

  // ========= LISTENERS =========
  if (el.logout) el.logout.onclick = () => apiHelpers.logoutAndRedirect();
  if (el.refreshAlunos) el.refreshAlunos.onclick = listarAlunos;
  if (el.refreshBeneficiosAluno) el.refreshBeneficiosAluno.onclick = listarBeneficiosAluno;
  if (el.refreshMeusResgates) el.refreshMeusResgates.onclick = listarMeusResgates;

  // ========= INICIALIZAÇÃO =========
  carregarPerfil();
  listarAlunos();
  listarBeneficiosAluno();
  listarMeusResgates();
  carregarExtrato();

});
