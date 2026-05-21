package br.com.ambientiubackend.controller;

import br.com.ambientiubackend.model.Model;
import br.com.ambientiubackend.dto.Dto;
import br.com.ambientiubackend.service.AmbienTiuService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@RestController
@RequestMapping ("/data")
public class AmbienTiuController {

    @Autowired
    AmbienTiuService ambienTiuService;

    @PostMapping
    public ResponseEntity<Dto> saveDataController(@RequestBody @Valid Dto dto){

        Dto newData = ambienTiuService.saveData(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(newData);
    }

    @GetMapping
    public ResponseEntity<Dto> viewData(){
        Dto dto = ambienTiuService.viewData();
        return ResponseEntity.ok(dto);
    }

    @GetMapping ("/stream")
    public SseEmitter liveStream(){
        return ambienTiuService.subscribeLiveStream();
    }

    @GetMapping ("/alldata")
    public ResponseEntity<List<Dto>> viewAllDataController(){
        List<Dto> allData = ambienTiuService.viewAllData();
        return ResponseEntity.ok(allData);

    }
}
