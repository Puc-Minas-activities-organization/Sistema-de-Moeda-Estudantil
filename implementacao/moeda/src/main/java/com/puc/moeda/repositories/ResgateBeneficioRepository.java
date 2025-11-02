package com.puc.moeda.repositories;

import com.puc.moeda.models.ResgateBeneficio;
import com.puc.moeda.models.Aluno;
import com.puc.moeda.models.Beneficio;
import com.puc.moeda.models.StatusResgate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ResgateBeneficioRepository extends JpaRepository<ResgateBeneficio, Long> {
    
    // Buscar por código de resgate
    Optional<ResgateBeneficio> findByCodigoResgate(String codigoResgate);
    
    // Buscar resgates de um aluno
    List<ResgateBeneficio> findByAlunoOrderByDataResgateDesc(Aluno aluno);
    
    // Buscar resgates de um benefício específico
    List<ResgateBeneficio> findByBeneficioOrderByDataResgateDesc(Beneficio beneficio);
    
    // Buscar resgates por status
    List<ResgateBeneficio> findByStatus(StatusResgate status);
    
    // Buscar resgates de uma empresa (através do benefício)
    @Query("SELECT r FROM ResgateBeneficio r WHERE r.beneficio.empresaParceira.id = :empresaId ORDER BY r.dataResgate DESC")
    List<ResgateBeneficio> findByEmpresaId(@Param("empresaId") Long empresaId);
    
    // Total gasto por um aluno em resgates
    @Query("SELECT COALESCE(SUM(r.valorPago), 0.0) FROM ResgateBeneficio r WHERE r.aluno = :aluno")
    Double calcularTotalGastoPorAluno(@Param("aluno") Aluno aluno);
}
