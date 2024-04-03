package uwu.narumi.deobfuscator.transformer.impl.bozoriusz.v2_0;

import org.objectweb.asm.tree.*;
import uwu.narumi.deobfuscator.Deobfuscator;
import uwu.narumi.deobfuscator.transformer.Transformer;
import java.util.Arrays;

public class BozoriuszCleanTransformer extends Transformer {
    @Override
    public void transform(Deobfuscator deobfuscator) throws Throwable {
        deobfuscator.classes().forEach(classNode -> {
            String[] toRemove = new String[2];
            classNode.methods.forEach(methodNode -> {
                Arrays.stream(methodNode.instructions.toArray())
                        .forEach(insn -> {
                            if(isLong(insn) && insn.getNext().getOpcode() == PUTSTATIC
                                    && insn.getNext().getNext().getOpcode() == PUTSTATIC){
                                toRemove[0] = ((FieldInsnNode)insn.getNext()).name;
                                toRemove[1] = ((FieldInsnNode)insn.getNext().getNext()).name;
                                getInstructionsBetween(insn, insn.getNext().getNext(), true, true).forEach(methodNode.instructions::remove);
                            }
                        });
            });
            classNode.fields.removeIf(field -> (field.name.equals(toRemove[0]) || field.name.equals(toRemove[1])));
        });
    }
}