/*
 * SkySkipped - Hypixel Skyblock QOL mod
 * Copyright (C) 2023  Cephetir
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package me.cephetir.skyskipped.transformers;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.TypeInsnNode;

public class NetworkManagerTransformer implements IClassTransformer {
    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (name.equals("me.cephetir.skyskipped.mixins.MixinNetworkManager")) {
            try {
                ClassReader reader = new ClassReader(basicClass);
                ClassNode classNode = new ClassNode();
                reader.accept(classNode, 0);
                classNode.methods.stream().filter(methodNode -> methodNode.name.equals("createNetworkManagerAndConnect")).forEach(methodNode -> {
                    for (int i = 0; i < methodNode.instructions.size(); ++i) {
                        AbstractInsnNode abstractInsnNode = methodNode.instructions.get(i);
                        if (abstractInsnNode instanceof TypeInsnNode) {
                            TypeInsnNode tin = (TypeInsnNode) abstractInsnNode;
                            if (!tin.desc.equals("me/cephetir/skyskipped/mixins/MixinNetworkManager$1")) continue;
                            ((TypeInsnNode) abstractInsnNode).desc = "net/minecraft/network/NetworkManager$5";
                            continue;
                        }
                        if (!(abstractInsnNode instanceof MethodInsnNode)) continue;
                        MethodInsnNode min = (MethodInsnNode) abstractInsnNode;
                        if (!min.owner.equals("me/cephetir/skyskipped/mixins/MixinNetworkManager$1") || !min.name.equals("<init>")) continue;
                        min.owner = "net/minecraft/network/NetworkManager$5";
                    }
                });
                ClassWriter writer = new ClassWriter(1);
                classNode.accept(writer);
                return writer.toByteArray();
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }
        return basicClass;
    }
}

