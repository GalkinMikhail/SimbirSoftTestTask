public class Word implements Comparable<Word> {
    public String word;
    public int count;

    @Override
    public int hashCode(){
        return word.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        return word.equals(((Word)o).word);
    }
    @Override
    public int compareTo(Word word){
        return word.count - count;
    }
}
