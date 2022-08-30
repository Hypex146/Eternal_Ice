package plugin.managers;

import java.util.Collection;
import java.util.logging.Level;

import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
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
	private String score_name_;
	private String score_disp_name_;
	private double transfer_coeff_;
	private double save_coeff_;
	private int delay_reduce_cooldown_farm_;
	private double sigmoida_coeff_;
	private double sigmoida_shift_;
	// const
	private final NamespacedKey score_reward_key_;
	private final NamespacedKey cooldown_farm_key_;
	
	public ScoreManager(EternalIce main_plugin) {
		main_plugin_ = main_plugin;
		score_reward_key_ = new NamespacedKey(main_plugin_, "score_reward");
		cooldown_farm_key_ = new NamespacedKey(main_plugin_, "score_cooldown");
	}
	
	public void updateParams() {
		FileConfiguration cfg = main_plugin_.getConfig();
		EIConfigurator ei_cfg = main_plugin_.getEIConfigurator();
		ConfigurationSection score_section = ei_cfg.getConfigurationSection(cfg, "Score_settings");
		score_name_ = ei_cfg.getString(score_section, "score_name", "ei_score");
		score_disp_name_ = ei_cfg.getString(score_section, "score_display_name", "Shards of ice");
		transfer_coeff_ = ei_cfg.getDouble(score_section, "transfer_coeff", 0.5D);
		save_coeff_ = ei_cfg.getDouble(score_section, "save_coeff", 0.5D);
		delay_reduce_cooldown_farm_ = ei_cfg.getInt(score_section, "delay_reduce_cooldown_farm", 1 * 60 * 20);
		sigmoida_coeff_ = ei_cfg.getDouble(score_section, "sigmoida_coeff", 1D);
		sigmoida_shift_ = ei_cfg.getDouble(score_section, "sigmoida_shift", 0D);
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
		if (board.getObjective(score_name_) != null) { return; }
		board.registerNewObjective(score_name_, "dummy", score_disp_name_);
		main_plugin_.getEILogger().log(LogLevel.DEBUG, Level.INFO, "Зарегистрировали новую цель в scorboard");
	}
	
	private double sigmoida(int x) { // TODO
		return (1) / (1 + Math.exp(sigmoida_coeff_ * (x - sigmoida_shift_)));
	}
	
	private void addCooldownFarm(Player player) {
		int cooldown = player.getPersistentDataContainer().getOrDefault(cooldown_farm_key_, 
				PersistentDataType.INTEGER, 0);
		cooldown++;
		player.getPersistentDataContainer().set(cooldown_farm_key_, PersistentDataType.INTEGER, cooldown);
	}
	
	private void reduceCooldownFarm(Player player, int count) {
		int cooldown = player.getPersistentDataContainer().getOrDefault(cooldown_farm_key_, 
				PersistentDataType.INTEGER, 0);
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
		cooldown_task_id_ = main_plugin_.getServer().getScheduler().scheduleSyncRepeatingTask(main_plugin_, 
				task, delay_reduce_cooldown_farm_, delay_reduce_cooldown_farm_);
		return;
	}
	
	private int getCooldownFarm(Player player) {
		return player.getPersistentDataContainer().getOrDefault(cooldown_farm_key_, PersistentDataType.INTEGER, 0);
	}
	
	public boolean takeRewardForMob(Player player, LivingEntity mob) {
		int reward = mob.getPersistentDataContainer().getOrDefault(score_reward_key_, PersistentDataType.INTEGER, 0);
		if (reward <= 0) { return false; }
		reward = (int) Math.floor(reward * sigmoida(getCooldownFarm(player))) + 1;
		Score score = board_.getObjective(score_name_).getScore(player.getName());
		int old_score = score.getScore();
		int new_score = old_score + reward;
		score.setScore(new_score);
		addCooldownFarm(player);
		return true;
	}
	
	public void takeRewardForPlayer(Player killer, Player prey) {
		Score killer_score = board_.getObjective(score_name_).getScore(killer.getName());
		Score prey_score = board_.getObjective(score_name_).getScore(prey.getName());
		int transfer_score = (int) Math.floor(prey_score.getScore() * transfer_coeff_);
		int save_score = (int) Math.floor(prey_score.getScore() * save_coeff_);
		killer_score.setScore(killer_score.getScore() + transfer_score);
		prey_score.setScore(save_score);
	}
	
	public void takePenaltyFromPlayer(Player prey) {
		Score prey_score = board_.getObjective(score_name_).getScore(prey.getName());
		int save_score = (int) Math.floor(prey_score.getScore() * save_coeff_);
		prey_score.setScore(save_score);
	}
	
	public boolean takeScoreFromPlayer(Player player, int score) {
		Score player_score = board_.getObjective(score_name_).getScore(player.getName());
		if (player_score.getScore() < score) {
			return false;
		}
		player_score.setScore(player_score.getScore() - score);
		return true;
	}
	
	public void addScoreToPlayer(Player player, int score) {
		Score player_score = board_.getObjective(score_name_).getScore(player.getName());
		player_score.setScore(player_score.getScore() + score);
	}
	
	public int getScore(Player player) {
		Score player_score = board_.getObjective(score_name_).getScore(player.getName());
		return player_score.getScore();
	}
	
	public void setScore(Player player, int score) {
		Score player_score = board_.getObjective(score_name_).getScore(player.getName());
		player_score.setScore(score);
	}
	
}
