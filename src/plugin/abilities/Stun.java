package plugin.abilities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

import org.bukkit.Particle;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitScheduler;

import plugin.EternalIce;
import plugin.utilities.EIConfigurator;
import plugin.utilities.MyExpansion;

public class Stun implements Ability {
	private EternalIce main_plugin_;
	private Map<String, Integer> cooldowns_table_;
	private int cooldown_task_id_;
	// configuration
	private Map<String, Object> ability_table_;
	// constants
	private final String ability_name_ = "Stun";
	private final Abilities type_ = Abilities.STUN;
	
	public Stun(EternalIce main_plugin) {
		main_plugin_ = main_plugin;
		cooldowns_table_ = new HashMap<String, Integer>();
		ability_table_ = new HashMap<String, Object>();
	}
	
	@Override
	public void updateParams() {
		FileConfiguration cfg = main_plugin_.getConfig();
		EIConfigurator ei_cfg = main_plugin_.getEIConfigurator();
		ConfigurationSection ability_section = ei_cfg.getConfigurationSection(cfg, 
				"Abilities." + ability_name_);
		updateAbilityTable(ability_section);
	}
	
	private void updateAbilityTable(ConfigurationSection ability_section) {
		EIConfigurator ei_cfg = main_plugin_.getEIConfigurator();
		ability_table_.clear();
		Integer required_lvl = ei_cfg.getInt(ability_section, "required_lvl", 1);
		Integer mana_cost = ei_cfg.getInt(ability_section, "mana_cost", 1);
		Double dist = ei_cfg.getDouble(ability_section, "dist", 1);
		Integer limit_stun = ei_cfg.getInt(ability_section, "limit_stun", 1);
		Integer time_stun = ei_cfg.getInt(ability_section, "time_stun", 1);
		Double cos_detect_angle = ei_cfg.getDouble(ability_section, "cos_detect_angle", 0.95D);
		Integer cooldown = ei_cfg.getInt(ability_section, "cooldown", 1);
		String use_message = ei_cfg.getString(ability_section, 
				"use_message", "используется Stun!");
		String low_mana_message = ei_cfg.getString(ability_section, 
				"low_mana_message", "Не хватает маны!");
		String cooldown_message = ei_cfg.getString(ability_section, 
				"cooldown_message", "Перезарядка способности!");
		String low_lvl_message = ei_cfg.getString(ability_section, 
				"low_lvl_message", "Для ипользования требуется больший уровень!");
		ability_table_.put("required_lvl", required_lvl);
		ability_table_.put("mana_cost", mana_cost);
		ability_table_.put("dist", dist);
		ability_table_.put("limit_stun", limit_stun);
		ability_table_.put("time_stun", time_stun);
		ability_table_.put("cos_detect_angle", cos_detect_angle);
		ability_table_.put("cooldown", cooldown);
		ability_table_.put("use_message", use_message);
		ability_table_.put("low_mana_message", low_mana_message);
		ability_table_.put("cooldown_message", cooldown_message);
		ability_table_.put("low_lvl_message", low_lvl_message);
		updateCooldownTask();
	}
	
	private void updateCooldownTask() {
		BukkitScheduler scheduler =  main_plugin_.getServer().getScheduler();
		if (scheduler.isCurrentlyRunning(cooldown_task_id_) 
				|| scheduler.isQueued(cooldown_task_id_)) {
			scheduler.cancelTask(cooldown_task_id_);
		}
		regCooldown();
	}

	@Override
	public Abilities getType() {
		return type_;
	}

	@Override
	public String getName() {
		return ability_name_;
	}

	@Override
	public Map<String, Object> getAbilityTable() {
		return ability_table_;
	}
	
	private void useAbility(Player player) { // FIXME
		player.sendMessage((String) ability_table_.get("use_message"));
		List<EntityType> filter = new ArrayList<EntityType>();
		filter.add(EntityType.PLAYER);
		Collection<LivingEntity> preys;
		double dist = (double) ability_table_.get("dist");
		double cos_detect_angle = (double) ability_table_.get("cos_detect_angle");
		int limit_stun = (int) ability_table_.get("limit_stun");
		int time_stun = (int) ability_table_.get("time_stun");
		if (limit_stun < 0) {
			preys = MyExpansion.getUnderAbservation(player, 
					MyExpansion.createPredicate(filter), dist, cos_detect_angle);
		} else {
			preys = MyExpansion.getUnderAbservation(player, 
					MyExpansion.createPredicate(filter), dist, cos_detect_angle, limit_stun);
		}
		if (preys!=null) {
			for (LivingEntity prey : preys) {
//				if(prey.hasPotionEffect(PotionEffectType.SLOW)) {
//					prey.removePotionEffect(PotionEffectType.SLOW);
//					prey.removePotionEffect(PotionEffectType.BLINDNESS);
//				}
				prey.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, time_stun, 9));
				prey.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, time_stun, 0));
				prey.getWorld().spawnParticle(Particle.CLOUD, prey.getLocation(), 25, 0.5, 0.5, 0.5, 0.25);
			}
		}
	}

	@Override
	public boolean canCall(Player player) {
		if (main_plugin_.getLevelManager().getLevel(player) < 
				(int) ability_table_.get("required_lvl")) {
			player.sendMessage((String) ability_table_.get("low_lvl_message"));
			return false;
			}
		if (main_plugin_.getManaManager().getMana(player) < 
				(int) ability_table_.get("mana_cost")) {
			player.sendMessage((String) ability_table_.get("low_mana_message"));
			return false;
		}
		if (cooldowns_table_.containsKey(player.getName())) {
			player.sendMessage((String) ability_table_.get("cooldown_message"));
			return false;
		}
		return true;
	}

	@Override
	public boolean onCall(Player player) {
		if (!canCall(player)) { return false; }
		main_plugin_.getManaManager().addMana(player, 
				(-1) * (int) ability_table_.get("mana_cost"));
		cooldowns_table_.put(player.getName(), (Integer) ability_table_.get("cooldown"));
		useAbility(player);
		return false;
	}

	@Override
	public boolean onCall(Player player, boolean forced) {
		if (forced) {
			useAbility(player);
		} else {
			onCall(player);
		}
		return true;
	}
	
	private void regCooldown() {
		Runnable task = new Runnable() {
			@Override
			public void run() {
				Collection<String> for_del = new ArrayList<String>();
				cooldowns_table_.forEach(new BiConsumer<String, Integer>() {
					@Override
					public void accept(String t, Integer u) {
						Integer new_value = u - 1;
						if (new_value <= 0) {
							for_del.add(t);
						} else {
							cooldowns_table_.replace(t, new_value);
						}
					}
				});
				for (String name : for_del) {
					cooldowns_table_.remove(name);
				}
			}
		};
		main_plugin_.getServer().getScheduler().scheduleSyncRepeatingTask(main_plugin_, task, 20, 20);
	}

}
