package com.puc.moeda.repositories;

import com.puc.moeda.models.Beneficio;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BeneficioRepository extends JpaRepository<Beneficio, Long> {
  Beneficio findByNome(String nome);

  default Beneficio saveBeneficio(Beneficio beneficio) {
    return save(beneficio);
  }

  default Beneficio findBeneficioById(Long id) {
    return findById(id).orElse(null);
  }

  default void deleteBeneficioById(Long id) {
    deleteById(id);
  }

  default java.util.List<Beneficio> findAllBeneficios() {
    return findAll();
  }
}
