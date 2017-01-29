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
public class MasaDeAgua {
  
  
  
  public float DBO;
  public float DQO;
  public float Ph;
  public float Fosforo;
  public float SolidosEnSuspension;
  public float ContaminantesEmergentes;
  public float Sal;
  public float Carbonatos;
  public boolean ResiduosHumanos;
  
  public float Volumen;

    public void showData() {
        System.out.println("   DBO: "+DBO+" mg/l");
        System.out.println("   DQO: "+DQO+" mg/l");
        System.out.println("   Ph: "+Ph);
        System.out.println("   Fosfatos: "+Fosforo+" mg/l");
        System.out.println("   Solidos en suspensión: "+SolidosEnSuspension+" mg/l");
        System.out.println("   Contaminantes emergentes: "+ContaminantesEmergentes+" mg/l");
        System.out.println("   Sales: "+Sal+" mg/l");
        System.out.println("   Carbonatos: "+Carbonatos);
        System.out.println("   Residuos humanos: "+(ResiduosHumanos?"Sí":"No"));
    }
    
  
  public MasaDeAgua copy() {
    MasaDeAgua clon = new MasaDeAgua();
    clon.DBO = DBO;
    clon.DQO = DQO;
    clon.Ph = Ph;
    clon.Fosforo = Fosforo;
    clon.SolidosEnSuspension = SolidosEnSuspension;
    clon.ContaminantesEmergentes = ContaminantesEmergentes;
    clon.Sal = Sal;
    clon.Carbonatos = Carbonatos;
    clon.ResiduosHumanos = ResiduosHumanos;
    clon.Volumen = Volumen;
    return clon;
  }
  
  
}
