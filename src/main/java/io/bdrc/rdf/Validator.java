package io.bdrc.rdf;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.jena.ontology.DatatypeProperty;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.ontology.Restriction;
import org.apache.jena.rdf.model.InfModel;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.reasoner.Reasoner;
import org.apache.jena.reasoner.ReasonerRegistry;
import org.apache.jena.reasoner.ValidityReport;
import org.apache.jena.util.iterator.ExtendedIterator;
import org.apache.jena.vocabulary.OWL2;
import org.apache.jena.vocabulary.RDF;

public class Validator {

    final static Resource RDFPL = ResourceFactory.createResource("http://www.w3.org/1999/02/22-rdf-syntax-ns#PlainLiteral");
    private static Reasoner REASONER=ReasonerRegistry.getOWLReasoner();
    //private static Reasoner REASONER=ReasonerRegistry.getRDFSReasoner();
    private static OntModel ONTMOD;


    static {
        Model m =null;
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL("https://raw.githubusercontent.com/buda-base/owl-schema/master/bdrc.owl").openConnection();
            InputStream stream=connection.getInputStream();
            m = ModelFactory.createDefaultModel();
            m.read(stream, "", "RDF/XML");
            stream.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        ONTMOD = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, m);
        rdf10tordf11(ONTMOD);
        REASONER.bindSchema(ONTMOD);
    }

    public static ValidityReport validate(Model mod) {
        InfModel inf=ModelFactory.createInfModel(REASONER, mod);
        return inf.validate();
    }

    public static boolean isValid(Model m) {
        return validate(m).isValid();
    }

    public static void rdf10tordf11(OntModel o) {
        final ExtendedIterator<DatatypeProperty> it = o.listDatatypeProperties();
        while(it.hasNext()) {
            final DatatypeProperty p = it.next();
            if (p.hasRange(RDFPL)) {
                p.removeRange(RDFPL);
                p.addRange(RDF.langString);
            }
        }
        final ExtendedIterator<Restriction> it2 = o.listRestrictions();
        while(it2.hasNext()) {
            final Restriction r = it2.next();
            final Statement s = r.getProperty(OWL2.onDataRange); // is that code obvious? no
            if (s != null && s.getObject().asResource().equals(RDFPL)) {
                s.changeObject(RDF.langString);

            }
        }
    }

    public static void main(String[] args) throws IOException {
        InputStream stream=Validator.class.getClassLoader().getResourceAsStream("W12827_0001.ttl");
        Model m = ModelFactory.createDefaultModel();
        m.read(stream, "", "TURTLE");
        stream.close();
        System.out.println("Valid ="+Validator.isValid(m));
    }

}
