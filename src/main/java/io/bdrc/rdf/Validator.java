package io.bdrc.rdf;

import java.io.IOException;
import java.io.InputStream;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.topbraid.shacl.validation.ValidationUtil;
import org.topbraid.shacl.vocabulary.SH;

public class Validator {

    private Model dataModel;
    private Model shapeModel;

    public Validator(String datafile,String shapefile) throws IOException {
        InputStream dataStream=Validator.class.getClassLoader().getResourceAsStream(datafile);
        this.dataModel=ModelFactory.createDefaultModel();
        dataModel.read(dataStream, "", "TURTLE");
        dataStream.close();

        InputStream shapeStream=Validator.class.getClassLoader().getResourceAsStream(shapefile);
        this.shapeModel=ModelFactory.createDefaultModel();
        shapeModel.read(shapeStream, "", "TURTLE");
        shapeStream.close();
    }

    public Model getDataModel() {
        return dataModel;
    }

    public Model getShapeModel() {
        return shapeModel;
    }




    public static void main(String[] args) throws IOException {
       Validator val=new Validator("P1583.ttl","PersonShape.ttl");
       Resource reportResource = ValidationUtil.validateModel(val.getDataModel(), val.getShapeModel(), false);
       boolean conforms  = reportResource.getProperty(SH.conforms).getBoolean();
       System.out.println("Conforms = " + conforms);
       if (!conforms) {
           reportResource.getModel().write(System.out,  "TURTLE");
       }
    }

}
