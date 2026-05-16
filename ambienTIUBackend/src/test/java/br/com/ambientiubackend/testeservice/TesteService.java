package br.com.ambientiubackend.testeservice;

import br.com.ambientiubackend.dto.Dto;
import br.com.ambientiubackend.model.Model;
import br.com.ambientiubackend.repository.Repository;
import br.com.ambientiubackend.service.AmbienTiuService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class TesteService {

    @Mock
    private Repository repository;

    @InjectMocks
    private AmbienTiuService ambienTiuService;

    @Test
    public void salvarDadosSucesso(){

        LocalDateTime dataTeste = LocalDateTime.of(2026, 5, 16, 14, 30);
        Dto inputDto = new Dto("21", "29", "60", dataTeste);
        ArgumentCaptor<Model> modelCaptor = ArgumentCaptor.forClass(Model.class);

        ambienTiuService.saveData(inputDto);

        verify(repository, times(1)).save(modelCaptor.capture());

        Model modelSalvo = modelCaptor.getValue();
        assertEquals("21", modelSalvo.getTemperature());
        assertEquals("29", modelSalvo.getHumidity());
        assertEquals("60", modelSalvo.getIlumination());
        assertEquals(dataTeste, modelSalvo.getTime());
    }

    @Test
    public void dadosAoVivoSucesso(){

        LocalDateTime dataTeste = LocalDateTime.of(2026, 1, 20, 10, 12);
        Model inputModel = new Model("31", "34", "120", dataTeste);

        Dto resultado = ambienTiuService.viewData(inputModel);

        assertNotNull(resultado);
        assertEquals("31", resultado.temperature());
        assertEquals("34", resultado.humidity());
        assertEquals("120", resultado.ilumination());
        assertEquals(dataTeste, resultado.time());
    }
}
