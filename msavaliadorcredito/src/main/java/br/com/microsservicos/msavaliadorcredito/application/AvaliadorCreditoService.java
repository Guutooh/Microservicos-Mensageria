package br.com.microsservicos.msavaliadorcredito.application;

import br.com.microsservicos.msavaliadorcredito.application.ex.DadosClienteNotFoundException;
import br.com.microsservicos.msavaliadorcredito.application.ex.ErroComunicacaoMicroservicesException;
import br.com.microsservicos.msavaliadorcredito.application.ex.ErroSolicitacaoCartaoException;
import br.com.microsservicos.msavaliadorcredito.domain.model.*;
import br.com.microsservicos.msavaliadorcredito.infra.clients.CartoesResourceClient;
import br.com.microsservicos.msavaliadorcredito.infra.clients.ClienteResourceClient;
import br.com.microsservicos.msavaliadorcredito.infra.mqqueue.SolicitacaoEmissaoCartaoPublisher;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AvaliadorCreditoService {

    private final ClienteResourceClient clientesClient;
    private final CartoesResourceClient cartoesClient;
    private final SolicitacaoEmissaoCartaoPublisher emissaoCartaoPublisher;


    //obter dados do cliente - MsCliente
    //obter cartões do cliente - MsCartoes
    public SituacaoCliente obterSituacaoCliente(String cpf) throws DadosClienteNotFoundException, ErroComunicacaoMicroservicesException {

        try{


        ResponseEntity<DadosCliente> dadosClienteResponse = clientesClient.dadosCliente(cpf);
        ResponseEntity<List<CartaoCliente>> cartoesResponse = cartoesClient.getCartoesByCliente(cpf);


        return SituacaoCliente
                .builder()
                .cliente(dadosClienteResponse.getBody())
                .cartoes(cartoesResponse.getBody())
                .build();
        }catch (FeignException.FeignClientException.FeignClientException e){
            int status = e.status();
            if(HttpStatus.NOT_FOUND.value()==status){
                throw new DadosClienteNotFoundException();
            }
            throw new ErroComunicacaoMicroservicesException(e.getMessage(),status);
        }

    }

    public RetornoAvaliacaoCliente realizarAvaliacao(String cpf, Long renda)throws DadosClienteNotFoundException, ErroComunicacaoMicroservicesException {

        try {
            ResponseEntity<DadosCliente> dadosClienteResponse = clientesClient.dadosCliente(cpf);
            ResponseEntity<List<Cartao>> cartoesResponse = cartoesClient.getCartoesRendaAteh(renda);

            List<Cartao> cartoes = cartoesResponse.getBody();
            var listaCartoesAprovador = cartoes.stream().map(cartao -> {

                DadosCliente dadosCliente = dadosClienteResponse.getBody();

                BigDecimal limiteBasico = cartao.getLimiteBasico();
                BigDecimal idadeBD = BigDecimal.valueOf(dadosCliente.getIdade());
                var fator = idadeBD.divide(BigDecimal.valueOf(10));
                BigDecimal limiteAprovado = fator.multiply(limiteBasico);


                CartaoAprovado aprovado = new CartaoAprovado();
                aprovado.setCartao(cartao.getNome());
                aprovado.setBandeira(cartao.getBandeira());
                aprovado.setLimiteAprovado(limiteAprovado);

                return aprovado;
            }).toList();

            return new RetornoAvaliacaoCliente(listaCartoesAprovador);

        } catch (FeignException.FeignClientException.FeignClientException e) {
            int status = e.status();
            if (HttpStatus.NOT_FOUND.value() == status) {
                throw new DadosClienteNotFoundException();
            }
            throw new ErroComunicacaoMicroservicesException(e.getMessage(), status);
        }
    }

    public ProtocoloSolicitacaoCartao solicitarEmissaoCartao(DadosSolicitacaoEmissaoCartao dados){
        try{
            emissaoCartaoPublisher.solicitarCartao(dados);
            var protocolo = UUID.randomUUID().toString();
            return new ProtocoloSolicitacaoCartao(protocolo);
        }catch (Exception e){
            throw new ErroSolicitacaoCartaoException(e.getMessage());
        }
    }
}
