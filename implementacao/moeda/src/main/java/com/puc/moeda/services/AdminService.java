package com.puc.moeda.services;

import com.puc.moeda.models.Professor;
import com.puc.moeda.repositories.ProfessorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class AdminService {

	@Autowired
	private ProfessorRepository professorRepository;

	/**
	 * Distribui 1000 moedas para todos os professores (sobrescreve saldo atual)
	 */
	@Transactional
	public void distribuirMoedasParaTodosOsProfessores() {
		List<Professor> professores = professorRepository.findAll();
		for (Professor prof : professores) {
			prof.setSaldoMoedas(1000.0);
		}
		professorRepository.saveAll(professores);
	}
}
