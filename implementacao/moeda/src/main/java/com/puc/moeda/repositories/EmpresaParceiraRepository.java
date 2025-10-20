package com.puc.moeda.repositories;

import com.puc.moeda.models.EmpresaParceira;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmpresaParceiraRepository extends JpaRepository<EmpresaParceira, Long> {
  EmpresaParceira findByCnpj(String cnpj);

  default EmpresaParceira saveEmpresa(EmpresaParceira empresa) {
    return save(empresa);
  }

  default EmpresaParceira findEmpresaById(Long id) {
    return findById(id).orElse(null);
  }

  default void deleteEmpresaById(Long id) {
    deleteById(id);
  }

  default java.util.List<EmpresaParceira> findAllEmpresas() {
    return findAll();
  }
}
