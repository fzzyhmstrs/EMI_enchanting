package fzzyhmstrs.emi_enchanting;

import com.google.common.collect.ArrayListMultimap;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class EnchantmentRecipe implements EmiRecipe {

    public EnchantmentRecipe(Collection<ItemStack> books, Enchantment enchantment, Map<Enchantment, Collection<ItemStack>> enchantMap){
        this.enchantment = enchantment;
        this.books = EmiIngredient.of(books.stream().map(EmiStack::of).toList());
        List<List<EmiStack>> validItems = List.of(
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>());
        int counter = 0;
        for (Item item : Registries.ITEM){
            if (enchantment.isAcceptableItem(new ItemStack(item))){
                validItems.get(counter % 8).add(EmiStack.of(item));
                counter++;
            }
        }
        this.validItems = validItems.stream().map(EmiIngredient::of).toList();
        List<EmiIngredient> exclusions = new ArrayList<>();
        for (Map.Entry<Enchantment, Collection<ItemStack>> entry : enchantMap.entrySet()){
            if (!entry.getKey().canCombine(enchantment)){
                exclusions.add(EmiIngredient.of(entry.getValue().stream().map(EmiStack::of).toList()));
            }
        }
        this.exclusions = exclusions;
        List<EmiIngredient> inputs = new ArrayList<>();
        inputs.add(this.books);
        inputs.addAll(this.validItems);
        inputs.addAll(this.exclusions);
        this.inputs = inputs;
    }

    private final Enchantment enchantment;
    private final EmiIngredient books;
    private final List<EmiIngredient> validItems;
    private final List<EmiIngredient> exclusions;
    private final List<EmiIngredient> inputs;

    @Override
    public EmiRecipeCategory getCategory() {
        return EmiClientPlugin.ENCHANTING_CATEGORY;
    }

    @Override
    public @Nullable Identifier getId() {
        Identifier id = Registries.ENCHANTMENT.getId(enchantment);
        if (id == null) return null;
        return new Identifier(EmiEnchanting.MOD_ID,"/" + id.toTranslationKey() + "/enchanting_info" );
    }

    @Override
    public List<EmiIngredient> getInputs() {
        return new LinkedList<>();
    }

    @Override
    public List<EmiIngredient> getCatalysts() {
        return EmiRecipe.super.getCatalysts();
    }

    @Override
    public List<EmiStack> getOutputs() {
        return List.of();
    }

    @Override
    public int getDisplayWidth() {
        return 144;
    }

    @Override
    public int getDisplayHeight() {
        return 100;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {

    }

    @Override
    public boolean supportsRecipeTree() {
        return false;
    }

    @Override
    public boolean hideCraftable() {
        return EmiRecipe.super.hideCraftable();
    }
}
