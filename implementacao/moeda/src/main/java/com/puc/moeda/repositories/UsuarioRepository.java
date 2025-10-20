package com.puc.moeda.repositories;

import com.puc.moeda.models.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
  Usuario findByEmail(String email);

  default Usuario saveUsuario(Usuario usuario) {
    return save(usuario);
  }

  default Usuario findUsuarioById(Long id) {
    return findById(id).orElse(null);
  }

  default void deleteUsuarioById(Long id) {
    deleteById(id);
  }

  default java.util.List<Usuario> findAllUsuarios() {
    return findAll();
  }
}
