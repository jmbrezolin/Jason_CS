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
import java.util.TreeMap;

public class getObjectReference extends DefaultInternalAction {

    @Override
    public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception {
        // execute the internal action
    	try {	
    		// 1. gets the arguments as typed terms
    	StringTerm p1x = (StringTerm)args[0];
		String s = p1x.getString();
		
		TreeMap <String,String> tm_concepts = new TreeMap();
		  
		//tm_concepts.put("QuadroPri","Quadro de Flores da Pri");
       // tm_concepts.put("ArCond","Ar Condicionado: Cor Bege");
        //tm_concepts.put("MonitorLed","TV de LED: Cor Preta");
        tm_concepts.put("PortaGabinete1","A porta de saída encontra-se em frente no final do corredor a aproximadamente dois metros");
		tm_concepts.put("PortaGabinete2","A porta de saída encontra-se em frente no final do corredor a aproximadamente dois metros");
		tm_concepts.put("ChaveGabinete1","Ghave Gabinete: Cor verde");
		tm_concepts.put("ChaveGabinete2","Ghave Gabinete: Cor verde");
		tm_concepts.put("CadeiraPreta1","encontra-se  em frente.");
		tm_concepts.put("CadeiraPreta2","encontra-se  em frente.");
		tm_concepts.put("CadeiraPreta3","encontra-se  em frente.");
		tm_concepts.put("Mochila1","encontra-se em frente a aproximadamante um metro em cima do armario bége");
		tm_concepts.put("Mochila2","encontra-se em frente a aproximadamante um metro em cima do armario bége");
		tm_concepts.put("Mochila3","encontra-se em frente a aproximadamante um metro em cima do armario bége");
		tm_concepts.put("Notebook1","encontra-se em frente, em cima da  mesa de trabalho.");
		tm_concepts.put("Notebook2","encontra-se em frente, em cima da  mesa de trabalho.");
		tm_concepts.put("Notebook3","encontra-se ao lado, em cima da  mesa de trabalho a direita");
		tm_concepts.put("QuadroPequeno","Quadro Pequeno");
		tm_concepts.put("Mesa1","encontra-se em frente a aproximadamante um metro. Notebook encontram-se em cima da mesa.");
		tm_concepts.put("Mesa2","encontra-se em frente a aproximadamante um metro. Notebook encontram-se em cima da mesa.");
		tm_concepts.put("Mesa3","encontra-se em frente a aproximadamante um metro. Notebook encontram-se em cima da mesa.");
		tm_concepts.put("noteChair1","encontra-se  em frente a aproximadamente um metro, a direita da mesa de trabalho.");
		tm_concepts.put("warDrobe1","encontra-se em frente a aproximadamente um metro.");
		tm_concepts.put("warDrobe2","encontra-se em frente a aproximadamente um metro.");
		tm_concepts.put("warDrobe3","encontra-se em frente a aproximadamente um metro.");
		
		tm_concepts.put("NF","NF");
		tm_concepts.put("NV","Buscando identificar objeto ...");
		
		String obj_concept2 = tm_concepts.get(s);
			
		StringTerm result2 =  new StringTermImpl(obj_concept2);
		
    		return un.unifies(result2,args[1]);
    		} catch (ArrayIndexOutOfBoundsException e) {
    		throw new JasonException("The internal action conceito"+
    		"has not received five arguments!");
    		} catch (ClassCastException e) {
    		throw new JasonException("The internal action conceito"+
    		"has received arguments that are not numbers!");
    		} catch (Exception e) {
    		throw new JasonException("Error in conceito");
    		}
    	 	
    }
}
