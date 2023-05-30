package com.feroov.frv.item.tiers;

import com.feroov.frv.STLCON;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;

import static net.minecraft.sounds.SoundEvents.ARMOR_EQUIP_GENERIC;
import static net.minecraft.sounds.SoundEvents.ARMOR_EQUIP_IRON;

public enum ArmorMaterialSTLCON implements ArmorMaterial
{
    XENITE(3,6,5,2,0F,350,10, ARMOR_EQUIP_IRON,"xenite_ingot"),

    ;


    final String textureLocation, name;
    final SoundEvent equipSound;
    final Ingredient repairIngredient;
    final int headArmor, chestArmor, legsArmor, feetArmor, durability, enchantability;
    final float toughness;

    ArmorMaterialSTLCON(int headArmor, int chestArmor, int legsArmor, int feetArmor, float toughness, int durability, int enchantability)
    {
        this(null, null, headArmor, chestArmor, legsArmor, feetArmor, toughness, durability, enchantability, null, null);
    }
    ArmorMaterialSTLCON(int headArmor, int chestArmor, int legsArmor, int feetArmor, float toughness, int durability, int enchantability, @Nullable SoundEvent equipSound)
    {
        this(null, null, headArmor, chestArmor, legsArmor, feetArmor, toughness, durability, enchantability, equipSound, null);
    }
    ArmorMaterialSTLCON(int headArmor, int chestArmor, int legsArmor, int feetArmor, float toughness, int durability, int enchantability, @Nullable String repairIngredient)
    {
        this(null, null, headArmor, chestArmor, legsArmor, feetArmor, toughness, durability, enchantability, null, repairIngredient);
    }
    ArmorMaterialSTLCON(int headArmor, int chestArmor, int legsArmor, int feetArmor, float toughness, int durability, int enchantability, @Nullable SoundEvent equipSound, @Nullable String repairIngredient)
    {
        this(null, null, headArmor, chestArmor, legsArmor, feetArmor, toughness, durability, enchantability, equipSound, repairIngredient);
    }
    ArmorMaterialSTLCON(@Nullable String customTextureLocation, @Nullable String name, int headArmor, int chestArmor, int legsArmor, int feetArmor, float toughness, int durability, int enchantability, @Nullable SoundEvent equipSound, @Nullable String repairIngredient)
    {
        String nonnullName = name == null ? toString().toLowerCase() : name;
        this.textureLocation = customTextureLocation == null ? "drpg_" + nonnullName + "armor" : customTextureLocation;
        this.name = STLCON.MOD_ID + ":" + nonnullName;
        this.headArmor = headArmor;
        this.chestArmor = chestArmor;
        this.legsArmor = legsArmor;
        this.feetArmor = feetArmor;
        this.toughness = toughness;
        this.durability = durability;
        this.enchantability = enchantability;
        this.equipSound = equipSound == null ? ARMOR_EQUIP_GENERIC : equipSound;
        this.repairIngredient = repairIngredient == null ? Ingredient.EMPTY : Ingredient.of(ForgeRegistries.ITEMS.getValue(new ResourceLocation(STLCON.MOD_ID, repairIngredient)));

    }
    public String getTextureLocation() {return textureLocation;}

    @Override public int getDurabilityForType(ArmorItem.Type type) {return durability;}
    @Override public int getEnchantmentValue() {return enchantability;}
    @Override public SoundEvent getEquipSound() {return equipSound;}
    @Override public Ingredient getRepairIngredient() {return repairIngredient;}
    @Override public String getName() {return name;}
    @Override public float getToughness() {return toughness;}
    @Override public float getKnockbackResistance() {return 0;}
    @Override public int getDefenseForType(ArmorItem.Type type) {
        return switch(type) {
            case HELMET -> headArmor;
            case CHESTPLATE -> chestArmor;
            case LEGGINGS -> legsArmor;
            default -> feetArmor;
        };
    }
}
