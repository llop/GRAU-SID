/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package src.core;

/**
 *
 * @author llop
 */
public class Constants {
  
  
  //----------------------------------------------------------------------------
  // Masas de agua
  //----------------------------------------------------------------------------
  public static final String MASA_DE_AGUA = "MasaDeAgua";
  
  public static final String NEGRA = "Negras";
  public static final String DURA = "Dura";
  public static final String ALCALINA = "Alcalina";
  public static final String SALADA = "Salada";
  public static final String SALOBRE = "Salobre";
  public static final String GRIS = "Grises";
  public static final String POTABLE = "Potable";
  public static final String DULCE = "Dulce";
  public static final String BLANDA = "Blanda";
  
  public static final String NAMED_INDIVIDUAL = "NamedIndividual";
  public static final String MASA_DE_AGUA_DULCE = "MasaDeAguaDulce";
  public static final String MASA_DE_AGUA_VACIA = "MasaDeAguaVacia";
  
  public static final String MEZCLA_AGUAS = "MezclaAguas";
  public static final String LIMPIA_AGUAS = "LimpiaAguas";
  public static final String ENSUCIA_AGUAS = "EnsuciaAguas";
  
  
  //----------------------------------------------------------------------------
  // Rio
  //----------------------------------------------------------------------------
  public static final String RIO = "Rio";
  
  public static final String NOMBRE_RIO = "NombreRio";
  public static final String FUENTE = "Fuente";
  
  public static final String AVANZA_CURSO_RIO = "AvanzaCursoRio";
  
  
  //----------------------------------------------------------------------------
  // Tramo
  //----------------------------------------------------------------------------
  public static final String SIGUIENTE_TRAMO = "SiguienteTramo";
  public static final String AGUA_TRAMO = "AguaTramo";
  public static final String TRAMO_ID = "TramoID";
  
  
  //----------------------------------------------------------------------------
  // Ensuciadoras
  //----------------------------------------------------------------------------
  public static final String TRAMO_ORIGEN = "TramoOrigen";
  public static final String DEPURADORA_ASIGNADA = "DepuradoraAsignada";
  public static final String TANQUE_PROCESADORA = "TanqueProcesadora";
  
  
  //----------------------------------------------------------------------------
  // Industria
  //----------------------------------------------------------------------------
  public static final String INDUSTRIA = "Industria";
  
  public static final String OVINA = "Ovina";
  public static final String MADERERA = "Maderera";
  public static final String PORCINA = "Porcina";
  
  public static final String AGUA_SUCIA_INDUSTRIA = "AguaSuciaIndustria";
  // instancias: AguaSuciaIndustriaNormal FuncName="aguaSuciaIndustriaNormal"
  // instancias: AguaSuciaIndustriaMucho FuncName="aguaSuciaIndustriaMucho"
  
  //public static final String ENVIA_AGUA_INDUSTRIA = "EnviaAguaIndustria";
  // instancias: EnviaAguaIndustriaPrecio FuncName="enviaAguaIndustriaPrecio"
  // instancias: EnviaAguaIndustriaTiempo FuncName="enviaAguaIndustriaTiempo"
  
  
  //----------------------------------------------------------------------------
  // Ciudades
  //----------------------------------------------------------------------------
  public static final String CIUDADES = "Ciudades";
  
  public static final String AGUA_SUCIA_CIUDAD = "AguaSuciaCiudad";
  // instancias: AguaSuciaCiudadNormal FuncName="aguaSuciaCiudadNormal"
  // instancias: AguaSuciaCiudadMucho FuncName="aguaSuciaCiudadMucho"
  
  
  //----------------------------------------------------------------------------
  // Depuradoras
  //----------------------------------------------------------------------------
  public static final String DEPURADORA = "Depuradora";
  
  public static final String EFICIENCIA_DEPURADORA = "EficienciaDepuradora";
  public static final String COSTE_DEPURADORA = "CosteDepuradora";
  public static final String TRAMO_DESTINO = "TramoDestino";
  
  public static final String VACIAR_TANQUES = "VaciarTanques";
  public static final String LLENAR_TANQUES = "LlenarTanques";
  public static final String COSTE_LIMPIAR_AGUA = "CosteLimpiarAguaDepuradora";
  public static final String DIAS_LIMPIAR_AGUA = "DiasLimpiarAguaDepuradora";
  
  
  //----------------------------------------------------------------------------
  // Tanques
  //----------------------------------------------------------------------------
  public static final String MASA_DE_AGUA_TANQUE = "MasaDeAguaTanque";
  public static final String VOLUMEN_TANQUE = "VolumenTanque";
  
  
  //----------------------------------------------------------------------------
  // OWL files
  //----------------------------------------------------------------------------
  public static final String OWL_FILE_PATH = "src/main/java/resources/besosriver.owl";
  public static final String OWL_TMP_FILE_PATH = "src/main/java/resources/besosriver-tmp.owl";
  //public static final String OWL_TMP_FILE_PATH_INFERRED = "src/main/java/resources/besosriver-inferred.owl";
  
  public static final String ONTOLOGY_IRI = "http://www.semanticweb.org/pacific/ontologies/2016/4/besosriver#";
  
  
  
}
