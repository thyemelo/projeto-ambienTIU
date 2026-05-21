package br.com.ambientiubackend.service;

import br.com.ambientiubackend.dto.Dto;
import br.com.ambientiubackend.model.Model;
import br.com.ambientiubackend.repository.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class AmbienTiuService {

    @Autowired
    Repository repository;

    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

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

        // Informar dados atualizados para o método viewDataLive no qual informará os dados ao vivo
        Dto updateDto = new Dto(
                newModel.getTemperatura(),
                newModel.getUmidade(),
                newModel.getIluminacao(),
                newModel.getTime()
        );
        viewDataLive(updateDto);

        return dto;
    }

    /**
     *
     * Metodo responsavel por:
     *
     * Enviar dados ao vivo para o navegador
     * Remover usuário que tenha fechado navegador para melhor processamento do programa
     */
    private void viewDataLive(Dto dto){

        List<SseEmitter> closedEmitters = new ArrayList<>();

        for (SseEmitter emitter : emitters){
            // Envia os dados atualizados ao navegador
            try {
                emitter.send(SseEmitter.event().data(dto));
            } catch (IOException e) {
                // Caso o usúario tenha fechado o navegador, removeremos o usuario após finalização do for
                closedEmitters.add(emitter);
            }
        }

        emitters.removeAll(closedEmitters);
    }

    /**
     * Método responsável por enviar os dados ao Controller em tempo real
     */
    public SseEmitter subscribeLiveStream(){

        // Dados continuaram atualizando para o usuario por tempo ilimitado até que ele feche o navegador
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);

        // Adciona um novo usuario na lista da transmissão ao vivo
        this.emitters.add(emitter);

        // Caso o usuario feche a abba ou saia do navegador, remove da lista da transmissão ao vivo
        emitter.onCompletion(() -> this.emitters.remove(emitter));

        // Envia o ultimo dado do banco de dados para que a tela não comece vazia
        try {
            emitter.send(SseEmitter.event().data(viewData()));
        } catch(IOException ignored) {}

        return emitter;
    }

    /**
     * Metodo responsavel por mostrar o último dado salvo
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
}
