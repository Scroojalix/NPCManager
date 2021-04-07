package io.github.scroojalix.npcmanager.utils.storage.misc;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import com.google.gson.annotations.Expose;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;

public abstract interface Serialisable {

    /**
	 * Serialises an object into a Map<String, Object>
	 * @param instance The class instance to serialise.
	 * @return the serialised form of {@code instance}
	 */
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
							value = ((ConfigurationSerializable) value).serialize();
							//TODO remove serialisable class once previous system has been removed.
							//All Serialisable classes should implement ConfigurationSerializable
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
							value = ConfigurationSerialization.deserializeObject((Map<String, Object>) value);
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
						//Other values that do not fit.
						//Should probably create TypeAdapters like in gson but thats too much work.
						} else if (UUID.class.isAssignableFrom(type)) {
							value = UUID.fromString(value.toString());
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
