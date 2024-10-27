package com.example.energy.controllers;


import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.util.FileManager;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;

@RestController
@CrossOrigin(origins ="*")
@RequestMapping(path = "/hedi",produces = "application/json")

public class Hedi {
    private final Model model;
    String NS = "";

    private final String RDF_FILE ="data/test.owl";

    public Hedi() {
        this.model = ModelFactory.createDefaultModel();
        loadModel();
    }
    private void loadModel() {
        FileManager.get().readModel(model, RDF_FILE);
    }
    public Model getModel() {
        return model;
    }

    @GetMapping()
    public String getEquipements(){



    }


}
