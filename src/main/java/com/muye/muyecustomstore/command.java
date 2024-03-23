package com.muye.muyecustomstore;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.util.List;

import static com.muye.muyecustomstore.MuyeCustomStore.*;
import static com.muye.muyecustomstore.Other.*;
import static com.muye.muyecustomstore.Reload.sendMes;

public class command implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 5 && args[0].equalsIgnoreCase("add") && sender instanceof Player && sender.isOp()){
            Player player = (Player) sender;
            if (Stores.get(args[1]) != null){
                YamlConfiguration yaml = YamlConfiguration.loadConfiguration(Stores.get(args[1]));
                List<Integer> data = LoadSlot(args[3]);
                ItemStack item;
                for (int meta : data){
                    if (canParseInt(args[2])){
                        item = new ItemStack(Material.getMaterial(Integer.parseInt(args[2])), 1, (short) meta);
                    } else {
                        item = new ItemStack(Material.getMaterial(args[2]), 1, (short) meta);
                    }
                    String itemName = item.getType().toString()+"="+item.getData().getData();
                    if (StoreType.get(args[1]).equals("money")){
                        yaml.set("Items."+itemName,Double.valueOf(args[4]));
                    }
                    if (StoreType.get(args[1]).equals("point")){
                        yaml.set("Items."+itemName,Integer.valueOf(args[4]));
                    }
                }
                try {
                    yaml.save(Stores.get(args[1]));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                Reload.ReloadGoods("Items", YamlConfiguration.loadConfiguration(Stores.get(args[1])),args[1]);
                sender.sendMessage(" §a成功重载商店 §f" + args[1] + " §a的 §e商品§c!");
            } else {
                sendMes(player,"该商店不存在");
            }
        }
        if (args.length == 1 && args[0].equalsIgnoreCase("help")){
            sender.sendMessage("§6==============================================================");
            sender.sendMessage("§6|| §b§l沐夜自定义商店       §7[ 插件帮助 ]");
            if (sender instanceof Player){
                sender.sendMessage("§6|| §f/mcs open [商店]         §7- 打开指定商店");
                sender.sendMessage("§6|| §f/mcs check all           §7- 检测全部商店是否存在主手物品");
                sender.sendMessage("§6|| §f/mcs check [商店]        §7- 检测主手物品是否存在于指定商店");
                if (sender.isOp()){
                    sender.sendMessage("§6|| §f/mcs set [商店] [价格]   §7- 设置手上物品在指定商店的价格");
                    sender.sendMessage("§6|| §f/mcs add [商店] [物品id或材质名] [数据值范围如(0,1-6)] [价格]  §7- 添加物品");
                }
            }
            if (sender.isOp()){
                sender.sendMessage("§6|| §f/mcs reload              §7- 重载全部");
                sender.sendMessage("§6|| §f/mcs reload buy          §7- 重载购买界面");
                sender.sendMessage("§6|| §f/mcs reload edit         §7- 重载编辑界面");
                sender.sendMessage("§6|| §f/mcs reload message      §7- 重载指定商店");
                sender.sendMessage("§6|| §f/mcs reload stores       §7- 重载信息提示");
                sender.sendMessage("§6|| §f/mcs reload [商店]       §7- 重载指定商店");
                sender.sendMessage("§6|| §f/mcs reload [商店] (items|gui) §7- 重载指定商店的商品或者界面");
            }
            sender.sendMessage("§6==============================================================");
            return false;
        }
        if (args.length == 3 && args[0].equalsIgnoreCase("set") && sender instanceof Player && sender.isOp()){
            Player player = (Player) sender;
            if (player.getInventory().getItemInMainHand().getType().equals(Material.AIR)){
                sendMes(player,"手上好歹有个东西");
                return false;
            }
            if (Stores.get(args[1]) != null){
                YamlConfiguration yaml = YamlConfiguration.loadConfiguration(Stores.get(args[1]));
                String itemName = player.getInventory().getItemInMainHand().getType().toString()+"="+player.getInventory().getItemInMainHand().getData().getData();
                if (canParseInt(args[2]) || canParseDou(args[2])){
                    if (StoreType.get(args[1]).equals("money") && canParseDou(args[2])){
                        double value = Double.parseDouble(args[2]);
                        MoneyGoods.put(itemName,value);
                        yaml.set("Items."+itemName,value);
                        sendMes(player,"设置成功! 物品: &6" + itemName + " &f( " + value + " &e金币 &f)");
                    }
                    if (StoreType.get(args[1]).equals("point") && canParseInt(args[2])){
                        int value = Integer.parseInt(args[2]);
                        PointGoods.put(itemName,value);
                        yaml.set("Items."+itemName,value);
                        sendMes(player,"设置成功! 物品: &6" + itemName + " &f( " + value + " &9点券 &f)");
                    }
                    try {
                        yaml.save(Stores.get(args[1]));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    GuiGoods.get(args[1]).add(itemName);
                } else {
                    sendMes(player,"请输入正确的数字");
                }
            } else {
                sendMes(player,"该商店不存在");
            }
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("check") && sender instanceof Player){
            Player player = (Player) sender;
            if (player.getInventory().getItemInMainHand().getType().equals(Material.AIR)){
                sendMes(player,"&f手上好歹有个东西");
                return false;
            }
            String itemName = player.getInventory().getItemInMainHand().getType().toString()+"="+player.getInventory().getItemInMainHand().getData().getData();
            if (args[1].equalsIgnoreCase("all")){
                for (String store : StoresList){
                    String type = StoreType.get(store);
                    for (String item : GuiGoods.get(store)){
                        if (item.equals(itemName)){
                            if (type.equals("money")){
                                sendMes(player,"&f商店 §b"+ store +" §f存在该商品 &7( " + MoneyGoods.get(itemName) + " &e金币 &7)");
                            }
                            if (type.equals("point")){
                                sendMes(player,"&f商店 §b"+ store +" §f存在该商品 &7( " + PointGoods.get(itemName) + " &9点券 &7)");
                            }
                            return false;
                        }
                    }
                    sendMes(player,"&f所有商店都不存在该商品");
                    return false;
                }
            }
            if (Stores.get(args[1]) != null){
                String type = StoreType.get(args[1]);
                for (String item : GuiGoods.get(args[1])){
                    if (item.equals(itemName)){
                        if (type.equals("money")){
                            sendMes(player,"&f该商店存在该商品 &7( " + MoneyGoods.get(itemName) + " &e金币 &7)");
                        }
                        if (type.equals("point")){
                            sendMes(player,"&f该商店存在该商品 &7( " + PointGoods.get(itemName) + " &9点券 &7)");
                        }
                        return false;
                    }
                }
                sendMes(player,"&f该商店不存在该商品");
            } else {
                sendMes(player,"&f该商店不存在");
            }
            return false;
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("open") && sender instanceof Player){
            Player player = (Player) sender;
            if (Stores.get(args[1]) != null){
                openStore.open(player,args[1],1);
            } else {
                player.sendMessage("§a不存在该商店§c!");
            }
            return false;
        }
        if (args.length == 1 && args[0].equalsIgnoreCase("reload") && sender.isOp()){
            MuyeCustomStore.plugin.ReloadALLConfig();
            sender.sendMessage(" §a重载成功§c!");
            return false;
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("reload") && args[1].equalsIgnoreCase("stores") && sender.isOp()){
            plugin.ReloadALLStore();
            sender.sendMessage("§a重载全部商店成功§c!");
            return false;
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("reload") && args[1].equalsIgnoreCase("message") && sender.isOp()){
            Reload.ReloadMes();
            sender.sendMessage("§a重载消息设置成功§c!");
            return false;
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("reload") && args[1].equalsIgnoreCase("buy") && sender.isOp()){
            Reload.ReloadBuy();
            sender.sendMessage("§a重载购买界面成功§c!");
            return false;
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("reload") && args[1].equalsIgnoreCase("edit") && sender.isOp()){
            Reload.ReloadEdit();
            sender.sendMessage("§a重载编辑界面成功§c!");
            return false;
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("reload") && sender.isOp()){
            if (Stores.get(args[1]) != null){
                Reload.ReloadStore(args[1],Stores.get(args[1]));
                sender.sendMessage(" §a重载商店 §f" + args[1] + " §a成功§c!");
            } else {
                sender.sendMessage("§a不存在该商店§c!");
            }
            return false;
        }
        if (args.length == 3 && args[0].equalsIgnoreCase("reload") && args[2].equalsIgnoreCase("items") && sender.isOp()){
            if (Stores.get(args[1]) != null){
                Reload.ReloadGoods("Items", YamlConfiguration.loadConfiguration(Stores.get(args[1])),args[1]);
                sender.sendMessage(" §a成功重载商店 §f" + args[1] + " §a的 §e商品§c!");
            } else {
                sender.sendMessage("§a不存在该商店§c!");
            }
            return false;
        }
        if (args.length == 3 && args[0].equalsIgnoreCase("reload") && args[2].equalsIgnoreCase("gui") && sender.isOp()){
            if (Stores.get(args[1]) != null){
                Reload.reloadItems = false;
                Reload.ReloadStore(args[1],Stores.get(args[1]));
                Reload.reloadItems = true;
                sender.sendMessage(" §a成功重载商店 §f" + args[1] + " §a的 §e界面§c!");
            } else {
                sender.sendMessage("§a不存在该商店§c!");
            }
            return false;
        }
        return false;
    }
}
