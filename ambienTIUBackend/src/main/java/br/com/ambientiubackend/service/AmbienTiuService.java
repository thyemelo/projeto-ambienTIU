package br.com.ambientiubackend.service;

import br.com.ambientiubackend.dto.Dto;
import br.com.ambientiubackend.model.Model;
import br.com.ambientiubackend.repository.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AmbienTiuService {

    @Autowired
    Repository repository;

    /**
     * Metodo responsavel por receber os dados em tempo real do arduino
     *
     * Dto é utilizado para transportar os dados para que o model -- como entidade -- salve no repositorio os dados
     */
    public Dto saveData(Dto dto){

        Model newModel = new Model(
                dto.temperatura(),
                dto.umidade(),
                dto.iluminacao()
        );

        repository.save(newModel);
        return dto;
    }

    /**
     *
     * Metodo responsavel pela vizualição de todos os dados na web
     *
     * Dto irá transportar os dados através do metodo no service para o controller
     */
    public List<Dto> viewAllData(){

        // Recebe todos os dados do repositorio
        List<Model> listModel = repository.findAll();

        // Toda a lista model que foi retirada do repositorio será transformada em dto e enviada a lista para o controller
        return listModel.stream()
                .map(model -> new Dto(
                        model.getTemperatura(),
                        model.getUmidade(),
                        model.getIluminacao(),
                        model.getTime()
                ))
                .toList();
    }

    /**
     * Metodo responsavel por mostrar os dados ao vivo
     */
    public Dto viewData(){

        List<Model> listModel = repository.findAll();

        if (listModel.isEmpty()){
             Dto dtoZero = new Dto("00", "00", "00", null);
             return dtoZero;
        }

        Model dataModel = listModel.get(listModel.size() -1);

        Dto newDto = new Dto(
                dataModel.getTemperatura(),
                dataModel.getUmidade(),
                dataModel.getIluminacao(),
                dataModel.getTime()
        );

        return newDto;
    }
}
