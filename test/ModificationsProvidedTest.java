import static org.junit.Assert.*;

import org.junit.Test;

class ModificationsProvidedTest {

	@Test
	void testOmission() {

		IWords dict = new LListWords();
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
