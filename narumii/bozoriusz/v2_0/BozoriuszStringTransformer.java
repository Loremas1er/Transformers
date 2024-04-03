package uwu.narumi.deobfuscator.transformer.impl.bozoriusz.v2_0;

import org.objectweb.asm.tree.*;
import uwu.narumi.deobfuscator.Deobfuscator;
import uwu.narumi.deobfuscator.transformer.Transformer;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.*;
import javax.crypto.Cipher;
import java.util.ArrayList;
import java.util.Arrays;

public class BozoriuszStringTransformer extends Transformer {
    @Override
    public void transform(Deobfuscator deobfuscator) throws Throwable {
        Map strings = new HashMap();
        deobfuscator.classes().forEach(classNode -> {
            classNode.methods.forEach(methodNode -> {
                Map keys = new HashMap();
                if(!methodNode.name.equals("<clinit>") || methodNode.name.equals("<clinit>")){
                    Arrays.stream(methodNode.instructions.toArray())
                            .forEach(insn -> {
                                if(isLong(insn) && insn.getNext() != null
                                        && insn.getNext().getNext() != null
                                        && insn.getNext().getNext().getNext() != null
                                        && insn.getNext().getNext().getNext().getNext() != null
                                        && isString(insn.getNext().getNext().getNext().getNext())
                                        && insn.getNext().getNext().getNext().getNext().getNext() != null
                                        && insn.getNext().getNext().getNext().getNext().getNext().getNext() != null
                                        && insn.getNext().getNext().getNext().getNext().getNext().getNext().getNext() != null
                                        && insn.getNext().getNext().getNext().getNext().getNext().getNext().getOpcode() == INVOKEINTERFACE){
                                    long key = getLong(insn);
                                    String value = getString(insn.getNext().getNext().getNext().getNext());
                                    keys.put(key,value);

                                    //if(((FieldInsnNode) insn).name.equals(fieldName[0])){
                                    //    LabelNode lb = ((LookupSwitchInsnNode) insn.getNext().getNext()).dflt;
                                    //    methodNode.instructions.remove(insn.getNext().getNext());
                                    //    methodNode.instructions.remove(insn.getNext());
                                    //    methodNode.instructions.set(insn, new JumpInsnNode(GOTO, lb));
                                    //    toDel2.put(insn,lb);
                                    //}
                                }
                            });
                    Arrays.stream(methodNode.instructions.toArray())
                            .forEach(insn -> {
                                if(isLong(insn) && insn.getNext() != null
                                        && insn.getNext().getNext() != null
                                        && insn.getNext().getNext().getNext() != null
                                        && insn.getNext().getNext().getNext().getNext() != null
                                        && isString(insn.getNext().getNext().getNext().getNext())
                                        && insn.getNext().getNext().getNext().getNext().getNext() != null
                                        && insn.getNext().getNext().getNext().getNext().getNext().getNext() != null
                                        && isLong(insn.getNext().getNext().getNext().getNext().getNext().getNext().getNext())){
                                    long key = getLong(insn);
                                    long key2 = getLong(insn.getNext().getNext().getNext().getNext().getNext().getNext().getNext());
                                    String value = getString(insn.getNext().getNext().getNext().getNext());
                                    if(keys.get(key2) != null){
                                        try {
                                            strings.put(key,new String(decode(Base64.getDecoder().decode((String)keys.get(key2))
                                                    , Base64.getDecoder().decode(value.getBytes()))));
                                        } catch (Exception e) {
                                            throw new RuntimeException(e);
                                        }
                                    }

                                    //if(((FieldInsnNode) insn).name.equals(fieldName[0])){
                                    //    LabelNode lb = ((LookupSwitchInsnNode) insn.getNext().getNext()).dflt;
                                    //    methodNode.instructions.remove(insn.getNext().getNext());
                                    //    methodNode.instructions.remove(insn.getNext());
                                    //    methodNode.instructions.set(insn, new JumpInsnNode(GOTO, lb));
                                    //    toDel2.put(insn,lb);
                                    //}
                                }
                            });
                    //strings.forEach((key, str) -> System.out.println("Key: "+key+"     Value: "+str));
                    //keys.forEach((key, str) -> System.out.println("Key: "+key+"     Value: "+str));
                }
            });
        });
        deobfuscator.classes().forEach(classNode -> {
            classNode.methods.forEach(methodNode -> {
                List insns = new ArrayList<>(); //GOTO: не дай боже эта хуета сломается
                Arrays.stream(methodNode.instructions.toArray())
                        .forEach(insn -> {
                            if(isLong(insn) && insn.getNext() != null
                                    && insn.getNext().getOpcode() == INVOKESTATIC){
                                if(((MethodInsnNode)insn.getNext()).desc.equals("(J)Ljava/lang/String;")){
                                    long key = getLong(insn);
                                    if(strings.get(key) != null){
                                        insns.add(insn.getNext());
                                        //System.out.println("Key: "+key+"     Value: "+strings.get(key));
                                        methodNode.instructions.set(insn, new LdcInsnNode(strings.get(key)));
                                    } else {
                                        insns.add(insn);
                                        insns.add(insn.getNext());
                                        if(insn.getNext().getNext().getOpcode() == POP){
                                            insns.add(insn.getNext().getNext());
                                        }
                                    }
                                }
                            }
                        });
                insns.forEach(insnNode -> methodNode.instructions.remove((AbstractInsnNode) insnNode));
            });
        });
    }
    private byte[] decode(byte[] var1, byte[] var2) throws Exception {
        ArrayList var3 = new ArrayList();

        for(int var4 = 0; var4 < var2.length; var4 += 128) {
            byte[] var5 = Arrays.copyOfRange(var2, var4, Math.min(var2.length, var4 + 128));
            var3.add(var5);
        }

        Cipher var11 = Cipher.getInstance("RSA");
        ByteArrayOutputStream var12 = new ByteArrayOutputStream();
        PKCS8EncodedKeySpec var6 = new PKCS8EncodedKeySpec(var1);
        KeyFactory var7 = KeyFactory.getInstance("RSA");
        PrivateKey var8 = var7.generatePrivate(var6);
        Iterator var9 = var3.iterator();

        while(var9.hasNext()) {
            byte[] var10 = (byte[])var9.next();
            var11.init(2, var8);
            var12.write(var11.doFinal(var10));
        }

        return var12.toByteArray();
    }
}
