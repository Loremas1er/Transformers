package com.javadeobfuscator.deobfuscator.transformers.normalizer;

import com.javadeobfuscator.deobfuscator.config.TransformerConfig;
import org.objectweb.asm.tree.LdcInsnNode;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

@TransformerConfig.ConfigOptions(configClass = ClientRemapper.Config.class)
public class ClientRemapper  extends AbstractNormalizer<ClientRemapper.Config> {
    public Map<String, Object> myObjectAsDict = new HashMap<>();
    public int[] iconst = {ICONST_0,ICONST_1,ICONST_2,ICONST_3,ICONST_4,ICONST_5};
    public String[] font = {"\u00A7","0123456789abcdefklmnor"};
    public String[] files = {"AltConfig","HudConfig","FriendConfig","MacroConfig"};
    public String[] names = {"Meteor","Rich","MClient","code.cc","Celestial","Minced","Akrien","Winner","Sokol","Shit","Motion"};
    public String[] category = {"Combat","Movement","Render","Player","Util"};
    public String[] modules = {
            "AirJump","AirStealer","AntiAFK","AntiBot","AntiFlag",
            "AntiLagMachine","AutoArmor","AutoExplosion","AutoGApple",
            "AutoPotion","AutoSprint","AutoTool","AutoTotem","AutoTPAccept",
            "Baritone","BedrockClip","BetterChat","BlockESP","Chams","ChatHistory",
            "ChinaHat","ClickGUI","ClickPearl","ClientSound","Crosshair","CustomModel",
            "DamageFly","DamageParticles","DamageSpeed","DeathCoords","ElytraFix",
            "ElytraFly","EntityESP","FastBow","FastWorldLoading","Flight","FlyingParticles",
            "FogColor","FreeCam","FullBright","GAppleCooldown","GPS","GuiWalk","GuiWalk",
            "HitBox","HighJump","Hotbar","Hud","ItemESP","ItemPhysics","ItemScroller","ItemSwapFix",
            "Jesus","JumpCircles","KeepSprint","KillAura","KTLeave","MiddleClickFriend","ModuleList",
            "NameProtect","NameTags","Nimb","NoClip","NoFall","NoInteract","NoJumpDelay","NoPush",
            "NoRender","NoRotate","NoSlowDown","Notifications","PearlESP","PotionHUD","Scoreboard",
            "ShulkerViewer","Speed","Spider","Strafe","SwordTranslate","TargetESP","TargetHUD",
            "TargetStrafe","Timer","Tracers","Trails","TriggerBot","Velocity","ViewModel","WaterSpeed",
            "WorldFeatures","XCarry","Disabler","Staff Alert","Auto Tool","Auto GApple","Air Drop Steal",
            "Web Leave","Auto Totem","Elytra Swap","Auto TP Accept","ChunkAnimator","Auto Sprint","Anti Web",
            "GApple Timer","Shader ESP","Click GUI","Jump Circles","China Hat","Custom Model","StaffStats","Reach",
            "Minecraft Optimizer","No Slow","Shield Breaker","Anti AFK","Free Camera","Air Drop Way","Block ESP",
            "Name Protect","HVHHelper","ClientOverlay","AntiAim","AirDropWay","AirDropSteal","NoWeb","NoOverlay",
            "Baby Boy","JumpCircle","RWHelper","InvWalk","StaffAlert","NoServerRotation","AntiLevitation","FastPlace",
            "ModuleSoundAlert","AppleGoldenTimer","NoFriendDamage","SuperBow","Aura","BackTrack"};
    @Override
    public void remap(CustomRemapper remapper){
        classNodes().forEach(classNode -> {
            classNode.methods.forEach(methodNode -> {
                classNode.fields.forEach(field -> {
                    Arrays.stream(methodNode.instructions.toArray())
                            .forEach(insnNode ->{
                                if(insnNode.getOpcode() == LDC
                                        && insnNode.getNext().getOpcode() == INVOKESPECIAL
                                        && insnNode.getNext().getNext() != null
                                        && insnNode.getNext().getNext().getOpcode() == INVOKESPECIAL){
                                    for(String uga : modules){
                                        if(((LdcInsnNode)insnNode).cst.toString().contains(uga)){
                                            //checked.set(true);
                                            remapper.mapPackage(classNode.name.substring(0, classNode.name.lastIndexOf('/')),"ru/paimon/remap/module/impl");
                                            remapper.map(classNode.name, uga.replace(" ",""));
                                        } else if(((LdcInsnNode)insnNode).cst.toString().replace(" ","").contains(uga)){
                                            //checked.set(true);
                                            remapper.mapPackage(classNode.name.substring(0, classNode.name.lastIndexOf('/')),"ru/paimon/remap/module/impl");
                                            remapper.map(classNode.name, uga.replace(" ",""));
                                        }
                                    }
                                } else if(insnNode.getOpcode() == LDC
                                        && insnNode.getNext().getOpcode() == LDC
                                        && insnNode.getNext().getNext().getNext() != null
                                        //&& insnNode.getNext().getNext().getOpcode() == INVOKESPECIAL){
                                        && insnNode.getNext().getNext().getNext().getOpcode() == INVOKESPECIAL){
                                    for(String uga : modules){
                                        if(((LdcInsnNode)insnNode).cst.toString().contains(uga)){
                                            //checked.set(true);
                                            remapper.mapPackage(classNode.name.substring(0, classNode.name.lastIndexOf('/')),"ru/paimon/remap/module/impl");
                                            remapper.map(classNode.name, uga.replace(" ",""));
                                        } else if(((LdcInsnNode)insnNode).cst.toString().replace(" ","").contains(uga)){
                                            //checked.set(true);
                                            remapper.mapPackage(classNode.name.substring(0, classNode.name.lastIndexOf('/')),"ru/paimon/remap/module/impl");
                                            remapper.map(classNode.name, uga.replace(" ",""));
                                        }
                                    }
                                } else if(insnNode.getOpcode() == LDC
                                        && insnNode.getNext().getOpcode() == INVOKESPECIAL
                                        && insnNode.getNext().getNext().getOpcode() == PUTSTATIC){
                                    if(((LdcInsnNode)insnNode).cst.toString().contains("#version 120\r\n\r\nuniform sampler2D textureIn, mainTexture;\r\nuniform vec2 texelSize, direction;\r\nuniform float radius;\r\n")){
                                        remapper.map(classNode.name, "ShaderShell");
                                    }
                                } else if(insnNode.getOpcode() == LDC
                                        && insnNode.getNext().getOpcode() == ICONST_1
                                        && insnNode.getNext().getNext().getOpcode() == INVOKESPECIAL
                                        && insnNode.getNext().getNext().getNext().getOpcode() == INVOKEVIRTUAL){
                                    for(String uga : files){
                                        if(((LdcInsnNode)insnNode).cst.toString().contains(uga)){
                                            remapper.map(classNode.name, "FileManager");
                                        }
                                    }
                                } else if(insnNode.getOpcode() == LDC
                                        && insnNode.getNext().getOpcode() == INVOKEVIRTUAL
                                        && insnNode.getNext().getNext().getOpcode() == INVOKEVIRTUAL
                                        && insnNode.getNext().getNext().getNext().getOpcode() == INVOKESPECIAL){
                                    if(((LdcInsnNode)insnNode).cst.toString().equals(".json")){
                                        remapper.map(classNode.name, "CustomFile");
                                    }
                                } else if(insnNode.getOpcode() == INVOKEINTERFACE
                                        && insnNode.getNext().getOpcode() == CHECKCAST
                                        && insnNode.getNext().getNext().getOpcode() == ALOAD
                                        && insnNode.getNext().getNext().getNext().getOpcode() == FCONST_1
                                        && insnNode.getNext().getNext().getNext().getNext().getOpcode() == INVOKESPECIAL){
                                    remapper.map(classNode.name, "CapeHolder");
                                } else if(insnNode.getOpcode() == LDC
                                        && insnNode.getNext().getOpcode() == INVOKEVIRTUAL
                                        && insnNode.getNext().getNext().getOpcode() == IFEQ
                                        && insnNode.getNext().getNext().getNext().getOpcode() == LDC
                                        && insnNode.getNext().getNext().getNext().getNext().getOpcode() == ILOAD){
                                    for(String uga : font){
                                        if(((LdcInsnNode)insnNode).cst.toString().contains(uga)){
                                            remapper.map(classNode.name, "MCFontRenderer");
                                        }
                                    }
                                } else if(insnNode.getOpcode() == ILOAD
                                        && insnNode.getNext().getOpcode() == ILOAD
                                        && insnNode.getNext().getNext().getOpcode() == IF_ICMPGE
                                        && insnNode.getNext().getNext().getNext().getOpcode() == ALOAD
                                        && insnNode.getNext().getNext().getNext().getNext().getOpcode() == ILOAD
                                        && insnNode.getNext().getNext().getNext().getNext().getNext().getOpcode() == AALOAD
                                        && insnNode.getNext().getNext().getNext().getNext().getNext().getNext().getOpcode() == ASTORE
                                        && insnNode.getNext().getNext().getNext().getNext().getNext().getNext().getNext().getOpcode() == ALOAD
                                        && insnNode.getNext().getNext().getNext().getNext().getNext().getNext().getNext().getNext().getOpcode() == INVOKESTATIC
                                        && insnNode.getNext().getNext().getNext().getNext().getNext().getNext().getNext().getNext().getNext().getOpcode() == IFEQ
                                        && insnNode.getNext().getNext().getNext().getNext().getNext().getNext().getNext().getNext().getNext().getNext().getOpcode() == GOTO){
                                    remapper.mapPackage(classNode.name.substring(0, classNode.name.lastIndexOf('/')),"ru/paimon/remap/event");
                                    remapper.map(classNode.name, "EventManager");
                                }
                            });
                    Arrays.stream(methodNode.instructions.toArray())
                            .forEach(insnNode ->{
                                if(insnNode.getOpcode() == LDC
                                        && insnNode.getNext().getOpcode() == PUTFIELD
                                        && insnNode.getNext().getNext().getOpcode() == ALOAD
                                        && insnNode.getNext().getNext().getNext().getOpcode() == LDC
                                        && insnNode.getNext().getNext().getNext().getNext().getOpcode() == PUTFIELD
                                        && insnNode.getNext().getNext().getNext().getNext().getNext().getOpcode() == ALOAD
                                        && insnNode.getNext().getNext().getNext().getNext().getNext().getNext().getOpcode() == LDC
                                        && insnNode.getNext().getNext().getNext().getNext().getNext().getNext().getNext().getOpcode() == PUTFIELD){
                                    for(String uga : names){
                                        if(((LdcInsnNode)insnNode).cst.toString().contains(uga)
                                                || ((LdcInsnNode)insnNode).cst.toString().equalsIgnoreCase(uga)){
                                            remapper.mapPackage(classNode.name.substring(0, classNode.name.lastIndexOf('/')),"ru/paimon/remap");
                                            remapper.map(classNode.name, uga);
                                        }
                                    }
                                }
                            });
                    Arrays.stream(methodNode.instructions.toArray())
                            .forEach(insnNode -> {
                                for(int icon : iconst){
                                    if(insnNode.getOpcode() == LDC
                                            && insnNode.getNext().getOpcode() == icon
                                            && insnNode.getNext().getNext().getOpcode() == LDC){
                                        for(String uga : category){
                                            if(((LdcInsnNode)insnNode).cst.toString().contains(uga)
                                                    || ((LdcInsnNode)insnNode).cst.toString().equalsIgnoreCase(uga)
                                                    || ((LdcInsnNode)insnNode).cst.toString().equals(uga)){
                                                remapper.mapPackage(classNode.name.substring(0, classNode.name.lastIndexOf('/')),"ru/paimon/remap/module");
                                                remapper.map(classNode.name, "ModuleCategory");
                                            }
                                        }
                                    }
                                }
                            });
                });
            });
        });
    }
    public static class Config extends AbstractNormalizer.Config {
        public Config() {
            super(ClientRemapper.class);
        }
    }
}
