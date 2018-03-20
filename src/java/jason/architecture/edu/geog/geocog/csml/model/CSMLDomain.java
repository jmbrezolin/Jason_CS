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
import java.util.Hashtable;
import java.util.LinkedHashMap;

public class CSMLDomain extends CSMLElement {

    // elements
    private Hashtable<String, CSMLQualityDimension> qualityDimensions = new Hashtable<String, CSMLQualityDimension>();
    
    private  LinkedHashMap <String, CSMLQualityDimension> qualityDimensions2 = new LinkedHashMap <String, CSMLQualityDimension>();

    public CSMLDomain(String id, CSMLModel model) {
        super(id, model);
    }

    public void addQualityDimension(CSMLQualityDimension qd) {
        qualityDimensions2.put(qd.getId(), qd);
        qualityDimensions.put(qd.getId(), qd);
    }

    public void removeQualityDimension(String id) {
        qualityDimensions2.remove(id);
        qualityDimensions.remove(id);
    }

    public void removeQualityDimension(CSMLQualityDimension q) {
        qualityDimensions2.remove(q.getId());
        qualityDimensions.remove(q.getId());
    }

    public void removeAllQualityDimensions() {
        qualityDimensions2 = new LinkedHashMap <String, CSMLQualityDimension>();
        qualityDimensions = new Hashtable<String, CSMLQualityDimension>();
    }

    public CSMLQualityDimension getQualityDimension(String id) {
        return qualityDimensions.get(id);
    }

    public int numberOfDimensions() {
        return qualityDimensions.size();
    }

    public String[] getQualityDimensionIds() {
        return qualityDimensions2.keySet().toArray(new String[0]);
        //return qualityDimensions.keySet().toArray(new String[0]);
    }

    public String toCSML() {
        String csml = "";
        csml += "<csml:Domain csml:ID=\"" + getId() + "\">\n";
        csml += getLabelCSML();
        csml += getDescriptionCSML();
        Enumeration<String> keys = qualityDimensions.keys();
        while (keys.hasMoreElements()) {
            csml += qualityDimensions.get(keys.nextElement()).toCSML();
        }
        csml += "</csml:Domain>\n";
        return csml;
    }
}
