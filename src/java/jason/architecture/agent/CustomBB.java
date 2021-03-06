package agent;

import jason.JasonException;
import jason.asSemantics.*
;
import jason.asSyntax.*;
import jason.bb.*;
import jason.stdlib.*;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CustomBB extends DefaultBeliefBase {
	
	//private long start;
	private static Logger logger = Logger.getLogger(DefaultBeliefBase.class.getSimpleName());
	int flag=0;	
	Literal l1;
	StringTerm result =  new StringTermImpl();
	StringTerm result2 =  new StringTermImpl();
	@Override
	public void init(Agent ag, String[] args) {
	 // start = System.currentTimeMillis();
	  super.init(ag,args);
	  }
	
	@Override
	 public boolean add(Literal l) {

	     if (l.getFunctor().equals("objectCSML")){
		    logger.warning("Building CSML model:...:");	
		    result = (StringTerm)(l.getTerm(0));
			String s = result.getString();
			result2 = (StringTerm)(l.getTerm(1));
			String s2 = result2.getString();
			String nb = s+"("+s2+")[source(percept),csmldb(mycsmldb)]";
            l1 = Literal.parseLiteral(nb);
		     flag=1;
					  
          }
		 logger.warning("Building CSML model:...:"+l.getFunctor());		
	
	     if (flag==0){
		  return super.add(l);
		
	     }else{
	
	       return super.add(l1);
	     }
	 }	 
		
}
