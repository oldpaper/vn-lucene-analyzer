package lab.lucene.analysis.vn;

import org.apache.lucene.analysis.util.TokenizerFactory;
import org.apache.lucene.util.AttributeFactory;

import java.util.Map;

/**
 * Created by s1620423 on 2017/08/22.
 */
public class VNTokenizerFactory extends TokenizerFactory {

    public VNTokenizerFactory(Map<String, String> args) {
        super(args);
    }

    @Override
    public VNTokenizer create(AttributeFactory factory) {
        if(this.getOriginalArgs().containsKey("glue")){
            return new VNTokenizer(this.getOriginalArgs().get("glue"));
        }else{
            return new VNTokenizer();
        }
    }
}
