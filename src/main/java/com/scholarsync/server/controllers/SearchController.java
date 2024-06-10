package com.scholarsync.server.controllers;

import com.scholarsync.server.services.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/search")
public class SearchController {

    @Autowired private SearchService searchService;

    @GetMapping("/do-search")
    public ResponseEntity<Object> doSearch(
            @RequestParam(name = "type") String type,
            @RequestParam(name = "text") String text) {

        // Llamar al método doSearch del servicio y devolver la respuesta
        return ResponseEntity.ok(searchService.doSearch(type, text));
    }
}
