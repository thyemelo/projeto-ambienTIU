package br.com.ambientiubackend.service;

import br.com.ambientiubackend.dto.Dto;
import br.com.ambientiubackend.model.Model;
import br.com.ambientiubackend.repository.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AmbienTiuService {

    @Autowired
    Repository repository;

    /**
     * Metodo responsavel por receber os dados em tempo real do arduino
     *
     * Dto é utilizado para transportar os dados para que o model -- como entidade -- salve no repositorio os dados
     */
    public void data(Dto dto){

        Model newModel = new Model(
                dto.temperature(),
                dto.humidity(),
                dto.ilumination(),
                dto.time()
        );

        repository.save(newModel);
    }
}
