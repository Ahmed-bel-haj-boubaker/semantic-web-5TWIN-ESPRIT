package com.example.energy.controllers;

import com.example.energy.JenaEngine;
import com.example.energy.entities.Financement;
import com.example.energy.entities.TechnologieEnergetique;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.update.UpdateAction;
import org.apache.jena.update.UpdateFactory;
import org.apache.jena.update.UpdateRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/financement")
public class FinancementController {

    private static final String ONTOLOGY_FILE_PATH = "data/test.owl";
    private static final String DEFAULT_NS = "http://www.semanticweb.org/saidg/ontologies/2024/8/untitled-ontology-8#";

    @GetMapping("/all")
    public List<Financement> getAllFinancements() {
        List<Financement> financements = new ArrayList<>();
        Model model = JenaEngine.readModel(ONTOLOGY_FILE_PATH);

        if (model == null) {
            System.out.println("Erreur lors de la lecture du modèle de l'ontologie.");
            return financements;
        }

        String NS = model.getNsPrefixURI("");
        if (NS == null || NS.isEmpty()) {
            NS = DEFAULT_NS;
        }

        // Requête SPARQL pour récupérer tous les financements
        String sparqlQuery = "PREFIX ns: <" + NS + ">\n" +
                "SELECT ?montant_total ?sourceFinancement WHERE { " +
                "?financement ns:montant_total ?montant_total . " +
                "?financement ns:sourceFinancement ?sourceFinancement . }";

        Query query = QueryFactory.create(sparqlQuery);
        try (QueryExecution qexec = QueryExecutionFactory.create(query, model)) {
            ResultSet results = qexec.execSelect();

            while (results.hasNext()) {
                QuerySolution soln = results.nextSolution();
                Financement financement = new Financement();

                // Récupérer les données et les affecter à l'objet financement
                financement.setMontantTotal(soln.getLiteral("montant_total").getDouble()); // Changement ici
                financement.setSourceFinancement(soln.getLiteral("sourceFinancement").getString());
                financements.add(financement);
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de l'exécution de la requête SPARQL : " + e.getMessage());
        }

        return financements;
    }

    @PostMapping("/add")
    public String addFinancement(@RequestBody Financement financement) {
        // Vérification de la valeur de financement
        if (financement.getMontantTotal() <= 0) { // Changement ici
            return "Erreur : le montant_total doit être supérieur à 0.";
        }

        Model model = JenaEngine.readModel(ONTOLOGY_FILE_PATH);
        if (model == null) {
            return "Erreur lors de la lecture du modèle de l'ontologie.";
        }

        String NS = model.getNsPrefixURI("");
        if (NS == null || NS.isEmpty()) {
            NS = DEFAULT_NS;
        }

        // Requête SPARQL pour insérer un nouveau financement
        String sparqlInsert = "PREFIX ns: <" + NS + ">\n" +
                "INSERT DATA { ns:Financement_" + financement.getSourceFinancement().replaceAll(" ", "_") +
                " ns:montant_total " + financement.getMontantTotal() + " ; " +
                "ns:sourceFinancement \"" + financement.getSourceFinancement() + "\" . }";

        UpdateRequest updateRequest = UpdateFactory.create(sparqlInsert);
        UpdateAction.execute(updateRequest, model);

        // Sauvegarder le modèle mis à jour dans le fichier d'ontologie
        try (OutputStream out = new FileOutputStream(ONTOLOGY_FILE_PATH)) {
            model.write(out, "RDF/XML");
            return "Financement ajouté avec succès et sauvegardé dans test.owl";
        } catch (Exception e) {
            return "Erreur lors de l'enregistrement du modèle : " + e.getMessage();
        }
    }

    @PutMapping("/update")
    public String updateFinancement(@RequestBody Financement financement) {
        // Vérification de la valeur de financement
        if (financement.getMontantTotal() <= 0) { // Changement ici
            return "Erreur : le montant_total doit être supérieur à 0.";
        }

        Model model = JenaEngine.readModel(ONTOLOGY_FILE_PATH);
        if (model == null) {
            return "Erreur lors de la lecture du modèle de l'ontologie.";
        }

        String NS = model.getNsPrefixURI("");
        if (NS == null || NS.isEmpty()) {
            NS = DEFAULT_NS;
        }

        // Requête SPARQL pour mettre à jour un financement existant
        String sparqlUpdate = "PREFIX ns: <" + NS + ">\n" +
                "DELETE { ns:Financement_" + financement.getSourceFinancement().replaceAll(" ", "_") +
                " ns:montant_total ?oldMontant ; " +
                "ns:sourceFinancement ?oldSource . }\n" +
                "INSERT { ns:Financement_" + financement.getSourceFinancement().replaceAll(" ", "_") +
                " ns:montant_total " + financement.getMontantTotal() + " ; " +
                "ns:sourceFinancement \"" + financement.getSourceFinancement() + "\" . }\n" +
                "WHERE { ns:Financement_" + financement.getSourceFinancement().replaceAll(" ", "_") +
                " ns:montant_total ?oldMontant ; " +
                "ns:sourceFinancement ?oldSource . }";

        UpdateRequest updateRequest = UpdateFactory.create(sparqlUpdate);
        UpdateAction.execute(updateRequest, model);

        // Sauvegarder le modèle mis à jour dans le fichier d'ontologie
        try (OutputStream out = new FileOutputStream(ONTOLOGY_FILE_PATH)) {
            model.write(out, "RDF/XML");
            return "Financement mis à jour avec succès et sauvegardé dans test.owl";
        } catch (Exception e) {
            return "Erreur lors de l'enregistrement du modèle : " + e.getMessage();
        }
    }

    @DeleteMapping("/delete")
    public String deleteFinancement(@RequestParam String sourceFinancement) {
        Model model = JenaEngine.readModel(ONTOLOGY_FILE_PATH);

        if (model == null) {
            return "Erreur lors de la lecture du modèle de l'ontologie.";
        }

        String NS = model.getNsPrefixURI("");
        if (NS == null || NS.isEmpty()) {
            NS = DEFAULT_NS;
        }

        // Requête SPARQL pour supprimer un financement basé sur sourceFinancement
        String sparqlDelete = "PREFIX ns: <" + NS + ">\n" +
                "DELETE { ns:Financement_" + sourceFinancement.replaceAll(" ", "_") + " ?p ?o . }\n" +
                "WHERE { ns:Financement_" + sourceFinancement.replaceAll(" ", "_") + " ?p ?o . }";

        UpdateRequest updateRequest = UpdateFactory.create(sparqlDelete);
        UpdateAction.execute(updateRequest, model);

        // Sauvegarder le modèle mis à jour dans le fichier d'ontologie
        try (OutputStream out = new FileOutputStream(ONTOLOGY_FILE_PATH)) {
            model.write(out, "RDF/XML");
            return "Financement supprimé avec succès et sauvegardé dans test.owl";
        } catch (Exception e) {
            return "Erreur lors de l'enregistrement du modèle : " + e.getMessage();
        }
    }

    @GetMapping("/searchByMontant")
    public List<Financement> searchByMontant(@RequestParam double min, @RequestParam double max) { // Changement ici
        List<Financement> financements = new ArrayList<>();
        Model model = JenaEngine.readModel(ONTOLOGY_FILE_PATH);

        if (model == null) {
            System.out.println("Erreur lors de la lecture du modèle de l'ontologie.");
            return financements;
        }

        String NS = model.getNsPrefixURI("");
        if (NS == null || NS.isEmpty()) {
            NS = DEFAULT_NS;
        }

        String sparqlQuery = "PREFIX ns: <" + NS + ">\n" +
                "SELECT ?montant_total ?sourceFinancement WHERE { " +
                "?financement ns:montant_total ?montant_total . " +
                "?financement ns:sourceFinancement ?sourceFinancement . " +
                "FILTER(?montant_total >= " + min + " && ?montant_total <= " + max + ") }";

        try (QueryExecution qexec = QueryExecutionFactory.create(QueryFactory.create(sparqlQuery), model)) {
            ResultSet results = qexec.execSelect();

            while (results.hasNext()) {
                QuerySolution soln = results.nextSolution();
                Financement financement = new Financement();
                financement.setMontantTotal(soln.getLiteral("montant_total").getDouble()); // Changement ici
                financement.setSourceFinancement(soln.getLiteral("sourceFinancement").getString());
                financements.add(financement);
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de l'exécution de la requête SPARQL : " + e.getMessage());
        }

        return financements;
    }

    @PostMapping("/associate")
    public ResponseEntity<String> associateFinancementWithTechnologie(@RequestBody Financement financement) {
        Model model = JenaEngine.readModel(ONTOLOGY_FILE_PATH);
        if (model == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de la lecture du modèle de l'ontologie.");
        }

        String NS = model.getNsPrefixURI("");
        if (NS == null || NS.isEmpty()) {
            NS = DEFAULT_NS;
        }

        // Récupération des informations de l'objet Financement
        String sourceFinancement = financement.getSourceFinancement();
        TechnologieEnergetique technologie = financement.getTechnologie();

        // Vérification que la technologie n'est pas nulle
        if (technologie == null) {
            return ResponseEntity.badRequest()
                    .body("La technologie doit être spécifiée.");
        }

        String nomTechnologie = technologie.getNomTechnologie();
        String sourceTechnologie = nomTechnologie.replaceAll(" ", "_");
        String formattedSourceFinancement = sourceFinancement.replaceAll(" ", "_");

        // Requête SPARQL pour associer un financement à une technologie
        String sparqlInsert = "PREFIX ns: <" + NS + ">\n" +
                "INSERT DATA { " +
                "ns:Financement_" + formattedSourceFinancement + " ns:financementPourTechnologie ns:Technologie_" + sourceTechnologie + " . " +
                "}";

        UpdateRequest updateRequest = UpdateFactory.create(sparqlInsert);
        UpdateAction.execute(updateRequest, model);

        // Sauvegarder le modèle mis à jour dans le fichier d'ontologie
        try (OutputStream out = new FileOutputStream(ONTOLOGY_FILE_PATH)) {
            model.write(out, "RDF/XML");
            return ResponseEntity.ok("Association réussie entre le financement et la technologie.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de l'enregistrement du modèle : " + e.getMessage());
        }
    }

    @GetMapping("/financementsParTechnologie")
    public ResponseEntity<List<Financement>> getFinancementsByTechnologie(@RequestParam String sourceTechnologie) {
        List<Financement> financements = new ArrayList<>();
        Model model = JenaEngine.readModel(ONTOLOGY_FILE_PATH);

        if (model == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(financements); // Retourne une liste vide avec un statut 500
        }

        String NS = model.getNsPrefixURI("");
        if (NS == null || NS.isEmpty()) {
            NS = DEFAULT_NS;
        }

        // Requête SPARQL pour récupérer les financements associés à une technologie spécifique
        String sparqlQuery = "PREFIX ns: <" + NS + ">\n" +
                "SELECT ?financement ?montant_total ?sourceFinancement WHERE { " +
                "?financement ns:financementPourTechnologie ns:Technologie_" + sourceTechnologie.replaceAll(" ", "_") + " . " +
                "?financement ns:montant_total ?montant_total . " +
                "?financement ns:sourceFinancement ?sourceFinancement . " +
                "}";

        try (QueryExecution qexec = QueryExecutionFactory.create(QueryFactory.create(sparqlQuery), model)) {
            ResultSet results = qexec.execSelect();

            while (results.hasNext()) {
                QuerySolution soln = results.nextSolution();
                Financement financement = new Financement();
                financement.setMontantTotal(soln.getLiteral("montant_total").getDouble());
                financement.setSourceFinancement(soln.getLiteral("sourceFinancement").getString());
                financements.add(financement);
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de l'exécution de la requête SPARQL : " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(financements); // Retourne une liste vide avec un statut 500
        }

        return ResponseEntity.ok(financements); // Retourne la liste des financements avec un statut 200
    }

}
