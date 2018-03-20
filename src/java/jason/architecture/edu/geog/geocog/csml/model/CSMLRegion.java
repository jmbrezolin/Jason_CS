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

import java.util.ArrayList;

public class CSMLRegion extends CSMLElement {

    // attributes
    private String domainid;
    private double[][] A = new double[0][0];
    private String[] q = new String[0];
    private double[] b = new double[0];
    private double[] centroid;
    // minimum axis-aligned bounding box
    private double[] mbbMins;
    private double[] mbbMaxs;
    private CSMLPoint[] vPolytope;
    private CSMLDomain domain;

    protected CSMLRegion(String id, CSMLModel model, String domid) {
        super(id, model);
        this.domainid = domid;
    }

    protected CSMLRegion(String id, CSMLModel model, CSMLDomain domain) {
        super(id, model);
        if (!model.containsCSMLElement(domain)) {
            throw new RuntimeException("Doman and region must be in the same model");
        }
        this.domainid = domain.getId();
        this.domain = domain;
    }

    @Deprecated
    public void postLoadSetup() {
        domain = model.getDomain(domainid);
        setq(domain.getQualityDimensionIds());
    }

    public String getDomainId() {
        return domainid;
    }
    
    public CSMLDomain getDomain(){
        return domain;
    }

    /**
     * Checks if this region comply the domain d
     *
     * @param d
     * @return
     */
    public boolean domainOK() {
        for (int i = 0; i < q.length; i++) {
            if (domain.getQualityDimension(q[i]) == null) {
                return false;
            }
        }

        return true;
    }

    public boolean isLoose() {
        return A.length == 0;
    }

    public boolean isOK() {
        if (A == null || q == null || b == null) {
            return false;
        }
        if (A.length != 0) {;
            if (A[0].length != q.length) {
                return false;
            }
            if (A.length != b.length) {
                return false;
            }
            if (!isRectangular(A)) {
                return false;
            }
        }
        return true;
    }

    private boolean isRectangular(double[][] m) {
        if (m.length > 0) {
            int colnum = m[0].length;
            for (int i = 1; i < m.length; i++) {
                if (m[i].length != colnum) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public boolean setRegion(double[][] A, String[] q, double[] b) {
        if (A == null || q == null || b == null) {
            return false;
        }
        if (A.length == 0) {
            return false;
        }
        if (A[0].length != q.length) {
            return false;
        }
        if (A.length != b.length) {
            return false;
        }
        if (!isRectangular(A)) {
            return false;
        }

        this.A = A;
        this.q = q;
        this.b = b;
        vPolytope = null;
        return true;
    }

    public boolean setA(double[][] A) {
        if (A == null) {
            return false;
        }
        if (A.length == 0) {
            return false;
        }
        if (!isRectangular(A)) {
            return false;
        }

        this.A = A;
        vPolytope = null;
        return true;
    }

    public double[][] getA() {
        return A;
    }

    private int getQualityDimensionIndex(String qd) {
        int result = -1;
        for (int i = 0; i < q.length; i++) {
            if (q[i].equals(qd)) {
                return i;
            }
        }

        return result;
    }

    // returns the A Matrix with the columns ordered based on order of quality dimensions in qOrder
    public double[][] getAOrdered(String[] qOrder) {
        if (A == null || A.length == 0) {
            return null;
        }
        double[][] orderedA = new double[A.length][A[0].length];
        if (qOrder.length != q.length) {
            return null;
        }

        for (int i = 0; i < qOrder.length; i++) {
            int n = getQualityDimensionIndex(qOrder[i]);
            for (int m = 0; m < A.length; m++) {
                orderedA[m][i] = A[m][n];
            }
        }
        return orderedA;
    }

    public boolean setq(String[] q) {
        if (q == null) {
            return false;
        }

        this.q = new String[q.length];
        for (int i = 0; i < q.length; i++) {
            this.q[i] = new String(q[i]);
        }
        vPolytope = null;
        return true;
    }

    public String[] getq() {
        if (q == null) {
            return null;
        }
        String[] newQ = new String[q.length];
        for (int i = 0; i < q.length; i++) {
            newQ[i] = new String(q[i]);
        }
        return newQ;
    }

    public boolean setb(double[] b) {
        if (b == null) {
            return false;
        }

        this.b = b;
        vPolytope = null;
        return true;
    }

    public double[] getb() {
        return b;
    }

    public boolean setCentroid(double[] centroid) {
        if (centroid == null || q == null) {
            return false;
        }
        if (centroid.length != q.length) {
            return false;
        }
        this.centroid = centroid;
        return true;
    }

    public double[] getCentroid() {
        //patch!
        if (centroid == null) {
            if (b.length == 2) {
                //bounded 1d region
                double c = (b[1] - b[0]) / 2;
                centroid = new double[]{c};
            }
        }
        return centroid;
    }

    public CSMLPoint getCentroidAsPoint(String pId) {
        if (!isOK() || centroid == null || centroid.length != q.length) {
            return null;
        }
        CSMLPoint p = model.createCSMLPoint(pId, domain);

        String[] qCopy = new String[q.length];
        double[] centroidCopy = new double[centroid.length];
        for (int i = 0; i < q.length; i++) {
            qCopy[i] = new String(q[i]);
            centroidCopy[i] = centroid[i];
        }
        p.setPoint(qCopy, centroidCopy);

        return p;
    }

    public CSMLPoint getCentroidAsPoint() {
        return getCentroidAsPoint(new String(getId() + ".CENTROID"));
    }

    /**
     * Returns true if the region contains the given point in domain, otherwise
     * false
     */
    public boolean contains(CSMLPoint p, double precision) {
        if (p instanceof CSMLUndefinedPoint) {
            return this.equals(((CSMLUndefinedPoint) p).getReferedProperty().getRegions()[0]);            
        } else {
            if (!isOK() || !p.isOK()) {//|| !domainOK(d) || !p.domainOK(d)) {
                return false;
            }

            ArrayList<Double> values = new ArrayList<Double>();
            for (int i = 0; i < q.length; i++) {
                String qd = q[i];
                    values.add(p.getValue(qd));
            }

            // test each inequality in system
            for (int m = 0; m < A.length; m++) {
                double value = 0.0;
                for (int n = 0; n < A[m].length; n++) {
                    value += A[m][n] * values.get(n);
                }
                if (value > (b[m] + precision)) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean contains(CSMLPoint p) {
        return contains(p, Globals.roundPrecision);
    }

    public boolean isProperty() {
        return q.length == 1;
    }

    private boolean qMatch(String[] otherQ) {
        if (q == null || otherQ == null) {
            return false;
        }
        if (q.length != otherQ.length) {
            return false;
        }
        for (int i = 0; i < q.length; i++) {
            for (int j = 0; j < otherQ.length; j++) {
                if (q[i].equals(otherQ[j])) {
                    break;
                }
                if (j == otherQ.length - 1) {
                    return false;
                }
            }
        }
        return true;
    }

    private int[] qIdxOrder(String[] otherQ) {
        if (!qMatch(otherQ)) {
            return null;
        }
        int[] idxOrder = new int[q.length];

        for (int i = 0; i < q.length; i++) {
            for (int j = 0; j < otherQ.length; j++) {
                if (q[i].equals(otherQ[j])) {
                    idxOrder[j] = i;
                    break;
                }
            }
        }

        return idxOrder;
    }

    // get qhull format in the given quality dimension order
    public String qHullFormat(String[] qDimOrder) {
        String s = "";
        if (!isOK()) {
            return s;
        }

        int[] idxOrder = qIdxOrder(qDimOrder);
        if (idxOrder == null) {
            return s;
        }

        // write centroid
        if (centroid != null) {
            s += q.length + " 1\n";
            for (int i = 0; i < idxOrder.length; i++) {
                int curIdx = idxOrder[i];
                s += "  ";
                s += centroid[curIdx];
            }
            s += "\n";
        }

        s += (q.length + 1) + "\n";
        s += (A.length) + "\n";

        for (int m = 0; m < A.length; m++) {
            for (int n = 0; n < idxOrder.length; n++) {
                int curIdx = idxOrder[n];
                s += "  ";
                s += A[m][curIdx];
            }
            s += "  " + (-b[m]) + "\n";
        }

        return s;
    }

    // get qhull format
    public String qHullFormat() {
        return qHullFormat(q);
    }

    public boolean setBoundingBox(double[] mins, double[] maxs) {
        if (mins == null || maxs == null || isOK()) {
            return false;
        }
        if (mins.length != q.length || maxs.length != q.length) {
            return false;
        }
        mbbMins = mins.clone();
        mbbMaxs = maxs.clone();
        return true;
    }

    public double[][] getBoundingBox() {
        if (mbbMins == null || mbbMaxs == null) {
            return null;
        }

        double[][] bb = new double[2][mbbMins.length];
        for (int i = 0; i < mbbMins.length; i++) {
            bb[0][i] = mbbMins[i];
            bb[1][i] = mbbMaxs[i];
        }
        return bb;
    }

    public void setVPolytope(CSMLPoint[] pts) {
        vPolytope = pts;
        // TODO error checking
    }

    public CSMLPoint[] getVPolytope() {
        return vPolytope;
    }

    public String toCSML() {
        String csml = "";
        csml += "<csml:Region csml:ID=\"" + getId() + "\" csml:domainID=\"" + domain.getId() + "\">\n";
        csml += getLabelCSML();
        csml += getDescriptionCSML();
        csml += "<csml:AMatrix>\n";
        for (int m = 0; m < A.length; m++) {
            csml += "<matrixrow>\n";
            for (int n = 0; n < A[m].length; n++) {
                csml += "<cn>" + A[m][n] + "</cn>\n";
            }
            csml += "</matrixrow>\n";
        }
        csml += "</csml:AMatrix>\n";
        csml += "<csml:qVector>\n";
        for (int n = 0; n < q.length; n++) {
            csml += "<ci>" + q[n] + "</ci>\n";
        }
        csml += "</csml:qVector>\n";
        csml += "<csml:bVector>\n";
        for (int m = 0; m < b.length; m++) {
            csml += "<cn>" + b[m] + "</cn>\n";
        }
        csml += "</csml:bVector>\n";
        csml += "</csml:Region>\n";
        return csml;
    }
}
