package io.github.scroojalix.npcmanager.common.storage.misc;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;

import com.google.gson.annotations.Expose;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.inventory.ItemStack;

import io.github.scroojalix.npcmanager.NPCMain;

public abstract interface Serialisable {

    /**
	 * Serialises an object into a Map<String, Object>
	 * @param instance The class instance to serialise.
	 * @return the serialised form of {@code instance}
	 */
	@SuppressWarnings("unchecked")
	public default Map<String, Object> serialise() {
		Map<String, Object> serialised = new LinkedHashMap<String, Object>();
		Field[] fields = this.getClass().getDeclaredFields();
		for (Field f : fields) {
			try {
				boolean access = f.isAccessible();
				f.setAccessible(true);
				
				//Check that the field has the @Expose annotation
				if (f.isAnnotationPresent(Expose.class)) {
					Object value = f.get(this);
					if (value != null) {
						if (ConfigurationSerializable.class.isAssignableFrom(f.getType())) {
							if (ItemStack.class.isAssignableFrom(f.getType())) {
								//Serialise itemstack to base64 string
								//Source: https://gist.github.com/graywolf336/8153678
								value = Base64Serialisation.toBase64(value);
							} else {
								Map<String, Object> map = ((ConfigurationSerializable) value).serialize();
								map.put(ConfigurationSerialization.SERIALIZED_TYPE_KEY, ConfigurationSerialization.getAlias((Class<ConfigurationSerializable>)f.getType()));
								value = map;
							}
						} else if (Serialisable.class.isAssignableFrom(f.getType())) {
							value = ((Serialisable) value).serialise();
						}
						serialised.put(f.getName(), value);
					}
				}

				f.setAccessible(access);
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		return serialised;
	}

	@SuppressWarnings("unchecked")
    public static <T extends Serialisable> T deserialise(Map<String, Object> serialised, Class<T> result) {
		try {
			Constructor<T> con = result.getDeclaredConstructor();
			con.setAccessible(true);
			T object = con.newInstance();
            Field[] fields = result.getDeclaredFields();
			for (Field f : fields) {
				boolean access = f.isAccessible();
				f.setAccessible(true);
				Class<?> type = f.getType();

				if (f.isAnnotationPresent(Expose.class)) {
					Object value = serialised.get(f.getName());
					if (value != null) {
						if (ConfigurationSerializable.class.isAssignableFrom(type)) {
							if (ItemStack.class.isAssignableFrom(type)) {
								try {
									value = Base64Serialisation.fromBase64((String)value);
								} catch(ClassCastException e) {
									//User is updating from version with old serialisation system to new one.
									value = null;
									NPCMain.instance.log(Level.SEVERE, "Could not deserialise ItemStack for an NPC. You may have to customise this NPC's equipment again.");
								} 
							} else {
								Map<String, Object> map = (Map<String, Object>) value;
								// TODO refactor this code, because it is crap. 
								// Add CustomSerialisationRule class then apply all rules to serialise/deserialise the object.
								if (map.containsKey("\"==\""))
									map.put(ConfigurationSerialization.SERIALIZED_TYPE_KEY, map.get("\"==\""));
								value = ConfigurationSerialization.deserializeObject((Map<String, Object>) value);
							}
						} else if (Serialisable.class.isAssignableFrom(type)) {
							value = deserialise((Map<String, Object>) value, (Class<T>) type);
						} else if (type.isEnum()) {
							Object[] constants = type.getEnumConstants();
							boolean failed = true;
							for (Object constant : constants) {
								if (constant.toString().equals(value)) {
									failed = false;
									value = constant;
								}
							}
							if (failed) {
								throw new IllegalArgumentException("Invalid enum constant "+value+" for type "+type.getName());
							}
						} else if (type.isPrimitive() && Number.class.isAssignableFrom(value.getClass())) {
							Number num = (Number) value;
							switch(type.getName()) {
								case "byte":
									value = num.byteValue();
									break;
								case "short":
									value = num.shortValue();
									break;
								case "int":
									value = num.intValue();
									break;
								case "long":
									value = num.longValue();
									break;
								case "float":
									value = num.floatValue();
									break;
								case "double":
									value = num.doubleValue();
									break;
							}
						}
						
						f.set(object, value);
					}
				}
				f.setAccessible(access);
			}
            return object;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
