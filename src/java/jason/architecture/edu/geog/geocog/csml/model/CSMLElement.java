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

import java.net.URI;
import java.net.URISyntaxException;

public class CSMLElement {

    // attributes
    private String id; // absolute or fragment URI
    // elements
    private String label;
    private String description;
    protected CSMLModel model;

    protected CSMLElement(String id, CSMLModel model) {
        this.id = id;
        this.model = model;
        label = "";
        description = "";
    }

    /**
     * Can be called if any post load setup is needed in the object
     */
    @Deprecated
    protected void postLoadSetup() {
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public boolean idIsAbsolute() {
        try {
            URI uri = new URI(id);
            return uri.isAbsolute();
        } catch (URISyntaxException ex) {
            return false;
        }
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public String getLabelCSML() {
        return "	<csml:Label>" + label + "</csml:Label>\n";
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public String getDescriptionCSML() {
        return "	<csml:Description>" + description + "</csml:Description>\n";
    }

    @Override
    public String toString() {
        String s = label.trim();
        if (label != null && !label.equals("")) {
            return label;
        } else {
            try {
                URI uri = new URI(id);
                if (uri.getFragment() != null) {
                    return uri.getFragment();
                } else {
                    return id;
                }
            } catch (URISyntaxException ex) {
                return id;
            }
        }
        /*  String s = "";
         String labelS = getLabel();
         if (labelS != null && !labelS.equals("")) {
         s += "<html><b>" + labelS + "</b>" + " ";
         }
         String idS = getId();
         if (idS != null) {
         s += "("+idS+")";
         } 
         return s;
         */
    }
}
