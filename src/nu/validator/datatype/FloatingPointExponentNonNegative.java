/*
 * Copyright (c) 2007-2011 Mozilla Foundation
 *
 * Permission is hereby granted, free of charge, to any person obtaining a 
 * copy of this software and associated documentation files (the "Software"), 
 * to deal in the Software without restriction, including without limitation 
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, 
 * and/or sell copies of the Software, and to permit persons to whom the 
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in 
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR 
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, 
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL 
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER 
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING 
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER 
 * DEALINGS IN THE SOFTWARE.
 */

package nu.validator.datatype;

import org.relaxng.datatype.DatatypeException;

public final class FloatingPointExponentNonNegative extends AbstractDatatype {

    /**
     * The singleton instance.
     */
    public static final FloatingPointExponentNonNegative THE_INSTANCE = new FloatingPointExponentNonNegative();
    
    private enum State {
        AT_START, AT_START_MINUS_SEEN, IN_INTEGER_PART_DIGITS_SEEN, IN_INTEGER_PART_DIGITS_SEEN_ZERO, DOT_SEEN, DOT_SEEN_ZERO, E_SEEN, IN_DECIMAL_PART_DIGITS_SEEN, IN_DECIMAL_PART_DIGITS_SEEN_ZERO, IN_EXPONENT_SIGN_SEEN, IN_EXPONENT_DIGITS_SEEN
        
    }
    
    /**
     * 
     */
    private FloatingPointExponentNonNegative() {
        super();
    }

    @Override
    public void checkValid(CharSequence literal) throws DatatypeException {
        State state = State.AT_START;
        for (int i = 0; i < literal.length(); i++) {
            char c = literal.charAt(i);
            switch (state) {
                case AT_START:
                    if (c == '-') {
                        state = State.AT_START_MINUS_SEEN;
                        continue;
                    } else if (c == '.') {
                        state = State.DOT_SEEN;
                        continue;
                    } else if (isAsciiDigit(c)) {
                        state = State.IN_INTEGER_PART_DIGITS_SEEN;
                        continue;
                    } else {
                        throw newDatatypeException(i, "Expected a minus sign or a digit but saw ", c, " instead.");
                    }
                case AT_START_MINUS_SEEN:
                    if (c == '0') {
                        state = State.IN_INTEGER_PART_DIGITS_SEEN_ZERO;
                        continue;
                    } else {
                        throw newDatatypeException(i, "Expected a zero but saw ", c, " instead.");
                    }
                case IN_INTEGER_PART_DIGITS_SEEN:
                    if (c == '.') {
                        state = State.DOT_SEEN;
                        continue;
                    } else if (c == 'e' || c == 'E') {
                        state = State.E_SEEN;
                        continue;
                    } else if (isAsciiDigit(c)) {
                        continue;
                    } else {
                        throw newDatatypeException(i, "Expected a decimal point, \u201Ce\u201D, \u201CE\u201D or a digit but saw ", c, " instead.");
                    }
                case IN_INTEGER_PART_DIGITS_SEEN_ZERO:
                    if (c == '.') {
                        state = State.DOT_SEEN_ZERO;
                        continue;
                    } else if (c == 'e' || c == 'E') {
                        state = State.E_SEEN;
                        continue;
                    } else if (c == '0') {
                        continue;
                    } else {
                        throw newDatatypeException(i, "Expected a decimal point, \u201Ce\u201D, \u201CE\u201D or a zero but saw ", c, " instead.");
                    }
                case DOT_SEEN:
                    if (isAsciiDigit(c)) {
                        state = State.IN_DECIMAL_PART_DIGITS_SEEN;
                        continue;
                    } else {
                        throw newDatatypeException(i, "Expected a digit after the decimal point but saw ", c, " instead.");                        
                    }
                case DOT_SEEN_ZERO:
                    if (c == '0') {
                        state = State.IN_DECIMAL_PART_DIGITS_SEEN_ZERO;
                        continue;
                    } else {
                        throw newDatatypeException(i, "Expected a zero after the decimal point but saw ", c, " instead.");                        
                    }
                case IN_DECIMAL_PART_DIGITS_SEEN:
                    if (isAsciiDigit(c)) {
                        continue;
                    } else if (c == 'e' || c == 'E') {
                        state = State.E_SEEN;
                        continue;
                    } else {
                        throw newDatatypeException(i, "Expected \u201Ce\u201D, \u201CE\u201D or a digit but saw ", c, " instead.");                        
                    }
                case IN_DECIMAL_PART_DIGITS_SEEN_ZERO:
                    if (c == '0') {
                        continue;
                    } else if (c == 'e' || c == 'E') {
                        state = State.E_SEEN;
                        continue;
                    } else {
                        throw newDatatypeException(i, "Expected \u201Ce\u201D, \u201CE\u201D or a zero but saw ", c, " instead.");                        
                    }
                case E_SEEN:
                    if (c == '-' || c == '+') {
                        state = State.IN_EXPONENT_SIGN_SEEN;
                        continue;
                    } else if (isAsciiDigit(c)) {
                        state = State.IN_EXPONENT_DIGITS_SEEN;
                        continue;
                    } else {
                        throw newDatatypeException(i, "Expected a minus sign, a plus sign or a digit but saw ", c, " instead.");                                                
                    }
                case IN_EXPONENT_SIGN_SEEN:
                    if (isAsciiDigit(c)) {
                        state = State.IN_EXPONENT_DIGITS_SEEN;
                        continue;
                    } else {
                        throw newDatatypeException(i, "Expected a digit but saw ", c, " instead.");                                                                        
                    }
                case IN_EXPONENT_DIGITS_SEEN:
                    if (isAsciiDigit(c)) {
                        continue;
                    } else {
                        throw newDatatypeException(i, "Expected a digit but saw ", c, " instead.");                                                                        
                    }                    
            }
        }
        switch (state) {
            case IN_INTEGER_PART_DIGITS_SEEN:
            case IN_DECIMAL_PART_DIGITS_SEEN:
            case IN_INTEGER_PART_DIGITS_SEEN_ZERO:
            case IN_DECIMAL_PART_DIGITS_SEEN_ZERO:
            case IN_EXPONENT_DIGITS_SEEN:
                return;
            case AT_START:
                throw newDatatypeException("The empty string is not a valid non-negative floating point number.");
            case AT_START_MINUS_SEEN:
                throw newDatatypeException("The minus sign alone is not a valid non-negative floating point number.");
            case DOT_SEEN:
            case DOT_SEEN_ZERO:
                throw newDatatypeException("A non-negative floating point number must not end with the decimal point.");
            case E_SEEN:
                throw newDatatypeException("A non-negative floating point number must not end with the exponent \u201Ce\u201D.");
            case IN_EXPONENT_SIGN_SEEN:
                throw newDatatypeException("A non-negative floating point number must not end with only a sign in the exponent.");                
        }
    }

    @Override
    public String getName() {
        return "non-negative floating point number";
    }

}
