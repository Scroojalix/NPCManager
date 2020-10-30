package me.scroojalix.npcmanager.nms.v1_8_R2;

import java.lang.reflect.Method;

import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R2.CraftWorld;

import me.scroojalix.npcmanager.nms.interfaces.NMSHologram;
import net.minecraft.server.v1_8_R2.EntityArmorStand;

public class EntityNMSHologram extends EntityArmorStand implements NMSHologram {

	public EntityNMSHologram(Location loc, String text) {
		super(((CraftWorld)loc.getWorld()).getHandle());
		setCustomName(text);
		setCustomNameVisible(getCustomName() != null && !getCustomName().isEmpty());
	    setInvisible(true);
	    setSmall(true);
		setArms(false);
		setGravity(false);
		setBasePlate(true);
		setPosition(loc.getX(), loc.getY(), loc.getZ());
		try {
			Method method = EntityArmorStand.class.getDeclaredMethod("n", new Class[] { boolean.class });
			method.setAccessible(true);
			method.invoke(this, new Object[] { Boolean.valueOf(true) });
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public EntityArmorStand getEntity() {
		return this;
	}
}
