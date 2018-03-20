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

public class getCSMLObject extends DefaultInternalAction {

    @Override
    public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception {
        // execute the internal action
    	try {
    		
    		/*CSMLModel m = new CSMLModel();
    		CSMLDomain d1 = m.createCSMLDomain("d1");
    		CSMLQualityDimension qd1 = m.createCSMLQualityDimension("ColorHue");                          
            qd1.setLabel("Hue");
            qd1.setCircular(true);
            qd1.setScale("interval");
            qd1.setRange(0.0, 360.0);
            qd1.setDescription("My Color Hue");
            qd1.setUnits("degrees");
            d1.addQualityDimension(qd1);
    		CSMLQualityDimension qd2 = m.createCSMLQualityDimension("ColorSaturation");
            qd2.setLabel("Saturation");
            qd2.setScale("ratio");
            qd2.setRange(0.0, 100.0);
            qd2.setDescription("My Color Saturation");
            qd2.setUnits("percent");
            d1.addQualityDimension(qd2);
            CSMLQualityDimension qd3 =  m.createCSMLQualityDimension("ColorValue");
            qd3.setLabel("Value");
            qd3.setScale("ratio");
            qd3.setRange(0.0, 100.0);
            qd3.setDescription("My Color Value");
            qd3.setUnits("percent");
            d1.addQualityDimension(qd3);
    		m.addDomain(d1);
    		
    		CSMLPoint p1 = m.createCSMLPoint("p1", d1);
    		p1.setq(d1.getQualityDimensionIds());
    		double[] pv1 = {57, 18,100};
    		p1.setv(pv1); */
    		
    		// 1. gets the arguments as typed terms
    		NumberTerm p1x = (NumberTerm)args[0];
    		NumberTerm p1y = (NumberTerm)args[1];
    		NumberTerm p2x = (NumberTerm)args[2];
    		NumberTerm p2y = (NumberTerm)args[3];
    		// 2. calculates the distance
    		double r = Math.abs(p1x.solve()-p2x.solve()) +
    		Math.abs(p1y.solve()-p2y.solve());
    		// 3. creates the term with the result and
    		// unifies the result with the 5th argument
    		NumberTerm result = new NumberTermImpl(r);
    		return un.unifies(result,args[4]);
    		} catch (ArrayIndexOutOfBoundsException e) {
    		throw new JasonException("The internal action ’distance’"+
    		"has not received five arguments!");
    		} catch (ClassCastException e) {
    		throw new JasonException("The internal action ’distance’"+
    		"has received arguments that are not numbers!");
    		} catch (Exception e) {
    		throw new JasonException("Error in ’distance’");
    		}
    	 	
    }
}
