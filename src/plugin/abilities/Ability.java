package plugin.abilities;

import java.util.Map;

import org.bukkit.entity.Player;

public interface Ability {
	
	public void updateParams();
	
	public Abilities getType();
	
	public String getName();
	
	public Map<String, Object> getAbilityTable();
	
	public boolean canCall(Player player);
	
	public boolean onCall(Player player);
	
	public boolean onCall(Player player, boolean forced);
	
}
