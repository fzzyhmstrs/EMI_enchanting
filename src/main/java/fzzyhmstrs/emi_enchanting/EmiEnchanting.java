package fzzyhmstrs.emi_enchanting;

import net.minecraft.util.math.random.LocalRandom;
import net.minecraft.util.math.random.Random;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod("emi_enchanting")
public class EmiEnchanting {

    public static String MOD_ID = "emi_enchanting";
    public static final Logger LOGGER = LoggerFactory.getLogger("emi_enchanting");
    public static Random emiEnchantingRandom = new LocalRandom(System.currentTimeMillis());
}