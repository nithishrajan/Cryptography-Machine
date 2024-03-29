package enigma;

import static enigma.EnigmaException.*;

/** Superclass that represents a rotor in the enigma machine.
 *  @author Nithish Rajan
 */
class Rotor {

    /** A rotor named NAME whose permutation is given by PERM. */
    Rotor(String name, Permutation perm) {
        _name = name;
        _permutation = perm;
        _setting = 0;
        _ringSetting = "";
    }

    /** Return my name. */
    String name() {
        return _name;
    }

    /** Return my alphabet. */
    Alphabet alphabet() {
        return _permutation.alphabet();
    }

    /** Return my permutation. */
    Permutation permutation() {
        return _permutation;
    }

    /** Return the size of my alphabet. */
    int size() {
        return _permutation.size();
    }

    /** Return true iff I have a ratchet and can move. */
    boolean rotates() {
        return false;
    }

    /** Return true iff I reflect. */
    boolean reflecting() {
        return false;
    }

    /** Return my current setting. */
    int setting() {
        return _setting;
    }

    /** Set setting() to POSN.  */
    void set(int posn) {
        _setting = posn % alphabet().size();
    }

    /** Set setting() to character CPOSN. */
    void set(char cposn) {
        _setting = (alphabet().toInt(cposn)) % alphabet().size();
    }

    /** Return the conversion of P (an integer in the range 0..size() - 1)
     *  according to my permutation. */
    int convertForward(int p) {
        int d = _setting - _intRing;
        int comingIn = permutation().wrap(p + d);
        int permutation = permutation().permute(comingIn) - d;
        int result = permutation().wrap(permutation);
        if (Main.verbose()) {
            System.err.printf("%c -> ", alphabet().toChar(result));
        }
        return result;
    }

    /** Return the conversion of E (an integer in the range 0..size() - 1)
     *  according to the inverse of my permutation. */
    int convertBackward(int e) {
        int d = _setting - _intRing;
        int comingIn = permutation().wrap(e + d);
        int inversePermutation = permutation().invert(comingIn) - d;
        int result = permutation().wrap(inversePermutation);
        if (Main.verbose()) {
            System.err.printf("%c -> ", alphabet().toChar(result));
        }
        return result;
    }

    /** Returns the positions of the notches, as a string giving the letters
     *  on the ring at which they occur. */
    String notches() {
        return "";
    }

    /** Returns true iff I am positioned to allow the rotor to my left
     *  to advance. */
    boolean atNotch() {
        return false;
    }

    /** Advance me one position, if possible. By default, does nothing. */
    void advance() {
    }

    /** Set Ring Setting S to passed in String. */
    void setRingSetting(String S) {
        _ringSetting = S;
        _intRing = alphabet().toInt(_ringSetting.charAt(0));
    }

    @Override
    public String toString() {
        return "Rotor " + _name;
    }

    /** My name. */
    private final String _name;

    /** The permutation implemented by this rotor in its 0 position. */
    private Permutation _permutation;

    /** The setting given for this Rotor. */
    private int _setting;

    /** The ring setting for each rotor. */
    private String _ringSetting;

    /** The int-ring setting for each rotor. */
    private int _intRing;

}
