package br.com.microsservicos.msavaliadorcredito.application.ex;

public class DadosClienteNotFoundException extends  Exception{
    public DadosClienteNotFoundException() {
        super("Dados no cliente não encontrados para o CPF informado");
    }
}
