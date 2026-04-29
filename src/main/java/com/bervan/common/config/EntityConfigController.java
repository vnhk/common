package com.bervan.common.config;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/config")
public class EntityConfigController {

    private final BervanViewConfig viewConfig;

    public EntityConfigController(BervanViewConfig viewConfig) {
        this.viewConfig = viewConfig;
    }

    @GetMapping
    public ResponseEntity<Map<String, Map<String, ClassViewAutoConfigColumn>>> getAll() {
        return ResponseEntity.ok(viewConfig);
    }
}
