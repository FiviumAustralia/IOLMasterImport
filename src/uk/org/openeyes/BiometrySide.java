/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.org.openeyes;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.ArrayList;

/**
 *
 * @author VEDELEKT
 */
public class BiometrySide {
    private double K1;
    private double K2;
    private double AxisK1;
    private double AxisK2;
    private double DeltaK;
    private double DeltaKAxis;
    private double RefractionSphere = 0;
    private double RefractionDelta = 0;
    private double RefractionAxis = 0;
    private Integer EyeStatus = -1;
    private double AL;      // axial length
    private double ACD = 0;
    private double TargetRef = 0;
    private BigDecimal SNR;
    private BigDecimal SNRMin = BigDecimal.ZERO;
    private boolean isALModified = false;
    private boolean isKModified = false;
    private boolean isACDModified = false;
    private ArrayList<BiometryMeasurementData> Measurements = new ArrayList<>();

    public void setK1(Double BK1){
        if(K1 == 0){
            this.K1 = BK1;
        }
    }
    
    public double getK1(){
        return this.K1;
    }
    
    public void setK2(Double BK2){
        this.K2 = BK2;
    }
    
    public double getK2(){
        return this.K2;
    }

    public void setAxisK1(Double BAxisK1){
        this.AxisK1 = BAxisK1;
    }
    
    public double getAxisK1(){
        return this.AxisK1;
    }
    
    public void setAxisK2(Double BAxisK2){
        this.AxisK2 = BAxisK2;
    }
    
    public double getAxisK2(){
        return this.AxisK2;
    }
    
    public void setDeltaK(Double DeltaK){
        this.DeltaK = DeltaK;
    }
    
    public double getDeltaK(){
        return this.DeltaK;
    }
    
    public void setTargetRef(Double TargetRef){
        this.TargetRef = TargetRef;
    }
    
    public double getTargetRef(){
        return this.TargetRef;
    }
    
    public void setDeltaKAxis(Double DeltaKAxis){
        this.DeltaKAxis = DeltaKAxis;
    }
    
    public double getDeltaKAxis(){
        return this.DeltaKAxis;
    }

    public void setRefractionSphere(Double RefractionSphere){
        if(this.RefractionSphere == 0){
            this.RefractionSphere = RefractionSphere;
        }
    }
    
    public double getRefractionSphere(){
        return this.RefractionSphere;
    }
    
    public void setRefractionDelta(Double RefractionDelta){
        if(this.RefractionDelta == 0){
            this.RefractionDelta = RefractionDelta;
        }
    }
    
    public double getRefractionDelta(){
        return this.RefractionDelta;
    }

    public void setRefractionAxis(Double RefractionAxis){
        if(this.RefractionAxis == 0){
            this.RefractionAxis = RefractionAxis;
        }
    }
    
    public double getRefractionAxis(){
        return this.RefractionAxis;
    }    

    public void setEyeStatus(String EyeStatus){
        this.EyeStatus = Integer.parseInt(EyeStatus);
    }
    
    public Integer getEyeStatus(){
        return this.EyeStatus;
    }

    public void setACD(Double BACD){
        //if(this.ACD == 0){
            this.ACD = BACD;
        //}
    }
    
    public double getACD(){
        return this.ACD;
    }
    
    public void setAL(Double BAL){
        this.AL = BAL;
    }
    
    public double getAL(){
        return this.AL;
    }

    public void setSNRMin(Double BSNRMin){
        if(this.SNRMin == null || this.SNRMin.equals(BigDecimal.ZERO) || BigDecimal.valueOf(BSNRMin).compareTo(this.SNRMin) == -1 ){
            if(BSNRMin != 0.0){
                this.SNRMin = BigDecimal.valueOf(BSNRMin);
            }
        }
    }
    
    public BigDecimal getSNRMin(){
        return this.SNRMin;
    }
    
    public void setSNR(Double BSNR){
        this.SNR = BigDecimal.valueOf(BSNR);
    }
    
    public BigDecimal getSNR(){
        return this.SNR;
    }
    
    // add new lenses to the selected side
    public void addCalculations(){
        Measurements.add(new BiometryMeasurementData());
    }
    
    public Integer getMeasurementsIndex(){
        return Measurements.size()-1;
    }
    
    public void setLensesName(String LName, Integer LNum){
        Measurements.get(LNum).setLenseName(LName);
    }
    
    public void setFormulaName(String LName, Integer LNum){
        Measurements.get(LNum).setFormulaName(LName);
    }
    
    public void setLenseAConst(Double LAConst, Integer LNum){
        Measurements.get(LNum).setAConst(LAConst);
    }
    
    public void setLenseEmmetropia(Double LEmmetropia, Integer LNum){
        Measurements.get(LNum).setEmmetropia(LEmmetropia);
    }
    
    public void setLenseIOL(Double LIOL, Integer LNum){
        Measurements.get(LNum).setIOL(LIOL);
    }
    
    public void setLenseREF(Double LREF, Integer LNum){
        Measurements.get(LNum).setREF(LREF);
    }
    
    public ArrayList<BiometryMeasurementData> getMeasurements(){
        return this.Measurements;
    }
    
    public void setMeasurements(ArrayList<BiometryMeasurementData> LMeasurements){
        this.Measurements = LMeasurements;
    }
    
    public void setisALModified(String isALModified){
        // this value is stored in the DICOM file as string 'YES'
        if(isALModified.equals("YES")){
            this.isALModified = true;
        }
    }
    
    public boolean getisALModified(){
        return this.isALModified;
    }

    public void setisKModified(String isKModified){
        // this value is stored in the DICOM file as string 'YES'
        if(isKModified.equals("YES")){
            this.isKModified = true;
        }
    }
    
    public boolean getisKModified(){
        return this.isKModified;
    }
    
    public void setisACDModified(String isACDModified){
        // this value is stored in the DICOM file as string 'YES'
        if(isACDModified.equals("YES")){
            this.isACDModified = true;
        }
    }
    
    public boolean getisACDModified(){
        return this.isACDModified;
    }
    
    
    // check if this side have any particular data
    // if all values are in default than we return false
    public boolean isSideHaveData(){
        if( this.K1 == 0.0 && this.K2 == 0.0 && this.AxisK1 == 0.0 && this.AL == 0.0 && this.Measurements.size() == 0){
            return false;
        }else{
            return true;
        }
    }
    
    public String printBiometrySide(String sideName){
       String output;
       output = sideName+" K1: "+this.K1+"\n";
       output += sideName+" K2: "+this.K2+"\n";
       output += sideName+" Axis K1: "+this.AxisK1+"\n";
       output += sideName+" Axis K2: "+this.AxisK2+"\n";
       output += sideName+" Delta K: "+this.DeltaK+"\n";
       output += sideName+" Delta K Axis: "+this.DeltaKAxis+"\n";
       output += sideName+" Target refraction: "+this.TargetRef+"\n";
       output += sideName+" Refraction sphere: "+this.RefractionSphere+"\n";
       output += sideName+" Refraction delta: "+this.RefractionDelta+"\n";
       output += sideName+" Refraction axis: "+this.RefractionAxis+"\n";
       output += sideName+" Eye status: "+this.EyeStatus+"\n";
       output += sideName+" ACD: "+this.ACD+"\n";
       output += sideName+" Axial length: "+this.AL+"\n";
       output += sideName+" SNR: "+this.SNR+"\n";
       output += sideName+" SNR Min: "+this.SNRMin+"\n";
       output += sideName+" AL modified: "+this.isALModified+"\n";
       output += sideName+" K modified: "+this.isKModified+"\n";
       output += sideName+" ACD modified: "+this.isACDModified+"\n";
       output += "\n";
       for(BiometryMeasurementData LensData: Measurements){
           output += LensData.printLenses();
       }
       //System.out.println("Predicted refraction: "+this.PredictedREF);
       return output;
    }
}
