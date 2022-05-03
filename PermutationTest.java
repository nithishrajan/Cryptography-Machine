package enigma;

import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.Timeout;
import static org.junit.Assert.*;

import static enigma.TestUtils.*;

/** The suite of all JUnit tests for the Permutation class.
 *  @author Nithish Rajan
 */
public class PermutationTest {

    /** Testing time limit. */
    @Rule
    public Timeout globalTimeout = Timeout.seconds(5);

    /* ***** TESTING UTILITIES ***** */

    private Permutation perm;
    private String alpha = UPPER_STRING;

    /** Check that perm has an alphabet whose size is that of
     *  FROMALPHA and TOALPHA and that maps each character of
     *  FROMALPHA to the corresponding character of FROMALPHA, and
     *  vice-versa. TESTID is used in error messages. */
    private void checkPerm(String testId,
                           String fromAlpha, String toAlpha) {
        int N = fromAlpha.length();
        assertEquals(testId + " (wrong length)", N, perm.size());
        for (int i = 0; i < N; i += 1) {
            char c = fromAlpha.charAt(i), e = toAlpha.charAt(i);
            assertEquals(msg(testId, "wrong translation of '%c'", c),
                         e, perm.permute(c));
            assertEquals(msg(testId, "wrong inverse of '%c'", e),
                         c, perm.invert(e));
            int ci = alpha.indexOf(c), ei = alpha.indexOf(e);
            assertEquals(msg(testId, "wrong translation of %d", ci),
                         ei, perm.permute(ci));
            assertEquals(msg(testId, "wrong inverse of %d", ei),
                         ci, perm.invert(ei));
        }
    }

    /* ***** TESTS ***** */

    @Test
    public void checkIdTransform() {
        perm = new Permutation("", UPPER);
        checkPerm("identity", UPPER_STRING, UPPER_STRING);
    }
    @Test
    public void checkPerm1() {
        perm = new Permutation("(BACD)", UPPER);
        checkPerm("Cycle Test", UPPER_STRING, "CADBEFGHIJKLMNOPQRSTUVWXYZ");
    }
    @Test
    public void checkEmptyCycle() {
        perm = new Permutation("(A)", UPPER);
        checkPerm("Empty", UPPER_STRING, UPPER_STRING);
        assertFalse(perm.derangement());
    }

    @Test(expected = EnigmaException.class)
    public void checkIdTransform2() {
        perm = new Permutation("()", UPPER);
        checkPerm("identity", UPPER_STRING, UPPER_STRING);
        assertFalse(perm.derangement());
    }
    @Test
    public void checkPerm2() {
        perm = new Permutation("(ABCDEFGHIJKLMNOPQRSTUVWXYZ)", UPPER);
        checkPerm("Cycle Test 2", UPPER_STRING, "BCDEFGHIJKLMNOPQRSTUVWXYZA");
        assertTrue(perm.derangement());
    }
    @Test
    public void checkPermTests() {
        perm = new Permutation("(SADBER)", UPPER);
        assertEquals('D', perm.permute('A'));
        assertEquals('R', perm.permute('E'));
        assertEquals(3, perm.permute(0));
        assertEquals(4, perm.permute(1));
    }
    @Test
    public void testInvertChar() {
        Alphabet test = new Alphabet("ABCD");
        perm = new Permutation("(BACD)", test);
        assertEquals('B', perm.invert('A'));
        assertEquals('D', perm.invert('B'));
        assertEquals(0, perm.invert(2));
        assertEquals(2, perm.invert(3));
    }

    @Test(expected = EnigmaException.class)
    public void testNotInAlphabet() {
        perm = new Permutation("(BACD)", new Alphabet("ABCD"));
        perm.invert('F');
        perm.invert('%');
        perm.invert('#');
    }
    @Test(expected = EnigmaException.class)
    public void testNotInAlphabet2() {
        perm = new Permutation("(BCAD#@*!$^&)", UPPER);
        perm.invert('F');
        perm.invert('%');
        perm.invert('#');
        perm.permute('D');
    }
    @Test(expected = EnigmaException.class)
    public void testRepeats() {
        perm = new Permutation("(BCADB)", UPPER);
        perm.invert('B');
    }

    @Test
    public void testSize() {
        Alphabet testAlphabet = new Alphabet("ABCD");
        assertEquals(4, testAlphabet.size());
        assertTrue(testAlphabet.contains('A'));
        assertFalse(testAlphabet.contains('V'));
        assertEquals('A', testAlphabet.toChar(0));
        assertEquals('D', testAlphabet.toChar(3));
    }

    @Test(expected = EnigmaException.class)
    public void testNumNotInAlphabet() {
        perm = new Permutation("(BACD)", new Alphabet("abcdefyuij123"));
        perm.invert('1');
        perm.invert('2');
        perm.invert('3');
    }

    @Test(expected = EnigmaException.class)
    public void alphaTest() {
        String testString = "abcdefyuij123";
        Alphabet testAlphabet = new Alphabet("abcdefyuij123");
        assertEquals(13, testAlphabet.size());
        for (int x = 0; x < testString.length(); x++) {
            char test = testString.charAt(x);
            assertEquals(test, testAlphabet.toChar(x));
            assertEquals(x, testAlphabet.toInt(test));
            assertTrue(testAlphabet.contains(test));
        }
    }

    @Test
    public void derangementTest() {
        perm = new Permutation("(BACD)", UPPER);
        assertFalse(perm.derangement());
    }

    @Test
    public void permutationTest1() {
        perm = new Permutation("(WORDLE) (IS) (FUN)", UPPER);
        assertEquals(26, perm.size());

        assertEquals(0, perm.permute(0));
        assertEquals(22, perm.permute(4));
        assertEquals(4, perm.permute(11));

        assertEquals('D', perm.permute('R'));
        assertEquals('I', perm.permute('S'));
        assertEquals('G', perm.permute('G'));

        assertEquals(13, perm.invert(5));
        assertEquals(7, perm.invert(7));
        assertEquals(9, perm.invert(9));

        assertEquals('O', perm.invert('R'));
        assertEquals('U', perm.invert('N'));
        assertEquals('R', perm.invert('D'));

        assertFalse(perm.derangement());
    }

    @Test
    public void permutationTest2() {
        perm = new Permutation("(TEARS) (BOING) (LUCKY)", UPPER);
        assertEquals(26, perm.size());

        assertEquals(17, perm.permute(0));
        assertEquals(0, perm.permute(4));
        assertEquals(20, perm.permute(11));

        assertEquals('S', perm.permute('R'));
        assertEquals('T', perm.permute('S'));
        assertEquals('B', perm.permute('G'));

        assertEquals(5, perm.invert(5));
        assertEquals(7, perm.invert(7));
        assertEquals(9, perm.invert(9));

        assertEquals('A', perm.invert('R'));
        assertEquals('I', perm.invert('N'));
        assertEquals('D', perm.invert('D'));

        assertFalse(perm.derangement());
    }

}
