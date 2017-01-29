/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package src.data;

/**
 *
 * @author llop
 */
public class ProcesadoraDeAgua {
  
  
  public float DBOProcesadora;
  public float DQOProcesadora;
  public float PhProcesadora;
  public float FosforoProcesadora;
  public float SolidosEnSuspensionProcesadora;
  public float ContaminantesEmergentesProcesadora;
  public float SalProcesadora;
  public float CarbonatosProcesadora;
  public boolean ResiduosHumanosProcesadora;
  
  public float VolumenProcesadora;
  
  
  public MasaDeAgua toMasaDeAgua() {
    MasaDeAgua agua = new MasaDeAgua();
    agua.Volumen = VolumenProcesadora;
    agua.DBO = DBOProcesadora;
    agua.DQO = DQOProcesadora;
    agua.Ph = PhProcesadora;
    agua.Fosforo = FosforoProcesadora;
    agua.SolidosEnSuspension = SolidosEnSuspensionProcesadora;
    agua.ContaminantesEmergentes = ContaminantesEmergentesProcesadora;
    agua.Sal = SalProcesadora;
    agua.Carbonatos = CarbonatosProcesadora;
    agua.ResiduosHumanos = ResiduosHumanosProcesadora;
    return agua;
  }
  
  
}
