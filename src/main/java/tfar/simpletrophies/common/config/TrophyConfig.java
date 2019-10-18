package tfar.simpletrophies.common.config;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class TrophyConfig {


  public static final ClientConfig CLIENT;
  public static final ForgeConfigSpec CLIENT_SPEC;

  static {
    final Pair<ClientConfig, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(ClientConfig::new);
    CLIENT_SPEC = specPair.getRight();
    CLIENT = specPair.getLeft();
  }

  public static class ClientConfig {

    public static ForgeConfigSpec.BooleanValue SKIP_ITEM_BASES;
    public static ForgeConfigSpec.BooleanValue SKIP_ITEM_ITEMS;
    public static ForgeConfigSpec.BooleanValue SKIP_BLOCK_ITEMS;
    public static ForgeConfigSpec.BooleanValue NO_TEISR;
    public static ForgeConfigSpec.BooleanValue NO_TESR;
    public static ForgeConfigSpec.BooleanValue TOOLTIP_CREDITS;
    public static ForgeConfigSpec.BooleanValue SHOW_EARNEDAT;

    ClientConfig(ForgeConfigSpec.Builder builder) {

      builder.push("general");
      SKIP_ITEM_BASES = builder
              .comment("Don't show trophy bases on trophies in your inventory and on other GUIs. Saves on performance.")
              .define("skip item bases",false);
      SKIP_ITEM_ITEMS = builder
              .comment("Don't show the items on top of trophies in your inventory and on other GUIs. Saves on performance.")
              .define("skip item items",false);
      SKIP_BLOCK_ITEMS = builder
              .comment("Don't show the items on top of trophies in your inventory and on other GUIs. Saves on performance.")
              .define("skip block items",false);
      NO_TESR = builder
              .comment("Emergency killswitch for the tile entity renderer. Enable in cases of extreme performance issues or client rendering-related crashes.\n(Requires a game restart in some cases.)")
              .define("no tesr",false);
      NO_TEISR = builder
              .comment("Emergency killswitch for the in-inventory trophy renderer. Enable in cases of extreme performance issues or client rendering-related crashes.\n(Requires a game restart in some cases.)\nIf this option is enabled, and skipItemBases is not, trophy item bases will render using a 'fast path' that is about as expensive as rendering a grass block item. This fast path is not compatible with the fancy trophy TEISR, to my knowledge.")
              .define("no teisr",false);
      TOOLTIP_CREDITS = builder
              .comment("Display the author of trophy models on their tooltips.")
              .define("tooltip credits",false);
      SHOW_EARNEDAT = builder
              .comment("Show the date and time you earned the trophy on the tooltip and on hover.")
              .define("show earnedat",true);
      builder.pop();
    }
  }

  public static class ServerConfig{}

}
