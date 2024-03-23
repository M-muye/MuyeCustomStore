package com.muye.muyecustomstore;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.util.List;

import static com.muye.muyecustomstore.BuyOrEdit.BuyItem;
import static com.muye.muyecustomstore.BuyOrEdit.PapiBuy;
import static com.muye.muyecustomstore.MuyeCustomStore.*;
import static com.muye.muyecustomstore.Other.*;
import static com.muye.muyecustomstore.Reload.*;

public class listener implements Listener {
    public static boolean input = false;
    private static boolean Set = false;
    private void CheckBuy(Player player,Integer amount){
        ItemStack Item = Buy.getItem(slot);
        ItemStack itemStack = new ItemStack(Material.getMaterial(Item.getType().toString()),amount,Item.getData().getData());
        System.out.println();
        if (StoreType.get(openStore.name.get(openStore.gui)).equalsIgnoreCase("money")){
            double money = MoneyGoods.get(Item.getType().toString()+"="+Item.getData().getData())*amount;
            if (money <= economyManager.getBalance(Bukkit.getOfflinePlayer(player.getUniqueId()))) {
                economyManager.withdrawPlayer(player, money);
                player.getInventory().addItem(itemStack);
                sendMes(player,Messages.get("Buy").replace("%item%",Item.getType().toString()).replace("%amount%",String.valueOf(amount)).replace("%money%",String.valueOf(economyManager.getBalance(Bukkit.getOfflinePlayer(player.getUniqueId())))));
                sendMes(player,Messages.get("LeftMoney").replace("%item%",Item.getType().toString()).replace("%amount%",String.valueOf(amount)).replace("%money%",String.valueOf(economyManager.getBalance(Bukkit.getOfflinePlayer(player.getUniqueId())))));
                BuyItem(player,Item);
            } else {
                sendMes(player,Messages.get("NoMoney").replace("%item%",Item.getType().toString()).replace("%amount%",String.valueOf(amount)).replace("%money%",String.valueOf(economyManager.getBalance(Bukkit.getOfflinePlayer(player.getUniqueId())))));
            }
        }
        if (StoreType.get(openStore.name.get(openStore.gui)).equalsIgnoreCase("point")){
            int point = PointGoods.get(Item.getType().toString()+"="+Item.getData().getData())*amount;
            if (point <= playerPointsManager.getAPI().look(player.getUniqueId())){
                playerPointsManager.getAPI().take(player.getUniqueId(),point);
                player.getInventory().addItem(itemStack);
                sendMes(player,Messages.get("Buy").replace("%item%",Item.getType().toString()).replace("%amount%",String.valueOf(amount)).replace("%point%",String.valueOf(playerPointsManager.getAPI().look(player.getUniqueId()))));
                sendMes(player,Messages.get("LeftPoint").replace("%item%",Item.getType().toString()).replace("%amount%",String.valueOf(amount)).replace("%point%",String.valueOf(playerPointsManager.getAPI().look(player.getUniqueId()))));
                BuyItem(player,Item);
            } else {
                sendMes(player,Messages.get("NoPoint").replace("%item%",Item.getType().toString()).replace("%amount%",String.valueOf(amount)).replace("%point%",String.valueOf(playerPointsManager.getAPI().look(player.getUniqueId()))));
            }
        }
    }
    @EventHandler
    public void getPlayerChat(AsyncPlayerChatEvent event) {
        if (input){
            event.setCancelled(true);
            if (event.getMessage().equals("取消")){
                sendMes(event.getPlayer(),"§f取消成功!");
                input = false;
                return;
            }
            if (canParseInt(event.getMessage())){
                int amount = Integer.parseInt(event.getMessage());
                CheckBuy(event.getPlayer(),amount);
                input = false;
            } else {
                event.getPlayer().sendTitle("§a请输入§n正确§a的数字","§a输入§e取消§a即可取消购买",10,20,10);
                input = true;
            }
        }
        if (Set){
            event.setCancelled(true);
            if (event.getMessage().equals("取消")){
                sendMes(event.getPlayer(),"§f取消成功!");
                Set = false;
                return;
            }
            if (canParseDou(event.getMessage()) || canParseInt(event.getMessage())){
                if (!canParseInt(event.getMessage()) && StoreType.get(openStore.name.get(openStore.gui)).equals("point")){
                    event.getPlayer().sendTitle("§a点券商品只能输入整数","§a输入 §e取消 §a即可取消",10,20,10);
                    Set = true;
                    return;
                }
                if (canParseDou(event.getMessage()) && Double.parseDouble(event.getMessage()) <= 0){
                    event.getPlayer().sendTitle("§a请输入大于 §f0 §a的数字","§a输入 §e取消 §a即可取消",10,20,10);
                    Set = true;
                    return;
                }
                String EditItemName = Edit.getItem(Reload.slot2).getType().toString()+"="+Edit.getItem(Reload.slot2).getData().getData();
                YamlConfiguration yaml = YamlConfiguration.loadConfiguration(Stores.get(openStore.name.get(openStore.gui)));
                if (canParseDou(event.getMessage()) && StoreType.get(openStore.name.get(openStore.gui)).equals("money")){
                    double value = Double.parseDouble(event.getMessage());
                    MoneyGoods.put(EditItemName,value);
                    yaml.set("Items."+EditItemName,value);
                    sendMes(event.getPlayer(),"设置成功! 物品: &6" + EditItemName + " &f( " + value + " &e金币 &f)");
                }
                if (canParseInt(event.getMessage()) && StoreType.get(openStore.name.get(openStore.gui)).equals("point")){
                    int value = Integer.parseInt(event.getMessage());
                    PointGoods.put(EditItemName,value);
                    yaml.set("Items."+EditItemName,value);
                    sendMes(event.getPlayer(),"设置成功! 物品: &6" + EditItemName + " &f( " + value + " &9点券 &f)");
                }
                try {
                    yaml.save(Stores.get(openStore.name.get(openStore.gui)));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                openStore.open(event.getPlayer(),openStore.name.get(openStore.gui),openStore.ye);
                Set = false;
            } else {
                event.getPlayer().sendTitle("§a请输入 §n正确 §a的数字","§a输入 §e取消 §a即可取消",10,20,10);
                Set = true;
            }
        }
    }
    @EventHandler
    public void click(InventoryClickEvent event){
        if (event.getInventory().equals(openStore.gui)){
            event.setCancelled(true);
            if (event.getAction().equals(InventoryAction.NOTHING)){
                return;
            }
            Player player = (Player) event.getWhoClicked();
            if (ItemMap.get(openStore.name.get(openStore.gui)).contains(event.getRawSlot())){
                List<String> Actionlist = Action.get(openStore.name.get(openStore.gui)+event.getRawSlot());
                if (Actionlist != null){
                    for (String actionType : Actionlist){
                        if (actionType.startsWith("ALL|-")){
                            Perform(Replace(actionType.replace("ALL|-","")).toLowerCase(),player);
                            continue;
                        }
                        if (actionType.startsWith("LEFT|-") && event.getClick().isLeftClick()){
                            Perform(Replace(actionType.replace("LEFT|-","")).toLowerCase(),player);
                            continue;
                        }
                        if (actionType.startsWith("RIGHT|-") && event.getClick().isRightClick()){
                            Perform(Replace(actionType.replace("RIGHT|-","")).toLowerCase(),player);
                        }
                        if (actionType.startsWith("SHIFT_LEFT|-") && event.getClick().isLeftClick() && event.getClick().isShiftClick()){
                            Perform(Replace(actionType.replace("SHIFT_LEFT|-","")).toLowerCase(),player);
                        }
                        if (actionType.startsWith("SHIFT_RIGHT|-") && event.getClick().isRightClick() && event.getClick().isShiftClick()){
                            Perform(Replace(actionType.replace("SHIFT_RIGHT|-","")).toLowerCase(),player);
                        }
                    }
                }
                return;
            }
            if (GoodsSlot.get(openStore.name.get(openStore.gui)).contains(event.getRawSlot())){
                if (event.getClick().isShiftClick() && event.getClick().isRightClick() && player.isOp()){
                    BuyOrEdit.EditItem(player,event.getCurrentItem());
                    return;
                }
                BuyOrEdit.BuyItem(player,event.getCurrentItem());
            }
        }
        if (event.getInventory().equals(PapiBuy)){
            event.setCancelled(true);
            if (event.getAction().equals(InventoryAction.NOTHING)){
                return;
            }
            Player player = (Player) event.getWhoClicked();
            if (BuyAction.get(event.getCurrentItem()) != null){
                List<String> Actionlist = BuyAction.get(event.getCurrentItem());
                if (Actionlist != null){
                    for (String actionType : Actionlist){
                        if (actionType.startsWith("ALL|-")){
                            Perform(Replace(actionType.replace("ALL|-","")).toLowerCase(),player);
                            continue;
                        }
                        if (actionType.startsWith("LEFT|-") && event.getClick().isLeftClick()){
                            Perform(Replace(actionType.replace("LEFT|-","")).toLowerCase(),player);
                            continue;
                        }
                        if (actionType.startsWith("RIGHT|-") && event.getClick().isRightClick()){
                            Perform(Replace(actionType.replace("RIGHT|-","")).toLowerCase(),player);
                        }
                        if (actionType.startsWith("SHIFT_LEFT|-") && event.getClick().isLeftClick() && event.getClick().isShiftClick()){
                            Perform(Replace(actionType.replace("SHIFT_LEFT|-","")).toLowerCase(),player);
                        }
                        if (actionType.startsWith("SHIFT_RIGHT|-") && event.getClick().isRightClick() && event.getClick().isShiftClick()){
                            Perform(Replace(actionType.replace("SHIFT_RIGHT|-","")).toLowerCase(),player);
                        }
                    }
                }
                return;
            }
        }
        if (event.getInventory().equals(Reload.Edit)){
            event.setCancelled(true);
            if (event.getAction().equals(InventoryAction.NOTHING)){
                return;
            }
            Player player = (Player) event.getWhoClicked();
            if (EditAction.get(event.getCurrentItem()) != null){
                List<String> Actionlist = EditAction.get(event.getCurrentItem());
                if (Actionlist != null){
                    for (String actionType : Actionlist){
                        if (actionType.startsWith("ALL|-")){
                            Perform(plugin.getPapi(player,Replace(actionType.replace("ALL|-","")).toLowerCase()),player);
                            continue;
                        }
                        if (actionType.startsWith("LEFT|-") && event.getClick().isLeftClick()){
                            Perform(plugin.getPapi(player,Replace(actionType.replace("LEFT|-","")).toLowerCase()),player);
                            continue;
                        }
                        if (actionType.startsWith("RIGHT|-") && event.getClick().isRightClick()){
                            Perform(plugin.getPapi(player,Replace(actionType.replace("RIGHT|-","")).toLowerCase()),player);
                            continue;
                        }
                        if (actionType.startsWith("SHIFT_LEFT|-") && event.getClick().isLeftClick() && event.getClick().isShiftClick()){
                            Perform(plugin.getPapi(player,Replace(actionType.replace("SHIFT_LEFT|-","")).toLowerCase()),player);
                            continue;
                        }
                        if (actionType.startsWith("SHIFT_RIGHT|-") && event.getClick().isRightClick() && event.getClick().isShiftClick()){
                            Perform(plugin.getPapi(player,Replace(actionType.replace("SHIFT_RIGHT|-","")).toLowerCase()),player);
                            continue;
                        }
                    }
                }
                return;
            }
        }
    }
    private void Perform(String action,Player player){
        if (action.toLowerCase().startsWith("console: ")){
            int Index = action.toLowerCase().indexOf("console: ") + "console: ".length();
            String cmd = plugin.getPapi(player,action.substring(Index));
            Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), cmd);
            return;
        }
        if (action.toLowerCase().startsWith("op: ")){
            int Index = action.toLowerCase().indexOf("op: ") + "op: ".length();
            String cmd = plugin.getPapi(player,action.substring(Index));
            player.setOp(true);
            player.performCommand(cmd);
            player.setOp(false);
            return;
        }
        if (action.toLowerCase().startsWith("player: ")){
            int Index = action.toLowerCase().indexOf("player: ") + "player: ".length();
            String cmd = plugin.getPapi(player,action.substring(Index));
            player.performCommand(cmd);
            return;
        }
        if (action.equalsIgnoreCase("pre")){
            if (openStore.ye.equals(1)){
                sendMes(player,Messages.get("FirstPage"));
            } else {
                openStore.open(player,openStore.name.get(openStore.gui),openStore.ye-1);
            }
            return;
        }
        if (action.equalsIgnoreCase("next")){
            if (openStore.ye.equals(openStore.last)){
                sendMes(player,Messages.get("LastPage"));
            } else {
                openStore.open(player,openStore.name.get(openStore.gui),openStore.ye+1);
            }
            return;
        }
        if (action.equalsIgnoreCase("back")){
            openStore.open(player,openStore.name.get(openStore.gui),openStore.ye);
            return;
        }
        if (action.toLowerCase().startsWith("buy") && action.length() == 3){
            player.closeInventory();
            input = true;
            player.sendTitle("§a请输入正确的数字","§a输入 §e取消 §a即可取消购买",10,20,10);
            return;
        }
        if (action.toLowerCase().startsWith("buy: ")){
            int Index = action.toLowerCase().indexOf("buy: ") + "buy: ".length();
            if (canParseInt(plugin.getPapi(player,action.substring(Index)))){
                Integer amount = Integer.valueOf(plugin.getPapi(player,action.substring(Index)));
                CheckBuy(player,amount);
                return;
            }
            sendMes(player,"&f参数有问题");
            return;
        }
        if (action.equalsIgnoreCase("delete")){
            String EditItemName = Edit.getItem(Reload.slot2).getType().toString()+"="+Edit.getItem(Reload.slot2).getData().getData();
            YamlConfiguration yaml = YamlConfiguration.loadConfiguration(Stores.get(openStore.name.get(openStore.gui)));
            if (StoreType.get(openStore.name.get(openStore.gui)).equals("money")){
                MoneyGoods.remove(EditItemName);
                sendMes(player,"删除成功! 物品: &6" + EditItemName);
            }
            if (StoreType.get(openStore.name.get(openStore.gui)).equals("point")){
                PointGoods.remove(EditItemName);
                sendMes(player,"删除成功! 物品: &6" + EditItemName);
            }
            GuiGoods.get(openStore.name.get(openStore.gui)).remove(EditItemName);
            yaml.set("Items."+EditItemName,null);
            try {
                yaml.save(Stores.get(openStore.name.get(openStore.gui)));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            openStore.open(player,openStore.name.get(openStore.gui),openStore.ye);
            return;
        }
        if (action.equalsIgnoreCase("set")){
            player.closeInventory();
            player.sendTitle("§a请输入数字","§a输入§e取消§a即可取消",10,20,10);
            Set = true;
            return;
        }
        sendMes(player,Messages.get("UnKnown"));
    }

}
