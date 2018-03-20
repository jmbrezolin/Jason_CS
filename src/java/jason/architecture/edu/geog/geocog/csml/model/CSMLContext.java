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

public abstract class CSMLContext extends CSMLElement {

    public static final int QUALITY_DIMENSION_TYPE = 0;
    public static final int DOMAIN_TYPE = 1;
    public static final int CONCEPT_TYPE = 2;
    public static final int INSTANCE_TYPE = 3;
    public static final int CONTRAST_CLASS_TYPE = 4;
    public static final String[] typeStrings = {"qualitydimension", "domain", "concept", "instance", "contrastclass"};
    // attributes
    private int type;
    // elements cId -> weight value
    protected Hashtable<String, Double> weights = new Hashtable<String, Double>();

    protected CSMLContext(String id, CSMLModel model, int type) {
        super(id, model);
        this.type = type;
    }

    public int getType() {
        return type;
    }

    private double sumOfWeights() {
        double returnval = 0.0;
        for (Enumeration<Double> e = weights.elements(); e.hasMoreElements();) {
            returnval += e.nextElement();
        }
        return returnval;
    }

    public void normalizeWeights() {
        if (weights == null) {
            return;
        }
        double sum = sumOfWeights();
        if (sum != 1.0 && sum != 0.0) {
            Hashtable<String, Double> newWeights = new Hashtable<String, Double>();
            for (Enumeration<String> e = weights.keys(); e.hasMoreElements();) {
                String key = e.nextElement();
                newWeights.put(key, weights.get(key) / sum);
            }
            weights = newWeights;
        }
    }

    public void addWeight(String elementId, double weight) {
        double newWeight = weight;
        // limit weights to be in range [0,1]
        if (weight < 0.0) {
            weight = 0.0;
        }
        if (weight > 1.0) {
            weight = 1.0;
        }
        weights.put(elementId, newWeight);
    }

    public void removeWeight(String elementId) {
        weights.remove(elementId);
    }

    public Double getWeight(String element) {
        return weights.get(element);
    }

    public String[] getcIds() {
        return weights.keySet().toArray(new String[0]);
    }

    public String toCSML() {
        String csml = "";
        csml += "<csml:Context csml:ID=\"" + getId() + "\" csml:type=\"" + typeStrings[type] + "\">\n";
        Enumeration<String> keys = weights.keys();
        while (keys.hasMoreElements()) {
            String key = keys.nextElement();
            double value = weights.get(key);
            csml += "	<csml:Weight csml:cID=\"" + key + "\">";
            csml += "		" + value;
            csml += "	</csml:Weight>\n";
        }
        csml += "</csml:Context>\n";
        return csml;
    }
}
