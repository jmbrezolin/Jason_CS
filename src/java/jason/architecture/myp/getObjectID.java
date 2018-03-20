// Internal action code for project customBB

package myp;

import edu.geog.geocog.csml.model.CSMLRegion;
import edu.geog.geocog.csml.model.CSMLQualityDimension;
import edu.geog.geocog.csml.model.CSMLDomain;
import edu.geog.geocog.csml.model.CSMLModel;
import edu.geog.geocog.csml.reasoner.CSMLConceptualSpaceReasoner;
import edu.geog.geocog.csml.model.CSMLPoint;
import edu.geog.geocog.csml.model.CSMLInstance;

import jason.*;
import jason.asSemantics.*;
import jason.asSyntax.*;
import jason.bb.*;

import jason.stdlib.*;

import java.util.*;
import java.util.logging.Logger;

public class getObjectID extends DefaultInternalAction {

    @Override
    public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception {
        // execute the internal action
		Logger logger = Logger.getLogger(DefaultInternalAction.class.getSimpleName());
    	try {
    		int i=0, flag=0;
			StringTerm result =  new StringTermImpl(); //recives the final result
    		// 1. gets the arguments as typed terms
    		StringTerm p1x = (StringTerm)args[0];     // recives the message
			String s = p1x.getString();
			String[] parts = s.split(",");           //splits the message
			String obj_id=""; 	
			int tam = parts[0].length();            //gets part[0] length to perform subscript
			
			double value = Double.parseDouble(parts[2]);
			NumberTerm p2x = new NumberTermImpl(value);
			double value2 = Double.parseDouble(parts[3]);
			NumberTerm p2y  = new NumberTermImpl(value2);

            //Verify if the belief already exists 			
			
			for (Literal b: ts.getAg().getBB()) {
				 
				if (b.getFunctor().equals("object_center")){

				   StringTerm sub = (StringTerm) b.getTerm(0);
				   String sub1 = sub.getString();
				   String sub2 = sub1.substring(0,tam);
				   NumberTerm p3x = (NumberTerm)b.getTerm(1);
    		       NumberTerm p3y = (NumberTerm)b.getTerm(2); 
                   //TODO verify if this rule can be used to identify the same object in a diferent position
					double r = Math.abs(p2x.solve()-p3x.solve()) +
    		                   Math.abs(p2y.solve()-p3y.solve());
					if ((r==0.0) && (sub2.equals(parts[0])))             
				    {
						logger.warning("Entrei"); 
					  result = (StringTerm)(b.getTerm(0));
					  flag=1;
			        } 
				} 
             }
			 
			 //if the belief already exists (flag=1) just return the old ID
			 
			 if (flag==1){
			      obj_id = result.getString();		 
			 }else{                               // if not, computes the new ID

			     for (Literal bel: ts.getAg().getBB()) {
				     if (bel.getFunctor().equals("object")){
						 StringTerm t0 = (StringTerm) bel.getTerm(0);
						 String st0 = t0.getString();
					     if (st0.equals(parts[0])) {  	 
				             i++;
				         }
				     }
			     }	
			     obj_id = parts[0]+Integer.toString(i);
			}
			
			StringTerm result2 =  new StringTermImpl(obj_id); //return ID
    		return un.unifies(result2,args[1]);
    		} catch (ArrayIndexOutOfBoundsException e) {
    		throw new JasonException("The internal action "+
    		"has not received two arguments!");
    		} catch (ClassCastException e) {
    		throw new JasonException("The internal action "+
    		"has received arguments that are not strings!");
    		} catch (Exception e) {
    		throw new JasonException("Error in ID calculation");
    		}
    	 	
    }
}
