package io.zhc1.realworld.controller.api.v1;

import io.zhc1.realworld.model.Comentario;
import io.zhc1.realworld.repository.ComentarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/comentarios")
public class ComentarioController {

    @Autowired
    private ComentarioRepository comentarioRepository;

    @GetMapping
    public List<Comentario> listarComentarios() {
        return comentarioRepository.findAll();
    }
}