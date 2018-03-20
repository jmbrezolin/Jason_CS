/**
 * ROS artifact code for ROS Tutorials
 * http://wiki.ros.org/ROS/Tutorials
 * 
 * This class provides a listener for publisher/subscriber tutorial
 * and a client for service/client tutorial 
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package jason.architecture;

import org.ros.exception.RemoteException;
import org.ros.exception.ServiceNotFoundException;
import org.ros.message.MessageListener;
import org.ros.node.service.ServiceClient;
import org.ros.node.service.ServiceResponseListener;
import org.ros.node.topic.Subscriber;

import org.ros.node.topic.Publisher;

import test_rosjava_jni.AddTwoInts;
import test_rosjava_jni.AddTwoIntsRequest;
import test_rosjava_jni.AddTwoIntsResponse;
import cartago.IBlockingCmd;
import cartago.INTERNAL_OPERATION;
import cartago.OPERATION;
import cartago.ObsProperty;

import edu.geog.geocog.csml.CSMLParser;
import edu.geog.geocog.csml.Logger;
import edu.geog.geocog.csml.model.CSMLRegion;
import edu.geog.geocog.csml.model.CSMLQualityDimension;
import edu.geog.geocog.csml.model.CSMLDomain;
import edu.geog.geocog.csml.model.CSMLModel;
import edu.geog.geocog.csml.reasoner.CSMLConceptualSpaceReasoner;
import edu.geog.geocog.csml.model.CSMLPoint;
import edu.geog.geocog.csml.model.CSMLInstance;
import edu.geog.geocog.csml.model.CSMLConcept;
import java.util.ArrayList;

import edu.geog.geocog.csml.exceptions.InvalidContextTypeException;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.io.*;
import java.lang.reflect.Field;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;


import org.opencv.calib3d.Calib3d;
import org.opencv.core.*;
import org.opencv.features2d.*;
import org.opencv.highgui.Highgui;


/**
 * ROS artifact that handles /chatter topic and /add_two_ints service.
 * 
 * @author Rodrigo Wesz <rodrigo.wesz@acad.pucrs.br>
 * @version 1.0
 * @since 2015-03-01
 */
public class ArtTutorial extends JaCaRosArtifact {
	/** Jason Node name */
	
	//private TreeMap<String, String> tmap = new TreeMap<String, String>();
	
	TreeMap <String,String> tm = new TreeMap();
	TreeMap <String,String> tm_color = new TreeMap();
	TreeMap <String,String> tm_lbp = new TreeMap();
	TreeMap <String,String> tm_concepts = new TreeMap();	
    TreeMap <String,String> candidates = new TreeMap();
    Map <String,Double> concept_distances = new HashMap <String,Double>();
	Map <String,Double> concept_distances_hist = new HashMap <String,Double>();
	Map <String,Double> concept_distances3 = new HashMap <String,Double>();
	
	ArrayList<String> my_env = new ArrayList<String> ();
    Map <String,String> my_env_color = new HashMap <String,String>();
    Map <String,String> my_env_lbp = new HashMap <String,String>();
	
	//Map <String,String> gabinete_obj_320 = new HashMap <String,String>();
    //Map <String,Integer> concept_keypoints = new HashMap <String,Integer>();
    //Map <String,Integer> concept_keypoints2 = new HashMap <String,Integer>();
   
	ArrayList<String> frame_objects = new ArrayList<String> ();
	
	private static String rosNodeName = "ArtTutorial";
	
	/* service properties */
	/** Property responsible to connects with AddTwoInts service */
	private ServiceClient<AddTwoIntsRequest, AddTwoIntsResponse> serviceClient;
	private long currentSum;
	/** Property to be observable within artifact */
	private String propertyNameSum = "twoIntsSum";
	private ReadCmdSum cmdSum;	
    
	/* chatter properties */
	/** Property responsible to connects with chatter topic */
	private Subscriber<std_msgs.String> subscriberChatter;	
	private Publisher<std_msgs.String> publisherChatter;

	private String topicName = "/chatter5";
	private String topicName2 = "/chatter2";
        private String topicType = std_msgs.String._TYPE;
        private String currentMessage;
        public String currentMessage2;
		public String currentMessage3;
		public String filePath;
		public String previous = "0";
         
        /** Property to be observable within artifact */
	private String propertyNameChatter = "chatter5";
	private ReadCmdMsg cmdMsg;
    
    /** Creates ROS node */
    public ArtTutorial() {
        super(rosNodeName);
        //logger1.info("ArtTutorial >> constructor()");
    }

    /**
     * Initialize communication with ROS
     * @param agentName string used for disambiguation at multi-agent environments
     */
    void init(String agentName) {
        //logger1.info("ArtTutorial >> init()");
        //logger1.info("ArtTutorial >> init(): agent name=" + agentName);

        if (agentName != null) {            
            topicName = "/" + agentName + topicName; // Update topic name with agent name            
            propertyNameChatter = agentName + "_" + propertyNameChatter;
            propertyNameSum =  agentName + "_" + propertyNameSum;
        }
        
        defineObsProperty(propertyNameChatter, "");
        cmdMsg = new ReadCmdMsg();
        defineObsProperty(propertyNameSum, 0);
        cmdSum = new ReadCmdSum();
        
       // try {
		//	serviceClient = (ServiceClient<AddTwoIntsRequest, AddTwoIntsResponse>) createClient("add_two_ints", AddTwoInts._TYPE);
	//	} catch (ServiceNotFoundException e) {
			// TODO Auto-generated catch block
			//throw new RosRuntimeException(e);
		//	logger1.info("ArtTutorial >> init() Service not found!");
		//	e.printStackTrace();
	//	}
        
		//MyPublisher
		
        publisherChatter = (Publisher<std_msgs.String>) createPublisher(topicName2, topicType);
		
        subscriberChatter = (Subscriber <std_msgs.String>) createSubscriber(topicName, topicType);
        subscriberChatter.addMessageListener(new MessageListener <std_msgs.String> () {
        	@Override
        	public void onNewMessage(std_msgs.String message) {
        		//logger1.info("I heard (from Artifact)" + message.getData());        		        		
        		currentMessage2 = message.getData();
				filePath = "./imgs/"+currentMessage2;
				System.out.println(currentMessage2+"=="+previous);
				String[] parts1 = currentMessage2.split("/");
                String fname = parts1[parts1.length-1];
                String fname2 = fname.substring(0, fname.length()-4);
				if (previous.equals("0")){
					previous = fname2;
				}
				
				String meio = "head";
				String fim = "tail";
				
				try{
					
				  FileReader fr = new FileReader(filePath);
                  LineNumberReader lnr = new LineNumberReader(fr);
 
                  int linenumber = 0;
 
                  while (lnr.readLine() != null){
                        linenumber++;
                  }
                  
                  int lines = 0;
					
					
                   BufferedReader br = new BufferedReader(new FileReader(filePath));
                   while(br.ready()){
                    String linha = br.readLine();
					 lines++;
					 if (lines ==linenumber){
					   linha = linha +","+previous+","+fim;
					 }else{
					   linha = linha +","+previous+","+meio;
					 }  
                    //System.out.println(linha);
					currentMessage = Cmapping2(linha);
					//currentMessage = Cmapping2(currentMessage2);
					execInternalOp("receivingMsg");
                 }
				 previous = fname2;
                 br.close();
                  }catch(IOException ioe){
                    ioe.printStackTrace();
                 }
				
        		//currentMessage2 = message.getData();
        		//currentMessage = Cmapping2(currentMessage2);
				//tmap.put("20160819_192601_03", currentMessage);
        		//currentMessage = "teste";
        		//execInternalOp("receivingMsg");
            }

			private String Cmapping2(String currentMessage2) {
				
				CSMLModel m = new CSMLModel();
                       
                //Shape Domain - Inicio
		               
			    CSMLDomain d1 = m.createCSMLDomain("shape_domain");
		               
		        CSMLQualityDimension qd1 = m.createCSMLQualityDimension("H1");                          
		               
		               qd1.setLabel("H1");
		               qd1.setScale("interval");
		               qd1.setRange(0.0, 1.0);
		               qd1.setDescription("H1");
		               qd1.setUnits("point");
		               d1.addQualityDimension(qd1);
		               
		        CSMLQualityDimension qd2 = m.createCSMLQualityDimension("H2");
		               qd2.setLabel("H2"); 
		               qd2.setScale("interval");
		               qd2.setRange(0.0, 1.0);
		               qd2.setDescription("H2");
		               qd2.setUnits("point");
		               d1.addQualityDimension(qd2);
		               
		        CSMLQualityDimension qd3 = m.createCSMLQualityDimension("H3");
		               qd3.setLabel("H3"); 
		               qd3.setScale("interval");
		               qd3.setRange(0.0, 1.0);
		               qd3.setDescription("H3");
		               qd3.setUnits("point");
		               d1.addQualityDimension(qd3);
		               
		        CSMLQualityDimension qd4 = m.createCSMLQualityDimension("H4");
		               qd4.setLabel("H4"); 
		               qd4.setScale("interval");
		               qd4.setRange(0.0, 1.0);
		               qd4.setDescription("H4");
		               qd4.setUnits("point");
		               d1.addQualityDimension(qd4);
		               
		       CSMLQualityDimension qd5 = m.createCSMLQualityDimension("H5");
		               qd5.setLabel("H5"); 
		               qd5.setScale("interval");
		               qd5.setRange(0.0, 1.0);
		               qd5.setDescription("H5");
		               qd5.setUnits("point");
		               d1.addQualityDimension(qd5);
		               
		       CSMLQualityDimension qd6 = m.createCSMLQualityDimension("H6");
		               qd6.setLabel("H6"); 
		               qd6.setScale("interval");
		               qd6.setRange(0.0, 1.0);
		               qd6.setDescription("H6");
		               qd6.setUnits("point");
		               d1.addQualityDimension(qd6);
		               
		       CSMLQualityDimension qd7 = m.createCSMLQualityDimension("H7");
		               qd7.setLabel("H7"); 
		               qd7.setScale("interval");
		               qd7.setRange(0.0, 1.0);
		               qd7.setDescription("H7");
		               qd7.setUnits("point");
		               d1.addQualityDimension(qd7);
		                
		      m.addDomain(d1);
		               
		      //Shape Domain - Fim
		               
		      //Color Domain - Inicio
		               
		      CSMLDomain color_domain = m.createCSMLDomain("color_domain");
		               
		      CSMLQualityDimension hue = m.createCSMLQualityDimension("ColorHue");                          
		              hue.setLabel("Hue");  
		              hue.setScale("interval");
		              hue.setRange(0.0, 255.0);
		              hue.setDescription("Hue");
		              hue.setUnits("degrees");
		              color_domain.addQualityDimension(hue);
		               
			 CSMLQualityDimension sat = m.createCSMLQualityDimension("ColorSaturation");
		             sat.setLabel("Saturation");
		             sat.setScale("ratio");
		             sat.setRange(0.0, 255.0);
		             sat.setDescription("My Color Saturation");
		             sat.setUnits("percent");
		             color_domain.addQualityDimension(sat);
		                
		     CSMLQualityDimension cvalue =  m.createCSMLQualityDimension("ColorValue");
		             cvalue.setLabel("Value");
		             cvalue.setScale("ratio");
		             cvalue.setRange(0.0, 255.0);
		             cvalue.setDescription("My Color Value");
		             cvalue.setUnits("percent");
		             color_domain.addQualityDimension(cvalue);
				
		     m.addDomain(color_domain);  
		               
		     //Color Domain - Fim
					 
			 //Size Domain - Inicio
					 
			 CSMLDomain size_domain = m.createCSMLDomain("size_domain");
              
             CSMLQualityDimension width = m.createCSMLQualityDimension("Width");                          
                     width.setLabel("Width");
                     width.setScale("interval");
                     width.setRange(0.0, 960.0);
                     width.setDescription("Width");
                     width.setUnits("point");
                     size_domain.addQualityDimension(width);
               
             CSMLQualityDimension height = m.createCSMLQualityDimension("Height");                          
                     height.setLabel("Height");
                     height.setScale("interval");
                     height.setRange(0.0, 960.0);
                     height.setDescription("Height");
                     height.setUnits("point");
                    size_domain.addQualityDimension(height);
              
             m.addDomain(size_domain);
					 
			//Position Domain - Inicio
					 
			CSMLDomain position_domain = m.createCSMLDomain("positon_domain");
               
            CSMLQualityDimension position1 = m.createCSMLQualityDimension("Position 1");                          
                    position1.setLabel("P1");
                    position1.setScale("interval");
                    position1.setRange(0.0, 960.0);
                    position1.setDescription("P1");
                    position1.setUnits("point");
                    position_domain.addQualityDimension(position1);
               
	        CSMLQualityDimension position2 = m.createCSMLQualityDimension("Position 2");                          
                    position2.setLabel("P2");
                    position2.setScale("interval");
                    position2.setRange(0.0, 960.0);
                    position2.setDescription("P2");
                    position2.setUnits("point");
                    position_domain.addQualityDimension(position2);
                		
            m.addDomain(position_domain);

				 
			// Quadro Pri - (Shape)
					  
	        CSMLPoint qPri1 = m.createCSMLPoint("qPri1", d1);
		           qPri1.setq(d1.getQualityDimensionIds());
		           double[] pvqPri1 = {0.169863, 0.000982013, 5.45514e-06, 7.34343e-08, 3.2788e-14, -1.97165e-09, -3.29423e-14 };
		           qPri1.setv(pvqPri1); 
					  
		   CSMLPoint qPri2 = m.createCSMLPoint("qPri2", d1);
		           qPri2.setq(d1.getQualityDimensionIds());
		           double[] pvqPri2 = {0.170151, 0.0011864, 5.62796e-06, 1.76351e-07, 1.30042e-13, 5.27625e-10, 1.18133e-13};
		           qPri2.setv(pvqPri2);
					  
		   CSMLPoint qPri3 = m.createCSMLPoint("qPri3", d1);
		           qPri3.setq(d1.getQualityDimensionIds());
		           double[] pvqPri3 = {0.173792, 0.00261683, 5.30839e-06, 1.5674e-07, 1.24255e-13, 2.57179e-09, -7.07215e-14 };
		           qPri3.setv(pvqPri3);
					  
		  CSMLPoint qPri4 = m.createCSMLPoint("qPri4", d1);
		           qPri4.setq(d1.getQualityDimensionIds());
		           double[] pvqPri4 = {0.174103, 0.00266963, 1.87616e-06, 3.62474e-08, -8.33265e-15, -1.75087e-09, -4.46299e-15};
		           qPri4.setv(pvqPri4);
					  
		  CSMLPoint qPri5 = m.createCSMLPoint("qPri5", d1);
		           qPri5.setq(d1.getQualityDimensionIds());
		           double[] pvqPri5 = {0.174712, 0.00274755, 1.77252e-06, 2.25241e-08, 8.55936e-16, 5.61539e-10, 4.41841e-15 };
		           qPri5.setv(pvqPri5);
					  
		  CSMLPoint qPri6 = m.createCSMLPoint("qPri6", d1);
		           qPri6.setq(d1.getQualityDimensionIds());
		           double[] pvqPri6 = {0.174253, 0.00263063, 3.8723e-06, 1.58615e-08, 3.93057e-15, -3.92937e-10, -5.60083e-17};
		           qPri6.setv(pvqPri6);
					  
		  CSMLPoint qPri7 = m.createCSMLPoint("qPri7", d1);
		          qPri7.setq(d1.getQualityDimensionIds());
		          double[] pvqPri7 = {0.174696, 0.00273226, 1.21217e-05, 1.35502e-07, -4.94953e-14, 4.97118e-09, -1.66457e-13};
		          qPri7.setv(pvqPri7);
					  
		  CSMLPoint qPri8 = m.createCSMLPoint("qPri8", d1);
		         qPri8.setq(d1.getQualityDimensionIds());
		         double[] pvqPri8 = {0.173878, 0.00264858, 2.73167e-05, 9.8997e-07, -4.35808e-12, -5.00858e-08, -2.74045e-12};
		         qPri8.setv(pvqPri8);
					  
		  //Fim Quadro Pri 
			  
		  //Quadro Pri Color
                
          CSMLPoint qPri_color1 = m.createCSMLPoint("qPri_color1", color_domain);
	            qPri_color1.setq(color_domain.getQualityDimensionIds());
	            double[] pvPri_color1 = {128.0, 120.0, 101.0};
	            qPri_color1.setv(pvPri_color1);
                
          CSMLPoint qPri_color2 = m.createCSMLPoint("qPri8_color2", color_domain);
	           qPri_color2.setq(color_domain.getQualityDimensionIds());
	           double[] pvPri_color2 = {135.0, 127.0, 109.0};
	           qPri_color2.setv(pvPri_color2);
                
         CSMLPoint qPri_color3 = m.createCSMLPoint("qPri_color3", color_domain);
	           qPri_color3.setq(color_domain.getQualityDimensionIds());
	           double[] pvPri_color3 = { 133.0, 124.0, 104.0};
	           qPri_color3.setv(pvPri_color3);
                
         CSMLPoint qPri_color4 = m.createCSMLPoint("qPri_color4", color_domain);
	           qPri_color4.setq(color_domain.getQualityDimensionIds());
	           double[] pvPri_color4 = { 148.0, 132.0, 114.0 };
	           qPri_color4.setv(pvPri_color4);
                
         //Fim Quadro Pri - (Color)
			 
		 //Quadro Pri Color - (Size)
                
         CSMLPoint qPri_size1 = m.createCSMLPoint("qPri_size1", size_domain);
	           qPri_size1.setq(size_domain.getQualityDimensionIds());
	           double[] pvqPri_size1 = { 141.0, 164.0 };
	           qPri_size1.setv(pvqPri_size1);
                
         CSMLPoint qPri_size2 = m.createCSMLPoint("qPri_size2", size_domain);
	          qPri_size2.setq(size_domain.getQualityDimensionIds());
	          double[] pvqPri_size2 = { 222.0,  317.0 };
	          qPri_size2.setv(pvqPri_size2);
                
        CSMLPoint qPri_size3 = m.createCSMLPoint("qPri_size3", size_domain);
	          qPri_size3.setq(size_domain.getQualityDimensionIds());
	          double[] pvqPri_size3 = {254.0, 347.0};
	          qPri_size3.setv(pvqPri_size3);
                
        // Fim Quadro Pri (Size)
		
		
		 CSMLPoint smallPicture1 = m.createCSMLPoint("smallPicture1", d1);
		           smallPicture1.setq(d1.getQualityDimensionIds());
		           double[] pvsmallPicture1 = {0.193052,0.00151688,0.00406647,6.51399e-05,1.64474e-08,1.36043e-06,-2.92141e-08};
		           smallPicture1.setv(pvsmallPicture1); 
					  
		   CSMLPoint smallPicture2 = m.createCSMLPoint("smallPicture2", d1);
		           smallPicture2.setq(d1.getQualityDimensionIds());
		           double[] pvsmallPicture2 = {0.196895,0.000602235,0.005007,3.84229e-05,1.65956e-08,9.4206e-07,2.93315e-09};
		           smallPicture2.setv(pvsmallPicture2);
					  
		   CSMLPoint smallPicture3 = m.createCSMLPoint("smallPicture3", d1);
		           smallPicture3.setq(d1.getQualityDimensionIds());
		           double[] pvsmallPicture3 = {0.196839,0.00060999,0.00500366,4.01411e-05,1.68538e-08,9.66292e-07,6.29146e-09};
		           smallPicture3.setv(pvsmallPicture3);
					  
		  CSMLPoint smallPicture4 = m.createCSMLPoint("smallPicture4", d1);
		           smallPicture4.setq(d1.getQualityDimensionIds());
		           double[] pvsmallPicture4 = {0.195051,0.000899978,0.00454282,4.95268e-05,2.28062e-08,1.39573e-06,-5.63548e-09};
		           smallPicture4.setv(pvsmallPicture4);
					  
		  CSMLPoint smallPicture5 = m.createCSMLPoint("smallPicture5", d1);
		           smallPicture5.setq(d1.getQualityDimensionIds());
		           double[] pvsmallPicture5 = {0.195786,0.000846236,0.00469075,4.95713e-05,2.15804e-08,1.23596e-06,-1.028e-08};
		           smallPicture5.setv(pvsmallPicture5);
					  
		  CSMLPoint smallPicture6 = m.createCSMLPoint("smallPicture6", d1);
		           smallPicture6.setq(d1.getQualityDimensionIds());
		           double[] pvsmallPicture6 = {0.19555,0.000917361,0.00463328,5.04093e-05,2.15736e-08,1.28001e-06,-1.13171e-08};
		           smallPicture6.setv(pvsmallPicture6);
					  
		  CSMLPoint smallPicture7 = m.createCSMLPoint("smallPicture7", d1);
		          smallPicture7.setq(d1.getQualityDimensionIds());
		          double[] pvsmallPicture7 = {0.195272,0.000948414,0.00456776,5.04727e-05,2.00913e-08,1.21636e-06,-1.35521e-08};
		          smallPicture7.setv(pvsmallPicture7);
					  
		  CSMLPoint smallPicture8 = m.createCSMLPoint("smallPicture8", d1);
		         smallPicture8.setq(d1.getQualityDimensionIds());
		         double[] pvsmallPicture8 = {0.196295,0.000640274,0.00488698,3.74303e-05,1.57585e-08,9.4509e-07,2.81907e-09};
		         smallPicture8.setv(pvsmallPicture8);
					  
		  //Fim smallPicture 
			  
		  //smallPicture Color
                
          CSMLPoint smallPicture_color1 = m.createCSMLPoint("smallPicture_color1", color_domain);
	            smallPicture_color1.setq(color_domain.getQualityDimensionIds());
	            double[] pvsmallPicture_color1 = {99.0, 93.0, 88.0};
				// double[] pvsmallPicture_color1 = {0.3882352941,	0.3647058824,	0.3450980392};
	            smallPicture_color1.setv(pvsmallPicture_color1);
                
          CSMLPoint smallPicture_color2 = m.createCSMLPoint("smallPicture8_color2", color_domain);
	           smallPicture_color2.setq(color_domain.getQualityDimensionIds());
	           double[] pvsmallPicture_color2 ={ 51.0, 43.0, 40.0};
			   //double[] pvsmallPicture_color2 ={ 0.2,	0.168627451,	0.1568627451};
	           smallPicture_color2.setv(pvsmallPicture_color2);
                
         CSMLPoint smallPicture_color3 = m.createCSMLPoint("smallPicture_color3", color_domain);
	           smallPicture_color3.setq(color_domain.getQualityDimensionIds());
	           double[] pvsmallPicture_color3 = { 64.0, 55.0, 52.0};
			   //double[] pvsmallPicture_color3 = { 0.2509803922,	0.2156862745,	0.2039215686};
	           smallPicture_color3.setv(pvsmallPicture_color3);
                
         CSMLPoint smallPicture_color4 = m.createCSMLPoint("smallPicture_color4", color_domain);
	           smallPicture_color4.setq(color_domain.getQualityDimensionIds());
	           double[] pvsmallPicture_color4 = { 79.0, 66.0, 65.0 };
			   //double[] pvsmallPicture_color4 = { 0.3098039216,	0.2588235294,	0.2549019608 };
	           smallPicture_color4.setv(pvsmallPicture_color4);
                
         //Fim smallPicture - (Color)
			 
		 //Quadro Pri Color - (Size)
                
         CSMLPoint smallPicture_size1 = m.createCSMLPoint("smallPicture_size1", size_domain);
	           smallPicture_size1.setq(size_domain.getQualityDimensionIds());
	           double[] pvsmallPicture_size1 = { 331.0, 255.0 };
	           smallPicture_size1.setv(pvsmallPicture_size1);
                
         CSMLPoint smallPicture_size2 = m.createCSMLPoint("smallPicture_size2", size_domain);
	          smallPicture_size2.setq(size_domain.getQualityDimensionIds());
	          double[] pvsmallPicture_size2 = { 317.0,  240.0 };
	          smallPicture_size2.setv(pvsmallPicture_size2);
                
        CSMLPoint smallPicture_size3 = m.createCSMLPoint("smallPicture_size3", size_domain);
	          smallPicture_size3.setq(size_domain.getQualityDimensionIds());
	          double[] pvsmallPicture_size3 = {321.0, 244.0};
	          smallPicture_size3.setv(pvsmallPicture_size3);
                
        // Fim Quadro Pri (Size)
		
		
		ArrayList<CSMLPoint> smallPicture_points = new ArrayList();
		       smallPicture_points.add(0, smallPicture1);
		       smallPicture_points.add(1, smallPicture2);
		       smallPicture_points.add(2, smallPicture3);
		       smallPicture_points.add(3, smallPicture4);
		       smallPicture_points.add(4, smallPicture5);
		       smallPicture_points.add(5, smallPicture6);
		       smallPicture_points.add(6, smallPicture7);
		       smallPicture_points.add(7, smallPicture8);

     ArrayList<CSMLPoint> smallPicture_color_points = new ArrayList();
               smallPicture_color_points.add(0, smallPicture_color1);
               smallPicture_color_points.add(1, smallPicture_color2);
               smallPicture_color_points.add(2, smallPicture_color3);
               smallPicture_color_points.add(3, smallPicture_color4);
                
     ArrayList<CSMLPoint> smallPicture_size_points1 = new ArrayList();
               smallPicture_size_points1.add(0, smallPicture_size1);
               smallPicture_size_points1.add(1, smallPicture_size2);
               smallPicture_size_points1.add(2, smallPicture_size3);
			
		// Inicio - TV (Shape)
					  
	    CSMLPoint tv1 = m.createCSMLPoint("tv1", d1);
		      tv1.setq(d1.getQualityDimensionIds());
		      double[] pvtv1 = {0.186842, 0.00758252, 1.39852e-05, 3.45865e-07, -1.8532e-13, 1.34987e-09, 7.37746e-13  };
		      tv1.setv(pvtv1); 
					  
		CSMLPoint tv2 = m.createCSMLPoint("tv2", d1);
		      tv2.setq(d1.getQualityDimensionIds());
		      double[] pvtv2 = { 0.185472, 0.0067751, 1.20243e-05, 1.03316e-07, 1.15064e-13, 8.50226e-09, 4.58562e-15};
		      tv2.setv(pvtv2);
					  
		CSMLPoint tv3 = m.createCSMLPoint("tv3", d1);
		       tv3.setq(d1.getQualityDimensionIds());
		       double[] pvtv3 = {0.18629, 0.00707005, 1.25136e-05, 5.2675e-07, -1.33072e-12, -4.21692e-08, -2.41066e-13 };
		       tv3.setv(pvtv3);
					  
		 CSMLPoint tv4 = m.createCSMLPoint("tv4", d1);
		      tv4.setq(d1.getQualityDimensionIds());
		      double[] pvtv4 = {0.186724, 0.00716709, 1.86629e-05, 7.51399e-07, -1.97569e-12, -6.36115e-08, -2.00354e-12 };
		      tv4.setv(pvtv4);
					  
		CSMLPoint tv5 = m.createCSMLPoint("tv5", d1);
		     tv5.setq(d1.getQualityDimensionIds());
		     double[] pvtv5 = {0.185585, 0.00692963, 4.2163e-05, 2.17838e-06, -2.06926e-11, -1.73553e-07, -2.76833e-12 };
		     tv5.setv(pvtv5);
					  
		CSMLPoint tv6 = m.createCSMLPoint("tv6", d1);
		     tv6.setq(d1.getQualityDimensionIds());
		     double[] pvtv6 = {0.189866, 0.00801046, 4.9837e-05, 4.01525e-06, -5.53299e-11, -3.58594e-07, -1.28368e-11};
		     tv6.setv(pvtv6);
					  
		CSMLPoint tv7 = m.createCSMLPoint("tv7", d1);
		     tv7.setq(d1.getQualityDimensionIds());
		     double[] pvtv7 = {0.18646, 0.00731003, 3.56158e-05, 1.69446e-06, -1.09087e-11, -9.93805e-08, 7.36715e-12};
		     tv7.setv(pvtv7);
					  
		CSMLPoint tv8 = m.createCSMLPoint("tv8", d1);
		     tv8.setq(d1.getQualityDimensionIds());
		     double[] pvtv8 = {0.189994, 0.00808069, 4.82314e-05, 4.23883e-06, -5.41092e-11, -3.74509e-07, -2.73056e-11};
		     tv8.setv(pvtv8);
					  
		//Fim Tv - (Shape)
		
		//Tv  (Color)
                
        CSMLPoint tv_color1 = m.createCSMLPoint("tv_color1", color_domain);
	         tv_color1.setq(color_domain.getQualityDimensionIds());
	         double[] pvtv_color1 = { 25, 23, 25};
	         tv_color1.setv(pvtv_color1);
                
        CSMLPoint tv_color2 = m.createCSMLPoint("tv_color2", color_domain);
	         tv_color2.setq(color_domain.getQualityDimensionIds());
	         double[] pvtv_color2 = { 16, 14, 16};
	         tv_color2.setv(pvtv_color2);
                
        CSMLPoint tv_color3 = m.createCSMLPoint("tv_color3", color_domain);
	         tv_color3.setq(color_domain.getQualityDimensionIds());
	         double[] pvtv_color3 = {  15, 13, 14 };
	         tv_color3.setv(pvtv_color3);
                
        CSMLPoint tv_color4 = m.createCSMLPoint("tv_color4", color_domain);
	         tv_color4.setq(color_domain.getQualityDimensionIds());
	         double[] pvtv_color4 = { 21, 20, 22 };
	         tv_color4.setv(pvtv_color4);
                
        //Fim Tv - color
		
		// Tv - (Size)
			
		CSMLPoint tv_size1 = m.createCSMLPoint("tv_size1", size_domain);
	         tv_size1.setq(size_domain.getQualityDimensionIds());
	         double[] pvtv_size1 = { 117, 73 };
	         tv_size1.setv(pvtv_size1);
                
        CSMLPoint tv_size2 = m.createCSMLPoint("tv_size2", size_domain);
	         tv_size2.setq(size_domain.getQualityDimensionIds());
	         double[] pvtv_size2 = { 219, 138 };
	         tv_size2.setv(pvtv_size2);
                
        CSMLPoint tv_size3 = m.createCSMLPoint("tv_size3", size_domain);
	         tv_size3.setq(size_domain.getQualityDimensionIds());
	         double[] pvtv_size3 = {427, 258};
	         tv_size3.setv(pvtv_size3);
			
	     // Fim Tv - (Size)		
		
		 // Inicio ArCond (Shape)
					  
	    CSMLPoint arCond1 = m.createCSMLPoint("arCond1", d1);
		     arCond1.setq(d1.getQualityDimensionIds());
		     double[] pvarCond1 = { 0.234708, 0.027387, 5.67147e-05, 6.4193e-06, 1.15392e-10, 9.38048e-07, 4.1072e-11 };
		     arCond1.setv(pvarCond1); 
					  
	    CSMLPoint arCond2 = m.createCSMLPoint("arCond2", d1);
		     arCond2.setq(d1.getQualityDimensionIds());
		     double[] pvarCond2 = { 0.240624, 0.0301939, 0.00010071, 1.556e-05, 5.89886e-10, 2.46457e-06, 1.77304e-10};
		     arCond2.setv(pvarCond2);
					  
	    CSMLPoint arCond3 = m.createCSMLPoint("arCond3", d1);
		     arCond3.setq(d1.getQualityDimensionIds());
		     double[] pvarCond3 = { 0.232097, 0.0265108, 4.97906e-05, 3.19945e-06, -2.5103e-11, -2.55803e-07, 3.16314e-11};
		     arCond3.setv(pvarCond3);
					  
	    CSMLPoint arCond4 = m.createCSMLPoint("arCond4", d1);
		     arCond4.setq(d1.getQualityDimensionIds());
		     double[] pvarCond4 = {0.251783, 0.0350985, 3.11114e-05, 3.8649e-06, 3.98445e-11, 7.18493e-07, -1.44407e-11};
		     arCond4.setv(pvarCond4);
					  
	    CSMLPoint arCond5 = m.createCSMLPoint("arCond5", d1);
		     arCond5.setq(d1.getQualityDimensionIds());
		     double[] pvarCond5 = { 0.262022, 0.0408367, 2.15886e-05, 1.92689e-06, 1.22478e-11, 3.88251e-07, -2.10818e-12 };
		     arCond5.setv(pvarCond5);
					  
	    CSMLPoint arCond6 = m.createCSMLPoint("arCond6", d1);
		     arCond6.setq(d1.getQualityDimensionIds());
		     double[] pvarCond6 = {0.266205, 0.0431472, 3.52026e-06, 2.40083e-07, -2.15754e-13, 4.8023e-08, -4.65281e-14};
		     arCond6.setv(pvarCond6);
					  
	    CSMLPoint arCond7 = m.createCSMLPoint("arCond7", d1);
		     arCond7.setq(d1.getQualityDimensionIds());
		     double[] pvarCond7 = {0.269891, 0.0455741, 2.92774e-05, 4.58762e-06, 4.45959e-11, 7.68605e-07, 2.89483e-11 };
		     arCond7.setv(pvarCond7);
					  
	    CSMLPoint arCond8 = m.createCSMLPoint("arCond8", d1);
		     arCond8.setq(d1.getQualityDimensionIds());
		     double[] pvarCond8 = {0.263664, 0.0422556, 1.60633e-05, 1.22241e-06, -5.24561e-12, -2.4695e-07, -1.3511e-12};
		     arCond8.setv(pvarCond8);
					  
		 //Fim Ar Cond (Shape)
		 
		// Inicio ArCond (Color)
                
         CSMLPoint arCond_color1 = m.createCSMLPoint("arCond_color1", color_domain);
	          arCond_color1.setq(color_domain.getQualityDimensionIds());
	          double[] pvarCond_color1 = {122, 116, 105 };
	          arCond_color1.setv(pvarCond_color1);
                
         CSMLPoint arCond_color2 = m.createCSMLPoint("arCond_color2", color_domain);
	          arCond_color2.setq(color_domain.getQualityDimensionIds());
	          double[] pvarCond_color2 = {108, 107, 94};
	          arCond_color2.setv(pvarCond_color2);
                
        CSMLPoint arCond_color3 = m.createCSMLPoint("arCond_color3", color_domain);
	         arCond_color3.setq(color_domain.getQualityDimensionIds());
	         double[] pvarCond_color3 = { 117, 115, 102};
	         arCond_color3.setv(pvarCond_color3);
                
        CSMLPoint arCond_color4 = m.createCSMLPoint("arCond_color4", color_domain);
	        arCond_color4.setq(color_domain.getQualityDimensionIds());
	        double[] pvarCond_color4 = { 126, 120, 106  };
	        arCond_color4.setv(pvarCond_color4);
                
        //Fim ArCond  Color
		
	    // Inicio ArCond (Size)
				     
        CSMLPoint arCond_size1 = m.createCSMLPoint("arCond_size1", size_domain);
	         arCond_size1.setq(size_domain.getQualityDimensionIds());
	         double[] pvarCond_size1 = { 159.0, 73.0  };
	         arCond_size1.setv(pvarCond_size1);
                
        CSMLPoint arCond_size2 = m.createCSMLPoint("arCond_size2", size_domain);
	        qPri_size2.setq(size_domain.getQualityDimensionIds());
	        double[] pvarCond_size2 = {  234.0, 97.0 };
	        arCond_size2.setv(pvarCond_size2);
                
        CSMLPoint arCond_size3 = m.createCSMLPoint("arCond_size3", size_domain);
	        arCond_size3.setq(size_domain.getQualityDimensionIds());
	        double[] pvarCond_size3 = { 329.0, 128.0};
	        arCond_size3.setv(pvarCond_size3);
             
        //Fim ArCond (Size)
				
	  //Painel Extintor Points (Shape)	
					   
	  CSMLPoint pextintor1 = m.createCSMLPoint("pextintor1", d1);
		  pextintor1.setq(d1.getQualityDimensionIds());
		  double[] pvpextintor1 = {0.371022, 0.0277065, 0.011266, 0.0108437, 0.000119378, 0.00178918, -1.06586e-05   };
		  pextintor1.setv(pvpextintor1); 
                 
      CSMLPoint pextintor2 = m.createCSMLPoint("pextintor2", d1);
		  pextintor2.setq(d1.getQualityDimensionIds());
		  double[] pvpextintor2 = {0.287533, 0.0348753, 0.000758094, 0.000496253, 3.04371e-07, 8.42135e-05, 2.34981e-09  };
		  pextintor2.setv(pvpextintor2);
                 
      CSMLPoint pextintor3 = m.createCSMLPoint("pextintor3", d1);
		  pextintor3.setq(d1.getQualityDimensionIds());
		  double[] pvpextintor3 = {0.258956, 0.0215953, 0.000185814, 7.29096e-05, 4.65551e-09, -3.21313e-06, 7.09527e-09  };
		  pextintor3.setv(pvpextintor3);
                 
      CSMLPoint pextintor4 = m.createCSMLPoint("pextintor4", d1);
		   pextintor4.setq(d1.getQualityDimensionIds());
		   double[] pvpextintor4 = {0.295864, 0.0356407, 0.000479327, 0.0007699, 4.47886e-07, 0.000129006, 1.34692e-07   };
		   pextintor4.setv(pvpextintor4);
                 
      CSMLPoint pextintor5 = m.createCSMLPoint("pextintor5", d1);
		  pextintor5.setq(d1.getQualityDimensionIds());
		  double[] pvpextintor5 = {0.197197, 0.00911409, 4.08129e-05, 1.89891e-05, 4.88279e-10, 1.27054e-06, -2.0258e-10  };
		  pextintor5.setv(pvpextintor5);
                 
      CSMLPoint pextintor6 = m.createCSMLPoint("pextintor6", d1);
		  pextintor6.setq(d1.getQualityDimensionIds());
		  double[] pvpextintor6 = {0.208553,0.0158327,0.000350129,4.27329e-05,4.01632e-09,3.8746e-06,-3.34535e-09  };
		  pextintor6.setv(pvpextintor6);
                 
      CSMLPoint pextintor7 = m.createCSMLPoint("pextintor7", d1);
		  pextintor7.setq(d1.getQualityDimensionIds());
		  double[] pvpextintor7 = {0.236773, 0.0197432, 6.45296e-05, 2.6065e-05, 4.44101e-10, 1.71314e-06, 9.72354e-10  };
		  pextintor7.setv(pvpextintor7);
                 
      CSMLPoint pextintor8 = m.createCSMLPoint("pextintor8", d1);
		  pextintor8.setq(d1.getQualityDimensionIds());
		  double[] pvpextintor8 = {0.24931, 0.0202579, 0.000778696, 0.000613793, 4.02438e-07, 7.04129e-05, 1.34574e-07 };
		  pextintor8.setv(pvpextintor8);
		  
	  //Painel Extintor Points (Shape)	-Fim 
	  
	  //Painel Extintor Points (Color)		  
                 
       CSMLPoint pextintor_color1 = m.createCSMLPoint("pextintor_color1", color_domain);
	       pextintor_color1.setq(color_domain.getQualityDimensionIds());
	       double[] pvpextintor_color1 = {198, 60, 62 };
	       pextintor_color1.setv(pvpextintor_color1);
                
       CSMLPoint pextintor_color2 = m.createCSMLPoint("pextintor_color2", color_domain);
	        pextintor_color2.setq(color_domain.getQualityDimensionIds());
	        double[] pvpextintor_color2 = {138,  30, 32  };
	        pextintor_color2.setv(pvpextintor_color2);
                
       CSMLPoint pextintor_color3 = m.createCSMLPoint("pextintor_color3", color_domain);
	        pextintor_color3.setq(color_domain.getQualityDimensionIds());
	        double[] pvpextintor_color3 = {228, 120, 122 };
	        pextintor_color3.setv(pvpextintor_color3);
                
       CSMLPoint pextintor_color4 = m.createCSMLPoint("pextintor_color4", color_domain);
	        pextintor_color4.setq(color_domain.getQualityDimensionIds());
	        double[] pvpextintor_color4 = {226, 38, 29 };
	        pextintor_color4.setv(pvpextintor_color4);
      
	  //Painel Extintor Points (Color)	-Fim 
	  
	  //Painel Extintor Points (Size)	          
			
       CSMLPoint pextintor_size1 = m.createCSMLPoint("pextintor_size1", size_domain);
	        pextintor_size1.setq(size_domain.getQualityDimensionIds());
	        double[] pvpextintor_size1 = { 156, 240 };
	        pextintor_size1.setv(pvpextintor_size1);
                
       CSMLPoint pextintor_size2 = m.createCSMLPoint("pextintor_size2", size_domain);
	        pextintor_size2.setq(size_domain.getQualityDimensionIds());
	        double[] pvpextintor_size2 = { 151, 271 };
	        pextintor_size2.setv(pvpextintor_size2);
                
       CSMLPoint pextintor_size3 = m.createCSMLPoint("pextintor_size3", size_domain);
	        pextintor_size3.setq(size_domain.getQualityDimensionIds());
	        double[] pvpextintor_size3 = { 199, 238 };
	        pextintor_size3.setv(pvpextintor_size3);
       
	    //Tapete
              
              
       CSMLPoint tapeteFr1 = m.createCSMLPoint("tapeteFr1", d1);
		   tapeteFr1.setq(d1.getQualityDimensionIds());
		   double[] pvtapeteFr1 = {0.257801, 0.027956, 0.000974269, 6.5496e-05, 1.11573e-08, 9.96578e-06, -1.22167e-08 };
		   tapeteFr1.setv(pvtapeteFr1);
                 
       CSMLPoint tapeteFr2 = m.createCSMLPoint("tapeteFr2", d1);
		   tapeteFr2.setq(d1.getQualityDimensionIds());
		   double[] pvtapeteFr2 = { 0.226617, 0.0209522, 0.000406699, 0.000124801, 2.77646e-08, 1.76313e-05, 4.43606e-09 };
		   tapeteFr2.setv(pvtapeteFr2);
                 
       CSMLPoint tapeteFr3 = m.createCSMLPoint("tapeteFr3", d1);
		    tapeteFr3.setq(d1.getQualityDimensionIds());
		    double[] pvtapeteFr3 = {0.226881, 0.0238697, 8.99487e-05, 1.17468e-05, 2.06884e-10, 6.75366e-07, -3.20934e-10 };
		    tapeteFr3.setv(pvtapeteFr3);
                 
       CSMLPoint tapeteFr4 = m.createCSMLPoint("tapeteFr4", d1);
		    tapeteFr4.setq(d1.getQualityDimensionIds());
		    double[] pvtapeteFr4 = {0.235492, 0.0267227, 8.96959e-05, 2.93736e-05, 1.31467e-09, 3.4444e-06, -7.38147e-10 };
		    tapeteFr4.setv(pvtapeteFr4);
                 
       CSMLPoint tapeteFr5 = m.createCSMLPoint("tapeteFr5", d1);
		    tapeteFr5.setq(d1.getQualityDimensionIds());
		    double[] pvtapeteFr5 = { 0.381554, 0.0928404, 0.00260077, 0.00055533, 1.95622e-07, 3.67154e-05, -6.38072e-07 };
		    tapeteFr5.setv(pvtapeteFr5);
                 
       CSMLPoint tapeteFr6 = m.createCSMLPoint("tapeteFr6", d1);
		    tapeteFr6.setq(d1.getQualityDimensionIds());
		    double[] pvtapeteFr6 = {0.427822, 0.0479489, 0.0164959, 0.00607443, -3.40286e-05, 0.00103838, -5.03925e-05 };
		    tapeteFr6.setv(pvtapeteFr6);
                 
       CSMLPoint tapeteFr7 = m.createCSMLPoint("tapeteFr7", d1);
		    tapeteFr7.setq(d1.getQualityDimensionIds());
		    double[] pvtapeteFr7 = {0.338435, 0.0653439, 0.00536523, 0.00181834, 4.7281e-06, 0.00036567, -3.14667e-06};
		    tapeteFr7.setv(pvtapeteFr7);
                 
       CSMLPoint tapeteFr8 = m.createCSMLPoint("tapeteFr8", d1);
		    tapeteFr8.setq(d1.getQualityDimensionIds());
		    double[] pvtapeteFr8 = {0.357663, 0.067733, 0.00784252, 0.00168914, 3.80314e-06, 0.000405326, -4.83038e-06};
		    tapeteFr8.setv(pvtapeteFr8); 
                   
       CSMLPoint tapeteFr_color1 = m.createCSMLPoint("tapeteFr_color1", color_domain);
	        tapeteFr_color1.setq(color_domain.getQualityDimensionIds());
	        double[] pvtapeteFr_color1 = {78, 80, 88};
	        tapeteFr_color1.setv(pvtapeteFr_color1);
                
      CSMLPoint tapeteFr_color2 = m.createCSMLPoint("tapeteFr_color2", color_domain);
	      tapeteFr_color2.setq(color_domain.getQualityDimensionIds());
	      double[] pvtapeteFr_color2 = {108, 106, 116};
	      tapeteFr_color2.setv(pvtapeteFr_color2);
                
      CSMLPoint tapeteFr_color3 = m.createCSMLPoint("tapeteFr_color3", color_domain);
	       tapeteFr_color3.setq(color_domain.getQualityDimensionIds());
	       double[] pvtapeteFr_color3 = {48, 52, 60};
	       tapeteFr_color3.setv(pvtapeteFr_color3);
                
      CSMLPoint tapeteFr_color4 = m.createCSMLPoint("tapeteFr_color4", color_domain);
	       tapeteFr_color4.setq(color_domain.getQualityDimensionIds());
	       double[] pvtapeteFr_color4 = {118, 116, 126 };
	       tapeteFr_color4.setv(pvtapeteFr_color4);  
                   
     CSMLPoint tapeteFr_size1 = m.createCSMLPoint("tapeteFr_size1", size_domain);
	      tapeteFr_size1.setq(size_domain.getQualityDimensionIds());
	      double[] pvtapeteFr_size1 = { 309, 154};
	      tapeteFr_size1.setv(pvtapeteFr_size1);
                
     CSMLPoint tapeteFr_size2 = m.createCSMLPoint("tapeteFr_size2", size_domain);
	      tapeteFr_size2.setq(size_domain.getQualityDimensionIds());
	      double[] pvtapeteFr_size2 = { 319, 206};
	      tapeteFr_size2.setv(pvtapeteFr_size2);
                
     CSMLPoint tapeteFr_size3 = m.createCSMLPoint("tapeteFr_size3", size_domain);
	      tapeteFr_size3.setq(size_domain.getQualityDimensionIds());
	      double[] pvtapeteFr_size3 = { 469, 251};
	      tapeteFr_size3.setv(pvtapeteFr_size3);
		  
	//Novos
	
	 CSMLPoint blackChair1 = m.createCSMLPoint("blackChair1", d1);
		           blackChair1.setq(d1.getQualityDimensionIds());
		           double[] pvblackChair1 = {0.399241,0.0443788,0.000116877,0.00713163,2.98366e-07,0.00150019,6.50415e-06 };
		           blackChair1.setv(pvblackChair1); 
					  
		   CSMLPoint blackChair2 = m.createCSMLPoint("blackChair2", d1);
		           blackChair2.setq(d1.getQualityDimensionIds());
		           double[] pvblackChair2 = {0.396649,0.0476986,0.000166015,0.00654458,6.74107e-06,0.00141938,1.04609e-06};
		           blackChair2.setv(pvblackChair2);
					  
		   CSMLPoint blackChair3 = m.createCSMLPoint("blackChair3", d1);
		           blackChair3.setq(d1.getQualityDimensionIds());
		           double[] pvblackChair3 = {0.355653,0.0634567,0.000677448,0.000316143,1.45364e-07,7.94081e-05,-1.65785e-08 };
		           blackChair3.setv(pvblackChair3);
					  
		  CSMLPoint blackChair4 = m.createCSMLPoint("blackChair4", d1);
		           blackChair4.setq(d1.getQualityDimensionIds());
		           double[] pvblackChair4 = {0.332085,0.0535736,0.000689402,0.000293209,1.30526e-07,6.77409e-05,-1.84729e-08};
		           blackChair4.setv(pvblackChair4);
					  
		  CSMLPoint blackChair5 = m.createCSMLPoint("blackChair5", d1);
		           blackChair5.setq(d1.getQualityDimensionIds());
		           double[] pvblackChair5 = {0.346014,0.060156,0.000971017,0.000532055,3.80761e-07,0.000129771,-3.56552e-08 };
		           blackChair5.setv(pvblackChair5);
					  
		  CSMLPoint blackChair6 = m.createCSMLPoint("blackChair6", d1);
		           blackChair6.setq(d1.getQualityDimensionIds());
		           double[] pvblackChair6 = {0.327794,0.0524692,0.000787088,0.000409516,2.31755e-07,9.34229e-05,-1.85634e-08};
		           blackChair6.setv(pvblackChair6);
					  
		  CSMLPoint blackChair7 = m.createCSMLPoint("blackChair7", d1);
		          blackChair7.setq(d1.getQualityDimensionIds());
		          double[] pvblackChair7 = {0.328339,0.0530505,0.000741387,0.000409479,2.24773e-07,9.39514e-05,-1.94834e-08};
		          blackChair7.setv(pvblackChair7);
					  
		  CSMLPoint blackChair8 = m.createCSMLPoint("blackChair8", d1);
		         blackChair8.setq(d1.getQualityDimensionIds());
		         double[] pvblackChair8 = {0.325474,0.0516989,0.000941611,0.000509965,3.51687e-07,0.000115898,-3.45825e-08};
		         blackChair8.setv(pvblackChair8);
					  
		  //Fim blackChair 
		  
		  //blackChair2
		  
		   CSMLPoint blackChair_2_1 = m.createCSMLPoint("blackChair_2_1", d1);
		           blackChair_2_1.setq(d1.getQualityDimensionIds());
		           double[] pvblackChair_2_1 = {0.309163,0.0242501,0.00401204,0.00113195,2.3974e-06,0.000100081,-2.67443e-07 };
		           blackChair_2_1.setv(pvblackChair_2_1); 
					  
		   CSMLPoint blackChair_2_2 = m.createCSMLPoint("blackChair_2_2", d1);
		           blackChair_2_2.setq(d1.getQualityDimensionIds());
		           double[] pvblackChair_2_2 = {0.305815,0.0149211,0.000886352,0.00101884,9.59658e-07,5.90121e-05,-1.28248e-07};
		           blackChair_2_2.setv(pvblackChair_2_2);
					  
		   CSMLPoint blackChair_2_3 = m.createCSMLPoint("blackChair_2_3", d1);
		           blackChair_2_3.setq(d1.getQualityDimensionIds());
		           double[] pvblackChair_2_3 = {0.30801,0.0239459,0.0040293,0.00112567,2.3797e-06,9.57543e-05,-2.90349e-07 };
		           blackChair_2_3.setv(pvblackChair_2_3);
					  
		  CSMLPoint blackChair_2_4 = m.createCSMLPoint("blackChair_2_4", d1);
		           blackChair_2_4.setq(d1.getQualityDimensionIds());
		           double[] pvblackChair_2_4 = {0.334975,0.0286564,0.000399696,0.00195788,-1.41452e-06,0.000330296,9.99443e-07};
		           blackChair_2_4.setv(pvblackChair_2_4);
					  
		  CSMLPoint blackChair_2_5 = m.createCSMLPoint("blackChair_2_5", d1);
		           blackChair_2_5.setq(d1.getQualityDimensionIds());
		           double[] pvblackChair_2_5 = {0.303174,0.0405807,0.000856846,0.000422831,2.52027e-07,8.49012e-05,-3.54518e-08 };
		           blackChair_2_5.setv(pvblackChair_2_5);
					  
		  CSMLPoint blackChair_2_6 = m.createCSMLPoint("blackChair_2_6", d1);
		           blackChair_2_6.setq(d1.getQualityDimensionIds());
		           double[] pvblackChair_2_6 = {0.301645,0.0324609,0.000310198,0.000219215,3.39039e-08,3.84979e-05,4.60245e-08};
		           blackChair_2_6.setv(pvblackChair_2_6);
					  
		  CSMLPoint blackChair_2_7 = m.createCSMLPoint("blackChair_2_7", d1);
		          blackChair_2_7.setq(d1.getQualityDimensionIds());
		          double[] pvblackChair_2_7 = {0.303593,0.0357562,0.00221972,0.000449159,4.28089e-07,7.09496e-05,1.33717e-07};
		          blackChair_2_7.setv(pvblackChair_2_7);
					  
		  CSMLPoint blackChair_2_8 = m.createCSMLPoint("blackChair_2_8", d1);
		         blackChair_2_8.setq(d1.getQualityDimensionIds());
		         double[] pvblackChair_2_8 = {0.305471,0.0416184,0.000690527,0.000341601,1.62482e-07,6.9278e-05,-3.35461e-08};
		         blackChair_2_8.setv(pvblackChair_2_8);
		  
		  
		 //Fim blackChair2
		 
		 // blackChair3
		 
		 CSMLPoint blackChair_3_1 = m.createCSMLPoint("blackChair_3_1", d1);
		           blackChair_3_1.setq(d1.getQualityDimensionIds());
		           double[] pvblackChair_3_1 = { 0.299124, 0.00871448, 0.000249229, 0.000792566, 3.51256e-07, 7.39035e-05, 2.64539e-08 };
		           blackChair_3_1.setv(pvblackChair_3_1); 
					  
		   CSMLPoint blackChair_3_2 = m.createCSMLPoint("blackChair_3_2", d1);
		           blackChair_3_2.setq(d1.getQualityDimensionIds());
		           double[] pvblackChair_3_2 = {0.304145, 0.0101717, 0.000244523, 0.000785545, 2.78576e-07, 7.92241e-05, -2.02303e-07};
		           blackChair_3_2.setv(pvblackChair_3_2);
					  
		   CSMLPoint blackChair_3_3 = m.createCSMLPoint("blackChair_3_3", d1);
		           blackChair_3_3.setq(d1.getQualityDimensionIds());
		           double[] pvblackChair_3_3 = {0.301988, 0.0103899, 0.000275271, 0.000693932, 2.62652e-07, 7.07237e-05, -1.5165e-07};
		           blackChair_3_3.setv(pvblackChair_3_3);
					  
		  CSMLPoint blackChair_3_4 = m.createCSMLPoint("blackChair_3_4", d1);
		           blackChair_3_4.setq(d1.getQualityDimensionIds());
		           double[] pvblackChair_3_4 = {0.324027, 0.021141, 0.000194718, 0.000188558, -3.27824e-08, 2.70767e-05, 1.51891e-08};
		           blackChair_3_4.setv(pvblackChair_3_4);
					  
		  CSMLPoint blackChair_3_5 = m.createCSMLPoint("blackChair_3_5", d1);
		           blackChair_3_5.setq(d1.getQualityDimensionIds());
		           double[] pvblackChair_3_5 = {0.323962, 0.0216674, 0.000975066, 9.30665e-05, 5.95907e-09, 1.08257e-05, 2.73948e-08};
		           blackChair_3_5.setv(pvblackChair_3_5);
					  
		  CSMLPoint blackChair_3_6 = m.createCSMLPoint("blackChair_3_6", d1);
		           blackChair_3_6.setq(d1.getQualityDimensionIds());
		           double[] pvblackChair_3_6 = {0.32224, 0.0198672, 0.000395062, 0.000115303, -2.44407e-08, 1.6251e-05, 2.87202e-09};
		           blackChair_3_6.setv(pvblackChair_3_6);
					  
		  CSMLPoint blackChair_3_7 = m.createCSMLPoint("blackChair_3_7", d1);
		          blackChair_3_7.setq(d1.getQualityDimensionIds());
		          double[] pvblackChair_3_7 = { 0.324158, 0.0213743, 0.000207324, 0.000164046, -2.94692e-08, 2.34726e-05, 6.84272e-09};
		          blackChair_3_7.setv(pvblackChair_3_7);
					  
		  CSMLPoint blackChair_3_8 = m.createCSMLPoint("blackChair_3_8", d1);
		         blackChair_3_8.setq(d1.getQualityDimensionIds());
		         double[] pvblackChair_3_8 = {0.324822, 0.0212234, 0.000198229, 0.000195779, -3.72268e-08, 2.79073e-05, 1.00839e-08};
		         blackChair_3_8.setv(pvblackChair_3_8);
		 
		 
		 //Fim blackChair3 (Shape)
		 
		 //NoteChair3 
		 
		CSMLPoint noteChair_1_1 = m.createCSMLPoint("blackChair_3_1", d1);
		          noteChair_1_1.setq(d1.getQualityDimensionIds());
		           double[] pvnoteChair_1_1 = {0.297272,0.0140098,0.00395157,0.000111337,7.19756e-08,1.29018e-05,-1.65303e-08 };
		          noteChair_1_1.setv(pvnoteChair_1_1); 
					  
		   CSMLPoint noteChair_1_2 = m.createCSMLPoint("blackChair_3_2", d1);
		          noteChair_1_2.setq(d1.getQualityDimensionIds());
		           double[] pvnoteChair_1_2 = {0.28207,0.0106295,0.00408299,6.31353e-05,3.17031e-08,6.45067e-06,-4.73784e-09};
		          noteChair_1_2.setv(pvnoteChair_1_2);
					  
		   CSMLPoint noteChair_1_3 = m.createCSMLPoint("blackChair_3_3", d1);
		          noteChair_1_3.setq(d1.getQualityDimensionIds());
		           double[] pvnoteChair_1_3 = {0.183753,0.00326013,0.00128306,8.61873e-05,2.83206e-08,4.83246e-06,4.40273e-09 };
		          noteChair_1_3.setv(pvnoteChair_1_3);
					  
		  CSMLPoint noteChair_1_4 = m.createCSMLPoint("blackChair_3_4", d1);
		          noteChair_1_4.setq(d1.getQualityDimensionIds());
		           double[] pvnoteChair_1_4 = {0.183609,0.0032186,0.00124291,8.15451e-05,2.52499e-08,4.22241e-06,6.03323e-09};
		          noteChair_1_4.setv(pvnoteChair_1_4);
					  
		  CSMLPoint noteChair_1_5 = m.createCSMLPoint("blackChair_3_5", d1);
		          noteChair_1_5.setq(d1.getQualityDimensionIds());
		           double[] pvnoteChair_1_5 = {0.182883,0.00293846,0.00123817,7.42639e-05,2.21267e-08,3.82954e-06,4.18698e-099 };
		          noteChair_1_5.setv(pvnoteChair_1_5);
					  
		  CSMLPoint noteChair_1_6 = m.createCSMLPoint("blackChair_3_6", d1);
		          noteChair_1_6.setq(d1.getQualityDimensionIds());
		           double[] pvnoteChair_1_6 = {0.183222,0.00309871,0.00127716,7.8357e-05,2.3823e-08,4.08658e-06,6.84893e-09};
		          noteChair_1_6.setv(pvnoteChair_1_6);
					  
		  CSMLPoint noteChair_1_7 = m.createCSMLPoint("blackChair_3_7", d1);
		         noteChair_1_7.setq(d1.getQualityDimensionIds());
		          double[] pvnoteChair_1_7 = {0.183687,0.0033482,0.00119564,8.06234e-05,2.42015e-08,4.27253e-06,6.39366e-09};
		         noteChair_1_7.setv(pvnoteChair_1_7);
					  
		  CSMLPoint noteChair_1_8 = m.createCSMLPoint("blackChair_3_8", d1);
		        noteChair_1_8.setq(d1.getQualityDimensionIds());
		         double[] pvnoteChair_1_8 = {0.18346,0.00301475,0.00128746,7.72915e-05,2.42548e-08,4.11577e-06,2.48525e-09};
		        noteChair_1_8.setv(pvnoteChair_1_8);
		  
		 //Fim NoteChair3 
		 
		 //blackChair Color
                
           CSMLPoint blackChair_color1 = m.createCSMLPoint("blackChair_color1", color_domain);
	            blackChair_color1.setq(color_domain.getQualityDimensionIds());
	            double[] pvblackChair_color1 = {87.0, 83.0, 79.0};
				//double[] pvblackChair_color1 = {0.262745098,	0.2980392157,	0.2431372549};
	            blackChair_color1.setv(pvblackChair_color1);
                
          CSMLPoint blackChair_color2 = m.createCSMLPoint("blackChair8_color2", color_domain);
	           blackChair_color2.setq(color_domain.getQualityDimensionIds());
	           double[] pvblackChair_color2 = {29.0, 27.0, 25.0};
			   //double[] pvblackChair_color2 = {0.1137254902,	0.1058823529,	0.0980392157};
	           blackChair_color2.setv(pvblackChair_color2);
                
         CSMLPoint blackChair_color3 = m.createCSMLPoint("blackChair_color3", color_domain);
	           blackChair_color3.setq(color_domain.getQualityDimensionIds());
	           double[] pvpvblackChair_color3 = { 37.0, 36.0, 35.0};
			   //double[] pvpvblackChair_color3 = { 0.1450980392,	0.1411764706,	0.137254902};
	           blackChair_color3.setv(pvpvblackChair_color3);
                
         CSMLPoint blackChair_color4 = m.createCSMLPoint("blackChair_color4", color_domain);
	           blackChair_color4.setq(color_domain.getQualityDimensionIds());
	           double[] pvblackChair_color4 = { 45.0, 41.0, 40.0 };
			   //double[] pvblackChair_color4 = { 0.168627451,	0.1607843137,	0.1568627451};
	           blackChair_color4.setv(pvblackChair_color4);
                
         //Fim blackChair - (Color)
		 
		 //Quadro Pri Color - (Size)
                
         CSMLPoint blackChair_size1 = m.createCSMLPoint("blackChair_size1", size_domain);
	           blackChair_size1.setq(size_domain.getQualityDimensionIds());
	           double[] pvblackChair_size1 = { 140.0, 285.0 };
	           blackChair_size1.setv(pvblackChair_size1);
                
         CSMLPoint blackChair_size2 = m.createCSMLPoint("blackChair_size2", size_domain);
	          blackChair_size2.setq(size_domain.getQualityDimensionIds());
	          double[] pvblackChair_size2 = { 152.0,  321.0 };
	          blackChair_size2.setv(pvblackChair_size2);
                
        CSMLPoint blackChair_size3 = m.createCSMLPoint("blackChair_size3", size_domain);
	          blackChair_size3.setq(size_domain.getQualityDimensionIds());
	          double[] pvblackChair_size3 = {153.0, 322.0};
	          blackChair_size3.setv(pvblackChair_size3);
                
        // Fim Quadro Pri (Size)
		
		
		ArrayList<CSMLPoint> blackChair_points = new ArrayList();
		       blackChair_points.add(0, blackChair1);
		       blackChair_points.add(1, blackChair2);
		       blackChair_points.add(2, blackChair3);
		       blackChair_points.add(3, blackChair4);
		       blackChair_points.add(4, blackChair5);
		       blackChair_points.add(5, blackChair6);
		       blackChair_points.add(6, blackChair7);
		       blackChair_points.add(7, blackChair8);
			   
		//blackChair2
		
		ArrayList<CSMLPoint> blackChair_2_points = new ArrayList();
		       blackChair_2_points.add(0, blackChair_2_1);
		       blackChair_2_points.add(1, blackChair_2_2);
		       blackChair_2_points.add(2, blackChair_2_3);
		       blackChair_2_points.add(3, blackChair_2_4);
		       blackChair_2_points.add(4, blackChair_2_5);
		       blackChair_2_points.add(5, blackChair_2_6);
		       blackChair_2_points.add(6, blackChair_2_7);
		       blackChair_2_points.add(7, blackChair_2_8);
			   
		//Fim BlackChair2
		
		// BlackChair3	
		
		ArrayList<CSMLPoint> blackChair_3_points = new ArrayList();
		       blackChair_3_points.add(0, blackChair_3_1);
		       blackChair_3_points.add(1, blackChair_3_2);
		       blackChair_3_points.add(2, blackChair_3_3);
		       blackChair_3_points.add(3, blackChair_3_4);
		       blackChair_3_points.add(4, blackChair_3_5);
		       blackChair_3_points.add(5, blackChair_3_6);
		       blackChair_3_points.add(6, blackChair_3_7);
		       blackChair_3_points.add(7, blackChair_3_8);
			   
	  //Fim BlackChair3	

	  ArrayList<CSMLPoint>noteChair_1_points = new ArrayList();
		      noteChair_1_points.add(0,noteChair_1_1);
		      noteChair_1_points.add(1,noteChair_1_2);
		      noteChair_1_points.add(2,noteChair_1_3);
		      noteChair_1_points.add(3,noteChair_1_4);
		      noteChair_1_points.add(4,noteChair_1_5);
		      noteChair_1_points.add(5,noteChair_1_6);
		      noteChair_1_points.add(6,noteChair_1_7);
		      noteChair_1_points.add(7,noteChair_1_8);
			  
	 ArrayList<CSMLPoint> blackChair_color_points = new ArrayList();
               blackChair_color_points.add(0, blackChair_color1);
               blackChair_color_points.add(1, blackChair_color2);
               blackChair_color_points.add(2, blackChair_color3);
               blackChair_color_points.add(3, blackChair_color4);
                
     ArrayList<CSMLPoint> blackChair_size_points1 = new ArrayList();
               blackChair_size_points1.add(0, blackChair_size1);
               blackChair_size_points1.add(1, blackChair_size2);
               blackChair_size_points1.add(2, blackChair_size3);
	
	//Fim Black Chair
		 
	//Fim - All BlackChair	 
		 
		 CSMLPoint warDrobe_1_1 = m.createCSMLPoint("warDrobe_1_1", d1);
		          warDrobe_1_1.setq(d1.getQualityDimensionIds());
		           double[] pvwarDrobe_1_1 = {0.490874,0.0194305,0.00515489,0.00312796,1.01042e-05,-0.000163816,-7.46097e-06 };
		          warDrobe_1_1.setv(pvwarDrobe_1_1); 
					  
		   CSMLPoint warDrobe_1_2 = m.createCSMLPoint("warDrobe_1_2", d1);
		          warDrobe_1_2.setq(d1.getQualityDimensionIds());
		           double[] pvwarDrobe_1_2 = {0.427786,0.50898,0.0157551,0.00236141,0.00264467,6.21446e-06,-0.000195726,-2.24963e-06};
		          warDrobe_1_2.setv(pvwarDrobe_1_2);
					  
		   CSMLPoint warDrobe_1_3 = m.createCSMLPoint("warDrobe_1_3", d1);
		          warDrobe_1_3.setq(d1.getQualityDimensionIds());
		           double[] pvwarDrobe_1_3 = {0.484966,0.0149283,0.0069371,0.00129801,-2.39949e-06,6.90847e-05,-3.06809e-06 };
		          warDrobe_1_3.setv(pvwarDrobe_1_3);
					  
		  CSMLPoint warDrobe_1_4 = m.createCSMLPoint("warDrobe_1_4", d1);
		          warDrobe_1_4.setq(d1.getQualityDimensionIds());
		           double[] pvwarDrobe_1_4 = {0.477811,0.0189513,0.00357493,0.00262327,-6.59747e-06,0.000249359,-4.58355e-06};
		          warDrobe_1_4.setv(pvwarDrobe_1_4);
					  
		  CSMLPoint warDrobe_1_5 = m.createCSMLPoint("warDrobe_1_5", d1);
		          warDrobe_1_5.setq(d1.getQualityDimensionIds());
		           double[] pvwarDrobe_1_5 = {0.453884,0.0128058,0.00125874,0.0036063,-4.41442e-06,0.00019449,-6.28883e-06 };
		          warDrobe_1_5.setv(pvwarDrobe_1_5);
					  
		  CSMLPoint warDrobe_1_6 = m.createCSMLPoint("warDrobe_1_6", d1);
		          warDrobe_1_6.setq(d1.getQualityDimensionIds());
		           double[] pvwarDrobe_1_6 = {0.574043,0.0432923,0.050428,0.00164961,1.43466e-05,-0.000265162,-4.53223e-06};
		          warDrobe_1_6.setv(pvwarDrobe_1_6);
					  
		  CSMLPoint warDrobe_1_7 = m.createCSMLPoint("warDrobe_1_7", d1);
		         warDrobe_1_7.setq(d1.getQualityDimensionIds());
		          double[] pvwarDrobe_1_7 = {0.547597,0.0359434,0.0262438,0.00233672,5.30745e-06,-9.41658e-05,-1.75123e-05};
		         warDrobe_1_7.setv(pvwarDrobe_1_7);
					  
		  CSMLPoint warDrobe_1_8 = m.createCSMLPoint("warDrobe_1_8", d1);
		        warDrobe_1_8.setq(d1.getQualityDimensionIds());
		         double[] pvwarDrobe_1_8 = {0.435466,0.0123041,0.000674067,0.0044744,-5.85119e-06,0.000344755,-5.11327e-06};
		        warDrobe_1_8.setv(pvwarDrobe_1_8);
					  
					  
		  //Fim warDrobe_1
		  CSMLPoint warDrobe_2_1 = m.createCSMLPoint("warDrobe_2_1", d1);
		          warDrobe_2_1.setq(d1.getQualityDimensionIds());
		           double[] pvwarDrobe_2_1 = {0.191089,0.000293897,0.000223599,2.23408e-05,8.70972e-10,2.27022e-07,-1.31706e-09 };
		          warDrobe_2_1.setv(pvwarDrobe_2_1); 
					  
		   CSMLPoint warDrobe_2_2 = m.createCSMLPoint("warDrobe_2_2", d1);
		          warDrobe_2_2.setq(d1.getQualityDimensionIds());
		           double[] pvwarDrobe_2_2 = {0.192679,0.00149273,7.77638e-05,7.35172e-06,1.69663e-10,2.66653e-07,-4.59724e-11};
		          warDrobe_2_2.setv(pvwarDrobe_2_2);
					  
		   CSMLPoint warDrobe_2_3 = m.createCSMLPoint("warDrobe_2_3", d1);
		          warDrobe_2_3.setq(d1.getQualityDimensionIds());
		           double[] pvwarDrobe_2_3 = {0.174834,0.00028914,4.38366e-05,1.2529e-05,6.5434e-11,1.25409e-08,-2.86241e-10 };
		          warDrobe_2_3.setv(pvwarDrobe_2_3);
					  
		  CSMLPoint warDrobe_2_4 = m.createCSMLPoint("warDrobe_2_4", d1);
		          warDrobe_2_4.setq(d1.getQualityDimensionIds());
		           double[] pvwarDrobe_2_4 = {0.181322,0.000324551,1.10532e-05,2.35161e-05,3.74651e-10,-2.78713e-07,5.81197e-11};
		          warDrobe_2_4.setv(pvwarDrobe_2_4);
					  
		  CSMLPoint warDrobe_2_5 = m.createCSMLPoint("warDrobe_2_5", d1);
		          warDrobe_2_5.setq(d1.getQualityDimensionIds());
		           double[] pvwarDrobe_2_5 = {0.182463,0.000357758,4.87077e-05,1.97595e-05,6.00291e-10,-1.49091e-07,-1.24189e-10 };
		          warDrobe_2_5.setv(pvwarDrobe_2_5);
					  
		  CSMLPoint warDrobe_2_6 = m.createCSMLPoint("warDrobe_2_6", d1);
		          warDrobe_2_6.setq(d1.getQualityDimensionIds());
		           double[] pvwarDrobe_2_6 = {0.204006,0.000612541,0.000500222,0.000308431,-1.15933e-07,-7.2365e-06,-3.51646e-08};
		          warDrobe_2_6.setv(pvwarDrobe_2_6);
					  
		  CSMLPoint warDrobe_2_7 = m.createCSMLPoint("warDrobe_2_7", d1);
		         warDrobe_2_7.setq(d1.getQualityDimensionIds());
		          double[] pvwarDrobe_2_7 = {0.193099,0.00156805,3.08487e-05,3.74204e-07,2.08258e-13,1.24479e-08,-1.25422e-12};
		         warDrobe_2_7.setv(pvwarDrobe_2_7);
					  
		  CSMLPoint warDrobe_2_8 = m.createCSMLPoint("warDrobe_2_8", d1);
		        warDrobe_2_8.setq(d1.getQualityDimensionIds());
		         double[] pvwarDrobe_2_8 = {0.189961,0.000222637,1.13e-05,4.67478e-07,-3.24514e-13,-5.67493e-09,1.02426e-12};
		        warDrobe_2_8.setv(pvwarDrobe_2_8);
					  

					  
		  //FimwarDrobe_2_ 
		  
		  CSMLPoint warDrobe_3_1 = m.createCSMLPoint("warDrobe_3_1", d1);
		          warDrobe_3_1.setq(d1.getQualityDimensionIds());
		           double[] pvwarDrobe_3_1 = {0.192305,0.00076312,0.000196517,3.6141e-06,-6.328e-11,7.78986e-08,7.26116e-11 };
		          warDrobe_3_1.setv(pvwarDrobe_3_1); 
					  
		   CSMLPoint warDrobe_3_2 = m.createCSMLPoint("warDrobe_3_2", d1);
		          warDrobe_3_2.setq(d1.getQualityDimensionIds());
		           double[] pvwarDrobe_3_2 = {0.223511,0.000689308,0.000480629,6.51876e-05,8.95968e-09,1.46652e-06,-7.27072e-09};
		          warDrobe_3_2.setv(pvwarDrobe_3_2);
					  
		   CSMLPoint warDrobe_3_3 = m.createCSMLPoint("warDrobe_3_3", d1);
		          warDrobe_3_3.setq(d1.getQualityDimensionIds());
		           double[] pvwarDrobe_3_3 = {0.206615,0.000304547,4.29319e-06,4.99165e-06,-1.90895e-11,6.00355e-08,1.30212e-11 };
		          warDrobe_3_3.setv(pvwarDrobe_3_3);
					  
		  CSMLPoint warDrobe_3_4 = m.createCSMLPoint("warDrobe_3_4", d1);
		          warDrobe_3_4.setq(d1.getQualityDimensionIds());
		           double[] pvwarDrobe_3_4 = {0.229617,0.00119392,0.000169349,6.72328e-05,7.23979e-10,2.29115e-06,-7.13739e-09};
		          warDrobe_3_4.setv(pvwarDrobe_3_4);
					  
		  CSMLPoint warDrobe_3_5 = m.createCSMLPoint("warDrobe_3_5", d1);
		          warDrobe_3_5.setq(d1.getQualityDimensionIds());
		           double[] pvwarDrobe_3_5 = {0.206538,0.00127698,0.000154848,2.455e-06,-1.26889e-11,4.49156e-08,4.61539e-11 };
		          warDrobe_3_5.setv(pvwarDrobe_3_5);
					  
		  CSMLPoint warDrobe_3_6 = m.createCSMLPoint("warDrobe_3_6", d1);
		          warDrobe_3_6.setq(d1.getQualityDimensionIds());
		           double[] pvwarDrobe_3_6 = {0.230201,0.000542559,0.000410095,6.36164e-05,7.95755e-09,1.00077e-06,-6.50076e-09};
		          warDrobe_3_6.setv(pvwarDrobe_3_6);
					  
		  CSMLPoint warDrobe_3_7 = m.createCSMLPoint("warDrobe_3_7", d1);
		         warDrobe_3_7.setq(d1.getQualityDimensionIds());
		          double[] pvwarDrobe_3_7 = {0.228127,0.000520721,0.000631007,5.12694e-05,7.9008e-09,6.98362e-07,-4.75548e-09};
		         warDrobe_3_7.setv(pvwarDrobe_3_7);
					  
		  CSMLPoint warDrobe_3_8 = m.createCSMLPoint("warDrobe_3_8", d1);
		        warDrobe_3_8.setq(d1.getQualityDimensionIds());
		         double[] pvwarDrobe_3_8 = {0.230418,0.000388541,0.000151389,7.18126e-06,-2.32667e-10,-1.35714e-07,-4.39489e-11};
		        warDrobe_3_8.setv(pvwarDrobe_3_8);
					  
		//Fim warDrobe_3_ 
		  			  		
		
		ArrayList<CSMLPoint>warDrobe_1_points = new ArrayList();
		      warDrobe_1_points.add(0,warDrobe_1_1);
		      warDrobe_1_points.add(1,warDrobe_1_2);
		      warDrobe_1_points.add(2,warDrobe_1_3);
		      warDrobe_1_points.add(3,warDrobe_1_4);
		      warDrobe_1_points.add(4,warDrobe_1_5);
		      warDrobe_1_points.add(5,warDrobe_1_6);
		      warDrobe_1_points.add(6,warDrobe_1_7);
		      warDrobe_1_points.add(7,warDrobe_1_8);
			  
		ArrayList<CSMLPoint>warDrobe_2_points = new ArrayList();
		      warDrobe_2_points.add(0,warDrobe_2_1);
		      warDrobe_2_points.add(1,warDrobe_2_2);
		      warDrobe_2_points.add(2,warDrobe_2_3);
		      warDrobe_2_points.add(3,warDrobe_2_4);
		      warDrobe_2_points.add(4,warDrobe_2_5);
		      warDrobe_2_points.add(5,warDrobe_2_6);
		      warDrobe_2_points.add(6,warDrobe_2_7);
		      warDrobe_2_points.add(7,warDrobe_2_8);	  
			  
		ArrayList<CSMLPoint>warDrobe_3_points = new ArrayList();
		      warDrobe_3_points.add(0,warDrobe_3_1);
		      warDrobe_3_points.add(1,warDrobe_3_2);
		      warDrobe_3_points.add(2,warDrobe_3_3);
		      warDrobe_3_points.add(3,warDrobe_3_4);
		      warDrobe_3_points.add(4,warDrobe_3_5);
		      warDrobe_3_points.add(5,warDrobe_3_6);
		      warDrobe_3_points.add(6,warDrobe_3_7);
		      warDrobe_3_points.add(7,warDrobe_3_8);	  
			  
       //Blue Door
	   
         CSMLPoint blueDoor1 = m.createCSMLPoint("blueDoor1", d1);
		           blueDoor1.setq(d1.getQualityDimensionIds());
		           double[] pvblueDoor1 = {0.291743,0.0565012,0.000100163,3.05264e-05,1.64302e-09,6.87247e-06,-3.86987e-10};
		           blueDoor1.setv(pvblueDoor1); 
					  
		   CSMLPoint blueDoor2 = m.createCSMLPoint("blueDoor2", d1);
		           blueDoor2.setq(d1.getQualityDimensionIds());
		           double[] pvblueDoor2 = {0.291705,0.0565464,0.000114929,3.53284e-05,2.17862e-09,7.82585e-06,-5.66741e-10};
		           blueDoor2.setv(pvblueDoor2);
					  
		   CSMLPoint blueDoor3 = m.createCSMLPoint("blueDoor3", d1);
		           blueDoor3.setq(d1.getQualityDimensionIds());
		           double[] pvblueDoor3 = {0.296048,0.0586381,0.00011761,3.48267e-05,2.04678e-09,7.06657e-06,-8.82431e-10 };
		           blueDoor3.setv(pvblueDoor3);
					  
		  CSMLPoint blueDoor4 = m.createCSMLPoint("blueDoor4", d1);
		           blueDoor4.setq(d1.getQualityDimensionIds());
		           double[] pvblueDoor4 = {0.291437,0.0562635,8.27887e-05,2.50702e-05,1.06092e-09,5.10044e-06,-4.23019e-10};
		           blueDoor4.setv(pvblueDoor4);
					  
		  CSMLPoint blueDoor5 = m.createCSMLPoint("blueDoor5", d1);
		           blueDoor5.setq(d1.getQualityDimensionIds());
		           double[] pvblueDoor5 = {0.291258,0.0562613,9.31743e-05,2.76457e-05,1.33732e-09,5.90462e-06,-4.24597e-10 };
		           blueDoor5.setv(pvblueDoor5);
					  
		  CSMLPoint blueDoor6 = m.createCSMLPoint("blueDoor6", d1);
		           blueDoor6.setq(d1.getQualityDimensionIds());
		           double[] pvblueDoor6 = {0.28746,0.0541565,0.000128463,3.69902e-05,2.47199e-09,8.10002e-06,-6.25366e-10};
		           blueDoor6.setv(pvblueDoor6);
					  
		  CSMLPoint blueDoor7 = m.createCSMLPoint("blueDoor7", d1);
		          blueDoor7.setq(d1.getQualityDimensionIds());
		          double[] pvblueDoor7 = {0.290321,0.0556989,0.000108313,3.31216e-05,1.95853e-09,7.62059e-06,-3.15922e-10};
		          blueDoor7.setv(pvblueDoor7);
					  
		  CSMLPoint blueDoor8 = m.createCSMLPoint("blueDoor8", d1);
		         blueDoor8.setq(d1.getQualityDimensionIds());
		         double[] pvblueDoor8 = {0.296499,0.0592357,9.39866e-05,2.17935e-05,9.66688e-10,5.18341e-06,-1.95888e-10};
		         blueDoor8.setv(pvblueDoor8);
					  
		  //Fim blueDoor 
		  
		  // blueDoor 2
		  
		  CSMLPoint blueDoor_2_1 = m.createCSMLPoint("blueDoor_2_1", d1);
		           blueDoor_2_1.setq(d1.getQualityDimensionIds());
		           double[] pvblueDoor_2_1 = {0.298405,0.0598441,0.000145806,4.60985e-05,3.5348e-09,9.78031e-06,-1.33743e-09 };
		           blueDoor_2_1.setv(pvblueDoor_2_1); 
					  
		   CSMLPoint blueDoor_2_2 = m.createCSMLPoint("blueDoor_2_2", d1);
		           blueDoor_2_2.setq(d1.getQualityDimensionIds());
		           double[] pvblueDoor_2_2 = {0.298594,0.0600371,0.000170979,5.67897e-05,5.28466e-09,1.21751e-05,-1.84049e-09};
		           blueDoor_2_2.setv(pvblueDoor_2_2);
					  
		   CSMLPoint blueDoor_2_3 = m.createCSMLPoint("blueDoor_2_3", d1);
		           blueDoor_2_3.setq(d1.getQualityDimensionIds());
		           double[] pvblueDoor_2_3 = {0.298253,0.0598374,0.000176533,5.92219e-05,5.7833e-09,1.30219e-05,-1.79448e-09 };
		           blueDoor_2_3.setv(pvblueDoor_2_3);
					  
		  CSMLPoint blueDoor_2_4 = m.createCSMLPoint("blueDoor_2_4", d1);
		           blueDoor_2_4.setq(d1.getQualityDimensionIds());
		           double[] pvblueDoor_2_4 = {0.298064,0.0597022,0.000197323,6.5352e-05,7.11443e-09,1.45545e-05,-2.11189e-09};
		           blueDoor_2_4.setv(pvblueDoor_2_4);
					  
		  CSMLPoint blueDoor_2_5 = m.createCSMLPoint("blueDoor_2_5", d1);
		           blueDoor_2_5.setq(d1.getQualityDimensionIds());
		           double[] pvblueDoor_2_5 = {0.298646,0.0600019,0.000189862,6.17744e-05,6.35619e-09,1.3524e-05,-2.08715e-09 };
		           blueDoor_2_5.setv(pvblueDoor_2_5);
					  
		  CSMLPoint blueDoor_2_6 = m.createCSMLPoint("blueDoor_2_6", d1);
		           blueDoor_2_6.setq(d1.getQualityDimensionIds());
		           double[] pvblueDoor_2_6 = {0.29878,0.0600938,0.000207164,6.56603e-05,7.18677e-09,1.39941e-05,-2.64465e-09};
		           blueDoor_2_6.setv(pvblueDoor_2_6);
					  
		  CSMLPoint blueDoor_2_7 = m.createCSMLPoint("blueDoor_2_7", d1);
		          blueDoor_2_7.setq(d1.getQualityDimensionIds());
		          double[] pvblueDoor_2_7 = {0.298636,0.0599424,0.000201357,6.75219e-05,7.47333e-09,1.46641e-05,-2.47715e-09};
		          blueDoor_2_7.setv(pvblueDoor_2_7);
					  
		  CSMLPoint blueDoor_2_8 = m.createCSMLPoint("blueDoor_2_8", d1);
		         blueDoor_2_8.setq(d1.getQualityDimensionIds());
		         double[] pvblueDoor_2_8 = {0.299672,0.0605714,0.000212042,7.29935e-05,8.65997e-09,1.60412e-05,-2.73326e-09};
		         blueDoor_2_8.setv(pvblueDoor_2_8);
					  		  
		  //Fim blueDoor 2
			  
		  //blueDoor Color
                
          CSMLPoint blueDoor_color1 = m.createCSMLPoint("blueDoor_color1", color_domain);
	            blueDoor_color1.setq(color_domain.getQualityDimensionIds());
	            double[] pvblueDoor_color1 = {54.0, 62.0, 85.0};
	            blueDoor_color1.setv(pvblueDoor_color1);
                
          CSMLPoint blueDoor_color2 = m.createCSMLPoint("blueDoor8_color2", color_domain);
	           blueDoor_color2.setq(color_domain.getQualityDimensionIds());
	           double[] pvblueDoor_color2 = {20.0, 25.0, 41.0};
	           blueDoor_color2.setv(pvblueDoor_color2);
                
         CSMLPoint blueDoor_color3 = m.createCSMLPoint("blueDoor_color3", color_domain);
	           blueDoor_color3.setq(color_domain.getQualityDimensionIds());
	           double[] pvblueDoor_color3 = { 28.0, 34.0, 52.0};
	           blueDoor_color3.setv(pvblueDoor_color3);
                
         CSMLPoint blueDoor_color4 = m.createCSMLPoint("blueDoor_color4", color_domain);
	           blueDoor_color4.setq(color_domain.getQualityDimensionIds());
	           double[] pvblueDoor_color4 = { 36.0, 44.0, 62.0 };
	           blueDoor_color4.setv(pvblueDoor_color4);
                
         //Fim blueDoor - (Color)
			 
		 //Quadro Pri Color - (Size)
                
         CSMLPoint blueDoor_size1 = m.createCSMLPoint("blueDoor_size1", size_domain);
	           blueDoor_size1.setq(size_domain.getQualityDimensionIds());
	           double[] pvblueDoor_size1 = { 160.0, 359.0 };
	           blueDoor_size1.setv(pvblueDoor_size1);
                
         CSMLPoint blueDoor_size2 = m.createCSMLPoint("blueDoor_size2", size_domain);
	          blueDoor_size2.setq(size_domain.getQualityDimensionIds());
	          double[] pvblueDoor_size2 = { 163.0,  366.0 };
	          blueDoor_size2.setv(pvblueDoor_size2);
                
        CSMLPoint blueDoor_size3 = m.createCSMLPoint("blueDoor_size3", size_domain);
	          blueDoor_size3.setq(size_domain.getQualityDimensionIds());
	          double[] pvblueDoor_size3 = {159.0, 355.0};
	          blueDoor_size3.setv(pvblueDoor_size3);
                
        // Fim Quadro Pri (Size)
		
		
		ArrayList<CSMLPoint> blueDoor_points = new ArrayList();
		       blueDoor_points.add(0, blueDoor1);
		       blueDoor_points.add(1, blueDoor2);
		       blueDoor_points.add(2, blueDoor3);
		       blueDoor_points.add(3, blueDoor4);
		       blueDoor_points.add(4, blueDoor5);
		       blueDoor_points.add(5, blueDoor6);
		       blueDoor_points.add(6, blueDoor7);
		       blueDoor_points.add(7, blueDoor8);
			   
		ArrayList<CSMLPoint> blueDoor_2_points = new ArrayList();
		       blueDoor_2_points.add(0, blueDoor_2_1);
		       blueDoor_2_points.add(1, blueDoor_2_2);
		       blueDoor_2_points.add(2, blueDoor_2_3);
		       blueDoor_2_points.add(3, blueDoor_2_4);
		       blueDoor_2_points.add(4, blueDoor_2_5);
		       blueDoor_2_points.add(5, blueDoor_2_6);
		       blueDoor_2_points.add(6, blueDoor_2_7);
		       blueDoor_2_points.add(7, blueDoor_2_8);	   
			   

     ArrayList<CSMLPoint> blueDoor_color_points = new ArrayList();
               blueDoor_color_points.add(0, blueDoor_color1);
               blueDoor_color_points.add(1, blueDoor_color2);
               blueDoor_color_points.add(2, blueDoor_color3);
               blueDoor_color_points.add(3, blueDoor_color4);
                
     ArrayList<CSMLPoint> blueDoor_size_points1 = new ArrayList();
               blueDoor_size_points1.add(0, blueDoor_size1);
               blueDoor_size_points1.add(1, blueDoor_size2);
               blueDoor_size_points1.add(2, blueDoor_size3);
		
	//Fim Blue Door
	
		
		
		
	 CSMLPoint roundTable1 = m.createCSMLPoint("roundTable1", d1);
		           roundTable1.setq(d1.getQualityDimensionIds());
		           double[] pvroundTable1 = {0.202062,0.0154878,5.97354e-07,1.16144e-07,3.03772e-14,1.41217e-08,-3.62048e-15 };
		           roundTable1.setv(pvroundTable1); 
					  
		   CSMLPoint roundTable2 = m.createCSMLPoint("roundTable2", d1);
		           roundTable2.setq(d1.getQualityDimensionIds());
		           double[] pvroundTable2 = {0.204453,0.0164609,2.27127e-07,3.96573e-08,3.49355e-15,3.99151e-09,-1.40031e-15};
		           roundTable2.setv(pvroundTable2);
					  
		   CSMLPoint roundTable3 = m.createCSMLPoint("roundTable3", d1);
		           roundTable3.setq(d1.getQualityDimensionIds());
		           double[] pvroundTable3 = {0.204477,0.0164653,7.57964e-08,1.60149e-08,5.2714e-16,1.75554e-09,-1.82906e-16 };
		           roundTable3.setv(pvroundTable3);
					  
		  CSMLPoint roundTable4 = m.createCSMLPoint("roundTable4", d1);
		           roundTable4.setq(d1.getQualityDimensionIds());
		           double[] pvroundTable4 = {0.203829,0.0161926,1.26886e-07,3.32279e-08,2.15479e-15,4.22655e-09,1.09176e-16};
		           roundTable4.setv(pvroundTable4);
					  
		  CSMLPoint roundTable5 = m.createCSMLPoint("roundTable5", d1);
		           roundTable5.setq(d1.getQualityDimensionIds());
		           double[] pvroundTable5 = {0.203864,0.016213,3.73164e-07,7.1251e-08,1.12867e-14,7.98711e-09,-2.75531e-15 };
		           roundTable5.setv(pvroundTable5);
					  
		  CSMLPoint roundTable6 = m.createCSMLPoint("roundTable6", d1);
		           roundTable6.setq(d1.getQualityDimensionIds());
		           double[] pvroundTable6 = {0.20481,0.0166033,4.27208e-07,9.28901e-08,1.842e-14,1.1703e-08,-1.7644e-15};
		           roundTable6.setv(pvroundTable6);
					  
		  CSMLPoint roundTable7 = m.createCSMLPoint("roundTable7", d1);
		          roundTable7.setq(d1.getQualityDimensionIds());
		          double[] pvroundTable7 = {0.204876,0.0166278,5.12203e-08,1.28409e-08,3.29256e-16,1.65581e-09,6.30059e-18};
		          roundTable7.setv(pvroundTable7);
					  
		  CSMLPoint roundTable8 = m.createCSMLPoint("roundTable8", d1);
		         roundTable8.setq(d1.getQualityDimensionIds());
		         double[] pvroundTable8 = {0.205002,0.0166706,3.68462e-07,3.81326e-08,2.97036e-15,1.87709e-09,-3.40699e-15};
		         roundTable8.setv(pvroundTable8);
					  
		  //Fim roundTable 
		  
		  //roundTable2
		  
		   CSMLPoint roundTable_2_1 = m.createCSMLPoint("roundTable_2_1", d1);
		           roundTable_2_1.setq(d1.getQualityDimensionIds());
		           double[] pvroundTable_2_1 = {0.237127,0.0255762,0.00109612,7.73553e-05,-1.41459e-08,-1.04379e-05,1.75291e-08 };
		           roundTable_2_1.setv(pvroundTable_2_1); 
					  
		   CSMLPoint roundTable_2_2 = m.createCSMLPoint("roundTable_2_2", d1);
		           roundTable_2_2.setq(d1.getQualityDimensionIds());
		           double[] pvroundTable_2_2 = {0.242409,0.0243765,0.00180394,0.000280251,9.69674e-08,9.51574e-06,-1.7408e-07};
		           roundTable_2_2.setv(pvroundTable_2_2);
					  
		   CSMLPoint roundTable_2_3 = m.createCSMLPoint("roundTable_2_3", d1);
		           roundTable_2_3.setq(d1.getQualityDimensionIds());
		           double[] pvroundTable_2_3 = {0.233315,0.024621,0.00100888,6.29418e-05,-1.5794e-08,-9.87574e-06,1.45592e-09 };
		           roundTable_2_3.setv(pvroundTable_2_3);
					  
		  CSMLPoint roundTable_2_4 = m.createCSMLPoint("roundTable_2_4", d1);
		           roundTable_2_4.setq(d1.getQualityDimensionIds());
		           double[] pvroundTable_2_4 = {0.232605,0.0241975,0.00100083,6.42872e-05,-1.45567e-08,-9.65136e-06,7.34929e-09};
		           roundTable_2_4.setv(pvroundTable_2_4);
					  
		  CSMLPoint roundTable_2_5 = m.createCSMLPoint("roundTable_2_5", d1);
		           roundTable_2_5.setq(d1.getQualityDimensionIds());
		           double[] pvroundTable_2_5 = {0.233342,0.0242343,0.00109453,6.98664e-05,-1.60586e-08,-1.02443e-05,1.07424e-08};
		           roundTable_2_5.setv(pvroundTable_2_5);
					  
		  CSMLPoint roundTable_2_6 = m.createCSMLPoint("roundTable_2_6", d1);
		           roundTable_2_6.setq(d1.getQualityDimensionIds());
		           double[] pvroundTable_2_6 = {0.233368,0.02484,0.000917007,5.95859e-05,-1.06818e-08,-8.48074e-06,8.93871e-09};
		           roundTable_2_6.setv(pvroundTable_2_6);
					  
		  CSMLPoint roundTable_2_7 = m.createCSMLPoint("roundTable_2_7", d1);
		          roundTable_2_7.setq(d1.getQualityDimensionIds());
		          double[] pvroundTable_2_7 = {0.23332,0.0247071,0.000918777,6.08547e-05,-1.05006e-08,-8.48368e-06,9.83851e-09};
		          roundTable_2_7.setv(pvroundTable_2_7);
					  
		  CSMLPoint roundTable_2_8 = m.createCSMLPoint("roundTable_2_8", d1);
		         roundTable_2_8.setq(d1.getQualityDimensionIds());
		         double[] pvroundTable_2_8 = {0.232532,0.0244673,0.000895378,5.60399e-05,-1.11582e-08,-8.43212e-06,5.75105e-09};
		         roundTable_2_8.setv(pvroundTable_2_8);
		  
		  //Fim roundTable2
		  //roundTable3
		  
		        CSMLPoint roundTable_3_1 = m.createCSMLPoint("roundTable_3_1", d1);
		           roundTable_3_1.setq(d1.getQualityDimensionIds());
		           double[] pvroundTable_3_1 = {0.252168,0.0354609,0.000629035,0.000192001,5.76779e-08,2.27657e-05,-3.35492e-08 };
		           roundTable_3_1.setv(pvroundTable_3_1); 
					  
		   CSMLPoint roundTable_3_2 = m.createCSMLPoint("roundTable_3_2", d1);
		           roundTable_3_2.setq(d1.getQualityDimensionIds());
		           double[] pvroundTable_3_2 = {0.258571,0.0384132,0.00069282,0.000205113,6.48991e-08,2.34166e-05,-4.20321e-08};
		           roundTable_3_2.setv(pvroundTable_3_2);
					  
		   CSMLPoint roundTable_3_3 = m.createCSMLPoint("roundTable_3_3", d1);
		           roundTable_3_3.setq(d1.getQualityDimensionIds());
		           double[] pvroundTable_3_3 = {0.248793,0.0337305,0.000628059,0.0001904,5.71313e-08,2.28736e-05,-3.27284e-08 };
		           roundTable_3_3.setv(pvroundTable_3_3);
					  
		  CSMLPoint roundTable_3_4 = m.createCSMLPoint("roundTable_3_4", d1);
		           roundTable_3_4.setq(d1.getQualityDimensionIds());
		           double[] pvroundTable_3_4 = {0.249018,0.0337221,0.000618181,0.000181833,5.13672e-08,1.99346e-05,-3.28314e-08};
		           roundTable_3_4.setv(pvroundTable_3_4);
					  
		  CSMLPoint roundTable_3_5 = m.createCSMLPoint("roundTable_3_5", d1);
		           roundTable_3_5.setq(d1.getQualityDimensionIds());
		           double[] pvroundTable_3_5 = {0.248717,0.0337062,0.00062319,0.000187524,5.52009e-08,2.18562e-05,-3.25942e-08};
		           roundTable_3_5.setv(pvroundTable_3_5);
					  
		  CSMLPoint roundTable_3_6 = m.createCSMLPoint("roundTable_3_6", d1);
		           roundTable_3_6.setq(d1.getQualityDimensionIds());
		           double[] pvroundTable_3_6 = {0.250515,0.0344496,0.000716158,0.000228797,8.20504e-08,2.90359e-05,-4.29565e-08};
		           roundTable_3_6.setv(pvroundTable_3_6);
					  
		  CSMLPoint roundTable_3_7 = m.createCSMLPoint("roundTable_3_7", d1);
		          roundTable_3_7.setq(d1.getQualityDimensionIds());
		          double[] pvroundTable_3_7 = {0.248079,0.0334042,0.000613172,0.000189435,5.63386e-08,2.30727e-05,-3.15322e-08};
		          roundTable_3_7.setv(pvroundTable_3_7);
					  
		  CSMLPoint roundTable_3_8 = m.createCSMLPoint("roundTable_3_8", d1);
		         roundTable_3_8.setq(d1.getQualityDimensionIds());
		         double[] pvroundTable_3_8 = {0.249843,0.0340958,0.000682909,0.000188927,5.52907e-08,1.96665e-05,-3.93456e-08};
		         roundTable_3_8.setv(pvroundTable_3_8);
		  
           //Fim roundTable3
		  
		  //roundTable Color
                
          CSMLPoint roundTable_color1 = m.createCSMLPoint("roundTable_color1", color_domain);
	            roundTable_color1.setq(color_domain.getQualityDimensionIds());
	            double[] pvroundTable_color1 = {222.0, 216.0, 213.0};
	            roundTable_color1.setv(pvroundTable_color1);
                
          CSMLPoint roundTable_color2 = m.createCSMLPoint("roundTable8_color2", color_domain);
	           roundTable_color2.setq(color_domain.getQualityDimensionIds());
	           double[] pvroundTable_color2 = {167.0, 162.0, 160.0};
	           roundTable_color2.setv(pvroundTable_color2);
                
         CSMLPoint roundTable_color3 = m.createCSMLPoint("roundTable_color3", color_domain);
	           roundTable_color3.setq(color_domain.getQualityDimensionIds());
	           double[] pvroundTable_color3 = { 140.0, 136.0, 134.0};
	           roundTable_color3.setv(pvroundTable_color3);
                
         CSMLPoint roundTable_color4 = m.createCSMLPoint("roundTable_color4", color_domain);
	           roundTable_color4.setq(color_domain.getQualityDimensionIds());
	           double[] pvroundTable_color4 = { 113.0, 108.0, 107.0 };
	           roundTable_color4.setv(pvroundTable_color4);
                
         //Fim roundTable - (Color)
			 
		 //Quadro Pri Color - (Size)
                
         CSMLPoint roundTable_size1 = m.createCSMLPoint("roundTable_size1", size_domain);
	           roundTable_size1.setq(size_domain.getQualityDimensionIds());
	           double[] pvroundTable_size1 = { 623.0, 303.0 };
	           roundTable_size1.setv(pvroundTable_size1);
                
         CSMLPoint roundTable_size2 = m.createCSMLPoint("roundTable_size2", size_domain);
	          roundTable_size2.setq(size_domain.getQualityDimensionIds());
	          double[] pvroundTable_size2 = { 611.0,  292.0 };
	          roundTable_size2.setv(pvroundTable_size2);
                
        CSMLPoint roundTable_size3 = m.createCSMLPoint("roundTable_size3", size_domain);
	          roundTable_size3.setq(size_domain.getQualityDimensionIds());
	          double[] pvroundTable_size3 = {619.0, 300.0};
	          roundTable_size3.setv(pvroundTable_size3);
                
        // Fim Quadro Pri (Size)
		
		
		ArrayList<CSMLPoint> roundTable_points = new ArrayList();
		       roundTable_points.add(0, roundTable1);
		       roundTable_points.add(1, roundTable2);
		       roundTable_points.add(2, roundTable3);
		       roundTable_points.add(3, roundTable4);
		       roundTable_points.add(4, roundTable5);
		       roundTable_points.add(5, roundTable6);
		       roundTable_points.add(6, roundTable7);
		       roundTable_points.add(7, roundTable8);
			   
	//	roundTable_2_points	   
	
	ArrayList<CSMLPoint> roundTable_2_points = new ArrayList();
		       roundTable_2_points.add(0, roundTable_2_1);
		       roundTable_2_points.add(1, roundTable_2_2);
		       roundTable_2_points.add(2, roundTable_2_3);
		       roundTable_2_points.add(3, roundTable_2_4);
		       roundTable_2_points.add(4, roundTable_2_5);
		       roundTable_2_points.add(5, roundTable_2_6);
		       roundTable_2_points.add(6, roundTable_2_7);
		       roundTable_2_points.add(7, roundTable_2_8);
			   
	// fim roundTable_2_points
	
	ArrayList<CSMLPoint> roundTable_3_points = new ArrayList();
		       roundTable_3_points.add(0, roundTable_3_1);
		       roundTable_3_points.add(1, roundTable_3_2);
		       roundTable_3_points.add(2, roundTable_3_3);
		       roundTable_3_points.add(3, roundTable_3_4);
		       roundTable_3_points.add(4, roundTable_3_5);
		       roundTable_3_points.add(5, roundTable_3_6);
		       roundTable_3_points.add(6, roundTable_3_7);
		       roundTable_3_points.add(7, roundTable_3_8);
   			   
		// fim roundTable_3_points

     ArrayList<CSMLPoint> roundTable_color_points = new ArrayList();
               roundTable_color_points.add(0, roundTable_color1);
               roundTable_color_points.add(1, roundTable_color2);
               roundTable_color_points.add(2, roundTable_color3);
               roundTable_color_points.add(3, roundTable_color4);
                
     ArrayList<CSMLPoint> roundTable_size_points1 = new ArrayList();
               roundTable_size_points1.add(0, roundTable_size1);
               roundTable_size_points1.add(1, roundTable_size2);
               roundTable_size_points1.add(2, roundTable_size3);
     
      //Fim roundTable

     CSMLPoint numberSeven1 = m.createCSMLPoint("numberSeven1", d1);
		           numberSeven1.setq(d1.getQualityDimensionIds());
		           double[] pvnumberSeven1 = {0.343751,0.0335272,0.0189289,0.00270461,2.07081e-06,0.000217146,-1.92406e-05 };
		           numberSeven1.setv(pvnumberSeven1); 
					  
		   CSMLPoint numberSeven2 = m.createCSMLPoint("numberSeven2", d1);
		           numberSeven2.setq(d1.getQualityDimensionIds());
		           double[] pvnumberSeven2 = {0.34341,0.0326453,0.0187392,0.00247595,-4.55986e-07,0.000157,-1.68589e-05};
		           numberSeven2.setv(pvnumberSeven2);
					  
		   CSMLPoint numberSeven3 = m.createCSMLPoint("numberSeven3", d1);
		           numberSeven3.setq(d1.getQualityDimensionIds());
		           double[] pvnumberSeven3 = {0.344502,0.0330386,0.0188469,0.00262707,1.06773e-06,0.00020359,-1.84544e-05 };
		           numberSeven3.setv(pvnumberSeven3);
					  
		  CSMLPoint numberSeven4 = m.createCSMLPoint("numberSeven4", d1);
		           numberSeven4.setq(d1.getQualityDimensionIds());
		           double[] pvnumberSeven4 = {0.343417,0.0336194,0.0184262,0.00255345,1.2137e-06,0.000204596,-1.74728e-05};
		           numberSeven4.setv(pvnumberSeven4);
					  
		  CSMLPoint numberSeven5 = m.createCSMLPoint("numberSeven5", d1);
		           numberSeven5.setq(d1.getQualityDimensionIds());
		           double[] pvnumberSeven5 = {0.343397,0.0330685,0.0187914,0.00263307,1.458e-06,0.000207187,-1.84639e-05 };
		           numberSeven5.setv(pvnumberSeven5);
					  
		  CSMLPoint numberSeven6 = m.createCSMLPoint("numberSeven6", d1);
		           numberSeven6.setq(d1.getQualityDimensionIds());
		           double[] pvnumberSeven6 = {0.339856,0.0337932,0.0156662,0.00203903,-1.23616e-06,0.000112863,-1.14578e-05};
		           numberSeven6.setv(pvnumberSeven6);
					  
		  CSMLPoint numberSeven7 = m.createCSMLPoint("numberSeven7", d1);
		          numberSeven7.setq(d1.getQualityDimensionIds());
		          double[] pvnumberSeven7 = {0.342582,0.0330682,0.0184985,0.00254242,1.21408e-06,0.000202973,-1.73934e-05};
		          numberSeven7.setv(pvnumberSeven7);
					  
		  CSMLPoint numberSeven8 = m.createCSMLPoint("numberSeven8", d1);
		         numberSeven8.setq(d1.getQualityDimensionIds());
		         double[] pvnumberSeven8 = {0.342226,0.03315,0.0181874,0.00249162,6.92457e-07,0.000189559,-1.67586e-05};
		         numberSeven8.setv(pvnumberSeven8);
					  
		  //Fim numberSeven 
			  
		  //numberSeven Color
                
          CSMLPoint numberSeven_color1 = m.createCSMLPoint("numberSeven_color1", color_domain);
	            numberSeven_color1.setq(color_domain.getQualityDimensionIds());
	            //double[] pvnumberSeven_color1 = {212.0, 193.0, 184.0};
				double[] pvnumberSeven_color1 = {0.831372549,	0.7568627451,	0.7215686275};
	            numberSeven_color1.setv(pvnumberSeven_color1);
                
          CSMLPoint numberSeven_color2 = m.createCSMLPoint("numberSeven8_color2", color_domain);
	           numberSeven_color2.setq(color_domain.getQualityDimensionIds());
	           //double[] pvnumberSeven_color2 = {138.0, 119.0, 114.0};
			   double[] pvnumberSeven_color2 = {0.5411764706,	0.4666666667,	0.4470588235};
	           numberSeven_color2.setv(pvnumberSeven_color2);
                
         CSMLPoint numberSeven_color3 = m.createCSMLPoint("numberSeven_color3", color_domain);
	           numberSeven_color3.setq(color_domain.getQualityDimensionIds());
	           //double[] pvnumberSeven_color3 = { 160.0, 137.0, 131.0};
			   double[] pvnumberSeven_color3 = { 0.6274509804,	0.537254902,	0.5137254902};
	           numberSeven_color3.setv(pvnumberSeven_color3);
                
         CSMLPoint numberSeven_color4 = m.createCSMLPoint("numberSeven_color4", color_domain);
	           numberSeven_color4.setq(color_domain.getQualityDimensionIds());
	           //double[] pvnumberSeven_color4 = { 177.0, 157.0, 150.0 };
			   double[] pvnumberSeven_color4 = { 0.6941176471,	0.6156862745,	0.5882352941};
	           numberSeven_color4.setv(pvnumberSeven_color4);
                
         //Fim numberSeven - (Color)
			 
		 //Quadro Pri Color - (Size)
                
         CSMLPoint numberSeven_size1 = m.createCSMLPoint("numberSeven_size1", size_domain);
	           numberSeven_size1.setq(size_domain.getQualityDimensionIds());
	           double[] pvnumberSeven_size1 = { 126.0, 173.0 };
	           numberSeven_size1.setv(pvnumberSeven_size1);
                
         CSMLPoint numberSeven_size2 = m.createCSMLPoint("numberSeven_size2", size_domain);
	          numberSeven_size2.setq(size_domain.getQualityDimensionIds());
	          double[] pvnumberSeven_size2 = { 135.0,  172.0 };
	          numberSeven_size2.setv(pvnumberSeven_size2);
                
        CSMLPoint numberSeven_size3 = m.createCSMLPoint("numberSeven_size3", size_domain);
	          numberSeven_size3.setq(size_domain.getQualityDimensionIds());
	          double[] pvnumberSeven_size3 = {123.0, 163.0};
	          numberSeven_size3.setv(pvnumberSeven_size3);
                
        // Fim Quadro Pri (Size)
		
		
		ArrayList<CSMLPoint> numberSeven_points = new ArrayList();
		       numberSeven_points.add(0, numberSeven1);
		       numberSeven_points.add(1, numberSeven2);
		       numberSeven_points.add(2, numberSeven3);
		       numberSeven_points.add(3, numberSeven4);
		       numberSeven_points.add(4, numberSeven5);
		       numberSeven_points.add(5, numberSeven6);
		       numberSeven_points.add(6, numberSeven7);
		       numberSeven_points.add(7, numberSeven8);

     ArrayList<CSMLPoint> numberSeven_color_points = new ArrayList();
               numberSeven_color_points.add(0, numberSeven_color1);
               numberSeven_color_points.add(1, numberSeven_color2);
               numberSeven_color_points.add(2, numberSeven_color3);
               numberSeven_color_points.add(3, numberSeven_color4);
                
     ArrayList<CSMLPoint> numberSeven_size_points1 = new ArrayList();
               numberSeven_size_points1.add(0, numberSeven_size1);
               numberSeven_size_points1.add(1, numberSeven_size2);
               numberSeven_size_points1.add(2, numberSeven_size3);
			   
	
		
		
		
	 CSMLPoint numberSix1 = m.createCSMLPoint("numberSix1", d1);
		           numberSix1.setq(d1.getQualityDimensionIds());
		           double[] pvnumberSix1 = {0.191689,0.0028817,0.00013554,0.000110116,1.33633e-08,4.74956e-06,-1.54765e-09 };
		           numberSix1.setv(pvnumberSix1); 
					  
		   CSMLPoint numberSix2 = m.createCSMLPoint("numberSix2", d1);
		           numberSix2.setq(d1.getQualityDimensionIds());
		           double[] pvnumberSix2 = {0.192858,0.00276827,0.000140775,0.000121371,1.56902e-08,4.72564e-06,-2.34692e-09};
		           numberSix2.setv(pvnumberSix2);
					  
		   CSMLPoint numberSix3 = m.createCSMLPoint("numberSix3", d1);
		           numberSix3.setq(d1.getQualityDimensionIds());
		           double[] pvnumberSix3 = {0.194998,0.00326786,0.00015924,0.000140961,2.10064e-08,6.22507e-06,-2.17903e-09 };
		           numberSix3.setv(pvnumberSix3);
					  
		  CSMLPoint numberSix4 = m.createCSMLPoint("numberSix4", d1);
		           numberSix4.setq(d1.getQualityDimensionIds());
		           double[] pvnumberSix4 = {0.194669,0.00339804,0.000153314,0.000134242,1.90871e-08,6.0601e-06,-2.56444e-09};
		           numberSix4.setv(pvnumberSix4);
					  
		  CSMLPoint numberSix5 = m.createCSMLPoint("numberSix5", d1);
		           numberSix5.setq(d1.getQualityDimensionIds());
		           double[] pvnumberSix5 = {0.195323,0.00330112,0.00016619,0.00014595,2.25563e-08,6.46456e-06,-2.80842e-09 };
		           numberSix5.setv(pvnumberSix5);
					  
		  CSMLPoint numberSix6 = m.createCSMLPoint("numberSix6", d1);
		           numberSix6.setq(d1.getQualityDimensionIds());
		           double[] pvnumberSix6 = {0.195232,0.00328172,0.000152169,0.000143094,2.08618e-08,6.22812e-06,-3.26102e-09};
		           numberSix6.setv(pvnumberSix6);
					  
		  CSMLPoint numberSix7 = m.createCSMLPoint("numberSix7", d1);
		          numberSix7.setq(d1.getQualityDimensionIds());
		          double[] pvnumberSix7 = {0.192955,0.00330762,0.00014331,0.000115015,1.4626e-08,4.9248e-06,-2.03049e-09};
		          numberSix7.setv(pvnumberSix7);
					  
		  CSMLPoint numberSix8 = m.createCSMLPoint("numberSix8", d1);
		         numberSix8.setq(d1.getQualityDimensionIds());
		         double[] pvnumberSix8 = {0.1953,0.00321808,0.000159072,0.000145509,2.19562e-08,6.40056e-06,-2.82951e-09};
		         numberSix8.setv(pvnumberSix8);
					  
		  //Fim numberSix 
			  
		  //numberSix Color
                
          CSMLPoint numberSix_color1 = m.createCSMLPoint("numberSix_color1", color_domain);
	            numberSix_color1.setq(color_domain.getQualityDimensionIds());
	            //double[] pvnumberSix_color1 = {237.0, 238.0, 227.0};
				double[] pvnumberSix_color1 = {0.9294117647,	0.9333333333,	0.8901960784};
	            numberSix_color1.setv(pvnumberSix_color1);
                
          CSMLPoint numberSix_color2 = m.createCSMLPoint("numberSix8_color2", color_domain);
	           numberSix_color2.setq(color_domain.getQualityDimensionIds());
	           //double[] pvnumberSix_color2 = {159.0, 160.0, 150.0};
			   double[] pvnumberSix_color2 = {0.6235294118,	0.6274509804,	0.5882352941};
	           numberSix_color2.setv(pvnumberSix_color2);
                
         CSMLPoint numberSix_color3 = m.createCSMLPoint("numberSix_color3", color_domain);
	           numberSix_color3.setq(color_domain.getQualityDimensionIds());
	           //double[] pvnumberSix_color3 = { 180.0, 179.0, 169.0};
			   double[] pvnumberSix_color3 = { 0.7058823529,	0.7019607843,	0.662745098};
	           numberSix_color3.setv(pvnumberSix_color3);
                
         CSMLPoint numberSix_color4 = m.createCSMLPoint("numberSix_color4", color_domain);
	           numberSix_color4.setq(color_domain.getQualityDimensionIds());
	           //double[] pvnumberSix_color4 = { 200.0, 198.0, 191.0 };
			   double[] pvnumberSix_color4 = { 0.7843137255,	0.7764705882,	0.7490196078 };
	           numberSix_color4.setv(pvnumberSix_color4);
                
         //Fim numberSix - (Color)
			 
		 //Quadro Pri Color - (Size)
                
         CSMLPoint numberSix_size1 = m.createCSMLPoint("numberSix_size1", size_domain);
	           numberSix_size1.setq(size_domain.getQualityDimensionIds());
	           double[] pvnumberSix_size1 = { 80.0, 104.0 };
	           numberSix_size1.setv(pvnumberSix_size1);
                
         CSMLPoint numberSix_size2 = m.createCSMLPoint("numberSix_size2", size_domain);
	          numberSix_size2.setq(size_domain.getQualityDimensionIds());
	          double[] pvnumberSix_size2 = { 85.0,  113.0 };
	          numberSix_size2.setv(pvnumberSix_size2);
                
        CSMLPoint numberSix_size3 = m.createCSMLPoint("numberSix_size3", size_domain);
	          numberSix_size3.setq(size_domain.getQualityDimensionIds());
	          double[] pvnumberSix_size3 = {84.0, 110.0};
	          numberSix_size3.setv(pvnumberSix_size3);
                
        // Fim Quadro Pri (Size)
		
		
		ArrayList<CSMLPoint> numberSix_points = new ArrayList();
		       numberSix_points.add(0, numberSix1);
		       numberSix_points.add(1, numberSix2);
		       numberSix_points.add(2, numberSix3);
		       numberSix_points.add(3, numberSix4);
		       numberSix_points.add(4, numberSix5);
		       numberSix_points.add(5, numberSix6);
		       numberSix_points.add(6, numberSix7);
		       numberSix_points.add(7, numberSix8);

     ArrayList<CSMLPoint> numberSix_color_points = new ArrayList();
               numberSix_color_points.add(0, numberSix_color1);
               numberSix_color_points.add(1, numberSix_color2);
               numberSix_color_points.add(2, numberSix_color3);
               numberSix_color_points.add(3, numberSix_color4);
                
     ArrayList<CSMLPoint> numberSix_size_points1 = new ArrayList();
               numberSix_size_points1.add(0, numberSix_size1);
               numberSix_size_points1.add(1, numberSix_size2);
               numberSix_size_points1.add(2, numberSix_size3);
			   

		
		
		
	 CSMLPoint powerOutlet1 = m.createCSMLPoint("powerOutlet1", d1);
		           powerOutlet1.setq(d1.getQualityDimensionIds());
		           double[] pvpowerOutlet1 = {0.174393,0.000877765,7.1314e-05,1.24176e-06,2.58045e-12,-3.18155e-08,-1.13969e-11 };
		           powerOutlet1.setv(pvpowerOutlet1); 
					  
		   CSMLPoint powerOutlet2 = m.createCSMLPoint("powerOutlet2", d1);
		           powerOutlet2.setq(d1.getQualityDimensionIds());
		           double[] pvpowerOutlet2 = {0.166167,0.000121895,5.25681e-05,1.37954e-06,-8.96873e-12,-1.52111e-08,-7.58796e-12};
		           powerOutlet2.setv(pvpowerOutlet2);
					  
		   CSMLPoint powerOutlet3 = m.createCSMLPoint("powerOutlet3", d1);
		           powerOutlet3.setq(d1.getQualityDimensionIds());
		           double[] pvpowerOutlet3 = {0.167726,0.000449351,4.729e-05,1.14063e-06,-8.12242e-12,6.45064e-10,2.05067e-12};
		           powerOutlet3.setv(pvpowerOutlet3);
					  
		  CSMLPoint powerOutlet4 = m.createCSMLPoint("powerOutlet4", d1);
		           powerOutlet4.setq(d1.getQualityDimensionIds());
		           double[] pvpowerOutlet4 = {0.168155,0.000665959,5.06485e-05,1.25749e-06,-2.16668e-13,-1.57633e-08,-1.00332e-11};
		           powerOutlet4.setv(pvpowerOutlet4);
					  
		  CSMLPoint powerOutlet5 = m.createCSMLPoint("powerOutlet5", d1);
		           powerOutlet5.setq(d1.getQualityDimensionIds());
		           double[] pvpowerOutlet5 = {0.166772,0.000206747,4.89037e-05,1.5952e-06,-5.73321e-12,-2.10302e-08,-1.28702e-11 };
		           powerOutlet5.setv(pvpowerOutlet5);
					  
		  CSMLPoint powerOutlet6 = m.createCSMLPoint("powerOutlet6", d1);
		           powerOutlet6.setq(d1.getQualityDimensionIds());
		           double[] pvpowerOutlet6 = {0.166766,0.000374017,3.67383e-05,8.84373e-07,-4.98584e-12,-1.08637e-09,7.43374e-13};
		           powerOutlet6.setv(pvpowerOutlet6);
					  
		  CSMLPoint powerOutlet7 = m.createCSMLPoint("powerOutlet7", d1);
		          powerOutlet7.setq(d1.getQualityDimensionIds());
		          double[] pvpowerOutlet7 = {0.167401,0.000530914,7.45869e-05,2.12753e-06,1.6093e-11,2.68662e-09,-2.1431e-11};
		          powerOutlet7.setv(pvpowerOutlet7);
					  
		  CSMLPoint powerOutlet8 = m.createCSMLPoint("powerOutlet8", d1);
		         powerOutlet8.setq(d1.getQualityDimensionIds());
		         double[] pvpowerOutlet8 = {0.16709,0.00019122,5.47226e-05,2.14569e-06,-1.59017e-11,-2.96318e-08,-1.69625e-11};
		         powerOutlet8.setv(pvpowerOutlet8);
					  
		  //Fim powerOutlet 
			  
		  //powerOutlet Color
                
          CSMLPoint powerOutlet_color1 = m.createCSMLPoint("powerOutlet_color1", color_domain);
	            powerOutlet_color1.setq(color_domain.getQualityDimensionIds());
	            //double[] pvpowerOutlet_color1 = {150.0, 143.0, 145.0};
				double[] pvpowerOutlet_color1 = {0.5882352941,	0.5607843137,	0.568627451};
	            powerOutlet_color1.setv(pvpowerOutlet_color1);
                
          CSMLPoint powerOutlet_color2 = m.createCSMLPoint("powerOutlet8_color2", color_domain);
	           powerOutlet_color2.setq(color_domain.getQualityDimensionIds());
	           //double[] pvpowerOutlet_color2 = {90.0, 85.0, 82.0};
			   double[] pvpowerOutlet_color2 = {0.3529411765,	0.3333333333,	0.3215686275};
	           powerOutlet_color2.setv(pvpowerOutlet_color2);
                
         CSMLPoint powerOutlet_color3 = m.createCSMLPoint("powerOutlet_color3", color_domain);
	           powerOutlet_color3.setq(color_domain.getQualityDimensionIds());
	           //double[] pvpowerOutlet_color3 = { 104.0, 100.0, 97.0};
			   double[] pvpowerOutlet_color3 = { 0.4078431373,	0.3921568627,	0.3803921569};
	           powerOutlet_color3.setv(pvpowerOutlet_color3);
                
         CSMLPoint powerOutlet_color4 = m.createCSMLPoint("powerOutlet_color4", color_domain);
	           powerOutlet_color4.setq(color_domain.getQualityDimensionIds());
	           //double[] pvpowerOutlet_color4 = { 119.0, 114.0, 112.0 };
			   double[] pvpowerOutlet_color4 = { 0.4666666667,	0.4470588235,	0.4392156863 };
	           powerOutlet_color4.setv(pvpowerOutlet_color4);
                
         //Fim powerOutlet - (Color)
			 
		 //Quadro Pri Color - (Size)
                
         CSMLPoint powerOutlet_size1 = m.createCSMLPoint("powerOutlet_size1", size_domain);
	           powerOutlet_size1.setq(size_domain.getQualityDimensionIds());
	           double[] pvpowerOutlet_size1 = { 169.0, 142.0 };
	           powerOutlet_size1.setv(pvpowerOutlet_size1);
                
         CSMLPoint powerOutlet_size2 = m.createCSMLPoint("powerOutlet_size2", size_domain);
	          powerOutlet_size2.setq(size_domain.getQualityDimensionIds());
	          double[] pvpowerOutlet_size2 = { 150.0,  139.0 };
	          powerOutlet_size2.setv(pvpowerOutlet_size2);
                
        CSMLPoint powerOutlet_size3 = m.createCSMLPoint("powerOutlet_size3", size_domain);
	          powerOutlet_size3.setq(size_domain.getQualityDimensionIds());
	          double[] pvpowerOutlet_size3 = {170.0, 149.0};
	          powerOutlet_size3.setv(pvpowerOutlet_size3);
                
        // Fim Quadro Pri (Size)
		
		
		ArrayList<CSMLPoint> powerOutlet_points = new ArrayList();
		       powerOutlet_points.add(0, powerOutlet1);
		       powerOutlet_points.add(1, powerOutlet2);
		       powerOutlet_points.add(2, powerOutlet3);
		       powerOutlet_points.add(3, powerOutlet4);
		       powerOutlet_points.add(4, powerOutlet5);
		       powerOutlet_points.add(5, powerOutlet6);
		       powerOutlet_points.add(6, powerOutlet7);
		       powerOutlet_points.add(7, powerOutlet8);

     ArrayList<CSMLPoint> powerOutlet_color_points = new ArrayList();
               powerOutlet_color_points.add(0, powerOutlet_color1);
               powerOutlet_color_points.add(1, powerOutlet_color2);
               powerOutlet_color_points.add(2, powerOutlet_color3);
               powerOutlet_color_points.add(3, powerOutlet_color4);
                
     ArrayList<CSMLPoint> powerOutlet_size_points1 = new ArrayList();
               powerOutlet_size_points1.add(0, powerOutlet_size1);
               powerOutlet_size_points1.add(1, powerOutlet_size2);
               powerOutlet_size_points1.add(2, powerOutlet_size3);
			   
			   
	CSMLPoint schoolBag1 = m.createCSMLPoint("schoolBag1", d1);
		           schoolBag1.setq(d1.getQualityDimensionIds());
		           double[] pvschoolBag1 = {0.17559,0.00167761,0.00044364,2.31837e-06,-6.10363e-11,9.08259e-08,4.24584e-11 };
		           schoolBag1.setv(pvschoolBag1); 
					  
		   CSMLPoint schoolBag2 = m.createCSMLPoint("schoolBag2", d1);
		           schoolBag2.setq(d1.getQualityDimensionIds());
		           double[] pvschoolBag2 = {0.175144,0.00151525,0.000445658,3.06387e-06,-1.03511e-10,1.17865e-07,4.58612e-11};
		           schoolBag2.setv(pvschoolBag2);
					  
		   CSMLPoint schoolBag3 = m.createCSMLPoint("schoolBag3", d1);
		           schoolBag3.setq(d1.getQualityDimensionIds());
		           double[] pvschoolBag3 = {0.171037,0.175144,0.00151525,0.000445658,3.06387e-06,-1.03511e-10,1.17865e-07,4.58612e-11 };
		           schoolBag3.setv(pvschoolBag3);
					  
		  CSMLPoint schoolBag4 = m.createCSMLPoint("schoolBag4", d1);
		           schoolBag4.setq(d1.getQualityDimensionIds());
		           double[] pvschoolBag4 = {0.175058,0.00135128,0.000467749,4.27833e-06,-1.83332e-10,1.57252e-07,5.49506e-11};
		           schoolBag4.setv(pvschoolBag4);
					  
		  CSMLPoint schoolBag5 = m.createCSMLPoint("schoolBag5", d1);
		           schoolBag5.setq(d1.getQualityDimensionIds());
		           double[] pvschoolBag5 = {0.171145,0.171145,0.175319,0.00150132,0.000449154,2.96505e-06,-1.06892e-10,1.14856e-07,1.68011e-11 };
		           schoolBag5.setv(pvschoolBag5);
					  
		  CSMLPoint schoolBag6 = m.createCSMLPoint("schoolBag6", d1);
		           schoolBag6.setq(d1.getQualityDimensionIds());
		           double[] pvschoolBag6 = {0.174989,0.00138956,0.000460093,3.9431e-06,-1.58574e-10,1.46837e-07,5.53287e-11};
		           schoolBag6.setv(pvschoolBag6);
					  
		  CSMLPoint schoolBag7 = m.createCSMLPoint("schoolBag7", d1);
		          schoolBag7.setq(d1.getQualityDimensionIds());
		          double[] pvschoolBag7 = {0.174732,0.00128536,0.000459869,3.26987e-06,-1.26068e-10,1.16378e-07,-1.35854e-11};
		          schoolBag7.setv(pvschoolBag7);
					  
		  CSMLPoint schoolBag8 = m.createCSMLPoint("schoolBag8", d1);
		         schoolBag8.setq(d1.getQualityDimensionIds());
		         double[] pvschoolBag8 = {0.174491,0.00122988,0.000463311,3.53103e-06,-1.42136e-10,1.22671e-07,-1.39632e-11};
		         schoolBag8.setv(pvschoolBag8);
					  
		  //Fim schoolBag 
		  
		  //schoolBag 2
		  
		  
		  CSMLPoint schoolBag_2_1 = m.createCSMLPoint("schoolBag_2_1", d1);
		           schoolBag_2_1.setq(d1.getQualityDimensionIds());
		           double[] pvschoolBag_2_1 = {0.184489,0.00113748,0.00177867,4.66778e-05,-1.34148e-08,-1.55465e-06,-9.68336e-10 };
		           schoolBag_2_1.setv(pvschoolBag_2_1); 
					  
		   CSMLPoint schoolBag_2_2 = m.createCSMLPoint("schoolBag_2_2", d1);
		           schoolBag_2_2.setq(d1.getQualityDimensionIds());
		           double[] pvschoolBag_2_2 = {0.176165,0.000599051,0.000926684,1.4346e-05,-1.35587e-09,-3.51081e-07,9.47454e-10};
		           schoolBag_2_2.setv(pvschoolBag_2_2);
					  
		   CSMLPoint schoolBag_2_3 = m.createCSMLPoint("schoolBag_2_3", d1);
		           schoolBag_2_3.setq(d1.getQualityDimensionIds());
		           double[] pvschoolBag_2_3 = {0.181978,0.000453787,0.00128283,2.29926e-05,-3.94799e-09,-4.71483e-07,8.11831e-11 };
		           schoolBag_2_3.setv(pvschoolBag_2_3);
					  
		  CSMLPoint schoolBag_2_4 = m.createCSMLPoint("schoolBag_2_4", d1);
		           schoolBag_2_4.setq(d1.getQualityDimensionIds());
		           double[] pvschoolBag_2_4 = {0.175096,0.000208059,0.00105331,4.71839e-06,-2.4037e-10,-6.7404e-08,2.29932e-10};
		           schoolBag_2_4.setv(pvschoolBag_2_4);
					  
		  CSMLPoint schoolBag_2_5 = m.createCSMLPoint("schoolBag_2_5", d1);
		           schoolBag_2_5.setq(d1.getQualityDimensionIds());
		           double[] pvschoolBag_2_5 = {0.176271,0.000286635,0.000945222,7.14323e-06,-4.64189e-10,-1.20857e-07,3.59237e-10 };
		           schoolBag_2_5.setv(pvschoolBag_2_5);
					  
		  CSMLPoint schoolBag_2_6 = m.createCSMLPoint("schoolBag_2_6", d1);
		           schoolBag_2_6.setq(d1.getQualityDimensionIds());
		           double[] pvschoolBag_2_6 = {0.182475,0.000666288,0.00116936,3.56476e-05,-7.02218e-09,-6.70532e-07,-1.91311e-09};
		           schoolBag_2_6.setv(pvschoolBag_2_6);
					  
		  CSMLPoint schoolBag_2_7 = m.createCSMLPoint("schoolBag_2_7", d1);
		          schoolBag_2_7.setq(d1.getQualityDimensionIds());
		          double[] pvschoolBag_2_7 = {0.178625,0.000449024,0.00116948,1.24906e-05,-1.41929e-09,-2.63379e-07,5.14385e-10};
		          schoolBag_2_7.setv(pvschoolBag_2_7);
					  
		  CSMLPoint schoolBag_2_8 = m.createCSMLPoint("schoolBag_2_8", d1);
		         schoolBag_2_8.setq(d1.getQualityDimensionIds());
		         double[] pvschoolBag_2_8 = {0.174975,0.000218858,0.00102991,5.11289e-06,-2.91252e-10,-7.52144e-08,2.29847e-10};
		         schoolBag_2_8.setv(pvschoolBag_2_8);
		  
		  //Fim schoolBag2
		  
		  //schoolBag3
		  
		  CSMLPoint schoolBag_3_1 = m.createCSMLPoint("schoolBag_3_1", d1);
		           schoolBag_3_1.setq(d1.getQualityDimensionIds());
		           double[] pvschoolBag_3_1 = {0.247692,0.00656697,0.0108178,0.000865483,2.64695e-06,6.46775e-05,-8.23422e-08 };
		           schoolBag_3_1.setv(pvschoolBag_3_1); 
					  
		   CSMLPoint schoolBag_3_2 = m.createCSMLPoint("schoolBag_3_2", d1);
		           schoolBag_3_2.setq(d1.getQualityDimensionIds());
		           double[] pvschoolBag_3_2 = {0.209052,0.000669166,0.00496212,0.000530011,8.59525e-07,6.78855e-06,-2.69456e-09};
		           schoolBag_3_2.setv(pvschoolBag_3_2);
					  
		   CSMLPoint schoolBag_3_3 = m.createCSMLPoint("schoolBag_3_3", d1);
		           schoolBag_3_3.setq(d1.getQualityDimensionIds());
		           double[] pvschoolBag_3_3 = {0.207776,0.000454977,0.00474618,0.000338234,4.12335e-07,3.7809e-06,-1.1676e-07 };
		           schoolBag_3_3.setv(pvschoolBag_3_3);
					  
		  CSMLPoint schoolBag_3_4 = m.createCSMLPoint("schoolBag_3_4", d1);
		           schoolBag_3_4.setq(d1.getQualityDimensionIds());
		           double[] pvschoolBag_3_4 = {0.208195,0.000537534,0.00479886,0.00040931,5.68847e-07,4.72657e-06,-7.40821e-08};
		           schoolBag_3_4.setv(pvschoolBag_3_4);
					  
		  CSMLPoint schoolBag_3_5 = m.createCSMLPoint("schoolBag_3_5", d1);
		           schoolBag_3_5.setq(d1.getQualityDimensionIds());
		           double[] pvschoolBag_3_5 = {0.208278,0.00042469,0.00488252,0.000370875,4.84886e-07,3.76504e-06,-1.18148e-070 };
		           schoolBag_3_5.setv(pvschoolBag_3_5);
					  
		  CSMLPoint schoolBag_3_6 = m.createCSMLPoint("schoolBag_3_6", d1);
		           schoolBag_3_6.setq(d1.getQualityDimensionIds());
		           double[] pvschoolBag_3_6 = {0.194179,0.000744644,0.00264582,3.32915e-05,-6.07172e-09,7.44497e-07,-7.79478e-09};
		           schoolBag_3_6.setv(pvschoolBag_3_6);
					  
		  CSMLPoint schoolBag_3_7 = m.createCSMLPoint("schoolBag_3_7", d1);
		          schoolBag_3_7.setq(d1.getQualityDimensionIds());
		          double[] pvschoolBag_3_7 = {0.208188,0.000369397,0.0048787,0.000324687,3.77075e-07,3.31118e-06,-1.57503e-07};
		          schoolBag_3_7.setv(pvschoolBag_3_7);
					  
		  CSMLPoint schoolBag_3_8 = m.createCSMLPoint("schoolBag_3_8", d1);
		         schoolBag_3_8.setq(d1.getQualityDimensionIds());
		         double[] pvschoolBag_3_8 = {0.215707,0.000136956,0.00638548,0.000254878,-4.50991e-08,2.84985e-06,-3.22017e-07};
		         schoolBag_3_8.setv(pvschoolBag_3_8);
					  
		  
		  //Fim schoolBag3
			  
		  //schoolBag Color
                
          CSMLPoint schoolBag_color1 = m.createCSMLPoint("schoolBag_color1", color_domain);
	            schoolBag_color1.setq(color_domain.getQualityDimensionIds());
				//double[] pvschoolBag_color1 = { 0.2666666667,	0.2588235294, 0.2862745098};
				double[] pvschoolBag_color1 = {97.0, 93.0, 92.0};
	            schoolBag_color1.setv(pvschoolBag_color1);
                
          CSMLPoint schoolBag_color2 = m.createCSMLPoint("schoolBag8_color2", color_domain);
	           schoolBag_color2.setq(color_domain.getQualityDimensionIds());
	           //double[] pvschoolBag_color2 ={ 0.1176470588,	0.1058823529,	0.1215686275};
			   double[] pvschoolBag_color2 ={ 30.0, 27.0, 31.0};
	           schoolBag_color2.setv(pvschoolBag_color2);
                
         CSMLPoint schoolBag_color3 = m.createCSMLPoint("schoolBag_color3", color_domain);
	           schoolBag_color3.setq(color_domain.getQualityDimensionIds());
	           //double[] pvschoolBag_color3 = {0.1607843137,	0.1450980392,	0.168627451};
			   double[] pvschoolBag_color3 = { 41.0, 37.0, 43.0};
	           schoolBag_color3.setv(pvschoolBag_color3);
                
         CSMLPoint schoolBag_color4 = m.createCSMLPoint("schoolBag_color4", color_domain);
	           schoolBag_color4.setq(color_domain.getQualityDimensionIds());
	           //double[] pvschoolBag_color4 = { 0.1803921569,	0.1921568627,	0.2 };
			   double[] pvschoolBag_color4 = { 46.0, 49.0, 51.0 };
	           schoolBag_color4.setv(pvschoolBag_color4);
                
         //Fim schoolBag - (Color)
			 
		 //Quadro Pri Color - (Size)
                
         CSMLPoint schoolBag_size1 = m.createCSMLPoint("schoolBag_size1", size_domain);
	           schoolBag_size1.setq(size_domain.getQualityDimensionIds());
	           double[] pvschoolBag_size1 = { 135.0, 151.0 };
	           schoolBag_size1.setv(pvschoolBag_size1);
                
         CSMLPoint schoolBag_size2 = m.createCSMLPoint("schoolBag_size2", size_domain);
	          schoolBag_size2.setq(size_domain.getQualityDimensionIds());
	          double[] pvschoolBag_size2 = { 139.0,  156.0 };
	          schoolBag_size2.setv(pvschoolBag_size2);
                
        CSMLPoint schoolBag_size3 = m.createCSMLPoint("schoolBag_size3", size_domain);
	          schoolBag_size3.setq(size_domain.getQualityDimensionIds());
	          double[] pvschoolBag_size3 = {116.0, 127.0};
	          schoolBag_size3.setv(pvschoolBag_size3);
                
        // Fim Quadro Pri (Size)
		
		
		ArrayList<CSMLPoint> schoolBag_points = new ArrayList();
		       schoolBag_points.add(0, schoolBag1);
		       schoolBag_points.add(1, schoolBag2);
		       schoolBag_points.add(2, schoolBag3);
		       schoolBag_points.add(3, schoolBag4);
		       schoolBag_points.add(4, schoolBag5);
		       schoolBag_points.add(5, schoolBag6);
		       schoolBag_points.add(6, schoolBag7);
		       schoolBag_points.add(7, schoolBag8);
			   
		ArrayList<CSMLPoint> schoolBag_2_points = new ArrayList();
		       schoolBag_2_points.add(0, schoolBag_2_1);
		       schoolBag_2_points.add(1, schoolBag_2_2);
		       schoolBag_2_points.add(2, schoolBag_2_3);
		       schoolBag_2_points.add(3, schoolBag_2_4);
		       schoolBag_2_points.add(4, schoolBag_2_5);
		       schoolBag_2_points.add(5, schoolBag_2_6);
		       schoolBag_2_points.add(6, schoolBag_2_7);
		       schoolBag_2_points.add(7, schoolBag_2_8);	
			   
			   
			   
		ArrayList<CSMLPoint> schoolBag_3_points = new ArrayList();
		       schoolBag_3_points.add(0, schoolBag_3_1);
		       schoolBag_3_points.add(1, schoolBag_3_2);
		       schoolBag_3_points.add(2, schoolBag_3_3);
		       schoolBag_3_points.add(3, schoolBag_3_4);
		       schoolBag_3_points.add(4, schoolBag_3_5);
		       schoolBag_3_points.add(5, schoolBag_3_6);
		       schoolBag_3_points.add(6, schoolBag_3_7);
		       schoolBag_3_points.add(7, schoolBag_3_8);	   
			   

     ArrayList<CSMLPoint> schoolBag_color_points = new ArrayList();
               schoolBag_color_points.add(0, schoolBag_color1);
               schoolBag_color_points.add(1, schoolBag_color2);
               schoolBag_color_points.add(2, schoolBag_color3);
               schoolBag_color_points.add(3, schoolBag_color4);
                
     ArrayList<CSMLPoint> schoolBag_size_points1 = new ArrayList();
               schoolBag_size_points1.add(0, schoolBag_size1);
               schoolBag_size_points1.add(1, schoolBag_size2);
               schoolBag_size_points1.add(2, schoolBag_size3);
			   
	
                  
                  
                   CSMLPoint noteBook1 = m.createCSMLPoint("noteBook1", d1);
		           noteBook1.setq(d1.getQualityDimensionIds());
		           double[] pvnoteBook1 = {0.194009,0.00820375,4.37079e-05,2.43914e-05,7.85062e-10,2.1241e-06,-1.33948e-10 };
		           noteBook1.setv(pvnoteBook1); 
					  
		   CSMLPoint noteBook2 = m.createCSMLPoint("noteBook2", d1);
		           noteBook2.setq(d1.getQualityDimensionIds());
		           double[] pvnoteBook2 = {0.189194,0.00713152,3.13294e-05,1.20185e-05,2.2945e-10,9.86051e-07,-4.17117e-11};
		           noteBook2.setv(pvnoteBook2);
					  
		   CSMLPoint noteBook3 = m.createCSMLPoint("noteBook3", d1);
		           noteBook3.setq(d1.getQualityDimensionIds());
		           double[] pvnoteBook3 = {0.189858,0.00766437,2.16092e-05,7.48473e-06,9.37898e-11,6.42003e-07,1.62564e-11 };
		           noteBook3.setv(pvnoteBook3);
					  
		  CSMLPoint noteBook4 = m.createCSMLPoint("noteBook4", d1);
		           noteBook4.setq(d1.getQualityDimensionIds());
		           double[] pvnoteBook4 = {0.192715,0.00917878,4.65182e-05,5.28659e-06,6.39638e-11,3.77323e-07,-5.27418e-11};
		           noteBook4.setv(pvnoteBook4);
					  
		  CSMLPoint noteBook5 = m.createCSMLPoint("noteBook5", d1);
		           noteBook5.setq(d1.getQualityDimensionIds());
		           double[] pvnoteBook5 = {0.19084,0.00893393,4.67477e-05,3.45243e-06,3.57709e-11,2.47186e-07,-2.53798e-11 };
		           noteBook5.setv(pvnoteBook5);
					  
		  CSMLPoint noteBook6 = m.createCSMLPoint("noteBook6", d1);
		           noteBook6.setq(d1.getQualityDimensionIds());
		           double[] pvnoteBook6 = {0.193319,0.00942727,0.000101617,7.43796e-06,2.02337e-10,7.16111e-07,-2.95744e-11};
		           noteBook6.setv(pvnoteBook6);
					  
		  CSMLPoint noteBook7 = m.createCSMLPoint("noteBook7", d1);
		          noteBook7.setq(d1.getQualityDimensionIds());
		          double[] pvnoteBook7 = {0.189773,0.00711398,1.70881e-05,1.15904e-05,1.60012e-10,9.612e-07,3.16697e-11};
		          noteBook7.setv(pvnoteBook7);
					  
		  CSMLPoint noteBook8 = m.createCSMLPoint("noteBook8", d1);
		         noteBook8.setq(d1.getQualityDimensionIds());
		         double[] pvnoteBook8 = {0.191236,0.00814943,0.000256859,1.97361e-05,1.30787e-09,1.57386e-06,5.13868e-10};
		         noteBook8.setv(pvnoteBook8);
					  
		  //Fim noteBook 
		  
		  //NoteBook 2
		  
		  CSMLPoint noteBook_2_1 = m.createCSMLPoint("noteBook_2_1", d1);
		           noteBook_2_1.setq(d1.getQualityDimensionIds());
		           double[] pvnoteBook_2_1 = {0.203462,0.0131173,7.02628e-05,6.07783e-06,-2.85373e-11,-4.33518e-07,1.22314e-10 };
		           noteBook_2_1.setv(pvnoteBook_2_1); 
					  
		   CSMLPoint noteBook_2_2 = m.createCSMLPoint("noteBook_2_2", d1);
		           noteBook_2_2.setq(d1.getQualityDimensionIds());
		           double[] pvnoteBook_2_2 = {0.191314,0.00931711,5.40869e-06,6.55901e-07,8.32569e-14,2.56051e-08,1.23258e-12};
		           noteBook_2_2.setv(pvnoteBook_2_2);
					  
		   CSMLPoint noteBook_2_3 = m.createCSMLPoint("noteBook_2_3", d1);
		           noteBook_2_3.setq(d1.getQualityDimensionIds());
		           double[] pvnoteBook_2_3 = {0.207264,0.0142439,5.50248e-05,1.12359e-05,1.95482e-10,3.38316e-07,1.99596e-10 };
		           noteBook_2_3.setv(pvnoteBook_2_3);
					  
		  CSMLPoint noteBook_2_4 = m.createCSMLPoint("noteBook_2_4", d1);
		           noteBook_2_4.setq(d1.getQualityDimensionIds());
		           double[] pvnoteBook_2_4 = {0.205572,0.0136425,8.26964e-05,1.31362e-05,2.33569e-10,5.03586e-08,3.64554e-10};
		           noteBook_2_4.setv(pvnoteBook_2_4);
					  
		  CSMLPoint noteBook_2_5 = m.createCSMLPoint("noteBook_2_5", d1);
		           noteBook_2_5.setq(d1.getQualityDimensionIds());
		           double[] pvnoteBook_2_5 = {0.207106,0.0141975,6.34217e-05,1.27072e-05,2.30556e-10,3.22516e-07,2.77445e-10 };
		           noteBook_2_5.setv(pvnoteBook_2_5);
					  
		  CSMLPoint noteBook_2_6 = m.createCSMLPoint("noteBook_2_6", d1);
		           noteBook_2_6.setq(d1.getQualityDimensionIds());
		           double[] pvnoteBook_2_6 = {0.206852,0.0140343,6.48557e-05,1.21559e-05,2.13866e-10,2.55923e-07,2.66003e-10};
		           noteBook_2_6.setv(pvnoteBook_2_6);
					  
		  CSMLPoint noteBook_2_7 = m.createCSMLPoint("noteBook_2_7", d1);
		          noteBook_2_7.setq(d1.getQualityDimensionIds());
		          double[] pvnoteBook_2_7 = {0.206132,0.0139154,6.43542e-05,9.05774e-06,1.04893e-10,-7.77435e-08,1.91887e-10};
		          noteBook_2_7.setv(pvnoteBook_2_7);
					  
		  CSMLPoint noteBook_2_8 = m.createCSMLPoint("noteBook_2_8", d1);
		         noteBook_2_8.setq(d1.getQualityDimensionIds());
		         double[] pvnoteBook_2_8 = {0.199739,0.012093,4.29917e-05,2.01613e-06,-1.34584e-11,-1.57791e-07,-1.30841e-11};
		         noteBook_2_8.setv(pvnoteBook_2_8);
		  
		  
		  // Fim Notebook2
		  
		  //NoteBook 3 
		  
		  CSMLPoint noteBook_3_1 = m.createCSMLPoint("noteBook_3_1", d1);
		           noteBook_3_1.setq(d1.getQualityDimensionIds());
		           double[] pvnoteBook_3_1 = {0.195328,0.00249409,0.00408277,6.33046e-05,8.85989e-09,4.29855e-07,3.09397e-08 };
		           noteBook_3_1.setv(pvnoteBook_3_1); 
					  
		   CSMLPoint noteBook_3_2 = m.createCSMLPoint("noteBook_3_2", d1);
		           noteBook_3_2.setq(d1.getQualityDimensionIds());
		           double[] pvnoteBook_3_2 = {0.194898,0.00280203,0.00389113,5.62048e-05,-2.8334e-09,-6.72927e-07,2.61312e-08};
		           noteBook_3_2.setv(pvnoteBook_3_2);
					  
		   CSMLPoint noteBook_3_3 = m.createCSMLPoint("noteBook_3_3", d1);
		           noteBook_3_3.setq(d1.getQualityDimensionIds());
		           double[] pvnoteBook_3_3 = {0.211895,0.0084224,0.00234653,0.000816274,1.01288e-06,3.61545e-05,5.0031e-07 };
		           noteBook_3_3.setv(pvnoteBook_3_3);
					  
		  CSMLPoint noteBook_3_4 = m.createCSMLPoint("noteBook_3_4", d1);
		           noteBook_3_4.setq(d1.getQualityDimensionIds());
		           double[] pvnoteBook_3_4 = {0.194725,0.00270388,0.00390744,5.90902e-05,4.4147e-09,3.85437e-08,2.80482e-08};
		           noteBook_3_4.setv(pvnoteBook_3_4);
					  
		  CSMLPoint noteBook_3_5 = m.createCSMLPoint("noteBook_3_5", d1);
		           noteBook_3_5.setq(d1.getQualityDimensionIds());
		           double[] pvnoteBook_3_5 = {0.195047,0.002461,0.0040793,6.28763e-05,9.55545e-09,4.36132e-07,3.03762e-08 };
		           noteBook_3_5.setv(pvnoteBook_3_5);
					  
		  CSMLPoint noteBook_3_6 = m.createCSMLPoint("noteBook_3_6", d1);
		           noteBook_3_6.setq(d1.getQualityDimensionIds());
		           double[] pvnoteBook_3_6 = {0.195325,0.0025149,0.00411469,6.62887e-05,1.02796e-08,4.22131e-07,3.30587e-08};
		           noteBook_3_6.setv(pvnoteBook_3_6);
					  
		  CSMLPoint noteBook_3_7 = m.createCSMLPoint("noteBook_3_7", d1);
		          noteBook_3_7.setq(d1.getQualityDimensionIds());
		          double[] pvnoteBook_3_7 = {0.195178,0.00240061,0.0041331,6.44998e-05,1.06121e-08,4.2853e-07,3.15663e-08};
		          noteBook_3_7.setv(pvnoteBook_3_7);
					  
		  CSMLPoint noteBook_3_8 = m.createCSMLPoint("noteBook_3_8", d1);
		         noteBook_3_8.setq(d1.getQualityDimensionIds());
		         double[] pvnoteBook_3_8 = {0.195834,0.00245785,0.00420563,6.60321e-05,9.83526e-09,4.35263e-07,3.33786e-08};
		         noteBook_3_8.setv(pvnoteBook_3_8);
		  
		  //Fim Notebook3
		  
		  CSMLPoint noteBook_4_1 = m.createCSMLPoint("noteBook_4_1", d1);
		           noteBook_4_1.setq(d1.getQualityDimensionIds());
		           double[] pvnoteBook_4_1 = {0.19186, 0.00283591, 0.000200075, 7.41225e-05, 8.84183e-09, 2.97392e-06, 1.81673e-09 };
		           noteBook_4_1.setv(pvnoteBook_4_1);
		  
		  
		   CSMLPoint noteBook_4_2 = m.createCSMLPoint("noteBook_4_2", d1);
		           noteBook_4_2.setq(d1.getQualityDimensionIds());
		           double[] pvnoteBook_4_2 = {0.17846, 0.00354918, 0.000117907, 5.56099e-07, -2.03903e-12, 1.88106e-08, -4.01484e-12};
		           noteBook_4_2.setv(pvnoteBook_4_2);
					  
		   CSMLPoint noteBook_4_3 = m.createCSMLPoint("noteBook_4_3", d1);
		           noteBook_4_3.setq(d1.getQualityDimensionIds());
		           double[] pvnoteBook_4_3 = {0.175388, 0.00279074, 0.000127381, 1.22775e-06, -1.02355e-11, 3.56436e-08, -1.14444e-11 };
		           noteBook_4_3.setv(pvnoteBook_4_3);
					  
		  CSMLPoint noteBook_4_4 = m.createCSMLPoint("noteBook_4_4", d1);
		           noteBook_4_4.setq(d1.getQualityDimensionIds());
		           double[] pvnoteBook_4_4 = {0.199939, 0.00556244, 0.000295975, 8.63396e-05, 1.31357e-08, 5.04766e-06, 4.23648e-09};
		           noteBook_4_4.setv(pvnoteBook_4_4);
					  
		  CSMLPoint noteBook_4_5 = m.createCSMLPoint("noteBook_4_5", d1);
		           noteBook_4_5.setq(d1.getQualityDimensionIds());
		           double[] pvnoteBook_4_5 = {0.216055, 0.00754218, 0.00062261, 9.1647e-05, 1.28598e-08, 1.94804e-06, 1.77168e-08 };
		           noteBook_4_5.setv(pvnoteBook_4_5);
					  
		  CSMLPoint noteBook_4_6 = m.createCSMLPoint("noteBook_4_6", d1);
		           noteBook_4_6.setq(d1.getQualityDimensionIds());
		           double[] pvnoteBook_4_6 = {0.218767, 0.00800991, 0.000813774, 0.000314994, 1.4856e-07, 1.99703e-05, 5.79978e-08};
		           noteBook_4_6.setv(pvnoteBook_4_6);
					  
		  CSMLPoint noteBook_4_7 = m.createCSMLPoint("noteBook_4_7", d1);
		          noteBook_4_7.setq(d1.getQualityDimensionIds());
		          double[] pvnoteBook_4_7 = {0.195104, 0.00474195, 0.000271155, 4.93209e-05, 5.41846e-09, 2.00979e-06, 1.7811e-09};
		          noteBook_4_7.setv(pvnoteBook_4_7);
					  
		  CSMLPoint noteBook_4_8 = m.createCSMLPoint("noteBook_4_8", d1);
		         noteBook_4_8.setq(d1.getQualityDimensionIds());
		         double[] pvnoteBook_4_8 = {0.179209, 0.00367387, 0.000120099, 5.32968e-06, 1.11055e-10, 3.14254e-07, -7.64787e-11};
		         noteBook_4_8.setv(pvnoteBook_4_8);
			  
		  //noteBook Color
                
          CSMLPoint noteBook_color1 = m.createCSMLPoint("noteBook_color1", color_domain);
	            noteBook_color1.setq(color_domain.getQualityDimensionIds());
	            double[] pvnoteBook_color1 = {104.0, 100.0, 98.0};
				//double[] pvnoteBook_color1 = {0.4901960784,	0.4941176471,	0.5254901961};
	            noteBook_color1.setv(pvnoteBook_color1);
                
          CSMLPoint noteBook_color2 = m.createCSMLPoint("noteBook8_color2", color_domain);
	           noteBook_color2.setq(color_domain.getQualityDimensionIds());
	           double[] pvnoteBook_color2 = {69.0, 70.0, 74.0};
			   //double[] pvnoteBook_color2 = {0.2705882353,	0.2745098039,	0.2901960784};
	           noteBook_color2.setv(pvnoteBook_color2);
                
         CSMLPoint noteBook_color3 = m.createCSMLPoint("noteBook_color3", color_domain);
	           noteBook_color3.setq(color_domain.getQualityDimensionIds());
	           double[] pvnoteBook_color3 = { 82.0, 79.0, 77.0};
			   //double[] pvnoteBook_color3 = { 0.3333333333,	0.3254901961,	0.3490196078};
	           noteBook_color3.setv(pvnoteBook_color3);
                
         CSMLPoint noteBook_color4 = m.createCSMLPoint("noteBook_color4", color_domain);
	           noteBook_color4.setq(color_domain.getQualityDimensionIds());
	           double[] pvnoteBook_color4 = { 97.0, 101.0, 105.0 };
			  // double[] pvnoteBook_color4 = { 0.3803921569,	0.3960784314,	0.4117647059 };
	           noteBook_color4.setv(pvnoteBook_color4);
                
         //Fim noteBook - (Color)
		 
		 //noteBook Color
                
          CSMLPoint noteBook2_color1 = m.createCSMLPoint("noteBook2_color1", color_domain);
	            noteBook2_color1.setq(color_domain.getQualityDimensionIds());
	            double[] pvnoteBook2_color1 = {109.0, 111.0, 117.0};
				//double[] pvnoteBook2_color1 = {0.4901960784,	0.4941176471,	0.5254901961};
	            noteBook2_color1.setv(pvnoteBook2_color1);
                
          CSMLPoint noteBook2_color2 = m.createCSMLPoint("noteBook8_color2", color_domain);
	           noteBook2_color2.setq(color_domain.getQualityDimensionIds());
	           double[] pvnoteBook2_color2 = {52.0, 50.0, 58.0};
			   //double[] pvnoteBook2_color2 = {0.2705882353,	0.2745098039,	0.2901960784};
	           noteBook2_color2.setv(pvnoteBook2_color2);
                
         CSMLPoint noteBook2_color3 = m.createCSMLPoint("noteBook2_color3", color_domain);
	           noteBook2_color3.setq(color_domain.getQualityDimensionIds());
	           double[] pvnoteBook2_color3 = { 62.0, 65.0, 61.0};
			   //double[] pvnoteBook2_color3 = { 0.3333333333,	0.3254901961,	0.3490196078};
	           noteBook2_color3.setv(pvnoteBook2_color3);
                
         CSMLPoint noteBook2_color4 = m.createCSMLPoint("noteBook2_color4", color_domain);
	           noteBook2_color4.setq(color_domain.getQualityDimensionIds());
	           double[] pvnoteBook2_color4 = { 32.0, 30.0, 38.0 };
			  // double[] pvnoteBook2_color4 = { 0.3803921569,	0.3960784314,	0.4117647059 };
	           noteBook2_color4.setv(pvnoteBook2_color4);
                
         //Fim noteBook - (Color
			 
		 //Quadro Pri Color - (Size)
                
         CSMLPoint noteBook_size1 = m.createCSMLPoint("noteBook_size1", size_domain);
	           noteBook_size1.setq(size_domain.getQualityDimensionIds());
	           double[] pvnoteBook_size1 = { 428.0, 248.0 };
	           noteBook_size1.setv(pvnoteBook_size1);
                
         CSMLPoint noteBook_size2 = m.createCSMLPoint("noteBook_size2", size_domain);
	          noteBook_size2.setq(size_domain.getQualityDimensionIds());
	          double[] pvnoteBook_size2 = { 432.0,  253.0 };
	          noteBook_size2.setv(pvnoteBook_size2);
                
        CSMLPoint noteBook_size3 = m.createCSMLPoint("noteBook_size3", size_domain);
	          noteBook_size3.setq(size_domain.getQualityDimensionIds());
	          double[] pvnoteBook_size3 = {327.0, 193.0};
	          noteBook_size3.setv(pvnoteBook_size3);
                
        // Fim Quadro Pri (Size)
		
		
		ArrayList<CSMLPoint> noteBook_points = new ArrayList();
		       noteBook_points.add(0, noteBook1);
		       noteBook_points.add(1, noteBook2);
		       noteBook_points.add(2, noteBook3);
		       noteBook_points.add(3, noteBook4);
		       noteBook_points.add(4, noteBook5);
		       noteBook_points.add(5, noteBook6);
		       noteBook_points.add(6, noteBook7);
		       noteBook_points.add(7, noteBook8);
			   
	//NoteBook2
	ArrayList<CSMLPoint> noteBook_2_points = new ArrayList();
		       noteBook_2_points.add(0, noteBook_2_1);
		       noteBook_2_points.add(1, noteBook_2_2);
		       noteBook_2_points.add(2, noteBook_2_3);
		       noteBook_2_points.add(3, noteBook_2_4);
		       noteBook_2_points.add(4, noteBook_2_5);
		       noteBook_2_points.add(5, noteBook_2_6);
		       noteBook_2_points.add(6, noteBook_2_7);
		       noteBook_2_points.add(7, noteBook_2_8);
			   
	//NoteBook3
	
	  ArrayList<CSMLPoint> noteBook_3_points = new ArrayList();
		       noteBook_3_points.add(0, noteBook_3_1);
		       noteBook_3_points.add(1, noteBook_3_2);
		       noteBook_3_points.add(2, noteBook_3_3);
		       noteBook_3_points.add(3, noteBook_3_4);
		       noteBook_3_points.add(4, noteBook_3_5);
		       noteBook_3_points.add(5, noteBook_3_6);
		       noteBook_3_points.add(6, noteBook_3_7);
		       noteBook_3_points.add(7, noteBook_3_8);
			   
	ArrayList<CSMLPoint> noteBook_4_points = new ArrayList();
		       noteBook_4_points.add(0, noteBook_4_1);
		       noteBook_4_points.add(1, noteBook_4_2);
		       noteBook_4_points.add(2, noteBook_4_3);
		       noteBook_4_points.add(3, noteBook_4_4);
		       noteBook_4_points.add(4, noteBook_4_5);
		       noteBook_4_points.add(5, noteBook_4_6);
		       noteBook_4_points.add(6, noteBook_4_7);
		       noteBook_4_points.add(7, noteBook_4_8);		   
	

     ArrayList<CSMLPoint> noteBook_color_points = new ArrayList();
               noteBook_color_points.add(0, noteBook_color1);
               noteBook_color_points.add(1, noteBook_color2);
               noteBook_color_points.add(2, noteBook_color3);
               noteBook_color_points.add(3, noteBook_color4);
			   
	ArrayList<CSMLPoint> noteBook2_color_points = new ArrayList();
               noteBook2_color_points.add(0, noteBook2_color1);
               noteBook2_color_points.add(1, noteBook2_color2);
               noteBook2_color_points.add(2, noteBook2_color3);
               noteBook2_color_points.add(3, noteBook2_color4);
                
     ArrayList<CSMLPoint> noteBook_size_points1 = new ArrayList();
               noteBook_size_points1.add(0, noteBook_size1);
               noteBook_size_points1.add(1, noteBook_size2);
               noteBook_size_points1.add(2, noteBook_size3);
	     
    // Fim Novos
	
	//Apace 
	
	
	
	
               CSMLPoint apaceBlackChair_1_1 = m.createCSMLPoint("apaceBlackChair_1_1", d1);
		           apaceBlackChair_1_1.setq(d1.getQualityDimensionIds());
		           double[] pvapaceBlackChair_1_1 = { 0.216711,0.0165581,0.000509724,4.15221e-05,5.28674e-09,1.90788e-07,-2.9224e-09 };
		           apaceBlackChair_1_1.setv(pvapaceBlackChair_1_1); 
					  
		   CSMLPoint apaceBlackChair_1_2 = m.createCSMLPoint("apaceBlackChair_1_2", d1);
		           apaceBlackChair_1_2.setq(d1.getQualityDimensionIds());
		           double[] pvapaceBlackChair_1_2 = {0.225651,0.0218807,0.000180306,6.28628e-05,6.67781e-09,5.62233e-06,4.44899e-10};
		           apaceBlackChair_1_2.setv(pvapaceBlackChair_1_2);
					  
		   CSMLPoint apaceBlackChair_1_3 = m.createCSMLPoint("apaceBlackChair_1_3", d1);
		           apaceBlackChair_1_3.setq(d1.getQualityDimensionIds());
		           double[] pvapaceBlackChair_1_3 = {0.211995,0.0149552,0.000466145,2.13985e-05,7.88529e-10,-1.38367e-06,-1.98636e-09};
		           apaceBlackChair_1_3.setv(pvapaceBlackChair_1_3);
					  
		  CSMLPoint apaceBlackChair_1_4 = m.createCSMLPoint("apaceBlackChair_1_4", d1);
		           apaceBlackChair_1_4.setq(d1.getQualityDimensionIds());
		           double[] pvapaceBlackChair_1_4 = {0.192374,0.00659174,0.000702534,0.000100109,2.33184e-08,7.51306e-08,1.26922e-08};
		           apaceBlackChair_1_4.setv(pvapaceBlackChair_1_4);
					  
		  CSMLPoint apaceBlackChair_1_5 = m.createCSMLPoint("apaceBlackChair_1_5", d1);
		           apaceBlackChair_1_5.setq(d1.getQualityDimensionIds());
		           double[] pvapaceBlackChair_1_5 = {0.219832,0.0178532,0.00044118,4.15002e-05,5.38249e-09,1.14341e-06,-1.60059e-09};
		           apaceBlackChair_1_5.setv(pvapaceBlackChair_1_5);
					  
		  CSMLPoint apaceBlackChair_1_6 = m.createCSMLPoint("apaceBlackChair_1_6", d1);
		           apaceBlackChair_1_6.setq(d1.getQualityDimensionIds());
		           double[] pvapaceBlackChair_1_6 = {0.218186,0.0170384,0.000458766,4.21195e-05,5.47396e-09,8.0845e-07,-2.07745e-09};
		           apaceBlackChair_1_6.setv(pvapaceBlackChair_1_6);
					  
		  CSMLPoint apaceBlackChair_1_7 = m.createCSMLPoint("apaceBlackChair_1_7", d1);
		          apaceBlackChair_1_7.setq(d1.getQualityDimensionIds());
		          double[] pvapaceBlackChair_1_7 = { 0.190237,0.00727442,0.00031625,1.78991e-05,2.22552e-10,-1.14584e-06,1.32816e-09};
		          apaceBlackChair_1_7.setv(pvapaceBlackChair_1_7);
					  
		  CSMLPoint apaceBlackChair_1_8 = m.createCSMLPoint("apaceBlackChair_1_8", d1);
		         apaceBlackChair_1_8.setq(d1.getQualityDimensionIds());
		         double[] pvapaceBlackChair_1_8 = {0.196956,0.00790608,0.000702935,0.000115266,3.08987e-08,8.78819e-07,1.10361e-08};
		         apaceBlackChair_1_8.setv(pvapaceBlackChair_1_8);
				 
//c2

  CSMLPoint apaceBlackChair_2_1 = m.createCSMLPoint("apaceBlackChair_2_1", d1);
		           apaceBlackChair_2_1.setq(d1.getQualityDimensionIds());
		           double[] pvapaceBlackChair_2_1 = { 0.17764,0.00458694,0.000341629,2.86014e-05,2.76296e-09,1.9135e-06,5.99328e-10 };
		           apaceBlackChair_2_1.setv(pvapaceBlackChair_2_1); 
					  
		   CSMLPoint apaceBlackChair_2_2 = m.createCSMLPoint("apaceBlackChair_2_2", d1);
		           apaceBlackChair_2_2.setq(d1.getQualityDimensionIds());
		           double[] pvapaceBlackChair_2_2 = {0.178157,0.00476248,0.000338827,2.70529e-05,2.57607e-09,1.866e-06,2.6886e-10};
		           apaceBlackChair_2_2.setv(pvapaceBlackChair_2_2);
					  
		   CSMLPoint apaceBlackChair_2_3 = m.createCSMLPoint("apaceBlackChair_2_3", d1);
		           apaceBlackChair_2_3.setq(d1.getQualityDimensionIds());
		           double[] pvapaceBlackChair_2_3 = {0.178572,0.00485331,0.000348621,2.86516e-05,2.84646e-09,1.99227e-06,3.1207e-10};
		           apaceBlackChair_2_3.setv(pvapaceBlackChair_2_3);
					  
		  CSMLPoint apaceBlackChair_2_4 = m.createCSMLPoint("apaceBlackChair_2_4", d1);
		           apaceBlackChair_2_4.setq(d1.getQualityDimensionIds());
		           double[] pvapaceBlackChair_2_4 = {0.177242,0.00443528,0.000342416,2.59155e-05,2.39317e-09,1.70978e-06,4.82191e-10};
		           apaceBlackChair_2_4.setv(pvapaceBlackChair_2_4);
					  
		  CSMLPoint apaceBlackChair_2_5 = m.createCSMLPoint("apaceBlackChair_2_5", d1);
		           apaceBlackChair_2_5.setq(d1.getQualityDimensionIds());
		           double[] pvapaceBlackChair_2_5 = {0.186267,0.00740262,0.000114575,2.53373e-06,-2.70092e-11,-2.14472e-07,-3.36777e-11};
		           apaceBlackChair_2_5.setv(pvapaceBlackChair_2_5);
					  
		  CSMLPoint apaceBlackChair_2_6 = m.createCSMLPoint("apaceBlackChair_2_6", d1);
		           apaceBlackChair_2_6.setq(d1.getQualityDimensionIds());
		           double[] pvapaceBlackChair_2_6 = {0.195504,0.00977833,0.000314296,1.20245e-05,-6.95851e-10,-1.18522e-06,-2.49459e-10};
		           apaceBlackChair_2_6.setv(pvapaceBlackChair_2_6);
					  
		  CSMLPoint apaceBlackChair_2_7 = m.createCSMLPoint("apaceBlackChair_2_7", d1);
		          apaceBlackChair_2_7.setq(d1.getQualityDimensionIds());
		          double[] pvapaceBlackChair_2_7 = {0.179484,0.00529255,0.000307881,2.80467e-05,2.56679e-09,2.00617e-06,-4.51711e-10};
		          apaceBlackChair_2_7.setv(pvapaceBlackChair_2_7);
					  
		  CSMLPoint apaceBlackChair_2_8 = m.createCSMLPoint("apaceBlackChair_2_8", d1);
		         apaceBlackChair_2_8.setq(d1.getQualityDimensionIds());
		         double[] pvapaceBlackChair_2_8 = {0.197367,0.0100752,0.000459292,2.05329e-05,-1.64996e-09,-2.03362e-06,-1.11963e-09};
		         apaceBlackChair_2_8.setv(pvapaceBlackChair_2_8); 
				 
//c3

   CSMLPoint apaceBlackChair_3_1 = m.createCSMLPoint("apaceBlackChair_3_1", d1);
		           apaceBlackChair_3_1.setq(d1.getQualityDimensionIds());
		           double[] pvapaceBlackChair_3_1 = { 0.19546,0.00715531,0.000312218,3.07691e-05,-2.83419e-09,2.59493e-06,1.03073e-09 };
		           apaceBlackChair_3_1.setv(pvapaceBlackChair_3_1); 
					  
		   CSMLPoint apaceBlackChair_3_2 = m.createCSMLPoint("apaceBlackChair_3_2", d1);
		           apaceBlackChair_3_2.setq(d1.getQualityDimensionIds());
		           double[] pvapaceBlackChair_3_2 = {0.192357,0.00184525,0.00109566,3.90587e-05,4.71701e-09,5.34764e-07,-6.56027e-09};
		           apaceBlackChair_3_2.setv(pvapaceBlackChair_3_2);
					  
		   CSMLPoint apaceBlackChair_3_3 = m.createCSMLPoint("apaceBlackChair_3_3", d1);
		           apaceBlackChair_3_3.setq(d1.getQualityDimensionIds());
		           double[] pvapaceBlackChair_3_3 = {0.190685,0.00705312,0.000112347,1.74306e-05,-7.44526e-10,1.34949e-06,-2.01635e-10};
		           apaceBlackChair_3_3.setv(pvapaceBlackChair_3_3);
					  
		  CSMLPoint apaceBlackChair_3_4 = m.createCSMLPoint("apaceBlackChair_3_4", d1);
		           apaceBlackChair_3_4.setq(d1.getQualityDimensionIds());
		           double[] pvapaceBlackChair_3_4 = {0.203229,0.00703189,0.000687895,9.12301e-05,-2.25764e-08,7.54002e-06,3.55304e-09};
		           apaceBlackChair_3_4.setv(pvapaceBlackChair_3_4);
					  
		  CSMLPoint apaceBlackChair_3_5 = m.createCSMLPoint("apaceBlackChair_3_5", d1);
		           apaceBlackChair_3_5.setq(d1.getQualityDimensionIds());
		           double[] pvapaceBlackChair_3_5 = {0.19282,0.0022152,0.000920677,4.62623e-05,4.73492e-09,3.46599e-07,-8.2908e-09};
		           apaceBlackChair_3_5.setv(pvapaceBlackChair_3_5);
					  
		  CSMLPoint apaceBlackChair_3_6 = m.createCSMLPoint("apaceBlackChair_3_6", d1);
		           apaceBlackChair_3_6.setq(d1.getQualityDimensionIds());
		           double[] pvapaceBlackChair_3_6 = {0.195389,0.00249214,0.000933052,3.33679e-05,5.27871e-09,1.11904e-06,-2.60776e-09};
		           apaceBlackChair_3_6.setv(pvapaceBlackChair_3_6);
					  
		  CSMLPoint apaceBlackChair_3_7 = m.createCSMLPoint("apaceBlackChair_3_7", d1);
		          apaceBlackChair_3_7.setq(d1.getQualityDimensionIds());
		          double[] pvapaceBlackChair_3_7 = { 0.193617,0.00223612,0.00127293,5.94733e-05,1.13539e-08,1.45163e-06,-1.17842e-08};
		          apaceBlackChair_3_7.setv(pvapaceBlackChair_3_7);
					  
		  CSMLPoint apaceBlackChair_3_8 = m.createCSMLPoint("apaceBlackChair_3_8", d1);
		         apaceBlackChair_3_8.setq(d1.getQualityDimensionIds());
		         double[] pvapaceBlackChair_3_8 = {0.194294,0.00767343,0.000388337,3.71889e-05,4.35975e-09,1.18029e-06,9.82717e-10};
		         apaceBlackChair_3_8.setv(pvapaceBlackChair_3_8);	

//c4

  CSMLPoint apaceBlackChair_4_1 = m.createCSMLPoint("apaceBlackChair_4_1", d1);
		           apaceBlackChair_4_1.setq(d1.getQualityDimensionIds());
		           double[] pvapaceBlackChair_4_1 = { 0.201468,0.0102735,0.00011982,5.85934e-05,3.55373e-09,3.50897e-06,-3.38738e-09 };
		           apaceBlackChair_4_1.setv(pvapaceBlackChair_4_1); 
					  
		   CSMLPoint apaceBlackChair_4_2 = m.createCSMLPoint("apaceBlackChair_4_2", d1);
		           apaceBlackChair_4_2.setq(d1.getQualityDimensionIds());
		           double[] pvapaceBlackChair_4_2 = {0.166674,0.00161359,7.68812e-05,1.09592e-06,7.45004e-12,3.91411e-08,-6.75945e-12};
		           apaceBlackChair_4_2.setv(pvapaceBlackChair_4_2);
					  
		   CSMLPoint apaceBlackChair_4_3 = m.createCSMLPoint("apaceBlackChair_4_3", d1);
		           apaceBlackChair_4_3.setq(d1.getQualityDimensionIds());
		           double[] pvapaceBlackChair_4_3 = {0.166967,0.0016078,9.10765e-05,1.72405e-06,1.7183e-11,6.3561e-08,-1.30942e-11};
		           apaceBlackChair_4_3.setv(pvapaceBlackChair_4_3);
					  
		  CSMLPoint apaceBlackChair_4_4 = m.createCSMLPoint("apaceBlackChair_4_4", d1);
		           apaceBlackChair_4_4.setq(d1.getQualityDimensionIds());
		           double[] pvapaceBlackChair_4_4 = {0.167402,0.00171283,8.43949e-05,1.53153e-06,1.08556e-11,5.52958e-08,-1.36136e-11};
		           apaceBlackChair_4_4.setv(pvapaceBlackChair_4_4);
					  
		  CSMLPoint apaceBlackChair_4_5 = m.createCSMLPoint("apaceBlackChair_4_5", d1);
		           apaceBlackChair_4_5.setq(d1.getQualityDimensionIds());
		           double[] pvapaceBlackChair_4_5 = {0.167675,0.0017251,7.98283e-05,1.42421e-06,8.7967e-12,4.72274e-08,-1.23784e-11};
		           apaceBlackChair_4_5.setv(pvapaceBlackChair_4_5);
					  
		  CSMLPoint apaceBlackChair_4_6 = m.createCSMLPoint("apaceBlackChair_4_6", d1);
		           apaceBlackChair_4_6.setq(d1.getQualityDimensionIds());
		           double[] pvapaceBlackChair_4_6 = {0.167744,0.0018116,8.15241e-05,1.90256e-06,1.4762e-11,6.95205e-08,-1.85343e-11};
		           apaceBlackChair_4_6.setv(pvapaceBlackChair_4_6);
					  
		  CSMLPoint apaceBlackChair_4_7 = m.createCSMLPoint("apaceBlackChair_4_7", d1);
		          apaceBlackChair_4_7.setq(d1.getQualityDimensionIds());
		          double[] pvapaceBlackChair_4_7 = { 0.167242,0.00170894,8.21534e-05,1.75939e-06,1.54637e-11,6.5482e-08,-1.44324e-11};
		          apaceBlackChair_4_7.setv(pvapaceBlackChair_4_7);
					  
		  CSMLPoint apaceBlackChair_4_8 = m.createCSMLPoint("apaceBlackChair_4_8", d1);
		         apaceBlackChair_4_8.setq(d1.getQualityDimensionIds());
		         double[] pvapaceBlackChair_4_8 = {0.185752,0.00460682,0.000102974,8.39997e-05,7.22461e-09,3.66183e-06,-2.97282e-09};
		         apaceBlackChair_4_8.setv(pvapaceBlackChair_4_8);	
				 
				 
				 
				 
	//apaceBlackChair Color
                
          CSMLPoint apaceBlackChair_color1 = m.createCSMLPoint("apaceBlackChair_color1", color_domain);
	            apaceBlackChair_color1.setq(color_domain.getQualityDimensionIds());
	            double[] pvapaceBlackChair_color1 = {49.0, 42.0, 40.0};
				//double[] pvapaceBlackChair_color1 = {0.262745098,	0.2980392157,	0.2431372549};
	            apaceBlackChair_color1.setv(pvapaceBlackChair_color1);
                
          CSMLPoint apaceBlackChair_color2 = m.createCSMLPoint("blackChair8_color2", color_domain);
	            apaceBlackChair_color2.setq(color_domain.getQualityDimensionIds());
	            double[] pvapaceBlackChair_color2 = {76.0, 78.0, 83.0}; 
	            apaceBlackChair_color2.setv(pvapaceBlackChair_color2);
                
          CSMLPoint apaceBlackChair_color3 = m.createCSMLPoint("apaceBlackChair_color3", color_domain);
	            apaceBlackChair_color3.setq(color_domain.getQualityDimensionIds());
	            double[] pvpvapaceBlackChair_color3 = { 29.0, 22.0, 20.0};
	            apaceBlackChair_color3.setv(pvpvapaceBlackChair_color3);
                
          CSMLPoint apaceBlackChair_color4 = m.createCSMLPoint("apaceBlackChair_color4", color_domain);
	            apaceBlackChair_color4.setq(color_domain.getQualityDimensionIds());
	            double[] pvapaceBlackChair_color4 = { 69.0, 61.0, 64.0 };
	            apaceBlackChair_color4.setv(pvapaceBlackChair_color4);	
			   
			   
		  ArrayList<CSMLPoint>apaceBlackChair_1_points = new ArrayList();
		        apaceBlackChair_1_points.add(0,apaceBlackChair_1_1);
		        apaceBlackChair_1_points.add(1,apaceBlackChair_1_2);
		        apaceBlackChair_1_points.add(2,apaceBlackChair_1_3);
		        apaceBlackChair_1_points.add(3,apaceBlackChair_1_4);
		        apaceBlackChair_1_points.add(4,apaceBlackChair_1_5);
		        apaceBlackChair_1_points.add(5,apaceBlackChair_1_6);
		        apaceBlackChair_1_points.add(6,apaceBlackChair_1_7);
		        apaceBlackChair_1_points.add(7,apaceBlackChair_1_8);
				
		 ArrayList<CSMLPoint>apaceBlackChair_2_points = new ArrayList();
		        apaceBlackChair_2_points.add(0,apaceBlackChair_2_1);
		        apaceBlackChair_2_points.add(1,apaceBlackChair_2_2);
		        apaceBlackChair_2_points.add(2,apaceBlackChair_2_3);
		        apaceBlackChair_2_points.add(3,apaceBlackChair_2_4);
		        apaceBlackChair_2_points.add(4,apaceBlackChair_2_5);
		        apaceBlackChair_2_points.add(5,apaceBlackChair_2_6);
		        apaceBlackChair_2_points.add(6,apaceBlackChair_2_7);
		        apaceBlackChair_2_points.add(7,apaceBlackChair_2_8);
				
		 ArrayList<CSMLPoint>apaceBlackChair_3_points = new ArrayList();
		        apaceBlackChair_3_points.add(0,apaceBlackChair_3_1);
		        apaceBlackChair_3_points.add(1,apaceBlackChair_3_2);
		        apaceBlackChair_3_points.add(2,apaceBlackChair_3_3);
		        apaceBlackChair_3_points.add(3,apaceBlackChair_3_4);
		        apaceBlackChair_3_points.add(4,apaceBlackChair_3_5);
		        apaceBlackChair_3_points.add(5,apaceBlackChair_3_6);
		        apaceBlackChair_3_points.add(6,apaceBlackChair_3_7);
		        apaceBlackChair_3_points.add(7,apaceBlackChair_3_8);
				
		 ArrayList<CSMLPoint>apaceBlackChair_4_points = new ArrayList();
		        apaceBlackChair_4_points.add(0,apaceBlackChair_4_1);
		        apaceBlackChair_4_points.add(1,apaceBlackChair_4_2);
		        apaceBlackChair_4_points.add(2,apaceBlackChair_4_3);
		        apaceBlackChair_4_points.add(3,apaceBlackChair_4_4);
		        apaceBlackChair_4_points.add(4,apaceBlackChair_4_5);
		        apaceBlackChair_4_points.add(5,apaceBlackChair_4_6);
		        apaceBlackChair_4_points.add(6,apaceBlackChair_4_7);
		        apaceBlackChair_4_points.add(7,apaceBlackChair_4_8);
			  
	     ArrayList<CSMLPoint> apaceBlackChair_color_points = new ArrayList();
                apaceBlackChair_color_points.add(0, apaceBlackChair_color1);
                apaceBlackChair_color_points.add(1, apaceBlackChair_color2);
                apaceBlackChair_color_points.add(2, apaceBlackChair_color3);
                apaceBlackChair_color_points.add(3, apaceBlackChair_color4);
				
				
				
			   

//mesa1

   CSMLPoint apaceTable_1_1 = m.createCSMLPoint("apaceTable_1_1", d1);
		           apaceTable_1_1.setq(d1.getQualityDimensionIds());
		           double[] pvapaceTable_1_1 = { 0.207822,0.000862456,0.000753057,0.000115571,-1.83263e-08,-3.36109e-06,-2.87505e-08 };
		           apaceTable_1_1.setv(pvapaceTable_1_1); 
					  
		   CSMLPoint apaceTable_1_2 = m.createCSMLPoint("apaceTable_1_2", d1);
		           apaceTable_1_2.setq(d1.getQualityDimensionIds());
		           double[] pvapaceTable_1_2 = {0.182929,0.000287693,0.000452626,9.49731e-06,3.37698e-10,-6.66553e-08,5.23165e-10};
		           apaceTable_1_2.setv(pvapaceTable_1_2);
					  
		   CSMLPoint apaceTable_1_3 = m.createCSMLPoint("apaceTable_1_3", d1);
		           apaceTable_1_3.setq(d1.getQualityDimensionIds());
		           double[] pvapaceTable_1_3 = {0.200582,9.34717e-05,0.000713747,0.000138877,-1.13535e-08,1.11689e-07,-4.22239e-08};
		           apaceTable_1_3.setv(pvapaceTable_1_3);
					  
		  CSMLPoint apaceTable_1_4 = m.createCSMLPoint("apaceTable_1_4", d1);
		           apaceTable_1_4.setq(d1.getQualityDimensionIds());
		           double[] pvapaceTable_1_4 = {0.24904,0.00555956,0.00779418,0.00306181,1.31059e-05,0.000203938,7.208e-06};
		           apaceTable_1_4.setv(pvapaceTable_1_4);
					  
		  CSMLPoint apaceTable_1_5 = m.createCSMLPoint("apaceTable_1_5", d1);
		           apaceTable_1_5.setq(d1.getQualityDimensionIds());
		           double[] pvapaceTable_1_5 = {0.183092,0.000317102,0.000516899,7.01863e-06,-1.39933e-11,-9.41651e-08,4.22516e-10};
		           apaceTable_1_5.setv(pvapaceTable_1_5);
					  
		  CSMLPoint apaceTable_1_6 = m.createCSMLPoint("apaceTable_1_6", d1);
		           apaceTable_1_6.setq(d1.getQualityDimensionIds());
		           double[] pvapaceTable_1_6 = {0.206278,0.000481254,0.000522568,0.000121111,-8.68259e-09,-2.30755e-06,-2.92047e-08};
		           apaceTable_1_6.setv(pvapaceTable_1_6);
					  
		  CSMLPoint apaceTable_1_7 = m.createCSMLPoint("apaceTable_1_7", d1);
		          apaceTable_1_7.setq(d1.getQualityDimensionIds());
		          double[] pvapaceTable_1_7 = { 0.207261,0.000818932,0.000760238,0.000123543,1.02202e-08,-2.65571e-06,-3.64566e-08};
		          apaceTable_1_7.setv(pvapaceTable_1_7);
					  
		  CSMLPoint apaceTable_1_8 = m.createCSMLPoint("apaceTable_1_8", d1);
		         apaceTable_1_8.setq(d1.getQualityDimensionIds());
		         double[] pvapaceTable_1_8 = {0.187691,0.000429888,0.000787416,1.91725e-05,-3.89576e-10,-2.4117e-07,2.32326e-09};
		         apaceTable_1_8.setv(pvapaceTable_1_8);
				 
				 
				 
        CSMLPoint apaceTable_color1 = m.createCSMLPoint("apaceTable_color1", color_domain);
	            apaceTable_color1.setq(color_domain.getQualityDimensionIds());
	            double[] pvapaceTable_color1 = {118.0, 113.0, 115.0};
				//double[] pvapaceTable_color1 = {0.262745098,	0.2980392157,	0.2431372549};
	            apaceTable_color1.setv(pvapaceTable_color1);
                
          CSMLPoint apaceTable_color2 = m.createCSMLPoint("blackChair8_color2", color_domain);
	            apaceTable_color2.setq(color_domain.getQualityDimensionIds());
	            double[] pvapaceTable_color2 = {156.0, 159.0, 158.0}; 
	            apaceTable_color2.setv(pvapaceTable_color2);
                
          CSMLPoint apaceTable_color3 = m.createCSMLPoint("apaceTable_color3", color_domain);
	            apaceTable_color3.setq(color_domain.getQualityDimensionIds());
	            double[] pvpvapaceTable_color3 = { 186.0, 189.0, 176.0};
	            apaceTable_color3.setv(pvpvapaceTable_color3);
                
          CSMLPoint apaceTable_color4 = m.createCSMLPoint("apaceTable_color4", color_domain);
	            apaceTable_color4.setq(color_domain.getQualityDimensionIds());
	            double[] pvapaceTable_color4 = { 50.0, 37.0, 32.0 };
	            apaceTable_color4.setv(pvapaceTable_color4);	
			   
			   
		  ArrayList<CSMLPoint>apaceTable_1_points = new ArrayList();
		        apaceTable_1_points.add(0,apaceTable_1_1);
		        apaceTable_1_points.add(1,apaceTable_1_2);
		        apaceTable_1_points.add(2,apaceTable_1_3);
		        apaceTable_1_points.add(3,apaceTable_1_4);
		        apaceTable_1_points.add(4,apaceTable_1_5);
		        apaceTable_1_points.add(5,apaceTable_1_6);
		        apaceTable_1_points.add(6,apaceTable_1_7);
		        apaceTable_1_points.add(7,apaceTable_1_8);
				
		ArrayList<CSMLPoint> apaceTable_color_points = new ArrayList();
                apaceTable_color_points.add(0, apaceTable_color1);
                apaceTable_color_points.add(1, apaceTable_color2);
                apaceTable_color_points.add(2, apaceTable_color3);
                apaceTable_color_points.add(3, apaceTable_color4);

        				

//bluebox	

   CSMLPoint apaceBlueBox_1_1 = m.createCSMLPoint("apaceBlueBox_11", d1);
		           apaceBlueBox_1_1.setq(d1.getQualityDimensionIds());
		           double[] pvapaceBlueBox_1_1 = { 0.180722,0.00391306,1.00195e-05,5.66598e-07,1.34095e-12,-8.43118e-09,-1.56094e-13 };
		           apaceBlueBox_1_1.setv(pvapaceBlueBox_1_1); 
					  
		   CSMLPoint apaceBlueBox_1_2 = m.createCSMLPoint("apaceBlueBox_12", d1);
		           apaceBlueBox_1_2.setq(d1.getQualityDimensionIds());
		           double[] pvapaceBlueBox_1_2 = {0.184662,0.00394256,0.000123403,1.85137e-06,2.09433e-11,1.66328e-08,1.85594e-11};
		           apaceBlueBox_1_2.setv(pvapaceBlueBox_1_2);
					  
		   CSMLPoint apaceBlueBox_1_3 = m.createCSMLPoint("apaceBlueBox_13", d1);
		           apaceBlueBox_1_3.setq(d1.getQualityDimensionIds());
		           double[] pvapaceBlueBox_1_3 = {0.182105,0.00381486,2.42611e-05,1.16401e-06,-3.51456e-12,6.14023e-08,-5.09028e-12};
		           apaceBlueBox_1_3.setv(pvapaceBlueBox_1_3);
					  
		  CSMLPoint apaceBlueBox_1_4 = m.createCSMLPoint("apaceBlueBox_14", d1);
		           apaceBlueBox_1_4.setq(d1.getQualityDimensionIds());
		           double[] pvapaceBlueBox_14 = {0.183255,0.00355936,9.80732e-05,1.75633e-06,1.28242e-12,6.07221e-08,2.3015e-11};
		           apaceBlueBox_1_4.setv(pvapaceBlueBox_14);
					  
		  CSMLPoint apaceBlueBox_1_5 = m.createCSMLPoint("apaceBlueBox_15", d1);
		           apaceBlueBox_1_5.setq(d1.getQualityDimensionIds());
		           double[] pvapaceBlueBox_1_5 = {0.183506,0.00361297,8.48796e-05,1.65251e-06,4.22669e-12,4.90768e-08,1.91094e-11};
		           apaceBlueBox_1_5.setv(pvapaceBlueBox_1_5);
					  
		  CSMLPoint apaceBlueBox_1_6 = m.createCSMLPoint("apaceBlueBox_16", d1);
		           apaceBlueBox_1_6.setq(d1.getQualityDimensionIds());
		           double[] pvapaceBlueBox_1_6 = {0.184605,0.00371807,8.84768e-05,3.73588e-06,1.09743e-11,1.16814e-07,6.70286e-11};
		           apaceBlueBox_1_6.setv(pvapaceBlueBox_1_6);
					  
		  CSMLPoint apaceBlueBox_1_7 = m.createCSMLPoint("apaceBlueBox_17", d1);
		          apaceBlueBox_1_7.setq(d1.getQualityDimensionIds());
		          double[] pvapaceBlueBox_1_7 = { 0.185681,0.00392732,8.7364e-05,3.54441e-06,5.51069e-11,2.15221e-08,2.92125e-11};
		          apaceBlueBox_1_7.setv(pvapaceBlueBox_1_7);
					  
		  CSMLPoint apaceBlueBox_1_8 = m.createCSMLPoint("apaceBlueBox_18", d1);
		         apaceBlueBox_1_8.setq(d1.getQualityDimensionIds());
		         double[] pvapaceBlueBox_1_8 = {0.185069,0.0038487,0.00012835,1.85652e-06,2.8542e-11,-2.89839e-08,2.57902e-12};
		         apaceBlueBox_1_8.setv(pvapaceBlueBox_1_8); 


          CSMLPoint apaceBlueBox_color1 = m.createCSMLPoint("apaceBlueBox_color1", color_domain);
	            apaceBlueBox_color1.setq(color_domain.getQualityDimensionIds());
	            double[] pvapaceBlueBox_color1 = {49.0, 42.0, 40.0};;
	            apaceBlueBox_color1.setv(pvapaceBlueBox_color1);
                    
          CSMLPoint apaceBlueBox_color2 = m.createCSMLPoint("apaceBlueBox_color2", color_domain);
	            apaceBlueBox_color2.setq(color_domain.getQualityDimensionIds());
	            double[] pvapaceBlueBox_color2 = {37.0, 99.0, 173.0};
	            apaceBlueBox_color2.setv(pvapaceBlueBox_color2);          
                
           CSMLPoint apaceBlueBox_color3 = m.createCSMLPoint("apaceBlueBox_color3", color_domain);
	            apaceBlueBox_color3.setq(color_domain.getQualityDimensionIds());
	            double[] pvpvapaceBlueBox_color3 = { 27.0, 91.0, 164.0};
	            apaceBlueBox_color3.setv(pvpvapaceBlueBox_color3);
                
           CSMLPoint apaceBlueBox_color4 = m.createCSMLPoint("apaceBlueBox_color4", color_domain);
	            apaceBlueBox_color4.setq(color_domain.getQualityDimensionIds());
	            double[] pvapaceBlueBox_color4 = { 8.0, 74.0, 146.0 };
	            apaceBlueBox_color4.setv(pvapaceBlueBox_color4); 
			   
			   
		  ArrayList<CSMLPoint>apaceBlueBox_1_points = new ArrayList();
		        apaceBlueBox_1_points.add(0,apaceBlueBox_1_1);
		        apaceBlueBox_1_points.add(1,apaceBlueBox_1_2);
		        apaceBlueBox_1_points.add(2,apaceBlueBox_1_3);
		       apaceBlueBox_1_points.add(3,apaceBlueBox_1_4);
		        apaceBlueBox_1_points.add(4,apaceBlueBox_1_5);
		        apaceBlueBox_1_points.add(5,apaceBlueBox_1_6);
		        apaceBlueBox_1_points.add(6,apaceBlueBox_1_7);
		        apaceBlueBox_1_points.add(7,apaceBlueBox_1_8);
				
		ArrayList<CSMLPoint> apaceBlueBox_color_points = new ArrayList();
               apaceBlueBox_color_points.add(0, apaceBlueBox_color1);
                apaceBlueBox_color_points.add(1, apaceBlueBox_color2);
                apaceBlueBox_color_points.add(2, apaceBlueBox_color3);
                apaceBlueBox_color_points.add(3, apaceBlueBox_color4);	

        				
//perciana

CSMLPoint apacePerciana_1_1 = m.createCSMLPoint("apacePerciana_1_1", d1);
		           apacePerciana_1_1.setq(d1.getQualityDimensionIds());
		           double[] pvapacePerciana_1_1 = { 0.378998,0.104842,0.000988572,0.000386519,2.32908e-07,8.17359e-05,-5.328e-08 };
		           apacePerciana_1_1.setv(pvapacePerciana_1_1); 
					  
		   CSMLPoint apacePerciana_1_2 = m.createCSMLPoint("apacePerciana_1_2", d1);
		           apacePerciana_1_2.setq(d1.getQualityDimensionIds());
		           double[] pvapacePerciana_1_2 = {0.275793,0.0357971,0.00303381,0.000741503,1.03646e-06,0.00013645,-4.03279e-07};
		           apacePerciana_1_2.setv(pvapacePerciana_1_2);
					  
		   CSMLPoint apacePerciana_1_3 = m.createCSMLPoint("apacePerciana_1_3", d1);
		           apacePerciana_1_3.setq(d1.getQualityDimensionIds());
		           double[] pvapacePerciana_1_3 = {0.228325,0.0144245,0.000268861,8.99895e-05,1.38681e-08,1.08079e-05,-1.89936e-09};
		           apacePerciana_1_3.setv(pvapacePerciana_1_3);
					  
		  CSMLPoint apacePerciana_1_4 = m.createCSMLPoint("apacePerciana_1_4", d1);
		           apacePerciana_1_4.setq(d1.getQualityDimensionIds());
		           double[] pvapacePerciana_1_4 = {0.385271,0.0844873,0.00881002,0.00338323,1.37325e-05,0.000432102,-1.23527e-05};
		           apacePerciana_1_4.setv(pvapacePerciana_1_4);
					  
		  CSMLPoint apacePerciana_1_5 = m.createCSMLPoint("apacePerciana_1_5", d1);
		           apacePerciana_1_5.setq(d1.getQualityDimensionIds());
		           double[] pvapacePerciana_1_5 = {0.315609,0.0494546,0.00148175,0.000363089,5.61976e-08,-1.71957e-06,-2.60325e-07};
		           apacePerciana_1_5.setv(pvapacePerciana_1_5);
					  
		  CSMLPoint apacePerciana_1_6 = m.createCSMLPoint("apacePerciana_1_6", d1);
		           apacePerciana_1_6.setq(d1.getQualityDimensionIds());
		           double[] pvapacePerciana_1_6 = {0.331641,0.0586059,0.00506331,0.00252162,8.78511e-06,0.000563917,-2.00155e-06};
		           apacePerciana_1_6.setv(pvapacePerciana_1_6);
					  
		  CSMLPoint apacePerciana_1_7 = m.createCSMLPoint("apacePerciana_1_7", d1);
		          apacePerciana_1_7.setq(d1.getQualityDimensionIds());
		          double[] pvapacePerciana_1_7 = { 0.350508,0.0624949,0.00704217,0.000686316,-8.83719e-07,-5.72713e-05,1.22295e-06};
		          apacePerciana_1_7.setv(pvapacePerciana_1_7);
					  
		  CSMLPoint apacePerciana_1_8 = m.createCSMLPoint("apacePerciana_1_8", d1);
		         apacePerciana_1_8.setq(d1.getQualityDimensionIds());
		         double[] pvapacePerciana_1_8 = {0.428774,0.115496,0.00368941,0.00126838,1.76333e-06,0.00010828,-2.10217e-06};
		         apacePerciana_1_8.setv(pvapacePerciana_1_8);
                         
                         
                         
                   CSMLPoint apacePerciana_2_1 = m.createCSMLPoint("apacePerciana_2_1", d1);
		           apacePerciana_2_1.setq(d1.getQualityDimensionIds());
		           double[] pvapacePerciana_2_1 = { 0.193202,0.0010689,1.90508e-05,1.60197e-05,1.76779e-10,2.37837e-07,-2.16956e-10 };
		           apacePerciana_2_1.setv(pvapacePerciana_2_1); 
					  
		   CSMLPoint apacePerciana_2_2 = m.createCSMLPoint("apacePerciana_2_2", d1);
		           apacePerciana_2_2.setq(d1.getQualityDimensionIds());
		           double[] pvapacePerciana_2_2 = {0.355666,0.00828576,0.00772098,0.00747898,3.88861e-05,0.000164827,4.1447e-05};
		           apacePerciana_2_2.setv(pvapacePerciana_2_2);
					  
		   CSMLPoint apacePerciana_2_3 = m.createCSMLPoint("apacePerciana_2_3", d1);
		           apacePerciana_2_3.setq(d1.getQualityDimensionIds());
		           double[] pvapacePerciana_2_3 = {0.316339,0.0064211,0.00455281,0.00368066,9.21867e-06,4.93381e-05,1.19177e-05};
		           apacePerciana_2_3.setv(pvapacePerciana_2_3);
					  
		  CSMLPoint apacePerciana_2_4 = m.createCSMLPoint("apacePerciana_2_4", d1);
		           apacePerciana_2_4.setq(d1.getQualityDimensionIds());
		           double[] pvapacePerciana_2_4 = {0.311555,0.00577083,0.00447172,0.00341876,6.83172e-06,2.10238e-05,1.14895e-05};
		           apacePerciana_2_4.setv(pvapacePerciana_2_4);
					  
		  CSMLPoint apacePerciana_2_5 = m.createCSMLPoint("apacePerciana_2_5", d1);
		           apacePerciana_2_5.setq(d1.getQualityDimensionIds());
		           double[] pvapacePerciana_2_5 = {0.319123,0.00578081,0.00356588,0.00367815,6.85323e-06,4.77011e-05,1.14225e-05};
		           apacePerciana_2_5.setv(pvapacePerciana_2_5);
					  
		  CSMLPoint apacePerciana_2_6 = m.createCSMLPoint("apacePerciana_2_6", d1);
		           apacePerciana_2_6.setq(d1.getQualityDimensionIds());
		           double[] pvapacePerciana_2_6 = {0.339639,0.0067134,0.00353939,0.00468438,1.57145e-05,0.000228627,1.08107e-05};
		           apacePerciana_2_6.setv(pvapacePerciana_2_6);
					  
		  CSMLPoint apacePerciana_2_7 = m.createCSMLPoint("apacePerciana_2_7", d1);
		          apacePerciana_2_7.setq(d1.getQualityDimensionIds());
		          double[] pvapacePerciana_2_7 = { 0.192833,0.0015525,3.88142e-05,1.81931e-05,4.51154e-10,6.19101e-07,1.73745e-10,353.505};
		          apacePerciana_2_7.setv(pvapacePerciana_2_7);
					  
		  CSMLPoint apacePerciana_2_8 = m.createCSMLPoint("apacePerciana_2_8", d1);
		         apacePerciana_2_8.setq(d1.getQualityDimensionIds());
		         double[] pvapacePerciana_2_8 = {0.311761,0.00228774,0.000854087,0.00297863,4.34204e-06,0.000126298,1.92813e-06};
		         apacePerciana_2_8.setv(pvapacePerciana_2_8);      
                         
				 
				 CSMLPoint apacePerciana_color1 = m.createCSMLPoint("apacePerciana_color1", color_domain);
	            apacePerciana_color1.setq(color_domain.getQualityDimensionIds());
	            double[] pvapacePerciana_color1 = {137.0, 127.0, 123.0};
				//double[] pvapacePerciana_color1 = {0.262745098,	0.2980392157,	0.2431372549};
	            apacePerciana_color1.setv(pvapacePerciana_color1);
                
          CSMLPoint apacePerciana_color2 = m.createCSMLPoint("blackChair8_color2", color_domain);
	            apacePerciana_color2.setq(color_domain.getQualityDimensionIds());
	            double[] pvapacePerciana_color2 = {147.0, 142.0, 139.0}; 
	            apacePerciana_color2.setv(pvapacePerciana_color2);
                
          CSMLPoint apacePerciana_color3 = m.createCSMLPoint("apacePerciana_color3", color_domain);
	            apacePerciana_color3.setq(color_domain.getQualityDimensionIds());
	            double[] pvpvapacePerciana_color3 = { 119.0, 112.0, 107.0};
	            apacePerciana_color3.setv(pvpvapacePerciana_color3);
                
          CSMLPoint apacePerciana_color4 = m.createCSMLPoint("apacePerciana_color4", color_domain);
	            apacePerciana_color4.setq(color_domain.getQualityDimensionIds());
	            double[] pvapacePerciana_color4 = { 140.0, 135.0, 131.0 };
	            apacePerciana_color4.setv(pvapacePerciana_color4);	
			   
			   
		  ArrayList<CSMLPoint>apacePerciana_1_points = new ArrayList();
		        apacePerciana_1_points.add(0,apacePerciana_1_1);
		        apacePerciana_1_points.add(1,apacePerciana_1_2);
		        apacePerciana_1_points.add(2,apacePerciana_1_3);
		        apacePerciana_1_points.add(3,apacePerciana_1_4);
		        apacePerciana_1_points.add(4,apacePerciana_1_5);
		        apacePerciana_1_points.add(5,apacePerciana_1_6);
		        apacePerciana_1_points.add(6,apacePerciana_1_7);
		        apacePerciana_1_points.add(7,apacePerciana_1_8);
                        
                  ArrayList<CSMLPoint>apacePerciana_2_points = new ArrayList();
		        apacePerciana_2_points.add(0,apacePerciana_2_1);
		        apacePerciana_2_points.add(1,apacePerciana_2_2);
		        apacePerciana_2_points.add(2,apacePerciana_2_3);
		        apacePerciana_2_points.add(3,apacePerciana_2_4);
		        apacePerciana_2_points.add(4,apacePerciana_2_5);
		        apacePerciana_2_points.add(5,apacePerciana_2_6);
		        apacePerciana_2_points.add(6,apacePerciana_2_7);
		        apacePerciana_2_points.add(7,apacePerciana_2_8);      
                        
				
		ArrayList<CSMLPoint> apacePerciana_color_points = new ArrayList();
                apacePerciana_color_points.add(0, apacePerciana_color1);
                apacePerciana_color_points.add(1, apacePerciana_color2);
                apacePerciana_color_points.add(2, apacePerciana_color3);
                apacePerciana_color_points.add(3, apacePerciana_color4);
				
	
				 
//note

   CSMLPoint apaceNoteBook_1_1 = m.createCSMLPoint("apaceNoteBook_1_1", d1);
		           apaceNoteBook_1_1.setq(d1.getQualityDimensionIds());
		           double[] pvapaceNoteBook_1_1 = { 0.240094,0.0257822,9.28683e-05,7.13008e-06,-1.47284e-10,-1.04855e-06,1.09409e-10 };
		           apaceNoteBook_1_1.setv(pvapaceNoteBook_1_1); 
					  
		   CSMLPoint apaceNoteBook_1_2 = m.createCSMLPoint("apaceNoteBook_1_2", d1);
		           apaceNoteBook_1_2.setq(d1.getQualityDimensionIds());
		           double[] pvapaceNoteBook_1_2 = {0.224603,0.0204569,4.88007e-05,3.39963e-06,4.08858e-11,2.29588e-07,-1.56775e-11};
		           apaceNoteBook_1_2.setv(pvapaceNoteBook_1_2);
					  
		   CSMLPoint apaceNoteBook_1_3 = m.createCSMLPoint("apaceNoteBook_1_3", d1);
		           apaceNoteBook_1_3.setq(d1.getQualityDimensionIds());
		           double[] pvapaceNoteBook_1_3 = {0.222267,0.0198256,5.18005e-05,1.05232e-05,2.45208e-10,1.22736e-06,1.53761e-11};
		           apaceNoteBook_1_3.setv(pvapaceNoteBook_1_3);
					  
		  CSMLPoint apaceNoteBook_1_4 = m.createCSMLPoint("apaceNoteBook_1_4", d1);
		           apaceNoteBook_1_4.setq(d1.getQualityDimensionIds());
		           double[] pvapaceNoteBook_1_4 = {0.231999,0.023271,1.80229e-05,2.26123e-06,1.16711e-11,6.55239e-08,8.49504e-12};
		           apaceNoteBook_1_4.setv(pvapaceNoteBook_1_4);
					  
		  CSMLPoint apaceNoteBook_1_5 = m.createCSMLPoint("apaceNoteBook_1_5", d1);
		           apaceNoteBook_1_5.setq(d1.getQualityDimensionIds());
		           double[] pvapaceNoteBook_1_5 = {0.237342,0.0266478,1.1222e-05,3.25493e-07,3.75277e-13,1.80642e-08,-4.9614e-13};
		           apaceNoteBook_1_5.setv(pvapaceNoteBook_1_5);
					  
		  CSMLPoint apaceNoteBook_1_6 = m.createCSMLPoint("apaceNoteBook_1_6", d1);
		           apaceNoteBook_1_6.setq(d1.getQualityDimensionIds());
		           double[] pvapaceNoteBook_1_6 = {0.230829,0.0233316,1.50635e-05,1.10534e-06,3.8879e-12,8.49993e-08,-2.28625e-12};
		           apaceNoteBook_1_6.setv(pvapaceNoteBook_1_6);
					  
		  CSMLPoint apaceNoteBook_1_7 = m.createCSMLPoint("apaceNoteBook_1_7", d1);
		          apaceNoteBook_1_7.setq(d1.getQualityDimensionIds());
		          double[] pvapaceNoteBook_1_7 = { 0.276584,0.045801,0.000591864,5.63829e-05,-7.75058e-09,-8.51701e-06,6.78353e-09};
		          apaceNoteBook_1_7.setv(pvapaceNoteBook_1_7);
					  
		  CSMLPoint apaceNoteBook_1_8 = m.createCSMLPoint("apaceNoteBook_1_8", d1);
		         apaceNoteBook_1_8.setq(d1.getQualityDimensionIds());
		         double[] pvapaceNoteBook_1_8 = {0.270514,0.0173609,0.00149266,0.000369275,1.89778e-07,2.68195e-05,-1.97859e-07};
		         apaceNoteBook_1_8.setv(pvapaceNoteBook_1_8);	

CSMLPoint apaceNoteBook_color1 = m.createCSMLPoint("apaceNoteBook_color1", color_domain);
	            apaceNoteBook_color1.setq(color_domain.getQualityDimensionIds());
	            double[] pvapaceNoteBook_color1 = {158.0, 164.0, 184.0};
				//double[] pvapaceNoteBook_color1 = {0.262745098,	0.2980392157,	0.2431372549};
	            apaceNoteBook_color1.setv(pvapaceNoteBook_color1);
                
          CSMLPoint apaceNoteBook_color2 = m.createCSMLPoint("blackChair8_color2", color_domain);
	            apaceNoteBook_color2.setq(color_domain.getQualityDimensionIds());
	            double[] pvapaceNoteBook_color2 = {170.0, 176.0, 195.0}; 
	            apaceNoteBook_color2.setv(pvapaceNoteBook_color2);
                
          CSMLPoint apaceNoteBook_color3 = m.createCSMLPoint("apaceNoteBook_color3", color_domain);
	            apaceNoteBook_color3.setq(color_domain.getQualityDimensionIds());
	            double[] pvpvapaceNoteBook_color3 = { 146.0, 162.0, 173.0};
	            apaceNoteBook_color3.setv(pvpvapaceNoteBook_color3);
                
          CSMLPoint apaceNoteBook_color4 = m.createCSMLPoint("apaceNoteBook_color4", color_domain);
	            apaceNoteBook_color4.setq(color_domain.getQualityDimensionIds());
	            double[] pvapaceNoteBook_color4 = { 140.0, 155.0, 179.0 };
	            apaceNoteBook_color4.setv(pvapaceNoteBook_color4);	
			   
			   
		  ArrayList<CSMLPoint>apaceNoteBook_1_points = new ArrayList();
		        apaceNoteBook_1_points.add(0,apaceNoteBook_1_1);
		        apaceNoteBook_1_points.add(1,apaceNoteBook_1_2);
		        apaceNoteBook_1_points.add(2,apaceNoteBook_1_3);
		        apaceNoteBook_1_points.add(3,apaceNoteBook_1_4);
		        apaceNoteBook_1_points.add(4,apaceNoteBook_1_5);
		        apaceNoteBook_1_points.add(5,apaceNoteBook_1_6);
		        apaceNoteBook_1_points.add(6,apaceNoteBook_1_7);
		        apaceNoteBook_1_points.add(7,apaceNoteBook_1_8);
				
		ArrayList<CSMLPoint> apaceNoteBook_color_points = new ArrayList();
                apaceNoteBook_color_points.add(0, apaceNoteBook_color1);
                apaceNoteBook_color_points.add(1, apaceNoteBook_color2);
                apaceNoteBook_color_points.add(2, apaceNoteBook_color3);
                apaceNoteBook_color_points.add(3, apaceNoteBook_color4);	

      //window

   CSMLPoint apaceWindow_1_1 = m.createCSMLPoint("apaceWindow_1_1", d1);
		           apaceWindow_1_1.setq(d1.getQualityDimensionIds());
		           double[] pvapaceWindow_1_1 = { 0.175965,0.000245654,5.29735e-05,1.79119e-05,-4.54454e-10,-1.98034e-07,3.12891e-10 };
		           apaceWindow_1_1.setv(pvapaceWindow_1_1); 
					  
		   CSMLPoint apaceWindow_1_2 = m.createCSMLPoint("apaceWindow_1_2", d1);
		           apaceWindow_1_2.setq(d1.getQualityDimensionIds());
		           double[] pvapaceWindow_1_2 = {0.194066,0.00119673,0.000173257,0.000105491,1.076e-08,9.73204e-07,-9.36017e-09};
		           apaceWindow_1_2.setv(pvapaceWindow_1_2);
					  
		   CSMLPoint apaceWindow_1_3 = m.createCSMLPoint("apaceWindow_1_3", d1);
		           apaceWindow_1_3.setq(d1.getQualityDimensionIds());
		           double[] pvapaceWindow_1_3 = {0.196134,0.00162938,0.000148066,0.000118073,1.48441e-08,2.37995e-06,-4.83544e-09};
		           apaceWindow_1_3.setv(pvapaceWindow_1_3);
					  
		  CSMLPoint apaceWindow_1_4 = m.createCSMLPoint("apaceWindow_1_4", d1);
		           apaceWindow_1_4.setq(d1.getQualityDimensionIds());
		           double[] pvapaceWindow_1_4 = {0.193705,0.00139597,0.000168777,0.000102079,1.12687e-08,1.35518e-06,-7.24867e-09};
		           apaceWindow_1_4.setv(pvapaceWindow_1_4);
					  
		  CSMLPoint apaceWindow_1_5 = m.createCSMLPoint("apaceWindow_1_5", d1);
		           apaceWindow_1_5.setq(d1.getQualityDimensionIds());
		           double[] pvapaceWindow_1_5 = {0.187079,0.000779912,0.000146798,6.27195e-05,3.22372e-09,-1.38587e-07,-5.08192e-09};
		           apaceWindow_1_5.setv(pvapaceWindow_1_5);
					  
		  CSMLPoint apaceWindow_1_6 = m.createCSMLPoint("apaceWindow_1_6", d1);
		           apaceWindow_1_6.setq(d1.getQualityDimensionIds());
		           double[] pvapaceWindow_1_6 = {0.187528,0.000618171,0.000146689,6.55565e-05,3.47619e-09,-3.33619e-07,-5.4078e-09};
		           apaceWindow_1_6.setv(pvapaceWindow_1_6);
					  
		  CSMLPoint apaceWindow_1_7 = m.createCSMLPoint("apaceWindow_1_7", d1);
		          apaceWindow_1_7.setq(d1.getQualityDimensionIds());
		          double[] pvapaceWindow_1_7 = { 0.187994,0.000715588,0.000146231,6.84279e-05,4.67315e-09,-6.22244e-08,-5.00149e-09};
		          apaceWindow_1_7.setv(pvapaceWindow_1_7);
					  
		  CSMLPoint apaceWindow_1_8 = m.createCSMLPoint("apaceWindow_1_8", d1);
		         apaceWindow_1_8.setq(d1.getQualityDimensionIds());
		         double[] pvapaceWindow_1_8 = {0.212006,0.000647817,0.000820819,0.000259516,-5.66705e-08,-5.362e-06,-1.05521e-07};
		         apaceWindow_1_8.setv(pvapaceWindow_1_8);	

CSMLPoint apaceWindow_color1 = m.createCSMLPoint("apaceWindow_color1", color_domain);
	            apaceWindow_color1.setq(color_domain.getQualityDimensionIds());
	            double[] pvapaceWindow_color1 = {135.0, 127.0, 140.0};
				//double[] pvapaceWindow_color1 = {0.262745098,	0.2980392157,	0.2431372549};
	            apaceWindow_color1.setv(pvapaceWindow_color1);
                
          CSMLPoint apaceWindow_color2 = m.createCSMLPoint("blackChair8_color2", color_domain);
	            apaceWindow_color2.setq(color_domain.getQualityDimensionIds());
	            double[] pvapaceWindow_color2 = {200.0, 184.0, 209.0}; 
	            apaceWindow_color2.setv(pvapaceWindow_color2);
                
          CSMLPoint apaceWindow_color3 = m.createCSMLPoint("apaceWindow_color3", color_domain);
	            apaceWindow_color3.setq(color_domain.getQualityDimensionIds());
	            double[] pvpvapaceWindow_color3 = { 105.0, 97.0, 110.0};
	            apaceWindow_color3.setv(pvpvapaceWindow_color3);
                
          CSMLPoint apaceWindow_color4 = m.createCSMLPoint("apaceWindow_color4", color_domain);
	            apaceWindow_color4.setq(color_domain.getQualityDimensionIds());
	            double[] pvapaceWindow_color4 = { 70.0, 60.0, 73.0 };
	            apaceWindow_color4.setv(pvapaceWindow_color4);	
			   
			   
		  ArrayList<CSMLPoint>apaceWindow_1_points = new ArrayList();
		        apaceWindow_1_points.add(0,apaceWindow_1_1);
		        apaceWindow_1_points.add(1,apaceWindow_1_2);
		        apaceWindow_1_points.add(2,apaceWindow_1_3);
		        apaceWindow_1_points.add(3,apaceWindow_1_4);
		        apaceWindow_1_points.add(4,apaceWindow_1_5);
		        apaceWindow_1_points.add(5,apaceWindow_1_6);
		        apaceWindow_1_points.add(6,apaceWindow_1_7);
		        apaceWindow_1_points.add(7,apaceWindow_1_8);
				
		ArrayList<CSMLPoint> apaceWindow_color_points = new ArrayList();
                apaceWindow_color_points.add(0, apaceWindow_color1);
                apaceWindow_color_points.add(1, apaceWindow_color2);
                apaceWindow_color_points.add(2, apaceWindow_color3);
                apaceWindow_color_points.add(3, apaceWindow_color4);	

	//Fim apace
	
	
	 //Position Points
              
     //A1
              
     CSMLPoint area1_v1 = m.createCSMLPoint("area1_v1",position_domain);
	      area1_v1.setq(position_domain.getQualityDimensionIds());
	      double[] parea1_v1 = {0.0,0.0};
	      area1_v1.setv(parea1_v1);
                
     CSMLPoint area1_v2 = m.createCSMLPoint("area1_v2",position_domain);
	      area1_v2.setq(position_domain.getQualityDimensionIds());
	      double[] parea1_v2 = {0.0,240.0};
	      area1_v2.setv(parea1_v2);
              
     CSMLPoint area1_v3 = m.createCSMLPoint("area1_v3",position_domain);
	      area1_v3.setq(position_domain.getQualityDimensionIds());
	      double[] parea1_v3 = {160.0,0.0};
	      area1_v3.setv(parea1_v3);
              
     //A2 
              
     CSMLPoint area2_v1 = m.createCSMLPoint("area2_v1",position_domain);
	      area2_v1.setq(position_domain.getQualityDimensionIds());
	      double[] parea2_v1 = {160.0,0.0};
	      area2_v1.setv(parea2_v1);
              
     CSMLPoint area2_v2 = m.createCSMLPoint("area2_v2",position_domain);
	      area2_v2.setq(position_domain.getQualityDimensionIds());
	      double[] parea2_v2 = {0.0,240.0};
	      area2_v2.setv(parea1_v2);
              
     CSMLPoint area2_v3 = m.createCSMLPoint("area2_v3",position_domain);
	      area2_v3.setq(position_domain.getQualityDimensionIds());
	      double[] parea2_v3 = {160.0,240.0};
	      area2_v3.setv(parea2_v3);
              
     //A3 
              
      CSMLPoint area3_v1 = m.createCSMLPoint("area3_v1",position_domain);
	       area3_v1.setq(position_domain.getQualityDimensionIds());
	       double[] parea3_v1 = {160.0,0.0};
	       area3_v1.setv(parea3_v1);
              
      CSMLPoint area3_v2 = m.createCSMLPoint("area3_v2",position_domain);
	       area3_v2.setq(position_domain.getQualityDimensionIds());
	       double[] parea3_v2 = {160.0,240.0};
	       area3_v2.setv(parea3_v2);
              
      CSMLPoint area3_v3 = m.createCSMLPoint("area3_v3",position_domain);
	       area3_v3.setq(position_domain.getQualityDimensionIds());
	       double[] parea3_v3 = {320.0,240.0};
	       area3_v3.setv(parea3_v3);

      //A4 
              
      CSMLPoint area4_v1 = m.createCSMLPoint("area4_v1",position_domain);
	       area4_v1.setq(position_domain.getQualityDimensionIds());
	       double[] parea4_v1 = {160.0,0.0};
	       area4_v1.setv(parea4_v1);
              
      CSMLPoint area4_v2 = m.createCSMLPoint("area3_v2",position_domain);
	       area4_v2.setq(position_domain.getQualityDimensionIds());
	       double[] parea4_v2 = {320.0,240.0};
	       area4_v2.setv(parea4_v2);
              
      CSMLPoint area4_v3 = m.createCSMLPoint("area4_v3",position_domain);
	       area4_v3.setq(position_domain.getQualityDimensionIds());
	       double[] parea4_v3 = {320.0,0.0};
	       area4_v3.setv(parea4_v3);

		   
     //fr2
	 
	 //Position Points
              
     //A1
              
     CSMLPoint area1_v1_fr2 = m.createCSMLPoint("area1_v1_fr2",position_domain);
	      area1_v1_fr2.setq(position_domain.getQualityDimensionIds());
	      double[] parea1_v1_fr2 = {0.0,0.0};
	      area1_v1_fr2.setv(parea1_v1_fr2);
                
     CSMLPoint area1_v2_fr2 = m.createCSMLPoint("area1_v2_fr2",position_domain);
	      area1_v2_fr2.setq(position_domain.getQualityDimensionIds());
	      double[] parea1_v2_fr2 = {0.0,480.0};
	      area1_v2_fr2.setv(parea1_v2_fr2);
              
     CSMLPoint area1_v3_fr2 = m.createCSMLPoint("area1_v3_fr2",position_domain);
	      area1_v3_fr2.setq(position_domain.getQualityDimensionIds());
	      double[] parea1_v3_fr2 = {360.0,0.0};
	      area1_v3_fr2.setv(parea1_v3_fr2);
              
     //A2 
              
     CSMLPoint area2_v1_fr2 = m.createCSMLPoint("area2_v1_fr2",position_domain);
	      area2_v1_fr2.setq(position_domain.getQualityDimensionIds());
	      double[] parea2_v1_fr2 = {360.0,0.0};
	      area2_v1_fr2.setv(parea2_v1_fr2);
              
     CSMLPoint area2_v2_fr2 = m.createCSMLPoint("area2_v2_fr2",position_domain);
	      area2_v2_fr2.setq(position_domain.getQualityDimensionIds());
	      double[] parea2_v2_fr2 = {0.0,480.0};
	      area2_v2_fr2.setv(parea1_v2_fr2);
              
     CSMLPoint area2_v3_fr2 = m.createCSMLPoint("area2_v3_fr2",position_domain);
	      area2_v3_fr2.setq(position_domain.getQualityDimensionIds());
	      double[] parea2_v3_fr2 = {360.0,480.0};
	      area2_v3_fr2.setv(parea2_v3_fr2);
              
     //A3 
              
      CSMLPoint area3_v1_fr2 = m.createCSMLPoint("area3_v1_fr2",position_domain);
	       area3_v1_fr2.setq(position_domain.getQualityDimensionIds());
	       double[] parea3_v1_fr2 = {360.0,0.0};
	       area3_v1_fr2.setv(parea3_v1_fr2);
              
      CSMLPoint area3_v2_fr2 = m.createCSMLPoint("area3_v2_fr2",position_domain);
	       area3_v2_fr2.setq(position_domain.getQualityDimensionIds());
	       double[] parea3_v2_fr2 = {360.0,480.0};
	       area3_v2_fr2.setv(parea3_v2_fr2);
              
      CSMLPoint area3_v3_fr2 = m.createCSMLPoint("area3_v3_fr2",position_domain);
	       area3_v3_fr2.setq(position_domain.getQualityDimensionIds());
	       double[] parea3_v3_fr2 = {720.0,480.0};
	       area3_v3_fr2.setv(parea3_v3_fr2);

      //A4 
              
      CSMLPoint area4_v1_fr2 = m.createCSMLPoint("area4_v1_fr2",position_domain);
	       area4_v1_fr2.setq(position_domain.getQualityDimensionIds());
	       double[] parea4_v1_fr2 = {360.0,0.0};
	       area4_v1_fr2.setv(parea4_v1_fr2);
              
      CSMLPoint area4_v2_fr2 = m.createCSMLPoint("area4_v2_fr2",position_domain);
	       area4_v2_fr2.setq(position_domain.getQualityDimensionIds());
	       double[] parea4_v2_fr2 = {720.0,480.0};
	       area4_v2_fr2.setv(parea4_v2_fr2);
              
      CSMLPoint area4_v3_fr2 = m.createCSMLPoint("area4_v3_fr2",position_domain);
	       area4_v3_fr2.setq(position_domain.getQualityDimensionIds());
	       double[] parea4_v3_fr2 = {720.0,0.0};
	       area4_v3_fr2.setv(parea4_v3_fr2);
		   
     //fr3		
	 
	 //fr3
	 
	 //Position Points
              
     //A1
              
     CSMLPoint area1_v1_fr3 = m.createCSMLPoint("area1_v1_fr3",position_domain);
	      area1_v1_fr3.setq(position_domain.getQualityDimensionIds());
	      double[] parea1_v1_fr3 = {0.0,0.0};
	      area1_v1_fr3.setv(parea1_v1_fr3);
                
     CSMLPoint area1_v2_fr3 = m.createCSMLPoint("area1_v2_fr3",position_domain);
	      area1_v2_fr3.setq(position_domain.getQualityDimensionIds());
	      double[] parea1_v2_fr3 = {0.0,720.0};
	      area1_v2_fr3.setv(parea1_v2_fr3);
              
     CSMLPoint area1_v3_fr3 = m.createCSMLPoint("area1_v3_fr3",position_domain);
	      area1_v3_fr3.setq(position_domain.getQualityDimensionIds());
	      double[] parea1_v3_fr3 = {640.0,0.0};
	      area1_v3_fr3.setv(parea1_v3_fr3);
              
     //A2 
              
     CSMLPoint area2_v1_fr3 = m.createCSMLPoint("area2_v1_fr3",position_domain);
	      area2_v1_fr3.setq(position_domain.getQualityDimensionIds());
	      double[] parea2_v1_fr3 = {640.0,0.0};
	      area2_v1_fr3.setv(parea2_v1_fr3);
              
     CSMLPoint area2_v2_fr3 = m.createCSMLPoint("area2_v2_fr3",position_domain);
	      area2_v2_fr3.setq(position_domain.getQualityDimensionIds());
	      double[] parea2_v2_fr3 = {0.0,720.0};
	      area2_v2_fr3.setv(parea1_v2_fr3);
              
     CSMLPoint area2_v3_fr3 = m.createCSMLPoint("area2_v3_fr3",position_domain);
	      area2_v3_fr3.setq(position_domain.getQualityDimensionIds());
	      double[] parea2_v3_fr3 = {640.0,720.0};
	      area2_v3_fr3.setv(parea2_v3_fr3);
              
     //A3 
              
      CSMLPoint area3_v1_fr3 = m.createCSMLPoint("area3_v1_fr3",position_domain);
	       area3_v1_fr3.setq(position_domain.getQualityDimensionIds());
	       double[] parea3_v1_fr3 = {640.0,0.0};
	       area3_v1_fr3.setv(parea3_v1_fr3);
              
      CSMLPoint area3_v2_fr3 = m.createCSMLPoint("area3_v2_fr3",position_domain);
	       area3_v2_fr3.setq(position_domain.getQualityDimensionIds());
	       double[] parea3_v2_fr3 = {640.0,720.0};
	       area3_v2_fr3.setv(parea3_v2_fr3);
              
      CSMLPoint area3_v3_fr3 = m.createCSMLPoint("area3_v3_fr3",position_domain);
	       area3_v3_fr3.setq(position_domain.getQualityDimensionIds());
	       double[] parea3_v3_fr3 = {1280.0,720.0};
	       area3_v3_fr3.setv(parea3_v3_fr3);

      //A4 
              
      CSMLPoint area4_v1_fr3 = m.createCSMLPoint("area4_v1_fr3",position_domain);
	       area4_v1_fr3.setq(position_domain.getQualityDimensionIds());
	       double[] parea4_v1_fr3 = {640.0,0.0};
	       area4_v1_fr3.setv(parea4_v1_fr3);
              
      CSMLPoint area4_v2_fr3 = m.createCSMLPoint("area4_v2_fr3",position_domain);
	       area4_v2_fr3.setq(position_domain.getQualityDimensionIds());
	       double[] parea4_v2_fr3 = {1280.0,720.0};
	       area4_v2_fr3.setv(parea4_v2_fr3);
              
      CSMLPoint area4_v3_fr3 = m.createCSMLPoint("area4_v3_fr3",position_domain);
	       area4_v3_fr3.setq(position_domain.getQualityDimensionIds());
	       double[] parea4_v3_fr3 = {1280.0,0.0};
	       area4_v3_fr3.setv(parea4_v3_fr3);
	
		   
	//fim fr3	   
		   
     
      CSMLConceptualSpaceReasoner reasoner = new CSMLConceptualSpaceReasoner(m);
		     	    
                  
			  
	 ArrayList<CSMLPoint> qPri_points = new ArrayList();
		       qPri_points.add(0, qPri1);
		       qPri_points.add(1, qPri2);
		       qPri_points.add(2, qPri3);
		       qPri_points.add(3, qPri4);
		       qPri_points.add(4, qPri5);
		       qPri_points.add(5, qPri6);
		       qPri_points.add(6, qPri7);
		       qPri_points.add(7, qPri8);

     ArrayList<CSMLPoint> qPri_color_points = new ArrayList();
               qPri_color_points.add(0, qPri_color1);
               qPri_color_points.add(1, qPri_color2);
               qPri_color_points.add(2, qPri_color3);
               qPri_color_points.add(3, qPri_color4);
                
     ArrayList<CSMLPoint> qPri_size_points1 = new ArrayList();
               qPri_size_points1.add(0, qPri_size1);
               qPri_size_points1.add(1, qPri_size2);
               qPri_size_points1.add(2, qPri_size3);
			   
			   
	 ArrayList<CSMLPoint> arCond_points = new ArrayList();
		       arCond_points.add(0, arCond1);
		       arCond_points.add(1, arCond2);
		       arCond_points.add(2, arCond3);
		       arCond_points.add(3, arCond4);
		       arCond_points.add(4, arCond5);
		       arCond_points.add(5, arCond6);
		       arCond_points.add(6, arCond7);
		       arCond_points.add(7, arCond8); 
                   
      ArrayList<CSMLPoint> arCond_color_points = new ArrayList();
                arCond_color_points.add(0, arCond_color1);
                arCond_color_points.add(1, arCond_color2);
                arCond_color_points.add(2, arCond_color3);
                arCond_color_points.add(3, arCond_color4);
                
                
      ArrayList<CSMLPoint> arCond_size_points = new ArrayList();
                arCond_size_points.add(0, arCond_size1);
                arCond_size_points.add(1, arCond_size2);
                arCond_size_points.add(2, arCond_size3);
				
	
      ArrayList<CSMLPoint> tv_points = new ArrayList();
		        tv_points.add(0, tv1);
		        tv_points.add(1, tv2);
		        tv_points.add(2, tv3);
		        tv_points.add(3, tv4);
		        tv_points.add(4, tv5);
		        tv_points.add(5, tv6);
		        tv_points.add(6, tv7);
		        tv_points.add(7, tv8); 				
                
      ArrayList<CSMLPoint> tv_color_points = new ArrayList();
                tv_color_points.add(0, tv_color1);
                tv_color_points.add(1, tv_color2);
                tv_color_points.add(2, tv_color3);
                tv_color_points.add(3, tv_color4);           
                
      ArrayList<CSMLPoint> tv_size_points = new ArrayList();
                tv_size_points.add(0, tv_size1);
                tv_size_points.add(1, tv_size2);
                tv_size_points.add(2, tv_size3);    
       
				   
	  ArrayList<CSMLPoint> pextintor_points = new ArrayList();
		        pextintor_points.add(0, pextintor1);
		        pextintor_points.add(1, pextintor2);
		        pextintor_points.add(2, pextintor3);
	            pextintor_points.add(3, pextintor4);
		        pextintor_points.add(4, pextintor5);
		        pextintor_points.add(5, pextintor6);
		        pextintor_points.add(6, pextintor7);
		        pextintor_points.add(7, pextintor8); 		
                           
     ArrayList<CSMLPoint> pextintor_color_points = new ArrayList();
                pextintor_color_points.add(0, pextintor_color1);
                pextintor_color_points.add(1, pextintor_color2);
                pextintor_color_points.add(2, pextintor_color3);
                pextintor_color_points.add(3, pextintor_color4);
                
     ArrayList<CSMLPoint> pextintor_size_points1 = new ArrayList();
                pextintor_size_points1.add(0, pextintor_size1);
                pextintor_size_points1.add(1, pextintor_size2);
                pextintor_size_points1.add(2, pextintor_size3);   
                
                				
    ArrayList<CSMLPoint> tapeteFr_points = new ArrayList();
		      tapeteFr_points.add(0, tapeteFr1);
		      tapeteFr_points.add(1, tapeteFr2);
		      tapeteFr_points.add(2, tapeteFr3);
		      tapeteFr_points.add(3, tapeteFr4);
		      tapeteFr_points.add(4, tapeteFr5);
		      tapeteFr_points.add(5, tapeteFr6);
		      tapeteFr_points.add(6, tapeteFr7);
		      tapeteFr_points.add(7, tapeteFr8);      
                        
                
    ArrayList<CSMLPoint> tapeteFr_color_points = new ArrayList();
              tapeteFr_color_points.add(0, tapeteFr_color1);
              tapeteFr_color_points.add(1, tapeteFr_color2);
              tapeteFr_color_points.add(2, tapeteFr_color3);
              tapeteFr_color_points.add(3, tapeteFr_color4);   
                
    ArrayList<CSMLPoint> tapeteFr_size_points1 = new ArrayList();
              tapeteFr_size_points1.add(0, tapeteFr_size1);
              tapeteFr_size_points1.add(1, tapeteFr_size2);
              tapeteFr_size_points1.add(2, tapeteFr_size3);   

 
			  
 //Fim			  
				   
				   
	ArrayList<CSMLPoint> area1_points = new ArrayList();
              area1_points.add(0, area1_v1);
              area1_points.add(1, area1_v2);
             area1_points.add(2, area1_v3);
              
    ArrayList<CSMLPoint> area2_points = new ArrayList();
              area2_points.add(0, area2_v1);
              area2_points.add(1, area2_v2);
              area2_points.add(2, area2_v3);
              
    ArrayList<CSMLPoint> area3_points = new ArrayList();
              area3_points.add(0, area3_v1);
              area3_points.add(1, area3_v2);
              area3_points.add(2, area3_v3);
              
    ArrayList<CSMLPoint> area4_points = new ArrayList();
              area4_points.add(0, area4_v1);
              area4_points.add(1, area4_v2);
              area4_points.add(2, area4_v3);  
                		
		        	  
	//Regioes	
					  
	// Regioes - Posicao
	
	CSMLRegion area1_region = reasoner.convexHull(area1_points, "area1_region");
    CSMLRegion area2_region = reasoner.convexHull(area2_points, "area2_region");
    CSMLRegion area3_region = reasoner.convexHull(area3_points, "area3_region");
    CSMLRegion area4_region = reasoner.convexHull(area4_points, "area4_region");
	CSMLPoint area1_region_mid =  area1_region.getCentroidAsPoint();
	CSMLPoint area2_region_mid =  area2_region.getCentroidAsPoint();
	CSMLPoint area3_region_mid =  area3_region.getCentroidAsPoint();
	CSMLPoint area4_region_mid =  area4_region.getCentroidAsPoint();
					  
	ArrayList<CSMLPoint> all_area_points = new ArrayList();
              all_area_points.add(0, area1_region_mid);
              all_area_points.add(1, area2_region_mid);
              all_area_points.add(2, area3_region_mid);
			  all_area_points.add(3, area4_region_mid);
			  
			  
			  
//fr2

ArrayList<CSMLPoint> area1_fr2_points = new ArrayList();
              area1_fr2_points.add(0, area1_v1_fr2);
              area1_fr2_points.add(1, area1_v2_fr2);
             area1_fr2_points.add(2, area1_v3_fr2);
              
    ArrayList<CSMLPoint> area2_fr2_points = new ArrayList();
              area2_fr2_points.add(0, area2_v1_fr2);
              area2_fr2_points.add(1, area2_v2_fr2);
              area2_fr2_points.add(2, area2_v3_fr2);
              
    ArrayList<CSMLPoint> area3_fr2_points = new ArrayList();
              area3_fr2_points.add(0, area3_v1_fr2);
              area3_fr2_points.add(1, area3_v2_fr2);
              area3_fr2_points.add(2, area3_v3_fr2);
              
    ArrayList<CSMLPoint> area4_fr2_points = new ArrayList();
              area4_fr2_points.add(0, area4_v1_fr2);
              area4_fr2_points.add(1, area4_v2_fr2);
              area4_fr2_points.add(2, area4_v3_fr2);  
                		
		        	  
   //Regioes	
					  
   // Regioes - Posicao
	
    CSMLRegion area1_fr2_region = reasoner.convexHull(area1_fr2_points, "area1_fr2_region");
    CSMLRegion area2_fr2_region = reasoner.convexHull(area2_fr2_points, "area2_fr2_region");
    CSMLRegion area3_fr2_region = reasoner.convexHull(area3_fr2_points, "area3_fr2_region");
    CSMLRegion area4_fr2_region = reasoner.convexHull(area4_fr2_points, "area4_fr2_region");
    CSMLPoint area1_fr2_region_mid =  area1_fr2_region.getCentroidAsPoint();
    CSMLPoint area2_fr2_region_mid =  area2_fr2_region.getCentroidAsPoint();
    CSMLPoint area3_fr2_region_mid =  area3_fr2_region.getCentroidAsPoint();
    CSMLPoint area4_fr2_region_mid =  area4_fr2_region.getCentroidAsPoint();
					  
    ArrayList<CSMLPoint> all_area_fr2_points = new ArrayList();
              all_area_fr2_points.add(0, area1_fr2_region_mid);
              all_area_fr2_points.add(1, area2_fr2_region_mid);
              all_area_fr2_points.add(2, area3_fr2_region_mid);
	      all_area_fr2_points.add(3, area4_fr2_region_mid);

//fr3

ArrayList<CSMLPoint> area1_fr3_points = new ArrayList();
              area1_fr3_points.add(0, area1_v1_fr3);
              area1_fr3_points.add(1, area1_v2_fr3);
             area1_fr3_points.add(2, area1_v3_fr3);
              
    ArrayList<CSMLPoint> area2_fr3_points = new ArrayList();
              area2_fr3_points.add(0, area2_v1_fr3);
              area2_fr3_points.add(1, area2_v2_fr3);
              area2_fr3_points.add(2, area2_v3_fr3);
              
    ArrayList<CSMLPoint> area3_fr3_points = new ArrayList();
              area3_fr3_points.add(0, area3_v1_fr3);
              area3_fr3_points.add(1, area3_v2_fr3);
              area3_fr3_points.add(2, area3_v3_fr3);
              
    ArrayList<CSMLPoint> area4_fr3_points = new ArrayList();
              area4_fr3_points.add(0, area4_v1_fr3);
              area4_fr3_points.add(1, area4_v2_fr3);
              area4_fr3_points.add(2, area4_v3_fr3);  
                		
		        	  
   //Regioes	
					  
   // Regioes - Posicao
	
    CSMLRegion area1_fr3_region = reasoner.convexHull(area1_fr3_points, "area1_fr3_region");
    CSMLRegion area2_fr3_region = reasoner.convexHull(area2_fr3_points, "area2_fr3_region");
    CSMLRegion area3_fr3_region = reasoner.convexHull(area3_fr3_points, "area3_fr3_region");
    CSMLRegion area4_fr3_region = reasoner.convexHull(area4_fr3_points, "area4_fr3_region");
    CSMLPoint area1_fr3_region_mid =  area1_fr3_region.getCentroidAsPoint();
    CSMLPoint area2_fr3_region_mid =  area2_fr3_region.getCentroidAsPoint();
    CSMLPoint area3_fr3_region_mid =  area3_fr3_region.getCentroidAsPoint();
    CSMLPoint area4_fr3_region_mid =  area4_fr3_region.getCentroidAsPoint();
					  
    ArrayList<CSMLPoint> all_area_fr3_points = new ArrayList();
              all_area_fr3_points.add(0, area1_fr3_region_mid);
              all_area_fr3_points.add(1, area2_fr3_region_mid);
              all_area_fr3_points.add(2, area3_fr3_region_mid);
	      all_area_fr3_points.add(3, area4_fr3_region_mid);




    // Regioes - Objetos (Shape/Color)
	
	    //CSMLRegion painelGpin_region = reasoner.convexHull(painelGpin_points, "painelGpin_region");
		//CSMLRegion painelGpin_color_region = reasoner.convexHull(painelGpin_color_points, "painelGpin_color_region");
		//CSMLRegion painelGpin_size_region = reasoner.convexHull(painelGpin_size_points1, "painelGpin_size_region");
	
	    //CSMLRegion painelLuz_region = reasoner.convexHull(painelLuz_points, "painelLuz_region");
		//CSMLRegion painelLuz_color_region = reasoner.convexHull(painelLuz_color_points, "painelLuz_color_region");
		//CSMLRegion painelLuz_size_region = reasoner.convexHull(painelLuz_size_points1, "painelLuz_size_region");
					  
		//CSMLRegion parm_dir_region = reasoner.convexHull(qPri_points, "parm_dir_region");
		//CSMLRegion parm_dir_color_region = reasoner.convexHull(qPri_color_points, "parm_dir_color_region");
		//CSMLRegion parm_dir_size_region = reasoner.convexHull(parm_dir_size_points1, "parm_dir_size_region");
					  
		CSMLRegion qPri_region = reasoner.convexHull(qPri_points, "qPri_region");
        CSMLRegion qPri_color_region = reasoner.convexHull(qPri_color_points, "qPri_color_region");
        CSMLRegion qPri_size_region = reasoner.convexHull(qPri_size_points1, "qPri_size_region");
					  
        CSMLRegion tv_region = reasoner.convexHull(tv_points, "tv_region");
        CSMLRegion tv_color_region = reasoner.convexHull(tv_color_points, "tv_color_region");
        CSMLRegion tv_size_region = reasoner.convexHull(tv_size_points, "qPri_size_region");
					  
        CSMLRegion arCond_region = reasoner.convexHull(arCond_points, "arCond_region");
        CSMLRegion arCond_color_region = reasoner.convexHull(arCond_color_points, "arCond_color_region");
        CSMLRegion arCond_size_region = reasoner.convexHull(arCond_size_points, "arCond_size_region");
                      
	    //CSMLRegion elevator_region = reasoner.convexHull(elevator_points, "elevator_region");
		//CSMLRegion elevator_color_region = reasoner.convexHull(elevator_color_points, "elevator_color_region");

		CSMLRegion tapeteFr_region = reasoner.convexHull(tapeteFr_points, "tapeteFr_region");
		CSMLRegion tapeteFr_color_region = reasoner.convexHull(tapeteFr_color_points, "tapeteFr_color_region");
		CSMLRegion tapeteFr_size_region = reasoner.convexHull(tapeteFr_size_points1, "tapeteFr_size_region");
		
		
		//CSMLRegion armarioPr_region = reasoner.convexHull(armarioPr_points, "armarioPr_region");
		//CSMLRegion armarioPrb_region = reasoner.convexHull(armarioPrb_points, "armarioPrb_region");
		//CSMLRegion armarioPr_color_region = reasoner.convexHull(armarioPr_color_points, "armarioPr_color_region");
		//CSMLRegion armarioPr_size_region = reasoner.convexHull(armarioPr_size_points1, "armarioPr_size_region");
		
		 //CSMLRegion painelLab_region = reasoner.convexHull(painelLab_points, "painelLab_region");
		 //CSMLRegion painelLab_color_region = reasoner.convexHull(painelLab_color_points, "painelLab_color_region");
		 //CSMLRegion painelLab_size_region = reasoner.convexHull(painelLab_size_points1, "painelLab_size_region");
		 
		// CSMLRegion painelLabP_size_region = reasoner.convexHull(painelLabP_size_points1, "painelLabP_size_region");
		 
		 //CSMLRegion painelArx_region = reasoner.convexHull(painelArx_points, "painelArx_region");
		 //CSMLRegion painelAry_region = reasoner.convexHull(painelAry_points, "painelAry_region");
		 //CSMLRegion painelArx_color_region = reasoner.convexHull(qPri_color_points, "painelArx_color_region");
		 //CSMLRegion painelArx_size_region = reasoner.convexHull(painelArx_size_points1, "painelArx_size_region");
		 
		 //CSMLRegion mesaBaiax_region = reasoner.convexHull(mesaBaiax_points, "mesaBaiax_region");	 
		 //CSMLRegion mesaBaiay_region = reasoner.convexHull(mesaBaiay_points, "mesaBaiay_region");
		 //CSMLRegion mesaBaia_color_region = reasoner.convexHull(mesaBaia_color_points, "mesaBaia_color_region");
		 //CSMLRegion mesaBaia_size_region = reasoner.convexHull(mesaBaia_size_points1, "mesaBaia_size_region");
		 
		 CSMLRegion pextintor_region = reasoner.convexHull(pextintor_points, "pextintor_region");
		 CSMLRegion pextintor_color_region = reasoner.convexHull(pextintor_color_points, "pextintor_color_region");
		 CSMLRegion pextintor_size_region = reasoner.convexHull(pextintor_size_points1, "pextintor_size_region");
		 
		CSMLRegion numberSeven_region = reasoner.convexHull(numberSeven_points, "numberSeven_region");
        CSMLRegion numberSeven_color_region = reasoner.convexHull(numberSeven_color_points, "numberSeven_color_region");
        CSMLRegion numberSeven_size_region = reasoner.convexHull(numberSeven_size_points1, "numberSeven_size_region");

		CSMLRegion numberSix_region = reasoner.convexHull(numberSix_points, "numberSix_region");
        CSMLRegion numberSix_color_region = reasoner.convexHull(numberSix_color_points, "numberSix_color_region");
        CSMLRegion numberSix_size_region = reasoner.convexHull(numberSix_size_points1, "numberSix_size_region");
       
		CSMLRegion powerOutlet_region = reasoner.convexHull(powerOutlet_points, "powerOutlet_region");
        CSMLRegion powerOutlet_color_region = reasoner.convexHull(powerOutlet_color_points, "powerOutlet_color_region");
        CSMLRegion powerOutlet_size_region = reasoner.convexHull(powerOutlet_size_points1, "powerOutlet_size_region");
        
		CSMLRegion roundTable_region = reasoner.convexHull(roundTable_points, "roundTable_region");
		CSMLRegion roundTable_2_region = reasoner.convexHull(roundTable_2_points, "roundTable_2_region");
		CSMLRegion roundTable_3_region = reasoner.convexHull(roundTable_3_points, "roundTable_3_region");
		
        CSMLRegion roundTable_color_region = reasoner.convexHull(roundTable_color_points, "roundTable_color_region");
        CSMLRegion roundTable_size_region = reasoner.convexHull(roundTable_size_points1, "roundTable_size_region");
	
        CSMLRegion smallPicture_region = reasoner.convexHull(smallPicture_points, "smallPicture_region");
        CSMLRegion smallPicture_color_region = reasoner.convexHull(smallPicture_color_points, "smallPicture_color_region");
        CSMLRegion smallPicture_size_region = reasoner.convexHull(smallPicture_size_points1, "smallPicture_size_region");
       
		CSMLRegion noteBook_region = reasoner.convexHull(noteBook_points, "noteBook_region");
		CSMLRegion noteBook_2_region = reasoner.convexHull(noteBook_2_points, "noteBook_2_region");
		CSMLRegion noteBook_3_region = reasoner.convexHull(noteBook_3_points, "noteBook_3_region");
		CSMLRegion noteBook_4_region = reasoner.convexHull(noteBook_4_points, "noteBook_3_region");
		
		
        CSMLRegion noteBook_color_region = reasoner.convexHull(noteBook_color_points, "noteBook_color_region");
		CSMLRegion noteBook2_color_region = reasoner.convexHull(noteBook2_color_points, "noteBook2_color_region");
		
        CSMLRegion noteBook_size_region = reasoner.convexHull(noteBook_size_points1, "noteBook_size_region");
        
		CSMLRegion schoolBag_region = reasoner.convexHull(schoolBag_points, "schoolBag_region");
		CSMLRegion schoolBag_2_region = reasoner.convexHull(schoolBag_2_points, "schoolBag_2_region");
		CSMLRegion schoolBag_3_region = reasoner.convexHull(schoolBag_3_points, "schoolBag_3_region");
		
		
        CSMLRegion schoolBag_color_region = reasoner.convexHull(schoolBag_color_points, "schoolBag_color_region");
        CSMLRegion schoolBag_size_region = reasoner.convexHull(schoolBag_size_points1, "schoolBag_size_region");
        
		CSMLRegion blackChair_region = reasoner.convexHull(blackChair_points, "blackChair_region");
		CSMLRegion blackChair_2_region = reasoner.convexHull(blackChair_2_points, "blackChair_2_region");
		CSMLRegion blackChair_3_region = reasoner.convexHull(blackChair_3_points, "blackChair_3_region");
		
		CSMLRegion noteChair_1_region = reasoner.convexHull(blackChair_3_points, "noteChair_1_region");
		
		CSMLRegion warDrobe_1_region = reasoner.convexHull(warDrobe_1_points, "warDrobe_1_region");
		CSMLRegion warDrobe_2_region = reasoner.convexHull(warDrobe_2_points, "warDrobe_2_region");
	    CSMLRegion warDrobe_3_region = reasoner.convexHull(warDrobe_3_points, "warDrobe_3_region");
		
		
        CSMLRegion blackChair_color_region = reasoner.convexHull(blackChair_color_points, "blackChair_color_region");
        CSMLRegion blackChair_size_region = reasoner.convexHull(blackChair_size_points1, "blackChair_size_region");
        
		CSMLRegion blueDoor_region = reasoner.convexHull(blueDoor_points, "blueDoor_region");
		CSMLRegion blueDoor_2_region = reasoner.convexHull(blueDoor_2_points, "blueDoor_2_region");
		
		
        CSMLRegion blueDoor_color_region = reasoner.convexHull(blueDoor_color_points, "blueDoor_color_region");
        CSMLRegion blueDoor_size_region = reasoner.convexHull(blueDoor_size_points1, "blueDoor_size_region");
		
		
		//Apace - Regioes/Conceitos
		
		CSMLRegion apaceWindow_region = reasoner.convexHull(apaceWindow_1_points, "apaceWindow_region");
        CSMLRegion apaceWindow_color_region = reasoner.convexHull(apaceWindow_color_points, "apaceWindow_color_region");			
    
        CSMLRegion apaceBlackChair_1_region = reasoner.convexHull(apaceBlackChair_1_points, "apaceBlackChair_region");
	    CSMLRegion apaceBlackChair_2_region = reasoner.convexHull(apaceBlackChair_2_points, "apaceBlackChair_2_region");
	    CSMLRegion apaceBlackChair_3_region = reasoner.convexHull(apaceBlackChair_3_points, "apaceBlackChair_3_region");
	    CSMLRegion apaceBlackChair_4_region = reasoner.convexHull(apaceBlackChair_4_points, "apaceBlackChair_4_region");
        CSMLRegion apaceBlackChair_color_region = reasoner.convexHull(apaceBlackChair_color_points, "apaceBlackChair_color_region");
         
        CSMLRegion apaceTable_region = reasoner.convexHull(apaceTable_1_points, "apaceTable_region");
        CSMLRegion apaceTable_color_region = reasoner.convexHull(apaceTable_color_points, "apaceTable_color_region");
         
        CSMLRegion apaceBlueBox_region = reasoner.convexHull(apaceBlueBox_1_points, "apaceBlueBox_region");
        CSMLRegion apaceBlueBox_color_region = reasoner.convexHull(apaceBlueBox_color_points, "apaceBlueBox_color_region");
        
        CSMLRegion apacePerciana_region = reasoner.convexHull(apacePerciana_1_points, "apacePerciana_region");
        CSMLRegion apacePerciana2_region = reasoner.convexHull(apacePerciana_2_points, "apacePerciana_region");
        CSMLRegion apacePerciana_color_region = reasoner.convexHull(apacePerciana_color_points, "apacePerciana_color_region");
          
        CSMLRegion apaceNoteBook_region = reasoner.convexHull(apaceNoteBook_1_points, "apaceNoteBook_region");
        CSMLRegion apaceNoteBook_color_region = reasoner.convexHull(apaceNoteBook_color_points, "apaceNoteBook_color_region");
        
         
		CSMLConcept apaceWindow_cp = m.createCSMLConcept("apaceWindow1");
	                apaceWindow_cp.addRegion(apaceWindow_region);
                    apaceWindow_cp.addRegion(apaceWindow_color_region);
                    apaceWindow_cp.addRegion(tv_size_region); 
                     
        String apaceWindow = "/home/joao/NetBeansProjects/mySift/images/apaceWindow_1.jpg";
        String apaceWindow_lbp = "/home/joao/NetBeansProjects/mySift/images/apaceWindow_1_lbp.jpg";
        
        tm.put(apaceWindow_cp.getId(),apaceWindow);
        tm_color.put(apaceWindow_cp.getId(),apaceWindow);
        tm_lbp.put(apaceWindow_cp.getId(),apaceWindow_lbp);
                     
         CSMLConcept apaceBlackChair_1_cp = m.createCSMLConcept("apaceBlackChair1");
	                 apaceBlackChair_1_cp.addRegion(apaceBlackChair_1_region);
                     apaceBlackChair_1_cp.addRegion(apaceBlackChair_color_region);
                     apaceBlackChair_1_cp.addRegion(tv_size_region);
                     
        String apaceBlackChair1 = "/home/joao/NetBeansProjects/mySift/images/apaceBlackChair_1.jpg";
        String apaceBlackChair1_lbp = "/home/joao/NetBeansProjects/mySift/images/apaceBlackChair_1_lbp.jpg";
        
        tm.put(apaceBlackChair_1_cp.getId(),apaceBlackChair1);
        tm_color.put(apaceBlackChair_1_cp.getId(),apaceBlackChair1);
        tm_lbp.put(apaceBlackChair_1_cp.getId(),apaceBlackChair1_lbp);
                     
         CSMLConcept apaceBlackChair_2_cp = m.createCSMLConcept("apaceBlackChair2");
	                 apaceBlackChair_2_cp.addRegion(apaceBlackChair_2_region);
                     apaceBlackChair_2_cp.addRegion(apaceBlackChair_color_region);
                     apaceBlackChair_2_cp.addRegion(tv_size_region);
                     
        String apaceBlackChair2 = "/home/joao/NetBeansProjects/mySift/images/apaceBlackChair_2.jpg";
        String apaceBlackChair2_lbp = "/home/joao/NetBeansProjects/mySift/images/apaceBlackChair_2_lbp.jpg";
        
        tm.put(apaceBlackChair_2_cp.getId(),apaceBlackChair2);
        tm_color.put(apaceBlackChair_2_cp.getId(),apaceBlackChair2);
        tm_lbp.put(apaceBlackChair_2_cp.getId(),apaceBlackChair2_lbp);
                     
        CSMLConcept apaceBlackChair_3_cp = m.createCSMLConcept("apaceBlackChair3");
	                apaceBlackChair_3_cp.addRegion(apaceBlackChair_3_region);
                    apaceBlackChair_3_cp.addRegion(apaceBlackChair_color_region);
                    apaceBlackChair_3_cp.addRegion(tv_size_region);
                    
        String apaceBlackChair3 = "/home/joao/NetBeansProjects/mySift/images/apaceBlackChair_3.jpg";
        String apaceBlackChair3_lbp = "/home/joao/NetBeansProjects/mySift/images/apaceBlackChair_3_lbp.jpg";
        
        tm.put(apaceBlackChair_3_cp.getId(),apaceBlackChair3);
        tm_color.put(apaceBlackChair_3_cp.getId(),apaceBlackChair3);
        tm_lbp.put(apaceBlackChair_3_cp.getId(),apaceBlackChair3_lbp);
                    
        CSMLConcept apaceBlackChair_4_cp = m.createCSMLConcept("apaceBlackChair4");
	                apaceBlackChair_4_cp.addRegion(apaceBlackChair_4_region);
                    apaceBlackChair_4_cp.addRegion(apaceBlackChair_color_region);
                    apaceBlackChair_4_cp.addRegion(tv_size_region);
                    
        String apaceBlackChair4 = "/home/joao/NetBeansProjects/mySift/images/apaceBlackChair_4.jpg";
        String apaceBlackChair4_lbp = "/home/joao/NetBeansProjects/mySift/images/apaceBlackChair_4_lbp.jpg";;
        
        tm.put(apaceBlackChair_4_cp.getId(),apaceBlackChair4);
        tm_color.put(apaceBlackChair_4_cp.getId(),apaceBlackChair4);
        tm_lbp.put(apaceBlackChair_4_cp.getId(),apaceBlackChair4_lbp);
                    
        CSMLConcept apaceTable_cp = m.createCSMLConcept("apaceTable1");
	                apaceTable_cp.addRegion(apaceTable_region);
                    apaceTable_cp.addRegion(apaceTable_color_region);
                    apaceTable_cp.addRegion(tv_size_region);
                    
        String apaceTable = "/home/joao/NetBeansProjects/mySift/images/apaceTable.jpg";
        String apaceTable_lbp = "/home/joao/NetBeansProjects/mySift/images/apaceTable_lbp.jpg";
        
        tm.put(apaceTable_cp.getId(),apaceTable);
        tm_color.put(apaceTable_cp.getId(),apaceTable);
        tm_lbp.put(apaceTable_cp.getId(),apaceTable_lbp);
                    
        CSMLConcept apaceBlueBox_cp = m.createCSMLConcept("apaceBlueBox1");
	                apaceBlueBox_cp.addRegion(apaceBlueBox_region);
                    apaceBlueBox_cp.addRegion(apaceBlueBox_color_region);
                    apaceBlueBox_cp.addRegion(tv_size_region);
                    
        String apaceBlueBox = "/home/joao/NetBeansProjects/mySift/images/apaceBlueBox_1.jpg";
        String apaceBlueBox_lbp = "/home/joao/NetBeansProjects/mySift/images/apaceBlueBox_1_lbp.jpg"; 
        
        tm.put(apaceBlueBox_cp.getId(),apaceBlueBox);
        tm_color.put(apaceBlueBox_cp.getId(),apaceBlueBox);
        tm_lbp.put(apaceBlueBox_cp.getId(),apaceBlueBox_lbp);
                    
        CSMLConcept apacePerciana_cp = m.createCSMLConcept("apacePerciana1");
	                apacePerciana_cp.addRegion(apacePerciana_region);
                    apacePerciana_cp.addRegion(apacePerciana_color_region);
                    apacePerciana_cp.addRegion(tv_size_region);
                    
        String apacePerciana = "/home/joao/NetBeansProjects/mySift/images/perciana_1.jpg";
        String apacePerciana_lbp = "/home/joao/NetBeansProjects/mySift/images/perciana_1_lbp.jpg"; 
        
        tm.put(apacePerciana_cp.getId(),apacePerciana);
        tm_color.put(apacePerciana_cp.getId(),apacePerciana);
        tm_lbp.put(apacePerciana_cp.getId(),apacePerciana_lbp);
                    
       CSMLConcept apacePerciana2_cp = m.createCSMLConcept("apacePerciana2");
	               apacePerciana2_cp.addRegion(apacePerciana_region);
                   apacePerciana2_cp.addRegion(apacePerciana_color_region);
                   apacePerciana2_cp.addRegion(tv_size_region);
                     
        String apacePerciana2 = "/home/joao/NetBeansProjects/mySift/images/perciana_2.jpg";
        String apacePerciana2_lbp = "/home/joao/NetBeansProjects/mySift/images/perciana_2_lbp.jpg";
        
        tm.put(apacePerciana2_cp.getId(),apacePerciana2);
        tm_color.put(apacePerciana2_cp.getId(),apacePerciana2);
        tm_lbp.put(apacePerciana2_cp.getId(),apacePerciana2_lbp);
        
        CSMLConcept apacePerciana3_cp = m.createCSMLConcept("apacePerciana3");
	                apacePerciana3_cp.addRegion(apacePerciana2_region);
                    apacePerciana3_cp.addRegion(apacePerciana_color_region);
                    apacePerciana3_cp.addRegion(tv_size_region);
                    
        String apacePerciana3 = "/home/joao/NetBeansProjects/mySift/images/perciana_3.jpg";
        String apacePerciana3_lbp = "/home/joao/NetBeansProjects/mySift/images/perciana_3_lbp.jpg";
        
        tm.put(apacePerciana3_cp.getId(),apacePerciana3);
        tm_color.put(apacePerciana3_cp.getId(),apacePerciana3);
        tm_lbp.put(apacePerciana3_cp.getId(),apacePerciana3_lbp);
                    
        CSMLConcept apaceNoteBook_cp = m.createCSMLConcept("apaceNoteBook1");
	                apaceNoteBook_cp.addRegion(apaceNoteBook_region);
                    apaceNoteBook_cp.addRegion(apaceNoteBook_color_region);
                    apaceNoteBook_cp.addRegion(tv_size_region);
                    
        String apaceNoteBook = "/home/joao/NetBeansProjects/mySift/images/apaceNoteBook_1.jpg";
        String apaceNoteBook_lbp = "/home/joao/NetBeansProjects/mySift/images/apaceNoteBook_1_lbp.jpg";
        
        tm.put(apaceNoteBook_cp.getId(),apaceNoteBook);
        tm_color.put(apaceNoteBook_cp.getId(),apaceNoteBook);
        tm_lbp.put(apaceNoteBook_cp.getId(),apaceNoteBook_lbp);
        
        CSMLConcept apaceNoteBook2_cp = m.createCSMLConcept("apaceNoteBook2");
	                apaceNoteBook2_cp.addRegion(noteBook_region);
                    apaceNoteBook2_cp.addRegion(apaceNoteBook_color_region);
                    apaceNoteBook2_cp.addRegion(tv_size_region);
                    
        String apaceNoteBook2 = "/home/joao/NetBeansProjects/mySift/images/apaceNoteBook_2.jpg";
        String apaceNoteBook2_lbp = "/home/joao/NetBeansProjects/mySift/images/apaceNoteBook_2_lbp.jpg";
        
        tm.put(apaceNoteBook2_cp.getId(),apaceNoteBook2);
        tm_color.put(apaceNoteBook2_cp.getId(),apaceNoteBook2);
        tm_lbp.put(apaceNoteBook2_cp.getId(),apaceNoteBook2_lbp);

		//Fim Apace
	

		//Conceitos
				  
		CSMLConcept qPri_cp = m.createCSMLConcept("QuadroPri");
	                qPri_cp.addRegion(qPri_region);
                    qPri_cp.addRegion(qPri_color_region);
                    qPri_cp.addRegion(qPri_size_region);
					
		String bookObject3 = "/home/joao/NetBeansProjects/mySift/images/quadro_9.jpg";
		String bookObject3_lbp = "/home/joao/NetBeansProjects/mySift/images/quadro_9_lbp.jpg";

		tm.put(qPri_cp.getId(),bookObject3);
		tm_color.put(qPri_cp.getId(),bookObject3);
		tm_lbp.put(qPri_cp.getId(),bookObject3_lbp);
              
		          
                
      //  CSMLConcept tv_cp = m.createCSMLConcept("MonitorLed");
	           //     tv_cp.addRegion(tv_region);
                //    tv_cp.addRegion(tv_color_region);
              //      tv_cp.addRegion(tv_size_region);   
			  
		String bookObject5 = "/home/joao/NetBeansProjects/mySift/images/tv.jpg";
		String bookObject5_lbp = "/home/joao/NetBeansProjects/mySift/images/tv_lbp.jpg";
            
        CSMLConcept arCond_cp = m.createCSMLConcept("ArCond");
	                arCond_cp.addRegion(arCond_region);
                    arCond_cp.addRegion(arCond_color_region);
                    arCond_cp.addRegion(arCond_size_region);
					
		 String bookObject4 = "/home/joao/NetBeansProjects/mySift/images/arCond.jpg";
		 String bookObject4_lbp = "/home/joao/NetBeansProjects/mySift/images/arCond_lbp.jpg";
		 
		 tm.put(arCond_cp.getId(),bookObject4);		
         tm_color.put(arCond_cp.getId(),bookObject4);
    	 tm_lbp.put(arCond_cp.getId(),bookObject4_lbp);
		
	  // CSMLConcept blueDoor_cp = m.createCSMLConcept("PortaGabinete1");
	              // blueDoor_cp.addRegion(blueDoor_region);
                 //  blueDoor_cp.addRegion(blueDoor_color_region);
                 //  blueDoor_cp.addRegion(blueDoor_size_region);
				   
		//CSMLConcept blueDoor_2_cp = m.createCSMLConcept("PortaGabinete2");
	            // blueDoor_2_cp.addRegion(blueDoor_2_region);
               //  blueDoor_2_cp.addRegion(blueDoor_color_region);
               //  blueDoor_2_cp.addRegion(blueDoor_size_region);		   
				   
				  
	   CSMLConcept blackChair_cp = m.createCSMLConcept("CadeiraPreta1");
	               blackChair_cp.addRegion(blackChair_region);
                   blackChair_cp.addRegion(blackChair_color_region);
                   blackChair_cp.addRegion(blackChair_size_region);
				   
	   String siftSample3 = "/home/joao/NetBeansProjects/mySift/images/blackChair3.jpg";
	   String lbpSample3 = "/home/joao/NetBeansProjects/mySift/images/blackChair_lbp.jpg";
	   String colorSample5 = "/home/joao/NetBeansProjects/mySift/images/blackChair.jpg";
		
	   tm.put(blackChair_cp.getId(),siftSample3);
	   tm_color.put(blackChair_cp.getId(),colorSample5);	  
	   tm_lbp.put(blackChair_cp.getId(),lbpSample3);		  
			  
				   
	   CSMLConcept blackChair_2_cp = m.createCSMLConcept("CadeiraPreta2");
	               blackChair_2_cp.addRegion(blackChair_2_region);
                   blackChair_2_cp.addRegion(blackChair_color_region);
                   blackChair_2_cp.addRegion(blackChair_size_region);	
				   
	   String siftSample3_2 = "/home/joao/NetBeansProjects/mySift/images/blackChair_2.jpg";
	   String lbpSample3_2 = "/home/joao/NetBeansProjects/mySift/images/blackChair_2_lbp.jpg";
	   String colorSample5_2 = "/home/joao/NetBeansProjects/mySift/images/blackChair_2.jpg";
				   
		tm.put(blackChair_2_cp.getId(),siftSample3_2);
		tm_color.put(blackChair_2_cp.getId(),colorSample5_2);
		tm_lbp.put(blackChair_2_cp.getId(),lbpSample3_2);
				 
	  CSMLConcept blackChair_3_cp = m.createCSMLConcept("CadeiraPreta3");
	              blackChair_3_cp.addRegion(blackChair_3_region);
                  blackChair_3_cp.addRegion(blackChair_color_region);
                  blackChair_3_cp.addRegion(blackChair_size_region);
				  
	  String siftSample3_3 = "/home/joao/NetBeansProjects/mySift/images/blackChair_3.jpg";
	  String lbpSample3_3 = "/home/joao/NetBeansProjects/mySift/images/blackChair_3_lbp.jpg";
	  String colorSample5_3 = "/home/joao/NetBeansProjects/mySift/images/blackChair_3.jpg";
				  
	   //String lbpSample15 = "/home/joao/NetBeansProjects/mySift/images/blackChair_4_lbp.jpg";
	   //String siftSample15 = "/home/joao/NetBeansProjects/mySift/images/blackChair_4.jpg";
	   //String colorSample15 = "/home/joao/NetBeansProjects/mySift/images/blackChair_4.jpg";
				  
  
	  tm.put(blackChair_3_cp.getId(),siftSample3_2);
      tm_color.put(blackChair_3_cp.getId(),colorSample5_3);
      tm_lbp.put(blackChair_3_cp.getId(),lbpSample3_3);
				 
	  //CSMLConcept noteChair_1_cp = m.createCSMLConcept("noteChair1");
	            //  noteChair_1_cp.addRegion(noteChair_1_region);
                //  noteChair_1_cp.addRegion(blackChair_color_region);
               //   noteChair_1_cp.addRegion(blackChair_size_region);
				
	  //CSMLConcept warDrobe_1_cp = m.createCSMLConcept("warDrobe1");
	         //     warDrobe_1_cp.addRegion(warDrobe_1_region);
             //     warDrobe_1_cp.addRegion(schoolBag_color_region);
             //     warDrobe_1_cp.addRegion(schoolBag_size_region);		
				
	//CSMLConcept warDrobe_2_cp = m.createCSMLConcept("warDrobe2");
	          //  warDrobe_2_cp.addRegion(warDrobe_2_region);
             //   warDrobe_2_cp.addRegion(schoolBag_color_region);
             //   warDrobe_2_cp.addRegion(schoolBag_size_region);		
				
	//CSMLConcept warDrobe_3_cp = m.createCSMLConcept("warDrobe3");
	        //    warDrobe_3_cp.addRegion(warDrobe_3_region);
            //    warDrobe_3_cp.addRegion(schoolBag_color_region);
             //   warDrobe_3_cp.addRegion(schoolBag_size_region);			
				   
				  
	  CSMLConcept schoolBag_cp = m.createCSMLConcept("Mochila1");
	              schoolBag_cp.addRegion(schoolBag_region);
                  schoolBag_cp.addRegion(schoolBag_color_region);
                  schoolBag_cp.addRegion(schoolBag_size_region); 
				  
	 String siftSample12 = "/home/joao/NetBeansProjects/mySift/images/schoollBag.jpg";
	 String colorSample12 = "/home/joao/NetBeansProjects/mySift/images/schoollBag2.jpg";
	 String lbpSample12 = "/home/joao/NetBeansProjects/mySift/images/schoollBag_lbp2.jpg";
	 
	 tm.put(schoolBag_cp.getId() ,siftSample12);
	 tm_color.put(schoolBag_cp.getId() ,colorSample12);
	 tm_lbp.put(schoolBag_cp.getId() ,lbpSample12);
		  
	CSMLConcept schoolBag_2_cp = m.createCSMLConcept("Mochila2");
	             schoolBag_2_cp.addRegion(schoolBag_2_region);
                 schoolBag_2_cp.addRegion(schoolBag_color_region);
                 schoolBag_2_cp.addRegion(schoolBag_size_region);
				 
	 String siftSample12_2 = "/home/joao/NetBeansProjects/mySift/images/schoollBag_2b.jpg";
	 String colorSample12_2 = "/home/joao/NetBeansProjects/mySift/images/schoollBag_2.jpg";
	 String lbpSample12_2 = "/home/joao/NetBeansProjects/mySift/images/schoollBag_2_lbp.jpg";
	 
	 tm.put(schoolBag_2_cp.getId() ,siftSample12_2);
	 tm_color.put(schoolBag_2_cp.getId() ,colorSample12_2);
	 tm_lbp.put(schoolBag_2_cp.getId() ,lbpSample12_2);
				 
	CSMLConcept schoolBag_3_cp = m.createCSMLConcept("Mochila3");
	            schoolBag_3_cp.addRegion(schoolBag_3_region);
                schoolBag_3_cp.addRegion(schoolBag_color_region);
                schoolBag_3_cp.addRegion(schoolBag_size_region);
				
	 String siftSample12_3 = "/home/joao/NetBeansProjects/mySift/images/schoollBag_3.jpg";
     String lbpSample12_3 = "/home/joao/NetBeansProjects/mySift/images/schoollBag_3_lbp.jpg";
     String colorSample12_3 = "/home/joao/NetBeansProjects/mySift/images/schoollBag_3.jpg";			
				
	tm.put(schoolBag_3_cp.getId() ,siftSample12_3);
    tm_color.put(schoolBag_3_cp.getId() ,colorSample12_3);
	tm_lbp.put(schoolBag_3_cp.getId() ,lbpSample12_3);
				 
				  
	  CSMLConcept noteBook_cp = m.createCSMLConcept("Notebook1");
	              noteBook_cp.addRegion(noteBook_region);
                  noteBook_cp.addRegion(noteBook_color_region);
                  noteBook_cp.addRegion(noteBook_size_region);
				  
	 String siftSample13 = "/home/joao/NetBeansProjects/mySift/images/noteBook_4.jpg";
	 String lbpSample13 = "/home/joao/NetBeansProjects/mySift/images/noteBook_4_lbp.jpg";
				  
	 tm.put(noteBook_cp.getId() ,siftSample13);
	 tm_color.put(noteBook_cp.getId() ,siftSample13);
	 tm_lbp.put(noteBook_cp.getId() ,lbpSample13);
				  
	 CSMLConcept noteBook_2_cp = m.createCSMLConcept("NoteBook2");
	             noteBook_2_cp.addRegion(noteBook_2_region);
                 noteBook_2_cp.addRegion(noteBook_color_region);
                 noteBook_2_cp.addRegion(noteBook_size_region);	
				 
	String siftSample13_2 = "/home/joao/NetBeansProjects/mySift/images/noteBook_2.jpg";
	String lbpSample13_2 = "/home/joao/NetBeansProjects/mySift/images/noteBook_2_lbp.jpg";
				 
	tm.put(noteBook_2_cp.getId() ,siftSample13_2);
	tm_color.put(noteBook_2_cp.getId() ,siftSample13_2);
	tm_lbp.put(noteBook_2_cp.getId() ,lbpSample13_2);
	 
	 CSMLConcept noteBook_3_cp = m.createCSMLConcept("NoteBook3");
	             noteBook_3_cp.addRegion(noteBook_3_region);
                 noteBook_3_cp.addRegion(noteBook_color_region);
                 noteBook_3_cp.addRegion(noteBook_size_region);
				 
	String siftSample13_3 = "/home/joao/NetBeansProjects/mySift/images/noteBook_3.jpg";
	String lbpSample13_3 = "/home/joao/NetBeansProjects/mySift/images/noteBook_3_lbp.jpg";
				 
	tm.put(noteBook_3_cp.getId() ,siftSample13_3);
	tm_color.put(noteBook_3_cp.getId() ,siftSample13_3);
	tm_lbp.put(noteBook_3_cp.getId() ,lbpSample13_3);
				 
	 CSMLConcept noteBook_4_cp = m.createCSMLConcept("NoteBook4");
	             noteBook_4_cp.addRegion(noteBook_4_region);
                 noteBook_4_cp.addRegion(noteBook2_color_region);
                 noteBook_4_cp.addRegion(noteBook_size_region);

	tm.put(noteBook_4_cp.getId() ,siftSample13_3);
    tm_color.put(noteBook_4_cp.getId() ,siftSample13_3);
	tm_lbp.put(noteBook_4_cp.getId() ,lbpSample13_3);
				  			  		  
				  
	// CSMLConcept smallPicture_cp = m.createCSMLConcept("QuadroPequeno");
	            //  smallPicture_cp.addRegion(smallPicture_region);
              //    smallPicture_cp.addRegion(smallPicture_color_region);
               //   smallPicture_cp.addRegion(smallPicture_size_region); 
				  
	//  CSMLConcept roundTable_cp = m.createCSMLConcept("Mesa1");
	           //  roundTable_cp.addRegion(roundTable_region);
              //   roundTable_cp.addRegion(roundTable_color_region);
               //  roundTable_cp.addRegion(roundTable_size_region);	
				 
	//  CSMLConcept roundTable_2_cp = m.createCSMLConcept("Mesa2");
	           //  roundTable_2_cp.addRegion(roundTable_2_region);
				// roundTable_2_cp.addRegion(roundTable_color_region);
              //   roundTable_2_cp.addRegion(roundTable_size_region);
				 
				 
	 //CSMLConcept roundTable_3_cp = m.createCSMLConcept("Mesa3");
	           //  roundTable_3_cp.addRegion(roundTable_3_region);
             //    roundTable_3_cp.addRegion(roundTable_color_region);
             //    roundTable_3_cp.addRegion(roundTable_size_region);		 
				   
				  
	 //CSMLConcept powerOutlet_cp = m.createCSMLConcept("Tomada de energia");
	   //          powerOutlet_cp.addRegion(powerOutlet_region);
       //          powerOutlet_cp.addRegion(powerOutlet_color_region);
       //          powerOutlet_cp.addRegion(powerOutlet_size_region);
				  
	//CSMLConcept numberSix_cp = m.createCSMLConcept("Sexto andar");
	   //         numberSix_cp.addRegion(numberSix_region);
        //        numberSix_cp.addRegion(numberSix_color_region);
        //        numberSix_cp.addRegion(numberSix_size_region);
				  
	//CSMLConcept numberSeven_cp = m.createCSMLConcept("Setimo andar");
	   //         numberSeven_cp.addRegion(numberSeven_region);
       //         numberSeven_cp.addRegion(numberSeven_color_region);
       //         numberSeven_cp.addRegion(numberSeven_size_region);
				  
				  		
	// Concept Samples
		

             // String siftSample1 = "/home/joao/NetBeansProjects/mySift/images/elevatorDoor.jpg";
              
			  //String siftSample2 = "/home/joao/NetBeansProjects/mySift/images/blueDoor3.jpg";
			  //String siftSample2_2 = "/home/joao/NetBeansProjects/mySift/images/blueDoor_2b.jpg";
              //String siftSample4 = "/home/joao/NetBeansProjects/mySift/images/firstGreenKey.jpg";
              //String siftSample5 = "/home/joao/NetBeansProjects/mySift/images/secGreenKey.jpg";
              //String siftSample6 = "/home/joao/NetBeansProjects/mySift/images/numberSix.jpg";
              //String siftSample7 = "/home/joao/NetBeansProjects/mySift/images/numberSeven.jpg";  
              //String siftSample8 = "/home/joao/NetBeansProjects/mySift/images/powerOutlet.jpg";
              //String siftSample9 = "/home/joao/NetBeansProjects/mySift/images/pextintor.jpg";
              //String siftSample10 = "/home/joao/NetBeansProjects/mySift/images/tapeteFr.jpg"; 
			 // String siftSample11 = "/home/joao/NetBeansProjects/mySift/images/roundTable.jpg";
			 // String siftSample11_2 = "/home/joao/NetBeansProjects/mySift/images/roundTable_2.jpg";
			 // String siftSample11_3 = "/home/joao/NetBeansProjects/mySift/images/roundTable_3.jpg";

             // String siftSample14 = "/home/joao/NetBeansProjects/mySift/images/smallPicture.jpg";
   
			 //  String siftSample16 = "/home/joao/NetBeansProjects/mySift/images/warDrobe_1.jpg";
			 //  String siftSample16_2 = "/home/joao/NetBeansProjects/mySift/images/warDrobe_2.jpg";
			 //  String siftSample16_3 = "/home/joao/NetBeansProjects/mySift/images/warDrobe_3.jpg";

             // String lbpSample1 = "/home/joao/NetBeansProjects/mySift/images/elevatorDoor_lbp.jpg";
            //  String lbpSample2 = "/home/joao/NetBeansProjects/mySift/images/blueDoor_lbp2.jpg";
			//  String lbpSample2_2 = "/home/joao/NetBeansProjects/mySift/images/blueDoor_2_lbp.jpg";

              //String lbpSample4 = "/home/joao/NetBeansProjects/mySift/images/firstGreenKey_lbp.jpg";
              //String lbpSample5 = "/home/joao/NetBeansProjects/mySift/images/secGreenKey_lbp.jpg";
             // String lbpSample6 = "/home/joao/NetBeansProjects/mySift/images/numberSix_lbp.jpg";
            //  String lbpSample7 = "/home/joao/NetBeansProjects/mySift/images/numberSeven_lbp.jpg";  
             // String lbpSample8 = "/home/joao/NetBeansProjects/mySift/images/powerOutlet_lbp.jpg";
            //  String lbpSample9 = "/home/joao/NetBeansProjects/mySift/images/pextintor_lbp.jpg";
            //  String lbpSample10 = "/home/joao/NetBeansProjects/mySift/images/tapeteFr_lbp.jpg";
              
			//  String lbpSample11 = "/home/joao/NetBeansProjects/mySift/images/roundTable_lbp.jpg";
			//  String lbpSample11_2 = "/home/joao/NetBeansProjects/mySift/images/roundTable_2_lbp.jpg";
			//  String lbpSample11_3 = "/home/joao/NetBeansProjects/mySift/images/roundTable_3_lbp.jpg";
 
            //  String lbpSample14 = "/home/joao/NetBeansProjects/mySift/images/smallPicture_lbp2.jpg";
  
			//  String lbpSample16 = "/home/joao/NetBeansProjects/mySift/images/warDrobe_1_lbp.jpg";
			//  String lbpSample16_2 = "/home/joao/NetBeansProjects/mySift/images/warDrobe_2_lbp.jpg";
			//  String lbpSample16_3 = "/home/joao/NetBeansProjects/mySift/images/warDrobe_3_lbp.jpg";

           //   tm.put(tv_cp.getId(),bookObject5);
			  
			  //tm.put(pextintor_cp.getId(),siftSample5);
			 // tm.put(tapeteFr_cp.getId() ,siftSample7);
			  
			  
			  
             
			  
             // tm.put(smallPicture_cp.getId() ,siftSample14);
              
             // tm.put(blueDoor_cp.getId(),siftSample2);
			 // tm.put(blueDoor_2_cp.getId(),siftSample2_2);
			  
			 // tm.put(roundTable_cp.getId(),siftSample11);
			//  tm.put(roundTable_2_cp.getId(),siftSample11_2);
			//  tm.put(roundTable_3_cp.getId(),siftSample11_3);
			  
			  //tm.put(firstGreenKey_cp.getId(),siftSample4);
             // tm.put(secGreenKey_cp.getId(),siftSample5);
			  
			  
			  
			  //tm.put(noteChair_1_cp.getId(),siftSample15);
			//  tm.put(warDrobe_1_cp.getId(),siftSample16);
			 // tm.put(warDrobe_2_cp.getId(),siftSample16_2);
			  //tm.put(warDrobe_3_cp.getId(),siftSample16_3);
			  
			  
		
			  
			  
			  
            //  tm.put(armarioPr_cp.getId(),siftSample1);
            //  tm.put(painelArx_cp.getId(),siftSample3);
            //  tm.put(painelAry_cp.getId(),siftSample3);
            //  tm.put(painelLab_cp.getId(),siftSample4);
              
            //  tm.put(mesaBaiax_cp.getId(),siftSample6);
            //  tm.put(mesaBaiay_cp.getId(),siftSample6);
             
			//  tm.put(painelLuz_cp.getId() ,siftSample8);
			//  tm.put(painelGpin_cp.getId() ,siftSample9);
			//  tm.put(armarioPr2_cp.getId(),siftSample10);
			
			 
			 
			 
			// String colorSample2_2 = "/home/joao/NetBeansProjects/mySift/images/blueDoor_2.jpg";
			 
			
			  
			// String colorSample14 = "/home/joao/NetBeansProjects/mySift/images/smallPicture2.jpg";
			 
			  
			 // String colorSample16 = "/home/joao/NetBeansProjects/mySift/images/warDrobe_1.jpg";
			 // String colorSample16_2 = "/home/joao/NetBeansProjects/mySift/images/warDrobe_2.jpg";
			 // String colorSample16_3 = "/home/joao/NetBeansProjects/mySift/images/warDrobe_3.jpg";
			
			 
           //  tm_color.put(tv_cp.getId(),bookObject5);
			
			
			  
             // tm_color.put(smallPicture_cp.getId() ,colorSample14);
              
             // tm_color.put(blueDoor_cp.getId(),siftSample2);
			  // tm_color.put(blueDoor_2_cp.getId(),colorSample2_2);
			  
			//  tm_color.put(roundTable_cp.getId(),siftSample11);
			 // tm_color.put(roundTable_2_cp.getId(),siftSample11_2);
			//  tm_color.put(roundTable_3_cp.getId(),siftSample11_3);
			  
			 // tm_color.put(firstGreenKey_cp.getId(),siftSample4);
              //tm_color.put(secGreenKey_cp.getId(),siftSample5);
			 
			  
			  //tm_color.put(noteChair_1_cp.getId(),colorSample15);
			//  tm_color.put(warDrobe_1_cp.getId(),siftSample16);
			//  tm_color.put(warDrobe_2_cp.getId(),siftSample16_2);
			//  tm_color.put(warDrobe_3_cp.getId(),siftSample16_3);
			
			 
			
			
            //  tm_lbp.put(tv_cp.getId(),bookObject5_lbp);
			  
			 // tm_lbp.put(pextintor_cp.getId(),lbpSample9);//
            //  tm_lbp.put(tapeteFr_cp.getId() ,lbpSample10); 
              
            //  tm_lbp.put(elevatorDoor_cp.getId(),lbpSample1);//
             // tm_lbp.put(blueDoor_cp.getId(),lbpSample2);
			//   tm_lbp.put(blueDoor_2_cp.getId(),lbpSample2_2);
			  
             
			  
			//   tm_lbp.put(noteChair_1_cp.getId(),lbpSample15);
			  
              //tm_lbp.put(firstGreenKey_cp.getId(),lbpSample4);
              //tm_lbp.put(secGreenKey_cp.getId(),lbpSample5);
            //  tm_lbp.put(numberSix_cp.getId(),lbpSample6);//
            //  tm_lbp.put(numberSeven_cp.getId(),lbpSample7); //
            //  tm_lbp.put(powerOutlet_cp.getId() ,lbpSample8); //
              //
          //    tm_lbp.put(roundTable_cp.getId() ,lbpSample11);
		//	  tm_lbp.put(roundTable_2_cp.getId() ,lbpSample11_2);
		//	  tm_lbp.put(roundTable_3_cp.getId() ,lbpSample11_3);
		   
		   
			  
           //   tm_lbp.put(smallPicture_cp.getId() ,lbpSample14);
		    //  tm_lbp.put(warDrobe_1_cp.getId(),lbpSample16);
			//  tm_lbp.put(warDrobe_2_cp.getId(),lbpSample16_2);
			//  tm_lbp.put(warDrobe_3_cp.getId(),lbpSample16_3);
			  
		  
	 // ArrayList<String> quarto = new ArrayList<String> ();
		//			    quarto.add(parm_dir_cp.getId());
						
	  ArrayList<String> sala = new ArrayList<String> ();
                        sala.add(qPri_cp.getId());
						
	//  ArrayList<String> sala_estar = new ArrayList<String> ();
	              //      sala_estar.add(tv_cp.getId());
	  
	  ArrayList<String> sala_jantar = new ArrayList<String> ();	  	  
						sala_jantar.add(arCond_cp.getId());
						
	//  ArrayList<String> env_corredor1 = new ArrayList<String> ();
	         //         env_corredor1.add(numberSeven_cp.getId());
	
	
     // ArrayList<String> env_corredor2 = new ArrayList<String> ();
	     //              env_corredor2.add(numberSix_cp.getId());
					   
	//  ArrayList<String> env_corredor_saida = new ArrayList<String> ();
	         //          env_corredor_saida.add(tapeteFr_cp.getId());

               
      //ArrayList<String> env_entrada = new ArrayList<String> ();
			   //         env_entrada.add(pextintor_cp.getId());
			   
     ArrayList<String> env_gabinete = new ArrayList<String> ();
			            env_gabinete.add(blackChair_cp.getId()); 
						env_gabinete.add(blackChair_2_cp.getId());
						env_gabinete.add(blackChair_3_cp.getId());
					//	env_gabinete.add(noteChair_1_cp.getId());
                      // env_gabinete.add(powerOutlet_cp.getId());
                      //  env_gabinete.add(roundTable_cp.getId());
					  //  env_gabinete.add(roundTable_2_cp.getId());
					//	env_gabinete.add(roundTable_3_cp.getId());
					 
					  env_gabinete.add(schoolBag_cp.getId());
					  env_gabinete.add(schoolBag_2_cp.getId()); 
					  env_gabinete.add(schoolBag_3_cp.getId()); 
					  
                      env_gabinete.add(noteBook_cp.getId());
					  env_gabinete.add(noteBook_2_cp.getId());
					  env_gabinete.add(noteBook_3_cp.getId());
					  
					  env_gabinete.add(apaceWindow_cp.getId());
					  env_gabinete.add(apaceBlackChair_1_cp.getId());
					  env_gabinete.add(apaceBlackChair_2_cp.getId());
					  env_gabinete.add(apaceBlackChair_3_cp.getId());
					  env_gabinete.add(apaceBlackChair_4_cp.getId());
					  env_gabinete.add(apaceTable_cp.getId());
					  env_gabinete.add(apaceBlueBox_cp.getId());
					  env_gabinete.add(apacePerciana_cp.getId());
					  env_gabinete.add(apacePerciana2_cp.getId());
					  env_gabinete.add(apacePerciana3_cp.getId());
					  env_gabinete.add(apaceNoteBook_cp.getId());
					  env_gabinete.add(apaceNoteBook2_cp.getId());
					  
					  
                     // env_gabinete.add(smallPicture_cp.getId());
					//   env_gabinete.add(blueDoor_cp.getId());
					//   env_gabinete.add(blueDoor_2_cp.getId());
					//   env_gabinete.add(warDrobe_1_cp.getId());
					//   env_gabinete.add(warDrobe_2_cp.getId());
					 //  env_gabinete.add(warDrobe_3_cp.getId());
					 
		   Map <String,String> gabinete_obj_320 = new HashMap <String,String>();
		
		                    gabinete_obj_320.put(qPri_cp.getId(),"180-142"); 
		                    gabinete_obj_320.put(blackChair_cp.getId(),"180-50"); 
						    gabinete_obj_320.put(blackChair_2_cp.getId(),"180-50");
						    gabinete_obj_320.put(blackChair_3_cp.getId(),"180-50");
						   // gabinete_obj_320.put(noteChair_1_cp.getId(),"180-50");
                            // env_gabinete.add(powerOutlet_cp.getId());
                          //  gabinete_obj_320.put(roundTable_cp.getId(),"180-50");
					      //  gabinete_obj_320.put(roundTable_2_cp.getId(),"224-180-50");
						  //  gabinete_obj_320.put(roundTable_3_cp.getId(),"224-180-50");
					 
					        gabinete_obj_320.put(schoolBag_cp.getId(),"224-180-50");
					        gabinete_obj_320.put(schoolBag_2_cp.getId(),"224-180-50"); 
					        gabinete_obj_320.put(schoolBag_3_cp.getId(),"224-180-50"); 
                            gabinete_obj_320.put(noteBook_cp.getId(),"224-180-50");
					        gabinete_obj_320.put(noteBook_2_cp.getId(),"224-180-50");
					        gabinete_obj_320.put(noteBook_3_cp.getId(),"224-180-50");
					  
                           // gabinete_obj_320.put(smallPicture_cp.getId(),"224-180-50");
					      //  gabinete_obj_320.put(blueDoor_cp.getId(),"224-180-50");
					      //  gabinete_obj_320.put(blueDoor_2_cp.getId(),"224-180-50");
					      //  gabinete_obj_320.put(warDrobe_1_cp.getId(),"224-180-50");
					     //   gabinete_obj_320.put(warDrobe_2_cp.getId(),"224-180-50");
					       // gabinete_obj_320.put(warDrobe_3_cp.getId(),"224-180-50");
							
		Map <String,String> gabinete_obj_720 = new HashMap <String,String>();
		                    gabinete_obj_720.put(qPri_cp.getId(),"180-232"); 
		                    gabinete_obj_720.put(blackChair_cp.getId(),"180-50"); 
						    gabinete_obj_720.put(blackChair_2_cp.getId(),"224-180-50");
						    gabinete_obj_720.put(blackChair_3_cp.getId(),"224-180-50");
						  //  gabinete_obj_720.put(noteChair_1_cp.getId(),"224-180-50");
                            // env_gabinete.add(powerOutlet_cp.getId());
                          //  gabinete_obj_720.put(roundTable_cp.getId(),"224-180-50");
					       // gabinete_obj_720.put(roundTable_2_cp.getId(),"224-180-50");
						  //  gabinete_obj_720.put(roundTable_3_cp.getId(),"224-180-50");
					 
					        gabinete_obj_720.put(schoolBag_cp.getId(),"224-180-50");
					        gabinete_obj_720.put(schoolBag_2_cp.getId(),"224-180-50"); 
					        gabinete_obj_720.put(schoolBag_3_cp.getId(),"224-180-50"); 
                            gabinete_obj_720.put(noteBook_cp.getId(),"224-180-50");
					        gabinete_obj_720.put(noteBook_2_cp.getId(),"224-180-50");
					        gabinete_obj_720.put(noteBook_3_cp.getId(),"224-180-50");
					  
                        //    gabinete_obj_720.put(smallPicture_cp.getId(),"224-180-50");
					    //    gabinete_obj_720.put(blueDoor_cp.getId(),"224-180-50");
					    //    gabinete_obj_720.put(blueDoor_2_cp.getId(),"224-180-50");
					    //    gabinete_obj_720.put(warDrobe_1_cp.getId(),"224-180-50");
					    //    gabinete_obj_720.put(warDrobe_2_cp.getId(),"224-180-50");
					     //   gabinete_obj_720.put(warDrobe_3_cp.getId(),"224-180-50");					
			   
			   
     // ArrayList<String> env_entrada_gabinete = new ArrayList<String> ();					
			    //        env_entrada_gabinete.add(blueDoor_cp.getId());
             
              
              
            
              
						
	 // ArrayList<String> env_corredor1 = new ArrayList<String> ();
	 //                   env_corredor1.add(painelArx_cp.getId()); 
     //                   env_corredor1.add(painelAry_cp.getId());
	//					env_corredor1.add(painelLuz_cp.getId());
	  
	  //ArrayList<String> env_corredor2 = new ArrayList<String> ();
	   //                 env_corredor2.add(painelLab_cp.getId());
	  
	  //ArrayList<String> env_corredor_saida = new ArrayList<String> ();
	   //                 env_corredor_saida.add(tapeteFr_cp.getId());
	  
	  //ArrayList<String> env_entrada = new ArrayList<String> ();
	   //                 env_entrada.add(pextintor_cp.getId());
	  
	  //ArrayList<String> env_baia1 = new ArrayList<String> ();
	   //                 env_baia1.add(mesaBaiax_cp.getId());
        //                env_baia1.add(mesaBaiay_cp.getId());
	  
	  //ArrayList<String> env_baia2 = new ArrayList<String> ();
	    //                env_baia2.add(armarioPr_cp.getId());
						
						
      Map<String, List<String>> mteste = new HashMap<String, List<String>>();
                            //    mteste.put("quarto", quarto);
                                mteste.put("sala", sala);
						      //  mteste.put("Sala de Estar", sala_estar);
						        mteste.put("Sala de Jantar", sala_jantar);
							//	mteste.put("Corredor", env_corredor1);
							//	mteste.put("Corredor2", env_corredor2);
							//	mteste.put("Corredor Saida", env_corredor2);
							//	mteste.put("Entrada", env_entrada);
						//		mteste.put("Local de trabalho", env_baia1);
							//	mteste.put("Local de trabalho2", env_baia2);
						//	mteste.put("CorredorEntrada", env_corredor1);
                         //   mteste.put("CorredorBaia", env_corredor2);
                       //     mteste.put("Entrada ", env_entrada);
                       //     mteste.put("CorredorSaida", env_corredor_saida);
                            mteste.put("Sala de reuniões", env_gabinete);
                         //   mteste.put("Entrada Gabinete", env_entrada_gabinete);
					   
		              
                      // Criando a instancia temporaria
		     	      
		     	      String[] parts = currentMessage2.split(",");
					  
					  
					 
					  
					    //Instancia temporaria: Posicao
					  
					    //11-12
					  
					    CSMLPoint ti_posicao = m.createCSMLPoint("ti_posicao", position_domain);
		     	        ti_posicao.setq(position_domain.getQualityDimensionIds());
		                   
		                double[] ti_position_point = { Double.parseDouble(parts[13]) , Double.parseDouble(parts[14])};
		     	           
		                ti_posicao.setv(ti_position_point);
		     	      
		     	      //Instancia temporaria: Cor
		     	      
		     	      CSMLPoint ti_color = m.createCSMLPoint("ti_color", color_domain);
		     	      ti_color.setq(color_domain.getQualityDimensionIds());
					  
					  //1 2 3
		                   
		              double[] ti_color_point = { Double.parseDouble(parts[3]) , Double.parseDouble(parts[4]),
		            		                      Double.parseDouble(parts[5])};
		     	           
		              ti_color.setv(ti_color_point);
					  
					  
					  //Instancia temporaria: Size
		     	      
		     	      CSMLPoint ti_size = m.createCSMLPoint("ti_size", size_domain);
		     	      ti_size.setq(size_domain.getQualityDimensionIds());
					  
					  //16-17 
		                   
		              double[] ti_size_point = { Double.parseDouble(parts[15]), Double.parseDouble(parts[16])};
		     	           
		              ti_size.setv(ti_size_point);
					  
		              
		            //Instancia temporaria: Shape
					
					//4-10
		     	       
		              CSMLPoint ti_shape = m.createCSMLPoint("ti_shape", d1);
		              ti_shape.setq(d1.getQualityDimensionIds());
		                   
		              double[] ti_shape_point = { Double.parseDouble(parts[6]), Double.parseDouble(parts[7]),
		            		               Double.parseDouble(parts[8]),Double.parseDouble(parts[9]),
		            		               Double.parseDouble(parts[10]),Double.parseDouble(parts[11]),
		            		               Double.parseDouble(parts[12])};
		                   
		              ti_shape.setv(ti_shape_point);
		              
		              CSMLInstance temp_ins = m.createCSMLInstance("temp_ins");
		              temp_ins.addPoint(ti_shape);
		              temp_ins.addPoint(ti_color);
					  temp_ins.addPoint(ti_size);
		              m.addInstance(temp_ins);
				
		           // Fim da instancia temporaria
				   
				   double img_width = mySift.imageSize(parts[0]);
				   System.out.println("tamanho =>"+img_width );
				   
				   double menor_pos = 1000;
				   
	               String elected_position=""; 
                   String [] pos = {"Lado Esquerdo","Frente Esquerda","Frente Direita","Lado Direito"};
                   String [] str = {"area1_region.CENTROID","area2_region.CENTROID","area3_region.CENTROID","area3_region.CENTROID"};
				   String [] str_fr2 = {"area1_fr2_region.CENTROID","area2_fr2_region.CENTROID","area3_fr2_region.CENTROID","area3_fr2_region.CENTROID"};
				   String [] str_fr3 = {"area1_fr3_region.CENTROID","area2_fr3_region.CENTROID","area3_fr3_region.CENTROID","area3_fr3_region.CENTROID"};
				   
				   if (img_width == 320){
					   System.out.println("320 =>"+img_width );
 
                   for (CSMLPoint all_area_point : all_area_points) {
		                double position_sim = reasoner.distance(ti_posicao, all_area_point, null); 
	                    if (position_sim  < menor_pos){
		                    menor_pos = position_sim ;  
		                    elected_position = all_area_point.getId();
                        }
                    
	                }
				   }else{
					   if (img_width == 720){
						   System.out.println("720 =>"+img_width );
				         for (CSMLPoint all_area_fr2_point : all_area_fr2_points) {
		                     double position_sim_fr2 = reasoner.distance(ti_posicao, all_area_fr2_point, null); 
	                          if (position_sim_fr2  < menor_pos){
		                          menor_pos = position_sim_fr2 ;  
                                  elected_position = all_area_fr2_point.getId();
                              }
                    
	                     }
					   }else{
						    if (img_width == 1280){
								System.out.println("1280 =>"+img_width );
					          for (CSMLPoint all_area_fr3_point : all_area_fr3_points) {
                                   double position_sim_fr3 = reasoner.distance(ti_posicao, all_area_fr3_point, null); 
	                               if (position_sim_fr3  < menor_pos){
                                       menor_pos = position_sim_fr3 ;  
                                       elected_position = all_area_fr3_point.getId();            
	                                }
							  }		
							}
					   }
				   }	   
					
                    String pos_final="";
					
					if (img_width == 320){
            
                      for (int i=0; i < str.length; i++){
                           if (elected_position.equals(str[i])){
                            pos_final = pos[i];
                          }
                       }
					}
					
					if (img_width == 720){
										 
					 for (int i=0; i < str_fr2.length; i++){
                        if (elected_position.equals(str_fr2[i])){
                            pos_final = pos[i];
                         }
                      }
					} 
					
					if (img_width == 1280){
					 
					  for (int i=0; i < str_fr3.length; i++){
                        if (elected_position.equals(str_fr3[i])){
                            pos_final = pos[i];
                        }
                     }
					} 
					
					//alternativa  2
					
					double x_point = Double.parseDouble(parts[13]);
					
					if (img_width == 320){
						
						if (x_point>=0 && x_point<=106)
						    pos_final = "esquerda";
						
						if (x_point>106 && x_point<=213)
						    pos_final = "frente";
						
						if (x_point>213 && x_point<=320)
						    pos_final = "direita";
					}	
					
					if (img_width == 720){
											
						if (x_point>=0 && x_point<=240)
						    pos_final = "esquerda";
						
						if (x_point>240 && x_point<=480)
						    pos_final = "frente";
						
						if (x_point>480 && x_point<=720)
						    pos_final = "direita";
					} 
										   
					   String eleito3 = "";
					   String eleito4 = "";

                     // if (!ok){
                      //  System.out.println("Escolhido == NF" );
						//eleito3 = "NF";
                     // }else{
  
		                CSMLPoint ins_color = temp_ins.getPoint2(color_domain);
		                CSMLPoint ins_shape = temp_ins.getPoint2(d1);
						CSMLPoint ins_size = temp_ins.getPoint2(size_domain);
		                double [] pins_color = ins_color.getv();

		              
		                CSMLConcept [] concepts =   m.getConcepts();
		              
		                CSMLConcept elected = m.createCSMLConcept("elected");
		              
		                //double menor = 1000;
						
						String cs_elected= " ";
						String hist_elected= " ";
						String desc_con = " ";
						
						boolean ok = mySift.VerifyFrameObject(parts[2]);
						
						concept_distances.clear();
						concept_distances3.clear();
						my_env.clear();
						concept_distances_hist.clear();
						candidates.clear();
						
						String ss = parts[2];
                        String [] vv = ss.split("/");
                        String [] vvv = vv[vv.length-1].split("\\.");
                        String rep = vvv[0]+"_lbp";
                        String replacedStr = ss.replaceAll(vvv[0], rep);
						
						
						if (!ok){
							eleito4 = "NV";
						//	desc_con = "NV";
						}else{
							double contour_width = Double.parseDouble(parts[15]);
							if (img_width == 320 && contour_width >=315){
								
								String qpri_320 = "/home/joao/NetBeansProjects/mySift/images/qpri.jpg";
                                String qpri_320_lbp = "/home/joao/NetBeansProjects/mySift/images/qpri_lbp.jpg";
               
                                my_env.add("qpri"); 
                                my_env_color.put("qpri",qpri_320);
                                my_env_lbp.put("qpri",qpri_320_lbp);
								
								String janela_320 = "/home/joao/NetBeansProjects/mySift/images/env1/apaceJanela.jpg";
                                String janela_320_lbp = "/home/joao/NetBeansProjects/mySift/images/env1/apaceJanela_lbp.jpg";
               
                                my_env.add("janela3"); 
                                my_env_color.put("janela3",janela_320);
                                my_env_lbp.put("janela3",janela_320_lbp);
               
                                String cadeiras_320 = "/home/joao/NetBeansProjects/mySift/images/env1/cadeiras.jpg";
                                String cadeiras_320_lbp = "/home/joao/NetBeansProjects/mySift/images/env1/cadeiras_lbp.jpg";
               
                                my_env.add("cadeiras31"); 
                                my_env_color.put("cadeiras31",cadeiras_320);
                                my_env_lbp.put("cadeiras31",cadeiras_320_lbp);
               
                                String cadeiras2_320 = "/home/joao/NetBeansProjects/mySift/images/env1/cadeiras2.jpg";
                                String cadeiras2_320_lbp = "/home/joao/NetBeansProjects/mySift/images/env1/cadeiras2_lbp.jpg";
               
                                my_env.add("cadeiras32"); 
                                my_env_color.put("cadeiras32",cadeiras2_320);
                                my_env_lbp.put("cadeiras32",cadeiras2_320_lbp);
               
                                String cadeiras3_320 = "/home/joao/NetBeansProjects/mySift/images/env1/cadeiras3.jpg";
                                String cadeiras3_320_lbp = "/home/joao/NetBeansProjects/mySift/images/env1/cadeiras3_lbp.jpg";
               
                                my_env.add("cadeiras33"); 
                                my_env_color.put("cadeiras33",cadeiras3_320);
                                my_env_lbp.put("cadeiras33",cadeiras3_320_lbp);
               
                                String cadeiras5_320 = "/home/joao/NetBeansProjects/mySift/images/env1/cadeiras5.jpg";
                                String cadeiras5_320_lbp = "/home/joao/NetBeansProjects/mySift/images/env1/cadeiras5_lbp.jpg";
               
                                my_env.add("cadeiras35"); 
                                my_env_color.put("cadeiras35",cadeiras5_320);
                                my_env_lbp.put("cadeiras35",cadeiras5_320_lbp);
               
                                String cadeiras6_320 = "/home/joao/NetBeansProjects/mySift/images/env1/cadeiras6.jpg";
                                String cadeiras6_320_lbp = "/home/joao/NetBeansProjects/mySift/images/env1/cadeiras6_lbp.jpg";
               
                                my_env.add("cadeiras36"); 
                                my_env_color.put("cadeiras36",cadeiras6_320);
                                my_env_lbp.put("cadeiras36",cadeiras6_320_lbp);
               
                                String caixaAzul_320 = "/home/joao/NetBeansProjects/mySift/images/env1/caixaAzul.jpg";
                                String caixaAzul_320_lbp = "/home/joao/NetBeansProjects/mySift/images/env1/caixaAzul_lbp.jpg";
               
                                my_env.add("caixa3"); 
                                my_env_color.put("caixa3",caixaAzul_320);
                                my_env_lbp.put("caixa3",caixaAzul_320_lbp);
               
                                String computador_320 = "/home/joao/NetBeansProjects/mySift/images/env1/computador.jpg";
                                String computador_320_lbp = "/home/joao/NetBeansProjects/mySift/images/env1/computador_lbp.jpg";
               
                                my_env.add("computador3"); 
                                my_env_color.put("computador3",computador_320);
                                my_env_lbp.put("computador3",computador_320_lbp);
               
                                String mesa_320 = "/home/joao/NetBeansProjects/mySift/images/env1/mesa.jpg";
                                String mesa_320_lbp = "/home/joao/NetBeansProjects/mySift/images/env1/mesa_lbp.jpg";
               
                                my_env.add("mesa31"); 
                                my_env_color.put("mesa31",mesa_320);
                                my_env_lbp.put("mesa31",mesa_320_lbp);
               
                                String mesa2_320 = "/home/joao/NetBeansProjects/mySift/images/env1/mesa2.jpg";
                                String mesa2_320_lbp = "/home/joao/NetBeansProjects/mySift/images/env1/mesa2_lbp.jpg";
               
                                my_env.add("mesa32"); 
                                my_env_color.put("mesa32",mesa2_320);
                                my_env_lbp.put("mesa32",mesa2_320_lbp);
               
                                String mesa3_320 = "/home/joao/NetBeansProjects/mySift/images/env1/mesa3.jpg";
                                String mesa3_320_lbp = "/home/joao/NetBeansProjects/mySift/images/env1/mesa3_lbp.jpg";
               
                                my_env.add("mesa33"); 
                                my_env_color.put("mesa33",mesa3_320);
                                my_env_lbp.put("mesa33",mesa3_320_lbp);
               
                                String perciana_320 = "/home/joao/NetBeansProjects/mySift/images/env1/perciana.jpg";
                                String perciana_320_lbp = "/home/joao/NetBeansProjects/mySift/images/env1/perciana_lbp.jpg";
               
                                my_env.add("perciana3"); 
                                my_env_color.put("perciana3",perciana_320);
                                my_env_lbp.put("perciana3",perciana_320_lbp);
               
                                String porta_320 = "/home/joao/NetBeansProjects/mySift/images/env1/porta.jpg";
                                String porta_320_lbp = "/home/joao/NetBeansProjects/mySift/images/env1/porta_lbp.jpg";
               
                                my_env.add("porta3"); 
                                my_env_color.put("porta3",porta_320);
                                my_env_lbp.put("porta3",porta_320_lbp);
               
                                String quadroBranco_320 = "/home/joao/NetBeansProjects/mySift/images/env1/quadroBranco.jpg";
                                String quadroBranco_320_lbp = "/home/joao/NetBeansProjects/mySift/images/env1/quadroBranco_lbp.jpg";
               
                                my_env.add("quadrobranco3"); 
                                my_env_color.put("quadrobranco3",quadroBranco_320);
                                my_env_lbp.put("quadrobranco3",quadroBranco_320_lbp);
               
                                 for (String temp : my_env) {
                       
                                       String fvalue = my_env_color.get(temp);
                                       String fvalue2 = my_env_lbp.get(temp);
                      
                                       double color_env1 = mySift.showHistogram2(fvalue,parts[2]);
                                      
                                       double lbp_texture_env1 = mySift.lpbHistogram2(fvalue2,replacedStr);
                     
                                       double soma_env1 = color_env1+lbp_texture_env1;
                      
		                               concept_distances3.put (temp, soma_env1); 
		                        }
               
                                List<Entry<String, Double>> list_env1 = new ArrayList<Entry<String, Double>>();
               
                                list_env1 = mySift.setOrder2(concept_distances3);
								
								if (list_env1.get(0).getValue() < 4.1 ){
					                // if (cs_elected.equals(sift_elected)){
					                  eleito4 = list_env1.get(0).getKey();;
					            }else{
									eleito4 = "NV";
								
								}
								pos_final = "frente";
								
								
							}else{
							   if (img_width == 720 && contour_width >=715){
								   
								   String cadeiras_720 = "/home/joao/NetBeansProjects/mySift/images/env2/cadeiras1.jpg";
                                   String cadeiras_720_lbp = "/home/joao/NetBeansProjects/mySift/images/env2/cadeiras1_lbp.jpg";
			   
			                       my_env.add("cadeiras71"); 
                                   my_env_color.put("cadeiras71",cadeiras_720);
                                   my_env_lbp.put("cadeiras71",cadeiras_720_lbp);
               
                                   String cadeiras2_720 = "/home/joao/NetBeansProjects/mySift/images/env2/cadeiras2.jpg";
                                   String cadeiras2_720_lbp = "/home/joao/NetBeansProjects/mySift/images/env2/cadeiras2_lbp.jpg";
               
                                   my_env.add("cadeiras72"); 
                                   my_env_color.put("cadeiras72",cadeiras2_720);
                                   my_env_lbp.put("cadeiras72",cadeiras2_720_lbp);
               
                                   String cadeiras4_720 = "/home/joao/NetBeansProjects/mySift/images/env2/cadeiras4.jpg";
                                   String cadeiras4_720_lbp = "/home/joao/NetBeansProjects/mySift/images/env2/cadeiras4_lbp.jpg";
               
                                   my_env.add("cadeiras74"); 
                                   my_env_color.put("cadeiras74",cadeiras4_720);
                                   my_env_lbp.put("cadeiras74",cadeiras4_720_lbp);
               
                                  String cadeiras5_720 = "/home/joao/NetBeansProjects/mySift/images/env2/cadeiras5.jpg";
                                  String cadeiras5_720_lbp = "/home/joao/NetBeansProjects/mySift/images/env2/cadeiras5_lbp.jpg";
               
                                  my_env.add("cadeiras75"); 
                                  my_env_color.put("cadeiras75",cadeiras5_720);
                                  my_env_lbp.put("cadeiras75",cadeiras5_720_lbp);
               
                                  String caixa_720 = "/home/joao/NetBeansProjects/mySift/images/env2/caixa.jpg";
                                  String caixa_720_lbp = "/home/joao/NetBeansProjects/mySift/images/env2/caixa_lbp.jpg";
               
                                  my_env.add("caixa7"); 
                                  my_env_color.put("caixa7",caixa_720);
                                  my_env_lbp.put("caixa7",caixa_720);
               
                                  String computador_720 = "/home/joao/NetBeansProjects/mySift/images/env2/computador.jpg";
                                  String computador_720_lbp = "/home/joao/NetBeansProjects/mySift/images/env2/computador_lbp.jpg";
               
                                  my_env.add("computador7"); 
                                  my_env_color.put("computador7",computador_720);
                                  my_env_lbp.put("computador7",computador_720_lbp);
               
                                  String janela_720 = "/home/joao/NetBeansProjects/mySift/images/env2/janela.jpg";
                                  String janela_720_lbp = "/home/joao/NetBeansProjects/mySift/images/env2/janela_lbp.jpg";
               
                                  my_env.add("janela7"); 
                                  my_env_color.put("janela7",janela_720);
                                  my_env_lbp.put("janela7",janela_720_lbp);
               
                                  String mesa_720 = "/home/joao/NetBeansProjects/mySift/images/env2/mesa.jpg";
                                  String mesa_720_lbp = "/home/joao/NetBeansProjects/mySift/images/env2/mesa_lbp.jpg";
               
                                  my_env.add("mesa71"); 
                                  my_env_color.put("mesa71",mesa_720);
                                  my_env_lbp.put("mesa71",mesa_720_lbp);
               
                                  String mesa2_720 = "/home/joao/NetBeansProjects/mySift/images/env2/mesa2.jpg";
                                  String mesa2_720_lbp = "/home/joao/NetBeansProjects/mySift/images/env2/mesa2_lbp.jpg";
               
                                  my_env.add("mesa72"); 
                                  my_env_color.put("mesa72",mesa2_720);
                                  my_env_lbp.put("mesa72",mesa2_720_lbp);
               
                                  String quadroBranco_720 = "/home/joao/NetBeansProjects/mySift/images/env2/quadroBranco.jpg";
                                  String quadroBranco_720_lbp = "/home/joao/NetBeansProjects/mySift/images/env2/quadroBranco_lbp.jpg";
               
                                  my_env.add("quadrobranco7"); 
                                  my_env_color.put("quadrobranco7",quadroBranco_720);
                                  my_env_lbp.put("quadrobranco7",quadroBranco_720_lbp);
			      
							      for (String temp : my_env) {
                       
                                       String fvalue = my_env_color.get(temp);
                                       String fvalue2 = my_env_lbp.get(temp);
                      
                                       double color_env2 = mySift.showHistogram2(fvalue,parts[2]);
                                      
                                       double lbp_texture_env2 = mySift.lpbHistogram2(fvalue2,replacedStr);
                     
                                       double soma_env2 = color_env2+lbp_texture_env2;
                      
		                               concept_distances3.put (temp, soma_env2); 
		                          }
               
                                  List<Entry<String, Double>> list_env2 = new ArrayList<Entry<String, Double>>();
               
                                  list_env2 = mySift.setOrder2(concept_distances3);
								
								  if (list_env2.get(0).getValue() < 4.1 ){
					                // if (cs_elected.equals(sift_elected)){
					                  eleito4 = list_env2.get(0).getKey();;
					              }else{
									 eleito4 = "NV";
								
								  }
								  pos_final = "em frente";
								
							   }else{	   
							     
							
							
		                          for (CSMLConcept concept1 : concepts) {
									
									  String img_value = tm_color.get(concept1.getId());
                                      double color_sim2 = mySift.showHistogram2(img_value,parts[2]);

                                      String img_lbp_value = tm_lbp.get(concept1.getId());
                                      double lbp_texture = mySift.lpbHistogram2(img_lbp_value, replacedStr);

		                              CSMLPoint color_mid = concept1.getRegion("color_domain").getCentroidAsPoint();
		                              double [] pcolor_mid = color_mid.getv(); 
		                   
		                              CSMLPoint shape_mid = concept1.getRegion("shape_domain").getCentroidAsPoint();
		                              double [] pshape_mid = shape_mid.getv();
						   
						              CSMLPoint size_mid = concept1.getRegion("size_domain").getCentroidAsPoint();
		                              double [] psize_mid = size_mid.getv();
		                     
		                              double color_sim = reasoner.distance(ins_color, color_mid, null);
		                              double shape_sim = reasoner.distance(ins_shape, shape_mid, null);
						              double size_sim = reasoner.distance(ins_size, size_mid, null);
								      int color_s = (int)color_sim;
									  double color_norm = color_sim/100;
								      int size_s = (int)size_sim;
									  int lpb_t = (int) lbp_texture;
									  
									  //System.out.println(concept1.getId()+" "+color_sim+" "+color_norm+ " " +shape_sim);
								 
									  //double soma = color_norm + shape_sim;
								      double soma =  color_s + shape_sim ;
									  double soma2 =  color_sim2 + lbp_texture;

						   
		                              concept_distances.put(concept1.getId(),soma);
									  concept_distances_hist.put(concept1.getId(),soma2);

		                          }
							  

					              List<Entry<String, Double>> list = new ArrayList<Entry<String, Double>>();
               
                                  list = mySift.setOrder2(concept_distances);
								 
								  cs_elected = list.get(0).getKey();
								 
								  List<Entry<String, Double>> list_hist = new ArrayList<Entry<String, Double>>();
               
                                  list_hist = mySift.setOrder2(concept_distances_hist);
					   
					              hist_elected = list_hist.get(0).getKey();
					   
					   
					              for(Map.Entry<String, Double> entry:list){
                                        candidates.put(entry.getKey(),"teste3");
                                  }
					   

					              List<Map.Entry<String, Integer>> listk2 = new ArrayList<Map.Entry<String, Integer>>();
					   
					              // listk2 = mySift.findSiftCandidates (parts[2], tm ,  candidates);
					   
					              //String eleito2 = listk2.get(0).getKey();
					   
					              //String sift_elected = listk2.get(0).getKey();
								 
								  //String sift_elected = "NF";
					   
					              //System.out.println(parts[1]+" CS_ELECTED! "+cs_elected+"SIFT_ELECTED! "+  sift_elected );
					  
					              //System.out.println(" CS_ELECTED! "+list.get(0).getValue()+"SIFT_ELECTED! "+  listk2.get(0).getValue());
								 
					              System.out.println(parts[1]+" CS_ELECTED! "+list.get(0).getKey()+" " +list.get(0).getValue());
					              System.out.println(" CS_ELECTED2! "+list.get(1).getKey()+" " +list.get(1).getValue());
								 
								  System.out.println(parts[1]+" HIST_ELECTED! "+list_hist.get(0).getKey()+" " +list_hist.get(0).getValue());
					              System.out.println(" HIST_ELECTED2! "+list_hist.get(1).getKey()+" " +list_hist.get(1).getValue());
								  
								  try(FileWriter fw = new FileWriter("/home/joao/NetBeansProjects/mySift/images/log.txt", true);
                                          BufferedWriter bw = new BufferedWriter(fw);
                                          PrintWriter out = new PrintWriter(bw))
                                   {
                                       out.println(parts[1]+" CS_ELECTED! "+list.get(0).getKey()+" " +list.get(0).getValue());
                                        //more code
                                        out.println(parts[1]+" HIST_ELECTED! "+list_hist.get(0).getKey()+" " +list_hist.get(0).getValue());
                                       //more code
                                   } catch (IOException e) {
                                      //exception handling left as an exercise for the reader
                                   }

					             
								  
								   if (cs_elected.equals(hist_elected)){
									     eleito4 = cs_elected;
								   }else{
									   if (list_hist.get(0).getValue() < 1.1 ){
										   eleito4 = hist_elected;
									   }else{
										   if (list.get(0).getValue() < 6.1 ){ 
										      eleito4 = cs_elected;
										   }else{
											  eleito4 = "NF";
										   }	  
									   }	
								   }	   
									     
					              
								  System.out.println("ELECTED! "+eleito4 );
								  
								  
								//    desc_con = tm_concepts.get(eleito4);
					  
					   
					           //  if (listk2.get(0).getKey().equals("NF")){
						        //     eleito3 = "NF";
                               //  }else{
                
                                //   for (CSMLConcept concept2 : concepts) {
                    
                               //        if (concept2.getId().equals(eleito2)){
                               //           elected = concept2;  
                               //        }
                               //    }
							  //     eleito3 = elected.getId();
					          //  }
						   }
						}  
				      } 
					  
					  if (img_width == 320){
						  
						  if (eleito4.equals("NF")){
						      System.out.println("Distancia:");
						  }else{
							   if (eleito4.equals("NV")){
								   System.out.println("Distancia:");
						      }else{	   
						  
					           for ( Map.Entry<String, String> entry_h : gabinete_obj_320.entrySet()) {
                                     if (entry_h.getKey().equals(eleito4)){  
                                         String value = entry_h.getValue();
                                         String dvalues = value;
                                         String [] dvaluesv = dvalues.split("-");
                                         Double p=  Double.parseDouble(parts[15]);
                                         Double d=  Double.parseDouble(dvaluesv[0]);
                                         Double w=  Double.parseDouble(dvaluesv[1]);
										 System.out.println("Distancia:"+p+" "+" "+d+" "+w);
                                         Double f = (p*d)/w;
                                         Double d2 = (w*f)/p;
                                         System.out.println("Distancia 2:"+d2);
							         }
					          }	
							}  
						  }
					 }
					 
					  if (img_width == 720){
						  
						  if (eleito4.equals("NF")){
						  System.out.println("Distancia:");
						  }else{
							   if (eleito4.equals("NV")){
								   System.out.println("Distancia:");
						      }else{	   
						  
					           for ( Map.Entry<String, String> entry_h : gabinete_obj_720.entrySet()) {
                                     if (entry_h.getKey().equals(eleito4)){  
                                         String value = entry_h.getValue();
                                         String dvalues = value;
                                         String [] dvaluesv = dvalues.split("-");
                                         Double p=  Double.parseDouble(parts[15]);
                                         Double d=  Double.parseDouble(dvaluesv[0]);
                                         Double w=  Double.parseDouble(dvaluesv[1]);
										 System.out.println("Distancia:"+p+" "+" "+d+" "+w);
                                         Double f = (p*d)/w;
                                         Double d2 = (w*f)/p;
                                         System.out.println("Distancia 2:"+d2);
							         }
					          }	
							}  
						  }
					 }
					 
					 
					  
					  String env_eleito=" ";
					   
					  frame_objects.add(eleito4);
					  
					  if (parts[18].equals("tail") ){
						   
						   //frame_objects.add(eleito3);
						   
						   Map <String,Integer> enviroment_candidates = new HashMap <String,Integer>();
						   
						   for(String frame_objects1:frame_objects){
							   if (frame_objects1.equals("NF") || frame_objects1.equals("NV") ){
								   System.out.println("Escolhido = "+frame_objects1);
							   }else{ 	   
						           for(Map.Entry<String, List<String>> entry_env:mteste.entrySet()){
                                      List  <String> test = entry_env.getValue();
                                      if (test.contains(frame_objects1)){ 
                                          String value1 = entry_env.getKey(); 
                                          if (enviroment_candidates.containsKey(value1)){
                                              enviroment_candidates.put(entry_env.getKey(), enviroment_candidates.get(entry_env.getKey()) + 1);
                        
                                         }else{
                                             enviroment_candidates.put(entry_env.getKey(), 1);
                                      }                       
                    
                                     }
                                  }
						       }
						   }
						   
						   if (enviroment_candidates.isEmpty()){
							   env_eleito ="ND";
                               System.out.println("Env eleito not found");
               
                           }else{
							    if (enviroment_candidates.size() > 1){
						            List<Entry<String, Integer>> liste = new ArrayList<Entry<String, Integer>>();
							        liste = mySift.setOrder(enviroment_candidates);
									env_eleito = liste.get(0).getKey();
								}else{
								   Map.Entry<String,Integer> entry_only1=enviroment_candidates.entrySet().iterator().next();
								   String only_key = entry_only1.getKey();
                                   env_eleito = only_key;
								}   
                               System.out.println("Env eleito ==== "+env_eleito);
						   } 

						   frame_objects.clear();
                   
					   }else{
					      
					      env_eleito ="ND";
					   }
               
                
					       
					   //}  
					//  }	
					   
		            currentMessage2 = parts[0]+","+parts[1]+","+eleito4+","+pos_final+","+parts[13]+","+parts[14]+","+parts[15]+","+parts[16]+","+parts[18]+","+env_eleito+","+img_width;
		               
				      
				
				return currentMessage2;
			}
        });
        //logger1.info("ArtTutorial >> init(): end of method");
    }
    
    /** Initialize communication on ROS topic */
    void init() {        
        init(null);        
    }
    
    /** Get current sensor values */
    @OPERATION void getCurrentMessage() {
    	//logger1.info("ArtTutorial >> getCurrentMessage()");
    	ObsProperty prop = getObsProperty(propertyNameChatter);
    	if(currentMessage!= null)
    		prop.updateValues(currentMessage);
    	else
    		prop.updateValues("Error: current message is null");
    	//logger1.info("ArtTutorial >>  getCurrentMessage() >> Observable Property Value: " + prop);
        signal(propertyNameChatter);
        //logger1.info("ArtTutorial >>  FIM DO getCurrentMessage()");
    }
	
    /**
     * Adds two integers using AddTwoInts ROS service
     * @param valueA First integer value
     * @param valueB Second integer value
     */
	@OPERATION void sum(int valueA, int valueB){
		//if (serviceClient==null)
		//	logger1.info("ArtTutorial >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>  sum()");				
		//logger1.info("ArtTutorial >>  sum()");
		final AddTwoIntsRequest request = serviceClient.newMessage();
	    request.setA(valueA);
	    request.setB(valueB);
	    serviceClient.call(request, new ServiceResponseListener<AddTwoIntsResponse>() {
	      @Override
	      public void onSuccess(AddTwoIntsResponse response) {
	    	  currentSum = response.getSum();
	    	  //logger1.info(String.format("%d + %d = %d", request.getA(), request.getB(), currentSum));
	    	  execInternalOp("receivingSum");	    	  	    	  
	      }

	      @Override
	      public void onFailure(RemoteException e) {
	    	  currentSum = 0;
	        //throw new RosRuntimeException(e);
	      }
	    });
	    //logger1.info("ArtTutorial >>  FIM DO sum()");
	}
	
	//MyPublisher operation
	
	@OPERATION void pub(String message){
		std_msgs.String str = publisherChatter.newMessage();
        str.setData(message);
        publisherChatter.publish(str);
        //Thread.sleep(1000);		
	}	
	
    @INTERNAL_OPERATION
    void receivingMsg() {
        //logger1.info("ArtOdometry >> receivingMsg()");
        await(cmdMsg);
        signal(propertyNameChatter);
        //sleepNoLog(500);
    }
    
    /** The ReadCmdMsg implements a blocking command – implementing the IBlockingCmd interface – containing the command code in the exec method. */
    class ReadCmdMsg implements IBlockingCmd {
        /** The command code */
        public void exec() {
            try {
                ObsProperty prop = getObsProperty(propertyNameChatter);
                prop.updateValues(currentMessage);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
	
    @INTERNAL_OPERATION
    void receivingSum() {
        //logger1.info("ArtOdometry >> receivingSum()");
        await(cmdSum);
        signal(propertyNameSum);
        //sleepNoLog(500);
    }

    /** The ReadCmdSum implements a blocking command – implementing the IBlockingCmd interface – containing the command code in the exec method. */
    class ReadCmdSum implements IBlockingCmd {
        /** The command code */
        public void exec() {
            try {
                ObsProperty prop = getObsProperty(propertyNameSum);
                prop.updateValues(currentSum);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
