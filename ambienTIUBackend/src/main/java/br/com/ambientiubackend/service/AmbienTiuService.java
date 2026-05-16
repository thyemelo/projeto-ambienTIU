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
    public void saveData(Dto dto){

        Model newModel = new Model(
                dto.temperature(),
                dto.humidity(),
                dto.ilumination(),
                dto.time()
        );

        repository.save(newModel);
    }

    /**
     *
     * Classe responsavel pela vizualição dos dados na web
     *
     * Dto irá transportar os dados através do metodo no service para o controller
     */
    public Dto viewData(Model model){

        Dto newDto = new Dto(
                model.getTemperature(),
                model.getHumidity(),
                model.getIlumination(),
                model.getTime()
        );

        return newDto;
    }
}
