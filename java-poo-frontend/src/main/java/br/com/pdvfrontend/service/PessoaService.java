package br.com.pdvfrontend.service;

import br.com.pdvfrontend.model.Pessoa;
import java.util.Arrays;
import java.util.List;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

public class PessoaService {

    private final String BASE_URL = "http://localhost:8080/api/pessoas";
    private final RestTemplate restTemplate = new RestTemplate();

    public List<Pessoa> listar() {
        ResponseEntity<Pessoa[]> response = restTemplate.getForEntity(BASE_URL, Pessoa[].class);
        return Arrays.asList(response.getBody());
    }

    public Pessoa buscarPorId(Long id) {
        return restTemplate.getForObject(BASE_URL + "/" + id, Pessoa.class);
    }

    public Pessoa salvar(Pessoa pessoa) {
        ResponseEntity<Pessoa> response = restTemplate.postForEntity(BASE_URL, pessoa, Pessoa.class);
        return response.getBody();
    }

    public void atualizar(Pessoa pessoa) {
        restTemplate.put(BASE_URL + "/" + pessoa.getId(), pessoa);
    }

    public void deletar(Long id) {
        restTemplate.delete(BASE_URL + "/" + id);
    }
}




