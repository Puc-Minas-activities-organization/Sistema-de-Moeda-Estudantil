document.addEventListener('DOMContentLoaded', () => {
  const perfilEl = document.getElementById('perfil');
  const logoutBtn = document.getElementById('logoutBtn');
  const enviarMoedasForm = document.getElementById('enviarMoedasForm');
  const enviarMoedasMsg = document.getElementById('enviarMoedasMsg');
  const extratoEl = document.getElementById('extrato');

  if (logoutBtn) logoutBtn.addEventListener('click', () => { apiHelpers.logoutAndRedirect(); });

  async function carregarPerfil() {
    try {
      const res = await fetch(apiHelpers.API_BASE + '/professor/perfil', { headers: apiHelpers.authHeaders() });
      if (!res.ok) {
        perfilEl.innerHTML = '<span class="text-red-600">Erro ao carregar perfil</span>';
        return;
      }
      const data = await res.json();
      perfilEl.innerHTML = `<div class="p-4 bg-white rounded shadow">
        <div class="mt-4 grid grid-cols-1 md:grid-cols-2 gap-2 text-sm text-gray-700">
        <div><strong>Nome:</strong> ${data.nome}</div>
        <div><strong>Email:</strong> ${data.email}</div>
        <div><strong>CPF:</strong> ${data.cpf}</div>
        <div><strong>Departamento:</strong> ${data.departamento}</div>
        <div><strong>Instituição:</strong> ${data.instituicao}</div>
        <div><strong>Saldo de Moedas:</strong> ${data.saldoMoedas}</div>
        </div>
      </div>`;
    } catch (err) {
      perfilEl.innerHTML = '<span class="text-red-600">Erro ao carregar perfil</span>';
    }
  }

  async function enviarMoedas(e) {
    e.preventDefault();
    enviarMoedasMsg.textContent = '';
    const alunoEmail = document.getElementById('aluno_email').value;
    const valor = document.getElementById('valor').value;
    const mensagem = document.getElementById('mensagem').value;
    try {
      // Buscar aluno pelo email para obter o ID
      const alunoRes = await fetch(apiHelpers.API_BASE + '/usuarios/perfil/' + encodeURIComponent(alunoEmail) + '?tipoUsuario=ALUNO', {
        headers: apiHelpers.authHeaders()
      });
      if (!alunoRes.ok) {
        enviarMoedasMsg.textContent = 'Aluno não encontrado.';
        return;
      }
      const alunoData = await alunoRes.json();
      const alunoId = alunoData.id || alunoData.userId || alunoData.alunoId;
      if (!alunoId) {
        enviarMoedasMsg.textContent = 'ID do aluno não encontrado.';
        return;
      }
      // Enviar moedas usando o alunoId
      const res = await fetch(apiHelpers.API_BASE + '/professor/enviar-moedas', {
        method: 'POST',
        headers: apiHelpers.authHeaders(),
        body: JSON.stringify({ alunoId, valor, mensagem })
      });
      const data = await res.json();
      if (!res.ok) {
        enviarMoedasMsg.textContent = data.message || 'Erro ao enviar moedas';
        return;
      }
      enviarMoedasMsg.textContent = 'Moedas enviadas com sucesso!';
    } catch (err) {
      enviarMoedasMsg.textContent = 'Erro ao enviar moedas';
    }
  }

  async function carregarExtrato() {
    try {
      const res = await fetch(apiHelpers.API_BASE + '/professor/extrato', { headers: apiHelpers.authHeaders() });
      if (!res.ok) {
        extratoEl.innerHTML = '<span class="text-red-600">Erro ao carregar extrato</span>';
        return;
      }
      const data = await res.json();
      if (!data.transacoes || !Array.isArray(data.transacoes)) {
        extratoEl.innerHTML = '<span class="text-gray-600">Nenhuma transação encontrada.</span>';
        return;
      }
      function formatDate(iso) {
        if (!iso) return '-';
        const d = new Date(iso);
        return d.toLocaleDateString('pt-BR', { day: '2-digit', month: '2-digit', year: 'numeric' }) +
          ' ' + d.toLocaleTimeString('pt-BR', { hour: '2-digit', minute: '2-digit' });
      }
      const extratoCards = data.transacoes.map(t => {
        let desc = t.descricao || '';
        if (t.alunoNome || t.alunoEmail) {
          desc += ` <span class='text-xs text-gray-500'>(Aluno: ${t.alunoNome || ''} ${t.alunoEmail ? '- ' + t.alunoEmail : ''})</span>`;
        }
        return `<div class="bg-white rounded shadow p-3 mb-2 border border-slate-200">
          <div class="flex justify-between items-center mb-1">
            <span class="text-xs text-gray-500">${formatDate(t.data)}</span>
            <span class="font-semibold">${t.tipo}</span>
          </div>
          <div class="mb-1"><strong>Valor:</strong> <span class="${t.valor < 0 ? 'text-red-600' : 'text-green-600'}">${t.valor}</span></div>
          <div class="mb-1"><strong>Descrição:</strong> ${desc}</div>
        </div>`;
      }).join('');
      extratoEl.innerHTML = `<div class="max-h-96 overflow-y-auto">${extratoCards}</div>`;
    } catch (err) {
      extratoEl.innerHTML = '<span class="text-red-600">Erro ao carregar extrato</span>';
    }
  }

  if (enviarMoedasForm) enviarMoedasForm.addEventListener('submit', enviarMoedas);

  carregarPerfil();
  carregarExtrato();
});
