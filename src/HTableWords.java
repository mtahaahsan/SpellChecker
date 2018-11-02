import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

public class HTableWords implements IHashing, IWords, IMonitor {
    private int maxProbes;
    private String[] table;
    private float maxLF;
    private ArrayList<String> wordsTable;
    private int probes;
    private int count;
    private int operations;


    public HTableWords() {
        this(0.5F);
    }

    public HTableWords(float maxLF) {
        this.maxLF = maxLF;
        maxProbes = 0;
        count = 0;
        table = new String[7];
        wordsTable = new ArrayList<>();
        probes = 0;
        operations = 0;

    }

    /**
     * @param input
     * @return The nextPrime method is taken from https://stackoverflow.com/a/47408893/7331819 by user kvk30
     */
    private int nextPrime(int input) {
        int counter;
        input++;
        while (true) {
            counter = 0;
            for (int i = 2; i <= Math.sqrt(input); i++) {
                if (input % i == 0) counter++;
            }
            if (counter == 0)
                return input;
            else {
                input++;
                continue;
            }
        }
    }

    /**
     * Takes a string, converts it to an ascii value, mods it by the table size, and returns it as the index
     *
     * @param s
     * @return Index that the word needs to go in
     */
    @Override
    public int giveCode(String s) {
        int value = 0;
        for (int i = 0; i < s.length(); i++) {
            value += value * 33 + s.charAt(i);     /* Polynomial Accumulation Function */
        }
        return Math.abs(value % table.length);     /* Makes sure there are no negative values */
    }

    /**
     * This method stores the current HashTable into a temporary ArrayList, re-instantiates the size of the table,
     * and then copies back the temporary ArrayList into the HashTable
     **/
    private void reTable() {
        ArrayList<String> tempAL = (ArrayList<String>) wordsTable.clone();
        wordsTable = new ArrayList<>();
        int newTableSize = nextPrime(table.length * 2);
        table = new String[newTableSize];
        count = 0;

        for (String word : tempAL) {
            try {
                addWord(word);
            } catch (WException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Compares the current load factor the max load factor, if it is, then call the reTable method
     **/
    private void checkTable() {
        if (loadFactor() > maxLF) {
            reTable();
        }
    }

    /**
     * Returns the max load-factor
     **/
    @Override
    public float maxLoadFactor() {
        return maxLF;
    }

    /**
     * calculate the current load factor
     **/
    @Override
    public float loadFactor() {
        return ((float) count / (float) table.length);
    }

    /**
     * returns the average number of times each word collided
     **/
    @Override
    public float averageProbes() { return ((float) probes / (float) operations); }

    /**
     * Adds a word into the HashTable, and calls the method that checks if the table needs to be resized
     **/
    @Override
    public void addWord(String word) throws WException {
        operations++;
        probes++;
        if (wordExists(word)) {
            throw new WException("This word already exists: ");
        } else {
            int code = giveCode(word);      /* Gets the index value of the word */
            if (table[code] == null) {      /* Checks if that index is empty */
                table[code] = word;
                wordsTable.add(word);
                count++;
            } else {
                int secondCode = doubleHash(code);   /* This calls the second hash function */
                table[secondCode] = word;
                wordsTable.add(word);
                count++;
            }

            checkTable();

        }
    }

    /**
     * This method handles collisions using polynomial accumulation
     *
     * @return An index that has an empty spot
     * @input The code that caused a collision
     */
    private int doubleHash(int oldCode) {
        int currentProbes = 0;
        int secondValue = oldCode;

        for (int i = 2; i < table.length; i++) {
            secondValue = (oldCode + i * (7 - (oldCode % 7))) % table.length;     /* Double Hash of Polynomial Accumulation */
            probes++;
            currentProbes++;
            if (table[secondValue] == null)
            { if (currentProbes > maxProbes)
                    maxProbes = currentProbes;    /* Checks and reassigns the max number of probes per word */
            break;}
        }

        return secondValue;
    }

    /**
     * Deletes a word using reverse hashing
     **/
    @Override
    public void delWord(String word) throws WException {
        operations++;
        probes++;
        int code = giveCode(word);
        if (table[code] != null && table[code].equals(word)) {
            table[code] = null;
            count--;
            wordsTable.remove(word);
            return;
        }
        else {
            for (int i = 2; i < maxProbes + 2; i++) {
                int secondCode = (code + i * (7 - (code % 7))) % table.length;
                if (table[secondCode] != null && table[secondCode].equals(word)) {
                    table[secondCode] = null;
                    count--;
                    wordsTable.remove(word);
                    probes++;
                    return;
                }
            }
        }
        throw new WException("Word doesn't exist");
    }

    /**
     * Checks if the word exists using reverse hashing
     **/
    @Override
    public boolean wordExists(String word) {
        int code = giveCode(word);
        if (table[code] != null && table[code].equals(word)) {
            operations++;
            return true;
        } else {
            for (int i = 2; i < maxProbes + 2; i++) {
                int secondCode = (code + i * (7 - (code % 7))) % table.length;
                if (table[secondCode] != null && table[secondCode].equals(word))
                    return true;
            }
            operations++;
        }
        return false;
    }

    /**
     * Returns the number of words
     **/
    @Override
    public int nbWords() {
        return count;
    }

    /**
     * Takes the ArrayList, converts is to an Array of type String, and then returns the Array
     **/
    private String[] wordsToList() {
        return wordsTable.toArray(new String[0]);
    }

    /**
     * Takes the returned Array from the method above, and returns it as an iterator type
     **/
    @Override
    public Iterator<String> allWords() {
        return Arrays.asList(wordsToList()).iterator();
    }
}
