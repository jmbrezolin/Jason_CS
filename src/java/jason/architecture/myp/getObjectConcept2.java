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

public class getObjectConcept2 extends DefaultInternalAction {

    @Override
    public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception {
        // execute the internal action
    	try {	
    		// 1. gets the arguments as typed terms
    	StringTerm p1x = (StringTerm)args[0];
		String s = p1x.getString();
		String[] parts = s.split(",");
		String fname22 = parts[0].substring(2, parts[0].length());
		
			
		StringTerm result2 =  new StringTermImpl(fname22);
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
