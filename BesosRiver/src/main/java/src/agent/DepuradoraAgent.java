/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package src.agent;

import static java.lang.Math.min;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntProperty;
import org.apache.jena.rdf.model.NodeIterator;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.util.iterator.ExtendedIterator;
import src.core.Constants;
import src.data.MasaDeAgua;
import src.data.ProcesadoraDeAgua;
import src.utils.ReflectionUtils;

/**
 *
 * @author llop
 */
public class DepuradoraAgent extends BaseAgent {

    public DepuradoraAgent(Individual individual) {
        super(individual);
    }

    @Override
    public void action() {
        // pillar el nombre de la funcion que vacía los tanques
        List<String> funcNames = getFuncNames(Constants.VACIAR_TANQUES);
        if (!funcNames.isEmpty()) {
            ReflectionUtils.callFunction(this, funcNames.get(0));
        }
    }

    private boolean estaLimpia(MasaDeAgua aguaSucia, MasaDeAgua aguaLimpia) {
        return aguaSucia.DBO <= aguaLimpia.DBO
                && aguaSucia.DQO <= aguaLimpia.DQO
                && aguaSucia.Ph <= aguaLimpia.Ph
                && aguaSucia.Fosforo <= aguaLimpia.Fosforo
                && aguaSucia.SolidosEnSuspension <= aguaLimpia.SolidosEnSuspension
                && aguaSucia.ContaminantesEmergentes <= aguaLimpia.ContaminantesEmergentes
                && aguaSucia.Sal <= aguaLimpia.Sal
                && aguaSucia.Carbonatos <= aguaLimpia.Carbonatos;
    }

    public int diasLimpiarAgua(MasaDeAgua aguaSucia, MasaDeAgua aguaLimpia) {

        // pillar la funcion de limpiar aguas
        List<String> funcLimpiarAguaNames = getFuncNames(Constants.LIMPIA_AGUAS);
        String funcLimpiarAguaName = funcLimpiarAguaNames.get(0);

        // ir limpiando el agua hasta que quede bien
        MasaDeAguaAgent aguasAgent = new MasaDeAguaAgent();
        ProcesadoraDeAgua procesadora = (ProcesadoraDeAgua) ReflectionUtils.castIndividual(individual, ProcesadoraDeAgua.class);
        aguaSucia = aguaSucia.copy();
        int dias = 0;
        while (!estaLimpia(aguaSucia, aguaLimpia)) {
            ReflectionUtils.callFunction(aguasAgent, funcLimpiarAguaName, procesadora, aguaSucia);
            ++dias;
        }
        return dias;
    }

    // cuanto costaria limpiar el agua de esa procesadora
    // si es negativo, es que la depuradora no puede limpiarla
    public float costeLimpiarAgua(MasaDeAgua aguaSucia) {

        OntModel model = ontoManager.getModel();
        String namingContext = ontoManager.getNamingContext();

        // propiedades
        OntProperty taqueProcesadoraProperty = model.getOntProperty(namingContext + Constants.TANQUE_PROCESADORA);
        OntProperty aguaTanqueProperty = model.getOntProperty(namingContext + Constants.MASA_DE_AGUA_TANQUE);
        OntProperty volumenTanqueProperty = model.getOntProperty(namingContext + Constants.VOLUMEN_TANQUE);

        // encontrar tanques donde podria ir el agua
        float volumenDisponible = 0f;
        NodeIterator tanquesIt = individual.listPropertyValues(taqueProcesadoraProperty);
        while (tanquesIt.hasNext()) {
            RDFNode tanqueNode = tanquesIt.next();
            Individual tanque = tanqueNode.as(Individual.class);

            // tanque esta vacio si no tiene agua
            RDFNode aguaTanqueNode = tanque.getPropertyValue(aguaTanqueProperty);
            if (aguaTanqueNode == null) {
                float volumenDisponibleTanque = tanque.getPropertyValue(volumenTanqueProperty).asLiteral().getFloat();
                volumenDisponible += volumenDisponibleTanque;
            }
        }

        // mirar si hay suficiente espacio
        if (aguaSucia.Volumen > volumenDisponible) {
            return -1f;
        }

        // pillar el coste de limpiar un litro por dia
        OntProperty costeProperty = model.getOntProperty(namingContext + Constants.COSTE_DEPURADORA);
        RDFNode costeNode = individual.getPropertyValue(costeProperty);

        // el volumen de la procesadora se supone que tambien esta en litros
        float costeTotalDia = aguaSucia.Volumen * costeNode.asLiteral().getFloat();

        // cuantos dias para limpiar ese volumen?
        // pillar como de limpia queremos el agua al final
        String aguaDulceIndividualName = namingContext + Constants.MASA_DE_AGUA_DULCE;
        Individual aguaDulceIndividual = model.getIndividual(aguaDulceIndividualName);
        MasaDeAgua aguaLimpia = (MasaDeAgua)ReflectionUtils.castIndividual(aguaDulceIndividual, MasaDeAgua.class);

        // llamar la funcion de coste
        List<String> funcDiasNames = getFuncNames(Constants.DIAS_LIMPIAR_AGUA);
        int diasLimpieza = ((Integer) ReflectionUtils.callFunction(this, funcDiasNames.get(0), aguaSucia, aguaLimpia));

        return diasLimpieza * costeTotalDia;
    }

    public List<RDFNode> listaTanques() {
        OntModel model = ontoManager.getModel();
        String namingContext = ontoManager.getNamingContext();

        // propiedades
        OntProperty taqueProcesadoraProperty = model.getOntProperty(namingContext + Constants.TANQUE_PROCESADORA);

        // encontrar tanques donde podria ir el agua
        NodeIterator tanquesIt = individual.listPropertyValues(taqueProcesadoraProperty);

        List<RDFNode> listaTanques = new ArrayList<>();
        while (tanquesIt.hasNext()) {
            RDFNode tanqueNode = tanquesIt.next();
            listaTanques.add(tanqueNode);
        }
        return listaTanques;
    }

    public List<Pair<Float, RDFNode>> listaTanquesVacios() {
        OntModel model = ontoManager.getModel();
        String namingContext = ontoManager.getNamingContext();

        // propiedades
        OntProperty taqueProcesadoraProperty = model.getOntProperty(namingContext + Constants.TANQUE_PROCESADORA);
        OntProperty aguaTanqueProperty = model.getOntProperty(namingContext + Constants.MASA_DE_AGUA_TANQUE);
        OntProperty volumenTanqueProperty = model.getOntProperty(namingContext + Constants.VOLUMEN_TANQUE);

        // encontrar tanques donde podria ir el agua
        NodeIterator tanquesIt = individual.listPropertyValues(taqueProcesadoraProperty);

        List<Pair<Float, RDFNode>> listaTanques = new ArrayList<>();
        while (tanquesIt.hasNext()) {
            RDFNode tanqueNode = tanquesIt.next();
            Individual tanque = tanqueNode.as(Individual.class);
            RDFNode aguaTanqueNode = tanque.getPropertyValue(aguaTanqueProperty);

            if (aguaTanqueNode == null) {
                listaTanques.add(
                        Pair.of(
                                tanque.getPropertyValue(volumenTanqueProperty).asLiteral().getFloat(),
                                tanqueNode
                        )
                );
            }
        }
        return listaTanques;
    }

    public void llenarTanques(MasaDeAgua aguaSucia) {
        OntModel model = ontoManager.getModel();
        String namingContext = ontoManager.getNamingContext();

        OntProperty aguaTanqueProperty = model.getOntProperty(namingContext + Constants.MASA_DE_AGUA_TANQUE);
        OntProperty volumenTanqueProperty = model.getOntProperty(namingContext + Constants.VOLUMEN_TANQUE);

        List<Pair<Float, RDFNode>> listaTanques = listaTanquesVacios();

        Collections.sort(listaTanques, Comparator.comparing(coste -> -coste.getLeft()));

        int i = 0;

        while (aguaSucia.Volumen > 0) {
            MasaDeAgua aguaSuciaClone = aguaSucia.copy();
            RDFNode tanqueNode = listaTanques.get(i++).getRight();
            Individual tanque = tanqueNode.as(Individual.class);
            float volumenTanque = min(tanque.getPropertyValue(volumenTanqueProperty).asLiteral().getFloat(), aguaSuciaClone.Volumen);
            aguaSuciaClone.Volumen = volumenTanque;
            aguaSucia.Volumen -= volumenTanque;
            model.add(tanqueNode.asResource(), aguaTanqueProperty, ontoManager.createIndividual(aguaSuciaClone));
            
            // imprimir resultado
            System.out.println("Se ha añadido al tanque " + tanque.getLocalName() + " " + aguaSuciaClone.Volumen + " litros de agua sucia");
            aguaSuciaClone.showData();
            
        }
    }

    public float calcularEficiencia() {
        float volumenTotal = 0f;
        float volumenOcupado = 0f;

        OntModel model = ontoManager.getModel();
        String namingContext = ontoManager.getNamingContext();
        OntProperty aguaTanqueProperty = model.getOntProperty(namingContext + Constants.MASA_DE_AGUA_TANQUE);
        
        List<RDFNode> listaTanques = listaTanques();
        
        for (int i = 0; i < listaTanques.size(); i++) {
            RDFNode tanquerdf = listaTanques.get(i);
            Individual tanque = tanquerdf.as(Individual.class);
            RDFNode aguaTanqueNode = tanque.getPropertyValue(aguaTanqueProperty);
            OntProperty volumenTanqueProperty = model.getOntProperty(namingContext + Constants.VOLUMEN_TANQUE);

            volumenTotal += tanque.getPropertyValue(volumenTanqueProperty).asLiteral().getFloat();
            if (aguaTanqueNode != null) {
                Individual aguaTanqueIndividual = aguaTanqueNode.as(Individual.class);
                MasaDeAgua masaDeAguaTanque = (MasaDeAgua) ReflectionUtils.castIndividual(aguaTanqueIndividual, MasaDeAgua.class);
                volumenOcupado += masaDeAguaTanque.Volumen;
            }
        }

        return volumenOcupado / volumenTotal;
    }

    public void vaciarTanques() {
      //System.out.println("Vaciando tanques de "+individual.getLocalName());
      System.out.println();
      
      OntModel model = ontoManager.getModel();
      String namingContext = ontoManager.getNamingContext();
      
      // la depuradora
      ProcesadoraDeAgua procesadora = (ProcesadoraDeAgua)ReflectionUtils.castIndividual(individual, ProcesadoraDeAgua.class);

      // propiedades
      OntProperty taqueProcesadoraProperty = model.getOntProperty(namingContext + Constants.TANQUE_PROCESADORA);
      OntProperty aguaTanqueProperty = model.getOntProperty(namingContext + Constants.MASA_DE_AGUA_TANQUE);
      OntProperty tramoProperty = model.getOntProperty(namingContext + Constants.TRAMO_DESTINO);
    
      // tramo destino
      Resource tramoResource = individual.getPropertyResourceValue(tramoProperty);
      Individual tramo = tramoResource.as(Individual.class);
      
      // pillar la funcion de limpiar aguas
      List<String> funcLimpiarAguaNames = getFuncNames(Constants.LIMPIA_AGUAS);
      String funcLimpiarAguaName = funcLimpiarAguaNames.get(0);
      
      // pillar la funcion de calcular eficiencia
      List<String> funcEficienciaNames = getFuncNames(Constants.EFICIENCIA_DEPURADORA);
      String funcEficienciaName = funcEficienciaNames.get(0);
      
      // calcular la eficiencia
      float eficiencia = (float)ReflectionUtils.callFunction(this, funcEficienciaName);
      System.out.println("Depuradora: " + individual.getLocalName());
      System.out.println("Eficiencia: " + (eficiencia*100) + "%");
      
      // ejemplo de agua limpia
      String aguaDulceIndividualName = namingContext + Constants.MASA_DE_AGUA_DULCE;
      Individual aguaDulceIndividual = model.getIndividual(aguaDulceIndividualName);
      MasaDeAgua aguaLimpia = (MasaDeAgua)ReflectionUtils.castIndividual(aguaDulceIndividual, MasaDeAgua.class);

      // limpiar el agua de todos los tanques
      MasaDeAguaAgent aguasAgent = new MasaDeAguaAgent();
      NodeIterator tanquesIt = individual.listPropertyValues(taqueProcesadoraProperty);
      while (tanquesIt.hasNext()) {
        RDFNode tanqueNode = tanquesIt.next();
        Individual tanque = tanqueNode.as(Individual.class);
        RDFNode aguaTanqueNode = tanque.getPropertyValue(aguaTanqueProperty);
        if (aguaTanqueNode != null) {
          
          // si tiene agua el tanque, limpiarla
          Individual aguaTanqueIndividual = aguaTanqueNode.as(Individual.class);
          MasaDeAgua masaDeAguaTanque = (MasaDeAgua)ReflectionUtils.castIndividual(aguaTanqueIndividual, MasaDeAgua.class);
          
          // imprimir
          // sacar sus clases
          String classesStr = "";
          ExtendedIterator<OntClass> classesIt = aguaTanqueIndividual.listOntClasses(true);
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
          System.out.println("Iniciando depuración del tanque " + tanque.getLocalName() + " : " + masaDeAguaTanque.Volumen + " litros de agua");
          System.out.println("   Clasificación: " + classesStr);
          masaDeAguaTanque.showData();
          
          // hacer limpiado
          ReflectionUtils.callFunction(aguasAgent, funcLimpiarAguaName, procesadora, masaDeAguaTanque);
          
          // primero quitar el agua del tanque
          model.remove(tanque, aguaTanqueProperty, aguaTanqueIndividual);
          ontoManager.dropIndividual(aguaTanqueIndividual);
          
          if (estaLimpia(masaDeAguaTanque, aguaLimpia)) {
            // si el agua esta limpia, la enviamos al tramo
            new MasaDeAguaAgent(tramo, masaDeAguaTanque).action();
          } else {
            // si esta sucia, de vuelta al tanque
            Individual aguaNuevaTanque = ontoManager.createIndividual(masaDeAguaTanque);
            model.add(tanque, aguaTanqueProperty, aguaNuevaTanque);
            
            System.out.println("El agua sigue sucia, y debe quedarse más tiempo en el tanque");
            masaDeAguaTanque.showData();
          }
          
        }
      }
    }

    public void showTanques() {
        OntModel model = ontoManager.getModel();
        String namingContext = ontoManager.getNamingContext();

        // propiedades
        OntProperty taqueProcesadoraProperty = model.getOntProperty(namingContext + Constants.TANQUE_PROCESADORA);
        OntProperty aguaTanqueProperty = model.getOntProperty(namingContext + Constants.MASA_DE_AGUA_TANQUE);
        OntProperty volumenTanqueProperty = model.getOntProperty(namingContext + Constants.VOLUMEN_TANQUE);

        // encontrar tanques donde podria ir el agua
        NodeIterator tanquesIt = individual.listPropertyValues(taqueProcesadoraProperty);
        while (tanquesIt.hasNext()) {
            RDFNode tanqueNode = tanquesIt.next();
            Individual tanque = tanqueNode.as(Individual.class);
            System.out.println("Tanque: " + tanque.getLocalName());
            System.out.println("   -Volumen total: " + tanque.getPropertyValue(volumenTanqueProperty).asLiteral().getFloat() + "l");
            RDFNode aguaTanqueNode = tanque.getPropertyValue(aguaTanqueProperty);
            if (aguaTanqueNode != null) {
                Individual aguaTanqueIndividual = aguaTanqueNode.as(Individual.class);
                MasaDeAgua masaDeAguaTanque = (MasaDeAgua) ReflectionUtils.castIndividual(aguaTanqueIndividual, MasaDeAgua.class);
                System.out.println("   -Agua en el tanque: " + masaDeAguaTanque.Volumen);
            } else {
                System.out.println("   -Agua en el tanque: 0");
            }
        }
    }

}
