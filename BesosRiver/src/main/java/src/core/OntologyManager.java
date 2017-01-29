/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package src.core;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntDocumentManager;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.util.iterator.ExtendedIterator;
import src.utils.ReflectionUtils;

/**
 *
 * @author pacific
 */
public class OntologyManager {
  
  
  //----------------------------------------------------------------------------
  // singleton pattern
  //----------------------------------------------------------------------------
  
  private static OntologyManager ins = null;
  private OntologyManager() {
    loadOntology();
  }
  public static OntologyManager getInstance() {
    if (ins == null) 
      synchronized(OntologyManager.class) {
        ins = new OntologyManager();
      }
    return ins;
  }
  
  
  
  //----------------------------------------------------------------------------
  // variables
  //----------------------------------------------------------------------------
  
  public final String ontologyPath = Constants.OWL_TMP_FILE_PATH;
  public final String ontologyIri = Constants.ONTOLOGY_IRI;
    
  private OntModel model;
  private OntDocumentManager ODM;
    
    

  //----------------------------------------------------------------------------
  // funciones acceso
  //----------------------------------------------------------------------------
  
  public OntModel getModel() {
    return model;
  }
  
  public String getNamingContext() {
    return ontologyIri;
  }
      
  
  
  //----------------------------------------------------------------------------
  // funciones fichero owl
  //----------------------------------------------------------------------------
  
  private void loadOntology() {
    model = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM_TRANS_INF);
    model.setStrictMode(false);
    ODM = model.getDocumentManager();
    ODM.addAltEntry(ontologyIri, "file:" + ontologyPath);
    model.read(ontologyIri);
  }
  
  public void reload() {
    loadOntology();
  }
  
  public void infer() {
    Hermit hermit = new Hermit();
    hermit.infer(Constants.OWL_TMP_FILE_PATH);
    reload();
  }
  
  public void saveOntology() {
    try {
      if (!model.isClosed()) {
        model.write(new FileOutputStream(ontologyPath));
        //model.close();
      }
    } catch (Exception ex) {
      ex.printStackTrace(System.out);
    }
  }
  
  
  
  //----------------------------------------------------------------------------
  // anadir instancias
  //----------------------------------------------------------------------------
  
  public Individual createIndividual(Object obj) {
    String className = obj.getClass().getSimpleName();
    OntClass klass = model.getOntClass(ontologyIri + className);
    Individual ind = klass.createIndividual(ontologyIri + className + UUID.randomUUID());
    ReflectionUtils.copyFields(obj, ind);
    return ind;
  }
  
  
  public void dropIndividual(Individual ind) {
     OntClass klass = ind.getOntClass(true);
     klass.dropIndividual(ind);
     ind.remove();
  }
  
    
  
  //----------------------------------------------------------------------------
  // funciones acceso
  //----------------------------------------------------------------------------
  
  // debug: imprime todas las clases y sus instancias
  //public List<Individual> getMasasDeAgua() {
  //  List<Individual> result = new ArrayList<>();
  //  OntClass masaDeAgua = model.getOntClass(ontologyIri + "MasaDeAgua");
  //  for (Iterator<Individual> i = model.listIndividuals(masaDeAgua); i.hasNext();) {
  //    Individual ind = i.next();
  //    result.add(ind);
  //    System.out.println("    · " + ind.toString());
  //  }
  //  return result;
  //}
  
  // para cada clase hay una lista de instancias
  public List<Individual> getIndividuals(String ontClass) {
    List<Individual> individuals = new ArrayList<>();
    OntClass klass = model.getOntClass(ontClass);
    ExtendedIterator<Individual> individualsIt = model.listIndividuals(klass);
    while (individualsIt.hasNext()) individuals.add(individualsIt.next());
    return individuals;
  }
    
    
  // hacer funcion listar masas de agua (divdidas por subtipos: blanda, negra, etc...)
  // hacer funcion listar industrias (divdidas por subtipos)

  // 

  public void generarAgua(String type) {  // generar agua("madereera");

    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }
  
  
}
