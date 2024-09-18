package fzzyhmstrs.emi_enchanting;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableMap;
import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.client.MinecraftClient;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.Collection;
import java.util.Map;

@EmiEntrypoint
public class EmiClientPlugin implements EmiPlugin {

    private static final Identifier ENCHANTING_ID = Identifier.of(EmiEnchanting.MOD_ID,"enchantments");
    public static final EmiRecipeCategory ENCHANTING_CATEGORY = new EmiRecipeCategory(ENCHANTING_ID, EmiStack.of(Items.ENCHANTED_BOOK));


    @Override
    public void register(EmiRegistry registry) {

        registry.addCategory(ENCHANTING_CATEGORY);

        World world = MinecraftClient.getInstance().world;
        if (world != null) {
            DynamicRegistryManager manager = world.getRegistryManager();

            ArrayListMultimap<RegistryEntry<Enchantment>, ItemStack> enchantsMap = ArrayListMultimap.create();

            for (RegistryEntry<Enchantment> enchantment : manager.get(RegistryKeys.ENCHANTMENT).getIndexedEntries()) {
                for (int i = 1; i <= enchantment.value().getMaxLevel(); ++i) {
                    enchantsMap.put(enchantment, EnchantedBookItem.forEnchantment(new EnchantmentLevelEntry(enchantment, i)));
                }
            }

            Map<RegistryEntry<Enchantment>, Collection<ItemStack>> enchantsMapAsMap = ImmutableMap.copyOf(enchantsMap.asMap());

            for (Map.Entry<RegistryEntry<Enchantment>, Collection<ItemStack>> entry : enchantsMapAsMap.entrySet()) {
                registry.addRecipe(new EnchantmentRecipe(entry.getValue(), world, entry.getKey(), enchantsMapAsMap));
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


    }
}