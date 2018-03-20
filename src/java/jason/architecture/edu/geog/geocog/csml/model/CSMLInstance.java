/**
 * Copyright (c) 2009, Benjamin Adams All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer. Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials provided
 * with the distribution. Neither the name of Benjamin Adams nor the names of
 * its contributors may be used to endorse or promote products derived from this
 * software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY Benjamin Adams ''AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO
 * EVENT SHALL Benjamin Adams BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * @author Ben
 */
package edu.geog.geocog.csml.model;

import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;
import java.util.*;

public class CSMLInstance extends CSMLElement {

    // elements
    // note the key is domainID of point, NOT ID!
    private Hashtable<CSMLDomain, CSMLPoint> points = new Hashtable<CSMLDomain, CSMLPoint>();

    public CSMLInstance(String id, CSMLModel model) {
        super(id, model);
    }

    public void addPoint(CSMLPoint p) {
       // System.out.println("Adicionei"+p);
        points.put(p.getDomain(), p);
    }

    // remove point by domainId
    public void removePoint(CSMLDomain domain) {
        points.remove(domain);
    }

    // remove point by domainId
    public void removePoint(CSMLPoint p) {
        points.remove(p.getDomainId());
    }

    public CSMLPoint getPointById(String pid) {
        for (Enumeration<CSMLPoint> e = points.elements(); e.hasMoreElements();) {
            CSMLPoint p = e.nextElement();
            if (p.getId().equals(pid)) {
                return p;
            }
        }
        return null;
    }

    // get point by domainId
    public CSMLPoint getPoint(String domainId) {
        
        return points.get(domainId);
    }
    
      public CSMLPoint getPoint2(CSMLDomain domainId) {
        
        return points.get(domainId);
    }

    public String[] getDomainIds() {
        Set <CSMLDomain> key = points.keySet();
        String[] keyArray = new String[key.size()];
        int i=0;
    for (Iterator<CSMLDomain> it = key.iterator(); it.hasNext(); ) {
        CSMLDomain f = it.next();
        keyArray[i]=f.toString();
        i++;
       System.out.println("foo found"+f);
       }
    
       for(int j=0;j<keyArray.length; j++){
        System.out.println("foo foundy"+  keyArray[j]);
       }        
       // String[] keyArray = points.keySet().toArray(new String[points.size()]);
    
        //return points.keySet().toArray(new String[0]);
       return keyArray;
    }

    public Set<CSMLPoint> getPoints() {
        return new HashSet(points.values());
    }

    public boolean hasPointInDomain(String domainId) {
        return points.containsKey(domainId);
    }

    public String toCSML() {
        String csml = "";
        csml += "<csml:Instance csml:ID=\"" + getId() + "\">\n";
        csml += getLabelCSML();
        csml += getDescriptionCSML();
        Enumeration<CSMLDomain> keys = points.keys();
        while (keys.hasMoreElements()) {
            csml += points.get(keys.nextElement()).toCSML();
        }
        csml += "</csml:Instance>\n";
        return csml;
    }
}
