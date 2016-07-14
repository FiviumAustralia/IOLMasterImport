/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.org.openeyes;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.pdfbox.io.RandomAccessBuffer;
import org.apache.pdfbox.io.RandomAccessRead;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageTree;
import org.apache.pdfbox.text.PDFTextStripper;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Sequence;
import org.dcm4che3.data.VR;

/**
 *
 * @author veta
 */
public class DICOMIOLMaster700 extends IOLMasterAbstract{
    private PDFFunctions PDFHelper = null;
     
    public DICOMIOLMaster700(DICOMParser mainParser){
        this.parser = mainParser;
    }

    public void collectData(Attributes Attrs){
        
        /* ---- IOLMaster 700 SQ tags ---
        *  IOL_Measured_Values sequence: 771Bxx30 (first page)
        *       >> Sequence of single axial length measurements: 771Bxx31
        *  Sequence of keratometry values:  771Bxx32  >> Sequence of single keratometry measurements 771Bxx33
        *  Sequence of anterior chamber depth values: 771Bxx34
        *  Sequence of white-to-white values measured for one eye: 771Bxx35 >> Sequence of single white-to-white measurements: 771Bxx3B
        *  Calculations are stored in an encapsulated PDF file stored in tag 00420011
        */
        
        // data priority: if we have calculation data we should extract all values from there
        //dumpDCMStructure(Attrs);
        
        if(Attrs.contains(parser.getTagInteger("771B1032"))){
            parser.debugMessage("IOL_Measured_Values sequence exists, extracting values");            
            // Axis K1, DeltaK and SNR values
            collectCommonMeasuredValues(Attrs, "L");
            collectCommonMeasuredValues(Attrs, "R");
            // K1, K2, Axis, ACD, Delta K, etc.
            collectMeasuredValues(Attrs, "L");
            collectMeasuredValues(Attrs, "R");
        }
        
        if(Attrs.contains(parser.getTagInteger("00420011"))){
            parser.debugMessage("Calculation sequence exists, extracting values");
            if(Attrs.contains(parser.getTagInteger("771B1030"))){
                setMinSnrForSides(Attrs);
            }
            //collectMeasuredValuesFromCalculation(Attrs);
            collectCalculationValues(Attrs);
        }else{
            parser.debugMessage("No basic measurement data found");
        }

        parser.Biometry.setSideData("L", parser.BiometryLeft);
        parser.Biometry.setSideData("R", parser.BiometryRight);

    }
    
    public void collectMeasuredValuesFromCalculation(Attributes Attrs){
        // the sequence structure is different, with different TAG numbers!!!
        String sequenceTag = selectSequenceTag(Attrs);
        
        Sequence CalcSeq = Attrs.getSequence(parser.getTagInteger(sequenceTag));
        if(CalcSeq != null && !CalcSeq.isEmpty()){
            if(sequenceTag.equals("771B1001")){
                for(int cs = 0; cs < CalcSeq.size(); cs++ ){
                    collectMeasuredValuesFromFormulaSeq(CalcSeq.get(cs), parser.getSideFromAttributes(CalcSeq.get(cs)));
                }
            }else{
                Attributes CalcAttrs = (Attributes) CalcSeq.get(0);
                Sequence FormulaSeq = CalcAttrs.getSequence(parser.getTagInteger("771B1001"));
                if(FormulaSeq != null && !FormulaSeq.isEmpty()){
                    for(int fs = 0; fs < FormulaSeq.size(); fs++ ){
                        collectMeasuredValuesFromFormulaSeq(FormulaSeq.get(fs), parser.getSideFromAttributes(FormulaSeq.get(fs)));
                    }
                }
            }
        }
    }
        
    public void collectCalculationValuesFromSeq(Attributes Attrs, String side, String inFormulaName, String inLensName){
        String FormulaName;
        String LensName;
        BiometrySide sideData;
        if(side.equals("L")){
            sideData = parser.BiometryLeft;
        }else{
            sideData = parser.BiometryRight;
        }
        //parser.debugMessage(":: "+side+"::"+inFormulaName+"::"+inLensName);
        parser.CurrentSide = parser.getSideFromAttributes(Attrs);
        if(side.equals(parser.CurrentSide)){
            Sequence CalcSeq = Attrs.getSequence(parser.getTagInteger("771B1003"));
            if(CalcSeq != null && !CalcSeq.isEmpty()){
                for(int cs=0; cs<CalcSeq.size(); cs++){
                    Attributes CalcAttrs = CalcSeq.get(cs);
                    if(inFormulaName.equals("")){
                        FormulaName = VR.PN.toStrings(CalcAttrs.getValue(parser.getTagInteger("771B1006")), true, parser.CharacterSet).toString();
                        LensName=inLensName;
                    }else{
                        LensName = VR.PN.toStrings(CalcAttrs.getValue(parser.getTagInteger("771B1006")), true, parser.CharacterSet).toString();
                        FormulaName = inFormulaName;
                    }
                    // we add new data set
                    sideData.addCalculations();
                    //parser.debugMessage("Index: "+sideData.getMeasurementsIndex());
                    sideData.setFormulaName(FormulaName, sideData.getMeasurementsIndex());
                    sideData.setLensName(LensName, sideData.getMeasurementsIndex());
                    sideData.setLensEmmetropia(VR.FD.toDouble(CalcAttrs.getValue(parser.getTagInteger("771B102B")), false, 0, 0), sideData.getMeasurementsIndex());
                    sideData.setLensAConst(VR.FD.toDouble(CalcAttrs.getValue(parser.getTagInteger("771B1007")), false, 0, 0), sideData.getMeasurementsIndex());
                    Sequence IOLCalcSeq = CalcAttrs.getSequence(parser.getTagInteger("771B1005"));
                    if(IOLCalcSeq != null && !IOLCalcSeq.isEmpty()){
                        for(int iols=0; iols<IOLCalcSeq.size(); iols++){
                            Attributes IOLCalcAttrs = IOLCalcSeq.get(iols);
                            sideData.setLensIOL(VR.FD.toDouble(IOLCalcAttrs.getValue(parser.getTagInteger("771B102A")), false, 0, 0), sideData.getMeasurementsIndex());
                            sideData.setLensREF(VR.FD.toDouble(IOLCalcAttrs.getValue(parser.getTagInteger("771B1028")), false, 0, 0), sideData.getMeasurementsIndex());
                        }
                    }
                }
            }
        }
        
    }
    
    private String getFormulaLensName(String fullText, String FormulaLens){
        Pattern p;
        if(FormulaLens.equals("F")){
            p = Pattern.compile("Formula: (.*)",Pattern.MULTILINE);
        }else{
            p = Pattern.compile("IOL: (.*)",Pattern.MULTILINE);
        }
        Matcher m = p.matcher( fullText );
        while( m.find() ) {
            return m.group(1);
        }
        return "";
    }
    
    private String testAconst(Pattern p, String fullText){
        Matcher m = p.matcher( fullText );
        while( m.find() ){
            return m.group(1);
        }
        return "";
    }
    
    private Double getAconstValue(String AconstTxt){
        Pattern p;
        String AconstValue = "";
        p = Pattern.compile("A-Const: (.*)",Pattern.MULTILINE);
        // SRK/T formula constant
        AconstValue = testAconst(p, AconstTxt);

        if(!AconstValue.equals("")){
            return Double.parseDouble(AconstValue);
        }else{
            p = Pattern.compile("pACD: (.*)",Pattern.MULTILINE);    
            // Hoffer Q formula constant
            AconstValue = testAconst(p, AconstTxt);
            if(!AconstValue.equals("")){
                return Double.parseDouble(AconstValue);
            }else{
                p = Pattern.compile("A0: (.*)",Pattern.MULTILINE);
                // Haigis formula constants
                //!!!! TODO: need to work on Haigis here!!! As we have 3 constants and the text is 2 lines!!!
                if(!AconstValue.equals("")){
                    return Double.parseDouble(AconstValue);
                }else{
                    return 0.0;
                }
            }
        }
    }
    
    public void collectCalculationValuesPDFSide(PDPage page, String side) throws IOException{
        String mainFormulaName = "";
        String mainLensName = "";
        String FormulaName = "";
        String LensName = "";
        String CalcType = "";
        BiometrySide sideData;
        boolean isPageCalculation = false;
        
        if(side.equals("L")){
            sideData = parser.BiometryLeft;
        }else{
            sideData = parser.BiometryRight;
        }
        
        if(PDFHelper.getPageTitleIOLM700(page).equals("IOL calculation")){
            // single formula multi lens    
            CalcType = "F";
            mainFormulaName = getFormulaLensName(PDFHelper.getTopLensFormulaNameIOLM700(page), CalcType);
            isPageCalculation = true;
        }else if(PDFHelper.getPageTitleIOLM700(page).equals("IOL calculation (Multiformula)")){
            // multi formula singe lens
            CalcType = "L";
            mainLensName = getFormulaLensName(PDFHelper.getTopLensFormulaNameIOLM700(page), CalcType);
            isPageCalculation = true;
        }
        
        if(isPageCalculation){
            System.out.println("Page is calculation");
            for(int pos=1; pos< 5; pos++){
                String FormulaLens = PDFHelper.getMultiLensFormulaNamesIOLM700(page, side, pos);
                if(FormulaLens.length() > 2){
                    if(CalcType.equals("F")){
                        LensName = FormulaLens;
                        FormulaName = mainFormulaName;
                    }else{
                        FormulaName = FormulaLens;
                        LensName = mainLensName;
                    }
                    sideData.addCalculations();
                    sideData.setFormulaName(FormulaName, sideData.getMeasurementsIndex() );
                    sideData.setLensName(LensName, sideData.getMeasurementsIndex() );
                    String AconstTxt = PDFHelper.getMultiLensAValuesIOLM700(page, side, pos);
                    sideData.setLensAConst( getAconstValue(AconstTxt), sideData.getMeasurementsIndex());
                    for(int calc=0; calc<5; calc++){
                        sideData.setLensIOL(PDFHelper.getIOLREFValueRowIOLM700(page, side, pos, "IOL", calc), sideData.getMeasurementsIndex());
                        sideData.setLensREF(PDFHelper.getIOLREFValueRowIOLM700(page, side, pos, "REF", calc), sideData.getMeasurementsIndex());
                    }
                }
            }
        }
        
        
    }
    
    public void collectCalculationValuesSequenceSide(Attributes Attrs, String side, String sequenceTag){
        String LensName = "";
        String FormulaName = "";

        if(!sequenceTag.equals("771B1001")){
            // single formula, multi lense
            // TODO similar solution required as for the side!!!
            parser.Study.setSurgeonName(parser.getStringValueFromSequence(sequenceTag,"771B102C","",Attrs));
            Sequence innerSeq = Attrs.getSequence(parser.getTagInteger(sequenceTag));
            if(innerSeq != null && !innerSeq.isEmpty()){
                for(int is=0; is<innerSeq.size();is++){
                    Attributes innerAttrs = innerSeq.get(is);
                    //dumpDCMStructure(innerAttrs);
                    FormulaName = VR.PN.toStrings(innerAttrs.getValue(parser.getTagInteger("771B1009")), true, parser.CharacterSet).toString();
                    Sequence CalcSeq = innerAttrs.getSequence(parser.getTagInteger("771B1001"));
                    if(CalcSeq != null && !CalcSeq.isEmpty()){
                        for(int cs=0; cs<CalcSeq.size(); cs++){
                            //dumpDCMStructure(CalcSeq.get(cs));
                            collectCalculationValuesFromSeq(CalcSeq.get(cs), side, FormulaName, LensName);
                        }
                    }   
                }
            }
        }else{
            // single lens multi formula
            LensName = VR.PN.toStrings(Attrs.getValue(parser.getTagInteger("771B100A")), true, parser.CharacterSet).toString();
            Sequence CalcSeq = Attrs.getSequence(parser.getTagInteger("771B1001"));
            if(CalcSeq != null && !CalcSeq.isEmpty()){
                for(int cs=0; cs<CalcSeq.size(); cs++){
                    //dumpDCMStructure(CalcSeq.get(cs));
                    collectCalculationValuesFromSeq(CalcSeq.get(cs), side, FormulaName, LensName);
                }
            }
        }
    }

    public void collectCalculationValues(Attributes Attrs){
        // we can extract the calculation values from the encapsulated PDF file if that's exist
        
        if(Attrs.contains(parser.getTagInteger("00420011"))){
            byte[] pdfBytes;
            String fileType;
            String fileName;
            PDPage currentPage;
            
            try {
                this.PDFHelper = new PDFFunctions();
            } catch (IOException ex) {
                Logger.getLogger(DICOMIOLMaster700.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            try {
                pdfBytes = Attrs.getBytes(parser.getTagInteger("00420011"));
                fileType = Attrs.getString(parser.getTagInteger("00420012"));
                fileName = parser.inputFileName.substring(0, parser.inputFileName.length()-4);
                parser.saveBinaryDataToFile(pdfBytes, fileType, fileName);
                
                // loop through pages and extract calculation from each page
                PDFHelper.setPDFDoc(pdfBytes);
                int maxPageNum = PDFHelper.getMaxPageNum();
                
                for(int p=0; p<maxPageNum; p++){
                    currentPage = PDFHelper.getPDFPage(p);
                    collectCalculationValuesPDFSide(currentPage, "L");
                    collectCalculationValuesPDFSide(currentPage, "R");
                }
                
                // single formula multi lens format
                // search for formula name
                
                
            } catch (IOException ex) {
                Logger.getLogger(DICOMIOLMaster700.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }
    }
}