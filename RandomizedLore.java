package com.codisimus.plugins.phatloots.addon.randomizedlore;

import com.codisimus.plugins.phatloots.events.MobDropLootEvent;
import com.codisimus.plugins.phatloots.events.MobEquipEvent;
import com.codisimus.plugins.phatloots.events.PlayerLootEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class RandomizedLore 
  extends JavaPlugin 
  implements Listener
{
  private static final String ANYTHING = ".*";
  private static final String REGEX = "%rand[0-9]+-[0-9]+%";
  private static final String RAND = "%rand";
  private static final Random RANDOM = new Random();
  
  public static void main(String[] args) {
    }

  @Override
  public void onEnable()
  {
    Bukkit.getPluginManager().registerEvents((Listener) this, this);
  }
  
  @EventHandler
  public void onPlayerLoot(PlayerLootEvent event)
  {
      event.getItemList().forEach((item) -> {
          randomize(item);
      });
  }
  
  @EventHandler
  public void onMobEquip(MobEquipEvent event)
  {
    randomize(event.getEquipment().getItemInHand());
    for (ItemStack item : event.getEquipment().getArmorContents()) {
      randomize(item);
    }
  }
  
  @EventHandler
  public void onMobDropLoot(MobDropLootEvent event)
  {
      event.getItemList().forEach((item) -> {
          randomize(item);
      });
  }
  
  private static void randomize(ItemStack item)
  {
    if ((item != null) && (item.hasItemMeta()))
    {
      ItemMeta meta = item.getItemMeta();
      if (meta.hasDisplayName()) {
        meta.setDisplayName(randomize(meta.getDisplayName()));
      }
      if (meta.hasLore())
      {
        List<String> newLore = new ArrayList();
        meta.getLore().forEach((line) -> {
            newLore.add(randomize(line));
          });
        meta.setLore(newLore);
      }
      item.setItemMeta(meta);
    }
  }
  
  private static String randomize(String s)
  {
    while (s.matches(".*%rand[0-9]+-[0-9]+%.*"))
    {
      int startIndex = s.indexOf("%rand") + "%rand".length();
      int endIndex = s.indexOf('%', startIndex);
      String range = s.substring(startIndex, endIndex);
      int r;
      try
      {
        String[] split = range.split("-");
        r = random(Integer.parseInt(split[0]), Integer.parseInt(split[1]));
      }
      catch (NumberFormatException e)
      {
        r = -1;
      }
      s = s.replaceFirst("%rand[0-9]+-[0-9]+%", String.valueOf(r));
    }
    return s;
  }
  
  private static int random(int low, int high)
  {
    if (high < low)
    {
      int temp = low;
      low = high;
      high = temp;
    }
    return RANDOM.nextInt(high - low) + low;
  }
}