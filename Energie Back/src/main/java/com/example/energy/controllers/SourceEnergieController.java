package com.example.energy.controllers;

import com.example.energy.JenaEngine;
import com.example.energy.entities.Source_énergie;
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
@RequestMapping("/api/sourceEnergie")
public class SourceEnergieController {

    private static final String ONTOLOGY_FILE_PATH = "data/test.owl";
    private static final String DEFAULT_NS = "http://www.semanticweb.org/saidg/ontologies/2024/8/untitled-ontology-8#";

    @GetMapping("/all")
    public List<Source_énergie> getAllSourcesEnergie() {
        List<Source_énergie> sources = new ArrayList<>();
        Model model = JenaEngine.readModel(ONTOLOGY_FILE_PATH);

        if (model == null) {
            System.out.println("Erreur lors de la lecture du modèle de l'ontologie.");
            return sources;
        }

        String NS = model.getNsPrefixURI("");
        if (NS == null || NS.isEmpty()) {
            NS = DEFAULT_NS;
        }

        // Requête SPARQL pour récupérer toutes les sources d'énergie
        String sparqlQuery = "PREFIX ns: <" + NS + ">\n" +
                "SELECT ?conditionsUtilisation ?potentielEnergitique WHERE { " +
                "?source ns:conditionsUtilisation ?conditionsUtilisation . " +
                "?source ns:potentielEnergitique ?potentielEnergitique . }";

        Query query = QueryFactory.create(sparqlQuery);
        try (QueryExecution qexec = QueryExecutionFactory.create(query, model)) {
            ResultSet results = qexec.execSelect();

            while (results.hasNext()) {
                QuerySolution soln = results.nextSolution();
                Source_énergie source = new Source_énergie();

                // Récupérer les données et les affecter à l'objet source
                source.setConditionsUtilisation(soln.getLiteral("conditionsUtilisation").getString());
                source.setPotentielEnergitique(soln.getLiteral("potentielEnergitique").getInt());
                sources.add(source);
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de l'exécution de la requête SPARQL : " + e.getMessage());
        }

        return sources;
    }

    @PostMapping("/add")
    public String addSourceEnergie(@RequestBody Source_énergie source) {
        Model model = JenaEngine.readModel(ONTOLOGY_FILE_PATH);

        if (model == null) {
            return "Erreur lors de la lecture du modèle de l'ontologie.";
        }

        String NS = model.getNsPrefixURI("");
        if (NS == null || NS.isEmpty()) {
            NS = DEFAULT_NS;
        }

        // Requête SPARQL pour insérer une nouvelle source d'énergie
        String sparqlInsert = "PREFIX ns: <" + NS + ">\n" +
                "INSERT DATA { ns:SourceEnergie_" + source.getConditionsUtilisation().replaceAll(" ", "_") +
                " ns:conditionsUtilisation \"" + source.getConditionsUtilisation() + "\" ; " +
                "ns:potentielEnergitique " + source.getPotentielEnergitique() + " . }";

        UpdateRequest updateRequest = UpdateFactory.create(sparqlInsert);
        UpdateAction.execute(updateRequest, model);

        // Sauvegarder le modèle mis à jour dans le fichier d'ontologie
        try (OutputStream out = new FileOutputStream(ONTOLOGY_FILE_PATH)) {
            model.write(out, "RDF/XML");
            return "Source d'énergie ajoutée avec succès et sauvegardée dans test.owl";
        } catch (Exception e) {
            return "Erreur lors de l'enregistrement du modèle : " + e.getMessage();
        }
    }

    @PutMapping("/update")
    public String updateSourceEnergie(@RequestBody Source_énergie source) {
        Model model = JenaEngine.readModel(ONTOLOGY_FILE_PATH);

        if (model == null) {
            return "Erreur lors de la lecture du modèle de l'ontologie.";
        }

        String NS = model.getNsPrefixURI("");
        if (NS == null || NS.isEmpty()) {
            NS = DEFAULT_NS;
        }

        // Requête SPARQL pour mettre à jour une source d'énergie existante
        String sparqlUpdate = "PREFIX ns: <" + NS + ">\n" +
                "DELETE { ns:SourceEnergie_" + source.getConditionsUtilisation().replaceAll(" ", "_") +
                " ns:conditionsUtilisation ?oldConditionsUtilisation ; " +
                "ns:potentielEnergitique ?oldPotentiel . }\n" +
                "INSERT { ns:SourceEnergie_" + source.getConditionsUtilisation().replaceAll(" ", "_") +
                " ns:conditionsUtilisation \"" + source.getConditionsUtilisation() + "\" ; " +
                "ns:potentielEnergitique " + source.getPotentielEnergitique() + " . }\n" +
                "WHERE { ns:SourceEnergie_" + source.getConditionsUtilisation().replaceAll(" ", "_") +
                " ns:conditionsUtilisation ?oldConditionsUtilisation ; " +
                "ns:potentielEnergitique ?oldPotentiel . }";

        UpdateRequest updateRequest = UpdateFactory.create(sparqlUpdate);
        UpdateAction.execute(updateRequest, model);

        // Sauvegarder le modèle mis à jour dans le fichier d'ontologie
        try (OutputStream out = new FileOutputStream(ONTOLOGY_FILE_PATH)) {
            model.write(out, "RDF/XML");
            return "Source d'énergie mise à jour avec succès et sauvegardée dans test.owl";
        } catch (Exception e) {
            return "Erreur lors de l'enregistrement du modèle : " + e.getMessage();
        }
    }

    @DeleteMapping("/delete")
    public String deleteSourceEnergie(@RequestParam String conditionsUtilisation) {
        Model model = JenaEngine.readModel(ONTOLOGY_FILE_PATH);

        if (model == null) {
            return "Erreur lors de la lecture du modèle de l'ontologie.";
        }

        String NS = model.getNsPrefixURI("");
        if (NS == null || NS.isEmpty()) {
            NS = DEFAULT_NS;
        }

        // Requête SPARQL pour supprimer une source d'énergie basée sur conditionsUtilisation
        String sparqlDelete = "PREFIX ns: <" + NS + ">\n" +
                "DELETE { ns:SourceEnergie_" + conditionsUtilisation.replaceAll(" ", "_") + " ?p ?o . }\n" +
                "WHERE { ns:SourceEnergie_" + conditionsUtilisation.replaceAll(" ", "_") + " ?p ?o . }";

        UpdateRequest updateRequest = UpdateFactory.create(sparqlDelete);
        UpdateAction.execute(updateRequest, model);

        // Sauvegarder le modèle mis à jour dans le fichier d'ontologie
        try (OutputStream out = new FileOutputStream(ONTOLOGY_FILE_PATH)) {
            model.write(out, "RDF/XML");
            return "Source d'énergie supprimée avec succès et sauvegardée dans test.owl";
        } catch (Exception e) {
            return "Erreur lors de l'enregistrement du modèle : " + e.getMessage();
        }
    }

    @GetMapping("/searchByPotentiel")
    public List<Source_énergie> searchByPotentiel(@RequestParam int min, @RequestParam int max) {
        List<Source_énergie> sources = new ArrayList<>();
        Model model = JenaEngine.readModel(ONTOLOGY_FILE_PATH);

        if (model == null) {
            System.out.println("Erreur lors de la lecture du modèle de l'ontologie.");
            return sources;
        }

        String NS = model.getNsPrefixURI("");
        if (NS == null || NS.isEmpty()) {
            NS = DEFAULT_NS;
        }

        String sparqlQuery = "PREFIX ns: <" + NS + ">\n" +
                "SELECT ?conditionsUtilisation ?potentielEnergitique WHERE { " +
                "?source ns:conditionsUtilisation ?conditionsUtilisation . " +
                "?source ns:potentielEnergitique ?potentielEnergitique . " +
                "FILTER(?potentielEnergitique >= " + min + " && ?potentielEnergitique <= " + max + ") }";

        try (QueryExecution qexec = QueryExecutionFactory.create(QueryFactory.create(sparqlQuery), model)) {
            ResultSet results = qexec.execSelect();
            while (results.hasNext()) {
                QuerySolution soln = results.nextSolution();
                Source_énergie source = new Source_énergie();
                source.setConditionsUtilisation(soln.getLiteral("conditionsUtilisation").getString());
                source.setPotentielEnergitique(soln.getLiteral("potentielEnergitique").getInt());
                sources.add(source);
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de l'exécution de la requête SPARQL : " + e.getMessage());
        }

        return sources;
    }

}
