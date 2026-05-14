package br.com.ambientiubackend.repository;

import br.com.ambientiubackend.model.Model;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface Repository extends JpaRepository <Model, Long> {

    List<Model> findByTemperate(String temperate);
    List<Model> findByHumidity(String humidity);
    List<Model> findByIlumination(String ilumination);
    List<Model> findByTime(LocalDateTime time);
}
