package uwu.narumi.deobfuscator.transformer.impl.mfuscator;

import org.objectweb.asm.tree.MethodInsnNode;
import uwu.narumi.deobfuscator.Deobfuscator;
import uwu.narumi.deobfuscator.transformer.Transformer;

import java.util.Arrays;

public class mFuscatorTrashRemoverTransformer  extends Transformer {
    @Override
    public void transform(Deobfuscator deobfuscator) throws Throwable {
        deobfuscator.classes().forEach(classNode -> {
            classNode.methods.forEach(methodNode -> {
                Arrays.stream(methodNode.instructions.toArray())
                        .forEach(insn -> {
                            if(isString(insn) && insn.getNext().getOpcode() == INVOKEVIRTUAL){
                                if(((MethodInsnNode)insn.getNext()).name.equals("lenght")){
                                    char[] uga = getString(insn).toCharArray();
                                    String buga ="";
                                    if(getString(insn).contains(buga.repeat(2))){
                                        methodNode.instructions.remove(insn.getNext());
                                        methodNode.instructions.remove(insn);
                                    }
                                }
                            } else if(isString(insn)) {
                                char[] uga = getString(insn).toCharArray();
                                if(uga.length>=3){
                                    String buga = String.valueOf(uga[0]+uga[1]+uga[2]);
                                    if(getString(insn).contains(buga.repeat(2))){
                                        methodNode.instructions.remove(insn.getNext());
                                        methodNode.instructions.remove(insn);
                                    }
                                }
                            }
                        });
            });
        });
    }
}
