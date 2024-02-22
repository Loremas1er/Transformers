package uwu.narumi.deobfuscator.transformer.impl.colonial.r2;

import org.objectweb.asm.Label;
import org.objectweb.asm.tree.*;
import uwu.narumi.deobfuscator.Deobfuscator;
import uwu.narumi.deobfuscator.transformer.Transformer;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ColonialStringTransformer extends Transformer {
    private static char[] chars;
    @Override
    public void transform(Deobfuscator deobfuscator) throws Throwable {
        deobfuscator.classes()
                .forEach(classNode -> {
                    classNode.methods.stream().filter(methodNode -> methodNode.name.equals("<clinit>")).forEach(methodNode -> {
                        try {
                            char[] chars1 = getChars(classNode);
                            if(chars1 != null){
                                if(!getSwitchCases(methodNode).equals("Not Found!")){
                                    int[] switchCases = new int[7];
                                    AtomicInteger i = new AtomicInteger(0);
                                    Arrays.stream(methodNode.instructions.toArray())
                                            .forEach(insn -> {
                                                if(insn.getOpcode() == BIPUSH && insn.getNext().getOpcode() == GOTO){
                                                    switchCases[i.getAndIncrement()] = (int)getNumber(insn);
                                                } else if(insn.getOpcode() == BIPUSH && insn.getNext().getOpcode() != IREM){
                                                    switchCases[i.getAndIncrement()] = (int)getNumber(insn);
                                                }
                                            });

                                    boolean mod = modifedStringEnc(classNode);;

                                    if(!mod){
                                        decStep1(chars1,switchCases);
                                    } else {
                                        decStep2(chars1,switchCases);
                                    }

                                    classNode.methods.forEach(methodNodee -> {
                                        Arrays.stream(methodNodee.instructions.toArray())
                                                .forEach(node -> {
                                                    if (isInteger(node)
                                                            && isInteger(node.getNext())
                                                            && isString(node.getNext().getNext())
                                                            && isInteger(node.getNext().getNext().getNext())
                                                            && isInteger(node.getNext().getNext().getNext().getNext())
                                                            && node.getNext().getNext().getNext().getNext().getNext().getOpcode() == INVOKESTATIC
                                                            && ((MethodInsnNode)node.getNext().getNext().getNext().getNext().getNext()).desc.equals("(IILjava/lang/String;II)Ljava/lang/String;")) {
                                                        int one = getInteger(node);
                                                        int two = getInteger(node.getNext());
                                                        int three = getInteger(node.getNext().getNext().getNext());
                                                        int four = getInteger(node.getNext().getNext().getNext().getNext());
                                                        String enc = getString(node.getNext().getNext());

                                                        String deobfedString = decStepFinal(one,two,enc,three,four,mod);

                                                        methodNodee.instructions.remove(node.getNext().getNext().getNext().getNext().getNext());
                                                        methodNodee.instructions.remove(node.getNext().getNext().getNext().getNext());
                                                        methodNodee.instructions.remove(node.getNext().getNext().getNext());
                                                        methodNodee.instructions.remove(node.getNext().getNext());
                                                        methodNodee.instructions.remove(node.getNext());
                                                        methodNodee.instructions.set(node, new LdcInsnNode(deobfedString));

                                                    }
                                                });
                                        /*for (AbstractInsnNode node : methodNodee.instructions.toArray()) {
                                            if (isInteger(node)
                                                    && isInteger(node.getNext())
                                                    && isString(node.getNext().getNext())
                                                    && isInteger(node.getNext().getNext().getNext())
                                                    && isInteger(node.getNext().getNext().getNext().getNext())
                                                    && node.getNext().getNext().getNext().getNext().getNext().getOpcode() == INVOKESTATIC
                                                    && ((MethodInsnNode)node.getNext().getNext().getNext().getNext().getNext()).desc.equals("(IILjava/lang/String;II)Ljava/lang/String;")) {
                                                int one = getInteger(node);
                                                int two = getInteger(node.getNext());
                                                int three = getInteger(node.getNext().getNext().getNext());
                                                int four = getInteger(node.getNext().getNext().getNext().getNext());
                                                String enc = getString(node.getNext().getNext());

                                                String deobfedString = decStepFinal(one,two,enc,three,four,mod);

                                                methodNodee.instructions.remove(node.getNext().getNext().getNext().getNext().getNext());
                                                methodNodee.instructions.remove(node.getNext().getNext().getNext().getNext());
                                                methodNodee.instructions.remove(node.getNext().getNext().getNext());
                                                methodNodee.instructions.remove(node.getNext().getNext());
                                                methodNodee.instructions.remove(node.getNext());
                                                methodNodee.instructions.set(node, new LdcInsnNode(deobfedString));

                                            }
                                        }*/
                                    });
                                }
                            }
                        } catch (Exception ignored) {}
                    });

                });
    }
    private String getSwitchCases(MethodNode methodNode){
        int[] switchCases = new int[7];
        AtomicInteger i = new AtomicInteger(0);
        Arrays.stream(methodNode.instructions.toArray())
                .forEach(insn -> {
                    if(insn.getOpcode() == BIPUSH && insn.getNext().getOpcode() == GOTO){
                        switchCases[i.getAndIncrement()] = (int)getNumber(insn);
                    } else if(insn.getOpcode() == BIPUSH && insn.getNext().getOpcode() != IREM){
                        switchCases[i.getAndIncrement()] = (int)getNumber(insn);
                    }
                });
        if(i.get() == 7){
            return "Found";
        } else {
            return "Not Found!";
        }
    }
    private char[] getChars(ClassNode classNode) {
        final String[] chars = {""};
        MethodNode methodNode = classNode.methods.stream().filter(methodNode1 -> methodNode1.name.equals("<clinit>")).findFirst().orElse(null);
        assert methodNode != null;
        Arrays.stream(methodNode.instructions.toArray())
                .forEach(insn -> {
                    if(isString(insn) && insn.getNext().getOpcode() == ICONST_M1){
                        chars[0] = new String(getString(insn).getBytes(),StandardCharsets.UTF_8);
                    }
                });

        if(chars[0] != null){
            return chars[0].toCharArray();
        } else {
            return null;
        }
    }
    private void decStep2(char[] encryptedData, int[] switchCases){
        int var10003 = encryptedData.length;
        int var0 = 0;
        while(true) {
            if (var10003 <= var0) {
                String var5 = (new String(encryptedData)).intern();
                chars = var5.toCharArray();
                return;
            }

            char var10005 = encryptedData[var0];
            byte var10006 = switch (var0 % 7) {
                case 0 -> (byte) switchCases[0];
                case 1 -> (byte) switchCases[1];
                case 2 -> (byte) switchCases[2];
                case 3 -> (byte) switchCases[3];
                case 4 -> (byte) switchCases[4];
                case 5 -> (byte) switchCases[5];
                default -> (byte) switchCases[6];
            };

            encryptedData[var0] = (char)(var10005 ^ var10006);
            ++var0;
        }
    }
    private void decStep1(char[] encryptedData, int[] switchCases){
        for(int var0 = 0; encryptedData.length > var0; ++var0) {
            char var10005 = encryptedData[var0];
            byte var10006 = switch (var0 % 7) {
                case 0 -> (byte) switchCases[0];
                case 1 -> (byte) switchCases[1];
                case 2 -> (byte) switchCases[2];
                case 3 -> (byte) switchCases[3];
                case 4 -> (byte) switchCases[4];
                case 5 -> (byte) switchCases[5];
                default -> (byte) switchCases[6];
            };
            encryptedData[var0] = (char)(var10005 ^ var10006);
        }
        String var5 = (new String(encryptedData)).intern();
        chars = var5.toCharArray();
    }
    public static String decStepFinal(int var0, int var1, String var2, int var3, int var4, boolean mod) {
        StringBuilder var5 = new StringBuilder();
        int var6 = 0;
        char[] var10;
        int var9 = (var10 = var2.toCharArray()).length;

        if(!mod){
            for(int var8 = 0; var8 < var9; ++var8) {
                char var7 = var10[var8];
                var5.append((char)(var7 ^ chars[var6 % chars.length] ^ var0 ^ var3 + var6 ^ var1 ^ var4));
                ++var6;
            }
            return var5.toString();
        } else {
            for(int var8 = 0; var8 < var9; ++var8) {
                char var7 = var10[var8];
                var5.append((char)(var7 ^ chars[var6 % chars.length] ^ var0 ^ XOR2(var3, var6) ^ var1 ^ var4));
                ++var6;
            }
            return var5.toString();
        }
    }
    private static int XOR2(int var0, int var1) {
        return ((var0 | var1) << 1) + ~(var0 ^ var1) + 1;
    }
    private boolean modifedStringEnc(ClassNode classNode){
        int[] mod = {0};
        classNode.methods.forEach(methodNode -> {
            Arrays.stream(methodNode.instructions.toArray())
                    .forEach(insn -> {
                        if(insn.getOpcode() == ILOAD
                                && insn.getNext().getOpcode() == ILOAD
                                && insn.getNext().getNext().getOpcode() == INVOKESTATIC){
                            if(((MethodInsnNode)insn.getNext().getNext()).desc.equals("(II)I")){
                                mod[0] = 1;
                            }
                        }
                    });
        });
        return mod[0] == 1;
    }

}
