package uwu.narumi.deobfuscator.transformer.impl.bozoriusz.v2_0;

import org.objectweb.asm.tree.*;
import uwu.narumi.deobfuscator.Deobfuscator;
import uwu.narumi.deobfuscator.transformer.Transformer;
import uwu.narumi.deobfuscator.transformer.impl.universal.other.RefreshTransformer;

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

                                    List<AbstractInsnNode> instructions = new ArrayList<>();
                                    while ((insn = insn.getNext()) != null && insn instanceof LabelNode && insn != lb) {
                                        instructions.add(insn);
                                    }
                                    instructions.forEach(methodNode.instructions::remove);
                                }
                            }
                        });
                Arrays.stream(methodNode.instructions.toArray())
                        .filter(insn -> insn instanceof LabelNode)
                        .filter(insn -> {
                            try { return methodNode.instructions.get((methodNode.instructions.indexOf(insn)+9)).getOpcode() == GOTO; } catch (Exception ex){ return false; }
                        })
                        .forEach(insn -> {
                            LabelNode second = ((JumpInsnNode)insn.getNext().getNext().getNext().getNext().getNext().getNext().getNext().getNext().getNext()).label;
                            if(insn.equals(second)){
                                getInstructionsBetween(insn,insn.getNext().getNext().getNext().getNext().getNext().getNext().getNext().getNext().getNext()).forEach(insn2 -> {
                                    if(!(insn2 instanceof LabelNode)){
                                        methodNode.instructions.remove(insn2);
                                    }
                                });
                            }
                        });
                Arrays.stream(methodNode.instructions.toArray())
                        .filter(insn -> insn instanceof LabelNode)
                        .filter(insn -> insn.getNext().getOpcode() == L2I)
                        .filter(insn -> insn.getNext().getNext().getOpcode() == POP)
                        .forEach(insn -> {
                            List<AbstractInsnNode> instructions = new ArrayList<>();
                            while ((insn = insn.getNext()) != null && !(insn instanceof LabelNode)) {
                                instructions.add(insn);
                            }
                            instructions.forEach(methodNode.instructions::remove);
                        });
                Arrays.stream(methodNode.instructions.toArray())
                        .filter(insn -> insn instanceof LabelNode)
                        //.filter(insn -> {
                        //    try { return methodNode.instructions.get((methodNode.instructions.indexOf(insn)-1)) != null; } catch (Exception ex){ return false; }
                        //})
                        //.filter(insn -> insn.getPrevious().getOpcode() == GOTO)
                        .filter(insn -> insn.getNext() != null)
                        .filter(insn -> insn.getNext().getOpcode() == POP2)
                        .filter(insn -> insn.getNext().getNext().getOpcode() == GOTO)
                        .filter(insn -> {
                            try { return methodNode.instructions.get((methodNode.instructions.indexOf(insn)+8)).getOpcode() == GOTO; } catch (Exception ex){ return false; }
                        })
                        .forEach(insn -> {
                            if(insn.equals(((JumpInsnNode)methodNode.instructions.get((methodNode.instructions.indexOf(insn)+8))).label)){
                                getInstructionsBetween(insn, methodNode.instructions.get((methodNode.instructions.indexOf(insn)+8)), true, true)
                                        .forEach(methodNode.instructions::remove);
                            }
                        });
                Arrays.stream(methodNode.instructions.toArray())
                        .forEach(insn -> {
                            if(insn.getOpcode() == GOTO && insn.getNext() instanceof LabelNode
                                    && ((JumpInsnNode)insn).label.equals(insn.getNext())){
                                methodNode.instructions.remove(insn);
                            } else if(insn instanceof LabelNode){
                                if(insn.getNext().getOpcode() == GOTO
                                        && insn.equals(((JumpInsnNode)insn.getNext()).label)){
                                    methodNode.instructions.remove(insn.getNext());
                                    methodNode.instructions.remove(insn);
                                } else if(isLong(insn.getNext()) && insn.getNext().getNext().getNext().getNext() != null
                                        && insn.getNext().getNext().getNext().getNext().getOpcode() == GOTO
                                        && ((LabelNode)insn).equals(((JumpInsnNode)insn.getNext().getNext().getNext().getNext()).label)){
                                    getInstructionsBetween(insn, insn.getNext().getNext().getNext().getNext(), true, true)
                                            .forEach(methodNode.instructions::remove);
                                }
                            }
                        });
            });
            classNode.methods.forEach(methodNode -> {
                Arrays.stream(methodNode.instructions.toArray())
                        .forEach(insn -> {
                            if(insn.getOpcode() == GOTO && insn.getNext() instanceof LabelNode
                                    && ((JumpInsnNode)insn).label.equals(insn.getNext())){
                                methodNode.instructions.remove(insn);
                            } else if(insn instanceof LabelNode){
                                if(insn.getNext().getOpcode() == GOTO
                                        && insn.equals(((JumpInsnNode)insn.getNext()).label)){
                                    methodNode.instructions.remove(insn.getNext());
                                    methodNode.instructions.remove(insn);
                                }
                            }
                        });
            });
        });

        new RefreshTransformer().transform(deobfuscator);

        deobfuscator.classes().forEach(classNode -> {
            classNode.methods.forEach(methodNode -> {
                Arrays.stream(methodNode.instructions.toArray())
                        .forEach(insn -> {
                            if(insn.getOpcode() == GOTO && insn.getNext() instanceof LabelNode
                                    && ((JumpInsnNode)insn).label.equals(insn.getNext())){
                                methodNode.instructions.remove(insn);
                            } else if(insn instanceof LabelNode){
                                if(insn.getNext().getOpcode() == GOTO
                                        && insn.equals(((JumpInsnNode)insn.getNext()).label)){
                                    methodNode.instructions.remove(insn.getNext());
                                    methodNode.instructions.remove(insn);
                                } else if(isLong(insn.getNext()) && insn.getNext().getNext().getNext().getNext() != null
                                        && insn.getNext().getNext().getNext().getNext().getOpcode() == GOTO
                                        && ((LabelNode)insn).equals(((JumpInsnNode)insn.getNext().getNext().getNext().getNext()).label)){
                                    getInstructionsBetween(insn, insn.getNext().getNext().getNext().getNext(), true, true)
                                            .forEach(methodNode.instructions::remove);
                                }
                            }
                        });
                Arrays.stream(methodNode.instructions.toArray())
                        .forEach(insn -> {
                            if(insn.getOpcode() == GOTO && insn.getNext() instanceof LabelNode
                                    && ((JumpInsnNode)insn).label.equals(insn.getNext())){
                                methodNode.instructions.remove(insn);
                            } else if(insn instanceof LabelNode){
                                if(insn.getNext().getOpcode() == GOTO
                                        && insn.equals(((JumpInsnNode)insn.getNext()).label)){
                                    methodNode.instructions.remove(insn.getNext());
                                    methodNode.instructions.remove(insn);
                                } else if(isLong(insn.getNext()) && insn.getNext().getNext().getNext().getNext() != null
                                        && insn.getNext().getNext().getNext().getNext().getOpcode() == GOTO
                                        && ((LabelNode)insn).equals(((JumpInsnNode)insn.getNext().getNext().getNext().getNext()).label)){
                                    getInstructionsBetween(insn, insn.getNext().getNext().getNext().getNext(), true, true)
                                            .forEach(methodNode.instructions::remove);
                                }
                            }
                        });
            });
        });
    }
}
