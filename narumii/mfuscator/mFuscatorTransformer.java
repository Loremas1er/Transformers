package uwu.narumi.deobfuscator.transformer.impl.mfuscator;

import uwu.narumi.deobfuscator.transformer.ComposedTransformer;
import uwu.narumi.deobfuscator.transformer.Transformer;
import uwu.narumi.deobfuscator.transformer.impl.colonial.r2.ColonialBooleanTransformer;
import uwu.narumi.deobfuscator.transformer.impl.colonial.r2.ColonialFlowTransformer;
import uwu.narumi.deobfuscator.transformer.impl.colonial.r2.ColonialStringTransformer;
import uwu.narumi.deobfuscator.transformer.impl.mfuscator.mFuscatorNumberTransformer;
import uwu.narumi.deobfuscator.transformer.impl.universal.other.RefreshTransformer;
import uwu.narumi.deobfuscator.transformer.impl.universal.other.StackOperationTransformer;
import uwu.narumi.deobfuscator.transformer.impl.universal.other.UnHideTransformer;
import uwu.narumi.deobfuscator.transformer.impl.universal.other.UniversalNumberTransformer;

import java.util.Arrays;
import java.util.List;

public class mFuscatorTransformer extends ComposedTransformer {
    @Override
    public List<Transformer> transformers() {
        return Arrays.asList(
                new ColonialFlowTransformer(),
                new UniversalNumberTransformer(),
                new mFuscatorNumberTransformer(),
                new ColonialBooleanTransformer(),
                new UniversalNumberTransformer(),
                new RefreshTransformer(),
                new ColonialStringTransformer(),
                new StackOperationTransformer(),
                new UnHideTransformer()
        );
    }
}
