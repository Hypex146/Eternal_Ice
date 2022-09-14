package plugin.managers;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import plugin.EternalIce;
import plugin.abilities.Abilities;
import plugin.abilities.Ability;
import plugin.abilities.PsyExplosion;
import plugin.abilities.Pull;
import plugin.abilities.Repulsion;
import plugin.abilities.Stun;

public class AbilityManager {
	private EternalIce main_plugin_;
	// configuration
	// constants
	private final Map<Abilities, Ability> abilities_;
	private final NamespacedKey r_button_key_;
	private final NamespacedKey l_button_key_;
	
	public AbilityManager(EternalIce main_plugin) {
		main_plugin_ = main_plugin;
		r_button_key_ = new NamespacedKey(main_plugin_, "r_button_action");
		l_button_key_ = new NamespacedKey(main_plugin_, "l_button_action");
		abilities_ = new HashMap<Abilities, Ability>();
		abilities_.put(Abilities.PSYEXPLOSION, new PsyExplosion(main_plugin_));
		abilities_.put(Abilities.STUN, new Stun(main_plugin_));
		abilities_.put(Abilities.REPULSION, new Repulsion(main_plugin_));
		abilities_.put(Abilities.PULL, new Pull(main_plugin_));
	}
	
	public void updateParams() {
		updateAbilities();
	}
	
	private void updateAbilities() {
		abilities_.forEach(new BiConsumer<Abilities, Ability>() {
			@Override
			public void accept(Abilities t, Ability u) {
				u.updateParams();
			}
		});
	}
	
	public int getCooldown(Player player, boolean isRBM) {
		ItemStack item_stack = player.getEquipment().getItemInMainHand();
		PersistentDataContainer container = item_stack.getItemMeta().getPersistentDataContainer();
		NamespacedKey current_key;
		if (isRBM) {
			current_key = r_button_key_;
		} else {
			current_key = l_button_key_;
		}
		String tag = container.getOrDefault(current_key, PersistentDataType.STRING, null);
		if (tag == null) { return -1; }
		Abilities ability_type = null;
		try {
			ability_type = Abilities.valueOf(tag);
		} catch (Exception e) {
			player.sendMessage("Ошибка при вызове способности!");
		}
		if (ability_type == null) { return -1; }
		Ability ability = abilities_.get(ability_type);
		return ability.getCooldown(player);
	}
	
	public void processClickEvent(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		if (event.getAction() == Action.PHYSICAL) { return; }
		if (event.getHand()!=EquipmentSlot.HAND) { return; }
		if (!event.hasItem()) { return; }
		ItemStack item_stack = event.getItem();
		NamespacedKey current_key;
		if (event.getAction() == Action.RIGHT_CLICK_AIR 
				|| event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			current_key = r_button_key_;
		} else {
			current_key = l_button_key_;
		}
		PersistentDataContainer container = item_stack.getItemMeta().getPersistentDataContainer();
		String tag = null;
		if (container.has(current_key, PersistentDataType.STRING)) {
			tag = container.getOrDefault(current_key, PersistentDataType.STRING, null);
		}
		if (tag == null) { return; }
		if (!(main_plugin_.getWorlds().contains(player.getWorld()))) {
			player.sendMessage("В этом мире нельзя пользоваться способностями!");
			return;
		}
		Abilities ability = null;
		try {
			ability = Abilities.valueOf(tag);
		} catch (Exception e) {
			player.sendMessage("Ошибка при вызове способности!");
		}
		if (ability == null) { return; }
		abilities_.get(ability).onCall(player);
	}
	
	public NamespacedKey getRightClickTag() {
		return r_button_key_;
	}
	
	public NamespacedKey getLeftClickTag() {
		return l_button_key_;
	}
	
}
