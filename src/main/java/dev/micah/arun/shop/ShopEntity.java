package dev.micah.arun.shop;

import dev.micah.arun.utils.Chat;
import org.bukkit.entity.Villager;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class ShopEntity {

    public ShopEntity(Villager entity) {
        entity.setCustomName(Chat.color("&cKill Points Shop"));
        entity.setCustomNameVisible(true);
        entity.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 255, false, false));
    }

}
