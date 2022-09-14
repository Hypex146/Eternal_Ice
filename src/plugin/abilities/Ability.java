package plugin.abilities;

import java.util.Map;

import org.bukkit.entity.Player;

public interface Ability {
	
	public void updateParams();
	
	public Abilities getType();
	
	public String getName();
	
	public Map<String, Object> getParams();
	
	public boolean canCall(Player player);
	
	public boolean onCall(Player player);
	
	public boolean onCall(Player player, boolean forced);
	
	public int getCooldown(Player player);
	
}
