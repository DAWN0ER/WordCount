package priv.dawn.workers.domain;

/**
 * Created with IntelliJ IDEA.
 *
 * @Description: 重写了 Word 字符串的 HashCode 和 Equals, 用于 HashSet 和 HashMap
 * @Auther: Dawn Yang
 * @Since: 2024/10/20/20:11
 */
public abstract class WordHashEntry {

    protected final String word;

    public WordHashEntry(String word) {
        this.word = word;
    }

    public String getWord() {
        return word;
    }

    protected abstract int hash(String word);

    @Override
    public int hashCode(){
        return hash(this.word);
    }

    @Override
    public boolean equals(Object obj){
        if(obj == null) return false;
        if(obj instanceof WordHashEntry){
            if(this.hashCode()!= obj.hashCode()) return false;
            String word2 = ((WordHashEntry) obj).getWord();
            return word.equals(word2);
        }
        if(obj instanceof String){
            return word.equals(obj);
        }
        return false;
    }

}
