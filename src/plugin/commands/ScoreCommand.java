package plugin.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import plugin.EternalIce;

public class ScoreCommand implements CommandExecutor {
	private EternalIce main_plugin_;
	// configuration
	// constants
	
	public ScoreCommand(EternalIce main_plugin) {
		main_plugin_ = main_plugin;
	}
	
	private boolean scrInfoCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!sender.hasPermission("eternal_ice.user")) {
			sender.sendMessage("У вас нет прав для выполнения данной комманды!");
			return false;
		}
		if (!(sender instanceof Player)) {
			sender.sendMessage("Данную команду можно выполнить только от лица игрока!");
			return false;
		}
		Player player = (Player)sender;
		int score = main_plugin_.getScoreManager().getScore(player);
		sender.sendMessage("У вас " + score + " score points!");
		return true;
	}
	
	private boolean scrGetCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!sender.hasPermission("eternal_ice.admin")) {
			sender.sendMessage("У вас нет прав для выполнения данной комманды!");
			return false;
		}
		Player player = main_plugin_.getServer().getPlayer(args[1]);
		if (player == null) {
			sender.sendMessage("Игрок не найден!");
			return false;
		}
		int score = main_plugin_.getScoreManager().getScore(player);
		sender.sendMessage("У игрока " + player.getName() + 
				" " + score + " score points!");
		return true;
	}
	
	private boolean scrSetCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!sender.hasPermission("eternal_ice.admin")) {
			sender.sendMessage("У вас нет прав для выполнения данной комманды!");
			return false;
		}
		Player player = main_plugin_.getServer().getPlayer(args[1]);
		if (player == null) {
			sender.sendMessage("Игрок не найден!");
			return false;
		}
		int score = 0;
		try {
			score = Integer.parseInt(args[2]);
		} catch (Exception e) {
			sender.sendMessage("Ошибка в команде!");
			return false;
		}
		main_plugin_.getScoreManager().setScore(player, score);
		return true;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (args.length == 0) {
			return scrInfoCommand(sender, command, label, args);
		}
		if (args.length == 1) {
			if (args[0].equals("info")) {
				return scrInfoCommand(sender, command, label, args);
			}
		}
		if (args.length == 2) {
			if (args[0].equals("get")) {
				return scrGetCommand(sender, command, label, args);
			}
		}
		if (args.length == 3) {
			if (args[0].equals("set")) {
				return scrSetCommand(sender, command, label, args);
			}
		}
		sender.sendMessage("Ошибка в команде!");
		return false;
	}

}
