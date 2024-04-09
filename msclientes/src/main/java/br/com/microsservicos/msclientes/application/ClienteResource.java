package br.com.microsservicos.msclientes.application;



import br.com.microsservicos.msclientes.application.representation.ClienteSaveRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/clientes")
@RequiredArgsConstructor
@Slf4j
public class ClienteResource {


    private final ClienteService service;

    @GetMapping
    public String status() {
        log.info("Obtendo s status do microserviço de cliente");
        return "Application ok";
    }

    @PostMapping
    public ResponseEntity save(@RequestBody ClienteSaveRequest request) {
        var cliente = request.toModel();
        service.salvar(cliente);

        URI headerLocation = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .query("cpf={cpf}")
                .buildAndExpand(cliente.getCpf())
                .toUri();

        return ResponseEntity.created(headerLocation).build();
    }

    @GetMapping(params = "cpf")
    public ResponseEntity dadosCliente(@RequestParam String cpf) {
        var cliente = service.getByCPF(cpf);

        if (cliente.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(cliente);
    }


}
