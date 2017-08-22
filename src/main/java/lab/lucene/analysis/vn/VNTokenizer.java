package lab.lucene.analysis.vn;

import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import vn.hus.nlp.tokenizer.VietTokenizer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

/**
 * Created by s1620423 on 2017/08/22.
 */
public class VNTokenizer extends Tokenizer {
    private static VietTokenizer vietTokenizer = null;

    String[] taggedWords;
    private int numWord;

    private int index = 0;
    private int offset = 0;
    private final CharTermAttribute termAttr;
    private final PositionIncrementAttribute posAttr;
    private final OffsetAttribute offsetAttr;
    private String lastContent;
    public static final String DEFAULT_GLUE = "_";
    private final String glue;

    public static VietTokenizer getVietTokenizer() {
        if (vietTokenizer == null) {
            vietTokenizer = new VietTokenizer();
        }
        return vietTokenizer;
    }

    public VNTokenizer() {
        this(DEFAULT_GLUE);
    }

    public VNTokenizer(String glue) {
        this.glue = glue;
        this.termAttr = addAttribute(CharTermAttribute.class);
        this.posAttr = addAttribute(PositionIncrementAttribute.class);
        this.offsetAttr = addAttribute(OffsetAttribute.class);
    }

    private void getTaggedWords(Reader input) {

        BufferedReader bufferedReader = new BufferedReader(input);
        StringBuffer bufferContent = new StringBuffer("");
        String line;

        try {
            while ((line = bufferedReader.readLine()) != null) {
                bufferContent.append(line + "\n");
            }
            lastContent = bufferContent.toString();
            taggedWords = getVietTokenizer().segment(lastContent).split(" ");
            for (int i = 0; i < taggedWords.length; i++) {
                taggedWords[i] = taggedWords[i].replaceAll("_", this.glue);
            }
        } catch (IOException e) {
            System.err.println("Error Tokenizer Input : " + input);
            taggedWords = new String[0];
        }

        numWord = taggedWords.length;
        offset = 0;
        index = 0;
    }


    @Override
    public final boolean incrementToken() throws IOException {
        clearAttributes();
        if (index == numWord)
            return false;
        String wordTag = taggedWords[index];
        String nextWordTag = null;
        try {
            nextWordTag = taggedWords[index + 1];
        } catch (ArrayIndexOutOfBoundsException e) {

        }

        if (wordTag.equalsIgnoreCase("")) {

        } else {
            termAttr.copyBuffer(wordTag.toCharArray(), 0, wordTag.length());
            posAttr.setPositionIncrement(1);
            offsetAttr.setOffset(offset, offset + wordTag.length());
        }


        offset += wordTag.length();

        // Correct offset for two words
        if (index != numWord) {
            if (nextWordTag == null || lastContent.indexOf(nextWordTag, offset) == offset) {

            } else {
                offset++;
            }
        }
        index++;
        return true;
    }

    @Override
    public final void end() {
        try {
            super.end();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // set final offset
        int finalOffset = correctOffset(offset);
        offsetAttr.setOffset(finalOffset, finalOffset);
    }

    @Override
    public void reset() throws IOException {
        super.reset();
        getTaggedWords(input);
    }
}

