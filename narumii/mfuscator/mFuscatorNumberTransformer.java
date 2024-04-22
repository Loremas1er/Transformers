package uwu.narumi.deobfuscator.transformer.impl.mfuscator;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.tree.*;
import uwu.narumi.deobfuscator.Deobfuscator;
import uwu.narumi.deobfuscator.transformer.Transformer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class mFuscatorNumberTransformer extends Transformer {
    @Override
    public void transform(Deobfuscator deobfuscator) throws Throwable {
        final String[] fieldIIII = {""};
        final String[] fieldIII1 = {""};

        List<String> mh = new ArrayList<>();
        List<String> mh1 = new ArrayList<>();
        List<String> mh2 = new ArrayList<>();
        List<String> fields = new ArrayList<>();

        deobfuscator.classes().forEach(classNode -> {
            classNode.methods.forEach(methodNode -> {
                if(methodNode.desc.equals("(III)I")){
                    Arrays.stream(methodNode.instructions.toArray())
                                    .forEach(insn -> {
                                        if(insn.getOpcode() == ILOAD
                                                && insn.getNext().getOpcode() == ILOAD
                                                && insn.getNext().getNext().getOpcode() == IXOR
                                                && insn.getNext().getNext().getNext().getOpcode() == GETSTATIC
                                                && insn.getNext().getNext().getNext().getNext().getOpcode() == ILOAD){
                                            mh.add(methodNode.name);

                                            classNode.fields.forEach(field -> {
                                                if(field.desc.equals("I")){
                                                    fields.add(field.name);
                                                }
                                            });

                                            for (String name : fields){
                                                if(((FieldInsnNode)insn.getNext().getNext().getNext()).name.equals(name)){
                                                    fieldIIII[0] = name;
                                                }
                                            }
                                        }
                                    });
                } else if(methodNode.desc.equals("(II)I")){
                    Arrays.stream(methodNode.instructions.toArray())
                            .forEach(insn -> {
                                if(insn.getOpcode() == ILOAD
                                        && insn.getNext().getOpcode() == GETSTATIC
                                        && insn.getNext().getNext().getOpcode() == ILOAD
                                        && insn.getNext().getNext().getNext().getOpcode() == IXOR
                                        && insn.getNext().getNext().getNext().getNext().getOpcode() == IXOR){
                                    mh1.add(methodNode.name);

                                    classNode.fields.forEach(field -> {
                                        if(field.desc.equals("I")){
                                            fields.add(field.name);
                                        }
                                    });

                                    for (String name : fields){
                                        if(((FieldInsnNode)insn.getNext()).name.equals(name)){
                                            fieldIII1[0] = name;
                                        }
                                    }
                                } else if(insn.getOpcode() == ILOAD
                                        && insn.getNext().getOpcode() == ILOAD
                                        && insn.getNext().getNext().getOpcode() == IOR
                                        && insn.getNext().getNext().getNext().getOpcode() == ICONST_1
                                        && insn.getNext().getNext().getNext().getNext().getOpcode() == ISHL){
                                    mh2.add(methodNode.name);

                                    classNode.fields.forEach(field -> {
                                        if(field.desc.equals("I")){
                                            fields.add(field.name);
                                        }
                                    });

                                    //for (String name : fields){
                                    //    if(((VarInsnNode)insn.getNext()).name.equals(name)){
                                    //        fieldIII2[0] = name;
                                    //    }
                                    //}
                                }
                            });
                }
            });
            int[] fieldIIIIint = {0};
            int[] fieldIII1int = {0};
            classNode.methods.stream().filter(methodNode -> methodNode.name.equals("<clinit>")).forEach(methodNode -> {
                methodNode.instructions.forEach(insn -> {
                    if(isInteger(insn) && insn.getNext().getOpcode() == PUTSTATIC){
                        if(((FieldInsnNode)insn.getNext()).name.equals(fieldIIII[0])){
                            fieldIIIIint[0] = getInteger(insn);
                            //System.out.println(getInteger(insn));
                        } else if(((FieldInsnNode)insn.getNext()).name.equals(fieldIII1[0])){
                            fieldIII1int[0] = getInteger(insn);
                        }
                    }
                });
            });
            classNode.methods.forEach(methodNode -> {
                Arrays.stream(methodNode.instructions.toArray())
                        .forEach(insn -> {
                            if(isInteger(insn)
                                    && isInteger(insn.getNext())
                                    && isInteger(insn.getNext().getNext())
                                    && insn.getNext().getNext().getNext().getOpcode() == INVOKESTATIC){
                                for(String name : mh){
                                    if(((MethodInsnNode)insn.getNext().getNext().getNext()).name.equals(name)){
                                        int var0 = getInteger(insn);
                                        int var1 = getInteger(insn.getNext());
                                        int var2 = getInteger(insn.getNext().getNext());
                                        methodNode.instructions.remove(insn.getNext().getNext().getNext());
                                        methodNode.instructions.remove(insn.getNext().getNext());
                                        methodNode.instructions.remove(insn.getNext());
                                        methodNode.instructions.set(insn, new LdcInsnNode(defColonial(var0,var1,var2,fieldIIIIint[0])));
                                    }
                                }
                            } else if(isInteger(insn) && isInteger(insn.getNext()) && isInteger(insn.getNext().getNext())
                                    && insn.getNext().getNext().getNext().getOpcode() == INVOKESTATIC && insn.getNext().getNext().getNext().getNext().getOpcode() == IXOR){

                                methodNode.instructions.remove(insn.getNext().getNext().getNext());
                                methodNode.instructions.add(new InsnNode(IXOR));
                                methodNode.instructions.add(new InsnNode(IXOR));
                            } else if(isInteger(insn) && isInteger(insn.getNext())
                                    && insn.getNext().getNext().getOpcode() == INVOKESTATIC){
                                boolean transf = false;
                                for(String name : mh1){
                                    if(((MethodInsnNode)insn.getNext().getNext()).name.equals(name)){
                                        int var0 = getInteger(insn);
                                        int var1 = getInteger(insn.getNext());
                                        methodNode.instructions.remove(insn.getNext().getNext());
                                        methodNode.instructions.remove(insn.getNext());
                                        methodNode.instructions.set(insn, new LdcInsnNode(Type1(var0,var1,fieldIII1int[0])));
                                        transf = true;
                                    }
                                }
                                if(!transf){
                                    for(String name : mh2){
                                        if(((MethodInsnNode)insn.getNext().getNext()).name.equals(name)){
                                            int var0 = getInteger(insn);
                                            int var1 = getInteger(insn.getNext());
                                            methodNode.instructions.remove(insn.getNext().getNext());
                                            methodNode.instructions.remove(insn.getNext());
                                            methodNode.instructions.set(insn, new LdcInsnNode(Type2(var0,var1)));
                                        }
                                    }
                                }
                            }  else if(insn.getOpcode() == ILOAD && insn.getNext()!=null && insn.getNext().getNext()!=null
                                    && insn.getNext().getNext().getOpcode() == INVOKESTATIC){
                                if(isInteger(insn.getNext())){
                                    for(String name : mh2){
                                        if(((MethodInsnNode)insn.getNext().getNext()).name.equals(name)){
                                            int var0 = insn.getOpcode();
                                            int var0_val = ((VarInsnNode)insn).var;

                                            InsnList list = new InsnList();

                                            list.add(new VarInsnNode(var0,var0_val));
                                            list.add(new LdcInsnNode(getInteger(insn.getNext())));
                                            list.add(new InsnNode(IOR));
                                            list.add(new InsnNode(ICONST_1));
                                            list.add(new InsnNode(ISHL));
                                            list.add(new VarInsnNode(var0,var0_val));
                                            list.add(new LdcInsnNode(getInteger(insn.getNext())));
                                            list.add(new InsnNode(IXOR));
                                            list.add(new InsnNode(ICONST_M1));
                                            list.add(new InsnNode(IXOR));
                                            list.add(new InsnNode(IADD));
                                            list.add(new InsnNode(ICONST_1));
                                            list.add(new InsnNode(IADD));

                                            methodNode.instructions.remove(insn.getNext().getNext());
                                            methodNode.instructions.remove(insn.getNext());
                                            methodNode.instructions.remove(insn);

                                            methodNode.instructions.add(list);

                                            //methodNode.instructions.set(insn, new LdcInsnNode(Type2(var0,var1)));
                                        }
                                    }
                                } else if(insn.getNext() instanceof VarInsnNode) {
                                    boolean transf = false;
                                    for(String name : mh1) {
                                        if (((MethodInsnNode) insn.getNext().getNext()).name.equals(name)) {
                                            int var0 = getInteger(insn);
                                            int var1 = getInteger(insn.getNext());
                                            methodNode.instructions.remove(insn.getNext().getNext());
                                            methodNode.instructions.remove(insn.getNext());
                                            methodNode.instructions.set(insn, new LdcInsnNode(Type1(var0, var1, fieldIII1int[0])));
                                            transf = true;
                                        }
                                    }
                                    for(String name : mh2){
                                        if(((MethodInsnNode)insn.getNext().getNext()).name.equals(name)){
                                            int var0 = insn.getOpcode();
                                            int var0_val = ((VarInsnNode)insn).var;

                                            int var1 = insn.getNext().getOpcode();
                                            int var1_val = ((VarInsnNode)insn.getNext()).var;

                                            InsnList list = new InsnList();

                                            list.add(new VarInsnNode(var0,var0_val));
                                            list.add(new VarInsnNode(var1,var1_val));
                                            list.add(new InsnNode(IOR));
                                            list.add(new InsnNode(ICONST_1));
                                            list.add(new InsnNode(ISHL));
                                            list.add(new VarInsnNode(var0,var0_val));
                                            list.add(new VarInsnNode(var1,var1_val));
                                            list.add(new InsnNode(IXOR));
                                            list.add(new InsnNode(ICONST_M1));
                                            list.add(new InsnNode(IXOR));
                                            list.add(new InsnNode(IADD));
                                            list.add(new InsnNode(ICONST_1));
                                            list.add(new InsnNode(IADD));

                                            //methodNode.instructions.remove(insn.getNext().getNext());
                                            //methodNode.instructions.remove(insn.getNext());
                                            methodNode.instructions.insert(insn, list);
                                            methodNode.instructions.remove(insn);

                                            //methodNode.instructions.set(insn, new LdcInsnNode(Type2(var0,var1)));
                                        }
                                    }
                                } else if(insn.getNext().getNext().getOpcode() == INVOKESTATIC) {
                                    for(String name : mh2){
                                        if(((MethodInsnNode)insn.getNext().getNext()).name.equals(name)){

                                            InsnList list = new InsnList();

                                            list.add(insn);
                                            list.add(insn.getNext());
                                            list.add(new InsnNode(IOR));
                                            list.add(new InsnNode(ICONST_1));
                                            list.add(new InsnNode(ISHL));
                                            list.add(insn);
                                            list.add(insn.getNext());
                                            list.add(new InsnNode(IXOR));
                                            list.add(new InsnNode(ICONST_M1));
                                            list.add(new InsnNode(IXOR));
                                            list.add(new InsnNode(IADD));
                                            list.add(new InsnNode(ICONST_1));
                                            list.add(new InsnNode(IADD));

                                            methodNode.instructions.remove(insn.getNext().getNext());
                                            methodNode.instructions.remove(insn.getNext());

                                            methodNode.instructions.insert(insn, list);
                                            methodNode.instructions.remove(insn);

                                            //methodNode.instructions.set(insn, new LdcInsnNode(Type2(var0,var1)));
                                        }
                                    }
                                }
                            }
                        });
            });
        });
    }
    private int defColonial(int var0, int var1, int var2, int var3){
        return var1 ^ var0 ^ var3 ^ var2 ^ var0;
    }
    private static int Type1(int var0, int var1, int var3) {
        return var0 ^ var3 ^ var1;
    }

    private static int Type2(int var0, int var1) {
        return ((var0 | var1) << 1) + ~(var0 ^ var1) + 1;
    }
}
