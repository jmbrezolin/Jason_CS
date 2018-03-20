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

package edu.geog.geocog.csml.model;

/**
 * 
 * @author Ben
 */
public class CSMLQualityDimension extends CSMLElement {

    // attributes
    private boolean circular = false;

    // elements
    private Scale scale = new Scale();
    private CSML range = new CSML();
    private Units units = new Units();

    public CSMLQualityDimension(String id, CSMLModel model) {
        super(id, model);
    }

    public boolean isCircular() { return circular; }

    public void setCircular(boolean circular) {
        this.circular = circular;
    }

    public void setScale(String s) {
        scale = new Scale(s);
    }

    public void setScale(int s) {
        scale = new Scale(s);
    }

    public int getScale() {
        return scale.getType();
    }

    public void setRange(double min, double max) {
        range = new CSML(min, max);
    }

    public void setRange(CSML r) {
        range = r;
    }

    public CSML getRange() {
        return range;
    }

    public void setUnits(String u) {
        units = new Units(u);
    }

    public void setUnits(Units u) {
        units = new Units(u.getUnitsString());
    }

    public Units getUnits() {
        return units;
    }

    public String toCSML() {
        String csml = "";
        csml += "	<csml:QualityDimension csml:ID=\""+getId()+"\" csml:circular=\""+circular+"\">\n";
        csml += getLabelCSML();
        csml += getDescriptionCSML();
        csml += scale.toCSML();
        if (!range.isDefaultRange())
            csml += range.toCSML();
        if (!units.getUnitsString().equals(""))
            csml += units.toCSML();
        csml += "	</csml:QualityDimension>\n";
        return csml;
    }

}
