/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package src.agent;

import java.util.List;
import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntProperty;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.util.iterator.ExtendedIterator;
import src.core.Constants;
import src.data.MasaDeAgua;
import src.utils.ReflectionUtils;

/**
 *
 * @author llop
 */
public class RioAgent extends BaseAgent {
  
  
  public RioAgent(Individual individual) {
    super(individual);
  }
  
  
  
  @Override public void action() {
    // pillar el nombre de la funcion que avanza el curso del rio
    List<String> funcNames = getFuncNames(Constants.AVANZA_CURSO_RIO);
    if (!funcNames.isEmpty()) ReflectionUtils.callFunction(this, funcNames.get(0));
  }
  
  
  public void avanzaCursoAgua() {
    System.out.println();
  
    OntModel model = ontoManager.getModel();
    String namingContext = ontoManager.getNamingContext();
    
    // agua por defecto del rio
    String defaultAguaIndividualName = namingContext + Constants.MASA_DE_AGUA_DULCE;
    Individual aguaTramo = model.getIndividual(defaultAguaIndividualName);
    MasaDeAgua aguaFuente = (MasaDeAgua)ReflectionUtils.castIndividual(aguaTramo, MasaDeAgua.class);
    aguaTramo = ontoManager.createIndividual(aguaFuente);
    
    // sacar listado de tramos
    OntProperty fuenteProperty = model.getOntProperty(namingContext + Constants.FUENTE);
    Resource tramoResource = individual.getPropertyResourceValue(fuenteProperty);
    OntProperty siguienteTramoProperty = model.getOntProperty(namingContext + Constants.SIGUIENTE_TRAMO);
    OntProperty aguaTramoProperty = model.getOntProperty(namingContext + Constants.AGUA_TRAMO);
    while (tramoResource != null) {
      Individual tramo = tramoResource.as(Individual.class);
      
      RDFNode aguaNode = tramo.getPropertyValue(aguaTramoProperty);
      Individual aguaTramoTmp = aguaNode.as(Individual.class);
      
      //System.out.println(aguaTramoTmp.getLocalName());
      
      model.remove(tramoResource, aguaTramoProperty, aguaNode);
      model.add(tramoResource, aguaTramoProperty, aguaTramo);
      
      tramoResource = tramo.getPropertyResourceValue(siguienteTramoProperty);
      aguaTramo = aguaTramoTmp;
    }
    
    // imprimir mensaje
    MasaDeAgua aguaFinal = (MasaDeAgua)ReflectionUtils.castIndividual(aguaTramo, MasaDeAgua.class);
    // sacar sus clases
    String classesStr = "";
    ExtendedIterator<OntClass> classesIt = aguaTramo.listOntClasses(true);
    boolean first = true;
    while (classesIt.hasNext()) {
      OntClass klass = classesIt.next();
      String klassLocalName = klass.getLocalName();
      if (!klassLocalName.equals(Constants.NAMED_INDIVIDUAL)) {
        if (first) first = false;
        else classesStr += ", ";
        classesStr += klassLocalName;
      }
    }
    System.out.println("Han llegado al mar " + aguaFinal.Volumen + " litros de agua");
    System.out.println("   Clasificación: " + classesStr);
    aguaFinal.showData();
    
    ontoManager.dropIndividual(aguaTramo);
    
  }
  
  
}
