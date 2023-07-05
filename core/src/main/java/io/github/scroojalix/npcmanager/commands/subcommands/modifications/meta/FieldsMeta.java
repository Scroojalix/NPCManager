package io.github.scroojalix.npcmanager.commands.subcommands.modifications.meta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import io.github.scroojalix.npcmanager.NPCMain;
import io.github.scroojalix.npcmanager.commands.CommandUtils;
import io.github.scroojalix.npcmanager.commands.SubCommand;
import io.github.scroojalix.npcmanager.npc.meta.MetaField;
import io.github.scroojalix.npcmanager.npc.meta.NPCMetaInfo;
import io.github.scroojalix.npcmanager.npc.meta.enums.ActiveHand;
import io.github.scroojalix.npcmanager.npc.meta.enums.MetaColor;
import io.github.scroojalix.npcmanager.npc.meta.enums.Pose;
import io.github.scroojalix.npcmanager.utils.PluginUtils;

public class FieldsMeta extends SubCommand {

    public FieldsMeta() {
        super(
            "fields",
            "Modify NPC metadata field values",
            "/npc modify <npc> metadata fields <field> <value>",
            true
        );
    }

    private String lastUpdate = null;

    @Override
    public boolean execute(NPCMain main, CommandSender sender, String[] args) {
        if (args.length < 6) return false;
        NPCMetaInfo npcMeta = PluginUtils.getNPCDataByName(args[1]).getTraits().getMetaInfo();

        // This if-else statement is messy, but its the best I could come up with
        // whilst retaining the typesafe MetaField#setField implementation.
        // TODO May end up reworking this entirely. For now though it will do.

        // Pose
        if (args[4].equalsIgnoreCase(MetaField.POSE.fieldName)) {
            Pose pose = PluginUtils.getEnumFromName(args[5], Pose.class);
            if (pose != null)
            setFieldValue(npcMeta, MetaField.POSE, pose);

        // Potion Effect Color
        } else if (args[4].equalsIgnoreCase(MetaField.POTION_COLOR.fieldName)) {
            MetaColor tempColor = PluginUtils.getEnumFromName(args[5], MetaColor.class);
            // Pre-defined Meta Color
            if (tempColor != null) {
                setFieldValue(npcMeta, MetaField.POTION_COLOR, tempColor.getHexValue(), tempColor.getBukkitColor() + tempColor.toString());
                
            // Custom hex color
            } else if (args[5].startsWith("#")) {
                if (args[5].matches("^#[a-fA-F0-9]{6}$")) {
                    Integer hexColor = Integer.parseInt(args[5].replace("#", ""), 16);
                    String valueString = PluginUtils.format(args[5]) + args[5];
                    setFieldValue(npcMeta, MetaField.POTION_COLOR, hexColor, valueString);
                } else {
                    sender.sendMessage(ChatColor.RED + "That is not a valid hex value");
                    return false;
                }
            }

        // Number of arrows stuck in NPC
        } else if (args[4].equalsIgnoreCase(MetaField.ARROWS.fieldName)) {
            Integer arrows = CommandUtils.toIntNullable(args[5]);
            if (arrows != null)
            setFieldValue(npcMeta, MetaField.ARROWS, arrows);

        // Number of stingers stuck in NPC. Only visible in 1.15+
        } else if (args[4].equalsIgnoreCase(MetaField.STINGERS.fieldName)) {
            Integer stingers = CommandUtils.toIntNullable(args[5]);
            if (stingers != null)
            setFieldValue(npcMeta, MetaField.STINGERS, stingers);
        
        // Active hand of NPC. Allows user to make the NPC block with a shield, sword, etc.
        } else if (args[4].equalsIgnoreCase(MetaField.ACTIVE_HAND.fieldName)) {
            ActiveHand activeHand = PluginUtils.getEnumFromName(args[5], ActiveHand.class);
            if (activeHand != null)
            setFieldValue(npcMeta, MetaField.ACTIVE_HAND, activeHand);
        
        // Glow color of NPC.
        } else if (args[4].equalsIgnoreCase(MetaField.GLOW_COLOR.fieldName)) {
            MetaColor glowColor = PluginUtils.getEnumFromName(args[5], MetaColor.class);
            if (glowColor != null)
            setFieldValue(npcMeta, MetaField.GLOW_COLOR, glowColor);

        // Invalid Metadata
        } else {
            sender.sendMessage(ChatColor.RED + "That is not a valid metadata field.");
            return false;
        }

        if (lastUpdate != null) {
            sender.sendMessage(lastUpdate);
            lastUpdate = null;
            return true;
        } else {
            sender.sendMessage(ChatColor.RED+"That is not a valid field value.");
            return false;
        }
    }

    @Override
    public List<String> onTabComplete(String[] args) {
        List<String> result = new ArrayList<>();
        if (args.length == 5) {
            result.addAll(Arrays.stream(MetaField.values())
            .map(f -> f.fieldName).collect(Collectors.toList()));
        } else if (args.length == 6) {
            MetaField<?> meta = MetaField.fromName(args[4]);
            if (meta == null) return result;
            if (meta.valueClass.isEnum()) {
                for (Object o : meta.valueClass.getEnumConstants()) {
                    result.add(o.toString());
                }
            } else if (meta.fieldName.equalsIgnoreCase(MetaField.POTION_COLOR.fieldName)) {
                result.addAll(Arrays.stream(MetaColor.values())
                .map(f -> f.toString()).collect(Collectors.toList()));
                result.add("#");
            }
        }
        return result;
    }
    
    private <T> void setFieldValue(NPCMetaInfo metaInfo, @Nonnull MetaField<T> field, @Nonnull T value) {
        String valueAsString = value.toString();
        if (valueAsString == null) valueAsString = "Error";
        setFieldValue(metaInfo, field, value, valueAsString);
    }

    private <T> void setFieldValue(NPCMetaInfo metaInfo, @Nonnull MetaField<T> field, @Nonnull T value, @Nonnull String valueString) {
        metaInfo.setFieldValue(field, value);
        lastUpdate = PluginUtils.format("&6Set field &f%s &6to &f%s", field.fieldName, valueString);
    }
}
