package enigma;

/** An alphabet of encodable characters.  Provides a mapping from characters
 *  to and from indices into the alphabet.
 *  @author Nithish Rajan
 */
class Alphabet {

    /** A new alphabet containing CHARS. The K-th character has index
     *  K (numbering from 0). No character may be duplicated. */
    Alphabet(String chars) {
        _char = chars;
        if (_char.length() == 0) {
            throw EnigmaException.error("Empty Alphabet Inputted");
        }
        for (int x = 0; x < _char.length(); x++) {
            for (int y = x + 1; y < _char.length(); y++) {
                if (_char.charAt(x) == _char.charAt(y)) {
                    throw EnigmaException.error("Duplicate Detected");
                }
            }
        }
    }

    /** A default alphabet of all upper-case characters. */
    Alphabet() {
        this("ABCDEFGHIJKLMNOPQRSTUVWXYZ");
    }

    /** Returns the size of the alphabet. */
    int size() {
        return _char.length();
    }

    /** Returns true if CH is in this alphabet. */
    boolean contains(char ch) {
        for (int x = 0; x < size(); x++) {
            if (_char.charAt(x) == ch) {
                return true;
            }
        }
        return false;
    }

    /** Returns character number INDEX in the alphabet, where
     *  0 <= INDEX < size(). */
    char toChar(int index) {
        try {
            return _char.charAt(index);
        } catch (IndexOutOfBoundsException e) {
            throw EnigmaException.error("Index is too large for the alphabet");
        }
    }

    /** Returns the index of character CH which must be in
     *  the alphabet. This is the inverse of toChar(). */
    int toInt(char ch) {
        if (contains(ch)) {
            return _char.indexOf(ch);
        } else {
            throw EnigmaException.error("Character not in Alphabet.");
        }
    }


    /** Characters of the alphabet. */
    private String _char;
}
