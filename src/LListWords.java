
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;
import sun.awt.geom.AreaOp;

import java.util.Iterator;
import java.util.LinkedList;

public class LListWords implements IWords{
    LinkedList wordList;

    public LListWords() {
        wordList = new LinkedList();
    }

    /** Checks if a word exists; if it does, throw an exception, and add the word if the word doesn't exist **/
    @Override
    public void addWord(String word) throws WException {
        if(!(wordList.contains(word)))
            wordList.add(word);
        else
            throw new WException("This word already exists in the LInkedList");
    }

    /** Deletes a word from the LinkedList **/
    @Override
    public void delWord(String word) {
        wordList.remove(word);
    }

    /** Checks if a word exists inside the LinkedList **/
    @Override
    public boolean wordExists(String word) {
        if(wordList.contains(word))
            return true;
        else
            return false;
    }

    /** Returns the number of items inside the LinkedList **/
    @Override
    public int nbWords() {
        return wordList.size();
    }

    /** Returns the LinkedLIst as an Iterator type **/
    @Override
    public Iterator<String> allWords() {
        Iterator<String> itr = wordList.iterator();
        System.out.println("Printing the words");

        while(itr.hasNext())
            System.out.println(itr.next());
        return null;
    }
}
