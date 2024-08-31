package fzzyhmstrs.emi_enchanting;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import fzzyhmstrs.emi_enchanting.widget.PageWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.EnchantmentTags;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.text.MutableText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class EnchantmentRecipe implements EmiRecipe {

    public EnchantmentRecipe(Collection<ItemStack> books, World world, RegistryEntry<Enchantment> enchantment, Map<RegistryEntry<Enchantment>, Collection<ItemStack>> enchantMap){
        this.enchantment = enchantment;
        List<EmiStack> bookStacks = books.stream().map(EmiStack::of).toList();
        EmiIngredient books1 = EmiIngredient.of(bookStacks);
        this.bookStacks = bookStacks;


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
        for (RegistryEntry<Item> item : enchantment.value().getApplicableItems()) {
            validItems.get(counter % 8).add(EmiStack.of(item.value()));
            counter++;
        }
        List<EmiIngredient> validItems1 = validItems.stream().map(EmiIngredient::of).toList();

        Map<RegistryEntry<Enchantment>, EmiIngredient> exclusions = new HashMap<>();
        for (Map.Entry<RegistryEntry<Enchantment>, Collection<ItemStack>> entry : enchantMap.entrySet()){
            if (!Enchantment.canBeCombined(enchantment, entry.getKey())) {
                List<EmiStack> stacks = entry.getValue().stream().map(EmiStack::of).toList();
                exclusions.put(entry.getKey(), EmiIngredient.of(stacks));
            }
        }

        List<EmiIngredient> inputs = new ArrayList<>();
        inputs.add(books1);
        inputs.addAll(validItems1);
        inputs.addAll(exclusions.values());
        this.inputs = inputs;

        Text curse = Text.translatable("emi_enchanting.curse", enchantment.isIn(EnchantmentTags.CURSE) ? Text.translatable("emi_enchanting.yes_bad") : Text.translatable("emi_enchanting.no_bad"));
        Text treasure = Text.translatable("emi_enchanting.treasure", enchantment.isIn(EnchantmentTags.TREASURE) && !enchantment.isIn(EnchantmentTags.NON_TREASURE) ? Text.translatable("emi_enchanting.yes_bad") : Text.translatable("emi_enchanting.no_bad"));

        boolean trades = !(world.getEnabledFeatures().contains(FeatureFlags.TRADE_REBALANCE))
                ?
            enchantment.isIn(EnchantmentTags.TRADEABLE) || enchantment.isIn(EnchantmentTags.ON_TRADED_EQUIPMENT)
                :
            enchantment.isIn(EnchantmentTags.TAIGA_SPECIAL_TRADE) ||
            enchantment.isIn(EnchantmentTags.SWAMP_SPECIAL_TRADE) ||
            enchantment.isIn(EnchantmentTags.SNOW_SPECIAL_TRADE) ||
            enchantment.isIn(EnchantmentTags.SAVANNA_SPECIAL_TRADE) ||
            enchantment.isIn(EnchantmentTags.PLAINS_SPECIAL_TRADE) ||
            enchantment.isIn(EnchantmentTags.JUNGLE_SPECIAL_TRADE) ||
            enchantment.isIn(EnchantmentTags.DESERT_SPECIAL_TRADE) ||
            enchantment.isIn(EnchantmentTags.TAIGA_COMMON_TRADE) ||
            enchantment.isIn(EnchantmentTags.SWAMP_COMMON_TRADE) ||
            enchantment.isIn(EnchantmentTags.SNOW_COMMON_TRADE) ||
            enchantment.isIn(EnchantmentTags.SAVANNA_COMMON_TRADE) ||
            enchantment.isIn(EnchantmentTags.PLAINS_COMMON_TRADE) ||
            enchantment.isIn(EnchantmentTags.JUNGLE_COMMON_TRADE) ||
            enchantment.isIn(EnchantmentTags.DESERT_COMMON_TRADE);

        Text tradeable = Text.translatable("emi_enchanting.tradeable", trades ? Text.translatable("emi_enchanting.yes_good") : Text.translatable("emi_enchanting.no_good"));
        Text random = Text.translatable("emi_enchanting.random", (enchantment.isIn(EnchantmentTags.IN_ENCHANTING_TABLE) || enchantment.isIn(EnchantmentTags.ON_RANDOM_LOOT)) ? Text.translatable("emi_enchanting.yes_good") : Text.translatable("emi_enchanting.no_good"));
        Text maxLvl = Text.translatable("emi_enchanting.maxLvl", Text.literal(String.valueOf(enchantment.value().getMaxLevel())).formatted(Formatting.BLACK));

        String descLangKey = Util.createTranslationKey("enchantment", enchantment.getKey().map(RegistryKey::getValue).orElse(Identifier.of(EmiEnchanting.MOD_ID, "unknown"))) + ".desc";
        MutableText descText = Text.translatable(descLangKey);

        List<PageWidget> widgets = new ArrayList<>();

        PageWidget firstPage = new PageWidget(0,0,144,124);
        firstPage.addSlot(books1,0,0);
        firstPage.addText(Enchantment.getName(enchantment, 1).copyContentOnly().formatted(Formatting.BLACK),22,2,0x000000,false);
        firstPage.addText(curse,0,20,0x000000,false);
        firstPage.addText(treasure,0,31,0x000000,false);
        firstPage.addText(tradeable,0,42,0x000000,false);
        firstPage.addText(random,0,53,0x000000,false);
        firstPage.addText(maxLvl,0,64,0x000000,false);
        firstPage.addText(Text.translatable("emi_enchanting.valid_items"),0,82,0x000000,false);
        for (int i = 0; i < 8; i++) {
            if (!validItems.get(i).isEmpty())
                firstPage.addSlot(validItems1.get(i),i*18,92);
        }
        if (!exclusions.isEmpty() || !Objects.equals(descText.getString(), descLangKey)){
            firstPage.addButton(0, 112, 12, 12, 0, 0, () -> true, (mouseX, mouseY, button) -> previous());
            firstPage.addButton(132, 112, 12, 12, 12, 0, () -> true, (mouseX, mouseY, button) -> next());
        }
        firstPage.setActive(true);
        widgets.add(firstPage);


        if (!Objects.equals(descText.getString(), descLangKey)){
            PageWidget descriptionPage = new PageWidget(0,0,144,124);
            descriptionPage.addText(Text.translatable("emi_enchanting.description"),2,2,0x000000,false);
            List<OrderedText> descriptionLines = MinecraftClient.getInstance().textRenderer.wrapLines(descText.formatted(Formatting.ITALIC),140);
            int y = 16;
            for (OrderedText text : descriptionLines){
                descriptionPage.addText(text,2,y,0x000000,false);
                y+= 11;
            }
            descriptionPage.addButton(0, 112, 12, 12, 0, 0, () -> true, (mouseX, mouseY, button) -> previous());
            descriptionPage.addButton(132, 112, 12, 12, 12, 0, () -> true, (mouseX, mouseY, button) -> next());
            widgets.add(descriptionPage);
        }

        int exclusionsIndex = 0;
        List<Map.Entry<RegistryEntry<Enchantment>, EmiIngredient>> exclusionsList = exclusions.entrySet().stream().toList();
        while (exclusionsIndex < exclusions.size()){
            PageWidget exclusionPage = new PageWidget(0,0,144,124);
            exclusionPage.addText(Text.translatable("emi_enchanting.exclusions"),0,0,0x000000,false);
            for (int i = 0; i < 5; i++){
                int y = 14 + (20 * i);
                exclusionPage.addSlot(exclusionsList.get(exclusionsIndex).getValue(), 0, y);
                exclusionPage.addText(Enchantment.getName(exclusionsList.get(exclusionsIndex).getKey(),1).copyContentOnly().formatted(Formatting.BLACK),20,y,0x000000,false);
                exclusionsIndex++;
                if (exclusionsIndex >= exclusionsList.size()) break;
            }
            exclusionPage.addButton(0, 112, 12, 12, 0, 0, () -> true, (mouseX, mouseY, button) -> previous());
            exclusionPage.addButton(132, 112, 12, 12, 12, 0, () -> true, (mouseX, mouseY, button) -> next());
            widgets.add(exclusionPage);
        }
        this.pageWidgets = widgets;
        this.maxPage = this.pageWidgets.size() - 1;
    }

    private final RegistryEntry<Enchantment> enchantment;

    private final List<EmiStack> bookStacks;

    private final List<EmiIngredient> inputs;

    private final List<PageWidget> pageWidgets;

    private int currentPage = 0;
    private final int maxPage;

    private void previous(){
        for (PageWidget widget : pageWidgets){
            widget.setActive(false);
        }
        currentPage--;
        if (currentPage < 0)
            currentPage = maxPage;
        pageWidgets.get(currentPage).setActive(true);
    }
    private void next(){
        for (PageWidget widget : pageWidgets){
            widget.setActive(false);
        }
        currentPage++;
        if (currentPage > maxPage)
            currentPage = 0;
        pageWidgets.get(currentPage).setActive(true);
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return EmiClientPlugin.ENCHANTING_CATEGORY;
    }

    @Override
    public @Nullable Identifier getId() {
        Identifier id = enchantment.getKey().map(RegistryKey::getValue).orElse(Identifier.of(EmiEnchanting.MOD_ID, "unknown"));
        if (id == null) return null;
        return Identifier.of(EmiEnchanting.MOD_ID,"/" + id.toTranslationKey() + "/enchanting_info" );
    }

    @Override
    public List<EmiIngredient> getInputs() {
        return inputs;
    }

    @Override
    public List<EmiIngredient> getCatalysts() {
        return EmiRecipe.super.getCatalysts();
    }

    @Override
    public List<EmiStack> getOutputs() {
        return bookStacks;
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