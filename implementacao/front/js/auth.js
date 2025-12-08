const API_BASE = 'http://localhost:8080/api';

document.addEventListener('DOMContentLoaded', () => {

  // ---------------------------------------
  // LOGIN
  // ---------------------------------------
  const loginForm = document.getElementById('loginForm');
  if (loginForm) {
    loginForm.addEventListener('submit', async (e) => {
      e.preventDefault();

      const email = document.getElementById('email').value;
      const senha = document.getElementById('senha').value;
      const msg = document.getElementById('msg');

      try {
        const data = await api.post('/auth/login', { email, senha });

        // Salva token e role
        localStorage.setItem('token', data.token);
        localStorage.setItem('role', data.role || data.tipo || '');

        const role = data.role || data.tipo || '';

        if (role.includes('ALUNO')) {
          window.location.href = 'aluno.html';
        } else if (role.includes('EMPRESA')) {
          window.location.href = 'empresa.html';
        } else if (role.includes('PROFESSOR')) {
          window.location.href = 'professor.html';
        } else {
          window.location.href = 'aluno.html';
        }

      } catch (err) {
        msg.textContent = err.message || 'Erro no login';
      }
    });
  }

  // ---------------------------------------
  // CADASTRO ALUNO
  // ---------------------------------------
  const alunoForm = document.getElementById('alunoForm');
  if (alunoForm) {
    alunoForm.addEventListener('submit', async (e) => {
      e.preventDefault();

      const nome = document.getElementById('aluno_nome').value;
      const email = document.getElementById('aluno_email').value;
      const senha = document.getElementById('aluno_senha').value;
      const cpf = document.getElementById('aluno_cpf').value;
      const rg = document.getElementById('aluno_rg').value;
      const instituicao = document.getElementById('aluno_instituicao').value;
      const curso = document.getElementById('aluno_curso').value;
      const endereco = document.getElementById('aluno_endereco').value;

      const msgEl = document.getElementById('alunoMsg');
      msgEl.textContent = '';

      try {
        const body = { email, senha, cpf, rg, nome, instituicao, curso, endereco };

        await api.post('/usuarios/cadastrar/aluno', body);

        msgEl.classList.remove('text-red-600');
        msgEl.classList.add('text-green-600');
        msgEl.textContent = 'Aluno cadastrado com sucesso! Faça login.';

      } catch (err) {
        msgEl.textContent = err.message || 'Erro ao cadastrar aluno';
      }
    });
  }

  // ---------------------------------------
  // CADASTRO EMPRESA
  // ---------------------------------------
  const empresaForm = document.getElementById('empresaForm');
  if (empresaForm) {
    empresaForm.addEventListener('submit', async (e) => {
      e.preventDefault();

      const nome = document.getElementById('empresa_nome').value;
      const email = document.getElementById('empresa_email').value;
      const senha = document.getElementById('empresa_senha').value;
      const cnpj = document.getElementById('empresa_cnpj').value;
      const endereco = document.getElementById('empresa_endereco').value;

      const msgEl = document.getElementById('empresaMsg');
      msgEl.textContent = '';

      try {
        const body = { email, senha, cnpj, nome, endereco };

        await api.post('/usuarios/cadastrar/empresa', body);

        msgEl.classList.remove('text-red-600');
        msgEl.classList.add('text-green-600');
        msgEl.textContent = 'Empresa cadastrada com sucesso! Faça login.';

      } catch (err) {
        msgEl.textContent = err.message || 'Erro ao cadastrar empresa';
      }
    });
  }
});


// ---------------------------------------
// Helpers
// ---------------------------------------
function authHeaders() {
  const token = localStorage.getItem('token');
  if (!token) return { 'Content-Type': 'application/json' };
  return { 'Content-Type': 'application/json', 'Authorization': 'Bearer ' + token };
}

function logoutAndRedirect() {
  localStorage.removeItem('token');
  localStorage.removeItem('role');
  window.location.href = 'index.html';
}

window.apiHelpers = { authHeaders, logoutAndRedirect, API_BASE };
