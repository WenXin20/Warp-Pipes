package com.wenxin2.warp_pipes.mixin.iris;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.wenxin2.warp_pipes.WarpPipes;
import com.wenxin2.warp_pipes.registries.ModRegistry;
import java.util.Collections;
import java.util.List;
import net.irisshaders.iris.shaderpack.IdMap;
import net.irisshaders.iris.shaderpack.materialmap.BlockEntry;
import net.irisshaders.iris.shaderpack.materialmap.NamespacedId;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(IdMap.class)
public class IdMapMixin {
    @WrapOperation(method = "lambda$parseBlockMap$2", at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z"), remap = false)
    private static boolean addBlockEntry(List instance, Object entry, Operation<Boolean> original) {
        if (entry instanceof BlockEntry blockEntry) {
            if (blockEntry.id().getNamespace().equals("minecraft") && blockEntry.id().getName().equals("water"))
                original.call(instance, new BlockEntry(new NamespacedId(WarpPipes.MOD_ID, ModRegistry.WATER_SPOUT.getId().getPath()), Collections.emptyMap()));
        }
        return original.call(instance, entry);
    }
}
