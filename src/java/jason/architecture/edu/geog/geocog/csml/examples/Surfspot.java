/**
 * Copyright (c) 2009, Benjamin Adams
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
    * Neither the name of Benjamin Adams nor the
      names of its contributors may be used to endorse or promote products
      derived from this software without specific prior written permission.

 * THIS SOFTWARE IS PROVIDED BY Benjamin Adams ''AS IS'' AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL Benjamin Adams BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * @author Ben
 */

package edu.geog.geocog.csml.examples;

import edu.geog.geocog.csml.CSMLParser;
import edu.geog.geocog.csml.model.CSMLModel;
import edu.geog.geocog.csml.reasoner.CSMLConceptualSpaceReasoner;
import edu.geog.geocog.csml.Logger;

/**
 *
 * @author Ben
 */
public class Surfspot {
    public static void main(String[] args) {
        // Create a CSML parser object
        CSMLParser parser = new CSMLParser(new Logger());
        // Parse a csml file and generate a conceptual space data model
        try {
          CSMLModel model = parser.parseCSML("surf.csml");
          CSMLConceptualSpaceReasoner reasoner = new CSMLConceptualSpaceReasoner(model);
          reasoner.standardizeInstancesInAllDomains();
     
          double sim = reasoner.semanticSimilarity(
                "surfspot.csml#CampusPoint",
                "surfspot.csml#Rincon",
                "surfspot.csml#WaveCoordWindWeights",
                new String[] {"surfspot.csml#WaveDimWeights",
                               "surfspot.csml#WindDimWeights",
                              "surfspot.csml#CoordDimWeights"});
            System.out.println(sim);
          }
        catch (Exception e) {
                System.out.println("teste");
                }

       
        // Create a reasoner object for the model
        // The reasoner has methods for various operations such as similarity measurement
       
            // calculate similarity of two instances
            // first parameter:  first instance's id
            // second parameter: second instance's id
            // third parameter:  domain-type context's id
            // fourth parameter: array of quality dimension-type context ids
            
       

        } 
    }
