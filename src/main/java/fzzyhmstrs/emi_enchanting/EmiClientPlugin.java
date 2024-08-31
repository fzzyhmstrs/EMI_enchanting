package fzzyhmstrs.emi_enchanting;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableMap;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.util.Collection;
import java.util.Map;

public class EmiClientPlugin implements EmiPlugin {

    private static final Identifier ENCHANTING_ID = new Identifier(EmiEnchanting.MOD_ID,"enchantments");
    public static final EmiRecipeCategory ENCHANTING_CATEGORY = new EmiRecipeCategory(ENCHANTING_ID, EmiStack.of(Items.ENCHANTED_BOOK));


    @Override
    public void register(EmiRegistry registry) {

        registry.addCategory(ENCHANTING_CATEGORY);

        ArrayListMultimap<Enchantment, ItemStack> enchantsMap = ArrayListMultimap.create();

        for (Enchantment enchantment : Registries.ENCHANTMENT) {
            for (int i = 1; i <= enchantment.getMaxLevel(); ++i) {
                enchantsMap.put(enchantment, EnchantedBookItem.forEnchantment(new EnchantmentLevelEntry(enchantment, i)));
            }
        }

        /*for (ItemStack stack : ItemGroups.getSearchGroup().getDisplayStacks()) {
            if (stack.isOf(Items.ENCHANTED_BOOK)) {
                Map<Enchantment, Integer> map = EnchantmentHelper.get(stack);
                for (Enchantment key : map.keySet()) {
                    enchantsMap.put(key, stack.copy());
                }
            }
        }*/

        Map<Enchantment, Collection<ItemStack>> enchantsMapAsMap = ImmutableMap.copyOf(enchantsMap.asMap());

        for (Map.Entry<Enchantment, Collection<ItemStack>> entry : enchantsMapAsMap.entrySet()) {
            registry.addRecipe(new EnchantmentRecipe(entry.getValue(), entry.getKey(), enchantsMapAsMap));
        }
    }
}