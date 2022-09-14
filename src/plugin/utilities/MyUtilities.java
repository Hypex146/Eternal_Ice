package plugin.utilities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.TreeMap;
import java.util.function.Predicate;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

public abstract class MyUtilities {
	
	public static Collection<Entity> getNearbyEntities(World world, Location loc, 
			double dist, Predicate<Entity> filter) {
		return world.getNearbyEntities(loc, dist, dist, dist, filter);
	}
	
	public static Collection<Entity> getNearbyEntities(World world, Location loc, 
			double dist, Predicate<Entity> filter, int max_count) {
		Collection<Entity> entities = world.getNearbyEntities(loc, dist, dist, dist, filter);
		TreeMap<Double, Entity> entities_tree = new TreeMap<Double, Entity>();
		for (Entity entity : entities) {
			entities_tree.put(entity.getLocation().distance(loc), entity);
		}
		entities.clear();
		for (int i = 0; i<entities_tree.size(); i++) {
			double first_key = entities_tree.firstKey();
			Entity entity = entities_tree.get(first_key);
			entities_tree.remove(first_key);
			entities.add(entity);
			if (i >= max_count) {break;}
		}
		return entities;
	}
	
	public static Predicate<Entity> createPredicate(List<EntityType> filter) {
		if (filter == null) { return null; }
		return new Predicate<Entity>() {
			@Override
			public boolean test(Entity t) {
				if (filter.contains(t.getType())) { return true; }
				return false;
			}
		};
	}
	
	public static Block getFirstBlockIntersectsVector(
			World world, Vector start, Vector direct, double y_offset, double max_dist) {
		int max_block_dist = (int) Math.round(max_dist * 2);
		BlockIterator blockIterator = new BlockIterator(world, start, direct, y_offset, max_block_dist);
		while (blockIterator.hasNext()) {
			Block block = blockIterator.next();
			if (block.getType()!=Material.AIR) {return block;}
		}
		return null;
	}
	
	public static Block getFirstBlockIntersectsVector(
			World world, Vector start, Vector direct, double y_offset, double max_dist, 
			Collection<Material> filter) {
		int max_block_dist = (int) Math.round(max_dist * 2);
		BlockIterator blockIterator = new BlockIterator(world, start, direct, y_offset, max_block_dist);
		while (blockIterator.hasNext()) {
			Block block = blockIterator.next();
			if (!filter.contains(block.getType())) {return block;}
		}
		return null;
	}
	
	public static boolean isLookingAt(LivingEntity beholder, LivingEntity observed, 
			double dist, double cos_detect_angle){
		if (beholder.equals(observed)) { return false; }
	    Location beholder_eye = beholder.getEyeLocation();
	    Vector from_beholder_to_observed = 
	    		observed.getEyeLocation().toVector().subtract(beholder_eye.toVector());
	    double dot = from_beholder_to_observed.normalize().dot(beholder_eye.getDirection());
	    Block obstacle = getFirstBlockIntersectsVector(
	    		beholder.getWorld(), beholder_eye.toVector(), 
	    		from_beholder_to_observed.normalize(), 0, dist + 1);
	    if (obstacle!=null) {
		    Location obstacle_loc = obstacle.getLocation();
		    double dist_to_observed = beholder_eye.distance(observed.getEyeLocation());
		    double dist_to_obstacle = obstacle_loc.distance(observed.getEyeLocation());
		    return (dot >= cos_detect_angle) && (dist_to_observed < dist_to_obstacle);
	    } else {
	    	return dot >= cos_detect_angle;
	    }
	}
	
	public static boolean isLookingAt(LivingEntity beholder, LivingEntity observed, 
			double dist, double cos_detect_angle, Collection<Material> filter){
		if (beholder.equals(observed)) { return false; }
	    Location beholder_eye = beholder.getEyeLocation();
	    Vector from_beholder_to_observed = 
	    		observed.getEyeLocation().toVector().subtract(beholder_eye.toVector());
	    double dot = from_beholder_to_observed.normalize().dot(beholder_eye.getDirection());
	    Block obstacle = getFirstBlockIntersectsVector(
	    		beholder.getWorld(), beholder_eye.toVector(), 
	    		from_beholder_to_observed.normalize(), 0, dist + 1, filter);
	    if (obstacle!=null) {
		    Location obstacle_loc = obstacle.getLocation();
		    double dist_to_observed = beholder_eye.distance(observed.getEyeLocation());
		    double dist_to_obstacle = obstacle_loc.distance(observed.getEyeLocation());
		    return (dot >= cos_detect_angle) && (dist_to_observed < dist_to_obstacle);
	    } else {
	    	return dot >= cos_detect_angle;
	    }
	}
	
	public static Collection<LivingEntity> getLookingEntities(LivingEntity observed, Predicate<Entity> filter, 
			double dist, double cos_detect_angle) {
		Collection<LivingEntity> beholders = new ArrayList<LivingEntity>();
		for (Entity entity : getNearbyEntities(observed.getWorld(), 
				observed.getLocation(), dist, filter)) {
			if (!(entity instanceof LivingEntity)) {continue;}
			LivingEntity possible_beholder = (LivingEntity)entity;
			if (!isLookingAt(possible_beholder, observed, dist, cos_detect_angle)) { continue; }
			beholders.add(possible_beholder);
		}
		return beholders;
	}
	
	public static Collection<LivingEntity> getLookingEntities(LivingEntity observed, Predicate<Entity> filter, 
			double dist, double cos_detect_angle, Collection<Material> material_filter) {
		Collection<LivingEntity> beholders = new ArrayList<LivingEntity>();
		for (Entity entity : getNearbyEntities(observed.getWorld(), 
				observed.getLocation(), dist, filter)) {
			if (!(entity instanceof LivingEntity)) {continue;}
			LivingEntity possible_beholder = (LivingEntity)entity;
			if (!isLookingAt(possible_beholder, observed, dist, cos_detect_angle, material_filter)) { continue; }
			beholders.add(possible_beholder);
		}
		return beholders;
	}
	
	public static Collection<LivingEntity> getLookingEntities(LivingEntity observed, Predicate<Entity> filter, 
			double dist, double cos_detect_angle, int max_count) {
		Collection<LivingEntity> beholders = new ArrayList<LivingEntity>();
		TreeMap<Double, LivingEntity> beholders_tree = new TreeMap<Double, LivingEntity>();
		for (Entity entity : getNearbyEntities(observed.getWorld(), 
				observed.getLocation(), dist, filter)) {
			if (!(entity instanceof LivingEntity)) {continue;}
			LivingEntity possible_beholder = (LivingEntity)entity;
			if (!isLookingAt(possible_beholder, observed, dist, cos_detect_angle)) { continue; }
			beholders_tree.put(observed.getLocation().distance(possible_beholder.getLocation()), 
					possible_beholder);
		}
		for (int i = 0; i < beholders_tree.size(); i++) {
			double first_key = beholders_tree.firstKey();
			LivingEntity beholder = beholders_tree.get(first_key);
			beholders_tree.remove(first_key);
			beholders.add(beholder);
			if (i >= max_count) {break;}
		}
		return beholders;
	}
	
	public static Collection<LivingEntity> getLookingEntities(LivingEntity observed, Predicate<Entity> filter, 
			double dist, double cos_detect_angle, int max_count, Collection<Material> material_filter) {
		Collection<LivingEntity> beholders = new ArrayList<LivingEntity>();
		TreeMap<Double, LivingEntity> beholders_tree = new TreeMap<Double, LivingEntity>();
		for (Entity entity : getNearbyEntities(observed.getWorld(), 
				observed.getLocation(), dist, filter)) {
			if (!(entity instanceof LivingEntity)) {continue;}
			LivingEntity possible_beholder = (LivingEntity)entity;
			if (!isLookingAt(possible_beholder, observed, dist, cos_detect_angle, material_filter)) { continue; }
			beholders_tree.put(observed.getLocation().distance(possible_beholder.getLocation()), 
					possible_beholder);
		}
		for (int i = 0; i < beholders_tree.size(); i++) {
			double first_key = beholders_tree.firstKey();
			LivingEntity beholder = beholders_tree.get(first_key);
			beholders_tree.remove(first_key);
			beholders.add(beholder);
			if (i >= max_count) {break;}
		}
		return beholders;
	}
	
	public static Collection<LivingEntity> getUnderAbservation(LivingEntity beholder, 
			Predicate<Entity> filter, double dist, double cos_detect_angle){
		Collection<LivingEntity> observeds = new ArrayList<LivingEntity>();
		for (Entity entity : getNearbyEntities(beholder.getWorld(), 
				beholder.getLocation(), dist, filter)) {
			if (!(entity instanceof LivingEntity)) { continue; }
			LivingEntity observed = (LivingEntity)entity;
			if (!isLookingAt(beholder, observed, dist, cos_detect_angle)) { continue; }
			observeds.add(observed);
		}
		return observeds;
	}
	
	public static Collection<LivingEntity> getUnderAbservation(LivingEntity beholder, 
			Predicate<Entity> filter, double dist, double cos_detect_angle, 
			Collection<Material> material_filter){
		Collection<LivingEntity> observeds = new ArrayList<LivingEntity>();
		for (Entity entity : getNearbyEntities(beholder.getWorld(), 
				beholder.getLocation(), dist, filter)) {
			if (!(entity instanceof LivingEntity)) { continue; }
			LivingEntity observed = (LivingEntity)entity;
			if (!isLookingAt(beholder, observed, dist, cos_detect_angle, material_filter)) { continue; }
			observeds.add(observed);
		}
		return observeds;
	}
	
	public static Collection<LivingEntity> getUnderAbservation(LivingEntity beholder, 
			Predicate<Entity> filter, double dist, double cos_detect_angle, int max_count){
		Collection<LivingEntity> observeds = new ArrayList<LivingEntity>();
		TreeMap<Double, LivingEntity> observeds_tree = new TreeMap<Double, LivingEntity>();
		for (Entity entity : getNearbyEntities(beholder.getWorld(), 
				beholder.getLocation(), dist, filter)) {
			if (!(entity instanceof LivingEntity)) { continue; }
			LivingEntity observed = (LivingEntity)entity;
			if (!isLookingAt(beholder, observed, dist, cos_detect_angle)) { continue; }
			observeds_tree.put(beholder.getLocation().distance(observed.getLocation()), observed);
		}
		for (int i = 0; i < observeds_tree.size(); i++) {
			double first_key = observeds_tree.firstKey();
			LivingEntity observed = observeds_tree.get(first_key);
			observeds_tree.remove(first_key);
			observeds.add(observed);
			if (i >= max_count) {break;}
		}
		return observeds;
	}
	
	public static Collection<LivingEntity> getUnderAbservation(LivingEntity beholder, 
			Predicate<Entity> filter, double dist, double cos_detect_angle, int max_count, 
			Collection<Material> material_filter){
		Collection<LivingEntity> observeds = new ArrayList<LivingEntity>();
		TreeMap<Double, LivingEntity> observeds_tree = new TreeMap<Double, LivingEntity>();
		for (Entity entity : getNearbyEntities(beholder.getWorld(), 
				beholder.getLocation(), dist, filter)) {
			if (!(entity instanceof LivingEntity)) { continue; }
			LivingEntity observed = (LivingEntity)entity;
			if (!isLookingAt(beholder, observed, dist, cos_detect_angle, material_filter)) { continue; }
			observeds_tree.put(beholder.getLocation().distance(observed.getLocation()), observed);
		}
		for (int i = 0; i < observeds_tree.size(); i++) {
			double first_key = observeds_tree.firstKey();
			LivingEntity observed = observeds_tree.get(first_key);
			observeds_tree.remove(first_key);
			observeds.add(observed);
			if (i >= max_count) {break;}
		}
		return observeds;
	}

}
