package uwu.narumi.deobfuscator.transformer.impl.mfuscator;

import uwu.narumi.deobfuscator.Deobfuscator;
import uwu.narumi.deobfuscator.transformer.Transformer;

import java.util.Arrays;
import java.util.Random;

public class mFuscatorFlowTransformer extends Transformer {
    public Random RANDOM = new Random();
    public String[] a = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};

    @Override
    public void transform(Deobfuscator deobfuscator) throws Exception {
        deobfuscator.classes()
                .forEach(classNode -> classNode.methods.stream()
                        .filter(methodNode -> !methodNode.name.startsWith("<"))
                        .forEach(methodNode -> {
                            Arrays.stream(methodNode.instructions.toArray())
                                    .filter(insnNode -> insnNode.getOpcode() == DUP || insnNode.getOpcode() == POP || insnNode.getOpcode() == SWAP || insnNode.getOpcode() == FSUB || insnNode.getOpcode() == ISUB || insnNode.getOpcode() == DSUB || insnNode.getOpcode() == ATHROW)
                                    .forEach(insnNode -> {
                                        while (insnNode.getPrevious() != null
                                                && insnNode.getPrevious().getPrevious() != null
                                                && insnNode.getOpcode() == DUP
                                                && insnNode.getPrevious().getOpcode() == INVOKEVIRTUAL
                                                && insnNode.getPrevious().getPrevious().getOpcode() == LDC) {

                                            methodNode.instructions.remove(insnNode.getPrevious().getPrevious());
                                            methodNode.instructions.remove(insnNode.getPrevious());

                                            if (insnNode.getNext().getOpcode() == POP2) {

                                                methodNode.instructions.remove(insnNode.getNext());

                                            } else if (insnNode.getNext().getOpcode() == POP) {

                                                methodNode.instructions.remove(insnNode.getNext());
                                                methodNode.instructions.remove(insnNode.getNext());

                                            }
                                            methodNode.instructions.remove(insnNode);
                                        }
                                    });
                        }));
    }
}
