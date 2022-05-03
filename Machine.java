package enigma;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Collection;

import static enigma.EnigmaException.*;

/** Class that represents a complete enigma machine.
 *  @author Nithish Rajan
 */
class Machine {

    /** A new Enigma machine with alphabet ALPHA, 1 < NUMROTORS rotor slots,
     *  and 0 <= PAWLS < NUMROTORS pawls.  ALLROTORS contains all the
     *  available rotors. */
    Machine(Alphabet alpha, int numRotors, int pawls,
            Collection<Rotor> allRotors) {
        _alphabet = alpha;
        _pawls = pawls;
        if (numRotors <= 1) {
            throw EnigmaException.error("Machine lacks enough rotor slots.");
        }
        _numRotors = numRotors;
        if (_pawls > numRotors || _pawls <= 0) {
            throw EnigmaException.error("Machine has illegal number of pawls.");
        }
        _availablerotorList = new ArrayList<>(allRotors);

    }

    /** Return the number of rotor slots I have. */
    int numRotors() {
        return _numRotors;
    }

    /** Return the number pawls (and thus rotating rotors) I have. */
    int numPawls() {
        return _pawls;
    }

    /** Return Rotor #K, where Rotor #0 is the reflector, and Rotor
     *  #(numRotors()-1) is the fast Rotor.  Modifying this Rotor has
     *  undefined results. */
    Rotor getRotor(int k) {
        return _rotor.get(k);
    }

    Alphabet alphabet() {
        return _alphabet;
    }

    /** Return Rotors provided into Machine. */
    ArrayList<Rotor> rotors() {
        return _rotor;
    }

    /** Set my rotor slots to the rotors named ROTORS from my set of
     *  available rotors (ROTORS[0] names the reflector).
     *  Initially, all rotors are set at their 0 setting. */
    void insertRotors(String[] rotors) {
        _rotor = new ArrayList<Rotor>();
        ArrayList<String> insertList = new ArrayList<String>();
        for (Rotor X : _availablerotorList) {
            insertList.add(X.name());
        }
        for (String V : rotors) {
            int index = insertList.indexOf(V);
            Rotor selected = _availablerotorList.get(index);
            if (selected == null) {
                throw EnigmaException.error("Bad Rotor Given");
            }
            if (_rotor.contains(selected)) {
                throw EnigmaException.error("Duplicate Found");
            }
            selected.set(0);
            _rotor.add(selected);
        }
        if (_rotor.size() > numRotors()) {
            throw EnigmaException.error("More slots than rotors selected");
        }
        if (!_rotor.get(0).reflecting()) {
            throw EnigmaException.error("First Rotor not Reflector.");
        }
    }

    /** Set my rotors according to SETTING, which must be a string of
     *  numRotors()-1 characters in my alphabet. The first letter refers
     *  to the leftmost rotor setting (not counting the reflector).  */
    void setRotors(String setting) {
        if (setting.length() > numRotors() - 1) {
            throw EnigmaException.error("Setting Given was too long.");
        }
        for (int x = 0; x < numRotors() - 1; x++) {
            char rotorCharacter = setting.charAt(x);
            if (!alphabet().contains(rotorCharacter)) {
                throw EnigmaException.error("Character not in alphabet.");
            }
            _rotor.get(x + 1).set(rotorCharacter);
        }
    }

    /** Return the current plugboard's permutation. */
    Permutation plugboard() {
        return _plugboard;
    }

    /** Set the plugboard to PLUGBOARD. */
    void setPlugboard(Permutation plugboard) {
        _plugboard = plugboard;
    }

    /** Returns the result of converting the input character C (as an
     *  index in the range 0..alphabet size - 1), after first advancing
     *  the machine. */
    int convert(int c) {
        advanceRotors();
        if (Main.verbose()) {
            System.err.printf("[");
            for (int r = 1; r < numRotors(); r += 1) {
                System.err.printf("%c",
                        alphabet().toChar(getRotor(r).setting()));
            }
            System.err.printf("] %c -> ", alphabet().toChar(c));
        }
        c = plugboard().permute(c);
        if (Main.verbose()) {
            System.err.printf("%c -> ", alphabet().toChar(c));
        }
        c = applyRotors(c);
        c = plugboard().permute(c);
        if (Main.verbose()) {
            System.err.printf("%c%n", alphabet().toChar(c));
        }
        return c;
    }

    /** Advance all rotors to their next position. */
    private void advanceRotors() {
        ArrayList<Rotor> rotorOrder = new ArrayList<Rotor>(_rotor);
        Collections.reverse(rotorOrder);
        ArrayList<Rotor> advancedAlready = new ArrayList<Rotor>();
        ArrayList<Boolean> atNotch = new ArrayList<Boolean>();
        for (int y = 0; y < rotorOrder.size(); y++) {
            if (rotorOrder.get(y).atNotch()) {
                atNotch.add(true);
            } else {
                atNotch.add(false);
            }
        }
        for (int x = 0; x < rotorOrder.size(); x++) {
            if (rotorOrder.get(x) == rotorOrder.get(rotorOrder.size() - 1)) {
                break;
            }
            if (advancedAlready.contains(rotorOrder.get(x))) {
                if (rotorOrder.get(x + 1).rotates()) {
                    if (atNotch.get(x)) {
                        rotorOrder.get(x + 1).advance();
                        advancedAlready.add(rotorOrder.get(x + 1));
                    }
                }
            } else if (rotorOrder.get(x).atNotch()) {
                if (rotorOrder.get(x + 1).rotates()) {
                    rotorOrder.get(x).advance();
                    rotorOrder.get(x + 1).advance();
                    advancedAlready.add(rotorOrder.get(x + 1));
                    advancedAlready.add(rotorOrder.get(x));
                }
            }
        }
        if (!advancedAlready.contains(rotorOrder.get(0))) {
            rotorOrder.get(0).advance();
        }
    }

    /** Return the result of applying the rotors to the character C (as an
     *  index in the range 0..alphabet size - 1). */
    private int applyRotors(int c) {
        if (!_alphabet.contains(_alphabet.toChar(c))) {
            throw EnigmaException.error("Character not in alphabet");
        }
        ArrayList<Rotor> frontPermutes = new ArrayList<Rotor>(_rotor);
        Collections.reverse(frontPermutes);
        ArrayList<Rotor> backPermutes = new ArrayList<Rotor>(_rotor);
        backPermutes.remove(0);
        for (Rotor V: frontPermutes) {
            c = V.convertForward(c);
        }
        for (Rotor V: backPermutes) {
            c = V.convertBackward(c);
        }
        return c;
    }

    /** Returns the encoding/decoding of MSG, updating the state of
     *  the rotors accordingly. */
    String convert(String msg) {
        if (msg == null) {
            throw EnigmaException.error("Bad String Given");
        } else if (msg == "") {
            return msg;
        } else {
            int intAdded = convert(_alphabet.toInt(msg.charAt(0)));
            char charAdded = _alphabet.toChar(intAdded);
            return (charAdded + convert(msg.substring(1)));
        }
    }

    /** Takes String RING and assigns to Rotor. */
    void ringstellung(String ring) {
        for (int x = 1; x < numRotors(); x++) {
            Rotor dRotor = _rotor.get(x);
            String dString = ring.substring(x - 1, x);
            dRotor.setRingSetting(dString);
        }
    }

    /** Common alphabet of my rotors. */
    private final Alphabet _alphabet;

    /** Number of Rotor slots machine has. */
    private int _numRotors;

    /** Number of pawls machine has. */
    private int _pawls;

    /** All available Rotors. */
    private ArrayList<Rotor> _availablerotorList;

    /** List of Selected Rotors. */
    private ArrayList<Rotor> _rotor;

    /** Plugboard represented by the Permutation Class. */
    private Permutation _plugboard;


}

