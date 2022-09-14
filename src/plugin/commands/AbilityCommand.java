package plugin.commands;

import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import plugin.EternalIce;

public class AbilityCommand implements CommandExecutor {
	private EternalIce main_plugin_;
	// configuration
	// constants
	
	public AbilityCommand(EternalIce main_plugin) {
		main_plugin_ = main_plugin;
	}
	
	private boolean abilitySetCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!sender.hasPermission("eternal_ice.admin")) {
			sender.sendMessage("У вас нет прав для выполнения данной комманды!");
			return false;
		}
		if (!(sender instanceof Player)) {
			sender.sendMessage("Данную команду можно выполнить только от лица игрока!");
			return false;
		}
		Player player = (Player)sender;
		ItemStack item_stack = player.getInventory().getItemInMainHand();
		if (item_stack == null) {
			sender.sendMessage("Предмет должен находится в основной руке!");
			return false;
		}
		ItemMeta item_meta = item_stack.getItemMeta();
		String tag = args[2];
		NamespacedKey key;
		if (args[1].equals("right")) {
			key = main_plugin_.getAbilityManager().getRightClickTag();
		} else if (args[1].equals("left")) {
			key = main_plugin_.getAbilityManager().getLeftClickTag();
		} else {
			sender.sendMessage("Первый аргумент либо \"right\", либо \"left\"!");
			return false;
		}
		item_meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, tag); 
		item_stack.setItemMeta(item_meta);
		return true;
	}
	
	private boolean itemAbilityInfoCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!sender.hasPermission("eternal_ice.user")) {
			sender.sendMessage("У вас нет прав для выполнения данной комманды!");
			return false;
		}
		if (!(sender instanceof Player)) {
			sender.sendMessage("Данную команду можно выполнить только от лица игрока!");
			return false;
		}
		Player player = (Player)sender;
		ItemStack item_stack = player.getInventory().getItemInMainHand();
		if (item_stack == null) {
			sender.sendMessage("Предмет должен находится в основной руке!");
			return false;
		}
		ItemMeta item_meta = item_stack.getItemMeta();
		if (item_meta == null) {
			sender.sendMessage("Предмет должен находится в основной руке!");
			return false;
		}
		String right_tag = "не назначено";
		if (item_meta.getPersistentDataContainer().has(
				main_plugin_.getAbilityManager().getRightClickTag(), PersistentDataType.STRING)) {
			right_tag = item_meta.getPersistentDataContainer().getOrDefault(
			main_plugin_.getAbilityManager().getRightClickTag(), 
			PersistentDataType.STRING, "не назначено");
		}
		String left_tag = "не назначено";
		if (item_meta.getPersistentDataContainer().has(
				main_plugin_.getAbilityManager().getLeftClickTag(), PersistentDataType.STRING)) {
			left_tag = item_meta.getPersistentDataContainer().getOrDefault(
			main_plugin_.getAbilityManager().getLeftClickTag(), 
			PersistentDataType.STRING, "не назначено");
		}
		sender.sendMessage("ПКМ: " + right_tag);
		sender.sendMessage("ЛКМ: " + left_tag);
		return true;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (args.length == 0) {
			return itemAbilityInfoCommand(sender, command, label, args);
		}
		if (args.length == 1) {
			if (args[0].equals("info")) {
				return itemAbilityInfoCommand(sender, command, label, args);
			}
		}
		if (args.length == 3) {
			if (args[0].equals("set")) {
				return abilitySetCommand(sender, command, label, args);
			}
		}
		sender.sendMessage("Ошибка в команде!");
		return false;
	}

}
