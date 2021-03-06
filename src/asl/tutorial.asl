/* Plans for tutorial */

+!create_tutorial(AgentName) : 
    true <-
        .print("Creating Tutorial artifact. We are using a agent name: ", AgentName);
        makeArtifact("Tutorial","jason.architecture.ArtTutorial",[AgentName],Tutorial).
        
-!create_tutorial(AgentName) : 
    true <-
        .print("Tutorial artifact creation failed.").

+!create_tutorial : 
    true <-
        .print("Creating Tutorial artifact. The agent name is unknown.");
        makeArtifact("Tutorial","jason.architecture.ArtTutorial",[],Tutorial).
		   
-!create_tutorial : 
    true <-
        .print("Tutorial artifact creation failed.").
		
+?toolTutorial(TutorialId) :
    true <- 
        .print("Discovering Tutorial artifact.");
        lookupArtifact("Tutorial",TutorialId).
	 
-?toolTutorial(TutorialId) :
    true <-
        .print("Trying to discover Tutorial artifact again."); 
        .wait(10);
        ?toolTutorial(TutorialId).        
    
+chatter5(A)
    <-  .println("a chatter from service was perceived");
    	.println("Message: ", A);	
		myp.getObjectFrame(A,F);
		.println("Frame: ", F);
		+frame(F);
		?lastFrame(K);
		+hasPreviousFrame(F,K);
		myp.getObjectConcept(A,C);
		.println("Concept: ", C);
		!verify_concept(A,C);
		
		//myp.getObjectFrameID (A,I);  	
		//myp.getObjectPosition(A,P);
		//myp.getObjectWidth(A,W);
		//myp.getPreviousFrame (A,R); 
        myp.getHT(A,H);
		//myp.getEnviroment(A,E);
		//!verify_enviroment(A,E);
		//myp.getObjectDistance (E,C,D);
		//.findall(c(B,G), enviroment(E) & conected(E,B) & conected_relation(E,B, G) ,D);
		.println("Head or Tail? ", H);
		//+object(I);
		
		//+hasObject(F,I);
		//+hasConcept(I,C);
		//+hasPosition(I,P);
		 //myp.list_bels;

		//.println("Found -> Frame:",F," ObjectID:",I," Concept: ",C," Position: ",P," Frame Anterior: ",K, " ou ",R, " HT : ",H," Width: ",W, "Enviroment",E);
		!readall(A,H).
		//!readall(F,H).
		//.concat("Objeto localizado:",C," Posicao:",P,M);
		//println(M);
		//pub(M).
		//myp.getObjectID(A,D); 
		//myp.getObjectPosition(A,P);
		//myp.getObjectCenterX(A,X);
		//myp.getObjectCenterY(A,Y);
		
	   // +object_center(D,X,Y);
		//+object_position(D,P);
	   // +object(C,D).	
		//!find_object_parts(C,D).
		//.add_annot(objectCSML, source(percept),B);
		//myp.list_bels.
		
+!find_object_parts(A,B):  object(A,B) & A=="facin_elevator"
<-  pub(A);
   .println("Conceito: ",A," Instancia: ",B).

+!readall(A,H): H=="tail"
<-   myp.getEnviroment(A,E); 
     !verify_enviroment(A,E);

//-+lastFrame(X);
        
		//myp.getObjectFrame(A,F);
		
		
    //  .findall(c(Y,A),hasObject(X,Y)& hasPosition(Y,A)& A=="Lado Esquerdo",L); 
   //   .findall(c(Y,A),hasObject(X,Y)& hasPosition(Y,A)& A=="Frente Esquerda",M);
	//  .findall(c(Y,A),hasObject(X,Y)& hasPosition(Y,A)& A=="Frente Direita",N);
	 // .findall(c(Y,A),hasObject(X,Y)& hasPosition(Y,A)& A=="Lado Direito",O);
	//  .concat(L,M,C);
	//  .concat(C,N,D);
	//  .concat(D,O,W);
    //   for ( .member(Z,W) ) {
      //       .print("Entrei ->",Z);
	//		 //?hasConcept(I,C);
			// myp.getObjectDistance (E,C,D);
     //  }
    .println("Li todos -> Frame:",X, "HT",H).

+!readall(X,Y): Y=="head"
<-  myp.getObjectFrame(X,F);
   .println("Nao Li todos -> Frame:",F, "HT",Y).

+!verify_concept(A,C): not(C=="NF")
<-  
        myp.getObjectFrame(A,F); 
		myp.getObjectFrameID (A,I); 
		myp.getObjectConcept(A,C); 
		myp.getObjectPosition(A,P);
		myp.getObjectWidth(A,W);
		myp.getObjectDescription(C,Desc);
		//myp.getObjectReference(C,Ref);
		myp.getHT(A,H);
		myp.getEnviroment(A,E); 
		//myp.getPreviousFrame (A,R);
		//+frame(F);
		+object(I);
		//?lastFrame(K);
		//+hasPreviousFrame(F,K);
		+hasObject(F,I);
		+hasConcept(I,C);
		+hasPosition(I,P);
		+hasWidth(I,W);
		+env(H);
		if (env(H) & H=="Head"){
		    .concat("Objeto encontrado: ",Desc,"posição: ",P,Msg1);
			.println("Entrei 1");
		    // pub(Msg1);
		 }else{
		     +env2(E);
			 if (env2(E) & E=="ND"){
			    .concat("Objeto encontrado: ",Desc,"posição: ",P,Msg1);
			    .println("Entrei 1");
		        // pub(Msg1);
			 }else{
		        .concat("Objeto encontrado: ",Desc,"posição: ",P,". Ambiente similar a ",E,    Msg1);
			    .println("Entrei 2");
		        // pub(Msg1);
			 }
		 }
		.println("Found -> Frame:",F," ObjectID:",I," Concept: ",C," Position: ",P," Width: ",W);
        .println(Msg1).

+!verify_concept(A,C): C=="NF"
<-  myp.getObjectPosition(A,P);
    .concat("Objeto não indetificado, posição: ",P,Msg1);
    //pub(Msg1);
   //.wait(500);
   .println(Msg1).

+!verify_enviroment(A,E): E=="ND"
<- .println("Ambiente não identificado: ",E).

+!verify_enviroment(A,E): not(E=="ND")
<-   myp.getObjectFrame(A,F);
     myp.getObjectWidth(A,W);
    -+lastFrame(F);
	+hasSimilarity(F,E);
	
    .findall(Id1,hasObject(F,Id1)& hasPosition(Id1,Pos1)& Pos1=="esquerda",I); 
	 if ( .length(I,Tam1) & Tam1 > 0) { 
	     for ( .member(Oid1,I) ) {
			   ?hasConcept(Oid1,Con1);
			   ?hasWidth (Oid1,Wid1);
			   myp.getObjectDescription(Con1,Desc1);
		     //  myp.getObjectReference(Con1,Ref1);
			   .concat("Objeto encontrado: ",Desc1,"posição: a direita",Msg2);
		//	   //.concat(Con1, " localizado a esquerda ",Msg2);
			   pub(Msg2);
			   .wait(500);
		//	  //  myp.getObjectDistance (E,Con1,Wid1,Dist1);
			  //	.print(Wid1, " distancia => ", Dist1);
			  // .concat(Con1, " localizado ao lado esquerdo a", Dist1, "de distancia",Msg2);
			  .print(Msg2);
			  
          } 
        //  .print("Lado Esquerdo",Tam1);
     }
	 .findall(Id2,hasObject(F,Id2)& hasPosition(Id2,Pos2)& Pos2=="frente",J); 
	  if ( .length(J,Tam2) & Tam2 > 0) { 
	     for ( .member(Oid2,J) ) {
			   ?hasConcept(Oid2,Con2);
			   ?hasWidth (Oid2,Wid2);
			   myp.getObjectDescription(Con2,Desc2);
		 //      myp.getObjectReference(Con2,Ref2);
			   .concat("Objeto encontrado: ",Desc2,"posição: em frente ",Msg3);
			   //.concat(Con2, " localizado em frente a esquerda ",Msg3);
			   pub(Msg3);
			   .wait(500);
			  // myp.getObjectDistance (E,Con2,Wid2,Dist2);
			  // .print(Wid2, " distancia => ", Dist2);
			 //  .concat(Con2, " localizado em frente a esquerda a", Dist2, "de distancia",Msg3);
			   .print(Msg3);
          } 
     //     .print("Frente Esquerda",Tam2);
      }
	  .findall(Id3,hasObject(F,Id3)& hasPosition(Id3,Pos3)& Pos3=="direita",K);
	  
	   if ( .length(K,Tam3) & Tam3 > 0) { 
	     for ( .member(Oid3,K) ) {
			   ?hasConcept(Oid3,Con3);
			   ?hasWidth (Oid3,Wid3);
			   myp.getObjectDescription(Con3,Desc3);
		//       myp.getObjectReference(Con3,Ref3);
			   .concat("Objeto encontrado: ",Desc3,"posição: a esquerda ",Msg4);
			   //.concat(Con3, " localizado em frente a direita ",Msg4);
			   pub(Msg4);
			   .wait(500);
			//  myp.getObjectDistance (E,Con3,Wid3,Dist3);
			   // .print(Wid3, " distancia => ", Dist3);
			  // .concat(Con3, " localizado em frente a direita a ", Dist3, "de distancia",Msg4);
		//	   .print(Msg4);
          } 
         // .print("Frente Direita",Tam3);
      }
	//  .findall(Id3,hasObject(F,Id3)& hasPosition(Id3,Pos3)& Pos4=="Lado Direito",L);
	  
	 //  if ( .length(L,Tam4) & Tam4 > 0) { 
	  //   for ( .member(Oid4,L) ) {
		//	   ?hasConcept(Oid4,Con4);
		//	   ?hasWidth (Oid4,Wid4);
		//	   myp.getObjectDescription(Con4,Desc4);
		//       myp.getObjectReference(Con4,Ref4);
		//	   .concat("Objeto encontrado: ",Desc4,"posição: ",Ref4,Msg5);
		//	   //.concat(Con4, " localizado ao lado direito ",Msg5);
		//	    pub(Msg5);
			  // myp.getObjectDistance (E,Con4,Wid4,Dist4);
			   //.print(Wid4, " distancia => ", Dist4);
			  // .concat(Con4, " localizado ao lado direito a ", Dist4, "de distancia",Msg5);
			//   .print(Msg5);
       //   } 
       //   .print("Lado Direito",Tam4);
     // }
	  .concat("Local similar a ",E,Msg1);
	  .print(Msg1);
	   pub(Msg1);
	 // .findall(Env1, conected(E,Env1) & conected_relation(E,Env1, Pos5) ,M);
	//  .print("Entrei aqui",M);
	 // if ( .length(M,Tam5) & Tam5 > 0) { 
	   //  for ( .member(Eid1,M) ) {
		 // .concat(" A esquerda de ",E, " encontra-se", Eid1,Msg6);
			 //  .print(Msg6);
	//	 }
	//  }	 
	  
	//  .findall(Env2, conected(E,Env2) & conected_relation(E,Env2, Pos6)& Pos6=="Lado direito" ,N);
	//  .print("Entrei aqui tambem",N);
	 // if ( .length(N,Tam5) & Tam5 > 0) { 
	 //    for ( .member(Eid2,N) ) {
		//  .concat(" A direita de ",E, " encontra-se", Eid2,Msg7);
		//	   .print(Msg7);
	//	 }
	//  }
	
     .println("Li todos -> Frame:",F);
     .println("Nao Li ND: ",E).
		        
/* +twoIntsSum(A)
    <- .println("a somatory from service was perceived");
    	println("Value: ", A);        
        -+currentSum(A).
 */       
        