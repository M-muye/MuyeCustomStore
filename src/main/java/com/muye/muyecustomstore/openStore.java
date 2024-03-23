package com.muye.muyecustomstore;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.muye.muyecustomstore.MuyeCustomStore.*;
import static com.muye.muyecustomstore.Other.Replace;
import static com.muye.muyecustomstore.Reload.pages;

public class openStore {
    public static Map<Inventory, String> name = new HashMap<>();
    public static Inventory gui;
    public static Integer ye = 1;
    public static Integer last;
    public static List<String> Lore;
    private static List<ItemStack> nowItem = null;
    public static Player playerPapi;

    public static void open(Player player,String storeName,Integer page){
        playerPapi = player;
        ye = page;
        last = pages.get(storeName);
        if (last == null){
            last = 1;
        }
        gui = Bukkit.createInventory(null,stores.get(storeName).getSize(), plugin.getPapi(player,Replace(stores.get(storeName).getTitle()).replace("%money%", String.valueOf(economyManager.getBalance(Bukkit.getOfflinePlayer(player.getUniqueId())))).replace("%point%", String.valueOf(playerPointsManager.getAPI().look(player.getUniqueId()))).replace("%page%",String.valueOf(openStore.ye)).replace("%pages%",String.valueOf(openStore.last))));
        for (int i = 0; i < stores.get(storeName).getSize(); i++) {
            ItemStack item = stores.get(storeName).getItem(i);
            if (item != null) {
                ItemMeta itemMeta = item.getItemMeta();
                itemMeta.setDisplayName(plugin.getPapi(player,Replace(itemMeta.getDisplayName())));
                List<String> lore = itemMeta.getLore();
                if (StoreType.get(storeName).equals("money") && lore != null){
                    lore.replaceAll(s -> plugin.getPapi(player,s.replace("%money%", String.valueOf(economyManager.getBalance(Bukkit.getOfflinePlayer(player.getUniqueId())))).replace("%point%", String.valueOf(playerPointsManager.getAPI().look(player.getUniqueId()))).replace("%page%",String.valueOf(openStore.ye)).replace("%pages%",String.valueOf(openStore.last)).replace("%type%", "§e金币")));
                }
                if (StoreType.get(storeName).equals("point") && lore != null){
                    lore.replaceAll(s -> plugin.getPapi(player,s.replace("%money%", String.valueOf(economyManager.getBalance(Bukkit.getOfflinePlayer(player.getUniqueId())))).replace("%point%", String.valueOf(playerPointsManager.getAPI().look(player.getUniqueId()))).replace("%page%",String.valueOf(openStore.ye)).replace("%pages%",String.valueOf(openStore.last)).replace("%type%", "§9点券")));
                }
                itemMeta.setLore(lore);
                item.setItemMeta(itemMeta);
                gui.setItem(i, item);
            }
        }
        if (GuiGoods.get(storeName) != null){
            List<ItemStack> itemStacks = new ArrayList<>();
            for (String good : GuiGoods.get(storeName)){
                List<String> lore = new ArrayList<>(Lore);
                String[] split = good.split("=");
                short data = Short.parseShort(split[1]);
                ItemStack itemStack = new ItemStack(Material.getMaterial(split[0]), 1, data);
                ItemMeta itemMeta = itemStack.getItemMeta();
                if (StoreType.get(storeName).equals("money")){
                    lore.replaceAll(s -> plugin.getPapi(player,s.replace("%page%",String.valueOf(openStore.ye)).replace("%pages%",String.valueOf(openStore.last)).replace("%money%", String.valueOf(economyManager.getBalance(Bukkit.getOfflinePlayer(player.getUniqueId())))).replace("%point%", String.valueOf(playerPointsManager.getAPI().look(player.getUniqueId()))).replace("%type%", "§e金币").replace("%value%", String.valueOf(MoneyGoods.get(good)))));
                }
                if (StoreType.get(storeName).equals("point")){
                    lore.replaceAll(s -> plugin.getPapi(player,s.replace("%page%",String.valueOf(openStore.ye)).replace("%pages%",String.valueOf(openStore.last)).replace("%money%", String.valueOf(economyManager.getBalance(Bukkit.getOfflinePlayer(player.getUniqueId())))).replace("%point%", String.valueOf(playerPointsManager.getAPI().look(player.getUniqueId()))).replace("%type%", "§9点券").replace("%value%", String.valueOf(PointGoods.get(good)))));
                }
                itemMeta.setLore(lore);
                itemStack.setItemMeta(itemMeta);
                itemStacks.add(itemStack);
            }
            if (nowItem != null){
                for (ItemStack item : nowItem){
                    gui.clear(GoodsSlot.get(storeName).get(nowItem.indexOf(item)));
                }
            }
            nowItem = itemStacks.subList((ye - 1) * GoodsSlot.get(storeName).size(), Math.min(ye * GoodsSlot.get(storeName).size(), itemStacks.size()));
            for (ItemStack item : nowItem){
                gui.setItem(GoodsSlot.get(storeName).get(nowItem.indexOf(item)),item);
            }
        }
        name.put(gui,storeName);
        player.openInventory(gui);
    }
}
