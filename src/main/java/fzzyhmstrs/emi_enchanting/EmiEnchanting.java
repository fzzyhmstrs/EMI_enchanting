package fzzyhmstrs.emi_enchanting;

import net.fabricmc.api.ModInitializer;
import net.minecraft.util.math.random.LocalRandom;
import net.minecraft.util.math.random.Random;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EmiEnchanting {

    public static String MOD_ID = "emi_enchanting";
    public static final Logger LOGGER = LoggerFactory.getLogger("emi_enchanting");
    public static Random emiEnchantingRandom = new LocalRandom(System.currentTimeMillis());
}
