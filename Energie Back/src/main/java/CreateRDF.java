import org.apache.jena.rdf.model.*;

public class CreateRDF {
    public void create() {
        String adressURI = "http://www.example.com/adressesprit";
        String arianaURI = "http://www.example.com/Ariana";
        String nameAdresse = "18 rue de l'usine";
        String cp = "2035";
        String ns = "http://www.example.com/espritont";

        Model model = ModelFactory.createDefaultModel();
        model.setNsPrefix("en", ns);

        Resource adresse = model.createResource(adressURI);
        Property name = model.createProperty(ns, "name");
        adresse.addProperty(name, nameAdresse);
        Property codeps = model.createProperty(ns, "Codepostale");
        adresse.addProperty(codeps, cp);

        Resource ville = model.createResource(arianaURI);
        Property commeville = model.createProperty(ns, "Commeville");
        adresse.addProperty(commeville, ville);

        model.write(System.out, "rdf/xml");


    }
}
