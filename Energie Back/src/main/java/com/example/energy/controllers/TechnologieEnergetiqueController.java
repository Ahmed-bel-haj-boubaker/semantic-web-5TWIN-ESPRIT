package com.example.energy.controllers;

import com.example.energy.JenaEngine;
import com.example.energy.entities.TechnologieEnergetique;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.update.UpdateAction;
import org.apache.jena.update.UpdateFactory;
import org.apache.jena.update.UpdateRequest;
import org.springframework.web.bind.annotation.*;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/technologie")
public class TechnologieEnergetiqueController {

    private static final String ONTOLOGY_FILE_PATH = "data/test.owl";
    private static final String DEFAULT_NS = "http://www.semanticweb.org/saidg/ontologies/2024/8/untitled-ontology-8#";

    @GetMapping("/all")
    public List<TechnologieEnergetique> getAllTechnologies() {
        List<TechnologieEnergetique> technologies = new ArrayList<>();
        Model model = JenaEngine.readModel(ONTOLOGY_FILE_PATH);

        if (model == null) {
            System.out.println("Erreur lors de la lecture du modèle de l'ontologie.");
            return technologies;
        }

        String NS = model.getNsPrefixURI("");
        if (NS == null || NS.isEmpty()) {
            NS = DEFAULT_NS;
        }

        // Requête SPARQL pour récupérer toutes les technologies énergétiques
        String sparqlQuery = "PREFIX ns: <" + NS + ">\n" +
                "SELECT ?nomTechnologie ?efficacite WHERE { " +
                "?technologie ns:nomTechnologie ?nomTechnologie . " +
                "?technologie ns:efficacite ?efficacite . }";

        Query query = QueryFactory.create(sparqlQuery);
        try (QueryExecution qexec = QueryExecutionFactory.create(query, model)) {
            ResultSet results = qexec.execSelect();

            while (results.hasNext()) {
                QuerySolution soln = results.nextSolution();
                TechnologieEnergetique technologie = new TechnologieEnergetique();

                // Récupérer les données et les affecter à l'objet technologie
                technologie.setNomTechnologie(soln.getLiteral("nomTechnologie").getString());
                technologie.setEfficacite(soln.getLiteral("efficacite").getDouble());
                technologies.add(technologie);
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de l'exécution de la requête SPARQL : " + e.getMessage());
        }

        return technologies;
    }

    @PostMapping("/add")
    public String addTechnologie(@RequestBody TechnologieEnergetique technologie) {
        // Vérification de la valeur de l'efficacité
        if (technologie.getEfficacite() < 0) {
            return "Erreur : l'efficacité doit être positive.";
        }

        Model model = JenaEngine.readModel(ONTOLOGY_FILE_PATH);
        if (model == null) {
            return "Erreur lors de la lecture du modèle de l'ontologie.";
        }

        String NS = model.getNsPrefixURI("");
        if (NS == null || NS.isEmpty()) {
            NS = DEFAULT_NS;
        }

        // Requête SPARQL pour insérer une nouvelle technologie
        String sparqlInsert = "PREFIX ns: <" + NS + ">\n" +
                "INSERT DATA { ns:Technologie_" + technologie.getNomTechnologie().replaceAll(" ", "_") +
                " ns:nomTechnologie \"" + technologie.getNomTechnologie() + "\" ; " +
                "ns:efficacite " + technologie.getEfficacite() + " . }";

        UpdateRequest updateRequest = UpdateFactory.create(sparqlInsert);
        UpdateAction.execute(updateRequest, model);

        // Sauvegarder le modèle mis à jour dans le fichier d'ontologie
        try (OutputStream out = new FileOutputStream(ONTOLOGY_FILE_PATH)) {
            model.write(out, "RDF/XML");
            return "Technologie ajoutée avec succès et sauvegardée dans test.owl";
        } catch (Exception e) {
            return "Erreur lors de l'enregistrement du modèle : " + e.getMessage();
        }
    }

    @PutMapping("/update")
    public String updateTechnologie(@RequestBody TechnologieEnergetique technologie) {
        // Vérification de la valeur de l'efficacité
        if (technologie.getEfficacite() < 0) {
            return "Erreur : l'efficacité doit être positive.";
        }

        Model model = JenaEngine.readModel(ONTOLOGY_FILE_PATH);
        if (model == null) {
            return "Erreur lors de la lecture du modèle de l'ontologie.";
        }

        String NS = model.getNsPrefixURI("");
        if (NS == null || NS.isEmpty()) {
            NS = DEFAULT_NS;
        }

        // Requête SPARQL pour mettre à jour une technologie existante
        String sparqlUpdate = "PREFIX ns: <" + NS + ">\n" +
                "DELETE { ns:Technologie_" + technologie.getNomTechnologie().replaceAll(" ", "_") +
                " ns:efficacite ?oldEfficacite . }\n" +
                "INSERT { ns:Technologie_" + technologie.getNomTechnologie().replaceAll(" ", "_") +
                " ns:efficacite " + technologie.getEfficacite() + " . }\n" +
                "WHERE { ns:Technologie_" + technologie.getNomTechnologie().replaceAll(" ", "_") +
                " ns:efficacite ?oldEfficacite . }";

        UpdateRequest updateRequest = UpdateFactory.create(sparqlUpdate);
        UpdateAction.execute(updateRequest, model);

        // Sauvegarder le modèle mis à jour dans le fichier d'ontologie
        try (OutputStream out = new FileOutputStream(ONTOLOGY_FILE_PATH)) {
            model.write(out, "RDF/XML");
            return "Technologie mise à jour avec succès et sauvegardée dans test.owl";
        } catch (Exception e) {
            return "Erreur lors de l'enregistrement du modèle : " + e.getMessage();
        }
    }

    @DeleteMapping("/delete")
    public String deleteTechnologie(@RequestParam String nomTechnologie) {
        Model model = JenaEngine.readModel(ONTOLOGY_FILE_PATH);

        if (model == null) {
            return "Erreur lors de la lecture du modèle de l'ontologie.";
        }

        String NS = model.getNsPrefixURI("");
        if (NS == null || NS.isEmpty()) {
            NS = DEFAULT_NS;
        }

        // Requête SPARQL pour supprimer une technologie basée sur nomTechnologie
        String sparqlDelete = "PREFIX ns: <" + NS + ">\n" +
                "DELETE { ns:Technologie_" + nomTechnologie.replaceAll(" ", "_") + " ?p ?o . }\n" +
                "WHERE { ns:Technologie_" + nomTechnologie.replaceAll(" ", "_") + " ?p ?o . }";

        UpdateRequest updateRequest = UpdateFactory.create(sparqlDelete);
        UpdateAction.execute(updateRequest, model);

        // Sauvegarder le modèle mis à jour dans le fichier d'ontologie
        try (OutputStream out = new FileOutputStream(ONTOLOGY_FILE_PATH)) {
            model.write(out, "RDF/XML");
            return "Technologie supprimée avec succès et sauvegardée dans test.owl";
        } catch (Exception e) {
            return "Erreur lors de l'enregistrement du modèle : " + e.getMessage();
        }
    }

    @GetMapping("/searchByEfficacite")
    public List<TechnologieEnergetique> searchByEfficacite(@RequestParam double min, @RequestParam double max) {
        List<TechnologieEnergetique> technologies = new ArrayList<>();
        Model model = JenaEngine.readModel(ONTOLOGY_FILE_PATH);

        if (model == null) {
            System.out.println("Erreur lors de la lecture du modèle de l'ontologie.");
            return technologies;
        }

        String NS = model.getNsPrefixURI("");
        if (NS == null || NS.isEmpty()) {
            NS = DEFAULT_NS;
        }

        String sparqlQuery = "PREFIX ns: <" + NS + ">\n" +
                "SELECT ?nomTechnologie ?efficacite WHERE { " +
                "?technologie ns:nomTechnologie ?nomTechnologie . " +
                "?technologie ns:efficacite ?efficacite . " +
                "FILTER(?efficacite >= " + min + " && ?efficacite <= " + max + ") }";

        try (QueryExecution qexec = QueryExecutionFactory.create(QueryFactory.create(sparqlQuery), model)) {
            ResultSet results = qexec.execSelect();

            while (results.hasNext()) {
                QuerySolution soln = results.nextSolution();
                TechnologieEnergetique technologie = new TechnologieEnergetique();
                technologie.setNomTechnologie(soln.getLiteral("nomTechnologie").getString());
                technologie.setEfficacite(soln.getLiteral("efficacite").getDouble());
                technologies.add(technologie);
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de l'exécution de la requête SPARQL : " + e.getMessage());
        }

        return technologies;
    }
}
