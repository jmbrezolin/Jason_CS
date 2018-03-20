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

public class CSMLPoint extends CSMLElement {

    // elements
    private String[] q = new String[0];	// Make Hashtable ??!!
    private double[] v = new double[0];
    private final String domainid;
    private CSMLDomain domain;

    //public CSMLPoint(String id, CSMLModel model, String domid) {
   //     super(id, model);
    //    this.domainid = domid;
  //  }

    protected CSMLPoint(String id, CSMLModel model, CSMLDomain domain) {
        super(id, model);
        if (!model.containsCSMLElement(domain)) {
            throw new RuntimeException("Doman and point must be in the same model");
        }
        this.domainid = domain.getId();
        this.domain = domain;
        setq(domain.getQualityDimensionIds());
    }

    @Deprecated
    protected void postLoadSetup() {
        domain = model.getDomain(domainid);
        setq(domain.getQualityDimensionIds());
    }

    public boolean setPoint(String[] q, double[] v) {
        if (q == null || v == null) {
            return false;
        }
        if (q.length != v.length) {
            return false;
        }

        this.q = q;
        this.v = v;

        return true;
    }

    public boolean isOK() {
        if (q == null || v == null) {
            return false;
        }
        if (q.length != v.length) {
            return false;
        }

        return true;
    }

    public boolean setq(String[] q) {
        if (q == null) {
            return false;
        }

        this.q = q;
        return true;
    }

    public String[] getq() {
        return q;
    }

    public boolean setv(double[] v) {
        if (v == null) {
            return false;
        }

        this.v = v;
        return true;
    }

    public double[] getv() {
        return v;
    }

    public Double[] getV() {
        Double[] newV = new Double[v.length];
        for (int i = 0; i < v.length; i++) {
            newV[i] = v[i];
        }
        return newV;
    }

    public String getDomainId() {
        return domain.getId();
    }
    
    public CSMLDomain getDomain(){
        return domain;
    }

    public Double getValue(String qDimId) {
        for (int i = 0; i < q.length; i++) {
            if (q[i].equals(qDimId)) {
                return v[i];
            }
        }

        return null;
    }

    public boolean updateValue(String qDimId, double newValue) {
        for (int i = 0; i < q.length; i++) {
            if (qDimId.equals(q[i])) {
                v[i] = newValue;
                return true;
            }
        }
        return false;
    }

    public boolean domainOK() {
        for (int i = 0; i < q.length; i++) {
            if (domain.getQualityDimension(q[i]) == null) {
                return false; // quality dimension not in domain
            }
        }

        return true;
    }

    public String toCSML() {
        String csml = "";
        csml += "	<csml:Point csml:ID=\"" + getId() + "\" csml:domainID=\"" + domainid + "\">\n";
        csml += "		<csml:PointValues>\n";
        for (int i = 0; i < v.length; i++) {
            csml += "			<cn>" + v[i] + "</cn>\n";
        }
        csml += "		</csml:PointValues>\n";
        csml += "	  <csml:qVector>\n";
        for (int i = 0; i < q.length; i++) {
            csml += "			<ci>" + q[i] + "</ci>\n";
        }
        csml += "	  </csml:qVector>\n";
        csml += "	</csml:Point>\n";
        return csml;
    }
}
