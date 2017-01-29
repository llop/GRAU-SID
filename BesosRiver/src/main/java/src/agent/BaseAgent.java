/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package src.agent;

import java.util.ArrayList;
import java.util.List;
import org.apache.jena.ontology.Individual;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFactory;
import org.apache.jena.rdf.model.RDFNode;
import src.core.Constants;
import src.core.OntologyManager;

/**
 *
 * @author llop
 */
public abstract class BaseAgent {
  
  
  protected OntologyManager ontoManager;
  protected Individual individual;
  
  
  public BaseAgent(Individual individual) {
    this.ontoManager = OntologyManager.getInstance();
    this.individual = individual;
  }
  
  
  public abstract void action();
  
  
  public List<String> getFuncNames(String funcId) {
    // create query string
    String qStr = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "+
                  "PREFIX owl: <http://www.w3.org/2002/07/owl#> "+
                  "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> "+
                  "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "+
                  "PREFIX prac: <" + Constants.ONTOLOGY_IRI + "> "+
                  "SELECT ?funcName "+
                  "WHERE { "+
                  "?s a prac:" + funcId + " ." +
                  "?s prac:FuncName ?funcName }";
    
    // execute actual query
    Query query = QueryFactory.create(qStr);
    List<String> funcNames = new ArrayList<>();
    try (QueryExecution qe = QueryExecutionFactory.create(query, ontoManager.getModel())) {
      ResultSet rs = qe.execSelect();
      rs = ResultSetFactory.copyResults(rs);

      // stuff function names into a list
      while (rs.hasNext()) {
        QuerySolution sol = rs.next();
        RDFNode n = sol.get("funcName");
        if (n != null) funcNames.add(n.toString());
      }
      
      // try-with-resources should close the 'qe' automatically:
      // qe.close();
    }
    return funcNames;
  }
  
  
}
