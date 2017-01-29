/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package src.agent;

import java.util.List;
import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntProperty;
import org.apache.jena.rdf.model.RDFNode;
import src.core.Constants;
import src.data.MasaDeAgua;
import src.data.ProcesadoraDeAgua;
import src.utils.ReflectionUtils;

/**
 *
 * @author llop
 */
public class MasaDeAguaAgent extends BaseAgent {
  
  
  private MasaDeAgua aguaSucia;
  
  
  public MasaDeAguaAgent() {
    super(null);
  }
  
  public MasaDeAguaAgent(Individual individual, MasaDeAgua aguaSucia) {
    super(individual);
    this.aguaSucia = aguaSucia;
  }
  
  @Override public void action() {
    if (individual != null) {
      List<String> funcNames = getFuncNames(Constants.MEZCLA_AGUAS);
        if (!funcNames.isEmpty()) {
            ReflectionUtils.callFunction(this, funcNames.get(0));
        }
    }
  }

  public void ensuciarAgua(MasaDeAgua muestraProcesadora, MasaDeAgua aguaSucia) {
    aguaSucia.DBO = aguaSucia.DBO + muestraProcesadora.DBO;
    aguaSucia.DQO = aguaSucia.DQO + muestraProcesadora.DQO;
    aguaSucia.Ph = (aguaSucia.Ph + muestraProcesadora.Ph) / 2f;
    aguaSucia.Fosforo = aguaSucia.Fosforo + muestraProcesadora.Fosforo;
    aguaSucia.SolidosEnSuspension = aguaSucia.SolidosEnSuspension + muestraProcesadora.SolidosEnSuspension;
    aguaSucia.ContaminantesEmergentes = aguaSucia.ContaminantesEmergentes + muestraProcesadora.ContaminantesEmergentes;
    aguaSucia.Sal = aguaSucia.Sal + muestraProcesadora.Sal;
    aguaSucia.Carbonatos = aguaSucia.Carbonatos + muestraProcesadora.Carbonatos;
    aguaSucia.ResiduosHumanos = aguaSucia.ResiduosHumanos || muestraProcesadora.ResiduosHumanos;
  }

  public void limpiarAgua(ProcesadoraDeAgua procesadora, MasaDeAgua aguaSucia) {
    aguaSucia.DBO = Math.max(aguaSucia.DBO - procesadora.DBOProcesadora, 0f);
    aguaSucia.DQO = Math.max(aguaSucia.DQO - procesadora.DQOProcesadora, 0f);
    aguaSucia.Ph = procesadora.PhProcesadora;
    aguaSucia.Fosforo = Math.max(aguaSucia.Fosforo - procesadora.FosforoProcesadora, 0f);
    aguaSucia.SolidosEnSuspension = Math.max(aguaSucia.SolidosEnSuspension - procesadora.SolidosEnSuspensionProcesadora, 0f);
    aguaSucia.ContaminantesEmergentes = Math.max(aguaSucia.ContaminantesEmergentes - procesadora.ContaminantesEmergentesProcesadora, 0f);
    aguaSucia.Sal = Math.max(aguaSucia.Sal - procesadora.SalProcesadora, 0f);
    aguaSucia.Carbonatos = Math.max(aguaSucia.Carbonatos - procesadora.CarbonatosProcesadora, 0f);
    aguaSucia.ResiduosHumanos = !procesadora.ResiduosHumanosProcesadora;
  }
  
  
  private MasaDeAgua mezclaAguas(MasaDeAgua a, MasaDeAgua b) {
    MasaDeAgua masaAgua = new MasaDeAgua();
    masaAgua.Volumen = a.Volumen + b.Volumen;
    masaAgua.DBO = (a.DBO * a.Volumen + b.DBO * b.Volumen) / masaAgua.Volumen;
    masaAgua.DQO = (a.DQO * a.Volumen + b.DQO * b.Volumen) / masaAgua.Volumen;
    masaAgua.Ph = (a.Ph * a.Volumen + b.Ph * b.Volumen) / masaAgua.Volumen;
    masaAgua.Fosforo = (a.Fosforo * a.Volumen + b.Fosforo * b.Volumen) / masaAgua.Volumen;
    masaAgua.SolidosEnSuspension = (a.SolidosEnSuspension * a.Volumen + b.SolidosEnSuspension * b.Volumen) / masaAgua.Volumen;
    masaAgua.ContaminantesEmergentes = (a.ContaminantesEmergentes * a.Volumen + b.ContaminantesEmergentes * b.Volumen) / masaAgua.Volumen;
    masaAgua.Sal = (a.Sal * a.Volumen + b.Sal * b.Volumen) / masaAgua.Volumen;
    masaAgua.Carbonatos = (a.Carbonatos * a.Volumen + b.Carbonatos * b.Volumen) / masaAgua.Volumen;
    masaAgua.ResiduosHumanos = a.ResiduosHumanos || b.ResiduosHumanos;
    return masaAgua;
  }
  
  public void mezclaAguas() {
    OntModel model = ontoManager.getModel();
    String namingContext = ontoManager.getNamingContext();
    OntProperty aguaTramoProperty = model.getOntProperty(namingContext + Constants.AGUA_TRAMO);
    
    // mezclar las aguas
    RDFNode aguaNode = individual.getPropertyValue(aguaTramoProperty);
    Individual aguaNodeIndividual = aguaNode.as(Individual.class);
    MasaDeAgua aguaTramo = (MasaDeAgua)ReflectionUtils.castIndividual(aguaNodeIndividual, MasaDeAgua.class);
    MasaDeAgua aguaNueva = mezclaAguas(aguaTramo, aguaSucia);
    
    if (aguaNueva != null) {
      // quitar del tramo el agua que habia
      model.remove(individual, aguaTramoProperty, aguaNodeIndividual);
      ontoManager.dropIndividual(aguaNodeIndividual);
      
      // anadir al tramo el agua mezclada
      Individual aguaNuevaIndividual = ontoManager.createIndividual(aguaNueva);
      model.add(individual, aguaTramoProperty, aguaNuevaIndividual);
      
      // imprimir resultado accion
      OntProperty tramoIdProperty = model.getOntProperty(namingContext + Constants.TRAMO_ID);
      RDFNode tramoIdNode = individual.getPropertyValue(tramoIdProperty);
      System.out.println("Se ha vertido agua en el tramo " + tramoIdNode.asLiteral().getInt());
      System.out.println("Agua que había en el tramo:");
      aguaTramo.showData();
      System.out.println("   Volumen: " + aguaTramo.Volumen + " litros");
      System.out.println("Agua vertida:");
      aguaSucia.showData();
      System.out.println("   Volumen: " + aguaSucia.Volumen + " litros");
      System.out.println("Agua que queda en el tramo:");
      aguaNueva.showData();
      System.out.println("   Volumen: " + aguaNueva.Volumen + " litros");
    }
    
  }
  
}
