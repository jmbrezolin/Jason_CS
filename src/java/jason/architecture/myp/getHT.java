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

public class getHT extends DefaultInternalAction {

    @Override
    public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception {
        // execute the internal action
    	try {	
    		// 1. gets the arguments as typed terms
    	StringTerm p1x = (StringTerm)args[0];
		String s = p1x.getString();
		String[] parts = s.split(",");
		String obj_concept = parts[8];
		String obj_concept2 = obj_concept.trim();
			
		StringTerm result2 =  new StringTermImpl(obj_concept2);
    		return un.unifies(result2,args[1]);
    		} catch (ArrayIndexOutOfBoundsException e) {
    		throw new JasonException("The internal action distance"+
    		"has not received five arguments!");
    		} catch (ClassCastException e) {
    		throw new JasonException("The internal action distance"+
    		"has received arguments that are not numbers!");
    		} catch (Exception e) {
    		throw new JasonException("Error in distance");
    		}
    	 	
    }
}
