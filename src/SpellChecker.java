import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;


/**
 * Main class for the Spell-Checker program
 */
public class SpellChecker {
    /**
     * Suggests word modifications for a given word and a given word dictionary.
     */
    static public IWords suggestions(String word, IWords dict) {
        String[] letterList = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"};
        HTableWords modTable = new HTableWords();

        substitution(word, dict, modTable, letterList);
        omission(word, dict, modTable);
        insertion(word, dict, modTable, letterList);
        reverse(word, dict, modTable);

        Iterator modItr = modTable.allWords();
        System.out.print(word + "->");
        while(modItr.hasNext()) {
            Object modWord = modItr.next();
            System.out.print(" " +modWord);
        }
        System.out.println(" ");
        return null;
    }

    public static void substitution(String word, IWords table, HTableWords modTable, String[] letterList) {
        for (int i = 0; i < word.length(); i++) {
            for (String aLetterList : letterList) {
                StringBuffer wordCheck = new StringBuffer(word);

                wordCheck.replace(i, i + 1, aLetterList);
                try {
                    if (table.wordExists(wordCheck.toString())) {
                        modTable.addWord(wordCheck.toString());
                    }
                } catch (WException e) {
                    System.out.println(e.getMessage() + word);
                }
            }
        }
    }

    public static void omission(String word, IWords table, HTableWords modTable) {
        for (int i = 0; i < word.length(); i++) {
            StringBuffer wordCheck = new StringBuffer(word);
            wordCheck.deleteCharAt(i);
            try {
                if (table.wordExists(wordCheck.toString())) {
                    modTable.addWord(wordCheck.toString());
                }
            } catch (WException e) {
                System.out.println(e.getMessage() + wordCheck);
            }
        }
    }

    public static void insertion(String word, IWords table, HTableWords modTable, String[] letterList) {
        for (int i = 0; i < word.length() + 1; i++) {
            for (int j = 0; j < letterList.length; j++) {
                StringBuffer wordCheck = new StringBuffer(word);
                wordCheck.insert(i, letterList[j]);

                if (table.wordExists(wordCheck.toString())) {
                    try {
                        modTable.addWord(wordCheck.toString());
                    } catch (WException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public static void reverse(String word, IWords dict, HTableWords modWords) {
        for (int i = 2; i < word.length() + 1; i++) {
            StringBuffer wordCheck = new StringBuffer(word);
            String temp = wordCheck.substring(i - 2, i);
            StringBuffer tempReverse = new StringBuffer(temp);

            temp = tempReverse.reverse().toString();
            wordCheck.replace(i - 2, i, temp);
            if (dict.wordExists(wordCheck.toString())) {
                try {
                    modWords.addWord(wordCheck.toString());
                } catch (WException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Main method for the Spell-Checker program. The program takes two input
     * filenames in the command line: the word dictionary file and the file
     * containing the words to spell-check. .
     */
    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();
        if (args.length != 2) {
            System.err.println("Usage: SpellChecker dictionaryFile.txt inputFile.txt ");
            System.exit(1);
        }
        HTableWords table = new HTableWords(0.1f);

        LListWords wordList = new LListWords();
        try {
            BufferedInputStream dict, file;
            dict = new BufferedInputStream(new FileInputStream(args[0]));

            FileWordRead readWords = new FileWordRead(dict);

            while (readWords.hasNextWord()) {
                String nextWord = readWords.nextWord();
                try {
                    table.addWord(nextWord);
                } catch (WException e) {
                    System.out.println(e.getMessage() + nextWord);
                }
            }
            System.out.println(table.averageProbes());

            dict.close();


            file = new BufferedInputStream(new FileInputStream(args[1]));

            FileWordRead readWords2 = new FileWordRead(file);

            while (readWords2.hasNextWord()) {
                String word = readWords2.nextWord();

                if (!table.wordExists(word)) {
                    suggestions(word, table);
                }
            }
            file.close();

        } catch (IOException e) { // catch exceptions caused by file input/output errors
            System.err.println("Missing input file, check your filenames");
            System.exit(1);
        }
        System.out.println(System.currentTimeMillis() - startTime + "ms");
    }


}
