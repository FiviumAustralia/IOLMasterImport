/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.org.openeyes;

import java.awt.Rectangle;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.pdfbox.io.RandomAccessBuffer;
import org.apache.pdfbox.io.RandomAccessRead;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageTree;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.PDFTextStripperByArea;
import org.apache.pdfbox.text.TextPosition;

/**
 *
 * @author veta
 */
public class PDFFunctions extends PDFTextStripper{
    private Map<String, Coordinates> CoordMap = new HashMap<String, Coordinates>();
    private PDDocument PDFDoc = null;
    
    /**
     * Instantiate a new PDFTextStripper object.
     *
     * @throws IOException If there is an error loading the properties.
     */
    public PDFFunctions() throws IOException
    {
        addDataCoordinates();
    }   
    
    /**
     *
     * @param pdfBin
     */
    public void setPDFDoc(byte[] pdfBin){
        RandomAccessRead pdfData = new RandomAccessBuffer(pdfBin);
        PDFParser myPDFparser;
        PDFTextStripper pdfStripper;

        try {
            myPDFparser = new PDFParser(pdfData);
            myPDFparser.parse();
            
            pdfStripper = new PDFTextStripper();
            this.PDFDoc = myPDFparser.getPDDocument();
        } catch (IOException ex) {
            Logger.getLogger(PDFFunctions.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    
    /**
     *
     * @return
     */
    public int getMaxPageNum(){
        if(! this.PDFDoc.equals(null)){
            return PDFDoc.getNumberOfPages();
        }else{
            return -1;
        }
    }
    
    /**
     *
     * @param PDFDoc
     * @throws IOException
     */
    public void dumpPDFStructure(PDDocument PDFDoc) throws IOException{
        PDFTextStripper stripper = new PDFFunctions();
	stripper.setSortByPosition( true );
	stripper.setStartPage( 0 );
	stripper.setEndPage( PDFDoc.getNumberOfPages() );
        Writer dummy = new OutputStreamWriter(new ByteArrayOutputStream());
        stripper.writeText(PDFDoc, dummy);
        
    }
    
    /**
     *
     * @param pageNum
     * @return
     */
    public PDPage getPDFPage(int pageNum){
        PDPage outPage = new PDPage();
        PDPageTree allPages;
        
        if(!PDFDoc.equals(null)){
            allPages = this.PDFDoc.getDocumentCatalog().getPages();
            outPage = allPages.get(pageNum);
        }
        return outPage;
    }
    
    /**
     *
     * @param PDFDoc
     * @throws IOException
     */
    public void dumpPDFContent(PDDocument PDFDoc) throws IOException{
        PDPageTree allPages;
        allPages = PDFDoc.getDocumentCatalog().getPages();
        
        PDPage currentPage;
        Iterator pages = allPages.iterator();
        while(pages.hasNext()){
            currentPage = (PDPage)pages.next();
            String pageTitle = getPageTitleIOLM700(currentPage).trim();
            System.out.println(pageTitle);
            if(pageTitle.equals("IOL calculation") || pageTitle.equals("IOL calculation (Multiformula)")){
                System.out.println("Extracting calculation values");
                System.out.println(getTopLensFormulaNameIOLM700(currentPage));
                for(int i = 1;i<=4;i++){
                   System.out.println("Right "+i);
                   System.out.println(getMultiLensFormulaNamesIOLM700(currentPage, "R", i));
                   System.out.println(getMultiLensAValuesIOLM700(currentPage, "R", i));
                   System.out.println(dumpIOLREFValuesIOLM700(currentPage, "R", i));
                   System.out.println("Left "+i);
                   System.out.println(getMultiLensFormulaNamesIOLM700(currentPage, "L", i));
                   System.out.println(getMultiLensAValuesIOLM700(currentPage, "L", i));
                   System.out.println(dumpIOLREFValuesIOLM700(currentPage, "L", i));
                }
            }
        }        
    }
       
    /**
     *
     * @param page
     * @return
     * @throws IOException
     */
    public String getPageTitleIOLM700(PDPage page) throws IOException{
        return getTextArea(page, new Rectangle(200,270,250,10)).trim();
    }
    
    /**
     *
     * @param page
     * @return
     * @throws IOException
     */
    public String getTopLensFormulaNameIOLM700(PDPage page)throws IOException{
        return normaliseLensName(getTextArea(page, new Rectangle(50,180,250,10)));
    }
    
    /**
     *
     * @param page
     * @param side
     * @return
     * @throws IOException
     */
    public String getTargetRefractionIOLM700(PDPage page, String side) throws IOException{
        Rectangle area = new Rectangle(getDataCoordinates("TargetRefractionIOLM700", side, 1).x, getDataCoordinates("TargetRefractionIOLM700", side, 1).y, 110, 10);
        return getTextArea(page, area).trim();
    }
    
    /**
     *
     * @param page
     * @param side
     * @return
     * @throws IOException
     */
    public String getEyeStatusIOLM700(PDPage page, String side) throws IOException{
        Rectangle area = new Rectangle(getDataCoordinates("EyeStatusIOLM700", side, 1).x, getDataCoordinates("EyeStatusIOLM700", side, 1).y, 200, 10);
        return getTextArea(page, area).trim();
    }
    
    /**
     *
     * @param page
     * @param side
     * @return
     * @throws IOException
     */
    public String getK1Side(PDPage page, String side)throws IOException{
        Rectangle area = new Rectangle(getDataCoordinates("K1IOLM700", side, 1).x, getDataCoordinates("K1IOLM700", side, 1).y, 25, 10);
        return getTextArea(page, area).trim().replace("---","0"); 
    }
    
    /**
     *
     * @param page
     * @param side
     * @return
     * @throws IOException
     */
    public String getAxisK1Side(PDPage page, String side)throws IOException{
        Rectangle area = new Rectangle(getDataCoordinates("AxisK1IOLM700", side, 1).x, getDataCoordinates("AxisK1IOLM700", side, 1).y, 19, 10);
        String result = getTextArea(page, area).replace("º","").trim().replace("---","0");
        return ("".equals(result) ? "0" : result);
    }
    
    /**
     *
     * @param page
     * @param side
     * @return
     * @throws IOException
     */
    public String getK2Side(PDPage page, String side)throws IOException{
        Rectangle area = new Rectangle(getDataCoordinates("K2IOLM700", side, 1).x, getDataCoordinates("K2IOLM700", side, 1).y, 25, 10);
        return getTextArea(page, area).trim().replace("---","0"); 
    }
    
    /**
     *
     * @param page
     * @param side
     * @return
     * @throws IOException
     */
    public String getAxisK2Side(PDPage page, String side)throws IOException{
        Rectangle area = new Rectangle(getDataCoordinates("AxisK2IOLM700", side, 1).x, getDataCoordinates("AxisK2IOLM700", side, 1).y, 19, 10);
        String result = getTextArea(page, area).replace("º","").trim().replace("---","0");
        return ("".equals(result) ? "0" : result);
    }
    
    /**
     *
     * @param page
     * @param side
     * @return
     * @throws IOException
     */
    public String getALSide(PDPage page, String side)throws IOException{
        Rectangle area = new Rectangle(getDataCoordinates("ALIOLM700", side, 1).x, getDataCoordinates("ALIOLM700", side, 1).y, 25, 10);
        return getTextArea(page, area).trim().replace("---","0"); 
    }
    
    /**
     *
     * @param page
     * @param side
     * @return
     * @throws IOException
     */
    public String getACDSide(PDPage page, String side)throws IOException{
        Rectangle area = new Rectangle(getDataCoordinates("ACDIOLM700", side, 1).x, getDataCoordinates("ACDIOLM700", side, 1).y, 25, 10);
        return getTextArea(page, area).trim().replace("---","0"); 
    }
    /**
     *
     * @param page
     * @param side
     * @param position
     * @return
     * @throws IOException
     */
    public String getMultiLensFormulaNamesIOLM700(PDPage page, String side, int position) throws IOException{
        return normaliseLensName(getTextArea(page, getRectangleMultiLensFormulaNamesIOLM700(side, position)));
    }
    
    private Rectangle getRectangleMultiLensFormulaNamesIOLM700(String side, int position){
        Coordinates coord = getDataCoordinates("MultiLensFormulaNamesIOLM700", side, position);
         return new Rectangle(coord.x, coord.y, 116, 20);
    }
    
    /**
     *
     * @param page
     * @param side
     * @param position
     * @return
     * @throws IOException
     */
    public String getMultiLensAValuesIOLM700(PDPage page, String side, int position) throws IOException{
        return getTextArea(page, getRectangleMultiLensAValuesIOLM700(side, position));
    }
    
    private Rectangle getRectangleMultiLensAValuesIOLM700(String side, int position){
        Coordinates coord = getDataCoordinates("MultiLensAValuesIOLM700", side, position);
         return new Rectangle(coord.x, coord.y, 120, 20);
    }
    
    /***
     * Returns one specific IOL or REF value
     * 
     * @param page
     * @param side
     * @param position
     * @param type
     * @param row
     * @return
     * @throws IOException 
     */
    
    public Double getIOLREFValueRowIOLM700(PDPage page, String side, int position, String type, int row) throws IOException{
        Coordinates coords;
        if(type.equals("IOL")){
            coords = getDataCoordinates("MultiLensIOLStartIOLM700", side, position);
        }else{
            coords = getDataCoordinates("MultiLensREFStartIOLM700", side, position);
        }

        String textArea = getTextArea(page, new Rectangle(coords.x, coords.y + (row * 9), 50, 9)).trim();

        Double result = null;

        if (!"---".equals(textArea)) {
            result = Double.parseDouble(textArea);
        }

        return result;
    }
    
    /**
     *
     * @param page
     * @param side
     * @param position
     * @return
     * @throws IOException
     */
    public String dumpIOLREFValuesIOLM700(PDPage page, String side, int position) throws IOException{
        // IOL values
        String output = "";
        for(int k=0;k<5;k++){
            output += "IOL: "+getIOLREFValueRowIOLM700(page, side, position, "IOL", k)+" - ";
            output += "REF: "+getIOLREFValueRowIOLM700(page, side, position, "REF", k)+"\n";
        }
        return output;
    }

    
    private void addDataCoordinates(){
        CoordMap.put("MultiLensFormulaNamesIOLM700_L_1",new Coordinates(317, 460));
        CoordMap.put("MultiLensFormulaNamesIOLM700_L_2",new Coordinates(441, 460));
        CoordMap.put("MultiLensFormulaNamesIOLM700_L_3",new Coordinates(317, 595));
        CoordMap.put("MultiLensFormulaNamesIOLM700_L_4",new Coordinates(441, 595));
        CoordMap.put("MultiLensFormulaNamesIOLM700_R_1",new Coordinates(50, 460));
        CoordMap.put("MultiLensFormulaNamesIOLM700_R_2",new Coordinates(185, 460));
        CoordMap.put("MultiLensFormulaNamesIOLM700_R_3",new Coordinates(50, 595));
        CoordMap.put("MultiLensFormulaNamesIOLM700_R_4",new Coordinates(185, 595));
        
        CoordMap.put("MultiLensAValuesIOLM700_L_1", new Coordinates(317, 495));
        CoordMap.put("MultiLensAValuesIOLM700_L_2", new Coordinates(441, 495));
        CoordMap.put("MultiLensAValuesIOLM700_L_3", new Coordinates(317, 620));
        CoordMap.put("MultiLensAValuesIOLM700_L_4", new Coordinates(441, 620));
        CoordMap.put("MultiLensAValuesIOLM700_R_1", new Coordinates(50, 495));
        CoordMap.put("MultiLensAValuesIOLM700_R_2", new Coordinates(185, 495));
        CoordMap.put("MultiLensAValuesIOLM700_R_3", new Coordinates(50, 620));
        CoordMap.put("MultiLensAValuesIOLM700_R_4", new Coordinates(185, 620));

        CoordMap.put("MultiLensIOLStartIOLM700_L_1", new Coordinates(317, 532));
        CoordMap.put("MultiLensIOLStartIOLM700_L_2", new Coordinates(441, 532));
        CoordMap.put("MultiLensIOLStartIOLM700_L_3", new Coordinates(317, 660));
        CoordMap.put("MultiLensIOLStartIOLM700_L_4", new Coordinates(441, 660));
        CoordMap.put("MultiLensIOLStartIOLM700_R_1", new Coordinates(50, 532));
        CoordMap.put("MultiLensIOLStartIOLM700_R_2", new Coordinates(185, 532));
        CoordMap.put("MultiLensIOLStartIOLM700_R_3", new Coordinates(50, 660));
        CoordMap.put("MultiLensIOLStartIOLM700_R_4", new Coordinates(185, 660));
        
        CoordMap.put("MultiLensREFStartIOLM700_L_1", new Coordinates(367, 532));
        CoordMap.put("MultiLensREFStartIOLM700_L_2", new Coordinates(491, 532));
        CoordMap.put("MultiLensREFStartIOLM700_L_3", new Coordinates(367, 660));
        CoordMap.put("MultiLensREFStartIOLM700_L_4", new Coordinates(491, 660));
        CoordMap.put("MultiLensREFStartIOLM700_R_1", new Coordinates(105, 532));
        CoordMap.put("MultiLensREFStartIOLM700_R_2", new Coordinates(235, 532));
        CoordMap.put("MultiLensREFStartIOLM700_R_3", new Coordinates(105, 660));
        CoordMap.put("MultiLensREFStartIOLM700_R_4", new Coordinates(235, 660));
        
        CoordMap.put("TargetRefractionIOLM700_L_1", new Coordinates(310, 410));
        CoordMap.put("TargetRefractionIOLM700_R_1", new Coordinates(30, 410));
        
        CoordMap.put("EyeStatusIOLM700_L_1", new Coordinates(300, 430));
        CoordMap.put("EyeStatusIOLM700_R_1", new Coordinates(20, 430));
        
        CoordMap.put("ALIOLM700_L_1", new Coordinates(340,320));
        CoordMap.put("ALIOLM700_R_1", new Coordinates(80,320));
        
        CoordMap.put("ACDIOLM700_L_1", new Coordinates(340,330));
        CoordMap.put("ACDIOLM700_R_1", new Coordinates(80,330));
        
        CoordMap.put("K1IOLM700_L_1", new Coordinates(340,360));
        CoordMap.put("K1IOLM700_R_1", new Coordinates(80,360));
        CoordMap.put("AxisK1IOLM700_L_1", new Coordinates(392,360));
        CoordMap.put("AxisK1IOLM700_R_1", new Coordinates(132,360));
        
        CoordMap.put("K2IOLM700_L_1", new Coordinates(340,370));
        CoordMap.put("K2IOLM700_R_1", new Coordinates(80,370));
        CoordMap.put("AxisK2IOLM700_L_1", new Coordinates(392,370));
        CoordMap.put("AxisK2IOLM700_R_1", new Coordinates(132,370));
    }
    
    
    private Coordinates getDataCoordinates(String dataname, String side, int position){
        String index = dataname+'_'+side+'_'+position;
        return (Coordinates) CoordMap.get(index);
    }
            
    private String getTextArea(PDPage page, Rectangle titleArea) throws IOException{
        PDFTextStripperByArea stripper = new PDFTextStripperByArea();
        stripper.setSortByPosition( true );
        stripper.addRegion( "area", titleArea );
        stripper.extractRegions( page );
        return stripper.getTextForRegion( "area" );
    }

    private String normaliseLensName(String lensName) {
        String result = lensName.replaceAll("(\\r|\\n)", " ");  // Replace any LF/CR characters with space
        result = result.replaceAll(" +", " ");  // Replace any consecutive spaces with a single space
        return result.trim();
    }
    
    /**
     * Override the default functionality of PDFTextStripper.
     * @param string
     * @param textPositions
     * @throws java.io.IOException
    */
    @Override
    protected void writeString(String string, List<TextPosition> textPositions) throws IOException
    {
        for (TextPosition text : textPositions)
        {
            System.out.println( (char)27 + "[0m String[" + text.getXDirAdj() + "," +
                    text.getYDirAdj() + " fs=" + text.getFontSize() + " xscale=" +
                    text.getXScale() + " height=" + text.getHeightDir() + " space=" +
                    text.getWidthOfSpace() + " width=" +
                    text.getWidthDirAdj() + "] " + (char)27 + "[34;43m \t" + text.getUnicode() +" ");
        }
    }
}
