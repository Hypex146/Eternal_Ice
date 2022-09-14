package plugin.managers;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import plugin.EternalIce;
import plugin.utilities.EIConfigurator;
import plugin.utilities.LogLevel;

public class ScoreManager {
	private EternalIce main_plugin_;
	private Scoreboard board_;
	private int cooldown_task_id_;
	// configuration
	private final Map<String, Object> params_;
	// const
	private final NamespacedKey score_reward_key_;
	private final NamespacedKey cooldown_farm_key_;
	
	public ScoreManager(EternalIce main_plugin) {
		main_plugin_ = main_plugin;
		params_ = new HashMap<String, Object>();
		score_reward_key_ = new NamespacedKey(main_plugin_, "score_reward");
		cooldown_farm_key_ = new NamespacedKey(main_plugin_, "score_cooldown");
	}
	
	public void updateParams() {
		FileConfiguration cfg = main_plugin_.getConfig();
		EIConfigurator ei_cfg = main_plugin_.getEIConfigurator();
		ConfigurationSection score_section = ei_cfg.getConfigurationSection(cfg, "Score_settings");
		params_.clear();
		String score_name_ = ei_cfg.getString(score_section, "score_name", "ei_score");
		String score_disp_name_ = ei_cfg.getString(score_section, "score_display_name", "Shards of ice");
		Double transfer_coeff_ = ei_cfg.getDouble(score_section, "transfer_coeff", 0.5D);
		Double save_coeff_ = ei_cfg.getDouble(score_section, "save_coeff", 0.5D);
		Integer delay_reduce_cooldown_farm_ = ei_cfg.getInt(score_section, "delay_reduce_cooldown_farm", 1 * 60 * 20);
		Double linear_k_ = ei_cfg.getDouble(score_section, "linear_k", 0D);
		Double linear_b_ = ei_cfg.getDouble(score_section, "linear_b", 1D);
		params_.put("score_name", score_name_);
		params_.put("score_disp_name", score_disp_name_);
		params_.put("transfer_coeff", transfer_coeff_);
		params_.put("save_coeff", save_coeff_);
		params_.put("delay_reduce_cooldown_farm", delay_reduce_cooldown_farm_);
		params_.put("linear_k", linear_k_);
		params_.put("linear_b", linear_b_);
		updateCooldownTask();
		loadScoreboard();
	}
	
	public void updateCooldownTask() {
		BukkitScheduler scheduler =  main_plugin_.getServer().getScheduler();
		if (scheduler.isCurrentlyRunning(cooldown_task_id_) 
				|| scheduler.isQueued(cooldown_task_id_)) {
			scheduler.cancelTask(cooldown_task_id_);
		}
		regReduceCooldownFarm();
	}
	
	public void loadScoreboard() {
		Scoreboard board = main_plugin_.getServer().getScoreboardManager().getMainScoreboard();
		board_ = board;
		String score_name = (String) params_.get("score_name");
		String score_disp_name = (String) params_.get("score_disp_name");
		if (board.getObjective(score_name) != null) { return; }
		board.registerNewObjective(score_name, "dummy", score_disp_name);
		main_plugin_.getEILogger().log(LogLevel.DEBUG, Level.INFO, "Зарегистрировали новую цель в scorboard");
	}

	private double linear(int x) {
		double linear_k = (double) params_.get("linear_k");
		double linear_b = (double) params_.get("linear_b");
		double res = linear_k * x + linear_b;
		if (res > 1) { res = 1D; }
		if (res < 0) { res = 0D; }
		return res;
	}
	
	private void addCooldownFarm(Player player) {
		int cooldown = 0;
		if (player.getPersistentDataContainer().has(cooldown_farm_key_, PersistentDataType.INTEGER)) {
			cooldown = player.getPersistentDataContainer().getOrDefault(cooldown_farm_key_, 
					PersistentDataType.INTEGER, 0);
		}
		cooldown++;
		player.getPersistentDataContainer().set(cooldown_farm_key_, PersistentDataType.INTEGER, cooldown);
	}
	
	private void reduceCooldownFarm(Player player, int count) {
		int cooldown = 0;
		if (player.getPersistentDataContainer().has(cooldown_farm_key_, PersistentDataType.INTEGER)) {
			cooldown = player.getPersistentDataContainer().getOrDefault(cooldown_farm_key_, 
					PersistentDataType.INTEGER, 0);
		}
		cooldown -= count;
		if (cooldown < 0) { cooldown = 0; }
		player.getPersistentDataContainer().set(cooldown_farm_key_, PersistentDataType.INTEGER, cooldown);
	}
	
	private void reduceCooldownFarm() {
		Collection<? extends Player> players = main_plugin_.getServer().getOnlinePlayers();
		for (Player player : players) {
			reduceCooldownFarm(player, 1);
		}
	}
	
	public void regReduceCooldownFarm() {
		Runnable task = new Runnable() {
			@Override
			public void run() {
				reduceCooldownFarm();
			}
		};
		int delay_reduce_cooldown_farm = (int) params_.get("delay_reduce_cooldown_farm");
		cooldown_task_id_ = main_plugin_.getServer().getScheduler().scheduleSyncRepeatingTask(main_plugin_, 
				task, delay_reduce_cooldown_farm, delay_reduce_cooldown_farm);
		return;
	}
	
	private int getCooldownFarm(Player player) {
		int cooldown = 0;
		if (player.getPersistentDataContainer().has(cooldown_farm_key_, PersistentDataType.INTEGER)) {
			cooldown = player.getPersistentDataContainer().getOrDefault(
					cooldown_farm_key_, PersistentDataType.INTEGER, 0);
		}
		return cooldown;
	}
	
	public boolean takeRewardForMob(Player player, LivingEntity mob) {
		int reward = 0;
		if (mob.getPersistentDataContainer().has(score_reward_key_, PersistentDataType.INTEGER)) {
			reward = mob.getPersistentDataContainer().getOrDefault(
					score_reward_key_, PersistentDataType.INTEGER, 0);
		}
		if (reward <= 0) { return false; }
		reward = (int) Math.floor(reward * linear(getCooldownFarm(player))) + 1;
		String score_name = (String) params_.get("score_name");
		Score score = board_.getObjective(score_name).getScore(player.getName());
		int old_score = score.getScore();
		int new_score = old_score + reward;
		score.setScore(new_score);
		addCooldownFarm(player);
		return true;
	}
	
	public void takeRewardForPlayer(Player killer, Player prey) {
		String score_name = (String) params_.get("score_name");
		double transfer_coeff = (double) params_.get("transfer_coeff");
		double save_coeff = (double) params_.get("save_coeff");
		Score killer_score = board_.getObjective(score_name).getScore(killer.getName());
		Score prey_score = board_.getObjective(score_name).getScore(prey.getName());
		int transfer_score = (int) Math.floor(prey_score.getScore() * transfer_coeff);
		int save_score = (int) Math.floor(prey_score.getScore() * save_coeff);
		killer_score.setScore(killer_score.getScore() + transfer_score);
		prey_score.setScore(save_score);
	}
	
	public void takePenaltyFromPlayer(Player prey) {
		String score_name = (String) params_.get("score_name");
		double save_coeff = (double) params_.get("save_coeff");
		Score prey_score = board_.getObjective(score_name).getScore(prey.getName());
		int save_score = (int) Math.floor(prey_score.getScore() * save_coeff);
		prey_score.setScore(save_score);
	}
	
	public boolean takeScoreFromPlayer(Player player, int score) {
		String score_name = (String) params_.get("score_name");
		Score player_score = board_.getObjective(score_name).getScore(player.getName());
		if (player_score.getScore() < score) {
			return false;
		}
		player_score.setScore(player_score.getScore() - score);
		return true;
	}
	
	public void addScoreToPlayer(Player player, int score) {
		String score_name = (String) params_.get("score_name");
		Score player_score = board_.getObjective(score_name).getScore(player.getName());
		player_score.setScore(player_score.getScore() + score);
	}
	
	public int getScore(Player player) {
		String score_name = (String) params_.get("score_name");
		Score player_score = board_.getObjective(score_name).getScore(player.getName());
		return player_score.getScore();
	}
	
	public void setScore(Player player, int score) {
		String score_name = (String) params_.get("score_name");
		Score player_score = board_.getObjective(score_name).getScore(player.getName());
		player_score.setScore(score);
	}
	
	public void processPlayerDeathEvent(PlayerDeathEvent event) {
		Player prey = event.getEntity();
		Player killer = prey.getKiller();
		if (killer == null || killer.equals(prey)) {
			takePenaltyFromPlayer(prey);
		} else {
			takeRewardForPlayer(killer, prey);	
		}
	}
	
	public void processEntityDeathEvent(EntityDeathEvent event) {
		LivingEntity mob = event.getEntity();
		if (mob instanceof Player) { return; }
		Player player = mob.getKiller();
		takeRewardForMob(player, mob);
	}
	
}
