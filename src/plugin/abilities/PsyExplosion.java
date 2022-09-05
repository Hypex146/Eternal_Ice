package plugin.abilities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.util.Vector;

import plugin.EternalIce;
import plugin.utilities.EIConfigurator;
import plugin.utilities.MyUtilities;

public class PsyExplosion implements Ability {
	private EternalIce main_plugin_;
	private Map<String, Integer> cooldowns_;
	private int cooldown_task_id_;
	// configuration
	private Map<String, Object> params_;
	// constants
	private final String ability_name_ = "Psy_Explosion";
	private final Abilities type_ = Abilities.PSYEXPLOSION;
	
	public PsyExplosion(EternalIce main_plugin) {
		main_plugin_ = main_plugin;
		cooldowns_ = new HashMap<String, Integer>();
		params_ = new HashMap<String, Object>();
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
		params_.clear();
		Integer required_lvl = ei_cfg.getInt(ability_section, "required_lvl", 1);
		Integer mana_cost = ei_cfg.getInt(ability_section, "mana_cost", 1);
		Double dist = ei_cfg.getDouble(ability_section, "dist", 1);
		Integer charging_time = ei_cfg.getInt(ability_section, "charging_time", 1);
		Double cos_detect_angle = ei_cfg.getDouble(ability_section, "cos_detect_angle", 0.95D);
		Double damage = ei_cfg.getDouble(ability_section, "damage", 1D);
		Integer cooldown = ei_cfg.getInt(ability_section, "cooldown", 1);
		Double vertical_speed = ei_cfg.getDouble(ability_section, "vertical_speed", 0);
		String use_message = ei_cfg.getString(ability_section, 
				"use_message", "используется Psy Explosion!");
		String low_mana_message = ei_cfg.getString(ability_section, 
				"low_mana_message", "Не хватает маны!");
		String cooldown_message = ei_cfg.getString(ability_section, 
				"cooldown_message", "Перезарядка способности!");
		String low_lvl_message = ei_cfg.getString(ability_section, 
				"low_lvl_message", "Для ипользования требуется больший уровень!");
		Collection<Material> material_filter = new ArrayList<Material>();
		List<String> str_material_filter = ei_cfg.getStringList(ability_section, "transparent_materials");
		for (String str_material : str_material_filter) {
			try {
				Material material = Material.valueOf(str_material);
				material_filter.add(material);
			} catch (IllegalArgumentException e) {}
		}
		Collection<EntityType> entity_filter = new ArrayList<EntityType>();
		List<String> str_entity_filter = ei_cfg.getStringList(ability_section, "susceptible_entities");
		for (String str_material : str_entity_filter) {
			try {
				EntityType material = EntityType.valueOf(str_material);
				entity_filter.add(material);
			} catch (IllegalArgumentException e) {}
		}
		params_.put("required_lvl", required_lvl);
		params_.put("mana_cost", mana_cost);
		params_.put("dist", dist);
		params_.put("charging_time", charging_time);
		params_.put("cos_detect_angle", cos_detect_angle);
		params_.put("damage", damage);
		params_.put("cooldown", cooldown);
		params_.put("vertical_speed", vertical_speed);
		params_.put("use_message", use_message);
		params_.put("low_mana_message", low_mana_message);
		params_.put("cooldown_message", cooldown_message);
		params_.put("low_lvl_message", low_lvl_message);
		params_.put("transparent_materials", material_filter);
		params_.put("susceptible_entities", entity_filter);
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
	public String getName() {
		return ability_name_;
	}

	@Override
	public Abilities getType() {
		return type_;
	}
	
	@Override
	public Map<String, Object> getParams() {
		return params_;
	}
	
	private void useAbility(Player player) {
		player.sendMessage((String) params_.get("use_message"));
		Location loc = player.getLocation();
		player.getWorld().spawnParticle(Particle.CLOUD, loc, 25, 0.5, 0.5, 0.5, 0.1);
		int charging_time = (int) params_.get("charging_time");
		double vertical_speed = (double) params_.get("vertical_speed");
		Vector velocity = player.getVelocity();
		velocity.setX(0D);
		velocity.setZ(0D);
		velocity.setY(vertical_speed);
		main_plugin_.getServer().getScheduler().scheduleSyncDelayedTask(main_plugin_, 
				new Runnable() {
					private int count_ = 0;
					@SuppressWarnings("unchecked")
					@Override
					public void run() {
						count_++;
						if (count_ <= charging_time) {
							player.setVelocity(velocity);
							main_plugin_.getServer().getScheduler().scheduleSyncDelayedTask(
									main_plugin_, this, 1);
						} else {
							Location current_loc = player.getLocation();
							current_loc.setY(current_loc.getY() + 0.5D);
							player.getWorld().spawnParticle(Particle.DRAGON_BREATH, 
									current_loc, 75, 0.5, 0.5, 0.5, 0.25);
							List<EntityType> filter = (ArrayList<EntityType>) params_.get("susceptible_entities");
							Collection<LivingEntity> preys = MyUtilities.getLookingEntities(
									player,
									MyUtilities.createPredicate(filter),
									(double) params_.get("dist"),
									(double) params_.get("cos_detect_angle"),
									(ArrayList<Material>) params_.get("transparent_materials"));
							for (LivingEntity prey : preys) {
								prey.damage((double) params_.get("damage"), player);
							}
						}
					}
			}, 0);
	}
	
//	private void useAbility(Player player) {
//		player.sendMessage((String) ability_table_.get("use_message"));
//		Location loc = player.getLocation();
//		player.getWorld().spawnParticle(Particle.CLOUD, loc, 25, 0.5, 0.5, 0.5, 0.1);
//		loc.setY(loc.getY() + 1D);
//		int charging_time = (int) ability_table_.get("charging_time");
//		main_plugin_.getServer().getScheduler().scheduleSyncDelayedTask(main_plugin_, 
//				new Runnable() {
//					private int count_ = 0;
//					@Override
//					public void run() {
//						count_++;
//						if (count_ <= charging_time) {
//							player.teleport(loc);
//							main_plugin_.getServer().getScheduler().scheduleSyncDelayedTask(
//									main_plugin_, this, 1);
//						} else {
//							loc.setY(loc.getY() + 0.5D);
//							player.getWorld().spawnParticle(Particle.DRAGON_BREATH, 
//									loc, 75, 0.5, 0.5, 0.5, 0.25);
//							List<EntityType> filter = new ArrayList<EntityType>();
//							filter.add(EntityType.PLAYER);
//							Collection<LivingEntity> preys = MyExpansion.getLookingEntities(
//									player,
//									MyExpansion.createPredicate(filter),
//									(double) ability_table_.get("dist"),
//									(double) ability_table_.get("cos_detect_angle"));
//							for (LivingEntity prey : preys) {
//								prey.damage((double) ability_table_.get("damage"));
//							}
//						}
//					}
//			}, 0);
//	}

	@Override
	public boolean canCall(Player player) {
		if (main_plugin_.getLevelManager().getLevel(player) < 
				(int) params_.get("required_lvl")) {
			player.sendMessage((String) params_.get("low_lvl_message"));
			return false;
			}
		if (main_plugin_.getManaManager().getMana(player) < 
				(int) params_.get("mana_cost")) {
			player.sendMessage((String) params_.get("low_mana_message"));
			return false;
		}
		if (cooldowns_.containsKey(player.getName())) {
			player.sendMessage((String) params_.get("cooldown_message"));
			return false;
		}
		return true;
	}

	@Override
	public boolean onCall(Player player) {
		if (!canCall(player)) { return false; }
		main_plugin_.getManaManager().addMana(player, 
				(-1) * (int) params_.get("mana_cost"));
		cooldowns_.put(player.getName(), (Integer) params_.get("cooldown"));
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
				cooldowns_.forEach(new BiConsumer<String, Integer>() {
					@Override
					public void accept(String t, Integer u) {
						Integer new_value = u - 1;
						if (new_value <= 0) {
							for_del.add(t);
						} else {
							cooldowns_.replace(t, new_value);
						}
					}
				});
				for (String name : for_del) {
					cooldowns_.remove(name);
				}
			}
		};
		cooldown_task_id_ = main_plugin_.getServer().getScheduler().scheduleSyncRepeatingTask(
				main_plugin_, task, 20, 20);
	}

	@Override
	public int getCooldown(Player player) {
		return cooldowns_.getOrDefault(player.getName(), 0);
	}

}
