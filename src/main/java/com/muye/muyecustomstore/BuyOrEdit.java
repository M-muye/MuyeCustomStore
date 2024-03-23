package com.muye.muyecustomstore;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

import static com.muye.muyecustomstore.MuyeCustomStore.*;
import static com.muye.muyecustomstore.Other.Replace;
import static com.muye.muyecustomstore.Reload.Buy;

public class BuyOrEdit {
    public static Inventory PapiBuy;
    public static void BuyItem(Player player, ItemStack item) {
        Buy.setItem(Reload.slot,item);
        PapiBuy = Bukkit.createInventory(null,Buy.getSize(),plugin.getPapi(player,Replace(Buy.getTitle())));
        for (int i = 0; i < Buy.getSize(); i++) {
            ItemStack buyItem = Buy.getItem(i);
            if (item != null) {
                ItemMeta itemMeta = item.getItemMeta();
                if (itemMeta.getDisplayName() != null){
                    itemMeta.setDisplayName(plugin.getPapi(player,Replace(itemMeta.getDisplayName())));
                }
                List<String> lore = itemMeta.getLore();
                if (StoreType.get(openStore.name.get(openStore.gui)).equals("money") && lore != null){
                    lore.replaceAll(s -> plugin.getPapi(player,s.replace("%type%", "§e金币").replace("%value%", String.valueOf(MoneyGoods.get(item.getType().toString()+"="+item.getData().getData())))));
                }
                if (StoreType.get(openStore.name.get(openStore.gui)).equals("point") && lore != null){
                    lore.replaceAll(s -> plugin.getPapi(player,s.replace("%type%", "§e金币").replace("%value%", String.valueOf(PointGoods.get(item.getType().toString()+"="+item.getData().getData())))));
                }
                itemMeta.setLore(lore);
                item.setItemMeta(itemMeta);
                PapiBuy.setItem(i, buyItem);
            }
        }
        player.openInventory(PapiBuy);
    }
    public static void EditItem(Player player, ItemStack item){
        Reload.Edit.setItem(Reload.slot2,item);
        player.openInventory(Reload.Edit);
    }
}
