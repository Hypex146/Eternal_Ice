package plugin.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

import plugin.EternalIce;

public class PluginCommand implements CommandExecutor {
	private EternalIce main_plugin_;
	// configuration
	// constants
	
	public PluginCommand (EternalIce main_plugin) {
		this.main_plugin_ = main_plugin;
	}
	
	private boolean reloadCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!sender.hasPermission("eternal_ice.admin") && !(sender instanceof ConsoleCommandSender)) {
			sender.sendMessage("У вас нет прав для выполнения данной комманды!");
			return false;
		}
		main_plugin_.loadConfig();
		main_plugin_.checkEnableStatus();
		sender.sendMessage("Конфиг успешно перезагружен");
		return true;
	}
	
	private void sendHelpsList(CommandSender sender) {
		sender.sendMessage("> §a/eternal_ice §fили §a/eternal_ice help "
				+ "§f- помощь по командам ядра Eternal Ice");
		sender.sendMessage("> §a/eternal_ice help level "
				+ "§f- помощь по командам работы с уровнями");
		sender.sendMessage("> §a/eternal_ice help score "
				+ "§f- помощь по командам работы со score points");
		sender.sendMessage("> §a/eternal_ice help ability "
				+ "§f- помощь по командам работы со способностями");
		sender.sendMessage("> §a/eternal_ice help aliases "
				+ "§f- все сокращения команд");
	}
	
	private boolean helpCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!sender.hasPermission("eternal_ice.user")) {
			sender.sendMessage("У вас нет прав для выполнения данной комманды!");
			return false;
		}
		sender.sendMessage("===Eternal Ice help===");
		sender.sendMessage("> §c/eternal_ice reload "
				+ "§f- перезагрузка плагина и конфигов");
		sender.sendMessage("----------------------");
		sendHelpsList(sender);
		sender.sendMessage("======================");
		return true;
	}
	
	private boolean helpAliasesCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!sender.hasPermission("eternal_ice.user")) {
			sender.sendMessage("У вас нет прав для выполнения данной комманды!");
			return false;
		}
		sender.sendMessage("===Eternal Ice help aliases===");
		sender.sendMessage("Команда: eternal_ice; "
				+ "aliases: ei");
		sender.sendMessage("Команда: eternal_ice_level; "
				+ "aliases: level, ei_level, lvl, ei_lvl");
		sender.sendMessage("Команда: eternal_ice_score; "
				+ "aliases: score, ei_score, scr, ei_scr");
		sender.sendMessage("Команда: eternal_ice_ability; "
				+ "aliases: ability, ei_ability, abl, ei_abl");
		sender.sendMessage("------------------------------");
		sendHelpsList(sender);
		sender.sendMessage("==============================");
		return true;
	}
	
	private boolean helpLevelCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!sender.hasPermission("eternal_ice.user")) {
			sender.sendMessage("У вас нет прав для выполнения данной комманды!");
			return false;
		}
		sender.sendMessage("====Eternal Ice help level====");
		sender.sendMessage("> §a/eternal_ice_level или /eternal_ice_level info "
				+ "§f- узнать ваш уровень");
		sender.sendMessage("> §a/eternal_ice_level up "
				+ "§f- повысить ваш уровень");
		sender.sendMessage("> §c/eternal_ice_level list "
				+ "§f- просмотреть список доступных уровней");
		sender.sendMessage("> §c/eternal_ice_level get <player> "
				+ "§f- узнать уровень игрока");
		sender.sendMessage("> §c/eternal_ice_level set <player> <level> "
				+ "§f- установить игроку определённй уровень");
		sender.sendMessage("------------------------------");
		sendHelpsList(sender);
		sender.sendMessage("==============================");
		return true;
	}
	
	private boolean helpScoreCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!sender.hasPermission("eternal_ice.user")) {
			sender.sendMessage("У вас нет прав для выполнения данной комманды!");
			return false;
		}
		sender.sendMessage("====Eternal Ice help score====");
		sender.sendMessage("> §a/eternal_ice_score или /eternal_ice_score info "
				+ "§f- узнать ваши score points");
		sender.sendMessage("> §c/eternal_ice_score get <player> "
				+ "§f- узнать score points игрока");
		sender.sendMessage("> §c/eternal_ice_score set <player> <points> "
				+ "§f- установить игроку score points");
		sender.sendMessage("------------------------------");
		sendHelpsList(sender);
		sender.sendMessage("==============================");
		return true;
	}
	
	private boolean helpAbilityCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!sender.hasPermission("eternal_ice.user")) {
			sender.sendMessage("У вас нет прав для выполнения данной комманды!");
			return false;
		}
		sender.sendMessage("===Eternal Ice help ability===");
		sender.sendMessage("> §a/eternal_ice_ability или /eternal_ice_ability info "
				+ "§f- узнать какая способность привязана к предмету в руках");
		sender.sendMessage("> §c/eternal_ice_ability set [left|right] <ability> "
				+ "§f- связасть предмет в руках с определённой способностью");
		sender.sendMessage("------------------------------");
		sendHelpsList(sender);
		sender.sendMessage("==============================");
		return true;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (args.length == 0) {
			return helpCommand(sender, command, label, args);
		}
		if (args.length == 1) {
			if (args[0].equals("help")) {
				return helpCommand(sender, command, label, args);
			}
			if (args[0].equals("reload")) {
				return reloadCommand(sender, command, label, args);
			}
		}
		if (args.length == 2) {
			if (args[0].equals("help")) {
				if (args[1].equals("level")) {
					return helpLevelCommand(sender, command, label, args);
				} else if (args[1].equals("score")) {
					return helpScoreCommand(sender, command, label, args);
				} else if (args[1].equals("ability")) {
					return helpAbilityCommand(sender, command, label, args);
				} else if (args[1].equals("aliases")) {
					return helpAliasesCommand(sender, command, label, args);
				}
			}
		}
		sender.sendMessage("Ошибка в команде!");
		return false;
	}

}
