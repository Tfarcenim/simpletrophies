package tfar.simpletrophies.common.config;

import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import tfar.simpletrophies.SimpleTrophies;

import java.util.List;

@Mod.EventBusSubscriber
public class SimpleTrophiesConfig {

	
	public static List<CompoundNBT> CREATIVETAB_TAGS;
	
	public static String DEFAULT_CREATIVETAB_STR = "{TrophyName:\"Add your own trophies here in the config!\",TrophyVariant:\"classic\",TrophyItem:{id:\"minecraft:diamond_axe\",Count:1b,Damage:0s},TrophyColorRed:65,TrophyColorGreen:205,TrophyColorBlue:52}";

	
	private static void load() {

		/*
		String[] tagStrings = config.getStringList("creativeTabTrophies", "client", new String[] { DEFAULT_CREATIVETAB_STR }, "Trophy tags that will be displayed on the Simple Trophies creative tab. Obtain them by right clicking a trophy in the air in creative. One per line, please.\n\n");
		*/
		/*CREATIVETAB_TAGS = new ArrayList<>();
		for(String s : tagStrings) {
			try {
				CREATIVETAB_TAGS.add(JsonToNBT.getTagFromJson(s));
			} catch(CommandSyntaxException e) {
				SimpleTrophies.LOG.error("Can't parse this NBT tag: " + s, e);
			}
		}*/


	}
	
	@SubscribeEvent
	public static void configChanged(ConfigChangedEvent.OnConfigChangedEvent e) {
		if(e.getModID().equals(SimpleTrophies.MODID)) {
			load();
		}
	}
}
