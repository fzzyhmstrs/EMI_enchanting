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
        
        Map<Enchantment,EmiIngredient> exclusions = new hashMap<>();
        for (Map.Entry<Enchantment, Collection<ItemStack>> entry : enchantMap.entrySet()){
            if (!entry.getKey().canCombine(enchantment)){
                exclusions.put(entry.getKey(),EmiIngredient.of(entry.getValue().stream().map(EmiStack::of).toList()));
            }
        }
        this.exclusions = exclusions;
        
        List<EmiIngredient> inputs = new ArrayList<>();
        inputs.add(this.books);
        inputs.addAll(this.validItems);
        inputs.addAll(this.exclusions.values());
        this.inputs = inputs;

        Text curse = Text.translatable("emi_enchanting.curse", enchantment.isCursed() ? Text.translatable("emi_enchanting.yes_bad") : Text.translatable("emi_enchanting.no_bad"));
        Text treasure = Text.translatable("emi_enchanting.treasure", enchantment.isTreasure() ? Text.translatable("emi_enchanting.yes_bad") : Text.translatable("emi_enchanting.no_bad"));
        Text tradeable = Text.translatable("emi_enchanting.tradeable", enchantment.isAvailableForEnchantedBookOffer() ? Text.translatable("emi_enchanting.yes_good") : Text.translatable("emi_enchanting.no_good"));
        Text random = Text.translatable("emi_enchanting.random", enchantment.isAvailableForRandomSelection() ? Text.translatable("emi_enchanting.yes_good") : Text.translatable("emi_enchanting.no_good"));
        
        List<PageWidget> widgets = new ArrayList<>();
        
        PageWidget firstPage = new PageWidget(0,0,144,124);
        firstPage.addSlot(this.books,0,0);
        firstPage.addText(enchantment.getName(1),22,0,0x000000,false);
        firstPage.addText(curse,0,20,0x000000,false);
        firstPage.addText(treasre,0,32,0x000000,false);
        firstPage.addText(tradeable,0,44,0x000000,false);
        firstPage.addText(random,0,56,0x000000,false);
        firstPage.addText(Text.translatable("emi_enchanting.valid_items"),0,72,0x000000,false);
        for (int i = 0; i < 8; i++) {
            if (!validItems.get(i).isEmpty())
                firstPage.addSlot(validItems.get(i),84,i*18);
        }
        if (!this.exclusions.isEmpty()){
            firstPage.addButton(0, 112, 12, 12, 0, 0, () -> true, (mouseX, mouseY, button) -> previous());
            firstPage.addButton(132, 112, 12, 12, 12, 0, () -> true, (mouseX, mouseY, button) -> next());
        }
        firstPage.setActive(true);
        widgets.add(firstPage);
        
        int exclusionsIndex = 0;
        List<Map.Entry<Enchantment,EmiIngredient>> exclusionsList = this.exclusions.entrySet().stream().toList();
        while (exclusionsIndex < this.exclusionsList.size()){
            PageWidget exclusionPage = new PageWidget(0,0,144,124);
            exclusionPage.addText(Text.translatable("emi_enchanting.exlcusions"),0,0,0x000000,false);
            for (int i = 0; i < 5; i++){
                int y = 12 + (20 * i)
                exclusionPage.addSlot(this.exclusionsList.get(exclusionsIndex).value(), 0, y);
                exclusionPage.addText(this.exclusionsList.get(exclusionsIndex).key().getName(1),20,y);
                exclusionsIndex++;
                if (exclusionsIndex >= this.exclusionsList.size()) break;
            }
            exclusionsPage.addButton(0, 112, 12, 12, 0, 0, () -> true, (mouseX, mouseY, button) -> previous());
            exclusionsPage.addButton(132, 112, 12, 12, 12, 0, () -> true, (mouseX, mouseY, button) -> next());
            widgets.add(exclusionsPage);
        }
        this.pageWidgets = widgets;
        this.maxPage = this.pageWidgets.size() - 1
    }

    private final Enchantment enchantment;
    private final EmiIngredient books;
    private final List<EmiIngredient> validItems;
    private final Map<Enchantment,EmiIngredient> exclusions;
    private final List<EmiIngredient> inputs;
    
    private final List<PageWidget> pageWidgets;
    
    private int currentPage = 0;
    private final int maxPage;

    private void previous(){
        for (PageWidget widget : widgets){
            widget.setActive(false);
        }
        currentPage--;
        if (currentPage < 0)
            currentPage = maxPage;
        widgets.get(currentPage).setActive(true);
    }
    private void next(){
        for (PageWidget widget : widgets){
            widget.setActive(false);
        }
        currentPage++;
        if (currentPage > maxPage)
            currentPage = 0;
        widgets.get(currentPage).setActive(true);
    }
    
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
        return 124;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        for(PageWidget widget : pageWidgets){
            widgets.add(widget);
        }
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
