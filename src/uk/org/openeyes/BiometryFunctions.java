/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.org.openeyes;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.plaf.metal.MetalIconFactory;
import org.json.simple.parser.JSONParser;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import uk.org.openeyes.models.DicomEyeStatus;
import uk.org.openeyes.models.EtOphinbiometryCalculation;
import uk.org.openeyes.models.EtOphinbiometryIolRefValues;
import uk.org.openeyes.models.EtOphinbiometryMeasurement;
import uk.org.openeyes.models.EtOphinbiometrySelection;
import uk.org.openeyes.models.Event;
import uk.org.openeyes.models.Eye;
import uk.org.openeyes.models.OphinbiometryCalculationFormula;
import uk.org.openeyes.models.OphinbiometryImportedEvents;
import uk.org.openeyes.models.OphinbiometryLenstypeLens;
import uk.org.openeyes.models.OphinbiometrySurgeon;
import uk.org.openeyes.models.User;

/**
 *
 * @author VEDELEKT
 */
public class BiometryFunctions extends DatabaseFunctions{

    private DICOMLogger dicomLogger;
    
    public BiometryFunctions(DICOMLogger SystemLogger){
        this.dicomLogger = SystemLogger;
        
    }
    
    /**
     *
     * @param lensName the value of lensName
     * @param aConst the value of aConst
     */
    private OphinbiometryLenstypeLens searchForLensData(String lensName, Double aConst) {
        OphinbiometryLenstypeLens lensType = null;
        Criteria crit = session.createCriteria(OphinbiometryLenstypeLens.class);
        crit.add(Restrictions.eq("name", lensName));
        if (!crit.list().isEmpty()) {
            if (crit.list().get(0) != null) {
                lensType = (OphinbiometryLenstypeLens) crit.list().get(0);
            }
        }
        if (lensType == null) {
            
            lensType = new OphinbiometryLenstypeLens();
            lensType.setName(lensName);
            lensType.setAcon(BigDecimal.valueOf(aConst));
            User selectedUser = searchStudyUser("");
            lensType.setCreatedUserId(selectedUser);
            lensType.setLastModifiedUserId(selectedUser);
            lensType.setCreatedDate(new Date());
            lensType.setLastModifiedDate(new Date());
            lensType.setDescription("(Created by IOL Master input)");
            lensType.setDisplayOrder(0);
            lensType.setDeleted(false);
            lensType.setActive(true);
            lensType.setComments("Imported values, please check! Remove this comment when confirmed!");
            lensType.setPositionId(0);
            session.save(lensType);
        }
        return lensType;
    }

    /**
     *
     * @param formulaName the value of formulaName
     */
    private OphinbiometryCalculationFormula searchForFormulaData(String formulaName) {
        OphinbiometryCalculationFormula formulaType = null;
        Criteria crit = session.createCriteria(OphinbiometryCalculationFormula.class);
        crit.add(Restrictions.eq("name", formulaName));
        if (!crit.list().isEmpty()) {
            if (crit.list().get(0) != null) {
                formulaType = (OphinbiometryCalculationFormula) crit.list().get(0);
            }
        } else {
            Criteria crit2 = session.createCriteria(OphinbiometryCalculationFormula.class);
            crit2.add(Restrictions.eq("name", formulaName.replace("\u00ae", "")));
            if (!crit2.list().isEmpty()) {
                if (crit2.list().get(0) != null) {
                    formulaType = (OphinbiometryCalculationFormula) crit2.list().get(0);
                }
            }
        }
        if (formulaType == null) {
            formulaType = new OphinbiometryCalculationFormula();
            formulaType.setName(formulaName);
            User selectedUser = searchStudyUser("");
            formulaType.setCreatedUserId(selectedUser);
            formulaType.setLastModifiedUserId(selectedUser);
            formulaType.setCreatedDate(new Date());
            formulaType.setLastModifiedDate(new Date());
            formulaType.setDisplayOrder(0);
            formulaType.setDeleted(false);
            session.save(formulaType);
        }
        return formulaType;
    }

    private BiometrySide setEmptySideData(){
        BiometrySide sideData = new BiometrySide();
        sideData.setK1(0.0);
        sideData.setK2(0.0);
        sideData.setAxisK1(0.0);
        sideData.setAL(0.0); 
        sideData.setSNR(0.0);       
        return  sideData;
    }
    
    /**
     *
     * @param basicMeasurementData the value of basicMeasurementData
     */
    private void setMeasurementData(EtOphinbiometryMeasurement basicMeasurementData) {
        BiometrySide sideData;
        BigDecimal SNR = BigDecimal.ZERO;
        
        if (basicMeasurementData.getEventId() == null) {
            basicMeasurementData.setEventId(importedBiometryEvent.getEventId());
            basicMeasurementData.setEyeId(new Eye(eventBiometry.getEyeId()));
            basicMeasurementData.setCreatedDate(new Date());
            basicMeasurementData.setCreatedUserId(selectedUser);
            basicMeasurementData.setLastModifiedDate(new Date());
            basicMeasurementData.setLastModifiedUserId(selectedUser);
        }
        
        // saving left side
        sideData = eventBiometry.getBiometryValue("L");
        if(sideData == null){
            sideData = setEmptySideData();
        }
        basicMeasurementData.setK1Left(BigDecimal.valueOf(sideData.getK1()));
        basicMeasurementData.setK2Left(BigDecimal.valueOf(sideData.getK2()));
        basicMeasurementData.setAxisK1Left(BigDecimal.valueOf(sideData.getAxisK1()));
        basicMeasurementData.setAxialLengthLeft(BigDecimal.valueOf(sideData.getAL()));
        SNR = sideData.getSNR();
        if(SNR == null){
            SNR = BigDecimal.ZERO;
        }
        basicMeasurementData.setSnrLeft(SNR);
        basicMeasurementData.setSnrMinLeft(sideData.getSNRMin());
        basicMeasurementData.setK2AxisLeft(BigDecimal.valueOf(sideData.getAxisK2()));
        Double currentDeltaK = sideData.getDeltaK();
        
        // in the device is an IOLMaster 500 than we make sure that the value is negative
        // Based on documentation: Manufacturer’s model name of the equipment that produced the composite instances. Always “IOLMaster 500”.
        if(eventStudy.getDeviceModel().equals("IOLMaster 500") && currentDeltaK > 0){
            currentDeltaK = -1 * currentDeltaK;
        }
        basicMeasurementData.setDeltaKLeft(BigDecimal.valueOf(currentDeltaK));
        basicMeasurementData.setDeltaKAxisLeft(BigDecimal.valueOf(sideData.getDeltaKAxis()));
        basicMeasurementData.setAcdLeft(BigDecimal.valueOf(sideData.getACD()));
        basicMeasurementData.setRefractionSphereLeft(BigDecimal.valueOf(sideData.getRefractionSphere()));
        basicMeasurementData.setRefractionDeltaLeft(BigDecimal.valueOf(sideData.getRefractionDelta()));
        basicMeasurementData.setRefractionAxisLeft(BigDecimal.valueOf(sideData.getRefractionAxis()));
        DicomEyeStatus eyeStatusLeft = new DicomEyeStatus(sideData.getEyeStatus());
        basicMeasurementData.setEyeStatusLeft(eyeStatusLeft);
        basicMeasurementData.setKModifiedLeft(sideData.getisKModified());
        basicMeasurementData.setAlModifiedLeft(sideData.getisALModified());
        
        // saving right side
        sideData = eventBiometry.getBiometryValue("R");
        if(sideData == null){
            sideData = setEmptySideData();
        }
        basicMeasurementData.setK1Right(BigDecimal.valueOf(sideData.getK1()));
        basicMeasurementData.setK2Right(BigDecimal.valueOf(sideData.getK2()));
        basicMeasurementData.setAxisK1Right(BigDecimal.valueOf(sideData.getAxisK1()));
        basicMeasurementData.setAxialLengthRight(BigDecimal.valueOf(sideData.getAL()));
        SNR = sideData.getSNR();
        if(SNR == null){
            SNR = BigDecimal.ZERO;
        }
        basicMeasurementData.setSnrRight(SNR);
        basicMeasurementData.setSnrMinRight(sideData.getSNRMin());
        basicMeasurementData.setK2AxisRight(BigDecimal.valueOf(sideData.getAxisK2()));
        currentDeltaK = sideData.getDeltaK();
        
        // in the device is an IOLMaster 500 than we make sure that the value is negative
        // Based on documentation: Manufacturer’s model name of the equipment that produced the composite instances. Always “IOLMaster 500”.
        if(eventStudy.getDeviceModel().equals("IOLMaster 500") && currentDeltaK > 0){
            currentDeltaK = -1 * currentDeltaK;
        }
        basicMeasurementData.setDeltaKRight(BigDecimal.valueOf(currentDeltaK));
        basicMeasurementData.setDeltaKAxisRight(BigDecimal.valueOf(sideData.getDeltaKAxis()));
        basicMeasurementData.setAcdRight(BigDecimal.valueOf(sideData.getACD()));
        basicMeasurementData.setRefractionSphereRight(BigDecimal.valueOf(sideData.getRefractionSphere()));
        basicMeasurementData.setRefractionDeltaRight(BigDecimal.valueOf(sideData.getRefractionDelta()));
        basicMeasurementData.setRefractionAxisRight(BigDecimal.valueOf(sideData.getRefractionAxis()));
        DicomEyeStatus eyeStatusRight = new DicomEyeStatus(sideData.getEyeStatus());
        basicMeasurementData.setEyeStatusRight(eyeStatusRight);
        basicMeasurementData.setKModifiedRight(sideData.getisKModified());
        basicMeasurementData.setAlModifiedRight(sideData.getisALModified());
    }

    /**
     *
     */
    private void createSelectionData() {
        EtOphinbiometrySelection newBasicSelectionData = new EtOphinbiometrySelection();
        newBasicSelectionData.setCreatedDate(new Date());
        newBasicSelectionData.setLastModifiedDate(new Date());
        newBasicSelectionData.setCreatedUserId(selectedUser);
        newBasicSelectionData.setLastModifiedUserId(selectedUser);
        newBasicSelectionData.setEventId(importedBiometryEvent.getEventId());
        newBasicSelectionData.setEyeId(new Eye(eventBiometry.getEyeId()));
        newBasicSelectionData.setIolPowerLeft(BigDecimal.ZERO);
        newBasicSelectionData.setIolPowerRight(BigDecimal.ZERO);
        newBasicSelectionData.setPredictedRefractionLeft(BigDecimal.ZERO);
        newBasicSelectionData.setPredictedRefractionRight(BigDecimal.ZERO);
        session.save(newBasicSelectionData);
    }

    /**
     *
     */
    private void createCalculationData() {
        EtOphinbiometryCalculation newBasicCalculationData = new EtOphinbiometryCalculation();
        newBasicCalculationData.setCreatedDate(new Date());
        newBasicCalculationData.setLastModifiedDate(new Date());
        newBasicCalculationData.setCreatedUserId(selectedUser);
        newBasicCalculationData.setLastModifiedUserId(selectedUser);
        newBasicCalculationData.setEventId(importedBiometryEvent.getEventId());
        newBasicCalculationData.setEyeId(new Eye(eventBiometry.getEyeId()));
        newBasicCalculationData.setFormulaIdLeft(new OphinbiometryCalculationFormula(1));
        newBasicCalculationData.setFormulaIdRight(new OphinbiometryCalculationFormula(1));
        newBasicCalculationData.setTargetRefractionLeft(BigDecimal.valueOf(eventBiometry.getBiometryValue("L").getTargetRef()));
        newBasicCalculationData.setTargetRefractionRight(BigDecimal.valueOf(eventBiometry.getBiometryValue("R").getTargetRef()));
        newBasicCalculationData.setComments(eventStudy.getComments() );
        session.save(newBasicCalculationData);
        addVersionTableData(newBasicCalculationData, newBasicCalculationData.getId());

    }

    private JSONObject decodeJSONData(String IOLJSON){
        JSONParser parser=new JSONParser();
        JSONObject dataObj = null;
        try {
            dataObj = (JSONObject)parser.parse(IOLJSON);
            return dataObj;
            //JSONArray dataArray=(JSONArray)dataObj;
            //return dataArray;
        } catch (org.json.simple.parser.ParseException ex) {
            Logger.getLogger(BiometryFunctions.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
    
    private String mergeIolRefValues(EtOphinbiometryIolRefValues currentIolRefValues, String newIolRefValues, String side){
        String IOLJSON = "";
        if(side.equals("L")){
            IOLJSON = currentIolRefValues.getIolRefValuesLeft();    
        }else if(side.equals("R")){
            IOLJSON = currentIolRefValues.getIolRefValuesRight();    
        }
        if(IOLJSON == null){
            return newIolRefValues;
        }else{
            JSONObject currentIOLRefData = decodeJSONData(IOLJSON);
            JSONObject newIOLRefData = decodeJSONData(newIolRefValues);
                        
            // merge logic based on IOL values
            JSONArray currentIOLArray = (JSONArray) currentIOLRefData.get("IOL");
            JSONArray currentREFArray = (JSONArray) currentIOLRefData.get("REF");
            JSONArray newIOLArray = (JSONArray) newIOLRefData.get("IOL");
            JSONArray newREFArray = (JSONArray) newIOLRefData.get("REF");
            
            JSONObject mergedIOLRefData = new JSONObject();
           
            for(Integer i=0; i<newIOLArray.size(); i++){
                if(!currentIOLArray.contains(newIOLArray.get(i))){
                    currentIOLArray.add(newIOLArray.get(i));
                    currentREFArray.add(newREFArray.get(i));
                }
            }
            mergedIOLRefData.put("IOL", currentIOLArray);
            mergedIOLRefData.put("REF", currentREFArray);
        
            return mergedIOLRefData.toJSONString();
        }
    }
    
    private EtOphinbiometryIolRefValues searchCurrentIolRefValues( OphinbiometryLenstypeLens lensType, 
                                        OphinbiometryCalculationFormula formulaType){
        // we need to check if there is a saved value in the database now, and merge the new values to the existing records
        if(importedBiometryEvent.getEventId() != null){
            Criteria crit = getSession().createCriteria(EtOphinbiometryIolRefValues.class);
        
            crit.add(Restrictions.eq("eventId",importedBiometryEvent.getEventId()));
            crit.add(Restrictions.eq("lensId", lensType));
            crit.add(Restrictions.eq("formulaId", formulaType));
            List iolDataList = crit.list();
            if(!iolDataList.isEmpty()){
                EtOphinbiometryIolRefValues currentIOLValues = (EtOphinbiometryIolRefValues) iolDataList.get(0);
                return currentIOLValues;
            }else{
                return null;
            }
        }else{
            return null;
        }
    }
    
    private OphinbiometrySurgeon searchSurgeon(String surgeonName){
        OphinbiometrySurgeon surgeonData = null;
        Criteria crit = getSession().createCriteria(OphinbiometrySurgeon.class); 
        crit.add(Restrictions.eq("name", surgeonName));
        List surgeons = crit.list();
        dicomLogger.addToRawOutput("Searching for surgeon "+surgeonName+"...");

        if(surgeons.isEmpty()){
            surgeonData = new OphinbiometrySurgeon();
            surgeonData.setName(surgeonName);
            session.save(surgeonData);
            dicomLogger.addToRawOutput("New surgeon has been created...");    
        }else{
            surgeonData = (OphinbiometrySurgeon) surgeons.get(0);
            dicomLogger.addToRawOutput("Surgeon is already exists, using exiting data...");    
        }
        return surgeonData;
    }
    
    private boolean isDataModified(){
        return eventBiometry.getBiometryValue("L").getisACDModified() || 
               eventBiometry.getBiometryValue("L").getisALModified() ||
               eventBiometry.getBiometryValue("L").getisKModified() ||
               eventBiometry.getBiometryValue("R").getisACDModified() || 
               eventBiometry.getBiometryValue("R").getisALModified() ||
               eventBiometry.getBiometryValue("R").getisKModified();
    }
    
    /**
     *
     */
    private void saveIolRefValues() {
        
        ArrayList<BiometryMeasurementData> storedBiometryMeasurementDataLeft = eventBiometry.getBiometryValue("L").getMeasurements();
        ArrayList<BiometryMeasurementData> storedBiometryMeasurementDataRight = eventBiometry.getBiometryValue("R").getMeasurements();
        Integer ArrayListSize;
        String ReferenceSide;
        
        // we need to find which side contains more data
        if (storedBiometryMeasurementDataLeft.size() > storedBiometryMeasurementDataRight.size()) {
            ArrayListSize = storedBiometryMeasurementDataLeft.size();
            ReferenceSide = "L";
        } else {
            ArrayListSize = storedBiometryMeasurementDataRight.size();
            ReferenceSide = "R";
        }
        OphinbiometryLenstypeLens lensType = null;
        OphinbiometryCalculationFormula formulaType = null;
        for (Integer i = 0; i < ArrayListSize; i++) {
            BiometryMeasurementData rowData;
            if (ReferenceSide.equals("L")) {
                rowData = storedBiometryMeasurementDataLeft.get(i);
            } else {
                rowData = storedBiometryMeasurementDataRight.get(i);
            }
            
            // TODO: what is the A constant and emmetropia value here??
            lensType = searchForLensData(rowData.getLensName(), rowData.getAConst());
            formulaType = searchForFormulaData(rowData.getFormulaName());
            
            // we search for current values
            EtOphinbiometryIolRefValues iolRefValues = null;
            iolRefValues = searchCurrentIolRefValues(lensType, formulaType);    
            
            if(isDataModified()){
                if(iolRefValues != null){
                    iolRefValues.setActive(false);
                    session.saveOrUpdate(iolRefValues);
                    iolRefValues = null;
                }
                dicomLogger.addToRawOutput("Measurement data modified manually (AL, K or ACD values), creating new IOL REF record...");
            }

            boolean isNewIolRefValues = false;
            if( iolRefValues == null){
                isNewIolRefValues = true;
                iolRefValues = new EtOphinbiometryIolRefValues();
            }

            iolRefValues.setCreatedUserId(selectedUser);
            iolRefValues.setLastModifiedUserId(selectedUser);
            iolRefValues.setCreatedDate(new Date());
            iolRefValues.setLastModifiedDate(new Date());
            iolRefValues.setEventId(importedBiometryEvent.getEventId());
            iolRefValues.setEyeId(new Eye(eventBiometry.getEyeId()));
            iolRefValues.setFormulaId(formulaType);
            iolRefValues.setLensId(lensType);
            iolRefValues.setConstant(BigDecimal.valueOf(rowData.getAConst()));
            iolRefValues.setSurgeonId(searchSurgeon(eventStudy.getSurgeonName()));
            if (ReferenceSide.equals("L")) {
                if(isNewIolRefValues){
                    iolRefValues.setIolRefValuesLeft(rowData.getIOLREFJSON());
                }else{
                    iolRefValues.setIolRefValuesLeft(mergeIolRefValues(iolRefValues, rowData.getIOLREFJSON(), "L" ));
                }
                iolRefValues.setEmmetropiaLeft(BigDecimal.valueOf(rowData.getEmmetropia()));
                if (storedBiometryMeasurementDataLeft.size() == storedBiometryMeasurementDataRight.size()) {
                    if(isNewIolRefValues){    
                        iolRefValues.setIolRefValuesRight(storedBiometryMeasurementDataRight.get(i).getIOLREFJSON());
                    }else{
                        iolRefValues.setIolRefValuesRight(mergeIolRefValues(iolRefValues, storedBiometryMeasurementDataRight.get(i).getIOLREFJSON(),"R"));
                    }
                    iolRefValues.setEmmetropiaRight(BigDecimal.valueOf(storedBiometryMeasurementDataRight.get(i).getEmmetropia()));
                }
            } else {
                if(isNewIolRefValues){
                    iolRefValues.setIolRefValuesRight(rowData.getIOLREFJSON());
                }else{
                    iolRefValues.setIolRefValuesRight(mergeIolRefValues(iolRefValues, rowData.getIOLREFJSON(), "R" ));
                }
                iolRefValues.setEmmetropiaRight(BigDecimal.valueOf(rowData.getEmmetropia()));
                if (storedBiometryMeasurementDataLeft.size() == storedBiometryMeasurementDataRight.size()) {
                    if(isNewIolRefValues){
                        iolRefValues.setIolRefValuesLeft(storedBiometryMeasurementDataLeft.get(i).getIOLREFJSON());
                    }else{
                        iolRefValues.setIolRefValuesLeft(mergeIolRefValues(iolRefValues, storedBiometryMeasurementDataLeft.get(i).getIOLREFJSON(),"L"));
                    }
                    iolRefValues.setEmmetropiaLeft(BigDecimal.valueOf(storedBiometryMeasurementDataLeft.get(i).getEmmetropia()));
                }
            }
            session.saveOrUpdate(iolRefValues);

            addVersionTableData(iolRefValues, iolRefValues.getId());

            formulaType = null;
            lensType = null;
        }
    }

    private EtOphinbiometryMeasurement getMeasurementId(){
        Criteria crit = getSession().createCriteria(EtOphinbiometryMeasurement.class);

        crit.add(Restrictions.eq("eventId", importedBiometryEvent.getEventId()));
        EtOphinbiometryMeasurement currentMeasurement = (EtOphinbiometryMeasurement) crit.list().get(0);        
        return currentMeasurement;
    }
    
    /**
     *
     * @param IOLStudy the value of IOLStudy
     * @param IOLBiometry the value of IOLBiometry
     */
    public void processBiometryEvent(StudyData IOLStudy, BiometryData IOLBiometry) throws ParseException {

        setEventStudy(IOLStudy);
        //System.out.println("Study data has been set successfully");
        dicomLogger.addToRawOutput("Study data has been set successfully");
        setEventBiometry(IOLBiometry);
        //System.out.println("Biometry data set successfully");
        dicomLogger.addToRawOutput("Biometry data has been set successfully");
        this.setSelectedUser();
        //System.out.println("User selected successfully");
        dicomLogger.addToRawOutput("User data has been set successfully");

        selectActiveEpisode();
        importedBiometryEvent = processImportedEvent();
        EtOphinbiometryMeasurement basicMeasurementData;
        if (isNewEvent) {
            basicMeasurementData = new EtOphinbiometryMeasurement();
            this.createSelectionData();
            this.createCalculationData();
        }else{
            basicMeasurementData = getMeasurementId();
        }

        setMeasurementData(basicMeasurementData);
        //databaseFunctions.session.merge(basicMeasurementData);
        session.saveOrUpdate(basicMeasurementData);
        
        addVersionTableData(basicMeasurementData, basicMeasurementData.getId());

        this.saveIolRefValues();
        
        // we save the log entry for the import
        dicomLogger.getLogger().setStudyInstanceId(eventStudy.getStudyInstanceID());
        dicomLogger.getLogger().setSeriesInstanceId(eventStudy.getSeriesInstanceID());
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
        dicomLogger.getLogger().setStudyDatetime(df.parse(getStudyYMD(eventStudy.getStudyDateTime())));
        dicomLogger.getLogger().setStudyLocation(eventStudy.getInstituionName());
        dicomLogger.getLogger().setStationId(eventStudy.getStationName());
        dicomLogger.getLogger().setMachineManufacturer(eventStudy.getDeviceManufacturer());
        dicomLogger.getLogger().setMachineModel(eventStudy.getDeviceModel());
        dicomLogger.getLogger().setMachineSoftwareVersion(eventStudy.getDeviceSoftwareVersion());
        dicomLogger.getLogger().setReportType("biometry");
        dicomLogger.getLogger().setPatientNumber(selectedPatient.getHosNum());
        dicomLogger.getLogger().setImportDatetime(new Date());
        dicomLogger.getLogger().setImportType("F");

    }

    
    /**
     *
     *
     * @return OphinbiometryImportedEvents
     */
    private OphinbiometryImportedEvents processImportedEvent() {
        OphinbiometryImportedEvents importedEvent = null;
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
        Criteria currentEvent = session.createCriteria(OphinbiometryImportedEvents.class);
        currentEvent.add(Restrictions.eq("studyId", eventStudy.getStudyInstanceID()));
        currentEvent.add(Restrictions.eq("seriesId", eventStudy.getSeriesInstanceID()));
        currentEvent.add(Restrictions.eq("surgeonName", eventStudy.getSurgeonName()));
        
        // we should check if event is deleted, and we should create a new one if yes
        currentEvent.add(Restrictions.sqlRestriction("event_id = (SELECT max(event_id) FROM ophinbiometry_imported_events WHERE study_id='"+eventStudy.getStudyInstanceID()+"' AND series_id='"+eventStudy.getSeriesInstanceID()+"')"));        
        if (!currentEvent.list().isEmpty()) {
            importedEvent = (OphinbiometryImportedEvents) currentEvent.list().get(0);
            // if the event is in deleted state we cerate a new record
            if(importedEvent.getEventId().getDeleted() == 0){
                dicomLogger.addToRawOutput("StudyID exists in database, merging with existing event...");

                isNewEvent = false;
                // we decide if the imported file content time is newer then the stored content date
                if(importedEvent.getContentDateTime() != null){
                    Calendar lastContentDateTime = new GregorianCalendar();
                    Calendar studyContentDateTime = new GregorianCalendar();
                    
                    try {
                        lastContentDateTime.setTime(df.parse(getSQLFormattedDate(importedEvent.getContentDateTime())));
                        studyContentDateTime.setTime(df.parse(getSQLFormattedDate(eventStudy.getContentDateTime().getTime())));
                    } catch (ParseException ex) {
                        Logger.getLogger(BiometryFunctions.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    
                    if(studyContentDateTime.after(lastContentDateTime)){
                        try {
                            importedEvent.setContentDateTime(df.parse(getSQLFormattedDate(eventStudy.getContentDateTime().getTime())));
                        }catch (ParseException ex) {
                            ex.printStackTrace();
                        }
                        importedEvent.setIsMerged(true);
                        dicomLogger.addToRawOutput("Event content date and time is newer then the stored values, updating existing measuerements...");
                    }
                }
            }else{
                isNewEvent = true;
            }
        } 

        if(isNewEvent){
            Event newEvent = createNewEvent();
            importedEvent = new OphinbiometryImportedEvents();
            importedEvent.setDeviceName(eventStudy.getInstituionName());
            importedEvent.setDeviceId(eventStudy.getStationName());
            importedEvent.setDeviceManufacturer(eventStudy.getDeviceManufacturer());
            importedEvent.setDeviceModel(eventStudy.getDeviceModel());
            importedEvent.setDeviceSoftwareVersion(eventStudy.getDeviceSoftwareVersion());
            importedEvent.setStudyId(eventStudy.getStudyInstanceID());
            importedEvent.setSeriesId(eventStudy.getSeriesInstanceID());
            importedEvent.setPatientId(getSelectedPatient());
            importedEvent.setSurgeonName(eventStudy.getSurgeonName());
            try {
                importedEvent.setContentDateTime(df.parse(getSQLFormattedDate(eventStudy.getContentDateTime().getTime())));
            } catch (ParseException ex) {
                ex.printStackTrace();
            }
            
            importedEvent.setEventId(newEvent);
            importedEvent.setCreatedDate(new Date());
            importedEvent.setLastModifiedDate(new Date());
            importedEvent.setCreatedUserId(selectedUser);
            importedEvent.setLastModifiedUserId(selectedUser);
            boolean isLinked = false;
            if (getSelectedEpisode() != null) {
                isLinked = true;
            }
            importedEvent.setIsLinked(isLinked);
            importedEvent.setIsMerged(false);
        }
        session.saveOrUpdate(importedEvent);
        return importedEvent;
    }
    
    protected String getCalculatedValues(String formulaName, BiometryLensData lens, BiometrySide sideData){
        double IOLPower;
        double closestIOLPower;
        double refraction;
        Method calculateMethod = null;
        
        try {
            if(formulaName.equals("Haigis suite")){
                // will call Haigis calculation Here
                calculateMethod = this.getClass().getMethod("calculateHaigis", double.class, double.class, double.class, double.class, BiometryLensData.class, double.class, String.class);
            }else if(formulaName.equals("SRK/T")){
                calculateMethod = this.getClass().getMethod("calculateSRKT", double.class, double.class, double.class, double.class, BiometryLensData.class, double.class, String.class);
            }else if(formulaName.equals("HofferQ")){
                calculateMethod = this.getClass().getMethod("calculateHofferQ", double.class, double.class, double.class, double.class, BiometryLensData.class, double.class, String.class);
            }
            IOLPower = (double) calculateMethod.invoke(this, sideData.getAL(), sideData.getK1(), sideData.getK2(), sideData.getACD(), lens, sideData.getTargetRef(), "IOL");
            
            // Select IOL that gives power nearest to target refraction
            
            double roundDownIOLPower = Math.floor(IOLPower * 2)/2;
            double nextUpIOLPower = roundDownIOLPower + 0.5;
            double roundDownRefraction = (double) calculateMethod.invoke(this, sideData.getAL(), sideData.getK1(), sideData.getK2(), sideData.getACD(), lens, roundDownIOLPower, "REF");
            double nextUpRefraction = (double) calculateMethod.invoke(this, sideData.getAL(), sideData.getK1(), sideData.getK2(), sideData.getACD(), lens, nextUpIOLPower, "REF");
            if (Math.abs(sideData.getTargetRef() - roundDownRefraction) < Math.abs(sideData.getTargetRef() - nextUpRefraction)) {
                    closestIOLPower = roundDownIOLPower;
            }
            else {
                    closestIOLPower = nextUpIOLPower;
            }

            // Produce results for a range of refraction around this one, starting two 0.5D less powerful
            double startPower = closestIOLPower - 1.0;
            for (int i = 0; i < 5; i++)
            {
                    refraction = (double) calculateMethod.invoke(this, sideData.getAL(), sideData.getK1(), sideData.getK2(), sideData.getACD(), lens, startPower, "REF");

                    // need to add values to the check object here!
                    
                    startPower = startPower + 0.5;
            }
            

        } catch (NoSuchMethodException ex) {
                Logger.getLogger(BiometryFunctions.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
                Logger.getLogger(BiometryFunctions.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(BiometryFunctions.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(BiometryFunctions.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvocationTargetException ex) {
            Logger.getLogger(BiometryFunctions.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return "";
    }
    
    /**
     *
     * @param axialLength   -- Axial length
     * @param r1            -- Radius of curvature 1
     * @param r2            -- Radius of curvature 2
     * @param acd           -- Optical anterior chamber depth
     * @param lens          -- lens object containing IOL data
     * @param dioptresRefraction  -- Target refraction or power of IOL
     * @param resultType    -- Result is either IOL power (IOL) or predicted refraction (REF)
     * @return              -- Refractive power in Dioptres
     */
    protected double calculateSRKT(double axialLength, double r1, double r2, double acd, BiometryLensData lens, double dioptresRefraction, String resultType){
        // Constants
    	double n =  1.3375;			// Refractive index of cornea with fudge factor for converting radius of curvature to dioptric power
	double nc = 1.333;			// Refractive index of the cornea
	double na = 1.336;			// Refractive index of aqueous and vitreous
	double vd = 12.0;			// Vertex distance
        String calculationComments = "SRK/T formula calculation has been called\n";        // comments for debug
        double returnPower;                     // the return value
        
        // Calculate average radius of curvature and corneal power in dioptres
	double averageRadius = (r1 + r2) / 2;
	double dioptresCornea = (n - 1) * 1000 / averageRadius;
    
        // Difference in refractive indices (NB uses different value of n here)
        double diffRI = nc - 1;

        double retinalThickness = 0.65696 - 0.02029 * axialLength;
        double opticalAxialLength = axialLength + retinalThickness;

        // 'A' constant correction
        double aconstant;
        if (lens.aConst > 100)
        {
            aconstant = lens.aConst * 0.62467 - 68 - 0.74709;
            calculationComments += "A-constant correction applied\n";
        }
        else
        {
            aconstant = lens.aConst;
        }

        // Difference between natural lens and IOL to cornea
        double diff = aconstant - 3.3357;

        // Axial length correction for high myopes
        double correctedAxialLength;
        if (axialLength > 24.2)
        {
            // Value of 1.716 (as in original SRK/T paper) gives identical results to IOLMaster. Using 1.715 as in erratum gives slightly different results
            correctedAxialLength = -3.446 + 1.716 * axialLength - 0.0237 * axialLength * axialLength;
            //axialLength = -3.446 + 1.715 * _axialLength - 0.0237 * _axialLength * _axialLength;
            calculationComments += "Axial length correction applied\n";
        }
        else
        {
            correctedAxialLength = axialLength;
        }        
                
        // Corneal width
        double cornealWidth = -5.40948 + 0.58412 * correctedAxialLength + 0.098 * dioptresCornea;

        // Corneal dome height (check for negative result here before taking square root)
        double cornealDomeHeight;
        if (averageRadius * averageRadius - cornealWidth * cornealWidth / 4 > 0) {
            cornealDomeHeight = averageRadius - Math.sqrt(averageRadius * averageRadius - cornealWidth * cornealWidth / 4);
        }
        else {
            calculationComments += "Negative square root for corneal dome height\n";
            cornealDomeHeight = averageRadius;
        }
        if (cornealDomeHeight > 5.5) {
            cornealDomeHeight = 5.5;
            calculationComments += "Corneal dome height capped at 5.5\n";
        }
        
        // Post-op anterior chamber depth
        double postopACDepth = cornealDomeHeight + diff;
        double numerator, denominator;
        
        // IOL power - we use this to determine the start value of IOL power
        if (resultType.equals("IOL")) {
            numerator = 1000 * na * (na * averageRadius - diffRI * opticalAxialLength - 0.001 * dioptresRefraction * (vd * (na * averageRadius - diffRI * opticalAxialLength) + opticalAxialLength * averageRadius));
            denominator = (opticalAxialLength - postopACDepth) * (na * averageRadius - diffRI * postopACDepth - 0.001 * dioptresRefraction * (vd * (na * averageRadius - diffRI * postopACDepth) + postopACDepth * averageRadius));
            returnPower = numerator/denominator;
        }
        // Predicted refraction
        else {
            numerator = 1000 * na * (na * averageRadius - diffRI * opticalAxialLength) - dioptresRefraction * (opticalAxialLength - postopACDepth) * (na * averageRadius - diffRI * postopACDepth);
            denominator = (na * (vd * (na * averageRadius - diffRI * opticalAxialLength) + opticalAxialLength * averageRadius) - 0.001 * dioptresRefraction * (opticalAxialLength - postopACDepth) * (vd * (na * averageRadius - diffRI * postopACDepth) + postopACDepth * averageRadius));
            returnPower = numerator/denominator;				
        }
        
        dicomLogger.addToRawOutput(calculationComments);
        return returnPower;
    }

    /**
     *
     * @param axialLength   -- Axial length
     * @param r1            -- Radius of curvature 1
     * @param r2            -- Radius of curvature 2
     * @param acd           -- Optical anterior chamber depth
     * @param lens          -- lens object containing IOL data
     * @param dioptresRefraction  -- Target refraction or power of IOL
     * @param resultType    -- Result is either IOL power (IOL) or predicted refraction (REF)
     * @return              -- Refractive power in Dioptres
     */    
    protected double calculateHofferQ(double axialLength, double r1, double r2, double acd, BiometryLensData lens, double dioptresRefraction, String resultType){
        // Constants
        double n = 1.3375;			// Refractive index of cornea with fudge factor for converting radius of curvature to dioptric power
        double vd =12.0;			// Vertex distance
        String calculationComments = "HofferQ formula calculation has been called\n";        // comments for debug
        double returnPower;                     // the return value
        
        // Calculate average radius of curvature and corneal power in dioptres
        double averageRadius = (r1 + r2) / 2;
        double dioptresCornea = (n - 1) * 1000 / averageRadius;

        // Calculate refractive error at corneal plane
        double R = dioptresRefraction / (1 - vd * dioptresRefraction/1000);
        
        // Hoffer's factors
        double M, G;
        if (axialLength <= 23 ) {
            M = +1;
            G = +28;
            calculationComments += "Hoffer factors for AL <= 23 applied\n";
        }
        else {
            M = -1;
            G = +23.5;
            calculationComments += "Hoffer factors for AL > 23 applied</br>";
        }
                
        // Constrain axial length (NB used ONLY for ACD calculation and replaces ACD constraint as described in erratum)
        double AL = axialLength;
        if (AL > 31) {
            AL = 31;
            calculationComments += "Axial length constrained down to 31</br>";
        }
        if (AL < 18.5) {
            AL = 18.5;
            calculationComments += "Axial length constrained up to 18.5</br>";
        }
        
        // Predicted ACD 
        double ACD = lens.pACDConst
                + 0.3 * (AL - 23.5)
                + Math.tan(dioptresCornea * Math.PI/180) * Math.tan(dioptresCornea * Math.PI/180)
                + 0.1 * M * (23.5 - AL) * (23.5 - AL) * Math.tan(Math.PI * (0.1 * (G - AL) * (G - AL))/180)
                - 0.99166;
	
        // IOL power
        // TODO: need to check if we need to use the original Axial Lenght here or the constrained AL?
        if (resultType.equals("IOL")) {				
            returnPower = 1336/(axialLength - ACD - 0.05) - 1.336/((1.336/(dioptresCornea + R)) - (ACD + 0.05)/1000);
        }
        // Predicted refraction
        else {
            R = (1.336/(1.336/(1336/(axialLength - ACD - 0.05) - dioptresRefraction)+ (ACD + 0.05)/1000)) - dioptresCornea;
            returnPower = R/(1 + vd * R/1000);
        }
        
        return returnPower;
    }
    
     /**
     *
     * @param axialLength   -- Axial length
     * @param r1            -- Radius of curvature 1
     * @param r2            -- Radius of curvature 2
     * @param acd           -- Optical anterior chamber depth
     * @param lens          -- lens object containing IOL data
     * @param dioptresRefraction  -- Target refraction or power of IOL
     * @param resultType    -- Result is either IOL power (IOL) or predicted refraction (REF)
     * @return              -- Refractive power in Dioptres
     */
    protected double calculateHaigis(double axialLength, double r1, double r2, double acd, BiometryLensData lens, double dioptresRefraction, String resultType){
        // TODO: implement this! :)
        double n = 1.3315;			// Refractive index of cornea with fudge factor for converting radius of curvature to dioptric power
        double na =1.336;			// Refractive index of aqueous and vitreous
        double vd =12.0;			// Vertex distance
        double returnPower;                     // the return value

        // Calculate average radius of curvature and corneal power in dioptres
        double averageRadius = (r1 + r2) / 2;
        double dioptresCornea = (n - 1) * 1000 / averageRadius;

        // Additional Haigis constants
        double a0 = lens.A0;
        double a1 = lens.A1;
        double a2 = lens.A2;

        // Optical ACD
        double opticalACD = (a0 + a1 * acd + a2 * axialLength);

        // IOL power
        if (resultType.equals("IOL")) {	    				
                double z = dioptresCornea + dioptresRefraction/(1 - dioptresRefraction * vd/1000);
                returnPower = na/(axialLength/1000 - opticalACD/1000) - na/(na/z - opticalACD/1000);
        }
        // Predicted refraction
        else {
                double z = 1000 * na/((1/(1/(axialLength - opticalACD) - dioptresRefraction/(1000 * na))) + opticalACD);
                returnPower = (z - dioptresCornea)/(1 + vd * (z - dioptresCornea)/1000);	    				
        }
        return returnPower;
    }    
}
