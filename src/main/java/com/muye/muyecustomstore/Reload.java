package com.muye.muyecustomstore;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.*;

import static com.muye.muyecustomstore.MuyeCustomStore.*;
import static com.muye.muyecustomstore.Other.*;

public class Reload {
    public static Map<String,Integer> pages = new HashMap<>();
    public static boolean reloadItems = true;
    public static Inventory Buy = null;
    public static Inventory Edit = null;
    public static Integer slot = null;
    public static Integer slot2 = null;
    public static Map<ItemStack, List<String>> BuyAction = new HashMap<>();
    public static Map<ItemStack, List<String>> EditAction = new HashMap<>();
    public static String prefix;
    public static void ReloadStore(String storeName,File store) {
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(store);
        Set<String> set = yaml.getConfigurationSection("").getKeys(false);
        String title = "MuyeCustomStore";
        List<String> layout = null;
        List<Integer> slots;
        String type;
        Inventory gui = null;
        for (String key : set) {
            if (key.equalsIgnoreCase("title")){
                title = Replace(yaml.getString(key));
                continue;
            }
            if (key.equalsIgnoreCase("layout")){
                layout = yaml.getStringList(key);
                if (layout != null) {
                    gui = Bukkit.createInventory(null,layout.size()*9,title);
                } else {
                    gui = Bukkit.createInventory(null,0,title);
                    plugin.getLogger().info("§6| §c布局出错 §7( " + storeName + " )");
                    break;
                }
                continue;
            }
            if (key.equalsIgnoreCase("currency")){
                type = yaml.getString(key).toLowerCase();
                if (!type.equals("money") && !type.equals("point")){
                    plugin.getLogger().info("§6| §c货币类型出错 §7( " + storeName + " )");
                    break;
                } else {
                    if (type.equals("money")){
                        plugin.getLogger().info("§6| §a加载商店§f: " + storeName + " §7(货币类型: §e金币§7)");
                    }
                    if (type.equals("point")){
                        plugin.getLogger().info("§6| §a加载商店§f: " + storeName + " §7(货币类型: §9点券§7)");
                    }
                    StoreType.put(storeName,type);
                }
                continue;
            }
            if (key.equalsIgnoreCase("item")){
                Set<String> items = yaml.getConfigurationSection(key).getKeys(false);
                for (String setting : items){
                    if (setting.equalsIgnoreCase("slots")){
                        slots = LoadSlot(yaml.getString(key+"."+setting));
                        GoodsSlot.put(storeName,slots);
                    }
                    if (setting.equalsIgnoreCase("lore")){
                        openStore.Lore = ReplaceList(yaml.getStringList(key+"."+setting));
                    }
                }
                continue;
            }
            if (key.equalsIgnoreCase("Items") && reloadItems){
                ReloadGoods(key, YamlConfiguration.loadConfiguration(Stores.get(storeName)),storeName);
                continue;
            }
            if (key.equalsIgnoreCase("icons")){
                Map<String, ItemStack> itemList = new HashMap<>();
                Set<String> icons = yaml.getConfigurationSection(key).getKeys(false);
                List<Integer> Icons = new ArrayList<>();
                Map<String,List<String>> LoadAction = new HashMap<>();
                for (String icon : icons){
                    String mat;
                    int amount = 1;
                    short data = 0;
                    String name;
                    if (yaml.get(key + "." + icon + ".amt") != null) {
                        if (yaml.get(key + "." + icon + ".amt").getClass().equals(String.class)){
                            amount = Integer.parseInt(yaml.getString(key + "." + icon + ".amt"));
                        }
                        if (yaml.get(key + "." + icon + ".amt").getClass().equals(Integer.class)){
                            amount = yaml.getInt(key + "." + icon + ".amt");
                        }
                    }
                    if (yaml.get(key + "." + icon + ".data") != null) {
                        if (yaml.get(key + "." + icon + ".data").getClass().equals(String.class)){
                            data = Short.parseShort(yaml.getString(key + "." + icon + ".data"));
                        }
                        if (yaml.get(key + "." + icon + ".data").getClass().equals(Integer.class)){
                            data = (short) yaml.getInt(key + "." + icon + ".data");
                        }
                    }
                    ItemStack itemStack = null;
                    if (yaml.getString(key + "." + icon + ".mat") != null) {
                        mat = yaml.getString(key + "." + icon + ".mat");
                        if (mat.equalsIgnoreCase("air")){
                            itemStack = new ItemStack(Material.AIR);
                        } else {
                            if (canParseInt(mat)){
                                itemStack = new ItemStack(Material.getMaterial(Integer.parseInt(mat)),amount,data);
                            } else {
                                itemStack = new ItemStack(Material.getMaterial(mat.toUpperCase()),amount,data);
                            }
                        }
                    }
                    ItemMeta itemMeta;
                    if (itemStack != null) {
                        itemMeta = itemStack.getItemMeta();
                        if (yaml.getString(key + "." + icon + ".name") != null) {
                            name = Replace(yaml.getString(key + "." + icon + ".name"));
                            itemMeta.setDisplayName(name);
                        }
                        if (yaml.getStringList(key + "." + icon + ".lore") != null) {
                            List<String> lore;
                            lore = ReplaceList(yaml.getStringList(key + "." + icon + ".lore"));
                            itemMeta.setLore(lore);
                        }
                        itemStack.setItemMeta(itemMeta);
                    }
                    if (yaml.getConfigurationSection(key + "." + icon + ".action") != null){
                        Set<String> clickType = yaml.getConfigurationSection(key + "." + icon + ".action").getKeys(false);
                        List<String> ActionList = new ArrayList<>();
                        for (String click : clickType) {
                            if (click.equalsIgnoreCase("all")){
                                List<String> actionlist = yaml.getStringList(key + "." + icon + ".action." + click);
                                for (String action : actionlist) {
                                    String newAction = "ALL|-" + action;
                                    ActionList.add(newAction);
                                }
                                continue;
                            }
                            if (click.equalsIgnoreCase("left")){
                                List<String> actionlist = yaml.getStringList(key + "." + icon + ".action." + click);
                                for (String action : actionlist) {
                                    String newAction = "LEFT|-" + action;
                                    ActionList.add(newAction);
                                }
                                continue;
                            }
                            if (click.equalsIgnoreCase("right")){
                                List<String> actionlist = yaml.getStringList(key + "." + icon + ".action." + click);
                                for (String action : actionlist) {
                                    String newAction = "RIGHT|-" + action;
                                    ActionList.add(newAction);
                                }
                            }
                            if (click.equalsIgnoreCase("shift_left")) {
                                List<String> actionlist = yaml.getStringList(key + "." + icon + ".action." + click);
                                for (String action : actionlist) {
                                    String newAction = "SHIFT_LEFT|-" + action;
                                    ActionList.add(newAction);
                                }
                            }
                            if (click.equalsIgnoreCase("shift_right")) {
                                List<String> actionlist = yaml.getStringList(key + "." + icon + ".action." + click);
                                for (String action : actionlist) {
                                    String newAction = "SHIFT_RIGHT|-" + action;
                                    ActionList.add(newAction);
                                }
                            }
                        }
                        LoadAction.put(icon,ActionList);
                    }
                    itemList.put(icon,itemStack);
                }
                //布局
                if (layout != null) {
                    int i = 0;
                    for (String list : layout){
                        for (int k = 0; k<list.length(); k++){
                            String item = String.valueOf(list.charAt(k));
                            if (itemList.get(item) != null){
                                gui.setItem(i+k,itemList.get(item));
                                Icons.add(i+k);
                                Action.put(storeName+(i+k),LoadAction.get(item));
                            }
                        }
                        i = i+9;
                    }
                }
                ItemMap.put(storeName,Icons);
            }
        }
        stores.put(storeName,gui);
    }

    public static void ReloadGoods(String key, YamlConfiguration yaml, String storeName){
        Set<String> items = yaml.getConfigurationSection(key).getKeys(false);
        List<String> goods = new ArrayList<>();
        if (StoreType.get(storeName) != null){
            if (!StoreType.get(storeName).equalsIgnoreCase("money") && !StoreType.get(storeName).equalsIgnoreCase("point")){
                plugin.getLogger().info("§b|§6货币类型出错");
                return;
            }
            if (StoreType.get(storeName).equalsIgnoreCase("point")){
                for (String item : items) {
                    int value = yaml.getInt(key+"."+item);
                    plugin.getLogger().info("§b|- §6加载物品§f: §7" + item + " §7( " + value + " §9点券 §7)");
                    goods.add(item);
                    PointGoods.put(item,value);
                }
            }
            if (StoreType.get(storeName).equalsIgnoreCase("money")){
                for (String item : items) {
                    double value = yaml.getDouble(key+"."+item);
                    plugin.getLogger().info("§b|- §6加载物品§f: §7" + item + " §7( " + value + " §e金币 §7)");
                    goods.add(item);
                    MoneyGoods.put(item,value);
                }
            }
            GuiGoods.put(storeName,goods);
        }
        List<String> Goods = GuiGoods.get(storeName);
        List<Integer> slots = GoodsSlot.get(storeName);
        pages.put(storeName,1);
        if (Goods.size() > slots.size()){
            pages.put(storeName,Goods.size()/slots.size()+1);
        }
        if (slots.size() == 1){
            pages.put(storeName,Goods.size()/slots.size());
        }
    }
    public static void ReloadMes(){
        plugin.getLogger().info("§6| §a加载信息提示");
        FileConfiguration config = plugin.getConfig();
        Set<String> messages = config.getConfigurationSection("Messages").getKeys(false);
        for (String mes : messages) {
            Messages.put(mes,Replace(config.getString("Messages."+mes)));
        }
        if (config.getString("Prefix") != null){
            prefix = Replace(config.getString("Prefix"));
        }
    }
    public static void ReloadBuy(){
        plugin.getLogger().info("§6| §a加载购买界面");
        FileConfiguration config = plugin.getConfig();
        Set<String> buy = config.getConfigurationSection("Buy").getKeys(false);
        String title = "§a购买界面";
        List<String> layout = Collections.singletonList("");
        Map<String, ItemStack> itemList = new HashMap<>();
        for (String key : buy){
            if (key.equalsIgnoreCase("title")){
                title = Replace(config.getString("Buy."+key));
                continue;
            }
            if (key.equalsIgnoreCase("layout")){
                layout = config.getStringList("Buy."+key);
                if (layout != null) {
                    Buy = Bukkit.createInventory(null,layout.size()*9,title);
                } else {
                    Buy = Bukkit.createInventory(null,0,title);
                    plugin.getLogger().info("§6| §c购买界面布局出错");
                    break;
                }
                continue;
            }
            if (!key.equalsIgnoreCase("title") && !key.equalsIgnoreCase("layout")){
                String mat;
                int amount = 1;
                short data = 0;
                String name;
                if (config.get("Buy." + key + ".amt") != null) {
                    if (config.get("Buy." + key + ".amt").getClass().equals(String.class)){
                        amount = Integer.parseInt(config.getString("Buy." + key + ".amt"));
                    }
                    if (config.get("Buy." + key + ".amt").getClass().equals(Integer.class)){
                        amount = config.getInt("Buy." + key + ".amt");
                    }
                }
                if (config.get("Buy." + key + ".data") != null) {
                    if (config.get("Buy." + key + ".data").getClass().equals(String.class)){
                        data = Short.parseShort(config.getString("Buy." + key + ".data"));
                    }
                    if (config.get("Buy." + key + ".data").getClass().equals(Integer.class)){
                        data = (short) config.getInt("Buy." + key + ".data");
                    }
                }
                ItemStack itemStack = null;
                if (config.getString("Buy." + key + ".mat") != null) {
                    mat = config.getString("Buy." + key + ".mat");
                    if (mat.equalsIgnoreCase("air")) {
                        itemStack = new ItemStack(Material.AIR);
                    } else {
                        if (canParseInt(mat)) {
                            itemStack = new ItemStack(Material.getMaterial(Integer.parseInt(mat)), amount, data);
                        } else {
                            itemStack = new ItemStack(Material.getMaterial(mat.toUpperCase()), amount, data);
                        }
                    }
                }
                ItemMeta itemMeta;
                if (itemStack != null) {
                    itemMeta = itemStack.getItemMeta();
                    if (config.getString("Buy." + key + ".name") != null) {
                        name = Replace(config.getString("Buy." + key + ".name"));
                        itemMeta.setDisplayName(name);
                    }
                    if (config.getStringList("Buy." + key + ".lore") != null) {
                        List<String> lore;
                        lore = ReplaceList(config.getStringList("Buy." + key + ".lore"));
                        itemMeta.setLore(lore);
                    }
                    itemStack.setItemMeta(itemMeta);
                }
                if (config.getConfigurationSection("Buy." + key + ".action") != null) {
                    Set<String> clickType = config.getConfigurationSection("Buy." + key + ".action").getKeys(false);
                    List<String> ActionList = new ArrayList<>();
                    for (String click : clickType) {
                        if (click.equalsIgnoreCase("all")) {
                            List<String> actionlist = config.getStringList("Buy." + key + ".action." + click);
                            for (String action : actionlist) {
                                String newAction = "ALL|-" + action;
                                ActionList.add(newAction);
                            }
                            continue;
                        }
                        if (click.equalsIgnoreCase("left")) {
                            List<String> actionlist = config.getStringList("Buy." + key + ".action." + click);
                            for (String action : actionlist) {
                                String newAction = "LEFT|-" + action;
                                ActionList.add(newAction);
                            }
                            continue;
                        }
                        if (click.equalsIgnoreCase("right")) {
                            List<String> actionlist = config.getStringList("Buy." + key + ".action." + click);
                            for (String action : actionlist) {
                                String newAction = "RIGHT|-" + action;
                                ActionList.add(newAction);
                            }
                        }
                        if (click.equalsIgnoreCase("shift_left")) {
                            List<String> actionlist = config.getStringList("Buy." + key + ".action." + click);
                            for (String action : actionlist) {
                                String newAction = "SHIFT_LEFT|-" + action;
                                ActionList.add(newAction);
                            }
                        }
                        if (click.equalsIgnoreCase("shift_right")) {
                            List<String> actionlist = config.getStringList("Buy." + key + ".action." + click);
                            for (String action : actionlist) {
                                String newAction = "SHIFT_RIGHT|-" + action;
                                ActionList.add(newAction);
                            }
                        }
                    }
                    BuyAction.put(itemStack, ActionList);
                }
                itemList.put(key, itemStack);
            }
        }
        //布局
        int i = 0;
        if (layout != null) {
            for (String list : layout) {
                for (int k = 0; k < list.length(); k++) {
                    String item = String.valueOf(list.charAt(k));
                    if (itemList.get(item) != null) {
                        Buy.setItem(i + k, itemList.get(item));
                    }
                    if (item.equals("$")){
                        slot = i + k;
                    }
                }
                i = i + 9;
            }
        }
    }

    public static void ReloadEdit() {
        plugin.getLogger().info("§6| §a加载编辑界面");
        FileConfiguration config = plugin.getConfig();
        Set<String> edit = config.getConfigurationSection("Edit").getKeys(false);
        String title = "§a编辑界面";
        List<String> layout = Collections.singletonList("");
        Map<String, ItemStack> itemList = new HashMap<>();
        for (String key : edit){
            if (key.equalsIgnoreCase("title")){
                title = Replace(config.getString("Edit."+key));
                continue;
            }
            if (key.equalsIgnoreCase("layout")){
                layout = config.getStringList("Edit."+key);
                if (layout != null) {
                    Edit = Bukkit.createInventory(null,layout.size()*9,title);
                } else {
                    Edit = Bukkit.createInventory(null,0,title);
                    plugin.getLogger().info("§6| §c编辑界面布局出错");
                    break;
                }
                continue;
            }
            if (!key.equalsIgnoreCase("title") && !key.equalsIgnoreCase("layout")){
                String mat;
                int amount = 1;
                short data = 0;
                String name;
                if (config.get("Edit." + key + ".amt") != null) {
                    if (config.get("Edit." + key + ".amt").getClass().equals(String.class)){
                        amount = Integer.parseInt(config.getString("Edit." + key + ".amt"));
                    }
                    if (config.get("Edit." + key + ".amt").getClass().equals(Integer.class)){
                        amount = config.getInt("Edit." + key + ".amt");
                    }
                }
                if (config.get("Edit." + key + ".data") != null) {
                    if (config.get("Edit." + key + ".data").getClass().equals(String.class)){
                        data = Short.parseShort(config.getString("Edit." + key + ".data"));
                    }
                    if (config.get("Edit." + key + ".data").getClass().equals(Integer.class)){
                        data = (short) config.getInt("Edit." + key + ".data");
                    }
                }
                ItemStack itemStack = null;
                if (config.getString("Edit." + key + ".mat") != null) {
                    mat = config.getString("Edit." + key + ".mat");
                    if (mat.equalsIgnoreCase("air")) {
                        itemStack = new ItemStack(Material.AIR);
                    } else {
                        if (canParseInt(mat)) {
                            itemStack = new ItemStack(Material.getMaterial(Integer.parseInt(mat)), amount, data);
                        } else {
                            itemStack = new ItemStack(Material.getMaterial(mat.toUpperCase()), amount, data);
                        }
                    }
                }
                ItemMeta itemMeta;
                if (itemStack != null) {
                    itemMeta = itemStack.getItemMeta();
                    if (config.getString("Edit." + key + ".name") != null) {
                        name = Replace(config.getString("Edit." + key + ".name"));
                        itemMeta.setDisplayName(name);
                    }
                    if (config.getStringList("Edit." + key + ".lore") != null) {
                        List<String> lore;
                        lore = ReplaceList(config.getStringList("Edit." + key + ".lore"));
                        itemMeta.setLore(lore);
                    }
                    itemStack.setItemMeta(itemMeta);
                }
                if (config.getConfigurationSection("Edit." + key + ".action") != null) {
                    Set<String> clickType = config.getConfigurationSection("Edit." + key + ".action").getKeys(false);
                    List<String> ActionList = new ArrayList<>();
                    for (String click : clickType) {
                        if (click.equalsIgnoreCase("all")) {
                            List<String> actionlist = config.getStringList("Edit." + key + ".action." + click);
                            for (String action : actionlist) {
                                String newAction = "ALL|-" + action;
                                ActionList.add(newAction);
                            }
                            continue;
                        }
                        if (click.equalsIgnoreCase("left")) {
                            List<String> actionlist = config.getStringList("Edit." + key + ".action." + click);
                            for (String action : actionlist) {
                                String newAction = "LEFT|-" + action;
                                ActionList.add(newAction);
                            }
                            continue;
                        }
                        if (click.equalsIgnoreCase("right")) {
                            List<String> actionlist = config.getStringList("Edit." + key + ".action." + click);
                            for (String action : actionlist) {
                                String newAction = "RIGHT|-" + action;
                                ActionList.add(newAction);
                            }
                            continue;
                        }
                        if (click.equalsIgnoreCase("shift_left")) {
                            List<String> actionlist = config.getStringList("Edit." + key + ".action." + click);
                            for (String action : actionlist) {
                                String newAction = "SHIFT_LEFT|-" + action;
                                ActionList.add(newAction);
                            }
                        }
                        if (click.equalsIgnoreCase("shift_right")) {
                            List<String> actionlist = config.getStringList("Edit." + key + ".action." + click);
                            for (String action : actionlist) {
                                String newAction = "SHIFT_RIGHT|-" + action;
                                ActionList.add(newAction);
                            }
                        }
                    }
                    EditAction.put(itemStack, ActionList);
                }
                itemList.put(key, itemStack);
            }
        }
        //布局
        int i = 0;
        if (layout != null) {
            for (String list : layout) {
                for (int k = 0; k < list.length(); k++) {
                    String item = String.valueOf(list.charAt(k));
                    if (itemList.get(item) != null) {
                        Edit.setItem(i + k, itemList.get(item));
                    }
                    if (item.equals("$")){
                        slot2 = i + k;
                    }
                }
                i = i + 9;
            }
        }
    }
    public static void sendMes(Player player, String mes){
        String Mes = plugin.getPapi(player,Replace(mes));
        player.sendMessage(prefix+Mes);
    }
}
