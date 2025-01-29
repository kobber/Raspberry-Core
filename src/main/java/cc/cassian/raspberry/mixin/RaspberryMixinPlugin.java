package cc.cassian.raspberry.mixin;

import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.LoadingModList;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class RaspberryMixinPlugin implements IMixinConfigPlugin {
    @Override
    public void onLoad(String mixinPackage) {
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if (mixinClassName.contains("cc.cassian.raspberry.mixin.environmental") && LoadingModList.get().getModFileById("environmental") == null){
            return false;
        }
        else if (mixinClassName.contains("cc.cassian.raspberry.mixin.create") && LoadingModList.get().getModFileById("create") == null){
            return false;
        }
        else if (mixinClassName.contains("cc.cassian.raspberry.mixin.farmersdelight") && LoadingModList.get().getModFileById("farmersdelight") == null){
            return false;
        }
        else if (mixinClassName.contains("cc.cassian.raspberry.mixin.naturalist") && LoadingModList.get().getModFileById("naturalist") == null){
            return false;
        }
        else if (mixinClassName.contains("cc.cassian.raspberry.mixin.upgrade_aquatic") && LoadingModList.get().getModFileById("upgrade_aquatic") == null){
            return false;
        }
        return true;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {

    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }
}
