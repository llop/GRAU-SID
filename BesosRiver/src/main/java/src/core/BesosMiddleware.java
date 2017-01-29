/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package src.core;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Scanner;
import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntProperty;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.util.iterator.ExtendedIterator;
import src.agent.CiudadAgent;
import src.agent.DepuradoraAgent;
import src.agent.IndustriaAgent;
import src.agent.RioAgent;
import src.data.MasaDeAgua;
import src.utils.ReflectionUtils;

/**
 *
 * @author llop
 */
public class BesosMiddleware {
  
  
  public BesosMiddleware() {
    // backup source owl file and infer
    try (final InputStream inStream = new FileInputStream(Constants.OWL_FILE_PATH);
         final OutputStream outStream = new FileOutputStream(Constants.OWL_TMP_FILE_PATH)) {
      byte[] buffer = new byte[1024];
      int length;
      while ((length = inStream.read(buffer)) > 0) outStream.write(buffer, 0, length);
      inferir();
    } catch (IOException e) {
      e.printStackTrace(System.out);
    }
  }
  
  //----------------------------------------------------------------------------
  // funciones para main
  //----------------------------------------------------------------------------

  public final void inferir() {
    System.out.println("Inferiendo");
    System.out.println("----------");
    OntologyManager.getInstance().infer();
  }
  
  public void guardarOntologia() {
    System.out.println("Guardando ontología");
    System.out.println("-------------------");
    OntologyManager.getInstance().saveOntology();
  }
  
  public void cerrar() {
    OntModel model = OntologyManager.getInstance().getModel();
    if (model != null && !model.isClosed()) model.close();
  }
  
  
  
  public void listarAguasRio() {
    System.out.println("Aguas del río");
    System.out.println("-------------");
    
    OntologyManager ontoManager = OntologyManager.getInstance();
    OntModel model = ontoManager.getModel();
    String namingContext = ontoManager.getNamingContext();
    
    // sacar el rio
    Individual rioBesos = ontoManager.getIndividuals(namingContext + Constants.RIO).get(0);
    OntProperty nombreRioProperty = model.getOntProperty(namingContext + Constants.NOMBRE_RIO);
    RDFNode nombreRioNode = rioBesos.getPropertyValue(nombreRioProperty);
    System.out.println("Rio " + nombreRioNode.asLiteral().getString());
    
    // sacar la fuente
    OntProperty fuenteProperty = model.getOntProperty(namingContext + Constants.FUENTE);
    Resource tramoResource = rioBesos.getPropertyResourceValue(fuenteProperty);
    
    // seguir la cadena de tramos
    OntProperty tramoIdProperty = model.getOntProperty(namingContext + Constants.TRAMO_ID);
    OntProperty siguienteTramoProperty = model.getOntProperty(namingContext + Constants.SIGUIENTE_TRAMO);
    OntProperty aguaTramoProperty = model.getOntProperty(namingContext + Constants.AGUA_TRAMO);
    
    while (tramoResource != null) {
      Individual tramo = tramoResource.as(Individual.class);
      RDFNode tramoIdNode = tramo.getPropertyValue(tramoIdProperty);
      
      // sacar la masa de agua
      Resource aguaResource = tramo.getPropertyResourceValue(aguaTramoProperty);
      Individual aguaTramo = aguaResource.as(Individual.class);
      MasaDeAgua masaDeAguaTramo = (MasaDeAgua)ReflectionUtils.castIndividual(aguaTramo, MasaDeAgua.class);
      
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
      
      // imprimir todo
      String tramoData = "Tramo "+tramoIdNode.asLiteral().getInt()+" : "+masaDeAguaTramo.Volumen+" litros de agua";
      System.out.println(tramoData);
      System.out.println("   Clasificación: " + classesStr);
      masaDeAguaTramo.showData();
//      System.out.println("Nombre: "+aguaTramo.getLocalName());
//      System.out.println("  -Volumen: "+aguaTramo.Volumen);

      tramoResource = tramo.getPropertyResourceValue(siguienteTramoProperty);
    }
    
  }
  
  
  public void listarCiudades() {
    System.out.println("Ciudades");
    System.out.println("--------");
    
    OntologyManager ontoManager = OntologyManager.getInstance();
    String ciudadesClass = ontoManager.getNamingContext() + Constants.CIUDADES;
    List<Individual> listaCiudades = ontoManager.getIndividuals(ciudadesClass);
    listaCiudades.stream().forEach(ciudad -> { System.out.println(ciudad.getLocalName()); });
    
  }
  
  public void listarIndustrias() {
    System.out.println("Industrias");
    System.out.println("----------");
    
    OntologyManager ontoManager = OntologyManager.getInstance();
    String industriaClass = ontoManager.getNamingContext() + Constants.INDUSTRIA;
    List<Individual> listaIndustrias = ontoManager.getIndividuals(industriaClass);
    listaIndustrias.stream().forEach(industria -> { System.out.println(industria.getLocalName()); });
    
  }
  
  public void listarDepuradoras() {
    System.out.println("Depuradoras");
    System.out.println("-----------");
    
    OntologyManager ontoManager = OntologyManager.getInstance();
    String depuradoraClass = ontoManager.getNamingContext() + Constants.DEPURADORA;
    List<Individual> listaDepuradoras = ontoManager.getIndividuals(depuradoraClass);
    listaDepuradoras.stream().forEach(depuradora -> {
        System.out.println(depuradora.getLocalName()); 
        new DepuradoraAgent(depuradora).showTanques();
    });
    
  }
  
  public void pasarDias() {
    // pedir al usuario los días que quiere que pasen
    System.out.println("Pasar días");
    System.out.println("----------");
    System.out.println("¿Cuántos días quieres que transcurran? (¡No te pases!)");
    Scanner scanner = new Scanner(System.in);
    int dias = scanner.nextInt();
    
    // pasar el tiempo
    pasarDias(dias);
  }
  
  private void pasarDias(int dias) {
    
    OntologyManager ontoManager = OntologyManager.getInstance();
    String namingContext = ontoManager.getNamingContext();
    
    Individual rioBesos = ontoManager.getIndividuals(namingContext + Constants.RIO).get(0);
    List<Individual> listaDepuradoras = ontoManager.getIndividuals(namingContext + Constants.DEPURADORA);
    List<Individual> listaCiudades = ontoManager.getIndividuals(namingContext + Constants.CIUDADES);
    List<Individual> listaIndustrias = ontoManager.getIndividuals(namingContext + Constants.INDUSTRIA);
    
    for (int i=0; i<dias; ++i) {
      // tratar tramos
      // mover las aguas de los tramos para abajo
      new RioAgent(rioBesos).action();
      
      // tratar depuradoras
      // devolver el agua depurada al rio
      listaDepuradoras.stream().forEach(depuradora -> { new DepuradoraAgent(depuradora).action(); });
      
      // tratar ensuciadoras
      // ensuciar el agua
      listaCiudades.stream().forEach(ciudad -> { new CiudadAgent(ciudad).action(); });
      listaIndustrias.stream().forEach(industria -> { new IndustriaAgent(industria).action(); });
      
    }
    
  }
  
  
}
