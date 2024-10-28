package com.example.energy.controllers;
import com.example.energy.entities.EmplacementRequest;

import com.example.energy.entities.Emplacement;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.update.UpdateAction;
import org.apache.jena.update.UpdateFactory;
import org.apache.jena.update.UpdateRequest;
import org.apache.jena.util.FileManager;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.*;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(path = "/emplacement", produces = "application/json")
public class EmplacementController {

    private final Model model;
    private final String NS = "http://www.example.com/espritont#";
    private final String RDF_FILE = "data/test.owl";

    public EmplacementController() {
        this.model = ModelFactory.createDefaultModel();
        loadModel();
    }

    private void loadModel() {
        FileManager.get().readModel(model, RDF_FILE);
    }

    @GetMapping("/list")
    public String getEmplacements() {
        String sparqlQuery = "PREFIX ns: <" + NS + "> "
                + "SELECT ?adresse ?conditions ?coordonnees WHERE { "
                + "?emp ns:adresse ?adresse ; "
                + "ns:conditions_environnementales ?conditions ; "
                + "ns:coordonnees ?coordonnees . }";

        List<Emplacement> emplacements = new ArrayList<>();

        try (QueryExecution qexec = QueryExecutionFactory.create(sparqlQuery, model)) {
            ResultSet results = qexec.execSelect();
            while (results.hasNext()) {
                QuerySolution soln = results.nextSolution();
                String adresse = soln.getLiteral("adresse").getString();
                String conditions = soln.getLiteral("conditions").getString();
                String coordonnees = soln.getLiteral("coordonnees").getString();
                emplacements.add(new Emplacement(adresse, conditions, coordonnees));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        JSONArray jsonArray = new JSONArray();
        for (Emplacement emp : emplacements) {
            JSONObject jsonObj = new JSONObject();
            jsonObj.put("adresse", emp.getAdresse());
            jsonObj.put("conditions_environnementales", emp.getConditions_environnementales());
            jsonObj.put("coordonnees", emp.getCoordonnees());
            jsonArray.put(jsonObj);
        }

        return jsonArray.toString();
    }

    @PostMapping("/add")
    public String addEmplacement(@RequestBody Emplacement emplacement) {
        String newEmpUri = NS + "Emplacement_" + UUID.randomUUID();

        String sparqlUpdate = "PREFIX ns: <" + NS + "> "
                + "INSERT DATA { "
                + "<" + newEmpUri + "> a ns:Emplacement; "
                + "ns:adresse \"" + emplacement.getAdresse() + "\" ; "
                + "ns:conditions_environnementales \"" + emplacement.getConditions_environnementales() + "\" ; "
                + "ns:coordonnees \"" + emplacement.getCoordonnees() + "\" . }";

        try {
            UpdateRequest updateRequest = UpdateFactory.create(sparqlUpdate);
            UpdateAction.execute(updateRequest, model);

            try (FileOutputStream out = new FileOutputStream(RDF_FILE)) {
                model.write(out, "RDF/XML");
            } catch (Exception e) {
                JSONObject errorResponse = new JSONObject();
                errorResponse.put("message", "Failed to save the RDF model");
                errorResponse.put("error", e.getMessage());
                return errorResponse.toString();
            }

        } catch (Exception e) {
            JSONObject errorResponse = new JSONObject();
            errorResponse.put("message", "Failed to add emplacement");
            errorResponse.put("error", e.getMessage());
            return errorResponse.toString();
        }

        JSONObject response = new JSONObject();
        response.put("message", "Emplacement added successfully");
        response.put("uri", newEmpUri);
        return response.toString();
    }

    @DeleteMapping("/delete/{conditions_environnementales}")
    public String deleteEmplacement(@PathVariable("conditions_environnementales") String conditionsEnvironnementales) {
        Property conditionsEnvProperty = model.createProperty(NS, "conditions_environnementales");

        String sparqlQuery = "PREFIX ns: <" + NS + "> "
                + "SELECT ?emp WHERE { ?emp ns:conditions_environnementales \"" + conditionsEnvironnementales + "\" }";

        try (QueryExecution qexec = QueryExecutionFactory.create(sparqlQuery, model)) {
            ResultSet results = qexec.execSelect();

            if (results.hasNext()) {
                QuerySolution soln = results.nextSolution();
                Resource empResource = soln.getResource("emp");

                model.removeAll(empResource, null, null);
                model.removeAll(null, null, empResource);

                JSONObject response = new JSONObject();
                response.put("message", "Emplacement deleted successfully");
                response.put("conditions_environnementales", conditionsEnvironnementales);
                return response.toString();
            } else {
                JSONObject response = new JSONObject();
                response.put("message", "Emplacement not found");
                return response.toString();
            }
        }
    }


    @PutMapping("/update/{conditions_environnementales}")
    public String updateEmplacement(@PathVariable("conditions_environnementales") String conditionsEnvironnementales,
                                    @RequestBody Emplacement updatedEmplacement) {
        // Define the property for conditions_environnementales
        Property conditionsEnvProperty = model.createProperty(NS, "conditions_environnementales");

        // Find the emplacement resource by the conditions_environnementales property
        String sparqlQuery = "PREFIX ns: <" + NS + "> "
                + "SELECT ?emp WHERE { ?emp ns:conditions_environnementales \"" + conditionsEnvironnementales + "\" }";

        try (QueryExecution qexec = QueryExecutionFactory.create(sparqlQuery, model)) {
            ResultSet results = qexec.execSelect();

            if (results.hasNext()) {
                QuerySolution soln = results.nextSolution();
                Resource empResource = soln.getResource("emp"); // Use "emp" instead of "org"

                // Update conditions_environnementales if provided
                if (updatedEmplacement.getConditions_environnementales() != null) {
                    empResource.removeAll(conditionsEnvProperty);
                    empResource.addProperty(conditionsEnvProperty, updatedEmplacement.getConditions_environnementales());
                }

                // Update adresse if provided
                if (updatedEmplacement.getAdresse() != null) {
                    Property adresseProperty = model.createProperty(NS, "adresse");
                    empResource.removeAll(adresseProperty);
                    empResource.addProperty(adresseProperty, updatedEmplacement.getAdresse());
                }

                // Update coordonnees if provided
                if (updatedEmplacement.getCoordonnees() != null) {
                    Property coordonneesProperty = model.createProperty(NS, "coordonnees");
                    empResource.removeAll(coordonneesProperty);
                    empResource.addProperty(coordonneesProperty, updatedEmplacement.getCoordonnees());
                }

                // Prepare successful response
                JSONObject response = new JSONObject();
                response.put("message", "Emplacement updated successfully");
                response.put("conditions_environnementales", updatedEmplacement.getConditions_environnementales());
                return response.toString();
            } else {
                // Prepare not found response
                JSONObject response = new JSONObject();
                response.put("message", "Emplacement not found");
                return response.toString();
            }
        }
    }

    @GetMapping("/exists")
    public String checkEmplacementExists(@RequestBody EmplacementRequest emplacementRequest) {
        String conditionsEnvironnementales = emplacementRequest.getConditions_environnementales();

        // Construct the ASK query using the conditions_environnementales
        String askQuery = "PREFIX ns: <" + NS + "> "
                + "ASK WHERE { "
                + "?emp a ns:Emplacement ; "
                + "ns:conditions_environnementales \"" + conditionsEnvironnementales + "\" . "
                + "}";

        boolean exists;
        try (QueryExecution qexec = QueryExecutionFactory.create(askQuery, model)) {
            exists = qexec.execAsk();
        }

        // Prepare the JSON response
        JSONObject response = new JSONObject();
        if (exists) {
            response.put("message", "The emplacement exists.");
            response.put("exists", true);
        } else {
            response.put("message", "The emplacement does not exist.");
            response.put("exists", false);
        }
        return response.toString();
    }


}
