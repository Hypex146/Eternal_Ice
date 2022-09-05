package plugin.commands;

import java.util.List;
import java.util.Map;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import plugin.EternalIce;

public class LevelCommand implements CommandExecutor {
	private EternalIce main_plugin_;
	// configuration
	// constants
	
	public LevelCommand(EternalIce main_plugin) {
		main_plugin_ = main_plugin;
	}
	
	private boolean lvlInfoCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!sender.hasPermission("eternal_ice.user")) {
			sender.sendMessage("У вас нет прав для выполнения данной комманды!");
			return false;
		}
		if (!(sender instanceof Player)) {
			sender.sendMessage("Данную команду можно выполнить только от лица игрока!");
			return false;
		}
		Player player = (Player)sender;
//		if (!player.getWorld().getName().equals(main_plugin_.getWorldName())) {
//			sender.sendMessage("Данную команду можно выполнить только в мире " + 
//					main_plugin_.getWorldName()  + "!");
//			return false;
//		}
		int level = main_plugin_.getLevelManager().getLevel(player);
		if (level == -1) {
			sender.sendMessage("Игрок не найден!");
			return false;
		}
		sender.sendMessage("У вас " + level + " уровень!");
		return true;
	}
	
	private boolean lvlUpCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!sender.hasPermission("eternal_ice.user")) {
			sender.sendMessage("У вас нет прав для выполнения данной комманды!");
			return false;
		}
		if (!(sender instanceof Player)) {
			sender.sendMessage("Данную команду можно выполнить только от лица игрока!");
			return false;
		}
		Player player = (Player)sender;
//		if (!player.getWorld().getName().equals(main_plugin_.getWorldName())) {
//			sender.sendMessage("Данную команду можно выполнить только в мире " + 
//					main_plugin_.getWorldName()  + "!");
//			return false;
//		}
		if (!main_plugin_.getLevelManager().levelUp(player)) {
			sender.sendMessage("Невозможно повысить уровень! "
					+ "У вас не хватает очков, либо у вас уже наивысший уровень, либо игрок не найден!");
			return false;
		}
		sender.sendMessage("Ваш уровень успешно повышен!");
		return true;
	}
	
	private boolean lvlListCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!sender.hasPermission("eternal_ice.admin")) {
			sender.sendMessage("У вас нет прав для выполнения данной комманды!");
			return false;
		}
		List<Map<String, Object>> level_table = main_plugin_.getLevelManager().getLevelTable();
		int max_level = main_plugin_.getLevelManager().getMaxLevel();
		sender.sendMessage("Всего уровней: " + max_level);
		for (int i = 0; i < max_level; i++) {
			int cost = (int) level_table.get(i).get("cost");
			int mana_reserve = (int) level_table.get(i).get("mana_reserve");
			int add_mana_value = (int) level_table.get(i).get("add_mana_value");
			sender.sendMessage("Уровень " + 
					(i + 1) + ": cost=" + cost + 
						" mana_reserve=" + mana_reserve + 
							" add_mana_value=" + add_mana_value);
		}
		return true;
	}
	
	private boolean lvlGetCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!sender.hasPermission("eternal_ice.admin")) {
			sender.sendMessage("У вас нет прав для выполнения данной комманды!");
			return false;
		}
		Player player = main_plugin_.getServer().getPlayer(args[1]);
		if (player == null) {
			sender.sendMessage("Игрок не найден!");
			return false;
		}
		int level = main_plugin_.getLevelManager().getLevel(player);
		if (level == -1) {
			sender.sendMessage("Игрок не найден!");
			return false;
		}
		sender.sendMessage("Уровень игрока " + player.getName() + ": " + level);
		return true;
	}
	
	private boolean lvlSetCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!sender.hasPermission("eternal_ice.admin")) {
			sender.sendMessage("У вас нет прав для выполнения данной комманды!");
			return false;
		}
		Player player = main_plugin_.getServer().getPlayer(args[1]);
		if (player == null) {
			sender.sendMessage("Игрок не найден!");
			return false;
		}
		int level = 0;
		try {
			level = Integer.parseInt(args[2]);
		} catch (Exception e) {
			sender.sendMessage("Ошибка в команде!");
			return false;
		}
		if (!main_plugin_.getLevelManager().setLevel(player, level)) {
			sender.sendMessage("Неверный уровень, либо игрок не найден!");
			return false;
		}
		return true;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (args.length == 0) {
			return lvlInfoCommand(sender, command, label, args);
		}
		if (args.length == 1) {
			if (args[0].equals("info")) {
				return lvlInfoCommand(sender, command, label, args);
			}
			if (args[0].equals("up")) {
				return lvlUpCommand(sender, command, label, args);
			}
			if (args[0].equals("list")) {
				return lvlListCommand(sender, command, label, args);
			}
		}
		if (args.length == 2) {
			if (args[0].equals("get")) {
				return lvlGetCommand(sender, command, label, args);
			}
		}
		if (args.length == 3) {
			if (args[0].equals("set")) {
				return lvlSetCommand(sender, command, label, args);
			}
		}
		sender.sendMessage("Ошибка в команде!");
		return false;
	}

}
