import static org.junit.Assert.*;

import org.junit.Test;

public class ModificationsTest {

    @Test
    public void testOmission() {

        IWords dict = new HTableWords();
        try {
            dict.addWord("cats");
            dict.addWord("like");
            dict.addWord("on");
            dict.addWord("of");
            dict.addWord("to");
            dict.addWord("play");
        } catch (WException e) {
            fail("Error with linked list implementation");
        }
        IWords sugg = SpellChecker.suggestions("catts", dict);
        assertTrue(sugg.wordExists("cats"));
    }
}
