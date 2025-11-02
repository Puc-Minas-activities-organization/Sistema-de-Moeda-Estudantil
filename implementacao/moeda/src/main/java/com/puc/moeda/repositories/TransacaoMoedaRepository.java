package com.puc.moeda.repositories;

import com.puc.moeda.models.TransacaoMoeda;
import com.puc.moeda.models.Aluno;
import com.puc.moeda.models.Professor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface TransacaoMoedaRepository extends JpaRepository<TransacaoMoeda, Long> {
    
    // Buscar transações de um aluno
    List<TransacaoMoeda> findByDestinatarioOrderByDataTransacaoDesc(Aluno aluno);
    
    // Buscar transações de um professor
    List<TransacaoMoeda> findByRemetenteOrderByDataTransacaoDesc(Professor professor);
    
    // Buscar transações em um período
    @Query("SELECT t FROM TransacaoMoeda t WHERE t.dataTransacao BETWEEN :inicio AND :fim")
    List<TransacaoMoeda> findByPeriodo(@Param("inicio") LocalDateTime inicio, 
                                       @Param("fim") LocalDateTime fim);
    
    // Total de moedas enviadas por um professor
    @Query("SELECT COALESCE(SUM(t.valor), 0.0) FROM TransacaoMoeda t WHERE t.remetente = :professor")
    Double calcularTotalEnviadoPorProfessor(@Param("professor") Professor professor);
    
    // Total de moedas recebidas por um aluno
    @Query("SELECT COALESCE(SUM(t.valor), 0.0) FROM TransacaoMoeda t WHERE t.destinatario = :aluno")
    Double calcularTotalRecebidoPorAluno(@Param("aluno") Aluno aluno);
}
