package io.zhc1.realworld.controller.api.v1;

import io.zhc1.realworld.model.Lugar;
import io.zhc1.realworld.repository.LugarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/lugares")
public class LugarController {

    @Autowired
    private LugarRepository lugarRepository;

    @GetMapping
    public List<Lugar> listarLugares() {
        return lugarRepository.findAll();
    }
}