/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package src.utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import src.core.OntologyManager;

/**
 *
 * @author llop
 */
public class ReflectionUtils {
  
  
  public static Object castIndividual(Individual ind, Class klass) {
    
    OntModel model = OntologyManager.getInstance().getModel();
    String namingContext = OntologyManager.getInstance().getNamingContext();
    
    Object obj = null;
    try {
      obj = klass.newInstance();
      Field[] fields = klass.getFields();
      for (Field field : fields) {
        String fieldName = field.getName();
        
        // get field value from individual
        Property prop = model.getProperty(namingContext + fieldName);
        RDFNode node = ind.getPropertyValue(prop);
        
        // set field's value in object
        if (node != null) {
          Object value = node.asLiteral().getValue();
          field.set(obj, value);
        }
      }
    } catch (InstantiationException | IllegalAccessException ex) {
      ex.printStackTrace(System.out);
    }
    return obj;
  }
  
  
  public static void copyFields(Object obj, Individual ind) {
    OntModel model = OntologyManager.getInstance().getModel();
    String namingContext = OntologyManager.getInstance().getNamingContext();
    
    try {
      Field[] fields = obj.getClass().getFields();
      for (Field field : fields) {
        String fieldName = field.getName();
        Object fieldValue = field.get(obj);
        
        Property prop = model.getProperty(namingContext + fieldName);
        Literal lit = model.createTypedLiteral(fieldValue);
        ind.addLiteral(prop, lit);
      }
    } catch (IllegalArgumentException | IllegalAccessException ex) {
      ex.printStackTrace(System.out);
    }
  }
  
  
  
  public static Object callFunction(Object obj, String funcName, Object... args) {
    Object returnValue = null;
    // llamar la funcion por reflection
    try {
      // pillar los tipos
      Class<?> parameterTypes[] = new Class<?>[args.length];
      for (int i=0; i<args.length; ++i) parameterTypes[i] = args[i].getClass();
      
      // pillar el metodo y llamarlo
      Method method = obj.getClass().getMethod(funcName, parameterTypes);
      returnValue = method.invoke(obj, args);
    } catch (SecurityException | NoSuchMethodException |
            IllegalArgumentException | IllegalAccessException | InvocationTargetException ex) {
      ex.printStackTrace(System.out);
    }
    return returnValue;
  }
  
  
}
