package com.plusls.carpet.mixin.rule.forceRestock;

import com.plusls.carpet.PluslsCarpetAdditionSettings;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.SpectralArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SpectralArrow.class)
public abstract class MixinSpectralArrow extends AbstractArrow {
    //#if MC <= 12002
    protected MixinSpectralArrow(EntityType<? extends AbstractArrow> entityType, Level world) {
        super(entityType, world);
    }
    //#else
    //$$ protected MixinSpectralArrow(EntityType<? extends AbstractArrow> entityType, Level world, ItemStack item) {
    //$$     super(entityType, world, item);
    //$$ }
    //#endif

    @Inject(
            method = "doPostHurtEffects",
            at = @At(
                    value = "RETURN"
            )
    )
    private void forceRestock(LivingEntity target, CallbackInfo ci) {
        if (PluslsCarpetAdditionSettings.forceRestock && !target.getLevelCompat().isClientSide && target instanceof AbstractVillager) {
            AbstractVillager villager = (AbstractVillager) target;
            for (MerchantOffer tradeOffer : villager.getOffers()) {
                tradeOffer.resetUses();
            }
            // make villager happy ~
            this.getLevelCompat().broadcastEntityEvent(villager, (byte) 14);
        }
    }
}
