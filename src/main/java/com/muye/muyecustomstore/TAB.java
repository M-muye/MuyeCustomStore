package com.muye.muyecustomstore;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class TAB implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        if (command.getName().equalsIgnoreCase("MuyeCustomStore") || command.getName().equalsIgnoreCase("mcs")) {
            if (args.length == 1) {
                if (args[0].toLowerCase().startsWith("r") && sender.isOp()){
                    completions.add("reload");
                    return completions;
                }
                if (args[0].toLowerCase().startsWith("h")){
                    completions.add("help");
                    return completions;
                }
                if (sender instanceof Player){
                    if (args[0].toLowerCase().startsWith("a")){
                        completions.add("add");
                        return completions;
                    }
                    if (args[0].toLowerCase().startsWith("o")){
                        completions.add("open");
                        return completions;
                    }
                    if (args[0].toLowerCase().startsWith("s") && sender.isOp()){
                        completions.add("set");
                        return completions;
                    }
                    if (args[0].toLowerCase().startsWith("c")){
                        completions.add("check");
                        return completions;
                    }
                    completions.add("open");
                    if (sender.isOp()){
                        completions.add("add");
                        completions.add("set");
                    }
                    completions.add("check");
                }
                if (sender.isOp()){
                    completions.add("reload");
                }
                completions.add("help");
                return completions;
            }
            if (args.length == 2 && args[0].equalsIgnoreCase("open") && sender instanceof Player) {
                completions.addAll(MuyeCustomStore.StoresList);
                return completions;
            }
            if (args.length == 2 && args[0].equalsIgnoreCase("add") && sender instanceof Player) {
                completions.addAll(MuyeCustomStore.StoresList);
                return completions;
            }
            if (args.length == 3 && args[0].equalsIgnoreCase("add") && MuyeCustomStore.StoresList.contains(args[1]) && sender instanceof Player) {
                completions.add("物品id或者物品材质名");
                return completions;
            }
            if (args.length == 4 && args[0].equalsIgnoreCase("add") && MuyeCustomStore.StoresList.contains(args[1]) && sender instanceof Player) {
                completions.add("物品数据值范围例如(0-12)");
                return completions;
            }
            if (args.length == 5 && args[0].equalsIgnoreCase("add") && MuyeCustomStore.StoresList.contains(args[1]) && sender instanceof Player) {
                completions.add("价格");
                return completions;
            }
            if (args.length == 2 && args[0].equalsIgnoreCase("check") && sender instanceof Player) {
                if (args[1].startsWith("a")){
                    completions.add("all");
                    return completions;
                }
                completions.add("all");
                completions.addAll(MuyeCustomStore.StoresList);
                return completions;
            }
            if (args.length == 2 && args[0].equalsIgnoreCase("set") && sender instanceof Player && sender.isOp()) {
                completions.addAll(MuyeCustomStore.StoresList);
                return completions;
            }
            if (args.length == 3 && args[0].equalsIgnoreCase("set") && MuyeCustomStore.StoresList.contains(args[1]) && sender instanceof Player && sender.isOp()) {
                if (MuyeCustomStore.StoreType.get(args[1]).equals("money")){
                    completions.add("浮点数");
                }
                if (MuyeCustomStore.StoreType.get(args[1]).equals("point")){
                    completions.add("整数");
                }
                return completions;
            }
            if (args.length == 2 && args[0].equalsIgnoreCase("reload") && sender.isOp()) {
                if (args[1].toLowerCase().startsWith("b")){
                    completions.add("buy");
                    return completions;
                }
                if (args[1].toLowerCase().startsWith("m")){
                    completions.add("message");
                    return completions;
                }
                if (args[1].toLowerCase().startsWith("e")){
                    completions.add("edit");
                    return completions;
                }
                if (args[1].toLowerCase().startsWith("s")){
                    completions.add("stores");
                    return completions;
                }
                completions.addAll(MuyeCustomStore.StoresList);
                completions.add("message");
                completions.add("buy");
                completions.add("edit");
                completions.add("stores");
                return completions;
            }
            if (args.length == 3 && args[0].equalsIgnoreCase("reload") && MuyeCustomStore.StoresList.contains(args[1]) && sender.isOp()){
                if (args[2].toLowerCase().startsWith("i")){
                    completions.add("items");
                    return completions;
                }
                if (args[2].toLowerCase().startsWith("g")){
                    completions.add("gui");
                    return completions;
                }
                completions.add("items");
                completions.add("gui");
                return completions;
            }
        }
        return completions;
    }
}
