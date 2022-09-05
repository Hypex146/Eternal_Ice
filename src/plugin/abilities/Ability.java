package plugin.abilities;

import java.util.Map;

import org.bukkit.entity.Player;

public interface Ability { //TODO damage by player CHECK?
	
	public void updateParams();
	
	public Abilities getType();
	
	public String getName();
	
	public Map<String, Object> getParams();
	
	public boolean canCall(Player player);
	
	public boolean onCall(Player player); //TODO cast ability on certain entities CHECK?
	
	public boolean onCall(Player player, boolean forced);
	
	public int getCooldown(Player player);
	
}
