package io.github.scroojalix.npcmanager.storage.misc;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

public class Base64Serialisation {

    public static String toBase64(Object data) throws IllegalStateException {
    	try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);

            dataOutput.writeObject(data);
            dataOutput.close();
            return Base64Coder.encodeLines(outputStream.toByteArray());
        } catch (Exception e) {
            throw new IllegalStateException("Unable to serialise object.", e);
        }
    }

    // FIXME cannot read ItemStacks from newer versions.
    // throws java.lang.IllegalArgumentException: 
    // Newer version! Server downgrades are not supported!
    // May need to rework ItemStack serialisation
    public static Object fromBase64(String data) throws IOException {
    	try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);

            Object result = dataInput.readObject();

            dataInput.close();
            return result;
        } catch (ClassNotFoundException e) {
            throw new IOException("Unable to decode class type.", e);
        }
    }
}
