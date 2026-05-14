package br.com.ambientiubackend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.LocalDateTime;

@Entity
public class Model {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String temperate;
    private String humidity;
    private String ilumination;
    private LocalDateTime time;

    public Model(){}

    public Model(String temperate, String humidity, String ilumination, LocalDateTime time){
        setTemperate(temperate);
        setHumidity(humidity);
        setIlumination(ilumination);
        setTime(time);
    }

    public void setTemperate(String temperate) {
        this.temperate = temperate;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }

    public void setIlumination(String ilumination){
        this.ilumination = ilumination;
    }

    public void setTime(LocalDateTime time){
        this.time = time;
    }

    public String getTemperate(){
        return temperate;
    }

    public String getHumidity() {
        return humidity;
    }

    public String getIlumination() {
        return ilumination;
    }

    public LocalDateTime getTime() {
        return time;
    }
}
