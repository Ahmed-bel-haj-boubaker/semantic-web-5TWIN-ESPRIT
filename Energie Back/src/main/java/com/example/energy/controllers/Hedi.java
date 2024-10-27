package com.example.energy.controllers;

import com.example.energy.entities.Equipement;
import com.example.energy.entities.FournisseurEquipement;
import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.rdf.model.*;
import org.apache.jena.util.FileManager;
import org.apache.jena.vocabulary.RDF;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.util.FileManager;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;

@RestController
@CrossOrigin(origins ="*")
@RequestMapping(path = "/hedi",produces = "application/json")

public class Hedi {
    private final Model model;
    String NS = "http://www.semanticweb.org/hedi/ontologies/2024/9/untitled-ontology-10#";

    private final String RDF_FILE ="data/sementique_finale.rdf";

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

    @GetMapping("/fournisseurs")
    public String getFournisseurs()
    {
        String queryString = "PREFIX ns: <http://www.semanticweb.org/hedi/ontologies/2024/9/untitled-ontology-10#>\n"
                + "\n" + "SELECT ?fournisseur ?nom ?contact ?statut  ?disponibilite ?type_equipement \n" + "WHERE {\n"
                + "    ?fournisseur a ns:Fournisseur .\n"
                + "    ?fournisseur ns:nom ?nom .\n"
                + "    ?fournisseur ns:contact ?contact .\n"
                + "    ?fournisseur ns:statut ?statut .\n"
                + "    ?fournisseur ns:disponibilite ?disponibilite .\n"
                + "    ?fournisseur ns:type_equipement ?type_equipement .\n"


                + "}";
        String qexec = queryString;

        QueryExecution qe = QueryExecutionFactory.create(qexec, model);
        ResultSet results = qe.execSelect();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        ResultSetFormatter.outputAsJSON(outputStream, results);

        String json = new String(outputStream.toByteArray());
        JSONObject j = new JSONObject(json);

        JSONArray res = j.getJSONObject("results").getJSONArray("bindings");

        return j.getJSONObject("results").getJSONArray("bindings").toString();
    }

    @PostMapping("/addFournisseur")
    public ResponseEntity<String> addFournisseur(@RequestBody FournisseurEquipement fournisseurEquipement) {

        OntModel ontModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM_MICRO_RULE_INF, model);

        Individual postIndividual = ontModel.createIndividual(NS + "Fournisseur" + System.currentTimeMillis(),
                ontModel.getOntClass(NS + "Fournisseur"));

        postIndividual.addProperty(ontModel.getDatatypeProperty(NS + "nom"), fournisseurEquipement.getNom());
        postIndividual.addProperty(ontModel.getDatatypeProperty(NS + "contact"), fournisseurEquipement.getContact());
        postIndividual.addProperty(ontModel.getDatatypeProperty(NS + "statut"), fournisseurEquipement.getStatut());
        postIndividual.addProperty(ontModel.getDatatypeProperty(NS + "disponibilite"), fournisseurEquipement.getDisponibilite());

        postIndividual.addProperty(ontModel.getDatatypeProperty(NS + "type_equipement"), fournisseurEquipement.getType_equipement());



        try (OutputStream outputStream = new FileOutputStream(RDF_FILE)) {
            ontModel.write(outputStream, "RDF/XML-ABBREV");
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to add the Fournisseur.");
        }

        return ResponseEntity.status(HttpStatus.CREATED).body("Fournisseur added successfully.");
    }



    @GetMapping()
    public String gte() {

        String queryString = "PREFIX ns: <http://www.semanticweb.org/hedi/ontologies/2024/9/untitled-ontology-10#>\n"
                + "\n" + "SELECT ?equipement ?capacite ?type \n" + "WHERE {\n"
                + "    ?equipement a ns:Equipement .\n"
                + "    ?equipement ns:capacite ?capacite .\n"
                + "    ?equipement ns:type ?type .\n"
               + "}";
        String qexec = queryString;

        QueryExecution qe = QueryExecutionFactory.create(qexec, model);
        ResultSet results = qe.execSelect();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        ResultSetFormatter.outputAsJSON(outputStream, results);

        String json = new String(outputStream.toByteArray());
        JSONObject j = new JSONObject(json);

        JSONArray res = j.getJSONObject("results").getJSONArray("bindings");

        return j.getJSONObject("results").getJSONArray("bindings").toString();
    }

    @PostMapping()
    public ResponseEntity<String> add(@RequestBody Equipement equipement) {

        OntModel ontModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM_MICRO_RULE_INF, model);

        Individual postIndividual = ontModel.createIndividual(NS + "Equipement" + System.currentTimeMillis(), ontModel.getOntClass(NS + "Equipement"));

        postIndividual.addProperty(ontModel.getDatatypeProperty(NS + "capacite"), equipement.getCapacite().toString());
        postIndividual.addProperty(ontModel.getDatatypeProperty(NS + "type"), equipement.getType());

        try (OutputStream outputStream = new FileOutputStream(RDF_FILE)) {
            ontModel.write(outputStream, "RDF/XML-ABBREV");
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to add the equipement.");
        }

        return ResponseEntity.status(HttpStatus.CREATED).body("Equipement added successfully.");
    }


    @GetMapping("{search}")
    public String searchEquipement(@PathVariable String search)
    {
        loadModel();

        String queryString = "PREFIX ns: <http://www.semanticweb.org/hedi/ontologies/2024/9/untitled-ontology-10#>\n" +
                "\n" +
                "SELECT ?equipement ?type ?capacite\n" +
                "WHERE {\n" +
                "    ?equipement a ns:Equipement .\n" +
                "    ?equipement ns:type ?type .\n" +
                "    ?equipement ns:capacite ?capacite .\n" +
                "    FILTER(CONTAINS(LCASE(?type), LCASE(\"" + search + "\")))\n" +
                "}";
        String qexec = queryString;

        QueryExecution qe = QueryExecutionFactory.create(qexec, model);
        ResultSet results = qe.execSelect();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        ResultSetFormatter.outputAsJSON(outputStream, results);

        String json = new String(outputStream.toByteArray());
        JSONObject j = new JSONObject(json);

        JSONArray res = j.getJSONObject("results").getJSONArray("bindings");

        return j.getJSONObject("results").getJSONArray("bindings").toString();

    }


    @DeleteMapping()
    public ResponseEntity<String> deleteEquipement(@RequestParam ("URI") String EqURI) {

        // Create an OntModel that performs inference
        OntModel ontModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM_MICRO_RULE_INF, model);
        Individual postIndividual = ontModel.getIndividual(EqURI);

        if (postIndividual != null) {
            postIndividual.remove();
            try (OutputStream outputStream = new FileOutputStream(RDF_FILE)) {
                ontModel.write(outputStream, "RDF/XML-ABBREV");
            } catch (IOException e) {
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete the equipement.");
            }

            return ResponseEntity.status(HttpStatus.OK).body("equipement deleted successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("equipement not found.");
        }
    }

    @DeleteMapping("/fournisseur")
    public ResponseEntity<String> deleteFournisseur(@RequestParam ("URI") String EqURI) {

        // Create an OntModel that performs inference
        OntModel ontModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM_MICRO_RULE_INF, model);
        Individual postIndividual = ontModel.getIndividual(EqURI);

        if (postIndividual != null) {
            postIndividual.remove();
            try (OutputStream outputStream = new FileOutputStream(RDF_FILE)) {
                ontModel.write(outputStream, "RDF/XML-ABBREV");
            } catch (IOException e) {
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete the fournisseur.");
            }

            return ResponseEntity.status(HttpStatus.OK).body("fournisseur deleted successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("fournisseur not found.");
        }
    }

    @GetMapping("search/{search}")
    public String search(@PathVariable String search)
    {
        loadModel();

        String queryString = "PREFIX ns: <http://www.semanticweb.org/hedi/ontologies/2024/9/untitled-ontology-10#>\n" +
                "\n" +
                "SELECT ?fournisseur ?nom ?contact ?statut ?disponibilite ?type_equipement\n" +
                "WHERE {\n" +
                "    ?fournisseur a ns:Fournisseur .\n" +
                "    ?fournisseur ns:nom ?nom .\n" +
                "    ?fournisseur ns:contact ?contact .\n" +
                "    ?fournisseur ns:statut ?statut .\n" +
                "    ?fournisseur ns:disponibilite ?disponibilite .\n" +
                "    ?fournisseur ns:type_equipement ?type_equipement .\n" +
                "    FILTER(CONTAINS(LCASE(?statut), LCASE(\"" + search + "\")))\n" +
                "}";
        String qexec = queryString;

        QueryExecution qe = QueryExecutionFactory.create(qexec, model);
        ResultSet results = qe.execSelect();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        ResultSetFormatter.outputAsJSON(outputStream, results);

        String json = new String(outputStream.toByteArray());
        JSONObject j = new JSONObject(json);

        JSONArray res = j.getJSONObject("results").getJSONArray("bindings");

        return j.getJSONObject("results").getJSONArray("bindings").toString();

    }


}
