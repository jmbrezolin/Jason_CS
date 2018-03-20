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

public class CSMLContrastClass extends CSMLElement {

    // attributes
    private String domainId;
    private CSMLDomain domain;
    // elements
    private double[] a = new double[0];
    private String[] q = new String[0];
    private double bMin;
    private double bMax;

    protected CSMLContrastClass(String id, CSMLModel model, String domid) {
        super(id, model);
        this.domainId = domid;
    }

    protected CSMLContrastClass(String id, CSMLModel model, CSMLDomain domain) {
        super(id, model);
        if (!model.containsCSMLElement(domain)) {
            throw new RuntimeException("Doman and region must be in the same model");
        }
        this.domainId = domain.getId();
        this.domain = domain;
    }

    @Deprecated
    public void postLoadSetup() {
        domain = model.getDomain(domainId);
        setq(domain.getQualityDimensionIds());
    }

    public boolean setContrastClass(double[] a, String[] q, double bMin, double bMax) {
        if (a == null || q == null) {
            return false;
        }
        if (a.length != q.length) {
            return false;
        }

        this.a = a;
        this.q = q;
        this.bMin = bMin;
        this.bMax = bMax;

        return true;
    }

    public boolean isOK() {
        if (a == null || q == null) {
            return false;
        }
        if (a.length != q.length) {
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

    public boolean seta(double[] a) {
        if (a == null) {
            return false;
        }

        this.a = a;
        return true;
    }

    public double[] geta() {
        return a;
    }

    public void setbMin(double bMin) {
        this.bMin = bMin;
    }

    public double getbMin() {
        return bMin;
    }

    public void setbMax(double bMax) {
        this.bMax = bMax;
    }

    public double getbMax() {
        return bMax;
    }

    public String getDomain() {
        return domain.getId();
    }

    public boolean domainOK(CSMLDomain d) {
        if (!d.getId().equals(domainId)) {
            return false;
        }
        for (int i = 0; i < q.length; i++) {
            if (d.getQualityDimension(q[i]) == null) {
                return false;
            }
        }

        return true;
    }

    public String toCSML() {
        String csml = "";
        csml += "<csml:ContrastClass csml:ID=\"" + getId() + "\" csml:domainID=\"" + domainId + "\">\n";
        csml += "<csml:aVector>\n";
        for (int i = 0; i < a.length; i++) {
            csml += "<cn>" + a[i] + "</cn>\n";
        }
        csml += "</csml:aVector>\n";
        csml += "<csml:qVector>\n";
        for (int i = 0; i < q.length; i++) {
            csml += "<ci>" + q[i] + "</ci>\n";
        }
        csml += "</csml:qVector>\n";
        csml += "<csml:ccMin>" + bMin + "</csml:ccMin>\n";
        csml += "<csml:ccMax>" + bMax + "</csml:ccMax>\n";
        csml += "</csml:ContrastClass>\n";
        return csml;
    }
}
