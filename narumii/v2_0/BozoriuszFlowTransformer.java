package uwu.narumi.deobfuscator.transformer.impl.bozoriusz.v2_0;

import org.objectweb.asm.tree.*;
import uwu.narumi.deobfuscator.Deobfuscator;
import uwu.narumi.deobfuscator.transformer.Transformer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class BozoriuszFlowTransformer extends Transformer {
    @Override
    public void transform(Deobfuscator deobfuscator) throws Throwable {
        deobfuscator.classes().forEach(classNode -> {
            final String[] fieldName = new String[1];
            classNode.fields.forEach(field -> {
                if(field.desc.equals("J") && isAccess(field.access,ACC_STATIC) && field.value == null){
                    if(isAccess(field.access,ACC_PROTECTED) || isAccess(field.access,~ACC_PROTECTED)){
                        fieldName[0] = field.name;
                    }
                }
            });
            HashMap<AbstractInsnNode, LabelNode> toDel2 = new HashMap<>();
            classNode.methods.forEach(methodNode -> {
                Arrays.stream(methodNode.instructions.toArray())
                        .forEach(insn -> {
                            if(insn.getOpcode() == GETSTATIC && insn.getNext() != null
                                    && insn.getNext().getNext() instanceof LookupSwitchInsnNode){
                                if(((FieldInsnNode) insn).name.equals(fieldName[0])){
                                    LabelNode lb = ((LookupSwitchInsnNode) insn.getNext().getNext()).dflt;
                                    methodNode.instructions.remove(insn.getNext().getNext());
                                    methodNode.instructions.remove(insn.getNext());
                                    methodNode.instructions.set(insn, new JumpInsnNode(GOTO, lb));
                                    toDel2.put(insn,lb);
                                }
                            }
                        });
                HashMap<AbstractInsnNode, AbstractInsnNode> toDel = new HashMap<>();
                Arrays.stream(methodNode.instructions.toArray())
                        .forEach(insn -> {
                            if(insn instanceof LabelNode first
                                    && insn.getNext() != null
                                    && insn.getNext().getNext() != null
                                    && insn.getNext().getNext().getNext() != null
                                    && insn.getNext().getNext().getNext().getNext() != null
                                    && insn.getNext().getNext().getNext().getNext().getNext() != null
                                    && insn.getNext().getNext().getNext().getNext().getNext().getNext() != null
                                    && insn.getNext().getNext().getNext().getNext().getNext().getNext().getNext() != null
                                    && insn.getNext().getNext().getNext().getNext().getNext().getNext().getNext().getNext() != null
                                    && insn.getNext().getNext().getNext().getNext().getNext().getNext().getNext().getNext().getNext() != null
                                    && insn.getNext().getNext().getNext().getNext().getNext().getNext().getNext().getNext().getNext().getOpcode() == GOTO){
                                LabelNode second = ((JumpInsnNode)insn.getNext().getNext().getNext().getNext().getNext().getNext().getNext().getNext().getNext()).label;
                                if(first.equals(second)){
                                    toDel.put(insn, insn.getNext().getNext().getNext().getNext().getNext().getNext().getNext().getNext().getNext());
                                }
                            }
                        });
                toDel.forEach((a,b)->{
                    getInstructionsBetween(a,b).forEach(insn -> {
                        if(!(insn instanceof LabelNode)){
                            methodNode.instructions.remove(insn);
                        }
                    });
                });
                toDel2.forEach((a,b)->{
                    List<AbstractInsnNode> instructions = new ArrayList<>();
                    while ((a = a.getNext()) != null && a instanceof LabelNode && a != b) {
                        instructions.add(a);
                    }
                    instructions.forEach(insn -> {
                        methodNode.instructions.remove(insn);
                    });
                });
            });
        });
    }
}
