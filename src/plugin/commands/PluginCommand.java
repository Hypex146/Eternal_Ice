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
			sender.sendMessage("� ��� ��� ���� ��� ���������� ������ ��������!");
			return false;
		}
		main_plugin_.loadConfig();
		main_plugin_.checkEnableStatus();
		sender.sendMessage("������ ������� ������������");
		return true;
	}
	
	private void sendHelpsList(CommandSender sender) {
		sender.sendMessage("> �a/eternal_ice �f��� �a/eternal_ice help "
				+ "�f- ������ �� �������� ���� Eternal Ice");
		sender.sendMessage("> �a/eternal_ice help level "
				+ "�f- ������ �� �������� ������ � ��������");
		sender.sendMessage("> �a/eternal_ice help score "
				+ "�f- ������ �� �������� ������ �� score points");
		sender.sendMessage("> �a/eternal_ice help ability "
				+ "�f- ������ �� �������� ������ �� �������������");
		sender.sendMessage("> �a/eternal_ice help aliases "
				+ "�f- ��� ���������� ������");
	}
	
	private boolean helpCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!sender.hasPermission("eternal_ice.user")) {
			sender.sendMessage("� ��� ��� ���� ��� ���������� ������ ��������!");
			return false;
		}
		sender.sendMessage("===Eternal Ice help===");
		sender.sendMessage("> �c/eternal_ice reload "
				+ "�f- ������������ ������� � ��������");
		sender.sendMessage("----------------------");
		sendHelpsList(sender);
		sender.sendMessage("======================");
		return true;
	}
	
	private boolean helpAliasesCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!sender.hasPermission("eternal_ice.user")) {
			sender.sendMessage("� ��� ��� ���� ��� ���������� ������ ��������!");
			return false;
		}
		sender.sendMessage("===Eternal Ice help aliases===");
		sender.sendMessage("�������: eternal_ice; "
				+ "aliases: ei");
		sender.sendMessage("�������: eternal_ice_level; "
				+ "aliases: level, ei_level, lvl, ei_lvl");
		sender.sendMessage("�������: eternal_ice_score; "
				+ "aliases: score, ei_score, scr, ei_scr");
		sender.sendMessage("�������: eternal_ice_ability; "
				+ "aliases: ability, ei_ability, abl, ei_abl");
		sender.sendMessage("------------------------------");
		sendHelpsList(sender);
		sender.sendMessage("==============================");
		return true;
	}
	
	private boolean helpLevelCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!sender.hasPermission("eternal_ice.user")) {
			sender.sendMessage("� ��� ��� ���� ��� ���������� ������ ��������!");
			return false;
		}
		sender.sendMessage("====Eternal Ice help level====");
		sender.sendMessage("> �a/eternal_ice_level ��� /eternal_ice_level info "
				+ "�f- ������ ��� �������");
		sender.sendMessage("> �a/eternal_ice_level up "
				+ "�f- �������� ��� �������");
		sender.sendMessage("> �c/eternal_ice_level list "
				+ "�f- ����������� ������ ��������� �������");
		sender.sendMessage("> �c/eternal_ice_level get <player> "
				+ "�f- ������ ������� ������");
		sender.sendMessage("> �c/eternal_ice_level set <player> <level> "
				+ "�f- ���������� ������ ���������� �������");
		sender.sendMessage("------------------------------");
		sendHelpsList(sender);
		sender.sendMessage("==============================");
		return true;
	}
	
	private boolean helpScoreCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!sender.hasPermission("eternal_ice.user")) {
			sender.sendMessage("� ��� ��� ���� ��� ���������� ������ ��������!");
			return false;
		}
		sender.sendMessage("====Eternal Ice help score====");
		sender.sendMessage("> �a/eternal_ice_score ��� /eternal_ice_score info "
				+ "�f- ������ ���� score points");
		sender.sendMessage("> �c/eternal_ice_score get <player> "
				+ "�f- ������ score points ������");
		sender.sendMessage("> �c/eternal_ice_score set <player> <points> "
				+ "�f- ���������� ������ score points");
		sender.sendMessage("------------------------------");
		sendHelpsList(sender);
		sender.sendMessage("==============================");
		return true;
	}
	
	private boolean helpAbilityCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!sender.hasPermission("eternal_ice.user")) {
			sender.sendMessage("� ��� ��� ���� ��� ���������� ������ ��������!");
			return false;
		}
		sender.sendMessage("===Eternal Ice help ability===");
		sender.sendMessage("> �a/eternal_ice_ability ��� /eternal_ice_ability info "
				+ "�f- ������ ����� ����������� ��������� � �������� � �����");
		sender.sendMessage("> �c/eternal_ice_ability set [left|right] <ability> "
				+ "�f- �������� ������� � ����� � ����������� ������������");
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
		sender.sendMessage("������ � �������!");
		return false;
	}

}
