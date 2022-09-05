package plugin.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import plugin.EternalIce;

public class TestCommand  implements CommandExecutor {
	private EternalIce main_plugin_;
	// configuration
	// constants
	
	public TestCommand (EternalIce main_plugin) {
		this.main_plugin_ = main_plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!sender.hasPermission("eternal_ice.admin")) { return false; }
		main_plugin_.getEILogger();
		return false;
	}

}
