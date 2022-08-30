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
import plugin.abilities.Repulsion;
import plugin.abilities.Stun;

public class AbilityManager {
	private EternalIce main_plugin_;
	// configuration
	// constants
	private final Map<Abilities, Ability> abilities_table_;
	private final NamespacedKey r_button_key_;
	private final NamespacedKey l_button_key_;
	
	public AbilityManager(EternalIce main_plugin) {
		main_plugin_ = main_plugin;
		r_button_key_ = new NamespacedKey(main_plugin_, "r_button_action");
		l_button_key_ = new NamespacedKey(main_plugin_, "l_button_action");
		abilities_table_ = new HashMap<Abilities, Ability>();
		abilities_table_.put(Abilities.PSYEXPLOSION, new PsyExplosion(main_plugin_));
		abilities_table_.put(Abilities.STUN, new Stun(main_plugin_));
		abilities_table_.put(Abilities.REPULSION, new Repulsion(main_plugin_));
	}
	
	public void updateParams() {
		updateAbilities();
	}
	
	private void updateAbilities() {
		abilities_table_.forEach(new BiConsumer<Abilities, Ability>() {
			@Override
			public void accept(Abilities t, Ability u) {
				u.updateParams();
			}
		});
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
		String tag = container.getOrDefault(current_key, PersistentDataType.STRING, null);
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
		abilities_table_.get(ability).onCall(player);
	}
	
	public NamespacedKey getRightClickTag() {
		return r_button_key_;
	}
	
	public NamespacedKey getLeftClickTag() {
		return l_button_key_;
	}
	
}
