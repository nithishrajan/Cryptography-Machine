package enigma;

import static enigma.EnigmaException.*;

/** Represents a permutation of a range of integers starting at 0 corresponding
 *  to the characters of an alphabet.
 *  @author Nithish Rajan
 */
class Permutation {

    /** Set this Permutation to that specified by CYCLES, a string in the
     *  form "(cccc) (cc) ..." where the c's are characters in ALPHABET, which
     *  is interpreted as a permutation in cycle notation.  Characters in the
     *  alphabet that are not included in any cycle map to themselves.
     *  Whitespace is ignored. */
    Permutation(String cycles, Alphabet alphabet) {
        _alphabet = alphabet;
        if (cycles == "()") {
            throw EnigmaException.error("Bad Cycle Given");
        }
        String testString = cycles.trim();
        testString = testString.replace("(", "");
        testString = testString.replace(")", "");
        for (int x = 0; x < testString.length(); x++) {
            for (int y = x + 1; y < testString.length(); y++) {
                if (testString.charAt(x) == testString.charAt(y)) {
                    if (testString.charAt(x) != ' ') {
                        throw EnigmaException.error("Duplicate Detected");
                    }
                }
            }
        }
        _cycles = testString.split(" ");
        for (int x = 0; x < _cycles.length; x++) {
            for (int y = 0; y < _cycles[x].length(); y++) {
                if (!alphabet().contains(_cycles[x].charAt(y))) {
                    throw EnigmaException.error("Character not in Alphabet");
                }
            }
        }
    }

    /** Add the cycle c0->c1->...->cm->c0 to the permutation, where CYCLE is
     *  c0c1...cm. */
    private void addCycle(String cycle) {
        if (cycle.length() == 0) {
            return;
        }
        String[] addCycle = new String[_cycles.length + 1];
        for (int x = 0; x < _cycles.length; x++) {
            addCycle[x] = _cycles[x];
        }
        addCycle[_cycles.length + 1] = cycle;
        _cycles = addCycle;
    }

    /** Return the value of P modulo the size of this permutation. */
    final int wrap(int p) {
        int r = p % size();
        if (r < 0) {
            r += size();
        }
        return r;
    }

    /** Returns the size of the alphabet I permute. */
    int size() {
        return _alphabet.size();
    }

    /** Return the result of applying this permutation to P modulo the
     *  alphabet size. */
    int permute(int p) {
        char charAtp = _alphabet.toChar(wrap(p));
        for (int x = 0; x < _cycles.length; x++) {
            for (int y = 0; y < _cycles[x].length(); y++) {
                if (_cycles[x].charAt(y) == charAtp) {
                    if (y == _cycles[x].length() - 1) {
                        char permedChar = _cycles[x].charAt(0);
                        p = _alphabet.toInt(permedChar);
                        return p;
                    } else {
                        char permedChar = _cycles[x].charAt(y + 1);
                        p = _alphabet.toInt(permedChar);
                        return p;
                    }

                }
            }
        }
        return p;
    }

    /** Return the result of applying the inverse of this permutation
     *  to  C modulo the alphabet size. */
    int invert(int c) {
        char charAtp = _alphabet.toChar(wrap(c));
        for (int x = 0; x < _cycles.length; x++) {
            for (int y = 0; y < _cycles[x].length(); y++) {
                if (_cycles[x].charAt(y) == charAtp) {
                    if (y == 0) {
                        int cycleLength = _cycles[x].length();
                        char permedChar = _cycles[x].charAt((cycleLength - 1));
                        c = _alphabet.toInt(permedChar);
                        return c;
                    }
                    char permedChar = _cycles[x].charAt(y - 1);
                    c = _alphabet.toInt(permedChar);
                    return c;
                }
            }
        }
        return c;
    }


    /** Return the result of applying this permutation to the index of P
     *  in ALPHABET, and converting the result to a character of ALPHABET. */
    char permute(char p) {
        int index = _alphabet.toInt(p);
        return _alphabet.toChar(permute(index));
    }

    /** Return the result of applying the inverse of this permutation to C. */
    char invert(char c) {
        int index = _alphabet.toInt(c);
        return _alphabet.toChar(invert(index));
    }

    /** Return the alphabet used to initialize this Permutation. */
    Alphabet alphabet() {
        return _alphabet;
    }

    /** Return true iff this permutation is a derangement (i.e., a
     *  permutation for which no value maps to itself). */
    boolean derangement() {
        int totalLength = size();
        int count = 0;
        for (int x = 0; x < _cycles.length; x++) {
            for (int y = 0; y < _cycles[x].length(); y++) {
                count += 1;
            }
        }
        if (count == totalLength) {
            return true;
        }
        return false;
    }

    /** Alphabet of this permutation. */
    private Alphabet _alphabet;
    /** Cycles of this Permutation. */
    private String[] _cycles;

}
