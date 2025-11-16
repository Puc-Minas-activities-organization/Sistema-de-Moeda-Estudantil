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
      perfilEl.innerHTML = `<div class="space-y-2">
        <div><strong>Nome:</strong> ${data.nome}</div>
        <div><strong>Email:</strong> ${data.email}</div>
        <div><strong>CPF:</strong> ${data.cpf}</div>
        <div><strong>Departamento:</strong> ${data.departamento}</div>
        <div><strong>Instituição:</strong> ${data.instituicao}</div>
        <div><strong>Saldo de Moedas:</strong> ${data.saldoMoedas}</div>
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
    try {
      const res = await fetch(apiHelpers.API_BASE + '/professor/enviar-moedas', {
        method: 'POST',
        headers: apiHelpers.authHeaders(),
        body: JSON.stringify({ alunoEmail, valor })
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
      extratoEl.innerHTML = `<ul class="divide-y">${data.transacoes.map(t => `
        <li class="py-2">
          <div><strong>Data:</strong> ${t.data}</div>
          <div><strong>Tipo:</strong> ${t.tipo}</div>
          <div><strong>Valor:</strong> ${t.valor}</div>
          <div><strong>Descrição:</strong> ${t.descricao}</div>
        </li>
      `).join('')}</ul>`;
    } catch (err) {
      extratoEl.innerHTML = '<span class="text-red-600">Erro ao carregar extrato</span>';
    }
  }

  if (enviarMoedasForm) enviarMoedasForm.addEventListener('submit', enviarMoedas);

  carregarPerfil();
  carregarExtrato();
});
