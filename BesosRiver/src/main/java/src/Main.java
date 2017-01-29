/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package src;

import java.io.IOException;
import java.util.Scanner;

import src.core.BesosMiddleware;

/**
 *
 * @author pacific
 */
public class Main {

  // http://www.kenbi.eu/kenbipedia_3.php?seccion=kenbipedia&capitulo=3
  // DQO (DEMANDA QUÍMICA DE OXÍGENO)
  // La DQO es “la cantidad de oxígeno necesario para oxidar la materia orgánica por medios químicos y convertirla en dióxido de carbono y agua”.
  // La DQO se utiliza para medir el grado de contaminación y se expresa en miligramos de oxígeno diatómico por litro (mgO2/l).
  // Cuanto mayor es la DQO más contaminante es la muestra.
  // Las concentraciones de DQO en las aguas residuales industriales pueden tener unos valores entre 50 y 2000 mgO2/l, 
  // aunque es frecuente, según el tipo de industria, valores de 5000, 1000 e incluso más altos.
  // ¿QUE ES LA DBO (DEMANDA BIOLÓGICA DE OXÍGENO)?
  // La D.B.O. es “la cantidad de oxígeno que los microorganismos, especialmente bacterias (aeróbias o anaerobias facultativas: 
  // Pseudomonas, Escherichia, Aerobacter, Bacillus), hongos y plancton, consumen durante la degradación de las sustancias orgánicas contenidas en la muestra”.
  // La DBO se utiliza para medir el grado de contaminación y se expresa en miligramos de oxígeno diatómico por litro (mgO2/l). 
  // Como el proceso de descomposición varía según la temperatura, este análisis se realiza en forma estándar  durante cinco días a 20 ºC; esto se indica como D.B.O5.
  // Cuanto mayor sea la contaminación, mayor será la D. B. O.
  // La D. B. O. proporciona una medida sólo aproximada de la materia orgánica biodegradable presente en las aguas residuales.
  // Agua Pura............................................................ 0 - 20 mg/lt
  // Agua Levemente Contaminada....................... 20 - 100 mg/lt
  // Agua Medianamente Contaminada ................100 - 500 mg/lt
  // Agua Muy Contaminada ............................. 500 - 3000 mg/lt
  // Agua Extremadamente Contaminada .... 3000 - 15000 mg/lt
  private static void printMenu() {
      System.out.println("------------ Welcome to Besos River AI ------------");
      System.out.println("¿Qué desea hacer?");
      System.out.println("Menú:");

      System.out.println("1. Listar aguas río");
      System.out.println("2. Listar ciudades");
      System.out.println("3. Listar industrias");
      System.out.println("4. Listar depuradoras");
      
      System.out.println("5. Pasar días");
      
      System.out.println("8. Inferir aguas");
      System.out.println("9. Guardar Ontologia");
      System.out.println("0. Salir");
  }

  public static void main(String[] args) throws IOException {

    BesosMiddleware middleware = new BesosMiddleware();

    // read commands
    Scanner scanner = new Scanner(System.in);
    int i = 1;
    while (i != 0) {
      printMenu();
      i = scanner.nextInt();

      switch (i) {
        case 1:
          middleware.listarAguasRio();
          break;
        case 2:
          middleware.listarCiudades();
          break;
        case 3:
          middleware.listarIndustrias();
          break;
        case 4:
          middleware.listarDepuradoras();
          break;
        case 5:
          middleware.pasarDias();
          break;
        case 8:
          middleware.inferir();
          break;
        case 9:
          middleware.guardarOntologia();
      }
      System.out.println();
      System.out.println();
    }
    middleware.cerrar();
  }

  

}
