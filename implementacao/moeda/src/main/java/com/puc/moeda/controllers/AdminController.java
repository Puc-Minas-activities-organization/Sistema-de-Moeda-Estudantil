package com.puc.moeda.controllers;

import com.puc.moeda.services.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

	@Autowired
	private AdminService adminService;

	/**
	 * Endpoint para distribuir 1000 moedas a todos os professores
	 * Apenas ADMIN pode chamar
	 */
	@PostMapping("/distribuir-moedas")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<?> distribuirMoedas() {
		adminService.distribuirMoedasParaTodosOsProfessores();
		return ResponseEntity.ok("Moedas distribuídas para todos os professores.");
	}
}
