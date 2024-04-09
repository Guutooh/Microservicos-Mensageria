package br.com.microsservicos.msclientes.infra.repository;

import br.com.microsservicos.msclientes.domain.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClienteRepositoty extends JpaRepository<Cliente, Long> {
    Optional<Cliente> findByCpf(String cpf);
}

