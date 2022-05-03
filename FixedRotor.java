package enigma;

import static enigma.EnigmaException.*;

/** Class that represents a rotor that has no ratchet and does not advance.
 *  @author Nithish Rajan
 */
class FixedRotor extends Rotor {

    /** A non-moving rotor named NAME whose permutation at the 0 setting
     * is given by PERM. */
    FixedRotor(String name, Permutation perm) {
        super(name, perm);
        _permutation = perm;
        _name = name;
    }

    /** Permutation of the Fixed Rotor. */
    private final Permutation _permutation;

    /** Name of the Fixed Rotor. */
    private final String _name;
}
