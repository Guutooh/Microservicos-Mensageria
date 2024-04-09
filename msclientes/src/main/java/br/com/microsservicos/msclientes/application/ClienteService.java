package br.com.microsservicos.msclientes.application;

import br.com.microsservicos.msclientes.domain.Cliente;
import br.com.microsservicos.msclientes.infra.repository.ClienteRepositoty;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ClienteService {


    private final ClienteRepositoty repositoty;

    @Transactional
    public Cliente salvar(Cliente cliente){
        return repositoty.save(cliente);
    }

    public Optional<Cliente> getByCPF(String cpf){
        return  repositoty.findByCpf(cpf);
    }


}
