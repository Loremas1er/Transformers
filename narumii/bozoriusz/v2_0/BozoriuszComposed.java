package uwu.narumi.deobfuscator.transformer.impl.bozoriusz.v2_0;

import uwu.narumi.deobfuscator.transformer.ComposedTransformer;
import uwu.narumi.deobfuscator.transformer.Transformer;
import uwu.narumi.deobfuscator.transformer.composed.CleanTransformer;
import uwu.narumi.deobfuscator.transformer.impl.binsecure.BinsecureNumberTransformer;
import uwu.narumi.deobfuscator.transformer.impl.binsecure.BinsecureSemiFlowTransformer;
import uwu.narumi.deobfuscator.transformer.impl.binsecure.latest.*;
import uwu.narumi.deobfuscator.transformer.impl.monsey.MonseyFakeTryCatchTransformer;
import uwu.narumi.deobfuscator.transformer.impl.universal.other.RefreshTransformer;
import uwu.narumi.deobfuscator.transformer.impl.universal.remove.TryCatchRemoveTransformer;

import java.util.Arrays;
import java.util.List;

public class BozoriuszComposed extends ComposedTransformer {


    @Override
    public List<Transformer> transformers() {
        return Arrays.asList(
                new BozoriuszFlowTransformer(),
                new CleanTransformer(),
                new RefreshTransformer(),
                new BozoriuszStringTransformer(),
                new BozoriuszCleanTransformer()
        );
    }
}
