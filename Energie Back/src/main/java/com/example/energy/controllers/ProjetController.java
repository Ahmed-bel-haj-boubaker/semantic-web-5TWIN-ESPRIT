package com.example.energy.controllers;

import com.example.energy.entities.Projet;
import com.example.energy.JenaEngine;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.update.UpdateAction;
import org.apache.jena.update.UpdateFactory;
import org.apache.jena.update.UpdateRequest;
import org.springframework.web.bind.annotation.*;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/projet")
public class ProjetController {

    private static final String ONTOLOGY_FILE_PATH = "data/test.owl";
    private static final String DEFAULT_NS = "http://www.semanticweb.org/saidg/ontologies/2024/8/untitled-ontology-8#";

    // DateTimeFormatter for parsing and formatting LocalDateTime
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @GetMapping("/all")
    public List<Projet> getAllProjects() {
        List<Projet> projets = new ArrayList<>();
        Model model = JenaEngine.readModel(ONTOLOGY_FILE_PATH);

        if (model == null) {
            System.out.println("Erreur lors de la lecture du modèle de l'ontologie.");
            return projets;
        }

        String NS = model.getNsPrefixURI("");
        if (NS == null || NS.isEmpty()) {
            NS = DEFAULT_NS;
        }

        // Requête SPARQL pour récupérer tous les projets avec leurs attributs
        String sparqlQuery = "PREFIX ns: <" + NS + ">\n" +
                "SELECT ?nom ?capacite ?dateDebut ?dateFin WHERE { " +
                "  ?project ns:capacite ?capacite ; " +
                "          ns:dateDebut ?dateDebut ; " +
                "          ns:DateFin ?dateFin ; " +
                "          ns:nom ?nom . " +
                "}";

        Query query = QueryFactory.create(sparqlQuery);
        try (QueryExecution qexec = QueryExecutionFactory.create(query, model)) {
            ResultSet results = qexec.execSelect();

            while (results.hasNext()) {
                QuerySolution soln = results.nextSolution();
                Projet projet = new Projet();

                // Récupérer les données et les affecter au projet
                projet.setNom(soln.getLiteral("nom").getString());
                projet.setCapacite(soln.getLiteral("capacite").getInt());

                // Convertir les dates en LocalDateTime
                projet.setDateDebut(LocalDateTime.parse(soln.getLiteral("dateDebut").getString(), DATE_TIME_FORMATTER));
                projet.setDateFin(LocalDateTime.parse(soln.getLiteral("dateFin").getString(), DATE_TIME_FORMATTER));

                projets.add(projet);
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de l'exécution de la requête SPARQL : " + e.getMessage());
        }

        return projets;
    }

    @PutMapping("/update")
    public String updateProjet(@RequestBody Projet projet) {
        Model model = JenaEngine.readModel(ONTOLOGY_FILE_PATH);

        if (model == null) {
            return "Erreur lors de la lecture du modèle de l'ontologie.";
        }

        // Obtenir le namespace (NS) de l'ontologie
        String NS = model.getNsPrefixURI("");
        if (NS == null || NS.isEmpty()) {
            NS = DEFAULT_NS;
        }

        // Créer la requête SPARQL pour supprimer les valeurs existantes et insérer les nouvelles
        String sparqlDeleteInsert = "PREFIX ns: <" + NS + ">\n" +
                "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n" +
                "DELETE { " +
                "    ns:Project_" + projet.getNom().replaceAll(" ", "_") + " ns:capacite ?oldCapacite ; " +
                "                                              ns:dateDebut ?oldStartDate ; " +
                "                                              ns:DateFin ?oldEndDate ." +
                "}\n" +
                "INSERT { " +
                "    ns:Project_" + projet.getNom().replaceAll(" ", "_") + " ns:capacite \"" + projet.getCapacite() + "\"^^xsd:int ; " +
                "                                              ns:dateDebut \"" + projet.getDateDebut().format(DATE_TIME_FORMATTER) + "\"^^xsd:dateTime ; " +
                "                                              ns:DateFin \"" + projet.getDateFin().format(DATE_TIME_FORMATTER) + "\"^^xsd:dateTime ." +
                "}\n" +
                "WHERE { " +
                "    ns:Project_" + projet.getNom().replaceAll(" ", "_") + " ns:capacite ?oldCapacite ; " +
                "                                              ns:dateDebut ?oldStartDate ; " +
                "                                              ns:DateFin ?oldEndDate ." +
                "}";

        // Exécuter la requête SPARQL
        UpdateRequest updateRequest = UpdateFactory.create(sparqlDeleteInsert);
        UpdateAction.execute(updateRequest, model);

        // Sauvegarder le modèle mis à jour dans le fichier d'ontologie
        try (OutputStream out = new FileOutputStream(ONTOLOGY_FILE_PATH)) {
            model.write(out, "RDF/XML");
            return "Projet mis à jour avec succès et sauvegardé dans test.owl";
        } catch (Exception e) {
            return "Erreur lors de l'enregistrement du modèle : " + e.getMessage();
        }
    }

    @PostMapping("/add")
    public String addProjet(@RequestBody Projet projet) {
        Model model = JenaEngine.readModel(ONTOLOGY_FILE_PATH);

        if (model == null) {
            return "Erreur lors de la lecture du modèle de l'ontologie.";
        }

        // Obtenir le namespace (NS) de l'ontologie
        String NS = model.getNsPrefixURI("");
        if (NS == null || NS.isEmpty()) {
            NS = DEFAULT_NS;
        }

        // Requête SPARQL pour insérer un nouveau projet avec les valeurs fournies
        String sparqlInsert = "PREFIX ns: <" + NS + ">\n" +
                "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n" +
                "INSERT DATA { " +
                "    ns:Project_" + projet.getNom().replaceAll(" ", "_") + " ns:nom \"" + projet.getNom() + "\" ; " +
                "                                              ns:capacite \"" + projet.getCapacite() + "\"^^xsd:int ; " +
                "                                              ns:dateDebut \"" + projet.getDateDebut().format(DATE_TIME_FORMATTER) + "\"^^xsd:dateTime ; " +
                "                                              ns:DateFin \"" + projet.getDateFin().format(DATE_TIME_FORMATTER) + "\"^^xsd:dateTime ." +
                "}";

        // Exécuter la requête d'insertion SPARQL
        UpdateRequest updateRequest = UpdateFactory.create(sparqlInsert);
        UpdateAction.execute(updateRequest, model);

        // Sauvegarder le modèle mis à jour dans le fichier d'ontologie
        try (OutputStream out = new FileOutputStream(ONTOLOGY_FILE_PATH)) {
            model.write(out, "RDF/XML");
            return "Projet ajouté avec succès et sauvegardé dans test.owl";
        } catch (Exception e) {
            return "Erreur lors de l'enregistrement du modèle : " + e.getMessage();
        }
    }

    @DeleteMapping("/delete")
    public String deleteProjet(@RequestParam String nom) {
        Model model = JenaEngine.readModel(ONTOLOGY_FILE_PATH);

        if (model == null) {
            return "Erreur lors de la lecture du modèle de l'ontologie.";
        }

        // Obtenir le namespace (NS) de l'ontologie
        String NS = model.getNsPrefixURI("");
        if (NS == null || NS.isEmpty()) {
            NS = DEFAULT_NS;
        }

        // Requête SPARQL pour supprimer un projet basé sur le nom
        String sparqlDelete = "PREFIX ns: <" + NS + ">\n" +
                "DELETE { " +
                "    ns:Project_" + nom.replaceAll(" ", "_") + " ?p ?o ." +
                "}\n" +
                "WHERE { " +
                "    ns:Project_" + nom.replaceAll(" ", "_") + " ?p ?o ." +
                "}";

        // Exécuter la requête de suppression SPARQL
        UpdateRequest updateRequest = UpdateFactory.create(sparqlDelete);
        UpdateAction.execute(updateRequest, model);

        // Sauvegarder le modèle mis à jour dans le fichier d'ontologie
        try (OutputStream out = new FileOutputStream(ONTOLOGY_FILE_PATH)) {
            model.write(out, "RDF/XML");
            return "Projet supprimé avec succès et sauvegardé dans test.owl";
        } catch (Exception e) {
            return "Erreur lors de l'enregistrement du modèle : " + e.getMessage();
        }
    }

    /**
     * Vérifier si un projet existe dans l'ontologie via SPARQL.
     * @param nom le nom du projet à vérifier
     * @return true si le projet existe, sinon false
     */
    @GetMapping("/exists")
    public boolean existsProjet(@RequestParam String nom) {
        Model model = JenaEngine.readModel(ONTOLOGY_FILE_PATH);

        if (model == null) {
            return false; // ou lancer une exception selon le comportement désiré
        }

        // Obtenir le namespace (NS) de l'ontologie
        String NS = model.getNsPrefixURI("");
        if (NS == null || NS.isEmpty()) {
            NS = DEFAULT_NS;
        }

        // Requête SPARQL pour vérifier l'existence du projet
        String sparqlAsk = "PREFIX ns: <" + NS + ">\n" +
                "ASK { " +
                "    ns:Project_" + nom.replaceAll(" ", "_") + " ?p ?o ." +
                "}";

        Query query = QueryFactory.create(sparqlAsk);
        try (QueryExecution qexec = QueryExecutionFactory.create(query, model)) {
            return qexec.execAsk();
        } catch (Exception e) {
            System.err.println("Erreur lors de l'exécution de la requête ASK : " + e.getMessage());
            return false;
        }
    }


    /**
     * Vérifie l'existence de projets utilisant une source d'énergie spécifique et récupère leurs noms.
     *
     * @param energySource La source d'énergie à vérifier (par exemple, "energie_solaire").
     * @return Un message contenant les noms des projets ou un message indiquant qu'aucun projet n'existe.
     */
    @GetMapping("/checkEnergySource")
    public String checkEnergySource(@RequestParam String energySource) {
        String NS = "";

        // Charger le modèle depuis le fichier ontologique
        Model model = JenaEngine.readModel(ONTOLOGY_FILE_PATH);

        if (model != null) {
            // Lire le namespace de l'ontologie
            NS = model.getNsPrefixURI(""); // Assurez-vous que l'URI de l'ontologie est bien défini

            // Appliquer les règles sur le modèle inféré
            Model inferredModel = JenaEngine.readInferencedModelFromRuleFile(model, "data/rules.txt");

            // Créer une requête SPARQL ASK pour vérifier l'existence de projets utilisant la source d'énergie spécifiée
            String sparqlAsk = "PREFIX ns: <" + NS + ">\n" +
                    "ASK WHERE {\n" +
                    "    ?project a ns:Projet .\n" +
                    "    ?project ns:utiliseSourceEnergie ns:" + energySource + " .\n" +
                    "}";

            // Exécuter la requête SPARQL ASK sur le modèle inféré
            try (QueryExecution qexec = QueryExecutionFactory.create(sparqlAsk, inferredModel)) {
                boolean exists = qexec.execAsk();

                if (exists) {
                    StringBuilder projectNames = new StringBuilder("Des projets utilisant " + energySource + " existent dans le modèle :\n");

                    // Créer une requête SPARQL SELECT pour obtenir les noms des projets
                    String sparqlSelect = "PREFIX ns: <" + NS + ">\n" +
                            "SELECT ?projectName WHERE {\n" +
                            "    ?project a ns:Projet .\n" +
                            "    ?project ns:utiliseSourceEnergie ns:" + energySource + " .\n" +
                            "    ?project ns:nom ?projectName .\n" +
                            "}";

                    // Exécuter la requête SPARQL SELECT
                    try (QueryExecution selectExec = QueryExecutionFactory.create(sparqlSelect, inferredModel)) {
                        ResultSet results = selectExec.execSelect();

                        // Itérer sur les résultats et ajouter les noms des projets à la réponse
                        while (results.hasNext()) {
                            QuerySolution sol = results.nextSolution();
                            String projectName = sol.get("projectName").toString();
                            projectNames.append("Nom du projet : ").append(projectName).append("\n");
                        }
                    } catch (Exception e) {
                        return "Erreur lors de l'exécution de la requête SELECT : " + e.getMessage();
                    }
                    return projectNames.toString();
                } else {
                    return "Aucun projet utilisant " + energySource + " n'existe dans le modèle.";
                }
            } catch (Exception e) {
                return "Erreur lors de l'exécution de la requête ASK : " + e.getMessage();
            }
        } else {
            return "Erreur lors de la lecture du modèle depuis l'ontologie.";
        }
    }




}
