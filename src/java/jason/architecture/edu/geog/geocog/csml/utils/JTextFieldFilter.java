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

package edu.geog.geocog.csml.utils;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;

public class JTextFieldFilter extends javax.swing.text.PlainDocument {

    public static final String LOWERCASE  =
            "abcdefghijklmnopqrstuvwxyz";
    public static final String UPPERCASE  =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    public static final String ALPHA   =
            LOWERCASE + UPPERCASE;
    public static final String NUMERIC =
            "0123456789";
    public static final String FLOAT =
            NUMERIC + ".Ee";
    public static final String ALPHA_NUMERIC =
            ALPHA + NUMERIC;

    protected String acceptedChars = null;
    protected boolean negativeAccepted = false;

    public JTextFieldFilter() {
        this(ALPHA_NUMERIC);
    }
    public JTextFieldFilter(String acceptedchars) {
        acceptedChars = acceptedchars;
    }

    public void setNegativeAccepted(boolean negativeaccepted) {
        if (acceptedChars.equals(NUMERIC) ||
                acceptedChars.equals(FLOAT) ||
                acceptedChars.equals(ALPHA_NUMERIC)){
            negativeAccepted = negativeaccepted;
            acceptedChars += "-";
        }
    }

    @Override
    @SuppressWarnings("static-access")
    public void insertString
            (int offset, String  str, AttributeSet attr)
            throws BadLocationException {
        if (str == null) return;

        if (acceptedChars.equals(UPPERCASE))
            str = str.toUpperCase();
        else if (acceptedChars.equals(LOWERCASE))
            str = str.toLowerCase();

        for (int i=0; i < str.length(); i++) {
            if (acceptedChars.indexOf(str.valueOf(str.charAt(i))) == -1)
                return;
        }

        if (acceptedChars.equals(FLOAT) ||
                (acceptedChars.equals(FLOAT + "-") && negativeAccepted)) {
            if (str.indexOf(".") != -1) {
                if (getText(0, getLength()).indexOf(".") != -1) {
                    return;
                }
            }
        }

        if (negativeAccepted && str.indexOf("-") != -1) {
            if (str.indexOf("-") != 0 || offset != 0 ) {
                return;
            }
        }

        super.insertString(offset, str, attr);
    }
}


