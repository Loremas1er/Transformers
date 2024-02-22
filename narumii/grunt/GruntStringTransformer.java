package uwu.narumi.deobfuscator.transformer.impl.grunt;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import uwu.narumi.deobfuscator.Deobfuscator;
import uwu.narumi.deobfuscator.transformer.Transformer;

import java.util.Arrays;

public class GruntStringTransformer extends Transformer {
    @Override
    public void transform(Deobfuscator deobfuscator) throws Throwable {
        deobfuscator.classes().forEach(classNode -> {
            classNode.methods.forEach(methodNode -> {
                Arrays.stream(methodNode.instructions.toArray())
                        .forEach(insn ->{
                            if(isString(insn) && insn.getNext().getOpcode() == INVOKESTATIC){
                                if(((MethodInsnNode)insn.getNext()).desc.equals("(Ljava/lang/String;)Ljava/lang/String;")){
                                    int[] num = {0};
                                    num[0] = getNum(classNode);

                                    String deobf = decode(getString(insn),num[0]);

                                    methodNode.instructions.remove(insn.getNext());
                                    methodNode.instructions.set(insn, new LdcInsnNode(deobf));
                                }
                            }
                        });
            });
        });
    }
    private int getNum(ClassNode classNode){
        int[] banan = {0};
        classNode.methods.forEach(mh -> {
            if(mh.desc.equals("(Ljava/lang/String;)Ljava/lang/String;")){
                mh.instructions.forEach(ban -> {
                    if(ban.getOpcode() == INVOKEVIRTUAL
                            && ban.getNext().getOpcode() == SIPUSH
                            && ban.getNext().getNext().getOpcode() == IXOR
                            && ban.getNext().getNext().getNext().getOpcode() == I2C){
                        banan[0] = (int) getNumber(ban.getNext());
                    }
                });
            }
        });
        return banan[0];
    }
    private static String decode(String var0, int arg) {
        StringBuilder var1 = new StringBuilder();

        for(int var2 = 0; var2 < var0.length(); ++var2) {
            var1.append((char)(var0.charAt(var2) ^ arg));
        }

        return var1.toString();
    }
}
