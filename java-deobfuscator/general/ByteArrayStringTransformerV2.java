package com.javadeobfuscator.deobfuscator.transformers.general;

import com.javadeobfuscator.deobfuscator.analyzer.AnalyzerResult;
import com.javadeobfuscator.deobfuscator.analyzer.MethodAnalyzer;
import com.javadeobfuscator.deobfuscator.analyzer.frame.*;
import com.javadeobfuscator.deobfuscator.config.TransformerConfig;
import com.javadeobfuscator.deobfuscator.transformers.Transformer;
import com.javadeobfuscator.deobfuscator.utils.TransformerHelper;
import com.javadeobfuscator.deobfuscator.utils.Utils;
import org.objectweb.asm.tree.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.LongAdder;

public class ByteArrayStringTransformerV2 extends Transformer<TransformerConfig> {

    @Override
    public boolean transform() throws Throwable {

        classNodes().forEach(classNode -> classNode.methods.stream().filter(Utils::notAbstractOrNative).forEach(methodNode -> {
            ConcurrentHashMap<AbstractInsnNode, AbstractInsnNode> toDelete = new ConcurrentHashMap<>();
            for(int idx = 0; idx < 2; ++idx){
                try {
                    for(AbstractInsnNode insn : methodNode.instructions){
                        if(insn.getOpcode() == NEW && insn.getNext() != null
                                && insn.getNext().getOpcode() == DUP){
                            if(isNum(insn.getNext().getNext())
                                    && insn.getNext().getNext().getNext().getOpcode() == NEWARRAY){
                                if(((TypeInsnNode)insn).desc.equals("java/lang/String") && getNum(insn.getNext().getNext()) != 1337333333){
                                    int index = methodNode.instructions.indexOf(insn.getNext().getNext().getNext());
                                    int indexAll = methodNode.instructions.size();
                                    byte[] bytes = new byte[getNum(insn.getNext().getNext())];
                                    int count = 0;
                                    boolean cont = true;
                                    AbstractInsnNode end = null;
                                    for(int i = 0; i < (indexAll - index); ++i){
                                        if(cont){
                                            AbstractInsnNode xui = methodNode.instructions.get(index+i);
                                            switch (xui.getOpcode()){
                                                case BIPUSH:
                                                    if(xui.getNext().getOpcode() != BIPUSH){
                                                        bytes[count] = (byte) ((IntInsnNode) xui).operand;
                                                        ++count;
                                                    }
                                                    break;
                                                case DUP, BASTORE, ICONST_0, ICONST_1, ICONST_2, ICONST_3, ICONST_4, ICONST_5:
                                                    break;
                                                case INVOKESPECIAL:
                                                    if(((MethodInsnNode)xui).owner.equals("java/lang/String")
                                                            && ((MethodInsnNode)xui).name.equals("<init>")
                                                            && ((MethodInsnNode)xui).desc.equals("([B)V")){
                                                        cont = false;
                                                        end = xui;
                                                    }
                                                    break;
                                                default:
                                                    break;
                                            }
                                        }
                                    }
                                    if(end != null){
                                        toDelete.put(insn.getNext(),end);
                                        //getInstructionsBetween(
                                        //        insn.getNext(),
                                        //        end,
                                        //        true,
                                        //        true
                                        //).forEach(methodNode.instructions::remove);
                                        //System.out.println(new String(bytes));
                                        methodNode.instructions.set(insn, new LdcInsnNode(new String(bytes)));
                                    }
                                }
                            }
                        }
                    }
                    toDelete.forEach((start, end) -> {
                        try{
                            getInstructionsBetween(
                                    methodNode.instructions.get(methodNode.instructions.indexOf(start)-1),
                                    methodNode.instructions.get(methodNode.instructions.indexOf(end)+1),
                                    false,
                                    false
                            ).forEach(methodNode.instructions::remove);
                        }catch (Exception ignored){}
                    });
                } catch (Exception ex) {
                    System.err.println("[ByteArrayStringTransformer] An error occurred while deobfuscating " + classNode.name + " " + methodNode.name + methodNode.desc + ":");
                    ex.printStackTrace();
                }
            }
        }));

        System.out.println("[ByteArrayStringTransformer] Successfully transformed.");

        return true;
    }
    private static int getNum(AbstractInsnNode insn){
        if(insn instanceof IntInsnNode){
            return ((IntInsnNode) insn).operand;
        } else {
            switch (insn.getOpcode()){
                case ICONST_0 -> {
                    return 0;
                }
                case ICONST_1 -> {
                    return 1;
                }
                case ICONST_2 -> {
                    return 2;
                }
                case ICONST_3 -> {
                    return 3;
                }
                case ICONST_4 -> {
                    return 4;
                }
                case ICONST_5 -> {
                    return 5;
                }
                default -> {
                    return 1337333333;
                }
            }
        }
    }
    private static boolean isNum(AbstractInsnNode insn){
        return (insn instanceof IntInsnNode || insn.getOpcode() == ICONST_0
                || insn.getOpcode() == ICONST_1 || insn.getOpcode() == ICONST_2
                || insn.getOpcode() == ICONST_3 || insn.getOpcode() == ICONST_4 || insn.getOpcode() == ICONST_5);
    }
    public static List<AbstractInsnNode> getInstructionsBetween(AbstractInsnNode start, AbstractInsnNode end, boolean includeStart, boolean includeEnd) {
        List<AbstractInsnNode> instructions = new ArrayList<>();

        if (includeStart)
            instructions.add(start);

        while ((start = start.getNext()) != null && start != end) {
            instructions.add(start);
        }

        if (includeEnd)
            instructions.add(end);

        return instructions;
    }
}
