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

public class CSMLQualityDimensionTypeContext extends CSMLContext {

    private String domainId;
    private CSMLDomain domain;

    protected CSMLQualityDimensionTypeContext(String id, CSMLModel model, String domid) {
        super(id, model, CSMLContext.QUALITY_DIMENSION_TYPE);
        this.domainId = domid;
    }

    protected CSMLQualityDimensionTypeContext(String id, CSMLModel model, CSMLDomain domain) {
        super(id, model, CSMLContext.QUALITY_DIMENSION_TYPE);
        if (!model.containsCSMLElement(domain)) {
            throw new RuntimeException("Doman and point must be in the same model");
        }
        this.domainId = domain.getId();
        this.domain = domain;
    }

    @Deprecated
    protected void postLoadSetup() {
        domain = model.getDomain(domainId);
    }

    public void setDomainId(String dId) {
        domainId = dId;
    }

    public String getDomainId() {
        return domainId;
    }

    public void setType(String type) {
    }

    @Override
    public String toCSML() {
        String csml = "";
        csml += "<csml:Context csml:ID=\"" + getId() + "\" csml:type=\"qualitydimension\" csml:domainID=\"" + domainId + "\">\n";
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
