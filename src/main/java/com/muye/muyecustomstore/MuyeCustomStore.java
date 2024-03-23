package com.muye.muyecustomstore;

import me.clip.placeholderapi.PlaceholderAPI;
import net.milkbowl.vault.economy.Economy;
import org.black_ixx.playerpoints.PlayerPoints;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.muye.muyecustomstore.Reload.*;

public final class MuyeCustomStore extends JavaPlugin {
    public static MuyeCustomStore plugin;
    public static Economy economyManager = null;
    public static PlayerPoints playerPointsManager = null;
    public static List<String> StoresList = new ArrayList<>();
    public static Map<String, String> Messages = new HashMap<>();
    public static Map<String, File> Stores = new HashMap<>();
    public static Map<String, String> StoreType = new HashMap<>();
    public static Map<String, Inventory> stores = new HashMap<>();
    public static Map<String, List<Integer>> ItemMap = new HashMap<>();
    public static Map<String, List<String>> Action = new HashMap<>();
    public static Map<String, List<String>> GuiGoods = new HashMap<>();
    public static Map<String, List<Integer>> GoodsSlot = new HashMap<>();
    public static Map<String, Double> MoneyGoods = new HashMap<>();
    public static Map<String, Integer> PointGoods = new HashMap<>();
    @Override
    public void onEnable() {
        getLogger().info("§6-----------------");
        getLogger().info("§6| §b沐夜自定义商店 §c启动!!!");
        checkVault();
        checkPP();
        checkPapi();
        plugin = this;
        ReloadALLConfig();
        getLogger().info("§6-----------------");
        Bukkit.getPluginCommand("MuyeCustomStore").setExecutor(new command());
        Bukkit.getPluginCommand("mcs").setExecutor(new command());
        getCommand("MuyeCustomStore").setTabCompleter(new TAB());
        getCommand("mcs").setTabCompleter(new TAB());
        Bukkit.getPluginManager().registerEvents(new listener(),this);
    }

    private void checkPapi() {
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            getLogger().info("§6| §b检测到 §dPlaceholderAPI §b插件");
        } else {
            getLogger().info("§6| §a未检测到 §dPlaceholderAPI §a插件");
        }
    }
    public String getPapi(Player player, String string){
        return PlaceholderAPI.setPlaceholders(player, string);
    }

    public void ReloadALLConfig(){
        saveDefaultConfig();
        reloadConfig();
        ReloadMes();
        ReloadBuy();
        ReloadEdit();
        ReloadALLStore();
    }
    public void ReloadALLStore(){
        File Store = new File(getDataFolder(),"/Store/");
        TryStore(Store);
        ReadStore(Store);
        for (Map.Entry<String, File> store : Stores.entrySet()) {
            ReloadStore(store.getKey(),store.getValue());
        }
    }
    private void TryStore(File store){
        if (!store.exists()) {
            store.mkdirs();
            File Point = new File(store, "Battle.yml");
            File Evolve = new File(store,"Evolve.yml");
            File Exclusive = new File(store,"Exclusive.yml");
            if (!Point.exists()){
                try {
                    Point.createNewFile();
                    saveResourceToFile("Battle.yml", Point);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            if (!Evolve.exists()){
                try {
                    Evolve.createNewFile();
                    saveResourceToFile("Evolve.yml", Evolve);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            if (!Exclusive.exists()){
                try {
                    Exclusive.createNewFile();
                    saveResourceToFile("Exclusive.yml", Exclusive);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
    private void saveResourceToFile(String resourcePath, File file) {
        InputStream inputStream = getResource(resourcePath);
        if (inputStream == null) {
            throw new IllegalArgumentException("§c未找到" + resourcePath);
        }
        try (OutputStream outputStream = Files.newOutputStream(file.toPath())) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private void ReadStore(File Store) {
        File[] stores = Store.listFiles();
        if (stores != null) {
            for (File store : stores) {
                if (store.isFile() && store.getName().endsWith(".yml")) {
                    String name = store.getName().replace(".yml","");
                    Stores.put(name,store);
                    StoresList.add(name);
                } else if (store.isDirectory()) {
                    ReadStore(store);
                }
            }
        }
    }

    @Override
    public void onDisable() {
        plugin = null;
        getLogger().info("§6-----------------");
        getLogger().info("§6| §b沐夜自定义商店 §c卸载!!!");
        getLogger().info("§6-----------------");
    }
    private void checkVault() {
        RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(Economy.class);
        if (economyProvider != null && economyProvider.getProvider() == null){
            getLogger().info("§6| §c未检测到 §eVault §c插件");
            getLogger().info("§6| §c已卸载本插件");
            getLogger().info("§6-----------------");
            Bukkit.getPluginManager().disablePlugin(this);
        } else {
            getLogger().info("§6| §b检测到 §eVault §b插件");
            economyManager = getServer().getServicesManager().getRegistration(Economy.class).getProvider();

        }
    }
    private void checkPP() {
        try {
            Class.forName("org.black_ixx.playerpoints.PlayerPointsAPI");
        } catch (ClassNotFoundException e) {
            getLogger().info("§6| §c未检测到 §9PlayerPoints §c插件");
            getLogger().info("§6| §c已卸载本插件");
            getLogger().info("§6-----------------");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        getLogger().info("§6| §b检测到 §9PlayerPoints §b插件");
        final Plugin plugin = this.getServer().getPluginManager().getPlugin("PlayerPoints");
        playerPointsManager = (PlayerPoints) plugin;
    }
}
