package com.example.energy.controllers;

import com.example.energy.JenaEngine;
import com.example.energy.entities.Projet;
import com.example.energy.entities.Source_énergie;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.update.UpdateAction;
import org.apache.jena.update.UpdateFactory;
import org.apache.jena.update.UpdateRequest;
import org.springframework.web.bind.annotation.*;
import org.apache.jena.rdf.model.ModelFactory;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/projetsource")
public class ProjetSourceController {

    private static final String ONTOLOGY_FILE_PATH = "data/test.owl";
    private static final String DEFAULT_NS = "http://www.semanticweb.org/saidg/ontologies/2024/8/untitled-ontology-8#";
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    // Méthode pour récupérer tous les projets avec leurs sources d'énergie
    @GetMapping("/all")
    public List<Projet> getAllProjets() {
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

        String sparqlQuery = "PREFIX ns: <" + NS + ">\n" +
                "SELECT ?nom ?capacite ?dateDebut ?dateFin ?conditionsUtilisation ?potentielEnergitique WHERE { " +
                "?projet ns:nom ?nom . " +
                "?projet ns:capacite ?capacite . " +
                "?projet ns:dateDebut ?dateDebut . " +
                "?projet ns:dateFin ?dateFin . " +
                "?projet ns:utiliseSourceEnergie ?source . " +
                "?source ns:conditionsUtilisation ?conditionsUtilisation . " +
                "?source ns:potentielEnergitique ?potentielEnergitique . }";

        Query query = QueryFactory.create(sparqlQuery);
        try (QueryExecution qexec = QueryExecutionFactory.create(query, model)) {
            ResultSet results = qexec.execSelect();

            Projet projet = null;
            while (results.hasNext()) {
                QuerySolution soln = results.nextSolution();

                // Vérifier si le projet existe déjà dans la liste
                String nomProjet = soln.getLiteral("nom").getString();
                projet = projets.stream()
                        .filter(p -> p.getNom().equals(nomProjet))
                        .findFirst()
                        .orElse(null);

                if (projet == null) {
                    projet = new Projet();
                    projet.setNom(nomProjet);
                    projet.setCapacite(soln.getLiteral("capacite").getInt());

                    // Conversion de la chaîne de date en LocalDateTime
                    String dateDebutStr = soln.getLiteral("dateDebut").getString();
                    String dateFinStr = soln.getLiteral("dateFin").getString();
                    projet.setDateDebut(LocalDateTime.parse(dateDebutStr, DATE_FORMAT));
                    projet.setDateFin(LocalDateTime.parse(dateFinStr, DATE_FORMAT));

                    projet.setSourcesEnergie(new ArrayList<>());
                    projets.add(projet);
                }

                // Ajouter la source d'énergie au projet
                Source_énergie source = new Source_énergie();
                source.setConditionsUtilisation(soln.getLiteral("conditionsUtilisation").getString());
                source.setPotentielEnergitique(soln.getLiteral("potentielEnergitique").getInt());
                projet.getSourcesEnergie().add(source);
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de l'exécution de la requête SPARQL : " + e.getMessage());
        }

        return projets;
    }

    // Méthode pour ajouter un projet avec des sources d'énergie
    @PostMapping("/add")
    public String addProjet(@RequestBody Projet projet) {
        Model model = JenaEngine.readModel(ONTOLOGY_FILE_PATH);

        if (model == null) {
            return "Erreur lors de la lecture du modèle de l'ontologie.";
        }

        String NS = model.getNsPrefixURI("");
        if (NS == null || NS.isEmpty()) {
            NS = DEFAULT_NS;
        }

        String projetId = projet.getNom().replaceAll(" ", "_");

        // Requête SPARQL pour insérer un projet et lier une source d'énergie
        StringBuilder sparqlInsert = new StringBuilder();
        sparqlInsert.append("PREFIX ns: <").append(NS).append(">\n");
        sparqlInsert.append("INSERT DATA { ");
        sparqlInsert.append("ns:Projet_").append(projetId).append(" ns:nom \"").append(projet.getNom()).append("\" ; ");
        sparqlInsert.append("ns:capacite ").append(projet.getCapacite()).append(" ; ");
        sparqlInsert.append("ns:dateDebut \"").append(projet.getDateDebut().format(DATE_FORMAT)).append("\" ; ");
        sparqlInsert.append("ns:dateFin \"").append(projet.getDateFin().format(DATE_FORMAT)).append("\" . ");

        for (Source_énergie source : projet.getSourcesEnergie()) {
            String sourceId = source.getConditionsUtilisation().replaceAll(" ", "_");

            // Lier la source d'énergie au projet
            sparqlInsert.append("ns:Projet_").append(projetId).append(" ns:utiliseSourceEnergie ns:SourceEnergie_").append(sourceId).append(" . ");
            sparqlInsert.append("ns:SourceEnergie_").append(sourceId).append(" ns:conditionsUtilisation \"")
                    .append(source.getConditionsUtilisation()).append("\" ; ");
            sparqlInsert.append("ns:potentielEnergitique ").append(source.getPotentielEnergitique()).append(" . ");
        }
        sparqlInsert.append("}");

        UpdateRequest updateRequest = UpdateFactory.create(sparqlInsert.toString());
        UpdateAction.execute(updateRequest, model);

        // Sauvegarder le modèle mis à jour dans le fichier d'ontologie
        try (OutputStream out = new FileOutputStream(ONTOLOGY_FILE_PATH)) {
            model.write(out, "RDF/XML");
            return "Projet et sources d'énergie ajoutés avec succès et sauvegardés dans test.owl";
        } catch (Exception e) {
            return "Erreur lors de l'enregistrement du modèle : " + e.getMessage();
        }


    }

    // Méthode pour modifier un projet et ses sources d'énergie
    @PutMapping("/edit/{nom}")
    public String editProjet(@PathVariable String nom, @RequestBody Projet projetModifie) {
        Model model = JenaEngine.readModel(ONTOLOGY_FILE_PATH);

        if (model == null) {
            return "Erreur lors de la lecture du modèle de l'ontologie.";
        }

        String NS = model.getNsPrefixURI("");
        if (NS == null || NS.isEmpty()) {
            NS = DEFAULT_NS;
        }

        String projetId = nom.replaceAll(" ", "_");
        String sparqlUpdate = "PREFIX ns: <" + NS + ">\n" +
                "DELETE { " +
                "ns:Projet_" + projetId + " ns:nom ?nom ; " +
                "ns:capacite ?capacite ; " +
                "ns:dateDebut ?dateDebut ; " +
                "ns:dateFin ?dateFin . " +
                "} " +
                "INSERT { " +
                "ns:Projet_" + projetId + " ns:nom \"" + projetModifie.getNom() + "\" ; " +
                "ns:capacite " + projetModifie.getCapacite() + " ; " +
                "ns:dateDebut \"" + projetModifie.getDateDebut().format(DATE_FORMAT) + "\" ; " +
                "ns:dateFin \"" + projetModifie.getDateFin().format(DATE_FORMAT) + "\" . " +
                "} " +
                "WHERE { " +
                "ns:Projet_" + projetId + " ns:nom ?nom ; " +
                "ns:capacite ?capacite ; " +
                "ns:dateDebut ?dateDebut ; " +
                "ns:dateFin ?dateFin . " +
                "}";

        UpdateRequest updateRequest = UpdateFactory.create(sparqlUpdate);
        UpdateAction.execute(updateRequest, model);

        // Sauvegarder le modèle mis à jour
        try (OutputStream out = new FileOutputStream(ONTOLOGY_FILE_PATH)) {
            model.write(out, "RDF/XML");
            return "Projet modifié avec succès et sauvegardé dans test.owl";
        } catch (Exception e) {
            return "Erreur lors de l'enregistrement du modèle : " + e.getMessage();
        }
    }

    // Méthode pour supprimer un projet et ses sources d'énergie
    @DeleteMapping("/delete/{nom}")
    public String deleteProjet(@PathVariable String nom) {
        Model model = JenaEngine.readModel(ONTOLOGY_FILE_PATH);

        if (model == null) {
            return "Erreur lors de la lecture du modèle de l'ontologie.";
        }

        String NS = model.getNsPrefixURI("");
        if (NS == null || NS.isEmpty()) {
            NS = DEFAULT_NS;
        }

        String projetId = nom.replaceAll(" ", "_");
        String sparqlDelete = "PREFIX ns: <" + NS + ">\n" +
                "DELETE { " +
                "ns:Projet_" + projetId + " ?p ?o . " +
                "} " +
                "WHERE { " +
                "ns:Projet_" + projetId + " ?p ?o . " +
                "}";

        UpdateRequest updateRequest = UpdateFactory.create(sparqlDelete);
        UpdateAction.execute(updateRequest, model);

        // Sauvegarder le modèle mis à jour
        try (OutputStream out = new FileOutputStream(ONTOLOGY_FILE_PATH)) {
            model.write(out, "RDF/XML");
            return "Projet supprimé avec succès et sauvegardé dans test.owl";
        } catch (Exception e) {
            return "Erreur lors de l'enregistrement du modèle : " + e.getMessage();
        }
    }

    // Exemple de requête ASK
    @GetMapping("/exists/{nom}")
    public boolean projetExists(@PathVariable String nom) {
        Model model = JenaEngine.readModel(ONTOLOGY_FILE_PATH);

        if (model == null) {
            return false;
        }

        String NS = model.getNsPrefixURI("");
        if (NS == null || NS.isEmpty()) {
            NS = DEFAULT_NS;
        }

        String sparqlAsk = "PREFIX ns: <" + NS + ">\n" +
                "ASK WHERE { ns:Projet_" + nom.replaceAll(" ", "_") + " ?p ?o . }";

        Query query = QueryFactory.create(sparqlAsk);
        try (QueryExecution qexec = QueryExecutionFactory.create(query, model)) {
            return qexec.execAsk();
        } catch (Exception e) {
            System.err.println("Erreur lors de l'exécution de la requête ASK : " + e.getMessage());
            return false;
        }
    }
    @GetMapping("/withSolarConditions")
    public List<String> getProjectsWithSolarConditions() {
        List<String> projects = new ArrayList<>();
        String NS = "";

        // Lecture du modèle RDF
        Model model = JenaEngine.readModel(ONTOLOGY_FILE_PATH);

        if (model != null) {
            NS = model.getNsPrefixURI("");

            // Requête SPARQL pour les projets utilisant des conditions d'utilisation "solaire"
            String sparqlSelect = "PREFIX ns: <" + NS + ">\n" +
                    "SELECT ?project WHERE {\n" +
                    "    ?project a ns:Projet .\n" +
                    "    ?project ns:utiliseSourceEnergie ?source .\n" +
                    "    ?source ns:conditions_utilisation ?conditions .\n" +
                    "    FILTER(CONTAINS(?conditions, \"solaire\"))\n" +
                    "}";

            try (QueryExecution qexec = QueryExecutionFactory.create(sparqlSelect, model)) {
                ResultSet results = qexec.execSelect();

                // Boucle pour ajouter chaque projet à la liste de résultats
                while (results.hasNext()) {
                    QuerySolution solution = results.next();
                    projects.add(solution.getResource("project").getURI());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.err.println("Erreur lors de la lecture du modèle de l'ontologie.");
        }

        return projects;
    }

    @GetMapping("/withHighPotential")
    public List<String> getProjectsWithHighPotential() {
        List<String> projects = new ArrayList<>();
        String NS = "";

        // Lecture du modèle RDF
        Model model = JenaEngine.readModel(ONTOLOGY_FILE_PATH);

        if (model != null) {
            NS = model.getNsPrefixURI("");

            // Requête SPARQL pour les projets avec un potentiel énergétique élevé
            String sparqlSelect = "PREFIX ns: <" + NS + ">\n" +
                    "SELECT ?project ?name WHERE {\n" +
                    "    ?project a ns:Projet .\n" +
                    "    ?project ns:utiliseSourceEnergie ?source .\n" +
                    "    ?source ns:potentiel_nergétique ?potential .\n" +
                    "    ?project ns:nom ?name .\n" +
                    "    FILTER (?potential > 1000)\n" +
                    "}";

            try (QueryExecution qexec = QueryExecutionFactory.create(sparqlSelect, model)) {
                ResultSet results = qexec.execSelect();

                // Boucle pour ajouter chaque projet à la liste de résultats
                while (results.hasNext()) {
                    QuerySolution solution = results.next();
                    projects.add(solution.getResource("project").getURI());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.err.println("Erreur lors de la lecture du modèle de l'ontologie.");
        }

        return projects;
    }


    @GetMapping("/construct")
    public Model constructExample() {
        Model model = JenaEngine.readModel(ONTOLOGY_FILE_PATH);
        Model resultModel = ModelFactory.createDefaultModel();

        String NS = model.getNsPrefixURI("");
        if (NS == null || NS.isEmpty()) {
            NS = DEFAULT_NS;
        }

        String sparqlConstruct = "PREFIX ns: <" + NS + ">\n" +
                "CONSTRUCT { ?projet ns:nom ?nom . } " +
                "WHERE { ?projet ns:nom ?nom . }";

        Query query = QueryFactory.create(sparqlConstruct);
        try (QueryExecution qexec = QueryExecutionFactory.create(query, model)) {
            resultModel = qexec.execConstruct();
        } catch (Exception e) {
            System.err.println("Erreur lors de l'exécution de la requête CONSTRUCT : " + e.getMessage());
        }

        return resultModel;
    }
    @GetMapping("/countHydraulicProjects")
    public String countHydraulicProjects() {
        String NS = "";

        Model model = JenaEngine.readModel(ONTOLOGY_FILE_PATH);

        if (model != null) {
            NS = model.getNsPrefixURI("");

            // SPARQL query to count the number of hydraulic energy projects
            String sparqlCount = "PREFIX ns: <" + NS + ">\n" +
                    "SELECT (COUNT(?project) AS ?numProjects) WHERE {\n" +
                    "    ?project a ns:Projet .\n" +
                    "    ?project ns:utiliseSourceEnergie ns:energie_hydrolique .\n" +
                    "}";

            try (QueryExecution qexec = QueryExecutionFactory.create(sparqlCount, model)) {
                ResultSet results = qexec.execSelect();
                if (results.hasNext()) {
                    QuerySolution solution = results.next();
                    return "Number of projects using hydraulic energy: " + solution.getLiteral("numProjects").getInt();
                }
            } catch (Exception e) {
                e.printStackTrace();
                return "Error occurred while counting projects.";
            }
        }

        return "Model is null or not loaded.";
    }


}
