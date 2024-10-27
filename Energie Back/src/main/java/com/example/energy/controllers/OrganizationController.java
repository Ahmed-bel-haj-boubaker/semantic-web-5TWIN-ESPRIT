package com.example.energy.controllers;

import com.example.energy.entities.Organization;

import com.example.energy.entities.OrganizationRequest;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.update.*;
import org.apache.jena.util.FileManager;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(path = "/organization", produces = "application/json")
public class OrganizationController {

    private final Model model;
    private final String NS = "http://www.example.com/espritont#";


    private final String RDF_FILE = "data/test.owl";

    public OrganizationController() {
        this.model = ModelFactory.createDefaultModel();
        loadModel();
    }

    private void loadModel() {
        FileManager.get().readModel(model, RDF_FILE);
    }

    public Model getModel() {
        return model;
    }


    @GetMapping("/organizations")
    public String getOrganizations() {
        String sparqlQuery = "PREFIX ns: <" + NS + "> "
                + "SELECT ?nom WHERE { ?org ns:nom ?nom }";

        List<Organization> organizations = new ArrayList<>();

        try (QueryExecution qexec = QueryExecutionFactory.create(sparqlQuery, model)) {
            ResultSet results = qexec.execSelect();

            while (results.hasNext()) {
                QuerySolution soln = results.nextSolution();
                String nom = soln.getLiteral("nom").getString();

                Organization org = new Organization(nom);
                organizations.add(org);
            }
        }

        // Convert the list to JSON
        JSONArray jsonArray = new JSONArray();
        for (Organization org : organizations) {
            JSONObject jsonObj = new JSONObject();
            jsonObj.put("nom", org.getNom());
            jsonArray.put(jsonObj);
        }

        return jsonArray.toString();
    }
    @PostMapping("/add")
    public String addOrganization(@RequestBody Organization organization) {
        // Generate a unique URI for the new organization
        String newOrgUri = NS + "Organization_" + UUID.randomUUID();

        // Define SPARQL INSERT query to add the organization
        String sparqlUpdate = "PREFIX ns: <" + NS + "> "
                + "INSERT DATA { "
                + "<" + newOrgUri + "> a ns:Organization; "  // Adding the rdf:type
                + "ns:nom \"" + organization.getNom() + "\" ."
                + "}";

        // Execute the SPARQL update
        try {
            UpdateRequest updateRequest = UpdateFactory.create(sparqlUpdate);
            UpdateAction.execute(updateRequest, model);

            // Persist changes to the RDF file
            try (FileOutputStream out = new FileOutputStream(RDF_FILE)) {
                model.write(out, "RDF/XML"); // Save in RDF/XML format
            } catch (Exception e) {
                JSONObject errorResponse = new JSONObject();
                errorResponse.put("message", "Failed to save the RDF model");
                errorResponse.put("error", e.getMessage());
                return errorResponse.toString();
            }

        } catch (Exception e) {
            JSONObject errorResponse = new JSONObject();
            errorResponse.put("message", "Failed to add organization");
            errorResponse.put("error", e.getMessage());
            return errorResponse.toString();
        }

        // Return confirmation in JSON format
        JSONObject response = new JSONObject();
        response.put("message", "Organization added successfully");
        response.put("uri", newOrgUri);
        return response.toString();
    }


    @DeleteMapping("/delete/{nom}")
    public String deleteOrganization(@PathVariable("nom") String nom) {

        Property nomProperty = model.createProperty(NS, "nom");


        String sparqlQuery = "PREFIX ns: <" + NS + "> "
                + "SELECT ?org WHERE { ?org ns:nom \"" + nom + "\" }";

        try (QueryExecution qexec = QueryExecutionFactory.create(sparqlQuery, model)) {
            ResultSet results = qexec.execSelect();

            if (results.hasNext()) {
                QuerySolution soln = results.nextSolution();
                Resource orgResource = soln.getResource("org");


                model.removeAll(orgResource, null, null);
                model.removeAll(null, null, orgResource);


                JSONObject response = new JSONObject();
                response.put("message", "Organization deleted successfully");
                response.put("name", nom);
                return response.toString();
            } else {

                JSONObject response = new JSONObject();
                response.put("message", "Organization not found");
                return response.toString();
            }
        }
    }

    @PutMapping("/update/{nom}")
    public String updateOrganization(@PathVariable("nom") String nom, @RequestBody Organization updatedOrganization) {
        // Define the property for the organization's name
        Property nomProperty = model.createProperty(NS, "nom");

        // Find the organization resource by the "nom" property
        String sparqlQuery = "PREFIX ns: <" + NS + "> "
                + "SELECT ?org WHERE { ?org ns:nom \"" + nom + "\" }";

        try (QueryExecution qexec = QueryExecutionFactory.create(sparqlQuery, model)) {
            ResultSet results = qexec.execSelect();

            if (results.hasNext()) {
                QuerySolution soln = results.nextSolution();
                Resource orgResource = soln.getResource("org");


                if (updatedOrganization.getNom() != null) {
                    orgResource.removeAll(nomProperty);  // Remove existing name
                    orgResource.addProperty(nomProperty, updatedOrganization.getNom());  // Add updated name
                }


                JSONObject response = new JSONObject();
                response.put("message", "Organization updated successfully");
                response.put("name", updatedOrganization.getNom());
                return response.toString();
            } else {

                JSONObject response = new JSONObject();
                response.put("message", "Organization not found");
                return response.toString();
            }
        }
    }

    @GetMapping("/exists")
    public String checkOrganizationExists(@RequestBody OrganizationRequest organizationRequest) {
        String name = organizationRequest.getName();

        // Construct the ASK query using the name only
        String askQuery = "PREFIX ns: <" + NS + "> "
                + "ASK WHERE { "
                + "?org a ns:Organization ; "
                + "ns:nom \"" + name + "\" . "
                + "}";

        boolean exists;
        try (QueryExecution qexec = QueryExecutionFactory.create(askQuery, model)) {
            exists = qexec.execAsk();
        }

        // Prepare the JSON response
        JSONObject response = new JSONObject();
        if (exists) {
            response.put("message", "The organization exists.");
            response.put("exists", true);
        } else {
            response.put("message", "The organization does not exist.");
            response.put("exists", false);
        }
        return response.toString();
    }

}
