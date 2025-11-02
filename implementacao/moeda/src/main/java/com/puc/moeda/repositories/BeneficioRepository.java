package com.puc.moeda.repositories;

import com.puc.moeda.models.Beneficio;
import com.puc.moeda.models.EmpresaParceira;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BeneficioRepository extends JpaRepository<Beneficio, Long> {
  
  Optional<Beneficio> findByNome(String nome);
  
  // Buscar benefícios ativos
  List<Beneficio> findByAtivoTrue();
  
  // Buscar benefícios de uma empresa
  List<Beneficio> findByEmpresaParceiraOrderByNomeAsc(EmpresaParceira empresaParceira);
  
  // Buscar benefícios ativos de uma empresa
  List<Beneficio> findByEmpresaParceiraAndAtivoTrue(EmpresaParceira empresaParceira);

  default java.util.List<Beneficio> findAllBeneficios() {
    return findAll();
  }
}
