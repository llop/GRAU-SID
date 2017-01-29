/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package src.agent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntProperty;
import org.apache.jena.rdf.model.NodeIterator;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import src.core.Constants;
import src.data.MasaDeAgua;
import src.data.ProcesadoraDeAgua;
import src.utils.ReflectionUtils;

/**
 *
 * @author llop
 */
public class CiudadAgent extends BaseAgent {
  
  
  public CiudadAgent(Individual individual) {
    super(individual);
  }
  
  
  @Override public void action() {
    // pillar el nombre de la funcion que vacía los tanques
    List<String> funcNames = getFuncNames(Constants.AGUA_SUCIA_CIUDAD);
    if (!funcNames.isEmpty()) {
      int randomIndex = new Random().nextInt(funcNames.size());
      ReflectionUtils.callFunction(this, funcNames.get(randomIndex));
    }
  }
  
  private void aguaSuciaCiudad(MasaDeAgua aguaSucia) {
    // acceder al tramo
    OntModel model = ontoManager.getModel();
    String namingContext = ontoManager.getNamingContext();
    OntProperty tramoProperty = model.getOntProperty(namingContext + Constants.TRAMO_ORIGEN);
    Resource tramoResource = individual.getPropertyResourceValue(tramoProperty);
    Individual tramo = tramoResource.as(Individual.class);
    
    // acceder al agua
    OntProperty aguaTramoProperty = model.getOntProperty(namingContext + Constants.AGUA_TRAMO);
    RDFNode aguaTramoNode = tramo.getPropertyValue(aguaTramoProperty);
    Individual aguaTramo = aguaTramoNode.as(Individual.class);
    MasaDeAgua agua = (MasaDeAgua)ReflectionUtils.castIndividual(aguaTramo, MasaDeAgua.class);
    
    // hemos vaciado el rio?
    if (agua.Volumen < aguaSucia.Volumen) {
      System.out.println("Rio vacío: No se puede eliminar la suciedad y la industria estalla!");
      return;
    }
    
    // quitar el volumen de agua del tramo 
    model.remove(tramoResource, aguaTramoProperty, aguaTramoNode);
    ontoManager.dropIndividual(aguaTramo);
    
    // crear el agua nueva
    agua.Volumen -= aguaSucia.Volumen;
    Individual aguaNuevaTramo = ontoManager.createIndividual(agua);
    model.add(tramoResource, aguaTramoProperty, aguaNuevaTramo);
    
    // pillar la funcion de calculo de coste
    List<String> funcCosteNames = getFuncNames(Constants.COSTE_LIMPIAR_AGUA);
    String funcCosteName = funcCosteNames.get(0);
    
    // ensuciar el agua pillada del rio
    List<String> funcEnsuciaNames = getFuncNames(Constants.ENSUCIA_AGUAS);
    String funcEnsuciaName = funcEnsuciaNames.get(0);
    ReflectionUtils.callFunction(new MasaDeAguaAgent(), funcEnsuciaName, agua, aguaSucia);
    
    // imprimir
    aguaSucia.showData();
    System.out.println("   Volumen: " + aguaSucia.Volumen + " litros");
    
    // acceder a las depuradoras asignadas
    List<Pair<Float, Individual>> costesPorDepuradora = new ArrayList<>();
    OntProperty depuradoraAsignadaProperty = model.getOntProperty(namingContext + Constants.DEPURADORA_ASIGNADA);
    NodeIterator depuradorasIt = individual.listPropertyValues(depuradoraAsignadaProperty);
    while (depuradorasIt.hasNext()) {
      RDFNode depuradoraNode = depuradorasIt.next();
      Individual depuradora = depuradoraNode.as(Individual.class);
      
      // calcular el coste
      DepuradoraAgent depuradoraAgent = new DepuradoraAgent(depuradora);
      float costeDepurar = (float)ReflectionUtils.callFunction(depuradoraAgent, funcCosteName, aguaSucia);
      if (costeDepurar >= 0f) costesPorDepuradora.add(Pair.of(costeDepurar, depuradora));
    }
    
    // si no hay opciones, avisar al usuario
    if (costesPorDepuradora.isEmpty()) {
      System.out.println("Ninguna depuradora acepta el agua sucia: No se puede eliminar y se devuelve al río!");
      
      // devolver el agua sucia al rio
      tramoResource = individual.getPropertyResourceValue(tramoProperty);
      tramo = tramoResource.as(Individual.class);
      new MasaDeAguaAgent(tramo, aguaSucia).action();
      return;
    }
    
    // ordenar por coste, y quedarsela mas barata
    Collections.sort(costesPorDepuradora, Comparator.comparing(coste -> coste.getLeft()));
    Individual depuradoraSeleccionada = costesPorDepuradora.get(0).getRight();
    
    // pillar la funcion de llenado de tanques
    List<String> funcLlenarNames = getFuncNames(Constants.LLENAR_TANQUES);
    String funcLlenarName = funcLlenarNames.get(0);
    
    // llenar los tanques
    ReflectionUtils.callFunction(new DepuradoraAgent(depuradoraSeleccionada), funcLlenarName, aguaSucia);
  }
  
  
  public void aguaSuciaCiudadNormal() {
    System.out.println();
    
    // agua con kk que genera la ciudad
    ProcesadoraDeAgua procesadora = (ProcesadoraDeAgua)ReflectionUtils.castIndividual(individual, ProcesadoraDeAgua.class);
    MasaDeAgua aguaSucia = procesadora.toMasaDeAgua();
    System.out.println("La ciudad "+individual.getLocalName()+" ha generado suciedad");
    aguaSuciaCiudad(aguaSucia);
    
  }
  
  public void aguaSuciaCiudadMucho() {
    System.out.println();
    
    // agua con kk que genera la ciudad
    ProcesadoraDeAgua procesadora = (ProcesadoraDeAgua)ReflectionUtils.castIndividual(individual, ProcesadoraDeAgua.class);
    MasaDeAgua aguaSucia = procesadora.toMasaDeAgua();
    aguaSucia.Volumen *= 2f;
    System.out.println("La ciudad "+individual.getLocalName()+" ha generado mucha suciedad");
    aguaSuciaCiudad(aguaSucia);
    
  }
  
}
